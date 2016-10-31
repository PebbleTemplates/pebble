package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.extension.ChainableBuilder;
import com.mitchellbosecke.pebble.extension.NodeVisitorFactory;

import java.util.ArrayList;
import java.util.List;

public class NodeVisitorsBuilder extends ChainableBuilder<CoreExtension.Builder> {

    private boolean macroAndBlockRegistrant = true;

    public NodeVisitorsBuilder(CoreExtension.Builder builder) {
        super(builder);
    }

    public NodeVisitorsBuilder enableMacroAndBlockRegistrant() {
        macroAndBlockRegistrant = true;
        return this;
    }

    public NodeVisitorsBuilder disableMacroAndBlockRegistrant() {
        macroAndBlockRegistrant = false;
        return this;
    }

    public NodeVisitorsBuilder disableAll() {
        macroAndBlockRegistrant = false;
        return this;
    }

    public List<NodeVisitorFactory> build() {
        List<NodeVisitorFactory> visitors = new ArrayList<>();

        if(macroAndBlockRegistrant) {
            visitors.add(new MacroAndBlockRegistrantNodeVisitorFactory());
        }

        return visitors;
    }

}
