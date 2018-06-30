/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.utils;

/**
 * A small utility class used to pair relevant objects together.
 *
 * @author Mitchell
 */
public class Pair<L, R> {

  private final L left;

  private final R right;

  public Pair(L left, R right) {
    this.left = left;
    this.right = right;
  }

  public L getLeft() {
    return this.left;
  }

  public R getRight() {
    return this.right;
  }

  @Override
  public String toString() {
    return String.format("(%s, %s)", this.left, this.right);
  }

}
