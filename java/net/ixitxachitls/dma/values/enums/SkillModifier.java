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

import net.ixitxachitls.dma.proto.Entries.BaseSkillProto;
import net.ixitxachitls.dma.values.Parser;

/**
 * The possible sizes in the game.
 *
 * @file SkillModifier.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public enum SkillModifier
  implements Named, Proto<BaseSkillProto.Modifier>
{
  /** Unknown. */
  UNKNOWN("Unknown", BaseSkillProto.Modifier.UNKNOWN_MODIFIER),

  /** The skill is modified by a creatures speed. */
  SPEED("Speed", BaseSkillProto.Modifier.SPEED),

  /** The skill is modified by a creatures size. */
  SIZE("Size", BaseSkillProto.Modifier.SIZE);

  /** The value's name. */
  private String m_name;

  /** The prot enum value. */
  private BaseSkillProto.Modifier m_proto;

  /** The parser for skill modifiers. */
  public static final Parser<SkillModifier> PARSER =
    new Parser<SkillModifier>(1)
    {
      @Override
      public Optional<SkillModifier> doParse(String inValue)
      {
        return SkillModifier.fromString(inValue);
      }
    };

  /** Create the name.
   *
   * @param inName      the name of the value
   * @param inProto     the prot enum value
   */
  private SkillModifier(String inName, BaseSkillProto.Modifier inProto)
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
  public BaseSkillProto.Modifier toProto()
  {
    return m_proto;
  }

  /**
   * Get the group matching the given proto value.
   *
   * @param  inProto     the proto value to look for
   * @return the matched enum (will throw exception if not found)
   */
  public static SkillModifier fromProto(BaseSkillProto.Modifier inProto)
  {
    for(SkillModifier modifier: values())
      if(modifier.m_proto == inProto)
        return modifier;

    throw new IllegalStateException("invalid proto modifier: " + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<SkillModifier> fromString(String inValue)
  {
    for(SkillModifier modifier : values())
      if(modifier.getName().equalsIgnoreCase(inValue))
        return Optional.of(modifier);

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
    for(SkillModifier modifier : values())
      names.add(modifier.getName());

    return names;
  }
}
