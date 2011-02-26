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

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

import net.ixitxachitls.output.Document;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This action allows the combination of multiple other actions into a single
 * new actions. The actions are executed first the first one, then the next
 * and so on. Thus the first action may take any arguments, but all following
 * must take exactly one argument.
 *
 * @file          Multi.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Multi extends Action
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Multi ------------------------------

  /**
   * Construct the action from multiple other actions.
   *
   * @param       inActions the actions to execute together
   *
   */
  public Multi(@Nonnull Action []inActions)
  {
    if(inActions.length <= 1)
      throw new IllegalArgumentException("at least two actions must be given");

    m_actions = Arrays.copyOf(inActions, inActions.length);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The actions stored representing this one. */
  protected @Nonnull Action []m_actions;

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
    if(inArguments == null || inArguments.isEmpty())
      throw new IllegalArgumentException("expecting at least one argument");

    Document doc = inDocument.createSubDocument();

    // treat first argument normally
    m_actions[0].execute(doc, inOptionals, inArguments);

    // fill each result to the next action
    for(int i = 1; i < m_actions.length; i++)
    {
      String argument = doc.toString();

      doc = inDocument.createSubDocument();
      m_actions[i].execute(doc, null, ImmutableList.of(argument));
    }

    inDocument.add(doc.toString());
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
      Action action =
        new Multi(new Action [] { new net.ixitxachitls.output.actions.ascii
                                  .List("* "),
                                  new net.ixitxachitls.output.actions.ascii
                                  .UpperCase(),
                                  new Pad('~') });

      Document doc = new net.ixitxachitls.output.ascii.ASCIIDocument(40);

      action.execute(doc, null, ImmutableList.of
                     ("first",
                      "second",
                      "third, now with some more text to make sure that "
                      + "word wrapping works as well, otherwise we'd have "
                      + "to make another debugging session, which I don't "
                      + "really need...",
                      "fourth"));

      assertEquals("action did not produce desired result",
                   "~*~FIRST~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"
                   + "~~*~SECOND~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"
                   + "~~~*~THIRD,~NOW~WITH~SOME~MORE~TEXT~TO~M\n"
                   + "AKE~~~SURE~THAT~WORD~WRAPPING~WORKS~AS~W\n"
                   + "ELL,~~~OTHERWISE~WE'D~HAVE~TO~MAKE~ANOTH\n"
                   + "ER~~~~~~DEBUGGING~SESSION,~WHICH~I~DON'T\n"
                   + "~~~~~~~~~REALLY~NEED...~~~~~~~~~~~~~~~~~\n"
                   + "~~~~~~~~*~FOURTH~",
                   doc.toString());

      action = new Multi(new Action []
        {
          new net.ixitxachitls.output.actions.ascii
          .Align(net.ixitxachitls.output.Buffer.Alignment.center),
          new net.ixitxachitls.output.actions.ascii.UpperCase(),
        });

      doc = new net.ixitxachitls.output.ascii.ASCIIDocument(40);

      action.execute(doc, null, ImmutableList.of("some text"));

      assertEquals("action did not produce desired result",
                   "                SOME TEXT               \n",
                   doc.toString());
  }

    //......................................................................
  }

  //........................................................................
}
