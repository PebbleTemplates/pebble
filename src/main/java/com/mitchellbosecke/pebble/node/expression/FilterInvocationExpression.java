/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

/**
 * The right hand side to the filter expression.
 * 
 * @author Mitchell
 * 
 */
public class FilterInvocationExpression implements Expression<Object> {

	private final String filterName;

	private final ArgumentsNode args;

	public FilterInvocationExpression(String filterName, ArgumentsNode args) {
		this.filterName = filterName;
		this.args = args;
	}

	@Override
	public Object evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
		throw new UnsupportedOperationException();
	}

	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public ArgumentsNode getArgs() {
		return args;
	}

	public String getFilterName() {
		return filterName;
	}

}
