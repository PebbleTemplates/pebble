package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.compiler.Compiler;

public interface Node {
	
	public void compile(Compiler compiler);

}
