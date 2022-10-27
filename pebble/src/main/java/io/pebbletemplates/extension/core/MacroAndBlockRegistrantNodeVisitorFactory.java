package io.pebbletemplates.extension.core;

import io.pebbletemplates.extension.NodeVisitor;
import io.pebbletemplates.extension.NodeVisitorFactory;
import io.pebbletemplates.template.PebbleTemplate;
import io.pebbletemplates.template.PebbleTemplateImpl;

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
