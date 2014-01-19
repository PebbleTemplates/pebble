package com.mitchellbosecke.pebble.extension;

import java.util.Locale;

import com.mitchellbosecke.pebble.utils.LocaleAware;

public abstract class LocaleAwareSimpleFunction implements SimpleFunction, LocaleAware {

	protected Locale locale;

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
