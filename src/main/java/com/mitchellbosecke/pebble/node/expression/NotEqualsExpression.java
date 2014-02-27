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
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.utils.OperatorUtils;

public class NotEqualsExpression extends BinaryExpression<Boolean> {

	@Override
	public Boolean evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
		return OperatorUtils.equals(getLeftExpression().evaluate(self, context),
				getRightExpression().evaluate(self, context)) == false;
	}
}
