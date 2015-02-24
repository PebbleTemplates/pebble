/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.PositionalArgumentNode;
import com.mitchellbosecke.pebble.template.ClassAttributeCache;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

/**
 * Used to get an attribute from an object. It will look up attributes in the
 * following order: map entry, get method, is method, has method, public method,
 * public field.
 * 
 * @author Mitchell
 * 
 */
public class GetAttributeExpression implements Expression<Object> {

    private final Expression<?> node;

    private final String attributeName;

    private final ArgumentsNode args;

    /**
     * Cached on first evaluation.
     */
    private Class<?>[] argumentTypes;

    private Object[] argumentValues;
    
    private boolean firstEvaluation;

    public GetAttributeExpression(Expression<?> node, String attributeName) {
        this(node, attributeName, null);
    }

    public GetAttributeExpression(Expression<?> node, String attributeName, ArgumentsNode args) {
        this.node = node;
        this.attributeName = attributeName;
        this.args = args;
        this.firstEvaluation = true;
    }

    @Override
    public Object evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
        Object object = node.evaluate(self, context);

        Object result = null;
        boolean found = false;

        if (object != null) {

            // optimization check to avoid checking maps/arrays/lists if user
            // provided argument
            if (args == null) {

                // first we check maps
                if (object instanceof Map && ((Map<?, ?>) object).containsKey(attributeName)) {
                    result = ((Map<?, ?>) object).get(attributeName);
                    found = true;
                }

                try {

                    // then we check arrays
                    if (object instanceof Object[]) {
                        Integer key = Integer.valueOf(attributeName);
                        Object[] arr = ((Object[]) object);
                        return arr[key];
                    }

                    // then lists
                    if (object instanceof List) {
                        Integer key = Integer.valueOf(attributeName);
                        @SuppressWarnings("unchecked")
                        List<Object> list = (List<Object>) object;
                        return list.get(key);
                    }
                } catch (NumberFormatException ex) {
                    // do nothing
                }

            }

            if (!found) {

                /*
                 * turn args into an array of types and an array of values in
                 * order to use them for our reflection calls
                 */
                if (this.args != null) {

                    List<PositionalArgumentNode> args = this.args.getPositionalArgs();

                    // argument types is only set on the first evaluation
                    // of the template; cached from that point on.
                    if (this.firstEvaluation) {
                        this.argumentTypes = new Class<?>[args.size()];
                        this.argumentValues = new Object[args.size()];
                    }

                    int index = 0;
                    for (PositionalArgumentNode arg : args) {
                        Object argumentValue = arg.getValueExpression().evaluate(self, context);
                        argumentValues[index] = argumentValue;
                        
                        if(this.firstEvaluation){
                            argumentTypes[index] = argumentValue.getClass();
                        }
                        
                        index++;
                    }
                }

                Member member = null;
                try {

                    ClassAttributeCache cache = self.getAttributeCache();

                    /*
                     * Because we are performing more than one atomic action on
                     * the cache ("get", and "put") we would typically wrap
                     * these in a synchronized block. However, objects are never
                     * removed from the cache (only added) and therefore I don't
                     * think complete synchronization is necessary.
                     * 
                     * There is a chance that more than one thread will attempt
                     * to insert an entry into the cache at the same time but
                     * theoretically each thread would be adding the exact same
                     * value and the "put" method by itself is atomic so no harm
                     * done (other than some unnecessary work by at least one of
                     * the threads).
                     */
                    member = cache.get(object, attributeName, this.argumentTypes);
                    if (member == null) {
                        member = findMember(object, attributeName, this.argumentTypes);
                        if (member != null) {
                            cache.put(object, attributeName, argumentTypes, member);
                        }
                    }

                    if (member != null) {

                        if (member instanceof Method) {
                            result = ((Method) member).invoke(object, argumentValues);
                        } else if (member instanceof Field) {
                            result = ((Field) member).get(object);
                        }
                        found = true;
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                    throw new PebbleException(e, "Could not access attribute [" + attributeName + "]");
                }

            }
        }

        this.firstEvaluation = false;
        if (!found && context.isStrictVariables()) {
            throw new AttributeNotFoundException(
                    null,
                    String.format(
                            "Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true.",
                            attributeName, object.getClass().getName()));
        }
        return result;

    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public Expression<?> getNode() {
        return node;
    }

    public String getAttribute() {
        return attributeName;
    }

    public ArgumentsNode getArgumentsNode() {
        return args;
    }

    private Member findMember(Object object, String attributeName, Class<?>[] parameterTypes)
            throws IllegalAccessException {

        Class<?> clazz = object.getClass();

        boolean found = false;
        Member result = null;

        // capitalize first letter of attribute for the following attempts
        String attributeCapitalized = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);

        // check get method
        if (!found) {
            try {
                result = clazz.getMethod("get" + attributeCapitalized, parameterTypes);
                found = true;
            } catch (NoSuchMethodException | SecurityException e) {
            }
        }

        // check is method
        if (!found) {
            try {
                result = clazz.getMethod("is" + attributeCapitalized, parameterTypes);
                found = true;
            } catch (NoSuchMethodException | SecurityException e) {
            }
        }

        // check has method
        if (!found) {
            try {
                result = clazz.getMethod("has" + attributeCapitalized, parameterTypes);
                found = true;
            } catch (NoSuchMethodException | SecurityException e) {
            }
        }

        // check if attribute is a public method
        if (!found) {
            try {
                result = clazz.getMethod(attributeName, parameterTypes);
                found = true;
            } catch (NoSuchMethodException | SecurityException e) {
            }
        }

        // public field
        if (!found) {
            try {
                result = clazz.getField(attributeName);
                found = true;
            } catch (NoSuchFieldException | SecurityException e) {
            }
        }

        if (result != null) {
            ((AccessibleObject) result).setAccessible(true);
        }
        return result;
    }

}
