/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.lexer;

import java.io.Reader;

import com.mitchellbosecke.pebble.error.ParserException;

public interface Lexer {

    TokenStream tokenize(Reader templateReader, String name) throws ParserException;

    /**
     * @return the commentOpenDelimiter
     */
    String getCommentOpenDelimiter();

    /**
     * @param commentOpenDelimiter
     *            the commentOpenDelimiter to set
     */
    void setCommentOpenDelimiter(String commentOpenDelimiter);

    /**
     * @return the commentCloseDelimiter
     */
    String getCommentCloseDelimiter();

    /**
     * @param commentCloseDelimiter
     *            the commentCloseDelimiter to set
     */
    void setCommentCloseDelimiter(String commentCloseDelimiter);

    /**
     * @return the executeOpenDelimiter
     */
    String getExecuteOpenDelimiter();

    /**
     * @param executeOpenDelimiter
     *            the executeOpenDelimiter to set
     */
    void setExecuteOpenDelimiter(String executeOpenDelimiter);

    /**
     * @return the executeCloseDelimiter
     */
    String getExecuteCloseDelimiter();

    /**
     * @param executeCloseDelimiter
     *            the executeCloseDelimiter to set
     */
    void setExecuteCloseDelimiter(String executeCloseDelimiter);

    /**
     * @return the printOpenDelimiter
     */
    String getPrintOpenDelimiter();

    /**
     * @param printOpenDelimiter
     *            the printOpenDelimiter to set
     */
    void setPrintOpenDelimiter(String printOpenDelimiter);

    /**
     * @return the printCloseDelimiter
     */
    String getPrintCloseDelimiter();

    /**
     * @param printCloseDelimiter
     *            the printCloseDelimiter to set
     */
    void setPrintCloseDelimiter(String printCloseDelimiter);
}
