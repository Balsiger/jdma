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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.StringData;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.values.BaseNumber;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Remark;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.util.Classes;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.logging.Log;

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
   *
   */
  public SoyValue(@Nonnull String inName, @Nonnull Value inValue,
                  @Nonnull AbstractEntry inEntry)
  {
    this(inName, inValue, inEntry, true);
  }

  //........................................................................
  //------------------------------- SoyValue -------------------------------

  /**
   * Create the soy value.
   *
   * @param    inName     the name of the value
   * @param    inValue    the dma value
   * @param    inEntry    the entry for the value
   * @param    inEditable true if the value can be edited, false if not
   *
   */
  public SoyValue(@Nonnull String inName, @Nonnull Value inValue,
                  @Nonnull AbstractEntry inEntry, boolean inEditable)
  {
    m_name = inName;
    m_value = inValue;
    m_entry = inEntry;
    m_editable = inEditable;
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

  /** The command renderer for rendering values. */
  public static final @Nonnull SoyRenderer COMMAND_RENDERER =
    new SoyRenderer(new SoyTemplate("commands", "value", "page"));

  /** A flag if the value can be edited. */
  protected final boolean m_editable;

  /** The combination for this value, if any needed yet. */
  protected @Nullable SoyCombination m_combination = null;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- print ---------------------------------

  /**
   * Print the value to soy.
   *
   * @return  a string representation for html rendering
   *
   */
  public @Nonnull String print()
  {
    String template = m_value.getTemplate();
    if (template == null)
      return m_value.print(m_entry);

    return COMMAND_RENDERER.render
      ("dma.value." + template,
       SoyTemplate.map("name", template,
                       "args", m_value.getTemplateArguments(),
                       "value", this,
                       "entry", new SoyEntry(m_entry),
                       "naked", m_value.print(m_entry)));
  }

  //........................................................................
  //--------------------------------- raw ----------------------------------

  /**
   * Convert the value to a raw string (no template processing for the value).
   *
   * @return   the raw string representation
   *
   */
  public @Nonnull String raw()
  {
    return m_value.print(m_entry);
  }

  //........................................................................


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

    if("isEditable".equals(inName))
      return BooleanData.forValue(m_editable);

    if("raw".equals(inName))
      return StringData.forValue(m_value.toString(false));

    if("print".equals(inName))
      return StringData.forValue(m_value.print(m_entry));

    if("combine".equals(inName))
    {
      if(m_combination == null)
        m_combination =
          new SoyCombination(m_name,
                             new Combination<Value>(m_entry, m_name)
                             /*.withIgnoreTop()*/, m_value, m_entry);

      return m_combination;
    }

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
        values.add(new SoyValue(m_name, value, m_entry));

      return new SoyListData(values);
    }

    if("multi".equals(inName) && m_value instanceof Multiple)
    {
      List<SoyValue> values = new ArrayList<SoyValue>();
      for (Multiple.Element element : (Multiple)m_value)
        values.add(new SoyValue(m_name, element.get(), m_entry));

      return new SoyListData(values);
    }

    if("number".equals(inName) && m_value instanceof BaseNumber)
      return IntegerData.forValue((int)((BaseNumber)m_value).get());

    if("remark".equals(inName))
    {
      Remark remark = m_value.getRemark();
      if (remark != null)
        return new SoyMapData("type", remark.getType().getDescription(),
                              "comment", remark.getComment());
    }

    // check if there is a function with the given name
    Method method = Classes.getMethod(m_value.getClass(), inName);
    if(method != null)
      try
      {
        return convert(method.invoke(m_value));
      }
      catch(IllegalAccessException e)
      {
        Log.warning("cannot access method: " + e);
      }
      catch(java.lang.reflect.InvocationTargetException e)
      {
        Log.warning("cannot invoke method: " + e);
      }

    Object value = m_value.compute(inName);
    if(value == null)
      return null;

    return convert(value);
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
    return super.hashCode() + m_entry.hashCode()
      + m_value.hashCode() + m_name.hashCode();
  }

  //........................................................................

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
  public @Nonnull SoyData convert(@Nonnull Object inObject)
  {
    if(inObject instanceof Boolean)
      return BooleanData.forValue((Boolean)inObject);

    return StringData.forValue(inObject.toString());
  }

  //........................................................................


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
                     new net.ixitxachitls.dma.entries.BaseEntry("test entry"));

      assertEquals("edit", "&#34;just a name&#34;",
                   soyValue.getSingle("edit").toString());
      assertEquals("raw", "\"just a name\"",
                   soyValue.getSingle("raw").toString());
      assertEquals("print", "just a name",
                   soyValue.getSingle("print").toString());
      assertEquals("combine", "total , min {}, max {}, bases []",
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
