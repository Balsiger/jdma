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

import net.ixitxachitls.dma.entries.BaseSkill;
import net.ixitxachitls.dma.proto.Entries.BaseSkillProto;
import net.ixitxachitls.dma.values.Value;

/** The possible sizes in the game. */
public enum SkillRestriction implements Named,
    Proto<BaseSkillProto.Restriction>
{
  /** Unknown. */
  UNKNOWN("Unknown", BaseSkillProto.Restriction.UNKNOWN),

  /** Trained only. */
  TRAINED_ONLY("Trained Only", BaseSkillProto.Restriction.TRAINED_ONLY),

  /** Armor check penalty. */
  ARMOR_CHECK_PENALTY("Armor Check Penalty",
                      BaseSkillProto.Restriction.ARMOR_CHECK_PENALTY),

  /** Armor check penalty. */
  SUBTYPE_ONLY("Subtype Only", BaseSkillProto.Restriction.SUBTYPE_ONLY);

  /** The value's name. */
  private String m_name;

  /** The prot enum value. */
  private BaseSkillProto.Restriction m_proto;

  /** The parser for armor types. */
  public static final Value.Parser<SkillRestriction> PARSER =
    new Value.Parser<SkillRestriction>(1)
    {
      @Override
      public Optional<SkillRestriction> doParse(String inValue)
      {
        return SkillRestriction.fromString(inValue);
      }
    };

  /** Create the name.
   *
   * @param inName       the name of the value
   * @param inProto      the proto enum value
   */
  private SkillRestriction(String inName, BaseSkillProto.Restriction inProto)
  {
    m_name = BaseSkill.constant("skill.restrictions", inName);
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
  public BaseSkillProto.Restriction toProto()
  {
    return m_proto;
  }

  /**
   * Get the group matching the given proto value.
   *
   * @param  inProto     the proto value to look for
   * @return the matched enum (will throw exception if not found)
   */
  public static SkillRestriction fromProto(BaseSkillProto.Restriction inProto)
  {
    for(SkillRestriction restriction : values())
      if(restriction.m_proto == inProto)
        return restriction;

    throw new IllegalStateException("invalid proto restriction: " + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<SkillRestriction> fromString(String inValue)
  {
    for(SkillRestriction restriction : values())
      if(restriction.getName().equalsIgnoreCase(inValue))
        return Optional.of(restriction);

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
    for(SkillRestriction restriction : values())
      names.add(restriction.getName());

    return names;
  }
}