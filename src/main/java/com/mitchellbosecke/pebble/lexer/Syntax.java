package com.mitchellbosecke.pebble.lexer;

import java.util.regex.Pattern;

/**
 * The syntax describes the different syntax parts of the Pebble language.
 *
 * <p>
 * This object is immutable after the creation. This is to make sure the syntax
 * cannot be changed during the execution.
 */
public final class Syntax {

    private final String delimiterCommentOpen;

    private final String delimiterCommentClose;

    private final String delimiterExecuteOpen;

    private final String delimiterExecuteClose;

    private final String delimiterPrintOpen;

    private final String delimiterPrintClose;

    private final String whitespaceTrim;

    /**
     * The regular expressions used to find the different delimiters
     */
    private final Pattern regexPrintClose;

    private final Pattern regexExecuteClose;

    private final Pattern regexCommentClose;

    private final Pattern regexStartDelimiters;

    private final Pattern regexLeadingWhitespaceTrim;

    private final Pattern regexTrailingWhitespaceTrim;

    /**
     * Regular expressions used to find "verbatim" and "endverbatim" tags.
     */
    private final Pattern regexVerbatimStart;

    private final Pattern regexVerbatimEnd;

    private static final String POSSIBLE_NEW_LINE = "(\r\n|\n\r|\r|\n|\u0085|\u2028|\u2029)?";

    public Syntax(final String delimiterCommentOpen, final String delimiterCommentClose,
            final String delimiterExecuteOpen, final String delimiterExecuteClose, final String delimiterPrintOpen,
            final String delimiterPrintClose, final String whitespaceTrim, final boolean enableNewLineTrimming) {
        this.delimiterCommentClose = delimiterCommentClose;
        this.delimiterCommentOpen = delimiterCommentOpen;
        this.delimiterExecuteOpen = delimiterExecuteOpen;
        this.delimiterExecuteClose = delimiterExecuteClose;
        this.delimiterPrintOpen = delimiterPrintOpen;
        this.delimiterPrintClose = delimiterPrintClose;
        this.whitespaceTrim = whitespaceTrim;
        
        // Do we trim the newline following a tag?
        String newlineRegexSuffix = enableNewLineTrimming ? POSSIBLE_NEW_LINE : "";

        // regexes used to find the individual delimiters
        this.regexPrintClose = Pattern.compile("^\\s*" + Pattern.quote(whitespaceTrim) + "?"
                + Pattern.quote(delimiterPrintClose) + newlineRegexSuffix);
        
        this.regexExecuteClose = Pattern.compile("^\\s*" + Pattern.quote(whitespaceTrim) + "?"
                + Pattern.quote(delimiterExecuteClose) + newlineRegexSuffix);
        this.regexCommentClose = Pattern.compile(Pattern.quote(delimiterCommentClose) + newlineRegexSuffix);

        // combination regex used to find the next START delimiter of any kind
        this.regexStartDelimiters = Pattern.compile(Pattern.quote(delimiterPrintOpen) + "|"
                + Pattern.quote(delimiterExecuteOpen) + "|" + Pattern.quote(delimiterCommentOpen));

        // regex to find the verbatim tag
        this.regexVerbatimStart = Pattern.compile("^\\s*verbatim\\s*(" + Pattern.quote(whitespaceTrim) + ")?"
                + Pattern.quote(delimiterExecuteClose) + newlineRegexSuffix);
        this.regexVerbatimEnd = Pattern.compile(Pattern.quote(delimiterExecuteOpen) + "("
                + Pattern.quote(whitespaceTrim) + ")?" + "\\s*endverbatim\\s*(" + Pattern.quote(whitespaceTrim) + ")?"
                + Pattern.quote(delimiterExecuteClose) + newlineRegexSuffix);

        // regex for the whitespace trim character
        this.regexLeadingWhitespaceTrim = Pattern.compile(Pattern.quote(whitespaceTrim) + "\\s+");
        this.regexTrailingWhitespaceTrim = Pattern.compile("^\\s*" + Pattern.quote(whitespaceTrim) + "("
                + Pattern.quote(delimiterPrintClose) + "|" + Pattern.quote(delimiterExecuteClose) + "|"
                + Pattern.quote(delimiterCommentClose) + ")");

    }

    /**
     * @return the commentOpenDelimiter
     */
    public String getCommentOpenDelimiter() {
        return delimiterCommentOpen;
    }

    /**
     * @return the commentCloseDelimiter
     */
    public String getCommentCloseDelimiter() {
        return delimiterCommentClose;
    }

    /**
     * @return the executeOpenDelimiter
     */
    public String getExecuteOpenDelimiter() {
        return delimiterExecuteOpen;
    }

    /**
     * @return the executeCloseDelimiter
     */
    public String getExecuteCloseDelimiter() {
        return delimiterExecuteClose;
    }

