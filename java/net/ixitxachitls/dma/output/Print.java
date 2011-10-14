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

package net.ixitxachitls.dma.output;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.output.commands.Command;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A wrapper to store all values used for printing and converting them
 * into a list of commands for output.
 *
 *
 * @file          Print.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Print extends AbstractPrint
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Print ---------------------------------

  /**
   * Create the print values.
   *
   * @param    inTemplate the template to print with.
   *
   */
  public Print(@Nonnull String inTemplate)
  {
    m_template = inTemplate;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The template to print with. */
  private @Nonnull String m_template;

  /** The tokens to print. */
  private volatile @Nullable List<String> m_tokens;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- print ---------------------------------

  /**
   * Print the given entry into a command.
   *
   * @param       inEntry the entry to print
   * @param       inDM    true if printing for a dm, false if not
   *
   * @return      the object that can be added to a document for printing
   *
   */
  public @Nonnull Object print(@Nonnull AbstractEntry inEntry, boolean inDM)
  {
    // CHECKSTYLE:OFF (this works in Java 1.6)
    if(m_tokens == null)
      synchronized(this)
      {
        if(m_tokens == null)
          m_tokens = tokenize(m_template);
      }
    // CHECKSTYLE:ON

    return convert(m_tokens, inEntry, "**null**", inDM);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------ addValue --------------------------------

  /**
   * Add a value to the print output.
   *
   * @param       inKey      the key of the value added
   * @param       inValue    the value to add
   * @param       inEditable true if editable, false if not
   * @param       inDM       true if the value is for DMs only, false else
   * @param       inPlayer   true if the data is for players only
   * @param       inPlural   the plural for the key of the value
   *
   */
//   public void add(@Nonnull String inKey, @Nonnull Object inValue,
//                   boolean inEditable, boolean inDM, boolean inPlayer,
//                   @Nonnull String inPlural)
//   {
//     Value value = new Value(inValue, inEditable, inDM, inPlayer, inPlural);
//     Value old = m_values.get(inKey);

//     if(old == null)
//       m_values.put(inKey, value);
//     else
//       old.add(value);
//     }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- print ----------------------------------------------------------

    /** The print Test. */
    @SuppressWarnings("unchecked") // unchecked cast
    @org.junit.Test
    public void print()
    {
      net.ixitxachitls.dma.entries.BaseEntry entry =
        new net.ixitxachitls.dma.entries.BaseEntry("test", new net.ixitxachitls
                                                   .dma.data.DMAData("path"));

      entry.setDescription("desc");

      Print print =
        new Print("start $first ${title} middle $description the end");

      assertEquals("printing",
                   "[start , \\color{error}{ * first * },  , "
                   + "\\title[entrytitle]"
                   + "{test},  middle , "
                   + "\\editable{test}{base entry}"
                   + "{\\baseCommand{desc}}{description}"
                   + "{\"desc\"}{formatted},  the end]",
                   ((Command)print.print(entry, true)).getArguments()
                   .toString());
    }

    //......................................................................
  }

  //........................................................................
}
