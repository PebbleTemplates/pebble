package com.mitchellbosecke.pebble.lexer;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SyntaxTest {

  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(SyntaxTest.class);

  private Syntax syntax;

  private String delimiterCommentOpen = "{#";

  private String delimiterCommentClose = "#}";

  private String delimiterExecuteOpen = "{%";

  private String delimiterExecuteClose = "%}";

  private String delimiterPrintOpen = "{{";

  private String delimiterPrintClose = "}}";

  private String delimiterInterpolationOpen = "#{";

  private String delimiterInterpolationClose = "}";

  private String whitespaceTrim = "-";
  
  @BeforeEach
  void setup() {
    this.syntax = new Syntax.Builder().build();
  }
  
  @Test
  void testRegexTrailingWhiltespaceTrim() {

    StringBuilder templateText = new StringBuilder().append("-%}");
    
    Matcher whitespaceTrimMatcher = syntax.getRegexTrailingWhitespaceTrim().matcher(templateText);

    assertThat(whitespaceTrimMatcher.lookingAt()).isEqualTo(true);

  }
  
  @Test
  void testTrailingWhiltespaceTrimPatternString() {
    
    String patternString = "^\\s*\\Q-\\E(\\Q}}\\E|\\Q%}\\E|\\Q#}\\E)";
       
    System.out.println(patternString);
      
    assertThat(this.syntax.getRegexTrailingWhitespaceTrim().toString()).isEqualTo(patternString);
  }
  
  
}
