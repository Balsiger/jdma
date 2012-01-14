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

import java.math.BigInteger;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a rational and is capable of reading such rationals
 * from a reader (and write it to a writer of course).
 *
 * @file          Rational.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Rational extends BaseRational<Rational>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Rational -------------------------------

  /**
   * Construct the rational object with an undefined value.
   *
   */
  public Rational()
  {
    // nothing to do
  }

  //........................................................................
  //------------------------------- Rational -------------------------------

  /**
   * Construct the rational object (simple integer).
   *
   * @param       inLeader the leading value
   *
   */
  public Rational(long inLeader)
  {
    this(inLeader, 0, 1);
  }

  //........................................................................
  //------------------------------- Rational -------------------------------

  /**
   * Construct the rational object with a real fraction.
   *
   * @param       inNominator   value above the line
   * @param       inDenominator value below the line
   *
   */
  public Rational(long inNominator, long inDenominator)
  {
    this(0, inNominator, inDenominator);
  }

  //........................................................................
  //------------------------------- Rational -------------------------------

  /**
   * Construct the rational object with all values.
   *
   * @param       inLeader      the leader before the real fraction
   * @param       inNominator   value above the line
   * @param       inDenominator value below the line
   *
   */
  public Rational(long inLeader, long inNominator, long inDenominator)
  {
    super(inLeader, inNominator, inDenominator);
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
  public @Nonnull Rational create()
  {
    return super.create(new Rational());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables
  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- getNominator -----------------------------

  /**
   * Get the nominator stored.
   *
   * @return      the nominator stored
   *
   */
  public long getNominator()
  {
    return m_nominator;
  }

  //........................................................................
  //---------------------------- getDenominator ----------------------------

  /**
   * Get the denominator stored.
   *
   * @return      the denominator stored
   *
   */
  public long getDenominator()
  {
    return m_denominator;
  }

  //........................................................................

  //------------------------------ hasRandom -------------------------------

  /**
   * Check if this value has a random element.
   *
   * @return      true if there is a random element, false if not
   *
   */
  // public boolean hasRandom()
  // {
  //   return m_randomNominator != null || m_randomDenominator != null;
  // }

  //........................................................................

  //------------------------------- getValue -------------------------------

  /**
   * Get the real value stored.
   *
   * @return      the internal value as a floating point number
   *
   */
  public double getValue()
  {
    if(m_denominator == 0)
      return m_leader;

    int nominator   = 1;
    int denominator = 1;

    if(m_negative)
      return -1 * (m_leader + ((double)m_nominator * nominator)
                   / ((double)m_denominator * denominator));
    else
      return m_leader + ((double)m_nominator * nominator)
        / ((double)m_denominator * denominator);
  }

  //........................................................................

  //------------------------------- compare --------------------------------

  /**
   * Compare the Rational to the given value.
   *
   * @param       inValue the value to compare to.
   *
   * @return      a value < 0 if this one is below the given value, a
   *              value > 0, if it is above it and 0 if it is equal
   *
   */
  public int compare(long inValue)
  {
    Rational rational = reduce();

    if(inValue > 0 && rational.m_negative)
      return -1;

    if(inValue < 0)
      if(!rational.m_negative)
        return +1;
      else
        inValue *= -1;

    if(rational.m_leader < inValue)
      return !rational.m_negative ? -1 : +1;

    if(rational.m_leader > inValue)
      return !m_negative ? +1 : -1;

    if(rational.m_nominator == 0)
        return 0;

    return !rational.m_negative ? +1 : -1;
  }

  //........................................................................
  //------------------------------- compare --------------------------------

  /**
   * Compare the Rational to the given value.
   *
   * @param       inValue the value to compare to
   *
   * @return      a value < 0 if this one is below the given value, a
   *              value > 0, if it is above it and 0 if it is equal
   *
   */
  public int compare(@Nonnull Rational inValue)
  {
    // reduce the fractions if possible
    Rational first = reduce();
    Rational second = inValue.reduce();

    if(!second.m_negative && first.m_negative)
      return -1;

    if(second.m_negative && !first.m_negative)
      return +1;

    if(first.m_leader < second.m_leader)
      return !first.m_negative ? -1 : +1;

    if(first.m_leader > second.m_leader)
      return !first.m_negative ? +1 : -1;

    if(first.m_nominator == second.m_nominator
       && first.m_denominator == second.m_denominator)
      return 0;

    if(first.getValue() < second.getValue())
      return -1;

    return +1;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- set ----------------------------------

  /**
   * Set the value to x y/z.
   *
   * @param       inLeader      the leading value (x)
   * @param       inNominator   the nominator (y)
   * @param       inDenominator the denominator (z)
   *
   * @return      true if set, false if not (0 denominator)
   *
   * @undefined   never
   *
   */
  // public boolean set(long inLeader, long inNominator, long inDenominator)
  // {
  //   if(inDenominator == 0)
  //     return false;

  //   m_leader      = inLeader;
  //   m_nominator   = inNominator;
  //   m_denominator = inDenominator;

  //   computeNegative();

  //   return true;
  // }

  //........................................................................
  //--------------------------------- set ----------------------------------

  // /**
  //  * Set the value to x y/z.
  //  *
  //  * @param       inLeader      the leading value (x)
  //  *
  //  * @return      true if set, false if not (never)
  //  *
  //  * @undefined   never
  //  *
  //  */
  // public boolean set(long inLeader)
  // {
  //   return set(inLeader, 0, 1);
  // }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set the value to x y/z.
   *
   * @param       inNominator   the nominator (y)
   * @param       inDenominator the denominator (z)
   *
   * @return      true if set, false if not (0 denominator)
   *
   * @undefined   never
   *
   */
  // public boolean set(long inNominator, long inDenominator)
  // {
  //   return set(0, inNominator, inDenominator);
  // }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set the value to x y/z.
   *
   * @param       inLeader            the leading value
   * @param       inNominator         the nominator
   * @param       inDenominator       the denominator
   * @param       inRandomNominator   a random nominator value (as a dice)
   * @param       inRandomDenominator a random denominator value (as a dice)
   *
   * @return      true if set, false if not (0 denominator)
   *
   * @undefined   never
   *
   */
  // public boolean set(long inLeader, long inNominator, long inDenominator,
  //                    Dice inRandomNominator, Dice inRandomDenominator)
  // {
  //   m_leader            = inLeader;
  //   m_nominator         = inNominator;
  //   m_denominator       = inDenominator;
  //   m_randomNominator   = inRandomNominator;
  //   m_randomDenominator = inRandomDenominator;

  //   computeNegative();

  //   return true;
  // }

  //........................................................................

  //-------------------------------- reduce --------------------------------

  /**
   * Reduce the fraction, if possible.
   *
   * We use the following special cases:
   *
   *   a -b/c   is defined as  (a*c - b)/c
   *   -a -b/c  is defined as  -((a*c - b)/c)
   *   -a b/c   is defined as  -((a*c + b)/c)
   *
   * @return      the reduced rational
   *
   */
  public @Nonnull Rational reduce()
  {
    // nothing to do if not defined
    if(!isDefined())
      return this;

    // determine new leader
    Rational result = create();
    result.m_denominator = m_denominator;
    result.m_nominator   = m_nominator + m_leader * m_denominator;
    result.m_leader      = result.m_nominator / result.m_denominator;
    result.m_negative    = m_negative;

    result.m_nominator %= result.m_denominator;

    int divisor =
      BigInteger.valueOf(result.m_nominator)
      .gcd(BigInteger.valueOf(result.m_denominator))
      .intValue();

    result.m_nominator   /= divisor;
    result.m_denominator /= divisor;

    return result;
  }

  //........................................................................

  //--------------------------------- add ----------------------------------

  /**
   * Add a value to the rational.
   *
   * @param       inValue the value to add
   *
   * @undefined   IllegalArgumentException if given value is null
   *
   */
  // public void add(Rational inValue)
  // {
  //   if(inValue == null)
  //     throw new IllegalArgumentException("must have a value here");

  //   if(hasRandom() || inValue.hasRandom())
  //     throw new UnsupportedOperationException("cannot add random values");

  //   long nominator1 = m_leader * m_denominator + m_nominator;
  //   if(m_negative)
  //     nominator1 *= -1;

  //   long nominator2 = inValue.m_leader * inValue.m_denominator
  //     + inValue.m_nominator;
  //   if(inValue.m_negative)
  //     nominator2 *= -1;

  //   m_nominator = nominator1 * inValue.m_denominator
  //     + nominator2 * m_denominator;

  //   m_denominator *= inValue.m_denominator;

  //   m_leader = 0;

  //   if(m_nominator < 0)
  //   {
  //     m_nominator *= -1;
  //     m_negative = true;
  //   }
  //   else
  //     m_negative = false;

  //   reduce();
  // }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add a value to the rational.
   *
   * @param       inValue the value to add
   *
   * @undefined   undefined if one is undefined
   *
   */
  // public void add(long inValue)
  // {
  //   add(new Rational(inValue));
  // }

  //........................................................................
  //------------------------------- subtract -------------------------------

  /**
   * Add a value to the rational.
   *
   * @param       inValue the value to add
   *
   * @undefined   IllegalArgumentException if given value is null
   *
   */
  // public void subtract(Rational inValue)
  // {
  //   if(inValue == null)
  //     throw new IllegalArgumentException("must have a value here");

  //   if(hasRandom() || inValue.hasRandom())
  //   throw new UnsupportedOperationException("cannot subtract random values");

  //   long nominator1 = m_leader * m_denominator + m_nominator;
  //   if(m_negative)
  //     nominator1 *= -1;

  //   long nominator2 = inValue.m_leader * inValue.m_denominator
  //     + inValue.m_nominator;
  //   if(inValue.m_negative)
  //     nominator2 *= -1;

  //   m_nominator = nominator1 * inValue.m_denominator
  //     - nominator2 * m_denominator;

  //   m_denominator *= inValue.m_denominator;

  //   m_leader = 0;

  //   if(m_nominator < 0)
  //   {
  //     m_nominator *= -1;
  //     m_negative = true;
  //   }
  //   else
  //     m_negative = false;

  //   reduce();
  // }

  //........................................................................
  //------------------------------- subtract -------------------------------

  /**
   * Add a value to the rational.
   *
   * @param       inValue the value to add
   *
   * @undefined   undefined if one is undefined
   *
   */
  // public void subtract(long inValue)
  // {
  //   subtract(new Rational(inValue));
  // }

  //........................................................................
  //-------------------------------- divide --------------------------------

  /**
   * Divide a value to the rational.
   *
   * @param       inValue the value to divide with
   *
   * @undefined   undefined if one is undefined
   *
   */
  // public void divide(long inValue)
  // {
  //   if(inValue == 0)
  //     throw new IllegalArgumentException("cannot divide by zero!");

  //   if(inValue == 1)
  //     return;

  //   m_nominator   += m_leader * m_denominator;
  //   m_denominator *= Math.abs(inValue);
  //   m_leader       = 0;

  //   if(inValue < 0)
  //     m_negative = !m_negative;

  //   reduce();
  // }

  //........................................................................
  //-------------------------------- divide --------------------------------

  /**
   * Divide a value to the rational.
   *
   * @param       inValue the value to divide with
   *
   * @undefined   IllegalArgumentException if given value is null
   *
   */
  // public void divide(Rational inValue)
  // {
  //   if(inValue == null)
  //     throw new IllegalArgumentException("must have a value here");

  //   if(hasRandom() || inValue.hasRandom())
  //     throw new UnsupportedOperationException("cannot divide random values");

  //   m_nominator    =
  //     (m_leader * m_denominator + m_nominator) * inValue.m_denominator;

  //   m_denominator =
  //     (inValue.m_leader * inValue.m_denominator + inValue.m_nominator)
  //     * m_denominator;

  //   m_leader       = 0;

  //   if(inValue.m_negative)
  //     m_negative = !m_negative;

  //   reduce();
  // }

  //........................................................................
  //------------------------------- multiply -------------------------------

  /**
   * Multiply a value to the rational.
   *
   * @param       inValue the value to multiply with
   *
   * @return      true if multiplied successfully, false if not
   *
   * @undefined   undefined if one is undefined
   *
   */
  // public boolean multiply(long inValue)
  // {
  //   if(inValue == 1)
  //     return true;

  //   m_leader    *= Math.abs(inValue);
  //   m_nominator *= Math.abs(inValue);

  //   if(inValue < 0)
  //     m_negative = !m_negative;

  //   reduce();

  //   return true;
  // }

  //........................................................................
  //------------------------------- multiply -------------------------------

  /**
   * Multiply a value to the rational.
   *
   * @param       inValue the value to multiply with
   *
   * @return      true if multiplied successfully, false if not
   *
   * @undefined   IllegalArgumentException if given value is null
   *
   */
  // public boolean multiply(Rational inValue)
  // {
  //   if(inValue == null)
  //     throw new IllegalArgumentException("must have a value here");

  //   if(hasRandom() || inValue.hasRandom())
  //  throw new UnsupportedOperationException("cannot multiply random values");

  //   m_nominator    = (m_leader * m_denominator + m_nominator)
  //     * (inValue.m_leader * inValue.m_denominator + inValue.m_nominator);
  //   m_leader       = 0;
  //   m_denominator *= inValue.m_denominator;

  //   if(inValue.m_negative)
  //     m_negative = !m_negative;

  //   reduce();

  //   return true;
  // }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- readDice -------------------------------

  /**
   * Read a dice from the given reader.
   *
   * @param       inReader the reader to read from
   *
   * @return      the Dice read (in '(/)') or null if none found
   *
   * @undefined   may return null
   *
   */
  // private Dice readDice(ParseReader inReader)
  // {
  //   assert inReader != null : "must have a reader here!";

  //   // another dice?
  //   if(!inReader.expect('('))
  //     return null;

  //   Dice dice = new Dice();

  //   if(!dice.read(inReader) || !inReader.expect(')'))
  //     return null;

  //   return dice;
  // }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The Test class. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** Test init. */
    @org.junit.Test
    public void testInit()
    {
      Rational rational = new Rational();

      // undefined value
      assertEquals("not undefined at start", false, rational.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   rational.toString());
      assertEquals("undefined value", 0.0, rational.getValue(), 0.001);
      assertEquals("undefined leader", 0, rational.getLeader());
      assertEquals("undefined nominator", 0, rational.getNominator());
      assertEquals("undefined denominator", 0, rational.getDenominator());
      assertFalse("negative", rational.isNegative());
      assertEquals("format", "", rational.format(true).toString());

      // now with some rational
      rational = new Rational(1, 2);

      assertEquals("not defined after setting", true, rational.isDefined());
      assertEquals("output", "1/2", rational.toString());
      assertEquals("value", 0.5, rational.getValue(), 0.001);
      assertEquals("leader", 0, rational.getLeader());
      assertEquals("nominator", 1, rational.getNominator());
      assertEquals("denominator", 2, rational.getDenominator());
      assertFalse("negative", rational.isNegative());
      assertEquals("format", "\\frac{1}{2}", rational.format(true).toString());

      // now with some rational
      rational = new Rational(1, 2, 3);

      assertEquals("not defined after setting", true, rational.isDefined());
      assertEquals("output", "1 2/3", rational.toString());
      assertEquals("value", 1.666, rational.getValue(), 0.001);
      assertEquals("leader", 1, rational.getLeader());
      assertEquals("nominator", 2, rational.getNominator());
      assertEquals("denominator", 3, rational.getDenominator());
      assertFalse("negative", rational.isNegative());
      assertEquals("format", "\\frac[1]{2}{3}",
                   rational.format(true).toString());

      // how about some negative
      rational = new Rational(1, -2, -3);

      assertEquals("not defined after setting", true, rational.isDefined());
      assertEquals("output", "1 2/3", rational.toString());
      assertEquals("value", 1.666, rational.getValue(), 0.001);
      assertEquals("leader", 1, rational.getLeader());
      assertEquals("nominator", 2, rational.getNominator());
      assertEquals("denominator", 3, rational.getDenominator());
      assertFalse("negative", rational.isNegative());
      assertEquals("format", "\\frac[1]{2}{3}",
                   rational.format(true).toString());

      // how about some negative
      rational = new Rational(-1, 2, 3);

      assertEquals("not defined after setting", true, rational.isDefined());
      assertEquals("output", "-1 2/3", rational.toString());
      assertEquals("value", -1.666, rational.getValue(), 0.001);
      assertEquals("leader", 1, rational.getLeader());
      assertEquals("nominator", 2, rational.getNominator());
      assertEquals("denominator", 3, rational.getDenominator());
      assertTrue("negative", rational.isNegative());
      assertEquals("format", "-\\frac[1]{2}{3}",
                   rational.format(true).toString());

      // how about some negative
      rational = new Rational(-1, -2, 3);

      assertEquals("not defined after setting", true, rational.isDefined());
      assertEquals("output", "-1 2/3", rational.toString());
      assertEquals("value", -1.666, rational.getValue(), 0.001);
      assertEquals("leader", 1, rational.getLeader());
      assertEquals("nominator", 2, rational.getNominator());
      assertEquals("denominator", 3, rational.getDenominator());
      assertTrue("negative", rational.isNegative());
      assertEquals("format", "-\\frac[1]{2}{3}",
                   rational.format(true).toString());

      // how about some negative
      rational = new Rational(1, -2, 3);

      assertEquals("not defined after setting", true, rational.isDefined());
      assertEquals("output", "1/3", rational.toString());
      assertEquals("value", 0.333, rational.getValue(), 0.001);
      assertEquals("leader", 0, rational.getLeader());
      assertEquals("nominator", 1, rational.getNominator());
      assertEquals("denominator", 3, rational.getDenominator());
      assertFalse("negative", rational.isNegative());
      assertEquals("format", "\\frac{1}{3}", rational.format(true).toString());

      // how about some negative
      rational = new Rational(1, -4, 3);

      assertEquals("not defined after setting", true, rational.isDefined());
      assertEquals("output", "-1/3", rational.toString());
      assertEquals("value", -0.333, rational.getValue(), 0.001);
      assertEquals("leader", 0, rational.getLeader());
      assertEquals("nominator", 1, rational.getNominator());
      assertEquals("denominator", 3, rational.getDenominator());
      assertTrue("negative", rational.isNegative());
      assertEquals("format", "-\\frac{1}{3}", rational.format(true).toString());

      // how about some negative
      rational = new Rational(0, -1, 3);

      assertEquals("not defined after setting", true, rational.isDefined());
      assertEquals("output", "-1/3", rational.toString());
      assertEquals("value", -0.333, rational.getValue(), 0.001);
      assertEquals("leader", 0, rational.getLeader());
      assertEquals("nominator", 1, rational.getNominator());
      assertEquals("denominator", 3, rational.getDenominator());
      assertTrue("negative", rational.isNegative());
      assertEquals("format", "-\\frac{1}{3}", rational.format(true).toString());

      Value.Test.createTest(rational);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Test reading. */
    @org.junit.Test
    public void testRead()
    {
      String []tests =
        {
          "simple", "3/4", "3/4", null,
          "complete", "1 1/4", "1 1/4", null,
          "negative", "-1 1/3", "-1 1/3", null,
          "negative 2", "-1/4", "-1/4", null,
          "zero", "+0 2/3", "2/3", null,
          "zero 2", "-0 2/3", "2/3", null,
          "empty", "", null,  null,
          "whites", "-1 \n1  /  3 ", "-1 1/3", " ",
          "negatives", "-1/-3", "1/3", null,
          "negatives 2", "1/-3", "-1/3", null,
          "negatives 3", "1 -1/-3", "1 1/3", null,
          "negatives 4", "-1 -1/-3", "-1 1/3", null,
          "negatives 5", "1 1/-3", "2/3", null,
          "negatives 6", "-1 1/-3", "-1 1/3", null,
          "other", "2a", "2", "a",
          "other 2", "0 a", "0", "a",
          "invalid", "1/0", "1", "/0",
          "other 3", "1 2", "1", "2",
          "zero 3", "0 0/2", "0", null,
          "zero 4", "5 0/2", "5", null,
          "none", "a", null, "a",
          "invalid", "1/a", "1", "/a",
          "completely invalid", "a/b", null, "a/b",
          "leader invalid", "1 3/a", "1", "3/a",
          "zero only", "0", "0", null,
          "single", "3", "3", null,
          "simple", "3/4", "3/4", null,
        };

      Value.Test.readTest(tests, new Rational());

      // m_logger.addExpectedPattern("WARNING:.*\\(5d>>>e\\).*");
    }

    //......................................................................
    //----- reduce ---------------------------------------------------------

    /** Test reduction. */
    @org.junit.Test
    public void testReduce()
    {
      Rational value = new Rational(4, 8).reduce();
      assertEquals("reduced", "1/2", value.toString());

      value = new Rational(1, 35, 15).reduce();
      assertEquals("reduced", "3 1/3", value.toString());

      value = new Rational(1, -1, 2).reduce();
      assertEquals("reduced", "1/2", value.toString());

      value = new Rational(-1, -1, 2).reduce();
      assertEquals("reduced", "-1 1/2", value.toString());

      value = new Rational(-1, 1, 2).reduce();
      assertEquals("reduced", "-1 1/2", value.toString());

      value = new Rational(-1, -1, -2).reduce();
      assertEquals("reduced", "-1 1/2", value.toString());

      value = new Rational(11, -2).reduce();
      assertEquals("reduced", "-5 1/2", value.toString());

      value = new Rational(1, 11, -2).reduce();
      assertEquals("reduced", "-4 1/2", value.toString());
    }

    //......................................................................
    //----- add ------------------------------------------------------------

    /** Test additions. */
    // public void testAdd()
    // {
    //   Rational first  = new Rational(1, 2, 9);
    //   Rational second = new Rational(2, 1, 6);

    //   first.add(second);
    //   assertEquals("add", "3 7/18", first.toString());

    //   first.add(42);
    //   assertEquals("add int", "45 7/18", first.toString());

    //   first.add(-20);
    //   assertEquals("add int", "25 7/18", first.toString());

    //   first.add(-30);
    //   assertEquals("add int", "-4 11/18", first.toString());

    //   first.add(5);
    //   assertEquals("add int", "7/18", first.toString());

    //   first.set(5);
    //   second.set(-2, 5, 6);

    //   first.add(second);
    //   assertEquals("add negative", "2 1/6", first.toString());

    //   first.set(-2, 5, 6);
    //   second.set(1, 1, 6);

    //   first.add(second);
    //   assertEquals("add negative 2", "-1 2/3", first.toString());
    // }

    //......................................................................
    //----- subtract -------------------------------------------------------

    /** Test subtraction. */
    // public void testSubtract()
    // {
    //   Rational first  = new Rational(1, 2, 9);
    //   Rational second = new Rational(2, 1, 6);

    //   first.subtract(second);
    //   assertEquals("subtract", "-17/18", first.toString());

    //   first.subtract(42);
    //   assertEquals("subtract 2", "-42 17/18", first.toString());

    //   first = new Rational(5);
    //   second = new Rational(2, 1, 2);

    //   first.subtract(second);
    //   assertEquals("subtract 3", "2 1/2", first.toString());

    //   first = new Rational(10);
    //   second = new Rational(2, 5, 6);

    //   first.subtract(second);
    //   assertEquals("subtract 4", "7 1/6", first.toString());

    //   second.set(-2, 1, 6);
    //   first.subtract(second);
    //   assertEquals("subtract negative", "9 1/3", first.toString());
    // }

    //......................................................................
    //----- divide ---------------------------------------------------------

    /** Test divisions. */
    // public void testDivide()
    // {
    //   Rational value  = new Rational(2, 2, 8);

    //   value.divide(1);
    //   assertEquals("divide", "2 2/8",  value.toString());

    //   value.divide(5);
    //   assertEquals("divide", "9/20",   value.toString());

    //   value.divide(3);
    //   assertEquals("divide", "3/20",  value.toString());

    //   value.divide(-2);
    //   assertEquals("divide", "-3/40",  value.toString());

    //   value.set(-5, 2, 3);
    //   value.divide(-3);
    //   assertEquals("divide", "1 8/9",  value.toString());

    //   value.set(1, 2, 3);
    //   value.divide(new Rational(4, 5, 6));
    //   assertEquals("divide", "10/29", value.toString());
    // }

    //......................................................................
    //----- multiply -------------------------------------------------------

    /** Test multiplication. */
    // public void testMultiply()
    // {
    //   Rational value  = new Rational(2, 2, 8);

    //   value.multiply(1);
    //   assertEquals("multiply", "2 2/8",  value.toString());

    //   value.multiply(5);
    //   assertEquals("multiply", "11 1/4", value.toString());

    //   value.multiply(4);
    //   assertEquals("multiply", "45",      value.toString());

    //   value.set(-2, 1, 2);
    //   value.multiply(-3);

    //   assertEquals("multiply", "7 1/2",      value.toString());

    //   value.multiply(-2);
    //   assertEquals("multiply", "-15",      value.toString());

    //   value = new Rational(2, 3, 5);

    //   value.multiply(new Rational(3, 4, 6));
    //   assertEquals("multiply", "9 8/15", value.toString());

    //   value.multiply(new Rational(1, 4));
    //   assertEquals("multiply", "2 23/60", value.toString());
    // }

    //......................................................................
    //----- set ------------------------------------------------------------

    /** Test setting. */
    // public void testSet()
    // {
    //   Rational rational = new Rational();

    //    // undefined value
    //   assertEquals("not undefined at start", false, rational.isDefined());
    //   assertEquals("undefined value not correct", "$undefined$",
    //                rational.toString());

    //   assertTrue("set", rational.set(5, 6));
    //   assertTrue("set", rational.isDefined());
    //   assertEquals("set", "5/6", rational.toString());

    //   assertTrue("set", rational.set(42, 5, 6));
    //   assertTrue("set", rational.isDefined());
    //   assertEquals("set", "42 5/6", rational.toString());

    //   assertFalse("set", rational.set(42, 5, 0));
    //   assertTrue("set", rational.isDefined());
    //   assertEquals("set", "42 5/6", rational.toString());

    //   assertTrue("set", rational.set(0, 0, 1));
    //   assertTrue("set", rational.isDefined());
    //   assertEquals("set", "0", rational.toString());

    //   // with dice
    //   assertTrue("set",
    //           rational.set(1, 2, 3, new Dice(1, 4, 0), new Dice(2, 8, 3)));
    //   assertEquals("set", "1 2*(1d4)/3*(2d8 +3)", rational.toString());
    // }

    //......................................................................
    //----- compare --------------------------------------------------------

    /** Test comparison with base types. */
    @org.junit.Test
    public void testCompare()
    {
      assertEquals("lower",  -1, new Rational(5).compare(6));
      assertEquals("higher", +1, new Rational(5).compare(4));
      assertEquals("equal",   0, new Rational(5).compare(5));

      assertEquals("higher 2", +1, new Rational(-5).compare(-6));
      assertEquals("lower 2",  -1, new Rational(-5).compare(-4));
      assertEquals("equal 2",   0, new Rational(-5).compare(-5));

      assertEquals("lower 3",  -1, new Rational(1, 2).compare(1));
      assertEquals("higher 3", +1, new Rational(1, 2).compare(0));

      assertEquals("lower 4",  -1, new Rational(-1, 2).compare(0));
      assertEquals("higher 4", +1, new Rational(-1, 2).compare(-1));

      assertEquals("lower 5",   -1, new Rational(3, 5, 3).compare(5));
      assertEquals("higher 5a", +1, new Rational(3, 5, 3).compare(4));
      assertEquals("higher 5b", +1, new Rational(3, 5, 3).compare(-4));

      assertEquals("lower 6a",  -1, new Rational(-3, 5, 3).compare(4));
      assertEquals("lower 6b",  -1, new Rational(-3, 5, 3).compare(-4));
      assertEquals("higher 6",  +1, new Rational(-3, 5, 3).compare(-5));

      // special cases
      assertEquals("equal special",  0, new Rational(5, 5).compare(1));
      assertEquals("equal special",  0, new Rational(-14, 7).compare(-2));

      assertEquals("lower special",  -1, new Rational(3, 5, 5).compare(5));
      assertEquals("higher special", +1, new Rational(3, 5, 5).compare(3));
      assertEquals("equal special",   0, new Rational(3, 5, 5).compare(4));
    }

    //......................................................................
    //----- compareRational ------------------------------------------------

    /** Test comparison. */
    @org.junit.Test
    public void testCompareRational()
    {
      assertEquals("lower",  -1, new Rational(5).compare(new Rational(6)));
      assertEquals("lower",  -1, new Rational(5).compare(new Rational(27, 5)));
      assertEquals("lower",  -1,
                   new Rational(5).compare(new Rational(5, 1, 3)));
      assertEquals("higher", +1, new Rational(5).compare(new Rational(4)));
      assertEquals("higher", +1, new Rational(5).compare(new Rational(19, 4)));
      assertEquals("higher", +1,
                   new Rational(5).compare(new Rational(2, 9, 4)));
      assertEquals("equal",   0, new Rational(5).compare(new Rational(5)));
      assertEquals("equal",   0, new Rational(5).compare(new Rational(25, 5)));
      assertEquals("equal",   0, new
                   Rational(5).compare(new Rational(4, 1, 1)));

      assertEquals("lower",  +1, new Rational(-5).compare(new Rational(-6)));
      assertEquals("lower",  +1,
                   new Rational(-5).compare(new Rational(27, -5)));
      assertEquals("lower",  +1,
                   new Rational(-5).compare(new Rational(-5, 1, 3)));
      assertEquals("higher", -1, new Rational(-5).compare(new Rational(-4)));
      assertEquals("higher", -1,
                   new Rational(-5).compare(new Rational(-19, 4)));
      assertEquals("higher", -1,
                   new Rational(-5).compare(new Rational(-2, 9, 4)));
      assertEquals("equal",   0, new Rational(-5).compare(new Rational(-5)));
      assertEquals("equal",   0,
                   new Rational(-5).compare(new Rational(-25, 5)));
      assertEquals("equal",   0, new
                   Rational(-5).compare(new Rational(-4, 1, 1)));

      assertEquals("higher 2", +1, new Rational(-5).compare(new Rational(-6)));
      assertEquals("higher 2", +1,
                   new Rational(-5).compare(new Rational(-19, 3)));
      assertEquals("higher 2", +1,
                   new Rational(-5).compare(new Rational(-6, 3, 4)));
      assertEquals("lower 2",  -1, new Rational(-5).compare(new Rational(-4)));
      assertEquals("lower 2",  -1,
                   new Rational(-5).compare(new Rational(-17, 4)));
      assertEquals("lower 2",  -1,
                   new Rational(-5).compare(new Rational(-4, 2, 3)));
      assertEquals("equal 2",   0, new Rational(-5).compare(new Rational(-5)));
      assertEquals("equal 2",   0,
                   new Rational(-5).compare(new Rational(-10, 2)));
      assertEquals("equal 2",   0,
                   new Rational(-5).compare(new Rational(-4, 2, 2)));

      assertEquals("lower 3",  -1,
                   new Rational(1, 2).compare(new Rational(1)));
      assertEquals("lower 3",  -1,
                   new Rational(1, 2).compare(new Rational(2, 3)));
      assertEquals("lower 3",  -1,
                   new Rational(1, 2).compare(new Rational(1, 1, 3)));
      assertEquals("higher 3", +1,
                   new Rational(1, 2).compare(new Rational(0)));
      assertEquals("higher 3", +1,
                   new Rational(1, 2).compare(new Rational(1, 3)));
      assertEquals("higher 3", +1,
                   new Rational(1, 2).compare(new Rational(0, 1, 4)));

      assertEquals("lower 4",  -1,
                   new Rational(-1, 2).compare(new Rational(0)));
      assertEquals("lower 4",  -1,
                   new Rational(-1, 2).compare(new Rational(-1, 3)));
      assertEquals("lower 4",  -1,
                   new Rational(-1, 2).compare(new Rational(0, 1, 4)));
      assertEquals("higher 4", +1,
                   new Rational(-1, 2).compare(new Rational(-1)));
      assertEquals("higher 4", +1,
                   new Rational(-1, 2).compare(new Rational(-2, 3)));
      assertEquals("higher 4", +1,
                   new Rational(-1, 2).compare(new Rational(-1, 1, 2)));

      assertEquals("lower 5",   -1,
                   new Rational(3, 5, 3).compare(new Rational(5)));
      assertEquals("lower 5",   -1,
                   new Rational(3, 5, 3).compare(new Rational(10, 2)));
      assertEquals("lower 5",   -1,
                   new Rational(3, 5, 3).compare(new Rational(3, 6, 3)));
      assertEquals("higher 5a", +1,
                   new Rational(3, 5, 3).compare(new Rational(4)));
      assertEquals("higher 5a", +1,
                   new Rational(3, 5, 3).compare(new Rational(13, 3)));
      assertEquals("higher 5a", +1,
                   new Rational(3, 5, 3).compare(new Rational(4, 1, 3)));
      assertEquals("higher 5b", +1,
                   new Rational(3, 5, 3).compare(new Rational(-4)));
      assertEquals("higher 5b", +1,
                   new Rational(3, 5, 3).compare(new Rational(-13, 3)));
      assertEquals("higher 5b", +1,
                   new Rational(3, 5, 3).compare(new Rational(-4, 1, 3)));

      assertEquals("lower 6a",  -1,
                   new Rational(-3, 5, 3).compare(new Rational(4)));
      assertEquals("lower 6a",  -1,
                   new Rational(-3, 5, 3).compare(new Rational(13, 3)));
      assertEquals("lower 6a",  -1,
                   new Rational(-3, 5, 3).compare(new Rational(4, 1, 2)));
      assertEquals("lower 6b",  -1,
                   new Rational(-3, 5, 3).compare(new Rational(-4)));
      assertEquals("lower 6b",  -1,
                   new Rational(-3, 5, 3).compare(new Rational(-12, 3)));
      assertEquals("lower 6b",  -1,
                   new Rational(-3, 5, 3).compare(new Rational(-3, 4, 3)));
      assertEquals("higher 6",  +1,
                   new Rational(-3, 5, 3).compare(new Rational(-5)));
      assertEquals("higher 6",  +1,
                   new Rational(-3, 5, 3).compare(new Rational(-19, 4)));
      assertEquals("higher 6",  +1,
                   new Rational(-3, 5, 3).compare(new Rational(-4, 3, 3)));

      // special cases
      assertEquals("equal special",  0,
                   new Rational(5, 5).compare(new Rational(1)));
      assertEquals("equal special",  0,
                   new Rational(5, 5).compare(new Rational(3, 3)));
      assertEquals("equal special",  0,
                   new Rational(5, 5).compare(new Rational(0, 4, 4)));
      assertEquals("equal special",  0,
                   new Rational(-14, 7).compare(new Rational(-2)));
      assertEquals("equal special",  0,
                   new Rational(-14, 7).compare(new Rational(-4, 2)));
      assertEquals("equal special",  0,
                   new Rational(-14, 7).compare(new Rational(-1, 3, 3)));

      assertEquals("lower special",  -1,
                   new Rational(3, 5, 5).compare(new Rational(5)));
      assertEquals("lower special",  -1,
                   new Rational(3, 5, 5).compare(new Rational(26, 5)));
      assertEquals("lower special",  -1,
                   new Rational(3, 5, 5).compare(new Rational(4, 1, 2)));
      assertEquals("higher special", +1,
                   new Rational(3, 5, 5).compare(new Rational(3)));
      assertEquals("higher special", +1,
                   new Rational(3, 5, 5).compare(new Rational(19, 6)));
      assertEquals("higher special", +1,
                   new Rational(3, 5, 5).compare(new Rational(3, 1, 4)));
      assertEquals("equal special",   0,
                   new Rational(3, 5, 5).compare(new Rational(4)));
      assertEquals("equal special",   0,
                   new Rational(3, 5, 5).compare(new Rational(16, 4)));
      assertEquals("equal special",   0,
                   new Rational(3, 5, 5).compare(new Rational(2, 4, 2)));
    }

    //......................................................................
  }

  //........................................................................
}
