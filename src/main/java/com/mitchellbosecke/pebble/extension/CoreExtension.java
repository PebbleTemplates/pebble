/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension;

import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.filter.Filter;
import com.mitchellbosecke.pebble.filter.FilterFunction;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryAnd;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryEqual;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryNotEqual;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryOr;
import com.mitchellbosecke.pebble.tokenParser.BlockTokenParser;
import com.mitchellbosecke.pebble.tokenParser.ExtendsTokenParser;
import com.mitchellbosecke.pebble.tokenParser.ForTokenParser;
import com.mitchellbosecke.pebble.tokenParser.IfTokenParser;
import com.mitchellbosecke.pebble.tokenParser.ImportTokenParser;
import com.mitchellbosecke.pebble.tokenParser.IncludeTokenParser;
import com.mitchellbosecke.pebble.tokenParser.MacroTokenParser;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;
import com.mitchellbosecke.pebble.utils.Command;
import com.mitchellbosecke.pebble.utils.Operator;

public class CoreExtension implements Extension {

	@Override
	public void initRuntime(PebbleEngine engine) {
	}

	@Override
	public List<TokenParser> getTokenParsers() {
		ArrayList<TokenParser> parsers = new ArrayList<>();
		parsers.add(new BlockTokenParser());
		parsers.add(new ExtendsTokenParser());
		parsers.add(new IfTokenParser());
		parsers.add(new ForTokenParser());
		parsers.add(new MacroTokenParser());
		parsers.add(new ImportTokenParser());
		parsers.add(new IncludeTokenParser());
		return parsers;
	}
	
	@Override
	public List<Operator> getBinaryOperators(){
		ArrayList<Operator> operators = new ArrayList<>();
		operators.add(new Operator("and", 15, new NodeExpressionBinaryAnd(), Operator.Associativity.LEFT));
		operators.add(new Operator("or", 10, new NodeExpressionBinaryOr(), Operator.Associativity.LEFT));
		operators.add(new Operator("==", 20, new NodeExpressionBinaryEqual(), Operator.Associativity.LEFT));
		operators.add(new Operator("!=", 20, new NodeExpressionBinaryNotEqual(), Operator.Associativity.LEFT));
		return operators;
	}
	
	@Override
	public List<Filter> getFilters(){
		ArrayList<Filter> filters = new ArrayList<>();
		filters.add(new FilterFunction("lower", lowerFilter));
		filters.add(new FilterFunction("upper", upperFilter));
		return filters;
	}
	
	private Command<Object, List<Object>> lowerFilter = new Command<Object, List<Object>>(){
		@Override
		public Object execute(List<Object> data) {
			// first argument should be a string
			String arg = (String)data.get(0);
			return arg.toLowerCase();
		}
	};
	
	private Command<Object, List<Object>> upperFilter = new Command<Object, List<Object>>(){
		@Override
		public Object execute(List<Object> data) {
			// first argument should be a string
			String arg = (String)data.get(0);
			return arg.toUpperCase();
		}
	};

}
