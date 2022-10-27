package io.pebbletemplates.extension.escaper;

import io.pebbletemplates.extension.NodeVisitor;
import io.pebbletemplates.extension.NodeVisitorFactory;
import io.pebbletemplates.template.PebbleTemplate;
import io.pebbletemplates.template.PebbleTemplateImpl;

/**
 * Factory class for creating {@link EscaperNodeVisitor}.
 *
 * @author Thomas Hunziker
 */
public class EscaperNodeVisitorFactory implements NodeVisitorFactory {

  private boolean autoEscaping = true;

  @Override
  public NodeVisitor createVisitor(PebbleTemplate template) {
    return new EscaperNodeVisitor((PebbleTemplateImpl) template, this.autoEscaping);
  }

  public void setAutoEscaping(boolean auto) {
    autoEscaping = auto;
  }


}
