package com.mitchellbosecke.pebble.extension.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.mitchellbosecke.pebble.extension.LocaleAware;
import com.mitchellbosecke.pebble.extension.NamedArguments;
import com.mitchellbosecke.pebble.extension.SimpleFunction;

public class MessageFunction implements SimpleFunction, NamedArguments, LocaleAware {

	private Locale locale;
	
	public void setLocale(Locale locale){
		this.locale = locale;
	}
	
	@Override
	public List<String> getArgumentNames() {
		List<String> names = new ArrayList<>();
		names.add("bundle");
		names.add("key");
		return names;
	}
	@Override
	public Object execute(Map<String, Object> args) {
		String basename = (String) args.get("bundle");
		String key = (String) args.get("key");

		ResourceBundle bundle = ResourceBundle.getBundle(basename, locale);

		return bundle.getObject(key);
	}

}
