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

import net.ixitxachitls.dma.proto.Entries.NPCProto;
import net.ixitxachitls.dma.values.Value;

/** The possible gender types in the game. */
public enum Gender implements Named,
    Proto<NPCProto.Gender>
{
  /** Male. */
  MALE("Male", NPCProto.Gender.MALE),

  /** Female. */
  FEMALE("Female", NPCProto.Gender.FEMALE),

  /** Not known. */
  UNKNOWN("Unknown", NPCProto.Gender.UNKNOWN),

  /** Other. */
  OTHER("Other", NPCProto.Gender.OTHER);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  NPCProto.Gender m_proto;

  /** The parser for gender. */
  public static final Value.Parser<Gender> PARSER =
    new Value.Parser<Gender>(1)
  {
    @Override
    public Optional<Gender> doParse(String inValue)
    {
      return Gender.fromString(inValue);
    }
  };

  /**
   * Create the enum value.
   *
   * @param inName  the name of the value
   * @param inProto the proto enum value
   */
  private Gender(String inName, NPCProto.Gender inProto)
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
  public NPCProto.Gender toProto()
  {
    return m_proto;
  }

  public static Gender fromProto(NPCProto.Gender inProto)
  {
    for(Gender gender : values())
      if(gender.m_proto == inProto)
        return gender;

    throw new IllegalArgumentException("cannot convert gender: " + inProto);
  }

  /**
   * Get the alignment from the given string.
   *
   * @param inValue the string representation
   * @return the matching alignment, if any
   */
  public static Optional<Gender> fromString(String inValue)
  {
    for(Gender gender : values())
      if(gender.getName().equalsIgnoreCase(inValue))
        return Optional.of(gender);

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
    for(Gender gender : values())
      names.add(gender.getName());

    return names;
  }
}