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
 * The hidden command.
 *
 * @file          Hidden.java
 *
 * @author        balsiger@ixitxachitlls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Hidden extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Hidden --------------------------------

  /**
   * The constructor for the hidden command.
   *
   * @param       inLabel the label to use to hide
   * @param       inText  the text to set hidden
   *
   */
  public Hidden(@Nonnull Object inLabel, @Nonnull Object inText)
  {
    this();

    withArguments(inLabel, inText);
  }

  //........................................................................
  //-------------------------------- Hidden --------------------------------

  /**
   * This is the internal constructor for a command.
   *
   */
  protected Hidden()
  {
    super(HIDDEN, 0, 2);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for hidden text. */
  public static final String HIDDEN =
    Config.get("resource:commands/hidden", "hidden");

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
      Command command = new Hidden("label", "text");
      assertEquals("setup", "\\hidden{label}{text}", command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
