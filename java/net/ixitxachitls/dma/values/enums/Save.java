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

import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto;
import net.ixitxachitls.dma.values.Value;

/** The possible saves in the game. */
@ParametersAreNonnullByDefault
public enum Save implements Named, net.ixitxachitls.dma.values.enums.Short,
    Proto<BaseMonsterProto.Save>
{
  UNKNOWN("Unknown", "Unk", BaseMonsterProto.Save.UNKNOWN_SAVE),

  /** Fortitude. */
  FORTITUDE("Fortitude", "For", BaseMonsterProto.Save.FORTITUDE),

  /** Reflex. */
  REFLEX("Reflex", "Ref", BaseMonsterProto.Save.REFLEX),

  /** Wisdom. */
  WISDOM("Wisdom", "Wis", BaseMonsterProto.Save.WISDOM_SAVE);

  /** The value's name. */
  private String m_name;

  /** The value's short name. */
  private String m_short;

  /** The proto enum value. */
  private BaseMonsterProto.Save m_proto;

  /** The parser for armor types. */
  public static final Value.Parser<Save> PARSER =
    new Value.Parser<Save>(1)
    {
      @Override
      public Optional<Save> doParse(String inValue)
      {
        return Save.fromString(inValue);
      }
    };

    /**
   * Create the name.
   *
   * @param inName       the name of the value
   * @param inShort      the short name of the value
   * @param inProto      the proto value
   */
  private Save(String inName, String inShort, BaseMonsterProto.Save inProto)
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
  public BaseMonsterProto.Save toProto()
  {
    return m_proto;
  }

  public static Save fromProto(BaseMonsterProto.Save inProto)
  {
    for(Save save : values())
      if(save.m_proto == inProto)
        return save;

    throw new IllegalArgumentException("cannot convert save: " + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<Save> fromString(String inValue)
  {
    for(Save save : values())
      if(save.getName().equalsIgnoreCase(inValue))
        return Optional.of(save);

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
    for(Save save : values())
      names.add(save.getName());

    return names;
  }
}