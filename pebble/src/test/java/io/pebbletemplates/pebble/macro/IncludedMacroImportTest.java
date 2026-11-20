package io.pebbletemplates.pebble.macro;

import static org.assertj.core.api.Assertions.assertThat;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.MemoryLoader;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IncludedMacroImportTest {

  /** Sibling includes can import the same macro library under the same alias. */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void siblingIncludesCanReuseAnAlias(boolean strictVariables) throws IOException {
    MemoryLoader loader = formsLoader();
    loader.addTemplate("page", "<main>{% block content %}{% endblock %}</main>");
    loader.addTemplate("index", "{% extends 'page' %}{% block content %}"
        + "{% include 'login' %}{% include 'register' %}{% endblock %}");
    loader.addTemplate("login", "{% import 'forms' as form %}{{ form.input('username') }}");
    loader.addTemplate("register", "{% import 'forms' as form %}{{ form.input('email') }}");

    assertThat(render(loader, "index", strictVariables))
        .isEqualTo("<main><input name=\"username\"><input name=\"email\"></main>");
  }

  /** Identical aliases in sibling includes can refer to different macro libraries. */
  @Test
  void siblingIncludesKeepTheirOwnMacroLibrary() throws IOException {
    MemoryLoader loader = formsLoader();
    loader.addTemplate("buttons",
        "{% macro input(name) %}<button>{{ name }}</button>{% endmacro %}");
    loader.addTemplate("index", "{% include 'first' %}{% include 'second' %}");
    loader.addTemplate("first", "{% import 'forms' as form %}{{ form.input('email') }}");
    loader.addTemplate("second", "{% import 'buttons' as form %}{{ form.input('submit') }}");

    assertThat(render(loader, "index", true))
        .isEqualTo("<input name=\"email\"><button>submit</button>");
  }

  /** Repeated evaluation of a cached include does not retain its previous import alias. */
  @Test
  void includesCanBeRepeatedInALoop() throws IOException {
    MemoryLoader loader = formsLoader();
    loader.addTemplate("index", "{% for name in ['first', 'second'] %}"
        + "{% include 'field' with {'name': name} %}{% endfor %}");
    loader.addTemplate("field", "{% import 'forms' as form %}{{ form.input(name) }}");

    assertThat(render(loader, "index", true))
        .isEqualTo("<input name=\"first\"><input name=\"second\">");
  }

  /** An alias introduced by an include does not reserve that name in its caller. */
  @Test
  void callerCanImportAnAliasAfterAnInclude() throws IOException {
    MemoryLoader loader = formsLoader();
    loader.addTemplate("index", "{% include 'field' %}"
        + "{% import 'forms' as form %}{{ form.input('parent') }}");
    loader.addTemplate("field", "{% import 'forms' as form %}{{ form.input('child') }}");

    assertThat(render(loader, "index", true))
        .isEqualTo("<input name=\"child\"><input name=\"parent\">");
  }

  /** An include still sees an existing caller import and its additional variables. */
  @Test
  void includesCanUseTheCallersImport() throws IOException {
    MemoryLoader loader = formsLoader();
    loader.addTemplate("index", "{% import 'forms' as form %}"
        + "{% include 'field' with {'name': 'child'} %}{{ form.input('parent') }}");
    loader.addTemplate("field", "{{ form.input(name) }}");

    assertThat(render(loader, "index", true))
        .isEqualTo("<input name=\"child\"><input name=\"parent\">");
  }

  /** Embeds sharing the include context-copy path also keep their import aliases separate. */
  @Test
  void embedsCanReuseAnAlias() throws IOException {
    MemoryLoader loader = formsLoader();
    loader.addTemplate("index", "{% embed 'field' with {'name': 'first'} %}{% endembed %}"
        + "{% embed 'field' with {'name': 'second'} %}{% endembed %}");
    loader.addTemplate("field", "{% block content %}{% import 'forms' as form %}"
        + "{{ form.input(name) }}{% endblock %}");

    assertThat(render(loader, "index", true))
        .isEqualTo("<input name=\"first\"><input name=\"second\">");
  }

  private MemoryLoader formsLoader() {
    MemoryLoader loader = new MemoryLoader();
    loader.addTemplate("forms", "{% macro input(name) %}<input name=\"{{ name }}\">{% endmacro %}");
    return loader;
  }

  private String render(MemoryLoader loader, String name, boolean strictVariables)
      throws IOException {
    PebbleEngine engine = new PebbleEngine.Builder().loader(loader)
        .strictVariables(strictVariables).build();
    StringWriter writer = new StringWriter();
    engine.getTemplate(name).evaluate(writer);
    return writer.toString();
  }
}
