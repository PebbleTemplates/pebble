package io.pebbletemplates.pebble.extension.debug;

import io.pebbletemplates.pebble.extension.NodeVisitor;
import io.pebbletemplates.pebble.extension.NodeVisitorFactory;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

/**
 * Implementation of {@link NodeVisitorFactory} to create {@link PrettyPrintNodeVisitor}.
 */
public class PrettyPrintNodeVisitorFactory implements NodeVisitorFactory {

  @Override
  public NodeVisitor createVisitor(PebbleTemplate template) {
    return new PrettyPrintNodeVisitor((PebbleTemplateImpl) template);
  }

}
