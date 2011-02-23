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
 * The textblock command.
 *
 * @file          Textblock.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Textblock extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Textblock ------------------------------

  /**
   * The constructor for the textblock command.
   *
   * @param       inText the text to set in the text block
   *
   */
  public Textblock(@Nonnull Object inText)
  {
    this();

    withArguments(inText);
  }

  //........................................................................
  //------------------------------- Textblock ------------------------------

  /**
   * The constructor for the textblock command.
   *
   * @param       inText  the text to set in the text block
   * @param       inClass the specific output class to use for this text
   *                      block (for html)
   *
   */
  public Textblock(@Nonnull Object inText, @Nonnull String inClass)
  {
    this(inText);

    withOptionals(inClass);
  }

  //........................................................................
  //------------------------------- Textblock ------------------------------

  /**
   * This is the internal constructor for a command.
   *
   */
  protected Textblock()
  {
    super(TEXT_BLOCK, 1, 1);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for adding a text block. */
  public static final String TEXT_BLOCK =
    Config.get("resource:commands/textblock", "textblock");

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
      Command command = new Textblock("text");
      assertEquals("setup", "\\textblock{text}", command.toString());

      command = new Textblock("text", "class");
      assertEquals("setup", "\\textblock[class]{text}", command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
