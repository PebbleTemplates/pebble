package com.mitchellbosecke.pebble.spring.extension.function;

import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.spring.util.ViewUtils;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;

/**
 * Pebble function which adds the context path to the given url
 *
 * @author Eric Bussieres
 */
public class HrefFunction implements Function {

  public static final String FUNCTION_NAME = "href";

  protected static final String PARAM_URL = "url";

  protected List<String> argumentNames;
  private String contextPath;

  /**
   * Constructor
   */
  public HrefFunction() {
    this.argumentNames = new ArrayList<>();
    this.argumentNames.add(PARAM_URL);
  }

  /**
   * {@inheritDoc}
   *
   * @see Function#execute(Map, PebbleTemplate, EvaluationContext, int)
   */
  @Override
  public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context,
      int lineNumber) {
    StringBuffer result = new StringBuffer();

    result.append(this.getContextPath());
    this.addUrlParameter(args, result);

    return result.toString();
  }

  private void addUrlParameter(Map<String, Object> args, StringBuffer result) {
    String url = (String) args.get(PARAM_URL);
    if (StringUtils.hasText(url)) {
      result.append(url);
    }
  }

  private String getContextPath() {
    if (this.contextPath == null) {
      this.contextPath = ViewUtils.getRequest().getContextPath();
    }

    return this.contextPath;
  }

  /**
   * {@inheritDoc}
   *
   * @see com.mitchellbosecke.pebble.extension.NamedArguments#getArgumentNames()
   */
  @Override
  public List<String> getArgumentNames() {
    return this.argumentNames;
  }
}
