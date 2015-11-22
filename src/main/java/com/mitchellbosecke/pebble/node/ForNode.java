/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Represents a "for" loop within the template.
 * 
 * @author mbosecke
 *
 */
public class ForNode extends AbstractRenderableNode {

    private final String variableName;

    private final Expression<?> iterableExpression;

    private final BodyNode body;

    private final BodyNode elseBody;

    public ForNode(int lineNumber, String variableName, Expression<?> iterableExpression, BodyNode body,
            BodyNode elseBody) {
        super(lineNumber);
        this.variableName = variableName;
        this.iterableExpression = iterableExpression;
        this.body = body;
        this.elseBody = elseBody;
    }

    @Override
    public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException,
            IOException {
        Object iterableEvaluation = iterableExpression.evaluate(self, context);
        Iterable<?> iterable = null;

        if (iterableEvaluation == null) {
            return;
        }

        iterable = toIterable(iterableEvaluation);

        Iterator<?> iterator = iterable.iterator();

        boolean newScope = false;

        if (iterator.hasNext()) {

            /*
             * Only if there is a variable name conflict between one of the
             * variables added by the for loop construct and an existing
             * variable do we push another scope, otherwise we reuse the current
             * scope for performance purposes.
             */
            if (context.currentScopeContainsVariable("loop") || context.currentScopeContainsVariable(variableName)) {
                context.pushScope();
                newScope = true;
            }

            int length = getIteratorSize(iterableEvaluation);
            int index = 0;

            Map<String, Object> loop = new HashMap<>();

            while (iterator.hasNext()) {

                /*
                 * If the user is using an executor service (i.e. parallel node), we
                 * must create a new map with every iteration instead of
                 * re-using the same one; it's imperative that each thread would
                 * get it's own distinct copy of the context.
                 */
                if(context.getExecutorService() != null) {
                    loop = new HashMap<>();
                }
                loop.put("last", index == length - 1);
                loop.put("first", index == 0);
                loop.put("revindex", length - index - 1);
                loop.put("revindex0", length - index - 1);
                loop.put("index0", index);
                loop.put("index", index++);
                loop.put("length", length);

                context.put("loop", loop);

                context.put(variableName, iterator.next());
                body.render(self, writer, context);

            }

            if (newScope) {
                context.popScope();
            }

        } else if (elseBody != null) {
            elseBody.render(self, writer, context);
        }

    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public String getIterationVariable() {
        return variableName;
    }

    public Expression<?> getIterable() {
        return iterableExpression;
    }

    public BodyNode getBody() {
        return body;
    }

    public BodyNode getElseBody() {
        return elseBody;
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

            if (Array.getLength(obj) == 0) {
                return new ArrayList<>(0);
            }

            result = new Iterable<Object>() {

                @Override
                public Iterator<Object> iterator() {
                    return new Iterator<Object>() {

                        private int index = 0;

                        private final int length = Array.getLength(obj);

                        @Override
                        public boolean hasNext() {
                            return index < length;
                        }

                        @Override
                        public Object next() {
                            return Array.get(obj, index++);
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            };
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
}
