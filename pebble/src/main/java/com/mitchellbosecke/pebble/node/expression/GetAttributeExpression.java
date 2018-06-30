/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.attributes.AttributeResolver;
import com.mitchellbosecke.pebble.attributes.ResolvedAttribute;
import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.RootAttributeNotFoundException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.PositionalArgumentNode;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import java.util.List;

/**
 * Used to get an attribute from an object. It will look up attributes in the following order: map
 * entry, array item, list item, get method, is method, has method, public method, public field.
 *
 * @author Mitchell
 */
public class GetAttributeExpression implements Expression<Object> {

  private final Expression<?> node;

  private final Expression<?> attributeNameExpression;

  private final ArgumentsNode args;

  private final String filename;

  private final int lineNumber;

  public GetAttributeExpression(Expression<?> node, Expression<?> attributeNameExpression,
      String filename,
      int lineNumber) {
    this(node, attributeNameExpression, null, filename, lineNumber);
  }

  public GetAttributeExpression(Expression<?> node, Expression<?> attributeNameExpression,
      ArgumentsNode args,
      String filename, int lineNumber) {

    this.node = node;
    this.attributeNameExpression = attributeNameExpression;
    this.args = args;
    this.filename = filename;
    this.lineNumber = lineNumber;
  }

  @Override
  public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context)
      throws PebbleException {
    final Object object = this.node.evaluate(self, context);
    final Object attributeNameValue = this.attributeNameExpression.evaluate(self, context);
    final String attributeName = String.valueOf(attributeNameValue);
    final Object[] argumentValues = this.getArgumentValues(self, context);

    if (object == null && context.isStrictVariables()) {
      if (this.node instanceof ContextVariableExpression) {
        final String rootPropertyName = ((ContextVariableExpression) this.node).getName();
        throw new RootAttributeNotFoundException(null, String.format(
            "Root attribute [%s] does not exist or can not be accessed and strict variables is set to true.",
            rootPropertyName), rootPropertyName, this.lineNumber, this.filename);
      } else {
        throw new RootAttributeNotFoundException(null,
            "Attempt to get attribute of null object and strict variables is set to true.",
            attributeName, this.lineNumber, this.filename);
      }
    }

    for (AttributeResolver attributeResolver : context.getExtensionRegistry()
        .getAttributeResolver()) {
      ResolvedAttribute resolvedAttribute = attributeResolver
          .resolve(object, attributeNameValue, argumentValues, this.args, context, this.filename,
              this.lineNumber);
      if (resolvedAttribute != null) {
        return resolvedAttribute.evaluatedValue;
      }
    }

    if (context.isStrictVariables()) {
      throw new AttributeNotFoundException(null, String.format(
          "Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true.",
          attributeName,
          object != null ? object.getClass().getName() : null),
          attributeName,
          this.lineNumber,
          this.filename);
    }
    return null;
  }

  /**
   * Fully evaluates the individual arguments.
   */
  private Object[] getArgumentValues(PebbleTemplateImpl self, EvaluationContextImpl context) {

    Object[] argumentValues;

    if (this.args == null) {
      argumentValues = null;
    } else {
      List<PositionalArgumentNode> args = this.args.getPositionalArgs();

      argumentValues = new Object[args.size()];

      int index = 0;
      for (PositionalArgumentNode arg : args) {
        Object argumentValue = arg.getValueExpression().evaluate(self, context);
        argumentValues[index] = argumentValue;
        index++;
      }
    }
    return argumentValues;
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public Expression<?> getNode() {
    return this.node;
  }

  public Expression<?> getAttributeNameExpression() {
    return this.attributeNameExpression;
  }

  public ArgumentsNode getArgumentsNode() {
    return this.args;
  }

  @Override
  public int getLineNumber() {
    return this.lineNumber;
  }

}
