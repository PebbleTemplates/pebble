package com.mitchellbosecke.pebble.utils;

import java.util.Iterator;

/**
 * Convencience class, particularily used by the NodeFor class.
 * 
 * @author Mitchell
 * 
 */
public class ObjectUtils {

	/**
	 * Returns the size of an Iterator
	 * 
	 * @param iterable
	 * @return
	 */
	public static int getIteratorSize(Iterable<?> iterable) {
		if (iterable == null) {
			return 0;
		}
		Iterator<?> it = iterable.iterator();
		int size = 0;
		while (it.hasNext()) {
			size++;
			it.next();
		}
		return size;
	}

}
