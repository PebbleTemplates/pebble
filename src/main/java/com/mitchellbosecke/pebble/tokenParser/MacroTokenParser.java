/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.tokenParser;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.NamedArgumentNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.parser.StoppingCondition;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.Macro;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class MacroTokenParser extends AbstractTokenParser {

	@Override
	public RenderableNode parse(Token token) throws ParserException {

		TokenStream stream = this.parser.getStream();

		// skip over the 'macro' token
		stream.next();

		final String macroName = stream.expect(Token.Type.NAME).getValue();

		final ArgumentsNode args = this.parser.getExpressionParser().parseArguments(true);

		stream.expect(Token.Type.EXECUTE_END);

		// parse the body
		final BodyNode body = this.parser.subparse(decideMacroEnd);

		// skip the 'endmacro' token
		stream.next();

		stream.expect(Token.Type.EXECUTE_END);

		/*
		 * We register the macro with the parser instead of returning a node
		 * to be put into the AST. This is because a template must be completely aware
		 * of it's macros BEFORE it's evaluated just in case another template wants to use it's macros via
		 * the import tag.
		 */
		this.parser.addMacro(new Macro() {

			@Override
			public List<String> getArgumentNames() {
				List<String> names = new ArrayList<>();
				for (NamedArgumentNode arg : args.getNamedArgs()) {
					names.add(arg.getName());
				}
				return names;
			}

			@Override
			public String getName() {
				return macroName;
			}

			@Override
			public String call(PebbleTemplateImpl self, EvaluationContext context, Map<String, Object> macroArgs) throws PebbleException {
				Writer writer = new StringWriter();

				// scope for default arguments
				context.pushLocalScope();
				for (NamedArgumentNode arg : args.getNamedArgs()) {
					Expression<?> valueExpression = arg.getValueExpression();
					if (valueExpression == null) {
						context.put(arg.getName(), null);
					} else {
						context.put(arg.getName(), arg.getValueExpression().evaluate(self, context));
					}
				}

				// scope for user provided arguments
				context.pushScope();
				for (Entry<String, Object> arg : macroArgs.entrySet()) {
					context.put(arg.getKey(), arg.getValue());
				}
				try {
					body.render(self, writer, context);
				} catch (IOException e) {
					throw new RuntimeException("Could not evaluate macro [" + macroName + "]", e);
				}
				context.popScope();
				context.popScope();
				return writer.toString();
			}

		});
		
		return null;
	}

	private StoppingCondition decideMacroEnd = new StoppingCondition() {
		@Override
		public boolean evaluate(Token token) {
			return token.test(Token.Type.NAME, "endmacro");
		}
	};

	@Override
	public String getTag() {
		return "macro";
	}
}
