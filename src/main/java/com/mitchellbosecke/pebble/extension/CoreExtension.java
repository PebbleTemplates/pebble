package com.mitchellbosecke.pebble.extension;

import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryAnd;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryEqual;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryNotEqual;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryOr;
import com.mitchellbosecke.pebble.tokenParser.BlockTokenParser;
import com.mitchellbosecke.pebble.tokenParser.ExtendsTokenParser;
import com.mitchellbosecke.pebble.tokenParser.ForTokenParser;
import com.mitchellbosecke.pebble.tokenParser.IfTokenParser;
import com.mitchellbosecke.pebble.tokenParser.MacroTokenParser;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;
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

}
