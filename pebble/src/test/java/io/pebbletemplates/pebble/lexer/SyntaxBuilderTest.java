package io.pebbletemplates.pebble.lexer;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.StringLoader;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SyntaxBuilderTest {

  @Test
  void customDelimitersAreExposedAndUsedByTheEngine() throws Exception {
    Syntax.Builder builder = new Syntax.Builder()
        .setCommentOpenDelimiter("<#")
        .setCommentCloseDelimiter("#>")
        .setExecuteOpenDelimiter("<%")
        .setExecuteCloseDelimiter("%>")
        .setPrintOpenDelimiter("[[")
        .setPrintCloseDelimiter("]]")
        .setWhitespaceTrim("~")
        .setEnableNewLineTrimming(false);
    builder.setInterpolationOpenDelimiter("#{");
    builder.setInterpolationCloseDelimiter("}");

    assertEquals("<#", builder.getCommentOpenDelimiter());
    assertEquals("#>", builder.getCommentCloseDelimiter());
    assertEquals("<%", builder.getExecuteOpenDelimiter());
    assertEquals("%>", builder.getExecuteCloseDelimiter());
    assertEquals("[[", builder.getPrintOpenDelimiter());
    assertEquals("]]", builder.getPrintCloseDelimiter());
    assertEquals("#{", builder.getInterpolationOpenDelimiter());
    assertEquals("}", builder.getInterpolationCloseDelimiter());
    assertEquals("~", builder.getWhitespaceTrim());
    assertEquals(false, builder.isEnableNewLineTrimming());

    Syntax syntax = builder.build();
    assertEquals("<#", syntax.getCommentOpenDelimiter());
    assertEquals("#>", syntax.getCommentCloseDelimiter());
    assertEquals("<%", syntax.getExecuteOpenDelimiter());
    assertEquals("%>", syntax.getExecuteCloseDelimiter());
    assertEquals("[[", syntax.getPrintOpenDelimiter());
    assertEquals("]]", syntax.getPrintCloseDelimiter());
    assertEquals("#{", syntax.getInterpolationOpenDelimiter());
    assertEquals("}", syntax.getInterpolationCloseDelimiter());
    assertEquals("~", syntax.getWhitespaceTrim());
    assertEquals(false, builder.isEnableNewLineTrimming());

    PebbleEngine engine = new PebbleEngine.Builder().loader(new StringLoader())
        .syntax(syntax).strictVariables(false).build();
    StringWriter output = new StringWriter();
    Map<String, Object> context = new HashMap<>();
    context.put("value", "ok");
    context.put("enabled", true);
    engine.getTemplate("<# ignored #>[[ \"#{value}\" ]]\n<% if enabled %>!<% endif %>")
        .evaluate(output, context);

    assertEquals("ok\n!", output.toString());
  }
}
