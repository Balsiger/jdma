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
// $codepro.audit.disable closeInFinally

//------------------------------------------------------------------ imports

package net.ixitxachitls.output.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;
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
@ParametersAreNonnullByDefault
public class JsonWriter implements AutoCloseable
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ JsonWriter ------------------------------

  /**
   * Create the writer.
   *
   * @param       inWriter the writer to actually write to in the end
   *
   */
  public JsonWriter(PrintWriter inWriter)
  {
    m_writer = inWriter;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The writer to output to. */
  protected PrintWriter m_writer;

  /** The nesting level for arrays. */
  protected int m_nestingLevel = 0;

  /** If an array delimiter is necessary before adding text. */
  protected boolean m_needsDelimiter = false;

  /** If a newline is next to print. */
  protected boolean m_needsNewline = false;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //........................................................................

  //----------------------------------------------------------- manipulators

  @Override
  public String toString()
  {
    return "JsonWriter";
  }

  //------------------------------- strings --------------------------------

  /**
   * Add the given strings as an array to the output.
   *
   * @param       inStrings the strings to add
   *
   * @return      the writer for chaining
   *
   */
  public JsonWriter strings(Iterable<? extends Object> inStrings)
  {
    startArray();

    for(Object string : inStrings)
      if(string != null)
        string(string.toString()).next();

    endArray();

    return this;
  }

  //........................................................................
  //------------------------------- strings --------------------------------

  /**
   * Add the given strings as an array to the output.
   *
   * @param       inStrings the strings to add
   *
   * @return      the writer for chaining
   *
   */
  public JsonWriter strings(Map<? extends Object, ? extends Object> inStrings)
  {
    startArray();

    for(Map.Entry<? extends Object, ? extends Object> entry
          : inStrings.entrySet())
      startObject()
        .add("\"label\": ").string(entry.getKey().toString()).next()
        .add("\"value\": ").string(entry.getKey().toString())
        .endObject().next();

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
  public JsonWriter string(String inString)
  {
    add("\"").add(inString.replace("\"", "\\\"").replace("\\", "\\\\")
                  .replace("\n", "\\n").replace("\r", "\\r")).add("\"");

    return this;
  }

  //........................................................................
  //-------------------------------- value --------------------------------

  /**
   * Add some JSON object value.
   *
   * @param       inKey   the key
   * @param       inValue the value
   *
   * @return      the writer for chaining
   *
   */
  public JsonWriter value(String inKey, String inValue)
  {
    add(inKey).add(": ").add(inValue);

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
  public JsonWriter add(String inText)
  {
    if(m_needsDelimiter)
    {
      m_needsDelimiter = false;
      m_writer.print(',');
      newline();
    }

    if(m_needsNewline)
    {
      m_needsNewline = false;
      m_writer.println("");
      m_writer.print(Strings.spaces(m_nestingLevel * 2));
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
    add("[");
    m_nestingLevel++;
    newline();

    return this;
  }

  //........................................................................
  //------------------------------- endArray -------------------------------

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
    newline().add("]").newline();

    return this;
  }

  //........................................................................
  //------------------------------ startObject -----------------------------

  /**
   * Start a Json object.
   *
   * @return      the writer for chaining
   *
   */
  public JsonWriter startObject()
  {
    add("{");
    m_nestingLevel++;
    newline();

    return this;
  }

  //........................................................................
  //------------------------------- endObject ------------------------------

  /**
   * End a Json object.
   *
   * @return      the writer for chaining
   *
   */
  public JsonWriter endObject()
  {
    m_needsDelimiter = false;
    m_nestingLevel--;
    newline().add("}").newline();

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
  //-------------------------------- newline -------------------------------

  /**
   * Start a new line with the proper indent.
   *
   * @return      the writer for chaining
   *
   */
  public JsonWriter newline()
  {
    m_needsNewline = true;

    return this;
  }

  //........................................................................
  //-------------------------------- close ---------------------------------

  /**
   * Close the writer.
   *
   */
  @Override
  public void close()
  {
    if(m_nestingLevel > 0)
      Log.warning("writer closed, but arrays not closed");

    if(m_needsNewline)
      m_writer.println("");

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
    //----- empty ----------------------------------------------------------

    /**
     * The empty Test.
     *
     * @throws IOException when closing contents writer
     */
    @org.junit.Test
    public void empty() throws IOException
    {
      try (java.io.StringWriter contents = new java.io.StringWriter();
        JsonWriter writer = new JsonWriter(new PrintWriter(contents)))
      {
        assertEquals("contents", "", contents.toString());
      }
    }

    //......................................................................
    //----- add ------------------------------------------------------------

    /** The empty Test.
     *
     * @throws IOException when closing contents writer
     */
    @org.junit.Test
    public void add() throws IOException
    {
      try (java.io.StringWriter contents = new java.io.StringWriter();
        JsonWriter writer = new JsonWriter(new PrintWriter(contents)))
      {
        writer.add("some\ntext");

        assertEquals("contents", "some\ntext", contents.toString());
      }
    }

    //......................................................................
    //----- string ---------------------------------------------------------

    /**
     * The empty Test.
     * @throws IOException when closing contents writer
     */
    @org.junit.Test
    public void string() throws IOException
    {
      try (java.io.StringWriter contents = new java.io.StringWriter();
        JsonWriter writer = new JsonWriter(new PrintWriter(contents)))
      {
        writer.string("just 'a' \"test\" string\n!");

        assertEquals("contents", "\"just 'a' \\\\\"test\\\\\" string\\n!\"",
                     contents.toString());
      }
    }

    //......................................................................
    //----- value ----------------------------------------------------------

    /**
     * The empty Test.
     *
     * @throws IOException when closing contents writer
     */
    @org.junit.Test
    public void value() throws IOException
    {
      try (java.io.StringWriter contents = new java.io.StringWriter();
        JsonWriter writer = new JsonWriter(new PrintWriter(contents)))
      {
        writer.value("key", "value");

        assertEquals("contents", "key: value", contents.toString());
      }
    }

    // ......................................................................
    //----- arrays ---------------------------------------------------------

    /**
     * The empty Test.
     *
     * @throws IOException when closing contents writer
     */
    @org.junit.Test
    public void arrays() throws IOException
    {
      try (java.io.StringWriter contents = new java.io.StringWriter();
        JsonWriter writer = new JsonWriter(new PrintWriter(contents)))
      {
        writer
          .startArray()
          .add("5")
          .next()
          .startArray()
          .add("55")
          .next()
          .add("23")
          .next()
          .endArray()
          .endArray()
          .close();

        assertEquals("contents",
                     "[\n"
                     + "  5,\n"
                     + "  [\n"
                     + "    55,\n"
                     + "    23\n"
                     + "  ]\n"
                     + "]\n",
                     contents.toString());
      }
    }

    //......................................................................
    //----- object ---------------------------------------------------------

    /**
     * The empty Test.
     *
     * @throws IOException when closing contents writer
     */
    @org.junit.Test
    public void object() throws IOException
    {
      try (java.io.StringWriter contents = new java.io.StringWriter();
        JsonWriter writer = new JsonWriter(new PrintWriter(contents)))
      {
        writer
          .startObject()
          .value("key1", "value1")
          .next()
          .value("key2", "value2")
          .next()
          .endObject()
          .close();

        assertEquals("contents",
                     "{\n"
                     + "  key1: value1,\n"
                     + "  key2: value2\n"
                     + "}\n",
                     contents.toString());
      }
    }

    //......................................................................
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}

