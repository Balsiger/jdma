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

import net.ixitxachitls.dma.proto.Values;
import net.ixitxachitls.dma.values.Parser;

/**
 * The possible sizes in the game.
 *
 * @file Ability.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public enum Ability implements Named, Short
{
  /** Unknown.*/
  UNKNOWN("Unknown", "Unk", Values.Ability.UNKNOWN),

  /** Strength. */
  STRENGTH("Strength", "Str", Values.Ability.STRENGTH),

  /** Dexterity. */
  DEXTERITY("Dexterity", "Dex", Values.Ability.DEXTERITY),

  /** Constitution. */
  CONSTITUTION("Constitution", "Con", Values.Ability.CONSTITUTION),

  /** Intelligence. */
  INTELLIGENCE("Intelligence", "Int", Values.Ability.INTELLIGENCE),

  /** Wisdom. */
  WISDOM("Wisdom", "Wis", Values.Ability.WISDOM),

  /** Charisma. */
  CHARISMA("Charisma", "Cha", Values.Ability.CHARISMA),

  /** No ability. */
  NONE("None", "-", Values.Ability.NONE);

  /** The value's name. */
  private String m_name;

  /** The value's short name. */
  private String m_short;

  /** The proto enum value. */
  private Values.Ability m_proto;

  /** The parser for abilities. */
  public static final Parser<Ability> PARSER =
    new Parser<Ability>(1)
    {
      @Override
      public Optional<Ability> doParse(String inValue)
      {
        return Ability.fromString(inValue);
      }
    };

  /** Create the name.
   *
   * @param inName       the name of the value
   * @param inShort      the short name of the value
   * @param inProto      the proto enum value
   *
   */
  private Ability(String inName, String inShort,
                  Values.Ability inProto)
  {
    m_name = inName;
    m_short = inShort;
    m_proto = inProto;
  }

  /** Get the name of the value.
   *
   * @return the name of the value
   *
   */
  @Override
  public String getName()
  {
    return m_name;
  }

  /** Get the name of the value.
   *
   * @return the short name of the value
   *
   */
  @Override
  public String getShort()
  {
    return m_short;
  }

  /** Get the save as string.
   *
   * @return the name of the value
   *
   */
  @Override
  public String toString()
  {
    return m_name;
  }

  /**
   * Get the proto value for this value.
   *
   * @return the proto enum value
   */
  public Values.Ability toProto()
  {
    return m_proto;
  }

  /**
   * Get the group matching the given proto value.
   *
   * @param  inProto     the proto value to look for
   * @return the matched enum (will throw exception if not found)
   */
  public static Ability fromProto(Values.Ability inProto)
  {
    for(Ability ability : values())
      if(ability.m_proto == inProto)
        return ability;

    throw new IllegalStateException("invalid proto ability: " + inProto);
  }

 /**
   * All the possible names for the layout.
   *
   * @return the possible names
   */
  public static List<String> names()
  {
    List<String> names = new ArrayList<>();

    for(Ability ability : values())
      names.add(ability.getName());

    return names;
  }

  public static List<String> allNames()
  {
    List<String> names = new ArrayList<>();

    for(Ability ability : values())
    {
      names.add(ability.getName());
      names.add(ability.getShort());
    }

    return names;
  }

  /**
   * Get the layout matching the given text.
   *
   * @param inText the text to get the ability for
   * @return the ability for the text, if any
   */
  public static Optional<Ability> fromString(String inText)
  {
    for(Ability ability : values())
      if(ability.m_name.equalsIgnoreCase(inText)
         || ability.m_short.equalsIgnoreCase(inText))
        return Optional.of(ability);

    return Optional.absent();
  }
}
