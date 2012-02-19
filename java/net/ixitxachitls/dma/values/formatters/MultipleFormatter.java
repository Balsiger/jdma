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
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.output.commands.Command;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A formatter for a multiple.
 *
 * @file          MultipleFormatter.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param         <K> the type of value to format
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class MultipleFormatter<K extends Multiple> implements Formatter<K>
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------- MultipleFormattter ---------------------------

  /**
   * Create the formatter.
   *
   * @param       inDelimiters the delimiters to use between each sub value
   *
   */
  public MultipleFormatter(@Nonnull String ... inDelimiters)
  {
    m_delimiters = inDelimiters;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The delimiters between multiples. */
  private @Nonnull String []m_delimiters;

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

    int j = 0;
    for(java.util.Iterator<Multiple.Element> i = inValue.iterator();
        i.hasNext(); j += 2)
    {
      Multiple.Element element = i.next();

      if(!element.isOptional() || element.get().isDefined())
        if(m_delimiters.length > j && m_delimiters[j] != null)
          commands.add(m_delimiters[j]);

      commands.add(element.get().format(element.isOptional()));

      // trailing formatter
      if(!element.isOptional() || element.get().isDefined())
        if(m_delimiters.length > j + 1 && m_delimiters[j + 1] != null)
          commands.add(m_delimiters[j + 1]);
    }

    return new Command(commands.toArray(new Object [commands.size()]));
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
      Formatter<net.ixitxachitls.dma.values.Multiple> formatter =
        new MultipleFormatter<net.ixitxachitls.dma.values.Multiple>
        ("1", "2", "3", "4", "5", "6");

      assertEquals("simple", "1a23b45c6",
                   formatter.format
                   (new net.ixitxachitls.dma.values.
                    Multiple(new net.ixitxachitls.dma.values.Multiple.Element
                             (new net.ixitxachitls.dma.values.Name("a"),
                              false),
                             new net.ixitxachitls.dma.values.Multiple.Element
                             (new net.ixitxachitls.dma.values.Name("b"),
                              false),
                             new net.ixitxachitls.dma.values.Multiple.Element
                             (new net.ixitxachitls.dma.values.Name("c"),
                              false))).toString());

      assertEquals("undefined",
                   "1\\color{error}{$undefined$}23\\color{error}{$undefined$}"
                   + "45\\color{error}{$undefined$}6",
                   formatter.format
                   (new net.ixitxachitls.dma.values.
                    Multiple(new net.ixitxachitls.dma.values.Multiple.Element
                             (new net.ixitxachitls.dma.values.Name(),
                              false),
                             new net.ixitxachitls.dma.values.Multiple.Element
                             (new net.ixitxachitls.dma.values.Name(),
                              false),
                             new net.ixitxachitls.dma.values.Multiple.Element
                             (new net.ixitxachitls.dma.values.Name(),
                              false))).toString());

      // delimiters with null values
      formatter = new MultipleFormatter<net.ixitxachitls.dma.values.Multiple>();

      assertEquals("simple", "abc",
                   formatter.format
                   (new net.ixitxachitls.dma.values.
                    Multiple(new net.ixitxachitls.dma.values.Multiple.Element
                             (new net.ixitxachitls.dma.values.Name("a"),
                              false),
                             new net.ixitxachitls.dma.values.Multiple.Element
                             (new net.ixitxachitls.dma.values.Name("b"),
                              false),
                             new net.ixitxachitls.dma.values.Multiple.Element
                             (new net.ixitxachitls.dma.values.Name("c"),
                              false))).toString());

      // not enough delimiters
      formatter = new MultipleFormatter<net.ixitxachitls.dma.values.Multiple>
        ("1", null, null, "4");

      assertEquals("simple", "1ab4c",
                   formatter.format
                   (new net.ixitxachitls.dma.values.
                    Multiple(new net.ixitxachitls.dma.values.Multiple.Element
                             (new net.ixitxachitls.dma.values.Name("a"),
                              false),
                             new net.ixitxachitls.dma.values.Multiple.Element
                             (new net.ixitxachitls.dma.values.Name("b"),
                              false),
                             new net.ixitxachitls.dma.values.Multiple.Element
                             (new net.ixitxachitls.dma.values.Name("c"),
                              false))).toString());

    }

    //......................................................................
  }

  //........................................................................
}
