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
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The navigation command.
 *
 * @file          Navigation.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Navigation extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- Navigation -------------------------------

  /**
   * The constructor for the navigation command.
   *
   * @param       inParts the parts to navigate to
   * @param       inID    the id of the navigation
   *
   */
  public Navigation(@Nullable String inID, @Nonnull Object ... inParts)
  {
    this();

    withArguments(inParts);

    if(inID != null)
      withOptionals(inID);
  }

  //........................................................................
  //----------------------------- Navigation -------------------------------

  /**
   * This is the internal constructor for a command.
   *
   */
  protected Navigation()
  {
    super(NAVIGATION, 1, -1);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for setting an id at a special position in the text. */
  public static final @Nonnull String NAVIGATION =
    Config.get("resource:commands/navigation", "navigation");

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
      Command command =
        new Navigation(null, "text 1", "text 2", "text 3", "text 4");
      assertEquals("setup", "\\navigation{text 1}{text 2}{text 3}{text 4}",
                   command.toString());

      command = new Navigation("ID", "text 1", "text 2", "text 3", "text 4");
      assertEquals("setup", "\\navigation[ID]{text 1}{text 2}{text 3}{text 4}",
                   command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
