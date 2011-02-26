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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.output.Document;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base class for all action used to actually carry through
 * command embedded in the text printed to a BaseWriter or derived class
 * there of.
 *
 * @file          Action.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 * @example       <PRE>
 * Action action = new Action("test", 2, true);
 *
 * // get an execution of the action
 * Execution exec = action.getExecution();
 *
 * // start the optional argument
 * exec.startOptionalArgument();
 *
 * // add a text
 * exec.add("optional argument ");
 * exec.add("test");
 *
 * // terminate the first argument
 * exec.stopOptionalArgument();
 *
 * // start a new argument
 * exec.startArgument(null);
 *
 * // add a text
 * exec.add("first argument ");
 * exec.add("test");
 *
 * // terminate the first argument
 * exec.stopArgument();
 *
 * // start the second argument
 *  exec.startArgument(null);
 *
 * // add some text and end the argument
 * exec.add("2nd argument");
 * exec.stopArgument();
 *
 * // now to the execute
 * String result = exec.execute(null);
 * </PRE>
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Action
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Action --------------------------------

  /**
   * Create the action, with the given name and the given number of arguments
   * to look for.
   *
   */
  public Action()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables
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
    // here we just print out all the arguments
    if(inOptionals != null)
      for(Object optional : inOptionals)
        inDocument.add(optional);

    if(inArguments != null)
      for(Object argument : inArguments)
        inDocument.add(argument);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /**
   * The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- all ------------------------------------------------------------

    /** Test all. */
    @org.junit.Test
    public void all()
    {
      Action action = new Action();

      Document doc = new Document();

      action.execute(doc, null, null);
      assertEquals("empty", "", doc.toString());

      doc = new Document();
      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of("1", "2", "3"));
      assertEquals("args", "123", doc.toString());

      doc = new Document();
      action.execute(doc,
                     com.google.common.collect.ImmutableList.of("a", "b", "c"),
                     com.google.common.collect.ImmutableList.of("1", "2", "3"));
      assertEquals("opts", "abc123", doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
