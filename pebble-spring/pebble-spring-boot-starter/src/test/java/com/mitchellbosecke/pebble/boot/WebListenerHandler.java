package com.mitchellbosecke.pebble.boot;

import java.util.Map;
import javax.servlet.annotation.WebListener;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;

class WebListenerHandler extends ServletComponentHandler {

  WebListenerHandler() {
    super(WebListener.class);
  }

  @Override
  protected void doHandle(Map<String, Object> attributes, BeanDefinition beanDefinition,
      BeanDefinitionRegistry registry) {
    BeanDefinitionBuilder builder = BeanDefinitionBuilder
        .rootBeanDefinition(ServletListenerRegistrationBean.class);
    builder.addPropertyValue("listener", beanDefinition);
    registry.registerBeanDefinition(beanDefinition.getBeanClassName(), builder.getBeanDefinition());
  }

}