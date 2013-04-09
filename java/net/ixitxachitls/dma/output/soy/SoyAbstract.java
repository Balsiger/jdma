/******************************************************************************
 * Copyright (c) 2002-2013 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.Multimap;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.StringData;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.util.Classes;
import net.ixitxachitls.util.Pair;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * An abstract soy value.
 *
 * @file          SoyAbstract.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public abstract class SoyAbstract extends SoyMapData
{
  //----------------------------------------------------------------- nested

  /**
   * An undefined value that can still be dereferenced, resulting in another
   * undefined value.
   */
  @Immutable
  @ParametersAreNonnullByDefault
  public static class Undefined extends SoyAbstract
  {
    /**
     * Create the undefined value with the given name.
     *
     * @param inName the name of the value
     */
    public Undefined(String inName)
    {
      super(inName, null);
    }

    /**
     * Get the named value.
     *
     * @param  inName the name of the value to get
     *
     * @return the value with the given name
     */
    @Override
    public SoyData getSingle(String inName)
    {
      if("print".equals(inName)
         || "raw".equals(inName))
        return StringData.forValue(toString());

      return new Undefined(m_name + "." + inName);
    }

    /**
     * Convert the undefined value to a human readable string.
     *
     * @return the string conversion
     */
    @Override
    public String toString()
    {
      return "(undefined " + m_name + ")";
    }
  }

  /**
   * A wrapper for a soy value calling a method with the given name or
   * returning an undefined value.
   */
  @Immutable
  @ParametersAreNonnullByDefault
  public static class SoyWrapper extends SoyAbstract
  {
    /** Create the wrapper.
     *
     * @param inName  the name of the value
     * @param inValue the object in which to call the method
     * @param inEntry the entry the value comes from
     */
    public SoyWrapper(String inName, Object inValue, AbstractEntry inEntry)
    {
      super(inName, inEntry);

      m_value = inValue;
    }

    /** The object in which to call the method. */
    private Object m_value;

    /**
     * Get a single, named value.
     *
     * @param  inName the name of the value to get
     *
     * @return the value returned by the name method call or undefined if not
     *         found
     */
    @Override
    public SoyData getSingle(String inName)
    {
      Object value = Classes.callMethod(inName, m_value);
      if(value != null)
        return convert(inName, value);

      return new Undefined(m_name + "." + inName);
    }

    @Override
    public boolean equals(Object inOther)
    {
      return super.equals(inOther);
    }

    @Override
    public int hashCode()
    {
      return super.hashCode();
    }
  }

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  /**
   * Create the abstract soy value.
   *
   * @param inName  the name of the soy value
   * @param inEntry the entry in which to evaluate the values
   */
  public SoyAbstract(String inName, @Nullable AbstractEntry inEntry)
  {
    m_name = inName;
    m_entry = inEntry;
  }

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The name of the value. */
  protected final String m_name;

  /** The entry with the data. */
  protected final AbstractEntry m_entry;

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
   * @param       inName   the name of the object to convert
   * @param       inObject the object to convert
   *
   * @return      the converted object
   */
  @SuppressWarnings("unchecked")
  protected SoyData convert(String inName, Object inObject)
  {
    if(inObject instanceof Value)
      return new SoyValue(inName, (Value)inObject, m_entry);

    if(inObject instanceof AbstractEntry)
      return new SoyEntry((AbstractEntry)inObject);

    if(inObject instanceof Combined)
      return new SoyCombined((Combined)inObject);

    if(inObject instanceof List)
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
        map.putSingle(entry.getKey() == null ? "" : entry.getKey().toString(),
                      convert(inName, entry.getValue()));

      return map;
    }

    if(inObject instanceof Multimap)
      return convert(inName, ((Multimap)inObject).asMap());

    if(inObject instanceof Pair)
      return new SoyMapData
        ("first", convert(inName, ((Pair)inObject).first()),
         "second", convert(inName, ((Pair)inObject).second()));

    if(inObject instanceof Long)
      return IntegerData.forValue(((Long)inObject).intValue());

    if(inObject instanceof Integer)
      return IntegerData.forValue(((Integer)inObject).intValue());

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
  // public static class Test extends net.ixitxachitls.util.test.TestCase
  // {
  // }

  //........................................................................
}
