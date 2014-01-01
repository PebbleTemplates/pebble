package com.mitchellbosecke.pebble.utils;

public class ObjectUtils {

	public static boolean equals(Object a, Object b){
		return ((a==b) || ((a != null) && a.equals(b)));
	}

}
