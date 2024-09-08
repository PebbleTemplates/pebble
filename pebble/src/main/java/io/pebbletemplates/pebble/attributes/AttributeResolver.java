package io.pebbletemplates.pebble.attributes;

import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;

/**
 * Resolves instance attributes
 */
public interface AttributeResolver {

  /**
   * Attempts to resolve an attribute of the given instance. If this method returns {@code null},
   * Pebble asks the next attribute resolver in the list.
   *
   * @param instance The object which is being accessed
   * @param attributeNameValue The name of the attribute to resolve
   * @param argumentValues fully evaluated positional arguments
   * @param args The arguments
   * @param context The evaluation context
   * @param filename Filename of the template
   * @param lineNumber the line number on which the expression is defined on.
   * @return a {@link ResolvedAttribute} wrapping the attribute value, or {@code null} if the
   * attribute could not be resolved
   */
  ResolvedAttribute resolve(Object instance,
      Object attributeNameValue,
      Object[] argumentValues,
      ArgumentsNode args,
      EvaluationContextImpl context,
      String filename,
      int lineNumber);
}
