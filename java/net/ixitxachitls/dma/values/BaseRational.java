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

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.input.ParseReader;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a rational and is capable of reading such rationals
 * from a reader (and write it to a writer of course). It's the base for
 * rationals and dice rationals.
 *
 * @file          BaseRational.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 * @param         <T> the real type of rational
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public abstract class BaseRational<T extends BaseRational<T>> extends Value<T>
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- BaseRational -----------------------------

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**s
   * Construct an undefined rational.
   *
   */
  protected BaseRational()
  {
    // nothing to do
  }

  //........................................................................
  //----------------------------- BaseRational -----------------------------

  /**
   * Construct the rational object (simple integer).
   *
   * @param       inLeader      the leader before the real fraction
   * @param       inNominator   value above the line
   * @param       inDenominator value below the line
   *
   */
  protected BaseRational(long inLeader, long inNominator, long inDenominator)
  {
    if(inDenominator == 0)
      throw new IllegalArgumentException("denominator may not be zero");

    m_leader = inLeader;
    m_nominator = inNominator;
    m_denominator = inDenominator;

    computeNegative();
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
  public abstract T create();

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The leading value, the whole fractions. */
  protected long m_leader = 0;

  /** The fractions nominator (above the line ;-)). */
  protected long m_nominator = 0;

  /** The fractions denominator (below the line ;-)). A value of 0 means that
   * the Value is undefined. */
  protected long m_denominator = 0;

  /** The sign of the number (all stored values should be positive). */
  protected boolean m_negative = false;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getLeader -------------------------------

  /**
   * Get the leader stored.
   *
   * @return      the leader stored
   *
   */
  public long getLeader()
  {
    return m_leader;
  }

  //........................................................................
  //------------------------------- getValue -------------------------------

  /**
   * Get the real value stored.
   *
   * @return      the internal value as a floating point number
   *
   */
  public abstract double getValue();

  //........................................................................

  //------------------------------ isNegative ------------------------------

  /**
   * Check if the value is negative or not.
   *
   * @return      true if the value is negative, false if not
   *
   */
  public boolean isNegative()
  {
    return m_negative;
  }

  //........................................................................
  //----------------------------- hasFraction ------------------------------

  /**
   * Check if the rational has a fraction or not.
   *
   * @return      true if there is a fraction, false if not
   *
   */
  public boolean hasFraction()
  {
    return m_nominator != 0;
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
    if(!hasFraction())
      return false;

    return m_denominator != 1;
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
    return m_denominator != 0;
  }

  //........................................................................

  //-------------------------------- isOne ---------------------------------

  /**
   * Check if the value stored is equal to 1.
   *
   * @return      true if 1, false else
   *
   */
  public boolean isOne()
  {
    return (m_leader == 1 && m_nominator == 0 || m_denominator == 0)
      || (m_leader == 0 && m_nominator == m_denominator);
  }

  //........................................................................
  //----------------------------- isSingular -------------------------------

  /**
   * Check if the value stored is equal to a singular value.
   *
   * @return      true if singlular, false else
   *
   */
  public boolean isSingular()
  {
    return (m_leader == 1 && m_nominator == 0 || m_denominator == 0)
      || m_leader == 0;
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
    return (m_leader == 0 && m_nominator == 0 || m_denominator == 0);
  }

  //........................................................................
  //-------------------------- getNominatorString --------------------------

  /**
   * Get the nominator as a String.
   *
   * @return      a String representation of the nominator
   *
   */
  protected String getNominatorString()
  {
    return "" + m_nominator;
  }

  //........................................................................
  //------------------------- getDenominatorString -------------------------

  /**
   * Get the denominator as a String.
   *
   * @return      a String representation of the denominator
   *
   */
  protected String getDenominatorString()
  {
    return "" + m_denominator;
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
    if(!hasFraction())
      return (m_negative ? "-" : "") +  m_leader;

    String lead;
    if(m_leader == 0)
      lead = (m_negative ? "-" : "");
    else
      lead = (m_negative ? "-" : "") + m_leader + " ";

    if(isRealFraction())
      return lead + getNominatorString() + "/" + getDenominatorString();

    return lead + getNominatorString();
  }

  //........................................................................

  //------------------------------- compare --------------------------------

  /**
   * Compare the BaseRational to the given value.
   *
   * @param       inValue the value to compare to.
   *
   * @return      a value < 0 if this one is below the given value, a
   *              value > 0, if it is above it and 0 if it is equal
   *
   */
  public int compare(long inValue)
  {
    if(m_leader > inValue)
      return +1;

    if(m_leader < inValue)
      return -1;

    return 0;
  }

  //........................................................................
  //------------------------------- compare --------------------------------

  /**
   * Compare the BaseRational to the given value.
   *
   * @param       inValue the value to compare to
   *
   * @return      a value < 0 if this one is below the given value, a
   *              value > 0, if it is above it and 0 if it is equal
   *
   */
  public int compare(BaseRational<T> inValue)
  {
    return compare(inValue.m_leader);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- reduce --------------------------------

  /**
   * Reduce the fraction, if possible. This is protected, as it only reduces
   * the rational part and might not be able to reduce things of a derivation
   * (e.g. Random Rational).
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
  @SuppressWarnings("unchecked") // have to cast this
  protected T reduce()
  {
    // nothing to do if not defined
    if(!isDefined())
      return (T)this;

    // determine new leader
    T result = create();
    result.m_denominator = m_denominator;
    result.m_nominator = m_nominator + m_leader * m_denominator;
    result.m_leader = result.m_nominator / result.m_denominator;
    result.m_negative = m_negative;

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
  //--------------------------------- as ------------------------------------

  /**
   * Return a new value set the value to x y/z.
   *
   * @param       inLeader      the leading value (x)
   * @param       inNominator   the nominator (y)
   * @param       inDenominator the denominator (z)
   *
   * @return      the new rational with the given values
   *
   */
  public T as(long inLeader, long inNominator, long inDenominator)
  {
    if(inDenominator == 0)
      throw new IllegalArgumentException("denominator cannot be 0!");

    T result = create();
    result.m_leader      = inLeader;
    result.m_nominator   = inNominator;
    result.m_denominator = inDenominator;
    result.computeNegative();

    return result;
  }

  //........................................................................
  //---------------------------------- as -----------------------------------

  /**
   * Return a new value as x.
   *
   * @param       inLeader      the leading value (x)
   *
   * @return      the new value created
   *
   */
  public T as(long inLeader)
  {
    return as(inLeader, 0, 1);
  }

  //........................................................................
  //---------------------------------- as ----------------------------------

  /**
   * Return a new value set the value to y/z.
   *
   * @param       inNominator   the nominator (y)
   * @param       inDenominator the denominator (z)
   *
   * @return      the new value created
   *
   */
  public T as(long inNominator, long inDenominator)
  {
    return as(0, inNominator, inDenominator);
  }

  //........................................................................
  //--------------------------- computeNegative ----------------------------

  /**
   * Compute the negative sign and adjust all the values.
   *
   * This does the following:
   *
   *   x  y/ z     -> x     y/z
   *   x -y/ z     -> x-1 z-y/z neg?
   *   x  y/-z     -> x-1 z-y/z neg?
   *   x -y/-z     -> x     y/z
   *  -x  y/ z     -> x     y/z neg
   *  -x -y/ z     -> x+1 z-y/z neg?
   *  -x  y/-z     -> x+1 z-y/z neg?
   *  -x -y/-z     -> x     y/z neg
   *
   */
  protected void computeNegative()
  {
    assert !m_negative : "cannot already have negative computed";

    // first without the leader
    if(m_nominator < 0)
    {
      m_nominator *= -1;
      m_negative = true;
    }
    else
      m_negative = false;

    if(m_denominator < 0)
    {
      m_denominator *= -1;
      m_negative = !m_negative;
    }

    // now we have y/z and neg set to the sign they really have

    // now compute in the leader
    // we have to deal with some special cases here. If the real fraction
    // is positive, then its easy
    if(m_negative)
      if(m_leader < 0)
        m_leader *= -1;
      else
      {
        // now, the second negative actually means to subtract the fraction
        // from the leader
        m_nominator = m_leader * m_denominator - m_nominator;
        m_leader    = 0;

        if(m_nominator < 0)
          m_nominator *= -1;
        else
          m_negative = false;

        if(this instanceof Rational)
        {
          T reduced = reduce();
          m_nominator = reduced.m_nominator;
          m_denominator = reduced.m_denominator;
          m_leader = reduced.m_leader;
          m_negative = reduced.m_negative;
        }
      }
    else
      if(m_leader < 0)
      {
        m_leader *= -1;
        m_negative = true;
      }
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add a value to the rational.
   *
   * @param       inValue the value to add
   *
   * @return      the sum of the values
   *
   */
  @Override
  public T add(T inValue)
  {
    long nominator1 = m_leader * m_denominator + m_nominator;
    if(m_negative)
      nominator1 *= -1;

    long nominator2 = inValue.m_leader * inValue.m_denominator
      + inValue.m_nominator;
    if(inValue.m_negative)
      nominator2 *= -1;

    T result = create();
    result.m_nominator = nominator1 * inValue.m_denominator
      + nominator2 * m_denominator;
    result.m_denominator = m_denominator * inValue.m_denominator;
    result.m_leader = 0;

    if(result.m_nominator < 0)
    {
      result.m_nominator *= -1;
      result.m_negative = true;
    }
    else
      result.m_negative = false;

    return result.reduce();
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add a value to the rational.
   *
   * @param       inValue the value to add
   *
   * @return      a new value representing the addition
   *
   */
  public T add(long inValue)
  {
    return add(as(inValue));
  }

  //........................................................................
  //------------------------------- subtract -------------------------------

  /**
   * Add a value to the rational.
   *
   * @param       inValue the value to add
   *
   * @return      a new value representing the subtraction
   *
   */
  @Override
  public T subtract(T inValue)
  {
    long nominator1 = m_leader * m_denominator + m_nominator;
    if(m_negative)
      nominator1 *= -1;

    long nominator2 = inValue.m_leader * inValue.m_denominator
      + inValue.m_nominator;
    if(inValue.m_negative)
      nominator2 *= -1;

    T result = create();
    result.m_nominator = nominator1 * inValue.m_denominator
      - nominator2 * m_denominator;
    result.m_denominator = m_denominator * inValue.m_denominator;
    result.m_leader = 0;

    if(result.m_nominator < 0)
    {
      result.m_nominator *= -1;
      result.m_negative = true;
    }
    else
      result.m_negative = false;

    return result.reduce();
  }

  //........................................................................
  //------------------------------- subtract -------------------------------

  /**
   * Add a value to the rational.
   *
   * @param       inValue the value to add
   *
   * @return      a new value representing the subtraction
   *
   */
  public T subtract(long inValue)
  {
    return subtract(as(inValue));
  }

  //........................................................................
  //-------------------------------- divide --------------------------------

  /**
   * Divide a value to the rational.
   *
   * @param       inValue the value to divide with
   *
   * @return      a new value representing the division
   *
   */
  @Override
  @SuppressWarnings("unchecked")
  public T divide(long inValue)
  {
    if(inValue == 0)
      throw new IllegalArgumentException("cannot divide by zero!");

    if(inValue == 1)
      return (T)this;

    T result = create();
    result.m_nominator = m_nominator + m_leader * m_denominator;
    result.m_denominator = m_denominator * Math.abs(inValue);
    result.m_leader = 0;
    result.m_negative = inValue < 0 ? !m_negative : m_negative;

    return result.reduce();
  }

  //........................................................................
  //-------------------------------- divide --------------------------------

  /**
   * Divide a value to the rational.
   *
   * @param       inValue the value to divide with
   *
   * @return      a new value representing the division
   *
   */
  @Override
  public T divide(T inValue)
  {
    T result = create();
    result.m_nominator =
      (m_leader * m_denominator + m_nominator) * inValue.m_denominator;
    result.m_denominator =
      (inValue.m_leader * inValue.m_denominator + inValue.m_nominator)
      * m_denominator;
    result.m_leader = 0;
    result.m_negative = inValue.m_negative ? !m_negative : m_negative;

    return result.reduce();
  }

  //........................................................................
  //------------------------------- multiply -------------------------------

  /**
   * Multiply a value to the rational.
   *
   * @param       inValue the value to multiply with
   *
   * @return      a new value representing the multiplication
   *
   */
  @Override
  @SuppressWarnings("unchecked")
  public T multiply(long inValue)
  {
    if(inValue == 1)
      return (T)this;

    T result = create();
    result.m_leader = m_leader * Math.abs(inValue);
    result.m_nominator = m_nominator * Math.abs(inValue);
    result.m_denominator = m_denominator;
    result.m_negative = (inValue < 0 && !m_negative)
      || (inValue >= 0 && m_negative);

    return result.reduce();
  }

  //........................................................................
  //------------------------------- multiply -------------------------------

  /**
   * Multiply a value to the rational.
   *
   * @param       inValue the value to multiply with
   *
   * @return      the newly created, multiplied value
   *
   */
  @Override
  public T multiply(T inValue)
  {
    m_nominator    = (m_leader * m_denominator + m_nominator)
      * (inValue.m_leader * inValue.m_denominator + inValue.m_nominator);
    m_leader       = 0;
    m_denominator *= inValue.m_denominator;

    if(inValue.m_negative)
      m_negative = !m_negative;

    return reduce();
  }

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
  @Override
  public boolean doRead(ParseReader inReader)
  {
    if(!readNominator(inReader))
      return false;

    boolean expectFraction = false;

    m_denominator = 1;
    ParseReader.Position pos = inReader.getPosition();
    if(!inReader.expect('/'))
    {
      if(!nominatorToLeader())
        return true;

      pos = inReader.getPosition();
      if(!readNominator(inReader))
        return true;

      expectFraction = true;
      if(!inReader.expect('/'))
      {
        inReader.seek(pos);
        resetNominator();
        return true;
      }
    }

    if(!readDenominator(inReader))
    {
      if(expectFraction)
        resetNominator();

      inReader.seek(pos);
    }

    computeNegative();
    return true;
  }

  //........................................................................

  //------------------------------ readLeader ------------------------------

  /**
   * Read the leader.
   *
   * @param       inReader where to read from
   *
   * @return      true if read, false if not
   *
   */
  public boolean readLeader(ParseReader inReader)
  {
    try
    {
      // read the leader
      m_leader = inReader.readInt();
      return true;
    }
    catch(net.ixitxachitls.input.ReadException e)
    {
      return false;
    }
  }

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
  protected boolean readNominator(ParseReader inReader)
  {
    try
    {
      m_nominator = inReader.readInt();
      return true;
    }
    catch(net.ixitxachitls.input.ReadException e)
    {
      return false;
    }
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
  protected boolean readDenominator(ParseReader inReader)
  {
    ParseReader.Position pos = inReader.getPosition();

    try
    {
      int denominator = inReader.readInt();
      if(denominator == 0)
      {
        inReader.seek(pos);
        return false;
      }

      m_denominator = denominator;
      return true;
    }
    catch(net.ixitxachitls.input.ReadException e)
    {
      inReader.seek(pos);
      return false;
    }
  }

  //........................................................................
  //---------------------------- resetNominator ----------------------------

  /**
   * Reset the nominator value.
   *
   */
  protected void resetNominator()
  {
    m_nominator = 0;
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
  protected boolean nominatorToLeader()
  {
    m_leader = m_nominator;
    m_nominator = 0;

    return true;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  // no tests here, check Rational and RandomRational.

  //........................................................................
}
