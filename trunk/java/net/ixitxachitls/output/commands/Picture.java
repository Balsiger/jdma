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
 * The picture command.
 *
 * @file          Picture.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Picture extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Picture -------------------------------

  /**
   * The constructor for the picture command.
   *
   * @param       inName    the file name of the picture (without extension)
   * @param       inCaption the caption below the picture
   * @param       inLink    where the picture links to
   *
   */
  public Picture(@Nonnull Object inName, @Nonnull Object inCaption,
                 @Nonnull Object inLink)
  {
    this(inName, inCaption, inLink, false);
  }

  //........................................................................
  //-------------------------------- Picture -------------------------------

  /**
   * The constructor for the picture command.
   *
   * @param       inName      the file name of the picture (without extension)
   * @param       inCaption   the caption below the picture
   * @param       inLink      where the picture links to
   * @param       inHighlight flag if picture should be highlighted on mouse
   *                          over
   *
   */
  public Picture(@Nonnull Object inName, @Nonnull Object inCaption,
                 @Nonnull Object inLink,
                 @Nonnull boolean inHighlight)
  {
    this();

    withArguments(inName, inCaption, inLink);

    if(inHighlight)
      withOptionals("highlight");
  }

  //........................................................................
  //-------------------------------- Picture -------------------------------

  /**
   * This is the internal constructor for a command.
   *
   */
  protected Picture()
  {
    super(NAME, 2, -1);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for adding a picture. */
  public static final @Nonnull String NAME =
    Config.get("resource:commands/picture", "picture");

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
      Command command = new Picture("name", "caption", "link");
      assertEquals("setup", "\\picture{name}{caption}{link}",
                   command.toString());

      command = new Picture("name", "caption", "link", false);
      assertEquals("setup", "\\picture{name}{caption}{link}",
                   command.toString());

      command = new Picture("name", "caption", "link", true);
      assertEquals("setup", "\\picture[highlight]{name}{caption}{link}",
                   command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
