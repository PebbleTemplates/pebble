package com.mitchellbosecke.pebble.spring.reactive;

import com.mitchellbosecke.pebble.PebbleEngine;
import org.springframework.web.reactive.result.view.AbstractUrlBasedView;
import org.springframework.web.reactive.result.view.UrlBasedViewResolver;

public class PebbleReactiveViewResolver extends UrlBasedViewResolver {

  private final PebbleEngine pebbleEngine;

  public PebbleReactiveViewResolver(PebbleEngine pebbleEngine) {
    this.setViewClass(this.requiredViewClass());
    this.pebbleEngine = pebbleEngine;
  }

  @Override
  protected AbstractUrlBasedView createView(String viewName) {
    PebbleReactiveView view = (PebbleReactiveView) super.createView(viewName);
    view.setPebbleEngine(this.pebbleEngine);
    view.setTemplateName(viewName);

    return view;
  }

  @Override
  protected Class<?> requiredViewClass() {
    return PebbleReactiveView.class;
  }
}
