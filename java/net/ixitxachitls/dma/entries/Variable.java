/******************************************************************************
 * Copyright (c) 2002-2007 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import java.lang.reflect.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.values.Value;

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
public class Variable
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
   * @param       inDM             true if the value is for DMs only
   * @param       inPlayer         true if the value is for players only
   * @param       inPlayerEditable true if the value is editable for a player
   * @param       inPlural         the plural of the key
   *
   */
  public Variable(@Nonnull String inKey, @Nonnull Field inField,
                  boolean inStored, boolean inDM, boolean inPlayer,
                  boolean inPlayerEditable, @Nullable String inPlural)
  {
    m_key            = inKey;
    m_field          = inField;
    m_store          = inStored;
    m_dm             = inDM;
    m_player         = inPlayer;
    m_playerEditable = inPlayerEditable;

    if(inPlural == null || inPlural.length() == 0)
      m_plural = m_key + "s";
    else
      m_plural = inPlural;
  }

  //......................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The key for this variable. */
  protected @Nonnull String m_key;

  /** The field containing the value. */
  protected @Nonnull Field m_field;

  /** A flag denoting if the variable is to be stored (or computed). */
  protected boolean m_store;

  /** A flag denoting if the value is for DMs only. */
  protected boolean m_dm;

  /** A flag denoting if the value is for players only. */
  protected boolean m_player;

  /** A flag denoting if the value is for editable by players. */
  protected boolean m_playerEditable;

  /** A string with the plural of the key. */
  protected @Nonnull String m_plural;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- getKey --------------------------------

  /**
   * Get the keyword of the variable.
   *
   * @return      the keyword
   *
   */
  public @Nonnull String getKey()
  {
    return m_key;
  }

  //........................................................................
  //------------------------------ getValue --------------------------------

  /**
   * Get the value of the variable given a specific entry.
   *
   * @param       inEntry the entry to get the value from
   *
   * @return      the current value
   *
   */
  @SuppressWarnings("unchecked") // have to cast below
  public @Nullable Value getValue(@Nonnull Object inEntry)
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
  //--------------------------------- get ----------------------------------

  /**
   * Get the value of the variable given a specific entry (cloned).
   *
   * @param       inEntry the entry to get the value from
   *
   * @return      the current value (cloned)
   *
   */
  public @Nullable Value get(@Nonnull Object inEntry)
  {
    Value result = getValue(inEntry);
    if(result != null)
      return result.clone();

    return null;
  }

  //......................................................................
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
    Value result = getValue(inEntry);
    if(result != null)
      return result.toString();

    return null;
  }

  //........................................................................
  //----------------------------- getPluralKey -----------------------------

  /**
   * Get the pural version of the key.
   *
   * @return      a string with the plural
   *
   */
  public @Nonnull String getPluralKey()
  {
    return m_plural;
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
    Value value = getValue(inEntry);

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
  //------------------------------- isDMOnly -------------------------------

  /**
   * Check if the variable is for DMs only or not.
   *
   * @return      true if for DMs only, false if not
   *
   */
  public boolean isDMOnly()
  {
    return m_dm;
  }

  //........................................................................
  //----------------------------- isPlayerOnly -----------------------------

  /**
   * Check if the variable is for playsers only or not.
   *
   * @return      true if it is players only, false if not
   *
   */
  public boolean isPlayerOnly()
  {
    return m_player;
  }

  //........................................................................
  //--------------------------- isPlayerEditable ---------------------------

  /**
   * Check if the value is editable by players only or not.
   *
   * @return      true if it is editable by players, false if not
   *
   */
  public boolean isPlayerEditable()
  {
    return m_playerEditable;
  }

  //........................................................................

  //------------------------------- toString -------------------------------

  /**
   * Convert the object to a human readable String representation.
   *
   * @return      the String representation
   *
   */
  public @Nonnull String toString()
  {
    return "var " + m_key;
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
   * @return      the text of the input String that was not used
   *
   */
  public @Nonnull String setFromString(@Nonnull Object inEntry,
                                       @Nonnull String inValue)
  {
    Value value = getValue(inEntry);

    if(value == null)
      return inValue;

    return value.setFromString(inValue);
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
      protected Value m_value = new Value.Test().m_value.clone();

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
      Variable variable =
        new Variable("key", field, false, false, false, false, null);

      assertEquals("key", "key", variable.getKey());
      assertEquals("value", "$undefined$",
                   variable.getValue(new TestObject()).toString());
      assertEquals("value", "$undefined$",
                   variable.get(new TestObject()).toString());
      assertEquals("value", "$undefined$",
                   variable.getStringValue(new TestObject()));
      assertFalse("has value", variable.hasValue(new TestObject()));
      assertFalse("stored", variable.isStored());
      assertFalse("dm only", variable.isDMOnly());
      assertFalse("player only", variable.isPlayerOnly());
      assertFalse("player editable", variable.isPlayerEditable());
      assertEquals("string", "var key", variable.toString());
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
      Variable variable =
        new Variable("key", field, false, false, false, false, null);
      TestObject test = new TestObject();
      Value value = test.m_value.clone();

      value.setFromString("guru");
      variable.set(test, value);
      assertEquals("setting", "guru", variable.getValue(test).toString());
    }

    //......................................................................
  }

  //........................................................................
}
