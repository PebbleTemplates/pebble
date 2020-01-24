package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.error.ClassAccessException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

public class MethodAccessValidator {

  private static final String[] FORBIDDEN_OBJECT_CLASS_METHODS = {"getClass", "wait", "notify",
      "notifyAll"};

  void checkIfAccessIsAllowed(Object object, Method member, String filename, int lineNumber) {
    if (isAccessUnsafe(object, member)) {
      throw new ClassAccessException(member, filename, lineNumber);
    }
  }

  protected boolean isAccessUnsafe(Object object, Method member) {
    return object instanceof Class || object instanceof Runtime || object instanceof Thread
        || object instanceof ThreadGroup || object instanceof System
        || object instanceof AccessibleObject || isUnsafeMethodOfObjectClass(member);
  }

  private boolean isUnsafeMethodOfObjectClass(Method member) {
    return isAnyOfMethods(member, "java.lang.Object", FORBIDDEN_OBJECT_CLASS_METHODS);
  }

  private boolean isAnyOfMethods(Method member, String declaringClass, String... methods) {
    if (!hasDeclaringClass(member, declaringClass)) {
      return false;
    }

    for (String method : methods) {
      if (isMethodWithName(member, method)) {
        return true;
      }
    }
    return false;
  }

  private boolean hasDeclaringClass(Method member, String declaringClass) {
    return member.getDeclaringClass().getName().equals(declaringClass);
  }

  private boolean isMethodWithName(Method member, String method) {
    return member.getName().equals(method);
  }
}
