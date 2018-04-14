/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.template;

import java.util.Map;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NamedArguments;

public interface Macro extends NamedArguments {

    String getName();

    String call(PebbleTemplateImpl self, EvaluationContextImpl context, Map<String, Object> args)
            throws PebbleException;
}
