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

package net.ixitxachitls.output.actions.ascii;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.actions.Action;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * An action to make the argument upper case.
 *
 * @file          Hrule.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 * @example       <PRE>
 * Action action = new Hrule("test");
 *
 * // get an execution of the action
 * Execution exec = action.getExecution();
 *
 * // add the arguments
 * exec.startArgument(null);
 * exec.add("  this is some text to see\n\nhow it is      upper cased   ");
 * exec.stopArgument();
 *
 * // now to the execute
 * String result = exec.execute(null);
 * </PRE>
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Hrule extends Action
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Hrule ------------------------------

  /**
   * Construct the action.
   *
   */
  public Hrule()
  {
    // nothing to do here
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The ruler characters. */
  private static final @Nonnull String s_rule =
    "-------------------------------------------------------------------------"
    + "-----------------------------------------------------------------------"
    + "-----------------------------------------------------------------------"
    + "----------------------------------------------------------------------";

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
    int width = inDocument.getWidth();

    if(inOptionals != null && inOptionals.size() >= 1)
      width =
        width * Integer.parseInt(inDocument.convert(inOptionals.get(0))) / 100;

    inDocument.add("\n" + s_rule.substring(0, width) + "\n");
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- all ------------------------------------------------------------

    /** Testing all. */
    @org.junit.Test
    public void all()
    {
      Action action = new Hrule();

      Document doc = new net.ixitxachitls.output.ascii.ASCIIDocument(20);

      action.execute(doc, null, null);
      action.execute(doc,
                     com.google.common.collect.ImmutableList.of("50"), null);

      assertEquals("execution did not produce desired result",
                   "\n"
                   + "--------------------\n"
                   + "\n"
                   + "----------          \n",
                   doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
