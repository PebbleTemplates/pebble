/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.extension;

import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.AutoEscapeNode;
import com.mitchellbosecke.pebble.node.BlockNode;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.ExtendsNode;
import com.mitchellbosecke.pebble.node.FlushNode;
import com.mitchellbosecke.pebble.node.ForNode;
import com.mitchellbosecke.pebble.node.IfNode;
import com.mitchellbosecke.pebble.node.ImportNode;
import com.mitchellbosecke.pebble.node.IncludeNode;
import com.mitchellbosecke.pebble.node.MacroNode;
import com.mitchellbosecke.pebble.node.NamedArgumentNode;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.ParallelNode;
import com.mitchellbosecke.pebble.node.PositionalArgumentNode;
import com.mitchellbosecke.pebble.node.PrintNode;
import com.mitchellbosecke.pebble.node.RootNode;
import com.mitchellbosecke.pebble.node.SetNode;
import com.mitchellbosecke.pebble.node.TextNode;

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
