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
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.commands.Super;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a footnoteding action, allow to replace all white spaces with a given
 * character.
 *
 * @file          Footnote.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Footnote extends Action
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Footnote -----------------------------

  /**
   * Construct the action.
   *
   */
  public Footnote()
  {
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
    if(inArguments == null || inArguments.size() != 1)
      throw new IllegalArgumentException("expecting one argument");

    String marker = null;

    if(inOptionals == null || inOptionals.isEmpty())
      marker = "" + inDocument.getFootnoteCounter();
    else
      marker = inDocument.convert(inOptionals.get(0));

    inDocument.add(new Super(marker));

    inDocument.addFootnote(marker, inArguments.get(0));
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- all ------------------------------------------------------------

    /** Test everything. */
    @org.junit.Test
    public void all()
    {
//       Action action = new Footnote();

//       Document doc = new net.ixitxachitls.output.ASCIIDocument(40);

//       action.execute(doc, null, com.google.common.collect.ImmutableList.of
//                      ("just some footnote"));
//       action.execute(doc, com.google.common.collect.ImmutableList.of("*"),
//                      com.google.common.collect.ImmutableList.of
//                      ("another footnote"));

//       assertEquals("execution",
//                    "(1)(*)\n\n\n"
//                    + "------------                            \n"
//                    + "1)  just some footnote                  \n"
//                    + "*)  another footnote                    \n",
//                    doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
