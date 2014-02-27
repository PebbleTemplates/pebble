package com.mitchellbosecke.pebble.node;

import java.io.IOException;
import java.io.Writer;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public interface RenderableNode extends Node {

	public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException,
			IOException;
}
