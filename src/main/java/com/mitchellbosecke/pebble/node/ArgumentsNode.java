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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NamedArguments;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class ArgumentsNode implements Node {

	private final List<NamedArgumentNode> namedArgs;

	private List<PositionalArgumentNode> positionalArgs;

	public ArgumentsNode(List<PositionalArgumentNode> positionalArgs, List<NamedArgumentNode> namedArgs) {
		this.positionalArgs = positionalArgs;
		this.namedArgs = namedArgs;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public List<NamedArgumentNode> getNamedArgs() {
		return namedArgs;
	}

	public List<PositionalArgumentNode> getPositionalArgs() {
		return positionalArgs;
	}

	/**
	 * Using hints from the filter/function/test/macro it will convert an
	 * ArgumentMap (which holds both positional and named arguments) into a
	 * regular Map that the filter/function/test/macro is expecting.
	 * 
	 * @param invocableWithNamedArguments
	 * @param arguments
	 * @return
	 * @throws PebbleException
	 */
	public Map<String, Object> getArgumentMap(PebbleTemplateImpl self, EvaluationContext context,
			NamedArguments invocableWithNamedArguments) throws PebbleException {
		Map<String, Object> result = new HashMap<>();
		List<String> argumentNames = invocableWithNamedArguments.getArgumentNames();

		if (argumentNames == null) {
			if (positionalArgs == null || positionalArgs.isEmpty()) {
				return result;
			}

			/* Some functions such as min and max use un-named varags */
			else {
				for (int i = 0; i < positionalArgs.size(); i++) {
					result.put(String.valueOf(i), positionalArgs.get(i).getValueExpression().evaluate(self, context));
				}
			}
		} else {
			Iterator<String> nameIterator = argumentNames.iterator();

			if (positionalArgs != null) {
				for (PositionalArgumentNode arg : positionalArgs) {
					result.put(nameIterator.next(), arg.getValueExpression().evaluate(self, context));
				}
			}

			if (namedArgs != null) {
				for (NamedArgumentNode arg : namedArgs) {
					// check if user used an incorrect name
					if (!argumentNames.contains(arg.getName())) {
						throw new PebbleException(null, "The following named argument does not exist: " + arg.getName());
					}
					Object value = arg.getValueExpression() == null? null : arg.getValueExpression().evaluate(self, context);
					result.put(arg.getName(), value);
				}
			}
		}

		return result;
	}

}
