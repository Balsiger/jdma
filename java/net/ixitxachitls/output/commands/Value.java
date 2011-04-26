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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The value command.
 *
 * @file          Value.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Value extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Value ---------------------------------

  /**
   * The constructor for the value command.
   *
   * @param       inType  the type of the value
   * @param       inLabel the label for the value
   * @param       inValue the value itself
   *
   */
  public Value(@Nonnull Object inType, @Nonnull Object inLabel,
               @Nonnull Object inValue)
  {
    this();

    withArguments(inType, inLabel, inValue);
  }

  //........................................................................
  //--------------------------------- Value ---------------------------------

  /**
   * This is the internal constructor for a command.
   *
   */
  protected Value()
  {
    super(NAME, 0, 3);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for value printing. */
  public static final @Nonnull String NAME =
    Config.get("resource:commands/value", "value");

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
      Command command = new Value("type", "label", "value");
      assertEquals("command", "\\value{type}{label}{value}",
                   command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
