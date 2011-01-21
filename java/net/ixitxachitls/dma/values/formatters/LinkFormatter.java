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

package net.ixitxachitls.dma.values.formatters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Link;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A formatter for a link.
 *
 * @file          LinkFormatter.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param         <K> the type of values to format
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class LinkFormatter<K extends Value> implements Formatter<K>
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- LinkFormattter -----------------------------

  /**
   * Create the formatter.
   *
   * @param       inBase the base path for the link
   *
   */
  public LinkFormatter(@Nonnull String inBase)
  {
    m_base = inBase;
  }

  //........................................................................
  //---------------------------- LinkFormattter -----------------------------

  /**
   * Create the formatter.
   *
   * @param       inBase  the base path for the link
   * @param       inStyle the CSS style for the link
   *
   */
  public LinkFormatter(@Nonnull String inBase, @Nonnull String inStyle)
  {
    this(inBase);

    m_style = inStyle;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The base directory for the link. */
  private @Nonnull String m_base;

  /** The style to use, if any. */
  private @Nullable String m_style;

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
  public Command format(@Nonnull K inValue)
  {
    if(m_style != null)
      return new Link(inValue.format(false, true), m_base + inValue.group(),
                      m_style);

    return new Link(inValue.format(false, true), m_base + inValue.group());
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
        new LinkFormatter<net.ixitxachitls.dma.values.Name>("/base/");

      assertEquals("simple", "\\link[/base/group]{group}",
                   formatter.format
                   (new net.ixitxachitls.dma.values.Name("group"))
                   .toString());

      assertEquals("undefined",
                   "\\link[/base/$undefined$]{\\color{error}{$undefined$}}",
                   formatter.format
                   (new net.ixitxachitls.dma.values.Name()).toString());

      formatter =
        new LinkFormatter<net.ixitxachitls.dma.values.Name>("/base/", "style");

      assertEquals("simple", "\\link[/base/group][style]{group}",
                   formatter.format
                   (new net.ixitxachitls.dma.values.Name("group"))
                   .toString());

      assertEquals("undefined",
                   "\\link[/base/$undefined$][style]"
                   + "{\\color{error}{$undefined$}}",
                   formatter.format
                   (new net.ixitxachitls.dma.values.Name()).toString());
    }

    //......................................................................
  }

  //........................................................................
}
