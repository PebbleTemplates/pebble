package io.pebbletemplates.spring;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.spring.reactive.PebbleReactiveView;
import io.pebbletemplates.spring.reactive.PebbleReactiveViewResolver;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.result.view.View;

import java.util.Collections;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PebbleReactiveViewTest {

  @Test
  void rendersModelIntoReactiveResponse() {
    MockServerWebExchange exchange = exchange();
    PebbleReactiveView view = view("Hello {{ name }}");

    view.render(Collections.singletonMap("name", "world"), MediaType.TEXT_PLAIN, exchange)
        .block();

    assertEquals("Hello world", exchange.getResponse().getBodyAsString().block());
  }

  @Test
  void rendersWithoutModelOrContentType() {
    MockServerWebExchange exchange = exchange();
    PebbleReactiveView view = view("Hello");

    view.render(null, null, exchange).block();

    assertEquals("Hello", exchange.getResponse().getBodyAsString().block());
  }

  @Test
  void propagatesTemplateErrors() {
    MockServerWebExchange exchange = exchange();
    PebbleReactiveView view = view("{% unknown %}");

    assertThrows(RuntimeException.class, () -> view.render(null, MediaType.TEXT_PLAIN, exchange)
        .block());
  }

  @Test
  void resolverCreatesPebbleViewForResolvedTemplate() {
    PebbleEngine engine = new PebbleEngine.Builder().loader(new StringLoader()).build();
    PebbleReactiveViewResolver resolver = new PebbleReactiveViewResolver(engine);

    View resolvedView = resolver.resolveViewName("template", Locale.ROOT).block();

    PebbleReactiveView view = (PebbleReactiveView) resolvedView;
    assertEquals("template", view.getTemplateName());
    assertSame(engine, view.getPebbleEngine());
  }

  private PebbleReactiveView view(String template) {
    PebbleReactiveView view = new PebbleReactiveView();
    view.setApplicationContext(new StaticApplicationContext());
    view.setPebbleEngine(new PebbleEngine.Builder().loader(new StringLoader()).build());
    view.setTemplateName(template);
    view.setUrl(template);
    return view;
  }

  private static MockServerWebExchange exchange() {
    return MockServerWebExchange.from(MockServerHttpRequest.get("/").build());
  }
}
