package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.extension.ChainableBuilder;
import com.mitchellbosecke.pebble.extension.Test;

import java.util.HashMap;
import java.util.Map;

public class TestBuilder extends ChainableBuilder<CoreExtension.Builder> {

    private boolean empty = true;
    private boolean even = true;
    private boolean iterable = true;
    private boolean map = true;
    private boolean nullTest = true;
    private boolean odd = true;
    private boolean defined = true;

    private String emptyName = "empty";
    private String evenName = "even";
    private String iterableName = "iterable";
    private String mapName = "map";
    private String nullTestName = "null";
    private String oddName = "odd";
    private String definedName = "defined";

    public TestBuilder(CoreExtension.Builder builder) {
        super(builder);
    }

    public TestBuilder enableEmpty() {
        empty = true;
        return this;
    }

    public TestBuilder enableEven() {
        even = true;
        return this;
    }

    public TestBuilder enableIterable() {
        iterable = true;
        return this;
    }

    public TestBuilder enableMap() {
        map = true;
        return this;
    }

    public TestBuilder enableNullTest() {
        nullTest = true;
        return this;
    }

    public TestBuilder enableOdd() {
        odd = true;
        return this;
    }

    public TestBuilder enableDefined() {
        defined = true;
        return this;
    }

    public TestBuilder disableEmpty() {
        empty = false;
        return this;
    }

    public TestBuilder disableEven() {
        even = false;
        return this;
    }

    public TestBuilder disableIterable() {
        iterable = false;
        return this;
    }

    public TestBuilder disableMap() {
        map = false;
        return this;
    }

    public TestBuilder disableNullTest() {
        nullTest = false;
        return this;
    }

    public TestBuilder disableOdd() {
        odd = false;
        return this;
    }

    public TestBuilder disableDefined() {
        defined = false;
        return this;
    }

    public TestBuilder useEmptyName(String name) {
        emptyName = name;
        return this;
    }

    public TestBuilder useEvenName(String name) {
        evenName = name;
        return this;
    }

    public TestBuilder useIterableName(String name) {
        iterableName = name;
        return this;
    }

    public TestBuilder useMapName(String name) {
        mapName = name;
        return this;
    }

    public TestBuilder useNullTestName(String name) {
        nullTestName = name;
        return this;
    }

    public TestBuilder useOddName(String name) {
        oddName = name;
        return this;
    }

    public TestBuilder useDefinedName(String name) {
        definedName = name;
        return this;
    }

    public TestBuilder disableAll() {
        empty = false;
        even = false;
        iterable = false;
        map = false;
        nullTest = false;
        odd = false;
        defined = false;

        return this;
    }

    public Map<String, Test> build() {
        Map<String, Test> tests = new HashMap<>();

        if(empty) {
            tests.put(emptyName, new EmptyTest());
        }
        if(even) {
            tests.put(evenName, new EvenTest());
        }
        if(iterable) {
            tests.put(iterableName, new IterableTest());
        }
        if(map) {
            tests.put(mapName, new MapTest());
        }
        if(nullTest) {
            tests.put(nullTestName, new NullTest());
        }
        if(odd) {
            tests.put(oddName, new OddTest());
        }
        if(defined) {
            tests.put(definedName, new DefinedTest());
        }

        return tests;
    }
}
