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

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Optional;

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
public class Price extends Value<PriceProto>
{
  /** The parser for prices. */
  public static class PriceParser extends Parser<Price>
  {
    /** Construct the parser. */
    public PriceParser()
    {
      super(1);
    }

    @Override
    public Optional<Price> doParse(String inValue)
    {
      return Price.parse(inValue);
    }
  }

  /**
   * Construct the price object with all the values.
   *
   * @param       inCurrency  the currency symbol to use
   * @param       inNumber    the number itself
   * @param       inPrecision the precision for the price (100 for 2 digits)
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

  /**
   * Convert the given proto into a price.
   *
   * @param  inProto the proto to convert
   * @return the converted price
   */
  public static Price fromProto(PriceProto inProto)
  {
    return new Price(inProto.getCurrency(), inProto.getNumber(),
                     inProto.getPrecision());
  }

  /**
   * Parse the price from the given string.
   *
   * @param       inText the text to parse
   *
   * @return      the price parsed, if any
   */
  public static Optional<Price> parse(String inText)
  {
    String []parts = Strings.getPatterns(
        inText, "^\\s*(\\D.*?\\s?)\\s*(\\d+)(?:\\.(\\d+))?$");
    if(parts.length != 3)
      return Optional.absent();

    String currency = parts[0];
    int precision;
    int number;
    if(parts[2] != null)
    {
      precision = (int) Math.pow(10, parts[2].length());
      number =
          Integer.parseInt(parts[1]) * precision + Integer.parseInt(parts[2]);
    }
    else
    {
      precision = 100;
      number = Integer.parseInt(parts[1]) * 100;
    }

    return Optional.of(new Price(currency, number, precision));
  }

  //----------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Testing init. */
    @org.junit.Test
    public void init()
    {
      // now with some value
      Price value = new Price("SFr. ", 10023, 100);

      assertEquals("value not correctly gotten", 100.23, value.getPrice(), 0.1);
      assertEquals("value not correctly converted", "SFr. 100.23",
                   value.toString());
    }

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      assertEquals("simple", "$42.00", Price.parse("$42.00").get().toString());
      assertEquals("SFr", "SFr. 1.42",
                   Price.parse("SFr. 1.42").get().toString());
      assertEquals("white", "SFr. 11.12",
                   Price.parse("  \nSFr.   \n 11.12").get().toString());
      assertEquals("digits", "$ 1.234",
                   Price.parse("$ 1.234").get().toString());
      assertFalse("invalid", Price.parse("1.2").isPresent());
      assertFalse("empty", Price.parse("").isPresent());
      assertEquals("no digits", "$ 40.00",
                   Price.parse("$ 40").get().toString());
    }
  }
}
