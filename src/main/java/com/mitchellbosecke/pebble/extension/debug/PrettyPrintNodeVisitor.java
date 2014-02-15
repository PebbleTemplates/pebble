package com.mitchellbosecke.pebble.extension.debug;

import com.mitchellbosecke.pebble.compiler.BaseNodeVisitor;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.NodeBlock;
import com.mitchellbosecke.pebble.node.NodeBody;
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

public class PrettyPrintNodeVisitor extends BaseNodeVisitor {

	private StringBuilder output = new StringBuilder();

	private int level = 0;

	private void write(String message) {
		for (int i = 0; i < level - 1; i++) {
			output.append("| ");
		}
		if (level > 0) {
			output.append("|-");
		}
		output.append(message.toUpperCase()).append("\n");
	}

	public String toString() {
		return output.toString();
	}

	/**
	 * Default method used for unknown nodes such as nodes from a user provided
	 * extension.
	 */
	@Override
	public void visit(Node node) {
		write("unknown");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeBody node) {
		write("body");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeIf node) {
		write("if");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeFor node) {
		write("for");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeExpressionBinary node) {
		write("binary");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeExpressionUnary node) {
		write("unary");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeExpressionBlockReferenceAndFunction node) {
		if (node.isExpression()) {
			write("block function");
		} else {
			write("block reference");
		}
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeExpressionConstant node) {
		write(String.format("constant [%s]", node.getValue() == null ? null : node.getValue().toString()));
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeExpressionContextVariable node) {
		write(String.format("context variable [%s]", node.getName()));
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeExpressionFilterInvocation node) {
		write("filter");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeExpressionFunctionOrMacroInvocation node) {
		write("function or macro");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeExpressionGetAttribute node) {
		write("get attribute");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeExpressionNamedArgument node) {
		write("named argument");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeExpressionNamedArguments node) {
		write("named arguments");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeExpressionNewVariableName node) {
		write(String.format("new variable name [%s]", node.getName()));
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeExpressionParentFunction node) {
		write("parent function");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeExpressionString node) {
		write(String.format("string [%s]", node.getValue()));
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeExpressionTernary node) {
		write("ternary");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeExpressionTestInvocation node) {
		write("test");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeBlock node) {
		write(String.format("block [%s]", node.getName()));
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeFlush node) {
		write("flush");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeImport node) {
		write("import");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeInclude node) {
		write("include");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeMacro node) {
		write(String.format("macro [%s]", node.getName()));
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeParallel node) {
		write("parallel");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodePrint node) {
		write("print");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeRoot node) {
		write("root");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeSet node) {
		write("set");
		level++;
		super.visit(node);
		level--;
	}

	@Override
	public void visit(NodeText node) {
		String preview = node.getData().length() > 10 ? node.getData().substring(0, 10) + "..." : node.getData();
		write(String.format("text [%s]", preview));
		level++;
		super.visit(node);
		level--;
	}
}
