package io.pebbletemplates.pebble.template;

import io.pebbletemplates.pebble.attributes.methodaccess.MethodAccessValidator;

/**
 * Evaluation options.
 *
 * @author yanxiyue
 */
public class EvaluationOptions {

  /**
   * toggle to enable/disable greedy matching mode for finding java method
   */
  private final boolean greedyMatchMethod;

  /**
   * Validator that can be used to validate object/method access
   */
  private final MethodAccessValidator methodAccessValidator;

  public EvaluationOptions(boolean greedyMatchMethod,
      MethodAccessValidator methodAccessValidator) {
    this.greedyMatchMethod = greedyMatchMethod;
    this.methodAccessValidator = methodAccessValidator;
  }

  public boolean isGreedyMatchMethod() {
    return this.greedyMatchMethod;
  }

  public MethodAccessValidator getMethodAccessValidator() {
    return this.methodAccessValidator;
  }
}
