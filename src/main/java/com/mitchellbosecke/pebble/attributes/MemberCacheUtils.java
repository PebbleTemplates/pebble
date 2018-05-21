package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.error.ClassAccessException;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.EvaluationOptions;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MemberCacheUtils {
  private final ConcurrentHashMap<MemberCacheKey, Member> memberCache = new ConcurrentHashMap<>(100, 0.9f, 1);

  Member getMember(Object instance, String attributeName) {
    return this.memberCache.get(new MemberCacheKey(instance.getClass(), attributeName));
  }

  Member cacheMember(Object instance,
                     String attributeName,
                     Object[] argumentValues,
                     EvaluationContextImpl context,
                     String filename,
                     int lineNumber) {
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

    Member member = this.reflect(instance, attributeName, argumentTypes, filename, lineNumber, context.getEvaluationOptions());
    if (member != null) {
      this.memberCache.put(new MemberCacheKey(instance.getClass(), attributeName), member);
    }
    return member;
  }

  /**
   * Performs the actual reflection to obtain a "Member" from a class.
   */
  private Member reflect(Object object, String attributeName, Class<?>[] parameterTypes, String filename, int lineNumber, EvaluationOptions evaluationOptions) {

    Class<?> clazz = object.getClass();

    // capitalize first letter of attribute for the following attempts
    String attributeCapitalized = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);

    // check get method
    Member result = this.findMethod(clazz, "get" + attributeCapitalized, parameterTypes, filename, lineNumber, evaluationOptions);

    // check is method
    if (result == null) {
      result = this.findMethod(clazz, "is" + attributeCapitalized, parameterTypes, filename, lineNumber, evaluationOptions);
    }

    // check has method
    if (result == null) {
      result = this.findMethod(clazz, "has" + attributeCapitalized, parameterTypes, filename, lineNumber, evaluationOptions);
    }

    // check if attribute is a public method
    if (result == null) {
      result = this.findMethod(clazz, attributeName, parameterTypes, filename, lineNumber, evaluationOptions);
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
  private Method findMethod(Class<?> clazz, String name, Class<?>[] requiredTypes, String filename, int lineNumber, EvaluationOptions evaluationOptions) {
    if (!evaluationOptions.isAllowGetClass() && name.equals("getClass")) {
      throw new ClassAccessException(lineNumber, filename);
    }

    List<Method> candidates = getCandidates(clazz, name, requiredTypes);

    // perfect match
    for (Method candidate : candidates) {
      boolean compatibleTypes = true;
      Class<?>[] types = candidate.getParameterTypes();
      for (int i = 0; i < types.length; i++) {
        if (requiredTypes[i] != null && !this.widen(types[i]).isAssignableFrom(requiredTypes[i])) {
          compatibleTypes = false;
          break;
        }
      }

      if (compatibleTypes) {
        return candidate;
      }
    }

    // greedy match
    if (evaluationOptions.isGreedyMatchMethod()) {
      for (Method candidate : candidates) {
        boolean compatibleTypes = true;
        Class<?>[] types = candidate.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
          if (requiredTypes[i] != null && !isCompatibleType(types[i], requiredTypes[i])) {
            compatibleTypes = false;
            break;
          }
        }

        if (compatibleTypes) {
          return candidate;
        }
      }
    }

    return null;
  }

  /**
   * Performs a widening conversion (primitive to boxed type)
   */
  private Class<?> widen(Class<?> clazz) {
    if (clazz == int.class) {
      return Integer.class;
    }
    if (clazz == long.class) {
      return Long.class;
    }
    if (clazz == double.class) {
      return Double.class;
    }
    if (clazz == float.class) {
      return Float.class;
    }
    if (clazz == short.class) {
      return Short.class;
    }
    if (clazz == byte.class) {
      return Byte.class;
    }
    if (clazz == boolean.class) {
      return Boolean.class;
    }
    return clazz;
  }

  private List<Method> getCandidates(Class<?> clazz, String name, Object[] requiredTypes) {
    List<Method> candidates = new ArrayList<>();
    Method[] methods = clazz.getMethods();
    for (Method m : methods) {
      if (!m.getName().equalsIgnoreCase(name)) {
        continue;
      }

      Class<?>[] types = m.getParameterTypes();
      if (types.length != requiredTypes.length) {
        continue;
      }
      candidates.add(m);
    }
    return candidates;
  }

  private boolean isCompatibleType(Class<?> type1, Class<?> type2) {
    Class<?> widenType = widen(type1);
    return Number.class.isAssignableFrom(widenType) && Number.class.isAssignableFrom(type2);
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
