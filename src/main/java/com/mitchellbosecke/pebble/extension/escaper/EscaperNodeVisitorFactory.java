package com.mitchellbosecke.pebble.extension.escaper;

import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.extension.NodeVisitorFactory;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

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
