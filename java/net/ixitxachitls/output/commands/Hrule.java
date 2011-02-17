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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The Hrule command.
 *
 * @file          Hrule.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Hrule extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Hrule ---------------------------------

  /**
   * The constructor for the Hrule command.
   *
   * @param       inColor the color of the rule
   * @param       inWidth the width of the rule (in % of the total width)
   *
   */
  public Hrule(@Nonnull String inColor, int inWidth)
  {
    this(inColor);

    if(inWidth <= 0)
      throw new IllegalArgumentException("must be a positive width...");

    withOptionals("" + inWidth);
  }

  //........................................................................
  //-------------------------------- Hrule ---------------------------------

  /**
   * This is the internal constructor for a command.
   *
   * @param       inColor the color of the line
   *
   */
  public Hrule(@Nonnull String inColor)
  {
    super(HRULE, 1, 1);

    withArguments(inColor);
  }

  //........................................................................
  //-------------------------------- Hrule ---------------------------------

  /**
   * This is the internal constructor for a command.
   *
   */
  public Hrule()
  {
    this("black");
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for formatting a horizontal rule. */
  public static final String HRULE =
    Config.get("resource:commands/hrule", "hrule");

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- arguments ------------------------------------------------------

    /** Testing arguments. */
    @org.junit.Test
    public void arguments()
    {
      Command command = new Hrule();
      assertEquals("setup", "\\hrule{black}", command.toString());

      command = new Hrule("red");
      assertEquals("setup", "\\hrule{red}", command.toString());

      command = new Hrule("green", 20);
      assertEquals("setup", "\\hrule[20]{green}", command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
