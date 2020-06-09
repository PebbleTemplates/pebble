package com.mitchellbosecke.pebble.lexer;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.regex.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SyntaxTest {

  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(SyntaxTest.class);

  private Syntax syntax;
  
  @BeforeEach
  void setup() {
    this.syntax = new Syntax.Builder().build();
  }

  @Test
  void tesDelimiter() {
    assertThat(this.syntax.getCommentOpenDelimiter()).isEqualTo("{#");
    
    assertThat(this.syntax.getCommentCloseDelimiter()).isEqualTo("#}");
    
    assertThat(this.syntax.getExecuteOpenDelimiter()).isEqualTo("{%");
    
    assertThat(this.syntax.getExecuteCloseDelimiter()).isEqualTo("%}");
    
    assertThat(this.syntax.getPrintOpenDelimiter()).isEqualTo("{{");
    
    assertThat(this.syntax.getPrintCloseDelimiter()).isEqualTo("}}");    
    
    assertThat(this.syntax.getInterpolationCloseDelimiter()).isEqualTo("}");
    
    assertThat(this.syntax.getInterpolationOpenDelimiter()).isEqualTo("#{");
    
    assertThat(this.syntax.getWhitespaceTrim()).isEqualTo("-");
  }
 
  @Test
  void tesCommentCloseDelimiter() {
    assertThat(this.syntax.getCommentCloseDelimiter()).isEqualTo("#}");
  }
  
  @Test
  void tesCommentOpenDelimiter() {
    assertThat(this.syntax.getCommentOpenDelimiter()).isEqualTo("{#");
  }
 
  @Test
  void testExecuteOpenDelimiter() {    
    assertThat(this.syntax.getExecuteOpenDelimiter()).isEqualTo("{%");
  }
  
  @Test
  void testExecuteCloseDelimiter() {    
    assertThat(this.syntax.getExecuteCloseDelimiter()).isEqualTo("%}");
  }
  
  @Test
  void testPrintOpenDelimiter() {    
    assertThat(this.syntax.getPrintCloseDelimiter()).isEqualTo("}}");    
  }
 
  @Test
  void testPrintCloseDelimiter() {    
    assertThat(this.syntax.getPrintCloseDelimiter()).isEqualTo("}}");    
  }

  @Test
  void testInterpolationCloseDelimiter() {    
    assertThat(this.syntax.getInterpolationCloseDelimiter()).isEqualTo("}");
  }

  @Test
  void testInterpolationOpenDelimiter() {    
    assertThat(this.syntax.getInterpolationOpenDelimiter()).isEqualTo("#{");
  }

  @Test
  void testWhitespaceTrim() {    
    assertThat(this.syntax.getWhitespaceTrim()).isEqualTo("-");
  }
  
  @Test
  void testTrailingWhiltespaceTrimRegex() {

    // Use regex101.com to test regular expressions through a web site
    
    /*
     ^ match only from start of the string or line
     \\ matches the character \ literally (case sensitive)
     \s* zero or more space characters
     \Q start quote
     \E end quote
    */
    String patternString = "^\\s*\\Q-\\E(\\Q}}\\E|\\Q%}\\E|\\Q#}\\E)";
      
    assertThat(this.syntax.getRegexTrailingWhitespaceTrim().toString()).isEqualTo(patternString);
    
    StringBuilder templateText = null;
    Matcher whitespaceTrimMatcher = null;
    
    // Whitespace Trim character with Execution Close Delimiter should match
    templateText = new StringBuilder().append("-%}");
    whitespaceTrimMatcher = syntax.getRegexTrailingWhitespaceTrim().matcher(templateText);
    assertThat(whitespaceTrimMatcher.lookingAt()).isEqualTo(true);
    
    // Whitespace Trim character with Print Close Delimiter should match
    templateText = new StringBuilder().append("-}}");
    whitespaceTrimMatcher = syntax.getRegexTrailingWhitespaceTrim().matcher(templateText);
    assertThat(whitespaceTrimMatcher.lookingAt()).isEqualTo(true);
    
    // leading space characters with Whitespace Trim character with Execution Close Delimiter should match
    templateText = new StringBuilder().append("     -%}");
    whitespaceTrimMatcher = syntax.getRegexTrailingWhitespaceTrim().matcher(templateText);
    assertThat(whitespaceTrimMatcher.lookingAt()).isEqualTo(true);
    
    // End of expression without Whitespace Trim character should not match
    templateText = new StringBuilder().append("%}");
    whitespaceTrimMatcher = syntax.getRegexTrailingWhitespaceTrim().matcher(templateText);
    assertThat(whitespaceTrimMatcher.lookingAt()).isEqualTo(false);
    
    // Leading non space characters should not match
    templateText = new StringBuilder().append("abcd   -%}");
    whitespaceTrimMatcher = syntax.getRegexTrailingWhitespaceTrim().matcher(templateText);
    assertThat(whitespaceTrimMatcher.lookingAt()).isEqualTo(false);
  }

  
}
