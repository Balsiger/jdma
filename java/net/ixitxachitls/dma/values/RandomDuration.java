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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.input.ParseReader;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A duration that also can have a random value. It can only deal with
 * xdy [* value ] units, e.g. things like '2d4 rounds' or '2d4 * 1/2 rounds'.
 *
 * @file          RandomDuration.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class RandomDuration extends Duration
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------- RandomDuration -----------------------------

  /**
   * Construct the duration object with an undefined value.
   *
   */
  public RandomDuration()
  {
  }

  //........................................................................
  //--------------------------- RandomDuration -----------------------------

  /**
   * Construct the duration object.
   *
   * @param       inDays    the number of days
   * @param       inHours   the number of hours
   * @param       inMinutes the number of minutes
   * @param       inSeconds the number of seconds
   *
   */
  public RandomDuration(@Nullable Rational inDays, @Nullable Rational inHours,
                        @Nullable Rational inMinutes,
                        @Nullable Rational inSeconds)
  {
    super(inDays, inHours, inMinutes, inSeconds);
  }

  //........................................................................
  //--------------------------- RandomDuration -----------------------------

  /**
   * Construct the duration object.
   *
   * @param       inRounds the number of rounds
   *
   */
  public RandomDuration(Rational inRounds)
  {
    super(inRounds);
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
  @Override
  public RandomDuration create()
  {
    return (RandomDuration)super.create(new RandomDuration());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The random part of the duration. */
  private @Nullable Dice []m_dices;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------- singleUnitToString --------------------------

  /**
   * Convert a single unit to a string.
   *
   * @param       inIndex the index of the unit to print
   *
   * @return      the converted string
   *
   */
  @Override
  protected String singleUnitToString(int inIndex)
  {
    if(inIndex < 0 || m_dices == null || inIndex >= m_dices.length)
      return super.singleUnitToString(inIndex);

    if(m_dices[inIndex] == null)
      return super.singleUnitToString(inIndex);

    if(m_values[inIndex].isOne())
      return m_dices[inIndex].toString() + " " + m_set.m_units[inIndex].m_units;

    return m_dices[inIndex].toString() + " * "
      + super.singleUnitToString(inIndex);
  }

  //........................................................................
  //------------------------------- isRandom -------------------------------

  /**
   * Check if the object really has some random value.
   *
   * @return    true if there is a random part, false if not
   *
   */
  public boolean isRandom()
  {
    if(m_dices == null)
      return false;

    for(Dice dice : m_dices)
      if(dice != null && dice.isRandom())
        return true;

    return false;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //---------------------------- readSingleUnit ----------------------------

  /**
   * Read a single unit.
   *
   * @param       inReader the reader to read from
   *
   * @return      the number of the unit read or -1 if none read
   *
   */
  @Override
  protected int readSingleUnit(ParseReader inReader)
  {
    ParseReader.Position pos = inReader.getPosition();

    // try to read the dice first, if any
    Dice dice = new Dice().read(inReader);

    if(dice == null)
      return super.readSingleUnit(inReader);

    if(!inReader.expect("*"))
      if(dice.getDice() == 1 || dice.getNumber() == 0)
      {
        // it's not really random
        inReader.put(dice.getModifier() + " ");
        dice = null;
      }
      else
        // it's random, but the superclass expects a rational now
        inReader.put("1 ");

    int index = super.readSingleUnit(inReader);

    if(index >= 0)
    {
      if(m_dices == null)
        m_dices = new Dice[m_set.m_units.length];

      if(m_dices[index] == null)
        m_dices[index] = dice;
      else
      {
        try
        {
          m_values[index] = m_values[index].subtract(1);
          m_dices[index] = m_dices[index].add(dice);
        }
        catch(UnsupportedOperationException e)
        {
          inReader.seek(pos);
          return -1;
        }
      }
    }

    return index;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {

    //----- read -----------------------------------------------------------

    /** Testing read. */
    @org.junit.Test
    public void read()
    {
      String []texts =
        {
          "simple", "1 day", "1 day", null,
          "zero", "0 day", "0 seconds", null,
          "no space", "1min", "1 minute", null,

          "whites", "1 \n1/2    minute 5 \n    seconds",
          "1 1/2 minutes 5 seconds", null,

          "round", "5 round", "5 rounds", null,
          "other", "22 1/2 minute", "22 1/2 minutes", null,
          "mixed", "5 seconds 200 rounds", "5 seconds", "200 rounds",
          "none", "", null, null,
          "incomplete", "22", null, "22",
          "incomplete", "22.5 seconds", null, "22.5 seconds",
          "invalid", "1/2 guru", null, "1/2 guru",

          // random tests
          "simple random", "2d4 rounds", "2d4 rounds",  null,
          "rational random", "2d4 * 1/2 rounds", "2d4 * 1/2 round", null,
          "random and non", "2d4 hours 1 day", "1 day 2d4 hours", null,
          "two randoms", "2d4 minutes 1d8 days", "1d8 days 2d4 minutes", null,
          "adding no", "2d4 rounds 1d3 rounds", "2d4 rounds", " 1d3 rounds",
          "adding yes", "2d4 +3 rounds 1d4 -1 rounds", "3d4 +2 rounds", null,
        };

      Value.Test.readTest(texts, new RandomDuration());
    }
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
