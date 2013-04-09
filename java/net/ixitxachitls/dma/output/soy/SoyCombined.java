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

package net.ixitxachitls.dma.output.soy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.StringData;

import net.ixitxachitls.dma.values.BaseNumber;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Remark;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.util.Classes;
import net.ixitxachitls.util.Encodings;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A soy data object for combination values taking data from an entry and it's
 * base entries.
 *
 *
 * @file          SoyCombined.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class SoyCombined extends SoyValue
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ SoyCombined -----------------------------

  /**
   * Create the soy combination value.
   *
   * @param    inCombined    the combined value
   */
  public SoyCombined(Combined<?> inCombined)
  {
    super(inCombined.getName(), inCombined.getTopValue(),
          inCombined.getEntry());

    m_combined = inCombined;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The combination stored here. */
  private final Combined<?> m_combined;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getSingle -------------------------------

  /**
   * Get a single value out of the combination.
   *
   * @param  inName the name of the value to get
   *
   * @return the value found or null if not found
   */
  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public @Nullable SoyData getSingle(String inName)
  {
    if("isCombined".equals(inName))
      return BooleanData.forValue(true);

    if("name".equals(inName))
      return StringData.forValue(m_combined.getName());

    Value<?> top = m_combined.getEntry().getValue(m_combined.getName());

    if("isEditable".equals(inName))
      return BooleanData.forValue(top != null);

    if(top != null)
    {
      if("edit".equals(inName))
        return StringData.forValue
          (Encodings.encodeHTMLAttribute(top.getEditValue()));

      if("raw".equals(inName))
        return StringData.forValue(top.toString(false));

      if("type".equals(inName))
        return StringData.forValue(top.getEditType());

      if("related".equals(inName))
        if(top.getRelated() == null)
          return StringData.EMPTY_STRING;
        else
          return StringData.forValue(top.getRelated());

      if("choices".equals(inName))
      {
        if(top.getChoices() == null)
          return StringData.EMPTY_STRING;
        else
          return StringData.forValue(top.getChoices());
      }

      if("group".equals(inName))
        return StringData.forValue(top.group());

      if("isArithmetic".equals(inName))
        return BooleanData.forValue(top.isArithmetic());

      if("expression".equals(inName))
        if(top.hasExpression())
          return StringData.forValue(top.getExpression().toString());
        else
          return StringData.EMPTY_STRING;

      if("list".equals(inName) && top instanceof ValueList)
      {
        List<SoyValue> values = new ArrayList<SoyValue>();
        for (Object value : (ValueList)top)
          values.add(new SoyValue(m_name, (Value)value, m_entry));

        return new SoyListData(values);
      }

      if("multi".equals(inName) && top instanceof Multiple)
      {
        List<SoyValue> values = new ArrayList<SoyValue>();
        for (Multiple.Element element : (Multiple)top)
          values.add(new SoyValue(m_name, element.get(), m_entry));

        return new SoyListData(values);
      }

      if("number".equals(inName) && top instanceof BaseNumber)
        return IntegerData.forValue((int)((BaseNumber)top).get());

      if("remark".equals(inName))
      {
        Remark remark = top.getRemark();
        if (remark != null)
          return new SoyMapData("type", remark.getType().getDescription(),
                                "comment", remark.getComment());
      }
    }

    // check if there is a function with the given name in this soy value
    Object value = Classes.callMethod(inName, this);
    if(value != null)
      return convert(inName, value);

    // check if there is a function with the given name in the value itself
    value = Classes.callMethod(inName, m_combined);
    if(value != null)
      return convert(inName, value);

    return super.getSingle(inName);
  }

  //........................................................................
  //-------------------------------- equals --------------------------------

  /**
   * Checks if the given object is equal to this one.
   *
   * @param    inOther the object to compare against
   *
   * @return   true if the other is equal, false if not
   *
   */
  @Override
  public boolean equals(Object inOther)
  {
    if(!(inOther instanceof SoyCombined))
      return false;

    return m_combined.equals(((SoyCombined)inOther).m_combined)
      && super.equals(inOther);
  }

  //........................................................................
  //------------------------------- hashCode -------------------------------

  /**
   * Compute the hash code of the object.
   *
   * @return      the object's hash code
   *
   */
  @Override
  public int hashCode()
  {
    return super.hashCode() + m_combined.hashCode();
  }

  //........................................................................
  //------------------------------- toString -------------------------------

  /**
   * Convert to a string for debugging.
   *
   * @return      the string represenation
   *
   */
  public String toString()
  {
    return "combined " + m_combined;
  }

  //........................................................................


  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................
}
