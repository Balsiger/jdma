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

import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The link command.
 *
 * @file          ImageLink.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class ImageLink extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ ImageLink -------------------------------

  /**
   * The constructor for the link command.
   *
   * @param       inImage the url for the image
   * @param       inName  the name of the image
   * @param       inLink  the link to point to
   * @param       inStyle the style to use for the image
   *
   */
  public ImageLink(@Nonnull Object inImage, @Nonnull Object inName,
                   @Nonnull Object inLink, @Nonnull Object inStyle)
  {
    this();

    withArguments(inImage, inName, inLink, inStyle);
  }

  //........................................................................
  //-------------------------------- withID --------------------------------

  /**
   * Add an id to the image link.
   *
   * @param       inID the id to add
   *
   * @return      the object for chaining
   *
   */
  public ImageLink withID(@Nonnull String inID)
  {
    withOptionals(inID);

    return this;
  }

  //........................................................................

  //------------------------------- ImageLink ------------------------------

  /**
   * This is the internal constructor for a command.
   *
   * @undefined   never
   *
   */
  protected ImageLink()
  {
    super(NAME, 1, 4);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for adding a link to another file or position to the text. */
  public static final String NAME =
    Config.get("resource:commands/image.link", "imagelink");

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The Test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- arguments ------------------------------------------------------

    /** Testing the arguments. */
    @org.junit.Test
    public void arguments()
    {
      Command command = new ImageLink("text", "name", "some url", "style");
      assertEquals("base", "\\imagelink{text}{name}{some url}{style}",
                   command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