    /**
     * @return the printOpenDelimiter
     */
    public String getPrintOpenDelimiter() {
        return delimiterPrintOpen;
    }

    /**
     * @return the printCloseDelimiter
     */
    public String getPrintCloseDelimiter() {
        return delimiterPrintClose;
    }

    public String getWhitespaceTrim() {
        return whitespaceTrim;
    }

    Pattern getRegexPrintClose() {
        return regexPrintClose;
    }

    Pattern getRegexExecuteClose() {
        return regexExecuteClose;
    }

    Pattern getRegexCommentClose() {
        return regexCommentClose;
    }

    Pattern getRegexStartDelimiters() {
        return regexStartDelimiters;
    }

    Pattern getRegexLeadingWhitespaceTrim() {
        return regexLeadingWhitespaceTrim;
    }

    Pattern getRegexTrailingWhitespaceTrim() {
        return regexTrailingWhitespaceTrim;
    }

    Pattern getRegexVerbatimEnd() {
        return regexVerbatimEnd;
    }

    Pattern getRegexVerbatimStart() {
        return regexVerbatimStart;
    }

    /**
     * Helper class to create new instances of {@link Syntax}.
     */
    public static class Builder {

        private String delimiterCommentOpen = "{#";

        private String delimiterCommentClose = "#}";

        private String delimiterExecuteOpen = "{%";

        private String delimiterExecuteClose = "%}";

        private String delimiterPrintOpen = "{{";

        private String delimiterPrintClose = "}}";

        private String whitespaceTrim = "-";
        
        private boolean enableNewLineTrimming = true;

        /**
         * @return the commentOpenDelimiter
         */
        public String getCommentOpenDelimiter() {
            return delimiterCommentOpen;
        }

        /**
         * @param commentOpenDelimiter
         *            the commentOpenDelimiter to set
         */
        public void setCommentOpenDelimiter(String commentOpenDelimiter) {
            this.delimiterCommentOpen = commentOpenDelimiter;
        }

        /**
         * @return the commentCloseDelimiter
         */
        public String getCommentCloseDelimiter() {
            return delimiterCommentClose;
        }

        /**
         * @param commentCloseDelimiter
         *            the commentCloseDelimiter to set
         */
        public void setCommentCloseDelimiter(String commentCloseDelimiter) {
            this.delimiterCommentClose = commentCloseDelimiter;
        }

        /**
         * @return the executeOpenDelimiter
         */
        public String getExecuteOpenDelimiter() {
            return delimiterExecuteOpen;
        }

        /**
         * @param executeOpenDelimiter
         *            the executeOpenDelimiter to set
         */
        public void setExecuteOpenDelimiter(String executeOpenDelimiter) {
            this.delimiterExecuteOpen = executeOpenDelimiter;
        }

        /**
         * @return the executeCloseDelimiter
         */
        public String getExecuteCloseDelimiter() {
            return delimiterExecuteClose;
        }

        /**
         * @param executeCloseDelimiter
         *            the executeCloseDelimiter to set
         */
        public void setExecuteCloseDelimiter(String executeCloseDelimiter) {
            this.delimiterExecuteClose = executeCloseDelimiter;
        }

        /**
         * @return the printOpenDelimiter
         */
        public String getPrintOpenDelimiter() {
            return delimiterPrintOpen;
        }

        /**
         * @param printOpenDelimiter
         *            the printOpenDelimiter to set
         */
        public void setPrintOpenDelimiter(String printOpenDelimiter) {
            this.delimiterPrintOpen = printOpenDelimiter;
        }

        /**
         * @return the printCloseDelimiter
         */
        public String getPrintCloseDelimiter() {
            return delimiterPrintClose;
        }

        /**
         * @param printCloseDelimiter
         *            the printCloseDelimiter to set
         */
        public void setPrintCloseDelimiter(String printCloseDelimiter) {
            this.delimiterPrintClose = printCloseDelimiter;
        }

        public String getWhitespaceTrim() {
            return whitespaceTrim;
        }

        public void setWhitespaceTrim(String whitespaceTrim) {
            this.whitespaceTrim = whitespaceTrim;
        }
        
        public boolean isEnableNewLineTrimming() {
            return enableNewLineTrimming;
        }
        
        public Builder setEnableNewLineTrimming(boolean enableNewLineTrimming) {
            this.enableNewLineTrimming = enableNewLineTrimming;
            return this;
        }

        public Syntax build() {
            return new Syntax(delimiterCommentOpen, delimiterCommentClose, delimiterExecuteOpen, delimiterExecuteClose,
                    delimiterPrintOpen, delimiterPrintClose, whitespaceTrim, enableNewLineTrimming);
        }
    }

}
