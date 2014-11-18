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

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.proto.Entries.BaseWeaponProto;
import net.ixitxachitls.dma.values.enums.Named;
import net.ixitxachitls.dma.values.enums.Proto;

/** The possible weapon proficiencies. */
public enum Proficiency implements Named,
    Proto<BaseWeaponProto.Proficiency>
{
  /** An unknown proficiency. */
  UNKNOWN("Unknown", BaseWeaponProto.Proficiency.UNKNOWN_PROFICIENCY),

  /** Proficiency for simple weapons. */
  SIMPLE("Simple", BaseWeaponProto.Proficiency.SIMPLE),

  /** Proficiency for simple weapons. */
  MARTIAL("Martial", BaseWeaponProto.Proficiency.MARTIAL),

  /** Proficiency for simple weapons. */
  EXOTIC("Exotic", BaseWeaponProto.Proficiency.EXOCTIC),

  /** Proficiency for simple weapons. */
  IMPROVISED("Improvised", BaseWeaponProto.Proficiency.IMPROVISED),

  /** Proficiency for simple weapons. */
  NONE("None", BaseWeaponProto.Proficiency.NONE_PROFICIENCY);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private BaseWeaponProto.Proficiency m_proto;

  /** The parser for proficiency values. */
  public static final NewValue.Parser<Proficiency> PARSER =
    new NewValue.Parser<Proficiency>(1)
    {
      @Override
      public Optional<Proficiency> doParse(String inValue)
      {
        return Proficiency.fromString(inValue);
      }
    };

  /**
   * Create the name.
   *
   * @param inName     the name of the value
   * @param inProto    the corresponding proto enum value
   */
  private Proficiency(String inName, BaseWeaponProto.Proficiency inProto)
  {
    m_name = BaseItem.constant("weapon.proficiencies", inName);
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
  public BaseWeaponProto.Proficiency toProto()
  {
    return m_proto;
  }

  /**
   * Convert the proto enum value to the corresponding enum value.
   *
   * @param inProto the proto value to convert
   * @return the corresponding enum value
   */
  public static Proficiency fromProto(BaseWeaponProto.Proficiency inProto)
  {
    for(Proficiency proficiency : values())
      if(proficiency.m_proto == inProto)
        return proficiency;

    throw new IllegalStateException("unknown weapon proficiency: " + inProto);
  }

  /**
   * Match the proficiency to the given string.
   *
   * @param inValue the string representation
   * @return the matching proficiency, if any
   */
  public static Optional<Proficiency> fromString(String inValue)
  {
    for(Proficiency proficiency : values())
      if(proficiency.getName().equalsIgnoreCase(inValue))
        return Optional.of(proficiency);

    return Optional.absent();
  }

  /**
   * Get the possible names of proficiencies.
   *
   * @return a list of the names
   */
  public static List<String> names()
  {
    List<String> names = new ArrayList<>();
    for(Proficiency proficiency : values())
      names.add(proficiency.getName());

    return names;
  }
}