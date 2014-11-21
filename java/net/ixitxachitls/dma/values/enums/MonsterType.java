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
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto;
import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto.Type;
import net.ixitxachitls.dma.values.Value;

/** The possible monster types in the game. */
@ParametersAreNonnullByDefault
public enum MonsterType implements Named,
    Proto<Type>
{
  UNKNOWN("Unknown", BaseMonsterProto.Type.UNKNOWN_TYPE),

  /** Aberration. */
  ABERRATION("Aberration", BaseMonsterProto.Type.ABERRATION),

  /** Animal. */
  ANIMAL("Animal", BaseMonsterProto.Type.ANIMAL),

  /** Construct. */
  CONSTRUCT("Construct", BaseMonsterProto.Type.CONSTRUCT),

  /** Dragon. */
  DRAGON("Dragon", BaseMonsterProto.Type.DRAGON),

  /** Elemental. */
  ELEMENTAL("Elemental", BaseMonsterProto.Type.ELEMENTAL),

  /** Fey. */
  FEY("Fey", BaseMonsterProto.Type.FEY),

  /** Giant. */
  GIANT("Giant", BaseMonsterProto.Type.GIANT),

  /** Humanoid. */
  HUMANOID("Humanoid", BaseMonsterProto.Type.HUMANOID),

  /** Magical Beast. */
  MAGICAL_BEAST("Magical Beast", BaseMonsterProto.Type.MAGICAL_BEAST),

  /** Monstrous Humanoid. */
  MONSTROUS_HUMANOID("Monstrous Humanoid",
                     BaseMonsterProto.Type.MONSTROUS_HUMANOID),

  /** Ooze. */
  OOZE("Ooze", BaseMonsterProto.Type.OOZE),

  /** Outsider. */
  OUTSIDER("Outsider", BaseMonsterProto.Type.OUTSIDER),

  /** Plant. */
  PLANT("Plant", BaseMonsterProto.Type.PLANT),

  /** Undead. */
  UNDEAD("Undead", BaseMonsterProto.Type.UNDEAD),

  /** Vermin. */
  VERMIN("Vermin", BaseMonsterProto.Type.VERMIN);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private BaseMonsterProto.Type m_proto;

  /** The parser for armor types. */
  public static final Value.Parser<MonsterType> PARSER =
    new Value.Parser<MonsterType>(1)
    {
      @Override
      public Optional<MonsterType> doParse(String inValue)
      {
        return MonsterType.fromString(inValue);
      }
    };

  /**
   * Create the enum value.
   *
   * @param inName  the name of the value
   * @param inProto the proto enum value
   */
  private MonsterType(String inName, BaseMonsterProto.Type inProto)
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
  public Type toProto()
  {
    return m_proto;
  }

  /**
   * Get the monster type corresponding to the given proto enum value.
   *
   * @param inProto the proto value to get for
   * @return the corresponding enum value
   */
  public static MonsterType fromProto(BaseMonsterProto.Type inProto)
  {
    for(MonsterType type : values())
      if(type.m_proto == inProto)
        return type;

    throw new IllegalArgumentException("cannot convert monster type:"
      + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<MonsterType> fromString(String inValue)
  {
    for(MonsterType type : values())
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
    for(MonsterType type : values())
      names.add(type.getName());

    return names;
  }
}