package com.mitchellbosecke.pebble.attributes.methodaccess;

import java.lang.reflect.Constructor;

public class InstanceProvider {

  Object createObject(Class<?> declaringClass) throws NoSuchFieldException, NoSuchMethodException {
    try {
      Constructor<?> constructor = declaringClass.getDeclaredConstructor();
      constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (Exception e) {
      switch (declaringClass.getName()) {
        case "java.lang.reflect.Field":
          return Foo.class.getDeclaredField("x");
        case "java.lang.reflect.Method":
          return Foo.class.getDeclaredMethod("getX", (Class<?>[]) null);
        case "java.lang.Class":
          return Foo.class;
        case "java.lang.reflect.Constructor":
          return Foo.class.getDeclaredConstructor();
        case "java.lang.Integer":
          return Integer.valueOf(1);
        default:
          throw new RuntimeException(
              String.format("No object instance defined for class %s", declaringClass.getName()));
      }
    }
  }
}