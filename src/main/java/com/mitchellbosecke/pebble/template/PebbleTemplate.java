/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.template;

import com.mitchellbosecke.pebble.error.PebbleException;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

public interface PebbleTemplate {

    /**
     * Evaluate the template.
     *
     * @param writer    The results of the evaluation are written to this writer.
     * @throws PebbleException An exception with the evaluation of the template
     * @throws IOException An IO exception during the evaluation
     */
    void evaluate(Writer writer) throws PebbleException, IOException;

    /**
     * Evaluate the template.
     *
     * @param writer    The results of the evaluation are written to this writer.
     * @param locale    The locale used during the evaluation of the template.
     * @throws PebbleException An exception with the evaluation of the template
     * @throws IOException An IO exception during the evaluation
     */
    void evaluate(Writer writer, Locale locale) throws PebbleException, IOException;

    /**
     * Evaluate the template.
     *
     * @param writer    The results of the evaluation are written to this writer.
     * @param context   The variables used during the evaluation of the template.
     * @throws PebbleException An exception with the evaluation of the template
     * @throws IOException An IO exception during the evaluation
     */
    void evaluate(Writer writer, Map<String, Object> context) throws PebbleException, IOException;

    /**
     * Evaluate the template.
     *
     * @param writer    The results of the evaluation are written to this writer.
     * @param context   The variables used during the evaluation of the template.
     * @param locale    The locale used during the evaluation of the template.
     * @throws PebbleException An exception with the evaluation of the template
     * @throws IOException An IO exception during the evaluation
     */
    void evaluate(Writer writer, Map<String, Object> context, Locale locale) throws PebbleException, IOException;

}
