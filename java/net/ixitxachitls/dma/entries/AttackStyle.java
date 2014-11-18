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
import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto.Attack.Style;
import net.ixitxachitls.dma.values.NewValue;
import net.ixitxachitls.dma.values.enums.Named;
import net.ixitxachitls.dma.values.enums.Proto;

/** The possible attack styles in the game. */
@ParametersAreNonnullByDefault
public enum AttackStyle implements Named,
    Proto<Style>
{
  UNKNOWN("Unknown", BaseMonsterProto.Attack.Style.UNKNOWN_STYLE),

  /** A melee attack. */
  MELEE("melee", BaseMonsterProto.Attack.Style.MELEE),

  /** A ranged attack. */
  RANGED("ranged", BaseMonsterProto.Attack.Style.RANGED);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private BaseMonsterProto.Attack.Style m_proto;

  /** The parser for armor types. */
  public static final NewValue.Parser<AttackStyle> PARSER =
    new NewValue.Parser<AttackStyle>(1)
    {
      @Override
      public Optional<AttackStyle> doParse(String inValue)
      {
        return AttackStyle.fromString(inValue);
      }
    };

  /**
   * Create the name.
   *
   * @param inName       the name of the value
   * @param inProto      the proto enum value
   */
  private AttackStyle(String inName, BaseMonsterProto.Attack.Style inProto)
  {
    m_name = BaseMonster.constant("attack.style", inName);
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
  public Style toProto()
  {
    return m_proto;
  }

  /**
   * Convert the proto enum value to its enum value.
   *
   * @param inProto  the proto value to convert
   * @return         the corresponding enum value
   */
  public static AttackStyle fromProto(BaseMonsterProto.Attack.Style inProto)
  {
    for(AttackStyle style : values())
      if(style.m_proto == inProto)
        return style;

    throw new IllegalArgumentException("cannot convert attack style: "
                                       + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<AttackStyle> fromString(String inValue)
  {
    for(AttackStyle style : values())
      if(style.getName().equalsIgnoreCase(inValue))
        return Optional.of(style);

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
    for(AttackStyle style : values())
      names.add(style.getName());

    return names;
  }
}