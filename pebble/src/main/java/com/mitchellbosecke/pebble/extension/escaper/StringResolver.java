/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2019 by Axel Dörfler
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.extension.escaper;

import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public interface StringResolver
{
  String resolve(Object instance,
      PebbleTemplate self,
      EvaluationContext context,
      int lineNumber);
}
