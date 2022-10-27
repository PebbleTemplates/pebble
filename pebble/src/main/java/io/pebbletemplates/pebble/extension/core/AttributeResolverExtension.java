package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.attributes.AttributeResolver;
import io.pebbletemplates.pebble.attributes.DefaultAttributeResolver;
import io.pebbletemplates.pebble.extension.AbstractExtension;

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
