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

import net.ixitxachitls.dma.proto.Entries.BaseContainerProto;
import net.ixitxachitls.dma.values.enums.Named;
import net.ixitxachitls.dma.values.enums.Proto;

/** The possible sizes in the game. */
public enum AggregationState implements Named,
    net.ixitxachitls.dma.values.enums.Short, Proto<BaseContainerProto.State>
{
  /** Unknown state. */
  UNKNOWN("unknown", "unk", BaseContainerProto.State.UNKNOWN),

  /** Made of paper. */
  SOLID("solid", "sol", BaseContainerProto.State.SOLID),

  /** Made of cloth. */
  GRANULAR("granular", "gran", BaseContainerProto.State.GRANULAR),

  /** Made of rope. */
  LIQUID("liquid", "liq", BaseContainerProto.State.LIQUID),

  /** Made of glass. */
  GASEOUS("gaseous", "gas", BaseContainerProto.State.GASEOUS);
  private final String m_name;

  /** The short name. */
  private final String m_short;

  /** The proto enum value. */
  private final BaseContainerProto.State m_proto;

  /** The parser for aggregation states. */
  public static final Parser<AggregationState> PARSER =
    new Parser<AggregationState>(1)
    {
      @Override
      public Optional<AggregationState> doParse(String inValue)
      {
        return AggregationState.fromString(inValue);
      }
    };

  /** Create the name.
   *
   * @param inName     the name of the value
   * @param inProto    the proto enum value
   */
  private AggregationState(String inName, String inShort,
                           BaseContainerProto.State inProto)
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
  public BaseContainerProto.State toProto()
  {
    return m_proto;
  }

  /**
   * Convert the given proto value into a state enum.
   *
   * @param inProto  the proto to convert
   * @return the converted state
   */
  public static AggregationState fromProto(BaseContainerProto.State inProto)
  {
    for(AggregationState state : values())
      if(state.m_proto == inProto)
        return state;

    throw new IllegalStateException("invalid state proto: " + inProto);
  }

  /**
   * Get the state from the given string.
   *
   * @param inValue the string representation
   * @return the matching state, if any
   */
  public static Optional<AggregationState> fromString(String inValue)
  {
    for(AggregationState state : values())
      if(state.getName().equalsIgnoreCase(inValue)
        || state.getShort().equalsIgnoreCase(inValue))
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
    for(AggregationState state : values())
      names.add(state.getName());

    return names;
  }
}