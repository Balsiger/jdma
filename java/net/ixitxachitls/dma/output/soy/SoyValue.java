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

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.values.BaseNumber;
import net.ixitxachitls.dma.values.Remark;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.util.Classes;
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

@ParametersAreNonnullByDefault
@Immutable
public class SoyValue extends SoyAbstract
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
  public SoyValue(String inName, Value<?> inValue, AbstractEntry inEntry)
  {
    this(inName, inValue, inEntry, true);

    m_entry = inEntry;
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
  public SoyValue(String inName, Value<?> inValue,
                  AbstractEntry inEntry, boolean inEditable)
  {
    super(inName, inEntry);

    m_value = inValue;
    m_editable = inEditable;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The real value. */
  protected final Value<?> m_value;

  /** The command renderer for rendering values. */
  public static final SoyRenderer COMMAND_RENDERER =
    new SoyRenderer(new SoyTemplate("commands", "value", "page"));

  /** A flag if the value can be edited. */
  protected final boolean m_editable;

  protected AbstractEntry m_entry;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- toString -------------------------------

  /**
   * Convert the soy value to a string for debugging.
   *
   * @return  the converted string
   */
  @Override
  public String toString()
  {
    return print();
  }

  //........................................................................
  //-------------------------------- print ---------------------------------

  /**
   * Print the value to soy.
   *
   * @return  a string representation for html rendering
   *
   */
  public String print()
  {
    if(m_value == null)
      return "(no value)";

    if(m_entry == null)
      return "(no entry)";

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
  public String raw()
  {
    return m_value.print(m_entry);
  }

  //........................................................................

  //------------------------------ getSingle -------------------------------

  /**
   * Get a single value out of the value.
   *
   * @param  inName the name of the value to get
   *
   * @return the value found or null if not found
   */
  @Override
  @SuppressWarnings({ "rawtypes" }) // need to case to value list
  public @Nullable SoyData getSingle(String inName)
  {
    if("isEditable".equals(inName))
      return BooleanData.forValue(m_value != null && m_editable);

    if("isDefined".equals(inName))
      return BooleanData.forValue(m_value != null && m_value.isDefined());

    if(m_value != null)
    {
      if("edit".equals(inName))
        return StringData.forValue
          (Encodings.encodeHTMLAttribute(m_value.getEditValue()));

      if("raw".equals(inName))
        return StringData.forValue(m_value.toString(false));

      if("short".equals(inName))
        return StringData.forValue(m_value.toShortString());

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

      if("list".equals(inName) && m_value instanceof ValueList)
      {
        List<SoyValue> values = new ArrayList<SoyValue>();
        for (Object value : (ValueList)m_value)
          values.add(new SoyValue(m_name, (Value)value, m_entry));

         return new SoyListData(values);
      }

      if("remark".equals(inName))
      {
        Remark remark = m_value.getRemark();
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
    if(m_value != null)
    {
      value = Classes.callMethod(inName, m_value);
      if(value != null)
        return convert(inName, value);

      value = m_value.compute(inName);
      if(value != null)
        return convert(inName, value);
    }

    return new Undefined(m_name + "." + inName);
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
    if(inOther == this)
      return true;

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
      assertEquals("combined",
                   "combined test (test entry): test entry = null [], "
                   + "modifiers [], values [], expressions []",
                   soyValue.getSingle("combined").toString());
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
      assertEquals("list", "(undefined test.list)",
                   soyValue.getSingle("list").toString());
      assertEquals("multi", "(undefined test.multi)",
                   soyValue.getSingle("multi").toString());
    }

    //......................................................................
  }

  //........................................................................
}
