/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.extension.debug;

import io.pebbletemplates.pebble.extension.AbstractNodeVisitor;
import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.node.BlockNode;
import io.pebbletemplates.pebble.node.BodyNode;
import io.pebbletemplates.pebble.node.FlushNode;
import io.pebbletemplates.pebble.node.ForNode;
import io.pebbletemplates.pebble.node.IfNode;
import io.pebbletemplates.pebble.node.ImportNode;
import io.pebbletemplates.pebble.node.IncludeNode;
import io.pebbletemplates.pebble.node.NamedArgumentNode;
import io.pebbletemplates.pebble.node.Node;
import io.pebbletemplates.pebble.node.ParallelNode;
import io.pebbletemplates.pebble.node.PrintNode;
import io.pebbletemplates.pebble.node.RootNode;
import io.pebbletemplates.pebble.node.SetNode;
import io.pebbletemplates.pebble.node.TestInvocationExpression;
import io.pebbletemplates.pebble.node.TextNode;
import io.pebbletemplates.pebble.node.expression.BinaryExpression;
import io.pebbletemplates.pebble.node.expression.ContextVariableExpression;
import io.pebbletemplates.pebble.node.expression.FilterInvocationExpression;
import io.pebbletemplates.pebble.node.expression.FunctionOrMacroInvocationExpression;
import io.pebbletemplates.pebble.node.expression.GetAttributeExpression;
import io.pebbletemplates.pebble.node.expression.ParentFunctionExpression;
import io.pebbletemplates.pebble.node.expression.TernaryExpression;
import io.pebbletemplates.pebble.node.expression.UnaryExpression;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

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
