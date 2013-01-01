/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
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
import com.mitchellbosecke.pebble.utils.Command;

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
	 * Other
	 */
	private String parentClassName;
	private String parentFileName;
	private Map<String, NodeBlock> blocks;
	private Stack<String> blockStack;
	
	private Map<String, NodeMacro> macros;

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
	public NodeRoot parse(TokenStream stream) {
		this.stream = stream;

		// token parsers which have come from the extensions
		this.tokenParserBroker = engine.getTokenParserBroker();

		// expression parser
		this.expressionParser = new ExpressionParser(this);

		this.parentClassName = null;
		this.parentFileName = null;
		
		this.blocks = new HashMap<>();
		this.blockStack = new Stack<>();
		
		this.macros = new HashMap<>();

		NodeBody body = subparse();

		NodeRoot root = new NodeRoot(body, parentClassName, parentFileName, blocks, macros, stream.getFilename());

		return root;
	}

	@Override
	public NodeBody subparse() {
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
	public NodeBody subparse(Command<Boolean, Token> stopCondition) {

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

				case VARIABLE_START:

					/*
					 * We are entering variable tags at this point. These tags
					 * will contain some sort of expression so let's pass
					 * control to our expression parser.
					 */

					// go to the next token because the current one is just the
					// opening tag
					token = stream.next();

					NodeExpression expression = this.expressionParser.parseExpression();
					nodes.add(new NodePrint(expression, token.getLineNumber()));

					// we expect to see a variable closing tag
					stream.expect(Token.Type.VARIABLE_END);

					break;

				case BLOCK_START:

					// go to the next token because the current one is just the
					// opening tag
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
	public String getParentClassName() {
		return parentClassName;
	}
	

	@Override
	public void setParentClassName(String parent) {
		this.parentClassName = parent;
	}
	
	public String getParentFileName(){
		return this.parentFileName;
	}
	
	public void setParentFileName(String parent){
		this.parentFileName = parent;
	}

	@Override
	public boolean hasBlock(String name) {
		return blocks.containsKey(name);
	}

	@Override
	public void setBlock(String name, NodeBlock block) {
		blocks.put(name, block);
	}
	
	@Override
	public void setMacro(String name, NodeMacro macro){
		macros.put(name, macro);
	}

	@Override
	public void pushBlockStack(String name) {
		blockStack.push(name);
	}

	@Override
	public void popBlockStack() {
		blockStack.pop();
	}

	@Override
	public ExpressionParser getExpressionParser() {
		return this.expressionParser;
	}

	@Override
	public PebbleEngine getEngine() {
		return engine;
	}

}
