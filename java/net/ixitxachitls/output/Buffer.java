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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the interface for the buffers used for output.
 *
 * @file          Buffer.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public interface Buffer
{
  //----------------------------------------------------------------- nested

  //----- Alignment --------------------------------------------------------

  /** The alignment of a table cell. */
  public enum Alignment
  {
    /** Left alignment. */
    left("left", 'L'),

    /** Right alignment. */
    right("right", 'R'),

    /** Centered alignment. */
    center("center", 'C'),

    /** Block setting. */
    block("block", 'B');

    //----------------------------- Alignment ------------------------------

    /**
     * Construct the alignment value.
     *
     * @param       inName  the name of the alignment (for printing)
     * @param       inShort the short name for table definition
     *
     */
    private Alignment(@Nonnull String inName, char inShort)
    {
      m_name  = inName;
      m_short = inShort;
    }

    //......................................................................

    /** The name of the alignment. */
    private @Nonnull String m_name;

    /** The short name of the alignment. */
    private char m_short;

    //------------------------------ toString ------------------------------

    /**
     * Convert the object to a human readable string.
     *
     * @return      the String representation
     *
     */
    @Override
	public @Nonnull String toString()
    {
      return m_name;
    }

    //......................................................................
    //------------------------------ getShort ------------------------------

    /**
     * Get the short representation of the alignment.
     *
     * @return      the character denoting the alignment
     *
     */
    public char getShort()
    {
      return m_short;
    }

    //......................................................................
    //---------------------------- shortValueOf ----------------------------

    /**
     * Get the value associated with the given short value.
     *
     * @param       inShort the short value to look for
     *
     * @return      the Alignment found or null if not found
     *
     */
    public static @Nullable Alignment shortValueOf(@Nullable String inShort)
    {
      if(inShort == null)
        return null;

      for(Alignment value : values())
        if(inShort.equals("" + value.getShort()))
          return value;

      return null;
    }

    //......................................................................
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- getContents ------------------------------

  /**
   * Get the complete contents of the buffer.
   *
   * @return      a String with the complete contents of the buffer
   *
   */
  public @Nonnull String getContents();

  //........................................................................
  //------------------------------ newBuffer -------------------------------

  /**
   * Get a buffer similar to the current one.
   *
   * @return      a new buffer
   *
   */
  public @Nonnull Buffer newBuffer();

  //........................................................................
  //------------------------------ newBuffer -------------------------------

  /**
   * Get a buffer similar to the current one.
   *
   * @param       inWidth the width of the new buffer
   *
   * @return      a new buffer
   *
   */
  public @Nonnull Buffer newBuffer(int inWidth);

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
  public void append(@Nullable Object inObject);

  //........................................................................

  //------------------------------- endLine --------------------------------

  /**
   * End the current line of the buffer. If the line is already terminated
   * with a newline, nothing is done, otherwise a newline is added.
   *
   */
  public void endLine();

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................
}
