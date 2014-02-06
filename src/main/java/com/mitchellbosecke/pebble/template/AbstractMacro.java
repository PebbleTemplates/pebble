package com.mitchellbosecke.pebble.template;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import com.mitchellbosecke.pebble.error.PebbleException;

public abstract class AbstractMacro implements Macro {

	/**
	 * Will set up a new context scope and a local writer for this macro. A new
	 * scope is necessary because a macro should not have access to the main
	 * context variables, only whatever arguments were provided to it. A local
	 * writer is necessary because the result of the macro evaluation may be
	 * further modified (via filter for example) before being output to the
	 * user-provided writer.
	 */
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
