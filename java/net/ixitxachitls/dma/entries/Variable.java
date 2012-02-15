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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
  public Variable(@Nonnull String inKey, @Nonnull Field inField,
                  boolean inStored, boolean inPrintUndefined)
  {
    super(inKey);

    m_field          = inField;
    m_store          = inStored;
    m_printUndefined = inPrintUndefined;
  }

  //......................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The field containing the value. */
  protected @Nonnull Field m_field;

  /** A flag denoting if the variable is to be stored (or computed). */
  protected boolean m_store;

  /** Flag if printing the value even when undefined. */
  protected boolean m_printUndefined;

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
  public @Nullable Value get(@Nonnull Object inEntry)
  {
    try
    {
      return (Value)m_field.get(inEntry);
    }
    catch(java.lang.IllegalAccessException e)
    {
      throw new UnsupportedOperationException
        ("Cannot access field " + m_field.getName() + ": " + e);
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
  public @Nullable Object value(@Nonnull ValueGroup inEntry, boolean inDM)
  {
    return get(inEntry);
  }

  //........................................................................
  //----------------------------- formatted --------------------------------

  /**
   * Get the value of the variable given a specific entry.
   *
   * @param       inEntry the entry to get the value from
   * @param       inDM    true if getting the value for a DM
   *
   * @return      the current value
   *
   */
  public @Nullable Object formatted(@Nonnull ValueGroup inEntry, boolean inDM)
  {
    Value value = get(inEntry);

    // if the current value is not defined, use the first defined value from a
    // base entry
    if(!value.isDefined())
      return inEntry.combineBaseValues(m_key, true);

    return value.format(!m_printUndefined);
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
  public @Nullable String getStringValue(@Nonnull Object inEntry)
  {
    Value result = get(inEntry);
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
  public boolean hasVariable(@Nonnull ValueGroup inEntry)
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
  public boolean hasValue(@Nonnull Object inEntry)
  {
    Value value = get(inEntry);

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
  public @Nonnull String toString()
  {
    return "var " + m_key + " (" + (isEditable() ? "editable" : "not editable")
      + (isDMOnly() ? ", DM" : "") + ")";
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- read ---------------------------------

  /**
   * Read a variable from the given reader.
   *
   * @param       inGroup    the group into which to read
   * @param       inReader   the reader to read from
   *
   * @return      true if read, false if not
   *
   */
  public boolean read(@Nonnull ValueGroup inGroup,
                      @Nonnull ParseReader inReader)
  {
    Value read = get(inGroup).read(inReader);

    if(read == null)
      return false;

    set(inGroup, read);

    return true;
  }

  //........................................................................

  //--------------------------------- set ----------------------------------

  /**
   * Set the value of the variable in a specific entry. This method does
   * not change the variable itself.
   *
   * @param       inEntry the entry to set the value in
   * @param       inValue the value to set to
   *
   */
  public void set(@Nonnull Changeable inEntry, @Nonnull Value inValue)
  {
    try
    {
      m_field.set(inEntry, inValue);
    }
    catch(java.lang.IllegalAccessException e)
    {
      throw new UnsupportedOperationException
        ("Cannot access field " + m_field.getName() + " for " + m_key
         + ": " + e);
    }

    inEntry.changed();
  }

  //........................................................................
  //---------------------------- setFromString -----------------------------

  /**
   * Set the value of the variable in a specific entry as a String. This
   * method does not change the variable itself.
   *
   * @param       inEntry  the entry to set the value in
   * @param       inValue  the value to set to
   *
   * @return      the text of the input String that was not used or null if
   *              all was used
   *
   */
  public @Nullable String setFromString(@Nonnull Changeable inEntry,
                                        @Nonnull String inValue)
  {
    if(inValue.isEmpty())
      return inValue;

    String rest;
    if(inValue.startsWith(Value.UNDEFINED))
    {
      set(inEntry, get(inEntry).create());

      rest = inValue.substring(Value.UNDEFINED.length());
    }
    else
    {
      // init the reader
      StringReader string = new StringReader(inValue);
      ParseReader reader  = new ParseReader(string, "set");

      Value value = get(inEntry).read(reader);

      if(value == null)
        return inValue;

      set(inEntry, value);

      // return the part that was not read
      rest = reader.read(inValue.length());
    }

    if(rest.isEmpty() || rest.matches("\\s*"))
      return null;

    return rest;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** A simple class for testing variables. */
    public static class TestObject implements Changeable
    {
      /** Changed field for testing. */
      private boolean m_changed = false;

      /** Value field for testing. */
      protected Value m_value = new Value.Test.TestValue();

      /** Change method for testing. */
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

      variable.setFromString(test, "guru");
      assertEquals("setting", "guru", variable.get(test).toString());
    }

    //......................................................................
  }

  //........................................................................
}
