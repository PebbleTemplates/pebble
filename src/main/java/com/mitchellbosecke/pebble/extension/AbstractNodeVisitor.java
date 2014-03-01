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
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.FilterInvocationExpression;
import com.mitchellbosecke.pebble.node.expression.FunctionOrMacroInvocationExpression;
import com.mitchellbosecke.pebble.node.expression.GetAttributeExpression;
import com.mitchellbosecke.pebble.node.expression.LiteralStringExpression;
import com.mitchellbosecke.pebble.node.expression.ParentFunctionExpression;
import com.mitchellbosecke.pebble.node.expression.UnaryExpression;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.utils.Pair;

/**
 * A base node visitor that can be extended for the sake of using it's
 * navigational abilities.
 * 
 * @author Mitchell
 * 
 */
public abstract class AbstractNodeVisitor implements NodeVisitor {

	protected PebbleTemplateImpl template;

	public void setTemplate(PebbleTemplateImpl template) {
		this.template = template;
	}

	/**
	 * Default method used for unknown nodes such as nodes from a user provided
	 * extension.
	 */
	@Override
	public void visit(Node node) {
	}

	@Override
	public void visit(BodyNode node) {
		for (Node child : node.getChildren()) {
			child.accept(this);
		}
	}

	@Override
	public void visit(IfNode node) {
		for (Pair<Expression<?>, BodyNode> pairs : node.getConditionsWithBodies()) {
			pairs.getLeft().accept(this);
			pairs.getRight().accept(this);
		}
		if (node.getElseBody() != null) {
			node.getElseBody().accept(this);
		}
	}

	@Override
	public void visit(ForNode node) {
		node.getIterable().accept(this);
		node.getBody().accept(this);
		if (node.getElseBody() != null) {
			node.getElseBody().accept(this);
		}
	}

	@Override
	public void visit(BinaryExpression<?> node) {
		node.getLeftExpression().accept(this);
		node.getRightExpression().accept(this);
	}

	@Override
	public void visit(UnaryExpression node) {
		node.getChildExpression().accept(this);
	}

	@Override
	public void visit(ContextVariableExpression node) {

	}

	@Override
	public void visit(FilterInvocationExpression node) {
		node.getArgs().accept(this);
	}

	@Override
	public void visit(FunctionOrMacroInvocationExpression node) {
		node.getArguments().accept(this);
	}

	@Override
	public void visit(GetAttributeExpression node) {
		node.getNode().accept(this);
	}

	@Override
	public void visit(NamedArgumentNode node) {
		if (node.getValueExpression() != null) {
			node.getValueExpression().accept(this);
		}
	}

	@Override
	public void visit(ArgumentsNode node) {
		if (node.getNamedArgs() != null) {
			for (Node arg : node.getNamedArgs()) {
				arg.accept(this);
			}
		}
		if (node.getPositionalArgs() != null) {
			for (Node arg : node.getPositionalArgs()) {
				arg.accept(this);
			}
		}
	}

	@Override
	public void visit(ParentFunctionExpression node) {

	}

	@Override
	public void visit(LiteralStringExpression node) {

	}

	@Override
	public void visit(TernaryExpression node) {
		node.getExpression1().accept(this);
		node.getExpression2().accept(this);
		node.getExpression3().accept(this);
	}

	@Override
	public void visit(TestInvocationExpression node) {
		node.getArgs().accept(this);
	}

	@Override
	public void visit(BlockNode node) {
		node.getBody().accept(this);
	}

	@Override
	public void visit(MacroNode node) {
		node.getBody().accept(this);
		node.getArgs().accept(this);
	}

	@Override
	public void visit(AutoEscapeNode node) {
		node.getBody().accept(this);
	}

	@Override
	public void visit(FlushNode node) {

	}

	@Override
	public void visit(ImportNode node) {
		node.getImportExpression().accept(this);
	}

	@Override
	public void visit(IncludeNode node) {
		node.getIncludeExpression().accept(this);
	}

	@Override
	public void visit(ParallelNode node) {
		node.getBody().accept(this);
	}

	@Override
	public void visit(PrintNode node) {
		node.getExpression().accept(this);
	}

	@Override
	public void visit(RootNode node) {
		node.getBody().accept(this);
	}

	@Override
	public void visit(SetNode node) {
		node.getValue().accept(this);
	}

	@Override
	public void visit(TextNode node) {

	}

}
