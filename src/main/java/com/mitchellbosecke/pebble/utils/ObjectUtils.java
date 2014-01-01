package com.mitchellbosecke.pebble.utils;

import java.util.Iterator;

public class ObjectUtils {

	public static boolean equals(Object a, Object b){
		return ((a==b) || ((a != null) && a.equals(b)));
	}
	
	public static int getIteratorSize(Iterable<?> iterable){
		if(iterable == null){
			return 0;
		}
		Iterator<?> it = iterable.iterator();
		int size = 0;
		while(it.hasNext()){
			size++;
			it.next();
		}
		return size;
	}

}
