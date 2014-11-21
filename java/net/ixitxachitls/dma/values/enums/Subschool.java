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
import net.ixitxachitls.dma.values.Value;

/** The possible spell schools (cf. PHB 172/173). */
public enum Subschool
  implements Named, Proto<BaseSpellProto.Subschool>
{
  /** Unknown. */
  UNKNOWN("Unknown", BaseSpellProto.Subschool.UNKNOWN_SUBSCHOOL),

  /** None. */
  NONE("None", BaseSpellProto.Subschool.NONE),

  /** Calling. */
  CALLING("Calling", BaseSpellProto.Subschool.CALLING),

  /** Creation or Calling. */
  CREATION_OR_CALLING("Creation or Calling",
                      BaseSpellProto.Subschool.CREATION_OR_CALLING),

  /** Creation. */
  CREATION("Creation", BaseSpellProto.Subschool.CREATION),

  /** Healing. */
  HEALING("Healing", BaseSpellProto.Subschool.HEALING),

  /** Summoning. */
  SUMMONING("Summoning", BaseSpellProto.Subschool.SUMMONING),

  /** Teleportation. */
  TELEPORTATION("Teleportation", BaseSpellProto.Subschool.TELEPORTATION),

  /** Scrying. */
  SCRYING("Scrying", BaseSpellProto.Subschool.SCRYING),

  /** Charmn. */
  CHARM("Charm", BaseSpellProto.Subschool.CHARM),

  /** Compulsion. */
  COMPULSION("Compulsion", BaseSpellProto.Subschool.COMPULSION),

  /** Figment or Glamer. */
  FIGMENT_OR_GLAMER("Figment or Glamer",
                    BaseSpellProto.Subschool.FIGMENT_OR_GLAMER),

  /** Figment. */
  FIGMENT("Figment", BaseSpellProto.Subschool.FIGMENT),

  /** Glamer. */
  GLAMER("Glamer", BaseSpellProto.Subschool.GLAMER),

  /** Pattern. */
  PATTERN("Pattern", BaseSpellProto.Subschool.PATTERN),

  /** Phantasm. */
  PHANTASM("Phantasm", BaseSpellProto.Subschool.PHANTASM),

  /** Shadow. */
  SHADOW("Shadow", BaseSpellProto.Subschool.SHADOW);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private BaseSpellProto.Subschool m_proto;

  /** The parser for armor types. */
  public static final Value.Parser<Subschool> PARSER =
    new Value.Parser<Subschool>(1)
    {
      @Override
      public Optional<Subschool> doParse(String inValue)
      {
        return Subschool.fromString(inValue);
      }
    };

  /**
   * Create the name.
   *
   * @param inName       the name of the value
   * @param inProto      the proto enum value
   */
  private Subschool(String inName, BaseSpellProto.Subschool inProto)
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
  public BaseSpellProto.Subschool toProto()
  {
    return m_proto;
  }

  /**
   * Get the group matching the given proto value.
   *
   * @param  inProto     the proto value to look for
   * @return the matched enum (will throw exception if not found)
   */
  public static Subschool fromProto(BaseSpellProto.Subschool inProto)
  {
    for(Subschool subschool: values())
      if(subschool.m_proto == inProto)
        return subschool;

    throw new IllegalStateException("invalid proto subschool: " + inProto);
  }
  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<Subschool> fromString(String inValue)
  {
    for(Subschool school : values())
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
    for(Subschool school : values())
      names.add(school.getName());

    return names;
  }
}