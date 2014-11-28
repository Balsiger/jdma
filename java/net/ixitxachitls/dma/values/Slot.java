/******************************************************************************
 * Copyright (c) 2002-2013 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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


package net.ixitxachitls.dma.values;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Entries.BaseWearableProto;
import net.ixitxachitls.dma.values.enums.Named;
import net.ixitxachitls.dma.values.enums.Proto;

/** The available body slots (cf. ). */
public enum Slot implements Named, net.ixitxachitls.dma.values.enums.Short,
    Proto<BaseWearableProto.Slot>
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

  /** THe parser for slots. */
  public static final Parser<Slot> PARSER =
    new Parser<Slot>(1)
    {
      @Override
      public Optional<Slot> doParse(String inValue)
      {
        return Slot.fromString(inValue);
      }
    };

  /**
   * Create the name.
   *
   * @param inName     the name of the value
   * @param inShort    the short name of the value
   * @param inProto    the prot enum value
   */
  private Slot(String inName, String inShort, BaseWearableProto.Slot inProto)
  {
    m_name = inName;
    m_short = inShort;
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