package io.pebbletemplates.pebble.extension.escaper;

import io.pebbletemplates.pebble.extension.NodeVisitor;
import io.pebbletemplates.pebble.extension.NodeVisitorFactory;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

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
