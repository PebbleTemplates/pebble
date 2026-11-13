package io.pebbletemplates.pebble.attributes.methodaccess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.node.ForNode;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.io.Writer;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AllowlistMethodAccessValidatorTest {

  private final InstanceProvider instanceProvider = new InstanceProvider();

  @ParameterizedTest
  @MethodSource("provideUnsafeMethod")
  void unsafeMethodsAreAlwaysForbidden(Method unsafeMethod) throws Exception {
    MethodAccessValidator underTest = AllowlistMethodAccessValidator.builder()
        .allowDefaults()
        .allowTypes(Object.class)
        .allowPackages("java.lang")
        .allowMethod(Object.class, "getClass", "wait", "notify", "notifyAll")
        .build();
    Object instance = this.instanceProvider.createObject(unsafeMethod.getDeclaringClass());

    assertThat(underTest.isMethodAccessAllowed(instance, unsafeMethod)).isFalse();
  }

  @ParameterizedTest
  @MethodSource("provideAllowedMethod")
  void explicitlyAllowedMethodsAreAllowed(Method allowedMethod) throws Exception {
    MethodAccessValidator underTest = AllowlistMethodAccessValidator.builder()
        .allowTypes(Foo.class)
        .allowMethod(Object.class, "toString", "hashCode", "equals")
        .build();
    Object instance = this.instanceProvider.createObject(allowedMethod.getDeclaringClass());

    assertThat(underTest.isMethodAccessAllowed(instance, allowedMethod)).isTrue();
  }

  @Test
  void emptyAllowlistDeniesEverything() throws Exception {
    MethodAccessValidator underTest = AllowlistMethodAccessValidator.builder().build();

    assertThat(underTest.isMethodAccessAllowed("abc", String.class.getMethod("length")))
        .isFalse();
  }

  @Test
  void defaultsAllowCommonSafeTypes() throws Exception {
    MethodAccessValidator underTest = AllowlistMethodAccessValidator.builder()
        .allowDefaults()
        .build();

    assertThat(underTest.isMethodAccessAllowed("abc", String.class.getMethod("toUpperCase")))
        .isTrue();
    assertThat(underTest.isMethodAccessAllowed(1, Integer.class.getMethod("compareTo",
        Integer.class))).isTrue();
    assertThat(underTest.isMethodAccessAllowed(BigDecimal.ONE,
        BigDecimal.class.getMethod("scale"))).isTrue();
    assertThat(underTest.isMethodAccessAllowed(new ArrayList<>(),
        List.class.getMethod("size"))).isTrue();
    assertThat(underTest.isMethodAccessAllowed(new HashMap<>(),
        HashMap.class.getMethod("isEmpty"))).isTrue();
    assertThat(underTest.isMethodAccessAllowed(Optional.empty(),
        Optional.class.getMethod("isPresent"))).isTrue();
    assertThat(underTest.isMethodAccessAllowed(LocalDate.of(2020, 1, 1),
        LocalDate.class.getMethod("getYear"))).isTrue();
    assertThat(underTest.isMethodAccessAllowed(Collections.emptyList(),
        Object.class.getMethod("toString"))).isTrue();
    assertThat(underTest.isMethodAccessAllowed(Locale.CANADA_FRENCH,
        Locale.class.getMethod("getLanguage"))).isTrue();
  }

  @Test
  void pebbleBuiltinObjectsAreAlwaysAllowed() throws Exception {
    MethodAccessValidator underTest = AllowlistMethodAccessValidator.builder().build();
    PebbleTemplate template = new PebbleEngine.Builder().loader(new StringLoader()).build()
        .getTemplate("hello");

    assertThat(underTest.isMethodAccessAllowed(new ForNode.LoopVariables(),
        ForNode.LoopVariables.class.getMethod("getIndex"))).isTrue();
    assertThat(underTest.isMethodAccessAllowed(template,
        template.getClass().getMethod("getName"))).isTrue();
    assertThat(underTest.isMethodAccessAllowed(template,
        template.getClass().getMethod("evaluate", Writer.class))).isFalse();
  }

  @Test
  void defaultsDenyDangerousTypes() throws Exception {
    MethodAccessValidator underTest = AllowlistMethodAccessValidator.builder()
        .allowDefaults()
        .build();

    assertThat(underTest.isMethodAccessAllowed(new ProcessBuilder("echo"),
        ProcessBuilder.class.getMethod("start"))).isFalse();
    assertThat(underTest.isMethodAccessAllowed(Runtime.getRuntime(),
        Runtime.class.getMethod("availableProcessors"))).isFalse();
  }

  @Test
  void typeRuleDoesNotAllowMethodsOnlyDeclaredBySubtype() throws Exception {
    MethodAccessValidator underTest = AllowlistMethodAccessValidator.builder()
        .allowTypes(List.class)
        .build();
    ArrayList<String> list = new ArrayList<>();

    assertThat(underTest.isMethodAccessAllowed(list, ArrayList.class.getMethod("size")))
        .isTrue();
    assertThat(underTest.isMethodAccessAllowed(list, ArrayList.class.getMethod("trimToSize")))
        .isFalse();
  }

  @Test
  void staticMethodsAreForbidden() throws Exception {
    MethodAccessValidator underTest = AllowlistMethodAccessValidator.builder()
        .allowDefaults()
        .build();

    assertThat(underTest.isMethodAccessAllowed(Boolean.TRUE,
        Boolean.class.getMethod("getBoolean", String.class))).isFalse();
  }

  @Test
  void packageRuleAllowsClassesInPackageAndSubPackages() throws Exception {
    MethodAccessValidator underTest = AllowlistMethodAccessValidator.builder()
        .allowPackages("io.pebbletemplates.pebble.attributes.")
        .build();
    Object foo = this.instanceProvider.createObject(Foo.class);

    assertThat(underTest.isMethodAccessAllowed(foo, Foo.class.getMethod("getX"))).isTrue();
    assertThat(underTest.isMethodAccessAllowed("abc", String.class.getMethod("length")))
        .isFalse();
  }

  @Test
  void packageRuleDoesNotMatchPackagePrefix() throws Exception {
    MethodAccessValidator underTest = AllowlistMethodAccessValidator.builder()
        .allowPackages("io.pebbletemplates.pebble.attributes.method")
        .build();
    Object foo = this.instanceProvider.createObject(Foo.class);

    assertThat(underTest.isMethodAccessAllowed(foo, Foo.class.getMethod("getX"))).isFalse();
  }

  @Test
  void methodRuleAllowsOnlyListedMethods() throws Exception {
    MethodAccessValidator underTest = AllowlistMethodAccessValidator.builder()
        .allowMethod(Foo.class, "getX")
        .build();
    Object foo = this.instanceProvider.createObject(Foo.class);

    assertThat(underTest.isMethodAccessAllowed(foo, Foo.class.getMethod("getX"))).isTrue();
    assertThat(underTest.isMethodAccessAllowed(foo, Foo.class.getMethod("setX", String.class)))
        .isFalse();
  }

  @Test
  void builderRejectsInvalidArguments() {
    AllowlistMethodAccessValidator.Builder builder = AllowlistMethodAccessValidator.builder();

    assertThatThrownBy(() -> builder.allowTypes((Class<?>) null))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> builder.allowPackages(" "))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> builder.allowMethod(null, "foo"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  private static Stream<Method> provideUnsafeMethod() {
    return MethodsProvider.UNSAFE_METHODS.stream();
  }

  private static Stream<Method> provideAllowedMethod() {
    return MethodsProvider.ALLOWED_METHODS.stream();
  }
}
