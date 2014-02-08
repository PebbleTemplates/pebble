/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.template;

import java.io.IOException;
import java.io.Writer;

import com.mitchellbosecke.pebble.error.PebbleException;

public interface Block {

	public String getName();

	public void evaluate(Writer writer, EvaluationContext context) throws PebbleException, IOException;
}
