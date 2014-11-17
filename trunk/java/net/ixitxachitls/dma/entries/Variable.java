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

package net.ixitxachitls.dma.entries;

import java.io.StringReader;
import java.lang.reflect.Field;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.input.ParseReader;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * Stores the information about a readable variable of an entry.  This allows
 * values of entries to be read by key and also to define some static
 * properties for such values.
 *
 * @file          Variable.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Variable extends ValueHandle<Variable>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Variable -------------------------------

  /**
   * The constructor.
   *
   * @param       inKey            the key this variable is read with
   * @param       inField          the field that contains the value for this
   *                               variable
   * @param       inStored         true if the value will be stored, false
   *                               if not
   * @param       inPrintUndefined if printing the value when undefined
   *
   */
  public Variable(String inKey, Field inField, boolean inStored,
                  boolean inPrintUndefined)
  {
    super(inKey);

    m_field          = inField;
    m_store          = inStored;
  }

  //......................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The field containing the value. */
  protected Field m_field;

  /** A flag denoting if the variable is to be stored (or computed). */
  protected boolean m_store;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------------- get ----------------------------------

  /**
   * Get the value of the variable given a specific entry.
   *
   * @param       inEntry the entry to get the value from
   *
   * @return      the current value
   *
   */
  public @Nullable Value<?> get(Object inEntry)
  {
    try
    {
      return (Value)m_field.get(inEntry);
    }
    catch(java.lang.IllegalAccessException e)
    {
      throw new UnsupportedOperationException
        ("Cannot access field " + m_field.getName(), e);
    }
  }

  //........................................................................
  //------------------------------- value ----------------------------------

  /**
   * Get the value of the variable given a specific entry.
   *
   * @param       inEntry the entry to get the value from
   * @param       inDM    true if getting the value for a DM
   *
   * @return      the current value
   *
   */
  @Override
  public @Nullable Object value(ValueGroup inEntry, boolean inDM)
  {
    return get(inEntry);
  }

  //........................................................................
  //--------------------------- getStringValue ---------------------------

  /**
   * Get the value of the variable as a String given a specific entry.
   *
   * @param       inEntry the entry to get the value from
   *
   * @return      the current value as String
   *
     */
  public @Nullable String getStringValue(Object inEntry)
  {
    Value<?> result = get(inEntry);
    if(result != null)
      return result.toString();

    return null;
  }

  //........................................................................

  //----------------------------- hasVariable ------------------------------

  /**
   * Checks if the given entry has this variable. A base variable always is
   * present in an entry.
   *
   * @param       inEntry the entry to check
   *
   * @return      true if the variable is there, false if not
   *
   */
  public boolean hasVariable(ValueGroup inEntry)
  {
    return true;
  }

  //........................................................................
  //------------------------------- hasValue -------------------------------

  /**
   * Check if, using the given entry, a value is defined or not.
   *
   * @param       inEntry the entry to take the value from
   *
   * @return      true if there is a value, false else
   *
     */
  public boolean hasValue(Object inEntry)
  {
    Value<?> value = get(inEntry);

    // if value is not set, we don't have to print anything
    if(value == null || !value.isDefined())
      return false;

    return true;
  }

  //......................................................................
  //------------------------------- isStored -------------------------------

  /**
   * Check if the variable is stored or not.
   *
   * @return      true if it is stored, false if not
   *
   */
  public boolean isStored()
  {
    return m_store;
  }

  //.......................................................................

  //------------------------------- toString -------------------------------

  /**
   * Convert the object to a human readable String representation.
   *
   * @return      the String representation
   *
   */
  @Override
  public String toString()
  {
    return "var " + m_key + " (" + (isEditable() ? "editable" : "not editable")
      + (isDMOnly() ? ", DM" : "") + ")";
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- set ----------------------------------

  /**
   * Set the value of the variable in a specific entry. This method does
   * not change the variable itself.
   *
   * @param       inEntry the entry to set the value in
   * @param       inValue the value to set to
   *
   */
  public void set(Changeable inEntry, Value<?> inValue)
  {
    try
    {
      m_field.set(inEntry, inValue);
    }
    catch(java.lang.IllegalAccessException e)
    {
      throw new UnsupportedOperationException
        ("Cannot access field " + m_field.getName() + " for " + m_key, e);
    }

    inEntry.changed();
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** A simple class for testing variables. */
    public static class TestObject implements Changeable
    {
      /** Changed field for testing. */
      @SuppressWarnings("unused")
      private boolean m_changed = false;

      /** Value field for testing. */
      protected Value<?> m_value = new Value.Test.TestValue();

      /** Change method for testing. */
      @Override
      public void changed()
      {
        m_changed = true;
      }
    }

    //----- init -----------------------------------------------------------

    /**
     * The init Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void init() throws Exception
    {
      Field field = Variable.Test.TestObject.class.getDeclaredField("m_value");
      Variable variable = new Variable("key", field, false, false)
        .withDM(true);

      assertEquals("key", "key", variable.getKey());
      assertEquals("value", "$undefined$",
                   variable.get(new TestObject()).toString());
      assertEquals("value", "$undefined$",
                   variable.get(new TestObject()).toString());
      assertEquals("value", "$undefined$",
                   variable.getStringValue(new TestObject()));
      assertFalse("has value", variable.hasValue(new TestObject()));
      assertFalse("stored", variable.isStored());
      assertTrue("dm only", variable.isDMOnly());
      assertFalse("player only", variable.isPlayerOnly());
      assertFalse("player editable", variable.isPlayerEditable());
      assertFalse("player editable", variable.isEditable());
      assertEquals("string", "var key (not editable, DM)", variable.toString());
      assertEquals("string", "keys", variable.getPluralKey());
    }

    //......................................................................
    //----- setting --------------------------------------------------------

    /**
     * The setting Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void setting() throws Exception
    {
      Field field = Variable.Test.TestObject.class.getDeclaredField("m_value");
      Variable variable = new Variable("key", field, false, false)
        .withEditable(true);
      TestObject test = new TestObject();

      //variable.setFromString(test, "guru");
      assertEquals("setting", "guru", variable.get(test).toString());
    }

    //......................................................................
  }

  /**
   * Flag the variable as searchable.
   *
   * @param   inSearchable true if searchable, false if not
   *
   * @return  this value, for chaining
   */
  public ValueHandle<Variable> withSearchable(boolean inSearchable)
  {
    m_searchable = inSearchable;

    return this;
  }

  //........................................................................
}
