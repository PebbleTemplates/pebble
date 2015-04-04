/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.EvaluationContext;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NumberFormatFilter implements Filter {

    private final List<String> argumentNames = new ArrayList<>();

    public NumberFormatFilter() {
        argumentNames.add("format");
    }

    @Override
    public List<String> getArgumentNames() {
        return argumentNames;
    }

    @Override
    public Object apply(Object input, Map<String, Object> args) {
        if (input == null) {
            return null;
        }
        Number number = (Number) input;

        EvaluationContext context = (EvaluationContext) args.get("_context");
        Locale locale = context.getLocale();

        if (args.get("format") != null) {
            Format format = new DecimalFormat((String) args.get("format"), new DecimalFormatSymbols(locale));
            return format.format(number);
        } else {
            NumberFormat numberFormat = NumberFormat.getInstance(locale);
            return numberFormat.format(number);
        }
    }

}
