/*
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.spring.extension.function.bindingresult;

import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

/**
 * <p>
 * Function available to templates in Spring MVC applications in order to access the BindingResult
 * of a form
 * </p>
 *
 * @author Eric Bussieres
 */
public class GetAllErrorsFunction extends BaseBindingResultFunction {

  public static final String FUNCTION_NAME = "getAllErrors";

  private final MessageSource messageSource;

  public GetAllErrorsFunction(MessageSource messageSource) {
    super(PARAM_FORM_NAME);
    this.messageSource = messageSource;
  }

  @Override
  public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context,
      int lineNumber) {
    String formName = (String) args.get(PARAM_FORM_NAME);

    Locale locale = context.getLocale();
    BindingResult bindingResult = this.getBindingResult(formName, context);

    return this.constructErrorMessage(locale, bindingResult);
  }

  private List<String> constructErrorMessage(Locale locale, BindingResult bindingResult) {
    List<String> errorMessages = new ArrayList<>();
    if (bindingResult != null) {
      for (ObjectError error : bindingResult.getAllErrors()) {
        String msg = this.messageSource.getMessage(error, locale);
        errorMessages.add(msg);
      }
    }
    return errorMessages;
  }
}
