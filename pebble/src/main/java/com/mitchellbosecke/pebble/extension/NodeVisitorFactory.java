package com.mitchellbosecke.pebble.extension;

import com.mitchellbosecke.pebble.template.PebbleTemplate;

/**
 * The node visitor factory creates {@link NodeVisitor}s.
 *
 * <p>
 * {@link Extension} can provide own implementation to provide their own {@link NodeVisitor}s.
 *
 * @author Thomas Hunziker
 */
public interface NodeVisitorFactory {

  /**
   * This method creates a new instance of a {@link NodeVisitor}.
   *
   * <p>
   * The method is called whenever a visitor is applied to a {@link PebbleTemplate}.
   *
   * <p>
   * The method needs to be thread-safe. However the {@link NodeVisitor} itself does not need to be
   * thread-safe.
   *
   * @param template the template for which a visitor should be created for.
   * @return the visitor.
   */
  NodeVisitor createVisitor(PebbleTemplate template);

}
