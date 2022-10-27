package io.pebbletemplates.extension.debug;

import io.pebbletemplates.extension.NodeVisitor;
import io.pebbletemplates.extension.NodeVisitorFactory;
import io.pebbletemplates.template.PebbleTemplate;
import io.pebbletemplates.template.PebbleTemplateImpl;

/**
 * Implementation of {@link NodeVisitorFactory} to create {@link PrettyPrintNodeVisitor}.
 */
public class PrettyPrintNodeVisitorFactory implements NodeVisitorFactory {

  @Override
  public NodeVisitor createVisitor(PebbleTemplate template) {
    return new PrettyPrintNodeVisitor((PebbleTemplateImpl) template);
  }

}
