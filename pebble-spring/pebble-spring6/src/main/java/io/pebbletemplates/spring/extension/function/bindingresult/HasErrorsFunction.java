/*
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.spring.extension.function.bindingresult;

import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.util.Map;
import org.springframework.validation.BindingResult;

/**
 * <p>
 * Function available to templates in Spring MVC applications in order to access the BindingResult
 * of a form
 * </p>
 *
 * @author Eric Bussieres
 */
public class HasErrorsFunction extends BaseBindingResultFunction {

  public static final String FUNCTION_NAME = "hasErrors";

  public HasErrorsFunction() {
    super(PARAM_FORM_NAME);
  }

  @Override
  public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context,
      int lineNumber) {
    String formName = (String) args.get(PARAM_FORM_NAME);

    BindingResult bindingResult = this.getBindingResult(formName, context);
    return bindingResult != null && bindingResult.hasErrors();
  }
}
