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
     * Potentially cached on first evaluation.
     */
    private Member member;

    /**
     * A lock to ensure that only one thread at a time will update the "member"
     * field.
     */
    private Object memberLock = new Object();

    public GetAttributeExpression(Expression<?> node, String attributeName) {
        this(node, attributeName, null);
    }

    public GetAttributeExpression(Expression<?> node, String attributeName, ArgumentsNode args) {
        this.node = node;
        this.attributeName = attributeName;
        this.args = args;
        this.member = null;
    }

    @Override
    public Object evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
        Object object = node.evaluate(self, context);

        Object result = null;

        Object[] argumentValues = null;

        if (object != null && member == null) {

            /*
             * If, and only if, no arguments were provided does it make sense to
             * check maps/arrays/lists
             */
            if (args == null) {

                // first we check maps
                if (object instanceof Map && ((Map<?, ?>) object).containsKey(attributeName)) {
                    return ((Map<?, ?>) object).get(attributeName);
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

            /*
             * Only one thread at a time should
             */
            synchronized (memberLock) {

                if (member == null) {
                    /*
                     * turn args into an array of types and an array of values
                     * in order to use them for our reflection calls
                     */
                    argumentValues = getArgumentValues(self, context);
                    Class<?>[] argumentTypes = new Class<?>[argumentValues.length];

                    for (int i = 0; i < argumentValues.length; i++) {
                        argumentTypes[i] = argumentValues[i].getClass();
                    }

                    member = reflect(object, attributeName, argumentTypes);
                }
            }

        }

        if (object != null && member != null) {
            if (argumentValues == null) {
                argumentValues = getArgumentValues(self, context);
            }
            result = invokeMember(object, member, argumentValues);
        } else if (context.isStrictVariables()) {
            throw new AttributeNotFoundException(
                    null,
                    String.format(
                            "Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true.",
                            attributeName, object.getClass().getName()));
        }
        return result;

    }

    /**
     * Invoke the "Member" that was found via reflection.
     * 
     * @param object
     * @param member
     * @param argumentValues
     * @return
     */
    private Object invokeMember(Object object, Member member, Object[] argumentValues) {
        Object result = null;
        try {
            if (member != null) {

                if (member instanceof Method) {
                    result = ((Method) member).invoke(object, argumentValues);
                } else if (member instanceof Field) {
                    result = ((Field) member).get(object);
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();

        }
        return result;
    }

    /**
     * Fully evaluates the individual arguments.
     * 
     * @param self
     * @param context
     * @return
     * @throws PebbleException
     */
    private Object[] getArgumentValues(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {

        Object[] argumentValues;

        if (this.args == null) {
            argumentValues = new Object[0];
        } else {
            List<PositionalArgumentNode> args = this.args.getPositionalArgs();

            argumentValues = new Object[args.size()];

            int index = 0;
            for (PositionalArgumentNode arg : args) {
                Object argumentValue = arg.getValueExpression().evaluate(self, context);
                argumentValues[index] = argumentValue;
                index++;
            }
        }
        return argumentValues;
    }

    /**
     * Performs the actual reflection to obtain a "Member" from a class.
     * 
     * @param object
     * @param attributeName
     * @param parameterTypes
     * @return
     */
    private Member reflect(Object object, String attributeName, Class<?>[] parameterTypes) {

        Class<?> clazz = object.getClass();

        boolean found = false;
        Member result = null;

        // capitalize first letter of attribute for the following attempts
        String attributeCapitalized = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);

        // try {

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

}
