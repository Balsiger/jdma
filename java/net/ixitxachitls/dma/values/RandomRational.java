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
public class RandomRational extends BaseRational<RandomRational>
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- RandomRational ----------------------------

  /**
   * Construct the rational object with an undefined value.
   *
   */
  public RandomRational()
  {
    // nothing to do
  }

  //........................................................................
  //---------------------------- RandomRational ----------------------------

  /**
   * Construct the rational object (simple integer).
   *
   * @param       inLeader the leading value
   *
   */
  public RandomRational(long inLeader)
  {
    this(inLeader, 0, 1);
  }

  //........................................................................
  //---------------------------- RandomRational ----------------------------

  /**
   * Construct the rational object with a real fraction.
   *
   * @param       inNominator   value above the line
   * @param       inDenominator value below the line
   *
   */
  public RandomRational(long inNominator, long inDenominator)
  {
    this(0, inNominator, inDenominator);
  }

  //........................................................................
  //---------------------------- RandomRational ----------------------------

  /**
   * Construct the rational object with all values.
   *
   * @param       inLeader      the leader before the real fraction
   * @param       inNominator   value above the line
   * @param       inDenominator value below the line
   *
   */
  public RandomRational(long inLeader, long inNominator, long inDenominator)
  {
    super(inLeader, inNominator, inDenominator);
  }

  //........................................................................
  //---------------------------- RandomRational ----------------------------

  /**
   * Construct the rational object random values.
   *
   * @param       inLeader            the leader before the real fraction
   * @param       inNominator         value above the line
   * @param       inDenominator       value below the line
   * @param       inRandomNominator   a random nominator value (as a dice)
   * @param       inRandomDenominator a random denominator value (as a dice)
   *
   */
  public RandomRational(long inLeader, long inNominator, long inDenominator,
                        Dice inRandomNominator, Dice inRandomDenominator)
  {
    this(inLeader, inNominator, inDenominator);

    m_randomNominator = inRandomNominator;
    m_randomDenominator = inRandomDenominator;
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
  public @Nonnull RandomRational create()
  {
    return super.create(new RandomRational());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The fractions random nominator (above the line ;-)). */
  protected Dice m_randomNominator = null;

  /** The fractions random denominator (below the line ;-)). */
  protected Dice m_randomDenominator = null;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------- getNominatorString --------------------------

  /**
   * Get the nominator as a String.
   *
   * @return      a String representation of the nominator
   *
   */
  @Override
  protected @Nonnull String getNominatorString()
  {
    if(m_randomNominator == null)
      return super.getNominatorString();

    if(m_nominator != 1)
      return super.getNominatorString() + "*"
        + m_randomNominator.toString();

    return m_randomNominator.toString();
  }

  //........................................................................
  //------------------------- getDenominatorString -------------------------

  /**
   * Get the denominator as a String.
   *
   * @return      a String representation of the denominator
   *
   */
  @Override
  protected @Nonnull String getDenominatorString()
  {
    if(m_randomDenominator == null)
      return super.getDenominatorString();

    if(m_denominator != 1)
      return super.getDenominatorString() + "*"
        + m_randomDenominator.toString();

    return m_randomDenominator.toString();
  }

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

    int nominator = 1;
    int denominator = 1;

    if(m_randomNominator != null)
      nominator = m_randomNominator.roll();

    if(m_randomDenominator != null)
      denominator = m_randomDenominator.roll();

    if(m_negative)
      return -1 * (m_leader + ((double)m_nominator * nominator)
                   / ((double)m_denominator * denominator));
    else
      return m_leader + ((double)m_nominator * nominator)
        / ((double)m_denominator * denominator);
  }

  //........................................................................

  //-------------------------------- getMin --------------------------------

  /**
   * Get the minimal possible value.
   *
   * @return      the minimal possible value
   *
   */
  public Rational getMin()
  {
    return new Rational(m_leader,
                        m_randomNominator != null
                        ? m_nominator * m_randomNominator.getMin()
                        : m_nominator,
                        m_randomDenominator != null
                        ? m_denominator * m_randomDenominator.getMax()
                        : m_denominator)
      .reduce();
  }

  //........................................................................
  //-------------------------------- getMax --------------------------------

  /**
   * Get the maximal possible value.
   *
   * @return      the maximal possible value
   *
   */
  public Rational getMax()
  {
    return new Rational(m_leader,
                        m_randomNominator != null
                        ? m_nominator * m_randomNominator.getMax()
                        : m_nominator,
                        m_randomDenominator != null
                        ? m_denominator * m_randomDenominator.getMin()
                        : m_denominator)
      .reduce();
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
    return super.isDefined() || m_randomDenominator != null
      || m_randomNominator != null;
  }

  //........................................................................
  //-------------------------------- isOne ---------------------------------

  /**
   * Check if the value stored is equal to a singular value.
   *
   * @return      true if 1, false else
   *
   */
  public boolean isOne()
  {
    if(m_randomNominator != null || m_randomDenominator != null)
      return false;

    return super.isOne();
  }

  //........................................................................
  //-------------------------------- isNull --------------------------------

  /**
   * Check if the value stored is equal to 0.
   *
   * @return      true if 0, false else
   *
   */
  public boolean isNull()
  {
    if(m_randomNominator != null || m_randomDenominator != null)
      return false;

    return super.isNull();
  }

  //........................................................................
  //---------------------------- isRealFraction ----------------------------

  /**
   * Check if the rational has a denominator.
   *
   * @return    true if it's a real fraction, false if notj
   *
   */
  public boolean isRealFraction()
  {
    return super.isRealFraction() || m_randomDenominator != null;
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

  //-------------------------------- doRead --------------------------------

  /**
   * Read the value from the reader and replace the current one.
   *
   * @param       inReader the reader to read from
   *
   * @return      true if read, false if not
   *
   */
  // @Override
  // public boolean doRead(@Nonnull ParseReader inReader)
  // {
  //   if(!readNominator(inReader))
  //     return false;

  //   if(!inReader.expect('/'))
  //     if(m_nominator != 0 && m_randomNominator == null)
  //       m_leader = m_nominator;
  //     else
  //       return true;

  //   // Read again, this time we can't have a leader.
  //   if(!readNominator(inReader))
  //     return true;

  //   ParseReader.Position pos = inReader.getPosition();
  //   if(inReader.expect('/') && !readDenominator(inReader))
  //     inReader.seek(pos);

  //   return true;
  // }

  //........................................................................

  //---------------------------- readNominator -----------------------------

  /**
   * Try to read the nominator (above line).
   *
   * @param       inReader where to read from
   *
   * @return      true if read, false if not
   *
   */
  protected boolean readNominator(@Nonnull ParseReader inReader)
  {
    ParseReader.Position pos = inReader.getPosition();

    if(!super.readNominator(inReader))
      return false;

    boolean expectDice = false;
    if(inReader.expect('d'))
    {
      expectDice = true;

      // it was actually a dice that we should have read
      inReader.seek(pos);
      m_nominator = 1;
    }
    else
      if(!inReader.expect('*'))
        return true;

    m_randomNominator = readDice(inReader);
    return !expectDice || m_randomNominator != null;
  }

  //........................................................................
  //--------------------------- readDeominator ----------------------------

  /**
   * Try to read the denominator (below line).
   *
   * @param       inReader where to read from
   *
   * @return      true if read, false if not
   *
   */
  protected boolean readDenominator(@Nonnull ParseReader inReader)
  {
    ParseReader.Position pos = inReader.getPosition();

    if(!super.readDenominator(inReader))
      return false;

    if(inReader.expect('d'))
    {
      // it was actually a dice that we should have read
      inReader.seek(pos);
      m_denominator = 1;
    }
    else
      if(!inReader.expect('*'))
        return true;

    m_randomDenominator = readDice(inReader);
    return true;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //--------------------------- nominatorToLeader --------------------------

  /**
   * Move the value of the nominator to the leader, if possible.
   *
   * @return      true if moving was possible, false if not
   *
   */
  @Override
  protected boolean nominatorToLeader()
  {
    if(m_randomNominator == null)
      return super.nominatorToLeader();

    if(!m_randomNominator.isRandom())
    {
      super.nominatorToLeader();
      m_leader = m_randomNominator.getMin();

      return true;
    }

    return false;
  }

  //........................................................................
  //------------------------------- readDice -------------------------------

  /**
   * Read a dice from the given reader.
   *
   * @param       inReader the reader to read from
   *
   * @return      the Dice read or null if none found
   *
   */
  private @Nullable Dice readDice(@Nonnull ParseReader inReader)
  {
    return new Dice().read(inReader);
  }

  //........................................................................
  //---------------------------- resetNominator ----------------------------

  /**
   * Reset the nominator value.
   *
   */
  protected void resetNominator()
  {
    super.resetNominator();
    m_randomNominator = null;
  }

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
      RandomRational rational = new RandomRational();

      // undefined value
      assertEquals("not undefined at start", false, rational.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   rational.toString());
      assertEquals("undefined value", 0.0, rational.getValue(), 0.001);
      assertEquals("undefined leader", 0, rational.getLeader());
      assertEquals("undefined nominator", 0, rational.m_nominator);
      assertEquals("undefined denominator", 0, rational.m_denominator);
      assertFalse("negative", rational.isNegative());
      assertEquals("format", "", rational.format(true).toString());

      // now with some rational
      rational = new RandomRational(1, 2);

      assertEquals("not defined after setting", true, rational.isDefined());
      assertEquals("output", "1/2", rational.toString());
      assertEquals("value", 0.5, rational.getValue(), 0.001);
      assertEquals("leader", 0, rational.getLeader());
      assertEquals("nominator", 1, rational.m_nominator);
      assertEquals("denominator", 2, rational.m_denominator);
      assertFalse("negative", rational.isNegative());
      assertEquals("format", "\\frac{1}{2}", rational.format(true).toString());
      assertEquals("min", "1/2", rational.getMin().toString());
      assertEquals("max", "1/2", rational.getMin().toString());

      // now with some rational
      rational = new RandomRational(1, 2, 3);

      assertEquals("not defined after setting", true, rational.isDefined());
      assertEquals("output", "1 2/3", rational.toString());
      assertEquals("value", 1.666, rational.getValue(), 0.001);
      assertEquals("leader", 1, rational.getLeader());
      assertEquals("nominator", 2, rational.m_nominator);
      assertEquals("denominator", 3, rational.m_denominator);
      assertFalse("negative", rational.isNegative());
      assertEquals("format", "\\frac[1]{2}{3}",
                   rational.format(true).toString());

      // how about some negative
      rational = new RandomRational(1, -2, -3);

      assertEquals("not defined after setting", true, rational.isDefined());
      assertEquals("output", "1 2/3", rational.toString());
      assertEquals("value", 1.666, rational.getValue(), 0.001);
      assertEquals("leader", 1, rational.getLeader());
      assertEquals("nominator", 2, rational.m_nominator);
      assertEquals("denominator", 3, rational.m_denominator);
      assertFalse("negative", rational.isNegative());
      assertEquals("format", "\\frac[1]{2}{3}",
                   rational.format(true).toString());

      // how about some negative
      rational = new RandomRational(-1, 2, 3);

      assertEquals("not defined after setting", true, rational.isDefined());
      assertEquals("output", "-1 2/3", rational.toString());
      assertEquals("value", -1.666, rational.getValue(), 0.001);
      assertEquals("leader", 1, rational.getLeader());
      assertEquals("nominator", 2, rational.m_nominator);
      assertEquals("denominator", 3, rational.m_denominator);
      assertTrue("negative", rational.isNegative());
      assertEquals("format", "-\\frac[1]{2}{3}",
                   rational.format(true).toString());

      // how about some negative
      rational = new RandomRational(-1, -2, 3);

      assertEquals("not defined after setting", true, rational.isDefined());
      assertEquals("output", "-1 2/3", rational.toString());
      assertEquals("value", -1.666, rational.getValue(), 0.001);
      assertEquals("leader", 1, rational.getLeader());
      assertEquals("nominator", 2, rational.m_nominator);
      assertEquals("denominator", 3, rational.m_denominator);
      assertTrue("negative", rational.isNegative());
      assertEquals("format", "-\\frac[1]{2}{3}",
                   rational.format(true).toString());

      // how about some negative
      rational = new RandomRational(1, -2, 3);

      assertEquals("not defined after setting", true, rational.isDefined());
      assertEquals("output", "1/3", rational.toString());
      assertEquals("value", 0.333, rational.getValue(), 0.001);
      assertEquals("leader", 0, rational.getLeader());
      assertEquals("nominator", 1, rational.m_nominator);
      assertEquals("denominator", 3, rational.m_denominator);
      assertFalse("negative", rational.isNegative());
      assertEquals("format", "\\frac{1}{3}", rational.format(true).toString());

      // how about some negative
      rational = new RandomRational(1, -4, 3);

      assertEquals("not defined after setting", true, rational.isDefined());
      assertEquals("output", "-1/3", rational.toString());
      assertEquals("value", -0.333, rational.getValue(), 0.001);
      assertEquals("leader", 0, rational.getLeader());
      assertEquals("nominator", 1, rational.m_nominator);
      assertEquals("denominator", 3, rational.m_denominator);
      assertTrue("negative", rational.isNegative());
      assertEquals("format", "-\\frac{1}{3}", rational.format(true).toString());

      // how about some negative
      rational = new RandomRational(0, -1, 3);

      assertEquals("not defined after setting", true, rational.isDefined());
      assertEquals("output", "-1/3", rational.toString());
      assertEquals("value", -0.333, rational.getValue(), 0.001);
      assertEquals("leader", 0, rational.getLeader());
      assertEquals("nominator", 1, rational.m_nominator);
      assertEquals("denominator", 3, rational.m_denominator);
      assertTrue("negative", rational.isNegative());
      assertEquals("format", "-\\frac{1}{3}", rational.format(true).toString());

      // now with some random rational
      rational = new RandomRational(0, 1, 1, new Dice(1, 2, 0),
                                    new Dice(2, 4, -2));

      assertEquals("not defined after sestting", true,
                   rational.isDefined());
      assertEquals("output", "1d2/2d4 -2", rational.toString());
      assertEquals("format", "\\frac{1d2}{2d4 -2}",
                   rational.format(true).toString());
      assertEquals("min", "1/6", rational.getMin().toString());
      assertEquals("max", "2", rational.getMax().toString());

      // how about some mixed
      rational = new RandomRational(0, 2, 1, null, new Dice(1, 4, 5));

      assertEquals("not defined after setting", true,
                   rational.isDefined());
      assertEquals("output", "2/1d4 +5", rational.toString());
      assertEquals("format", "\\frac{2}{1d4 +5}",
                   rational.format(true).toString());
      assertEquals("min", "2/9", rational.getMin().toString());
      assertEquals("max", "1/3", rational.getMax().toString());

      // how about some mixed top
      rational = new RandomRational(0, 1, 3, new Dice(5, 6, 2), null);

      assertEquals("not defined after setting", true,
                   rational.isDefined());
      assertEquals("output", "5d6 +2/3", rational.toString());
      assertEquals("format", "\\frac{5d6 +2}{3}",
                   rational.format(true).toString());
      assertEquals("min", "2 1/3", rational.getMin().toString());
      assertEquals("max", "10 2/3", rational.getMax().toString());

      // how about some dice only
      rational = new RandomRational(0, 1, 1, new Dice(5, 6, 2), null);

      assertEquals("not defined after setting", true,
                   rational.isDefined());
      assertEquals("output", "5d6 +2", rational.toString());
      assertEquals("format", "5d6 +2", rational.format(true).toString());
      assertEquals("min", "7", rational.getMin().toString());
      assertEquals("max", "32", rational.getMax().toString());

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
          "whites", "-1 \n1  /  3 ", "-1 1/3", "",
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
          "random", "1d4", "1d4", null,
          "complete random", "2d5 -2/1d3 +2", "2d5 -2/1d3 +2", null,
          "mixed top", "1d6/2", "1d6/2", null,
          "mixed bottom", "3/5d3 +3", "3/5d3 +3", null,
          "leader", "5 2d6", "5", "2d6",
          "all", "2 3*5d6 -2/2*1d4 +1", "2 3*5d6 -2/2*1d4 +1", null,
          "mixed top*", "2 * 1d6/3", "2*1d6/3",  null,
          "mixed bottom*", "55/4*1d6", "55/4*1d6", null,
          "none", "a", null, "a",
          "none", "5de", null, "5de",
          "start", "1 d3", "1d3", null,
          "start 2", "1 2d3", "1", "2d3",
          "none 2", "6d2 +3", "6d2 +3", null,
          "none 3", "2 * ", "2",  null,
          "leader", "42 2 *", "42", "2 *",
          "bottom", "1/2 *", "1/2", null,
        };

      Value.Test.readTest(tests, new RandomRational());

      m_logger.addExpectedPattern("WARNING:.*5de>>>.*");
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
    //----- value ----------------------------------------------------------

    /** Testing of random values. */
    public void testValue()
    {
      RandomRational rational = new RandomRational(0, 1, 1, new Dice(1, 2, 0),
                                                   new Dice(2, 4, -2));

      for(int i = 0; i < 10; i++)
      {
        double value = rational.getValue();

        assertTrue("low",  value >= 1.0 / 6);
        assertTrue("high", value <= 2.0);
      }

      rational =
        new RandomRational(5, 2, 3, new Dice(2, 8, -4), new Dice(1, 12, 0));

      for(int i = 0; i < 10; i++)
      {
        double value = rational.getValue();

        assertTrue("low",  value >= 5 + 2.0 / (3 * 12));
        assertTrue("high", value <= 5 + 12.0);
      }
    }

    //......................................................................
  }

  //........................................................................
}
