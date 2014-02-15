package com.mitchellbosecke.pebble.compiler;

import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.NodeBlock;
import com.mitchellbosecke.pebble.node.NodeBody;
import com.mitchellbosecke.pebble.node.NodeExpression;
import com.mitchellbosecke.pebble.node.NodeFlush;
import com.mitchellbosecke.pebble.node.NodeFor;
import com.mitchellbosecke.pebble.node.NodeIf;
import com.mitchellbosecke.pebble.node.NodeImport;
import com.mitchellbosecke.pebble.node.NodeInclude;
import com.mitchellbosecke.pebble.node.NodeMacro;
import com.mitchellbosecke.pebble.node.NodeParallel;
import com.mitchellbosecke.pebble.node.NodePrint;
import com.mitchellbosecke.pebble.node.NodeRoot;
import com.mitchellbosecke.pebble.node.NodeSet;
import com.mitchellbosecke.pebble.node.NodeText;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBinary;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBlockReferenceAndFunction;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionConstant;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionContextVariable;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionFilterInvocation;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionFunctionOrMacroInvocation;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionGetAttribute;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionNamedArgument;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionNamedArguments;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionNewVariableName;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionParentFunction;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionString;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionTernary;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionTestInvocation;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionUnary;
import com.mitchellbosecke.pebble.utils.Pair;

/**
 * A base node visitor that can be extended for the sake of using it's
 * navigational abilities.
 * 
 * @author Mitchell
 * 
 */
public abstract class BaseNodeVisitor implements NodeVisitor {

	/**
	 * Default method used for unknown nodes such as nodes from a user provided
	 * extension.
	 */
	@Override
	public void visit(Node node) {
	}

	@Override
	public void visit(NodeBody node) {
		for (Node child : node.getChildren()) {
			child.accept(this);
		}
	}

	@Override
	public void visit(NodeIf node) {
		for (Pair<NodeExpression, NodeBody> pairs : node.getConditionsWithBodies()) {
			pairs.getLeft().accept(this);
			pairs.getRight().accept(this);
		}
		if (node.getElseBody() != null) {
			node.getElseBody().accept(this);
		}
	}

	@Override
	public void visit(NodeFor node) {
		node.getIterationVariable().accept(this);
		node.getIterable().accept(this);
		node.getBody().accept(this);
		if (node.getElseBody() != null) {
			node.getElseBody().accept(this);
		}
	}

	@Override
	public void visit(NodeExpressionBinary node) {
		node.getLeftExpression().accept(this);
		node.getRightExpression().accept(this);
	}

	@Override
	public void visit(NodeExpressionUnary node) {
		node.getChildExpression().accept(this);
	}

	@Override
	public void visit(NodeExpressionBlockReferenceAndFunction node) {
		if (node.getArgs() != null) {
			node.getArgs().accept(this);
		}
	}

	@Override
	public void visit(NodeExpressionConstant node) {

	}

	@Override
	public void visit(NodeExpressionContextVariable node) {

	}

	@Override
	public void visit(NodeExpressionFilterInvocation node) {
		node.getFilterName().accept(this);
		node.getArgs().accept(this);
	}

	@Override
	public void visit(NodeExpressionFunctionOrMacroInvocation node) {
		node.getFunctionName().accept(this);
		node.getArguments().accept(this);
	}

	@Override
	public void visit(NodeExpressionGetAttribute node) {
		node.getNode().accept(this);
		node.getAttributeOrMethod().accept(this);
	}

	@Override
	public void visit(NodeExpressionNamedArgument node) {
		if (node.getName() != null) {
			node.getName().accept(this);
		}
		if (node.getValue() != null) {
			node.getValue().accept(this);
		}
	}

	@Override
	public void visit(NodeExpressionNamedArguments node) {
		if (node.getArgs() != null) {
			for (Node arg : node.getArgs()) {
				arg.accept(this);
			}
		}
	}

	@Override
	public void visit(NodeExpressionNewVariableName node) {

	}

	@Override
	public void visit(NodeExpressionParentFunction node) {

	}

	@Override
	public void visit(NodeExpressionString node) {

	}

	@Override
	public void visit(NodeExpressionTernary node) {
		node.getExpression1().accept(this);
		node.getExpression2().accept(this);
		node.getExpression3().accept(this);
	}

	@Override
	public void visit(NodeExpressionTestInvocation node) {
		node.getTestName().accept(this);
		node.getArgs().accept(this);
	}

	@Override
	public void visit(NodeBlock node) {
		node.getBody().accept(this);
	}

	@Override
	public void visit(NodeFlush node) {

	}

	@Override
	public void visit(NodeImport node) {
		node.getImportExpression().accept(this);
	}

	@Override
	public void visit(NodeInclude node) {
		node.getIncludeExpression().accept(this);
	}

	@Override
	public void visit(NodeMacro node) {
		node.getArgs().accept(this);
	}

	@Override
	public void visit(NodeParallel node) {
		node.getBody().accept(this);
	}

	@Override
	public void visit(NodePrint node) {
		node.getExpression().accept(this);
	}

	@Override
	public void visit(NodeRoot node) {
		for (Node block : node.getBlocks().values()) {
			block.accept(this);
		}
		for (Node macro : node.getMacros().values()) {
			macro.accept(this);
		}
		node.getBody().accept(this);
		if (node.getParentTemplateExpression() != null) {
			node.getParentTemplateExpression().accept(this);
		}
	}

	@Override
	public void visit(NodeSet node) {
		node.getName().accept(this);
		node.getValue().accept(this);
	}

	@Override
	public void visit(NodeText node) {

	}

}
