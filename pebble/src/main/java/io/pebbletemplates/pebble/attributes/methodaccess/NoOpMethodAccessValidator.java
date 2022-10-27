package io.pebbletemplates.pebble.attributes.methodaccess;

import java.lang.reflect.Method;

public class NoOpMethodAccessValidator implements MethodAccessValidator {

  @Override
  public boolean isMethodAccessAllowed(Object object, Method method) {
    return true;
  }
}
