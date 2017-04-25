/*******************************************************************************
 * This file is part of Pebble.
 * <p/>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p/>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.i18n;

import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.text.MessageFormat;
import java.util.*;

public class i18nFunction implements Function {

    private final List<String> argumentNames = new ArrayList<>();

    public i18nFunction() {
        argumentNames.add("bundle");
        argumentNames.add("key");
        argumentNames.add("params");
    }

    @Override
    public List<String> getArgumentNames() {
        return argumentNames;
    }

    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
        String basename = (String) args.get("bundle");
        String key = (String) args.get("key");
        Object params = args.get("params");

        Locale locale = context.getLocale();

        ResourceBundle bundle = ResourceBundle.getBundle(basename, locale, new UTF8Control());
        Object phraseObject = bundle.getObject(key);

        if (phraseObject != null && params != null) {
            if (params instanceof List) {
                List<?> list = (List<?>) params;
                return MessageFormat.format(phraseObject.toString(), list.toArray());
            } else {
                return MessageFormat.format(phraseObject.toString(), params);
            }
        }

        return phraseObject;
    }

}
