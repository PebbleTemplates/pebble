package com.mitchellbosecke.pebble.template;

import com.mitchellbosecke.pebble.node.ArgumentsNode;


public class MacroAttributeProvider {

  private final PebbleTemplateImpl template;

  public MacroAttributeProvider(PebbleTemplateImpl template) {
    this.template = template;
  }

  /**
   * Invokes a macro
   *
   * @param context The evaluation context
   * @param macroName The name of the macro
   * @param args The arguments
   * @param ignoreOverriden Whether or not to ignore macro definitions in child template
   * @return The results of the macro invocation
   */
  public Object macro(EvaluationContextImpl context, String macroName, ArgumentsNode args,
      boolean ignoreOverriden, int lineNumber) {
    return template.macro(context, macroName, args, ignoreOverriden, lineNumber);
  }

}
