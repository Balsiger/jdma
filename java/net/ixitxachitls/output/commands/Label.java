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
 * The label command.
 *
 * @file          Label.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Label extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Label ---------------------------------

  /**
   * The constructor for the label command.
   *
   * @param       inLabel the text to set label
   *
   */
  public Label(@Nonnull Object inLabel)
  {
    this(inLabel, false);
  }

  //........................................................................
  //-------------------------------- Label ---------------------------------

  /**
   * The constructor for the label command.
   *
   * @param       inLabel     the text to set label
   * @param       inHighlight flag if label should be highlighted on mouse
   *                          over
   *
   */
  public Label(@Nonnull Object inLabel, boolean inHighlight)
  {
    this();

    withArguments(inLabel);

    if(inHighlight)
      withArguments("highlight");
  }

  //........................................................................
  //-------------------------------- Label ---------------------------------

  /**
   * This is the internal constructor for a command.
   *
   */
  protected Label()
  {
    super(LABEL, 1, 1);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /**
   * Command for adding a label (a small icon or text in front of more
   * text).
   */
  public static final @Nonnull String LABEL =
    Config.get("resource:commands/label", "label");

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
      Command command = new Label("text");
      assertEquals("setup", "\\label{text}", command.toString());

      command = new Label("text", true);
      assertEquals("setup", "\\label{text}{highlight}", command.toString());

      command = new Label("text", false);
      assertEquals("setup", "\\label{text}", command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
