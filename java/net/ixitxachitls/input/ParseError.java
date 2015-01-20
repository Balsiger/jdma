/******************************************************************************
 * Copyright (c) 2002-2011 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
 * All rights reserved
 *
 * This file is part of Dungeon Master Assistant.
 *
 * Dungeon Master Assistant is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Dungeon Master Assistant is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dungeon Master Assistant; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *****************************************************************************/

package net.ixitxachitls.input;

import com.google.common.base.Optional;

import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.errors.BaseError;

/**
 * A parsing error.
 *
 * @file          ParseError.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class ParseError extends BaseError
{
  /**
    * Create the error.
    *
    * @param       inError    the error number
    * @param       inMessage  an additional error text
    * @param       inLine     the line the error occurred
    * @param       inDocument the document the error occurred
    * @param       inPre      the text just before the error
    * @param       inPost     the text just after the error
    */
  public ParseError(String inError, Optional<String> inMessage,
                    long inLine, Optional<String> inDocument,
                    Optional<String> inPre, Optional<String> inPost)
  {
    super(inError);

    m_errorNumber = inError;
    m_error       = Config.get("resource:errors/" + m_errorNumber,
                               "not yet defined!");
    m_parseMessage = inMessage;
    m_line         = inLine;
    m_pre          = inPre;
    m_post         = inPost;
    m_document     = inDocument;

    m_message = toString();
  }

  /** Default serial version id. */
  private static final long serialVersionUID = 1L;

  /** The number or ID of the error. */
  private String m_errorNumber;

  /** The error message itself. */
  private String m_error;

  /** The individual message to this error. */
  private Optional<String> m_parseMessage;

  /** The line where the error occurred. */
  private long   m_line;

  /** The text just before the error. */
  private Optional<String> m_pre;

  /** The text just after the error. */
  private Optional<String> m_post;

  /** The name of the document in which the error occurred. */
  private Optional<String> m_document;

  /** The text to use for show cut text. */
  private static final String s_dots =
    Config.get("resource:parser/error.dots", "...");

  /** The marker to use to show the error itself. */
  private static final String s_mark =
    Config.get("resource:parser/error.mark", ">>>");

  /**
   * Get the error number of this ID.
   *
   * @return      the requested error ID (as a string)
   */
  public String getErrorNumber()
  {
    return m_errorNumber;
  }

   /**
    * Get the error message.
    *
    * @return      the requested error message
    *
    */
  public String getError()
  {
    return m_error;
  }

  /**
   * Get the individual error message text.
   *
   * @return      the requested individual text
   */
  public Optional<String> getParseMessage()
  {
    return m_parseMessage;
  }

  /**
   * Get the document where the error occurred.
   *
   * @return      the requested document
   */
  public Optional<String> getDocument()
  {
    return m_document;
  }

  /**
   * Get the number of the line on which the error occurred..
   *
   * @return      the requested line number
   */
  public long getLine()
  {
    return m_line;
  }

  /**
   * Get the text before the error.
   *
   * @return      the requested text
   *
   */
  public Optional<String> getPre()
  {
    return m_pre;
  }

  /**
   * Get the text just after the error.
   *
   * @return      the requested text
   */
  public Optional<String> getPost()
  {
    return m_post;
  }

  @Override
  public String toString()
  {
    return m_errorNumber + ": " + m_error
      + (m_parseMessage != null ? " (" + m_parseMessage + ")" : "")
      + (m_line > 0 ? " on line " + m_line : "")
      + (m_document != null ? " in document '" + m_document + "'" : "")
      + (m_pre != null || m_post != null ? "\n"
         + (m_pre != null ? s_dots + m_pre : "") + s_mark
         + (m_post != null ? m_post + s_dots : "") : "");
  }

  @Override
  public boolean equals(Object inOther)
  {
    if(inOther == this)
      return true;

    if(!(inOther instanceof ParseError))
      return false;

    return toString().equals(((ParseError)inOther).toString());
  }

  @Override
  public int hashCode()
  {
    return toString().hashCode();
  }

  //----------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Testing creation of errors. */
    @org.junit.Test
    public void creation()
    {
      ParseError error = new ParseError("id", Optional.of("message"), 42,
                                        Optional.of("document"),
                                        Optional.of("pre"),
                                        Optional.of("post"));

      // TODO: fix this (should be real error messages!)
      assertEquals("name", "[id] no definition found for this error",
                   error.getError());
      assertEquals("id", "id", error.getErrorNumber());
      assertEquals("message", "message", error.getParseMessage());
      assertEquals("line", 42, error.getLine());
      assertEquals("document", "document", error.getDocument());
      assertEquals("pre", "pre", error.getPre());
      assertEquals("post", "post", error.getPost());

      assertEquals("string",
                   "id: [id] no definition found for this error (message) "
                   + "on line 42 in document "
                   + "'document'\n"
                   + s_dots + "pre" + s_mark + "post" + s_dots,
                   error.toString());

      // minimal definition
      error = new ParseError("id", null, -1, null, null, null);

      assertEquals("minimal", "id: [id] no definition found for this error",
                   error.toString());
    }
  }
}
