package com.mitchellbosecke.pebble.utils;

public class StringUtils {

	public static boolean isEmpty(String value) {
		boolean blank = false;

		if (value == null) {
			blank = true;
		} else if ("".equals(value)) {
			blank = true;
		}
		return blank;
	}
	
	public static boolean isBlank(String value) {
		boolean blank = false;

		if (value == null) {
			blank = true;
		} else if ("".equals(value.trim())) {
			blank = true;
		}
		return blank;
	}

	public static String capitalize(String value) {
		if (value == null || (value.length() == 0)) {
			return value;
		}

		char firstCharacter = value.charAt(0);
		if (Character.isTitleCase(firstCharacter)) {
			return value;
		}
		return new StringBuilder(value.length()).append(Character.toTitleCase(firstCharacter))
				.append(value.substring(1)).toString();
	}

	public static String abbreviate(String value, int maxWidth) {
		if(value == null){
			return null;
		}
		String ellipsis = "...";
		int length = value.length();
		
		if(length < maxWidth){
			return value;
		}
		if(length <= 3){
			return value;
		}
		return value.substring(0, maxWidth - 3) + ellipsis;
	}

}
