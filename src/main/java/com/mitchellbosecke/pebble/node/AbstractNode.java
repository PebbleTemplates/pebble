/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.utils.TreeWriter;

public abstract class AbstractNode implements Node {

	private int lineNumber;

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


	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	public String toString(){
		TreeWriter tree = new TreeWriter();
		return tree.subtree(this, true).toString();
	}
}
