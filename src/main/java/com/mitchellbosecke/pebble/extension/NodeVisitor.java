package com.mitchellbosecke.pebble.extension;

import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.AutoEscapeNode;
import com.mitchellbosecke.pebble.node.BlockNode;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.FlushNode;
import com.mitchellbosecke.pebble.node.ForNode;
import com.mitchellbosecke.pebble.node.IfNode;
import com.mitchellbosecke.pebble.node.ImportNode;
import com.mitchellbosecke.pebble.node.IncludeNode;
import com.mitchellbosecke.pebble.node.MacroNode;
import com.mitchellbosecke.pebble.node.NamedArgumentNode;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.ParallelNode;
import com.mitchellbosecke.pebble.node.PrintNode;
import com.mitchellbosecke.pebble.node.RootNode;
import com.mitchellbosecke.pebble.node.SetNode;
import com.mitchellbosecke.pebble.node.TernaryExpression;
import com.mitchellbosecke.pebble.node.TestInvocationExpression;
import com.mitchellbosecke.pebble.node.TextNode;
import com.mitchellbosecke.pebble.node.expression.BinaryExpression;
import com.mitchellbosecke.pebble.node.expression.ContextVariableExpression;
import com.mitchellbosecke.pebble.node.expression.FilterInvocationExpression;
import com.mitchellbosecke.pebble.node.expression.FunctionOrMacroInvocationExpression;
import com.mitchellbosecke.pebble.node.expression.GetAttributeExpression;
import com.mitchellbosecke.pebble.node.expression.LiteralStringExpression;
import com.mitchellbosecke.pebble.node.expression.ParentFunctionExpression;
import com.mitchellbosecke.pebble.node.expression.UnaryExpression;

public interface NodeVisitor {

	/**
	 * Default method invoked with unknown nodes such as nodes provided by user
	 * extensions.
	 * 
	 * @param node
	 */
	public abstract void visit(Node node);

	public abstract void visit(BodyNode node);

	public abstract void visit(IfNode node);

	public abstract void visit(ForNode node);

	public abstract void visit(BinaryExpression<?> node);

	public abstract void visit(UnaryExpression node);

	public abstract void visit(ContextVariableExpression node);

	public abstract void visit(FilterInvocationExpression node);

	public abstract void visit(FunctionOrMacroInvocationExpression node);

	public abstract void visit(GetAttributeExpression node);

	public abstract void visit(NamedArgumentNode node);

	public abstract void visit(ArgumentsNode node);

	public abstract void visit(ParentFunctionExpression node);

	public abstract void visit(LiteralStringExpression node);

	public abstract void visit(TernaryExpression node);

	public abstract void visit(TestInvocationExpression node);

	public abstract void visit(AutoEscapeNode node);

	public abstract void visit(BlockNode node);

	public abstract void visit(FlushNode node);

	public abstract void visit(ImportNode node);

	public abstract void visit(IncludeNode node);

	public abstract void visit(MacroNode node);

	public abstract void visit(ParallelNode node);

	public abstract void visit(PrintNode node);

	public abstract void visit(RootNode node);

	public abstract void visit(SetNode node);

	public abstract void visit(TextNode node);

}