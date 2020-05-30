package com.mitchellbosecke.pebble.boot.autoconfigure;

abstract class AbstractPebbleConfiguration {

  protected String stripLeadingSlash(String value) {
    if (value == null) {
      return null;
    }
    if (value.startsWith("/")) {
      return value.substring(1);
    }
    return value;
  }
}
