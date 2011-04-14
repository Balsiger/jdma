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
 * The page command.
 *
 * @file          Page.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Page extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Page ---------------------------------

  /**
   * The constructor for the page command.
   *
   * @param       inTitle             the title to set for the page
   * @param       inIcons             icons set on the page
   * @param       inMainPictures      the main pictures displayed on the page
   * @param       inSecondaryPictures additional pictures to show
   * @param       inTable             first part of the table data
   * @param       inText              the descriptive text
   * @param       inRemarks           the remarks to set
   *
   */
  public Page(@Nonnull Object inTitle, @Nonnull Object inIcons,
              @Nonnull Object inMainPictures,
              @Nonnull Object inSecondaryPictures, @Nonnull Object inTable,
              @Nonnull Object inText, @Nonnull Object inRemarks)
  {
    this();

    withArguments(inTitle, inIcons, inMainPictures, inSecondaryPictures,
                  inTable, inText, inRemarks);
  }

  //........................................................................
  //--------------------------------- Page ---------------------------------

  /**
   * This is the internal constructor for a command.
   *
   */
  protected Page()
  {
    super(NAME, 0, 7);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for a page. */
  public static final @Nonnull String NAME =
    Config.get("resource:commands/page", "page");

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
      Command command = new Page("title", "icons", "main pictures",
                                 "secondary pictures", "table", "text",
                                 "remarks");
      assertEquals("page",
                   "\\page{title}{icons}{main pictures}{secondary pictures}"
                   + "{table}{text}{remarks}", command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
