package io.pebbletemplates.pebble.macro;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.MemoryLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NamedMacroArgumentsTest {

  /** Named arguments are matched by name when a macro is imported into a namespace. */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void namedArgumentsCanBeReordered(boolean strict) throws IOException {
    assertThat(render("forms.input(name='email', type='password')", strict))
        .isEqualTo("password:email");
  }

  /** An omitted named argument still uses the macro's default value. */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void namedArgumentsPreserveDefaults(boolean strict) throws IOException {
    assertThat(render("forms.input(name='email')", strict)).isEqualTo("text:email");
  }

  /** Positional arguments may precede named arguments in a namespaced macro call. */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void positionalAndNamedArgumentsCanBeCombined(boolean strict) throws IOException {
    assertThat(render("forms.input('password', name='email')", strict))
        .isEqualTo("password:email");
  }

  /** Named arguments retain explicit null values rather than applying a default. */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void namedArgumentsCanBeNull(boolean strict) throws IOException {
    assertThat(render("forms.input(type=null, name='email')", strict)).isEqualTo(":email");
  }

  /** Invalid macro arguments report the original argument error at the call site. */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void unknownNamedArgumentsAreRejected(boolean strict) {
    assertThatThrownBy(() -> render("forms.input(unknown='email')", strict))
        .isInstanceOfSatisfying(PebbleException.class, error -> {
          assertThat(error.getPebbleMessage())
              .isEqualTo("The following named argument does not exist: unknown");
          assertThat(error.getFileName()).isEqualTo("main");
          assertThat(error.getLineNumber()).isEqualTo(2);
        });
  }

  /** A Java bean method must not silently ignore a named argument. */
  @Test
  void beanMethodsStillRejectNamedArguments() {
    Bean bean = new Bean();
    assertThatThrownBy(() -> renderBean("bean.value(unused='email')", bean))
        .isInstanceOf(PebbleException.class)
        .hasMessageContaining("Can not use named arguments when calling a bean method");
    assertThat(bean.calls).isZero();
  }

  /** Ordinary positional Java bean calls remain supported. */
  @Test
  void beanMethodsStillAcceptPositionalArguments() throws IOException {
    Bean bean = new Bean();
    assertThat(renderBean("bean.value()", bean)).isEqualTo("value");
    assertThat(bean.calls).isEqualTo(1);
  }

  private String render(String expression, boolean strict) throws IOException {
    MemoryLoader loader = new MemoryLoader();
    loader.addTemplate("forms", "{% macro input(type='text', name='default') %}"
        + "{{ type }}:{{ name }}{% endmacro %}");
    loader.addTemplate("main", "{% import 'forms' as forms %}\n{{ " + expression + " }}");
    PebbleEngine engine = new PebbleEngine.Builder().loader(loader).strictVariables(strict).build();
    StringWriter writer = new StringWriter();
    engine.getTemplate("main").evaluate(writer);
    return writer.toString();
  }

  private String renderBean(String expression, Bean bean) throws IOException {
    PebbleTemplate template = new PebbleEngine.Builder().build()
        .getLiteralTemplate("{{ " + expression + " }}");
    StringWriter writer = new StringWriter();
    template.evaluate(writer, Collections.singletonMap("bean", bean));
    return writer.toString();
  }

  public static class Bean {
    private int calls;

    public String value() {
      calls++;
      return "value";
    }
  }
}
