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
 * The hide command.
 *
 * @file          Hide.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Hide extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Hide --------------------------------

  /**
   * The constructor for the hide command.
   *
   * @param       inID       the id to use to hide
   * @param       inContents the text to set hide
   *
   */
  public Hide(@Nonnull Object inID, @Nonnull Object inContents)
  {
    this();

    withArguments(inID, inContents);
  }

  //........................................................................
  //-------------------------------- Hide --------------------------------

  /**
   * This is the internal constructor for a command.
   *
   */
  protected Hide()
  {
    super(HIDE, 0, 2);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for hide contents. */
  public static final @Nonnull String HIDE =
    Config.get("resource:commands/hide", "hide");

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
      Command command = new Hide("label", "text");
      assertEquals("setup", "\\hide{label}{text}", command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
