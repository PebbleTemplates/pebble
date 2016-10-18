package com.mitchellbosecke.pebble.extension.core;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

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
    public Object apply(Object input, Map<String, Object> args, PebbleTemplateImpl self, int lineNumber) throws PebbleException{
        String data = input.toString();
        if (args.get(ARGUMENT_NAME) == null) {
            throw new PebbleException(null, MessageFormat.format("The argument ''{0}'' is required.", ARGUMENT_NAME), lineNumber, self.getName());
        }
        Map<?, ?> replacePair = (Map<?, ?>) args.get(ARGUMENT_NAME);

        for (Entry<?, ?> entry : replacePair.entrySet()) {
           data = data.replace(entry.getKey().toString(), entry.getValue().toString());
        }

        return data;
    }

}
