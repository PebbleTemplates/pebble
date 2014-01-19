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

public class NodeImport extends AbstractNode {

	private final NodeExpression importExpression;

	public NodeImport(int lineNumber, NodeExpression importExpression) {
		super(lineNumber);
		this.importExpression = importExpression;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("addImportedTemplate(this.engine.compile(").subcompile(importExpression).raw("));").newline();
	}

}
