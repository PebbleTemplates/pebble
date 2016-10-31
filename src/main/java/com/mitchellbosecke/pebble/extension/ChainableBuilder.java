package com.mitchellbosecke.pebble.extension;

public abstract class ChainableBuilder<PARENT_BUILDER> {

    private final PARENT_BUILDER parentBuilder;

    public ChainableBuilder(PARENT_BUILDER parentBuilder) {
        this.parentBuilder = parentBuilder;
    }

    /**
     * getting the parent builder
     *
     * @return the PARENT_BUILDER used to create this {@link ChainableBuilder}
     */
    public PARENT_BUILDER and() {
        return parentBuilder;
    }

}
