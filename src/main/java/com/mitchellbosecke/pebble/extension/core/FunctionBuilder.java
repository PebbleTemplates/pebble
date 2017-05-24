package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.extension.ChainableBuilder;
import com.mitchellbosecke.pebble.extension.Function;

import java.util.HashMap;
import java.util.Map;

public class FunctionBuilder extends ChainableBuilder<CoreExtension.Builder> {

    private boolean max = true;
    private boolean min = true;
    private boolean range = true;

    private String maxName = "max";
    private String minName = "min";
    private String rangeName = RangeFunction.FUNCTION_NAME;

    public FunctionBuilder(CoreExtension.Builder builder) {
        super(builder);
    }

    public FunctionBuilder enableMax() {
        max = true;
        return this;
    }

    public FunctionBuilder enableMin() {
        min = true;
        return this;
    }

    public FunctionBuilder enableRange() {
        range = true;
        return this;
    }

    public FunctionBuilder disableMax() {
        max = false;
        return this;
    }

    public FunctionBuilder disableMin() {
        min = false;
        return this;
    }

    public FunctionBuilder disableRange() {
        range = false;
        return this;
    }

    public FunctionBuilder useMaxName(String name) {
        maxName = name;
        return this;
    }

    public FunctionBuilder useMinName(String name) {
        minName = name;
        return this;
    }

    public FunctionBuilder useRangeName(String name) {
        rangeName = name;
        return this;
    }

    public FunctionBuilder disableAll() {
        max = false;
        min = false;
        range = false;

        return this;
    }

    public Map<String, Function> build() {
        Map<String, Function> functions = new HashMap<>();

        if(max) {
            functions.put(maxName, new MaxFunction());
        }
        if(min) {
            functions.put(minName, new MinFunction());
        }
        if(range) {
            functions.put(rangeName, new RangeFunction());
        }

        return functions;
    }


}
