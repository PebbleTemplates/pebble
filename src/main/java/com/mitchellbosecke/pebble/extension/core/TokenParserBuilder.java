package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.extension.ChainableBuilder;
import com.mitchellbosecke.pebble.tokenParser.*;

import java.util.ArrayList;
import java.util.List;

public class TokenParserBuilder extends ChainableBuilder<CoreExtension.Builder> {

    private boolean blockToken = true;
    private boolean extendsToken = true;
    private boolean filterToken = true;
    private boolean flushToken = true;
    private boolean forToken = true;
    private boolean ifToken = true;
    private boolean importToken = true;
    private boolean includeToken = true;
    private boolean macroToken = true;
    private boolean parallelToken = true;
    private boolean setToken = true;
    private boolean cacheToken = true;

    public TokenParserBuilder(CoreExtension.Builder builder) {
        super(builder);
    }

    public TokenParserBuilder enableBlockToken() {
        blockToken = true;
        return this;
    }

    public TokenParserBuilder enableExtendsToken() {
        extendsToken = true;
        return this;
    }

    public TokenParserBuilder enableFilterToken() {
        filterToken = true;
        return this;
    }

    public TokenParserBuilder enableFlushToken() {
        flushToken = true;
        return this;
    }

    public TokenParserBuilder enableForToken() {
        forToken = true;
        return this;
    }

    public TokenParserBuilder enableIfToken() {
        ifToken = true;
        return this;
    }

    public TokenParserBuilder enableImportToken() {
        importToken = true;
        return this;
    }

    public TokenParserBuilder enableIncludeToken() {
        includeToken = true;
        return this;
    }

    public TokenParserBuilder enableMacroToken() {
        macroToken = true;
        return this;
    }

    public TokenParserBuilder enableParallelToken() {
        parallelToken = true;
        return this;
    }

    public TokenParserBuilder enableSetToken() {
        setToken = true;
        return this;
    }

    public TokenParserBuilder enableCacheToken() {
        cacheToken = true;
        return this;
    }

    public TokenParserBuilder disableBlockToken() {
        blockToken = true;
        return this;
    }

    public TokenParserBuilder disableExtendsToken() {
        extendsToken = false;
        return this;
    }

    public TokenParserBuilder disableFilterToken() {
        filterToken = false;
        return this;
    }

    public TokenParserBuilder disableFlushToken() {
        flushToken = false;
        return this;
    }

    public TokenParserBuilder disableForToken() {
        forToken = false;
        return this;
    }

    public TokenParserBuilder disableIfToken() {
        ifToken = false;
        return this;
    }

    public TokenParserBuilder disableImportToken() {
        importToken = false;
        return this;
    }

    public TokenParserBuilder disableIncludeToken() {
        includeToken = false;
        return this;
    }

    public TokenParserBuilder disableMacroToken() {
        macroToken = false;
        return this;
    }

    public TokenParserBuilder disableParallelToken() {
        parallelToken = false;
        return this;
    }

    public TokenParserBuilder disableSetToken() {
        setToken = false;
        return this;
    }

    public TokenParserBuilder disableCacheToken() {
        cacheToken = false;
        return this;
    }


    public TokenParserBuilder disableAll() {
        blockToken = false;
        extendsToken = false;
        filterToken = false;
        flushToken = false;
        forToken = false;
        ifToken = false;
        importToken = false;
        includeToken = false;
        macroToken = false;
        parallelToken = false;
        setToken = false;
        cacheToken = false;

        return this;
    }


    public List<TokenParser> build() {
        List<TokenParser> features = new ArrayList<>();

        if(blockToken) {
            features.add(new BlockTokenParser());
        }
        if(extendsToken) {
            features.add(new ExtendsTokenParser());
        }
        if(filterToken) {
            features.add(new FilterTokenParser());
        }
        if(flushToken) {
            features.add(new FlushTokenParser());
        }
        if(forToken) {
            features.add(new ForTokenParser());
        }
        if(ifToken) {
            features.add(new IfTokenParser());
        }
        if(importToken) {
            features.add(new ImportTokenParser());
        }
        if(includeToken) {
            features.add(new IncludeTokenParser());
        }
        if(macroToken) {
            features.add(new MacroTokenParser());
        }
        if(parallelToken) {
            features.add(new ParallelTokenParser());
        }
        if(setToken) {
            features.add(new SetTokenParser());
        }
        if(cacheToken) {
            features.add(new CacheTokenParser());
        }

        return features;
    }

}
