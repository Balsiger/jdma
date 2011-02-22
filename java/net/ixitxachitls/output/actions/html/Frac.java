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

package net.ixitxachitls.output.actions.html;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.actions.Action;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a Fracding action. This action replaces a fraction with its HTML
 * equivalent.
 *
 * @file          Frac.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 * @example       <PRE>
 * Action action = new Frac("test");
 *
 * // get an execution of the action
 * Execution exec = action.getExecution();
 *
 * // add the arguments
 * exec.startArgument(null);
 * exec.add("1");
 * exec.stopArgument();
 * exec.startArgument(null);
 * exec.add("2");
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
public class Frac extends Action
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------------- Frac --------------------------------

  /**
   * Construct the action.
   *
   */
  public Frac()
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
    if(inArguments == null || inArguments.size() != 2)
      throw new IllegalArgumentException("expecting two arguments");

    String nominator   = inDocument.convert(inArguments.get(0)).trim();
    String denominator = inDocument.convert(inArguments.get(1)).trim();
    String integer     = null;

    if(inOptionals != null && !inOptionals.isEmpty())
      integer = inDocument.convert(inOptionals.get(0)).trim();

    if(nominator.equals("1") && denominator.equals("4"))
      inDocument.add(integer == null ? "&frac14;" : integer + "&frac14;");
    else
      if(nominator.equals("1") && denominator.equals("2"))
        inDocument.add(integer == null ? "&frac12;" : integer + "&frac12;");
      else
        if(nominator.equals("3") && denominator.equals("4"))
          inDocument.add(integer == null ? "&frac34;" : integer + "&frac34;");
        else
          if(integer == null)
            inDocument.add(nominator + "/" + denominator);
          else
            inDocument.add(integer + " " + nominator + "/" + denominator);
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
      Action action = new Frac();

      Document doc = new Document();

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of("1", "2"));

      assertEquals("test", "&frac12;", doc.toString());

      doc = new Document();

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of("12", "33"));

      assertEquals("test", "12/33", doc.toString());

      doc = new Document();

      action.execute(doc,
                     com.google.common.collect.ImmutableList.of("5"),
                     com.google.common.collect.ImmutableList.of("3", "4"));

      assertEquals("test", "5&frac34;", doc.toString());

      doc = new Document();

      action.execute(doc,
                     com.google.common.collect.ImmutableList.of("1"),
                     com.google.common.collect.ImmutableList.of("23", "42"));

      assertEquals("test", "1 23/42", doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
