package io.pebbletemplates.pebble.attributes;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.MacroAttributeProvider;
import java.util.Objects;

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
    try {
      return new ResolvedAttribute(
          macroAttributeProvider.macro(context, attributeName, args, false, lineNumber));
    } catch (PebbleException exception) {
      if (Integer.valueOf(lineNumber).equals(exception.getLineNumber())
          && Objects.equals(filename, exception.getFileName())) {
        throw exception;
      }
      throw new PebbleException(exception, exception.getPebbleMessage(),
          lineNumber, filename);
    }
  }
}
