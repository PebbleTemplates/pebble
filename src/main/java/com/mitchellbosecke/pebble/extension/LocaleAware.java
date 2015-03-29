/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension;

import java.util.Locale;

/**
 * 
 * The LocaleAware interface was an old technique used for filters/tests to
 * receive the template's locale. Unfortunately, it wasn't thread safe; the
 * PebbleEngine only stores one copy of the function/test so if multiple
 * concurrent templates call the setLocal method and then later apply the test,
 * it is likely that the locale has been overwritten by another template.
 * 
 * The new and preferred manner of the function/test receiving the locale is for
 * the template to provide some extra variables in the arguments map directly to
 * {@link com.mitchellbosecke.pebble.extension.Function#execute} or
 * {@link com.mitchellbosecke.pebble.extension.Test#apply}. See <a href=
 * "http://www.mitchellbosecke.com/pebble/documentation/guide/extending-pebble"
 * >the guide on extending pebble</a> for more information.
 * 
 * @deprecated
 * @author mbosecke
 *
 */
public interface LocaleAware {

    public void setLocale(Locale locale);
}
