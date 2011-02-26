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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.util.Grouping;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a number and is capable of reading such numbers
 * from a reader (and write it to a writer of course).
 *
 * @file          BaseNumber.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @param         <T> the actual type of value used, to allow clone and similar
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class BaseNumber<T extends BaseNumber> extends Value<T>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ BaseNumber ------------------------------

  /**
   * Construct the number object using real values.
   *
   * @param       inBaseNumber the number inside this value
   * @param       inMin    the minimal number
   * @param       inMax    the maximal value
   * @param       inSign   true if always print a sign (even for +)
   *
   */
  @SuppressWarnings("unchecked") // need to cast
  public BaseNumber(long inBaseNumber, long inMin, long inMax, boolean inSign)
  {
    this(inMin, inMax, inSign);

    m_number  = inBaseNumber;
    m_defined = true;
    m_editType = "number";
    m_grouping = (Grouping<T, Object>)s_grouping;

    check();
  }

  //........................................................................
  //------------------------------ BaseNumber ------------------------------

  /**
   * Construct the number object using real values.
   *
   * @param       inBaseNumber the number inside this value
   * @param       inMin    the minimal number
   * @param       inMax    the maximal value
   *
   */
  public BaseNumber(long inBaseNumber, long inMin, long inMax)
  {
    this(inBaseNumber, inMin, inMax, false);
  }

  //........................................................................
  //------------------------------ BaseNumber ------------------------------

  /**
   * Construct the number object as undefined.
   *
   * @param       inMin    the minimal number
   * @param       inMax    the maximal value
   * @param       inSign   true if always print a sign (even for +)
   *
   */
  @SuppressWarnings("unchecked") // need to cast
  public BaseNumber(long inMin, long inMax, boolean inSign)
  {
    if(inMin > inMax)
      throw new IllegalArgumentException("minimum must be less or equal than "
                                         + "the maximum");
    m_min     = inMin;
    m_max     = inMax;
    m_sign    = inSign;

    m_number  = (inMin + inMax) / 2;
    m_defined = false;
    m_editType = "number";
    m_grouping = (Grouping<T, Object>)s_grouping;
  }

  //........................................................................
  //------------------------------ BaseNumber ------------------------------

  /**
   * Construct the number object as undefined.
   *
   * @param       inMin    the minimal number
   * @param       inMax    the maximal value
   *
   */
  public BaseNumber(long inMin, long inMax)
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
  @SuppressWarnings("unchecked") // this method has to be overriden in
                                 // derivation for this to work
  public T create()
  {
    return super.create((T)new BaseNumber(m_min, m_max, m_sign));
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The number stored. */
  protected long m_number = 0;

  /** The minimally allowed value. */
  protected long m_min = Long.MIN_VALUE;

  /** The maximally allowed value. */
  protected long m_max = Long.MAX_VALUE;

  /** Flag if defined or not. */
  protected boolean m_defined = false;

  /** Flag if the sign is to be printed or not. */
  protected boolean m_sign = false;

  /** The grouping. */
  public static final @Nonnull Grouping<BaseNumber, Object> s_grouping =
    new Group<BaseNumber, Long, Object>(new Group.Extractor<BaseNumber, Long>()
      {
        public @Nonnull Long extract(@Nonnull BaseNumber inValue)
        {
          return inValue.m_number;
        }
      }, new Long [] { -1L, 0L, 1L, 2L, 5L, 10L, 25L, 50L, 100L, 250L, 500L,
                       750L, 1000L, 10000L, 100000L, 1000000L, },
                                    new String []
        { "negative", "0", "1", "2", "5", "10", "25", "50", "100", "250",
        "500", "750", "1000", "10000", "100000", "1000000", "Infinite" },
                                    UNDEFINED);

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ compareTo -------------------------------

  /**
   * Compare this value to another one.
   *
   * @param       inOther the value to compare to
   *
   * @return      -1 for less than, 0 for equal and +1 for greater than the
   *              object given
   *
   */
  public int compareTo(@Nonnull Object inOther)
  {
    if(inOther instanceof BaseNumber)
      return (int)(m_number - ((BaseNumber)inOther).m_number);

    return super.compareTo(inOther);
  }

  //........................................................................

  //--------------------------------- get ----------------------------------

  /**
   * Get the number stored.
   *
   * @return      the number stored
   *
   */
  public long get()
  {
    return m_number;
  }

  //........................................................................
  //-------------------------------- getMin --------------------------------

  /**
   * Get the minimally allowed number.
   *
   * @return      the minimum
   *
   */
  public long getMin()
  {
    return m_min;
  }

  //........................................................................
  //-------------------------------- getMax --------------------------------

  /**
   * Get the maximally allowed number.
   *
   * @return      the maximum
   *
   */
  public long getMax()
  {
    return m_max;
  }

  //........................................................................

  //------------------------------- doFormat -------------------------------

  /**
   * Really to the formatting.
   *
   * @return      the command for setting the value
   *
   */
  protected @Nonnull Command doFormat()
  {
    return new Command(toString());
  }

  //........................................................................
  //------------------------------ doToString ------------------------------

  /**
   * Convert the value to a string, depending on the given kind.
   *
   * @return      a String representation, depending on the kind given
   *
   */
  protected @Nonnull String doToString()
  {
    if(m_sign && m_number >= 0)
      return "+" + m_number;

    return "" + m_number;
  }

  //........................................................................

  //------------------------------ isDefined -------------------------------

  /**
   * Check if the value is defined or not.
   *
   * @return      true if the value is defined, false if not
   *
   */
  public boolean isDefined()
  {
    return m_defined;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  // immutable!

  //-------------------------------- check ---------------------------------

  /**
   * Check that the current value is valid.
   *
   */
  protected void check()
  {
    if(m_number > m_max)
    {
      Log.warning("number " + m_number + " too high, adjusted to " + m_max);

      m_number = m_max;
    }
    else
      if(m_number < m_min)
      {
        Log.warning("number " + m_number + " too low, adjusted to " + m_min);

        m_number = m_min;
      }
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //--------------------------------- add ----------------------------------

  /**
   * Add the given and current values.
   *
   * @param       inValue the value to add to this one
   *
   * @return      the addition of both values
   *
   */
  @SuppressWarnings("unchecked") // have to cast
  public @Nonnull T add(@Nonnull T inValue)
  {
    T result = create();

    result.m_defined = true;
    result.m_number = m_number + inValue.m_number;

    result.check();

    return result;
  }

  //........................................................................
  //------------------------------ subtract --------------------------------

  /**
   * Subtract the current value from the given one.
   *
   * @param       inValue the value to subtract from
   *
   * @return      the subtraction of both values
   *
   */
  public T subtract(@Nonnull T inValue)
  {
    T result = create();

    result.m_number = m_number - inValue.m_number;

    check();

    return result;
  }

  //........................................................................
  //------------------------------- multiply -------------------------------

  /**
   * Multiply the number.
   *
   * @param       inValue the multiplication factor
   *
   * @return      the multiplied value
   *
   */
  @SuppressWarnings("unchecked") // casting
  public T multiply(long inValue)
  {
    if(!m_defined)
      return (T)this;

    T result = create();

    result.m_number = m_number * inValue;
    result.m_defined = true;

    result.check();

    return result;
  }

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
  @SuppressWarnings("unchecked") // casting
  public T divide(long inValue)
  {
    if(!m_defined)
      return (T)this;

    T result = create();

    result.m_number = m_number / inValue;
    result.m_defined = true;

    result.check();

    return result;
  }

  //........................................................................

  //---------------------------------- as ----------------------------------

  /**
   * Set the number value.
   *
   * @param       inValue the value to set to
   *
   * @return      true if set, false if not in range
   *
   */
  @SuppressWarnings("unchecked")
  protected T as(long inValue)
  {
    if(inValue > m_max || inValue < m_min)
      return (T)this;

    T result = create();
    result.m_number = inValue;
    result.m_defined = true;

    return result;
  }

  //........................................................................
  //------------------------------- doRead ---------------------------------

  /**
   * Read the value from the reader and replace the current one.
   *
   * @param       inReader the reader to read from
   *
   * @return      true if read, false if not
   *
   */
  protected boolean doRead(@Nonnull ParseReader inReader)
  {
    ParseReader.Position pos = inReader.getPosition();

    long number;
    try
    {
      number = inReader.readLong();
    }
    catch(net.ixitxachitls.input.ReadException e)
    {
      return false;
    }

    if(number > m_max)
    {
      inReader.logWarning(pos, "value.number.high", "maximal " + m_max);

      return false;
    }

    if(number < m_min)
    {
      inReader.logWarning(pos, "value.number.low", "minimal " + m_min);

      return false;
    }

    m_number  = number;
    m_defined = true;

    return true;
  }

  //........................................................................

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
      BaseNumber<BaseNumber> number = new BaseNumber<BaseNumber>(10, 20);

      // undefined value
      assertFalse("not undefined at start", number.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   number.toString());
      assertEquals("undefined value not correct", 15, number.get());
      assertEquals("group", "$undefined$", number.group().toString());
      assertEquals("format", "\\color{error}{$undefined$}",
                   number.format().toString());

      // now with some number
      number = new BaseNumber<BaseNumber>(10, 0, 20);

      assertEquals("not defined after setting", true, number.isDefined());
      assertEquals("value not correctly gotten", 10, number.get());
      assertEquals("value not correctly converted", "10", number.toString());
      assertEquals("group", "10", number.group().toString());
      assertEquals("format", "10", number.format().toString());

      assertEquals("max", 20, number.getMax());
      assertEquals("min", 0, number.getMin());

      BaseNumber<BaseNumber> number2 = new BaseNumber<BaseNumber>(522, 0, 1000);
      assertEquals("group", "750", number2.group().toString());

      // comparisons
      assertTrue("equal", number.compareTo(number) == 0);
      assertTrue(">", number2.compareTo(number) > 0);
      assertTrue("<", number.compareTo(number2) < 0);

      // signs
      number = new BaseNumber<BaseNumber>(0, 0, 20, true);

      assertEquals("sign", "+0", number.toString());

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

      Value.Test.readTest(tests, new BaseNumber(-50, 50));
    }

    //......................................................................
    //----- set ------------------------------------------------------------

    /** Testing setting. */
    @org.junit.Test
    public void set()
    {
      BaseNumber number = new BaseNumber(10, 20);

      // undefined value
      assertEquals("not undefined at start", false, number.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   number.toString());

      assertEquals("set", "15", number.as(15).toString());
      assertEquals("low", UNDEFINED, number.as(9).toString());
      assertEquals("high", UNDEFINED, number.as(21).toString());
      assertEquals("max", "20", number.as(20).toString());
      assertEquals("min", "10", number.as(10).toString());
    }

    //......................................................................
    //----- compute --------------------------------------------------------

    /** Value computations. */
    @org.junit.Test
    @SuppressWarnings("unchecked")
    public void compute()
    {
      BaseNumber<BaseNumber> number = new BaseNumber<BaseNumber>(2, 20);

      // not initialized
      number = number.multiply(3);
      assertFalse("undefined", number.isDefined());
      assertEquals("start", 11, number.get());

      number = number.divide(3);
      assertFalse("undefined", number.isDefined());
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

      number = number.add(number);
      assertEquals("add", 4, number.get());

      number = number.subtract(new BaseNumber<BaseNumber>(3, 0, 10));
      assertEquals("subtract", 1, number.get());

      number = number.add(new BaseNumber<BaseNumber>(50, 0, 100));
      assertEquals("add", 20, number.get());

      m_logger.addExpected("WARNING: number 30 too high, adjusted to 20");
      m_logger.addExpected("WARNING: number 0 too low, adjusted to 2");
      m_logger.addExpected("WARNING: number 51 too high, adjusted to 20");
    }

    //......................................................................
    //----- modify ---------------------------------------------------------

    /** Test modifying a base number. */
    // TODO: check if we want this test again
//     @org.junit.Test
//     public void modify()
//     {
//       Modifiable<BaseNumber> number =
//         new Modifiable<BaseNumber>
//         (new BaseNumber(42, 0, 100, true));

//       assertEquals("unmodified", "+42", number.toString());
//       assertEquals("unmodified", "+42", number.format(false).toString());

//       // simple modifier
//       number.addModifier(new net.ixitxachitls.dma.values.modifiers.
//                        NumberModifier(net.ixitxachitls.dma.values.modifiers.
//                                        NumberModifier.Operation.ADD, 23,
//                                        net.ixitxachitls.dma.values.modifiers.
//                                        NumberModifier.Type.GENERAL, "test"));

//       assertEquals("modified 1", "+65 [+23 test]", number.toString());
//       assertEquals("modified 1", "\\window{+65}{+23 test}",
//                    number.format(false).toString());

//       // second modifier
//       number.addModifier(new net.ixitxachitls.dma.values.modifiers.
//                         NumberModifier(net.ixitxachitls.dma.values.modifiers.
//                                         NumberModifier.Operation.SUBTRACT,
//                                         10,
//                                        net.ixitxachitls.dma.values.modifiers.
//                                       NumberModifier.Type.GENERAL, "minus"));

//       assertEquals("modified 2", "+55 [+23 test, -10 minus]",
//                    number.toString());
//       assertEquals("modified 2", "\\window{+55}{+23 test, -10 minus}",
//                    number.format(false).toString());

//       // non-stacking modifier
//       number.addModifier(new net.ixitxachitls.dma.values.modifiers.
//                         NumberModifier(net.ixitxachitls.dma.values.modifiers.
//                                         NumberModifier.Operation.ADD,
//                                         5,
//                                        net.ixitxachitls.dma.values.modifiers.
//                                         NumberModifier.Type.ARMOR,
//                                         "non-stack 1"));
//       number.addModifier(new net.ixitxachitls.dma.values.modifiers.
//                         NumberModifier(net.ixitxachitls.dma.values.modifiers.
//                                         NumberModifier.Operation.ADD,
//                                         6,
//                                        net.ixitxachitls.dma.values.modifiers.
//                                         NumberModifier.Type.ARMOR,
//                                         "non-stack 2"));
//       number.addModifier(new net.ixitxachitls.dma.values.modifiers.
//                         NumberModifier(net.ixitxachitls.dma.values.modifiers.
//                                         NumberModifier.Operation.ADD,
//                                         6,
//                                        net.ixitxachitls.dma.values.modifiers.
//                                         NumberModifier.Type.ARMOR,
//                                         "non-stack 3"));

//       assertEquals("stacking",
//                    "+61 [+23 test, +6 armor non-stack 2, -10 minus]",
//                    number.toString());
//       assertEquals("stacking",
//                    "\\window{+61}{+23 test, +6 armor non-stack 2, "
//                    + "-10 minus}",
//                    number.format(false).toString());

//       // conditional modifier
//       number.addModifier
//         (new net.ixitxachitls.dma.values.modifiers.
//          NumberModifier(net.ixitxachitls.dma.values.modifiers.
//                         NumberModifier.Operation.ADD,
//                         -1,
//                         net.ixitxachitls.dma.values.modifiers.
//                         NumberModifier.Type.ARMOR,
//                         "penalty",
//                         new net.ixitxachitls.dma.values.conditions.
//                         Condition("condition")));

//       assertEquals("conditional",
//                    "+60 - +61 [+23 test, +6 armor non-stack 2, "
//                    + "-1 armor penalty if condition, -10 minus"
//                    + "]", number.toString());
//       assertEquals("conditional",
//                    "\\window{+60 - +61}{+23 test, +6 armor non-stack 2, "
//                    + "-1 armor penalty \\bold{if} \\italic{condition}, "
//                    + "-10 minus}",
//                    number.format(false).toString());

//       // second conditional modifier
//       number.addModifier
//         (new net.ixitxachitls.dma.values.modifiers.
//          NumberModifier(net.ixitxachitls.dma.values.modifiers.
//                         NumberModifier.Operation.ADD,
//                         -3,
//                         net.ixitxachitls.dma.values.modifiers.
//                         NumberModifier.Type.ARMOR,
//                         "penalty",
//                         new net.ixitxachitls.dma.values.conditions.
//                         Condition("condition 2")));

//       assertEquals("conditional",
//                    "+58 - +61 [+23 test, +6 armor non-stack 2, "
//                    + "-1 armor penalty if condition, "
//                    + "-3 armor penalty if condition 2, -10 minus]",
//                    number.toString());
//       assertEquals("conditional",
//                    "\\window{+58 - +61}{+23 test, "
//                    + "+6 armor non-stack 2, "
//                    + "-1 armor penalty \\bold{if} \\italic{condition}, "
//                    + "-3 armor penalty \\bold{if} \\italic{condition 2}, "
//                    + "-10 minus}",
//                    number.format(false).toString());

//       // another penalty
//       net.ixitxachitls.dma.values.modifiers.
//         BaseModifier modifier = new net.ixitxachitls.dma.values.modifiers.
//         NumberModifier(net.ixitxachitls.dma.values.modifiers.
//                        NumberModifier.Operation.ADD,
//                        -2,
//                        net.ixitxachitls.dma.values.modifiers.
//                        NumberModifier.Type.ARMOR,
//                        "penalty");
//       number.addModifier(modifier);

//       assertEquals("conditional",
//                    "+58 - +61 [+23 test, +6 armor non-stack 2, "
//                    + "-1 armor penalty if condition, "
//                    + "-3 armor penalty if condition 2, -2 armor penalty, "
//                    + "-10 minus]",
//                    number.toString());
//       assertEquals("conditional",
//                    "\\window{+58 - +61}{+23 test, "
//                    + "+6 armor non-stack 2, "
//                    + "-1 armor penalty \\bold{if} \\italic{condition}, "
//                    + "-3 armor penalty \\bold{if} \\italic{condition 2}, "
//                    + "-2 armor penalty, -10 minus}",
//                    number.format(false).toString());

//       // remove a modifier again
//       number.remove(modifier);

//       assertEquals("conditional",
//                    "+58 - +61 [+23 test, +6 armor non-stack 2, "
//                    + "-1 armor penalty if condition, "
//                    + "-3 armor penalty if condition 2, -10 minus]",
//                    number.toString());
//       assertEquals("conditional",
//                    "\\window{+58 - +61}{+23 test, "
//                    + "+6 armor non-stack 2, "
//                    + "-1 armor penalty \\bold{if} \\italic{condition}, "
//                    + "-3 armor penalty \\bold{if} \\italic{condition 2}, "
//                    + "-10 minus}",
//                    number.format(false).toString());

//       // read a modified value
//       number.doRead(new ParseReader
//                     (new java.io.StringReader("+23 [+42 test]"), "test"));

//       assertEquals("read", "+65 [+42 test]", number.toString());
//       assertEquals("read", 65, number.getLow().get());
//       assertEquals("read", 65, number.getHigh().get());

//       number.addModifier(modifier);

//       assertEquals("read", "+63 [+42 test, -2 armor penalty]",
//                    number.toString());
//       assertEquals("read", 63, number.getLow().get());
//       assertEquals("read", 63, number.getHigh().get());
//     }

    //......................................................................
  }

  //........................................................................
}
