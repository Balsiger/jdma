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

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.entries.BaseMonster;
import net.ixitxachitls.dma.proto.Values.SpeedProto;
import net.ixitxachitls.dma.values.Value;

/** The possible movement modes in the game. */
@ParametersAreNonnullByDefault
public enum MovementMode implements Named,
    Proto<SpeedProto.Mode>
{
  UNKNOWN("Unknown", SpeedProto.Mode.UNKNONW_MODE),

  /** Burrowing movement. */
  BURROW("Burrow", SpeedProto.Mode.BURROW),

  /** Climbing. */
  CLIMB("Climb", SpeedProto.Mode.CLIMB),

  /** Flying. */
  FLY("Fly", SpeedProto.Mode.FLY),

  /** Swimming. */
  SWIM("Swim", SpeedProto.Mode.SWIM),

  /** Running. */
  RUN("", SpeedProto.Mode.RUN);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private SpeedProto.Mode m_proto;

  /** The parser for armor types. */
  public static final Value.Parser<MovementMode> PARSER =
    new Value.Parser<MovementMode>(1)
    {
      @Override
      public Optional<MovementMode> doParse(String inValue)
      {
        return MovementMode.fromString(inValue);
      }
    };

  /**
   * Create the enum value.
   *
   * @param inName the name of the value
   * @param inProto the corresponding proto value
   */
  private MovementMode(String inName, SpeedProto.Mode inProto)
  {
    m_name = BaseMonster.constant("movement.mode", inName);
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
  public SpeedProto.Mode toProto()
  {
    return m_proto;
  }

  /**
   * Convert the proto value to the corresponding enum value.
   *
   * @param inProto the proto to convert
   * @return the corresponding enum value
   */
  public static MovementMode fromProto(SpeedProto.Mode inProto)
  {
    for(MovementMode mode : values())
      if(mode.m_proto == inProto)
        return mode;

    throw new IllegalArgumentException("cannot convert movement mode: "
                                       + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<MovementMode> fromString(String inValue)
  {
    for(MovementMode mode : values())
      if(mode.getName().equalsIgnoreCase(inValue))
        return Optional.of(mode);

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
    for(MovementMode type : values())
      names.add(type.getName());

    return names;
  }
}
