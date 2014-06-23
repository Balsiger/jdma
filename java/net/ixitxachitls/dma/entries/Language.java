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
public enum Language implements EnumSelection.Named,
  EnumSelection.Proto<BaseMonsterProto.Language.Name>
{
  UNKNOWN("Unknown", BaseMonsterProto.Language.Name.UNKNOWN_LANGUAGE),

  /** Aboleth. */
  ABOLETH("Aboleth", BaseMonsterProto.Language.Name.ABOLETH),

  /** Abyssal. */
  ABYSSAL("Abyssal", BaseMonsterProto.Language.Name.ABYSSAL),

  /** Aquan. */
  AQUAN("Aquan", BaseMonsterProto.Language.Name.AQUAN),

  /** Auran. */
  AURAN("Auran", BaseMonsterProto.Language.Name.AURAN),

  /** Celestial. */
  CELESTIAL("Celestial", BaseMonsterProto.Language.Name.CELESTIAL),

  /** Common. */
  COMMON("Common", BaseMonsterProto.Language.Name.COMMON),

  /** Draconic. */
  DRACONIC("Draconic", BaseMonsterProto.Language.Name.DRACONIC),

  /** Drow Sign Language. */
  DROW_SIGN("Drow Sign", BaseMonsterProto.Language.Name.DROW_SIGN),

  /** Druidic. */
  DRUIDIC("Druidic", BaseMonsterProto.Language.Name.DRUIDIC),

  /** Dwarven. */
  DWARVEN("Dwarven", BaseMonsterProto.Language.Name.DWARVEN),

  /** Elven. */
  ELVEN("Elven", BaseMonsterProto.Language.Name.ELVEN),

  /** Giant. */
  GIANT("Giant", BaseMonsterProto.Language.Name.GIANT),

  /** Gnome. */
  GNOME("Gnome", BaseMonsterProto.Language.Name.GNOME),

  /** Goblin. */
  GOBLIN("Goblin", BaseMonsterProto.Language.Name.GOBLIN),

  /** Gnoll. */
  GNOLL("Gnoll", BaseMonsterProto.Language.Name.GNOLL),

  /** Halfling. */
  HALFLING("Halfling", BaseMonsterProto.Language.Name.HALFLING),

  /** Ignan. */
  IGNAN("Ignan", BaseMonsterProto.Language.Name.IGNAN),

  /** Infernal. */
  INFERNAL("Infernal", BaseMonsterProto.Language.Name.INFERNAL),

  /** Kuo-toa. */
  KUO_TOA("Kuo-toa", BaseMonsterProto.Language.Name.KUO_TOA),

  /** Orc. */
  ORC("Orc", BaseMonsterProto.Language.Name.ORC),

  /** Sylvan. */
  SYLVAN("Sylvan", BaseMonsterProto.Language.Name.SYLVAN),

  /** Terran. */
  TERRAN("Terran", BaseMonsterProto.Language.Name.TERRAN),

  /** Undercommon. */
  UNDERCOMMON("Undercommon", BaseMonsterProto.Language.Name.UNDERCOMMON),

  /** None. */
  NONE("-", BaseMonsterProto.Language.Name.NONE);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private BaseMonsterProto.Language.Name m_proto;

  /** The parser for armor types. */
  public static final NewValue.Parser<Language> PARSER =
    new NewValue.Parser<Language>(1)
    {
      @Override
      public Optional<Language> doParse(String inValue)
      {
        return Language.fromString(inValue);
      }
    };

    /**
   * Create the name.
   *
   * @param inName       the name of the value
   * @param inProto      the proto enum value
   */
  private Language(String inName, BaseMonsterProto.Language.Name inProto)
  {
    m_name = BaseMonster.constant("language", inName);
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
  public BaseMonsterProto.Language.Name toProto()
  {
    return m_proto;
  }

  /**
   * Convert the given proto value to the corresponding enum value.
   *
   * @param inProto the proto value to convert
   * @return the corresponding enum value
   */
  public static Language fromProto(BaseMonsterProto.Language.Name inProto)
  {
    for(Language language : values())
      if(language.m_proto == inProto)
        return language;

    throw new IllegalArgumentException("cannot convert language: " + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<Language> fromString(String inValue)
  {
    for(Language language : values())
      if(language.getName().equalsIgnoreCase(inValue))
        return Optional.of(language);

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
    for(Language language : values())
      names.add(language.getName());

    return names;
  }
}