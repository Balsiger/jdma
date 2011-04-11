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
   * @param       inText the text to set the link to
   * @param       inURL  the url to link to
   *
   */
  public ImageLink(@Nonnull Object inText, @Nonnull Object inURL)
  {
    this();

    withArguments(inText, inURL);
  }

  //........................................................................
  //------------------------------ ImageLink -------------------------------

  /**
   * The constructor for the link command.
   *
   * @param       inText the text to set the link to
   * @param       inURL  the url to link to
   * @param       inID   the ID of the link for special formatting
   *
   */
  public ImageLink(@Nonnull Object inText, @Nonnull Object inURL,
                   @Nonnull Object inID)
  {
    this(inText, inURL);

    withOptionals(inID);
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
    super(IMAGE_LINK, 1, 2);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for adding a link to another file or position to the text. */
  public static final String IMAGE_LINK =
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
      Command command = new ImageLink("text", "some url");
      assertEquals("base", "\\imagelink{text}{some url}", command.toString());

      command = new ImageLink("text", "some url", "id");
      assertEquals("optional", "\\imagelink[id]{text}{some url}",
                   command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
