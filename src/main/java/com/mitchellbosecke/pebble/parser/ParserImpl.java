/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.SyntaxException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.NodeBlock;
import com.mitchellbosecke.pebble.node.NodeBody;
import com.mitchellbosecke.pebble.node.NodeExpression;
import com.mitchellbosecke.pebble.node.NodeMacro;
import com.mitchellbosecke.pebble.node.NodePrint;
import com.mitchellbosecke.pebble.node.NodeRoot;
import com.mitchellbosecke.pebble.node.NodeText;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;
import com.mitchellbosecke.pebble.tokenParser.TokenParserBroker;
import com.mitchellbosecke.pebble.utils.Function;

public class ParserImpl implements Parser {

	/**
	 * A reference to the main engine.
	 */
	private final PebbleEngine engine;

	/**
	 * TokenParserBroker filled with token parsers that were originally provided
	 * by the extensions
	 */
	private TokenParserBroker tokenParserBroker;

	/**
	 * An expression parser.
	 */
	private ExpressionParser expressionParser;

	/**
	 * The TokenStream that we are converting into an Abstract Syntax Tree.
	 */
	private TokenStream stream;

	/**
	 * The parent template file name which must be known for a proper
	 * compilation.
	 */
	private String parentFileName;

	/**
	 * Blocks to be compiled.
	 */
	private Map<String, NodeBlock> blocks;

	/**
	 * Macros to be compiled. Macros can be overloaded by name which explains
	 * why it's a Map of Lists.
	 */
	private Map<String, List<NodeMacro>> macros;

	/**
	 * blockStack stores the names of the nested blocks to ensure that we always
	 * have access to the name of the block that we are currently in. This can
	 * be useful when implementing functions such as parent().
	 */
	private Stack<String> blockStack;

	/**
	 * Parser stack storing the stateful data. This is so that one Parser
	 * instance can be used to parse multiple different templates by storing and
	 * resuming it's state
	 */
	private Stack<ParserImpl> parserStack = new Stack<>();

	/**
	 * Constructor
	 * 
	 * @param engine
	 *            The main PebbleEngine that this parser is working for
	 */
	public ParserImpl(PebbleEngine engine) {
		this.engine = engine;
	}

	/**
	 * Private constructor that takes in all stateful data
	 */
	private ParserImpl(PebbleEngine engine, TokenStream stream, String parentFileName, Map<String, NodeBlock> blocks,
			Map<String, List<NodeMacro>> macros) {
		this.engine = engine;
		this.stream = stream;
		this.parentFileName = parentFileName;
		this.setBlocks(blocks);
		this.setMacros(macros);
	}

	@Override
	public NodeRoot parse(TokenStream stream) throws SyntaxException {

		// token parsers which have come from the extensions
		this.tokenParserBroker = engine.getTokenParserBroker();

		// expression parser
		this.expressionParser = new ExpressionParser(this);

		/*
		 * Store the state of this current parser just in case this parser
		 * instance was already being used to parse a template and this
		 * particular occurrence is a "sub template"
		 */
		parserStack.push(new ParserImpl(engine, stream, parentFileName, getBlocks(), getMacros()));

		this.stream = stream;

		this.parentFileName = null;

		this.blocks = new HashMap<>();
		this.blockStack = new Stack<>();

		this.setMacros(new HashMap<String, List<NodeMacro>>());

		NodeBody body = subparse();

		String parentClassName = parentFileName == null ? null : engine.getTemplateClassName(parentFileName);

		NodeRoot root = new NodeRoot(body, parentClassName, parentFileName, getBlocks(), getMacros(),
				stream.getFilename());

		/*
		 * Resume the parser state
		 */
		Parser oldState = parserStack.pop();
		this.stream = oldState.getStream();
		this.parentFileName = oldState.getParentFileName();
		this.setBlocks(oldState.getBlocks());
		this.setMacros(oldState.getMacros());

		return root;
	}

	@Override
	public NodeBody subparse() throws SyntaxException {
		return subparse(null);
	}

