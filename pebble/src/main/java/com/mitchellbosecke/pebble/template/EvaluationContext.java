package com.mitchellbosecke.pebble.template;

import java.util.Locale;

/**
 * Created by mitchell on 2016-11-13.
 */
public interface EvaluationContext {

  boolean isStrictVariables();

  Locale getLocale();

  Object getVariable(String key);
}
