/*
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.spring.servlet;

import io.pebbletemplates.pebble.loader.Loader;
import io.pebbletemplates.pebble.PebbleEngine;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

public class PebbleViewResolver extends AbstractTemplateViewResolver implements InitializingBean {

  private String characterEncoding = "UTF-8";
  private final PebbleEngine pebbleEngine;

  public PebbleViewResolver(PebbleEngine pebbleEngine) {
    this.pebbleEngine = pebbleEngine;
    this.setViewClass(this.requiredViewClass());
  }

  @Override
  public void afterPropertiesSet() {
    Loader<?> templateLoader = this.pebbleEngine.getLoader();
    templateLoader.setPrefix(this.getPrefix());
    templateLoader.setSuffix(this.getSuffix());
  }

  public void setCharacterEncoding(String characterEncoding) {
    this.characterEncoding = characterEncoding;
  }

  @Override
  protected AbstractUrlBasedView buildView(String viewName) throws Exception {
    PebbleView view = (PebbleView) super.buildView(viewName);
    view.setTemplateName(viewName);
    view.setPebbleEngine(this.pebbleEngine);
    view.setCharacterEncoding(this.characterEncoding);

    return view;
  }

  @Override
  protected Class<?> requiredViewClass() {
    return PebbleView.class;
  }
}
