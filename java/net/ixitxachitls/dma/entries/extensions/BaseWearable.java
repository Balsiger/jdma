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

package net.ixitxachitls.dma.entries.extensions;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseWearableProto;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.NewDuration;
import net.ixitxachitls.dma.values.NewValue;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the wearable extension for all the entries.
 *
 * @file          BaseWearable.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
public class BaseWearable extends ValueGroup
{
  /** The available body slots (cf. ). */
  public enum Slot implements EnumSelection.Named, EnumSelection.Short,
    EnumSelection.Proto<BaseWearableProto.Slot>
  {
    /** Unknown slots. */
    UNKNOWN("Unknown", "U", BaseWearableProto.Slot.UNKNOWN),
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

    /**
     * Convert a string into the corresponding slot.
     *
     * @param inValue the string to convert
     * @return the corresponding slot
     */
    public static Optional<Slot> fromString(String inValue)
    {
      for(Slot slot: values())
        if(slot.getName().equalsIgnoreCase(inValue))
          return Optional.of(slot);

      return Optional.absent();
    }

    /**
     * Get the possible names of types.
     *
     * @return a list of the names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();
      for(Slot slot: values())
        names.add(slot.getName());

      return names;
    }
  }

  /**
   * Default constructor.
   *
   * @param       inItem the base item attached to
   */
  public BaseWearable(BaseItem inItem)
  {
    m_item = inItem;
  }

  /** The entry this weapon belongs to. */
  protected BaseItem m_item;

  /** The slot where the item can be worn. */
  protected Slot m_slot = Slot.UNKNOWN;

  /** How much time it takes to don the item. */
  protected Optional<NewDuration> m_don = Optional.absent();

  /** How much time it takes to don the item hastily. */
  protected Optional<NewDuration> m_donHastily = Optional.absent();

  /** How much time it takes to remove the item. */
  protected Optional<NewDuration> m_remove = Optional.absent();

  /**
   * Get the slot.
   *
   * @return      the slot
   */
  public Slot getSlot()
  {
    return m_slot;
  }

  /**
   * Get the combined slot, including values of base wearable.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<Slot> getCombinedSlot()
  {
    if(m_slot != Slot.UNKNOWN)
      return new Combination.Max<Slot>(m_item, m_slot);

    List<Combination<Slot>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseWearable wearable = ((BaseItem)entry).getWearable();
        combinations.add(wearable.getCombinedSlot());
      }

    return new Combination.Max<Slot>(m_item, combinations);
  }

  /**
   * Get the duration for donning the item.
   *
   * @return      the don duration
   */
  public Optional<NewDuration> getDon()
  {
    return m_don;
  }

  /**
   * Get the combined duration for donning of the item, including values of
   * base wearable.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDuration> getCombinedDon()
  {
    if(m_don.isPresent())
      return new Combination.Addable<NewDuration>(m_item, m_don.get());

    List<Combination<NewDuration>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseWearable wearable = ((BaseItem)entry).getWearable();
        combinations.add(wearable.getCombinedDon());
      }

    return new Combination.Addable<NewDuration>(m_item, combinations);
  }

  /**
   * Get the duration for donning the item hastily.
   *
   * @return      the don hastily duration
   */
  public Optional<NewDuration> getDonHastily()
  {
    return m_donHastily;
  }

  /**
   * Get the combined duration for donning of the item hastily, including
   * values of base wearable.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDuration> getCombinedDonHastily()
  {
    if(m_donHastily.isPresent())
      return new Combination.Addable<NewDuration>(m_item, m_donHastily.get());

    List<Combination<NewDuration>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseWearable wearable = ((BaseItem)entry).getWearable();
        combinations.add(wearable.getCombinedDonHastily());
      }

    return new Combination.Addable<NewDuration>(m_item, combinations);
  }

  /**
   * Get the duration for removing the item.
   *
   * @return      the remove duration
   */
  public Optional<NewDuration> getRemove()
  {
    return m_remove;
  }

  /**
   * Get the combined duration for rewmoving of the item, including values of
   * base wearable.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDuration> getCombinedRemove()
  {
    if(m_remove.isPresent())
      return new Combination.Addable<NewDuration>(m_item, m_remove.get());

    List<Combination<NewDuration>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseWearable wearable = ((BaseItem)entry).getWearable();
        combinations.add(wearable.getCombinedRemove());
      }

    return new Combination.Addable<NewDuration>(m_item, combinations);
  }

  @Override
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

    if(m_don.isPresent())
      values.put(Index.Path.DONS, m_don.get().toString());
    if(m_donHastily.isPresent())
      values.put(Index.Path.DONS, m_donHastily.get().toString());

    values.put(Index.Path.SLOTS, m_slot.getName());
    if(m_remove.isPresent())
      values.put(Index.Path.REMOVES, m_remove.get().toString());

    return values;
  }

  @Override
  public Message toProto()
  {
    BaseWearableProto.Builder builder = BaseWearableProto.newBuilder();

    if(m_slot != Slot.UNKNOWN)
      builder.setSlot(m_slot.toProto());
    if(m_don.isPresent())
      builder.setWear(m_don.get().toProto());
    if(m_donHastily.isPresent())
      builder.setWearHastily(m_donHastily.get().toProto());
    if(m_remove.isPresent())
      builder.setRemove(m_remove.get().toProto());

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
      m_slot = Slot.fromProto(proto.getSlot());

    if(proto.hasWear())
      m_don = Optional.of(NewDuration.fromProto(proto.getWear()));

    if(proto.hasWearHastily())
      m_donHastily = Optional.of(NewDuration.fromProto(proto.getWearHastily()));

    if(proto.hasRemove())
      m_remove = Optional.of(NewDuration.fromProto(proto.getRemove()));
  }

  @Override
  public <T extends AbstractEntry> AbstractType<T> getType()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getEditType()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public AbstractEntry getEntry()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getName()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getID()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void changed(boolean inChanged)
  {
    // TODO Auto-generated method stub
  }

  /**
   * Check whether the relevant wearable values are defined.
   *
   * @return true if values are there, false if not
   */
  public boolean hasValues()
  {
    return m_slot != Slot.UNKNOWN;
  }

  @Override
  public void set(Values inValues)
  {
    m_slot = inValues.use("wearable.slot", m_slot,
                          new NewValue.Parser<Slot>(1)
   {
      @Override
      public Optional<Slot> doParse(String inValue)
      {
        return Slot.fromString(inValue);
      }
    });
    m_don = inValues.use("wearable.don", m_don, NewDuration.PARSER);
    m_donHastily = inValues.use("wearable.don_hastily", m_donHastily,
                                NewDuration.PARSER);
    m_remove = inValues.use("wearable.remove", m_remove, NewDuration.PARSER);
  }
}
