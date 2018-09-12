package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.MacroAttributeProvider;
import com.mitchellbosecke.pebble.utils.TypeUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class DefaultAttributeResolver implements AttributeResolver {

  private final MemberCacheUtils memberCacheUtils = new MemberCacheUtils();

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
        return new ResolvedAttribute(this.invokeMember(instance, member, argumentValues));
      }
    }
    return null;
  }

  private Class<?>[] getArgumentTypes(Object[] argumentValues) {
    if (argumentValues != null) {
      Class<?>[] argumentTypes = new Class<?>[argumentValues.length];

      for (int i = 0; i < argumentValues.length; i++) {
        Object o = argumentValues[i];
        if (o == null) {
          argumentTypes[i] = null;
        } else {
          argumentTypes[i] = o.getClass();
        }
      }
      return argumentTypes;
    }

    return new Class<?>[0];
  }

  /**
   * Invoke the "Member" that was found via reflection.
   */
  private Object invokeMember(Object object, Member member, Object[] argumentValues) {
    Object result = null;
    try {
      if (member instanceof Method) {
        argumentValues = TypeUtils
            .compatibleCast(argumentValues, ((Method) member).getParameterTypes());
        result = ((Method) member).invoke(object, argumentValues);
      } else if (member instanceof Field) {
        result = ((Field) member).get(object);
      }

    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
    return result;
  }
}
