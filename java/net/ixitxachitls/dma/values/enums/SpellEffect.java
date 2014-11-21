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

import net.ixitxachitls.dma.proto.Entries.BaseSpellProto;
import net.ixitxachitls.dma.values.Value;

/** The possible spell ranges. */
public enum SpellEffect implements Named,
    Proto<BaseSpellProto.Effect.Type>
{
  /** Unknown. */
  UNKNOWN("Unknown", BaseSpellProto.Effect.Type.UNKNOWN),

  /** Ray. */
  RAY("Ray", BaseSpellProto.Effect.Type.RAY),

  /** Spread. */
  SPREAD("Spread", BaseSpellProto.Effect.Type.SPREAD);

  /** The value's name. */
  private final String m_name;

  /** The proto enum value. */
  private final BaseSpellProto.Effect.Type m_proto;

  /** The parser for armor types. */
  public static final Value.Parser<SpellEffect> PARSER =
    new Value.Parser<SpellEffect>(1)
    {
      @Override
      public Optional<SpellEffect> doParse(String inValue)
      {
        return SpellEffect.fromString(inValue);
      }
    };

  /**
   * Create the name.
   *
   * @param inName       the name of the value
   * @param inProto      the proto enum value
   */
  private SpellEffect(String inName, BaseSpellProto.Effect.Type inProto)
  {
    m_name = inName;
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
  public BaseSpellProto.Effect.Type toProto()
  {
    return m_proto;
  }

  /**
   * Get the group matching the given proto value.
   *
   * @param  inProto     the proto value to look for
   * @return the matched enum (will throw exception if not found)
   */
  public static SpellEffect fromProto(BaseSpellProto.Effect.Type inProto)
  {
    for(SpellEffect effect : values())
      if(effect.m_proto == inProto)
        return effect;

    throw new IllegalStateException("invalid proto effect: " + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<SpellEffect> fromString(String inValue)
  {
    for(SpellEffect effect : values())
      if(effect.getName().equalsIgnoreCase(inValue))
        return Optional.of(effect);

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
    for(SpellEffect effect : values())
      names.add(effect.getName());

    return names;
  }
}