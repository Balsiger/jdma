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

package net.ixitxachitls.output;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.NotThreadSafe;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a simple buffer, actually just a StringBuilder with the Buffer
 * interface.
 *
 * @file          SimpleBuffer.java
 *
 * @author        balsiger@ixitxachits.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
@ParametersAreNonnullByDefault
public class SimpleBuffer implements Buffer
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- SimpleBuffer -----------------------------

  /**
   * This is the default constructor for the buffer.
   *
   */
  public SimpleBuffer()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The real buffer used. */
  private StringBuilder m_buffer = new StringBuilder();

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ newBuffer -------------------------------

  /**
   * Get a similar buffer.
   *
   * @return      the new buffer
   *
   */
  @Override
  public Buffer newBuffer()
  {
    return new SimpleBuffer();
  }

  //........................................................................
  //------------------------------ newBuffer -------------------------------

  /**
   * Get a similar buffer as the current one, but with a given width.
   *
   * @param       inWidth the desired with of the new buffer
   *
   * @return      the new buffer
   *
   */
  @Override
  public Buffer newBuffer(int inWidth)
  {
    return newBuffer();
  }

  //........................................................................

  //----------------------------- getContents ------------------------------

  /**
   * Get the complete contents of the buffer.
   *
   * @return      a String with the complete contents of the buffer
   *
   */
  @Override
  public String getContents()
  {
    return m_buffer.toString();
  }

  //........................................................................

  //------------------------------- toString -------------------------------

  /**
   * Convert the buffer to a human readable String.
   *
   * @return      a String representation
   *
   */
  @Override
  public String toString()
  {
    return m_buffer.toString();
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- append --------------------------------

  /**
   * Append the given object to the buffer.
   *
   * @param       inObject the object to add
   *
   */
  @Override
  public void append(@Nullable Object inObject)
  {
    if(inObject != null)
      m_buffer.append(inObject.toString());
  }

  //........................................................................

  //------------------------------- endLine --------------------------------

  /**
   * End the current line of the buffer. If the line is already terminated
   * with a newline, nothing is done, otherwise a newline is added.
   *
   */
  @Override
  public void endLine()
  {
    // the line in the buffer is already ended, if it is empty; in that case we
    // don't need to end it again
    if(m_buffer.length() > 0 && m_buffer.charAt(m_buffer.length() - 1) == '\n')
      return;

    append("\n");
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- add ------------------------------------------------------------

    /** Adding text. */
    @org.junit.Test
    public void add()
    {
      Buffer buffer = new SimpleBuffer();

      buffer.append("just some text to add to the buffer");
      assertEquals("add", "just some text to add to the buffer",
                   buffer.getContents());

      buffer = buffer.newBuffer();

      buffer.append("another text");
      buffer.endLine();

      assertEquals("add 2", "another text\n", buffer.getContents());
      assertEquals("add 2", "another text\n", buffer.toString());
    }

    //......................................................................
  }

  //........................................................................
}
