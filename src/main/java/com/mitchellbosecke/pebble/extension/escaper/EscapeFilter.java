package com.mitchellbosecke.pebble.extension.escaper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Filter;

public class EscapeFilter implements Filter {

	public List<String> getArgumentNames() {
		List<String> names = new ArrayList<>();
		names.add("strategy");
		return names;
	}

	public Object apply(Object inputObject, Map<String, Object> args) {
		String input = (String) inputObject;

		String strategy = "html";

		if (args.get("strategy") != null) {
			strategy = (String) args.get("strategy");
		}

		switch (strategy) {
			case "html":
				input = htmlEscape(input);
				break;
			default:
				throw new RuntimeException("Unknown escaping strategy");

		}
		return input;
	}

	private String htmlEscape(String input) {
		if (input == null) {
			return input;
		}
		StringBuilder result = new StringBuilder();
		char[] chars = input.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char charac = chars[i];

			switch (charac) {
				case '&':
					result.append("&amp;");
					break;
				case '<':
					result.append("&lt;");
					break;
				case '>':
					result.append("&gt;");
					break;
				case '"':
					result.append("&quot;");
					break;
				case '\'':
					result.append("&#x27;");
					break;
				case '/':
					result.append("&#x2F;");
					break;
				default:
					result.append(charac);
					break;
			}
		}
		return result.toString();
	}

}
