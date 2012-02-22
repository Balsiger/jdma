/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.entries.extensions;

import javax.annotation.Nonnull;

import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.values.Duration;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.formatters.Formatter;
import net.ixitxachitls.dma.values.formatters.LinkFormatter;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the wearable extension for all the entries.
 *
 * @file          BaseWearable.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class BaseWearable extends BaseExtension<BaseItem>
{
  //----------------------------------------------------------------- nested

  //----- slots ------------------------------------------------------------

  /** The available body slots (cf. ). */
  public enum Slot implements EnumSelection.Named
  {
    /** On the head. */
    HEAD("Head"),
    /** Around the neck. */
    NECK("Neck"),
    /** On the torso only. */
    TORSO("Torso"),
    /** On the whole body. */
    BODY("Body"),
    /** Around the waits. */
    WAIST("Waist"),
    /** On the shoulders. */
    SHOULDERS("Shoulders"),
    /** On both hands. */
    HANDS("Hands"),
    /** On a hand. */
    HAND("Hand"),
    /** On a finger. */
    FINGER("Finger"),
    /** On one or both wrists. */
    WRISTS("Wrists"),
    /** One one or both of the feet. */
    FEET("Feet");

    /** The value's name. */
    private @Nonnull String m_name;

    /** Create the name.
     *
     * @param inName     the name of the value
     *
     */
    private Slot(@Nonnull String inName)
    {
      m_name = constant("body.slots", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public @Nonnull String toString()
    {
      return m_name;
    }
  };

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------ BaseWearable ----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inName the name of the extension
   *
   */
  public BaseWearable(@Nonnull BaseItem inEntry, @Nonnull String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //------------------------------ BaseWearable ----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   */
  // public BaseWearable(BaseItem inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The printer for printing the whole base item. */
  public static final Print s_pagePrint =
    new Print("%slot %don %remove");

  //----- slot -------------------------------------------------------------

  /** The formatter for light failure. */
  protected static final Formatter<EnumSelection<Slot>>
    s_slotFormatter = new LinkFormatter<EnumSelection<Slot>>
    (link(BaseItem.TYPE, Index.Path.SLOTS));

  /** The slot where the item can be worn. */
  @Key("slot")
  protected @Nonnull EnumSelection<Slot> m_slot =
    new EnumSelection<Slot>(Slot.class).withFormatter(s_slotFormatter);

  static
  {
    addIndex(new Index(Index.Path.SLOTS, "Slots", BaseItem.TYPE));
  }

  //........................................................................
  //----- don --------------------------------------------------------------

  /** The formatter for donning it. */
  protected static final Formatter<Duration> s_donFormatter =
    new LinkFormatter<Duration>(link(BaseItem.TYPE, Index.Path.DONS));

  /** How much time it takes to don the armor. */
  @Key("don")
  protected @Nonnull Multiple m_don = new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new Duration().withFormatter(s_donFormatter), false,
                           null, "/"),
      new Multiple.Element(new Duration().withFormatter(s_donFormatter), false),
    });

  static
  {
    addIndex(new Index(Index.Path.DONS, "Donning Times", BaseItem.TYPE));
  }

  //........................................................................
  //----- remove -----------------------------------------------------------

  /** The formatter for donning it. */
  protected static final Formatter<Duration> s_removeFormatter =
    new LinkFormatter<Duration>(link(BaseItem.TYPE, Index.Path.REMOVES));

  /** How much time it takes to don the armor. */
  @Key("remove")
  protected @Nonnull Duration m_remove =
    new Duration().withFormatter(s_removeFormatter);

  static
  {
    addIndex(new Index(Index.Path.REMOVES, "Removing Times", BaseItem.TYPE));
  }

  //........................................................................

  static
  {
    setAutoExtensions(BaseWearable.class, "wearable");
    extractVariables(BaseItem.class, BaseWearable.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- getPagePrint -----------------------------

  /**
   * Get the print for a full page.
   *
   * @return the print for page printing
   *
   */
  @Override
  protected @Nonnull Print getPagePrint()
  {
    return s_pagePrint;
  }

  //........................................................................
  //----------------------------- getListPrint -----------------------------

  /**
   * Get the print for a list entry.
   *
   * @return the print for list entry
   *
   */
  @Override
  protected @Nonnull ListPrint getListPrint()
  {
    return s_listPrint;
  }

  //........................................................................
  //------------------------- computeIndexValues ---------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @param       ioValues a multi map of values per index name
   *
   */
  @Override
  public void computeIndexValues(@Nonnull Multimap<Index.Path, String> ioValues)
  {
    super.computeIndexValues(ioValues);

    // donning times
    ioValues.put(Index.Path.DONS, m_don.get(0).group());
    ioValues.put(Index.Path.DONS, m_don.get(1).group());

    ioValues.put(Index.Path.SLOTS, m_slot.group());
    ioValues.put(Index.Path.REMOVES, m_remove.group());
  }

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
