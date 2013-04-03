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

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a float and is capable of reading such floats
 * from a reader (and write it to a writer of course).
 *
 * @file          Decimal.java
 *
 * @author        balsiger@ixixachitls.net (Peter 'Merlin' Balsiger)
 *
 * @param         <T> the actual type of the object to support clone and the
 *                    like
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Decimal<T extends Decimal<T>> extends BaseNumber<T>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Decimal --------------------------------

  /**
   * Construct the decimal object, undefined.
   *
   * @param       inMin       the minimally allowed number (again to divide)
   * @param       inMax       the maximally allowed number (to divide as well)
   * @param       inPrecision the number to divide the values with (100 for
   *                          two digits after the comma)
   *
   */
  public Decimal(long inMin, long inMax, int inPrecision)
  {
    super(inMin, inMax);

    m_precision = inPrecision;
  }

  //........................................................................
  //------------------------------- Decimal --------------------------------

  /**
   * Construct the float object.
   *
   * @param       inNumber    the current number of the decimal (to get the
   *                          correct number, divide by precision)
   * @param       inMin       the minimally allowed number (again to divide)
   * @param       inMax       the maximally allowed number (to divide as well)
   * @param       inPrecision the number to divide the values with (100 for
   *                          two digits after the comma)
   *
   */
  public Decimal(long inNumber, long inMin, long inMax, int inPrecision)
  {
    super(inNumber, inMin, inMax);

    m_precision = inPrecision;
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
  @SuppressWarnings("unchecked") // this only works if derivations override this
  public T create()
  {
    return (T)new Decimal(m_min, m_max, m_precision);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The precision used, the factor used to convert the number to the final
   *  value. */
  protected int m_precision = 100;

  /** Nulls for padding. */
  protected static final String s_nulls = "00000000000000000";

  /** The decimal point. */
  protected static final char s_delimiter =
    Config.get("resource:values/decimal.delimiter", '.');

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- getPrecision -----------------------------

  /**
   * Get the precision to be used.
   *
   * @return      the precision used for the values
   *
   */
  public int getPrecision()
  {
    return m_precision;
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
    return format(m_number, m_precision);
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
  public boolean doRead(ParseReader inReader)
  {
    ParseReader.Position pos = inReader.getPosition();

    long number;
    try
    {
      // we read -0 as 0, resulting in -0.5 read as 0.5
      // thus we need to check for the negative sign
      boolean negative = false;

      if(inReader.expect('-'))
        negative = true;

      number = inReader.readInt() * m_precision;

      if(inReader.expect(s_delimiter))
      {
        int decimals = inReader.readInt();

        if(decimals >= m_precision)
        {
          inReader.logWarning(pos, "value.float.decimals",
                              "only up to " + m_precision + " allowed");

          return false;
        }

        number += decimals;
      }

      if(negative)
        number *= -1;
    }
    catch(net.ixitxachitls.input.ReadException e)
    {
      return false;
    }

    if(number > m_max)
    {
      inReader.logWarning(pos, "value.float.high", "maximal "
                          + format(m_max, m_precision));

      return false;
    }

    if(number < m_min)
    {
      inReader.logWarning(pos, "value.float.low", "minimal "
                          + format(m_min, m_precision));

      return false;
    }

    m_number  = number;
    m_defined = true;

    return true;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //-------------------------------- format --------------------------------

  /**
   * Format the value as a String.
   *
   * @param       inValue     the value to format
   * @param       inPrecision the number of decimals
   *
   * @return      a string with the formatted value
   *
   */
  protected static String format(long inValue, int inPrecision)
  {
    String precision = Integer.toString(inPrecision);
    String decimals  = Strings.pad(inValue % inPrecision,
                                   precision.length() - 1, true);

    return "" + inValue / inPrecision + s_delimiter + decimals;
  }

  //........................................................................
  //-------------------------------- check ---------------------------------

  /**
   * Check that the current value is valid.
   *
   */
  @Override
  protected void check()
  {
    if(m_number > m_max)
    {
      Log.warning("float too " + format(m_number, m_precision)
                  + " too high, adjusted to " + format(m_max, m_precision));

      m_number = m_max;
    }
    else
      if(m_number < m_min)
      {
        Log.warning("float too " + format(m_number, m_precision)
                    + " too low, adjusted to " + format(m_max, m_precision));

        m_number = m_min;
      }
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** Testing inits. */
    @org.junit.Test
    public void init()
    {
      Decimal value = new Decimal(10, 200, 100);

      // undefined value
      assertEquals("not undefined at start", false, value.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   value.toString());
      assertEquals("undefined value not correct", 105, value.get());
      assertEquals("undefined value not correct", 100, value.getPrecision());

      // now with some value
      value = new Decimal(10023, 0, 20000, 10000);

      assertEquals("not defined after setting", true, value.isDefined());
      assertEquals("value not correctly gotten", 10023, value.get());
      assertEquals("value not correctly converted", "1.0023",
                   value.toString());

      Value.Test.createTest(value);
    }

    //......................................................................
    //----- convert --------------------------------------------------------

    /** Testing conversion. */
    @org.junit.Test
    public void convert()
    {
      Value value = new Decimal(4200, 0, 5000, 100);

      assertEquals("string", "42.00", value.toString());

      value = new Decimal(4242, 0, 5000, 100);

      assertEquals("string", "42.42", value.toString());
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      String []tests =
        {
          "simple", "1.23", "1.23", null,
          "integer", "42", "42.00", null,
          "too many decimals", "2.199", null, "2.199",
          "too high", "50.01", null, "50.01",
          "not decimal", "5.a", null, "5.a",
          "following characters", "42 panic", "42.00", "panic",
          "too low", "-0.01", null, "-0.01",
          "way too low", "-50", null, "-50",
        };

      Value.Test.readTest(tests, new Decimal(0, 5000, 100));

      m_logger.addExpectedPattern("WARNING:.*\\(only up to 100 allowed\\) "
                                  + "on line 1 in document 'test'."
                                  + "...>>>2.199...");
      m_logger.addExpectedPattern("WARNING:.*\\(maximal 50.00\\) "
                                  + "on line 1 in document 'test'."
                                  + "...>>>50.01...");
      m_logger.addExpectedPattern("WARNING:.*\\(minimal 0.00\\) "
                                  + "on line 1 in document 'test'."
                                  + "...>>>-0.01...");
      m_logger.addExpectedPattern("WARNING:.*\\(minimal 0.00\\) "
                                  + "on line 1 in document 'test'."
                                  + "...>>>-50...");
    }

    //......................................................................
  }

  //........................................................................
}
