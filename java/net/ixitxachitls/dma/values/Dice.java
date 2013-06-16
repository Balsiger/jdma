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

import java.util.Random;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.input.ParseReader;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class represents a dice value.
 *
 * @file          Dice.java
 *
 * @author        yvonne.dauwalder@gmx.ch (Yvonne Dauwalder)
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Dice extends Value<Dice>
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Dice ---------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * Create a standard, empty dice.
   *
   */
  public Dice()
  {
    m_number   = 0;
    m_dice     = 0;
    m_modifier = 0;
    m_editType = "dice";
  }

  //........................................................................
  //--------------------------------- Dice ---------------------------------

  /**
   * Create a dice with specific values.
   *
   * @param       inNumber   - the number of dice to roll
   * @param       inDice     - the kind of dice to roll
   * @param       inModifier - the modifier of the dice
   *
   */
  public Dice(int inNumber, int inDice, int inModifier)
  {
    if(inNumber < 0)
      throw new IllegalArgumentException("must have at least one roll");

    if(inDice <= 0)
      throw new IllegalArgumentException("must have a real dice here");

    m_number   = inNumber;
    m_dice     = inDice;
    m_modifier = inModifier;
    m_editType = "dice";
  }

  //........................................................................

  //-------------------------------- create --------------------------------

  /**
   * Create a new list with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar list, but without any contents
   *
   */
  @Override
  public Dice create()
  {
    return super.create(new Dice());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The number of dice to roll. */
  private int m_number;

  /** The kind of dice to roll. */
  private int m_dice;

  /** The modifier for the roll. */
  private int m_modifier;

  /** The random generator. */
  private static final Random s_random = new Random();

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------------- roll ---------------------------------

  /**
   * Roll the dice and compute a result. This will always return a value
   * of at least 1.
   *
   * @return      the value rolled
   *
   */
  public int roll()
  {
    int rolled = m_modifier;

    for (int i = 0; i < m_number; i++)
      rolled += s_random.nextInt(m_dice) + 1;

    return Math.max(1, rolled);
  }

  //........................................................................

  //------------------------------ isDefined -------------------------------

  /**
   * Check if the value is defined or not.
   *
   * @return      true if the value is defined, false if not
   *
   */
  @Override
  public boolean isDefined()
  {
    return m_dice > 0 || m_number > 0;
  }

  //........................................................................
  //------------------------------ getNumber -------------------------------

  /**
   * Get the number stored (without escaping).
   *
   * @return      the number stored
   *
   */
  public int getNumber()
  {
    return m_number;
  }

  //........................................................................
  //------------------------------- getDice --------------------------------

  /**
   * Get the dice stored (without escaping).
   *
   * @return      the dice stored
   *
   */
  public int getDice()
  {
    return m_dice;
  }

  //........................................................................
  //----------------------------- getModifier ------------------------------

  /**
   * Get the modifier stored (without escaping).
   *
   * @return      the modifier stored
   *
   */
  public int getModifier()
  {
    return m_modifier;
  }

  //........................................................................
  //-------------------------------- getMax --------------------------------

  /**
    * Get the maximal value that can result of this dice.
    *
    * @return      the maximal possible value
    *
    */
  public long getMax()
  {
    return Math.max(1, m_number * m_dice + m_modifier);
  }

  //........................................................................
  //-------------------------------- getMin --------------------------------

  /**
    * Get the minimal value that can result of this dice.
    *
    * @return      the minimal possible value
    *
    */
  public long getMin()
  {
    return Math.max(1, m_number + m_modifier);
  }

  //........................................................................
  //------------------------------ getAverage ------------------------------

  /**
   * Get the average value that can result of this dice.
   *
   * The minimal value is always 1.
   *
   * @return      the average value
   *
   */
  public long getAverage()
  {
    return Math.max(1, m_number * (m_dice + 1) / 2 + m_modifier);
  }

  //........................................................................
  //------------------------------- isRandom -------------------------------

  /**
   * Determine if this dice represents any random value or is basically static.
   *
   * @return   true if the die is random, false if not
   *
   */
  public boolean isRandom()
  {
    return isDefined() && m_dice != 1 && m_dice != 0 && m_number != 0;
  }

  //........................................................................

  //------------------------------ doToString ------------------------------

  /**
   * Convert the value to a string, depending on the given kind.
   *
   * @return      a String representation, depending on the kind given
   *
   */
  @Override
  protected String doToString()
  {
    String dice = m_number + "d" + m_dice;

    if(m_number == 0 || m_dice == 0)
      dice = null;
    else
      if(m_dice == 1)
        dice = "" + m_number;

    if(m_modifier == 0)
      if(dice == null)
        return "0";
      else
        return dice;

    if(m_modifier > 0)
      if(dice == null)
        return "" + m_modifier;
      else
        return dice + " +" + m_modifier;
    else
      if(dice == null)
        return "" + m_modifier;
      else
        return dice + " " + m_modifier;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- doRead ---------------------------------

  /**
   * Read the value from the reader and replace the current one.
   *
   * @param       inReader   the reader to read from
   *
   * @return      true if read, false if not
   *
   */
  @Override
  protected boolean doRead(ParseReader inReader)
  {
    int number = 0;
    int dice = 0;

    // read the number of dice
    try
    {
      number = inReader.readInt();
    }
    catch(net.ixitxachitls.input.ReadException e)
    {
      return false;
    }

    if(!inReader.expect('d'))
    {
      m_modifier = number;
      m_dice     = 1;
      m_number   = 0;

      return true;
    }

    if(number <= 0)
      return false;

    try
    {
      dice = inReader.readInt();

      if(dice <= 0)
        return false;
    }
    catch(net.ixitxachitls.input.ReadException e)
    {
      return false;
    }

    m_number = number;
    m_dice = dice;

    try
    {
      m_modifier = inReader.readInt();
    }
    catch(net.ixitxachitls.input.ReadException e)
    { /* no modifier read, but rest is ok */ }

    return true;
  }

  //........................................................................

  //--------------------------------- add ----------------------------------

  /**
   * Add the current and given value and return it. The current value is not
   * changed.
   *
   * @param       inValue the value to add to this one
   *
   * @return      the added values
   *
   */
  @Override
  public Dice add(Dice inValue)
  {
    if(!isDefined())
      return inValue;

    if(!inValue.isDefined())
      return this;

    if(m_number > 0 && inValue.m_number > 0 && m_dice != inValue.m_dice)
      throw new UnsupportedOperationException("can only add same dice");

    Dice result = create();
    result.m_number = m_number + inValue.m_number;
    result.m_dice = m_number > 0 ? m_dice : inValue.m_dice;
    result.m_modifier = m_modifier + inValue.m_modifier;

    if(-result.m_modifier > m_number * m_dice)
      result.m_modifier = -m_number * m_dice + 1;

    return result;
  }

  //........................................................................
  //------------------------------- multiply -------------------------------

  /**
   * Multiply the units.
   *
   * @param       inValue the multiplication factor
   *
   * @return      true if multiplied, false if not
   *
   */
  // public boolean multiply(long inValue)
  // {
  //   for(int i = 0; i < inValue; i++)
  //     increase();

  //   return true;
  // }

  //........................................................................
  //-------------------------------- divide --------------------------------

  /**
   * Divide the dice. This decreases the dice type to the corresponding
   * dice, as shown in the Player's Handbook p. 116 and 114.
   *
   * @param       inValue the division factor
   *
   * @return      true if divided, false if not
   *
   */
  // public boolean divide(long inValue)
  // {
  //   for(int i = 0; i < inValue; i++)
  //     decrease();

  //   return true;
  // }

  //........................................................................

  //------------------------------- decrease -------------------------------

  /**
   * This decreases the dice type to the corresponding dice, as shown in the
   * Player's Handbook p. 116 and 114.
   *
   * This method is accurate for all the values mentioned in the players's
   * handbook, but other values are not according to the rules (as there are
   * none I know of...).
   *
   * Also note that increase and decrease are not inverse, thus it is in no
   * way the case that after increasing and afterwards decreasing a value you
   * end up at the same number.
   *
   * @algorithm   cf. Player's Handbook 114/116
   *
   */
  // protected void decrease()
  // {
  //   // if only one dice, reduce the dice by two (if > 4)
  //   if(m_number == 1)
  //   {
  //     if(m_dice > 4)
  //       m_dice -= 2;
  //     else
  //       if(m_dice > 0)
  //         m_dice -= 1;
  //   }
  //   else
  //     // as double dice are still in the rules, treat the special
  //     if(m_number == 2)
  //       if(m_dice < 8)
  //       {
  //         m_dice = 2 * m_dice - 2;
  //         m_number--;
  //       }
  //       else
  //         m_dice -= 2;
  //     // the following is no more covered by any rules
  //     else
  //       m_number--;
  // }

  //........................................................................
  //------------------------------- increase -------------------------------

  /**
   * This increases the dice type to the corresponding dice, as shown in the
   * Player's Handbook p. 116 and 114.
   *
   * This method is accurate for all the values mentioned in the players's
   * handbook, but other values are not according to the rules (as there are
   * none I know of...).
   *
   * Also note that increase and decrease are not inverse, thus it is in no
   * way the case that after increasing and afterwards decreasing a value you
   * end up at the same number.
   *
   * @algorithm   cf. Player's Handbook 114/116
   *
   */
  // protected void increase()
  // {
  //   // if only one dice, reduce the dice by two (if > 4)
  //   if(m_dice < 4)
  //     m_dice++;
  //   else
  //     if(m_dice < 6 || (m_dice < 8 && m_number == 1))
  //       m_dice += 2;
  //     else
  //       if(m_dice < 12)
  //         if(m_number == 1)
  //         {
  //           m_dice -= 2;
  //           m_number++;
  //         }
  //         else
  //           if(m_dice < 10)
  //             m_number++;
  //           else
  //           {
  //             m_number += 2;
  //             m_dice -= 2;
  //           }
  //       else
  //       {
  //         m_dice   /= 2;
  //         m_number *= 3;
  //       }
  // }

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
    public void testInit()
    {
      Dice dice = new Dice();

      // undefined value
      assertEquals("not undefined at start", false, dice.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   dice.toString());

      // now with some dice
      dice = new Dice(1, 6, -2);

      assertEquals("not defined after setting", true, dice.isDefined());
      assertEquals("value not correctly converted", "1d6 -2",
                   dice.toString());

      assertEquals("dice",      6, dice.getDice());
      assertEquals("number",    1, dice.getNumber());
      assertEquals("modifier", -2, dice.getModifier());
      assertEquals("average",   1, dice.getAverage());

      // assertTrue("devide", dice.divide(2));
      // assertEquals("devide", "1d3 -2", dice.toString());
      // assertEquals("average", 1, dice.getAverage());

      // assertTrue("multiply", dice.multiply(4));
      // assertEquals("multiply", "2d6 -2", dice.toString());
      // assertEquals("average", 5, dice.getAverage());

      Value.Test.createTest(dice);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void testRead()
    {
      String []tests =
        {
          "simple", "1d6 +2", "1d6 +2",  null,
          "whites", "\n   2  \n d  8 \n -2 ", "2d8 -2", " ",
          "dice", "3d12", "3d12",  null,
          "modifier", "+3", "3",  null,
          "modifier", "-5", "-5",  null,
          "negative", "2d10 -8", "2d10 -8", null,
          "invalid", "a", null, "a",
          "empty", "", null, null,
          "delimiter 1", "1e5 +2", "1", "e5 +2",
          "delimiter 2", "1ea", "1", "ea",
          "delimiter 3", "1min", "1", "min",
          "too much", "1d6 +e", "1d6 +2147483647", "e",
          "too low 1", "-1d3 +3", null, "-1d3 +3",
          "too low 2", "1d-3 -2", null, "1d-3 -2",
          "too low 3", "-1d-5 +2", null, "-1d-5 +2",
        };

      Value.Test.readTest(tests, new Dice());
    }

    //......................................................................
    //----- decrease -------------------------------------------------------

    /** Testing decreasing. */
    // @org.junit.Test
    // public void testDecrease()
    // {
    // int []values = { 1, 1,  1, 2,  1, 3,  1, 4,  1, 6,  1, 8,  1, 10,  1, 12,
    //                    2, 4,  2, 6,  2, 8,  2, 10,
    //                    3, 6,  1, 5,  2, 9
    //                  };

    //   String []results = { "0", "1", "1d2", "1d3", "1d4", "1d6", "1d8",
    //                        "1d10", "1d6", "1d10", "2d6", "2d8",
    //                        "2d6", "1d3", "2d7",
    //                      };

    //   for(int i = 0; i < results.length; i++)
    //   {
    //     Dice dice = new Dice(values[2 * i], values[2 * i + 1], 0);

    //     dice.decrease();
    //     assertEquals("Test " + i, results[i], dice.toString());
    //   }
    // }

    //......................................................................
    //----- increase -------------------------------------------------------

    /** Testing increasing. */
    // @org.junit.Test
    // public void testIncrease()
    // {
    // int []values = { 1, 1,  1, 2,  1, 3,  1, 4,  1, 6,  1, 8,  1, 10,  1, 12,
    //                    2, 4,  2, 6,  2, 8,  2, 10,
    //                    3, 6,  1, 5,  2, 9
    //                  };

    //   String []results = { "1d2", "1d3", "1d4", "1d6", "1d8", "2d6", "2d8",
    //                        "3d6", "2d6", "3d6", "3d8", "4d8",
    //                        "4d6", "1d7", "3d9",
    //                      };

    //   for(int i = 0; i < results.length; i++)
    //   {
    //     Dice dice = new Dice(values[2 * i], values[2 * i + 1], 0);

    //     dice.increase();
    //     assertEquals("Test " + i, results[i], dice.toString());
    //   }
    // }

    //......................................................................
    //----- roll -----------------------------------------------------------

    /** Rolling the dice. */
    @org.junit.Test
    public void testRoll()
    {
      Dice dice = new Dice(2, 6, -4);

      // testing limits
      for(int i = 0; i < 100; i++)
      {
        assertTrue("lower limit", 1 <= dice.roll());
        assertTrue("upper limit", 8 >= dice.roll());
      }

      dice = new Dice(1, 10, 0);

      int []result = new int[10];

      for(int i = 0; i < 1000; i++)
        result[dice.roll() - 1]++;

      for(int i = 0; i < result.length; i++)
        assertEquals("distribution", 0.1, result[i] / 1000.0, 0.03);
    }

    //......................................................................
  }

  //........................................................................
}
