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

/** The possible weapon styles. */
public enum WeaponStyle implements Named, net.ixitxachitls.dma.values.enums.Short,
    Proto<BaseWeaponProto.Style>
{
  /** An unknown style value. */
  UNKNOWN("Unknown", "U", false, 0, BaseWeaponProto.Style.UNKNOWN_STYLE),

  /** A two-handed melee weapon. */
  TWOHANDED_MELEE("Two-Handed Melee", "Two", true, 0,
                  BaseWeaponProto.Style.TWOHANDED_MELEE),

  /** A one-handed melee weapon. */
  ONEANDED_MELEE("One-Handed Melee", "One", true, -1,
                 BaseWeaponProto.Style.ONEHANDED_MELEE),

  /** A light melee weapon. */
  LIGHT_MELEE("Light Melee", "Light", true, -2,
              BaseWeaponProto.Style.LIGHT_MELEE),

  /** An unarmed 'weapon'. */
  UNARMED("Unarmed", "Unarmed", true, 0, BaseWeaponProto.Style.UNARMED),

  /** A ranged touch weapon. */
  RANGED_TOUCH("Ranged Touch", "Touch R", false, 0,
               BaseWeaponProto.Style.RANGED_TOUCH),

  /** A ranged weapon. */
  RANGED("Ranged", "Ranged", false, 0, BaseWeaponProto.Style.RANGED),

  /** A thrown touch weapon. */
  THROWN_TOUCH("Thrown Touch", "Touch T", false, 0,
               BaseWeaponProto.Style.THROWN_TOUCH),

  /** A thrown weapon. */
  THROWN("Thrown", "Thrown", false, 0, BaseWeaponProto.Style.THROWN),

  /** A touch weapon. */
  TOUCH("Touch", "Touch", true, 0, BaseWeaponProto.Style.TOUCH);

  /** The value's name. */
  private String m_name;

  /** The value's short name. */
  private String m_short;

  /** Flag if this is a range or melee weapon. */
  private boolean m_melee;

  /** The size difference between a normal item an a weapon. */
  private int m_sizeDifference;

  /** The corresponding proto value. */
  private BaseWeaponProto.Style m_proto;

  /** The parser for weapon styles. */
  public static final Parser<WeaponStyle> PARSER =
    new Parser<WeaponStyle>(1)
    {
      @Override
      public Optional<WeaponStyle> doParse(String inValue)
      {
        return WeaponStyle.fromString(inValue);
      }
    };

  /**
   * Create the name.
   *
   * @param inName           the name of the value
   * @param inShort          the short name of the value
   * @param inMelee          true if this is a melee weapon, false for ranged
   * @param inSizeDifference the number of steps between this and medium
   * @param inProto          the corresponding proto value
   */
  private WeaponStyle(String inName, String inShort, boolean inMelee,
                int inSizeDifference, BaseWeaponProto.Style inProto)
  {
    m_name = inName;
    m_short = inShort;
    m_melee = inMelee;
    m_sizeDifference = inSizeDifference;
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

  /**
   * Check if the weapon style is ranged for melee.
   *
   * @return true if the weapon is a melee weapon, false for ranged.
   */
  public boolean isMelee()
  {
    return m_melee;
  }

  /**
   * Get the size difference.
   *
   * @return the number of steps between this and medium.
   */
  public int getSizeDifference()
  {
    return m_sizeDifference;
  }

  @Override
  public BaseWeaponProto.Style toProto()
  {
    return m_proto;
  }

  /**
   * Convert the proto value to the corresponding enum value.
   *
   * @param   inProto the proto value
   * @return  the converted enum value
   */
  public static WeaponStyle fromProto(BaseWeaponProto.Style inProto)
  {
    for(WeaponStyle style : values())
      if(style.m_proto == inProto)
        return style;

    throw new IllegalStateException("unknown weapon style: " + inProto);
  }

  /**
   * Convert the given string into a style.
   *
   * @param inValue the string representation
   * @return the matching style, if any
   */
  public static Optional<WeaponStyle> fromString(String inValue)
  {
    for(WeaponStyle style : values())
      if(style.getName().equalsIgnoreCase(inValue))
        return Optional.of(style);

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
    for(WeaponStyle style : values())
      names.add(style.getName());

    return names;
  }
}