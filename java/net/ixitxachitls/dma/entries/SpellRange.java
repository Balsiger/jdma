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

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Entries.BaseSpellProto;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.NewValue;

/** The possible spell ranges. */
public enum SpellRange
  implements EnumSelection.Named, EnumSelection.Proto<BaseSpellProto.Range>
{
  /** Unknown. */
  UNKNOWN("Unknown", BaseSpellProto.Range.UNKNOWN),

  /** Personal or Touch. */
  PERSONAL_OR_TOUCH("Personal or Touch",
                    BaseSpellProto.Range.PERSONAL_OR_TOUCH),

  /** Personal and Touch. */
  PERSONAL_AND_TOUCH("Personal and Touch",
                     BaseSpellProto.Range.PERSONAL_AND_TOUCH),

  /** Personal or Close. */
  PERSONAL_OR_CLOSE("Personal or Close",
                    BaseSpellProto.Range.PERSONAL_OR_CLOSE),

  /** Personal. */
  PERSONAL("Personal", BaseSpellProto.Range.PERSONAL),

  /** Touch. */
  TOUCH("Touch", BaseSpellProto.Range.TOUCH),

  /** Close. */
  CLOSE("Close", BaseSpellProto.Range.CLOSE),

  /** Medium. */
  MEDIUM("Medium", BaseSpellProto.Range.MEDIUM),

  /** Long. */
  LONG("Long", BaseSpellProto.Range.LONG),

  /** Unlimited. */
  UNLIMITED("Unlimited", BaseSpellProto.Range.UNLIMITED),

  /** 40 ft/level. */
  FOURTY_FEET_PER_LEVEL("40 ft/level",
                        BaseSpellProto.Range.FOURTY_FEET_PER_LEVEL),

  /** See Text. */
  SEE_TEXT("See Text", BaseSpellProto.Range.SEE_TEXT_RANGE),

  /** Anywhere within the area to be warded. */
  ANYWHERE_WITHIN_AREA_WARDED
    ("Anywhere within the area to be warded",
     BaseSpellProto.Range.ANYWHERE_WITHIN_AREA_WARDED),

  /** Up to 10 ft/level. */
  UP_TO_TEN_FEET_PER_LEVEL("Up to 10 ft/level",
                           BaseSpellProto.Range.UP_TO_TEN_FEE_PER_LEVEL),

  /** 1 mile/level. */
  ONE_MILE_PER_LEVEL("1 mile/level", BaseSpellProto.Range.ONE_MILE_PER_LEVEL);

  /** The value's name. */
  private final String m_name;

  /** The prot enum value. */
  private final BaseSpellProto.Range m_proto;

  /** The parser for armor types. */
  public static final NewValue.Parser<SpellRange> PARSER =
    new NewValue.Parser<SpellRange>(1)
    {
      @Override
      public Optional<SpellRange> doParse(String inValue)
      {
        return SpellRange.fromString(inValue);
      }
    };

  /**
   * Create the name.
   *
   * @param inName       the name of the value
   * @param inProto      the proto enum value
   */
  private SpellRange(String inName, BaseSpellProto.Range inProto)
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
  public BaseSpellProto.Range toProto()
  {
    return m_proto;
  }

  /**
   * Get the group matching the given proto value.
   *
   * @param  inProto     the proto value to look for
   * @return the matched enum (will throw exception if not found)
   */
  public static SpellRange fromProto(BaseSpellProto.Range inProto)
  {
    for(SpellRange range : values())
      if(range.m_proto == inProto)
        return range;

    throw new IllegalStateException("invalid proto range: " + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<SpellRange> fromString(String inValue)
  {
    for(SpellRange range : values())
      if(range.getName().equalsIgnoreCase(inValue))
        return Optional.of(range);

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
    for(SpellRange range : values())
      names.add(range.getName());

    return names;
  }
}