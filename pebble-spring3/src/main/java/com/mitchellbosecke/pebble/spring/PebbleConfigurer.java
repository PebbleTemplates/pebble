package com.mitchellbosecke.pebble.spring;

import com.mitchellbosecke.pebble.PebbleEngine;

public class PebbleConfigurer implements PebbleConfig {
	
	private PebbleEngine pebbleEngine;

	/**
	 * Set a pre-configured PebbleEngine to use for the Pebble web
	 * configuration: e.g. a shared one for web and email usage, set up via
	 * {@link org.springframework.ui.pebble.PebbleEngineFactoryBean}.
	 * <p>Note that the Spring macros will <i>not</i> be enabled automatically in
	 * case of an external PebbleEngine passed in here. Make sure to include
	 * <code>spring.vm</code> in your template loader path in such a scenario
	 * (if there is an actual need to use those macros).
	 * <p>If this is not set, PebbleEngineFactory's properties
	 * (inherited by this class) have to be specified.
	 */
	public void setPebbleEngine(PebbleEngine pebbleEngine) {
		this.pebbleEngine = pebbleEngine;
	}

	public PebbleEngine getPebbleEngine() {
		return this.pebbleEngine;
	}


}
