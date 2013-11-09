/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.utils;

public class StringLengthComparator implements java.util.Comparator<String> {

	public StringLengthComparator() {
		super();
	}

	public int compare(String s1, String s2) {
		int dist1 = Math.abs(s1.length());
		int dist2 = Math.abs(s2.length());

		return dist2 - dist1;
	}
}
