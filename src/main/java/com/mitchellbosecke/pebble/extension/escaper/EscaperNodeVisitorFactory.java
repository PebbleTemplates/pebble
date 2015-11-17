package com.mitchellbosecke.pebble.extension.escaper;

import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.extension.NodeVisitorFactory;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

/**
 * Factory class for creating {@link EscaperNodeVisitor}.
 *
 * @author Thomas Hunziker
 *
 */
public class EscaperNodeVisitorFactory implements NodeVisitorFactory{

    private final List<String> safeFilters = new ArrayList<>();
    private boolean autoEscaping = true;

    public EscaperNodeVisitorFactory() {
        safeFilters.add("raw");
        safeFilters.add("escape");
        safeFilters.add("date");
    }

    @Override
    public NodeVisitor createVisitor(PebbleTemplate template) {
        return new EscaperNodeVisitor((PebbleTemplateImpl)template, safeFilters, this.autoEscaping);
    }

    public void addSafeFilter(String filter) {
        this.safeFilters.add(filter);
    }

    public void setAutoEscaping(boolean auto) {
        autoEscaping = auto;
    }


}
