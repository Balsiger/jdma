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
import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto;
import net.ixitxachitls.dma.values.Value;

/** The possible alignment modifiers in the game. */
@ParametersAreNonnullByDefault
public enum AlignmentStatus implements Named,
    Proto<BaseMonsterProto.AlignmentStatus>
{
  UNKNOWN("Unknown", BaseMonsterProto.AlignmentStatus.UNKNOWN_ALIGNMENT_STATU),
  /** Always. */
  ALWAYS("Always", BaseMonsterProto.AlignmentStatus.ALWAYS),

  /** Usually. */
  USUALLY("Usually", BaseMonsterProto.AlignmentStatus.USUALLY),

  /** Often. */
  OFTEN("Often", BaseMonsterProto.AlignmentStatus.OFTEN);

  /** The value's name. */
  private String m_name;

  /** The proto value. */
  private BaseMonsterProto.AlignmentStatus m_proto;

  /** The parser for armor types. */
  public static final Value.Parser<AlignmentStatus> PARSER =
    new Value.Parser<AlignmentStatus>(1)
    {
      @Override
      public Optional<AlignmentStatus> doParse(String inValue)
      {
        return AlignmentStatus.fromString(inValue);
      }
    };

  /**
   * Create the name.
   *
   * @param inName      the name of the value
   * @param inProto     the proto value
   */
  private AlignmentStatus(String inName,
                          BaseMonsterProto.AlignmentStatus inProto)
  {
    m_name = BaseMonster.constant("alignment.status", inName);
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
  public BaseMonsterProto.AlignmentStatus toProto()
  {
    return m_proto;
  }

  public static AlignmentStatus
    fromProto(BaseMonsterProto.AlignmentStatus inProto)
  {
    for(AlignmentStatus status : values())
      if(status.m_proto == inProto)
        return status;

    throw new IllegalArgumentException("cannot convert alignment status: "
                                       + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<AlignmentStatus> fromString(String inValue)
  {
    for(AlignmentStatus status : values())
      if(status.getName().equalsIgnoreCase(inValue))
        return Optional.of(status);

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
    for(AlignmentStatus status : values())
      names.add(status.getName());

    return names;
  }
}