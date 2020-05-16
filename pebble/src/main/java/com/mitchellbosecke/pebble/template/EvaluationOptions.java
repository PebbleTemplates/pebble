package com.mitchellbosecke.pebble.template;

import com.mitchellbosecke.pebble.attributes.methodaccess.MethodAccessValidator;

/**
 * Evaluation options.
 *
 * @author yanxiyue
 */
public class EvaluationOptions {
  /**
   * toggle to enable/disable greedy matching mode for finding java method
   */
  private boolean greedyMatchMethod;

  /**
   * Validator that can be used to validate object/method access
   */
  private MethodAccessValidator methodAccessValidator;

  public boolean isGreedyMatchMethod() {
    return this.greedyMatchMethod;
  }

  public void setGreedyMatchMethod(boolean greedyMatchMethod) {
    this.greedyMatchMethod = greedyMatchMethod;
  }

  public MethodAccessValidator getMethodAccessValidator() {
    return this.methodAccessValidator;
  }

  public void setMethodAccessValidator(
      MethodAccessValidator methodAccessValidator) {
    this.methodAccessValidator = methodAccessValidator;
  }
}
