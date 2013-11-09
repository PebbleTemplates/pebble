/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.error;

public class AttributeNotFoundException extends PebbleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3863732457312917327L;

	public AttributeNotFoundException(String message) {
		super(message);
	}

}
