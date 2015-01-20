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

import net.ixitxachitls.dma.proto.Entries.BaseQualityProto;
import net.ixitxachitls.dma.values.Parser;

/**
 * The possible immunities in the game.
 *
 * @file Immunity.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public enum Immunity implements Named
{
  /** The unknown value. */
  UNKNOWN("Unknown", BaseQualityProto.Immunity.UNKNOWN_IMMUNITY),

  /** Immunity to sleep spells. */
  SLEEP_SPELLS("Sleep Spells", BaseQualityProto.Immunity.SLEEP_SPELLS),

  /** Immunity to sleep effects. */
  SLEEP_EFFECTS("Sleep Effects", BaseQualityProto.Immunity.SLEEP_EFFECTS);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private BaseQualityProto.Immunity m_proto;

  /** The parser for abilities. */
  public static final Parser<Immunity> PARSER =
    new Parser<Immunity>(1)
    {
      @Override
      public Optional<Immunity> doParse(String inValue)
      {
        return Immunity.fromString(inValue);
      }
    };

  /**
   * Create the name.
   *
   * @param inName       the name of the value
   * @param inProto      the proto enum value
   */
  private Immunity(String inName, BaseQualityProto.Immunity inProto)
  {
    m_name = inName;
    m_proto = inProto;
  }

  /**
   * Get the name of the value.
   *
   * @return the name of the value
   */
  @Override
  public String getName()
  {
    return m_name;
  }

  /**
   * Get the value as string.
   *
   * @return the name of the value
   */
  @Override
  public String toString()
  {
    return m_name;
  }

  /**
   * Get the proto value for this value.
   *
   * @return the proto enum value
   */
  public BaseQualityProto.Immunity toProto()
  {
    return m_proto;
  }

  /**
   * Get the group matching the given proto value.
   *
   * @param  inProto     the proto value to look for
   * @return the matched enum (will throw exception if not found)
   */
  public static Immunity fromProto(BaseQualityProto.Immunity inProto)
  {
    for(Immunity immunity : values())
      if(immunity.m_proto == inProto)
        return immunity;

    throw new IllegalStateException("invalid proto immunity: " + inProto);
  }

 /**
   * All the possible names for the layout.
   *
   * @return the possible names
   */
  public static List<String> names()
  {
    List<String> names = new ArrayList<>();

    for(Immunity immunity : values())
      names.add(immunity.getName());

    return names;
  }

  /**
   * Get the layout matching the given text.
   *
   * @param inText the text to get the immunity for
   * @return the immunity, if one matches
   */
  public static Optional<Immunity> fromString(String inText)
  {
    for(Immunity immunity : values())
      if(immunity.m_name.equalsIgnoreCase(inText))
        return Optional.of(immunity);

    return Optional.absent();
  }
}
