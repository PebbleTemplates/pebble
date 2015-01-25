package com.mitchellbosecke.pebble.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.error.LoaderException;

/**
 * Loader that uses a servlet context to find templates.
 * 
 * @author mbosecke
 *
 */
public class ServletLoader implements Loader {

    private static final Logger logger = LoggerFactory.getLogger(ServletLoader.class);

    private String prefix;

    private String suffix;

    private String charset = "UTF-8";

    private final ServletContext context;

    public ServletLoader(ServletContext context) {
        this.context = context;
    }

    @Override
    public Reader getReader(String templateName) throws LoaderException {

        InputStreamReader isr = null;
        Reader reader = null;

        InputStream is = null;

        // Add the prefix and make sure that it ends with a separater character
        StringBuilder path = new StringBuilder("");
        if (getPrefix() != null) {

            path.append(getPrefix());

            if (!getPrefix().endsWith(String.valueOf(File.separatorChar))) {
                path.append(File.separatorChar);
            }
        }

        String location = path.toString() + templateName + (getSuffix() == null ? "" : getSuffix());
        logger.debug("Looking for template in {}.", location);

        is = context.getResourceAsStream(location);

        if (is == null) {
            throw new LoaderException(null, "Could not find template \"" + location + "\"");
        }

        try {
            isr = new InputStreamReader(is, charset);
            reader = new BufferedReader(isr);
        } catch (UnsupportedEncodingException e) {
        }

        return reader;
    }

    public String getSuffix() {
        return suffix;
    }

    @Override
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getCharset() {
        return charset;
    }

    @Override
    public void setCharset(String charset) {
        this.charset = charset;
    }

}
