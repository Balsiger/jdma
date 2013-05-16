/******************************************************************************
 * Copyright (c) 2002-2011 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import java.lang.reflect.Method;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a single value out of a given selection from a reader (and
 * write it to a writer of course).
 *
 * @file          EnumSelection.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @param         <T> the type of the real enumeration to instantiate for
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class EnumSelection<T extends Enum<T>> extends Value<EnumSelection<T>>
{
  //----------------------------------------------------------------- nested

  /** The interface for all specially named enumeration values. */
  @ParametersAreNonnullByDefault
  public interface Named
  {
    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public String getName();
  }

  /** The interface for all enumeration values with short names. */
  public interface Short
  {
    /** Get the short name of the value.
     *
     * @return the short name of the value
     *
     */
    public String getShort();
  }

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //---------------------------- EnumSelection -----------------------------

  /**
   * Construct the selection object.
   *
   * @param       inSelected   the selected enum value
   *
   */
  @SuppressWarnings("unchecked")
  public EnumSelection(T inSelected)
  {
    // have to cast here, don't know how to do it otherwise
    m_enum     = (Class<? extends T>)inSelected.getClass();
    m_selected = inSelected;

    init();
  }

  //........................................................................
  //---------------------------- EnumSelection -----------------------------

  /**
   * Construct the selection object.
   *
   * @param       inEnum   the class with all the enums
   *
   */
  public EnumSelection(Class<? extends T> inEnum)
  {
    m_enum = inEnum;

    init();
  }

  //........................................................................
  //---------------------------- EnumSelection -----------------------------

  /**
   * Construct the selection object from the values of another one.
   *
   * @param       inEnum       the class with all the enums
   * @param       inSelections the possible selections
   * @param       inSimple     the simple strings (if any)
   * @param       inMultiple   the multiple strings to look for (if any)
   *
   */
  protected EnumSelection(Class<? extends T> inEnum, T []inSelections,
                          @Nullable String []inSimple,
                          @Nullable String [][]inMultiple)
  {
    m_enum             = inEnum;
    m_selections       = inSelections;
    m_simpleSelections = inSimple;
    m_multiSelections  = inMultiple;
  }

  //........................................................................

  //------------------------------- create ---------------------------------

  /**
   * Create a new list with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar list, but without any contents
   *
   */
  @Override
  public EnumSelection<T> create()
  {
    return super.create(new EnumSelection<T>(m_enum, m_selections,
                                             m_simpleSelections,
                                             m_multiSelections));
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The enumeration class. */
  protected Class<? extends T> m_enum;

  /** The currently selected enumeration value. */
  protected @Nullable T m_selected = null;

  /** All the possible enum values. */
  protected T []m_selections;

  /** The possible strings for simple enums. */
  protected @Nullable String []m_simpleSelections;

  /** The possible strings for multiple value enums. */
  protected @Nullable String [][]m_multiSelections;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- getSelected ------------------------------

  /**
   * Get the selected value as an enum value.
   *
   * @return      the selection stored
   *
   */
  public @Nullable T getSelected()
  {
    return m_selected;
  }

  //........................................................................
  //------------------------------ getSorted -------------------------------

  /**
   * Get the selected value as a sortable string according to the definition
   * list.
   *
   * @return      the selection stored
   *
   */
  public @Nullable String getSorted()
  {
    if(m_selected == null)
      return null;

    return Strings.pad(m_selected.ordinal(), 2, true) + "-" + m_selected;
  }

  //........................................................................
  //------------------------------ getChoices ------------------------------

  /**
   * Get the all the possible values this value can be edited with.
   *
   * @return      the possible value to select from
   *
   */
  @Override
  public String getChoices()
  {
    return Strings.toString(m_selections, "||", "");
  }

  //........................................................................

  //------------------------------ doToString ------------------------------

  /**
   * Convert the value to a string.
   *
   * @return      a String representation
   *
   */
  @Override
  public String doToString()
  {
    if(m_selected instanceof Named)
      return ((Named)m_selected).getName();

    return m_selected.toString();
  }

  //........................................................................
  //----------------------------- toShortString ----------------------------

  /**
   * Convert the value to a short string.
   *
   * @return      a short String representation
   *
   */
  @Override
  public String toShortString()
  {
    if(m_selected instanceof Short)
      return ((Short)m_selected).getShort();

    if(m_selected instanceof Named)
      return ((Named)m_selected).getName();

    return m_selected.toString();
  }

  //........................................................................

  //------------------------------ isDefined -------------------------------

  /**
   * Check if the value is defined or not.
   *
   * @return      true if the value is defined, false if not
   *
   */
  @Override
  public boolean isDefined()
  {
    return m_selected != null;
  }

  //........................................................................
  //------------------------------ compareTo -------------------------------

  /**
   * Compare this value to another one.
   *
   * @param       inOther the value to compare to
   *
   * @return      -1 for less than, 0 for equal and +1 for greater than the
   *              object given
   *
   */
  @Override
  @SuppressWarnings("unchecked")
  public int compareTo(Object inOther)
  {
    if(!(inOther instanceof EnumSelection))
      return super.compareTo(inOther);

    EnumSelection<T> other = (EnumSelection<T>)inOther;

    // no values selected
    if(m_selected == null)
      if(other.m_selected == null)
        return 0;
      else
        return -1;

    if(other.m_selected == null)
      return +1;

    int compared = m_selected.compareTo(other.m_selected);
    if(compared != 0)
      return compared;

    if(m_remark == null && other.m_remark != null)
      return +1;

    if(m_remark != null && other.m_remark == null)
      return -1;

    return 0;
  }

  //........................................................................
  //----------------------------- isArithmetic -----------------------------

  /**
   * Checks whether the value is arithmetic and thus can be computed with.
   *
   * @return      true if the value is arithemtic
   *
   */
  @Override
  public boolean isArithmetic()
  {
    return false;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- add ----------------------------------

  /**
   * Add the current and given value and return it. The current value is not
   * changed.
   *
   * @param       inValue the value to add to this one
   *
   * @return      the added values
   *
   */
  @Override
  public EnumSelection<T> add(EnumSelection<T> inValue)
  {
    if(equals(inValue))
      return this;

    return super.add(inValue);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //---------------------------------- as ----------------------------------

  /**
   * Set the value of the selection.
   *
   * @param       inValue the value to select
   *
   * @return      a new selection with the given value
   *
   */
  public EnumSelection<T> as(T inValue)
  {
    EnumSelection<T> result = create();
    result.m_selected = inValue;

    return result;
  }

  //........................................................................
  //------------------------------- doRead ---------------------------------

  /**
   * Read the value from the reader and replace the current one.
   *
   * @param       inReader the reader to read from
   *
   * @return      true if read, false if not
   *
   */
  @Override
  public boolean doRead(ParseReader inReader)
  {
    int selected;
    if(m_multiSelections != null)
      selected = inReader.expectCase(m_multiSelections, true);
    else
      selected = inReader.expectCase(m_simpleSelections, true);

    if(selected < 0)
      return false;

    m_selected = m_selections[selected];

    return true;
  }

  //........................................................................

  //--------------------------------- max ----------------------------------

  /**
   * Compute the maximum of the two values.
   *
   * @param       inValue the value to add to this one
   *
   * @return      the maximum value (usually one of the values)
   *
   */
  @Override
  public EnumSelection<T> max(EnumSelection<T> inValue)
  {
    EnumSelection<T> result = create();

    if (m_selected == null
        || inValue.m_selected.ordinal() > m_selected.ordinal())
      result.m_selected = inValue.m_selected;
    else
      result.m_selected = m_selected;

    return result;
  }

  //........................................................................
  //--------------------------------- min ----------------------------------

  /**
   * Compute the minimal of the two values.
   *
   * @param       inValue the value to add to this one
   *
   * @return      the minimal value (usually one of the values)
   *
   */
  @Override
  public EnumSelection<T> min(EnumSelection<T> inValue)
  {
    EnumSelection<T> result = create();

    if (m_selected == null
        || inValue.m_selected.ordinal() < m_selected.ordinal())
      result.m_selected = inValue.m_selected;
    else
      result.m_selected = m_selected;

    return result;
  }

  //........................................................................

  //-------------------------------- init ----------------------------------

  /**
   * Initialize the enumeration array.
   *
   */
  @SuppressWarnings("unchecked")
  protected void init()
  {
    withEditType("selection");

    // we have to use reflection here, because the values() method in an
    // enumeration is static and only present in the enumeration values but not
    // in java.lang.Enum
    try
    {
      Method method = m_enum.getDeclaredMethod("values");

      // don't know how to do it without cast, it's reflection after all
      m_selections = (T [])method.invoke(m_enum);

      if(m_selections[0] instanceof Short)
      {
        m_multiSelections = new String[m_selections.length][2];

        for(int i = 0; i < m_selections.length; i++)
        {
          if(m_selections[0] instanceof Named)
            m_multiSelections[i][0] = ((Named)m_selections[i]).getName();
          else
            m_multiSelections[i][0] = m_selections[i].toString();

          m_multiSelections[i][1] = ((Short)m_selections[i]).getShort();
        }
      }
      else
      {
        m_simpleSelections = new String[m_selections.length];

        if(m_selections[0] instanceof Named)
          for(int i = 0; i < m_selections.length; i++)
            m_simpleSelections[i] = ((Named)m_selections[i]).getName();
        else
          for(int i = 0; i < m_selections.length; i++)
            m_simpleSelections[i] = m_selections[i].toString();
      }
    }
    catch(java.lang.NoSuchMethodException e)
    {
      Log.warning("could not get values() for '" + m_enum.getName() + "'");
    }
    catch(java.lang.IllegalAccessException e)
    {
      Log.warning("no access to values() for '" + m_enum.getName() + "'");
    }
    catch(java.lang.reflect.InvocationTargetException e)
    {
      Log.warning("could not execute values() for '" + m_enum.getName() + "'");
    }
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Simple enumeration for testing. */
    public enum Choice { one, two, three, four };

    //----- init -----------------------------------------------------------

    /** Testing init. */
    @org.junit.Test
    public void init()
    {
      EnumSelection<Choice> selection = new EnumSelection<Choice>(Choice.class);

      // undefined value
      assertEquals("not undefined at start", false, selection.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   selection.toString());
      assertNull("undefined value not correct", selection.getSelected());

      // now with some selection
      selection = new EnumSelection<Choice>(Choice.three);

      assertEquals("not defined after setting", true, selection.isDefined());
      assertEquals("value not correctly gotten", Choice.three,
                   selection.getSelected());
      assertEquals("value not correctly converted", "three",
                   selection.toString());
      assertEquals("sorted", "02-three", selection.getSorted());
      assertEquals("edit values", "one||two||three||four",
                   selection.getChoices());

      Value.Test.createTest(selection);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      String []tests =
        {
          "simple", "two", "two", null,
          "casing", "FoUr", "four", null,
          "whites", "    three   ", "three", "   ",
          "empty", "", null, null,
          "invalid", "guru", null, "guru",
          "invalid 2", "oneeee", null, "oneeee",
          "other", "two again", "two", " again",
          "other 2", "three.", "three", ".",
        };

      Value.Test.readTest(tests, new EnumSelection<Choice>(Choice.class));
    }

    //......................................................................
    //----- setting --------------------------------------------------------

    /** The setting Test. */
    @org.junit.Test
    public void setting()
    {
      EnumSelection<Choice> val1 = new EnumSelection<Choice>(Choice.one);
      EnumSelection<Choice> val2 = new EnumSelection<Choice>(Choice.two);

      assertEquals("compare", -1, val1.compareTo(val2));
      assertEquals("compare", +1, val2.compareTo(val1));

      val1 = val1.as(Choice.two);
      assertEquals("compare", 0, val1.compareTo(val2));

      val1 = val1.create();
      assertEquals("compare", -1, val1.compareTo(val2));
      assertEquals("compare", 1, val2.compareTo(val1));

      val1 = val1.as(Choice.four);
      assertEquals("max", Choice.four, val1.max(val2).getSelected());
      assertEquals("max", Choice.four, val2.max(val1).getSelected());
      assertEquals("min", Choice.two, val1.min(val2).getSelected());
      assertEquals("min", Choice.two, val2.min(val1).getSelected());
    }

    //......................................................................
  }

  //........................................................................
}
