/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.escaper;

import com.coverity.security.Escape;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EscapeFilter implements Filter {

    private String defaultStrategy = "html";

    private final List<String> argumentNames = new ArrayList<>();

    private final Map<String, EscapingStrategy> strategies;

    public EscapeFilter() {
        this.strategies = new HashMap<>();
        buildDefaultStrategies();
        argumentNames.add("strategy");
    }

    private void buildDefaultStrategies() {
        strategies.put("html", new EscapingStrategy() {

            @Override
            public String escape(String input) {
                return Escape.htmlText(input);
            }
        });

        strategies.put("js", new EscapingStrategy() {

            @Override
            public String escape(String input) {
                return Escape.jsString(input);
            }
        });

        strategies.put("css", new EscapingStrategy() {

            @Override
            public String escape(String input) {
                return Escape.cssString(input);
            }
        });

        strategies.put("html_attr", new EscapingStrategy() {

            @Override
            public String escape(String input) {
                return Escape.html(input);
            }
        });

        strategies.put("url_param", new EscapingStrategy() {

            @Override
            public String escape(String input) {
                return Escape.uriParam(input);
            }
        });
    }

    public List<String> getArgumentNames() {
        return argumentNames;
    }

    public Object apply(Object inputObject, Map<String, Object> args) {
        if (inputObject == null || inputObject instanceof SafeString) {
            return inputObject;
        }
        String input = StringUtils.toString(inputObject);

        String strategy = defaultStrategy;

        if (args.get("strategy") != null) {
            strategy = (String) args.get("strategy");
        }

        if (!strategies.containsKey(strategy)) {
            throw new RuntimeException(String.format("Unknown escaping strategy [%s]", strategy));
        }

        return new SafeString(strategies.get(strategy).escape(input));
    }

    public String getDefaultStrategy() {
        return defaultStrategy;
    }

    public void setDefaultStrategy(String defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }

    public void addEscapingStrategy(String name, EscapingStrategy strategy) {
        this.strategies.put(name, strategy);
    }
}
