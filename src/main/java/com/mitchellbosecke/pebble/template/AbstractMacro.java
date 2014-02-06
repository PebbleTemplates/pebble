package com.mitchellbosecke.pebble.template;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import com.mitchellbosecke.pebble.error.PebbleException;

public abstract class AbstractMacro implements Macro {

	@Override
	public String call(Context context, Map<String, Object> namedArguments) throws PebbleException {
		StringWriter writer = new StringWriter();

		context.pushLocalScope();
		context.putAll(namedArguments);
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
