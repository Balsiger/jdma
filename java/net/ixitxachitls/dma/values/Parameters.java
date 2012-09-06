/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.Command;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class is used to store various parameters, e.g. for initializing
 * entries to be used in the real entries.
 *
 * @file          Parameters.java
 *
 * @author        balsiger@ixitxachitlsnet (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Parameters extends Value<Parameters>
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- Parameters -------------------------------

  /**
   * Construct the parameters object.
   *
   * @param       inValues the key value pairs for the paramers.
   *
   * @undefined   IllegalArgumentException if no parameters given
   * @undefined   IllegalArgumentException if invalid parameters given (i.e.
   *                                       numbers don't match)
   *
   */
  public Parameters(@Nonnull Map<String, Value> inValues)
  {
    m_values = inValues;
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
  public Parameters create()
  {
    Map<String, Value> values = Maps.newHashMap();
    for(Map.Entry<String, Value> entry : m_values.entrySet())
      values.put(entry.getKey(), entry.getValue().create());

    return super.create(new Parameters(values));
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The values read or stored. */
  protected @Nonnull Map<String, Value> m_values;

  /** The joiner for printing the map values. */
  private static final Joiner s_joiner = Joiner.on(", ");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //---------------------------- getKeyValues ------------------------------

  /**
   * Get all the defined key value pairs.
   *
   * @return      the defined key/value pairs
   *
   */
  // public Map<String, Value> getKeyValues()
  // {
  //   Map<String, Value> result = new TreeMap<String, Value>();

  //   for(int i = 0; i < m_values.length; i++)
  //     if(m_values[i].isDefined())
  //       result.put(m_keys[i], m_values[i]);

  //   return result;
  // }

  //........................................................................
  //--------------------------- getLCKeyValues -----------------------------

  /**
   * Get all the defined lower case key value pairs.
   *
   * @return      the defined key/value pairs
   *
   * @undefined   never
   *
   */
  // public Map<String, Value> getLCKeyValues()
  // {
  //   Map<String, Value> result = new TreeMap<String, Value>();

  //   for(int i = 0; i < m_values.length; i++)
  //     if(m_values[i].isDefined())
  //       result.put(m_keys[i].toLowerCase(), m_values[i]);

  //   return result;
  // }

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
    // Map<String, Value> defined = getKeyValues();

    // ArrayList<Object> commands = new ArrayList<Object>();

    // for(Map.Entry<String, Value> entry : defined.entrySet())
    // {
    //   if(commands.size() > 0)
    //     commands.add(", ");

    //   commands.add(entry.getKey().toString());
    //   commands.add(" ");
    //   commands.add(entry.getValue().format(false));
    // }

    // return new Command(commands.toArray());
    return null;
  }

  //........................................................................
  //------------------------------ doToString ------------------------------

  /**
   * Return a string representation of the value. The value can be assumed to
   * be defined when this is called. This method should not be called directly,
   * instead call toString().
   *
   * @return      a string representation.
   *
   */
  protected @Nonnull String doToString()
  {
    List<String> values = new ArrayList<String>();
    for(Map.Entry<String, Value> entry : m_values.entrySet())
    {
      if (!entry.getValue().isDefined())
        continue;

      values.add(entry.getKey() + " " + entry.getValue().toString());
    }

    return s_joiner.join(values);
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
    for(Value value : m_values.values())
      if(value.isDefined())
        return true;

    return false;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

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
    boolean found = false;

    ParseReader.Position pos = inReader.getPosition();
    for(String key = inReader.expectCase(m_values.keySet(), true); key != null;
        key = inReader.expectCase(m_values.keySet(), true))
    {
      Value value = m_values.get(key).read(inReader);
      if(value == null)
      {
        inReader.seek(pos);
        break;
      }

      m_values.put(key, value);

      found = true;

      // store the position and try to read the next one
      pos = inReader.getPosition();

      // another value?
      if(!inReader.expect(","))
        break;
    }

    return found;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** Testing init. */
    @org.junit.Test
    public void init()
    {
      Parameters parameters =
        new Parameters(new ImmutableMap.Builder<String, Value>()
                       .put("a", new Name())
                       .put("b", new Rational())
                       .put("c", new Dice())
                       .build());

      // undefined value
      assertEquals("not undefined at start", false, parameters.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   parameters.toString());
      assertEquals("undefined value not correct", "\\color{error}{$undefined$}",
                   parameters.format(false).toString());

      // now with some parameters
      parameters = new Parameters(new ImmutableMap.Builder<String, Value>()
                                  .put("a", new Name())
                                  .put("b", new Rational(1, 2))
                                  .put("c", new Dice(1, 3, 2))
                                  .build());

      assertEquals("not defined after setting", true, parameters.isDefined());
      assertEquals("value not correctly converted", "b 1/2, c 1d3 +2",
                   parameters.toString());

      // what do we have?
      assertEquals("get a", "$undefined$",
                   parameters.m_values.get("a").toString());
      assertEquals("get b", "1/2",
                   parameters.m_values.get("b").toString());
      assertEquals("get c", "1d3 +2",
                   parameters.m_values.get("c").toString());

      Value.Test.createTest(parameters);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      String []tests =
        {
          "simple", "a first", "a first", null,
          "simple 2", "c 1d3", "c 1d3", null,
          "casing", "B 1/2", "b 1/2", null,
          "whites", "    a  first, b     \n  1 ", "b 1, a first", "",
          "multi", "a first, b 2, c 1d3", "b 2, c 1d3, a first", null,
          "empty", "", null, null,
          "invalid", "guru", null, "guru",
          "invalid 2", "aa", null, "aa",
          "invalid 3", "b c", null, "b c",
          "invalid 3", "a, c", null, "a, c",
          "partly", "a first, b", "a first", ", b",
          "partly", "c 1d4 +2, b g, a hello", "c 1d4 +2", ", b g, a hello",
        };

      Value.Test.readTest(tests,
                          new Parameters
                          (new ImmutableMap.Builder<String, Value>()
                           .put("a", new Name())
                           .put("b", new Rational())
                           .put("c", new Dice())
                           .build()));
    }

    //......................................................................
  }

  //........................................................................
}
