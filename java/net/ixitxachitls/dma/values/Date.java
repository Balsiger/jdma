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

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import net.ixitxachitls.dma.proto.Entries.BaseProductProto;

/**
 * This class stores a date and is capable of reading such dates from a reader
 * (and write it to a writer of course). This class is only intended for real
 * world dates. If you need to read campaign specific date, you probably must
 * make a derivation of this class.
 *
 * @file          Date.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@Immutable
public class Date extends Value<BaseProductProto.Date>
  implements Comparable<Date>
{
  /** The parser for date values. */
  public static class DateParser extends Parser<Date>
  {
    /** Create the parser. */
    public DateParser()
    {
      super(1);
    }

    @Override
    public Optional<Date> doParse(String inValue)
    {
      List<String> parts = SPACE_SPLITTER.splitToList(inValue);
      if(parts.size() > 2)
        return Optional.absent();

      int month = 0;
      if(parts.size() > 1)
      {
        for(int i = 0; i < MONTH_STRINGS.size(); i++)
          if(MONTH_STRINGS.get(i).equalsIgnoreCase(parts.get(0)))
          {
            month = i + 1;
            break;
          }
        if(month <= 0)
          return Optional.absent();
      }

      try
      {
        int year = Integer.parseInt(parts.get(parts.size() - 1));
        return Optional.of(new Date(month, year));
      }
      catch(NumberFormatException e)
      {
        return Optional.absent();
      }
    }
  }

  /**
   * Construct the date object.
   *
   * @param       inMonth the month of the date
   * @param       inYear  the year of the date
   */
  public Date(int inMonth, int inYear)
  {
    if(inMonth < 0)
      throw new IllegalArgumentException("month must not be negative");

    if(inYear < 0)
      throw new IllegalArgumentException("year must not be negative");

    m_month = inMonth;
    m_year = inYear;
  }

  /** The month of the date, if any (0 is no month). */
  private int m_month = 0;

  /** The year of the date. */
  private int m_year  = 0;

  /** Possible month strings. */
  private static final List<String> MONTH_STRINGS =
    ImmutableList.of("January", "February", "March", "April", "May", "June",
                     "July", "August", "September", "October", "November",
                     "December");

  /** The splitter by spaces. */
  private static final Splitter SPACE_SPLITTER = Splitter.on(' ');

  /** The parser for dates. */
  public static final Parser<Date> PARSER = new DateParser();

  /**
   * Get the month stored.
   *
   * @return      the date stored
   */
  public int getMonth()
  {
    return m_month;
  }

  /**
   * Get the month stored (as a string).
   *
   * @return      the month stored or an empty string if invalid
   */
  public String getMonthAsString()
  {
    if(m_month == 0 || m_month > MONTH_STRINGS.size())
      return "";

    return MONTH_STRINGS.get(m_month - 1);
  }

  /**
   * Get the year stored.
   *
   * @return      the date stored
   */
  public int getYear()
  {
    return m_year;
  }

  @Override
  public String toString()
  {
    if(m_month > 0)
      return getMonthAsString() + " " + m_year;

    return "" + m_year;
  }

  @Override
  public int compareTo(Date inOther)
  {
    Date other = inOther;
    if(m_year < other.m_year)
      return -1;

    if(m_year > other.m_year)
      return +1;

    if(m_month < other.m_month)
      return -1;

    if(m_month > other.m_month)
      return +1;

    return 0;
  }

  @Override
  public BaseProductProto.Date toProto()
  {
    return BaseProductProto.Date.newBuilder()
      .setYear(m_year)
      .setMonth(m_month)
      .build();
  }

  /**
   * Create a date from the given proto.
   *
   * @param inProto the proto to create from
   * @return the created date
   */
  public static Date fromProto(BaseProductProto.Date inProto)
  {
    if(inProto.hasMonth())
      return new Date(inProto.getMonth(), inProto.getYear());

    return new Date(inProto.getYear(), 0);
  }

  //---------------------------------------------------------------------------

  /** The Test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Testing init. */
    @org.junit.Test
    public void init()
    {
      // now with some date
      Date date = new Date(5, 2002);

      assertEquals("value not correctly gotten", "May 2002",
                   date.toString());
      assertEquals("undefined value not correct", 5, date.getMonth());
      assertEquals("undefined value not correct", "May",
                   date.getMonthAsString());
      assertEquals("undefined value not correct", 2002, date.getYear());
    }

    /** Testing converting. */
    @org.junit.Test
    @SuppressWarnings("rawtypes")
    public void convert()
    {
      Date value = new Date(5, 1969);

      assertEquals("string", "May 1969", value.toString());
    }

    /** Testing parsing. */
    @org.junit.Test
    public void parse()
    {
      assertEquals("simple", "May 2002",
                   PARSER.parse("May 2002").get().toString());
      assertEquals("casing", "May 2002",
                   PARSER.parse("mAY 2002").get().toString());
      assertEquals("year", "1999", PARSER.parse("1999").get().toString());
      assertFalse("invalid 1", PARSER.parse("guru").isPresent());
      assertFalse("invalid 2", PARSER.parse("January").isPresent());
      assertFalse("invalid 3", PARSER.parse("20a").isPresent());
    }
  }
}
