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
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a float and is capable of reading such floats
 * from a reader (and write it to a writer of course).
 *
 * @file          Price.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Price extends Decimal<Price>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Price --------------------------------

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Construct the price object with all the values.
   *
   * @param       inCurrency the currency symbol to use
   * @param       inNumber   the number itself
   * @param       inMin      the minimally allowed number
   * @param       inMax      the maximally allowed number
   *
   */
  public Price(String inCurrency, long inNumber, long inMin, long inMax)
  {
    super(inNumber, inMin, inMax, 100);

    m_currency = inCurrency;
  }

  //........................................................................
  //------------------------------- Price --------------------------------

  /**
   * Construct the price object with all the values.
   *
   * @param       inMin      the minimally allowed number
   * @param       inMax      the maximally allowed number
   *
   */
  public Price(long inMin, long inMax)
  {
    super(inMin, inMax, 100);
  }

  //........................................................................

  {
    m_editType = "price";
  }

  //-------------------------------- create --------------------------------

  /**
   * Create a new price with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar list, but without any contents
   *
   */
  @Override
  public Price create()
  {
    return super.create(new Price(m_min, m_max));
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The sign for the currency. */
  private String m_currency =
    Config.get("resource:values/price.default.currency", "$");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getCurrency -----------------------------

  /**
   * Get the currency used.
   *
   * @return      the currency symbol(s)
   *
   */
  public String getCurrency()
  {
    return m_currency;
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
    return m_currency + super.doToString();
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- doRead ---------------------------------

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
    ParseReader.Position pos = inReader.getPosition();

    long   number   = 0;
    String currency = null;

    try
    {
      currency = inReader.read("1234567890+-");

      // remove superfluous white spaces
      currency = currency.replaceAll("[ \n]+", " ");
      currency = currency.replaceFirst("^[ \n]*", "");

      if(currency.length() == 0)
        return false;

      // we read -0 as 0, resulting in -0.5 read as 0.5 ...
      // thus we need to check for the negative sign
      boolean negative = false;

      if(inReader.expect('-'))
        negative = true;

      number = inReader.readInt() * m_precision;

      if(inReader.expect(s_delimiter))
      {
        int prices = inReader.readInt();

        if(prices >= m_precision)
        {
          inReader.logWarning(pos, "value.price.decimals",
                              "only up to " + m_precision + " allowed");

          return false;
        }

        number += prices;
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

    // store the values
    m_currency = currency;
    m_number   = number;
    m_defined  = true;

    return true;
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

    /** Testing init. */
    @org.junit.Test
    public void init()
    {
      Price value = new Price(10, 200);

      // undefined value
      assertEquals("not undefined at start", false, value.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   value.toString());
      assertEquals("undefined value not correct", 105, value.get());
      assertEquals("undefined value not correct", "$", value.getCurrency());

      // now with some value
      value = new Price("SFr. ", 10023, 0, 20000);

      assertEquals("not defined after setting", true, value.isDefined());
      assertEquals("value not correctly gotten", 10023, value.get());
      assertEquals("value not correctly converted", "SFr. 100.23",
                   value.toString());

      Value.Test.createTest(value);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      String []tests =
        {
          "simple", "$42.00", "$42.00", null,
          "SFr", "SFr. 1.42", "SFr. 1.42", null,
          "white", "  \nSFr.  \n 11.12", "SFr. 11.12", null,
          "digits", "$ 1.234", null, "$ 1.234",
          "too high", "$ +120.00", null, "$ +120.00",
          "too low", "$ -10", null, "$ -10",
          "too low", "$ -0.1", null, "$ -0.1",
          "white currency", "A \n B 10.20", "A B 10.20", null,
          "invalid", "1.2", null, "1.2",
          "empty", "", null, null,
          "no digits", "$ 40", "$ 40.00", null,
        };

      m_logger.addExpectedPattern("WARNING:.*\\(only up to 100 allowed\\) "
                                  + "on line 1 in document 'test'."
                                  + "...>>>\\$ 1.234...");
      m_logger.addExpectedPattern("WARNING:.*\\(maximal 50.00\\) "
                                  + "on line 1 in document 'test'."
                                  + "...>>>\\$ \\+120.00...");
      m_logger.addExpectedPattern("WARNING:.*\\(minimal 0.00\\) "
                                  + "on line 1 in document 'test'."
                                  + "...>>>\\$ \\-10...");
      m_logger.addExpectedPattern("WARNING:.*\\(minimal 0.00\\) "
                                  + "on line 1 in document 'test'."
                                  + "...>>>\\$ \\-0.1...");

      Value.Test.readTest(tests, new Price(0, 5000));
    }

    //......................................................................
  }

  //........................................................................
}
