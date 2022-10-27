package io.pebbletemplates.pebble.attributes.methodaccess;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

public class BlacklistMethodAccessValidator implements MethodAccessValidator {

  private static final String[] FORBIDDEN_METHODS = {"getClass",
      "wait",
      "notify",
      "notifyAll"};

  @Override
  public boolean isMethodAccessAllowed(Object object, Method method) {
    boolean methodForbidden = object instanceof Class
        || object instanceof Runtime
        || object instanceof Thread
        || object instanceof ThreadGroup
        || object instanceof System
        || object instanceof AccessibleObject
        || this.isUnsafeMethod(method);
    return !methodForbidden;
  }

  private boolean isUnsafeMethod(Method member) {
    return this.isAnyOfMethods(member, FORBIDDEN_METHODS);
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
