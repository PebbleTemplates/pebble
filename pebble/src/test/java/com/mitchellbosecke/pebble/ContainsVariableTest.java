package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContainsVariableTest {

	@Test
	public void containsVariable() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
		HashMap<String, Object> map = new HashMap<>();
		map.put("something", "ok");
		map.put("valueNull", null);
		StringWriter writer = new StringWriter();
		PebbleTemplate template = pebble.getTemplate("");

		Method initContext = PebbleTemplateImpl.class.getDeclaredMethod("initContext", Locale.class);
		initContext.setAccessible(true);
		Method evaluate = PebbleTemplateImpl.class.getDeclaredMethod("evaluate", Writer.class, EvaluationContextImpl.class);
		evaluate.setAccessible(true);

		EvaluationContextImpl context = (EvaluationContextImpl) initContext.invoke(template, new Object[] { null });
		context.getScopeChain().pushScope(map);
		evaluate.invoke(template, writer, context);

		assertTrue(context.containsVariable("something"));
		assertTrue(context.containsVariable("valueNull"));
		assertFalse(context.containsVariable("whatever"));
	}
}
