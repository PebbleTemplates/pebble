package com.mitchellbosecke.pebble.spring.reactive;

import static java.util.Optional.ofNullable;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.spring.context.Beans;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.web.reactive.result.view.AbstractUrlBasedView;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PebbleReactiveView extends AbstractUrlBasedView {

  private static final String BEANS_VARIABLE_NAME = "beans";
  private static final String REQUEST_VARIABLE_NAME = "request";
  private static final String RESPONSE_VARIABLE_NAME = "response";
  private static final String SESSION_VARIABLE_NAME = "session";

  private PebbleEngine pebbleEngine;
  private String templateName;

  @Override
  public boolean checkResourceExists(Locale locale) {
    return this.pebbleEngine.getLoader().resourceExists(this.templateName);
  }

  @Override
  protected Mono<Void> renderInternal(Map<String, Object> renderAttributes,
                                      MediaType contentType,
                                      ServerWebExchange exchange) {
    DataBuffer dataBuffer = exchange.getResponse().bufferFactory().allocateBuffer();
    if (this.logger.isDebugEnabled()) {
      this.logger.debug(exchange.getLogPrefix() + "Rendering [" + this.getUrl() + "]");
    }

    Locale locale = LocaleContextHolder.getLocale(exchange.getLocaleContext());
    try {
      Charset charset = this.getCharset(contentType);
      Writer writer = new OutputStreamWriter(dataBuffer.asOutputStream(), charset);
      this.addVariablesToModel(renderAttributes, exchange);
      this.evaluateTemplate(renderAttributes, locale, writer);
    } catch (Exception ex) {
      DataBufferUtils.release(dataBuffer);
      return Mono.error(ex);
    }
    return exchange.getResponse().writeWith(Flux.just(dataBuffer));
  }

  private void addVariablesToModel(Map<String, Object> model, ServerWebExchange exchange) {
    model.put(BEANS_VARIABLE_NAME, new Beans(this.getApplicationContext()));
    model.put(REQUEST_VARIABLE_NAME, exchange.getRequest());
    model.put(RESPONSE_VARIABLE_NAME, exchange.getResponse());
    model.put(SESSION_VARIABLE_NAME, exchange.getSession());
  }

  private Charset getCharset(@Nullable MediaType mediaType) {
    return ofNullable(mediaType)
        .map(MimeType::getCharset)
        .orElse(this.getDefaultCharset());
  }

  private void evaluateTemplate(Map<String, Object> model, Locale locale, Writer writer)
      throws IOException, PebbleException {
    try {
      PebbleTemplate template = this.pebbleEngine.getTemplate(this.templateName);
      template.evaluate(writer, model, locale);
    } finally {
      writer.flush();
    }
  }

  public PebbleEngine getPebbleEngine() {
    return this.pebbleEngine;
  }

  public void setPebbleEngine(PebbleEngine pebbleEngine) {
    this.pebbleEngine = pebbleEngine;
  }

  public String getTemplateName() {
    return this.templateName;
  }

  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }
}
