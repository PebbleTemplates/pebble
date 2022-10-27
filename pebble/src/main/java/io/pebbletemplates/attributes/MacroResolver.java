package io.pebbletemplates.attributes;

import io.pebbletemplates.node.ArgumentsNode;
import io.pebbletemplates.template.EvaluationContextImpl;
import io.pebbletemplates.template.MacroAttributeProvider;

class MacroResolver implements AttributeResolver {

  static final MacroResolver INSTANCE = new MacroResolver();

  private MacroResolver() {
  }

  @Override
  public ResolvedAttribute resolve(Object instance,
      Object attributeNameValue,
      Object[] argumentValues,
      ArgumentsNode args,
      EvaluationContextImpl context,
      String filename,
      int lineNumber) {
    MacroAttributeProvider macroAttributeProvider = (MacroAttributeProvider) instance;
    String attributeName = String.valueOf(attributeNameValue);
    return new ResolvedAttribute(
        macroAttributeProvider.macro(context, attributeName, args, false, lineNumber));
  }
}
