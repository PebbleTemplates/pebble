package com.mitchellbosecke.pebble.extension.core;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mitchellbosecke.pebble.extension.Filter;

/**
 * This class implements the 'replace' filter.
 *
 * @author Thomas Hunziker
 *
 */
public class ReplaceFilter implements Filter {

    public static final String FILTER_NAME = "replace";

    private static final String ARGUMENT_NAME = "replace_pairs";

    private final static List<String> ARGS = Collections.unmodifiableList(Arrays.asList(ARGUMENT_NAME));

    @Override
    public List<String> getArgumentNames() {
        return ARGS;
    }

    @Override
    public Object apply(Object input, Map<String, Object> args) {
        String data = input.toString();
        if (args.get(ARGUMENT_NAME) == null) {
            throw new IllegalArgumentException(MessageFormat.format("The argument ''{0}'' is required.", ARGUMENT_NAME));
        }
        Map<?, ?> replacePair = (Map<?, ?>) args.get(ARGUMENT_NAME);

        for (Entry<?, ?> entry : replacePair.entrySet()) {
           data = data.replace(entry.getKey().toString(), entry.getValue().toString());
        }

        return data;
    }

}
