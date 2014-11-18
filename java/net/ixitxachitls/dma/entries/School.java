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

import net.ixitxachitls.dma.proto.Entries.BaseSpellProto;
import net.ixitxachitls.dma.values.NewValue;
import net.ixitxachitls.dma.values.enums.*;

/** The possible spell schools (cf. PHB 172/173). */
public enum School implements Named, net.ixitxachitls.dma.values.enums.Short,
    Proto<BaseSpellProto.School>
{
  /** Unknowbn. */
  UNKNOWN("Unknown", "Unk", BaseSpellProto.School.UNKNOWN_SCHOOL),

  /** Abjuration. */
  ABJURATION("Abjuration", "Abjur", BaseSpellProto.School.ABJURATION),

  /** Conjuration. */
  CONJURATION("Conjuration", "Conj", BaseSpellProto.School.CONJURATION),

  /** Divination. */
  DIVINATION("Divination", "Div", BaseSpellProto.School.DIVINATION),

  /** Enchantment. */
  ENCHANTMENT("Enchantment", "Ench", BaseSpellProto.School.ENCHANTMENT),

  /** Evocation. */
  EVOCATION("Evocation", "Evoc", BaseSpellProto.School.EVOACATION),

  /** Illusion. */
  ILLUSION("Illusion", "Illus", BaseSpellProto.School.ILLUSION),

  /** Necromancy. */
  NECROMANCY("Necromancy", "Necro", BaseSpellProto.School.NECROMANCY),

  /** Transmutation. */
  TRANSMUTATION("Transmutation", "Trans",
                BaseSpellProto.School.TRANSMUTATION),

  /** Universal. */
  UNIVERSAL("Universal", "Univ", BaseSpellProto.School.UNIVERSAL);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private BaseSpellProto.School m_proto;

  /** The value's short name. */
  private String m_short;

  /** The parser for armor types. */
  public static final NewValue.Parser<School> PARSER =
    new NewValue.Parser<School>(1)
    {
      @Override
      public Optional<School> doParse(String inValue)
      {
        return School.fromString(inValue);
      }
    };

  /** Create the name.
   *
   * @param inName       the name of the value
   * @param inShort      the short name of the value
   * @param inProto      the proto enum value
   */
  private School(String inName, String inShort, BaseSpellProto.School inProto)
  {
    m_name = BaseSpell.constant("school.name", inName);
    m_short = BaseSpell.constant("school.short", inShort);
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
  public BaseSpellProto.School toProto()
  {
    return m_proto;
  }

  /**
   * Get the group matching the given proto value.
   *
   * @param  inProto     the proto value to look for
   * @return the matched enum (will throw exception if not found)
   */
  public static School fromProto(BaseSpellProto.School inProto)
  {
    for(School school: values())
      if(school.m_proto == inProto)
        return school;

    throw new IllegalStateException("invalid proto school: " + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<School> fromString(String inValue)
  {
    for(School school : values())
      if(school.getName().equalsIgnoreCase(inValue))
        return Optional.of(school);

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
    for(School school : values())
      names.add(school.getName());

    return names;
  }
}