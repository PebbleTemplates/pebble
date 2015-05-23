package com.mitchellbosecke.pebble.extension;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;

@Ignore
public class TestingExtension extends AbstractExtension {

    private InvocationCountingFunction invocationCountingFunction = new InvocationCountingFunction();

    @Override
    public Map<String, Function> getFunctions() {
        Map<String, Function> functions = new HashMap<>();
        functions.put("invocationCountingFunction", invocationCountingFunction);
        return functions;
    }

    @Override
    public Map<String, Filter> getFilters() {
        Map<String, Filter> filters = new HashMap<>();
        filters.put("mapToString", new MapToStringFilter());
        return filters;
    }

    public InvocationCountingFunction getInvocationCountingFunction() {
        return invocationCountingFunction;
    }

}
