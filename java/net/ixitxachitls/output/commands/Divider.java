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
 * The divider command.
 *
 * @file          Divider.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Divider extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Divider -------------------------------

  /**
   * The constructor for the divider command.
   *
   * @param       inClass   the id of the divider (for css)
   * @param       inCommand the contents of the divider
   *
   */
  public Divider(@Nonnull Object inClass, @Nonnull Object inCommand)
  {
    this();

    withArguments(inClass, inCommand);
  }

  //........................................................................
  //-------------------------------- Divider -------------------------------

  /**
   *
   * The constructor for the divider command.
   *
   * @param       inID      the id of the divider (for css)
   * @param       inClass   the class for styling the divider
   * @param       inCommand the contents of the divider
   *
   */
  public Divider(@Nonnull Object inID, @Nonnull Object inClass,
                 @Nonnull Object inCommand)
  {
    this(inClass, inCommand);

    withOptionals(inID);
  }

  //........................................................................
  //-------------------------------- Divider -------------------------------

  /**
   * The constructor for the divider command.
   *
   */
  protected Divider()
  {
    super(DIVIDER, 1, 2);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for setting a divider. */
  public static final String DIVIDER =
    Config.get("resource:commands/divider", "divider");

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
      Command command = new Divider("class", new Bold("command"));
      assertEquals("setup", "\\divider{class}{\\bold{command}}",
                   command.toString());

      command = new Divider("id", "class", new Bold("command"));
      assertEquals("setup", "\\divider[id]{class}{\\bold{command}}",
                   command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