	@Override
	/**
	 * The main method for the parser. This method does the work of converting
	 * a TokenStream into a Node
	 * 
	 * @param stopCondition	A stopping condition provided by a token parser
	 * @return Node		The root node of the generated Abstract Syntax Tree
	 */
	public NodeBody subparse(Function<Boolean, Token> stopCondition) throws SyntaxException {

		// these nodes will be the children of the root node
		List<Node> nodes = new ArrayList<>();

		Token token;
		while (!stream.isEOF()) {

			switch (stream.current().getType()) {
				case TEXT:

					/*
					 * The current token is a text token. Not much to do here
					 * other than convert it to a text Node.
					 */
					token = stream.current();
					nodes.add(new NodeText(token.getValue(), token.getLineNumber()));
					stream.next();
					break;

				case PRINT_START:

					/*
					 * We are entering a print delimited region at this point. These regions
					 * will contain some sort of expression so let's pass
					 * control to our expression parser.
					 */

					// go to the next token because the current one is just the
					// opening delimiter
					token = stream.next();

					NodeExpression expression = this.expressionParser.parseExpression();
					nodes.add(new NodePrint(expression, token.getLineNumber()));

					// we expect to see a print closing delimiter
					stream.expect(Token.Type.PRINT_END);

					break;

				case EXECUTE_START:

					// go to the next token because the current one is just the
					// opening delimiter
					stream.next();

					token = stream.current();

					/*
					 * We expect a name token at the beginning of every block.
					 * 
					 * We do not use stream.expect() because it consumes the
					 * current token. The current token may be needed by a token
					 * parser which has provided a stopping condition. Ex. the
					 * 'if' token parser may need to check if the current token
					 * is either 'endif' or 'else' and act accordingly, thus we
					 * should not consume it.
					 */
					if (!Token.Type.NAME.equals(token.getType())) {
						throw new SyntaxException("A block must start with a tag name.", token.getLineNumber(),
								stream.getFilename());
					}

					// If this method was executed using a TokenParser and
					// that parser provided a stopping condition (ex. checking
					// for the 'endif' token) let's check for that condition
					// now.
					if (stopCondition != null && stopCondition.execute(token)) {
						return new NodeBody(token.getLineNumber(), nodes);
					}

					// find an appropriate parser for this name
					TokenParser subparser = tokenParserBroker.getTokenParser(token.getValue());

					if (subparser == null) {
						throw new SyntaxException(String.format("Unexpected tag name \"%s\"", token.getValue()),
								token.getLineNumber(), stream.getFilename());
					}

					subparser.setParser(this);
					Node node = subparser.parse(token);

					// node might be null (ex. token is "extend" and the parser
					// simply sets the parent node)
					if (node != null) {
						nodes.add(node);
					}

					break;

				default:
					throw new SyntaxException("Parser ended in undefined state.", stream.current().getLineNumber(),
							stream.getFilename());
			}
		}

		// create the root node with the children that we have found
		return new NodeBody(stream.current().getLineNumber(), nodes);
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

	public void setStream(TokenStream stream) {
		this.stream = stream;
	}

	@Override
	public String getParentFileName() {
		return this.parentFileName;
	}

	public void setParentFileName(String parent) {
		this.parentFileName = parent;
	}

	@Override
	public void setBlock(String name, NodeBlock block) {
		getBlocks().put(name, block);
	}

	@Override
	public void pushBlockStack(String name) {
		blockStack.push(name);
	}

	@Override
	public void popBlockStack() {
		blockStack.pop();
	}

	public String peekBlockStack() {
		return blockStack.lastElement();
	}

	@Override
	public ExpressionParser getExpressionParser() {
		return this.expressionParser;
	}

	@Override
	public PebbleEngine getEngine() {
		return engine;
	}

	@Override
	public Map<String, NodeBlock> getBlocks() {
		return blocks;
	}

	@Override
	public void setBlocks(Map<String, NodeBlock> blocks) {
		this.blocks = blocks;
	}

	@Override
	public Map<String, List<NodeMacro>> getMacros() {
		return macros;
	}

	@Override
	public void addMacro(String name, NodeMacro macro) {
		Map<String, List<NodeMacro>> existingMacros = getMacros();
		List<NodeMacro> macros;
		if (getMacros().containsKey(name)) {
			macros = getMacros().get(name);
		} else {
			macros = new ArrayList<NodeMacro>();
			existingMacros.put(name, macros);
		}
		macros.add(macro);
	}

	public void setMacros(Map<String, List<NodeMacro>> macros) {
		this.macros = macros;
	}

}
