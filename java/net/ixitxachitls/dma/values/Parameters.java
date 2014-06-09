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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.ixitxachitls.dma.entries.BaseSpell;
import net.ixitxachitls.dma.entries.SkillType;
import net.ixitxachitls.dma.proto.Values.ParametersProto;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

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
@ParametersAreNonnullByDefault
public class Parameters extends Value<Parameters>
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- Parameters -------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * Construct the parameters object.
   *
   */
  public Parameters()
  {
  }

  //........................................................................
  //--------------------------------- with ---------------------------------

  /**
   * Add a value to the parameters.
   *
   * @param   inName  the name of the parameter value
   * @param   inValue the parameter value
   * @param   inType  the type describing how to combine values
   *
   * @return  the parameters for chaining
   */
  public Parameters with(String inName, Value<?> inValue, Type inType)
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
   */
  @Override
  public Parameters create()
  {
    Parameters result = new Parameters();
    for(Map.Entry<String, Value<?>> entry : m_values.entrySet())
      result.with(entry.getKey(), entry.getValue().create(),
                  m_types.get(entry.getKey()));

    return super.create(result);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The values read or stored. */
  protected Map<String, Value<?>> m_values = Maps.newHashMap();;

  /** The type of the values stored. */
  protected Map<String, Type> m_types = Maps.newHashMap();

  /** The joiner for printing the map values. */
  private static final Joiner s_joiner = Joiner.on(", ");

  /** The parameter types. */
  public enum Type { UNIQUE, ADD, MAX, MIN, };

  //........................................................................

  //-------------------------------------------------------------- accessors

  /**
   * Get the defined values in the parameters.
   *
   * @return a map from name to defined value
   */
  public Map<String, Value<?>> getValues()
  {
    Map<String, Value<?>> defined = new HashMap<>();

    for(Map.Entry<String, Value<?>> entry : m_values.entrySet())
      if(entry.getValue().isDefined())
        defined.put(entry.getKey().toLowerCase(), entry.getValue());

    return defined;
  }

  //------------------------------- getValue -------------------------------

  /**
   * Get the value for the given name.
   *
   * @param       inName the name of value to get
   *
   * @return      the value for the name, if any
   */
  public @Nullable Value<?> getValue(String inName)
  {
    return m_values.get(inName.toLowerCase(Locale.US));
  }

  //........................................................................
  //------------------------------- hasValue -------------------------------

  /**
   * Get the value for the given name.
   *
   * @param       inName the name of value to get
   *
   * @return      the value for the name, if any
   */
  public boolean hasValue(String inName)
  {
    return m_values.containsKey(inName.toLowerCase(Locale.US));
  }

  //........................................................................
  //------------------------------ getSummary ------------------------------

  /**
   * Get a summary for the parameters.
   *
   * @return    the parameters summary
   */
  public String getSummary()
  {
    if(getValue("summary") != null && getValue("summary").isDefined())
      return getValue("summary").toString();

    List<String> result = Lists.newArrayList();
    for(String key : m_values.keySet())
      if(m_types.get(key) == Type.UNIQUE && m_values.get(key).isDefined())
        result.add(m_values.get(key).toString());

    return Strings.SPACE_JOINER.join(result);
  }

  //........................................................................
  //------------------------------ getUniques ------------------------------

  /**
   * Create a string with all the unique parameter values.
   *
   * @return  a string with all the unique values
   */
  public String getUniques()
  {
    List<String> uniques = Lists.newArrayList();

    for(String key : m_types.keySet())
      if(m_types.get(key) == Type.UNIQUE
         && m_values.get(key).isDefined())
        uniques.add(m_values.get(key).toString());

    return Strings.SPACE_JOINER.join(uniques);
  }

  //........................................................................

  //------------------------------ doToString ------------------------------

  /**
   * Return a string representation of the value. The value can be assumed to
   * be defined when this is called. This method should not be called directly,
   * instead call toString().
   *
   * @return      a string representation.
   */
  @Override
  protected String doToString()
  {
    List<String> values = new ArrayList<String>();
    for(Map.Entry<String, Value<?>> entry : m_values.entrySet())
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
   */
  @Override
  public boolean isDefined()
  {
    for(Value<?> value : m_values.values())
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
   */
  @Override
  public boolean doRead(ParseReader inReader)
  {
    boolean found = false;

    ParseReader.Position pos = inReader.getPosition();
    for(String key = inReader.expectCase(m_values.keySet(), true); key != null;
        key = inReader.expectCase(m_values.keySet(), true))
    {
      Value<?> value = getValue(key).read(inReader);
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
   * Create a new parameter value with the given values for parameters.
   *
   * @param       inParameters the parameter values to use
   *
   * @return      the copied parameter
   */
  public Parameters asValues(@Nullable Map<String, String> inParameters)
  {
    if(inParameters == null || inParameters.isEmpty())
      return this;

    Parameters result = new Parameters();
    for(Map.Entry<String, Value<?>> entry : m_values.entrySet())
      result.with(entry.getKey(), entry.getValue(),
                  m_types.get(entry.getKey()));

    for(Map.Entry<String, String> entry : inParameters.entrySet())
    {
      Value<?> value = result.getValue(entry.getKey());
      if(value == null)
      {
        Log.warning("cannot find parameter for " + entry.getKey());
        continue;
      }

      if(value.isDefined())
        continue;

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
   * Adds the current and given parameters into a new set of parameters.
   *
   * @param    inParameters  the parameters to add
   *
   * @return   new parameters with all the added values
   */
  @Override
  public Parameters add(Parameters inParameters)
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

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the value given values together.
   *
   * @param   inFirst   the first value to add
   * @param   inSecond  the second value to add
   * @param   inType    the type of parameter value, denoting whot to combine
   *                    them
   *
   * @return  the addition of the two values, according to type
   */
  @SuppressWarnings("unchecked")
  private Value<?> add(Value<?> inFirst, @Nullable Value<?> inSecond,
                       Type inType)
  {
    if(inSecond == null || !inSecond.isDefined())
      return inFirst;

    if(!inFirst.isDefined())
      return inSecond;

    switch(inType)
    {
      case UNIQUE: return inFirst;
      case ADD:    return ((Value)inFirst).add(inSecond);
      case MIN:    return ((Value)inFirst).min(inSecond);
      case MAX:    return ((Value)inFirst).max(inSecond);
      default: assert false : "should never happen";
    }

    return inFirst;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  /**
   * Convert to a proto value.
   *
   * @return the converted proto
   */
  @SuppressWarnings("unchecked")
  public ParametersProto toProto()
  {
    ParametersProto.Builder builder = ParametersProto.newBuilder();

    for(Map.Entry<String, Value<?>> entry : m_values.entrySet())
      if(entry.getValue().isDefined())
        if(entry.getValue() instanceof Distance)
          builder.addDistance(ParametersProto.Distance.newBuilder()
                              .setName(entry.getKey())
                              .setDistance(((Distance)entry.getValue())
                                           .toProto())
                              .build());
        else if(entry.getValue() instanceof Name)
          builder.addText(ParametersProto.Text.newBuilder()
                          .setName(entry.getKey())
                          .setText(((Name)entry.getValue()).get())
                          .build());
        else if(entry.getValue() instanceof Number)
          builder.addNumber(ParametersProto.Number.newBuilder()
                            .setName(entry.getKey())
                            .setNumber((int)((Number)entry.getValue()).get())
                            .build());
        else if(entry.getValue() instanceof Modifier)
          builder.addModifier(ParametersProto.Modifier.newBuilder()
                              .setName(entry.getKey())
                              .setModifier(((Modifier)entry.getValue())
                                           .toProto())
                              .build());
        else if(entry.getValue() instanceof EnumSelection
          && ((EnumSelection)entry.getValue()).getSelected()
              instanceof BaseSpell.SpellClass)
          builder.addSpellClass(ParametersProto.SpellClass.newBuilder()
                          .setName(entry.getKey())
                          .setSpellClass
                          (((EnumSelection<BaseSpell.SpellClass>)
                            entry.getValue()).getSelected().getProto())
                          .build());
        else if(entry.getValue() instanceof EnumSelection
          && ((EnumSelection)entry.getValue()).getSelected()
              instanceof SkillType)
          builder.addSkillSubtype(ParametersProto.SkillSubtype.newBuilder()
                                  .setName(entry.getKey())
                                  .setSkillSubtype
                                  (((EnumSelection<SkillType>)
                                    entry.getValue()).getSelected().toProto())
                                    .build());

    return builder.build();
  }

  /**
   * Create the parameters from the given proto.
   *
   * @param inProto the proto to read from
   * @return the created parameters
   */
  @SuppressWarnings("unchecked")
  public Parameters fromProto(ParametersProto inProto)
  {
    Parameters params = create();

    for(ParametersProto.Distance distance : inProto.getDistanceList())
      params.m_values.put(distance.getName(),
                          ((Distance)params.m_values.get(distance.getName()))
                          .fromProto(distance.getDistance()));
    for(ParametersProto.Text text: inProto.getTextList())
      params.m_values.put(text.getName(),
                          ((Name)params.m_values.get(text.getName()))
                          .as(text.getText()));
    for(ParametersProto.Number number : inProto.getNumberList())
      params.m_values.put(number.getName(),
                          ((Number)params.m_values.get(number.getName()))
                          .as(number.getNumber()));
    for(ParametersProto.Modifier modifier: inProto.getModifierList())
      params.m_values.put(modifier.getName(),
                          ((Modifier)params.m_values.get(modifier.getName()))
                          .fromProto(modifier.getModifier()));
    for(ParametersProto.Damage damage : inProto.getDamageList())
      params.m_values.put(damage.getName(),
                          ((Damage)params.m_values.get(damage.getName()))
                          .fromProto(damage.getDamage()));
    for(ParametersProto.SpellClass spellClass : inProto.getSpellClassList())
      params.m_values.put(spellClass.getName(),
                          ((EnumSelection<BaseSpell.SpellClass>)
                            params.m_values.get(spellClass.getName()))
                          .as(BaseSpell.SpellClass.fromProto
                              (spellClass.getSpellClass())));
    for(ParametersProto.SkillSubtype skill : inProto.getSkillSubtypeList())
      params.m_values.put(skill.getName(),
                          ((EnumSelection<SkillType>)
                            params.m_values.get(skill.getName()))
                          .as(SkillType.fromProto
                              (skill.getSkillSubtype())));

    return params;
  }

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
