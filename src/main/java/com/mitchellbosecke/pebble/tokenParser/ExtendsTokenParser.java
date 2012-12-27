package com.mitchellbosecke.pebble.tokenParser;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.SyntaxException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.Node;

public class ExtendsTokenParser extends AbstractTokenParser {

	@Override
	public Node parse(Token token) {
		TokenStream stream = this.parser.getStream();
		int lineNumber = token.getLineNumber();
		PebbleEngine engine = this.parser.getEngine();
		
		// skip the 'extends' token
		stream.next();
		
		if (this.parser.getParentClassName() != null) {
			throw new SyntaxException("Multiple extend tags are not allowed.",
					lineNumber, parser.getStream().getFilename());
		}
		
		
		String templateName = stream.current().getValue().replace("\"", "");
		this.parser.setParentClassName(engine.getTemplateClassName(templateName));
		
		// consume the parent name
		stream.next();
		
		stream.expect(Token.Type.BLOCK_END);
		return null;
	}

	@Override
	public String getTag() {
		return "extends";
	}
}
