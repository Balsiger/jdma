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
 * The verbatim command.
 *
 * @file          Verbatim.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Verbatim extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Verbatim -------------------------------

  /**
   * The constructor for the verbatim command.
   *
   * @param       inText the text to set verbatim
   *
   */
  public Verbatim(@Nonnull Object inText)
  {
    this();

    withArguments(inText);
  }

  //........................................................................
  //------------------------------- Verbatim -------------------------------

  /**
   * This is the internal constructor for a command.
   *
   */
  protected Verbatim()
  {
    super(VERBATIM, 0, 1);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for verbatim printing. */
  public static final @Nonnull String VERBATIM =
    Config.get("resource:commands/verbatim", "verbatim");

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
      Command command = new Verbatim("text");
      assertEquals("setup", "\\verbatim{text}", command.toString());

      command = new Verbatim(new Bold("test"));
      assertEquals("setup", "\\verbatim{\\bold{test}}", command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
