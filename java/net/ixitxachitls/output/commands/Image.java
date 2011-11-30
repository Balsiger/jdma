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
 * The image command.
 *
 * @file          Image.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Image extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Image --------------------------------

  /**
   * The constructor for the icon command.
   *
   * @param       inName    the file name of the image (without extension)
   *
   */
  public Image(@Nonnull Object inName)
  {
    this();

    withArguments(inName);
  }

  //........................................................................
  //--------------------------------- Image --------------------------------

  /**
   * The constructor for the image command.
   *
   * @param       inName  the file name of the image (without extension)
   * @param       inStyle the style for formatting the image
   *
   */
  public Image(@Nonnull Object inName, @Nonnull Object inStyle)
  {
    this(inName);

    withOptionals(inStyle);
  }

  //........................................................................
  //--------------------------------- Image --------------------------------

  /**
    * This is the internal constructor for a command.
    *
    */
  protected Image()
  {
    super(NAME, 1, 1);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for adding an image. */
  public static final @Nonnull String NAME =
    Config.get("resource:commands/image", "image");

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
      Command command = new Image("name");
      assertEquals("setup", "\\image{name}", command.toString());

      command = new Image("name", "default");
      assertEquals("setup", "\\image[default]{name}", command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
