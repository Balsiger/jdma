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

import net.ixitxachitls.dma.proto.Entries.BaseItemProto;
import net.ixitxachitls.dma.values.enums.Named;

/** The possible sizes in the game. */
public enum Substance implements Named
{
  /** Unknown substance,. */
  UNKNOWN("unknown", 0, 0, BaseItemProto.Substance.Material.UNKNOWN),

  /** Made of paper. */
  PAPER("paper", 0, 2, BaseItemProto.Substance.Material.PAPER),

  /** Made of cloth. */
  CLOTH("cloth", 0, 2, BaseItemProto.Substance.Material.CLOTH),

  /** Made of rope. */
  ROPE("rope", 0, 2, BaseItemProto.Substance.Material.ROPE),

  /** Made of glass. */
  GLASS("glass", 1, 1, BaseItemProto.Substance.Material.GLASS),

  /** Made of ice. */
  ICE("ice", 0, 3, BaseItemProto.Substance.Material.ICE),

  /** Made of leather. */
  LEATHER("leather", 2, 5, BaseItemProto.Substance.Material.LEATHER),

  /** Made of hide. */
  HIDE("hide", 2, 5, BaseItemProto.Substance.Material.HIDE),

  /** Made of wood. */
  WOOD("wood", 5, 10, BaseItemProto.Substance.Material.WOOD),

  /** Made of stone. */
  STONE("stone", 8, 15, BaseItemProto.Substance.Material.STONE),

  /** Made of iron. */
  IRON("iron", 10, 30, BaseItemProto.Substance.Material.IRON),

  /** Made of steel. */
  STEEL("steel", 10, 30, BaseItemProto.Substance.Material.STEEL),

  /** Made of crystal. */
  CRYSTAL("crystal", 10, 30, BaseItemProto.Substance.Material.CRYSTAL),

  /** Made of mithral. */
  MITHRAL("mithral", 15, 30, BaseItemProto.Substance.Material.MITHRAL),

  /** Made of adamantine. */
  ADAMANTINE("adamantine", 20, 40,
             BaseItemProto.Substance.Material.ADAMANTINE),

  /** Made of bone. */
  BONE("bone", 5, 10, BaseItemProto.Substance.Material.BONE);

  /** The value's name. */
  private String m_name;

  /** The hardness of the substance. */
  private int m_hardness;

  /** The hit points per inch. */
  private int m_hp;

  /** The proto enum value. */
  private BaseItemProto.Substance.Material m_proto;

  /** The parser for substance values. */
  public static final Parser<Substance> PARSER =
    new Parser<Substance>(1)
    {
      @Override
      public Optional<Substance> doParse(String inValue)
      {
        return Substance.fromString(inValue);
      }
    };

  /** Create the name.
   *
   * @param inName     the name of the value
   * @param inHardness the hardness of the material
   * @param inHP       the hit points of the material
   * @param inProto    the proto enum value
   */
  private Substance(String inName, int inHardness, int inHP,
                    BaseItemProto.Substance.Material inProto)
  {
    m_name = inName;
    m_hardness = inHardness;
    m_hp = inHP;
    m_proto = inProto;
  }

  /** Get the name of the value.
   *
   * @return the name of the value
   *
   */
  @Override
  public String getName()
  {
    return m_name;
  }

  /** Get the hardness for this substance type.
   *
   * @return the hardness
   *
   */
  public int hardness()
  {
    return m_hardness;
  }

  /** Get the hit points per inch of the substance.
   *
   * @return the (maxiaml) hit points
   *
   */
  public int hp()
  {
    return m_hp;
  }

  /** Convert to a human readable string.
   *
   * @return the converted string
   *
   */
  @Override
  public String toString()
  {
      return m_name;
  }

  /**
   * Get the proto value for this value.
   *
   * @return the proto enum value
   */
  public BaseItemProto.Substance.Material toProto()
  {
    return m_proto;
  }

  /**
   * Get the group matching the given proto value.
   *
   * @param  inProto     the proto value to look for
   * @return the matched enum (will throw exception if not found)
   */
  public static Substance fromProto(BaseItemProto.Substance.Material inProto)
  {
    for(Substance substance: values())
      if(substance.m_proto == inProto)
        return substance;

    throw new IllegalStateException("invalid proto substance: " + inProto);
  }

  /**
   * All the possible names for the layout.
   *
   * @return the possible names
   */
  public static List<String> names()
  {
    List<String> names = new ArrayList<>();

    for(Substance substance : values())
      names.add(substance.getName());

    return names;
  }

  /**
   * Get the substance matching the given text.
   */
  public static Optional<Substance> fromString(String inText)
  {
    for(Substance substance : values())
      if(substance.m_name.equalsIgnoreCase(inText))
        return Optional.of(substance);

    return Optional.absent();
  }
}