package com.mitchellbosecke.pebble.boot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;

class ServletComponentRegisteringPostProcessor implements BeanFactoryPostProcessor,
    ApplicationContextAware {

  private static final List<ServletComponentHandler> HANDLERS;

  static {
    List<ServletComponentHandler> handlers = new ArrayList<ServletComponentHandler>();
    handlers.add(new WebServletHandler());
    handlers.add(new WebFilterHandler());
    handlers.add(new WebListenerHandler());
    HANDLERS = Collections.unmodifiableList(handlers);
  }

  private final Set<String> packagesToScan;

  private ApplicationContext applicationContext;

  ServletComponentRegisteringPostProcessor(Set<String> packagesToScan) {
    this.packagesToScan = packagesToScan;
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    if (isRunningInEmbeddedContainer()) {
      ClassPathScanningCandidateComponentProvider componentProvider = createComponentProvider();
      for (String packageToScan : this.packagesToScan) {
        scanPackage(componentProvider, packageToScan);
      }
    }
  }

  private void scanPackage(ClassPathScanningCandidateComponentProvider componentProvider,
      String packageToScan) {
    for (BeanDefinition candidate : componentProvider.findCandidateComponents(packageToScan)) {
      if (candidate instanceof ScannedGenericBeanDefinition) {
        for (ServletComponentHandler handler : HANDLERS) {
          handler.handle(((ScannedGenericBeanDefinition) candidate),
              (BeanDefinitionRegistry) this.applicationContext);
        }
      }
    }
  }

  private boolean isRunningInEmbeddedContainer() {
    return this.applicationContext instanceof EmbeddedWebApplicationContext
        && ((EmbeddedWebApplicationContext) this.applicationContext).getServletContext() == null;
  }

  private ClassPathScanningCandidateComponentProvider createComponentProvider() {
    ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(
        false);
    for (ServletComponentHandler handler : HANDLERS) {
      componentProvider.addIncludeFilter(handler.getTypeFilter());
    }
    return componentProvider;
  }

  Set<String> getPackagesToScan() {
    return Collections.unmodifiableSet(this.packagesToScan);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

}