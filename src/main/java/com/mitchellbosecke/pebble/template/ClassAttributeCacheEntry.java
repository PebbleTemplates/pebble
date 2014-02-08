package com.mitchellbosecke.pebble.template;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

public class ClassAttributeCacheEntry {

	private final Map<String, Member> attributes = new HashMap<>();

	public boolean hasAttribute(String attributeName) {
		return attributes.containsKey(attributeName);
	}

	public Member getAttribute(String attributeName) {
		return attributes.get(attributeName);
	}

	public void putAttribute(String attributeName, Member attribute) {
		attributes.put(attributeName, attribute);
	}

}
