package io.pebbletemplates.pebble.attributes;

import io.pebbletemplates.pebble.error.ClassAccessException;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.EvaluationOptions;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class MemberCacheUtils {
  private final ConcurrentHashMap<MemberCacheKey, Member> memberCache = new ConcurrentHashMap<>(100,
          0.9f, 1);

  // REFACTOR (Duplicate Code): the get/is/has accessor attempts were three
  // near-identical blocks. Extracting the prefixes into a constant lets us try
  // them in a single loop and makes adding a new convention a one-line change.
  private static final String[] ACCESSOR_PREFIXES = {"get", "is", "has"};

  Member getMember(Object instance, String attributeName, Class<?>[] argumentTypes) {
    return this.memberCache.get(new MemberCacheKey(instance.getClass(), attributeName, argumentTypes));
  }

  Member cacheMember(Object instance,
                     String attributeName,
                     Class<?>[] argumentTypes,
                     EvaluationContextImpl context,
                     String filename,
                     int lineNumber) {
    Member member = this.reflect(instance, attributeName, argumentTypes, filename, lineNumber, context.getEvaluationOptions());
    if (member != null) {
      this.memberCache.put(new MemberCacheKey(instance.getClass(), attributeName, argumentTypes), member);
    }
    return member;
  }

  /**
   * Performs the actual reflection to obtain a "Member" from a class.
   */
  private Member reflect(Object object, String attributeName, Class<?>[] parameterTypes,
                         String filename, int lineNumber, EvaluationOptions evaluationOptions) {

    Class<?> clazz = object.getClass();

    // capitalize first letter of attribute for the following attempts
    String attributeCapitalized = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);

    // search well known super classes first to avoid illegal reflective access
    List<Class<?>> agenda = Arrays.asList(
            List.class,
            Set.class,
            Map.class,
            Map.Entry.class,
            Collection.class,
            Iterable.class,
            clazz);

    for (Class<?> type : agenda) {
      if (!type.isAssignableFrom(clazz)) {
        continue;
      }

      // check if attribute is a public method
      Member result = this.findMethod(object, type, attributeName, parameterTypes, filename, lineNumber, evaluationOptions);

      // public field
      if (result == null) {
        try {
          result = type.getField(attributeName);
        } catch (NoSuchFieldException | SecurityException e) {
        }
      }

      // REFACTOR (Duplicate Code): replaced the three copy-pasted get/is/has
      // blocks with a single loop over ACCESSOR_PREFIXES. Same order, same
      // behaviour, no repetition.
      if (result == null) {
        for (String prefix : ACCESSOR_PREFIXES) {
          result = this.findMethod(object, type, prefix + attributeCapitalized, parameterTypes, filename, lineNumber, evaluationOptions);
          if (result != null) {
            break;
          }
        }
      }

      if (result != null) {
        ((AccessibleObject) result).setAccessible(true);
        return result;
      }
    }
    return null;
  }

  /**
   * Finds an appropriate method by comparing if parameter types are compatible. This is more
   * relaxed than class.getMethod.
   *
   * REFACTOR (Long Method): the original findMethod was ~55 lines and mixed two
   * distinct matching strategies (perfect match and greedy match) in one body.
   * The two inner loops were extracted into findPerfectMatch and findGreedyMatch,
   * shrinking this method to its high-level control flow. verifyUnsafeMethod is
   * still called at the exact same point as before, so behaviour is unchanged.
   */
  private Method findMethod(Object object, Class<?> clazz, String name, Class<?>[] requiredTypes,
                            String filename, int lineNumber, EvaluationOptions evaluationOptions) {
    List<Method> candidates = this.getCandidates(clazz, name, requiredTypes);

    Method bestMatch = this.findPerfectMatch(candidates, requiredTypes);
    if (bestMatch != null) {
      this.verifyUnsafeMethod(filename, lineNumber, evaluationOptions, object, bestMatch);
      return bestMatch;
    }

    if (evaluationOptions.isGreedyMatchMethod()) {
      Method greedyMatch = this.findGreedyMatch(candidates, requiredTypes);
      if (greedyMatch != null) {
        this.verifyUnsafeMethod(filename, lineNumber, evaluationOptions, object, greedyMatch);
        return greedyMatch;
      }
    }

    return null;
  }

  /**
   * Finds the most specific candidate whose parameter types are assignable from
   * the required argument types. Extracted from findMethod (Long Method fix).
   */
  private Method findPerfectMatch(List<Method> candidates, Class<?>[] requiredTypes) {
    Method bestMatch = null;
    for (Method candidate : candidates) {
      // check if method is even compatible
      boolean compatibleTypes = true;
      Class<?>[] types = candidate.getParameterTypes();
      for (int i = 0; i < types.length; i++) {
        if (requiredTypes[i] != null && !this.widen(types[i]).isAssignableFrom(requiredTypes[i])) {
          compatibleTypes = false;
          break;
        }
      }

      // if it is compatible, check if it is a better match than the previous best
      if (compatibleTypes) {
        if (bestMatch == null) {
          bestMatch = candidate;
        } else {
          Class<?>[] bestMatchParamTypes = bestMatch.getParameterTypes();
          for (int i = 0; i < types.length; i++) {
            // if the current method's param strictly extends the previous best, it is a better match
            Class<?> widened = this.widen(bestMatchParamTypes[i]);
            if (widened.isAssignableFrom(types[i]) && !widened.equals(types[i])) {
              bestMatch = candidate;
              break;
            }
          }
        }
      }
    }
    return bestMatch;
  }

  /**
   * Finds the first candidate compatible under relaxed numeric matching. Only
   * used when greedy method matching is enabled. Extracted from findMethod
   * (Long Method fix).
   */
  private Method findGreedyMatch(List<Method> candidates, Class<?>[] requiredTypes) {
    for (Method candidate : candidates) {
      boolean compatibleTypes = true;
      Class<?>[] types = candidate.getParameterTypes();
      for (int i = 0; i < types.length; i++) {
        if (requiredTypes[i] != null && !this.isCompatibleType(types[i], requiredTypes[i])) {
          compatibleTypes = false;
          break;
        }
      }

      if (compatibleTypes) {
        return candidate;
      }
    }
    return null;
  }

  private void verifyUnsafeMethod(String filename, int lineNumber,
                                  EvaluationOptions evaluationOptions, Object object, Method method) {
    boolean methodAccessAllowed = evaluationOptions.getMethodAccessValidator()
            .isMethodAccessAllowed(object, method);
    if (!methodAccessAllowed) {
      throw new ClassAccessException(method, filename, lineNumber);
    }
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
    Class<?> widenType = this.widen(type1);
    return Number.class.isAssignableFrom(widenType) && Number.class.isAssignableFrom(type2);
  }

  // REFACTOR (Inappropriate Intimacy / hidden coupling): MemberCacheKey was a
  // non-static inner class, so every cached key silently held a reference to the
  // enclosing MemberCacheUtils instance even though it never uses it. Marking it
  // static removes that hidden reference from every key stored in the cache.
  private static class MemberCacheKey {

    private final Class<?> clazz;
    private final String attributeName;
    private final Class<?>[] methodParameterTypes;

    public MemberCacheKey(Class<?> clazz, String attributeName, Class<?>[] methodParameterTypes) {
      this.clazz = clazz;
      this.attributeName = attributeName;
      this.methodParameterTypes = methodParameterTypes;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || this.getClass() != o.getClass()) {
        return false;
      }

      MemberCacheKey that = (MemberCacheKey) o;

      if (!this.clazz.equals(that.clazz)) {
        return false;
      }
      if (!this.attributeName.equals(that.attributeName)) {
        return false;
      }

      return Arrays.equals(this.methodParameterTypes, that.methodParameterTypes);
    }

    @Override
    public int hashCode() {
      int result = this.clazz.hashCode();
      result = 31 * result + this.attributeName.hashCode();
      result = 31 * result + Arrays.hashCode(this.methodParameterTypes);
      return result;
    }
  }
}