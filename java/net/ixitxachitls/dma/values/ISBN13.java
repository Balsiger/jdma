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

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;

import net.ixitxachitls.dma.proto.Entries.BaseProductProto;

/**
 * This class stores a ISBN13 and is capable of reading such ISBN13s
 * from a reader (and write it to a writer of course). It also provides a
 * means to check the validity of an ISBN13 number.
 *
 * @file          ISBN13.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@Immutable
@ParametersAreNonnullByDefault
public class ISBN13 extends Value<BaseProductProto.ISBN13>
{
  public static class ISBNParser extends Parser<ISBN13>
  {
    public ISBNParser()
    {
      super(1);
    }

    @Override
    public Optional<ISBN13> doParse(String inValue)
    {
      try
      {
        return Optional.of(new ISBN13(inValue));
      }
      catch(IllegalArgumentException e)
      {
        return Optional.absent();
      }
    }
  }

  /**
   * Construct the ISBN13 object.
   *
   * @param       inNumber the ISBN13 number
   */
  public ISBN13(String inNumber)
  {
    List<String> parts = DASH_SPLITTER.splitToList(inNumber);

    if(parts.size() != 5)
      throw new IllegalArgumentException("not all five ISBN 13 parts given");

    m_g13 = parts.get(0);
    m_group = parts.get(1);
    m_publisher = parts.get(2);
    m_title = parts.get(3);
    m_check = Integer.parseInt(parts.get(4));

    if(m_check != compute(m_g13, m_group, m_publisher, m_title))
      throw new IllegalArgumentException("check number does not match");
  }

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
    if(inCheck >= 0)
      m_check = inCheck;
    else
      m_check = compute(inG13, inGroup, inPublisher, inTitle);

    m_g13 = inG13;
    m_group = inGroup;
    m_publisher = inPublisher;
    m_title = inTitle;

    if(m_check != compute(inG13, m_group, m_publisher, m_title))
      throw new IllegalArgumentException("check number does not match, "
                                         + "should be "
                                         + compute(inG13, m_group, m_publisher,
                                                   m_title));
  }

  private static Splitter DASH_SPLITTER = Splitter.on('-').trimResults();
  public static final Parser<ISBN13> PARSER = new ISBNParser();

  /** The isbn 13 leading group, if any. */
  private String m_g13 = null;

  /** The group part of the whole number. */
  private String m_group = null;

  /** The publisher part of the whole number. */
  private String m_publisher = null;

  /** The title part of the whole number. */
  private String m_title = null;

  /** The check number. */
  private int m_check = -1;

  /**
   * Convert the value to a string, depending on the given kind.
   *
   * @return      a String representation, depending on the kind given
   */
  @Override
  public String toString()
  {
    return m_g13 + "-" + m_group + '-' + m_publisher + '-' + m_title + '-'
      + (char)(m_check + '0');
  }

  /**
   * Get the additional group of the ISBN13 number.
   *
   * @return      a String with the stored additional group
   */
  public String get13()
  {
    return m_g13;
  }

  /**
   * Get the group of the ISBN13 number.
   *
   * @return      a String with the stored group
   */
  public String getGroup()
  {
    return m_group;
  }

  /**
   * Get the publisher part of the ISBN13 number.
   *
   * @return      a String with the stored publisher
   */
  public String getPublisher()
  {
    return m_publisher;
  }

  /**
   * Get the title of the ISBN13 number.
   *
   * @return      a String with the stored title
   */
  public String getTitle()
  {
    return m_title;
  }

  /**
   * Get the check number of the ISBN13 number.
   *
   * @return      the checksum number, 10 represents X
   */
  public int getCheck()
  {
    return m_check;
  }

  /**
   * Get the ISBN13 number unformatted.
   *
   * @return      a String with the stored title
   */
  public String toUnformatted()
  {
    return m_g13 + m_group + m_publisher + m_title + m_check;
  }

  /**
   * Compute the check number for the given ISBN13 number.
   *
   * @param       inG13       the ISBN13 13 part
   * @param       inGroup     the ISBN13 group part
   * @param       inPublisher the ISBN13 publisher part
   * @param       inTitle     the ISBN13 title part
   *
   * @return      the check number or -1 if invalid values are given
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

  @Override
  public BaseProductProto.ISBN13 toProto()
  {
    return BaseProductProto.ISBN13.newBuilder()
      .setGroup13(m_g13)
      .setGroup(m_group)
      .setPublisher(m_publisher)
      .setTitle(m_title)
      .setCheck(m_check)
      .build();
  }

  public static ISBN13 fromProto(BaseProductProto.ISBN13 inProto)
  {
    return new ISBN13(inProto.getGroup13(), inProto.getGroup(),
                      inProto.getPublisher(), inProto.getTitle(),
                      inProto.getCheck());
  }

  //---------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Testing the inits. */
    @org.junit.Test
    public void init()
    {
      // now with some ISBN13
      ISBN13 isbn13 = new ISBN13("978", "0", "7869", "3912", 1);

      assertEquals("value not correctly gotten", "978-0-7869-3912-1",
                   isbn13.toString());

      assertEquals("check",      1,  isbn13.getCheck());
      assertEquals("isbn 13",    "978", isbn13.get13());
      assertEquals("group",      "0", isbn13.getGroup());
      assertEquals("publischer", "7869", isbn13.getPublisher());
      assertEquals("title",      "3912", isbn13.getTitle());

      // last but not least with a complete number
      isbn13 = new ISBN13("978-0-7869-3912-1");
    }

    /** Testing reading. */
    @org.junit.Test
    public void parse()
    {
      assertEquals("simple", "978-1-60125-019-3",
                   PARSER.parse("978-1-60125-019-3"));
      assertEquals("whites", "978-0-7869-3912-1",
                   PARSER.parse("978- 0  -   7869\n- 3912 - 1 "));
      assertNull("invalid 0", PARSER.parse("978-0-7869-3912-2"));
      assertNull("invalid 2", PARSER.parse("978-0-7869-3912-3"));
      assertNull("invalid 3", PARSER.parse("978-0-7869-3912-4"));
      assertNull("invalid 4", PARSER.parse("978-0-7869-3912-5"));
      assertNull("invalid 5", PARSER.parse("978-0-7869-3912-6"));
      assertNull("invalid 6", PARSER.parse("978-0-7869-3912-7"));
      assertNull("invalid 7", PARSER.parse("978-0-7869-3912-8"));
      assertNull("invalid 8", PARSER.parse("978-0-7869-3912-9"));
      assertNull("invalid 9", PARSER.parse("978-0-7869-3912-0"));
      assertNull("missing", PARSER.parse("123-444-222 h"));
      assertNull("missing 2", PARSER.parse("123-444-22a-3"));
      assertNull("missing 3", PARSER.parse("123-123-444-22a-3"));
      assertNull("missing 4", PARSER.parse("123-123-444-22 a-3"));
    }

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

    /** Testing setting. */
    public void unformatted()
    {
      ISBN13 isbn13 = new ISBN13("978", "0", "7869", "3887", 2);
      assertEquals("simple", "9780786938872", isbn13.toUnformatted());
    }
  }
}
