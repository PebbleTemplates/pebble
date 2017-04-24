package com.mitchellbosecke.pebble.macro;

import java.util.HashMap;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;

public class PebbleExtension extends AbstractExtension{
  @Override
  public Map<String, Filter> getFilters() {
    Map<String, Filter> f = new HashMap<>();
    f.put(TestFilter.FILTER_NAME, new TestFilter());
      return f;
  }

}
