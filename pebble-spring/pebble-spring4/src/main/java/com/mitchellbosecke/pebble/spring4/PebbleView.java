/*
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.spring4;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.spring4.context.Beans;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractTemplateView;

public class PebbleView extends AbstractTemplateView {

  public static final String REQUEST_VARIABLE_NAME = "request";
  public static final String RESPONSE_VARIABLE_NAME = "response";
  public static final String SESSION_VARIABLE_NAME = "session";

  private static final String BEANS_VARIABLE_NAME = "beans";
  private static final int NANO_PER_SECOND = 1000000;
  /**
   * <p>
   * TIMER logger. This logger will output the time required for executing each template processing
   * operation.
   * </p>
   * <p>
   * The value of this constant is
   * <tt>com.mitchellbosecke.pebble.spring.PebbleView.timer</tt>. This allows
   * you to set a specific configuration and/or appenders for timing info at your logging system
   * configuration.
   * </p>
   */
  private static final Logger TIMER_LOGGER = LoggerFactory
      .getLogger(PebbleView.class.getName() + ".timer");

  private String characterEncoding = "UTF-8";
  private PebbleEngine pebbleEngine;
  private String templateName;

  @Override
  protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    long startNanoTime = System.nanoTime();

    this.setCharacterEncoding(response);
    this.addVariablesToModel(model, request, response);
    this.evaluateTemplate(model, request, response);
    this.logElapsedTime(startNanoTime, request);
  }

  private void setCharacterEncoding(HttpServletResponse response) {
    if (this.characterEncoding != null) {
      response.setCharacterEncoding(this.characterEncoding);
    }
  }

  private void addVariablesToModel(Map<String, Object> model, HttpServletRequest request,
      HttpServletResponse response) {
    model.put(BEANS_VARIABLE_NAME, new Beans(this.getApplicationContext()));
    model.put(REQUEST_VARIABLE_NAME, request);
    model.put(RESPONSE_VARIABLE_NAME, response);
    model.put(SESSION_VARIABLE_NAME, request.getSession(false));
  }

  private void evaluateTemplate(Map<String, Object> model, HttpServletRequest request,
      HttpServletResponse response) throws IOException, PebbleException {
    Locale locale = RequestContextUtils.getLocale(request);

    Writer writer = response.getWriter();
    try {
      PebbleTemplate template = this.pebbleEngine.getTemplate(this.templateName);
      template.evaluate(writer, model, locale);
    } finally {
      writer.flush();
    }
  }

  private void logElapsedTime(long startNanoTime, HttpServletRequest request) {
    if (TIMER_LOGGER.isDebugEnabled()) {
      Locale locale = RequestContextUtils.getLocale(request);
      long endNanoTime = System.nanoTime();

      long elapsed = endNanoTime - startNanoTime;
      long elapsedMs = elapsed / NANO_PER_SECOND;
      TIMER_LOGGER
          .debug("Pebble template \"{}\" with locale {} processed in {} nanoseconds (approx. {}ms)",
              this.templateName, locale, elapsed, elapsedMs);
    }
  }

  public void setCharacterEncoding(String characterEncoding) {
    this.characterEncoding = characterEncoding;
  }

  public void setPebbleEngine(PebbleEngine pebbleEngine) {
    this.pebbleEngine = pebbleEngine;
  }

  public void setTemplateName(String name) {
    this.templateName = name;
  }
}