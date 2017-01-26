package com.mitchellbosecke.pebble.extension.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

/**
 * java.time.LocalDateTime Filter
 * 
 * @author mboullouz <mohamed.boullouz@gmail.com>
 *
 */
public class LocalDateTimeFilter implements Filter {
	
	public static final String FILTER_NAME = "localDateTime";

	private final List<String> argumentNames = new ArrayList<>();

	public LocalDateTimeFilter() {
		argumentNames.add("format");
	}

	@Override
	public List<String> getArgumentNames() {
		return argumentNames;
	}

	@Override
	public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context,
			int lineNumber) throws PebbleException {
	    LocalDateTime localDateTime = (LocalDateTime)input;
	    String format= (String) args.get("format");
	    DateTimeFormatter formatter;
	    if(format==null || format.equals("")){
	    	formatter = DateTimeFormatter.ISO_DATE_TIME;
	    }
	    else {
		 formatter = DateTimeFormatter.ofPattern((String) args.get("format"));
	    }
		return localDateTime.format(formatter);
	}

}
