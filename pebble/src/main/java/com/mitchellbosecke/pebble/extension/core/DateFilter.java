/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.extension.core;

import static java.lang.String.format;

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
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DateFilter implements Filter {

  private final List<String> argumentNames = new ArrayList<>();

  public DateFilter() {
    this.argumentNames.add("format");
    this.argumentNames.add("existingFormat");
  }

  @Override
  public List<String> getArgumentNames() {
    return this.argumentNames;
  }

  @Override
  public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
      EvaluationContext context, int lineNumber) throws PebbleException {
    if (input == null) {
      return null;
    }
    final Locale locale = context.getLocale();
    final String format = (String) args.get("format");

    if (TemporalAccessor.class.isAssignableFrom(input.getClass())) {
      return this.applyTemporal((TemporalAccessor) input, self, locale, lineNumber, format);
    }
    return this
        .applyDate(input, self, locale, lineNumber, format, (String) args.get("existingFormat"));
  }

  private Object applyDate(Object dateOrString, final PebbleTemplate self, final Locale locale,
      int lineNumber, final String format, final String existingFormatString)
      throws PebbleException {
    Date date;
    DateFormat existingFormat;
    DateFormat intendedFormat;
    if (existingFormatString != null) {
      existingFormat = new SimpleDateFormat(existingFormatString, locale);
      try {
        date = existingFormat.parse(dateOrString.toString());
      } catch (ParseException e) {
        throw new PebbleException(e, String.format("Could not parse the string '%s' into a date.",
            dateOrString.toString()), lineNumber, self.getName());
      }
    } else {
      if (dateOrString instanceof Date) {
        date = (Date) dateOrString;
      } else if (dateOrString instanceof Number) {
        date = new Date(((Number) dateOrString).longValue());
      } else {
        throw new IllegalArgumentException(
            format("Unsupported argument type: %s (value: %s)", dateOrString.getClass().getName(),
                dateOrString));
      }
    }
    intendedFormat = new SimpleDateFormat(format, locale);
    return new SafeString(intendedFormat.format(date));
  }

  private Object applyTemporal(final TemporalAccessor input, PebbleTemplate self,
      final Locale locale,
      int lineNumber, final String format) throws PebbleException {
    final DateTimeFormatter formatter = format != null
        ? DateTimeFormatter.ofPattern(format, locale)
        : DateTimeFormatter.ISO_DATE_TIME;
    try {
      return new SafeString(formatter.format(input));
    } catch (DateTimeException dte) {
      throw new PebbleException(dte, String.format("Could not parse the string '%s' into a date.",
          input.toString()), lineNumber, self.getName());
    }
  }

}
