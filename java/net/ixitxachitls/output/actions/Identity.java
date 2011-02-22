/******************************************************************************
 * Copyright (c) 2002,2003 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.Document;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a simple class used to do nothing, an action that just
 * prints out its arguments again.
 *
 * @file          Identity.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Identity extends Action
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Identity -----------------------------

  /**
   * Create the action.
   *
   */
  public Identity()
  {
  }

  //........................................................................
  //--------------------------------- Identity -----------------------------

  /**
   * Convert the identity action with the given order.
   *
   * @param       inOrder the order of the arguments to print out again
   *                      (negative numbers stand for the appropriate optional
   *                       arguments), starting with number 1 for the first
   *                      argument
   *
   */
  public Identity(@Nonnull int ... inOrder)
  {
    m_order = inOrder;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The order in which to print out the arguments again. */
  private @Nullable int []m_order;

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- execute --------------------------------

  /**
   * Execute the action onto the given document.
   *
   * @param       inDocument  the document to output to
   * @param       inOptionals the optional argument
   * @param       inArguments the arguments
   *
   */
  public void execute(@Nonnull Document inDocument,
                      @Nullable List<? extends Object> inOptionals,
                      @Nullable List<? extends Object> inArguments)
  {
    if(inArguments == null || inArguments.size() < 1)
      throw new IllegalArgumentException("expecting at least one argument");

    if(m_order == null)
      return;

    for(int i = 0; i < m_order.length; i++)
      if(m_order[i] < 0)
        inDocument.add(inOptionals.get(-m_order[i] - 1));
      else
        inDocument.add(inArguments.get(m_order[i] - 1));
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- all ------------------------------------------------------------

    /** Test all. */
    @org.junit.Test
    public void all()
    {
      Action action = new Identity(2, 1, -1, 3);

      Document doc = new Document();

      action.execute(doc,
                     com.google.common.collect.ImmutableList.of("optional"),
                     com.google.common.collect.ImmutableList.of("first",
                                                                "second",
                                                                "third"));

      assertEquals("execution did not produce desired result",
                   "secondfirstoptionalthird",
                   doc.toString());

      action = new Identity();

      doc = new Document();

      action.execute(doc,
                     com.google.common.collect.ImmutableList.of("opt"),
                     com.google.common.collect.ImmutableList.of("arg"));

      assertEquals("empty", "", doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
