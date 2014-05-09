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

//--------------------------------------------------------------------- Imports

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.proto.Entries.BaseEncounterProto;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.values.LongFormattedText;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.util.logging.Log;

//.............................................................................

/**
 * A base encounter.
 *
 * @file   BaseEncounter.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class BaseEncounter extends BaseEntry
{
  /**
   * Create the encounter.
   */
  public BaseEncounter()
  {
    super(TYPE);
  }

  /**
   * Create the encounter with the given name.
   *
   * @param inName  the name of the base encounter
   */
  public BaseEncounter(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final BaseType<BaseEncounter> TYPE =
    new BaseType<>(BaseEncounter.class);

  /** The default serial version id. */
  private static final long serialVersionUID = 1L;

  /** The adventure this encounter takes places. */
  @Key("adventure")
  protected Text m_adventure = new Text();

  /** The location where this encounter takes place. */
  // TODO: long term, this should be a link to a location object
  @Key("location")
  protected ValueList<Name> m_location = new ValueList<Name>(new Name());

  /** A description of the doors. */
  @Key("doors")
  protected LongFormattedText m_doors = new LongFormattedText();

  /** A description of the floor. */
  @Key("floor")
  protected LongFormattedText m_floor = new LongFormattedText();

  /** A description of the ceiling. */
  @Key("ceiling")
  protected LongFormattedText m_ceiling = new LongFormattedText();

  /** A description of the walls. */
  @Key("walls")
  protected LongFormattedText m_walls = new LongFormattedText();

  /** A description of the temperature. */
  @Key("feel")
  protected LongFormattedText m_feel = new LongFormattedText();

  /** A description of the sounds. */
  @Key("sound")
  protected LongFormattedText m_sound = new LongFormattedText();

  /** A description of the odors. */
  @Key("smell")
  protected LongFormattedText m_smell = new LongFormattedText();

  /** A description of the odors. */
  @Key("taste")
  protected LongFormattedText m_taste = new LongFormattedText();

  /** A description of the light. */
  @Key("light")
  protected LongFormattedText m_light = new LongFormattedText();

  /** The base skills relevant for this encounter. */
  @Key("skills")
  protected ValueList<Name> m_skills = new ValueList<Name>(new Name());

  static
  {
    extractVariables(BaseEncounter.class);
  }

  @Override
  public boolean isDM(@Nullable BaseCharacter inUser)
  {
    if(inUser == null)
      return false;

    return inUser.hasAccess(BaseCharacter.Group.DM);
  }

  @Override
  public Message toProto()
  {
    BaseEncounterProto.Builder builder = BaseEncounterProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    if(m_adventure.isDefined())
      builder.setAdventure(m_adventure.get());

    if(m_location.isDefined())
      for(Name location : m_location)
        builder.addLocation(location.get());

    if(m_doors.isDefined())
      builder.setDoors(m_doors.get());

    if(m_floor.isDefined())
      builder.setFloor(m_floor.get());

    if(m_ceiling.isDefined())
      builder.setCeiling(m_ceiling.get());

    if(m_walls.isDefined())
      builder.setWalls(m_walls.get());

    if(m_feel.isDefined())
      builder.setFeel(m_feel.get());

    if(m_sound.isDefined())
      builder.setSound(m_sound.get());

    if(m_smell.isDefined())
      builder.setSmell(m_smell.get());

    if(m_taste.isDefined())
      builder.setTaste(m_taste.get());

    if(m_light.isDefined())
      builder.setLight(m_light.get());

    if(m_skills.isDefined())
      for(Name skill : m_skills)
        builder.addSkill(skill.get());

    BaseEncounterProto proto = builder.build();
    return proto;
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseEncounterProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    BaseEncounterProto proto = (BaseEncounterProto)inProto;

    super.fromProto(proto.getBase());

    if(proto.hasAdventure())
      m_adventure = m_adventure.as(proto.getAdventure());

    if(proto.getLocationCount() > 0)
    {
      List<Name> locations = new ArrayList<>();
      for(String location : proto.getLocationList())
        locations.add(m_location.createElement().as(location));

      m_location = m_location.as(locations);
    }

    if(proto.hasDoors())
      m_doors = m_doors.as(proto.getDoors());

    if(proto.hasFloor())
      m_floor = m_floor.as(proto.getFloor());

    if(proto.hasCeiling())
      m_ceiling = m_ceiling.as(proto.getCeiling());

    if(proto.hasWalls())
      m_walls = m_walls.as(proto.getWalls());

    if(proto.hasFeel())
      m_feel = m_feel.as(proto.getFeel());

    if(proto.hasSound())
      m_sound = m_sound.as(proto.getSound());

    if(proto.hasSmell())
      m_smell = m_smell.as(proto.getSmell());

    if(proto.hasTaste())
      m_taste = m_taste.as(proto.getTaste());

    if(proto.hasLight())
      m_light = m_light.as(proto.getLight());

    if(proto.getSkillCount() > 0)
    {
      List<Name> skills = new ArrayList<>();
      for(String skill : proto.getSkillList())
        skills.add(m_skills.createElement().as(skill));

      m_skills = m_skills.as(skills);
    }
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(BaseEncounterProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }
}
