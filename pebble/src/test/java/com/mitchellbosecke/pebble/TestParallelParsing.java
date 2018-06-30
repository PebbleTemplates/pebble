package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

/**
 * This tests tests the parallel parsing / compilation of templates.
 *
 * @author Thomas Hunziker
 */
public class TestParallelParsing {

  /**
   * Tests if the parse is working correctly within a multi threading environment.
   */
  @Test
  public void testParser() throws InterruptedException {
    final PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true)
        .extension(new DelayExtension()).build();

    final AtomicReference<String> resultThread1 = new AtomicReference<>();
    final AtomicReference<String> resultThread2 = new AtomicReference<>();

    Thread thread1 = new Thread(() -> {
      try {
        PebbleTemplate template = pebble.getTemplate("templates/template.parallelParsing1.peb");
        Writer writer = new StringWriter();
        template.evaluate(writer);
        resultThread1.set(writer.toString());
      } catch (PebbleException | IOException e) {
        throw new RuntimeException(e);
      }
    });

    Thread thread2 = new Thread(() -> {
      try {
        PebbleTemplate template = pebble.getTemplate("templates/template.parallelParsing2.peb");
        Writer writer = new StringWriter();
        template.evaluate(writer);
        resultThread2.set(writer.toString());
      } catch (PebbleException | IOException e) {
        throw new RuntimeException(e);
      }
    });

    // Start the threads.
    thread1.start();
    thread2.start();

    // Wait until both threads completed.
    thread1.join();
    thread2.join();

    assertEquals("output in 1: a|output in 1: b|output in 1: c", resultThread1.get());
    assertEquals("output in 2: a|output in 2: b|output in 2: c", resultThread2.get());
  }

  /**
   * This extension provides a token parser which does introduce a delay during the parser. This
   * allows to provoke failing of the test when the parallel implementation is not ok.
   */
  private static class DelayExtension extends AbstractExtension {

    @Override
    public List<TokenParser> getTokenParsers() {
      return Collections.singletonList(new DelayTokenParser());
    }

  }

  private static class DelayTokenParser implements TokenParser {

    @Override
    public String getTag() {
      return "delay";
    }

    @Override
    public RenderableNode parse(Token token, Parser parser) throws ParserException {

      TokenStream stream = parser.getStream();

      // skip over the 'delay' token
      Token delayName = stream.next();

      // expect a name or string for the new block
      if (!delayName.test(Token.Type.NUMBER)) {

        // we already know an error has occurred but let's just call the
        // typical "expect" method so that we know a proper error
        // message is given to user
        stream.expect(Token.Type.NUMBER);
      }

      int delay = Integer.valueOf(delayName.getValue());

      try {
        // We sleep for the given number of milliseconds:
        Thread.sleep(delay);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      // skip over the delay
      stream.next();

      stream.expect(Token.Type.EXECUTE_END);

      return new RenderableNode() {

        @Override
        public void accept(NodeVisitor visitor) {
          visitor.visit(this);
        }

        @Override
        public void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context)
            throws PebbleException {
          // Do nothing.
        }
      };
    }

  }

}
