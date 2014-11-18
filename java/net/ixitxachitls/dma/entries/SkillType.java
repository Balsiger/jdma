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

import net.ixitxachitls.dma.proto.Values.SharedProto;
import net.ixitxachitls.dma.proto.Values.SharedProto.SkillSubtype;
import net.ixitxachitls.dma.values.NewValue;
import net.ixitxachitls.dma.values.enums.Named;
import net.ixitxachitls.dma.values.enums.Proto;

/** The possible sizes in the game. */
public enum SkillType implements Named,
    Proto<SkillSubtype>
{
  /** Unknown type. */
  UNKNOWN("Unknown", SharedProto.SkillSubtype.UNKNOWN_SKILL_SUBTYPE),

  /** Drow religion. */
  DROW_RELIGION("Drow Religion", SharedProto.SkillSubtype.DROW_RELIGION),

  /** Religion. */
  RELIGION("Religion", SharedProto.SkillSubtype.RELIGION),

  /** Arcana. */
  ARCANA("Arcana", SharedProto.SkillSubtype.ARCANA),

  /** Alchemy. */
  ALCHEMY("Alchemy", SharedProto.SkillSubtype.ALCHEMY),

  /** Any sub type. */
  ANY_ONE("Any One", SharedProto.SkillSubtype.ANY_ONE);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private SharedProto.SkillSubtype m_proto;

  /** The parser for armor types. */
  public static final NewValue.Parser<SkillType> PARSER =
    new NewValue.Parser<SkillType>(1)
    {
      @Override
      public Optional<SkillType> doParse(String inValue)
      {
        return SkillType.fromString(inValue);
      }
    };

  /** Create the name.
   *
   * @param inName       the name of the value
   * @param inProto      the proto enum value
   */
  private SkillType(String inName, SharedProto.SkillSubtype inProto)
  {
    m_name = BaseSkill.constant("skill.subtype", inName);
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
  public SkillSubtype toProto()
  {
    return m_proto;
  }

  /**
   * Convert the given proto to an enum value.
   *
   * @param inProto the proto value to convert
   * @return the corresponding enum value
   */
  public static SkillType fromProto(SharedProto.SkillSubtype inProto)
  {
    for(SkillType subtype : values())
      if(subtype.m_proto == inProto)
        return subtype;

    throw new IllegalArgumentException("cannot convert skill subtype: "
                                       + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<SkillType> fromString(String inValue)
  {
    for(SkillType type : values())
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
    for(SkillType type : values())
      names.add(type.getName());

    return names;
  }
}