/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.utils;

import java.util.Stack;

import com.mitchellbosecke.pebble.node.Node;

/**
 * A class used to write the Abstract Syntax Tree into a human readable form for
 * the purpose of debugging.
 * 
 * @author Mitchell
 * 
 */
public class TreeWriter {

	private StringBuilder builder = new StringBuilder();
	
	private Stack<String> branchStack = new Stack<>();
	
	boolean isLast;

	public TreeWriter write(String string) {
		
		String branches = "";
		for(int i = 0; i < branchStack.size(); ++i){
			branches += branchStack.get(i);
		}
		builder.append(branches + "\n");
		builder.append(branches + "-" + string + "\n");
		
		if(isLast){
			branchStack.pop();
			branchStack.add("    ");
		}
		return this;
	}
	
	public TreeWriter subtree(Node node){
		return subtree(node, false);
	}
	
	/**
	 * Setting the "isLast" boolean to true will stop the current
	 * branch as soon as the subtree's node calls the "write" method
	 * 
	 * @param node
	 * @param isLast
	 * @return
	 */
	public TreeWriter subtree(Node node, boolean isLast){
		
		this.isLast = isLast;
		
		branchStack.add("   |");
		
		node.tree(this);
		
		branchStack.pop();
		return this;
	}
	
	public String toString(){
		return builder.toString();
	}

}
