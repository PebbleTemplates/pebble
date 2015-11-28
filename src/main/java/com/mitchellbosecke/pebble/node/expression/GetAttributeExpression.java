/*******************************************************************************
 * This file is part of Pebble.
<<<<<<< HEAD
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
=======
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
>>>>>>> d6a41085fe86ce30f23d3b7929ad492343ff01b7
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.RootAttributeNotFoundException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.PositionalArgumentNode;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

/**
 * Used to get an attribute from an object. It will look up attributes in the
 * following order: map entry, array item, list item, get method, is method, has
 * method, public method, public field.
 *
 * @author Mitchell
 *
 */
public class GetAttributeExpression implements Expression<Object> {

    private final Expression<?> node;

    private final Expression<?> attributeNameExpression;

    private final ArgumentsNode args;

    private final String filename;

    private final int lineNumber;

    /**
     * Potentially cached on first evaluation.
     */
    private final ConcurrentHashMap<Class<?>, Member> memberCache;

    public GetAttributeExpression(Expression<?> node, Expression<?> attributeNameExpression, String filename,
            int lineNumber) {
        this(node, attributeNameExpression, null, filename, lineNumber);
    }

    public GetAttributeExpression(Expression<?> node, Expression<?> attributeNameExpression, ArgumentsNode args,
            String filename, int lineNumber) {

        this.node = node;
        this.attributeNameExpression = attributeNameExpression;
        this.args = args;
        this.filename = filename;
        this.lineNumber = lineNumber;

        /*
         * I dont imagine that users will often give different types to the same
         * template so we will give this cache a pretty small initial capacity.
         */
        this.memberCache = new ConcurrentHashMap<>(2, 0.9f, 1);
    }

    @Override
    public Object evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
        Object object = node.evaluate(self, context);
        Object attributeNameValue = attributeNameExpression.evaluate(self, context);
        String attributeName = String.valueOf(attributeNameValue);

        Object result = null;

        Object[] argumentValues = null;

        Member member = object == null ? null : memberCache.get(object.getClass());

        if (object != null && member == null) {

            /*
             * If, and only if, no arguments were provided does it make sense to
             * check maps/arrays/lists
             */
            if (args == null) {

                // first we check maps
                if (object instanceof Map && ((Map<?, ?>) object).containsKey(attributeNameValue)) {
                    return ((Map<?, ?>) object).get(attributeNameValue);
                }

                try {

                    // then we check arrays
                    if (object.getClass().isArray()) {
                        int index = Integer.parseInt(attributeName);
                        return Array.get(object, index);
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
             * turn args into an array of types and an array of values in order
             * to use them for our reflection calls
             */
            argumentValues = getArgumentValues(self, context);
            Class<?>[] argumentTypes = new Class<?>[argumentValues.length];

            for (int i = 0; i < argumentValues.length; i++) {
                argumentTypes[i] = argumentValues[i].getClass();
            }

            member = reflect(object, attributeName, argumentTypes);
            if (member != null) {
                memberCache.put(object.getClass(), member);
            }

        }

        if (object != null && member != null) {
            if (argumentValues == null) {
                argumentValues = getArgumentValues(self, context);
            }
            result = invokeMember(object, member, argumentValues);
        } else if (context.isStrictVariables()) {
            if (object == null) {
                final String rootPropertyName = ((ContextVariableExpression) node).getName();

                throw new RootAttributeNotFoundException(
                        null,
                        String.format(
                                "Root attribute [%s] does not exist or can not be accessed and strict variables is set to true.",
                                rootPropertyName), rootPropertyName, this.lineNumber, this.filename);
            } else {
                throw new AttributeNotFoundException(
                        null,
                        String.format(
                                "Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true.",
                                attributeName, object.getClass().getName()), attributeName, this.lineNumber,
                        this.filename);
            }
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
            throw new RuntimeException(e);
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

        Member result = null;

        // capitalize first letter of attribute for the following attempts
        String attributeCapitalized = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);

        // check get method
        result = findMethod(clazz, "get" + attributeCapitalized, parameterTypes);

        // check is method
        if (result == null) {
            result = findMethod(clazz, "is" + attributeCapitalized, parameterTypes);
        }

        // check has method
        if (result == null) {
            result = findMethod(clazz, "has" + attributeCapitalized, parameterTypes);
        }

        // check if attribute is a public method
        if (result == null) {
            result = findMethod(clazz, attributeName, parameterTypes);
        }

        // public field
        if (result == null) {
            try {
                result = clazz.getField(attributeName);
            } catch (NoSuchFieldException | SecurityException e) {
            }
        }

        if (result != null) {
            ((AccessibleObject) result).setAccessible(true);
        }

        return result;
    }

    /**
     * Finds an appropriate method by comparing if parameter types are
     * compatible. This is more relaxed than class.getMethod.
     *
     * @param clazz
     * @param name
     * @param requiredTypes
     * @return
     */
    private Method findMethod(Class<?> clazz, String name, Class<?>[] requiredTypes) {
        Method result = null;

        Method[] candidates = clazz.getMethods();

        for (Method candidate : candidates) {
            if (!candidate.getName().equalsIgnoreCase(name)) {
                continue;
            }

            Class<?>[] types = candidate.getParameterTypes();

            if (types.length != requiredTypes.length) {
                continue;
            }

            boolean compatibleTypes = true;
            for (int i = 0; i < types.length; i++) {
                if (!widen(types[i]).isAssignableFrom(requiredTypes[i])) {
                    compatibleTypes = false;
                    break;
                }
            }

            if (compatibleTypes) {
                result = candidate;
                break;
            }
        }
        return result;
    }

    /**
     * Performs a widening conversion (primitive to boxed type)
     *
     * @param clazz
     * @return
     */
    private Class<?> widen(Class<?> clazz) {
        Class<?> result = clazz;
        if (clazz == int.class) {
            result = Integer.class;
        } else if (clazz == long.class) {
            result = Long.class;
        } else if (clazz == double.class) {
            result = Double.class;
        } else if (clazz == float.class) {
            result = Float.class;
        } else if (clazz == short.class) {
            result = Short.class;
        } else if (clazz == byte.class) {
            result = Byte.class;
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

    public ArgumentsNode getArgumentsNode() {
        return args;
    }

    @Override
    public int getLineNumber() {
        return this.lineNumber;
    }

}
