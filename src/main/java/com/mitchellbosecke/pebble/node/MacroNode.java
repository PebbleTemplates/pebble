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
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.Macro;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class MacroNode extends AbstractRenderableNode {

	private final String name;

	private final ArgumentsNode args;

	private final BodyNode body;

	public MacroNode(String name, ArgumentsNode args, BodyNode body) {
		this.name = name;
		this.args = args;
		this.body = body;
	}

	@Override
	public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException,
			IOException {
		self.macro(context, name, getArgs(), false);
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public Macro getMacro() {
		return new Macro() {

			@Override
			public List<String> getArgumentNames() {
				List<String> names = new ArrayList<>();
				for (NamedArgumentNode arg : getArgs().getNamedArgs()) {
					names.add(arg.getName());
				}
				return names;
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public String call(PebbleTemplateImpl self, EvaluationContext context, Map<String, Object> macroArgs)
					throws PebbleException {
				Writer writer = new StringWriter();

				// scope for default arguments
				context.pushLocalScope();
				for (NamedArgumentNode arg : getArgs().getNamedArgs()) {
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
					getBody().render(self, writer, context);
				} catch (IOException e) {
					throw new RuntimeException("Could not evaluate macro [" + name + "]", e);
				}
				context.popScope();
				context.popScope();
				return writer.toString();
			}

		};
	}

	public BodyNode getBody() {
		return body;
	}

	public ArgumentsNode getArgs() {
		return args;
	}

	public String getName() {
		return name;
	}

}
