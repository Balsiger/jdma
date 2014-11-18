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

import net.ixitxachitls.dma.proto.Entries.CharacterProto;
import net.ixitxachitls.dma.values.Value;

/** The character state. */
public enum CharacterState implements Named,
    Proto<CharacterProto.State>
{
  UNKNOWN("unknown", CharacterProto.State.UNKNOWN),
  /** A normal character going on adventures. */
  ADVENTURING("adventuring", CharacterProto.State.ADVENTURING),
  /** The character is currently incapable of adventuring. */
  INCAPACITATED("incapacitated", CharacterProto.State.INCAPACITATED),
  /** The character has been retired by the player or the DM. */
  RETIRED("retired", CharacterProto.State.RETIRED),
  /** The character died. */
  DEAD("dead", CharacterProto.State.DEAD);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private CharacterProto.State m_proto;

  /** The parser for alignment. */
  public static final Value.Parser<CharacterState> PARSER =
    new Value.Parser<CharacterState>(1)
  {
    @Override
    public Optional<CharacterState> doParse(String inValue)
    {
      return CharacterState.fromString(inValue);
    }
  };

  /**
   * Create the name.
   *
   * @param inName     the name of the value
   * @param inProto    the proto enum value
   */
  private CharacterState(String inName, CharacterProto.State inProto)
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
  public CharacterProto.State toProto()
  {
    return m_proto;
  }

  public static CharacterState fromProto(CharacterProto.State inProto)
  {
    for(CharacterState state : values())
      if(state.m_proto == inProto)
        return state;

    throw new IllegalArgumentException("cannot convert state: " + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<CharacterState> fromString(String inValue)
  {
    for(CharacterState state : values())
      if(state.getName().equalsIgnoreCase(inValue))
        return Optional.of(state);

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
    for(CharacterState state : values())
      names.add(state.getName());

    return names;
  }
}