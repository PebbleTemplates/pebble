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
import com.mitchellbosecke.pebble.error.ParserException;
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

public class ParserImpl implements Parser {

	/**
	 * A reference to the main engine.
	 */
	private final PebbleEngine engine;

	/**
	 * An expression parser.
	 */
	private ExpressionParser expressionParser;

	/**
	 * The TokenStream that we are converting into an Abstract Syntax Tree.
	 */
	private TokenStream stream;

	/**
	 * The parent template expression.
	 */
	private NodeExpression parentTemplateExpression;

	/**
	 * Blocks to be compiled.
	 */
	private Map<String, NodeBlock> blocks;

	/**
	 * Macros to be compiled. Macros can be overloaded by name which explains
	 * why it's a Map of Lists.
	 */
	private Map<String, NodeMacro> macros;

	/**
	 * blockStack stores the names of the nested blocks to ensure that we always
	 * have access to the name of the block that we are currently in. This can
	 * be useful when implementing functions such as parent().
	 */
	private Stack<String> blockStack;

	/**
	 * TokenParser objects provided by the extensions.
	 */
	private Map<String, TokenParser> tokenParsers;

	/**
	 * Constructor
	 * 
	 * @param engine
	 *            The main PebbleEngine that this parser is working for
	 */
	public ParserImpl(PebbleEngine engine) {
		this.engine = engine;
	}

	@Override
	public NodeRoot parse(TokenStream stream) throws ParserException {

		// token parsers which have come from the extensions
		this.tokenParsers = engine.getTokenParsers();

		// expression parser
		this.expressionParser = new ExpressionParser(this, engine.getBinaryOperators(), engine.getUnaryOperators());

		this.stream = stream;

		this.parentTemplateExpression = null;

		this.blocks = new HashMap<>();
		this.blockStack = new Stack<>();
		this.macros = new HashMap<String, NodeMacro>();

		NodeBody body = subparse();

		NodeRoot root = new NodeRoot(body, parentTemplateExpression, blocks, macros, stream.getFilename());
		return root;
	}

	@Override
	public NodeBody subparse() throws ParserException {
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
	public NodeBody subparse(StoppingCondition stopCondition) throws ParserException {

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
					 * We are entering a print delimited region at this point.
					 * These regions will contain some sort of expression so
					 * let's pass control to our expression parser.
					 */

					// go to the next token because the current one is just the
					// opening delimiter
					token = stream.next();

					NodeExpression expression = this.expressionParser.parseExpression();
					nodes.add(new NodePrint(expression, token.getLineNumber()));

					// we expect to see a print closing delimiter
					stream.expect(Token.Type.PRINT_END, engine.getLexer().getPrintCloseDelimiter());

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
						throw new ParserException(null, "A block must start with a tag name.", token.getLineNumber(),
								stream.getFilename());
					}

					// If this method was executed using a TokenParser and
					// that parser provided a stopping condition (ex. checking
					// for the 'endif' token) let's check for that condition
					// now.
					if (stopCondition != null && stopCondition.evaluate(token)) {
						return new NodeBody(token.getLineNumber(), nodes);
					}

					// find an appropriate parser for this name
					TokenParser subparser = tokenParsers.get(token.getValue());

					if (subparser == null) {
						throw new ParserException(null, String.format("Unexpected tag name \"%s\"", token.getValue()),
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
					throw new ParserException(null, "Parser ended in undefined state.", stream.current()
							.getLineNumber(), stream.getFilename());
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
	public NodeExpression getParentTemplateExpression() {
		return this.parentTemplateExpression;
	}

	public void setParentTemplateExpression(NodeExpression parentTemplateExpression) {
		this.parentTemplateExpression = parentTemplateExpression;
	}

	@Override
	public void addBlock(String name, NodeBlock block) {
		blocks.put(name, block);
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
	public void addMacro(String name, NodeMacro macro) throws ParserException {
		if (macros.containsKey(name)) {
			throw new ParserException(null, "Can not have more than one macro with the same name. [" + name + "]",
					stream.current().getLineNumber(), stream.getFilename());
		}
		macros.put(name, macro);
	}

}
