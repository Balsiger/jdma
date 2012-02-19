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

package net.ixitxachitls.dma.values.formatters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.output.commands.Command;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A formatter used to print some delimiter before and/or after a value.
 *
 * @file          DelimFormatter.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param         <K> the type of values to format
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class DelimFormatter<K extends Value> implements Formatter<K>
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- DelimFormatter ----------------------------

  /**
   * Create the delimiter formatter.
   *
   * @param       inPrefix  the leading delimiter
   * @param       inPostfix the trailing delimiter
   *
   */
  public DelimFormatter(@Nullable String inPrefix, @Nullable String inPostfix)
  {
    m_prefix  = inPrefix;
    m_postfix = inPostfix;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The delimiter in front. */
  private @Nullable String m_prefix;

  /** The delimiter in the back. */
  private @Nullable String m_postfix;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- format --------------------------------

  /**
   * Format the given value into a command.
   *
   * @param       inValue the value to format
   *
   * @return      the formatted command
   *
   */
  @Override
public Command format(@Nonnull K inValue)
  {
    if(m_prefix == null && m_postfix == null)
      return inValue.format(false, true);

    if(m_prefix != null && m_postfix != null)
      return new Command(m_prefix, inValue.format(false, true), m_postfix);

    if(m_prefix != null)
      return new Command(m_prefix, inValue.format(false, true));

    return new Command(inValue.format(false, true), m_postfix);
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
    //----- format ---------------------------------------------------------

    /** The complete test. */
    @org.junit.Test
    public void format()
    {
      // complete value
      Formatter<net.ixitxachitls.dma.values.Name> formatter =
        new DelimFormatter<net.ixitxachitls.dma.values.Name>("pre", "post");

      assertEquals("simple", "pretextpost",
                   formatter.format(new net.ixitxachitls.dma.values.Name
                                    ("text")).toString());
      assertEquals("undefined", "pre\\color{error}{$undefined$}post",
                   formatter.format(new net.ixitxachitls.dma.values.Name())
                   .toString());

      // delimiters with null values
      formatter =
        new DelimFormatter<net.ixitxachitls.dma.values.Name>(null, "post");

      assertEquals("null", "textpost",
                   formatter.format(new net.ixitxachitls.dma.values.Name
                                    ("text")).toString());

      formatter =
        new DelimFormatter<net.ixitxachitls.dma.values.Name>("pre", null);

      assertEquals("null", "pretext",
                   formatter.format(new net.ixitxachitls.dma.values.Name
                                    ("text")).toString());

      formatter =
        new DelimFormatter<net.ixitxachitls.dma.values.Name>(null, null);

      assertEquals("null", "text",
                   formatter.format(new net.ixitxachitls.dma.values.
                                    Name("text")).toString());
    }

    //......................................................................
  }

  //........................................................................
}
