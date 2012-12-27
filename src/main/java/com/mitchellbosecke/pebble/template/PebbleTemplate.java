package com.mitchellbosecke.pebble.template;

import java.util.Map;

public interface PebbleTemplate {

	public String render(Map<String, Object> model);

}
