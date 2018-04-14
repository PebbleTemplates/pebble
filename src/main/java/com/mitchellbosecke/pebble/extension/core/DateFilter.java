/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.escaper.SafeString;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.*;

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
    public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) throws PebbleException {
        if (input == null) {
            return null;
        }
        final Locale locale = context.getLocale();
        final String format = (String) args.get("format");

        if(TemporalAccessor.class.isAssignableFrom(input.getClass())) {
            return applyTemporal((TemporalAccessor)input, self, locale, lineNumber, format);
        }
        return applyDate(input, self, locale, lineNumber, format, (String) args.get("existingFormat"));
     }

    private Object applyDate(Object dateOrString, final PebbleTemplate self, final Locale locale,
        int lineNumber, final String format, final String existingFormatString) throws PebbleException {
        Date date = null;
        DateFormat existingFormat = null;
        DateFormat intendedFormat = null;
        if (existingFormatString != null) {
            existingFormat = new SimpleDateFormat(existingFormatString, locale);
            try {
                date = existingFormat.parse(dateOrString.toString());
            } catch (ParseException e) {
                throw new PebbleException(e, String.format("Could not parse the string '%1' into a date.",
                    dateOrString.toString()), lineNumber, self.getName());
            }
        } else {
            date = (Date) dateOrString;
        }
        intendedFormat = new SimpleDateFormat(format, locale);
        return new SafeString(intendedFormat.format(date));
    }

    private Object applyTemporal(final TemporalAccessor input, PebbleTemplate self, final Locale locale,
        int lineNumber, final String format) throws PebbleException {
        final DateTimeFormatter formatter = format != null
            ? DateTimeFormatter.ofPattern(format, locale)
            : DateTimeFormatter.ISO_DATE_TIME;
        try {
            return new SafeString(formatter.format(input));
        } catch (DateTimeException dte) {
            throw new PebbleException(dte, String.format("Could not parse the string '%1' into a date.",
                input.toString()), lineNumber, self.getName());
        }
    }

}
