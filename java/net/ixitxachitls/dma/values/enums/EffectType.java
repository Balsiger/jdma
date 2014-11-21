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

package net.ixitxachitls.dma.values.enums;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Entries.BaseQualityProto;
import net.ixitxachitls.dma.values.Value;

/** The possible spell components (cf. PHB 174). */
public enum EffectType implements Named, net.ixitxachitls.dma.values.enums.Short
{
  /** Unknown. */
  UNKNOWN("Unknown", "Un", BaseQualityProto.Type.UNKNOWN),

  /** Extraordinary effects. */
  EXTRAORDINARY("Extraordinary", "Ex", BaseQualityProto.Type.EXTRAORDINARY),

  /** Spell like effects. */
  SPELL_LIKE("Spell-like", "Sp", BaseQualityProto.Type.SPELL_LIKE),

  /** Supernatural effects. */
  SUPERNATURAL("Supernatural", "Su", BaseQualityProto.Type.SUPERNATURAL);

  /** The value's name. */
  private String m_name;

  /** The value's short name. */
  private String m_short;

  /** The enum proto value. */
  private BaseQualityProto.Type m_proto;

  /** The parser for armor types. */
  public static final Value.Parser<EffectType> PARSER =
    new Value.Parser<EffectType>(1)
    {
      @Override
      public Optional<EffectType> doParse(String inValue)
      {
        return EffectType.fromString(inValue);
      }
    };

  /** Create the effect type.
   *
   * @param inName      the name of the value
   * @param inShort     the short name of the value
   * @param inProto     the proto enum value
   */
  private EffectType(String inName, String inShort,
                     BaseQualityProto.Type inProto)
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

  /**
   * Get the proto value for this value.
   *
   * @return the proto enum value
   */
  public BaseQualityProto.Type toProto()
  {
    return m_proto;
  }

  /**
   * Get the group matching the given proto value.
   *
   * @param  inProto     the proto value to look for
   * @return the matched enum (will throw exception if not found)
   */
  public static EffectType fromProto(BaseQualityProto.Type inProto)
  {
    for(EffectType type : values())
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
  public static Optional<EffectType> fromString(String inValue)
  {
    for(EffectType type : values())
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
    for(EffectType type : values())
      names.add(type.getName());

    return names;
  }
}