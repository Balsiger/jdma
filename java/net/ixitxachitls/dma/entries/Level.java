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
import java.util.Collections;
import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries;
import net.ixitxachitls.dma.proto.Entries.LevelProto;
import net.ixitxachitls.dma.values.ArmorType;
import net.ixitxachitls.dma.values.Proficiency;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Ability;

/**
 * An actual character level.
 *
 * @file   Level.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Level extends NestedEntry
{
  /**
   * Create a default, unnamed level.
   */
  public Level() {}

  /** The hit points rolled for this level. */
  private int m_hp = 0;

  /** The qualities chosen at this level. */
  private List<Quality> m_qualities = new ArrayList<>();

  /** The feats chosen at this level. */
  private List<Feat> m_feats = new ArrayList<>();

  /** The base level to this level. */
  private Optional<Optional<BaseLevel>> m_base = Optional.absent();

  /** The ability that increased at the level, if any. */
  private Optional<Ability> m_abilityIncrease = Optional.absent();

  /**
   * Get the hp this level gives.
   *
   * @return the hp
   */
  public int getHP()
  {
    return m_hp;
  }

  public int getSkillPoints()
  {
    Optional<BaseLevel> level = getBase();
    if(!level.isPresent())
      return 0;

    Optional<Integer> points = level.get().getSkillPoints();
    if(points.isPresent())
      return points.get();

    return 0;
  }

  /**
   * Get the base level to this level.
   *
   * @return the base level
   */
  public Optional<BaseLevel> getBase()
  {
    if(!m_base.isPresent())
    {
      if(m_name.isPresent())
        m_base = Optional.of(DMADataFactory.get().<BaseLevel>getEntry
            (new EntryKey(m_name.get(), BaseLevel.TYPE)));
      else
        return Optional.absent();
    }

    return m_base.get();
  }

  /**
   * Get the abbreviated name of the level.
   *
   * @return the abbreviated name
   */
  public String getAbbreviation()
  {
    if(getBase().isPresent())
    {
      if(getBase().get().getAbbreviation().isPresent())
        return getBase().get().getAbbreviation().get();
      else
        return getBase().get().getName();
    }

    if(m_name.isPresent())
      return "(" + m_name.get() + ")";

    return "(unknown)";
  }

  @Override
  public String getName() {
    if(getBase().isPresent())
      return getBase().get().getName();

    return super.getName();
  }

  /**
   * Get a list of all available level names.
   *
   * @return a list of level names
   */
  public static List<String> getAvailableLevels()
  {
    return DMADataFactory.get().getIDs(BaseLevel.TYPE,
                                       Optional.<EntryKey>absent());
  }

  public List<Quality> getQualities()
  {
    return Collections.unmodifiableList(m_qualities);
  }

  public List<Feat> getFeats()
  {
    return Collections.unmodifiableList(m_feats);
  }

  public List<Feat> getAllFeats(int inLevel)
  {
    List<Feat> feats = new ArrayList<>(getFeats());

    if(getBase().isPresent())
      for(BaseLevel.QualityReference feat : getBase().get().getBonusFeats())
        if(feat.getLevel() <= inLevel)
          feats.add(new Feat(feat.getName()));

    return feats;
  }

  public Optional<Ability> getAbilityIncrease() {
    return m_abilityIncrease;
  }

  public List<Feat> getBonusFeats(int inLevel)
  {
    if(getBase().isPresent())
      return getBase().get().getBonusFeats(inLevel);

    return new ArrayList<>();
  }

  @Override
  public String toString()
  {
    return (m_name.isPresent() ? m_name.get() : "*undefined*")
      + " (" + m_hp + ")"
      + (m_base.isPresent() && m_base.get().isPresent()
         ? " [" + m_base.get().get().getName() : "");
  }

  @Override
  public void set(Values inValues)
  {
    m_name = inValues.use("name", m_name);
    m_qualities = inValues.useEntries("quality", m_qualities, Quality.CREATOR);
    m_feats = inValues.useEntries("feat", m_feats, Feat.CREATOR);
    m_abilityIncrease = inValues.use("ability_increase", m_abilityIncrease,
                                     Ability.PARSER);

    Optional<Integer> hp = inValues.use("hp", m_hp);
    if(hp.isPresent())
      m_hp = hp.get();
  }

  /**
   * Convert the level to a proto value.
   *
   * @return the proto
   */
  public LevelProto toProto()
  {
    LevelProto.Builder builder = LevelProto.newBuilder();

    if(m_name.isPresent())
      builder.setName(m_name.get());
    else
      builder.setName("unknown");
    builder.setHp(m_hp);

    for(Quality quality : m_qualities)
      builder.addQuality(quality.toProto());

    for(Feat feat : m_feats)
      builder.addFeat(feat.toProto());

    if(m_abilityIncrease.isPresent())
      builder.setAbilityIncrease(m_abilityIncrease.get().toProto());

    LevelProto proto = builder.build();
    return proto;
  }

  /**
   * Create a level from the given proto.
   *
   * @param inProto the proto to create from
   * @return a newly create level with the proto values
   */
  public static Level fromProto(LevelProto inProto)
  {
    Level level = new Level();
    level.m_name = Optional.of(inProto.getName());
    level.m_hp = inProto.getHp();

    for (Entries.QualityProto quality : inProto.getQualityList())
      level.m_qualities.add(Quality.fromProto(quality));

    for (Entries.FeatProto feat : inProto.getFeatList())
      level.m_feats.add(Feat.fromProto(feat));

    if(inProto.hasAbilityIncrease())
      level.m_abilityIncrease =
          Optional.of(Ability.fromProto(inProto.getAbilityIncrease()));

    return level;
  }

  public boolean isClassSkill(String inName)
  {
    if(!getBase().isPresent())
      return false;

    return getBase().get().isClassSkill(inName);
  }

  public List<Proficiency> weaponProficiencies()
  {
    if (getBase().isPresent())
      return getBase().get().getWeaponProficiencies();

    return new ArrayList<>();
  }

  public List<ArmorType> armorProficiencies()
  {
    if (getBase().isPresent())
      return getBase().get().getArmorProficiencies();

    return new ArrayList<>();
  }

  public int spellsKnown(int inCharacterLevel, int inSpellLevel)
  {
    if(!getBase().isPresent())
      return 0;

    List<Integer> spells = getBase().get().getSpellsKnown(inSpellLevel);
    if(spells.size() > inCharacterLevel)
      return spells.get(inCharacterLevel - 1);

    return -1;
  }

  public int spellsPerDay(int inCharacterLevel, int inSpellLevel)
  {
    if(!getBase().isPresent())
      return 0;

    List<Integer> spells = getBase().get().getSpellsPerDay(inSpellLevel);
    if(spells.size() > inCharacterLevel)
      return spells.get(inCharacterLevel - 1);

    return -1;
  }

  public Optional<Ability> getSpellAbility()
  {
    if(getBase().isPresent())
      return getBase().get().getSpellAbility();

    return Optional.absent();
  }
}
