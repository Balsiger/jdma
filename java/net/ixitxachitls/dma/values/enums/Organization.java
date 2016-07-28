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
 * @file Organization.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public enum Organization implements Named,
    Proto<BaseMonsterProto.Organization.Type>
{
  /** The unknown value. */
  UNKNOWN("Unknown", BaseMonsterProto.Organization.Type.UNKNOWN),

  /** Any organization. */
  ANY("Any", BaseMonsterProto.Organization.Type.ANY),

  /** Band organization. */
  BAND("Band", BaseMonsterProto.Organization.Type.BAND),

  /** Brood organization. */
  BROOD("Brood", BaseMonsterProto.Organization.Type.BROOD),

  /** Colony organization. */
  COLONY("Colony", BaseMonsterProto.Organization.Type.COLONY),

  /** Company organization. */
  COMPANY("Company", BaseMonsterProto.Organization.Type.COMPANY),

  /** Covey organization. */
  COVEY("Covey", BaseMonsterProto.Organization.Type.COVEY),

  /** Domestic organization. */
  DOMESTIC("Domesticated", BaseMonsterProto.Organization.Type.DOMESTICATED),

  /** Flight organization. */
  FLIGHT("Flight", BaseMonsterProto.Organization.Type.FLIGHT),

  /** Flock organization. */
  FLOCK("Flock", BaseMonsterProto.Organization.Type.FLOCK),

  /** Gang organization. */
  GANG("Gang", BaseMonsterProto.Organization.Type.GANG),

  /** Herd organization. */
  HERD("Herd", BaseMonsterProto.Organization.Type.HERD),
  HUNTING_PRTY("Hunting Party",
               BaseMonsterProto.Organization.Type.HUNTING_PARTY),
  TRADING_PARTY("Trading Party",
                BaseMonsterProto.Organization.Type.RAIDING_PARTY),
  RAIDING_PARTY("Raiding Party",
                BaseMonsterProto.Organization.Type.TRADING_PARTY),

  /** Infestation organization. */
  INFESTATION("Infestation", BaseMonsterProto.Organization.Type.INFESTATION),

  /** Nest organization. */
  NEST("Nest", BaseMonsterProto.Organization.Type.NEST),

  /** Pack organization. */
  PACK("Pack", BaseMonsterProto.Organization.Type.PACK),

  /** Pair organization. */
  PAIR("Pair", BaseMonsterProto.Organization.Type.PAIR),

  /** Patrol organization. */
  PATROL("Patrol", BaseMonsterProto.Organization.Type.PATROL),

  /** Slaver Brood organization. */
  SLAVER_BROOD("Slaver Brood",
               BaseMonsterProto.Organization.Type.SLAVER_BROOD),

  /** Solitary organization. */
  SOLITARY("Solitary", BaseMonsterProto.Organization.Type.SOLITARY),

  /** Squad organization. */
  SQUAD("Squad", BaseMonsterProto.Organization.Type.SQUAD),

  /** Storm organization. */
  STORM("Storm", BaseMonsterProto.Organization.Type.STORM),

  /** Swarm organization. */
  SWARM("Swarm", BaseMonsterProto.Organization.Type.SWARM),

  /** Tangle organization. */
  TANGLE("Tangle", BaseMonsterProto.Organization.Type.TANGLE),

  /** Troupe organization. */
  TRIBE("Tribe", BaseMonsterProto.Organization.Type.TRIBE),

  /** Troupe organization. */
  TROUPE("Troupe", BaseMonsterProto.Organization.Type.TROUPE),

  /** Troupe organization. */
  WARBAND("Warband", BaseMonsterProto.Organization.Type.WARBAND);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private BaseMonsterProto.Organization.Type m_proto;

  /** The parser for armor types. */
  public static final Parser<Organization> PARSER =
    new Parser<Organization>(1)
    {
      @Override
      public Optional<Organization> doParse(String inValue)
      {
        return Organization.fromString(inValue);
      }
    };

  /**
   * Create the enum value.
   *
   * @param inName the name of the value
   * @param inProto the proto enum value
   */
  private Organization(String inName,
                       BaseMonsterProto.Organization.Type inProto)
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
  public BaseMonsterProto.Organization.Type toProto()
  {
    return m_proto;
  }

  /**
   * Convert the given proto value into the corresponding organization value.
   *
   * @param inProto the proto value
   * @return the organization
   */
  public static Organization
    fromProto(BaseMonsterProto.Organization.Type inProto)
  {
    for(Organization organization : values())
      if(organization.m_proto == inProto)
        return organization;

    throw new IllegalArgumentException("cannot convert organization: "
                                       + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<Organization> fromString(String inValue)
  {
    for(Organization organization : values())
      if(organization.getName().equalsIgnoreCase(inValue))
        return Optional.of(organization);

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
    for(Organization organization : values())
      names.add(organization.getName());

    return names;
  }
}
