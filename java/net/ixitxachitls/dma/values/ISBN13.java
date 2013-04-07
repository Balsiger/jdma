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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.input.ParseReader;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a ISBN13 and is capable of reading such ISBN13s
 * from a reader (and write it to a writer of course). It also provides a
 * means to check the validity of an ISBN13 number.
 *
 * @file          ISBN13.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class ISBN13 extends Value<ISBN13>
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- ISBN13 ---------------------------------

  /**
   * Construct the ISBN13 object with an undefined value.
   *
   */
  public ISBN13()
  {
    m_editType = "isbn13[ISBN 13]";
  }

  //........................................................................
  //-------------------------------- ISBN13 ---------------------------------

  /**
   * Construct the ISBN13 object.
   *
   * @param       inNumber the ISBN13 number
   *
   */
  public ISBN13(String inNumber)
  {
    this();

    String []parts = inNumber.split("-");

    if(parts.length != 5)
      throw new IllegalArgumentException("not all five ISBN 13 parts given");

    m_g13       = parts[0];
    m_group     = parts[1];
    m_publisher = parts[2];
    m_title     = parts[3];
    m_check     = Integer.parseInt(parts[4]);

    assert m_check == compute(m_g13, m_group, m_publisher, m_title)
      : "check number does not match";
  }

  //........................................................................
  //-------------------------------- ISBN13 ---------------------------------

  /**
   * Construct the ISBN13 object.
   *
   * @param       inG13       the new group for isbn 13
   * @param       inGroup     the ISBN13 group part
   * @param       inPublisher the ISBN13 publisher part
   * @param       inTitle     the ISBN13 title part
   * @param       inCheck     the check number (or -1 if to calculate
   *                          automatically)
   *
   */
  public ISBN13(String inG13, String inGroup, String inPublisher,
                String inTitle, int inCheck)
  {
    this();

    if(inCheck >= 0)
      m_check = inCheck;
    else
      m_check = compute(inG13, inGroup, inPublisher, inTitle);

    m_g13       = inG13;
    m_group     = inGroup;
    m_publisher = inPublisher;
    m_title     = inTitle;

    if(m_check != compute(inG13, m_group, m_publisher, m_title))
      throw new IllegalArgumentException("check number does not match, "
                                         + "should be "
                                         + compute(inG13, m_group, m_publisher,
                                                   m_title));
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
public ISBN13 create()
  {
    return new ISBN13();
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The isbn 13 leading group, if any. */
  private @Nullable String m_g13 = null;

  /** The group part of the whole number. */
  private @Nullable String m_group = null;

  /** The publisher part of the whole number. */
  private @Nullable String m_publisher = null;

  /** The title part of the whole number. */
  private @Nullable String m_title = null;

  /** The check number. */
  private int m_check = -1;

  //........................................................................

  //-------------------------------------------------------------- accessors

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
    return m_g13 != null && m_group != null && m_publisher != null
      && m_title != null && m_check >= 0;
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

  //------------------------------- doToString ------------------------------

  /**
   * Convert the value to a string, depending on the given kind.
   *
   * @return      a String representation, depending on the kind given
   *
   */
  @Override
  protected String doToString()
  {
    return m_g13 + "-" + m_group + '-' + m_publisher + '-' + m_title + '-'
      + (char)(m_check + '0');
  }

  //........................................................................

  //-------------------------------- get13 ---------------------------------

  /**
   * Get the additional group of the ISBN13 number.
   *
   * @return      a String with the stored additional group
   *
   */
  public @Nullable String get13()
  {
    return m_g13;
  }

  //........................................................................
  //------------------------------- getGroup -------------------------------

  /**
   * Get the group of the ISBN13 number.
   *
   * @return      a String with the stored group
   *
   */
  public @Nullable String getGroup()
  {
    return m_group;
  }

  //........................................................................
  //----------------------------- getPublisher -----------------------------

  /**
   * Get the publisher part of the ISBN13 number.
   *
   * @return      a String with the stored publisher
   *
   */
  public @Nullable String getPublisher()
  {
    return m_publisher;
  }

  //........................................................................
  //------------------------------- getTitle -------------------------------

  /**
   * Get the title of the ISBN13 number.
   *
   * @return      a String with the stored title
   *
   */
  public @Nullable String getTitle()
  {
    return m_title;
  }

  //........................................................................
  //------------------------------- getCheck -------------------------------

  /**
   * Get the check number of the ISBN13 number.
   *
   * @return      the checksum number, 10 represents X
   *
   */
  public int getCheck()
  {
    return m_check;
  }

  //........................................................................
  //---------------------------- getUnformatted ----------------------------

  /**
   * Get the ISBN13 number unformatted.
   *
   * @return      a String with the stored title
   *
   */
  public String getUnformatted()
  {
    return m_g13 + m_group + m_publisher + m_title + m_check;
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
  @Override
  public boolean doRead(ParseReader inReader)
  {
    ParseReader.Position pos;

    try
    {
      m_g13 = inReader.readWord();
      if(!inReader.expect('-'))
        return false;

      m_group = inReader.readWord();
      if(!inReader.expect('-'))
        return false;

      m_publisher = inReader.readWord();
      if(!inReader.expect('-'))
        return false;

      m_title = inReader.readWord();
      if(!inReader.expect('-'))
        return false;

      pos = inReader.getPosition();
      m_check = inReader.readChar() - '0';
    }
    catch(net.ixitxachitls.input.ReadException e)
    {
      return false;
    }

    if(m_check != compute(m_g13, m_group, m_publisher, m_title))
    {
      inReader.logWarning(pos, "entry.ISBN13.invalid", "checksum should be "
                          + compute(m_g13, m_group, m_publisher, m_title));

      return false;
    }

    return true;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- compute --------------------------------

  /**
   * Compute the check number for the given ISBN13 number.
   *
   * @param       inG13       the ISBN13 13 part
   * @param       inGroup     the ISBN13 group part
   * @param       inPublisher the ISBN13 publisher part
   * @param       inTitle     the ISBN13 title part
   *
   * @return      the check number or -1 if invalid values are given
   *
   */
  public static int compute(String inG13, String inGroup, String inPublisher,
                            String inTitle)
  {
    if(inG13.length() + inGroup.length() + inPublisher.length()
       + inTitle.length() != 12)
      return -1;

    // isbn 13
    String number = inG13 + inGroup + inPublisher + inTitle;

    int sum = 0;
    for(int i = 0; i < 12; i++)
      sum += (number.charAt(i) - '0') * (i % 2 == 0 ? 1 : 3);

    return (10 - (sum % 10)) % 10;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** Testing the inits. */
    @org.junit.Test
    public void init()
    {
      ISBN13 isbn13 = new ISBN13();

      // undefined value
      assertEquals("not undefined at start", false, isbn13.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   isbn13.toString());

      // now with some ISBN13
      isbn13 = new ISBN13("978", "0", "7869", "3912", 1);

      assertEquals("not defined after setting", true, isbn13.isDefined());
      assertEquals("value not correctly gotten", "978-0-7869-3912-1",
                   isbn13.toString());

      assertEquals("check",      1,  isbn13.getCheck());
      assertEquals("isbn 13",    "978", isbn13.get13());
      assertEquals("group",      "0", isbn13.getGroup());
      assertEquals("publischer", "7869", isbn13.getPublisher());
      assertEquals("title",      "3912", isbn13.getTitle());

      assertEquals("not defined after setting", true, isbn13.isDefined());
      assertEquals("value not correctly gotten", "978-0-7869-3912-1",
                   isbn13.toString());

      // last but not least with a complete number
      isbn13 = new ISBN13("978-0-7869-3912-1");

      Value.Test.createTest(isbn13);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      String []tests =
        {          "simple", "978-1-60125-019-3", "978-1-60125-019-3", null,
          "whites", "978- 0  -   7869\n- 3912 - 1 ", "978-0-7869-3912-1", " ",
          "invalid 0", "978-0-7869-3912-2", null, "978-0-7869-3912-2",
          "invalid 2", "978-0-7869-3912-3", null, "978-0-7869-3912-3",
          "invalid 3", "978-0-7869-3912-4", null, "978-0-7869-3912-4",
          "invalid 4", "978-0-7869-3912-5", null, "978-0-7869-3912-5",
          "invalid 5", "978-0-7869-3912-6", null, "978-0-7869-3912-6",
          "invalid 6", "978-0-7869-3912-7", null, "978-0-7869-3912-7",
          "invalid 7", "978-0-7869-3912-8", null, "978-0-7869-3912-8",
          "invalid 8", "978-0-7869-3912-9", null, "978-0-7869-3912-9",
          "invalid 9", "978-0-7869-3912-0", null, "978-0-7869-3912-0",
          "missing", "123-444-222 h", null, "123-444-222 h",
          "missing 2", "123-444-22a-3", null, "123-444-22a-3",
          "missing 3", "123-123-444-22a-3", null, "123-123-444-22a-3",
          "missing 4", "123-123-444-22 a-3", null, "123-123-444-22 a-3",
        };

      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...978-0-7869-3912->>>2...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...978-0-7869-3912->>>3...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...978-0-7869-3912->>>4...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...978-0-7869-3912->>>5...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...978-0-7869-3912->>>6...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...978-0-7869-3912->>>7...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...978-0-7869-3912->>>8...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...978-0-7869-3912->>>9...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...978-0-7869-3912->>>0...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...123-123-444-22a->>>3...");

      Value.Test.readTest(tests, new ISBN13());
    }

    //......................................................................
    //----- check ----------------------------------------------------------

    /** Test for checks. */
    @org.junit.Test
    public void check()
    {
      assertEquals("check failed", 1,
                   ISBN13.compute("978", "0", "7869", "3912"));
      assertEquals("check failed", 8,
                   ISBN13.compute("978", "0", "7869", "4156"));
      assertEquals("check failed", 1,
                   ISBN13.compute("978", "0", "7869", "4155"));
      assertEquals("check failed", 7,
                   ISBN13.compute("978", "0", "7869", "4348"));
      assertEquals("check failed", 3,
                   ISBN13.compute("978", "1", "60125", "019"));
    }

    //......................................................................
    //----- unformatted ----------------------------------------------------

    /** Testing setting. */
    public void unformatted()
    {
      ISBN13 isbn13 = new ISBN13("978", "0", "7869", "3887", 2);
      assertEquals("simple", "9780786938872", isbn13.getUnformatted());
    }

    //......................................................................
  }

  //........................................................................
}
