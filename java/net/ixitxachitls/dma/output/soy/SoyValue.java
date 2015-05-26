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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.FloatData;
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
public class SoyValue extends SoyMapData
{
  /**
   * Create the abstract soy value.
   *
   * @param inName  the name of the soy value
   * @param inEntry the entry in which to evaluate the values
   */
  public SoyValue(String inName, @Nullable Object inEntry)
  {
    m_name = inName;
    m_object = inEntry;
  }

  /** The name of the value. */
  protected final String m_name;

  /** The entry with the data. */
  protected final Object m_object;

  /** The cache for already computed values. */
  protected final Map<String, SoyData> m_cache = new HashMap<>();

  /**
   * Get the value represented by this soy value.
   *
   * @return the value
   */
  public Object getValue()
  {
    return m_object;
  }

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
    if(m_cache.containsKey(inName))
    {
      return m_cache.get(inName);
    }

    SoyData data = getSingleUncached(inName);
    m_cache.put(inName, data);

    return data;
  }

  private SoyData getSingleUncached(String inName)
  {
    Object value = m_object;
    if(value instanceof Optional && !inName.equals("get"))
    {
      if("isPresent".equals(inName) || "present".equals(inName))
        return BooleanData.forValue(((Optional) value).isPresent());

      if(((Optional)value).isPresent())
        value = ((Optional)value).get();
      else
        return new SoyUndefined(m_name + "." + inName + "(optional)");
    }

    if("integer".equals(inName) && value instanceof Integer)
      return IntegerData.forValue((Integer) value);

    if("integer".equals(inName) && value instanceof Optional
        && ((Optional<?>)value).isPresent()
        && ((Optional<?>)value).get() instanceof Integer)
      return IntegerData.forValue(((Optional<Integer>)value).get());

    if("float".equals(inName) && value instanceof Float)
      return FloatData.forValue((float) value);

    if("float".equals(inName) && value instanceof Double)
      return FloatData.forValue((double) value);

    if("float".equals(inName) && value instanceof Optional
        && ((Optional<?>)value).isPresent())
    {
      if(((Optional<?>)value).get() instanceof Float)
        return FloatData.forValue(((Optional<Float>)value).get());

      if(((Optional<?>)value).get() instanceof Double)
        return FloatData.forValue(((Optional<Double>)value).get());
    }

    if("formatted".equals(inName)) {
      Object formatted = Classes.callMethod("formatted", value);
      if (formatted != null)
        return convert(inName, formatted);

      return convert(inName, value);
    }

    value = Classes.callMethod(inName, value);
    if(value != null)
      return convert(inName, value);

    return new SoyUndefined(m_name + "." + inName);
  }

  public SoyData call(String inMethod, Object ... inArguments)
  {
    Object value = Classes.callMethod(inMethod, m_object, inArguments);
    if(value != null)
      return convert(inMethod, value);

    return new SoyUndefined(m_name + ".call(" + inMethod + ", "
                            + Arrays.toString(inArguments) + ")");
  }

  @Override
  public boolean equals(Object inOther) // $codepro.audit.disable
  {
    if (this == inOther)
      return true;

    if (!(inOther instanceof SoyValue))
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
    if(m_object instanceof Optional)
      if(((Optional)m_object).isPresent())
        return ((Optional)m_object).get().toString();
      else
        return "(undefined)";

    return m_object.toString();
  }

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
    if(inObject instanceof SanitizedContent)
      return (SanitizedContent)inObject;

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

    if(false && inObject instanceof Optional)
      if(((Optional) inObject).isPresent())
        return convert(inName, ((Optional) inObject).get());
      else
        return new SoyUndefined(m_name + "." + inName + "(optional)");

    if(inObject == null)
      return new SoyUndefined(m_name + "." + inName);

    return new SoyValue(m_name + "." + inName, inObject);
  }
}
