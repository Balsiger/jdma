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
 * The huge command.
 *
 * @file          Huge.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Huge extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Huge ---------------------------------

  /**
   * The constructor for the huge command.
   *
   * @param       inText the text to set huge
   *
   */
  public Huge(@Nonnull Object inText)
  {
    this();

    withArguments(inText);
  }

  //........................................................................
  //--------------------------------- Huge ---------------------------------

  /**
   * This is the internal constructor for a command.
   *
   * @undefined   never
   *
   */
  protected Huge()
  {
    super(HUGE, 0, 1);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for huge printing. */
  public static final String HUGE =
    Config.get("resource:commands/huge", "huge");

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
      Command command = new Huge("text");
      assertEquals("setup", "\\huge{text}", command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
