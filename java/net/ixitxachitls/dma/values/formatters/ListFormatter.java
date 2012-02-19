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

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.output.commands.BaseCommand;
import net.ixitxachitls.output.commands.Command;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A formatter for a list.
 *
 * @file          ListFormatter.java
 *
 * @author        balsiger@ixitxchitls.net (Peter Balsiger)
 *
 * @param         <K> the type of list to format
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class ListFormatter<K extends ValueList<? extends Value>>
  implements Formatter<K>
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- ListFormattter ----------------------------

  /**
   * Create the formatter.
   *
   * @param       inDelimiter the delimiter to use between each entry
   *
   */
  public ListFormatter(@Nullable String inDelimiter)
  {
    m_delimiter = inDelimiter;
  }

  //........................................................................
  //---------------------------- ListFormatter -----------------------------

  /**
   * Format the list using the given command.
   *
   * @param       inCommand the command use to format the list; list
   *                        elements will be arguments to the command
   *
   */
  public ListFormatter(@Nonnull BaseCommand inCommand)
  {
    m_command = inCommand;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The delimiter between list entries. */
  private @Nullable String m_delimiter;

  /** The command to format the list. */
  private @Nullable BaseCommand m_command;

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
public @Nonnull Command format(@Nonnull K inValue)
  {
    ArrayList<Object> commands = new ArrayList<Object>();

    for(java.util.Iterator<? extends Value> i = inValue.iterator();
        i.hasNext(); )
    {
      commands.add(i.next().format(false));

      if(m_delimiter != null && i.hasNext())
        commands.add(m_delimiter);
    }

    if(m_command != null)
      return new BaseCommand.Builder(m_command).withArguments(commands).build();

    return new Command(commands);
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
      Formatter<ValueList<net.ixitxachitls.dma.values.Name>>
        formatter =
        new ListFormatter<ValueList<net.ixitxachitls.dma.values.Name>>
        (":");

      assertEquals("simple", "1:2:3",
                   formatter.format
                   (new ValueList<net.ixitxachitls.dma.values.Name>
                    (new net.ixitxachitls.dma.values.Name("1"),
                     new net.ixitxachitls.dma.values.Name("2"),
                     new net.ixitxachitls.dma.values.Name("3")))
                   .toString());
      assertEquals("undefined", "",
                   formatter.format
                   (new ValueList<net.ixitxachitls.dma.values.Name>
                    (new net.ixitxachitls.dma.values.Name()))
                   .toString());

      // delimiters with null values
      formatter =
        new ListFormatter<ValueList<net.ixitxachitls.dma.values.Name>>
        ((String)null);

      assertEquals("simple", "123",
                   formatter.format
                   (new ValueList<net.ixitxachitls.dma.values.Name>
                    (new net.ixitxachitls.dma.values.Name("1"),
                     new net.ixitxachitls.dma.values.Name("2"),
                     new net.ixitxachitls.dma.values.Name("3")))
                   .toString());
    }

    //......................................................................
  }

  //........................................................................
}
