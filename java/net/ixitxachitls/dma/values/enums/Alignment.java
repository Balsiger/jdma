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

import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto;
import net.ixitxachitls.dma.values.Parser;

/**
 * The possible sizes in the game.
 *
 * @file Alignment.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public enum Alignment implements Named, Short,
    Proto<BaseMonsterProto.Alignment>
{
  /** The unknown value. */
  UNKNOWN("Unknown", "UN", BaseMonsterProto.Alignment.UNKNOWN_ALIGNMENT),

  /** Lawful Evil. */
  LE("Lawful Evil", "LE", BaseMonsterProto.Alignment.LAEWFUL_EVIL),

  /** Lawful Neutral. */
  LN("Lawful Neutral", "LN", BaseMonsterProto.Alignment.LAWFUL_NEUTRAL),

  /** Lawful Good. */
  LG("Lawful Good", "LG", BaseMonsterProto.Alignment.LAWFUL_GOOD),

  /** Chaotic Evil. */
  CE("Chaotic Evil", "CE", BaseMonsterProto.Alignment.CHAOTIC_EVIL),

  /** Chaotic Neutral. */
  CN("Chaotic Neutral", "CN", BaseMonsterProto.Alignment.CHOATIC_NETURAL),

  /** Chaotic Good. */
  CG("Chaotic Good", "CG", BaseMonsterProto.Alignment.CHAOTIC_GOOD),

  /** Neutral Evil. */
  NE("Neutral Evil", "NE", BaseMonsterProto.Alignment.NEUTRAL_EVIL),

  /** True Neutral. */
  N("Neutral", "N", BaseMonsterProto.Alignment.TRUE_NEUTRAL),

  /** Neutral Good. */
  NG("Neutral Good", "NG", BaseMonsterProto.Alignment.NEUTRAL_GOOD),

  /** Any chaotic alignment. */
  ANY_CHAOTIC("Any Chaotic", "AC", BaseMonsterProto.Alignment.ANY_CHAOTIC),

  /** Any evil alignment. */
  ANY_EVIL("Any Evil", "AE", BaseMonsterProto.Alignment.ANY_EVIL),

  /** Any good alignment. */
  ANY_GOOD("Any Good", "AG", BaseMonsterProto.Alignment.ANY_GOOD),

  /** Any lawful alignment. */
  ANY_LAWFUL("Any Lawful", "AL", BaseMonsterProto.Alignment.ANY_LAWFUL),

  /** Any alignment. */
  ANY("Any", "A", BaseMonsterProto.Alignment.ANY_ALIGNMENT);

  /** The value's name. */
  private String m_name;

  /** The value's short name. */
  private String m_short;

  /** The proto enum value. */
  private BaseMonsterProto.Alignment m_proto;

  /** The parser for alignment. */
  public static final Parser<Alignment> PARSER =
    new Parser<Alignment>(1)
  {
    @Override
    public Optional<Alignment> doParse(String inValue)
    {
      return Alignment.fromString(inValue);
    }
  };

  /**
   * Create the name.
   *
   * @param inName      the name of the value
   * @param inShort     the short name of the value
   * @param inProto     the proto value
   */
  private Alignment(String inName, String inShort,
                    BaseMonsterProto.Alignment inProto)
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
  public String toString()
  {
    return m_name;
  }

  @Override
  public String getShort()
  {
    return m_short;
  }

  @Override
  public BaseMonsterProto.Alignment toProto()
  {
    return m_proto;
  }

  /**
   * Convert the proto to the corresponding enum value.
   *
   * @param inProto the proto to convert
   * @return the corresponding enum value
   */
  public static Alignment fromProto(BaseMonsterProto.Alignment inProto)
  {
    for(Alignment alignment : values())
      if(alignment.m_proto == inProto)
        return alignment;

    throw new IllegalArgumentException("cannot convert alignment: "
                                       + inProto);
  }

  /**
   * Get the alignment from the given string.
   *
   * @param inValue the string representation
   * @return the matching alignment, if any
   */
  public static Optional<Alignment> fromString(String inValue)
  {
    for(Alignment alignment : values())
      if(alignment.getName().equalsIgnoreCase(inValue))
        return Optional.of(alignment);

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
    for(Alignment type : values())
      names.add(type.getName());

    return names;
  }
}
