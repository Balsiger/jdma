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

import net.ixitxachitls.dma.proto.Values.SpeedProto;
import net.ixitxachitls.dma.values.Parser;

/**
 * The possible movement modes in the game.
 *
 * @file Maneuverability.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public enum Maneuverability implements Named,
    Proto<SpeedProto.Maneuverability>
{
  /** The unknown value. */
  UNKNOWN("Unknown", SpeedProto.Maneuverability.UNKNOWN_MANEUVERABILITY),

  /** Perfect maneuverability. */
  PERFECT("Perfect", SpeedProto.Maneuverability.PERFECT),

  /** Good maneuverability. */
  GOOD("Good", SpeedProto.Maneuverability.GOOD),

  /** Average maneuverability. */
  AVERAGE("Average", SpeedProto.Maneuverability.AVERAGE),

  /** Poor maneuverability. */
  POOR("Poor", SpeedProto.Maneuverability.POOR),

  /** Clumsy maneuverability. */
  CLUMSY("Clumsy", SpeedProto.Maneuverability.CLUMSY),

  /** Clumsy maneuverability. */
  NONE("", SpeedProto.Maneuverability.NONE);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private SpeedProto.Maneuverability m_proto;

  /** The parser for armor types. */
  public static final Parser<Maneuverability> PARSER =
    new Parser<Maneuverability>(1)
    {
      @Override
      public Optional<Maneuverability> doParse(String inValue)
      {
        return Maneuverability.fromString(inValue);
      }
    };

  /**
   * Create the enum value.
   *
   * @param inName the name of the value
   * @param inProto the proto value
   */
  private Maneuverability(String inName, SpeedProto.Maneuverability inProto)
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
  public SpeedProto.Maneuverability toProto()
  {
    return m_proto;
  }

  /**
   * Convert the proto value into the corresponding enum value.
   *
   * @param inProto the proto value to convert
   * @return the corresponding enum value
   */
  public static Maneuverability fromProto(SpeedProto.Maneuverability inProto)
  {
    for(Maneuverability maneuverability : values())
      if(maneuverability.m_proto == inProto)
        return maneuverability;

    throw new IllegalArgumentException("cannot convert maneuverability: "
                                       + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<Maneuverability> fromString(String inValue)
  {
    for(Maneuverability maneuverability : values())
      if(maneuverability.getName().equalsIgnoreCase(inValue))
        return Optional.of(maneuverability);

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
    for(Maneuverability maneuverability : values())
      names.add(maneuverability.getName());

    return names;
  }
}
