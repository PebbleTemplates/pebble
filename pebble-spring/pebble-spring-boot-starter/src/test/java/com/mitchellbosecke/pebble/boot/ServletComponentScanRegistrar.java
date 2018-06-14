package com.mitchellbosecke.pebble.boot;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

class ServletComponentScanRegistrar implements ImportBeanDefinitionRegistrar {

  private static final String BEAN_NAME = "servletComponentRegisteringPostProcessor";

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
      BeanDefinitionRegistry registry) {
    Set<String> packagesToScan = getPackagesToScan(importingClassMetadata);
    if (registry.containsBeanDefinition(BEAN_NAME)) {
      updatePostProcessor(registry, packagesToScan);
    } else {
      addPostProcessor(registry, packagesToScan);
    }
  }

  private void updatePostProcessor(BeanDefinitionRegistry registry, Set<String> packagesToScan) {
    BeanDefinition definition = registry.getBeanDefinition(BEAN_NAME);
    ValueHolder constructorArguments = definition.getConstructorArgumentValues()
        .getGenericArgumentValue(Set.class);
    @SuppressWarnings("unchecked")
    Set<String> mergedPackages = (Set<String>) constructorArguments.getValue();
    mergedPackages.addAll(packagesToScan);
    constructorArguments.setValue(mergedPackages);
  }

  private void addPostProcessor(BeanDefinitionRegistry registry, Set<String> packagesToScan) {
    GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
    beanDefinition.setBeanClass(ServletComponentRegisteringPostProcessor.class);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(packagesToScan);
    beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
    registry.registerBeanDefinition(BEAN_NAME, beanDefinition);
  }

  private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
    AnnotationAttributes attributes = AnnotationAttributes
        .fromMap(metadata.getAnnotationAttributes(ServletComponentScan.class.getName()));
    String[] value = attributes.getStringArray("value");
    String[] basePackages = attributes.getStringArray("basePackages");
    Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
    if (!ObjectUtils.isEmpty(value)) {
      Assert.state(ObjectUtils.isEmpty(basePackages),
          "@ServletComponentScan basePackages and value attributes are" + " mutually exclusive");
    }
    Set<String> packagesToScan = new LinkedHashSet<String>();
    packagesToScan.addAll(Arrays.asList(value));
    packagesToScan.addAll(Arrays.asList(basePackages));
    for (Class<?> basePackageClass : basePackageClasses) {
      packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
    }
    if (packagesToScan.isEmpty()) {
      return Collections.singleton(ClassUtils.getPackageName(metadata.getClassName()));
    }
    return packagesToScan;
  }

}