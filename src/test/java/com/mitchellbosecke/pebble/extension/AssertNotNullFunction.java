package com.mitchellbosecke.pebble.extension;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

public class AssertNotNullFunction implements Function {

    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    @Override
    public Object execute(Map<String, Object> args) {
        Object arg = args.get(String.valueOf(0));
        assertNotNull("Argument for assertNotNull function is null", arg);
        return arg;
    }

}
