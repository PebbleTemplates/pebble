/*
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.spring.extension;

import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.spring.extension.function.HrefFunction;
import com.mitchellbosecke.pebble.spring.extension.function.MessageSourceFunction;
import com.mitchellbosecke.pebble.spring.extension.function.bindingresult.GetAllErrorsFunction;
import com.mitchellbosecke.pebble.spring.extension.function.bindingresult.GetFieldErrorsFunction;
import com.mitchellbosecke.pebble.spring.extension.function.bindingresult.GetGlobalErrorsFunction;
import com.mitchellbosecke.pebble.spring.extension.function.bindingresult.HasErrorsFunction;
import com.mitchellbosecke.pebble.spring.extension.function.bindingresult.HasFieldErrorsFunction;
import com.mitchellbosecke.pebble.spring.extension.function.bindingresult.HasGlobalErrorsFunction;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * <p>
 * Extension for PebbleEngine to add spring functionality
 * </p>
 *
 * @author Eric Bussieres
 */
public class SpringExtension extends AbstractExtension {

  @Autowired
  private MessageSource messageSource;

  @Override
  public Map<String, Function> getFunctions() {
    Map<String, Function> functions = new HashMap<>();
    functions
        .put(MessageSourceFunction.FUNCTION_NAME, new MessageSourceFunction(this.messageSource));
    functions.put(HasErrorsFunction.FUNCTION_NAME, new HasErrorsFunction());
    functions.put(HasGlobalErrorsFunction.FUNCTION_NAME, new HasGlobalErrorsFunction());
    functions.put(HasFieldErrorsFunction.FUNCTION_NAME, new HasFieldErrorsFunction());
    functions.put(GetAllErrorsFunction.FUNCTION_NAME, new GetAllErrorsFunction(this.messageSource));
    functions.put(GetGlobalErrorsFunction.FUNCTION_NAME,
        new GetGlobalErrorsFunction(this.messageSource));
    functions
        .put(GetFieldErrorsFunction.FUNCTION_NAME, new GetFieldErrorsFunction(this.messageSource));
    functions.put(HrefFunction.FUNCTION_NAME, new HrefFunction());
    return functions;
  }
}
