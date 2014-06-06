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
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.proto.Entries.MonsterProto;
import net.ixitxachitls.dma.proto.Entries.NPCProto;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Reference;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.Union;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.util.logging.Log;


/**
 * A non-player character.
 *
 * @file   NPC.javas
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */
public class NPC extends Monster
{
  //----- Gender ---------------------------------------------------------

  /** The possible gender types in the game. */
  public enum Gender implements EnumSelection.Named,
    EnumSelection.Proto<NPCProto.Gender>
  {
    /** Male. */
    MALE("Male", NPCProto.Gender.MALE),

    /** Female. */
    FEMALE("Female", NPCProto.Gender.FEMALE),

    /** Not known. */
    UNKNOWN("Unknown", NPCProto.Gender.UNKNOWN),

    /** Other. */
    OTHER("Other", NPCProto.Gender.OTHER);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    NPCProto.Gender m_proto;

    /**
     * Create the enum value.
     *
     * @param inName  the name of the value
     * @param inProto the proto enum value
     */
    private Gender(String inName, NPCProto.Gender inProto)
    {
      m_name = constant("monster.gender", inName);
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
    public NPCProto.Gender toProto()
    {
      return m_proto;
    }

    public static Gender fromProto(NPCProto.Gender inProto)
    {
      for(Gender gender : values())
        if(gender.m_proto == inProto)
          return gender;

      throw new IllegalArgumentException("cannot convert gender: " + inProto);
    }
  }

  //........................................................................

  /**
   * Create the NPC with no name.
   */
  public NPC()
  {
    super(TYPE);
  }

  /**
   * Create an NPC with the given name.
   *
   * @param inName the name of the monster
   */
  public NPC(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final Type<NPC> TYPE =
    new Type<NPC>(NPC.class, BaseMonster.TYPE);

  /** The type of the base entry to this entry. */
  public static final BaseType<BaseMonster> BASE_TYPE = BaseMonster.TYPE;

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The gender of the npc. */
  @Key("gender")
  protected EnumSelection<Gender> m_gender =
    new EnumSelection<Gender>(Gender.class);

  /** A special name for the npc, if any. */
  @Key("given name")
  protected Text m_givenName = new Text();

  /** THe levels the npc has. */
  @Key("levels")
  protected ValueList<Reference<BaseLevel>> m_levels =
    new ValueList<Reference<BaseLevel>>(", ",
      new Reference<BaseLevel>(BaseLevel.TYPE));

  static
  {
    extractVariables(NPC.class);
  }

  @Override
  public String dmName()
  {
    if(m_givenName.isDefined())
      return m_givenName.get() + " - " + super.dmName();

    return super.dmName();
  }

  /**
   * Get an overview of class levels.
   *
   * @return All the level abbreviation with the count attached
   */
  public SortedSet<String> levelsOverview()
  {
    Multiset<String> abbreviations = HashMultiset.create();

    for(Reference<BaseLevel> level : m_levels)
    {
      BaseLevel baseLevel = level.getEntry();
      if (baseLevel == null)
        abbreviations.add("invalid " + level);
      else
        if(baseLevel.getAbbreviation().isPresent())
          abbreviations.add(baseLevel.getAbbreviation().get());
        else
          abbreviations.add(baseLevel.getName());
    }

    SortedSet<String> classes = new TreeSet<>();
    for(String abbreviation : abbreviations)
      classes.add(abbreviation + abbreviations.count(abbreviation));

    return classes;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected <T extends Value<T>> void collect(String inName,
                                              Combined<T> ioCombined)
  {
    super.collect(inName, ioCombined);

    Multiset<String> levels = HashMultiset.create();
    for(Reference<BaseLevel> level : m_levels)
    {
      /*
      levels.add(level.getName());
      if(level.hasEntry())
         level.getEntry().collect(levels.count(level.getName()), inName,
                                  ioCombined,
                                  level.getName() + " "
                                  + levels.count(level.getName()));
                                  */
    }

    switch(inName)
    {
      case "hit dice":
        /*
        for(Reference<BaseLevel> level : m_levels)
          if(level.hasEntry())
          {
            BaseLevel baseLevel = level.getEntry();
            ioCombined.addValue((T)baseLevel.getHitDie(), baseLevel,
                                baseLevel.getAbbreviation());
          }
          */

        break;

      default:
        break;
    }
  }

  @Override
  public int getLevel()
  {
    int totalAdjustment = 0;
    Combined<Union> adjustments = collect("level adjustment");
    for (Union adjustment : adjustments.valuesOnly())
      if(adjustment.get() instanceof Number)
        totalAdjustment += ((Number)adjustment.get()).get();

    return m_levels.size() + totalAdjustment;
  }

  @Override
  public Message toProto()
  {
    NPCProto.Builder builder = NPCProto.newBuilder();

    builder.setBase((MonsterProto)super.toProto());

    if(m_gender.isDefined())
      builder.setGender(m_gender.getSelected().toProto());

    if(m_givenName.isDefined())
      builder.setGivenName(m_givenName.get());

    if(m_levels.isDefined())
      for(Reference<BaseLevel> level : m_levels)
        builder.addLevel(level.getName());

    NPCProto proto = builder.build();
    return proto;
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof NPCProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    NPCProto proto = (NPCProto)inProto;

    super.fromProto(proto.getBase());

    if(proto.hasGender())
      m_gender = m_gender.as(Gender.fromProto(proto.getGender()));

    if(proto.hasGivenName())
      m_givenName = m_givenName.as(proto.getGivenName());

    if(proto.getLevelCount() > 0)
    {
      List<Reference<BaseLevel>> levels = new ArrayList<>();
      for(String level : proto.getLevelList())
        levels.add(m_levels.createElement().as(level));

      m_levels = m_levels.as(levels);
    }
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(NPCProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }
}
