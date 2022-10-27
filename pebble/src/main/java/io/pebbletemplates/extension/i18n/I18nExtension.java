/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.extension.i18n;

import io.pebbletemplates.extension.AbstractExtension;
import io.pebbletemplates.extension.Function;
import java.util.HashMap;
import java.util.Map;

public class I18nExtension extends AbstractExtension {

  @Override
  public Map<String, Function> getFunctions() {
    Map<String, Function> functions = new HashMap<>();
    functions.put("i18n", new i18nFunction());
    return functions;
  }

}
