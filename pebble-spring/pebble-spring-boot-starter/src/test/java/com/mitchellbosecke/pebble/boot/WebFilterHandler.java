package com.mitchellbosecke.pebble.boot;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.util.StringUtils;

class WebFilterHandler extends ServletComponentHandler {

  WebFilterHandler() {
    super(WebFilter.class);
  }

  @Override
  public void doHandle(Map<String, Object> attributes, BeanDefinition beanDefinition,
      BeanDefinitionRegistry registry) {
    BeanDefinitionBuilder builder = BeanDefinitionBuilder
        .rootBeanDefinition(FilterRegistrationBean.class);
    builder.addPropertyValue("asyncSupported", attributes.get("asyncSupported"));
    builder.addPropertyValue("dispatcherTypes", extractDispatcherTypes(attributes));
    builder.addPropertyValue("filter", beanDefinition);
    builder.addPropertyValue("initParameters", extractInitParameters(attributes));
    String name = determineName(attributes, beanDefinition);
    builder.addPropertyValue("name", name);
    builder.addPropertyValue("servletNames", attributes.get("servletNames"));
    builder.addPropertyValue("urlPatterns", extractUrlPatterns("urlPatterns", attributes));
    registry.registerBeanDefinition(name, builder.getBeanDefinition());
  }

  private EnumSet<DispatcherType> extractDispatcherTypes(Map<String, Object> attributes) {
    DispatcherType[] dispatcherTypes = (DispatcherType[]) attributes.get("dispatcherTypes");
    if (dispatcherTypes.length == 0) {
      return EnumSet.noneOf(DispatcherType.class);
    }
    if (dispatcherTypes.length == 1) {
      return EnumSet.of(dispatcherTypes[0]);
    }
    return EnumSet
        .of(dispatcherTypes[0], Arrays.copyOfRange(dispatcherTypes, 1, dispatcherTypes.length));
  }

  private String determineName(Map<String, Object> attributes, BeanDefinition beanDefinition) {
    return (String) (StringUtils.hasText((String) attributes.get("filterName")) ? attributes
        .get("filterName")
        : beanDefinition.getBeanClassName());
  }

}