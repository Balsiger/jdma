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
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.NewValue;

/** The possible spell components. */
public enum SpellComponent implements EnumSelection.Named, EnumSelection.Short,
  EnumSelection.Proto<BaseSpellProto.Components>
{
  /** Unknown. */
  UNKNOWN("Unknown", "U", BaseSpellProto.Components.UNKNOWN_COMPONENT),

  /** Verbose. */
  VERBOSE("Verbose", "V", BaseSpellProto.Components.VERBOSE),

  /** Somatic. */
  SOMATIC("Somatic", "S", BaseSpellProto.Components.SOMATIC),

  /** Material/Divine Focus. */
  MATERIAL_DEVINE_FOCUS("Material/Divine Focus", "M/DF",
                        BaseSpellProto.Components.MATERIAL_DEVINE_FOCUS),

  /** Material. */
  MATERIAL("Material", "M", BaseSpellProto.Components.MATERIAL),

  /** Focus/Divine Focus. */
  FOCUS_DIVINE_FOCUS("Focus/Divine Focus", "F/DF",
                     BaseSpellProto.Components.FOCUS_DIVINE_FOCUS),

  /** Focus. */
  FOCUS("Focus", "F", BaseSpellProto.Components.FOCUS),

  /** Divine Focus. */
  DIVINE_FOCUS("Divine Focus", "DF", BaseSpellProto.Components.DIVINE_FOCUS),

  /** Experience points. */
  EXPERIENCE_POINTS("Experience Points", "XP",
                    BaseSpellProto.Components.EXPERIENCE_POINTS);

  /** The value's name. */
  private String m_name;

  /** The value's short name. */
  private String m_short;

  /** The proto enum value. */
  private BaseSpellProto.Components m_proto;

  /** The parser for armor types. */
  public static final NewValue.Parser<SpellComponent> PARSER =
    new NewValue.Parser<SpellComponent>(1)
    {
      @Override
      public Optional<SpellComponent> doParse(String inValue)
      {
        return SpellComponent.fromString(inValue);
      }
    };

  /**
   * Create the name.
   *
   * @param inName       the name of the value
   * @param inShort      the short name of the value
   * @param inProto      the proto enum value
   */
  private SpellComponent(String inName, String inShort,
                     BaseSpellProto.Components inProto)
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
  public BaseSpellProto.Components toProto()
  {
    return m_proto;
  }

  /**
   * Get the group matching the given proto value.
   *
   * @param  inProto     the proto value to look for
   * @return the matched enum (will throw exception if not found)
   */
  public static SpellComponent fromProto(BaseSpellProto.Components inProto)
  {
    for(SpellComponent components : values())
      if(components.m_proto == inProto)
        return components;

    throw new IllegalStateException("invalid proto components: " + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<SpellComponent> fromString(String inValue)
  {
    for(SpellComponent component : values())
      if(component.getName().equalsIgnoreCase(inValue))
        return Optional.of(component);

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
    for(SpellComponent component : values())
      names.add(component.getName());

    return names;
  }
}