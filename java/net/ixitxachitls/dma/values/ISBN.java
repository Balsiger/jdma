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
 * This class stores a ISBN and is capable of reading such ISBNs
 * from a reader (and write it to a writer of course). It also provides a
 * means to check the validity of an ISBN number.
 *
 * @file          ISBN.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class ISBN extends Value<ISBN>
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- ISBN ---------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * Construct the ISBN object with an undefined value.
   *
   */
  public ISBN()
  {
    m_editType = "isbn[ISBN]";
  }

  //........................................................................
  //--------------------------------- ISBN ---------------------------------

  /**
   * Construct the ISBN object.
   *
   * @param       inNumber the ISBN number
   *
   */
  public ISBN(String inNumber)
  {
    this();

    String []parts = inNumber.split("-");

    if(parts.length != 4)
      throw new IllegalArgumentException("not all four ISBN parts given");

    m_group     = parts[0];
    m_publisher = parts[1];
    m_title     = parts[2];

    if(parts[3].equalsIgnoreCase("x"))
      m_check = 10;
    else
      m_check = Integer.parseInt(parts[3]);

    if(m_check != compute(m_group, m_publisher, m_title))
      throw new IllegalArgumentException("check number does not match, "
                                         + "should be "
                                         + compute(m_group, m_publisher,
                                                   m_title));
  }

  //........................................................................
  //--------------------------------- ISBN ---------------------------------

  /**
   * Construct the ISBN object.
   *
   * @param       inGroup     the ISBN group part
   * @param       inPublisher the ISBN publisher part
   * @param       inTitle     the ISBN title part
   * @param       inCheck     the check number (or -1 if to calculate
   *                          automatically)
   *
   */
  public ISBN(String inGroup, String inPublisher, String inTitle, int inCheck)
  {
    this();

    if(inCheck >= 0)
      m_check = inCheck;
    else
      m_check = compute(inGroup, inPublisher, inTitle);

    m_group     = inGroup;
    m_publisher = inPublisher;
    m_title     = inTitle;

    if(m_check != compute(m_group, m_publisher, m_title))
      throw new IllegalArgumentException("check number does not match, "
                                         + "should be "
                                         + compute(m_group, m_publisher,
                                                   m_title));
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
  @Override
public ISBN create()
  {
    return new ISBN();
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The group part of the whole number. */
  private @Nullable String m_group = null;

  /** The publisher part of the whole number. */
  private @Nullable String m_publisher = null;

  /** The title part of the whole number. */
  private @Nullable String m_title = null;

  /** The check number. */
  private int m_check = 0;

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
    return m_group != null && m_publisher != null && m_title != null
      && m_check >= 0;
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
    return m_group + '-' + m_publisher + '-' + m_title + '-'
      + (m_check > 9 ? 'X' : (char)(m_check + '0'));
  }

  //........................................................................

  //------------------------------- getGroup -------------------------------

  /**
   * Get the group of the ISBN number.
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
   * Get the publisher part of the ISBN number.
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
   * Get the title of the ISBN number.
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
   * Get the check number of the ISBN number.
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
   * Get the ISBN number unformatted.
   *
   * @return      a String with the stored title
   *
   */
  public String getUnformatted()
  {
    return m_group + m_publisher + m_title
      + (m_check == 10 ? "X" : "" + m_check);
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
      char check = inReader.readChar();

      if(check == 'x' || check == 'X')
        m_check = 10;
      else
        m_check = check - '0';
    }
    catch(net.ixitxachitls.input.ReadException e)
    {
      return false;
    }

    if(m_check != compute(m_group, m_publisher, m_title))
    {
      inReader.logWarning(pos, "entry.ISBN.invalid", "checksum should be "
                          + compute(m_group, m_publisher, m_title));

      return false;
    }

    return true;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- compute --------------------------------

  /**
   * Compute the check number for the given ISBN number.
   *
   * @param       inGroup     the ISBN group part
   * @param       inPublisher the ISBN publisher part
   * @param       inTitle     the ISBN title part
   *
   * @return      the check number, or -1 if the given values are invalid
   */
  public static int compute(String inGroup, String inPublisher, String inTitle)
  {
    if(inGroup.length() + inPublisher.length() + inTitle.length() != 9)
      return -1;

    // isbn 10
    String number = inGroup + inPublisher + inTitle;

    int sum = 0;
    for(int i = 10, j = 0; i > 1; i--, j++)
      sum += (number.charAt(j) - '0') * i;

    return (11 - (sum % 11)) % 11;
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
      ISBN isbn = new ISBN();

      // undefined value
      assertEquals("not undefined at start", false, isbn.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   isbn.toString());

      // now with some ISBN
      isbn = new ISBN("0", "596", "00283", 1);

      assertEquals("not defined after setting", true, isbn.isDefined());
      assertEquals("value not correctly gotten", "0-596-00283-1",
                   isbn.toString());

      // now with some ISBN
      isbn = new ISBN("0-7869-0139-X");

      assertEquals("not defined after setting", true, isbn.isDefined());
      assertEquals("value not correctly gotten", "0-7869-0139-X",
                   isbn.toString());

      assertEquals("check",      10,  isbn.getCheck());
      assertEquals("group",      "0", isbn.getGroup());
      assertEquals("publischer", "7869", isbn.getPublisher());
      assertEquals("title",      "0139", isbn.getTitle());

      Value.Test.createTest(isbn);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      String []tests =
        {
          "simple", "0-596-00283-1", "0-596-00283-1", null,
          "whites", "0-   596\n- 00283 - 1 ", "0-596-00283-1", " ",
          "invalid 0", "0-596-00283-0", null, "0-596-00283-0",
          "invalid 2", "0-596-00283-2", null, "0-596-00283-2",
          "invalid 3", "0-596-00283-3", null, "0-596-00283-3",
          "invalid 4", "0-596-00283-4", null, "0-596-00283-4",
          "invalid 5", "0-596-00283-5", null, "0-596-00283-5",
          "invalid 6", "0-596-00283-6", null, "0-596-00283-6",
          "invalid 7", "0-596-00283-7", null, "0-596-00283-7",
          "invalid 8", "0-596-00283-8", null, "0-596-00283-8",
          "invalid 9", "0-596-00283-9", null, "0-596-00283-9",
          "invalid X", "0-596-00283-x", null, "0-596-00283-x",
          "missing", "123-444-222 h", null, "123-444-222 h",
          "missing 2", "123-444-22a-3", null, "123-444-22a-3",
        };

      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...0-596-00283->>>0...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...0-596-00283->>>2...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...0-596-00283->>>3...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...0-596-00283->>>4...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...0-596-00283->>>5...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...0-596-00283->>>6...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...0-596-00283->>>7...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...0-596-00283->>>8...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...0-596-00283->>>9...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...0-596-00283->>>x...");
      m_logger.addExpectedPattern("WARNING:.* "
                                  + "on line 1 in document 'test'."
                                  + "...123-444-22a->>>3...");

      Value.Test.readTest(tests, new ISBN());
    }

    //......................................................................
    //----- check ----------------------------------------------------------

    /** Test for checks. */
    @org.junit.Test
    public void testCheck()
    {
      assertEquals("check failed", 0,
                   ISBN.compute("1", "56076", "460"));
      assertEquals("check failed", 3,
                   ISBN.compute("0", "7869", "2874"));
      assertEquals("check failed", 1,
                   ISBN.compute("0", "596", "00283"));
      assertEquals("check failed", 6,
                   ISBN.compute("3", "411", "02421"));
    }

    //......................................................................
    //----- unformatted ----------------------------------------------------

    /** Testing setting. */
    @org.junit.Test
    public void unformatted()
    {
      ISBN isbn = new ISBN("0", "7869", "2944", 8);
      assertEquals("simple", "0786929448", isbn.getUnformatted());

      isbn = new ISBN("0", "7869", "0755", 10);
      assertEquals("string", "078690755X", isbn.getUnformatted());
    }

    //......................................................................
  }

  //........................................................................
}
