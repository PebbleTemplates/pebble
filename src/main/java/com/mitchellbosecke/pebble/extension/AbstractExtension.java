package com.mitchellbosecke.pebble.extension;

import java.util.List;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;
import com.mitchellbosecke.pebble.utils.Operator;

public abstract class AbstractExtension implements Extension {

	@Override
	public void initRuntime(PebbleEngine engine) {

	}

	@Override
	public List<TokenParser> getTokenParsers() {
		return null;
	}
	
	@Override
	public List<Operator> getBinaryOperators(){
		return null;
	}

}
