package com.mitchellbosecke.pebble.template;

/**
 * Evaluation options.
 *
 * @author yanxiyue
 */
public class EvaluationOptions {

  /**
   * toggle to enable/disable getClass access
   */
  private boolean allowGetClass;

  /**
   * toggle to enable/disable greedy matching mode for finding java method
   */
  private boolean greedyMatchMethod;

  public boolean isAllowGetClass() {
    return allowGetClass;
  }

  public EvaluationOptions setAllowGetClass(boolean allowGetClass) {
    this.allowGetClass = allowGetClass;
    return this;
  }

  public boolean isGreedyMatchMethod() {
    return greedyMatchMethod;
  }

  public EvaluationOptions setGreedyMatchMethod(boolean greedyMatchMethod) {
    this.greedyMatchMethod = greedyMatchMethod;
    return this;
  }
}
