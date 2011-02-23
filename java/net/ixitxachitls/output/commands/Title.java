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
 * The title command.
 *
 * @file          Title.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Title extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Title --------------------------------

  /**
   * The constructor for the title command.
   *
   * @param       inText the text of the title
   *
   */
  public Title(@Nonnull Object inText)
  {
    this();

    withArguments(inText);
  }

  //........................................................................
  //--------------------------------- Title --------------------------------

  /**
   * The constructor for the title command.
   *
   * @param       inText  the text of the title
   * @param       inLabel the title label
   *
   */
  public Title(@Nonnull Object inText, @Nonnull String inLabel)
  {
    this(inText);

    withOptionals(inLabel);
  }

  //........................................................................
  //--------------------------------- Title --------------------------------

  /**
   * The constructor for the title command.
   *
   * @param       inText    the text of the title
   * @param       inLabel   the title label
   * @param       inSubText some text to print right after the title
   *
   */
  public Title(@Nonnull Object inText, @Nonnull String inLabel,
               @Nonnull Object inSubText)
  {
    this(inText, inLabel);

    withOptionals(inSubText);
  }

  //........................................................................
  //--------------------------------- Title --------------------------------

  /**
    *
    * This is the internal constructor for a command.
    *
    */
  protected Title()
  {
    super(TITLE, 2, 1);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for formatting a title. */
  public static final String TITLE =
    Config.get("resource:commands/title", "title");

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
      Command command = new Title("text");
      assertEquals("setup", "\\title{text}", command.toString());

      command = new Title("text", "label");
      assertEquals("setup", "\\title[label]{text}", command.toString());

      command = new Title("text", "label");
      assertEquals("setup", "\\title[label]{text}", command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
