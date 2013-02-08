/******************************************************************************
 * Copyright (c) 2002-2007 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.output.soy;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.Multimap;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.StringData;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.util.Classes;
import net.ixitxachitls.util.Pair;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * An abstract soy value.
 *
 *
 * @file          SoyAbstract.java
 *
 * @author        Peter Balsiger
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public abstract class SoyAbstract extends SoyMapData
{
  //----------------------------------------------------------------- nested

  @Immutable
  @ParametersAreNonnullByDefault
  public static class Undefined extends SoyAbstract
  {
    public Undefined(String inName)
    {
      super(inName, null);
    }

    @Override
    public SoyData getSingle(String inName)
    {
      if("print".equals(inName)
         || "raw".equals(inName))
        return StringData.forValue(toString());

      return new Undefined(m_name + "." + inName);
    }

    @Override
    public String toString()
    {
      return "(undefined " + m_name + ")";
    }
  }

  @Immutable
  @ParametersAreNonnullByDefault
  public static class SoyWrapper extends SoyAbstract
  {
    public SoyWrapper(String inName, Object inValue, AbstractEntry inEntry)
    {
      super(inName, inEntry);

      m_value = inValue;
    }

    private Object m_value;

    @Override
    public SoyData getSingle(String inName)
    {
      Object value = Classes.callMethod(inName, m_value);
      if(value != null)
        return convert(inName, value);

      return new Undefined(m_name + "." + inName);
    }
  }

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  public SoyAbstract(@Nonnull String inName, @Nonnull AbstractEntry inEntry)
  {
    m_name = inName;
    m_entry = inEntry;
  }

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The name of the value. */
  protected final @Nonnull String m_name;

  /** The entry with the data. */
  protected final @Nonnull AbstractEntry m_entry;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //........................................................................

  //----------------------------------------------------------- manipulators

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- convert --------------------------------

  /**
   * Convert the given object into a soy value.
   *
   * @param       inObject the object to convert
   *
   * @return      the converted object
   *
   */
  @SuppressWarnings("unchecked")
  protected @Nonnull SoyData convert(@Nonnull String inName,
                                     @Nonnull Object inObject)
  {
    if(inObject instanceof Value)
      return new SoyValue(inName, (Value)inObject, m_entry);

    if(inObject instanceof AbstractEntry)
      return new SoyEntry((AbstractEntry)inObject);

    if(inObject instanceof Combined)
      return new SoyCombined((Combined)inObject);

    if(inObject instanceof Combination)
    {
      Combination<? extends Value> combination =
        (Combination<? extends Value>)inObject;

      return new SoyCombination(combination.getName(), combination,
                                combination.getTopValue(),
                                combination.getEntry());
    }

    if(inObject instanceof Collection)
    {
      SoyListData list = new SoyListData();
      for(Object element : (List)inObject)
        list.add(convert(inName + "_list", element));

      return list;
    }

    if(inObject instanceof Boolean)
      return BooleanData.forValue((Boolean)inObject);

    if(inObject instanceof Map)
    {
      SoyMapData map = new SoyMapData();
      Map<?, ?> input = (Map<?, ?>)inObject;
      for(Map.Entry<?, ?> entry : input.entrySet())
        map.putSingle(entry.getKey().toString(),
                      convert(inName, entry.getValue()));

      return map;
    }

    if(inObject instanceof Multimap)
      return convert(inName, ((Multimap)inObject).asMap());

    if(inObject instanceof Pair)
      return new SoyMapData
        ("first", convert(inName, ((Pair)inObject).first()),
         "second", convert(inName, ((Pair)inObject).second()));

    if(inObject instanceof Long || inObject instanceof Integer)
      return IntegerData.forValue(((Long)inObject).intValue());

    if(inObject instanceof String)
      return StringData.forValue(inObject.toString());

    if(inObject == null)
      return new Undefined(m_name + "." + inName);

    return new SoyWrapper(m_name + "." + inName, inObject, m_entry);

    // try
    // {
    //   return SoyData.createFromExistingData(inObject);
    // }
    // catch(SoyDataException e)
    // {
    //   Log.warning("could not convert " + inObject.getClass()
    //     + ", defaulting to string");

    //   return StringData.forValue(inObject.toString());
    // }
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
