/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.loader;

import java.io.Reader;

import com.mitchellbosecke.pebble.error.LoaderException;

/**
 * Interface used to find templates for Pebble. Different implementations can
 * use different techniques for finding templates such as looking on the
 * classpath, looking in a database, using a servlet context, etc.
 * 
 * @author mbosecke
 *
 */
public interface Loader {

    /**
     * The reader which will be used by Pebble to read the contents of the
     * template.
     * 
     * @param templateName
     * @return
     * @throws LoaderException
     */
    public Reader getReader(String templateName) throws LoaderException;

    /**
     * A method for end users to change the charset used by the loader.
     * 
     * @param charset
     */
    public void setCharset(String charset);

    /**
     * Optional prefix to help find templates, ex "/WEB-INF/templates/" or
     * "database_schema."
     * 
     * @param prefix
     */
    public void setPrefix(String prefix);

    /**
     * Optional suffix to help find templates, ex ".html", ".peb"
     * 
     * @param suffix
     */
    public void setSuffix(String suffix);

}
