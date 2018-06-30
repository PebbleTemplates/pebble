/*
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.spring.extension.function;

import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;

/**
 * <p>
 * Function available to templates in Spring MVC applications in order to resolve message in the
 * application context
 * </p>
 *
 * @author Eric Bussieres
 */
public class MessageSourceFunction implements Function {

  public static final String FUNCTION_NAME = "message";

  private final MessageSource messageSource;

  public MessageSourceFunction(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context,
      int lineNumber) {
    String key = this.extractKey(args);
    List<Object> arguments = this.extractArguments(args);
    Locale locale = context.getLocale();

    return this.messageSource.getMessage(key, arguments.toArray(), "???" + key + "???", locale);
  }

  private List<Object> extractArguments(Map<String, Object> args) {
    int i = 1;
    List<Object> arguments = new ArrayList<>();
    while (args.containsKey(String.valueOf(i))) {
      Object param = args.get(String.valueOf(i));
      arguments.add(param);
      i++;
    }
    return arguments;
  }

  private String extractKey(Map<String, Object> args) {
    return (String) args.get("0");
  }

  @Override
  public List<String> getArgumentNames() {
    return null;
  }
}
