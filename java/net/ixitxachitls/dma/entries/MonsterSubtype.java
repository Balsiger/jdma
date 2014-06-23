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
import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto.Subtype;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.NewValue;

/** The possible monster sub types in the game. */
@ParametersAreNonnullByDefault
public enum MonsterSubtype implements EnumSelection.Named,
  EnumSelection.Proto<BaseMonsterProto.Subtype>
{
  UNKNOWN("Unknown", BaseMonsterProto.Subtype.UNKNOWN_SUBTYPE),

  /** None. */
  NONE("None", BaseMonsterProto.Subtype.NONE_SUBTYPE),

  /** Air. */
  AIR("Air", BaseMonsterProto.Subtype.AIR),

  /** Aquatic. */
  AQUATIC("Aquatic", BaseMonsterProto.Subtype.AQUATIC),

  /** Archon. */
  ARCHON("Archon", BaseMonsterProto.Subtype.ARCHON),

  /** Augmented. */
  AUGMENTED("Augmented", BaseMonsterProto.Subtype.AUGMENTED),

  /** Baatezu. */
  BAATEZU("Baatezu", BaseMonsterProto.Subtype.BAATEZU),

  /** Chaotic. */
  CHAOTIC("Chaotic", BaseMonsterProto.Subtype.CHAOTIC),

  /** Cold. */
  COLD("Cold", BaseMonsterProto.Subtype.COLD),

  /** Earth. */
  EARTH("Earth", BaseMonsterProto.Subtype.EARTH),

  /** Eladrin. */
  ELADRIN("Eladrin", BaseMonsterProto.Subtype.ELADRIN),

  /** Elf. */
  ELF("Elf", BaseMonsterProto.Subtype.ELF),

  /** Evil. */
  EVIL("Evil", BaseMonsterProto.Subtype.EVIL),

  /** Extraplanar. */
  EXTRAPLANAR("Extraplanar", BaseMonsterProto.Subtype.EXTRAPLANAR),

  /** Fire. */
  FIRE("Fire", BaseMonsterProto.Subtype.FIRE),

  /** Goblinoid. */
  GOBLINOID("Goblinoid", BaseMonsterProto.Subtype.GOBLINOID),

  /** Good. */
  GOOD("Good", BaseMonsterProto.Subtype.GOOD),

  /** Guardinal. */
  GUARDINAL("Guardinal", BaseMonsterProto.Subtype.GUARDINAL),

  /** Human. */
  HUMAN("Human", BaseMonsterProto.Subtype.HUMAN),

  /** Incorporeal. */
  INCORPOREAL("Incorporeal", BaseMonsterProto.Subtype.INCORPOREAL),

  /** Lawful. */
  LAWFUL("Lawful", BaseMonsterProto.Subtype.LAEFUL),

  /** Native. */
  NATIVE("Native", BaseMonsterProto.Subtype.NATIVE),

  /** Orc. */
  ORC("Orc", BaseMonsterProto.Subtype.ORC),

  /** Reptilian. */
  REPTILIAN("Reptilian", BaseMonsterProto.Subtype.REPTILIAN),

  /** Shapechanger. */
  SHAPECHANGER("Shapechanger", BaseMonsterProto.Subtype.SHAPECHANGER),

  /** Swarm. */
  SWARM("Swarm", BaseMonsterProto.Subtype.SWARM),

  /** Water. */
  WATER("Water", BaseMonsterProto.Subtype.WATER);

  /** The value's name. */
  private String m_name;

  /** The corresponding proto enum value. */
  private BaseMonsterProto.Subtype m_proto;

  /** The parser for armor types. */
  public static final NewValue.Parser<MonsterSubtype> PARSER =
    new NewValue.Parser<MonsterSubtype>(1)
    {
      @Override
      public Optional<MonsterSubtype> doParse(String inValue)
      {
        return MonsterSubtype.fromString(inValue);
      }
    };

    /**
   * Create the enum value.
   *
   * @param inName the name of the value
   * @param inProto the corresponding proto value
   */
  private MonsterSubtype(String inName, BaseMonsterProto.Subtype inProto)
  {
    m_name = BaseMonster.constant("monster.type", inName);
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
  public Subtype toProto()
  {
    return m_proto;
  }

  /**
   * Get the subtype corresponding to the given proto value.
   *
   * @param inProto the proto value to get for
   * @return the corresponding subtype
   */
  public static MonsterSubtype fromProto(BaseMonsterProto.Subtype inProto)
  {
    for(MonsterSubtype type : values())
      if(type.m_proto == inProto)
        return type;

    throw new IllegalArgumentException("cannot convert monster subtype: "
                                       + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<MonsterSubtype> fromString(String inValue)
  {
    for(MonsterSubtype type : values())
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
    for(MonsterSubtype type : values())
      names.add(type.getName());

    return names;
  }
}