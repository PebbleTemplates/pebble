/*
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.spring4;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.Loader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

public class PebbleViewResolver extends AbstractTemplateViewResolver implements InitializingBean {

  private String characterEncoding = "UTF-8";
  private PebbleEngine pebbleEngine;

  public PebbleViewResolver() {
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

  @Required
  public void setPebbleEngine(PebbleEngine pebbleEngine) {
    this.pebbleEngine = pebbleEngine;
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
