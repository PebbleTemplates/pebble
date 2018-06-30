/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.error;

public class PebbleException extends RuntimeException {

  private static final long serialVersionUID = -2855774187093732189L;

  protected final Integer lineNumber;

  protected final String filename;

  protected final String message;

  public PebbleException(Throwable cause, String message) {
    this(cause, message, null, null);
  }

  public PebbleException(Throwable cause, String message, Integer lineNumber, String filename) {
    super(String.format("%s (%s:%s)", message, filename == null ? "?" : filename,
        lineNumber == null ? "?" : String.valueOf(lineNumber)), cause);
    this.message = message;
    this.lineNumber = lineNumber;
    this.filename = filename;
  }

  /**
   * Returns the line number on which the exception was thrown.
   *
   * @return the line number on which the exception was thrown.
   */
  public Integer getLineNumber() {
    return this.lineNumber;
  }

  /**
   * Returns the filename in which the exception was thrown.
   *
   * @return the filename in which the exception was thrown.
   */
  public String getFileName() {
    return this.filename;
  }

  /**
   * Returns the message which is set for the exception by Pebble. Its the message which is not
   * enhanced with the line number and filename.
   *
   * @return the message which is set for the exception by Pebble.
   */
  public String getPebbleMessage() {
    return this.message;
  }

}
