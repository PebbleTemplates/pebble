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
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.utils.ReflectionUtils;

/**
 * This node can be either getting a field from a variable or calling a method
 * of that variable. Ex:
 * 
 * var.field OR var.getField() OR var.executeMethod(arg1, arg2)
 * 
 * @author Mitchell
 * 
 */
public class GetAttributeExpression implements Expression<Object> {

	private final Expression<?> node;
	private final String attributeName;

	public GetAttributeExpression(Expression<?> node, String attributeName) {
		this.node = node;
		this.attributeName = attributeName;
	}

	@Override
	public Object evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
		return ReflectionUtils.getAttribute(context, node.evaluate(self, context), attributeName);
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public Expression<?> getNode() {
		return node;
	}

	public String getAttribute() {
		return attributeName;
	}

}
