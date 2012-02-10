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

package net.ixitxachitls.util.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a simple enhancement to a JUnit TestCase, adding a mock logger
 * to the test, including verifying test messages.
 *
 * @file          TestCase.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class TestCase extends org.junit.Assert
{
  //--------------------------------------------------------- constructor(s)
  //........................................................................

  //-------------------------------------------------------------- variables

  /** The mock logger to test logging statements. */
  protected Log.Test.MockLogger m_logger;

  /** The number of errors occurred. */
  protected int m_errors = 0;

  /** The number of failures occurred. */
  protected int m_failures = 0;

  /** The name of the test logger. */
  private static final String s_logger = "test";

  //........................................................................

  //-------------------------------------------------------------- accessors

  //---------------------------- assertPattern -----------------------------

  /**
   * Assert that the given string matches the given pattern.
   *
   * @param       inMessage the message
   * @param       inPattern the expected pattern
   * @param       inText    the text obtained
   *
   */
  public void assertPattern(@Nonnull String inMessage,
                            @Nonnull String inPattern, @Nonnull String inText)
  {
    Matcher matcher =
      Pattern.compile(inPattern, Pattern.DOTALL).matcher(inText);

    if(!matcher.matches())
      throw new org.junit.ComparisonFailure(inMessage, inPattern, inText);
  }

  //........................................................................
  //------------------------ assertContentsAnyOrder ------------------------

  /**
   * Check that the iterable contains the given objects in any order.
   *
   * @param    inMessage  the message to show on failure
   * @param    inActual   the objects that were actually produced
   * @param    inExpected the objects expected
   *
   */
  public void assertContentAnyOrder(@Nonnull String inMessage,
                                    @Nonnull Iterable<?> inActual,
                                    @Nonnull Object ... inExpected)
  {
    List<Object> actual = new ArrayList<Object>();
    List<Object> expected = new ArrayList<Object>();

    for(Iterator<?> i = inActual.iterator(); i.hasNext(); )
      actual.add(i.next());

    for(Object o : inExpected)
      expected.add(o);

    if(actual.size() != expected.size())
      raiseFailure(inMessage, expected, actual);

    for(Object o : actual)
      if(!expected.remove(o))
        raiseFailure(inMessage, expected, actual);
  }

  //........................................................................
  //---------------------------- assertContent -----------------------------

  /**
   * Assert the contents in the iterator.
   *
   * @param    inMessage  the message to show on failure
   * @param    inActual   the objects that were actually produced
   * @param    inExpected the objects expected
   *
   */
  public void assertContent(@Nonnull String inMessage,
                            @Nonnull Iterator<?> inActual,
                            @Nonnull Object ... inExpected)
  {
    for(Object o : inExpected)
    {
      Object next = inActual.next();
      if(o == null)
      {
        if(next != null)
          throw new org.junit.ComparisonFailure(inMessage, "NULL",
                                                next.toString());
      }
      else
        if(!o.equals(next))
          throw new org.junit.ComparisonFailure(inMessage, o.toString(),
                                                next.toString());
    }

    if(inActual.hasNext())
      throw new org.junit.ComparisonFailure(inMessage + " (end)", "to at end",
                                            "not yet at end");
  }

  //........................................................................
  //---------------------------- assertContent -----------------------------

  /**
   * Assert the contents in the iterator.
   *
   * @param    inMessage  the message to show on failure
   * @param    inActual   the objects that were actually produced
   * @param    inExpected the objects expected
   *
   */
  public void assertContent(@Nonnull String inMessage,
                            @Nonnull Iterable<?> inActual,
                            @Nonnull Object ... inExpected)
  {
    assertContent(inMessage, inActual.iterator(), inExpected);
  }

  //........................................................................
  //---------------------------- assertContent -----------------------------

  /**
   * Assert the contents in the iterator.
   *
   * @param    inMessage  the message to show on failure
   * @param    inActual   the objects that were actually produced
   * @param    inExpected the objects expected, as pairs of key/value
   *
   */
  public void assertContent(@Nonnull String inMessage,
                            @Nonnull Map<?, ?> inActual,
                            @Nonnull Object ... inExpected)
  {
    if(inActual.size() * 2 != inExpected.length)
      throw new org.junit.ComparisonFailure(inMessage, "" + inActual.size(),
                                            "" + (inExpected.length / 2));

    for(int i = 0; i < inExpected.length; i += 2)
    {
      Object key = inExpected[i];
      Object value = inExpected[i + 1];

      Object actual = inActual.get(key);

      if ((actual == null && value != null)
          || (actual != null && !actual.equals(value)))
        throw new org.junit.ComparisonFailure
          (inMessage + " [" + key + "]",
           value != null ? value.toString() : "NULL",
           actual != null ? actual.toString() : "NULL");
    }
  }

  //........................................................................

  //----------------------------- raiseFailure -----------------------------

  /**
   * Raise a failure because the actual and expected values don't match.
   *
   * @param       inMessage  the failure message
   * @param       inExpected the epected value
   * @param       inActual   the real value obtained
   *
   */
   private void raiseFailure(@Nonnull String inMessage,
                             @Nonnull List<?> inExpected,
                             @Nonnull List<?> inActual)
  {
    throw new org.junit.ComparisonFailure(inMessage, inExpected.toString(),
                                          inActual.toString());
  }

  //........................................................................

  //----------------------------- assertEquals -----------------------------

  /**
   * Check that the given object when converted to String matches the given
   * string.
   *
   * @param       inMessage  the message
   * @param       inExpected the expected string
   * @param       inActual   the actual object computed
   *
   */
//   public void assertEquals(String inMessage, String inExpected,
//                            Object inActual)
//   {
//     assertEquals(inMessage, inExpected, inActual.toString());
//   }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- setUp ---------------------------------

  /**
   * Setup the test for the next test case. This is called before each
   * test method.
   *
   * If the tests are run under Windows OS, the line.separator will be
   * set to the linux default '\n' instead of the windows default '\r\n'.
   * Otherwise following code will fail:
   * <pre>
   *      PrintWriter writer = new PrintWriter();
   *      writer.println("test line 1");
   *      assertEquals("contents","test line 1\n",writer.toString())
   * </pre>
   */
  @org.junit.Before
  public void setUpTest()
  {
    if (System.getProperty("os.name").startsWith("Windows"))
    {
      System.setProperty("line.separator", "\n");
    }

    m_logger = new Log.Test.MockLogger();
    Log.add(s_logger, m_logger);
    Log.setLevel(Log.Type.DEBUG);

  }

  //........................................................................
  //------------------------------- tearDown -------------------------------

  /**
   * Tear down the test after a test case. This is called after each
   * test method.
   *
   */
  @org.junit.After
  public void tearDown()
  {
    m_logger.verify("logger tear down");

    Log.remove(s_logger);
    m_logger = null;

  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................
}
