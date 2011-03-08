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
import net.ixitxachitls.output.commands.Editable;;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A handle to an individual value.
 *
 * @file          ValueHandle.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public abstract class ValueHandle
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- ValueHandle ------------------------------

  /**
   * Create the value handle.
   *
   * @param       inKey            the key of the value
   * @param       inDM             true if the value is for dms only
   * @param       inPlayer         true if the value is for players only
   * @param       inPlayerEditable true if the value can be edited by players
   * @param       inPlural         the plural value of the key
   *
   */
  public ValueHandle(@Nonnull String inKey, boolean inDM, boolean inPlayer,
                     boolean inPlayerEditable, @Nullable String inPlural)
  {
    if(inDM && inPlayer)
      throw new IllegalArgumentException
        ("can't have value for DM and player only");

    m_key = inKey;
    m_dm = inDM;
    m_player = inPlayer;
    m_playerEditable = inPlayerEditable;

    if(inPlural != null && !inPlural.isEmpty())
      m_plural = inPlural;
    else
      m_plural = m_key + "s";
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The key for this variable. */
  protected @Nonnull String m_key;

  /** A flag denoting if the value is for editable by players. */
  protected boolean m_playerEditable;

  /** A flag denoting if the value is for DMs only. */
  protected boolean m_dm;

  /** A flag denoting if the value is for players only. */
  protected boolean m_player;

  /** A string with the plural of the key. */
  protected @Nonnull String m_plural;

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
  public @Nonnull Object format(@Nonnull ValueGroup inEntry, boolean inDM,
                                boolean inEdit)
  {
    Object value = value(inEntry, inDM);

    if(value == null)
      return "";

    if(inDM && isPlayerOnly())
      return "";

    if(!inDM && isDMOnly())
      return "";

    String type;
    String choices;
    Object formatted;
    if(value instanceof Value)
    {
      type = ((Value)value).getEditType();
      choices = ((Value)value).getChoices();
      formatted = ((Value)value).format(true);
    }
    else
    {
      type = "string";
      choices = "";
      formatted = value.toString();
    }

    if(inEdit && (inDM || m_playerEditable))
      return new Editable(inEntry.getID(), formatted, m_key, value.toString(),
                          type, "", choices);

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
       * @param   inDM             true if the value is for dms only
       * @param   inPlayer         true if the value is for players only
       * @param   inPlayerEditable true if the value can be edited by players
       * @param   inPlural         the plural value of the key
       * @param   inValue          the value to return for value()
       *
       */
      TestHandle(@Nonnull String inKey, boolean inDM, boolean inPlayer,
                 boolean inPlayerEditable, @Nullable String inPlural,
                 Object inValue)
      {
        super(inKey, inDM, inPlayer, inPlayerEditable, inPlural);

        m_value = inValue;
      }

      /** The value to return for value(). */
      private Object m_value;

      /** {@inheritDoc} */
      public Object value(ValueGroup inEntry, boolean inDM)
      {
        return m_value;
      }
    }

    //----- init -----------------------------------------------------------

    /** The formatEditab Test. */
    @org.junit.Test
    public void init()
    {
      ValueHandle handle =
        new TestHandle("test", true, false, false, null, null);

      assertEquals("plural", "tests", handle.getPluralKey());
      assertTrue("dm", handle.isDMOnly());
      assertFalse("player", handle.isPlayerOnly());
      assertFalse("editable", handle.isPlayerEditable());

      handle = new TestHandle("test", false, true, true, "more", null);

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
      ValueHandle handle =
        new TestHandle("test", true, false, false, null, null);
      assertEquals("null", "", handle.format(entry, false, false));

      handle = new TestHandle("test", false, true, false, null, null);
      assertEquals("player only for dm", "", handle.format(entry, true, false));

      handle = new TestHandle("test", true, false, false, null, null);
      assertEquals("dm only for player", "",
                   handle.format(entry, false, false));
    }

    //......................................................................
    //----- formatEditable -------------------------------------------------

    /** The formatEditab Test. */
    @org.junit.Test
    public void formatEditable()
    {
      net.ixitxachitls.dma.entries.BaseEntry entry =
        new net.ixitxachitls.dma.entries.BaseEntry("test id");
      ValueHandle handle =
        new TestHandle("test", true, false, false, null, "string value");

      assertEquals("string value",
                   "\\editable{test id}{string value}{test}{string value}"
                   + "{string}", handle.format(entry, true, true).toString());

      handle = new TestHandle("test", true, false, false, null,
                              new net.ixitxachitls.dma.values.Name("name"));

      assertEquals("name value",
                   "\\editable{test id}{name}{test}{name}{name}",
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
      ValueHandle handle =
        new TestHandle("test", true, false, false, null, "value");
      assertEquals("string value", "value", handle.format(entry, true, false));

      handle = new TestHandle("test", false, true, false, null, "value");
      assertEquals("string value", "value", handle.format(entry, false, true));

      handle = new TestHandle("test", false, true, false, null,
                              new net.ixitxachitls.dma.values.Name("name"));
      assertEquals("name value", "name",
                   handle.format(entry, false, true).toString());
    }

    //......................................................................
  }

  //........................................................................
}
