/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.escaper;

import java.util.HashMap;
import java.util.Map;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;

public class EscaperExtension extends AbstractExtension {

	@Override
	public void initRuntime(PebbleEngine engine) {
	}

	@Override
	public Map<String, Filter> getFilters() {
		Map<String, Filter> filters = new HashMap<>();
		filters.put("escape", new EscapeFilter());
		return filters;
	}

}
