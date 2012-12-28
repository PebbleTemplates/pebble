/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.template;

import java.util.Map;

import com.mitchellbosecke.pebble.PebbleEngine;

public interface PebbleTemplate {
	
	public String render(Map<String, Object> model);
	
	public void setEngine(PebbleEngine engine);

}
