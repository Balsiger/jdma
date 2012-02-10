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
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a date and is capable of reading such dates from a reader
 * (and write it to a writer of course). This class is only intended for real
 * world dates. If you need to read campaign specific date, you probably must
 * make a derivation of this class.
 *
 * @file          Date.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Date extends Value<Date>
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Date ---------------------------------

  /**
   * Construct the date object with an undefined value.
   *
   */
  public Date()
  {
    this(0, 0);
  }

  //........................................................................
  //--------------------------------- Date ---------------------------------

  /**
   * Construct the date object.
   *
   * @param       inMonth the month of the date
   * @param       inYear  the year of the date
   *
   */
  public Date(int inMonth, int inYear)
  {
    if(inMonth < 0)
      throw new IllegalArgumentException("month must not be negative");

    if(inYear < 0)
      throw new IllegalArgumentException("year must not be negative");

    m_month    = inMonth;
    m_year     = inYear;
    m_editType = "date";
  }

  //........................................................................

  //------------------------------ createNew -------------------------------

  /**
   * Create a new list with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar list, but without any contents
   *
   */
  public @Nonnull Date create()
  {
    return super.create(new Date());
  }

  //.......................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The month of the date, if any (0 is no month). */
  private int m_month = 0;

  /** The year of the date. */
  private int m_year  = 0;

  /** Possible month strings. */
  private static final @Nonnull String []MONTH_STRINGS =
    Config.get("resource:default/month.names", new String []
      {
        "January", "February", "March", "April", "May", "June", "July",
        "August", "September", "October", "November", "December"
      });

  /** Invalid month. */
  private static final @Nonnull String s_invalid =
    Config.get("resource:default/month.invalid", "<invalid>");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getMonth -------------------------------

  /**
   * Get the month stored.
   *
   * @return      the date stored
   *
   */
  public int getMonth()
  {
    return m_month;
  }

  //........................................................................
  //--------------------------- getMonthAsString ---------------------------

  /**
   * Get the month stored (as a string).
   *
   * @return      the date stored
   *
   */
  public @Nonnull String getMonthAsString()
  {
    return convertToMonthString(m_month);
  }

  //........................................................................
  //------------------------------- getYear --------------------------------

  /**
   * Get the year stored.
   *
   * @return      the date stored
   *
   */
  public int getYear()
  {
    return m_year;
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
    return m_year > 0;
  }

  //........................................................................
  //----------------------------- isArithmetic -----------------------------

  /**
   * Checks whether the value is arithmetic and thus can be computed with.
   *
   * @return      true if the value is arithemtic
   *
   */
  @Override
  public boolean isArithmetic()
  {
    return false;
  }

  //........................................................................

  //------------------------------- doFormat -------------------------------

  /**
   * Really to the formatting.
   *
   * @return      the command for setting the value
   *
   */
  protected Command doFormat()
  {
    return new Command(toString());
  }

  //........................................................................
  //----------------------------- convertValue -----------------------------

  /**
   * Convert the value to a string, depending on the given kind.
   *
   * @return      a String representation, depending on the kind given
   *
   */
  protected String doToString()
  {
    if(m_month > 0)
      return getMonthAsString() + ' ' + m_year;
    else
      return "" + m_year;
  }

  //........................................................................

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
    if(!(inOther instanceof Date))
      return super.compareTo(inOther);

    Date other = (Date)inOther;
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

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  // immutable!

  //------------------------------- doRead ---------------------------------

  /**
    * Read the value from the reader and replace the current one.
    *
    * @param       inReader the reader to read from
    *
    * @return      true if read, false if not
    *
    */
  public boolean doRead(@Nonnull ParseReader inReader)
  {
    m_month = inReader.expectCase(MONTH_STRINGS, true) + 1;

    try
    {
      m_year  = inReader.readInt();
    }
    catch(net.ixitxachitls.input.ReadException e)
    {
      return false;
    }

    return true;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------- convertToMonthString -------------------------

  /**
   * Convert the given month number into a String.
   *
   * @param       inMonth the number to convert
   *
   * @return      the String representation of the month
   *
   */
  public static @Nonnull String convertToMonthString(int inMonth)
  {
    if(inMonth <= 0)
      return "";

    if(inMonth - 1 >= MONTH_STRINGS.length)
      return s_invalid;

    return MONTH_STRINGS[inMonth - 1];
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The Test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** Testing init. */
    @org.junit.Test
    public void init()
    {
      Date date = new Date();

      // undefined value
      assertEquals("not undefined at start", false, date.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   date.toString());
      assertEquals("undefined value not correct", 0, date.getMonth());
      assertEquals("undefined value not correct", "", date.getMonthAsString());
      assertEquals("undefined value not correct", 0, date.getYear());

      // now with some date
      date = new Date(5, 2002);

      assertEquals("not defined after setting", true, date.isDefined());
      assertEquals("value not correctly gotten", "May 2002",
                   date.toString());
      assertEquals("undefined value not correct", 5, date.getMonth());
      assertEquals("undefined value not correct", "May",
                   date.getMonthAsString());
      assertEquals("undefined value not correct", 2002, date.getYear());

      Value.Test.createTest(date);
    }

    //......................................................................
    //----- convert --------------------------------------------------------

    /** Testing converting. */
    @org.junit.Test
    public void convert()
    {
      Value value = new Date(5, 1969);

      assertEquals("string", "May 1969", value.toString());
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      String []tests =
        {
          "simple", "May 2002", "May 2002", null,
          "casing", "mAY 2002", "May 2002",  null,
          "year", "1999", "1999", null,
          "invalid", "guru", null, "guru",
          "invalid", "jully 2004", null, "jully 2004",
        };

      Value.Test.readTest(tests, new Date());
    }

    //......................................................................
    //----- string set -----------------------------------------------------

    /** Testing set with String. */
//     @org.junit.Test
//     public void stringSet()
//     {
//       Date date = new Date();

//       assertNull("set", date.setFromString("january 2000"));
//       assertEquals("set", "January 2000", date.toString());

//    assertEquals("set", "januaryy 2000", date.setFromString("januaryy 2000"));
//       assertEquals("set", Value.UNDEFINED, date.toString());

//       assertEquals("set", " guru", date.setFromString("may 2005 guru"));
//       assertEquals("set", "May 2005", date.toString());

//       assertEquals("set", "guru", date.setFromString("guru"));
//       assertEquals("set", Value.UNDEFINED, date.toString());

//       assertNull("set", date.setFromString(null));
//       assertEquals("set", Value.UNDEFINED, date.toString());
//     }

    //......................................................................
  }

  //........................................................................
}
