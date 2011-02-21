/******************************************************************************
 * Copyright (c) 2002-2007 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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
import javax.annotation.concurrent.NotThreadSafe;

import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A html writer that only writes the body contents of the page.
 *
 *
 * @file          HTMLBodyWriter.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
public class HTMLBodyWriter extends HTMLWriter
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- HTMLBodyWriter ----------------------------

  /**
   * Create the html writer.
   *
   * @param     inWriter the writer to output to
   *
   */
  public HTMLBodyWriter(@Nonnull PrintWriter inWriter)
  {
    super(inWriter);

    m_inHTML = true;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  //........................................................................

  //-------------------------------------------------------------- accessors

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- title ---------------------------------

  /**
   * Set the title of the document.
   *
   * @param       inTitle the text for the title
   *
   * @return      this writer for chaining
   *
   */
  public HTMLWriter title(String inTitle)
  {
    return script("document.title = '" + inTitle + "';");
  }

  //........................................................................
  //------------------------------ ensureHead ------------------------------

  /**
   * Ensure that we are in the head of the document.
   *
   */
  protected void ensureHead()
  {
    throw
      new UnsupportedOperationException("cannot have head in body only HTML");
  }

  //........................................................................
  //-------------------------------- close ---------------------------------

  /**
   * Close the writer.
   *
   */
  public void close()
  {
    maybeCloseTag();
    maybeCloseHead();

    if(m_tags.size() > 0)
      Log.warning("writer closed, but tags " + m_tags + " not closed");

    m_writer.print(m_body.toString());
    m_writer.close();
    m_bodyWriter.close();
    m_inHTML = false;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- simple ---------------------------------------------------------

    /** The simple Test. */
    @org.junit.Test
    public void simple()
    {
      java.io.StringWriter contents = new java.io.StringWriter();
      HTMLBodyWriter writer = new HTMLBodyWriter(new PrintWriter(contents));

      writer
        .comment("This is a test.")
        .begin("p")
        .attribute("font", "Helvetica")
        .attribute("selected", null)
        .add("This is the body")
        .begin("br")
        .title("title")
        .end("br")
        .end("p")
        .close();

      assertEquals("contents",
                   "    <SCRIPT type=\"text/javascript\">\n"
                   + "      document.title = 'title';\n"
                   + "    </SCRIPT>\n"
                   + "    <!-- This is a test. -->\n"
                   + "    <P font=\"Helvetica\" selected>\n"
                   + "      This is the body\n"
                   + "      <BR/>\n"
                   + "    </P>\n", contents.toString());
    }

    //......................................................................

  }

  //........................................................................
}
