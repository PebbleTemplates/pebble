package io.pebbletemplates.pebble.loader;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import com.mitchellbosecke.pebble.error.LoaderException;

public class MemoryLoader implements Loader<String> {
    private String prefix;

    private String suffix;

    private String charset = "UTF-8";

    ArrayList<Hashtable<String, String>> fileTable = new ArrayList<>();

    public Reader getReader(String templateName)
    {
        String content = "";
        for (Hashtable<String, String> hashtable : fileTable)
        {
            if(hashtable.containsKey("templateName"))
            {
                if(hashtable.get("templateName").equals(templateName))
                {
                    content = hashtable.get("content");
                    break;
                }
            }
        }

        if(content.isEmpty())
        {
            throw new LoaderException(null, "Could not find template \"" + templateName + "\""); //XSS?
        }

        return new StringReader(content);
    }

    public void addFile(String templateName, String content)
    {
        Hashtable<String, String> table = new Hashtable<>();
        table.put("templateName", templateName);
        table.put("content", content);
        fileTable.add(table);
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getCharset() {
        return this.charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String resolveRelativePath(String relativePath, String anchorPath) {
        return relativePath; // hierarchy is flat
    }

    public String createCacheKey(String templateName) {
        return templateName;
    }

    public boolean resourceExists(String templateName)
    {
        for (Hashtable<String, String> hashtable : fileTable)
        {
            if(hashtable.containsKey("templateName"))
            {
                if(hashtable.get("templateName").equals(templateName))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
