package com.mitchellbosecke.pebble.spring;

import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

public class PebbleViewResolver extends AbstractTemplateViewResolver {

	public PebbleViewResolver() {
		setViewClass(requiredViewClass());
	}

	/**
	 * Requires {@link PebbleView}.
	 */
	@Override
	protected Class requiredViewClass() {
		return PebbleView.class;
	}

	@Override
	protected void initApplicationContext() {
		super.initApplicationContext();
	}

	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		PebbleView view = (PebbleView) super.buildView(viewName);
		return view;
	}

}
