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
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

/**
 * Date filter supports formatting of java.util.Date and dates/times from the java.time package. For valid {@link Date}
 * formats, see {@link SimpleDateFormat}. For valid <code>java.time.*</code> formats, see {@link DateTimeFormatter}.
 * <p>
 * Supports
 * <ul>
 * <li>{@link Date}
 * <li>{@link java.time.LocalDate}
 * <li>{@link java.time.LocalDateTime}
 * <li>{@link java.time.ZonedDateTime}
 * </ul>
 */
public class DateFilter implements Filter {

    public static final String FILTER_NAME = "date";
    private final static String ARG_FORMAT = "format";
    private final static String ARG_EXISTING_FORMAT = "existingFormat";
    private final List<String> argumentNames = new ArrayList<>();

    public DateFilter() {
        argumentNames.add(ARG_FORMAT);
        argumentNames.add(ARG_EXISTING_FORMAT);
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

        EvaluationContext context = (EvaluationContext) args.get("_context");
        Locale locale = context.getLocale();
        String format = (String) args.get(ARG_FORMAT);
        Object existingFormat = args.get(ARG_EXISTING_FORMAT);

        if (input instanceof Date || input instanceof String) {
            return new SafeString(formatDate(input, locale, format, existingFormat));
        }

        if (input instanceof TemporalAccessor) {
            return new SafeString(formatTemporalAccessor((TemporalAccessor) input, locale, format));
        }

        throw new RuntimeException(
                "Could not format date. Must be java.util.Date or implement java.time.temporal.TemporalAccessor");
    }

    private String formatTemporalAccessor(TemporalAccessor input, Locale locale, String formatString) {
        DateTimeFormatter intendedFormat = DateTimeFormatter.ofPattern(formatString, locale);
        return intendedFormat.format(input);
    }

    private String formatDate(Object input, Locale locale, String formatString, Object existingFormatString) {

        Date date;

        DateFormat existingFormat;
        DateFormat intendedFormat = new SimpleDateFormat(formatString, locale);

        if (existingFormatString != null) {
            existingFormat = new SimpleDateFormat((String) existingFormatString, locale);
            try {
                date = existingFormat.parse((String) input);
            } catch (ParseException e) {
                throw new RuntimeException("Could not parse date", e);
            }
        } else {
            date = (Date) input;
        }

        return intendedFormat.format(date);

    }
}

