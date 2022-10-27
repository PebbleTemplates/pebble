package io.pebbletemplates.pebble.attributes.methodaccess;

import java.lang.reflect.Method;

public interface MethodAccessValidator {

  boolean isMethodAccessAllowed(Object object, Method method);
}
