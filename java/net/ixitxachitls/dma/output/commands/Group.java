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

package net.ixitxachitls.dma.output.commands;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.commands.BaseCommand;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The group command.
 *
 * @file          Group.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Group extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Group --------------------------------

  /**
   * The constructor for the group command.
   *
   * @param       inText the text to set group
   *
   */
  public Group(@Nonnull Object inText)
  {
    this();

    withArguments(inText);
  }

  //........................................................................
  //------------------------------- Group --------------------------------

  /**
   * The constructor for the group command.
   *
   * @param       inText the text to set group
   * @param       inReal the real name of the group (ID)
   *
   */
  public Group(@Nonnull Object inText, @Nonnull Object inReal)
  {
    this(inText);

    withOptionals(inReal);
  }

  //........................................................................
  //------------------------------- Group --------------------------------

  /**
   * This is the internal constructor for a command.
   *
   */
  protected Group()
  {
    super(NAME, 1, 1);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for setting a group name. */
  public static final @Nonnull String NAME =
    Config.get("resource:commands/Group", "Group");

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
      Command command = new Group("text");
      assertEquals("setup", "\\Group{text}", command.toString());

      command = new Group("text", "real");
      assertEquals("setup", "\\Group[real]{text}", command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
