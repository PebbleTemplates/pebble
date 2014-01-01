package com.mitchellbosecke.pebble.extension;

import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.utils.TemplateAware;

public abstract class TemplateAwareFilter implements Filter, TemplateAware {

	protected PebbleTemplate template;

	public void setTemplate(PebbleTemplate template) {
		this.template = template;
	}

}
