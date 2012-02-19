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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.output.commands.Editable;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A handle to an individual value.
 *
 * @file          ValueHandle.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param         <T> the final, derived type
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public abstract class ValueHandle<T extends ValueHandle>
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- ValueHandle ------------------------------

  /**
   * Create the value handle.
   *
   * @param       inKey            the key of the value
   *
   */
  public ValueHandle(@Nonnull String inKey)
  {
    m_key = inKey;
    m_plural = m_key + "s";
  }

  //........................................................................

  //-------------------------------- withNote ------------------------------

  /**
   * Sets a note for editing the value.
   *
   * @param    inText the text of the note
   *
   * @return   the value handle for chaining
   *
   */
  public @Nonnull T withNote(@Nullable String inText)
  {
    m_note = inText;
    return (T)this;
  }

  //........................................................................
  //------------------------------- withPlural -----------------------------

  /**
   * Sets the plural of the key of the value.
   *
   * @param    inPlural the plural key for displaying
   *
   * @return   the value handle for chaining
   *
   */
  public @Nonnull T withPlural(@Nullable String inPlural)
  {
    if(inPlural == null)
      m_plural = m_key + "s";
    else
      m_plural = inPlural;

    return (T)this;
  }

  //........................................................................
  //-------------------------- withPlayerEditable --------------------------

  /**
   * Sets the value as editable by players.
   *
   * @param    inPlayer true for playe editable, false for not
   *
   * @return   the value handle for chaining
   *
   */
  public @Nonnull T withPlayerEditable(boolean inPlayer)
  {
    m_playerEditable = inPlayer;
    return (T)this;
  }

  //........................................................................
  //---------------------------- withPlayerOnly ----------------------------

  /**
   * Sets the value as for players only.
   *
   * @param    inPlayer true for playe value, false for all
   *
   * @return   the value handle for chaining
   *
   */
  public @Nonnull T withPlayerOnly(boolean inPlayer)
  {
    if(inPlayer && m_dm)
      throw new IllegalArgumentException("cannot set player only and dm");

    m_player = inPlayer;
    return (T)this;
  }

  //........................................................................
  //-------------------------------- withDM --------------------------------

  /**
   * Sets the value as for dms only.
   *
   * @param    inDM true for dm value, false for all
   *
   * @return   the value handle for chaining
   *
   */
  public @Nonnull T withDM(boolean inDM)
  {
    if(inDM && m_player)
      throw new IllegalArgumentException("cannot set player only and dm");

    m_dm = inDM;
    return (T)this;
  }

  //........................................................................
  //----------------------------- withEditable -----------------------------

  /**
   * Sets the value as being editable.
   *
   * @param    inEditable true for an editable value, false if not
   *
   * @return   the value handle for chaining
   *
   */
  public @Nonnull T withEditable(boolean inEditable)
  {
    m_editable = inEditable;
    return (T)this;
  }

  //........................................................................
  //----------------------------- withEditType -----------------------------

  /**
   * Set the edit type for the value.
   *
   * @param       inType the edit type to use
   *
   * @return      this object for chaining
   *
   */
  public @Nonnull ValueHandle withEditType(@Nonnull String inType)
  {
    m_editType = inType;

    return this;
  }

  //........................................................................
  //---------------------------- withEditChoices ---------------------------

  /**
   * Set the edit choices for the value.
   *
   * @param       inChoices the edit choices to use
   *
   * @return      this object for chaining
   *
   */
  public @Nonnull ValueHandle withEditChoices(@Nonnull String inChoices)
  {
    m_editChoices = inChoices;

    return this;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The key for this variable. */
  protected @Nonnull String m_key;

  /** A flag denoting if the value is for DMs only. */
  protected boolean m_dm = false;

  /** A flag denoting if the value can be edited. */
  protected boolean m_editable = false;

  /** A flag denoting if the value is for players only. */
  protected boolean m_player = false;

  /** A flag denoting if the value is for editable by players. */
  protected boolean m_playerEditable = false;

  /** A string with the plural of the key. */
  protected @Nonnull String m_plural;

  /** A string with a special note for editing. */
  protected @Nullable String m_note;

  /** The edit type. */
  protected @Nonnull String m_editType = "string";

  /** The edit choices. */
  protected @Nonnull String m_editChoices = "";

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- format --------------------------------

  /**
   * Format the associated value for printing.
   *
   * @param    inEntry    the entry from where to fetch the real value
   * @param    inDM       true if formatting for DM
   * @param    inEdit true if allowing to edit, false if not
   *
   * @return   the formatted value, usually either a String or a Command.
   *
   */
  public @Nullable Object format(@Nonnull ValueGroup inEntry, boolean inDM,
                                 boolean inEdit)
  {
    Object value = value(inEntry, inDM);

    if(value == null)
      return null;

    if(inDM && isPlayerOnly() || !inDM && isDMOnly())
      return null;

    String type;
    String choices;
    String name;
    Object formatted = formatted(inEntry, inDM);
    String related;
    String edit;

    if(value instanceof Value)
    {
      type = ((Value)value).getEditType();
      choices = ((Value)value).getChoices();
      related = ((Value)value).getRelated();
      edit = ((Value)value).getEditValue();
    }
    else
    {
      type = m_editType;
      choices = m_editChoices;
      related = "";
      edit = value(inEntry, inDM).toString();
    }

    // TODO: fix this (have to deal with attachments here)!
    assert inEntry instanceof AbstractEntry
      : "temporarily need an abstract entry here, please fix";

    String note = m_note;
    if(note == null)
      note = "";

    if(inEdit && m_editable && (inDM || m_playerEditable))
      return new Editable(inEntry.getID(),
                          inEntry.getEditType(),
                          formatted, m_key, edit, type, note,
                          choices, related);

    return formatted;
  }

  //........................................................................
  //-------------------------------- value ---------------------------------

  /**
   * Get the handled value from the given entry.
   *
   * @param    inEntry the entry containing the value
   * @param    inDM    true if getting the value for a DM
   *
   * @return   the value found or null if not found or not accessible
   *
   */
  public abstract @Nullable Object value(@Nonnull ValueGroup inEntry,
                                         boolean inDM);

  //........................................................................
  //------------------------------ formatted -------------------------------

  /**
   * Get the formatted value from the given entry.
   *
   * @param    inEntry the entry containing the value
   * @param    inDM    true if getting the value for a DM
   *
   * @return   the value found or null if not found or not accessible
   *
   */
  public abstract @Nullable Object formatted(@Nonnull ValueGroup inEntry,
                                             boolean inDM);

  //........................................................................

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
   * Check if the value] is editable by players only or not.
   *
   * @return      true if it is editable by players, false if not
   *
   */
  public boolean isPlayerEditable()
  {
    return m_playerEditable;
  }

  //........................................................................
  //------------------------------ isEditable ------------------------------

  /**
   * Check if the value] is editable or not.
   *
   * @return      true if it is editable, false if not
   *
   */
  public boolean isEditable()
  {
    return m_editable;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Simple value handle class for testing. */
    public static class TestHandle extends ValueHandle
    {
      /**
       * Create the value handle.
       *
       * @param   inKey            the key of the value
       * @param   inValue          the value to return for value()
       *
       */
      TestHandle(@Nonnull String inKey, Object inValue)
      {
        super(inKey);

        m_value = inValue;
      }

      /** The value to return for value(). */
      private Object m_value;

      /** {@inheritDoc} */
      @Override
	public Object value(ValueGroup inEntry, boolean inDM)
      {
        return m_value;
      }

      /** {@inheritDoc} */
      @Override
	public Object formatted(ValueGroup inEntry, boolean inDM)
      {
        return m_value;
      }
    }

    //----- init -----------------------------------------------------------

    /** The formatEditab Test. */
    @org.junit.Test
    public void init()
    {
      ValueHandle handle = new TestHandle("test", null)
        .withDM(true)
        .withEditable(true);

      assertEquals("plural", "tests", handle.getPluralKey());
      assertTrue("dm", handle.isDMOnly());
      assertFalse("player", handle.isPlayerOnly());
      assertFalse("editable", handle.isPlayerEditable());

      handle = new TestHandle("test", null)
        .withPlayerEditable(true)
        .withPlayerOnly(true)
        .withEditable(true)
        .withPlural("more");

      assertEquals("plural", "more", handle.getPluralKey());
      assertFalse("dm", handle.isDMOnly());
      assertTrue("player", handle.isPlayerOnly());
      assertTrue("editable", handle.isPlayerEditable());
    }

    //......................................................................
    //----- format ---------------------------------------------------------

    /** The formatEditab Test. */
    @org.junit.Test
    public void format()
    {
      net.ixitxachitls.dma.entries.BaseEntry entry =
        new net.ixitxachitls.dma.entries.BaseEntry("test id");
      ValueHandle handle = new TestHandle("test", "value")
        .withDM(true)
        .withEditable(true);
      assertNull("null", handle.format(entry, false, false));

      handle = new TestHandle("test", "value")
        .withPlayerOnly(true)
        .withEditable(true);
      assertNull("player only for dm", handle.format(entry, true, false));

      handle = new TestHandle("test", "value")
        .withDM(true)
        .withEditable(true);
      assertNull("dm only for player", handle.format(entry, false, false));
    }

    //......................................................................
    //----- formatEditable -------------------------------------------------

    /** The formatEditab Test. */
    @org.junit.Test
    public void formatEditable()
    {
      net.ixitxachitls.dma.entries.BaseEntry entry =
        new net.ixitxachitls.dma.entries.BaseEntry("test id");
      ValueHandle handle = new TestHandle("test", "string value")
        .withDM(true)
        .withEditable(true);

      assertEquals("string value",
                   "\\editable{test id}{base entry}{string value}{test}"
                   + "{string value}{string}",
                   handle.format(entry, true, true).toString());

      handle =
        new TestHandle("test", new net.ixitxachitls.dma.values.Name("name"))
        .withDM(true)
        .withEditable(true);

      assertEquals("name value",
                   "\\editable{test id}{base entry}{name}{test}{name}{name}",
                   handle.format(entry, true, true).toString());
    }

    //......................................................................
    //----- formatNoEdit ---------------------------------------------------

    /** The formatNoEdit Test. */
    @org.junit.Test
    public void formatNoEdit()
    {
      net.ixitxachitls.dma.entries.BaseEntry entry =
        new net.ixitxachitls.dma.entries.BaseEntry("test id");
      ValueHandle handle = new TestHandle("test", "value")
        .withDM(true)
        .withEditable(true);
      assertEquals("string value", "value", handle.format(entry, true, false));

      handle = new TestHandle("test", "value")
        .withPlayerOnly(true)
        .withEditable(true);
      assertEquals("string value", "value", handle.format(entry, false, true));

      handle =
        new TestHandle("test", new net.ixitxachitls.dma.values.Name("name"))
        .withPlayerOnly(true)
        .withEditable(true);
      assertEquals("name value", "name",
                   handle.format(entry, false, true).toString());
    }

    //......................................................................
  }

  //........................................................................
}
