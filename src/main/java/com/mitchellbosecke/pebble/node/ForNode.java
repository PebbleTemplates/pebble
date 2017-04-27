/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.template.ScopeChain;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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

    public ForNode(int lineNumber, String variableName, Expression<?> iterableExpression, BodyNode body,
            BodyNode elseBody) {
        super(lineNumber);
        this.variableName = variableName;
        this.iterableExpression = iterableExpression;
        this.body = body;
        this.elseBody = elseBody;
    }

    @Override
    public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context)
            throws PebbleException, IOException {
        Object iterableEvaluation = this.iterableExpression.evaluate(self, context);
        Iterable<?> iterable = null;

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

            int length = this.getIteratorSize(iterableEvaluation);
            int index = 0;

            Map<String, Object> loop = null;

            boolean usingExecutorService = context.getExecutorService() != null;

            while (iterator.hasNext()) {

                /*
                 * If the user is using an executor service (i.e. parallel node), we
                 * must create a new map with every iteration instead of
                 * re-using the same one; it's imperative that each thread would
                 * get it's own distinct copy of the context.
                 */
                if (index == 0 || usingExecutorService) {
                    loop = new HashMap<>();
                    loop.put("first", index == 0);
                    loop.put("last", index == length - 1);
                    loop.put("length", length);
                }else{

                    // second iteration
                    if(index == 1){
                        loop.put("first", false);
                    }

                    // last iteration
                    if(index == length - 1){
                        loop.put("last", true);
                    }
                }

                loop.put("revindex", length - index - 1);
                loop.put("index", index++);

                scopeChain.put("loop", loop);

                scopeChain.put(this.variableName, iterator.next());
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
