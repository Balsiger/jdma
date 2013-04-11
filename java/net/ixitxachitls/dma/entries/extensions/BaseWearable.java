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

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.values.Duration;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Multiple;

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

@ParametersAreNonnullByDefault
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
    private String m_name;

    /** Create the name.
     *
     * @param inName     the name of the value
     *
     */
    private Slot(String inName)
    {
      m_name = constant("body.slots", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String toString()
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
  public BaseWearable(BaseItem inEntry, String inName)
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

  //----- slot -------------------------------------------------------------

  /** The slot where the item can be worn. */
  @Key("slot")
  protected EnumSelection<Slot> m_slot =
    new EnumSelection<Slot>(Slot.class)
    .withTemplate("link", "slots");

  static
  {
    addIndex(new Index(Index.Path.SLOTS, "Slots", BaseItem.TYPE));
  }

  //........................................................................
  //----- don --------------------------------------------------------------

  /** How much time it takes to don the item. */
  @Key("don")
  protected Multiple m_don = new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new Duration(), false, null, "/"),
      new Multiple.Element(new Duration(), false),
    });

  static
  {
    addIndex(new Index(Index.Path.DONS, "Donning Times", BaseItem.TYPE));
  }

  //........................................................................
  //----- remove -----------------------------------------------------------

  /** How much time it takes to remove the item. */
  @Key("remove")
  protected Duration m_remove = new Duration();

  static
  {
    addIndex(new Index(Index.Path.REMOVES, "Removing Times", BaseItem.TYPE));
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------- computeIndexValues ---------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @param       ioValues a multi map of values per index name
   *
   */
  @Override
  public void computeIndexValues(Multimap<Index.Path, String> ioValues)
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
