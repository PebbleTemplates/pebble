package com.mitchellbosecke.pebble.extension.debug;

import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.extension.NodeVisitorFactory;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

/**
 * Implementation of {@link NodeVisitorFactory} to create {@link PrettyPrintNodeVisitor}.
 */
public class PrettyPrintNodeVisitorFactory implements NodeVisitorFactory {

  @Override
  public NodeVisitor createVisitor(PebbleTemplate template) {
    return new PrettyPrintNodeVisitor((PebbleTemplateImpl) template);
  }

}
