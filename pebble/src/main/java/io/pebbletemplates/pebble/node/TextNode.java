/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.node;

import io.pebbletemplates.pebble.extension.NodeVisitor;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

import java.io.IOException;
import java.io.Writer;

/**
 * Represents static text in a template.
 *
 * @author mbosecke
 */
public class TextNode extends AbstractRenderableNode {

  /**
   * Most Writers will convert strings to char[] so we might as well store it as a char[] to begin
   * with; small performance optimization.
   */
  private final char[] data;

  public TextNode(String text, int lineNumber) {
    super(lineNumber);

    int length = text.length();
    this.data = new char[text.length()];
    text.getChars(0, length, this.data, 0);
  }

  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context)
      throws IOException {
    writer.write(this.data);
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public char[] getData() {
    return this.data;
  }

}
