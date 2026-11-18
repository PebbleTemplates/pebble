package io.pebbletemplates.spring;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.spring.reactive.PebbleReactiveView;
import io.pebbletemplates.spring.reactive.PebbleReactiveViewResolver;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferWrapper;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.result.view.View;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
  void doesNotLeakBufferIfBodyIsNeverWritten() {
    TrackingDataBufferFactory bufferFactory = new TrackingDataBufferFactory();
    ServerHttpResponse response = new ServerHttpResponseDecorator(new MockServerHttpResponse(bufferFactory)) {
      @Override
      public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        // like a client that went away before the body is written: nobody ever subscribes to the body
        return Mono.empty();
      }
    };
    ServerWebExchange exchange = new ServerWebExchangeDecorator(exchange()) {
      @Override
      public ServerHttpResponse getResponse() {
        return response;
      }
    };

    view("Hello").render(null, MediaType.TEXT_PLAIN, exchange).block();

    assertEquals(0, bufferFactory.unreleasedBufferCount(), "buffers that were allocated but never released");
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

  /** Remembers every buffer it allocates, so a test can tell which ones were never released. */
  private static final class TrackingDataBufferFactory implements DataBufferFactory {

    private final DataBufferFactory delegate = new DefaultDataBufferFactory();
    private final List<ReleaseTrackingDataBuffer> buffers = new ArrayList<>();

    long unreleasedBufferCount() {
      return this.buffers.stream().filter(ReleaseTrackingDataBuffer::isAllocated).count();
    }

    @Override
    public DataBuffer allocateBuffer() {
      return this.track(this.delegate.allocateBuffer());
    }

    @Override
    public DataBuffer allocateBuffer(int initialCapacity) {
      return this.track(this.delegate.allocateBuffer(initialCapacity));
    }

    @Override
    public DataBuffer wrap(ByteBuffer byteBuffer) {
      return this.delegate.wrap(byteBuffer);
    }

    @Override
    public DataBuffer wrap(byte[] bytes) {
      return this.delegate.wrap(bytes);
    }

    @Override
    public DataBuffer join(List<? extends DataBuffer> dataBuffers) {
      return this.delegate.join(dataBuffers);
    }

    @Override
    public boolean isDirect() {
      return this.delegate.isDirect();
    }

    private DataBuffer track(DataBuffer buffer) {
      ReleaseTrackingDataBuffer tracked = new ReleaseTrackingDataBuffer(buffer);
      this.buffers.add(tracked);
      return tracked;
    }
  }

  /** A buffer that is "allocated" until {@code DataBufferUtils.release} is called, like a pooled Netty buffer. */
  private static final class ReleaseTrackingDataBuffer extends DataBufferWrapper implements PooledDataBuffer {

    private boolean allocated = true;

    ReleaseTrackingDataBuffer(DataBuffer delegate) {
      super(delegate);
    }

    @Override
    public boolean isAllocated() {
      return this.allocated;
    }

    @Override
    public PooledDataBuffer retain() {
      return this;
    }

    @Override
    public PooledDataBuffer touch(Object hint) {
      return this;
    }

    @Override
    public boolean release() {
      this.allocated = false;
      return true;
    }
  }
}
