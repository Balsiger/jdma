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

import net.ixitxachitls.dma.proto.Entries.BaseCountedProto;
import net.ixitxachitls.dma.values.enums.Named;
import net.ixitxachitls.dma.values.enums.Proto;

/** The possible counting unites in the game. */
public enum CountUnit implements Named,
    Proto<BaseCountedProto.Unit>
{
  /** Unknown count. */
  UNKNOWN("unknown", "unknowns", BaseCountedProto.Unit.UNKNOWN),
  /** Number of days. */
  DAY("day", "days", BaseCountedProto.Unit.DAY),
  /** Numer of pieces. */
  PIECE("piece", "pieces", BaseCountedProto.Unit.PIECE),
  /** Number of sheets. */
  SHEET("sheet", "sheets", BaseCountedProto.Unit.SHEET),
  /** Number of individual uses. */
  USE("use", "uses", BaseCountedProto.Unit.USE),
  /** Number of pages. */
  PAGE("page", "pages", BaseCountedProto.Unit.PAGE),
  /** Charges. */
  CHARGE("charge", "charges", BaseCountedProto.Unit.CHARGE),
  /** Can be applied. */
  APPLICATION("application", "applications",
              BaseCountedProto.Unit.APPLICATION),
  /** Can absorb or take some damage. */
  DAMAGE("damage", "damage", BaseCountedProto.Unit.DAMAGE);

  /** The value's name. */
  private String m_name;

  /** The value's name for multiple unites. */
  private String m_multiple;

  /** The proto enum value. */
  private BaseCountedProto.Unit m_proto;

  /** The parser for count units. */
  public static final Value.Parser<CountUnit> PARSER =
    new Value.Parser<CountUnit>(1)
    {
      @Override
      public Optional<CountUnit> doParse(String inValue)
      {
        return CountUnit.fromString(inValue);
      }
    };

  /** Create the name.
   *
   * @param inName     the name of the value
   * @param inMultiple the text for multiple units
   * @param inProto    the proto enum value
   */
  private CountUnit(String inName, String inMultiple,
                    BaseCountedProto.Unit inProto)
  {
    m_name = inName;
    m_multiple = inMultiple;
    m_proto = inProto;
  }

  @Override
  public String getName()
  {
    return m_name;
  }

  /**
   * Get the multiple name.
   *
   * @return the multiple name
   */
  public String getMultiple()
  {
    return m_multiple;
  }

  @Override
  public String toString()
  {
    return m_name;
  }

  @Override
  public BaseCountedProto.Unit toProto()
  {
    return m_proto;
  }

  /**
   * Get the unit corresponding to the given proto value.
   *
   * @param inProto the proto value to convert
   * @return the corresponding enum value
   */
  public static CountUnit fromProto(BaseCountedProto.Unit inProto)
  {
    for(CountUnit unit : values())
      if(unit.m_proto == inProto)
        return unit;

    throw new IllegalStateException("cannot convert unit: " + inProto);
  }

  /**
   * Get the unit from the given string.
   *
   * @param inValue the string representation
   * @return the matching unit, if any
   */
  public static Optional<CountUnit> fromString(String inValue)
  {
    for(CountUnit unit : values())
      if(unit.getName().equalsIgnoreCase(inValue))
        return Optional.of(unit);

    return Optional.absent();
  }

  /**
   * Get the possible names of units.
   *
   * @return a list of the names
   */
  public static List<String> names()
  {
    List<String> names = new ArrayList<>();
    for(CountUnit unit : values())
      names.add(unit.getName());

    return names;
  }
}