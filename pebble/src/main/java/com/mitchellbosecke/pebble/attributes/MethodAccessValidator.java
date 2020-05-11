package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.error.ClassAccessException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

public class MethodAccessValidator {

  private static final String[] FORBIDDEN_OBJECT_CLASS_METHODS = {"getClass",
      "wait",
      "notify",
      "notifyAll"};

  void checkIfAccessIsAllowed(Object object, Method member, String filename, int lineNumber) {
    if (this.isAccessUnsafe(object, member)) {
      throw new ClassAccessException(member, filename, lineNumber);
    }
  }

  private boolean isAccessUnsafe(Object object, Method member) {
    return object instanceof Class
        || object instanceof Runtime
        || object instanceof Thread
        || object instanceof ThreadGroup
        || object instanceof System
        || object instanceof AccessibleObject
        || this.isUnsafeMethodOfObjectClass(member);
  }

  private boolean isUnsafeMethodOfObjectClass(Method member) {
    return this.isAnyOfMethods(member, FORBIDDEN_OBJECT_CLASS_METHODS);
  }

  private boolean isAnyOfMethods(Method member, String... methods) {
    for (String method : methods) {
      if (this.isMethodWithName(member, method)) {
        return true;
      }
    }
    return false;
  }

  private boolean isMethodWithName(Method member, String method) {
    return member.getName().equals(method);
  }
}
