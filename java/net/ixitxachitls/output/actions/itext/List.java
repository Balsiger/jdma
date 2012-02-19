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

package net.ixitxachitls.output.actions.itext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.actions.Action;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * Formatting of a list.
 *
 * @file          List.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class List extends Action
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- List ---------------------------------

  /**
   * Standard constructor.
   */
  public List()
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

  //------------------------------- execute --------------------------------

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
                      @Nullable java.util.List<? extends Object> inOptionals,
                      @Nullable java.util.List<? extends Object> inArguments)
  {
    boolean nested = inDocument.getAttribute("list.nested") != null;

    if(nested)
      // ujac does not correctly handle nested lists, we have to
      // close an enclosing list item before being able to add a nested list
      inDocument.add("</list-item>");
    else
      inDocument.setAttribute("list.nested", "true");


    inDocument.add("<list numbered=\"false\" symbol-indent=\"10\">");

    if(inArguments != null)
      for(Object argument : inArguments)
    {
      inDocument.add("<list-symbol>\u2022</list-symbol>");
      inDocument.add("<list-item>");
      inDocument.add(argument);
      inDocument.add("</list-item>");
    }

    inDocument.add("</list>");

    if(nested)
        inDocument.add("<list-symbol>\uF8FF</list-symbol><list-item>");
    else
      inDocument.setAttribute("list.nested", null);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- normal ---------------------------------------------------------

    /** The normal Test. */
    @org.junit.Test
    public void normal()
    {
      Action action = new List();

      net.ixitxachitls.output.Document doc =
        new net.ixitxachitls.output.Document();

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of
                     ("aaa", "bbb", "ccc"));

      assertEquals("simple",
                   "<list numbered=\"false\" symbol-indent=\"10\">"
                   + "<list-symbol>•</list-symbol><list-item>aaa</list-item>"
                   + "<list-symbol>•</list-symbol><list-item>bbb</list-item>"
                   + "<list-symbol>•</list-symbol><list-item>ccc</list-item>"
                   + "</list>",
                   doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
