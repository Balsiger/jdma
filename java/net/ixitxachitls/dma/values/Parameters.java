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
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.util.logging.Log;
import net.ixitxachitls.util.Strings;

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
   */
  public Parameters()
  {
  }

  //........................................................................
  //--------------------------------- with ---------------------------------

  /**
   *
   *
   * @param
   *
   * @return
   *
   */
  public Parameters with(@Nonnull String inName, @Nonnull Value inValue,
                         @Nonnull Type inType)
  {
    m_values.put(inName.toLowerCase(Locale.US), inValue);
    m_types.put(inName.toLowerCase(Locale.US), inType);

    return this;
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
  public Parameters create()
  {
    Parameters result = new Parameters();
    for(Map.Entry<String, Value> entry : m_values.entrySet())
      result.with(entry.getKey(), entry.getValue().create(),
                  m_types.get(entry.getKey()));

    return super.create(result);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The values read or stored. */
  protected @Nonnull Map<String, Value> m_values = Maps.newHashMap();;

  /** The type of the values stored. */
  protected @Nonnull Map<String, Type> m_types = Maps.newHashMap();

  /** The joiner for printing the map values. */
  private static final Joiner s_joiner = Joiner.on(", ");

  /** The parameter types. */
  public enum Type { UNIQUE, ADD, MAX, MIN, };

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getValue -------------------------------

  /**
   * Get the value for the given name.
   *
   * @param       inName the name of value to get
   *
   * @return      the value for the name, if any
   *
   */
  public @Nullable Value getValue(@Nonnull String inName)
  {
    return m_values.get(inName.toLowerCase(Locale.US));
  }

  //........................................................................
  //------------------------------ getSummary ------------------------------

  /**
   * Get a summary for the parameters.
   *
   * @return    the parameters summary
   *
   */
  public @Nonnull String getSummary()
  {
    if(getValue("summary") != null && getValue("summary").isDefined())
      return getValue("summary").toString();

    List<String> result = Lists.newArrayList();
    for(Value value : m_values.values())
      if(value.isDefined())
        result.add(value.toString());

    return Strings.SPACE_JOINER.join(result);
  }

  //........................................................................
  //------------------------------ getUniques ------------------------------

  /**
   *
   *
   * @param
   *
   * @return
   *
   */
  public @Nonnull String getUniques()
  {
    List<String> uniques = Lists.newArrayList();

    for(String key : m_types.keySet())
      if(m_types.get(key) == Type.UNIQUE
         && m_values.get(key).isDefined())
        uniques.add(m_values.get(key).toString());

    return Strings.SPACE_JOINER.join(uniques);
  }

  //........................................................................

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
   */
  // public Map<String, Value> getLCKeyValues()
  // {
  //   Map<String, Value> result = new TreeMap<String, Value>();

  //   for(Map.Entry<String, Value> entry : m_values.sint i = 0; i < m_values.length; i++)
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
    return new Command("guru");
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
      Value value = getValue(key).read(inReader);
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
  //------------------------------- asValues -------------------------------

  /**
   * Create a new parameter value with the given values for parameters
   *
   * @param       inParameters the parameter values to use
   *
   * @return      the copied parameter
   *
   */
  public @Nonnull Parameters asValues
    (@Nullable Map<String, String> inParameters)
  {
    if(inParameters == null || inParameters.isEmpty())
      return this;

    Parameters result = new Parameters();
    for(Map.Entry<String, Value> entry : m_values.entrySet())
      result.with(entry.getKey(), entry.getValue(),
                  m_types.get(entry.getKey()));

    for(Map.Entry<String, String> entry : inParameters.entrySet())
    {
      Value value = result.getValue(entry.getKey());
      if(value == null)
      {
        Log.warning("cannot find parameter for " + entry.getKey());
        continue;
      }

      value = value.read(entry.getValue());
      if (value == null)
        Log.warning("invalid value for " + entry.getKey() + " ignored");
      else
        result.with(entry.getKey(), value, result.m_types.get(entry.getKey()));
    }

    return result;
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   *
   *
   * @param
   *
   * @return
   *
   */
  public @Nonnull Parameters add(@Nonnull Parameters inParameters)
  {
    Parameters result = new Parameters();
    for(String key : m_values.keySet())
    {
      result.m_types.put(key, m_types.get(key));
      result.m_values.put(key, add(m_values.get(key),
                                   inParameters.m_values.get(key),
                                   m_types.get(key)));
    }

    // Add all the values that only appear in the given parameters.
    for(String key : inParameters.m_values.keySet())
      if(!m_values.containsKey(key))
      {
        result.m_types.put(key, inParameters.m_types.get(key));
        result.m_values.put(key, inParameters.m_values.get(key));
      }

    return result;
  }

  @SuppressWarnings("unchecked")
  private @Nonnull Value add(@Nonnull Value inFirst, @Nullable Value inSecond,
                             @Nonnull Type inType)
  {
    if(inSecond == null || !inSecond.isDefined())
      return inFirst;

    if(!inFirst.isDefined())
      return inSecond;

    switch(inType)
    {
      case UNIQUE: return inFirst;
      case ADD:    return inFirst.add(inSecond);
      case MIN:    return inFirst.min(inSecond);
      case MAX:    return inFirst.max(inSecond);
    }

    return inFirst;
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
      Parameters parameters = new Parameters()
        .with("a", new Name(), Type.UNIQUE)
        .with("b", new Rational(), Type.UNIQUE)
        .with("c", new Dice(), Type.UNIQUE);

      // undefined value
      assertEquals("not undefined at start", false, parameters.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   parameters.toString());
      assertEquals("undefined value not correct", "\\color{error}{$undefined$}",
                   parameters.format(false).toString());

      // now with some parameters
      parameters = new Parameters()
        .with("a", new Name(), Type.UNIQUE)
        .with("b", new Rational(1, 2), Type.UNIQUE)
        .with("c", new Dice(1, 3, 2), Type.UNIQUE);

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

      Value.Test.readTest(tests, new Parameters()
                          .with("a", new Name(), Type.UNIQUE)
                          .with("b", new Rational(), Type.UNIQUE)
                          .with("c", new Dice(), Type.UNIQUE));
    }

    //......................................................................
  }

  //........................................................................
}
