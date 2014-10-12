package com.mitchellbosecke.pebble.extension;

import java.util.HashMap;
import java.util.Map;

public class AssertNotNullExtension extends AbstractExtension {

    @Override
    public Map<String, Function> getFunctions() {
        Map<String, Function> functions = new HashMap<>();
        functions.put("assertNotNull", new AssertNotNullFunction());
        return functions;
    }

}
