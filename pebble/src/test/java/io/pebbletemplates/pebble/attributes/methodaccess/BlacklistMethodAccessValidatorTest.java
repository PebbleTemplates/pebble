package io.pebbletemplates.pebble.attributes.methodaccess;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


class BlacklistMethodAccessValidatorTest {

  private final InstanceProvider instanceProvider = new InstanceProvider();
  private final MethodAccessValidator underTest = new BlacklistMethodAccessValidator();

  @ParameterizedTest
  @MethodSource("provideUnsafeMethod")
  void checkIfAccessIsForbidden(Method unsafeMethod)
      throws NoSuchFieldException, NoSuchMethodException {
    Class<?> declaringClass = unsafeMethod.getDeclaringClass();
    Object instance = this.instanceProvider.createObject(declaringClass);

    boolean methodAccessAllowed = this.underTest.isMethodAccessAllowed(instance, unsafeMethod);

    assertThat(methodAccessAllowed).isFalse();
  }

  @ParameterizedTest
  @MethodSource("provideAllowedMethod")
  void checkIfAccessIsAllowed(Method allowedMethod) throws Throwable {
    Class<?> declaringClass = allowedMethod.getDeclaringClass();
    Object instance = this.instanceProvider.createObject(declaringClass);

    boolean methodAccessAllowed = this.underTest.isMethodAccessAllowed(instance, allowedMethod);

    assertThat(methodAccessAllowed).isTrue();
  }

  private static Stream<Method> provideUnsafeMethod() {
    return MethodsProvider.UNSAFE_METHODS.stream();
  }

  private static Stream<Method> provideAllowedMethod() {
    return MethodsProvider.ALLOWED_METHODS.stream();
  }
}