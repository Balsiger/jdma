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

//------------------------------------------------------------------ imports

package net.ixitxachitls.input;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.output.commands.Color;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Divider;
import net.ixitxachitls.output.commands.Linebreak;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.errors.BaseError;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A parsing error.
 *
 * @file          ParseError.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class ParseError extends BaseError
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ ParseError ------------------------------

  /**
    * Create the error.
    *
    * @param       inError    the error number
    * @param       inMessage  an additional error text
    * @param       inLine     the line the error occurred
    * @param       inDocument the document the error occurred
    * @param       inPre      the text just before the error
    * @param       inPost     the text just after the error
    *
    */
  public ParseError(@Nonnull String inError, @Nullable String inMessage,
                    long inLine, @Nullable String inDocument,
                    @Nullable String inPre, @Nullable String inPost)
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

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The number or ID of the error. */
  private @Nonnull String m_errorNumber;

  /** The error message itself. */
  private @Nonnull String m_error;

  /** The individual message to this error. */
  private @Nullable String m_parseMessage;

  /** The line where the error occurred. */
  private long   m_line;

  /** The text just before the error. */
  private @Nullable String m_pre;

  /** The text just after the error. */
  private @Nullable String m_post;

  /** The name of the document in which the error occurred. */
  private @Nullable String m_document;

  /** The text to use for show cut text. */
  private static final @Nonnull String s_dots =
    Config.get("resource:parser/error.dots", "...");

  /** The marker to use to show the error itself. */
  private static final @Nonnull String s_mark =
    Config.get("resource:parser/error.mark", ">>>");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //---------------------------- getErrorNumber ----------------------------

  /**
   * Get the error number of this ID.
   *
   * @return      the requested error ID (as a string)
   *
   */
  public @Nonnull String getErrorNumber()
  {
    return m_errorNumber;
  }

  //........................................................................
  //------------------------------- getError -------------------------------

   /**
    * Get the error message.
    *
    * @return      the requested error message
    *
    */
  public @Nonnull String getError()
  {
    return m_error;
  }

  //........................................................................
  //---------------------------- getParseMessage ---------------------------

  /**
   * Get the individual error message text.
   *
   * @return      the requested individual text
   *
   */
  public @Nullable String getParseMessage()
  {
    return m_parseMessage;
  }

  //........................................................................
  //----------------------------- getDocument ------------------------------

  /**
   * Get the document where the error occurred.
   *
   * @return      the requested document
   *
   */
  public @Nullable String getDocument()
  {
    return m_document;
  }

  //........................................................................
  //------------------------------- getLine --------------------------------

  /**
   * Get the number of the line on which the error occurred..
   *
   * @return      the requested line number
   *
   */
  public long getLine()
  {
    return m_line;
  }

  //........................................................................
  //-------------------------------- getPre --------------------------------

  /**
   * Get the text before the error.
   *
   * @return      the requested text
   *
   */
  public @Nullable String getPre()
  {
    return m_pre;
  }

  //........................................................................
  //-------------------------------- getPost -------------------------------

  /**
   * Get the text just after the error.
   *
   * @return      the requested text
   *
   */
  public @Nullable String getPost()
  {
    return m_post;
  }

  //.......................................................................

  //-------------------------------- format --------------------------------

  /**
   * Format the error for printing.
   *
   * @return      a string or command representing the error
   *
   */
  public Object format()
  {
    return new Command(new Color("error", m_errorNumber + ": " + m_error
                                 + (m_parseMessage != null
                                    ? " (" + m_parseMessage + ")" : "")),
                       (m_line > 0 ? " on line " + m_line : ""),
                       (m_document != null ? " in document '" + m_document
                        + "'" : ""),
                       new Linebreak(),
                       (m_pre != null || m_post != null
                        ? new Divider("snippet",
                                      new Command((m_pre != null
                                                   ? s_dots + m_pre : ""),
                                                  new Color("error", s_mark),
                                                  (m_post != null
                                                   ? m_post + s_dots : "")))
                        : ""));
  }

  //........................................................................
  //------------------------------- toString -------------------------------

  /**
   * Create a String representation of the object, mainly for debugging
   * reasons, but also for printing the error to the screen.
   *
   * @return      the string representation
   *
   */
  public @Nonnull String toString()
  {
    return m_errorNumber + ": " + m_error
      + (m_parseMessage != null ? " (" + m_parseMessage + ")" : "")
      + (m_line > 0 ? " on line " + m_line : "")
      + (m_document != null ? " in document '" + m_document + "'" : "")
      + (m_pre != null || m_post != null ? "\n"
         + (m_pre != null ? s_dots + m_pre : "") + s_mark
         + (m_post != null ? m_post + s_dots : "") : "");
  }

  //.......................................................................
  //-------------------------------- equals --------------------------------

  /**
   * Check for equality of the given errors.
   *
   * @param       inOther the object to compare to
   *
   * @return      true if equal, false else
   *
   */
  public boolean equals(Object inOther)
  {
    if(inOther == null)
      return false;

    return toString().equals(inOther.toString());
  }

  //........................................................................
  //------------------------------- hashCode -------------------------------

  /**
   * Compute the hash code for this class.
   *
   * @return      the hash code
   *
   */
  public int hashCode()
  {
    return toString().hashCode();
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- creation -------------------------------------------------------

    /** Testing creation of errors. */
    @org.junit.Test
    public void creation()
    {
      ParseError error = new ParseError("id", "message", 42, "document",
                                        "pre", "post");

      assertEquals("name", "not yet defined!", error.getError());
      assertEquals("id", "id", error.getErrorNumber());
      assertEquals("message", "message", error.getParseMessage());
      assertEquals("line", 42, error.getLine());
      assertEquals("document", "document", error.getDocument());
      assertEquals("pre", "pre", error.getPre());
      assertEquals("post", "post", error.getPost());

      assertEquals("string",
                   "id: not yet defined! (message) on line 42 in document "
                   + "'document'\n"
                   + s_dots + "pre" + s_mark + "post" + s_dots,
                   error.toString());

      // minimal definition
      error = new ParseError("id", null, -1, null, null, null);

      assertEquals("minimal", "id: not yet defined!", error.toString());
    }

    //......................................................................
  }

  //........................................................................
}
