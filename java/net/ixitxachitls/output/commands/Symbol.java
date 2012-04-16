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

package net.ixitxachitls.output.commands;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The symbol command.
 *
 * @file          Symbol.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Symbol extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Symbol ---------------------------------

  /**
   * The constructor for the symbol command.
   *
   * @param       inText the text to set symbol
   *
   */
  public Symbol(@Nonnull Object inText)
  {
    this();

    withArguments(inText);
  }

  //........................................................................
  //--------------------------------- Symbol ---------------------------------

  /**
   * This is the internal constructor for a command.
   *
   * @undefined   never
   *
   */
  protected Symbol()
  {
    super(NAME, 0, 1);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for symbol printing. */
  public static final @Nonnull String NAME =
    Config.get("resource:commands/symbol", "symbol");

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test.
   *
   * @hidden
   *
   */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- arguments ------------------------------------------------------

    /** Testing arguments. */
    @org.junit.Test
    public void arguments()
    {
      List<Object> parsed = BaseCommand.parse("\\symbol{some test}");
      assertEquals("symbol", "\\symbol{some test}", parsed.get(0).toString());

      parsed = BaseCommand.parse("\\symbol{some test}{some other}");
      assertEquals("too much", "\\symbol{some test}", parsed.get(0).toString());

      m_logger.addExpected("WARNING: too many arguments given for 'symbol', "
                           + "surplus will be ignored "
                           + "(at \\symbol" + s_markArgStart + "<0>some test"
                           + s_markArgEnd + "<0>" + s_markArgStart
                           + "<0>some ot...)");
      m_logger.verify();

      parsed = BaseCommand.parse("\\symbol command");
      assertEquals("not enough", "\\symbol{}", parsed.get(0).toString());
      assertEquals("not enough", "command", parsed.get(1).toString());

      m_logger.addExpected("WARNING: not enough arguments given for 'symbol'"
                           + ", missing arguments will be empty "
                           + "(at \\symbol command...)");
      m_logger.verify();

      parsed = BaseCommand.parse("\\symbol[command]{test}");
      assertEquals("optional", "\\symbol{test}", parsed.get(0).toString());

      m_logger.addExpected("WARNING: too many optional arguments given for "
                           + "'symbol', surplus will be ignored");
      m_logger.verify();

       Command command = new Symbol("a test");
       assertEquals("command", "\\symbol{a test}", command.toString());

       command = new Symbol(new Command("a test"));
       assertEquals("command", "\\symbol{a test}", command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
