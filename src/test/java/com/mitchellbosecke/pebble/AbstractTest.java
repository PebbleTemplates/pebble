/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.loader.Loader;

public abstract class AbstractTest {

	protected final PebbleEngine pebble;
	protected final Loader<?> loader;

	public AbstractTest() {

		// main testing engine uses all default settings
		pebble = new PebbleEngine();
		pebble.getLoader().setPrefix("templates");

		loader = pebble.getLoader();
	}

}
