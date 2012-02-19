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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.output.commands.Sub;
import net.ixitxachitls.output.commands.Super;

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
  @Override
public void execute(@Nonnull Document inDocument,
                      @Nullable List<? extends Object> inOptionals,
                      @Nullable List<? extends Object> inArguments)
  {
    if(inArguments == null || inArguments.size() < 2)
      throw new IllegalArgumentException("expecting two arguments");

    String integer     = null;

    if(inOptionals != null && !inOptionals.isEmpty())
      integer = inDocument.convert(inOptionals.get(0)).trim();

    if(integer != null)
      inDocument.add(integer);

    int nominator = 0;
    int denominator = 0;

    try
    {
      nominator   = Integer.parseInt(inDocument.convert(inArguments.get(0)));
      denominator = Integer.parseInt(inDocument.convert(inArguments.get(1)));
    }
    catch(NumberFormatException e)
    {
      if(integer != null)
        inDocument.add(" ");

      inDocument.add(inDocument.convert(inArguments.get(0)) + "/"
                     + inDocument.convert(inArguments.get(1)));

      return;
    }

    // some of the unicode characters below do not seem to be supported by
    // FOP (*sigh*)
    switch(nominator)
    {
      case 1:
        switch(denominator)
        {
          case 2:
            inDocument.add("&#x00BD;");

            return;

//           case 3:
//             inDocument.add("&#x2153;");

//             return;

          case 4:
            inDocument.add("&#x00BC;");

            return;

//           case 5:
//             inDocument.add("&#x2155;");

//             return;

//           case 6:
//             inDocument.add("&#x2159;");

//             return;

//           case 8:
//             inDocument.add("&#x215B;");

//             return;

          default:

            // just go on below
            break;
        }

        break;

//       case 2:
//         switch(denominator)
//         {
//           case 3:
//             inDocument.add("&#x2154;");

//             return;

//           case 5:
//             inDocument.add("&#x2156;");

//             return;
//         }

//         break;

      case 3:
        switch(denominator)
        {
          case 4:
            inDocument.add("&#x00BE;");

            return;

//           case 5:
//             inDocument.add("&#x2157;");

//             return;

//           case 8:
//             inDocument.add("&#x215C;");

//             return;

          default:

            // just go on below
            break;
        }

        break;

//       case 4:
//         switch(denominator)
//         {
//           case 5:
//             inDocument.add("&#x2158;");

//             return;
//         }

//         break;

//       case 5:
//         switch(denominator)
//         {
//           case 6:
//             inDocument.add("&#x215A;");

//             return;

//           case 8:
//             inDocument.add("&#x215D;");

//             return;
//         }

//         break;

//       case 7:
//         switch(denominator)
//         {
//           case 8:
//             inDocument.add("&#x215E;");

//             return;
//         }

//         break;

      default:

        // just go on with the normal behavior

        break;
    }

    // now set it 'by hand'
    inDocument.add(new Super(nominator));
    inDocument.add("/");
    inDocument.add(new Sub(denominator));
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

      net.ixitxachitls.output.Document doc =
        new net.ixitxachitls.output.Document();

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of("1", "2"));

      assertEquals("test", "&#x00BD;", doc.toString());

      doc = new net.ixitxachitls.output.html.HTMLDocument("title");

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of("12", "33"));

      assertEquals("test", "<sup>12</sup>/<sub>33</sub>",
                   doc.toString());

      doc = new net.ixitxachitls.output.Document();

      action.execute(doc,
                     com.google.common.collect.ImmutableList.of("5"),
                     com.google.common.collect.ImmutableList.of("3", "4"));

      assertEquals("test", "5&#x00BE;", doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
