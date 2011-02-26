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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.Buffer;
import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.actions.Action;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the action used for alignment commands, the action aligns
 * the argument in the output writer.
 *
 * @file          Align.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 * @example       <PRE>
 * Action action = new Align("test", WrapBuffer.Alignment.RIGHT);
 * BaseWriter.Status status = new BaseWriter.Status(40);
 *
 * // get an execution of the action
 * Execution exec = action.getExecution();
 *
 * // add the argument
 * exec.startArgument(status);
 * exec.add("just a test for aligning");
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
public class Align extends Action
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Align -----------------------------

  /**
   * Construct the action.
   *
   * @param       inAlignment how to align the argument
   *
   */
  public Align(@Nonnull Buffer.Alignment inAlignment)
  {
    m_alignment = inAlignment;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The alignment of the text. */
  protected @Nonnull Buffer.Alignment m_alignment;

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
      throw new IllegalArgumentException("expected exactly one argument");

    // print into a sub document
    Document sub = inDocument.createSubDocument();

    sub.setAlignment(m_alignment);
    sub.add(inArguments.get(0));
    sub.add("\n");

    String contents = sub.toString();

    // check (and remove) the newline if it was not necessary
    if(contents.endsWith("\n\n"))
      contents = contents.substring(0, contents.length() - 1);

    inDocument.add(contents);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- left -----------------------------------------------------------

    /** Testing left. */
    @org.junit.Test
    public void left()
    {
      Action action = new Align(Buffer.Alignment.left);

      Document document = new net.ixitxachitls.output.ascii.ASCIIDocument(40);

      action.execute(document, null,
                     com.google.common.collect.ImmutableList.of
                     ("just a test for aligning"));

      assertEquals("execution did not produce desired result",
                   "just a test for aligning                \n",
                   document.toString());
    }

    //......................................................................
    //----- center ---------------------------------------------------------

    /** Testing center. */
    @org.junit.Test
    public void center()
    {
      Action action = new Align(Buffer.Alignment.center);

      Document document = new net.ixitxachitls.output.ascii.ASCIIDocument(40);

      action.execute(document, null,
                     com.google.common.collect.ImmutableList.of
                     ("just a test for aligning"));

      assertEquals("execution did not produce desired result",
                   "        just a test for aligning        \n",
                   document.toString());
    }

    //......................................................................
    //----- right ----------------------------------------------------------

    /** Testing right. */
    @org.junit.Test
    public void right()
    {
      Action action = new Align(Buffer.Alignment.right);

      Document document = new net.ixitxachitls.output.ascii.ASCIIDocument(40);

      action.execute(document, null,
                     com.google.common.collect.ImmutableList.of
                     ("just a test for aligning"));

      assertEquals("execution did not produce desired result",
                   "                just a test for aligning\n",
                   document.toString());
    }

    //......................................................................
    //----- block ----------------------------------------------------------

    /** Testing block. */
    @org.junit.Test
    public void block()
    {
      Action action = new Align(Buffer.Alignment.block);
      Document document = new net.ixitxachitls.output.ascii.ASCIIDocument(40);

      action.execute(document, null,
                     com.google.common.collect.ImmutableList.of
                     ("just a test for aligning, because this is for blocking "
                      + "the text has to be somewhat larger this time, but "
                      + "I think this is enough to fill more than a single "
                      + "line"));

      assertEquals("execution did not produce desired result",
                   "just a test  for  aligning, because this\n"
                   + "is  for  blocking  the  text  has  to be\n"
                   + "somewhat larger this  time,  but I think\n"
                   + "this  is  enough  to  fill  more  than a\n"
                   + "single line                             \n",
                   document.toString());
    }

    //......................................................................
  }

  //........................................................................
}
