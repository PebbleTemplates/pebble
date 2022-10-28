package io.pebbletemplates.pebble.loader;

import io.pebbletemplates.pebble.error.LoaderException;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MemoryLoader implements Loader<String> {
    private final List<TemplateDefinition> templateDefinitions = new ArrayList<>();

    @Override
    public Reader getReader(String templateName) {
        String content = "";
        for (TemplateDefinition templateDefinition : this.templateDefinitions) {
            if (templateDefinition.templateName.equals(templateName)) {
                content = templateDefinition.content;
                break;
            }
        }

        if (content.isEmpty()) {
            throw new LoaderException(null, "Could not find template \"" + templateName + "\"");
        }

        return new StringReader(content);
    }

    public void addTemplate(String templateName, String content) {
        if (templateName == null) {
            throw new IllegalArgumentException("templateName cannot be null");
        }
        if (content == null) {
            throw new IllegalArgumentException("content cannot be null");
        }
        this.templateDefinitions.add(new TemplateDefinition(templateName, content));
    }

    @Override
    public void setSuffix(String suffix) {
    }

    @Override
    public void setPrefix(String prefix) {
    }

    @Override
    public void setCharset(String charset) {
    }

    @Override
    public String resolveRelativePath(String relativePath, String anchorPath) {
        return relativePath; // hierarchy is flat
    }

    @Override
    public String createCacheKey(String templateName) {
        return templateName;
    }

    @Override
    public boolean resourceExists(String templateName) {
        for (TemplateDefinition templateDefinition : this.templateDefinitions) {
            if (templateDefinition.templateName.equals(templateName)) {
                return true;
            }
        }
        return false;
    }

    private static class TemplateDefinition {
        public final String templateName;
        public final String content;

        public TemplateDefinition(String templateName,
                                  String content) {
            this.templateName = templateName;
            this.content = content;
        }
    }
}
