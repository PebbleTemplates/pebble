package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.extension.NodeVisitor;
import io.pebbletemplates.pebble.extension.NodeVisitorFactory;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

/**
 * Implementation of {@link NodeVisitorFactory} to handle {@link MacroAndBlockRegistrantNodeVisitor}.
 *
 * @author hunziker
 */
public class MacroAndBlockRegistrantNodeVisitorFactory implements NodeVisitorFactory {

  @Override
  public NodeVisitor createVisitor(PebbleTemplate template) {
    return new MacroAndBlockRegistrantNodeVisitor((PebbleTemplateImpl) template);
  }

}
