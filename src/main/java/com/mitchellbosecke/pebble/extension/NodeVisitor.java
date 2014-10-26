/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
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
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

/**
 * Will visit all the nodes of the AST provided by the parser. The NodeVisitor
 * is responsible for the navigating the tree, it can extend AbstractNodeVisitor
 * for help with this.
 * 
 * A NodeVisitor can still use method overloading to visit expressions (it's
 * just not required).
 * 
 * @author Mitchell
 * 
 */
public interface NodeVisitor {

    public abstract void setTemplate(PebbleTemplateImpl template);

    /**
     * Default method invoked with unknown nodes such as nodes provided by user
     * extensions.
     * 
     * @param node
     */
    public abstract void visit(Node node);

    /*
     * OVERLOADED NODES (keep alphabetized)
     */
    public abstract void visit(ArgumentsNode node);

    public abstract void visit(AutoEscapeNode node);

    public abstract void visit(BlockNode node);

    public abstract void visit(BodyNode node);

    public abstract void visit(ExtendsNode node);

    public abstract void visit(FlushNode node);

    public abstract void visit(ForNode node);

    public abstract void visit(IfNode node);

    public abstract void visit(ImportNode node);

    public abstract void visit(IncludeNode node);

    public abstract void visit(MacroNode node);

    public abstract void visit(NamedArgumentNode node);

    public abstract void visit(ParallelNode node);

    public abstract void visit(PositionalArgumentNode node);

    public abstract void visit(PrintNode node);

    public abstract void visit(RootNode node);

    public abstract void visit(SetNode node);

    public abstract void visit(TextNode node);

}
