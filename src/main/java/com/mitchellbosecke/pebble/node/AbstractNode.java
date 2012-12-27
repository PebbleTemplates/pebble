package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.compiler.Compiler;

public abstract class AbstractNode implements Node {

	private int lineNumber;
	private String tag;

	@Override
	public abstract void compile(Compiler compiler);
	
	public AbstractNode(){
	}

	public AbstractNode(int lineNumber) {
		this.setLineNumber(lineNumber);
	}
	

	public int getLineNumber() {
		return lineNumber;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
}
