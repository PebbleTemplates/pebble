package io.pebbletemplates.pebble.attributes.methodaccess;

import java.lang.reflect.Method;

public class NoOpMethodAccessValidator implements MethodAccessValidator {

  private NoOpMethodAccessValidator(NoOpMethodAccessValidator.Builder builder) {
  }

  public static NoOpMethodAccessValidator.Builder builder() {
    return new NoOpMethodAccessValidator.Builder();
  }

  @Override
  public boolean isMethodAccessAllowed(Object object, Method method) {
    return true;
  }

  public static final class Builder {

    private Builder() {
    }

    public NoOpMethodAccessValidator build() {
      return new NoOpMethodAccessValidator(this);
    }
  }
}
