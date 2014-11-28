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

import net.ixitxachitls.dma.proto.Values.SharedProto;
import net.ixitxachitls.dma.values.Parser;

/** The possible spell classes. */
public enum SpellClass implements Named, net.ixitxachitls.dma.values.enums.Short,
    Proto<SharedProto.SpellClass>
{
  /** Unknown. */
  UNKNOWN("Unknown", "Unk", SharedProto.SpellClass.UNKNOWN),

  /** Assassin. */
  ASSASSIN("Assassin", "Asn", SharedProto.SpellClass.ASSASSIN),

  /** Bard. */
  BARD("Bard", "Brd", SharedProto.SpellClass.BARD),

  /** Clecric. */
  CLERIC("Cleric", "Clr", SharedProto.SpellClass.CLERIC),

  /** Druid. */
  DRUID("Druid", "Drd", SharedProto.SpellClass.DRUID),

  /** Paladin. */
  PALADIN("Paladin", "Pal", SharedProto.SpellClass.PALADIN),

  /** Ranger. */
  RANGER("Ranger", "Rgr", SharedProto.SpellClass.RANGER),

  /** Sorcerer. */
  SORCERER("Sorcerer", "Sor", SharedProto.SpellClass.SORCERER),

  /** Wizard. */
  WIZARD("Wizard", "Wiz", SharedProto.SpellClass.WIZARD),

  /** Air. */
  AIR("Air", "Air", SharedProto.SpellClass.AIR),

  /** Animal. */
  ANIMAL("Animal", "Animal", SharedProto.SpellClass.ANIMAL),

  /** Chaos. */
  CHAOS("Chaos", "Chaos", SharedProto.SpellClass.CHAOS),

  /** Death. */
  DEATH("Death", "Death", SharedProto.SpellClass.DEATH),

  /** Destruction. */
  DESTRUCTION("Destruction", "Destruction",
              SharedProto.SpellClass.DESTRUCTION),

  /** Drow. */
  DROW("Drow", "Drow", SharedProto.SpellClass.DROW),

  /** Earth. */
  EARTH("Earth", "Earth", SharedProto.SpellClass.EARTH),

  /** Evil. */
  EVIL("Evil", "Evil", SharedProto.SpellClass.EVIL),

  /** Fire. */
  FIRE("Fire", "Fire", SharedProto.SpellClass.FIRE),

  /** Good. */
  GOOD("Good", "Good", SharedProto.SpellClass.GOOD),

  /** Healing. */
  HEALING("Healing", "Healing", SharedProto.SpellClass.HEALING),

  /** Knowledge. */
  KNOWLEDGE("Knowledge", "Knowledge", SharedProto.SpellClass.KNOWLEDGE),

  /** Law. */
  LAW("Law", "Law", SharedProto.SpellClass.LAW),

  /** Luck. */
  LUCK("Luck", "Luck", SharedProto.SpellClass.LUCK),

  /** Magic. */
  MAGIC("Magic", "Magic", SharedProto.SpellClass.MAGIC),

  /** Plant. */
  PLANT("Plant", "Plant", SharedProto.SpellClass.PLANT),

  /** Protection. */
  PROTECTION("Protection", "Protection",
             SharedProto.SpellClass.PROTECTION),

  /** Strength. */
  STRENGTH("Strength", "Strength", SharedProto.SpellClass.STRENGTH),

  /** Sun. */
  SUN("Sun", "Sun", SharedProto.SpellClass.SUN),

  /** Travel. */
  TRAVEL("Travel", "Travel", SharedProto.SpellClass.TRAVEL),

  /** Trickery. */
  TRICKERY("Trickery", "Trickery", SharedProto.SpellClass.TRICKERY),

  /** War. */
  WAR("War", "War", SharedProto.SpellClass.WAR),

  /** Water. */
  Water("Water", "Water", SharedProto.SpellClass.WATER);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private SharedProto.SpellClass m_proto;

  /** The value's short name. */
  private String m_short;

  /** The parser for armor types. */
  public static final Parser<SpellClass> PARSER =
    new Parser<SpellClass>(1)
    {
      @Override
      public Optional<SpellClass> doParse(String inValue)
      {
        return SpellClass.fromString(inValue);
      }
    };

  /**
   * Create the name.
   *
   * @param inName       the name of the value
   * @param inShort      the short name of the value
   * @param inProto      the proto enum value
   */
  private SpellClass(String inName, String inShort,
                     SharedProto.SpellClass inProto)
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
  public String getShort()
  {
    return m_short;
  }

 @Override
 public String toString()
 {
   return m_name;
 }

 @Override
 public SharedProto.SpellClass toProto()
 {
   return m_proto;
 }

 /**
  * Get the group matching the given proto value.
  *
  * @param  inProto     the proto value to look for
  * @return the matched enum (will throw exception if not found)
  */
 public static SpellClass fromProto(SharedProto.SpellClass inProto)
 {
   for(SpellClass spellClass: values())
     if(spellClass.m_proto == inProto)
       return spellClass;

   throw new IllegalStateException("invalid proto class: " + inProto);
 }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<SpellClass> fromString(String inValue)
  {
    for(SpellClass value : values())
      if(value.getName().equalsIgnoreCase(inValue))
        return Optional.of(value);

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
    for(SpellClass value : values())
      names.add(value.getName());

    return names;
  }
}