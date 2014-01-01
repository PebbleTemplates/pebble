package com.mitchellbosecke.pebble.utils;

import com.mitchellbosecke.pebble.template.PebbleTemplate;

public interface TemplateAware {

	public void setTemplate(PebbleTemplate template);
}
