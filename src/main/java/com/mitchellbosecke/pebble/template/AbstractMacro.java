package com.mitchellbosecke.pebble.template;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.utils.Context;

public abstract class AbstractMacro implements Macro {
	
	protected List<String> argNames = new ArrayList<>();
	
	protected Context getLocalContext(Object[] argValues){
		Context context = new Context(true, null);
		
		for(int i = 0; i < argValues.length; i++){
			String argName = argNames.get(i);
			Object argValue = argValues[i];
			
			context.put(argName, argValue);
		}
		
		return context;
	}
	
	public String call(Object[] argValues) throws PebbleException{
		StringWriter writer = new StringWriter();
		
		Context context = getLocalContext(argValues);
		try {
			evaluate(writer, context);
		} catch (IOException e) {
			throw new PebbleException("Error occurred while calling macro");
		}
		return writer.toString();
	}
	
	public abstract void evaluate(Writer writer, Context context) throws PebbleException, IOException;
	
}
