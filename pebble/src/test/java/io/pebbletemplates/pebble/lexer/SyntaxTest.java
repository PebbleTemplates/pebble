package io.pebbletemplates.pebble.lexer;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// NOTE: Use regex101.com to test regular expressions through a web site

class SyntaxTest {

  private static final String POSSIBLE_NEW_LINE = "(\r\n|\n\r|\r|\n|\u0085|\u2028|\u2029)?";
  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(SyntaxTest.class);

  private Syntax syntax;
  
  @BeforeEach
  void setup() {
    this.syntax = new Syntax.Builder().build();
  }

  @Test
  void testDelimiters() {
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
  void testTrailingWhitespaceTrimRegex() {

	Pattern pattern = syntax.getRegexTrailingWhitespaceTrim();
    
	/*
	  matching from start of string, zero of more space characters, followed by a dash,
	  followed by one of the following: "}}", "%}", or "#}"
	 */
    String expectedPatternString = "^\\s*\\Q-\\E(\\Q}}\\E|\\Q%}\\E|\\Q#}\\E)";
    // verify that the TrailingWhitepaceTrim regex in the Syntax class is the expected pattern string
    assertThat(pattern.toString()).isEqualTo(expectedPatternString);
    
    StringBuilder templateText = null;
    Matcher whitespaceTrimMatcher = null;
    
    // Whitespace Trim character with Execution Close Delimiter should match
    templateText = new StringBuilder().append("-%}");
    whitespaceTrimMatcher = pattern.matcher(templateText);
    assertThat(whitespaceTrimMatcher.lookingAt()).isEqualTo(true);
    
    // Whitespace Trim character with Print Close Delimiter should match
    templateText = new StringBuilder().append("-}}");
    whitespaceTrimMatcher = pattern.matcher(templateText);
    assertThat(whitespaceTrimMatcher.lookingAt()).isEqualTo(true);
    
    // leading space characters with Whitespace Trim character with Execution Close Delimiter should match
    templateText = new StringBuilder().append("     -%}");
    whitespaceTrimMatcher = pattern.matcher(templateText);
    assertThat(whitespaceTrimMatcher.lookingAt()).isEqualTo(true);
    
    // Whitespace Trim character with Comment Close Delimiter should match
    templateText = new StringBuilder().append("-#}");
    whitespaceTrimMatcher = pattern.matcher(templateText);
    assertThat(whitespaceTrimMatcher.lookingAt()).isEqualTo(true); 
    
    // End of expression without Whitespace Trim character should not match
    templateText = new StringBuilder().append("%}");
    whitespaceTrimMatcher = pattern.matcher(templateText);
    assertThat(whitespaceTrimMatcher.lookingAt()).isEqualTo(false);
    
    // Leading non space characters should not match
    templateText = new StringBuilder().append("abcd   -%}");
    whitespaceTrimMatcher = pattern.matcher(templateText);
    assertThat(whitespaceTrimMatcher.lookingAt()).isEqualTo(false); 
  }
 
  @Test
  void testLeadingWhitespaceTrimRegex() {
	    
	Pattern pattern = syntax.getRegexLeadingWhitespaceTrim();

	/*
	 "-" followed by one more whitespace characters
	 */
    String expectedPatternString = "\\Q-\\E\\s+";
    // verify that the regex in the Syntax class is the expected pattern string
    assertThat(pattern.toString()).isEqualTo(expectedPatternString);
    
    StringBuilder templateText = null;
    Matcher matcher = null;
    
    // Dash followed by spaces should match
    templateText = new StringBuilder().append("- 	");
    matcher = pattern.matcher(templateText);
    assertThat(matcher.lookingAt()).isEqualTo(true);
    
    // Dash character followed by whitespace characters should match
    templateText = new StringBuilder().append("- \n\r\t");
    matcher = pattern.matcher(templateText);
    assertThat(matcher.lookingAt()).isEqualTo(true);
    
    // Trailing non-whitespace after whitespace characters should match
    templateText = new StringBuilder().append("- abcd");
    matcher = pattern.matcher(templateText);
    assertThat(matcher.lookingAt()).isEqualTo(true);
    
    // No leading dash character should not match
    templateText = new StringBuilder().append("     ");
    matcher = pattern.matcher(templateText);
    assertThat(matcher.lookingAt()).isEqualTo(false);
    
    // No trailing space character should not match
    templateText = new StringBuilder().append("-");
    matcher = pattern.matcher(templateText);
    assertThat(matcher.lookingAt()).isEqualTo(false);
    
    // Leading characters should not match
    templateText = new StringBuilder().append(" abcd - 	}");
    matcher = pattern.matcher(templateText);
    assertThat(matcher.lookingAt()).isEqualTo(false);
  }
  
  @Test
  void testRegexVerbatimEnd() {
	    
	Pattern pattern = this.syntax.getRegexVerbatimEnd();

	/*  
	  "{%" followed by an optional "-" and/or zero or more whitespace characters, followed by "endverbatim",
	  followed by zero or more whitespace characters and/or an optional "-" followed by "%}" 
	  followed by an optional possible new line character
	 */ 
	String expectedPatternString = "\\Q{%\\E(\\Q-\\E)?\\s*endverbatim\\s*(\\Q-\\E)?\\Q%}\\E" + POSSIBLE_NEW_LINE;
    // verify that the regex in the Syntax class is the expected pattern string
    assertThat(pattern.toString()).isEqualTo(expectedPatternString);	  
    
    StringBuilder templateText = null;
    Matcher matcher = null;
    
    // Space and whitespacetrim characters should match
    templateText = new StringBuilder().append("{%- endverbatim -%}abcd\r\n");
	matcher = pattern.matcher(templateText);
    assertThat(matcher.lookingAt()).isEqualTo(true);
    
    // No spaces or whitespacetrim characters should match
    templateText = new StringBuilder().append("{%endverbatim%}");
	matcher = pattern.matcher(templateText);
    assertThat(matcher.lookingAt()).isEqualTo(true);
    
    // Missing delimiters should not match
    templateText = new StringBuilder().append("endverbatim");
	matcher = pattern.matcher(templateText);
    assertThat(matcher.lookingAt()).isEqualTo(false);
  }

  @Test
  void testRegexStartDelimiters() {
	  
	Pattern pattern = this.syntax.getRegexStartDelimiters();
	  
	/*
	 Start delimiters must match "{{" or "{%" or "{#"
	 */
	String expectedPatternString = "\\Q{{\\E|\\Q{%\\E|\\Q{#\\E"; 
	
    // verify that the regex in the Syntax class is the expected pattern string
    assertThat(pattern.toString()).isEqualTo(expectedPatternString);	  
    
    StringBuilder templateText = null;
    Matcher matcher = null;
    
    // "{{", "{%", and "{#" should match
    templateText = new StringBuilder().append("{{");
	matcher = pattern.matcher(templateText);
    assertThat(matcher.lookingAt()).isEqualTo(true);  
    
    templateText = new StringBuilder().append("{%");
  	matcher = pattern.matcher(templateText);
      assertThat(matcher.lookingAt()).isEqualTo(true);
      
    templateText = new StringBuilder().append("{#");
  	matcher = pattern.matcher(templateText);
      assertThat(matcher.lookingAt()).isEqualTo(true);
      
    // Text after a start delimiter should match
    templateText = new StringBuilder().append("{{  abcd");
	matcher = pattern.matcher(templateText);
    assertThat(matcher.lookingAt()).isEqualTo(true);
    
    // Text before the start delimiter should not match
    templateText = new StringBuilder().append("abcd  {{");
	matcher = pattern.matcher(templateText);
    assertThat(matcher.lookingAt()).isEqualTo(false);
    
    // No start delimiter should not match
    templateText = new StringBuilder().append("abcd");
	matcher = pattern.matcher(templateText);
    assertThat(matcher.lookingAt()).isEqualTo(false);
  }
  
}

