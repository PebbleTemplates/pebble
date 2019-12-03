package com.mitchellbosecke.pebble.spring.reactive;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.web.reactive.result.view.AbstractUrlBasedView;
import org.springframework.web.server.ServerWebExchange;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

import static java.util.Optional.ofNullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PebbleReactiveView extends AbstractUrlBasedView {

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
      this.evaluateTemplate(renderAttributes, locale, writer);
    } catch (Exception ex) {
      DataBufferUtils.release(dataBuffer);
      return Mono.error(ex);
    }
    return exchange.getResponse().writeWith(Flux.just(dataBuffer));
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
