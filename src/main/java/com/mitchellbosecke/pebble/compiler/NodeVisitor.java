package com.mitchellbosecke.pebble.compiler;

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

public interface NodeVisitor {

	/**
	 * Default method invoked with unknown nodes such as nodes provided by user
	 * extensions.
	 * 
	 * @param node
	 */
	public abstract void visit(Node node);

	public abstract void visit(NodeBody node);

	public abstract void visit(NodeIf node);

	public abstract void visit(NodeFor node);
	
	public abstract void visit(NodeExpressionBinary node);
	
	public abstract void visit(NodeExpressionUnary node);
	
	public abstract void visit(NodeExpressionBlockReferenceAndFunction node);
	
	public abstract void visit(NodeExpressionConstant node);
	
	public abstract void visit(NodeExpressionContextVariable node);
	
	public abstract void visit(NodeExpressionFilterInvocation node);
	
	public abstract void visit(NodeExpressionFunctionOrMacroInvocation node);
	
	public abstract void visit(NodeExpressionGetAttribute node);
	
	public abstract void visit(NodeExpressionNamedArgument node);
	
	public abstract void visit(NodeExpressionNamedArguments node);
	
	public abstract void visit(NodeExpressionNewVariableName node);
	
	public abstract void visit(NodeExpressionParentFunction node);
	
	public abstract void visit(NodeExpressionString node);
	
	public abstract void visit(NodeExpressionTernary node);
	
	public abstract void visit(NodeExpressionTestInvocation node);
	
	public abstract void visit(NodeBlock node);
	
	public abstract void visit(NodeFlush node);
	
	public abstract void visit(NodeImport node);
	
	public abstract void visit(NodeInclude node);
	
	public abstract void visit(NodeMacro node);
	
	public abstract void visit(NodeParallel node);
	
	public abstract void visit(NodePrint node);
	
	public abstract void visit(NodeRoot node);
	
	public abstract void visit(NodeSet node);
	
	public abstract void visit(NodeText node);

}