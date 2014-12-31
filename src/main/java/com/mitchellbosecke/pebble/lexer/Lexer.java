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

    public TokenStream tokenize(Reader templateReader, String name) throws ParserException;

    /**
     * @return the commentOpenDelimiter
     */
    public String getCommentOpenDelimiter();

    /**
     * @param commentOpenDelimiter
     *            the commentOpenDelimiter to set
     */
    public void setCommentOpenDelimiter(String commentOpenDelimiter);

    /**
     * @return the commentCloseDelimiter
     */
    public String getCommentCloseDelimiter();

    /**
     * @param commentCloseDelimiter
     *            the commentCloseDelimiter to set
     */
    public void setCommentCloseDelimiter(String commentCloseDelimiter);

    /**
     * @return the executeOpenDelimiter
     */
    public String getExecuteOpenDelimiter();

    /**
     * @param executeOpenDelimiter
     *            the executeOpenDelimiter to set
     */
    public void setExecuteOpenDelimiter(String executeOpenDelimiter);

    /**
     * @return the executeCloseDelimiter
     */
    public String getExecuteCloseDelimiter();

    /**
     * @param executeCloseDelimiter
     *            the executeCloseDelimiter to set
     */
    public void setExecuteCloseDelimiter(String executeCloseDelimiter);

    /**
     * @return the printOpenDelimiter
     */
    public String getPrintOpenDelimiter();

    /**
     * @param printOpenDelimiter
     *            the printOpenDelimiter to set
     */
    public void setPrintOpenDelimiter(String printOpenDelimiter);

    /**
     * @return the printCloseDelimiter
     */
    public String getPrintCloseDelimiter();

    /**
     * @param printCloseDelimiter
     *            the printCloseDelimiter to set
     */
    public void setPrintCloseDelimiter(String printCloseDelimiter);
}
