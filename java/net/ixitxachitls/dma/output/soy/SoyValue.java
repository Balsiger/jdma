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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.StringData;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.util.Encodings;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A wrapper to use dma values in soy templates.
 *
 *
 * @file          SoyValue.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class SoyValue extends SoyMapData
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- SoyValue -------------------------------

  /**
   * Create the soy value.
   *
   * @param    inName     the name of the value
   * @param    inValue    the dma value
   * @param    inEntry    the entry for the value
   * @param    inRenderer the renderer for rendering sub values
   *
   */
  public SoyValue(@Nonnull String inName, @Nonnull Value inValue,
                  @Nonnull AbstractEntry inEntry,
                  @Nonnull SoyRenderer inRenderer)
  {
    m_name = inName;
    m_value = inValue;
    m_entry = inEntry;
    m_renderer = inRenderer;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The name of the value. */
  protected final @Nonnull String m_name;

  /** The real value. */
  protected final @Nonnull Value m_value;

  /** The entry containing the value. */
  protected final @Nonnull AbstractEntry m_entry;

  /** The renderer for rendering sub values. */
  protected final @Nonnull SoyRenderer m_renderer;

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
  @SuppressWarnings("unchecked") // need to case to value list
  public @Nullable SoyData getSingle(@Nonnull String inName)
  {
    if("edit".equals(inName))
      return StringData.forValue
        (Encodings.encodeHTMLAttribute(m_value.getEditValue()));

    if("raw".equals(inName))
      return StringData.forValue(m_value.toString(false));

    if("print".equals(inName))
      return StringData.forValue(m_value.print(m_entry, m_renderer));

    if("bases".equals(inName))
      return StringData.forValue(new Combination<Value>(m_entry, m_name)
                                 .withIgnoreTop()
                                 .print(m_renderer));

    if("combine".equals(inName))
      if(m_value.isDefined())
        return this;
      else
        return new SoyCombination(m_name,
                                  new Combination<Value>(m_entry, m_name),
                                  m_value, m_entry, m_renderer);

    if("name".equals(inName))
      return StringData.forValue(m_name);

    if("type".equals(inName))
      return StringData.forValue(m_value.getEditType());

    if("related".equals(inName))
      if(m_value.getRelated() == null)
        return StringData.EMPTY_STRING;
      else
        return StringData.forValue(m_value.getRelated());

    if("choices".equals(inName))
    {
      if(m_value.getChoices() == null)
        return StringData.EMPTY_STRING;
      else
        return StringData.forValue(m_value.getChoices());
    }

    if("group".equals(inName))
      return StringData.forValue(m_value.group());

    if("isArithmetic".equals(inName))
      return BooleanData.forValue(m_value.isArithmetic());

    if("isDefined".equals(inName))
      return BooleanData.forValue(m_value.isDefined());

    if("expression".equals(inName))
      if(m_value.hasExpression())
        return StringData.forValue(m_value.getExpression().toString());
      else
        return StringData.EMPTY_STRING;

    if("list".equals(inName) && m_value instanceof ValueList)
    {
      List<SoyValue> values = new ArrayList<SoyValue>();
      for (Value value : (ValueList<Value>)m_value)
        values.add(new SoyValue(m_name, value, m_entry, m_renderer));

      return new SoyListData(values);
    }

    if("multi".equals(inName) && m_value instanceof Multiple)
    {
      List<SoyValue> values = new ArrayList<SoyValue>();
      for (Multiple.Element element : (Multiple)m_value)
        values.add(new SoyValue(m_name, element.get(), m_entry, m_renderer));

      return new SoyListData(values);
    }

    return null;
  }

  //.........................................................................

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
    if(!(inOther instanceof SoyValue))
      return false;

    return m_name.equals(((SoyValue)inOther).m_name)
      && m_value.equals(((SoyValue)inOther).m_value)
      && m_entry.equals(((SoyValue)inOther).m_entry)
      && m_renderer.equals(((SoyValue)inOther).m_renderer)
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
    return super.hashCode() + m_renderer.hashCode() + m_entry.hashCode()
      + m_value.hashCode() + m_name.hashCode();
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- single ---------------------------------------------------------

    /** The single Test. */
    @org.junit.Test
    public void single()
    {
      SoyValue soyValue =
        new SoyValue("test",
                     new net.ixitxachitls.dma.values.Text("just a name"),
                     new net.ixitxachitls.dma.entries.BaseEntry("test entry"),
                     new SoyRenderer(new SoyTemplate("lib/test/soy/test",
                                                     "lib/test/soy/test2")));

      assertEquals("edit", "&#34;just a name&#34;",
                   soyValue.getSingle("edit").toString());
      assertEquals("raw", "\"just a name\"",
                   soyValue.getSingle("raw").toString());
      assertEquals("print", "++ just a name ++",
                   soyValue.getSingle("print").toString());
      assertEquals("bases", "total: *undefined* bases: [] defined: no",
                   soyValue.getSingle("bases").toString());
      assertEquals("combine", "{}",
                   soyValue.getSingle("combine").toString());
      assertEquals("name", "test",
                   soyValue.getSingle("name").toString());
      assertEquals("type", "string",
                   soyValue.getSingle("type").toString());
      assertEquals("related", "",
                   soyValue.getSingle("related").toString());
      assertEquals("choices", "",
                   soyValue.getSingle("choices").toString());
      assertEquals("group", "just a name",
                   soyValue.getSingle("group").toString());
      assertEquals("isArithmetic", "false",
                   soyValue.getSingle("isArithmetic").toString());
      assertEquals("isDefined", "true",
                   soyValue.getSingle("isDefined").toString());
      assertEquals("expression", "",
                   soyValue.getSingle("expression").toString());
      assertNull("list", soyValue.getSingle("list"));
      assertNull("multi", soyValue.getSingle("multi"));
    }

    //......................................................................
  }

  //........................................................................
}
