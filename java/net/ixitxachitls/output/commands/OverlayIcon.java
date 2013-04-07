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

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.util.Pair;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The icon command for an icon with overlay.
 *
 * @file          OverlayIcon.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class OverlayIcon extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ OverlayIcon ------------------------------

  /**
   * The constructor for the overlay icon command.
   *
   * @param       inName      the file name of the icon (without extension)
   * @param       inCaption   the caption below the icon
   * @param       inLink      where the icon links to
   * @param       inHighlight a flag if icon is highlighted or not
   * @param       inOverlays  pairs of name and icons for the desired
   *                          overlays
   *
   */
  @SuppressWarnings("unchecked")
  public OverlayIcon(Object inName,
                     Object inCaption,
                     Object inLink, boolean inHighlight,
                     Pair<String, Object> ... inOverlays)
  {
    this();

    withArguments(inName, inCaption, inLink);

    for(Pair<String, Object> overlay : inOverlays)
      withArguments(overlay.first(), overlay.second());

    if(inHighlight)
      withOptionals("highlight");
  }

  //........................................................................
  //------------------------------ OverlayIcon ------------------------------

  /**
   * This is the internal constructor for a command.
   *
   */
  protected OverlayIcon()
  {
    super(NAME, 1, -1);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for adding an overlay icon. */
  public static final String NAME =
    Config.get("resource:commands/icon.overlay", "overlayicon");

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
    @SuppressWarnings("unchecked")
    public void arguments()
    {
      Command command = new OverlayIcon("name", "caption", "link",
                                        false);
      assertEquals("setup", "\\overlayicon{name}{caption}{link}",
                   command.toString());

      command = new OverlayIcon("name", "", "", true,
                                new Pair<String, Object>("overlay", "icon"),
                                new Pair<String, Object>("overlay2", "icon2"),
                                new Pair<String, Object>("overlay3", "icon3"));
      assertEquals("setup",
                   "\\overlayicon[highlight]{name}{}{}{overlay}{icon}"
                   + "{overlay2}{icon2}{overlay3}{icon3}", command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
