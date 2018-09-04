/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.extension.escaper;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.utils.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.unbescape.css.CssEscape;
import org.unbescape.html.HtmlEscape;
import org.unbescape.javascript.JavaScriptEscape;
import org.unbescape.json.JsonEscape;
import org.unbescape.uri.UriEscape;

public class EscapeFilter implements Filter {

  public static final String HTML_ESCAPE_STRATEGY = "html";
  public static final String JAVASCRIPT_ESCAPE_STRATEGY = "js";
  public static final String CSS_ESCAPE_STRATEGY = "css";
  public static final String URL_PARAM_ESCAPE_STRATEGY = "url_param";
  public static final String JSON_ESCAPE_STRATEGY = "json";

  private String defaultStrategy = HTML_ESCAPE_STRATEGY;

  private final List<String> argumentNames = new ArrayList<>();

  private final Map<String, EscapingStrategy> strategies = new HashMap<>();

  public EscapeFilter() {
    this.buildDefaultStrategies();
    this.argumentNames.add("strategy");
  }

  private void buildDefaultStrategies() {
    this.strategies.put(HTML_ESCAPE_STRATEGY, HtmlEscape::escapeHtml4Xml);
    this.strategies.put(JAVASCRIPT_ESCAPE_STRATEGY, JavaScriptEscape::escapeJavaScript);
    this.strategies.put(CSS_ESCAPE_STRATEGY, CssEscape::escapeCssIdentifier);
    this.strategies.put(URL_PARAM_ESCAPE_STRATEGY, UriEscape::escapeUriQueryParam);
    this.strategies.put(JSON_ESCAPE_STRATEGY, JsonEscape::escapeJson);
  }

  @Override
  public List<String> getArgumentNames() {
    return this.argumentNames;
  }

  @Override
  public Object apply(Object inputObject, Map<String, Object> args, PebbleTemplate self,
      EvaluationContext context, int lineNumber) throws PebbleException {
    if (inputObject == null || inputObject instanceof SafeString) {
      return inputObject;
    }
    String input = StringUtils.toString(inputObject);

    String strategy = this.defaultStrategy;

    if (args.get("strategy") != null) {
      strategy = (String) args.get("strategy");
    }

    if (!this.strategies.containsKey(strategy)) {
      throw new PebbleException(null, String.format("Unknown escaping strategy [%s]", strategy),
          lineNumber, self.getName());
    }

    return new SafeString(this.strategies.get(strategy).escape(input));
  }

  public String getDefaultStrategy() {
    return this.defaultStrategy;
  }

  public void setDefaultStrategy(String defaultStrategy) {
    this.defaultStrategy = defaultStrategy;
  }

  public void addEscapingStrategy(String name, EscapingStrategy strategy) {
    this.strategies.put(name, strategy);
  }
}
