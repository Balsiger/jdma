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


package net.ixitxachitls.dma.values;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Entries.BaseMagicProto;
import net.ixitxachitls.dma.values.enums.*;

/** The possible modifier types. */
public enum ModifierType implements Named, net.ixitxachitls.dma.values.enums.Short,
    Proto<BaseMagicProto.Type>
{
  /** The unknown armor type. */
  UNKNOWN("Unknown", "U", BaseMagicProto.Type.UNKNOWN),
  /** Modifier on strength. */
  STRENGTH("Strength", "Str", BaseMagicProto.Type.STRENGTH),
  /** Modifier on dexterity. */
  DEXTERITY("Dexterity", "Dex", BaseMagicProto.Type.DEXTERITY),
  /** Modifier on constitution. */
  CONSTITUTION("Constitution", "Con", BaseMagicProto.Type.CONSTITUTION),
  /** Modifier on intelligence. */
  INTELLIGENCE("Intelligence", "Int", BaseMagicProto.Type.INTELLIGENCE),
  /** Modifier on wisdom. */
  WISDOM("Wisdom", "Wis", BaseMagicProto.Type.WISDOM),
  /** Modifier on charisma. */
  CHARISMA("Charisma", "Chr", BaseMagicProto.Type.CHARISMA),
  /** Modifier on attacks. */
  ATTACK("Attack", "Atk", BaseMagicProto.Type.ATTACK),
  /** Modifier on hide skills. */
  HIDE("Hide", "Hide", BaseMagicProto.Type.HIDE),
  /** Modifier on move silenty skills. */
  MOVE_SILENTLY("Move Silently", "Move Silently",
                BaseMagicProto.Type.MOVE_SILENTLY),
  /** Modifier on armor class. */
  ARMOR_CLASS("Armor Class", "AC", BaseMagicProto.Type.ARMOR_CLASS),
  /** Modifier on damage. */
  DAMAGE("Damage", "Dmg", BaseMagicProto.Type.DAMAGE);

  /** The value's name. */
  private String m_name;

  /** The value's short name. */
  private String m_short;

  /** The proto enum value. */
  private BaseMagicProto.Type m_proto;

  /** The parser for armor types. */
  public static final Value.Parser<ModifierType> PARSER =
    new Value.Parser<ModifierType>(1)
    {
      @Override
      public Optional<ModifierType> doParse(String inValue)
      {
        return ModifierType.fromString(inValue);
      }
    };

  /** Create the name.
   *
   * @param inName     the name of the value
   * @param inShort    the short name of the value
   * @param inProto    the prot enum value
   */
  private ModifierType(String inName, String inShort,
                       BaseMagicProto.Type inProto)
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
  public String getShort()
  {
    return m_short;
  }

  @Override
  public String toString()
  {
    return m_name;
  }

  @Override
  public BaseMagicProto.Type toProto()
  {
    return m_proto;
  }

  /**
   * Convert the given proto into the corresponding enum value.
   *
   * @param inProto  the proto value to convert
   * @return  the converted enum value
   */
  public static ModifierType fromProto(BaseMagicProto.Type inProto)
  {
    for(ModifierType type : values())
      if(type.m_proto == inProto)
        return type;

    throw new IllegalArgumentException("cannot convert armor type: "
      + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<ModifierType> fromString(String inValue)
  {
    for(ModifierType type : values())
      if(type.getName().equalsIgnoreCase(inValue))
        return Optional.of(type);

    System.out.println("cannot parse modifier value: " + inValue);
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
    for(ModifierType type : values())
      names.add(type.getName());

    return names;
  }
}