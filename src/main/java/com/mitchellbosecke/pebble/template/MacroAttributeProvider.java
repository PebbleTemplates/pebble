package com.mitchellbosecke.pebble.template;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.node.ArgumentsNode;


public class MacroAttributeProvider {
    private final PebbleTemplateImpl template;

    public MacroAttributeProvider(PebbleTemplateImpl template) {
        this.template = template;
    }

    /**
     * Invokes a macro
     *
     * @param context         The evaluation context
     * @param macroName       The name of the macro
     * @param args            The arguments
     * @param ignoreOverriden Whether or not to ignore macro definitions in child template
     * @return The results of the macro invocation
     * @throws PebbleException An exception that may have occurred
     */
    public Object macro(EvaluationContext context, String macroName, ArgumentsNode args, boolean ignoreOverriden, int lineNumber)
                    throws PebbleException {
        return template.macro(context, macroName, args, ignoreOverriden, lineNumber);
    }

}
