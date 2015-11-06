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
import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.error.LoaderException;

/**
 * This loader will delegate control to a list of children loaders. This is the
 * default implementation used by Pebble; it delegates to a classpath loader and
 * a file loader to increase the chances of finding templates with varying
 * setups.
 *
 * @author mbosecke
 *
 */
public class DelegatingLoader implements Loader {

    private String prefix;

    private String suffix;

    private String charset = "UTF-8";

    /**
     * Children loaders to delegate to. The loaders are used in order and as
     * soon as one of them finds a template, the others will not be given a
     * chance to do so.
     */
    private final List<Loader> loaders = new ArrayList<>();

    /**
     * Constructor provided with a list of children loaders.
     *
     * @param loaders
     *            A list of loaders to delegate to
     */
    public DelegatingLoader(List<Loader> loaders) {
        this.loaders.addAll(loaders);
    }

    @Override
    public Reader getReader(String templateName) throws LoaderException {

        Reader reader = null;

        for (Loader loader : this.loaders) {
            try {
                reader = loader.getReader(templateName);

                if (reader != null) {
                    break;
                }
            } catch (LoaderException e) {
                // do nothing
            }
        }
        if (reader == null) {
            throw new LoaderException(null, "Could not find template \"" + templateName + "\"");
        }

        return reader;
    }

    public String getSuffix() {
        return suffix;
    }

    @Override
    public void setSuffix(String suffix) {
        this.suffix = suffix;
        for (Loader loader : loaders) {
            loader.setSuffix(suffix);
        }
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
        for (Loader loader : loaders) {
            loader.setPrefix(prefix);
        }
    }

    public String getCharset() {
        return charset;
    }

    @Override
    public void setCharset(String charset) {
        this.charset = charset;
        for (Loader loader : loaders) {
            loader.setCharset(charset);
        }
    }

    @Override
    public String resolveRelativePath(String relativePath, String anchorPath) {
        if (relativePath == null) {
            return relativePath;
        }
        for (Loader loader : this.loaders) {
            String path = loader.resolveRelativePath(relativePath, anchorPath);
            if (path != null) {
                return path;
            }
        }
        return null;
    }
}
