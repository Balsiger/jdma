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

import net.ixitxachitls.output.Document;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a delimiter action, allowing to add delimiters before any arguments,
 * between arguments and at the end.
 *
 * @file          Bold.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Delimiter extends Action
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Delimiter ------------------------------

  /**
   * Construct the action, mainly by giving the delimiters to use. Any of
   * the delimiters given can be null, in which case they are ignored.
   *
   * @param       inStart    the delimiter right at the beginning
   * @param       inEnd      the delimiter totally at the end
   * @param       inArgStart the delimiters used before the arguments;
   *                         these delimiters given here are used round
   *                         robin before the arguments found, i.e. if 3
   *                         are given here and 5 arguments are found, then
   *                         the first 3 arguments use delimiter 1 to 3 and
   *                         arguments 4 and 5 again use delimiters 1 and 2;
   *                         if a delimiter is 'null', then the argument is
   *                         ignored
   * @param       inArgEnd   the delimiters at the end of the arguments,
   *                         again round robin
   * @param       inOptStart the delimiters before an optional argument
   *                         (start and end need to be null to prevent the
   *                         optional argument from being printed)
   * @param       inOptEnd   the delimiters after an optional argument
   *
   */
  public Delimiter(@Nullable String inStart, @Nullable String inEnd,
                   @Nullable String []inArgStart, @Nullable String []inArgEnd,
                   @Nullable String []inOptStart, @Nullable String []inOptEnd)
  {
    m_start = inStart;
    m_end = inEnd;

    if(inArgStart != null)
      m_argStart = Arrays.copyOf(inArgStart, inArgStart.length);

    if(inArgEnd != null)
      m_argEnd = Arrays.copyOf(inArgEnd, inArgEnd.length);

    if(inOptStart != null)
      m_optStart = Arrays.copyOf(inOptStart, inOptStart.length);

    if(inOptEnd != null)
      m_optEnd = Arrays.copyOf(inOptEnd, inOptEnd.length);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Delimiter at the beginning. */
  private @Nullable String m_start;

  /** Delimiter at the end. */
  private @Nullable String m_end;

  /** Delimiters before arguments, null delimiters ignore argument. */
  private @Nullable String []m_argStart;

  /** Delimiters after arguments, null delimiters ignore argument. */
  private @Nullable String []m_argEnd;

  /** Delimiter before optional argument. */
  private @Nullable String []m_optStart;

  /** Delimiter after optional argument. */
  private @Nullable String []m_optEnd;

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
   * @undefined   never
   *
   */
  @Override
public void execute(@Nonnull Document inDocument,
                      @Nullable List<? extends Object> inOptionals,
                      @Nullable List<? extends Object> inArguments)
  {
    // starting delimiter
    if(m_start != null)
      inDocument.add(m_start);

    // optional argument delimiters
    boolean start = m_optStart != null   && m_optStart.length != 0;
    boolean end   = m_optEnd   != null   && m_optEnd.length   != 0;

    if(inOptionals != null && (start || end))
      for(int i = 0; i < inOptionals.size(); i++)
      {
        if((!start || m_optStart[i % m_optStart.length] == null)
           && (!end || m_optEnd[i % m_optEnd.length] == null))
          continue;

        if(start && m_optStart[i % m_optStart.length] != null)
          inDocument.add(m_optStart[i % m_optStart.length]);

        inDocument.add(inOptionals.get(i));

        if(end && m_optEnd[i % m_optEnd.length] != null)
          inDocument.add(m_optEnd[i % m_optEnd.length]);
      }

    // argument delimiters
    start = m_argStart != null && m_argStart.length != 0;
    end   = m_argEnd != null && m_argEnd.length != 0;

    if(start || end)
      for(int i = 0; i < inArguments.size(); i++)
      {
        if((!start || m_argStart[i % m_argStart.length] == null)
           && (!end || m_argEnd[i % m_argEnd.length] == null))
          continue;

        if(start && m_argStart[i % m_argStart.length] != null)
          inDocument.add(m_argStart[i % m_argStart.length]);

        inDocument.add(inArguments.get(i));

        if(end && m_argEnd[i % m_argEnd.length] != null)
          inDocument.add(m_argEnd[i % m_argEnd.length]);
      }

    // end delimiter
    if(m_end != null)
      inDocument.add(m_end);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- normal ---------------------------------------------------------

    /** Test normal behavior. */
    @org.junit.Test
    public void normal()
    {
      Action action = new Delimiter("start", "end",
                                    new String [] { "1-A", "2-A", "3-A" },
                                    new String [] { "1-E", "2-E", "3-E" },
                                    null, null);

      Document doc = new Document();

      action.execute(doc, null, com.google.common.collect.ImmutableList.of
                     ("first", "second", "third"));

      assertEquals("action did not produce desired result",
                   "start1-Afirst1-E2-Asecond2-E3-Athird3-Eend",
                   doc.toString());
    }

    //......................................................................
    //----- more -----------------------------------------------------------

    /** Some more tests. */
    @org.junit.Test
    public void more()
    {
      Action action = new Delimiter(null, null,
                                    new String [] { "1-A", "2-A", "3-A" },
                                    new String [] { "1-E", "2-E", "3-E" },
                                    null, null);

      Document doc = new Document();

      action.execute(doc, null, com.google.common.collect.ImmutableList.of
                     ("first", "second", "third", "fourth", "fifth"));

      assertEquals("action did not produce desired result",
                   "1-Afirst1-E2-Asecond2-E3-Athird3-E1-Afourth1-E2-Afifth2-E",
                   doc.toString());
    }

    //......................................................................
    //----- single ---------------------------------------------------------

    /** Test with a single delimiter. */
    @org.junit.Test
    public void single()
    {
      Action action = new Delimiter(null, null,
                                    new String [] { "1-A", },
                                    new String [] { "1-E", }, null, null);

      Document doc = new Document();

      action.execute(doc, null, com.google.common.collect.ImmutableList.of
                     ("first", "second", "third", "fourth", "fifth"));

      assertEquals("action did not produce desired result",
                   "1-Afirst1-E1-Asecond1-E1-Athird1-E1-Afourth1-E1-Afifth1-E",
                   doc.toString());
    }

    //......................................................................
    //----- null -----------------------------------------------------------

    /** Test with null. */
    @org.junit.Test
    public void nullCheck()
    {
      Action action = new Delimiter(null, null, null, null,
                                    null, null);

      Document doc = new Document();

      action.execute(doc, null, com.google.common.collect.ImmutableList.of
                     ("first", "second"));

      assertEquals("action did not produce desired result",
                   "",
                   doc.toString());
    }

    //......................................................................
    //----- optional -------------------------------------------------------

    /** Test optional arguments. */
    @org.junit.Test
    public void optional()
    {
      Action action = new Delimiter(null, null,
                                    new String [] { "1-A", null, "2-A"},
                                    new String [] { "1-E", null, null },
                                    new String [] { "O-A" },
                                    new String [] { "O-E1", "O-E2", });

      Document doc = new Document();

      action.execute(doc, com.google.common.collect.ImmutableList.of
                     ("optional1", "optional2"),
                     com.google.common.collect.ImmutableList.of
                     ("first", "second", "third", "fourth", "fifth"));

      assertEquals("action did not produce desired result",
                   "O-Aoptional1O-E1O-Aoptional2O-E2"
                   + "1-Afirst1-E2-Athird1-Afourth1-E",
                   doc.toString());

      doc = new Document();

      action.execute(doc, null, com.google.common.collect.ImmutableList.of
                     ("first", "second", "third", "fourth", "fifth"));

      assertEquals("action did not produce desired result",
                   "1-Afirst1-E2-Athird1-Afourth1-E",
                   doc.toString());
   }

    //......................................................................
  }

  //........................................................................
}
