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

import com.google.common.collect.ImmutableList;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

/**
 * This is a simple enhancement to a JUnit TestCase, adding a mock logger
 * to the test, including verifying test messages.
 *
 * @file          TestCase.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */
public class TestCase extends org.junit.Assert
{
  /** The mock logger to test logging statements. */
  protected Log.Test.MockLogger m_logger;

  /** The name of the test logger. */
  private static final String s_logger = "test";

  /**
   * Assert that the given string matches the given pattern.
   *
   * @param       inMessage the message
   * @param       inPattern the expected pattern
   * @param       inText    the text obtained
   */
  public void assertPattern(String inMessage, String inPattern, String inText)
  {
    Matcher matcher =
      Pattern.compile(inPattern, Pattern.DOTALL).matcher(inText);

    if(!matcher.matches())
      throw new org.junit.ComparisonFailure(inMessage, inPattern, inText);
  }

  /**
   * Check that the iterable contains the given objects in any order.
   *
   * @param    inMessage  the message to show on failure
   * @param    inActual   the objects that were actually produced
   * @param    inExpected the objects expected
   */
  public void assertContentAnyOrder(String inMessage, Iterable<?> inActual,
                                    Object ... inExpected)
  {
    List<Object> actual = new ArrayList<Object>();
    List<Object> expected = new ArrayList<Object>();

    for(Iterator<?> i = inActual.iterator(); i.hasNext(); )
      actual.add(i.next().toString());

    for(Object o : inExpected)
      expected.add(o.toString());

    if(actual.size() != expected.size())
      raiseFailure(inMessage, expected, actual);

    for(Object o : actual)
      if(!expected.remove(o))
        raiseFailure(inMessage, ImmutableList.of(o), actual);
  }

  /**
   * Assert the contents in the iterator.
   *
   * @param    inMessage  the message to show on failure
   * @param    inActual   the objects that were actually produced
   * @param    inExpected the objects expected
   */
  public void assertContent(String inMessage, Iterator<?> inActual,
                            Object ... inExpected)
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

  /**
   * Assert the contents in the iterator.
   *
   * @param    inMessage  the message to show on failure
   * @param    inActual   the objects that were actually produced
   * @param    inExpected the objects expected
   */
  public void assertContent(String inMessage, Iterable<?> inActual,
                            Object ... inExpected)
  {
    assertContent(inMessage, inActual.iterator(), inExpected);
  }

  /**
   * Assert the contents in the iterator.
   *
   * @param    inMessage  the message to show on failure
   * @param    inActual   the objects that were actually produced
   * @param    inExpected the objects expected, as pairs of key/value
   */
  public void assertContent(String inMessage, Map<?, ?> inActual,
                            Object ... inExpected)
  {
    if(inActual.size() * 2 != inExpected.length)
      throw new org.junit.ComparisonFailure(inMessage, "" + inActual.size(),
                                            "" + (inExpected.length / 2));

    assertSomeContent(inMessage, inActual, inExpected);
  }

  public void assertSomeContent(String inMessage, Map<?, ?> inActual,
                                Object ... inExpected)
  {
    for(int i = 0; i < inExpected.length; i += 2)
    {
      Object key = inExpected[i];
      Object value = inExpected[i + 1];

      Object actual = inActual.get(key);

      if ((actual == null && value != null)
          || (actual != null && !actual.toString().equals(value.toString())))
        throw new org.junit.ComparisonFailure
            (inMessage + " [" + key + "]",
             value != null ? value.toString() : "NULL",
             actual != null ? actual.toString() : "NULL");
    }
  }

  /**
   * Assert that the map's string representations matches the given strings.
   *
   * @param inMessage   the message to print on failure
   * @param inActual    the actual map
   * @param inExpected  the expected key value pairs
   */
  public void assertStringContent(String inMessage, Map<?, ?> inActual,
                                  String ... inExpected)
  {
    if(inActual.size() * 2 != inExpected.length)
      throw new org.junit.ComparisonFailure(inMessage, "" + inActual.size(),
                                            "" + (inExpected.length / 2));

    for(int i = 0; i < inExpected.length; i += 2)
    {
      String key = inExpected[i];
      String value = inExpected[i + 1];

      String actual = inActual.get(key).toString();

      if ((actual == null && value != null)
          || (actual != null && !actual.equals(value)))
        throw new org.junit.ComparisonFailure
          (inMessage + " [" + key + "]",
           value != null ? value : "NULL",
           actual != null ? actual : "NULL");
    }
  }

  /**
   * Raise a failure because the actual and expected values don't match.
   *
   * @param       inMessage  the failure message
   * @param       inExpected the epected value
   * @param       inActual   the real value obtained
   */
   private void raiseFailure(String inMessage, List<?> inExpected,
                             List<?> inActual)
  {
    throw new org.junit.ComparisonFailure(inMessage, inExpected.toString(),
                                          inActual.toString());
  }

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
    if(System.getProperty("os.name").startsWith("Windows"))
    {
      System.setProperty("line.separator", "\n");
    }

    m_logger = new Log.Test.MockLogger();
    Log.add(s_logger, m_logger);
    Log.setLevel(Log.Type.DEBUG);

    Config.set("web.data.datastore", false);
    Config.set("web.data.datafiles", false);
    Config.set("web.data.testing", true);
  }

  /**
   * Tear down the test after a test case. This is called after each
   * test method.
   */
  @org.junit.After
  public void tearDown()
  {
    m_logger.verify("logger tear down");

    Log.remove(s_logger);
    m_logger = null;

    DMADataFactory.clear();
  }

  /**
   * Add the given entry to the data mock.
   *
   * @param       inEntry the entry to add
   */
  public void addEntry(AbstractEntry inEntry)
  {
    throw new UnsupportedOperationException("no more implemented");
  }
}
