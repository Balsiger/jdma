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


package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Entries.BaseFeatProto;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.NewValue;

/** The possible feat types to affect (cf. PHB 175). */
public enum FeatType
  implements EnumSelection.Named, EnumSelection.Proto<BaseFeatProto.Type>
{
  /** Unknown type. */
  UNKNOWN("Unknown", BaseFeatProto.Type.UNKNOWN),

  /** A general feat. */
  GENERAL("General", BaseFeatProto.Type.GENERAL),

  /** An item creation feat. */
  ITEM_CREATION("Item Creation", BaseFeatProto.Type.ITEM_CREATION),

  /** A metamagic feat. */
  METAMAGIC("Metamagic", BaseFeatProto.Type.METAMAGIC),

  /** A regional feat. */
  REGIONAL("Regional", BaseFeatProto.Type.REGIONAL),

  /** A special feat. */
  SPECIAL("Special", BaseFeatProto.Type.SPECIAL),

  /** A fighter feat. */
  FIGHTER("Fighter", BaseFeatProto.Type.FIGHTER);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private BaseFeatProto.Type m_proto;

  /** The parser for feat types. */
  public static final NewValue.Parser<FeatType> PARSER =
    new NewValue.Parser<FeatType>(1)
    {
      @Override
      public Optional<FeatType> doParse(String inValue)
      {
        return FeatType.fromString(inValue);
      }
    };

    /** Create the name.
   *
   * @param inName     the name of the value
   * @param inProto    the proto enum value
   */
  private FeatType(String inName, BaseFeatProto.Type inProto)
  {
    m_name = BaseFeat.constant("feat.type", inName);
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
  public BaseFeatProto.Type toProto()
  {
    return m_proto;
  }

  /**
   * Get the group matching the given proto value.
   *
   * @param  inProto     the proto value to look for
   * @return the matched enum (will throw exception if not found)
   */
  public static FeatType fromProto(BaseFeatProto.Type inProto)
  {
    for(FeatType type: values())
      if(type.m_proto == inProto)
        return type;

    throw new IllegalStateException("invalid proto type: " + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<FeatType> fromString(String inValue)
  {
    for(FeatType type : values())
      if(type.getName().equalsIgnoreCase(inValue))
        return Optional.of(type);

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
    for(FeatType type : values())
      names.add(type.getName());

    return names;
  }
}