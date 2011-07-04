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

package net.ixitxachitls.output.html;

import java.io.PrintWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A writer to write json aoutput.
 *
 * @file          JsonWriter.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
public class JsonWriter
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ JsonWriter ------------------------------

  /**
   * Create the writer.
   *
   * @param       inWriter the writer to actually write to in the end
   *
   */
  public JsonWriter(@Nonnull PrintWriter inWriter)
  {
    m_writer = inWriter;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The writer to output to. */
  protected @Nonnull PrintWriter m_writer;

  /** The nesting level for arrays. */
  protected int m_nestingLevel = 0;

  /** If an array delimiter is necessary before adding text. */
  protected boolean m_needsDelimiter = false;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- strings --------------------------------

  /**
   * Add the given strings as an array to the output.
   *
   * @param       inStrings the strings to add
   *
   * @return      the writer for chaining
   *
   */
  public JsonWriter strings(@Nonnull Iterable<? extends Object> inStrings)
  {
    startArray();

    for(Object string : inStrings)
      if(string != null)
        string(string.toString()).next();

    endArray();

    return this;
  }

  //........................................................................
  //-------------------------------- string --------------------------------

  /**
   * Add some text as a Json string.
   *
   * @param       inString the text to add as a string.
   *
   * @return      the writer for chaining
   *
   */
  public JsonWriter string(@Nonnull String inString)
  {
    add("\"").add(inString.replace("\"", "\\\"")
                  .replace("\n", "\\\n").replace("\r", "\\\r")).add("\"");

    return this;
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the given text to the output.
   *
   * @param       inText the text to write
   *
   * @return      the writer for chaining
   *
   */
  public JsonWriter add(@Nonnull String inText)
  {
    if(m_needsDelimiter)
    {
      m_writer.println(", ");
      m_writer.print(Strings.spaces(m_nestingLevel));
      m_needsDelimiter = false;
    }

    m_writer.print(inText);

    return this;
  }

  //........................................................................
  //------------------------------ startArray ------------------------------

  /**
   * Start a Json array.
   *
   * @return      the writer for chaining
   *
   */
  public JsonWriter startArray()
  {
    m_writer.print(Strings.spaces(m_nestingLevel));
    m_writer.println("[");
    m_nestingLevel++;
    m_writer.print(Strings.spaces(m_nestingLevel));

    return this;
  }

  //........................................................................
  //------------------------------ startArray ------------------------------

  /**
   * End a Json array.
   *
   * @return      the writer for chaining
   *
   */
  public JsonWriter endArray()
  {
    m_needsDelimiter = false;
    m_nestingLevel--;
    m_writer.println("");
    m_writer.print(Strings.spaces(m_nestingLevel));
    m_writer.println("]");

    return this;
  }

  //........................................................................
  //--------------------------------- next ---------------------------------

  /**
   * Start the next array element or object value.
   *
   * @return      the writer for chaining
   *
   */
  public JsonWriter next()
  {
    m_needsDelimiter = true;

    return this;
  }

  //........................................................................
  //-------------------------------- close ---------------------------------

  /**
   * Close the writer.
   *
   */
  public void close()
  {
    if(m_nestingLevel > 0)
      Log.warning("writer closed, but arrays not closed");

    m_writer.close();
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
