/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.mitchellbosecke.pebble.PebbleEngine;

public class I18nExtension extends AbstractExtension {

	@Override
	public void initRuntime(PebbleEngine engine) {
	}

	@Override
	public List<SimpleFunction> getFunctions() {
		ArrayList<SimpleFunction> functions = new ArrayList<>();
		functions.add(messageFunction);
		return functions;
	}

	private SimpleFunction messageFunction = new LocaleAwareSimpleFunction() {

		@Override
		public String getName() {
			return "message";
		}

		@Override
		public Object execute(List<Object> args) {
			String basename = (String) args.get(0);
			String key = (String) args.get(1);

			ResourceBundle bundle = ResourceBundle.getBundle(basename, locale);

			return bundle.getObject(key);
		}
	};

}
