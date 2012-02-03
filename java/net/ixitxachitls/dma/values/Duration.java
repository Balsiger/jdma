/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.values;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a duration value. It's a convenience class for the
 * Units class.
 *
 * @file          Duration.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Duration extends Units<Duration>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ Duration --------------------------------

  /**
   * Construct the duration object with an undefined value.
   *
   */
  public Duration()
  {
    super(s_sets, 5);
  }

  //........................................................................
  //------------------------------ Duration --------------------------------

  /**
   * Construct the duration object.
   *
   * @param       inDays    the number of days
   * @param       inHours   the number of hours
   * @param       inMinutes the number of minutes
   * @param       inSeconds the number of seconds
   *
   */
  public Duration(@Nullable Rational inDays, @Nullable Rational inHours,
                  @Nullable Rational inMinutes, @Nullable Rational inSeconds)
  {
    super(new Rational [] { inDays, inHours, inMinutes, inSeconds },
          s_sets, s_sets[0], 5);
  }

  //........................................................................
  //------------------------------ Duration --------------------------------

  /**
   * Construct the duration object.
   *
   * @param       inRounds the number of rounds
   *
   */
  public Duration(@Nonnull Rational inRounds)
  {
    super(new Rational [] { inRounds }, s_sets, s_sets[1], 5);
  }

  //........................................................................

  //------------------------------- create ---------------------------------

  /**
   * Create a new list with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar list, but without any contents
   *
   */
  public @Nonnull Duration create()
  {
    return super.create(new Duration());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The definition of the unit value.
   *
   * The action values have a divider of 1/10 each, although this is not really
   * true. But this prevents them from being reduced to rounds.  Adding these
   * values and having many of these values might fail, though.  Furthermore,
   * the number of seconds extracted from this value will probably be wrong.
   */
  private static @Nonnull String s_definition =
    Config.get("/rules/duration.units",
               "1/1    : Seconds = 86400/1 : day|days,"
               + "                    3600/1 : hour|hours|hour : hr|hrs,"
               + "                      60/1 : minute|minutes  : min|mins,"
               + "                       1/1 : second|seconds  : sec|secs."
               + "1/6    : Rounds =      1/1 : round|rounds    : rd|rds."
               + "1/6    : Actions =     1/1 : round|rounds    : rd|rds,"
               + "                    1/10 : standard action|standard actions,"
               + "                   1/100 : move action|move actions,"
               + "                  1/1000 : swift action|swift actions,"
               + "                 1/10000 : free action|free actions,"
               + "                    60/1 : minute|minutes : min|mins.");

  /** The sets of units for this value. */
  private static final @Nonnull Set []s_sets = parseDefinition(s_definition);

  /** The grouping for durations (time). */
  protected static final Group<Duration, Long, String> s_timeGrouping =
    new Group<Duration, Long, String>(new Group.Extractor<Duration, Long>()
      {
        public Long extract(@Nonnull Duration inValue)
        {
          return (long)inValue.getAsSeconds().getValue();
        }
      }, new Long [] { 0L, 1L, 6L, 60L, 5 * 60L, 10 * 60L, 15 * 60L, 30 * 60L,
                       45 * 60L, 60 * 60L, 2 * 60 * 60L, 4 * 60 * 60L,
                       6 * 60 * 60L, 12 * 60 * 60L, 24 * 60 * 60L, },
                               new String []
      { "0 seconds", "1 second", "6 seconds", "1 minute", "5 minutes",
        "10 minutes", "15 minutes", "30 minutes", "45 minutes", "1 hour",
        "2 hours", "4 hours", "6 hours", "12 hours", "1 day", "very long",
      }, "$undefined$");

  /** The grouping for durations (rounds). */
  protected static final Group<Duration, Long, String> s_roundGrouping =
    new Group<Duration, Long, String>(new Group.Extractor<Duration, Long>()
      {
        public Long extract(@Nonnull Duration inValue)
        {
          return (long)inValue.getAsRounds().getValue() * 10000;
        }
      }, new Long [] { 0L, 1L, 10L, 100L, 1000L, 10000L, 2 * 10000L,
                       3 * 10000L, 5 * 10000L, 10 * 10000L,
                       5 * 10 * 10000L, 10 * 10 * 10000L, 15 * 10 * 10000L,
                       30 * 10 * 10000L, 45 * 10 * 10000L,
                       60 * 10 * 10000L, 2 * 60 * 10 * 10000L,
                       4 * 60 * 10 * 10000L, 6 * 60 * 10 * 10000L,
                       12 * 60 * 10 * 10000L, 24 * 60 * 10 * 10000L, },
                        new String []
        { "0 rounds", "1 free action", "1 swift action", "1 move action",
          "1 standard action", "1 round", "2 rounds", "3 rounds", "5 rounds",
          "10 rounds", "5 minutes", "10 minutes", "15 minutes", "30 minutes",
          "45 minutes", "1 hour", "2 hours", "4 hours", "6 hours", "12 hours",
          "1 day", "very long",
        }, "$undefined$");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- isMetric -------------------------------

  /**
   * Determine if metric values are stored.
   *
   * @return      true if metric values are stored, false else
   *
   */
  public boolean isMetric()
  {
    return m_set == m_sets[0];
  }

  //........................................................................
  //------------------------------ isRounds --------------------------------

  /**
   * Determine if round values are stored.
   *
   * @return      true if round values are stored, false else
   *
   */
  public boolean isRounds()
  {
    return m_set == m_sets[1];
  }

  //........................................................................

  //----------------------------- getAsRounds ------------------------------

  /**
   * Get the value as an approximate equivalent of rounds.
   *
   * @return      the rounds
   *
   */
  public @Nonnull Rational getAsRounds()
  {
    if(!isDefined())
      return new Rational(0);

    if(m_set == m_sets[1])
      return getAsBase();

    return toSet(1, false).getAsBase();
  }

  //........................................................................
  //----------------------------- getAsSeconds -----------------------------

  /**
   * Get the value as an approximate equivalent of seconds.
   *
   * @return      the seconds
   *
   */
  public @Nonnull Rational getAsSeconds()
  {
    if(!isDefined())
      return new Rational(0);

    if(m_set == m_sets[0])
      return getAsBase();

    return toSet(0, false).getAsBase();
  }

  //........................................................................

  //------------------------------- asMetric -------------------------------

  /**
   * Convert this value into a metric value.
   *
   * @return      the corresponding metric value.
   *
   */
  public @Nonnull Duration asMetric()
  {
    if(m_set == m_sets[0])
      return this;

    return toSet(0, true);
  }

  //........................................................................
  //------------------------------- asRounds -------------------------------

  /**
   * Convert this value into a feet value.
   *
   * @return      the corresponding feet value.
   *
   */
  public @Nonnull Duration asRounds()
  {
    if(m_set == m_sets[1])
      return this;

    return toSet(1, true);
  }

  //........................................................................

  //------------------------------- doGroup --------------------------------

  /**
   * Return the group this value belongs to.
   *
   * @return      a string denoting the group this value is in
   *
   */
  public @Nonnull String doGroup()
  {
    if(isMetric())
      return s_timeGrouping.group(this);

    return s_roundGrouping.group(this);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------ asMetric --------------------------------

  /**
   * Set the weight as metric value.
   *
   * @param       inDays    the number of days
   * @param       inHours   the number of hours
   * @param       inMinutes the number of minutes
   * @param       inSeconds the number of seconds
   *
   * @return      true if set, false if values invalid
   *
   */
  public @Nonnull Duration asMetric(@Nullable Rational inDays,
                                    @Nullable Rational inHours,
                                    @Nullable Rational inMinutes,
                                    @Nullable Rational inSeconds)
  {
    return as(new Rational [] { inDays, inHours, inMinutes, inSeconds }, 0);
  }

  //........................................................................
  //------------------------------ setRounds -------------------------------

  /**
   * Set the weight as metric value.
   *
   * @param       inRounds the number of rounds
   *
   * @return      true if set, false if values invalid
   *
   */
  public @Nonnull Duration asRounds(@Nonnull Rational inRounds)
  {
    return as(new Rational [] { inRounds }, 1);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The Test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** Testing init. */
    @org.junit.Test
    public void init()
    {
      Duration value = new Duration();

      // undefined value
      assertEquals("not undefined at start", false, value.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   value.toString());
      assertEquals("undefined value not correct", "\\color{error}{$undefined$}",
                   value.format(false).toString());
      assertEquals("feet",   false, value.isRounds());
      assertEquals("metric", false, value.isMetric());
      assertEquals("undefined value not correct", "0",
                   value.getAsSeconds().toString());
      assertEquals("undefined value not correct", "0",
                   value.getAsRounds().toString());

      // now with some value (pounds)
      value = new Duration(null, null, null, new Rational(1, 1, 2));

      //System.out.println(value);
      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("string ", "1 1/2 seconds", value.toString());
      assertEquals("string ", "\\window{\\span{unit}{\\frac[1]{1}{2} seconds}}"
                   + "{\\table{#inline#1:L,,;100:L}"
                   + "{Total:}{\\frac[1]{1}{2} seconds}"
                   + "{}{}{Rounds:}{\\span{unit}{\\frac{1}{4} round}}"
                   + "{Actions:}{\\span{unit}{\\frac{1}{4} round}}}",
                   value.format(false).toString());
      assertEquals("rounds", false, value.isRounds());
      assertEquals("metric", true,  value.isMetric());
      assertEquals("rd",     "1/4", value.getAsRounds().toString());
      assertEquals("s",      "1 1/2", value.getAsSeconds().toString());

      value = new Duration(new Rational(2), new Rational(1, 2, 3),
                           new Rational(3), new Rational(1));

      //System.out.println(value);
      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("string", "2 days 1 2/3 hours 3 minutes 1 second",
                   value.toString());
      assertEquals("string",
                   "\\window{\\span{unit}{2 days} "
                   + "\\span{unit}{\\frac[1]{2}{3} hours} "
                   + "\\span{unit}{3 minutes} \\span{unit}{1 "
                   + "second}}{\\table{#inline#1:L,,;100:L}"
                   + "{Total:}{178981 seconds}{}{}"
                   + "{Rounds:}{\\span{unit}{\\frac[29830]{1}{6} rounds}}"
                   + "{Actions:}{\\span{unit}{29830 rounds} "
                   + "\\span{unit}{\\frac[1]{2}{3} standard actions}}}",
                   value.format(false).toString());
      assertEquals("rounds", false,  value.isRounds());
      assertEquals("metric", true,   value.isMetric());
      assertEquals("rd", "29830 1/6", value.getAsRounds().toString());
      assertEquals("s",  "178981", value.getAsSeconds().toString());

      // now with some value (metric)
       value = new Duration(new Rational(1, 1, 2));

      //System.out.println(value);
      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("string", "1 1/2 rounds", value.toString());
      assertEquals("print",
                   "\\window{\\span{unit}{\\frac[1]{1}{2} rounds}}"
                   + "{\\table{#inline#1:L,,;100:L}"
                   + "{Total:}{\\frac[1]{1}{2} rounds}{}{}"
                   + "{Seconds:}{\\span{unit}{9 seconds}}"
                   + "{Actions:}{\\span{unit}{\\frac[1]{1}{2} rounds}}}",
                   value.format(false).toString());
      assertEquals("rounds", true,  value.isRounds());
      assertEquals("metric", false,  value.isMetric());
      assertEquals("rd",   "1 1/2", value.getAsRounds().toString());
      assertEquals("s",    "9", value.getAsSeconds().toString());

      value = new Duration(new Rational(12033));

      //System.out.println(value);
      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("string", "12033 rounds",
                   value.toString());
      assertEquals("string",
                   "\\window{\\span{unit}{12033 rounds}}"
                   + "{\\table{#inline#1:L,,;100:L}"
                   + "{Total:}{12033 rounds}{}{}"
                   + "{Seconds:}{\\span{unit}{20 hours} "
                   + "\\span{unit}{3 minutes} \\span{unit}{18 seconds}}"
                   + "{Actions:}{\\span{unit}{12033 rounds}}}",
                   value.format(false).toString());
      assertEquals("feet",   true,  value.isRounds());
      assertEquals("metric", false, value.isMetric());
      assertEquals("in", "12033", value.getAsRounds().toString());
      assertEquals("cm", "72198", value.getAsSeconds().toString());

      // setting
      value =
        value.asMetric(null, new Rational(3), null, new Rational(1, 2, 3));
      assertEquals("set metric", "3 hours 1 2/3 seconds", value.toString());

      value = value.asRounds(new Rational(22));
      assertEquals("set rounds", "22 rounds", value.toString());

      Value.Test.createTest(value);
   }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing read. */
    @org.junit.Test
    public void read()
    {
      String []texts =
        {
          "simple", "1 day", "1 day", null,
          "no space", "1min", "1 minute", null,

          "whites", "1 \n1/2    minute 5 \n    seconds",
          "1 1/2 minutes 5 seconds", null,

          "round", "5 round", "5 rounds", null,
          "other", "22 1/2 minute", "22 1/2 minutes", null,
          "mixed", "5 seconds 200 rounds", "5 seconds", " 200 rounds",
          "none", "", null, null,
          "incomplete", "22", null, "22",
          "incomplete", "22.5 seconds", null, "22.5 seconds",
          "invalid", "1/2 guru", null, "1/2 guru",
        };

      Value.Test.readTest(texts, new Duration());
    }

    //......................................................................

    //----- rounds ---------------------------------------------------------

    /** Testing rounds. */
    @org.junit.Test
    public void rounds()
    {
      String []texts =
        {
          "1 round",    "6 seconds",
          "10 rounds",  "1 minute",
          "1/24 round", "1/4 second",
        };

      Duration value = new Duration();

      for(int i = 0; i < texts.length; i += 2)
      {
        ParseReader reader =
          new ParseReader(new java.io.StringReader(texts[i]), "test");

        value = value.read(reader);
        assertEquals("test " + i, texts[i + 1], value.asMetric().toString());
      }
    }

    //......................................................................
    //----- metric ---------------------------------------------------------

    /** Testing metric. */
    @org.junit.Test
    public void metric()
    {
      String []texts =
        {
          "6 seconds",          "1 round",
          "9 seconds",          "1 1/2 rounds",
          "22 minutes 3 days",  "43420 rounds",
          "66 seconds",         "11 rounds",
        };

      Duration value = new Duration();

      for(int i = 0; i < texts.length; i += 2)
      {
        ParseReader reader =
          new ParseReader(new java.io.StringReader(texts[i]), "test");

        value = value.read(reader);
        assertEquals("test " + i / 2, texts[i + 1],
                     value.asRounds().toString());
      }
    }

    //......................................................................
  }

  //........................................................................
}
