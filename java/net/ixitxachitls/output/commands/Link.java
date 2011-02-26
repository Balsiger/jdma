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
 * @file          Link.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Link extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Link ---------------------------------

  /**
   * The constructor for the link command.
   *
   * @param       inText the text to set the link to
   *
   */
  public Link(@Nonnull Object inText)
  {
    this();

    withArguments(inText);
  }

  //........................................................................
  //--------------------------------- Link ---------------------------------

  /**
   * The constructor for the link command.
   *
   * @param       inText the text to set the link to
   * @param       inURL  the url to link to
   *
   */
  public Link(@Nonnull Object inText, @Nonnull Object inURL)
  {
    this(inText);

    withOptionals(inURL);
  }

  //........................................................................
  //--------------------------------- Link ---------------------------------

  /**
   * The constructor for the link command.
   *
   * @param       inText the text to set the link to
   * @param       inURL  the url to link to
   * @param       inID   the ID of the link for special formatting
   *
   */
  public Link(@Nonnull Object inText, @Nonnull Object inURL,
              @Nonnull Object inID)
  {
    this(inText);

    withOptionals(inURL, inID);
  }

  //........................................................................
  //--------------------------------- Link ---------------------------------

  /**
   * This is the internal constructor for a command.
   *
   * @undefined   never
   *
   */
  protected Link()
  {
    super(LINK, 2, 1);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for adding a link to another file or position to the text. */
  public static final String LINK =
    Config.get("resource:commands/link", "link");

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
      Command command = new Link("text");
      assertEquals("link", "\\link{text}", command.toString());

      command = new Link("text", "some url");
      assertEquals("optional", "\\link[some url]{text}", command.toString());

      command = new Link("text", "some url", "id");
      assertEquals("optional", "\\link[some url][id]{text}",
                   command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
