/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.InheritanceChain;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class ParentFunctionExpression implements Expression<String> {

    private final String blockName;

    private final int lineNumber;

    public ParentFunctionExpression(String blockName, int lineNumber) {
        this.blockName = blockName;
        this.lineNumber = lineNumber;
    }

    @Override
    public String evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
        Writer writer = new StringWriter();
        try {
            InheritanceChain inheritanceChain = context.getInheritanceChain();
            if (inheritanceChain.getParent() == null) {
                throw new PebbleException(null,
                        "Can not use parent function if template does not extend another template.", lineNumber,
                        self.getName());
            }
            PebbleTemplateImpl parent = inheritanceChain.getParent();

            inheritanceChain.ascend();
            parent.block(writer, context, blockName, true);
            inheritanceChain.descend();
        } catch (IOException e) {
            throw new PebbleException(e, "Could not render block [" + blockName + "]", this.getLineNumber(),
                    self.getName());
        }
        return writer.toString();
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int getLineNumber() {
        return this.lineNumber;
    }

}
