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

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.proto.Entries.BaseArmorProto;
import net.ixitxachitls.dma.values.enums.Named;
import net.ixitxachitls.dma.values.enums.Proto;

/** The possible armor types. */
public enum ArmorType implements Named,
    Proto<BaseArmorProto.Type>
{
  /** The unknown armor type. */
  UNKNOWN("Unknown", BaseArmorProto.Type.UNKNOWN),
  /** Light armor. */
  LIGHT("Light Armor", BaseArmorProto.Type.LIGHT),
  /** Medium armor. */
  MEDIUM("Medium Armor", BaseArmorProto.Type.MEDIUM),
  /** Heavy armor. */
  HEAVY("Heavy Armor", BaseArmorProto.Type.HEAVY),
  /** A shield. */
  SHIELD("Shield", BaseArmorProto.Type.SHIELD),
  /** A shield. */
  TOWER_SHIELD("Tower Shield", BaseArmorProto.Type.TOWER_SHIELD),
  /** A shield. */
  None("None", BaseArmorProto.Type.NONE);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private BaseArmorProto.Type m_proto;

  /** The parser for armor types. */
  public static final NewValue.Parser<ArmorType> PARSER =
    new NewValue.Parser<ArmorType>(1)
    {
      @Override
      public Optional<ArmorType> doParse(String inValue)
      {
        return ArmorType.fromString(inValue);
      }
    };

  /** Create the name.
   *
   * @param inName     the name of the value
   * @param inProto    the prot enum value
   */
  private ArmorType(String inName, BaseArmorProto.Type inProto)
  {
    m_name = BaseItem.constant("armor.types", inName);
    m_proto = inProto;
  }

  @Override
  public String getName()
  {
    return m_name;
  }

  public boolean isShield()
  {
    return this == SHIELD || this == TOWER_SHIELD;
  }

  @Override
  public String toString()
  {
    return m_name;
  }

  @Override
  public BaseArmorProto.Type toProto()
  {
    return m_proto;
  }

  /**
   * Convert the given proto into the corresponding enum value.
   *
   * @param inProto  the proto value to convert
   * @return  the converted enum value
   */
  public static ArmorType fromProto(BaseArmorProto.Type inProto)
  {
    for(ArmorType type : values())
      if(type.m_proto == inProto)
        return type;

    throw new IllegalArgumentException("cannot convert armor type: "
      + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<ArmorType> fromString(String inValue)
  {
    for(ArmorType type : values())
      if(type.getName().equalsIgnoreCase(inValue))
        return Optional.of(type);

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
    for(ArmorType type : values())
      names.add(type.getName());

    return names;
  }
}