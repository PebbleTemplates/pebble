/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import java.io.IOException;
import java.io.Writer;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class IncludeNode extends AbstractRenderableNode {

    private final Expression<?> includeExpression;

    public IncludeNode(int lineNumber, Expression<?> includeExpression) {
        super(lineNumber);
        this.includeExpression = includeExpression;
    }

    @Override
    public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException,
            IOException {
        String templateName = (String) includeExpression.evaluate(self, context);
        if (templateName == null) {
            throw new PebbleException(
                    null,
                    String.format(
                            "The template name in an include tag evaluated to NULL. If the template name is static, make sure to wrap it in quotes.",
                            templateName), getLineNumber(), self.getName());
        }
        self.includeTemplate(writer, context, templateName);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public Expression<?> getIncludeExpression() {
        return includeExpression;
    }

}
