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


package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.NewValue;

/** The possible sizes in the game. */
@ParametersAreNonnullByDefault
public enum Treasure implements EnumSelection.Named,
  EnumSelection.Proto<BaseMonsterProto.Treasure>
{
  UNKNOWN("unknown", 0, BaseMonsterProto.Treasure.UNKNOWN_TREADSURE),

  /** No treasure at all. */
  NONE("none", 0, BaseMonsterProto.Treasure.NONE_TREASURE),

  /** Standard treasure. */
  STANDARD("standard", 1, BaseMonsterProto.Treasure.STANDARD),

  /** Double the standard treasure. */
  DOUBLE("double standard", 2, BaseMonsterProto.Treasure.DOUBLE),

  /** Triple the standard treasure. */
  TRIPLE("triple standard", 3, BaseMonsterProto.Treasure.TRIPLE),

  /** Quadruple the standard treasure. */
  QUADRUPLE("quadruple standard", 4, BaseMonsterProto.Treasure.QUADRUPLE);

  /** The value's name. */
  private String m_name;

  /** The multiplier for treasures. */
  private int m_multiplier;

  /** The proto enum value. */
  private BaseMonsterProto.Treasure m_proto;

  /** The parser for armor types. */
  public static final NewValue.Parser<Treasure> PARSER =
    new NewValue.Parser<Treasure>(1)
    {
      @Override
      public Optional<Treasure> doParse(String inValue)
      {
        return Treasure.fromString(inValue);
      }
    };

  /**
   * Create the name.
   *
   * @param inName       the name of the value
   * @param inMultiplier how much treasure we get
   * @param inProto      the proto enum value
   */
  private Treasure(String inName, int inMultiplier,
                   BaseMonsterProto.Treasure inProto)
  {
    m_name = BaseMonster.constant("skill.modifier", inName);
    m_multiplier = inMultiplier;
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

  /**
   * Get the multiplier for this treasure type.
   *
   * @return the multiplier to use for computing treasure amounts
   */
  public int multiplier()
  {
    return m_multiplier;
  }

  @Override
  public BaseMonsterProto.Treasure toProto()
  {
    return m_proto;
  }

  /**
   * Get the treasure value associated with the given proto value.
   *
   * @param inProto the proto to convert
   * @return the converted treasure value
   */
  public static Treasure fromProto(BaseMonsterProto.Treasure inProto)
  {
    for(Treasure treasure : values())
      if(treasure.m_proto == inProto)
        return treasure;

    throw new IllegalArgumentException("cannot convert treasure: " + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<Treasure> fromString(String inValue)
  {
    for(Treasure treasure : values())
      if(treasure.getName().equalsIgnoreCase(inValue))
        return Optional.of(treasure);

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
    for(Treasure treasure : values())
      names.add(treasure.getName());

    return names;
  }
}