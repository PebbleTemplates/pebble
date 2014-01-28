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

	private void addArgumentsToLocalScope(Context context, Object[] argValues) {
		int i = 0;
		for (String argName : argNames) {
			Object argValue = null;

			if (i < argValues.length) {
				argValue = argValues[i];
			}

			context.put(argName, argValue);
			i++;
		}
	}

	public String call(Context context, Object[] argValues) throws PebbleException {
		StringWriter writer = new StringWriter();

		context.pushLocalScope();
		addArgumentsToLocalScope(context, argValues);
		try {
			evaluate(writer, context);
		} catch (IOException e) {
			throw new PebbleException(e, "Error occurred while calling macro");
		}
		context.popScope();
		return writer.toString();
	}

	public abstract void evaluate(Writer writer, Context context) throws PebbleException, IOException;

}
