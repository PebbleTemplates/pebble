package com.mitchellbosecke.pebble.attributes;

import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.MacroAttributeProvider;

public class MacroResolver implements AttributeResolver {
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
    return () -> macroAttributeProvider.macro(context, attributeName, args, false, lineNumber);
  }
}
