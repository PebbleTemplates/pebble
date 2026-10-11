package io.pebbletemplates.pebble;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.loader.MemoryLoader;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MacroCallerTest {

  private static final String MACRO =
      "{% macro testMacro(a, b) %}\n"
          + "\t{% if(a > b) %}\n"
          + "\t\tA\n"
          + "\t{% else %}\n"
          + "\t\tB\n"
          + "\t{% endif %}\n"
          + "{% endmacro %}";

  /**
   * A macro error retains its definition location and adds the caller from the reported example.
   */
  @Test
  void reportsInlineMacroCallerAndOrigin() {
    MemoryLoader loader = new MemoryLoader();
    loader.addTemplate("demo.peb", MACRO + "\n\n{{ testMacro(\"foo\", \"bar\") }}");

    PebbleException exception = evaluateFailure(loader, "demo.peb");

    assertLocation(exception, "demo.peb", 9);
    assertThat(exception.getPebbleMessage())
        .isEqualTo("Wrong operand(s) type in conditional expression");
    assertOrigin(exception.getCause(), "demo.peb", 2);
  }

  /** An unaliased import adds the caller file without replacing the macro's own error location. */
  @Test
  void reportsImportedMacroCallerAndOrigin() {
    MemoryLoader loader = new MemoryLoader();
    loader.addTemplate("macros.peb", MACRO);
    loader.addTemplate(
        "caller.peb", "{% import 'macros.peb' %}\n\n" + "{{ testMacro(\"foo\", \"bar\") }}");

    PebbleException exception = evaluateFailure(loader, "caller.peb");

    assertLocation(exception, "caller.peb", 3);
    assertThat(exception.getPebbleMessage())
        .isEqualTo("Wrong operand(s) type in conditional expression");
    assertOrigin(exception.getCause(), "macros.peb", 2);
  }

  /** An import alias reports the invocation location rather than only the imported template. */
  @Test
  void reportsAliasedMacroCallerAndOrigin() {
    MemoryLoader loader = new MemoryLoader();
    loader.addTemplate("macros.peb", MACRO);
    loader.addTemplate(
        "caller.peb",
        "{% import 'macros.peb' as macros %}\n\n" + "{{ macros.testMacro(\"foo\", \"bar\") }}");

    PebbleException exception = evaluateFailure(loader, "caller.peb");

    assertLocation(exception, "caller.peb", 3);
    assertThat(exception.getPebbleMessage())
        .isEqualTo("Wrong operand(s) type in conditional expression");
    assertOrigin(exception.getCause(), "macros.peb", 2);
  }

  /** A from-import alias adds the caller while retaining the original evaluation error as cause. */
  @Test
  void reportsFromImportedMacroCaller() {
    MemoryLoader loader = new MemoryLoader();
    loader.addTemplate("macros.peb", MACRO);
    loader.addTemplate(
        "caller.peb",
        "{% from 'macros.peb' import testMacro as imported %}\n\n"
            + "{{ imported(\"foo\", \"bar\") }}");

    PebbleException exception = evaluateFailure(loader, "caller.peb");

    assertLocation(exception, "caller.peb", 3);
    assertThat(exception.getPebbleMessage())
        .isEqualTo("Wrong operand(s) type in conditional expression");
    assertThat(exception.getCause()).isInstanceOf(PebbleException.class);
    PebbleException origin = (PebbleException) exception.getCause();
    assertThat(origin.getLineNumber()).isEqualTo(2);
    assertThat(origin.getPebbleMessage())
        .isEqualTo("Wrong operand(s) type in conditional expression");
  }

  /** Nested macro errors retain each invocation and the failing expression in the cause chain. */
  @Test
  void reportsEachNestedMacroCaller() {
    MemoryLoader loader = new MemoryLoader();
    loader.addTemplate(
        "nested.peb",
        "{% macro inner(a, b) %}\n"
            + "{% if(a > b) %}A{% endif %}\n"
            + "{% endmacro %}\n"
            + "{% macro outer(a, b) %}\n"
            + "{{ inner(a, b) }}\n"
            + "{% endmacro %}\n"
            + "{{ outer(\"foo\", \"bar\") }}");

    PebbleException exception = evaluateFailure(loader, "nested.peb");

    assertLocation(exception, "nested.peb", 7);
    assertThat(exception.getPebbleMessage())
        .isEqualTo("Wrong operand(s) type in conditional expression");
    assertThat(exception.getCause()).isInstanceOf(PebbleException.class);
    PebbleException inner = (PebbleException) exception.getCause();
    assertLocation(inner, "nested.peb", 5);
    assertThat(inner.getPebbleMessage())
        .isEqualTo("Wrong operand(s) type in conditional expression");
    assertOrigin(inner.getCause(), "nested.peb", 2);
  }

  /** Successful inline macro calls keep their existing rendering behavior. */
  @Test
  void rendersSuccessfulInlineMacro() throws IOException {
    MemoryLoader loader = new MemoryLoader();
    loader.addTemplate(
        "caller.peb", "{% macro echo(value) %}{{ value }}{% endmacro %}" + "{{ echo('success') }}");

    assertThat(evaluate(loader, "caller.peb")).isEqualTo("success");
  }

  /** Successful unaliased, aliased, and from-import macro calls keep their rendering behavior. */
  @Test
  void rendersSuccessfulImportedMacros() throws IOException {
    MemoryLoader loader = new MemoryLoader();
    loader.addTemplate("macros.peb", "{% macro echo(value) %}{{ value }}{% endmacro %}");
    loader.addTemplate(
        "caller.peb",
        "{% import 'macros.peb' %}"
            + "{% import 'macros.peb' as macros %}"
            + "{% from 'macros.peb' import echo as imported %}"
            + "{{ echo('a') }}{{ macros.echo('b') }}{{ imported('c') }}");

    assertThat(evaluate(loader, "caller.peb")).isEqualTo("abc");
  }

  /** An undefined macro already reports the caller and keeps its original message and cause. */
  @Test
  void preservesUndefinedMacroCaller() {
    MemoryLoader loader = new MemoryLoader();
    loader.addTemplate("caller.peb", "before\n\n{{ missing() }}");

    PebbleException exception = evaluateFailure(loader, "caller.peb");

    assertLocation(exception, "caller.peb", 3);
    assertThat(exception.getPebbleMessage())
        .isEqualTo("Function or Macro [missing] does not exist.");
    assertThat(exception.getCause()).isNull();
  }

  /**
   * An invalid from-import keeps the established missing-alias error rather than a caller wrapper.
   */
  @Test
  void preservesInvalidFromImportMessage() {
    MemoryLoader loader = new MemoryLoader();
    loader.addTemplate("macros.peb", "{% macro echo(value) %}{{ value }}{% endmacro %}");
    loader.addTemplate("caller.peb", "{% from 'macros.peb' import missing as imported %}");

    PebbleException exception = evaluateFailure(loader, "caller.peb");

    assertThat(exception.getPebbleMessage())
        .isEqualTo("Function or Macro [missing] referenced by alias [imported] does not exist.");
  }

  /** Extension function failures are not changed by handling errors from macro invocations. */
  @Test
  void leavesFunctionExceptionUnchanged() {
    MemoryLoader loader = new MemoryLoader();
    loader.addTemplate("caller.peb", "{{ failing() }}");
    PebbleException original = new PebbleException(null, "Function failure", 42, "extension.peb");
    PebbleEngine engine =
        new PebbleEngine.Builder()
            .loader(loader)
            .extension(
                new AbstractExtension() {
                  @Override
                  public Map<String, Function> getFunctions() {
                    return Collections.singletonMap(
                        "failing",
                        new Function() {
                          @Override
                          public Object execute(
                              Map<String, Object> args,
                              PebbleTemplate self,
                              EvaluationContext context,
                              int lineNumber) {
                            throw original;
                          }

                          @Override
                          public List<String> getArgumentNames() {
                            return Collections.emptyList();
                          }
                        });
                  }
                })
            .build();

    PebbleException exception =
        catchThrowableOfType(
            PebbleException.class,
            () -> engine.getTemplate("caller.peb").evaluate(new StringWriter()));

    assertThat(exception).isSameAs(original);
  }

  private static PebbleException evaluateFailure(MemoryLoader loader, String templateName) {
    PebbleException exception =
        catchThrowableOfType(PebbleException.class, () -> evaluate(loader, templateName));
    assertThat(exception).isNotNull();
    return exception;
  }

  private static String evaluate(MemoryLoader loader, String templateName) throws IOException {
    PebbleEngine engine = new PebbleEngine.Builder().loader(loader).build();
    StringWriter writer = new StringWriter();
    engine.getTemplate(templateName).evaluate(writer);
    return writer.toString();
  }

  private static void assertLocation(PebbleException exception, String filename, int lineNumber) {
    assertThat(exception.getFileName()).isEqualTo(filename);
    assertThat(exception.getLineNumber()).isEqualTo(lineNumber);
  }

  private static void assertOrigin(Throwable cause, String filename, int lineNumber) {
    assertThat(cause).isInstanceOf(PebbleException.class);
    PebbleException origin = (PebbleException) cause;
    assertLocation(origin, filename, lineNumber);
    assertThat(origin.getPebbleMessage())
        .isEqualTo("Wrong operand(s) type in conditional expression");
    assertThat(origin.getCause()).isNotNull();
  }
}
