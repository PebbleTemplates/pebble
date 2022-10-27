package io.pebbletemplates.pebble.macro;

import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Filter;

import java.util.HashMap;
import java.util.Map;

public class PebbleExtension extends AbstractExtension {

  @Override
  public Map<String, Filter> getFilters() {
    Map<String, Filter> f = new HashMap<>();
    f.put(TestFilter.FILTER_NAME, new TestFilter());
    return f;
  }

}
