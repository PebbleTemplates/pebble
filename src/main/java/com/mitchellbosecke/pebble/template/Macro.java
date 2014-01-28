/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.template;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.utils.Context;

public interface Macro {

	public String getName();
	
	public void init();

	public String call(Context context, Object[] argValues) throws PebbleException;
}
