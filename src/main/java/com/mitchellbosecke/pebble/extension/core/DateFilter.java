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
import com.mitchellbosecke.pebble.extension.escaper.SafeString;
import com.mitchellbosecke.pebble.template.EvaluationContext;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.lang.String.format;

public class DateFilter implements Filter {

    private final List<String> argumentNames = new ArrayList<>();

    public DateFilter() {
        argumentNames.add("format");
        argumentNames.add("existingFormat");
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
        Date date = null;

        DateFormat existingFormat = null;
        DateFormat intendedFormat = null;

        EvaluationContext context = (EvaluationContext) args.get("_context");
        Locale locale = context.getLocale();

        intendedFormat = new SimpleDateFormat((String) args.get("format"), locale);

        if (args.get("existingFormat") != null) {
            existingFormat = new SimpleDateFormat((String) args.get("existingFormat"), locale);
            try {
                date = existingFormat.parse((String) input);
            } catch (ParseException e) {
                throw new RuntimeException("Could not parse date", e);
            }
        } else {
            if (input instanceof Date) {
                date = (Date) input;
            } else if (input instanceof Number) {
                date = new Date(((Number) input).longValue());
            } else {
                throw new IllegalArgumentException(format("Unsupported argument type: %s (value: %s)", input.getClass().getName(), input));
            }
        }

        return new SafeString(intendedFormat.format(date));
    }
}
