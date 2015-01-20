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

import net.ixitxachitls.dma.proto.Entries.BaseSpellProto;
import net.ixitxachitls.dma.values.Parser;

/**
 * The possible spell descriptors.
 *
 * @file SpellDescriptor.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public enum SpellDescriptor
  implements Named, Proto<BaseSpellProto.Descriptor>
{
  /** Unknown. */
  UNKNOWN("Unknown", BaseSpellProto.Descriptor.UNKNOWN_DESCRIPTOR),

  /** Acid. */
  ACID("Acid", BaseSpellProto.Descriptor.ACID),

  /** Air. */
  AIR("Air", BaseSpellProto.Descriptor.AIR),

  /** Chaotic. */
  CHAOTIC("Chaotic", BaseSpellProto.Descriptor.CHAOTIC),

  /** Cold. */
  COLD("Cold", BaseSpellProto.Descriptor.COLD),

  /** Darkness. */
  DARKNESS("Darkness", BaseSpellProto.Descriptor.DARKNESS),

  /** Death. */
  DEATH("Death", BaseSpellProto.Descriptor.DEATH),

  /** Earth. */
  EARTH("Earth", BaseSpellProto.Descriptor.EARTH),

  /** Electricity. */
  ELECTRICITY("Electricity", BaseSpellProto.Descriptor.ELECTRICITY),

  /** Evil. */
  EVIL("Evil", BaseSpellProto.Descriptor.EVIL),

  /** Fear. */
  FEAR("Fear", BaseSpellProto.Descriptor.FEAR),

  /** Fire or Cold. */
  FIRE_OR_COLD("Fire or Cold", BaseSpellProto.Descriptor.FIRE_OR_COLD),

  /** Fire. */
  FIRE("Fire", BaseSpellProto.Descriptor.FIRE),

  /** Force. */
  FORCE("Force", BaseSpellProto.Descriptor.FORCE),

  /** Good. */
  GOOD("Good", BaseSpellProto.Descriptor.GOOD),

  /** Language-dependent. */
  LANGUAGE_DEPENDENT("Language-dependent",
                     BaseSpellProto.Descriptor.LANGUAGE_DEPENDENT),

  /** Lawful. */
  LAWFUL("Lawful", BaseSpellProto.Descriptor.LAWFUL),

  /** Light. */
  LIGHT("Light", BaseSpellProto.Descriptor.LIGHT),

  /** Mind-affecting. */
  MIND_AFFECTING("Mind-affecting", BaseSpellProto.Descriptor.MIND_AFFECTING),

  /** Scrying. */
  SCRYING("Scrying", BaseSpellProto.Descriptor.SCRYING_DESCRIPTOR),

  /** Sonic. */
  SONIC("Sonic", BaseSpellProto.Descriptor.SONIC),

  /** Water. */
  WATER("Water", BaseSpellProto.Descriptor.WATER),

  /** See Text. */
  SEE_TEXT("See Text", BaseSpellProto.Descriptor.SEE_TEXT);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private BaseSpellProto.Descriptor m_proto;

  /** The value's short name. */
  @SuppressWarnings("unused")
  private String m_short;

  /** The parser for armor types. */
  public static final Parser<SpellDescriptor> PARSER =
    new Parser<SpellDescriptor>(1)
    {
      @Override
      public Optional<SpellDescriptor> doParse(String inValue)
      {
        return SpellDescriptor.fromString(inValue);
      }
    };

  /** Create the name.
   *
   * @param inName       the name of the value
   * @param inProto      the proto enum value
   */
  private SpellDescriptor(String inName, BaseSpellProto.Descriptor inProto)
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
  public String toString()
  {
    return m_name;
  }

  @Override
  public BaseSpellProto.Descriptor toProto()
  {
    return m_proto;
  }

  /**
   * Get the group matching the given proto value.
   *
   * @param  inProto     the proto value to look for
   * @return the matched enum (will throw exception if not found)
   */
  public static SpellDescriptor fromProto(BaseSpellProto.Descriptor inProto)
  {
    for(SpellDescriptor descriptor : values())
      if(descriptor.m_proto == inProto)
        return descriptor;

    throw new IllegalStateException("invalid proto descriptor: " + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<SpellDescriptor> fromString(String inValue)
  {
    for(SpellDescriptor descriptor : values())
      if(descriptor.getName().equalsIgnoreCase(inValue))
        return Optional.of(descriptor);

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
    for(SpellDescriptor descriptor : values())
      names.add(descriptor.getName());

    return names;
  }
}
