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
 * This class stores a ISBN and is capable of reading such ISBNs
 * from a reader (and write it to a writer of course). It also provides a
 * means to check the validity of an ISBN number.
 *
 * @file          ISBN.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@Immutable
@ParametersAreNonnullByDefault
public class ISBN extends Value<BaseProductProto.ISBN>
{
  public static class ISBNParser extends Parser<ISBN>
  {
    public ISBNParser()
    {
      super(1);
    }

    @Override
    public Optional<ISBN> doParse(String inValue)
    {
      try
      {
        return Optional.of(new ISBN(inValue));
      }
      catch(IllegalArgumentException e)
      {
        return Optional.absent();
      }
    }
  }

  /**
   * Construct the ISBN object.
   *
   * @param       inNumber the ISBN number
   */
  public ISBN(String inNumber)
  {
    List<String> parts = DASH_SPLITTER.splitToList(inNumber);

    if(parts.size() != 4)
      throw new IllegalArgumentException("not all four ISBN parts given");

    m_group     = parts.get(0);
    m_publisher = parts.get(1);
    m_title     = parts.get(2);

    if(parts.get(3).equalsIgnoreCase("x"))
      m_check = 10;
    else
      m_check = Integer.parseInt(parts.get(3));

    if(m_check != compute(m_group, m_publisher, m_title))
      throw new IllegalArgumentException("check number does not match, "
                                         + "should be "
                                         + compute(m_group, m_publisher,
                                                   m_title));
  }

  /**
   * Construct the ISBN object.
   *
   * @param       inGroup     the ISBN group part
   * @param       inPublisher the ISBN publisher part
   * @param       inTitle     the ISBN title part
   * @param       inCheck     the check number (or -1 if to calculate
   *                          automatically)
   */
  public ISBN(String inGroup, String inPublisher, String inTitle, int inCheck)
  {
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

  private static final Splitter DASH_SPLITTER = Splitter.on('-').trimResults();
  public static final Parser<ISBN> PARSER = new ISBNParser();

  /** The group part of the whole number. */
  private String m_group = null;

  /** The publisher part of the whole number. */
  private String m_publisher = null;

  /** The title part of the whole number. */
  private String m_title = null;

  /** The check number. */
  private int m_check = 0;

  @Override
  public String toString()
  {
    return m_group + '-' + m_publisher + '-' + m_title + '-'
      + (m_check > 9 ? 'X' : (char)(m_check + '0'));
  }

  /**
   * Get the group of the ISBN number.
   *
   * @return      a String with the stored group
   */
  public String getGroup()
  {
    return m_group;
  }

  /**
   * Get the publisher part of the ISBN number.
   *
   * @return      a String with the stored publisher
   */
  public String getPublisher()
  {
    return m_publisher;
  }

  /**
   * Get the title of the ISBN number.
   *
   * @return      a String with the stored title
   */
  public String getTitle()
  {
    return m_title;
  }

  /**
   * Get the check number of the ISBN number.
   *
   * @return      the checksum number, 10 represents X
   */
  public int getCheck()
  {
    return m_check;
  }

  /**
   * Get the ISBN number unformatted.
   *
   * @return      a String with the stored title
   */
  public String toUnformatted()
  {
    return m_group + m_publisher + m_title
      + (m_check == 10 ? "X" : "" + m_check);
  }

  @Override
  public BaseProductProto.ISBN toProto()
  {
    return BaseProductProto.ISBN.newBuilder()
      .setGroup(m_group)
      .setPublisher(m_publisher)
      .setTitle(m_title)
      .setCheck(m_check)
      .build();
  }

  public static ISBN fromProto(BaseProductProto.ISBN inProto)
  {
    return new ISBN(inProto.getGroup(), inProto.getPublisher(),
                    inProto.getTitle(), inProto.getCheck());
  }

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

  //---------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Testing the inits. */
    @org.junit.Test
    public void init()
    {
      // now with some ISBN
      ISBN isbn = new ISBN("0", "596", "00283", 1);

      assertEquals("value not correctly gotten", "0-596-00283-1",
                   isbn.toString());

      // now with some ISBN
      isbn = new ISBN("0-7869-0139-X");

      assertEquals("value not correctly gotten", "0-7869-0139-X",
                   isbn.toString());

      assertEquals("check",      10,  isbn.getCheck());
      assertEquals("group",      "0", isbn.getGroup());
      assertEquals("publischer", "7869", isbn.getPublisher());
      assertEquals("title",      "0139", isbn.getTitle());
    }

    /** Testing reading. */
    @org.junit.Test
    public void parse()
    {
      assertEquals("simple", "0-596-00283-1",
                   PARSER.parse("0-596-00283-1").toString());
      assertEquals("whites", "0-596-00283-1",
                   PARSER.parse("0-   596\n- 00283 - 1 "));
      assertNull("invalid 0", PARSER.parse("0-596-00283-0"));
      assertNull("invalid 2", PARSER.parse("0-596-00283-2"));
      assertNull("invalid 3", PARSER.parse("0-596-00283-3"));
      assertNull("invalid 4", PARSER.parse("0-596-00283-4"));
      assertNull("invalid 5", PARSER.parse("0-596-00283-5"));
      assertNull("invalid 6", PARSER.parse("0-596-00283-6"));
      assertNull("invalid 7", PARSER.parse("0-596-00283-7"));
      assertNull("invalid 8", PARSER.parse("0-596-00283-8"));
      assertNull("invalid 9", PARSER.parse("0-596-00283-9"));
      assertNull("invalid X", PARSER.parse("0-596-00283-x"));
      assertNull("missing", PARSER.parse("123-444-222 h"));
      assertNull("missing 2", PARSER.parse("123-444-22a-3"));
    }

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

    /** Testing setting. */
    @org.junit.Test
    public void unformatted()
    {
      ISBN isbn = new ISBN("0", "7869", "2944", 8);
      assertEquals("simple", "0786929448", isbn.toUnformatted());

      isbn = new ISBN("0", "7869", "0755", 10);
      assertEquals("string", "078690755X", isbn.toUnformatted());
    }
  }
}
