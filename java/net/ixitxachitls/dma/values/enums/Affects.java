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
import net.ixitxachitls.dma.values.Parser;

/** The possible affects in the game. */
public enum Affects implements Named, net.ixitxachitls.dma.values.enums.Short
{
  /** Unknown. */
  UNKNOWN("Unknown", "Unk", BaseQualityProto.Effect.Affects.UNKNOWN),

  /** The fortitude save. */
  FORTITUDE_SAVE("Fortitude Save", "Fort",
                 BaseQualityProto.Effect.Affects.FORTITUDE_SAVE),

  /** The reflex save. */
  REFLEX_SAVE("Reflex Save", "Ref",
              BaseQualityProto.Effect.Affects.REFLEX_SAVE),

  /** The will save. */
  WILL_SAVE("Will Save", "Will", BaseQualityProto.Effect.Affects.WILL_SAVE),

  /** The skill. */
  SKILL("Skill", "Skill", BaseQualityProto.Effect.Affects.SKILL),

  /** A grapple modifier. */
  GRAPPLE("Grapple", "Grp", BaseQualityProto.Effect.Affects.GRAPPLE),

  /** An initiative modifier. */
  INIT("Initiative", "Init", BaseQualityProto.Effect.Affects.INIT),

  /** A modifier to the armor class. */
  AC("Armor Class", "AC", BaseQualityProto.Effect.Affects.AC),

  /** A modifier to the attack roll. */
  ATTACK("Attack", "Atk", BaseQualityProto.Effect.Affects.ATTACK),

  /** A modifier to damage. */
  DAMAGE("Damage", "Dmg", BaseQualityProto.Effect.Affects.DAMAGE),

  /** A modifier to Speed. */
  SPEED("Speed", "Spd", BaseQualityProto.Effect.Affects.SPEED),

  /** A modifier to the hit points. */
  HP("Hit Points", "HP", BaseQualityProto.Effect.Affects.HP),

  /** A modifier to strength. */
  STRENGTH("Strength", "Str", BaseQualityProto.Effect.Affects.STRENGTH),

  /** A modifier to dexterity. */
  DEXTERITY("Dexterity", "Dex", BaseQualityProto.Effect.Affects.DEXTERITY),

  /** A modifier to constitution. */
  CONSTITUTION("Constitution", "Con",
               BaseQualityProto.Effect.Affects.CONSTITUTION),

  /** A modifier to intelligence. */
  INTELLIGENCE("Intelligence", "Int",
               BaseQualityProto.Effect.Affects.INTELLIGENCE),

  /** A modifier to wisdom. */
  WISDOM("Wisdom", "Wis", BaseQualityProto.Effect.Affects.WISDOM),

  /** A modifier to strength. */
  CHARISMA("Charisma", "Cha", BaseQualityProto.Effect.Affects.CHARISMA);

  /** The value's name. */
  private final String m_name;

  /** The value's short name. */
  private final String m_short;

  /** The proto enum value. */
  private final BaseQualityProto.Effect.Affects m_proto;

  /** The parser for affects. */
  public static final Parser<Affects> PARSER =
    new Parser<Affects>(1)
    {
      @Override
      public Optional<Affects> doParse(String inValue)
      {
        return Affects.fromString(inValue);
      }
    };

  /** Create the name.
   *
   * @param inName      the name of the value
   * @param inShort     the short name of the value
   * @param inProto     the prot enum value
   */
  private Affects(String inName, String inShort,
                  BaseQualityProto.Effect.Affects inProto)
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
  public BaseQualityProto.Effect.Affects toProto()
  {
    return m_proto;
  }

  /**
   * Get the group matching the given proto value.
   *
   * @param  inProto     the proto value to look for
   * @return the matched enum (will throw exception if not found)
   */
  public static Affects fromProto(BaseQualityProto.Effect.Affects inProto)
  {
    for(Affects affects : values())
      if(affects.m_proto == inProto)
        return affects;

    throw new IllegalStateException("invalid proto affects: " + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<Affects> fromString(String inValue)
  {
    for(Affects type : values())
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
    for(Affects type : values())
      names.add(type.getName());

    return names;
  }
}