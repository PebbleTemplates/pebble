package io.pebbletemplates.extension.core;

import io.pebbletemplates.attributes.AttributeResolver;
import io.pebbletemplates.attributes.DefaultAttributeResolver;
import io.pebbletemplates.extension.AbstractExtension;
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
