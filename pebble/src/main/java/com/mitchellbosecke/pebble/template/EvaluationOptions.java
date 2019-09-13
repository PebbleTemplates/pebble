package com.mitchellbosecke.pebble.template;

/**
 * Evaluation options.
 *
 * @author yanxiyue
 */
public class EvaluationOptions {

  /**
   * toggle to enable/disable unsafe methods access
   */
  private boolean allowUnsafeMethods;

  /**
   * toggle to enable/disable greedy matching mode for finding java method
   */
  private boolean greedyMatchMethod;

  public boolean isAllowUnsafeMethods() {
    return this.allowUnsafeMethods;
  }

  public EvaluationOptions setAllowUnsafeMethods(boolean allowUnsafeMethods) {
    this.allowUnsafeMethods = allowUnsafeMethods;
    return this;
  }

  public boolean isGreedyMatchMethod() {
    return this.greedyMatchMethod;
  }

  public EvaluationOptions setGreedyMatchMethod(boolean greedyMatchMethod) {
    this.greedyMatchMethod = greedyMatchMethod;
    return this;
  }
}
