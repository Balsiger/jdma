/******************************************************************************
 * Copyright (c) 2002,2003 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The bold command.
 *
 * @file          Bold.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Bold extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Bold ---------------------------------

  /**
   * The constructor for the bold command.
   *
   * @param       inText the text to set bold
   *
   */
  public Bold(@Nonnull Object inText)
  {
    this();

    withArguments(inText);
  }

  //........................................................................
  //--------------------------------- Bold ---------------------------------

  /**
   * This is the internal constructor for a command.
   *
   * @undefined   never
   *
   */
  protected Bold()
  {
    super(BOLD, 0, 1);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for bold printing. */
  public static final @Nonnull String BOLD =
    Config.get("resource:commands/bold", "bold");

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
      List<Object> parsed = BaseCommand.parse("\\bold{some test}");
      assertEquals("bold", "\\bold{some test}", parsed.get(0).toString());

      parsed = BaseCommand.parse("\\bold{some test}{some other}");
      assertEquals("too much", "\\bold{some test}", parsed.get(0).toString());

      m_logger.addExpected("WARNING: too many arguments given for 'bold', "
                           + "surplus will be ignored "
                           + "(at \\bold" + s_markArgStart + "<0>some test"
                           + s_markArgEnd + "<0>" + s_markArgStart
                           + "<0>some othe...)");
      m_logger.verify();

      parsed = BaseCommand.parse("\\bold command");
      assertEquals("not enough", "\\bold{}", parsed.get(0).toString());
      assertEquals("not enough", "command", parsed.get(1).toString());

      m_logger.addExpected("WARNING: not enough arguments given for 'bold'"
                           + ", missing arguments will be empty "
                           + "(at \\bold command...)");
      m_logger.verify();

      parsed = BaseCommand.parse("\\bold[command]{test}");
      assertEquals("optional", "\\bold{test}", parsed.get(0).toString());

      m_logger.addExpected("WARNING: too many optional arguments given for "
                           + "'bold', surplus will be ignored");
      m_logger.verify();

       Command command = new Bold("a test");
       assertEquals("command", "\\bold{a test}", command.toString());

       command = new Bold(new Command("a test"));
       assertEquals("command", "\\bold{a test}", command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
