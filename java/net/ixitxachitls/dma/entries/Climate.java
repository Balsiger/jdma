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

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.NewValue;

/** The possible climates in the game. */
@ParametersAreNonnullByDefault
public enum Climate implements EnumSelection.Named,
  EnumSelection.Proto<BaseMonsterProto.Climate>
{
  UNKNOWN("Unknown", BaseMonsterProto.Climate.UNKNOWN_CLIMATE),

  /** Warm climate. */
  WARM("Warm", BaseMonsterProto.Climate.WARM),

  /** Cold climate. */
  COLD("cold", BaseMonsterProto.Climate.COLD_CLIMATE),

  /** Any climate. */
  ANY("Any", BaseMonsterProto.Climate.ANY),

  /** Temparete climate. */
  TEMPERATE("Temperate", BaseMonsterProto.Climate.TEMPERATE);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private BaseMonsterProto.Climate m_proto;

  /** The parser for armor types. */
  public static final NewValue.Parser<Climate> PARSER =
    new NewValue.Parser<Climate>(1)
    {
      @Override
      public Optional<Climate> doParse(String inValue)
      {
        return Climate.fromString(inValue);
      }
    };

  /** Create the enum value.
   *
   * @param inName  the name of the value
   * @param inProto the proto enum value
   */
  private Climate(String inName, BaseMonsterProto.Climate inProto)
  {
    m_name = BaseMonster.constant("climate", inName);
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
  public net.ixitxachitls.dma.proto.Entries.BaseMonsterProto.Climate
    toProto()
  {
    return m_proto;
  }


  /**
   * Create a enum value from a given proto.
   *
   * @param inProto the proto to convert
   * @return the corresponding enum value
   */
  public static Climate fromProto(BaseMonsterProto.Climate inProto)
  {
    for(Climate climate : values())
      if(climate.m_proto == inProto)
        return climate;

    throw new IllegalArgumentException("cannot convert climate: "
                                       + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<Climate> fromString(String inValue)
  {
    for(Climate climate : values())
      if(climate.getName().equalsIgnoreCase(inValue))
        return Optional.of(climate);

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
    for(Climate climate : values())
      names.add(climate.getName());

    return names;
  }
}