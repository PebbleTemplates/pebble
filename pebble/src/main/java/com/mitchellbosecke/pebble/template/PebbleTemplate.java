/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.template;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

/**
 * A template object constructed by an instance of {@link com.mitchellbosecke.pebble.PebbleEngine}.
 * A template by itself is stateless and can therefore be re-used over and over to provide different
 * outputs depending on the variables that are provided at the time of evaluation.
 */
public interface PebbleTemplate {

  /**
   * Evaluate the template without any provided variables. This will use the default locale provided
   * by the {@link com.mitchellbosecke.pebble.PebbleEngine}.
   *
   * @param writer The results of the evaluation are written to this writer.
   * @throws IOException An IO exception during the evaluation
   */
  void evaluate(Writer writer) throws IOException;

  /**
   * Evaluate the template with a particular locale but without any provided variables.
   *
   * @param writer The results of the evaluation are written to this writer.
   * @param locale The locale used during the evaluation of the template.
   * @throws IOException An IO exception during the evaluation
   */
  void evaluate(Writer writer, Locale locale) throws IOException;

  /**
   * Evaluate the template with a set of variables and the default locale provided by the {@link
   * com.mitchellbosecke.pebble.PebbleEngine}
   *
   * @param writer The results of the evaluation are written to this writer.
   * @param context The variables used during the evaluation of the template.
   * @throws IOException An IO exception during the evaluation
   */
  void evaluate(Writer writer, Map<String, Object> context) throws IOException;

  /**
   * Evaluate the template with a particular locale and a set of variables.
   *
   * @param writer The results of the evaluation are written to this writer.
   * @param context The variables used during the evaluation of the template.
   * @param locale The locale used during the evaluation of the template.
   * @throws IOException An IO exception during the evaluation
   */
  void evaluate(Writer writer, Map<String, Object> context, Locale locale) throws IOException;

  /**
   * Evaluate the template but only render the contents of a specific block.
   *
   * @param blockName The name of the template block to return.
   * @param writer The results of the evaluation are written to this writer.
   * @throws IOException An IO exception during the evaluation
   */
  void evaluateBlock(String blockName, Writer writer) throws IOException;

  /**
   * Evaluate the template but only render the contents of a specific block.
   *
   * @param blockName The name of the template block to return.
   * @param writer The results of the evaluation are written to this writer.
   * @param locale The locale used during the evaluation of the template.
   * @throws IOException An IO exception during the evaluation
   */
  void evaluateBlock(String blockName, Writer writer, Locale locale) throws IOException;

  /**
   * Evaluate the template but only render the contents of a specific block.
   *
   * @param blockName The name of the template block to return.
   * @param writer The results of the evaluation are written to this writer.
   * @param context The variables used during the evaluation of the template.
   * @throws IOException An IO exception during the evaluation
   */
  void evaluateBlock(String blockName, Writer writer, Map<String, Object> context)
      throws IOException;

  /**
   * Evaluate the template but only render the contents of a specific block.
   *
   * @param blockName The name of the template block to return.
   * @param writer The results of the evaluation are written to this writer.
   * @param context The variables used during the evaluation of the template.
   * @param locale The locale used during the evaluation of the template.
   * @throws IOException An IO exception during the evaluation
   */
  void evaluateBlock(String blockName, Writer writer, Map<String, Object> context, Locale locale)
      throws IOException;

  /**
   * Returns the name of the template
   *
   * @return The name of the template
   */
  String getName();

}
