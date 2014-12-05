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

package net.ixitxachitls.dma.output.soy;

import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.StringData;

import net.ixitxachitls.util.Classes;
import net.ixitxachitls.util.Pair;

/**
 * An abstract soy value.
 *
 * @file          SoyAbstract.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@Immutable
@ParametersAreNonnullByDefault
public class SoyAbstract extends SoyMapData
{
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
     * @param inObject the entry the value comes from
     */
    public SoyWrapper(String inName, Object inValue, Object inObject)
    {
      super(inName, inObject);

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
    @SuppressWarnings("unchecked")
    @Override
    public SoyData getSingle(String inName)
    {
      Object value = m_value;
      if(value instanceof Optional)
      {
        if("isPresent".equals(inName) || "present".equals(inName))
          return BooleanData.forValue(((Optional)value).isPresent());

        if(((Optional)value).isPresent())
          value = ((Optional)m_value).get();
        else
          return new Undefined(m_name + "." + inName);
      }

      if("integer".equals(inName) && m_value instanceof Integer)
        return IntegerData.forValue((Integer)m_value);

      if("integer".equals(inName) && m_value instanceof Optional
        && ((Optional<?>)m_value).isPresent()
        && ((Optional<?>)m_value).get() instanceof Integer)
        return IntegerData.forValue(((Optional<Integer>)m_value).get());

      value = Classes.callMethod(inName, value);
      if(value != null)
        return convert(inName, value);

      return new Undefined(m_name + "." + inName);
    }

    @Override
    public boolean equals(Object inOther) // $codepro.audit.disable
    {
      if (this == inOther)
        return true;

      if (!(inOther instanceof SoyAbstract))
        return false;

      return super.equals(inOther);
    }

    @Override
    public int hashCode()
    {
      return super.hashCode();
    }

    @Override
    public String toString()
    {
      if(m_value instanceof Optional)
        if(((Optional)m_value).isPresent())
          return ((Optional)m_value).get().toString();
        else
          return "(undefined)";

      return m_value.toString();
    }
  }

  /**
   * Create the abstract soy value.
   *
   * @param inName  the name of the soy value
   * @param inEntry the entry in which to evaluate the values
   */
  public SoyAbstract(String inName, @Nullable Object inEntry)
  {
    m_name = inName;
    m_object = inEntry;
  }

  /** The name of the value. */
  protected final String m_name;

  /** The entry with the data. */
  protected final Object m_object;

  /**
   * Convert the given object into a soy value.
   *
   * @param       inName   the name of the object to convert
   * @param       inObject the object to convert
   *
   * @return      the converted object
   */
  protected SoyData convert(String inName, Object inObject)
  {
    if(inObject instanceof Iterable)
    {
      SoyListData list = new SoyListData();
      for(Object element : (Iterable)inObject)
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

    return new SoyWrapper(m_name + "." + inName, inObject, m_object);
  }
}
