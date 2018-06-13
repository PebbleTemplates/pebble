package com.mitchellbosecke.pebble.extension.escaper;

public interface EscapingStrategy {

  String escape(String input);

}
