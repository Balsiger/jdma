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
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.proto.Entries.CampaignEntryProto;
import net.ixitxachitls.dma.proto.Entries.EncounterProto;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.util.logging.Log;

/**
 * An actual encounter that can happen in the game.
 *
 * @file   Encounter.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Encounter extends CampaignEntry
{
  /**
   * Create a default, unnamed encounter.
   */
  public Encounter()
  {
    super(TYPE);
  }

  /**
   * Create an encounter with the given name.
   *
   * @param inName the name of the encounter
   */
  public Encounter(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final Type<Encounter> TYPE =
    new Type.Builder<>(Encounter.class, BaseEncounter.TYPE).build();

  /** The type of the base entry to this entry. */
  public static final BaseType<BaseEncounter> BASE_TYPE = BaseEncounter.TYPE;

  /** The serial version uid. */
  private static final long serialVersionUID = 1L;

  /** The number of XP per encounter level and character level
   * (according to DMG p. 39 and Epic Level Handbook p. 121) */
  public static final int [][] XP =
  {
    // EL 1
    {   300,   300,   300,   300,   300,   300,   263,   200,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 2
    {   600,   600,   600,   600,   500,   450,   350,   300,   225,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 3
    {   900,   900,   900,   800,   750,   600,   525,   400,   338,   250,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 4
    {  1350,  1350,  1350,  1200,  1000,   900,   700,   600,   450,   375,
        275,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 5
    {  1800,  1800,  1800,  1600,  1500,  1200,  1050,   800,   675,   500,
       413,    300,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 6
    {  2700,  2700,  2700,  2400,  2250,  1800,  1400,  1200,   900,   750,
        550,   450,   325,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 7
    {  3600,  3600,  3600,  3200,  3000,  2700,  2100,  1600,  1350,  1000,
        825,   600,   488,   350,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 8
    {  5400,  5400,  5400,  4800,  4500,  3600,  3150,  2400,  1800,  1500,
       1100,   900,   650,   525,   375,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 9
    {  7200,  7200,  7200,  6400,  6000,  5400,  4200,  3600,  2700,  2000,
       1650,  1200,   975,   700,   563,   400,     0,      0,    0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 10
    { 10800, 10800, 10800,  9600,  9000,  7200,  6300,  4800,  4050,  3000,
       2200,  1800,  1300,  1050,   750,   600,   425,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 11
    {     0,     0,     0, 12800, 12000, 10800,  8400,  7200,  5400,  4500,
       3300,  2400,  1950,  1400,  1125,   800,   638,   450,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 12
    {     0,     0,     0,     0, 18000, 14400, 12600,  9600,  8100,  6000,
       4950,  3600,  2600,  2100,  1500,  1200,   850,   675,   475,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 13
    {     0,     0,     0,     0,     0, 21600, 16800, 14400, 10800,  9000,
       6600,  5400,  3900,  2800,  2250,  1600,  1275,   900,   713,   500,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 14
    {     0,     0,     0,     0,     0,     0, 25200, 19200, 16200, 12000,
       9900,  7200,  5850,  4200,  3000,  2400,  1700,  1350,   950,   750,
        525,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 15
    {     0,     0,     0,     0,     0,     0,     0, 28800, 21600, 18000,
      13200, 10800,  7800,  6300,  4500,  3200,  2550,  1800,  1425,  1000,
        788,   550,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 16
    {     0,     0,     0,     0,     0,     0,     0,     0, 32400, 24000,
      19800, 14400, 11700,  8400,  6750,  4800,  3400,  2700,  1900,  1500,
       1050,   825,   575,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 17
    {     0,     0,     0,     0,     0,     0,     0,     0,     0, 36000,
      26400, 21600, 15600, 12600,  9000,  7200,  5100,  3600,  2850,  2000,
       1575,  1100,   863,   600,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 18
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      39600, 28800, 23400, 16800, 13500,  9600,  7650,  5400,  3800,  3000,
       2100,  1650,  1150,   900,   625,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 19
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0, 43200, 31200, 25200, 18000, 14400, 10200,  8100,  5700,  4000,
       4200,  3300,  2300,  1800,   938,   650,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 20
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0, 46800, 33600, 27000, 19200, 15300, 10800,  8550,  6000,
       4200,  3300,  2300,  1800,  1250,   975,   675,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 21
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0, 50400, 36000, 28800, 20400, 16200, 11400,  8000,
       6300,  4400,  3450,  2400,  1875,  1300,  1013,   700,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 22
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0, 54000, 48400, 30600, 21600, 17100, 12000,
       8400,  6600,  4600,  3600,  2500,  1950,  1350,  1050,   725,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 23
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0, 57600, 40800, 32400, 22800, 16000,
      12600,  8800,  6900,  4800,  3750,  2600,  2025,  1400,   1088,  750,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 24
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0, 61200, 43200, 34200, 24000,
      16800, 13200,  9200,  7200,  5000,  3900,  2700,  2100,  1450,  1125,
        775,     0,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 25
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0, 64800, 45600, 32000,
      25200, 17600, 13800,  9600,  7500,  5200,  4050,  2800,  2175,  1500,
       1163,   800,     0,     0,     0,     0,     0,     0,     0,     0, },
    // EL 26
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0, 68400, 48000,
      33600, 26400, 18400, 14400, 10000,  7800,  5400,  4200,  2900,  2250,
       1550,  1200,   825,     0,     0,     0,     0,     0,     0,     0, },
    // EL 27
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0, 64000,
      50400, 35200, 27600, 19200, 15000, 10400,  8100,  5600,  4350,  3000,
       2345,  1600, 1238,    850,     0,     0,     0,     0,     0,     0, },
    // EL 28
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      67200, 52800, 36800, 28800, 20000, 15600, 10800,  8400,  5800,  4500,
       3100,  2400, 1650,   1275,   875,     0,     0,     0,     0,     0, },
    // EL 29
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0, 70400, 55200, 38400, 30000, 20800, 16200, 11200,  8700,  6000,
       4650,  3200, 2475,   1700,  1313,   900,     0,     0,     0,     0, },
    // EL 30
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0, 73600, 57600, 40000, 31200, 21600, 16800, 11600,  9000,
       6200,  4800,  3300,  2550,  1750,  1350,   925,     0,     0,     0, },
    // EL 31
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0, 76800, 60000, 41600, 32400, 22400, 17400, 12000,
       9300,  6400,  4950,  3400,  2625,  1800,  1388,   950,     0,     0, },
    // EL 32
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0, 80000, 62400, 44800, 34800, 24000, 18000,
      12800,  9900,  6600,  5100,  3500,  2700,  1850,  1425,   975,     0, },
    // EL 33
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0, 83200, 64800, 44800, 34800, 24000,
      18600, 12800,  9900,  6800,  5250,  3600,  1850,  2775,  1463,  1000, },
    // EL 34
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0, 86400, 67200, 46400, 36000,
      24800, 19200, 13200, 10200,  7000,  5400,  3700,  2850,  1950,  1500, },
    // EL 35
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0, 89600, 69600, 48000,
      37200, 25600, 19800, 13600, 10500,  7200,  5500,  3800,  2925,  2000, },
    // EL 36
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0, 92800, 72000,
      49600, 38400, 26400, 20400, 14000, 10800,  7400,  5700,  3900,  3000, },
    // EL 37
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0, 96000,
      74400, 51200, 39600, 27200, 21000, 14400, 11100,  7600,  5850,  4000, },
    // EL 38
    {     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      99200, 76800, 52800, 40800, 28000, 21600, 14800, 11400,  7800,  6000, },
    // EL 39
    {     0,      0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,      0,     0,     0,     0,     0,     0,     0,     0,     0,
          0,      0,     0,     0,     0,     0,     0,     0,     0,     0,
          0, 102400, 79200, 54400, 42000, 28800, 22200, 15200, 11700,  8000, },
    // EL 40
    {    0,     0,      0,     0,     0,     0,     0,     0,     0,     0,
         0,     0,      0,     0,     0,     0,     0,     0,     0,     0,
         0,     0,      0,     0,     0,     0,     0,     0,     0,     0,
         0,     0, 105600, 81600, 56000, 43200, 29600, 22800, 15600, 12000, },
  };

  /** The encounter number (for sorting). */
  protected Optional<String> m_number = Optional.absent();

  /** The monster that can potentially be encountered. */
  protected Optional<List<Monster>> m_monsters = Optional.absent();

  /** The items that can potentially be encountered. */
  protected Optional<List<Item>> m_items = Optional.absent();

  /** The traps in the encounter. */
  protected List<String> m_traps = new ArrayList<>();

  /** The hazards in the encounter. */
  protected List<String> m_hazards = new ArrayList<>();

  /** The obstacles in the encounter. */
  protected List<String> m_obstacles = new ArrayList<>();

  /** The secrets in the encounter. */
  protected List<String> m_secrets = new ArrayList<>();

  /** The events in the encounter. */
  protected List<String> m_events = new ArrayList<>();

  /** Any special rules for the encounter. */
  protected List<String> m_rules = new ArrayList<>();

  /** Spells relevant to this encounter. */
  protected List<String> m_spells = new ArrayList<>();

  /** The encounter level. */
  protected List<Integer> m_els = new ArrayList<>();

  /** The location where this encounter takes place. */
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

  /** The initial encounter distance. */
  protected Optional<Distance> m_distance = Optional.absent();

  @Override
  public Message toProto()
  {
    EncounterProto.Builder builder = EncounterProto.newBuilder();

    builder.setBase((CampaignEntryProto)super.toProto());

    if(m_number.isPresent())
      builder.setNumber(m_number.get());

    builder.addAllTrap(m_traps);
    builder.addAllHazard(m_hazards);
    builder.addAllObstacle(m_obstacles);
    builder.addAllSecret(m_secrets);
    builder.addAllEvent(m_events);
    builder.addAllRule(m_rules);
    builder.addAllSpell(m_spells);
    builder.addAllEncounterLevel(m_els);
    builder.addAllLocation(m_locations);
    builder.addAllSkill(m_skills);

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
      builder.setDoors(m_taste.get());
    if(m_light.isPresent())
      builder.setDoors(m_light.get());
    if(m_distance.isPresent())
      builder.setDistance(m_distance.get().toProto());

    EncounterProto proto = builder.build();
    return proto;
  }

  /**
   * Set the value from the given proto.
   *
   * @param inProto the proto with the values
   */
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof EncounterProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    EncounterProto proto = (EncounterProto)inProto;

    super.fromProto(proto.getBase());

    if(proto.hasNumber())
      m_number = Optional.of(proto.getNumber());

    m_traps = proto.getTrapList();
    m_hazards = proto.getHazardList();
    m_obstacles = proto.getObstacleList();
    m_secrets = proto.getSecretList();
    m_events = proto.getEventList();
    m_rules = proto.getRuleList();
    m_spells = proto.getSpellList();
    m_els = proto.getEncounterLevelList();
    m_locations = proto.getLocationList();
    m_skills = proto.getSkillList();

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

    if(proto.hasDistance())
      m_distance = Optional.of(Distance.fromProto(proto.getDistance()));
  }

  @Override
  protected Message defaultProto()
  {
    return EncounterProto.getDefaultInstance();
  }
}
