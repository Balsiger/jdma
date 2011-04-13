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

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a picture action. It formats a picture and if desired its caption
 * and a link to the real picture.
 *
 * @file          Span.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 * @example       <PRE>
 * Action action = new Span("test", "dir", "url", true, true);
 *
 * // get an execution of the action
 * Execution exec = action.getExecution();
 *
 * // add the arguments
 * exec.startOptionalArgument();
 * exec.add("optional");
 * exec.stopOptionalArgument();
 * exec.startArgument(null);
 * exec.add("picture.extension");
 * exec.stopArgument();
 * exec.startArgument(null);
 * exec.add("caption");
 * exec.stopArgument();
 * exec.startArgument(null);
 * exec.add("link");
 * exec.stopArgument();
 *
 * // now to the execute
 * String result = exec.execute(null));
 * </PRE>
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Span extends Action
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------------- Span --------------------------------

  /**
   * Construct the action.
   *
   */
  public Span()
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
      throw new IllegalArgumentException("exactly two arguments expected");

    String id   = inDocument.convert(inArguments.get(0));
    String text = inDocument.convert(inArguments.get(1));

    if(id.equalsIgnoreCase("key"))
      inDocument.add("<phrase no-wrap=\"true\">" + text + "\\ </phrase>");
    else
      if(id.equalsIgnoreCase("value"))
        inDocument.add("<phrase no-wrap=\"true\">" + text + ";</phrase>\\ ");
      else
        if(id.equalsIgnoreCase("key-dm"))
        {
          if(inDocument.isDM())
            inDocument.add("<font color=\"dm\"><phrase no-wrap=\"true\">"
                           + text + "\\ </phrase></font>");
        }
        else
          if(id.equalsIgnoreCase("value-dm"))
          {
            if(inDocument.isDM())
              inDocument.add("<font color=\"dm\"><phrase no-wrap=\"true\">"
                             + text + ";</phrase></font>\\ ");
          }
          else
            if(id.equalsIgnoreCase("dm"))
            {
              if(inDocument.isDM())
                inDocument.add(text);
            }
            else
              inDocument.add(text);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- all ------------------------------------------------------------

    /** The test for all. */
    @org.junit.Test
    public void all()
    {
      Action action = new Span();

      net.ixitxachitls.output.Document doc =
        new net.ixitxachitls.output.Document();

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of("id", "text"));

      assertEquals("normal", "text", doc.toString());

      doc = new net.ixitxachitls.output.Document();
      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of("key", "text"));

      assertEquals("normal",
                   "<phrase no-wrap=\"true\">text\\ </phrase>",
                   doc.toString());

      doc = new net.ixitxachitls.output.Document();
      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of("value",
                                                                "text"));

      assertEquals("normal",
                   "<phrase no-wrap=\"true\">text;</phrase>\\ ",
                   doc.toString());

      doc = new net.ixitxachitls.output.Document();
      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of("key-dm",
                                                                "text"));

      assertEquals("normal", "", doc.toString());

      doc = new net.ixitxachitls.output.Document();
      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of("value-dm",
                                                                "text"));

      assertEquals("normal", "", doc.toString());

      doc = new net.ixitxachitls.output.Document();
      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of("dm", "text"));

      assertEquals("normal", "", doc.toString());

      doc = new net.ixitxachitls.output.Document(true);
      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of("key-dm",
                                                                "text"));

      assertEquals("normal",
                   "<font color=\"dm\"><phrase no-wrap=\"true\">"
                   + "text\\ </phrase></font>",
                   doc.toString());

      doc = new net.ixitxachitls.output.Document(true);
      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of("value-dm",
                                                                "text"));

      assertEquals("normal",
                   "<font color=\"dm\"><phrase no-wrap=\"true\">"
                   + "text;</phrase></font>\\ ",
                   doc.toString());

      doc = new net.ixitxachitls.output.Document(true);
      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of("dm", "text"));

      assertEquals("normal", "text", doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
