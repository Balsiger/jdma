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

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto;
import net.ixitxachitls.dma.values.Parser;

/**
 * The possible terrains in the game.
 *
 * @file Terrain.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public enum Terrain implements Named,
    Proto<BaseMonsterProto.Terrain>
{
  /** The Unknown value. */
  UNKNOWN("Unknown", BaseMonsterProto.Terrain.UNKNOWN_TERRAIN),

  /** Forest terrain. */
  FOREST("Forest", BaseMonsterProto.Terrain.FOREST),

  /** Marsh terrain. */
  MARSH("Marsh", BaseMonsterProto.Terrain.MARSH),

  /** Hills terrain. */
  HILLS("Hills", BaseMonsterProto.Terrain.HILLS),

  /** Mountain terrain. */
  MOUNTAIN("Mountain", BaseMonsterProto.Terrain.MOUNTAIN),

  /** Desert terrain. */
  DESERT("Desert", BaseMonsterProto.Terrain.DESERT),

  /** Plains terrain. */
  PLAINS("Plains", BaseMonsterProto.Terrain.PLAINS),

  /** Aquatic terrain. */
  AQUATIC("Aquatic", BaseMonsterProto.Terrain.AQUATIC_TERRAIN),

  /** Underground terrain. */
  UNDERGROUND("Underground", BaseMonsterProto.Terrain.UNDERGROUND),

  /** Infernal Battlefield of Acheron terrain. */
  INFENRAL_BATTLEFIELD_OF_ACHERON
  ("Infernal Battlefield of Acheron",
   BaseMonsterProto.Terrain.INFERNAL_BATTLEFIELD_OF_ACHERON),

  /** Infinite Layers of the Abyss terrain. */
  INFINITE_LAYERS_OF_THE_ABYSS
  ("Infinite Layers of the Abyss",
   BaseMonsterProto.Terrain.INFINITE_LAYERS_OF_THE_ABYSS),

  /** Elemental Plane of Air. */
  ELEMENTAL_PLANE_OF_AIR("Elemental Plane of Air",
                         BaseMonsterProto.Terrain.ELEMENTAL_PLANE_OF_AIR),

  /** Elemental Plane of Earth. */
  ELEMENTAL_PLANE_OF_EARTH("Elemental Plane of Earth",
                           BaseMonsterProto.Terrain.ELEMENTAL_PLANE_OF_EARTH),

  /** Elemental Plane of Fire. */
  ELEMENTAL_PLANE_OF_FIRE("Elemental Plane of Fire",
                          BaseMonsterProto.Terrain.ELEMENTAL_PLANE_OF_FIRE),

  /** Elemental Plane of Water. */
  ELEMENTAL_PLANE_OF_WATER("Elemental Plane of Water",
                           BaseMonsterProto.Terrain.ELEMENTAL_PLANE_OF_WATER),

  /** Windswept dephts of pandemonium. */
  WINDSWEPT_DEPTHS_OF_PANDEMONIUM
  ("Windswept Depths of Pandemonium",
   BaseMonsterProto.Terrain.WINDSWEPT_DEPTHS_OF_PANDEMONIUM),

  /** Any terrain. */
  ANY("Any", BaseMonsterProto.Terrain.ANY_TERRAIN);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private BaseMonsterProto.Terrain m_proto;

  /** The parser for armor types. */
  public static final Parser<Terrain> PARSER =
    new Parser<Terrain>(1)
    {
      @Override
      public Optional<Terrain> doParse(String inValue)
      {
        return Terrain.fromString(inValue);
      }
    };

  /**
   * Create the enum value.
   *
   * @param inName the name of the value
   * @param inProto the proto enum value
   */
  private Terrain(String inName, BaseMonsterProto.Terrain inProto)
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
  public BaseMonsterProto.Terrain toProto()
  {
    return m_proto;
  }

  /**
   * Convert the proto to the corresponding enum value.
   *
   * @param inProto the proto to convert
   * @return the corresponding enum value
   */
  public static Terrain fromProto(BaseMonsterProto.Terrain inProto)
  {
    for(Terrain terrain : values())
      if(terrain.m_proto == inProto)
        return terrain;

    throw new IllegalArgumentException("cannot convert terrain: " + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<Terrain> fromString(String inValue)
  {
    for(Terrain terrain : values())
      if(terrain.getName().equalsIgnoreCase(inValue))
        return Optional.of(terrain);

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
    for(Terrain terrain : values())
      names.add(terrain.getName());

    return names;
  }
}
