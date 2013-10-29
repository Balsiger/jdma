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
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseWearableProto;
import net.ixitxachitls.dma.values.Duration;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.util.logging.Log;

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

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /** The available body slots (cf. ). */
  public enum Slot implements EnumSelection.Named, EnumSelection.Short,
    EnumSelection.Proto<BaseWearableProto.Slot>
  {
    /** On the head. */
    HEAD("Head", "He", BaseWearableProto.Slot.HEAD),
    /** Around the neck. */
    NECK("Neck", "N", BaseWearableProto.Slot.NECK),
    /** On the torso only. */
    TORSO("Torso", "T", BaseWearableProto.Slot.TORSO),
    /** On the whole body. */
    BODY("Body", "B", BaseWearableProto.Slot.BODY),
    /** Around the waits. */
    WAIST("Waist", "Wa", BaseWearableProto.Slot.WAIST),
    /** On the shoulders. */
    SHOULDERS("Shoulders", "S", BaseWearableProto.Slot.SHOULDERS),
    /** On both hands. */
    HANDS("Hands", "Hs", BaseWearableProto.Slot.HANDS),
    /** On a hand. */
    HAND("Hand", "Ha", BaseWearableProto.Slot.HAND),
    /** On a finger. */
    FINGER("Finger", "F", BaseWearableProto.Slot.FINGER),
    /** On one or both wrists. */
    WRISTS("Wrists", "Wr", BaseWearableProto.Slot.WRISTS),
    /** One one or both of the feet. */
    FEET("Feet", "F", BaseWearableProto.Slot.FEET);

    /** The value's name. */
    private String m_name;

    /** The value's short name. */
    private String m_short;

    /** The proto enum value. */
    private BaseWearableProto.Slot m_proto;

    /**
     * Create the name.
     *
     * @param inName     the name of the value
     * @param inShort    the short name of the value
     * @param inProto    the prot enum value
     */
    private Slot(String inName, String inShort, BaseWearableProto.Slot inProto)
    {
      m_name = constant("body.slots", inName);
      m_short = constant("body.slots.short", inShort);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public String getShort()
    {
      return m_short;
    }

    @Override
    public BaseWearableProto.Slot toProto()
    {
      return m_proto;
    }

    /**
     * Convert the given proto value to the corresponding enum value.
     *
     * @param inProto the proto value to convert
     * @return the converted enum value
     */
    public static Slot fromProto(BaseWearableProto.Slot inProto)
    {
      for(Slot slot : values())
        if(slot.m_proto == inProto)
          return slot;

      throw new IllegalArgumentException("unknown slot: " + inProto);
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

  @Override
  public Message toProto()
  {
    BaseWearableProto.Builder builder = BaseWearableProto.newBuilder();

    if(m_slot.isDefined())
      builder.setSlot(m_slot.getSelected().toProto());
    if(m_don.isDefined())
    {
      builder.setWear(((Duration)m_don.get(0)).toProto());
      builder.setWearHastily(((Duration)m_don.get(1)).toProto());
    }
    if(m_remove.isDefined())
      builder.setRemove(m_remove.toProto());

    return builder.build();
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseWearableProto))
    {
      Log.warning("cannot parse base wearable proto " + inProto.getClass());
      return;
    }

    BaseWearableProto proto = (BaseWearableProto)inProto;

    if(proto.hasSlot())
      m_slot = m_slot.as(Slot.fromProto(proto.getSlot()));

    if(proto.hasWear() || proto.hasWearHastily())
    {
      Duration wear = (Duration)m_don.get(0);
      Duration wearHastily = (Duration)m_don.get(1);
      if(proto.hasWear())
        wear = wear.fromProto(proto.getWear());
      if(proto.hasWearHastily())
        wearHastily = wearHastily.fromProto(proto.getWearHastily());

      m_don = m_don.as(wear, wearHastily);
    }

    if(proto.hasRemove())
      m_remove = m_remove.fromProto(proto.getRemove());
  }

  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
