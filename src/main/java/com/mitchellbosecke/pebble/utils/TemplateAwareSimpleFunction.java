package com.mitchellbosecke.pebble.utils;

import com.mitchellbosecke.pebble.template.PebbleTemplate;

public abstract class TemplateAwareSimpleFunction implements SimpleFunction, TemplateAware {

	protected PebbleTemplate template;

	public void setTemplate(PebbleTemplate template) {
		this.template = template;
	}

}
