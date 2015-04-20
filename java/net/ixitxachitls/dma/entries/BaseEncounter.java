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

import com.google.common.base.Optional;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.proto.Entries.BaseEncounterProto;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Group;
import net.ixitxachitls.util.logging.Log;

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
    new BaseType.Builder<>(BaseEncounter.class).build();

  /** The default serial version id. */
  private static final long serialVersionUID = 1L;

  /** The adventure this encounter takes places. */
  protected Optional<String> m_adventure = Optional.absent();

  /** The location where this encounter takes place. */
  // TODO: long term, this should be a link to a location object
  protected List<String> m_locations = new ArrayList<>();

  /** A description of the doors. */
  protected Optional<String> m_doors = Optional.absent();

  /** A description of the floor. */
  protected Optional<String> m_floor = Optional.absent();

  /** A description of the ceiling. */
  protected Optional<String> m_ceiling = Optional.absent();

  /** A description of the walls. */
  protected Optional<String> m_walls = Optional.absent();

  /** A description of the temperature. */
  protected Optional<String> m_feel = Optional.absent();

  /** A description of the sounds. */
  protected Optional<String> m_sound = Optional.absent();

  /** A description of the odors. */
  protected Optional<String> m_smell = Optional.absent();

  /** A description of the odors. */
  protected Optional<String> m_taste = Optional.absent();

  /** A description of the light. */
  protected Optional<String> m_light = Optional.absent();

  /** The base skills relevant for this encounter. */
  protected List<String> m_skills = new ArrayList<>();

  /**
   * Get the name of the adventuer for this encounter.
   *
   * @return the adventure name (id)
   */
  public Optional<String> getAdventure()
  {
    return m_adventure;
  }

  /**
   * Get a list of locations for this encounter.
   *
   * @return the list of locations
   */
  public List<String> getLocations()
  {
    return m_locations;
  }

  /**
   * Get a description about the doors in the encounter.
   *
   * @return the door description
   */
  public Optional<String> getDoors()
  {
    return m_doors;
  }

  /**
   * Get a description about the floor in the encounter.
   *
   * @return the floor description
   */
  public Optional<String> getFloor()
  {
    return m_floor;
  }

  /**
   * Get a description about the ceiling in the encounter.
   *
   * @return the ceiling description
   */
  public Optional<String> getCeiling()
  {
    return m_ceiling;
  }

  /**
   * Get a description about the walls in the encounter.
   *
   * @return the wall description
   */
  public Optional<String> getWalls()
  {
    return m_walls;
  }

  /**
   * Get a description about how the encounter feels.
   *
   * @return the encounter feel
   */
  public Optional<String> getFeel()
  {
    return m_feel;
  }

  /**
   * Get a description about the sounds in the encounter.
   *
   * @return the sounds in the encounter
   */
  public Optional<String> getSound()
  {
    return m_sound;
  }

  /**
   * Get a description about the smells at and during the encounter.
   *
   * @return the smells
   */
  public Optional<String> getSmell()
  {
    return m_smell;
  }

  /**
   * Get a description about the tasts at and during the encounter.
   *
   * @return the tastes
   */
  public Optional<String> getTaste()
  {
    return m_taste;
  }

  /**
   * Get a description about the light conditions at and during the encounter.
   *
   * @return the lightning conditions
   */
  public Optional<String> getLight()
  {
    return m_light;
  }

  /**
   * Get a list of skills relevant to the encounter.
   *
   * @return the list of relevant skill names
   */
  public List<String> getSkills()
  {
    return m_skills;
  }

  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().hasAccess(Group.DM);
  }

  @Override
  public void setValues(Values inValues)
  {
    super.setValues(inValues);

    m_adventure = inValues.use("adventure", m_adventure);
    m_locations = inValues.use("location", m_locations);
    m_doors = inValues.use("doors", m_doors);
    m_floor = inValues.use("floor", m_floor);
    m_ceiling = inValues.use("ceiling", m_ceiling);
    m_walls = inValues.use("walls", m_walls);
    m_feel = inValues.use("feel", m_feel);
    m_ceiling = inValues.use("ceiling", m_ceiling);
    m_walls = inValues.use("walls", m_walls);
    m_feel = inValues.use("feel", m_feel);
    m_sound = inValues.use("sound", m_sound);
    m_smell = inValues.use("smell", m_smell);
    m_taste = inValues.use("taste", m_taste);
    m_light = inValues.use("light", m_light);
    m_skills = inValues.use("skill", m_skills);
  }

  @Override
  public Message toProto()
  {
    BaseEncounterProto.Builder builder = BaseEncounterProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    if(m_adventure.isPresent())
      builder.setAdventure(m_adventure.get());

    for(String location : m_locations)
        builder.addLocation(location);

    if(m_doors.isPresent())
      builder.setDoors(m_doors.get());

    if(m_floor.isPresent())
      builder.setFloor(m_floor.get());

    if(m_ceiling.isPresent())
      builder.setCeiling(m_ceiling.get());

    if(m_walls.isPresent())
      builder.setWalls(m_walls.get());

    if(m_feel.isPresent())
      builder.setFeel(m_feel.get());

    if(m_sound.isPresent())
      builder.setSound(m_sound.get());

    if(m_smell.isPresent())
      builder.setSmell(m_smell.get());

    if(m_taste.isPresent())
      builder.setTaste(m_taste.get());

    if(m_light.isPresent())
      builder.setLight(m_light.get());

    for(String skill : m_skills)
        builder.addSkill(skill);

    BaseEncounterProto proto = builder.build();
    return proto;
  }

  /**
   * Read values for the entry from the given proto.
   *
   * @param inProto the proto to read from
   */
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
      m_adventure = Optional.of(proto.getAdventure());

    if(proto.getLocationCount() > 0)
      for(String location : proto.getLocationList())
        m_locations.add(location);

    if(proto.hasDoors())
      m_doors = Optional.of(proto.getDoors());

    if(proto.hasFloor())
      m_floor = Optional.of(proto.getFloor());

    if(proto.hasCeiling())
      m_ceiling = Optional.of(proto.getCeiling());

    if(proto.hasWalls())
      m_walls = Optional.of(proto.getWalls());

    if(proto.hasFeel())
      m_feel = Optional.of(proto.getFeel());

    if(proto.hasSound())
      m_sound = Optional.of(proto.getSound());

    if(proto.hasSmell())
      m_smell = Optional.of(proto.getSmell());

    if(proto.hasTaste())
      m_taste = Optional.of(proto.getTaste());

    if(proto.hasLight())
      m_light = Optional.of(proto.getLight());

    if(proto.getSkillCount() > 0)
      for(String skill : proto.getSkillList())
        m_skills.add(skill);
  }

  @Override
  protected Message defaultProto()
  {
    return BaseEncounterProto.getDefaultInstance();
  }
}
