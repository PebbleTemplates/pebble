package com.mitchellbosecke.pebble.extension.core;

import java.util.Date;
import java.util.Map;

public class TimestampFilter extends DateFilter {

    public TimestampFilter() {
        super();
    }

    @Override
    public Object apply(Object input, Map<String, Object> args) {
        if (input instanceof Integer){
            long longValue = ((Integer)input).longValue() * 1000;
            Date date = new Date(longValue);
            return super.apply(date, args);
        } else if (input instanceof Long) {
            Date date = new Date((Long)input);
            return super.apply(date, args);
        }
        throw new RuntimeException("timestamp filter only accept integer or long value");

    }
}
