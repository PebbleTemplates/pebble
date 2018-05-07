/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.template.ScopeChain;

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

    class Control extends Object {

        protected int value = -1;

        public Control(int value) {
            this.value = value;
        }

        public Control() {
        }
    }

    public ForNode(int lineNumber, String variableName, Expression<?> iterableExpression, BodyNode body,
            BodyNode elseBody) {
        super(lineNumber);
        this.variableName = variableName;
        this.iterableExpression = iterableExpression;
        this.body = body;
        this.elseBody = elseBody;
    }

    @Override
    public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws IOException {
        final Object iterableEvaluation = this.iterableExpression.evaluate(self, context);
        Iterable<?> iterable;

        if (iterableEvaluation == null) {
            return;
        }

        iterable = this.toIterable(iterableEvaluation);

        if (iterable == null) {
            throw new PebbleException(null, "Not an iterable object. Value = [" + iterableEvaluation.toString() + "]",
                    this.getLineNumber(), self.getName());
        }

        Iterator<?> iterator = iterable.iterator();

        if (iterator.hasNext()) {

            ScopeChain scopeChain = context.getScopeChain();
            scopeChain.pushScope();

            final Control length = new Control() {

                @Override
                public String toString() {
                    if (this.value == -1) {
                        this.value = getIteratorSize(iterableEvaluation);
                    }
                    return String.valueOf(value);
                }
            };

            int index = 0;

            Map<String, Object> loop = null;

            boolean usingExecutorService = context.getExecutorService() != null;

            while (iterator.hasNext()) {

                /*
                 * If the user is using an executor service (i.e. parallel
                 * node), we must create a new map with every iteration instead
                 * of re-using the same one; it's imperative that each thread
                 * would get it's own distinct copy of the context.
                 */
                if (index == 0 || usingExecutorService) {
                    loop = new HashMap<>();
                    loop.put("first", index == 0);
                    loop.put("last", !iterator.hasNext());
                    loop.put("length", length);
                } else if (index == 1) {
                    // second iteration
                    loop.put("first", false);
                }

                Control revindex = new Control(index) {

                    @Override
                    public String toString() {
                        return String.valueOf(Integer.valueOf(length.toString()) - this.value - 1);
                    }
                };

                loop.put("revindex", revindex);
                loop.put("index", index++);
                scopeChain.put("loop", loop);
                scopeChain.put(this.variableName, iterator.next());

                // last iteration
                if (!iterator.hasNext()) {
                    loop.put("last", true);
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
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

    private int getIteratorSize(Object iterable) {
        if (iterable == null) {
            return 0;
        }
        if (iterable instanceof Collection) {
            return ((Collection<?>) iterable).size();
        } else if (iterable instanceof Map) {
            return ((Map<?, ?>) iterable).size();
        } else if (iterable.getClass().isArray()) {
            return Array.getLength(iterable);
        } else if (iterable instanceof Enumeration) {
            Enumeration<?> enumeration = (Enumeration<?>) iterable;
            int size = 0;
            while (enumeration.hasMoreElements()) {
                size++;
                enumeration.nextElement();
            }
            return size;
        }

        // assumed to be of type Iterator
        Iterator<?> it = ((Iterable<?>) iterable).iterator();
        int size = 0;
        while (it.hasNext()) {
            size++;
            it.next();
        }
        return size;
    }

    /**
     * Adapts an array to an Iterable
     */
    private class ArrayIterable implements Iterable<Object> {

        private Object obj;

        public ArrayIterable(Object array) {
            this.obj = array;
        }

        @Override
        public Iterator<Object> iterator() {
            return new Iterator<Object>() {

                private int index = 0;

                private final int length = Array.getLength(obj);

                @Override
                public boolean hasNext() {
                    return this.index < this.length;
                }

                @Override
                public Object next() {
                    return Array.get(obj, this.index++);
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

        public EnumerationIterable(Enumeration<Object> enumeration) {
            this.obj = enumeration;
        }

        @Override
        public Iterator<Object> iterator() {
            return new Iterator<Object>() {

                @Override
                public boolean hasNext() {
                    return obj.hasMoreElements();
                }

                @Override
                public Object next() {
                    return obj.nextElement();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

}
