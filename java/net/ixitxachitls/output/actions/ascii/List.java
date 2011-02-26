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

package net.ixitxachitls.output.actions.ascii;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.util.Strings;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the action used to set lists.
 *
 * @file          List.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 * @example       <PRE>
 * Action action = new List("test", " + ");
 * BaseWriter.Status status = new BaseWriter.Status(40);
 *
 * // get an execution of the action
 * Execution exec = action.getExecution();
 *
 * // add the arguments
 * exec.startArgument(status);
 * exec.add("first");
 * exec.stopArgument();
 * exec.startArgument(status);
 * exec.add("second");
 * exec.stopArgument();
 * exec.startArgument(status);
 * exec.add("third, now with some more text to make sure that word ");
 * exec.add("wrapping works as well, otherwise we'd have to make another ");
 * exec.add("debugging session, which I don't really need...");
 * exec.stopArgument();
 * exec.startArgument(status);
 * exec.add("fourth");
 * exec.stopArgument();
 *
 * // now to the execute
 * String result = exec.execute(status);
 * </PRE>
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class List extends Action
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------------- List -------------------------------

  /**
   * Construct the list action.
   *
   * @param       inBullet the text to use as bullet (including spaces)
   *
   */
  public List(@Nonnull String inBullet)
  {
    m_bullet = inBullet;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The text to use as the bullet, including any delimiters (spaces). */
  protected @Nonnull String m_bullet;

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
                      @Nullable java.util.List<? extends Object> inOptionals,
                      @Nullable java.util.List<? extends Object> inArguments)
  {
    if(inArguments == null || inArguments.isEmpty())
      throw new IllegalArgumentException("expecting at least one argument");

    int bullet = m_bullet.length();
    int width  = inDocument.getWidth() - bullet;

    if(width < 1)
      width = 2;

    for(int i = 0; i < inArguments.size(); i++)
    {
      inDocument.add(m_bullet);

      Document.SubDocument doc = inDocument.createSubDocument(width);
      doc.add(inArguments.get(i));

      for(String line = doc.getLine(); line != null; line = doc.getLine())
      {
        inDocument.add(line);
        inDocument.add("\n");
        inDocument.add(Strings.spaces(bullet));
      }

      inDocument.add(doc.toString());

      if(i + 1 < inArguments.size())
        inDocument.add("\n");
    }
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
      Action action = new List(" + ");

      Document doc = new net.ixitxachitls.output.ascii.ASCIIDocument(40);

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of
                     ("first",
                      "second",
                      "third, now with some more text to make sure that "
                      + "word wrapping works as well, otherwise we'd have "
                      + "to make another debugging session, which I don't "
                      + "really need...",
                      "fourth"));

      assertEquals("action did not produce desired result",
                   " + first                                \n"
                   + " + second                               \n"
                   + " + third, now with some more text to    \n"
                   + "   make sure that word wrapping works as\n"
                   + "   well, otherwise we'd have to make    \n"
                   + "   another debugging session, which I   \n"
                   + "   don't really need...                 \n"
                   + " + fourth",
                   doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
