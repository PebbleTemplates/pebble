package com.mitchellbosecke.pebble.loader;

import java.util.Date;

public interface Loader {
	
	public String getSource(String name);
	
	public String getCacheKey(String name);
	
	public boolean isFresh(String name, Date timestamp);

}