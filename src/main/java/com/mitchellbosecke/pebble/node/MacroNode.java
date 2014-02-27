/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.Macro;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class MacroNode extends AbstractRenderableNode {

	private final ArgumentsNode args;

	private final String name;

	private final BodyNode body;

	public MacroNode(int lineNumber, String name, ArgumentsNode args, BodyNode body) {
		super(lineNumber);
		this.name = name;
		this.args = args;
		this.body = body;
	}

	@Override
	public void render(final PebbleTemplateImpl self, final Writer writer, EvaluationContext context) {

		self.registerMacro(new Macro() {

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
				return name;
			}

			@Override
			public String call(EvaluationContext context, Map<String, Object> macroArgs) throws PebbleException {
				Writer writer = new StringWriter();
				context.pushLocalScope();
				for (NamedArgumentNode arg : args.getNamedArgs()) {
					context.put(arg.getName(), arg.getValueExpression().evaluate(self, context));
				}
				context.pushScope();
				for (Entry<String, Object> arg : macroArgs.entrySet()) {
					context.put(arg.getKey(), arg.getValue());
				}
				try {
					body.render(self, writer, context);
				} catch (IOException e) {
					throw new RuntimeException("Could not evaluate macro [" + name + "]", e);
				}
				context.popScope();
				context.popScope();
				return writer.toString();
			}

		});
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public ArgumentsNode getArgs() {
		return args;
	}

	public String getName() {
		return name;
	}

	public BodyNode getBody() {
		return body;
	}
}
