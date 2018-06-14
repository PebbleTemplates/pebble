package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.attributes.AttributeResolver;
import com.mitchellbosecke.pebble.attributes.DefaultAttributeResolver;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import java.util.ArrayList;
import java.util.List;

public class AttributeResolverExtension extends AbstractExtension {

  @Override
  public List<AttributeResolver> getAttributeResolver() {
    List<AttributeResolver> attributeResolvers = new ArrayList<>();
    attributeResolvers.add(new DefaultAttributeResolver());
    return attributeResolvers;
  }
}
