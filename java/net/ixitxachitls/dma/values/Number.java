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

package net.ixitxachitls.dma.values;

import javax.annotation.concurrent.Immutable;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a number and is capable of reading such numbers
 * from a reader (and write it to a writer of course).
 *
 * @file          Number.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Number extends BaseNumber<Number>
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Number --------------------------------

  /**
   * Construct the number object using real values.
   *
   * @param       inNumber the number inside this value
   * @param       inMin    the minimal number
   * @param       inMax    the maximal value
   * @param       inSign   true if always print a sign (even for +)
   *
   */
  public Number(long inNumber, long inMin, long inMax, boolean inSign)
  {
    super(inNumber, inMin, inMax, inSign);
  }

  //........................................................................
  //-------------------------------- Number --------------------------------

  /**
   * Construct the number object using real values.
   *
   * @param       inNumber the number inside this value
   * @param       inMin    the minimal number
   * @param       inMax    the maximal value
   *
   */
  public Number(long inNumber, long inMin, long inMax)
  {
    this(inNumber, inMin, inMax, false);
  }

  //........................................................................
  //-------------------------------- Number --------------------------------

  /**
   * Construct the number object as undefined.
   *
   * @param       inMin    the minimal number
   * @param       inMax    the maximal value
   * @param       inSign   true if always print a sign (even for +)
   *
   */
  public Number(long inMin, long inMax, boolean inSign)
  {
    super(inMin, inMax, inSign);
  }

  //........................................................................
  //-------------------------------- Number --------------------------------

  /**
   * Construct the number object as undefined.
   *
   * @param       inMin    the minimal number
   * @param       inMax    the maximal value
   *
   */
  public Number(long inMin, long inMax)
  {
    this(inMin, inMax, false);
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
  @SuppressWarnings("unchecked") // this method has to be overriden in
                                 // derivation for this to work
  public Number create()
  {
    return super.create(new Number(m_min, m_max, m_sign));
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
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** Test of init. */
    @org.junit.Test
    public void init()
    {
      Number number = new Number(10, 20);

      // undefined value
      assertFalse("not undefined at start", number.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   number.toString());
      assertEquals("undefined value not correct", 15, number.get());
      assertEquals("group", "$undefined$", number.group());

      // now with some number
      number = new Number(10, 0, 20);

      assertEquals("not defined after setting", true, number.isDefined());
      assertEquals("value not correctly gotten", 10, number.get());
      assertEquals("value not correctly converted", "10", number.toString());
      assertEquals("group", "10", number.group());

      assertEquals("max", 20, number.getMax());
      assertEquals("min", 0, number.getMin());

      number = new Number(522, 0, 1000);
      assertEquals("group", "750", number.group());

      Value.Test.createTest(number);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      String []tests =
        {
          "simple", "42", "42", null,
          "whites", "\n   42   \n  ", "42", "   ",
          "negative", "-13", "-13", null,
          "positive", "+13", "13", null,
          "zero", "+0", "0", null,
          "zero", "-0", "0", null,
          "invalid", "a", null, "a",
          "empty", "", null, null,
          "other", "42a", "42", "a",
          "too high", "123", null, "123",
          "too low", "-123", null, "-123",
        };

      m_logger.addExpectedPattern("WARNING:.*\\(maximal 50\\) "
                                  + "on line 1 in document 'test'."
                                  + "\\.\\.\\.>>>123\\.\\.\\.");
      m_logger.addExpectedPattern("WARNING:.*\\(minimal -50\\) "
                                  + "on line 1 in document 'test'."
                                  + "\\.\\.\\.>>>-123\\.\\.\\.");

      Value.Test.readTest(tests, new Number(-50, 50));
    }

    //......................................................................
    //----- set ------------------------------------------------------------

    /** Testing setting. */
    public void set()
    {
      Number number = new Number(10, 20);

      // undefined value
      assertEquals("not undefined at start", false, number.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   number.toString());

      assertEquals("set", "15", number.as(15).toString());
      assertEquals("low", "15", number.as(9).toString());
      assertEquals("high", "15", number.as(21).toString());
      assertEquals("max", "20", number.as(20).toString());
      assertEquals("min", "10", number.as(10).toString());
    }

    //......................................................................
    //----- compute --------------------------------------------------------

    /** Value computations. */
    @org.junit.Test
    public void compute()
    {
      Number number = new Number(2, 20);

      // not initialized
      number = number.multiply(3);
      assertEquals("start", 11, number.get());

      number = number.divide(3);
      assertEquals("start", 11, number.get());

      // initialize in the middle
      number = number.as(5);

      number = number.multiply(3);
      assertEquals("multiply", 15, number.get());

      number = number.multiply(2);
      assertEquals("multiply", 20, number.get());

      number = number.as(5);

      number = number.divide(2);
      assertEquals("divide", 2, number.get());

      number = number.divide(3);
      assertEquals("divide", 2, number.get());

      m_logger.addExpected("WARNING: number 30 too high, adjusted to 20");
      m_logger.addExpected("WARNING: number 0 too low, adjusted to 2");
    }

    //......................................................................
  }

  //........................................................................
}
