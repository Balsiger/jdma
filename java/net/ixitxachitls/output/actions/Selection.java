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

package net.ixitxachitls.output.actions;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.commands.Color;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * An action encapsulating various actions, selected by an argument of the
 * command.
 *
 *
 * @file          Selection.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Selection extends Action
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ Selection -------------------------------

  /**
   * Create the selection.
   *
   * @param       inIndex   the index of the argument specifying the action
   * @param       inDefault the index of the default argument to use if not
   *                        no action is defined
   * @param       inActions the map with all possible actions
   *
   */
  public Selection(int inIndex, int inDefault,
                   @Nonnull Map<String, Action> inActions)
  {
    m_index = inIndex;
    m_default = inDefault;
    m_actions = inActions;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The index for selecting the actions. */
  private int m_index;

  /** The index of the default argument for no action. */
  private int m_default;

  /** The possible actions. */
  private @Nonnull Map<String, Action> m_actions;

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  /**
   * Execute the action onto the given document.
   *
   * @param       inDocument  the document to output to
   * @param       inOptionals the optional argument
   * @param       inArguments the arguments
   *
   */
  @Override
public void execute(@Nonnull Document inDocument,
                      @Nullable List<? extends Object> inOptionals,
                      @Nullable List<? extends Object> inArguments)
  {
    if(inArguments == null || inArguments.isEmpty())
      throw new IllegalArgumentException("expecting at least one argument");

    if(inArguments.size() <= m_index)
    {
      inDocument.add(new Color("error", "not enough arguments for selection"));
      return;
    }

    String select = inDocument.convert(inArguments.get(m_index));
    Action action = m_actions.get(select);
    if(action == null)
    {
      if(inArguments.size() <= m_default)
        inDocument.add(new Color("error", "could not find default argument"));

      inDocument.add(inArguments.get(m_default));
      return;
    }

    action.execute(inDocument, inOptionals, inArguments);
  }

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- all ------------------------------------------------------------

    /** The all Test. */
    @org.junit.Test
    public void all()
    {
      Action action =
        new Selection(0, 1, com.google.common.collect.ImmutableMap.of
                      ("first", (Action)new Pattern("hello $2")));

      Document doc = new Document();
      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of
                     ("first", "there"));
      assertEquals("output not expected", "hello there", doc.toString());

      doc = new Document();
      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of
                     ("second", "there"));
      assertEquals("output not expected", "there", doc.toString());
    }

    //......................................................................
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
