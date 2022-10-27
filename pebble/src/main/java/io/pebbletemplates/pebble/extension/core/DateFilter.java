/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.extension.escaper.SafeString;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.*;

import static java.lang.String.format;

public class DateFilter implements Filter {

  private final List<String> argumentNames = new ArrayList<>();

  public DateFilter() {
    this.argumentNames.add("format");
    this.argumentNames.add("existingFormat");
    this.argumentNames.add("timeZone");
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
    final String timeZone = (String) args.get("timeZone");

    if (TemporalAccessor.class.isAssignableFrom(input.getClass())) {
      return this.applyTemporal((TemporalAccessor) input, self, locale, lineNumber, format, timeZone);
    }
    return this.applyDate(
            input, self, locale, lineNumber,
            format, (String) args.get("existingFormat"), timeZone);
  }

  private Object applyDate(Object dateOrString, final PebbleTemplate self, final Locale locale,
      int lineNumber, final String format, final String existingFormatString, final String timeZone)
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
    intendedFormat = new SimpleDateFormat(format == null ? "yyyy-MM-dd'T'HH:mm:ssZ" : format, locale);
    if (timeZone != null) {
      intendedFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
    }
    return new SafeString(intendedFormat.format(date));
  }

  private Object applyTemporal(final TemporalAccessor input, PebbleTemplate self,
      final Locale locale,
      int lineNumber, final String format, final String timeZone) throws PebbleException {
    DateTimeFormatter formatter = format != null
        ? DateTimeFormatter.ofPattern(format, locale)
        : DateTimeFormatter.ISO_DATE_TIME;

    ZoneId zoneId = getZoneId(input, timeZone);
    formatter = formatter.withZone(zoneId);

    try {
      return new SafeString(formatter.format(input));
    } catch (DateTimeException dte) {
      throw new PebbleException(
              dte,
              String.format("Could not format instance '%s' of type %s into a date.", input.toString(), input.getClass()),
              lineNumber,
              self.getName());
    }
  }

  private ZoneId getZoneId(TemporalAccessor input, String timeZone) {
    // First try the time zone of the input.
    ZoneId zoneId = input.query(TemporalQueries.zone());
    if (zoneId == null && timeZone != null) {
      // Fallback to time zone provided as filter argument.
      zoneId = ZoneId.of(timeZone);
    }
    if (zoneId == null) {
      // Fallback to system time zone.
      zoneId = ZoneId.systemDefault();
    }
    return zoneId;
  }

}
