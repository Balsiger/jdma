/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a number and is capable of reading such numbers
 * from a reader (and write it to a writer of course).
 *
 * @file          ModifiedNumber.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class ModifiedNumber extends BaseNumber<ModifiedNumber>
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- ModifiedNumber ----------------------------

  /**
   * Construct the number object using real values.
   *
   * @param       inNumber    the number inside this value
   * @param       inModifiers the modifiers to the value
   *
   */
  public ModifiedNumber(long inNumber)
  {
    super(inNumber, -100, +100, false);
  }

  //........................................................................
  //---------------------------- ModifiedNumber ----------------------------

  /**
   * Construct the number object as undefined.
   *
   */
  public ModifiedNumber()
  {
    super(-100, +100, false);
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
  public ModifiedNumber create()
  {
    return super.create(new ModifiedNumber());
  }

  //........................................................................
  //----------------------------- withModifier -----------------------------

  /**
   * Add a modifier to the number.
   *
   * @param    inModifier the modifier to add
   * @param    inName     the origin of the modifier
   *
   * @return   the modified number for chaining
   *
   */
  public @Nonnull ModifiedNumber withModifier(@Nonnull Modifier inModifier,
                                              @Nonnull String inName)
  {
    if(m_modifiers.containsKey(inName))
      throw new IllegalArgumentException("key " + inName + " already present");

    m_modifiers.put(inName, inModifier);
    if(m_total == null)
      m_total = inModifier;
    else
      m_total = m_total.add(inModifier);

    return this;
  }

  //........................................................................


  {
    withTemplate("modifiednumber");
  }

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The individual modifiers by name. */
  private Map<String, Modifier> m_modifiers = new HashMap<String, Modifier>();

  /** The total modifier with all values. */
  private @Nullable Modifier m_total;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- getModifier ------------------------------

  /**
   * Get the modifier to the base value.
   *
   * @return      the modifier
   *
   */
  public Modifier getModifier()
  {
    return m_total;
  }

  //........................................................................
  //----------------------------- getModifiers -----------------------------

  /**
   * Get all the invididual modifiers by name
   *
   * @return all the modifiers
   *
   */
  public Map<String, Modifier> getModifiers()
  {
    return Collections.unmodifiableMap(m_modifiers);
  }

  //........................................................................
  //----------------------------- getMinValue ------------------------------

  /**
   * Get the minimal possible modified value.
   *
   * @return  the miniaml value
   *
   */
  public long getMinValue()
  {
    if(m_total == null)
      return m_number;

    return m_total.getMinValue() + m_number;
  }

  //........................................................................
  //----------------------------- getMinValue ------------------------------

  /**
   * Get the maximal possible modified value.
   *
   * @return  the maximal value
   *
   */
  public long getMaxValue()
  {
    if(m_total == null)
      return m_number;

    return m_total.getMaxValue() + m_number;
  }

  //........................................................................
  //---------------------------- hasConditions -----------------------------

  /**
   * Returns true if the modified number has conditions and thus cannot
   * be determined fully.
   *
   * @return      true with conditions, false without
   *
   */
  public boolean hasConditions()
  {
    return getMinValue() != getMaxValue();
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
  protected @Nonnull String doToString()
  {
    if(m_total == null)
      return "" + m_number;

    int min = m_total.getMinValue();
    int max = m_total.getMaxValue();

    if(min == max)
      return "" + (m_number + min);

    return (m_number + min) + "-" + (m_number + max);
  }

  //........................................................................

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
