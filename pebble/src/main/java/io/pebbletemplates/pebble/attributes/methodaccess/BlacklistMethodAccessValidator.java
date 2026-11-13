package io.pebbletemplates.pebble.attributes.methodaccess;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * Denies access to a limited set of dangerous methods and types.
 *
 * @deprecated Use {@link AllowlistMethodAccessValidator} instead. This validator is planned for
 *     removal in version 4.3.x.
 */
@Deprecated
public class BlacklistMethodAccessValidator implements MethodAccessValidator {

  private static final String[] FORBIDDEN_METHODS = {"getClass",
      "wait",
      "notify",
      "notifyAll"};

  private BlacklistMethodAccessValidator(BlacklistMethodAccessValidator.Builder builder) {
  }

  public static BlacklistMethodAccessValidator.Builder builder() {
    return new BlacklistMethodAccessValidator.Builder();
  }

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

  public static final class Builder {

    private Builder() {
    }

    public BlacklistMethodAccessValidator build() {
      return new BlacklistMethodAccessValidator(this);
    }
  }
}
