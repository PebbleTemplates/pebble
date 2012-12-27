package com.mitchellbosecke.pebble.node;
import com.mitchellbosecke.pebble.compiler.Compiler;

public class NodeText extends AbstractNode implements DisplayableNode{
	
	private final String data;
	
	public NodeText(String data, int lineNumber){
		super(lineNumber);
		this.data = data;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.appendContent(getData());
	}

	public String getData() {
		return data;
	}

}
