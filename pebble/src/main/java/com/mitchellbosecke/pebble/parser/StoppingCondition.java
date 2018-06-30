/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.parser;

import com.mitchellbosecke.pebble.lexer.Token;

/**
 * Implementations of this class are provided by the TokenParsers and handed to the main Parser. The
 * main parser will parse some of the template until the stopping condition evaluates to true; at
 * this point responsibility is transferred back to the TokenParser.
 *
 * @author Mitchell
 */
public interface StoppingCondition {

  boolean evaluate(Token data);
}
