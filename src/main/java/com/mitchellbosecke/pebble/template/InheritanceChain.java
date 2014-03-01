package com.mitchellbosecke.pebble.template;

import java.util.ArrayList;

public class InheritanceChain {

	private ArrayList<PebbleTemplateImpl> family;

	private int current = 0;

	public InheritanceChain(PebbleTemplateImpl currentTemplate) {
		family = new ArrayList<>();
		family.add(currentTemplate);
		current = 0;
	}

	public void pushAncestor(PebbleTemplateImpl ancestor) {
		family.add(ancestor);
	}

	public void ascend() {
		current++;
	}

	public void descend() {
		current--;
	}

	public PebbleTemplateImpl getChild() {
		if(current == 0){
			return null;
		}
		return family.get(current - 1);
	}

	public PebbleTemplateImpl getParent() {
		if(current == family.size() - 1){
			return null;
		}
		return family.get(current + 1);
	}
}
