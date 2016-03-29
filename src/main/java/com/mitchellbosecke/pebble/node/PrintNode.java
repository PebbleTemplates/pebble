/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.utils.StringUtils;

import java.io.IOException;
import java.io.Writer;

public class PrintNode extends AbstractRenderableNode {

    private Expression<?> expression;

    public PrintNode(Expression<?> expression, int lineNumber) {
        super(lineNumber);
        this.expression = expression;
    }

    @Override
    public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws IOException,
            PebbleException {
        Object var = expression.evaluate(self, context);
        if (var != null) {
            writer.write(StringUtils.toString(var));
        }
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public Expression<?> getExpression() {
        return expression;
    }

    public void setExpression(Expression<?> expression) {
        this.expression = expression;
    }

}
