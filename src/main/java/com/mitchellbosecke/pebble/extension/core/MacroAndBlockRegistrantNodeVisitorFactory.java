package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.extension.NodeVisitorFactory;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

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
