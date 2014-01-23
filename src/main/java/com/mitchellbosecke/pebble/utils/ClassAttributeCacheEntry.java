package com.mitchellbosecke.pebble.utils;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

public class ClassAttributeCacheEntry {

	private class AttributeWithHitCount {
		private final Member member;
		private int hitCount = 0;

		public AttributeWithHitCount(Member member) {
			this.member = member;
		}

		public Member getMember() {
			return member;
		}

		public int getHitCount() {
			return hitCount;
		}

		public void incrementHitCount() {
			this.hitCount++;
		}
	}

	private final Map<String, AttributeWithHitCount> attributes = new HashMap<>();

	public boolean hasAttribute(String attributeName) {
		return attributes.containsKey(attributeName);
	}

	public Member getAttribute(String attributeName) {
		AttributeWithHitCount attr = attributes.get(attributeName);
		attr.incrementHitCount();
		return attr.getMember();
	}

	public int getAttributeHitCount(String attributeName) {
		AttributeWithHitCount attr = attributes.get(attributeName);
		return attr.getHitCount();
	}

	public void putAttribute(String attributeName, Member attribute) {
		AttributeWithHitCount attr = new AttributeWithHitCount(attribute);
		attributes.put(attributeName, attr);
	}

}
