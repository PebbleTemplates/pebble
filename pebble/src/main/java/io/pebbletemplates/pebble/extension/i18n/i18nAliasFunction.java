/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.extension.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

public class i18nAliasFunction extends i18nFunction {
    
    private final List<String> argumentNames = new ArrayList<>();

    public i18nAliasFunction() {
        this.argumentNames.add("key");
        this.argumentNames.add("params");
    }

    @Override
    public List<String> getArgumentNames() {
        return this.argumentNames;
    }

    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
        args.put("bundle", "messages");

        return super.execute(args, self, context, lineNumber);
    }
}
