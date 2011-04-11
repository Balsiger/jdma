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
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The Editable command.
 *
 * @file          Editable.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Editable extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Editable -------------------------------

  /**
   * The constructor for the Editable command.
   *
   * @param       inID        the id of the editable element
   * @param       inEntry     the type of the editable element
   * @param       inText      the text of the element to display
   * @param       inKey       the key of the value edited
   * @param       inValue     the original value of the element
   * @param       inType      the type of the element to edit
   *
   */
  public Editable(@Nonnull Object inID, @Nonnull Object inEntry,
                  @Nonnull Object inText, @Nonnull Object inKey,
                  @Nonnull Object inValue, @Nonnull Object inType)
  {
    this();

    withArguments(inID, inEntry, inText, inKey, inValue, inType);
  }

  //........................................................................
  //------------------------------- Editable -------------------------------

  /**
   * The constructor for the Editable command.
   *
   * @param       inID        the id of the editable element
   * @param       inEntry     the type of the editable element
   * @param       inText      the text of the element to display
   * @param       inKey       the key of the value edited
   * @param       inValue     the original value of the element
   * @param       inType      the type of the element to edit
   * @param       inNote      a special not for editing
   *
   */
  public Editable(@Nonnull Object inID, @Nonnull Object inEntry,
                  @Nonnull Object inText, @Nonnull Object inKey,
                  @Nonnull Object inValue, @Nonnull Object inType,
                  @Nullable String inNote)
  {
    this(inID, inEntry, inText, inKey, inValue, inType);

    if(inNote != null && !inNote.isEmpty())
      withOptionals(inNote);
  }

  //........................................................................
  //------------------------------- Editable -------------------------------

  /**
   * The constructor for the Editable command.
   *
   * @param       inID        the id of the editable element
   * @param       inEntry     the type of the editable element
   * @param       inText      the text of the element to display
   * @param       inKey       the key of the value edited
   * @param       inValue     the original value of the element
   * @param       inType      the type of the element to edit
   * @param       inNote      a special note for editing
   * @param       inValues    special values for the given type
   *
   */
  public Editable(@Nonnull Object inID, @Nonnull Object inEntry,
                  @Nonnull Object inText, @Nonnull Object inKey,
                  @Nonnull Object inValue, @Nonnull Object inType,
                  @Nullable String inNote, @Nullable String inValues)
  {
    this(inID, inEntry, inText, inKey, inValue, inType, inNote);

    if(inValues != null && !inValues.isEmpty())
    {
      if(inNote == null || inNote.isEmpty())
        withOptionals("");

      withOptionals(inValues);
    }
  }

  //........................................................................
  //------------------------------- Editable -------------------------------

  /**
   * This is the internal constructor for a command.
   *
   */
  protected Editable()
  {
    super(EDITABLE, 2, 6);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for an editable value. */
  public static final @Nonnull String EDITABLE =
    Config.get("resource:commands/editable", "editable");

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test.*/
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- arguments ------------------------------------------------------

    /** Testing arguments. */
    @org.junit.Test
    public void testArguments()
    {
      Command command =
        new Editable("id", "entry", "text", "key", "value", "type");
      assertEquals("command",
                   "\\editable{id}{entry}{text}{key}{value}{type}",
                   command.toString());

      command = new Editable("id", "entry", "text", "key", "value",
                             "type", "script");
      assertEquals("command",
                   "\\editable[script]{id}{entry}{text}{key}{value}{type}",
                   command.toString());

      command = new Editable("id", "entry", "text", "key", "value", "type",
                             "script", "values");
      assertEquals("command",
                   "\\editable[script][values]{id}{entry}{text}{key}"
                   + "{value}{type}",
                   command.toString());

      command = new Editable("id", "entry", "text", "key", "value", "type",
                             "", "values");
      assertEquals("command",
                   "\\editable[][values]{id}{entry}{text}{key}{value}"
                   + "{type}",
                   command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
