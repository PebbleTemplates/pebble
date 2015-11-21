/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.RootAttributeNotFoundException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class ContextVariableExpression implements Expression<Object> {

    protected final String name;

    private final int lineNumber;

    private final String filename;

    public ContextVariableExpression(String name, int lineNumber, String filename) {
        this.name = name;
        this.lineNumber = lineNumber;
        this.filename = filename;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    @Override
    public Object evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
        Object result = context.get(name);
        if (context.isStrictVariables() && result == null && !context.containsKey(name)) {
            throw new RootAttributeNotFoundException(null, String.format(
                    "Root attribute [%s] does not exist or can not be accessed and strict variables is set to true.",
                    this.name), this.name, this.lineNumber, this.filename);
        }
        return result;
    }

    /**
     * Returns the line number on which the expression is defined on.
     *
     * @return the line number on which the expression is defined on.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Returns the filename in which the expression is defined in.
     *
     * @return the filename in which the expression is defined in.
     */
    public String getFilename() {
        return filename;
    }

}
