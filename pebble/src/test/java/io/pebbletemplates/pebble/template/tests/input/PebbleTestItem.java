package io.pebbletemplates.pebble.template.tests.input;

public class PebbleTestItem {

	private String name;
	
	private PebbleTestItemType itemType;
	
	private boolean hasPrefix;

	public PebbleTestItem(String name, PebbleTestItemType itemType1) {
		this.name = name;
		this.itemType = itemType1;
	}

	public PebbleTestItem(String name, PebbleTestItemType itemType1, boolean hasPrefix) {
		this.name = name;
		this.itemType = itemType1;
		this.hasPrefix = hasPrefix;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PebbleTestItemType getItemType() {
		return itemType;
	}

	public void setItemType(PebbleTestItemType pebbleTestItemType) {
		this.itemType = pebbleTestItemType;
	}

	public boolean isHasPrefix() {
		return hasPrefix;
	}

	public void setHasPrefix(boolean hasPrefix) {
		this.hasPrefix = hasPrefix;
	}
	
}
