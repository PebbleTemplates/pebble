package com.mitchellbosecke.pebble.boot;

import java.util.Map;
import javax.servlet.annotation.WebServlet;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.util.StringUtils;

class WebServletHandler extends ServletComponentHandler {

  WebServletHandler() {
    super(WebServlet.class);
  }

  @Override
  public void doHandle(Map<String, Object> attributes, BeanDefinition beanDefinition,
      BeanDefinitionRegistry registry) {
    BeanDefinitionBuilder builder = BeanDefinitionBuilder
        .rootBeanDefinition(ServletRegistrationBean.class);
    builder.addPropertyValue("asyncSupported", attributes.get("asyncSupported"));
    builder.addPropertyValue("initParameters", extractInitParameters(attributes));
    builder.addPropertyValue("loadOnStartup", attributes.get("loadOnStartup"));
    String name = determineName(attributes, beanDefinition);
    builder.addPropertyValue("name", name);
    builder.addPropertyValue("servlet", beanDefinition);
    builder.addPropertyValue("urlMappings", extractUrlPatterns("urlPatterns", attributes));
    registry.registerBeanDefinition(name, builder.getBeanDefinition());
  }

  private String determineName(Map<String, Object> attributes, BeanDefinition beanDefinition) {
    return (String) (StringUtils.hasText((String) attributes.get("name")) ? attributes.get("name")
        : beanDefinition.getBeanClassName());
  }

}