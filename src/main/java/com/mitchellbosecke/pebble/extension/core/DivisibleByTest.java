package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.extension.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DivisibleByTest implements Test  {

    private final List<String> argumentNames = new ArrayList<>();

    public DivisibleByTest() {
        this.argumentNames.add("divisibleBy");
    }
    @Override
    public boolean apply(Object input, Map<String, Object> args) {
        if (input == null) {
            throw new IllegalArgumentException("Can not pass null value to \"divisible by\" test.");
        }

        if (input instanceof Integer) {
            return ((Integer) input) % ((Integer)args.get("divisibleBy")) == 0;
        } else {
            return ((Long) input) % ((Long) args.get("divisibleBy")) == 0;
        }
    }

    @Override
    public List<String> getArgumentNames() {
        return this.argumentNames;
    }
}