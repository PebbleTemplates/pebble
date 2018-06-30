/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NamedArguments;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArgumentsNode implements Node {

  private final List<NamedArgumentNode> namedArgs;

  private final List<PositionalArgumentNode> positionalArgs;

  private final int lineNumber;

  public ArgumentsNode(List<PositionalArgumentNode> positionalArgs,
      List<NamedArgumentNode> namedArgs,
      int lineNumber) {
    this.positionalArgs = positionalArgs;
    this.namedArgs = namedArgs;
    this.lineNumber = lineNumber;
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public List<NamedArgumentNode> getNamedArgs() {
    return this.namedArgs;
  }

  public List<PositionalArgumentNode> getPositionalArgs() {
    return this.positionalArgs;
  }

  /**
   * Using hints from the filter/function/test/macro it will convert an ArgumentMap (which holds
   * both positional and named arguments) into a regular Map that the filter/function/test/macro is
   * expecting.
   *
   * @param self The template implementation
   * @param context The evaluation context
   * @param invocableWithNamedArguments The named arguments object
   * @return Returns a map representaion of the arguments
   */
  public Map<String, Object> getArgumentMap(PebbleTemplateImpl self, EvaluationContextImpl context,
      NamedArguments invocableWithNamedArguments) {
    Map<String, Object> result = new HashMap<>();
    List<String> argumentNames = invocableWithNamedArguments.getArgumentNames();

    if (argumentNames == null) {

      /* Some functions such as min and max use un-named varags */
      if (this.positionalArgs != null && !this.positionalArgs.isEmpty()) {
        for (int i = 0; i < this.positionalArgs.size(); i++) {
          result.put(String.valueOf(i),
              this.positionalArgs.get(i).getValueExpression().evaluate(self, context));
        }
      }
    } else {

      if (this.positionalArgs != null) {
        int nameIndex = 0;

        for (PositionalArgumentNode arg: this.positionalArgs) {
          if (argumentNames.size() <= nameIndex) {
            throw new PebbleException(null, "The argument at position " + (nameIndex + 1)
                + " is not allowed. Only " + argumentNames.size() + " argument(s) are allowed.",
                this.lineNumber, self.getName());
          }

          result
              .put(argumentNames.get(nameIndex), arg.getValueExpression().evaluate(self, context));
          nameIndex++;
        }
      }

      if (this.namedArgs != null) {
        for (NamedArgumentNode arg: this.namedArgs) {
          // check if user used an incorrect name
          if (!argumentNames.contains(arg.getName())) {
            throw new PebbleException(null,
                "The following named argument does not exist: " + arg.getName(),
                this.lineNumber, self.getName());
          }
          Object value =
              arg.getValueExpression() == null ? null : arg.getValueExpression().evaluate(self,
                  context);
          result.put(arg.getName(), value);
        }
      }
    }

    return result;
  }

  @Override
  public String toString() {
    return this.positionalArgs.toString();
  }

}
