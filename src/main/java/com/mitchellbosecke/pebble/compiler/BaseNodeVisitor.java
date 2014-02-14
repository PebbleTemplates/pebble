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

public class BaseNodeVisitor implements NodeVisitor {

	/**
	 * Default method used for unknown nodes such as nodes from a user provided
	 * extension.
	 */
	@Override
	public void visit(Node node) {
	}

	@Override
	public void visit(NodeBody node) {
	}

	@Override
	public void visit(NodeIf node) {
	}

	@Override
	public void visit(NodeFor node) {
	}

	@Override
	public void visit(NodeExpressionBinary node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeExpressionUnary node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeExpressionBlockReferenceAndFunction node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeExpressionConstant node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeExpressionContextVariable node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeExpressionFilterInvocation node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeExpressionFunctionOrMacroInvocation node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeExpressionGetAttribute node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeExpressionNamedArgument node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeExpressionNamedArguments node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeExpressionNewVariableName node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeExpressionParentFunction node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeExpressionString node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeExpressionTernary node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeExpressionTestInvocation node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeBlock node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeExpression node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeFlush node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeImport node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeInclude node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeMacro node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeParallel node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodePrint node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeRoot node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeSet node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NodeText node) {
		// TODO Auto-generated method stub

	}

}
