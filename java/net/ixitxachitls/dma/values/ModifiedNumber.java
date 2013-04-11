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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.util.Strings;

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

@ParametersAreNonnullByDefault
@Immutable
public class ModifiedNumber extends BaseNumber<ModifiedNumber>
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- ModifiedNumber ----------------------------

  /**
   * Construct the number object using real values.
   *
   * @param       inNumber    the number inside this value
   *
   */
  public ModifiedNumber(long inNumber)
  {
    this(inNumber, false);
  }

  //........................................................................
  //---------------------------- ModifiedNumber ----------------------------

  /**
   * Construct the number object using real values.
   *
   * @param       inNumber    the number inside this value
   * @param       inSign      whether to show a + for positive numbers or not
   *
   */
  public ModifiedNumber(long inNumber, boolean inSign)
  {
    super(inNumber, -100, +100, inSign);
    m_sign = inSign;
  }

  //........................................................................
  //---------------------------- ModifiedNumber ----------------------------

  /**
   * Construct the number object as undefined.
   */
  public ModifiedNumber()
  {
    super(-100, +100, false);
  }

  //........................................................................
  //-------------------------------- create --------------------------------

  /**
   * Create a new modified number.
   *
   * @param   inBase           the base value for the number
   * @param   inContributions  the various modifications to the number
   * @param   <T>              the value type contributed
   *
   * @return  a newly created modified number
   */
  // public static <T extends Value<T>> ModifiedNumber
  //   create(int inBase, List<Contribution<T>> inContributions)
  // {
  //   ModifiedNumber number = new ModifiedNumber(inBase, true);

  //   for(Contribution<T> contribution : inContributions)
  //   {
  //     T value = contribution.getValue();
  //     String text = contribution.getDescription();

  //     if(value instanceof Number)
  //       number.withModifier(new Modifier((int)((Number)value).get()), text);
  //     else if(value instanceof Modifier)
  //       number.withModifier((Modifier)value, text);
  //     else if(value instanceof ModifiedNumber)
  //     {
  //       number.withModifier(new Modifier((int)((ModifiedNumber)value).get()),
  //                           text);
  //       for(Map.Entry<String, Modifier> modifier
  //             : ((ModifiedNumber)value).m_modifiers.entrySet())
  //         number.withModifier(modifier.getValue(), modifier.getKey());
  //     }
  //     else
  //       throw new UnsupportedOperationException("cannot modifiy number with "
  //                                               + value.getClass());
  //   }

  //   return number;
  // }

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
  public ModifiedNumber withModifier(Modifier inModifier, String inName)
  {
    if(!inModifier.isDefined())
      return this;

    String name = inName;
    if(m_modifiers.containsKey(name))
    {
      int i = 1;
      while(m_modifiers.containsKey(name + " (" + i++ + ")"))
        ;

      name = name + " (" + i + ")";
    }

    m_modifiers.put(name, inModifier);
    if(m_total == null)
      m_total = inModifier;
    else
      m_total = m_total.add(inModifier);

    return this;
  }

  //........................................................................
  //--------------------------------- with ---------------------------------

  /**
   * Add the given modified number to this one and return it.
   *
   * @param       inNumber the modified number to add
   *
   * @return      this object for chaining
   *
   */
  public ModifiedNumber with(ModifiedNumber inNumber)
  {
    // base
    m_number += inNumber.m_number;

    m_total = null;
    m_modifiers.putAll(inNumber.m_modifiers);

    return this;
  }

  //........................................................................
  //------------------------------ withValue -------------------------------

  /**
   * Setup the modified number with the given value.
   *
   * @param    inValue       the value to setup with
   * @param    inDescription the description for the value
   *
   * @return   this modified number for chaining
   */
  public ModifiedNumber withValue(Value<?> inValue, String inDescription)
  {
    if(inValue instanceof Number)
      withModifier(new Modifier((int)((Number)inValue).get()), inDescription);
    else if(inValue instanceof Modifier)
      withModifier((Modifier)inValue, inDescription);
    else if(inValue instanceof ModifiedNumber)
      with((ModifiedNumber)inValue);

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

  /** Flag if showing a plus for positive numbers. */
  private boolean m_sign = false;

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
   * Get all the invididual modifiers by name.
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
  //----------------------------- getMaxValue ------------------------------

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
  //-------------------------------- isZero --------------------------------

  /**
   * Check if the modified number represent zero.
   *
   * @return  true if it is zero, false if no.
   */
  public boolean isZero()
  {
    return getMinValue() == 0 && getMaxValue() == 0;
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
    if(m_total == null)
      return "" + m_number;

    int min = m_total.getMinValue();
    int max = m_total.getMaxValue();

    String prefix;
    if(min == max)
      if(m_sign)
        return Strings.signedNumber(m_number + min);
      else
        return "" + (m_number + min);

    if(m_sign)
      return Strings.signedNumber(m_number + min) + "-"
        + Strings.signedNumber(m_number + max);

    return (m_number + min) + "-" + (m_number + max);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- ignore --------------------------------

  /**
   * Creates a new modified number with all the given types ignored
   * (i.e. removed).
   *
   * @param    inTypes the types to ignore
   *
   * @return   a newly created modified number with the given types ignored
   *
   */
  public ModifiedNumber ignore(Modifier.Type ... inTypes)
  {
    ModifiedNumber modified = new ModifiedNumber(get(), m_sign);
    for (Map.Entry<String, Modifier> entry : m_modifiers.entrySet())
    {
      Modifier modifier = entry.getValue().ignore(inTypes);
      if(modifier != null)
        modified.withModifier(modifier, entry.getKey());
    }

    return modified;
  }

  //........................................................................

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
