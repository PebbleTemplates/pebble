package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.util.List;
import java.util.Map;

public class Nl2brFilter implements Filter {
    public static final String FILTER_NAME = "nl2br";

    @Override
    public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) throws PebbleException {
        if (input == null) {
            return null;
        }

        if (!(input instanceof String)) {
            throw new IllegalArgumentException("nl2br filters only supports String input.");
        }

        String strInput = (String) input;
        if (strInput.indexOf('\n') == -1 && strInput.indexOf('\r') == -1) {
            return strInput;
        }

        // Pre-size the StringBuilder to be the input length + 16 (to account for some extra <br /> tags)
        // The 16 is the default size of the StringBuilders default constructor (new StringBuilder())
        StringBuilder sb = new StringBuilder(strInput.length() + 16);

        return convertNewlinesToBr(strInput, sb);
    }

    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    private String convertNewlinesToBr(String input, StringBuilder sb) {
        final int len = input.length();
        for (int i = 0; i < len; i++) {
            char c = input.charAt(i);
            if (c == '\r') {
                // Convert CR (possibly part of CRLF) to <br>
                sb.append("<br />");

                // Skip a following LF to avoid double converting CRLF
                if (i + 1 < len && input.charAt(i + 1) == '\n') {
                    i++; // skip the '\n'
                }
            } else if (c == '\n') {
                // Lone LF
                sb.append("<br />");
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}
