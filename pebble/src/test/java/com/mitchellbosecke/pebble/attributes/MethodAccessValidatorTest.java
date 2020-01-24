package com.mitchellbosecke.pebble.attributes;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mitchellbosecke.pebble.error.ClassAccessException;
import java.lang.reflect.Method;
import java.util.stream.Stream;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


class MethodAccessValidatorTest {

  private MethodAccessValidator validator = new MethodAccessValidator();
  private InstanceProvider instanceProvider = new InstanceProvider();

  @ParameterizedTest
  @MethodSource("provideUnsafeMethod")
  void checkIfAccessIsForbidden(Method unsafeMethod) {
    assertThrows(ClassAccessException.class, methodCallExecutable(unsafeMethod));
  }

  @ParameterizedTest
  @MethodSource("provideAllowedMethod")
  void checkIfAccessIsAllowed(Method allowedMethod) throws Throwable {
    methodCallExecutable(allowedMethod).execute();
  }

  private Executable methodCallExecutable(Method method) {
    return () -> {
      Class<?> declaringClass = method.getDeclaringClass();
      Object instance = instanceProvider.createObject(declaringClass);
      validator.checkIfAccessIsAllowed(instance, method, "filename", 1);
    };
  }

  private static Stream<Method> provideUnsafeMethod() {
    return MethodsProvider.UNSAFE_METHODS.stream();
  }

  private static Stream<Method> provideAllowedMethod() {
    return MethodsProvider.ALLOWED_METHODS.stream();
  }
}