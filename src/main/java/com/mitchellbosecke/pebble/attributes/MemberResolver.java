package com.mitchellbosecke.pebble.attributes;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.mitchellbosecke.pebble.error.PebbleException;

public class MemberResolver implements AttributeResolver {

    private final ConcurrentHashMap<MemberCacheKey, Member> memberCache;

    public MemberResolver() {
        /*
         * I dont imagine that users will often give different types to the same
         * template so we will give this cache a pretty small initial capacity.
         */
        this.memberCache = new ConcurrentHashMap<>(2, 0.9f, 1);
    }
    
    @Override
    public Optional<ResolvedAttribute> resolve(final Object instance, Object attribute, final Object[] argumentValues,
            boolean isStrictVariables, String filename, int lineNumber) throws PebbleException {

        try {
            return resolveMemberCall(instance, String.valueOf(attribute), argumentValues);
        } catch (RuntimeException rx) {
            throw new RuntimeException("error on "+instance+"."+attribute+" ("+filename+":"+lineNumber+")");
        }
    }
    
    private Optional<ResolvedAttribute> resolveMemberCall(final Object object, String attributeName, final Object[] argumentValues) {
        
        final Member member = memberOf(object, attributeName, argumentValues);

        if (object != null && member != null) {
            return Optional.<ResolvedAttribute>of(new ResolvedAttribute() {
                
                @Override
                public Object evaluate() throws PebbleException {
                    return invokeMember(object, member, argumentValues);
                }
            });
        }
        
        return Optional.absent();
    }


    private Member memberOf(final Object object, String attributeName, final Object[] nullableArgumentValues) {
        Object[] argumentValues=nullableArgumentValues;
        if (argumentValues==null) {
            argumentValues=new Object[0];
        }
        
        MemberCacheKey key = new MemberCacheKey(object.getClass(), attributeName, argumentValues);
        
        Member member = object == null ? null : this.memberCache.get(key);
        if (object != null && member == null) {
            /*
             * turn args into an array of types and an array of values in order
             * to use them for our reflection calls
             */
            Class<?>[] argumentTypes = new Class<?>[argumentValues.length];

            for (int i = 0; i < argumentValues.length; i++) {
                Object o = argumentValues[i];
                if (o == null) {
                    argumentTypes[i] = null;
                } else {
                    argumentTypes[i] = o.getClass();
                }
            }

            member = reflect(object, attributeName, argumentTypes);
            if (member != null) {
                this.memberCache.put(key, member);
            }

        }
        
        final Member resultingMember = member;
        return resultingMember;
    }

    /**
     * Invoke the "Member" that was found via reflection.
     *
     * @param object
     * @param member
     * @param argumentValues
     * @return
     */
    private static Object invokeMember(Object object, Member member, Object[] argumentValues) {
        Object result = null;
        try {
            if (member instanceof Method) {
                result = ((Method) member).invoke(object, argumentValues);
            } else if (member instanceof Field) {
                result = ((Field) member).get(object);
            }

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("error invoking "+member+" with "+ImmutableList.copyOf(classesOf(argumentValues)),e);
        }
        return result;
    }

    /**
     * Performs the actual reflection to obtain a "Member" from a class.
     *
     * @param object
     * @param attributeName
     * @param parameterTypes
     * @return
     */
    private static Member reflect(Object object, String attributeName, Class<?>[] parameterTypes) {

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
    private static Method findMethod(Class<?> clazz, String name, Class<?>[] requiredTypes) {
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
                if (requiredTypes[i] != null && !widen(types[i]).isAssignableFrom(requiredTypes[i])) {
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
    private static Class<?> widen(Class<?> clazz) {
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
        } else if (clazz == boolean.class) {
            result = Boolean.class;
        }
        return result;
    }

    private static Class<?>[] classesOf(Object[] arguments) {
        if (arguments!=null) {
            Class<?>[] classes=new Class[arguments.length];
            for (int i=0;i<classes.length;i++) {
                classes[i]=arguments[i] != null ? arguments[i].getClass() : Void.class;
            }
            return classes;
        }
        return new Class[0];
    }
    
    private static class MemberCacheKey {
        private final Class<?> clazz;
        private final String attributeName;
        private final ImmutableList<Class<?>> argumentClasses;

        private MemberCacheKey(Class<?> clazz, String attributeName, Object[] arguments) {
            this.clazz = clazz;
            this.attributeName = attributeName;
            this.argumentClasses = ImmutableList.copyOf(classesOf(arguments));
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((argumentClasses == null) ? 0 : argumentClasses.hashCode());
            result = prime * result + ((attributeName == null) ? 0 : attributeName.hashCode());
            result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MemberCacheKey other = (MemberCacheKey) obj;
            if (argumentClasses == null) {
                if (other.argumentClasses != null)
                    return false;
            } else if (!argumentClasses.equals(other.argumentClasses))
                return false;
            if (attributeName == null) {
                if (other.attributeName != null)
                    return false;
            } else if (!attributeName.equals(other.attributeName))
                return false;
            if (clazz == null) {
                if (other.clazz != null)
                    return false;
            } else if (!clazz.equals(other.clazz))
                return false;
            return true;
        }

        
    }

}
