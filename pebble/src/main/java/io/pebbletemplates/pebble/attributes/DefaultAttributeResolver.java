package io.pebbletemplates.pebble.attributes;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.MacroAttributeProvider;
import io.pebbletemplates.pebble.utils.TypeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class DefaultAttributeResolver implements AttributeResolver {

  private final MemberCacheUtils memberCacheUtils = new MemberCacheUtils();

  // REFACTOR (hidden allocation on the hot path): plain property access such as
  // {{ user.name }} passes no arguments, so getArgumentTypes previously allocated
  // a brand-new empty Class<?>[] on every single attribute access. A shared,
  // immutable empty array is reused instead. An empty array is stateless, so
  // sharing one instance is safe.
  private static final Class<?>[] EMPTY_ARGUMENT_TYPES = new Class<?>[0];

  @Override
  public ResolvedAttribute resolve(Object instance,
                                   Object attributeNameValue,
                                   Object[] argumentValues,
                                   ArgumentsNode args,
                                   EvaluationContextImpl context,
                                   String filename,
                                   int lineNumber) {
    if (instance != null) {
      String attributeName = String.valueOf(attributeNameValue);

      Class<?>[] argumentTypes = this.getArgumentTypes(argumentValues);
      Member member = this.memberCacheUtils.getMember(instance, attributeName, argumentTypes);
      if (member == null) {
        if (argumentValues == null) {

          // first we check maps
          if (instance instanceof Map) {
            return MapResolver.INSTANCE
                    .resolve(instance, attributeNameValue, null, args, context, filename, lineNumber);
          }

          // then we check arrays
          if (instance.getClass().isArray()) {
            return ArrayResolver.INSTANCE
                    .resolve(instance, attributeNameValue, null, args, context, filename, lineNumber);
          }

          // then lists
          if (instance instanceof List) {
            ResolvedAttribute resolvedAttribute = ListResolver.INSTANCE
                    .resolve(instance, attributeNameValue, null, args, context, filename, lineNumber);
            if (resolvedAttribute != null) {
              return resolvedAttribute;
            }
          }
        }

        if (instance instanceof MacroAttributeProvider) {
          return MacroResolver.INSTANCE
                  .resolve(instance, attributeNameValue, argumentValues, args, context, filename,
                          lineNumber);
        }

        member = this.memberCacheUtils
                .cacheMember(instance, attributeName, argumentTypes, context, filename, lineNumber);
      }

      if (member != null) {
        return new ResolvedAttribute(this.invokeMember(instance, member, argumentValues, filename, lineNumber));
      }
    }
    return null;
  }

  // REFACTOR (hidden allocation on the hot path): the null branch now returns the
  // shared EMPTY_ARGUMENT_TYPES constant instead of "new Class<?>[0]". Also flipped
  // to an early return so the common no-argument case exits immediately.
  private Class<?>[] getArgumentTypes(Object[] argumentValues) {
    if (argumentValues == null) {
      return EMPTY_ARGUMENT_TYPES;
    }

    Class<?>[] argumentTypes = new Class<?>[argumentValues.length];
    for (int i = 0; i < argumentValues.length; i++) {
      Object o = argumentValues[i];
      argumentTypes[i] = (o == null) ? null : o.getClass();
    }
    return argumentTypes;
  }

  /**
   * Invoke the "Member" that was found via reflection.
   */
  private Object invokeMember(Object object, Member member, Object[] argumentValues, String filename, int lineNumber) {
    Object result = null;
    try {
      if (member instanceof Method) {
        Method method = (Method) member;
        argumentValues = TypeUtils.compatibleCast(argumentValues, method.getParameterTypes());
        result = method.invoke(object, argumentValues);
      } else if (member instanceof Field) {
        result = ((Field) member).get(object);
      }

    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new PebbleException(e, "Could not call " + member.getName(), lineNumber, filename);
    }
    return result;
  }
}