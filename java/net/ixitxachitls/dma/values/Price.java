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

package net.ixitxachitls.dma.values;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.proto.Values.PriceProto;
import net.ixitxachitls.util.Strings;

/**
 * This class stores a float and is capable of reading such floats
 * from a reader (and write it to a writer of course).
 *
 * @file          Price.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@Immutable
@ParametersAreNonnullByDefault
public class Price extends NewValue<PriceProto>
{
  public static class PriceParser implements Parser<Price>
  {
    @Override
    public @Nullable Price parse(String ... inValues)
    {
      if(inValues.length != 1)
        return null;

      return Price.parse(inValues[0]);
    }
  }

  /**
   * Construct the price object with all the values.
   *
   * @param       inCurrency the currency symbol to use
   * @param       inNumber   the number itself
   */
  public Price(String inCurrency, int inNumber, int inPrecision)
  {
    m_number = inNumber;
    m_currency = inCurrency;
    m_precision = inPrecision;
  }

  /** The sign for the currency. */
  private String m_currency = "$";

  /** The number of the price. */
  private int m_number = 0;

  /** The precision of the number. */
  private int m_precision = 100;

  /** THe parser for prices. */
  public static final PriceParser PARSER = new PriceParser();

  //------------------------------ getCurrency -----------------------------

  /**
   * Get the currency used.
   *
   * @return      the currency symbol(s)
   */
  public String getCurrency()
  {
    return m_currency;
  }

  /**
   * Get the stored price.
   *
   * @return the price
   */
  public double getPrice()
  {
    return 1.0 * m_number / m_precision;
  }

  @Override
  public String toString()
  {
    String precision = Integer.toString(m_precision);
    String decimals  = Strings.pad(m_number % m_precision,
                                   precision.length() - 1, true);

    return m_currency + m_number / m_precision + "." + decimals;
  }

  @Override
  public PriceProto toProto()
  {
    return PriceProto.newBuilder()
      .setCurrency(m_currency)
      .setNumber(m_number)
      .setPrecision(m_precision)
      .build();
  }

  public static Price fromProto(PriceProto inProto)
  {
    return new Price(inProto.getCurrency(), inProto.getNumber(),
                     inProto.getPrecision());
  }

  /**
   * Parse the price from the given string.
   *
   * @param       the text to parse
   *
   * @return      the price parsed, if any
   *
   */
  public static @Nullable Price parse(String inText)
  {
    String []parts = Strings.getPatterns(inText, "(.*)\\s*(\\d+)(?:\\.(\\d+))");
    if(parts.length != 3)
      return null;

    String currency = parts[0];
    int precision = (int)Math.pow(10, parts[2].length());
    int number =
      Integer.parseInt(parts[1]) * precision + Integer.parseInt(parts[2]);

    return new Price(currency, number, precision);
  }

  //---------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Testing init. */
    @org.junit.Test
    public void init()
    {
      // now with some value
      Price value = new Price("SFr. ", 10023, 2);

      assertEquals("value not correctly gotten", 100.23, value.getPrice(), 0.1);
      assertEquals("value not correctly converted", "SFr. 100.23",
                   value.toString());
    }

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      assertEquals("simple", "$42.00", Price.parse("$42.00").toString());
      assertEquals("SFr", "SFr. 1.42", Price.parse("SFr. 1.42").toString());
      assertEquals("white", "  \nSFr.  \n 11.12",
                   Price.parse("SFr. 11.12").toString());
      assertEquals("digits", "$ 1.234", Price.parse("$ 1.234").toString());
      assertNull("white currency", Price.parse("A \n B 10.20"));
      assertNull("invalid", Price.parse("1.2"));
      assertNull("empty", Price.parse(""));
      assertEquals("no digits", "$ 40", Price.parse("$ 40.00"));
    }

    //......................................................................
  }

  //........................................................................
}
