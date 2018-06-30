/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.extension.debug;

import com.mitchellbosecke.pebble.extension.AbstractNodeVisitor;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.BlockNode;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.FlushNode;
import com.mitchellbosecke.pebble.node.ForNode;
import com.mitchellbosecke.pebble.node.IfNode;
import com.mitchellbosecke.pebble.node.ImportNode;
import com.mitchellbosecke.pebble.node.IncludeNode;
import com.mitchellbosecke.pebble.node.NamedArgumentNode;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.ParallelNode;
import com.mitchellbosecke.pebble.node.PrintNode;
import com.mitchellbosecke.pebble.node.RootNode;
import com.mitchellbosecke.pebble.node.SetNode;
import com.mitchellbosecke.pebble.node.TestInvocationExpression;
import com.mitchellbosecke.pebble.node.TextNode;
import com.mitchellbosecke.pebble.node.expression.BinaryExpression;
import com.mitchellbosecke.pebble.node.expression.ContextVariableExpression;
import com.mitchellbosecke.pebble.node.expression.FilterInvocationExpression;
import com.mitchellbosecke.pebble.node.expression.FunctionOrMacroInvocationExpression;
import com.mitchellbosecke.pebble.node.expression.GetAttributeExpression;
import com.mitchellbosecke.pebble.node.expression.ParentFunctionExpression;
import com.mitchellbosecke.pebble.node.expression.TernaryExpression;
import com.mitchellbosecke.pebble.node.expression.UnaryExpression;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class PrettyPrintNodeVisitor extends AbstractNodeVisitor {

  public PrettyPrintNodeVisitor(PebbleTemplateImpl template) {
    super(template);
  }

  private StringBuilder output = new StringBuilder();

  private int level = 0;

  private void write(String message) {
    for (int i = 0; i < this.level - 1; i++) {
      this.output.append("| ");
    }
    if (this.level > 0) {
      this.output.append("|-");
    }
    this.output.append(message.toUpperCase()).append("\n");
  }

  public String toString() {
    return this.output.toString();
  }

  /**
   * Default method used for unknown nodes such as nodes from a user provided extension.
   */
  @Override
  public void visit(Node node) {
    this.write("unknown");
    this.level++;
    super.visit(node);
    this.level--;
  }

  @Override
  public void visit(BodyNode node) {
    this.write("body");
    this.level++;
    super.visit(node);
    this.level--;
  }

  @Override
  public void visit(IfNode node) {
    this.write("if");
    this.level++;
    super.visit(node);
    this.level--;
  }

  @Override
  public void visit(ForNode node) {
    this.write("for");
    this.level++;
    super.visit(node);
    this.level--;
  }

  public void visit(BinaryExpression<?> node) {
    this.write("binary");
    this.level++;
    super.visit(node);
    this.level--;
  }

  public void visit(UnaryExpression node) {
    this.write("unary");
    this.level++;
    super.visit(node);
    this.level--;
  }

  public void visit(ContextVariableExpression node) {
    this.write(String.format("context variable [%s]", node.getName()));
    this.level++;
    super.visit(node);
    this.level--;
  }

  public void visit(FilterInvocationExpression node) {
    this.write("filter");
    this.level++;
    super.visit(node);
    this.level--;
  }

  public void visit(FunctionOrMacroInvocationExpression node) {
    this.write("function or macro");
    this.level++;
    super.visit(node);
    this.level--;
  }

  public void visit(GetAttributeExpression node) {
    this.write("get attribute");
    this.level++;
    super.visit(node);
    this.level--;
  }

  @Override
  public void visit(NamedArgumentNode node) {
    this.write("named argument");
    this.level++;
    super.visit(node);
    this.level--;
  }

  @Override
  public void visit(ArgumentsNode node) {
    this.write("named arguments");
    this.level++;
    super.visit(node);
    this.level--;
  }

  public void visit(ParentFunctionExpression node) {
    this.write("parent function");
    this.level++;
    super.visit(node);
    this.level--;
  }

  public void visit(TernaryExpression node) {
    this.write("ternary");
    this.level++;
    super.visit(node);
    this.level--;
  }

  public void visit(TestInvocationExpression node) {
    this.write("test");
    this.level++;
    super.visit(node);
    this.level--;
  }

  @Override
  public void visit(BlockNode node) {
    this.write(String.format("block [%s]", node.getName()));
    this.level++;
    super.visit(node);
    this.level--;
  }

  @Override
  public void visit(FlushNode node) {
    this.write("flush");
    this.level++;
    super.visit(node);
    this.level--;
  }

  @Override
  public void visit(ImportNode node) {
    this.write("import");
    this.level++;
    super.visit(node);
    this.level--;
  }

  @Override
  public void visit(IncludeNode node) {
    this.write("include");
    this.level++;
    super.visit(node);
    this.level--;
  }

  @Override
  public void visit(ParallelNode node) {
    this.write("parallel");
    this.level++;
    super.visit(node);
    this.level--;
  }

  @Override
  public void visit(PrintNode node) {
    this.write("print");
    this.level++;
    super.visit(node);
    this.level--;
  }

  @Override
  public void visit(RootNode node) {
    this.write("root");
    this.level++;
    super.visit(node);
    this.level--;
  }

  @Override
  public void visit(SetNode node) {
    this.write("set");
    this.level++;
    super.visit(node);
    this.level--;
  }

  @Override
  public void visit(TextNode node) {
    String text = new String(node.getData());
    String preview = text.length() > 10 ? text.substring(0, 10) + "..." : text;
    this.write(String.format("text [%s]", preview));
    this.level++;
    super.visit(node);
    this.level--;
  }
}
