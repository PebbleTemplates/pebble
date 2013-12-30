/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.compiler.Compiler;

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
}
