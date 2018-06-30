/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.TestingExtension;
import com.mitchellbosecke.pebble.extension.core.LengthFilter;
import com.mitchellbosecke.pebble.extension.core.ReplaceFilter;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.Test;

public class CoreFiltersTest {

  @Test
  public void testAbs() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ -5 | abs }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("5", writer.toString());
  }

  @Test
  public void testAbsDouble() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ -5.2 | abs }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("5.2", writer.toString());
  }

  @Test
  public void testChainedFiltersWithNullInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ null | upper | lower }}");
    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("", writer.toString());
  }

  @Test
  public void testLower() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ 'TEMPLATE' | lower }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("template", writer.toString());
  }

  @Test
  public void testLowerWithNullInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ null | lower }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("", writer.toString());
  }

  @Test
  public void testUpper() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ 'template' | upper }}");
    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("TEMPLATE", writer.toString());
  }

  @Test
  public void testUpperWithNullInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ null | upper }}");
    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("", writer.toString());
  }

  @Test
  public void testDate() throws ParseException, PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false)
        .defaultLocale(Locale.ENGLISH).build();

    String source = "{{ realDate | date('MM/dd/yyyy') }}{{ realDate | date(format) }}{{ stringDate | date('yyyy/MMMM/d','yyyy-MMMM-d') }}";

    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> context = new HashMap<>();
    DateFormat format = new SimpleDateFormat("yyyy-MMMM-d", Locale.ENGLISH);
    Date realDate = format.parse("2012-July-01");
    context.put("realDate", realDate);
    context.put("stringDate", format.format(realDate));
    context.put("format", "yyyy-MMMM-d");

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("07/01/20122012-July-12012/July/1", writer.toString());
  }

  @Test
  public void testDateJava8() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine
        .Builder()
        .loader(new StringLoader())
        .strictVariables(false)
        .defaultLocale(Locale.ENGLISH)
        .build();

    final LocalDateTime localDateTime = LocalDateTime.of(2017, 6, 30, 13, 30, 35, 0);
    final ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("GMT+0100"));
    final LocalDate localDate = localDateTime.toLocalDate();
    final LocalTime localTime = localDateTime.toLocalTime();

    StringBuilder source = new StringBuilder();
    source
        .append("{{ localDateTime | date }}")
        .append("{{ localDateTime | date('yyyy-MM-dd HH:mm:ss') }}")
        .append("{{ zonedDateTime | date('yyyy-MM-dd HH:mm:ssXXX') }}")
        .append("{{ localDate | date('yyyy-MM-dd') }}")
        .append("{{ localTime | date('HH:mm:ss') }}");

    PebbleTemplate template = pebble.getTemplate(source.toString());
    Map<String, Object> context = new HashMap<>();
    context.put("localDateTime", localDateTime);
    context.put("zonedDateTime", zonedDateTime);
    context.put("localDate", localDate);
    context.put("localTime", localTime);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals(
        "2017-06-30T13:30:352017-06-30 13:30:352017-06-30 13:30:35+01:002017-06-3013:30:35",
        writer.toString());
  }


  @Test
  public void testDateWithNamedArguments() throws ParseException, PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false)
        .defaultLocale(Locale.ENGLISH).build();

    String source = "{{ stringDate | date(existingFormat='yyyy-MMMM-d', format='yyyy/MMMM/d') }}";

    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> context = new HashMap<>();
    DateFormat format = new SimpleDateFormat("yyyy-MMMM-d", Locale.ENGLISH);
    Date realDate = format.parse("2012-July-01");
    context.put("stringDate", format.format(realDate));

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("2012/July/1", writer.toString());
  }

  @Test
  public void testDateWithNullInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ null | date(\"MM/dd/yyyy\") }}";

    PebbleTemplate template = pebble.getTemplate(source);

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("", writer.toString());
  }

  @Test
  public void testDateWithNumberInput() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ dateAsNumber | date(\"MM/dd/yyyy\") }}";

    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> context = new HashMap<>();
    context.put("dateAsNumber", 1518004210000L);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("02/07/2018", writer.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDateWithUnsupportedInput() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    String source = "{{ unsupportedDateType | date(\"MM/dd/yyyy\") }}";

    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> context = new HashMap<>();
    context.put("unsupportedDateType", TRUE);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
  }

  @Test
  public void testUrlEncode() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ 'The string ü@foo-bar' | urlencode }}");
    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("The+string+%C3%BC%40foo-bar", writer.toString());
  }

  @Test
  public void testUrlEncodeWithNullInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ null | urlencode }}");
    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("", writer.toString());
  }

  @Test
  public void testNumberFormatFilterWithFormat() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false)
        .defaultLocale(Locale.ENGLISH).build();

    PebbleTemplate template = pebble
        .getTemplate("You owe me {{ 10000.235166 | numberformat(currencyFormat) }}.");
    Map<String, Object> context = new HashMap<>();
    context.put("currencyFormat", "$#,###,###,##0.00");

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("You owe me $10,000.24.", writer.toString());
  }

  @Test
  public void testNumberFormatFilterWithNamedArgument() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false)
        .defaultLocale(Locale.US).build();

    PebbleTemplate template = pebble
        .getTemplate("You owe me {{ 10000.235166 | numberformat(format=currencyFormat) }}.");
    Map<String, Object> context = new HashMap<>();
    context.put("currencyFormat", "$#,###,###,##0.00");

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("You owe me $10,000.24.", writer.toString());
  }

  @Test
  public void testNumberFormatFilterWithNullInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ null | numberformat(currencyFormat) }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("", writer.toString());
  }

  @Test
  public void testNumberFormatFilterWithLocale() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false)
        .defaultLocale(Locale.ENGLISH).build();

    PebbleTemplate template = pebble.getTemplate("{{ 1000000 | numberformat }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("1,000,000", writer.toString());
  }

  @Test
  public void testAbbreviate() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble
        .getTemplate("{{ 'This is a test of the abbreviate filter' | abbreviate(16) }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("This is a tes...", writer.toString());
  }

  @Test
  public void testAbbreviateWithNamedArguments() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble
        .getTemplate("{{ 'This is a test of the abbreviate filter' | abbreviate(length=16) }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("This is a tes...", writer.toString());
  }

  @Test
  public void testAbbreviateWithNullInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ null | abbreviate(16) }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("", writer.toString());
  }

  @Test
  public void testAbbreviateWithSmallLength() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("{{ text | abbreviate(2)}}");
    Map<String, Object> context = new HashMap<>();
    context.put("text", "1234567");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("12", writer.toString());
  }

  @Test
  public void testCapitalize() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble
        .getTemplate("{{ 'this should be capitalized.' | capitalize }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("This should be capitalized.", writer.toString());
  }

  @Test
  public void testCapitalizeWithLeadingWhitespace() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble
        .getTemplate("{{ ' \nthis should be capitalized.' | capitalize }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals(" \nThis should be capitalized.", writer.toString());
  }

  @Test
  public void testCapitalizeWithNullInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ null | capitalize }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("", writer.toString());
  }

  @Test
  public void testCapitalizeWithEmptyString() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ '' | capitalize }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("", writer.toString());
  }

  @Test
  public void testSortFilter() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble
        .getTemplate("{% for word in words|sort %}{{ word }} {% endfor %}");
    List<String> words = new ArrayList<>();
    words.add("zebra");
    words.add("apple");
    words.add(" cat");
    words.add("123");
    words.add("Apple");
    words.add("cat");

    Map<String, Object> context = new HashMap<>();
    context.put("words", words);
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals(" cat 123 Apple apple cat zebra ", writer.toString());
  }

  @Test
  public void testRsortFilter() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble
        .getTemplate("{% for word in words|rsort %}{{ word }} {% endfor %}");
    List<String> words = new ArrayList<>();
    words.add("zebra");
    words.add("apple");
    words.add(" cat");
    words.add("123");
    words.add("Apple");
    words.add("cat");

    Map<String, Object> context = new HashMap<>();
    context.put("words", words);
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("zebra cat apple Apple 123  cat ", writer.toString());
  }

  @Test
  public void testReverseFilter() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble
        .getTemplate("{% for word in words|reverse %}{{ word }} {% endfor %}");
    List<String> words = new ArrayList<>();
    words.add("one");
    words.add("two");
    words.add("three");

    Map<String, Object> context = new HashMap<>();
    context.put("words", words);
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("three two one ", writer.toString());
  }

  @Test
  public void testTitle() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate(
        "{{ null | title }} {{ 'test' | title }} {{ 'test test' | title }} {{ 'TEST TEST' | title }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals(" Test Test Test TEST TEST", writer.toString());
  }

  @Test
  public void testTitleWithLeadingWhitespace() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ ' \ntest' | title }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals(" \nTest", writer.toString());
  }

  @Test
  public void testTrim() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble
        .getTemplate("{{ '        		This should be trimmed. 		' | trim }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("This should be trimmed.", writer.toString());
  }

  @Test
  public void testTrimWithNullInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ null | trim }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("", writer.toString());
  }

  @Test
  public void testDefault() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate(
        "{{ obj|default('ONE') }} {{ null|default('TWO') }} {{ '  ' |default('THREE') }} {{ 4 |default('FOUR') }}");
    Map<String, Object> context = new HashMap<>();
    context.put("obj", null);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("ONE TWO THREE 4", writer.toString());
  }

  /**
   * Tests if the {@link com.mitchellbosecke.pebble.extension.core.DefaultFilter} is working as
   * expected.
   *
   * @throws Exception thrown when something went wrong.
   */
  @Test
  public void testDefaultFilterWithStrictMode() throws Exception {

    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("{{ name | default('test') }}");

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("test", writer.toString());

  }

  @Test
  public void testDefaultWithNamedArguments() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ obj|default(default='ONE') }}");
    Map<String, Object> context = new HashMap<>();
    context.put("obj", null);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("ONE", writer.toString());
  }

  @Test
  public void testFirst() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ names | first }}");

    List<String> names = new ArrayList<>();
    names.add("Alex");
    names.add("Joe");
    names.add("Bob");

    Map<String, Object> context = new HashMap<>();
    context.put("names", names);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Alex", writer.toString());
  }

  @Test
  public void testFirstWithNullInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ names | first }}");

    Map<String, Object> context = new HashMap<>();
    context.put("names", null);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("", writer.toString());
  }

  @Test
  public void testFirstWithStringInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ name | first }}");

    Map<String, Object> context = new HashMap<>();
    context.put("name", "Alex");

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("A", writer.toString());
  }

  @Test
  public void testFirstWithEmptyCollection() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ names | first }}");

    Map<String, Object> context = new HashMap<>();
    context.put("names", emptyList());

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("", writer.toString());
  }

  @Test
  public void testJoin() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ names | join(',') }}");

    List<String> names = new ArrayList<>();
    names.add("Alex");
    names.add("Joe");
    names.add("Bob");

    Map<String, Object> context = new HashMap<>();
    context.put("names", names);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Alex,Joe,Bob", writer.toString());
  }

  @Test
  public void testJoinWithoutGlue() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ names | join }}");

    List<String> names = new ArrayList<>();
    names.add("Alex");
    names.add("Joe");
    names.add("Bob");

    Map<String, Object> context = new HashMap<>();
    context.put("names", names);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("AlexJoeBob", writer.toString());
  }

  @Test
  public void testJoinWithNumbers() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ numbers | join(',') }}");

    List<Integer> numbers = new ArrayList<>();
    numbers.add(1);
    numbers.add(2);
    numbers.add(3);

    Map<String, Object> context = new HashMap<>();
    context.put("numbers", numbers);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("1,2,3", writer.toString());
  }

  @Test
  public void testJoinWithNullInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ null | join(',') }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("", writer.toString());
  }

  @Test
  public void testJoinWithStringArray() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ names | join(',') }}");

    String[] names = new String[]{"Alex", "Joe", "Bob"};

    Map<String, Object> context = new HashMap<>();
    context.put("names", names);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Alex,Joe,Bob", writer.toString());
  }

  @Test
  public void testJoinWithStringArrayWithoutGlue() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ names | join }}");

    String[] names = new String[]{"Alex", "Joe", "Bob"};

    Map<String, Object> context = new HashMap<>();
    context.put("names", names);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("AlexJoeBob", writer.toString());
  }

  @Test
  public void testJoinWithNumbersArray() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ numbers | join(',') }}");

    int[] numbers = new int[]{1, 2, 3};

    Map<String, Object> context = new HashMap<>();
    context.put("numbers", numbers);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("1,2,3", writer.toString());
  }

  @Test
  public void testJoinWithEmptyNumbersArray() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ numbers | join(',') }}");

    int[] numbers = new int[0];

    Map<String, Object> context = new HashMap<>();
    context.put("numbers", numbers);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("", writer.toString());
  }

  @Test
  public void testJoinWithFloatArray() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ numbers | join(',') }}");

    float[] numbers = new float[]{1.0f, 2.5f, 3.0f};

    Map<String, Object> context = new HashMap<>();
    context.put("numbers", numbers);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("1.0,2.5,3.0", writer.toString());
  }

  @Test
  public void testLast() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ names | last }}");

    List<String> names = new ArrayList<>();
    names.add("Alex");
    names.add("Joe");
    names.add("Bob");

    Map<String, Object> context = new HashMap<>();
    context.put("names", names);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Bob", writer.toString());
  }

  @Test
  public void testLastWithNullInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ null | last }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("", writer.toString());
  }

  @Test
  public void testLastWithStringInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ name | last }}");

    Map<String, Object> context = new HashMap<>();
    context.put("name", "Alex");

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("x", writer.toString());
  }

  @Test
  public void testLastWithArrayInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ names | last }}");

    Map<String, Object> context = new HashMap<>();
    context.put("names", new String[]{"FirstName", "FamilyName"});

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("FamilyName", writer.toString());
  }

  @Test
  public void testLastWithPrimitiveArrayInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ ages | last }}");

    Map<String, Object> context = new HashMap<>();
    context.put("ages", new int[]{28, 30});

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("30", writer.toString());
  }

  @Test
  public void testFirstWithArrayInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ names | first }}");

    Map<String, Object> context = new HashMap<>();
    context.put("names", new String[]{"FirstName", "FamilyName"});

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("FirstName", writer.toString());
  }

  @Test
  public void testFirstWithPrimitiveArrayInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ ages | first }}");

    Map<String, Object> context = new HashMap<>();
    context.put("ages", new int[]{28, 30});

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("28", writer.toString());
  }

  public class User {

    private final String username;

    public User(String username) {
      this.username = username;
    }

    public String getUsername() {
      return this.username;
    }
  }

  @Test
  public void testSliceWithNullInput() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ null | slice }}");

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("", writer.toString());
  }

  @Test
  public void testSliceWithDefaultArgs() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ name | slice }}");

    Map<String, Object> context = new HashMap<>();
    context.put("name", "Alex");

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Alex", writer.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSliceWithInvalidFirstArg() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ name | slice(-1) }}");

    Map<String, Object> context = new HashMap<>();
    context.put("name", "Alex");

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
  }

  @Test
  public void testSliceWithIntegerArguments() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble
        .getTemplate("{{ 'abcdefghijklmnopqrstuvwxyz' | slice(from, to) }}");

    Map<String, Object> context = new HashMap<>();
    context.put("from", new Integer(2));
    context.put("to", new Integer(4));

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("cd", writer.toString());
  }

  @Test(expected = PebbleException.class)
  public void testSliceWithInvalidSecondArg() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ name | slice(0,-1) }}");

    Map<String, Object> context = new HashMap<>();
    context.put("name", "Alex");

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
  }

  @Test(expected = PebbleException.class)
  public void testSliceWithInvalidSecondArg2() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ name | slice(0,1000) }}");

    Map<String, Object> context = new HashMap<>();
    context.put("name", "Alex");

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
  }

  @Test
  public void testSliceWithString() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ name | slice(2,5) }}");

    Map<String, Object> context = new HashMap<>();
    context.put("name", "Alexander");

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("exa", writer.toString());
  }

  @Test
  public void testSliceWithList() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ names | slice(2,5) }}");

    List<String> names = new ArrayList<>();
    names.add("Alex");
    names.add("Joe");
    names.add("Bob");
    names.add("Sarah");
    names.add("Mary");
    names.add("Marge");
    Map<String, Object> context = new HashMap<>();
    context.put("names", names);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("[Bob, Sarah, Mary]", writer.toString());
  }

  @Test
  public void testSliceWithStringArray() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{% set n = names | slice(2,5) %}{{ n[0] }}");

    String[] names = new String[]{"Alex", "Joe", "Bob", "Sarah", "Mary", "Marge"};
    Map<String, Object> context = new HashMap<>();
    context.put("names", names);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Bob", writer.toString());
  }

  @Test
  public void testSliceWithPrimitivesArray() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{% set p = primitives | slice(2,5) %}{{ p[0] }}");
    Map<String, Object> context = new HashMap<>();
    Writer writer;

    // boolean
    writer = new StringWriter();
    boolean[] booleans = new boolean[]{true, false, true, false, true, false};
    context.put("primitives", booleans);
    template.evaluate(writer, context);
    assertEquals("true", writer.toString());

    // byte
    writer = new StringWriter();
    byte[] bytes = new byte[]{0, 1, 2, 3, 4, 5};
    context.put("primitives", bytes);
    template.evaluate(writer, context);
    assertEquals("2", writer.toString());

    // char
    writer = new StringWriter();
    char[] chars = new char[]{'a', 'b', 'c', 'd', 'e', 'f'};
    context.put("primitives", chars);
    template.evaluate(writer, context);
    assertEquals("c", writer.toString());

    // double
    writer = new StringWriter();
    double[] doubles = new double[]{0.0d, 1.0d, 2.0d, 3.0d, 4.0d, 5.0d};
    context.put("primitives", doubles);
    template.evaluate(writer, context);
    assertEquals("2.0", writer.toString());

    // float
    writer = new StringWriter();
    float[] floats = new float[]{0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
    context.put("primitives", floats);
    template.evaluate(writer, context);
    assertEquals("2.0", writer.toString());

    // int
    writer = new StringWriter();
    int[] ints = new int[]{0, 1, 2, 3, 4, 5};
    context.put("primitives", ints);
    template.evaluate(writer, context);
    assertEquals("2", writer.toString());

    // long
    writer = new StringWriter();
    long[] longs = new long[]{0, 1, 2, 3, 4, 5};
    context.put("primitives", longs);
    template.evaluate(writer, context);
    assertEquals("2", writer.toString());

    // short
    writer = new StringWriter();
    short[] shorts = new short[]{0, 1, 2, 3, 4, 5};
    context.put("primitives", shorts);
    template.evaluate(writer, context);
    assertEquals("2", writer.toString());
  }

  @Test(expected = PebbleException.class)
  public void testSliceWithInvalidInputType() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ names | slice(2,5) }}");

    Map<String, Object> context = new HashMap<>();
    context.put("names", Long.valueOf(1));

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
  }

  /**
   * Tests {@link LengthFilter} with different inputs.
   */
  @Test
  public void testLengthFilterInputs() {
    LengthFilter filter = new LengthFilter();

    assertEquals(0, filter.apply(null, null, null, null, 0));
    assertEquals(4, filter.apply("test", null, null, null, 0));
    assertEquals(0, filter.apply(Collections.EMPTY_LIST, null, null, null, 0));
    assertEquals(2, filter.apply(Arrays.asList("tttt", "ssss"), null, null, null, 0));
    assertEquals(2, filter.apply(Arrays.asList("tttt", "ssss").iterator(), null, null, null, 0));
    Map<String, String> test = new HashMap<>();
    test.put("test", "test");
    test.put("other", "other");
    test.put("and_other", "other");
    assertEquals(3, filter.apply(test, null, null, null, 0));
  }

  /**
   * Tests {@link LengthFilter} if the length filter is working within templates.
   */
  @Test
  public void testLengthFilterInTemplate() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ names | length }}");

    Map<String, Object> context = new HashMap<>();
    context.put("names", "test");

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("4", writer.toString());
  }

  /**
   * Tests {@link ReplaceFilter} if the length filter is working within templates.
   */
  @Test
  public void testReplaceFilterInTemplate() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble
        .getTemplate(
            "{{ \"I like %this% and %that%.\"|replace({'%this%': foo, '%that%': \"bar\"}) }}");

    Map<String, Object> context = new HashMap<>();
    context.put("foo", "foo");

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("I like foo and bar.", writer.toString());
  }

  @Test
  public void testMergeOk() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .extension(new TestingExtension()).strictVariables(false).build();

    PebbleTemplate template = pebble
        .getTemplate(
            "{{{'one':1}|merge({'two':2})|mapToString}} {%set m1 = {'one':1}|merge(['two'])%}{{m1['two']}} {{[1]|merge([2])|listToString}} {%set l1 = [1]|merge({'two':2})%}{{l1[1].value}} {{arr1|merge(arr2)|arrayToString}}");

    Map<String, Object> context = new HashMap<>();
    context.put("arr1", new int[]{1});
    context.put("arr2", new int[]{2});

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("{one=1, two=2} two [1,2] 2 [1,2]", writer.toString());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testMergeMapWithStringAndFail() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ {'one':1}|merge('No way!') }}");

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
  }

  @Test(expected = PebbleException.class)
  public void testMergeListWithStringAndFail() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ [1]|merge('No way!') }}");

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
  }

  @Test(expected = PebbleException.class)
  public void testMergeDifferentArraysAndFail() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ arr1|merge(arr2) }}");

    Map<String, Object> context = new HashMap<>();
    context.put("arr1", new int[]{1});
    context.put("arr2", new String[]{"2"});

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
  }

}
