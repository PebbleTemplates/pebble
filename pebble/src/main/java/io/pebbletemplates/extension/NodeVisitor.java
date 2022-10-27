/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.extension;

import io.pebbletemplates.node.ArgumentsNode;
import io.pebbletemplates.node.AutoEscapeNode;
import io.pebbletemplates.node.BlockNode;
import io.pebbletemplates.node.BodyNode;
import io.pebbletemplates.node.ExtendsNode;
import io.pebbletemplates.node.FlushNode;
import io.pebbletemplates.node.ForNode;
import io.pebbletemplates.node.IfNode;
import io.pebbletemplates.node.ImportNode;
import io.pebbletemplates.node.IncludeNode;
import io.pebbletemplates.node.MacroNode;
import io.pebbletemplates.node.NamedArgumentNode;
import io.pebbletemplates.node.Node;
import io.pebbletemplates.node.ParallelNode;
import io.pebbletemplates.node.PositionalArgumentNode;
import io.pebbletemplates.node.PrintNode;
import io.pebbletemplates.node.RootNode;
import io.pebbletemplates.node.SetNode;
import io.pebbletemplates.node.TextNode;

/**
 * Will visit all the nodes of the AST provided by the parser. The NodeVisitor is responsible for
 * the navigating the tree, it can extend AbstractNodeVisitor for help with this.
 *
 * A NodeVisitor can still use method overloading to visit expressions (it's just not required).
 *
 * <p>
 * The implementor does not need to make sure that the implementation is thread-safe.
 *
 * @author Mitchell
 */
public interface NodeVisitor {

  /**
   * Default method invoked with unknown nodes such as nodes provided by user extensions.
   *
   * @param node Node to visit
   */
  void visit(Node node);

  /*
   * OVERLOADED NODES (keep alphabetized)
   */
  void visit(ArgumentsNode node);

  void visit(AutoEscapeNode node);

  void visit(BlockNode node);

  void visit(BodyNode node);

  void visit(ExtendsNode node);

  void visit(FlushNode node);

  void visit(ForNode node);

  void visit(IfNode node);

  void visit(ImportNode node);

  void visit(IncludeNode node);

  void visit(MacroNode node);

  void visit(NamedArgumentNode node);

  void visit(ParallelNode node);

  void visit(PositionalArgumentNode node);

  void visit(PrintNode node);

  void visit(RootNode node);

  void visit(SetNode node);

  void visit(TextNode node);

}
