package io.pebbletemplates.macro;

import io.pebbletemplates.extension.Filter;
import io.pebbletemplates.template.EvaluationContext;
import io.pebbletemplates.template.PebbleTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestFilter implements Filter {

  static int counter = 0;

  public static final String FILTER_NAME = "testfilter";

  @Override
  public List<String> getArgumentNames() {
    return Collections.singletonList("content");
  }

  @Override
  public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
                      EvaluationContext context, int lineNumber) {
    String content = (String) input;
    counter++;
    content = content + "?" + "Hello";
    return content;
  }

  public static int getCounter() {
    return counter;
  }

}
