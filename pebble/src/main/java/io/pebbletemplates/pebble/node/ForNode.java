/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.node;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.NodeVisitor;
import io.pebbletemplates.pebble.node.expression.Expression;
import io.pebbletemplates.pebble.node.fornode.LazyLength;
import io.pebbletemplates.pebble.node.fornode.LazyRevIndex;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;
import io.pebbletemplates.pebble.template.ScopeChain;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a "for" loop within the template.
 *
 * @author mbosecke
 */
public class ForNode extends AbstractRenderableNode {

  private final String variableName;

  private final Expression<?> iterableExpression;

  private final BodyNode body;

  private final BodyNode elseBody;

  public ForNode(int lineNumber, String variableName, Expression<?> iterableExpression,
      BodyNode body,
      BodyNode elseBody) {
    super(lineNumber);
    this.variableName = variableName;
    this.iterableExpression = iterableExpression;
    this.body = body;
    this.elseBody = elseBody;
  }

  public static class LoopVariables {
    private boolean first, last;
    private LazyLength length;
    private int index;
    private LazyRevIndex revindex;

    @Override
    public String toString() {
      return "{last=" + last + ", length=" + length + ", index=" + index + ", revindex=" + revindex + ", first=" + first + "}";
    }

    public boolean isFirst() {
      return first;
    }

    public boolean isLast() {
      return last;
    }

    public LazyLength getLength() {
      return length;
    }

    public int getIndex() {
      return index;
    }

    public LazyRevIndex getRevindex() {
      return revindex;
    }
  }

  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context)
      throws IOException {
    final Object iterableEvaluation = this.iterableExpression.evaluate(self, context);
    Iterable<?> iterable;

    if (iterableEvaluation == null) {
      return;
    }

    iterable = this.toIterable(iterableEvaluation);

    if (iterable == null) {
      throw new PebbleException(null,
          "Not an iterable object. Value = [" + iterableEvaluation.toString() + "]",
          this.getLineNumber(), self.getName());
    }

    Iterator<?> iterator = iterable.iterator();

    if (iterator.hasNext()) {

      ScopeChain scopeChain = context.getScopeChain();
      scopeChain.pushScope();

      LazyLength length = new LazyLength(iterableEvaluation);

      int index = 0;

      LoopVariables loop = null;

      boolean usingExecutorService = context.getExecutorService() != null;

      while (iterator.hasNext()) {

        /*
         * If the user is using an executor service (i.e. parallel
         * node), we must create a new map with every iteration instead
         * of re-using the same one; it's imperative that each thread
         * would get it's own distinct copy of the context.
         */
        if (index == 0 || usingExecutorService) {
          loop = new LoopVariables();
          loop.first = index == 0;
          loop.last = !iterator.hasNext();
          loop.length = length;
        } else if (index == 1) {
          // second iteration
          loop.first = false;
        }

        loop.revindex = new LazyRevIndex(index, length);
        loop.index = index++;
        scopeChain.put("loop", loop);
        scopeChain.put(this.variableName, iterator.next());

        // last iteration
        if (!iterator.hasNext()) {
          loop.last = true;
        }

        this.body.render(self, writer, context);
      }

      scopeChain.popScope();

    } else if (this.elseBody != null) {
      this.elseBody.render(self, writer, context);
    }

  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public String getIterationVariable() {
    return this.variableName;
  }

  public Expression<?> getIterable() {
    return this.iterableExpression;
  }

  public BodyNode getBody() {
    return this.body;
  }

  public BodyNode getElseBody() {
    return this.elseBody;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private Iterable<Object> toIterable(final Object obj) {

    Iterable<Object> result = null;

    if (obj instanceof Iterable<?>) {
      result = (Iterable<Object>) obj;
    } else if (obj instanceof Map) {
      // raw type
      result = ((Map) obj).entrySet();
    } else if (obj.getClass().isArray()) {
      result = new ArrayIterable(obj);
    } else if (obj instanceof Enumeration) {
      result = new EnumerationIterable((Enumeration) obj);
    }

    return result;
  }

  /**
   * Adapts an array to an Iterable
   */
  private class ArrayIterable implements Iterable<Object> {

    private Object obj;

    ArrayIterable(Object array) {
      this.obj = array;
    }

    @Override
    public Iterator<Object> iterator() {
      return new Iterator<Object>() {

        private int index = 0;

        private final int length = Array.getLength(ArrayIterable.this.obj);

        @Override
        public boolean hasNext() {
          return this.index < this.length;
        }

        @Override
        public Object next() {
          return Array.get(ArrayIterable.this.obj, this.index++);
        }

        @Override
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    }
  }

  /**
   * Adapts an Enumeration to an Iterable
   */
  private class EnumerationIterable implements Iterable<Object> {

    private Enumeration<Object> obj;

    EnumerationIterable(Enumeration<Object> enumeration) {
      this.obj = enumeration;
    }

    @Override
    public Iterator<Object> iterator() {
      return new Iterator<Object>() {

        @Override
        public boolean hasNext() {
          return EnumerationIterable.this.obj.hasMoreElements();
        }

        @Override
        public Object next() {
          return EnumerationIterable.this.obj.nextElement();
        }

        @Override
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    }
  }

}
