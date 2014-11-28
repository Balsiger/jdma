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

import net.ixitxachitls.dma.proto.Entries.BaseWeaponProto;
import net.ixitxachitls.dma.values.enums.Named;
import net.ixitxachitls.dma.values.enums.Proto;

/** The possible weapon types. */
public enum WeaponType implements Named, net.ixitxachitls.dma.values.enums.Short,
    Proto<BaseWeaponProto.Type>
{
  /** An unknown type. */
  UNKNOWN("Unknown", "U", BaseWeaponProto.Type.UNKNOWN),

  /** A piercing OR slashing weapon. */
  PIERCING_OR_SLASHING("Piercing or Slashing", "P or S",
                       BaseWeaponProto.Type.PIERCING_OR_SLASHING),

  /** A bludgeoning OR piercing weapon. */
  BLUDGEONING_OR_PIERCING("Bludgeoning or Piercing", "B or P",
                          BaseWeaponProto.Type.BLUDGEONING_OR_PIERCING),

  /** A bludeoning AND piercing weapon. */
  BLUDGEONING_AND_PIERCING("Bludgeoning and Piercing", "B and P",
                           BaseWeaponProto.Type.BLUDGEONING_AND_PIERCING),

  /** A slashing OR piercing weapon. */
  SLASHING_OR_PIERCING("Slashing or Piercing", "S or P",
                       BaseWeaponProto.Type.SLASHING_OR_PIERCING),

  /** A slashing weapon. */
  SLASHING("Slashing", "S", BaseWeaponProto.Type.SLASHING),

  /** A bludgeoning weapon. */
  BLUDGEONING("Bludgeoning", "B", BaseWeaponProto.Type.BLUDGEONING),

  /** A piercing weapon. */
  PIERCING("Piercing", "P", BaseWeaponProto.Type.PIERCING),

  /** A grenade. */
  GRENADE("Grenade", "G", BaseWeaponProto.Type.GRENADE),

  /** No type. */
  NONE("None", "N", BaseWeaponProto.Type.NONE);

  /** The value's name. */
  private String m_name;

  /** The value's short name. */
  private String m_short;

  /** The proto enum value. */
  private BaseWeaponProto.Type m_proto;

  /** The parser for weapon types. */
  public static final Parser<WeaponType> PARSER =
    new Parser<WeaponType>(1)
    {
      @Override
      public Optional<WeaponType> doParse(String inValue)
      {
        return WeaponType.fromString(inValue);
      }
    };

  /** Create the name.
   *
   * @param inName     the name of the value
   * @param inShort    the short name of the value
   * @param inProto    the proto enum value
   */
  private WeaponType(String inName, String inShort, BaseWeaponProto.Type inProto)
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
  public BaseWeaponProto.Type toProto()
  {
    return m_proto;
  }

  /**
   * Convert the proto enum to its enum value.
   *
   * @param inProto  the proto enum value
   * @return the converted enum value
   */
  public static WeaponType fromProto(BaseWeaponProto.Type inProto)
  {
    for(WeaponType type : values())
      if(type.m_proto == inProto)
        return type;

    throw new IllegalStateException("cannot convert weapon type proto: "
      + inProto);
  }

  /**
   * Get the type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<WeaponType> fromString(String inValue)
  {
    for(WeaponType type : values())
      if(type.getName().equalsIgnoreCase(inValue))
        return Optional.of(type);

    return Optional.absent();
  }

  /**
   * Get the possible names of types.
   *
   * @return a list of the namees
   */
  public static List<String> names()
  {
    List<String> names = new ArrayList<>();
    for(WeaponType type : values())
      names.add(type.getName());

    return names;
  }
}