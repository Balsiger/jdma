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

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.proto.Entries.CampaignEntryProto;
import net.ixitxachitls.dma.proto.Entries.EncounterProto;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.LongFormattedText;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Reference;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.util.logging.Log;

/**
 * An actual encounter that can happen in the game.
 *
 * @file   Encounter.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 *
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
    new Type<Encounter>(Encounter.class, BaseEncounter.TYPE);

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
  @Key("number")
  protected Name m_number = new Name();

  /** The monster that can potentially be encountered. */
  @Key("monsters")
  protected ValueList<Reference<Monster>> m_monsters =
    new ValueList<Reference<Monster>>(new Reference<Monster>(Monster.TYPE));

  /** The monster that can potentially be encountered. */
  @Key("items")
  protected ValueList<Reference<Item>> m_items =
    new ValueList<Reference<Item>>(new Reference<Item>(Item.TYPE));

  /** The traps in the encounter. */
  @Key("traps")
  protected ValueList<LongFormattedText> m_traps =
    new ValueList<LongFormattedText>(new LongFormattedText());

  /** The hazards in the encounter. */
  @Key("hazards")
  protected ValueList<LongFormattedText> m_hazards =
    new ValueList<LongFormattedText>(new LongFormattedText());

  /** The obstacles in the encounter. */
  @Key("obstacles")
  protected ValueList<LongFormattedText> m_obstacles =
    new ValueList<LongFormattedText>(new LongFormattedText());

  /** The secrets in the encounter. */
  @Key("secrets")
  protected ValueList<LongFormattedText> m_secrets =
    new ValueList<LongFormattedText>(new LongFormattedText());

  /** The events in the encounter. */
  @Key("events")
  protected ValueList<LongFormattedText> m_events =
    new ValueList<LongFormattedText>(new LongFormattedText());

  /** Any special rules for the encounter. */
  @Key("rules")
  protected ValueList<LongFormattedText> m_rules =
    new ValueList<LongFormattedText>(new LongFormattedText());

  /** Spells relevant to this encounter. */
  @Key("spells")
  protected ValueList<Reference<BaseSpell>> m_spells =
    new ValueList<Reference<BaseSpell>>
      (new Reference<BaseSpell>(BaseSpell.TYPE));

  /** The encounter level. */
  @Key("EL")
  protected ValueList<Number> m_els = new ValueList<Number>(new Number(0, 250));

  /** The location where this encounter takes place. */
  // TODO: long term, this should be a link to a location object
  @Key("location")
  protected ValueList<Name> m_location =
    new ValueList<Name>(new Name());

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
  protected ValueList<Reference<BaseSkill>> m_skills =
    new ValueList<Reference<BaseSkill>>
      (new Reference<BaseSkill>(BaseSkill.TYPE));

  /** The initial encounter distance. */
  @Key("distance")
  protected Distance m_distance = new Distance();

  static
  {
    extractVariables(Encounter.class);
  }

  @Override
  public Message toProto()
  {
    EncounterProto.Builder builder = EncounterProto.newBuilder();

    builder.setBase((CampaignEntryProto)super.toProto());

    if(m_number.isDefined())
      builder.setNumber(m_number.get());

    if(m_monsters.isDefined())
      for(Reference<Monster> monster : m_monsters)
        builder.addMonster(monster.getName());

    if(m_items.isDefined())
      for(Reference<Item> item : m_items)
        builder.addItem(item.getName());

    if(m_traps.isDefined())
      for(LongFormattedText trap : m_traps)
        builder.addTrap(trap.get());

    if(m_hazards.isDefined())
      for(LongFormattedText hazard : m_hazards)
        builder.addHazard(hazard.get());

    if(m_obstacles.isDefined())
      for(LongFormattedText obstacle : m_obstacles)
        builder.addObstacle(obstacle.get());

    if(m_secrets.isDefined())
      for(LongFormattedText secret : m_secrets)
        builder.addSecret(secret.get());

    if(m_events.isDefined())
      for(LongFormattedText event : m_events)
        builder.addEvent(event.get());

    if(m_rules.isDefined())
      for(LongFormattedText rule : m_rules)
        builder.addRule(rule.get());

    if(m_spells.isDefined())
      for(Reference<BaseSpell> spell : m_spells)
        builder.addSpell(spell.getName());

    if(m_els.isDefined())
      for(Number el : m_els)
        builder.addEncounterLevel((int)el.get());

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
      builder.setDoors(m_taste.get());

    if(m_light.isDefined())
      builder.setDoors(m_light.get());

    if(m_skills.isDefined())
      for(Reference<BaseSkill> skill : m_skills)
        builder.addSkill(skill.getName());

    if(m_distance.isDefined())
      builder.setDistance(m_distance.toProto());

    EncounterProto proto = builder.build();
    return proto;
  }

  @Override
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
      m_number = m_number.as(proto.getNumber());

    if(proto.getMonsterCount() > 0)
    {
      List<Reference<Monster>> monsters = new ArrayList<>();
      for(String monster : proto.getMonsterList())
        monsters.add(m_monsters.createElement().as(monster));
      m_monsters = m_monsters.as(monsters);
    }

    if(proto.getItemCount() > 0)
    {
      List<Reference<Item>> items = new ArrayList<>();
      for(String item : proto.getItemList())
        items.add(m_items.createElement().as(item));
      m_items = m_items.as(items);
    }

    if(proto.getTrapCount() > 0)
    {
      List<LongFormattedText> traps = new ArrayList<>();
      for(String trap : proto.getTrapList())
        traps.add(m_traps.createElement().as(trap));
      m_traps = m_traps.as(traps);
    }

    if(proto.getHazardCount() > 0)
    {
      List<LongFormattedText> hazards = new ArrayList<>();
      for(String hazard : proto.getHazardList())
        hazards.add(m_traps.createElement().as(hazard));
      m_hazards = m_hazards.as(hazards);
    }

    if(proto.getObstacleCount() > 0)
    {
      List<LongFormattedText> obstacles = new ArrayList<>();
      for(String obstacle : proto.getObstacleList())
        obstacles.add(m_traps.createElement().as(obstacle));
      m_obstacles = m_obstacles.as(obstacles);
    }

    if(proto.getSecretCount() > 0)
    {
      List<LongFormattedText> secrets = new ArrayList<>();
      for(String secret : proto.getSecretList())
        secrets.add(m_secrets.createElement().as(secret));
      m_secrets = m_secrets.as(secrets);
    }

    if(proto.getEventCount() > 0)
    {
      List<LongFormattedText> events = new ArrayList<>();
      for(String event : proto.getEventList())
        events.add(m_events.createElement().as(event));
      m_events = m_events.as(events);
    }

    if(proto.getSpellCount() > 0)
    {
      List<Reference<BaseSpell>> spells = new ArrayList<>();
      for(String spell : proto.getSpellList())
        spells.add(m_spells.createElement().as(spell));
      m_spells = m_spells.as(spells);
    }

    if(proto.getEncounterLevelCount() > 0)
    {
      List<Number> els = new ArrayList<>();
      for(int el : proto.getEncounterLevelList())
        els.add(m_els.createElement().as(el));
      m_els = m_els.as(els);
    }

    if(proto.getRuleCount() > 0)
    {
      List<LongFormattedText> rules = new ArrayList<>();
      for(String rule : proto.getRuleList())
        rules.add(m_rules.createElement().as(rule));
      m_rules = m_rules.as(rules);
    }

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
      List<Reference<BaseSkill>> skills = new ArrayList<>();
      for(String skill : proto.getSkillList())
        skills.add(m_skills.createElement().as(skill));

      m_skills = m_skills.as(skills);
    }

    if(proto.hasDistance())
      m_distance = m_distance.fromProto(proto.getDistance());
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(EncounterProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }
}
