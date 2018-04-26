package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.error.ClassAccessException;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class MemberResolver implements AttributeResolver {

  private final ConcurrentHashMap<MemberCacheKey, Member> memberCache = new ConcurrentHashMap<>(100, 0.9f, 1);

  @Override
  public ResolvedAttribute resolve(Object instance,
                                   Object attributeNameValue,
                                   Object[] argumentValues,
                                   boolean isStrictVariables,
                                   String filename,
                                   int lineNumber) {
    String attributeName = String.valueOf(attributeNameValue);
    Member member = this.getMember(instance, attributeName);
    if (member == null) {
      if (argumentValues == null) {
        argumentValues = new Object[0];
      }
      Class<?>[] argumentTypes = new Class<?>[argumentValues.length];

      for (int i = 0; i < argumentValues.length; i++) {
        Object o = argumentValues[i];
        if (o == null) {
          argumentTypes[i] = null;
        } else {
          argumentTypes[i] = o.getClass();
        }
      }

      member = this.reflect(instance, attributeName, argumentTypes);
      if (member != null) {
        this.memberCache.put(new MemberCacheKey(instance.getClass(), attributeName), member);
      }
    }

    if (member != null) {
      Member finalMember = member;
      Object[] finalArgumentValues = argumentValues;
      return () -> invokeMember(instance, finalMember, finalArgumentValues);
    } else if (isStrictVariables) {
      if (attributeName.equals("class") || attributeName.equals("getClass")) {
        throw new ClassAccessException(lineNumber, filename);
      }
    }

    return null;
  }

  Member getMember(Object instance, String attributeName) {
    return instance == null ? null : this.memberCache.get(new MemberCacheKey(instance.getClass(), attributeName));
  }

  /**
   * Performs the actual reflection to obtain a "Member" from a class.
   */
  private Member reflect(Object object, String attributeName, Class<?>[] parameterTypes) {

    Class<?> clazz = object.getClass();

    // capitalize first letter of attribute for the following attempts
    String attributeCapitalized = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);

    // check get method
    Member result = this.findMethod(clazz, "get" + attributeCapitalized, parameterTypes);

    // check is method
    if (result == null) {
      result = this.findMethod(clazz, "is" + attributeCapitalized, parameterTypes);
    }

    // check has method
    if (result == null) {
      result = this.findMethod(clazz, "has" + attributeCapitalized, parameterTypes);
    }

    // check if attribute is a public method
    if (result == null) {
      result = this.findMethod(clazz, attributeName, parameterTypes);
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
   */
  private Method findMethod(Class<?> clazz, String name, Class<?>[] requiredTypes) {
    if (name.equals("getClass")) {
      return null;
    }

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
        if (requiredTypes[i] != null && !this.widen(types[i]).isAssignableFrom(requiredTypes[i])) {
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
    } else if (clazz == boolean.class) {
      result = Boolean.class;
    }
    return result;
  }

  private Object invokeMember(Object instance, Member member, Object[] argumentValues) {
    Object result = null;
    try {
      if (member instanceof Method) {
        result = ((Method) member).invoke(instance, argumentValues);
      } else if (member instanceof Field) {
        result = ((Field) member).get(instance);
      }

    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  private class MemberCacheKey {
    private final Class<?> clazz;
    private final String attributeName;

    private MemberCacheKey(Class<?> clazz, String attributeName) {
      this.clazz = clazz;
      this.attributeName = attributeName;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || this.getClass() != o.getClass()) return false;

      MemberCacheKey that = (MemberCacheKey) o;

      if (!this.clazz.equals(that.clazz)) return false;
      return this.attributeName.equals(that.attributeName);

    }

    @Override
    public int hashCode() {
      int result = this.clazz.hashCode();
      result = 31 * result + this.attributeName.hashCode();
      return result;
    }
  }
}
