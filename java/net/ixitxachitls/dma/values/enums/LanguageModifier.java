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
import net.ixitxachitls.dma.values.Parser;

/** The possible sizes in the game. */
@ParametersAreNonnullByDefault
public enum LanguageModifier implements Named,
    Proto<BaseMonsterProto.Language.Modifier>
{
  UNKNOWN("Unknown", BaseMonsterProto.Language.Modifier.UNKNOWN_MODIFIER),
  /** Automatic. */
  AUTOMATIC("Automatic", BaseMonsterProto.Language.Modifier.AUTOMATIC),

  /** Bonus. */
  BONUS("Bonus", BaseMonsterProto.Language.Modifier.BONUS),

  /** Some. */
  SOME("Some", BaseMonsterProto.Language.Modifier.SOME),

  /** Understand. */
  UNDERSTAND("Understand", BaseMonsterProto.Language.Modifier.UNDERSTAND);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private BaseMonsterProto.Language.Modifier m_proto;

  /** The parser for armor types. */
  public static final Parser<LanguageModifier> PARSER =
    new Parser<LanguageModifier>(1)
    {
      @Override
      public Optional<LanguageModifier> doParse(String inValue)
      {
        return LanguageModifier.fromString(inValue);
      }
    };

    /**
   * Create the name.
   *
   * @param inName       the name of the value
   * @param inProto      the proto value
   */
  private LanguageModifier(String inName,
                           BaseMonsterProto.Language.Modifier inProto)
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
  public BaseMonsterProto.Language.Modifier toProto()
  {
    return m_proto;
  }

  /**
   * Convert a proto value to the enum value.
   *
   * @param inProto the proto value to convert
   * @return the corresponding enum value
   */
  public static LanguageModifier
    fromProto(BaseMonsterProto.Language.Modifier inProto)
  {
    for(LanguageModifier modifier : values())
      if(modifier.m_proto == inProto)
        return modifier;

    throw new IllegalArgumentException("cannot convert language modifier: "
                                       + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<LanguageModifier> fromString(String inValue)
  {
    for(LanguageModifier modifier : values())
      if(modifier.getName().equalsIgnoreCase(inValue))
        return Optional.of(modifier);

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
    for(LanguageModifier modifier: values())
      names.add(modifier.getName());

    return names;
  }
}