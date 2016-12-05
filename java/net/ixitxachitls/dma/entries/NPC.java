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
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.LevelProto;
import net.ixitxachitls.dma.proto.Entries.MonsterProto;
import net.ixitxachitls.dma.proto.Entries.NPCProto;
import net.ixitxachitls.dma.values.Annotated;
import net.ixitxachitls.dma.values.ArmorType;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Proficiency;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Ability;
import net.ixitxachitls.dma.values.enums.Gender;
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

  /**
   * Create an npc of the given type.
   *
   * @param inType the type of the npc to create
   */
  protected NPC(Type<? extends NPC> inType)
  {
    super(inType);
  }

  /**
   * Create an npc with type and name.
   *
   * @param inName the name of the npc
   * @param inType the type of the npc
   */
  protected NPC(String inName, Type<? extends NPC> inType)
  {
    super(inName, inType);
  }

  /** The type of this entry. */
  public static final Type<NPC> TYPE =
    new Type.Builder<>(NPC.class, BaseMonster.TYPE).build();

  /** The type of the base entry to this entry. */
  public static final BaseType<BaseMonster> BASE_TYPE = BaseMonster.TYPE;

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The gender of the npc. */
  protected Gender m_gender = Gender.UNKNOWN;

  /** The individual levels gained, in order. */
  protected List<Level> m_levels = new ArrayList<>();

  /** The religion or patron deity of the npc. */
  protected Optional<String> m_religion = Optional.absent();

  /** The NPCs height description. */
  protected Optional<String> m_height = Optional.absent();

  /** The NPC's weight description. */
  protected Optional<String> m_weight = Optional.absent();

  /** The NPC's description of how it looks. */
  protected Optional<String> m_looks = Optional.absent();
  protected List<String> m_occupations = new ArrayList<>();
  protected Optional<String> m_familyName = Optional.absent();
  protected List<String> m_titles = new ArrayList<>();
  protected List<String> m_locations = new ArrayList<>();
  protected List<String> m_factions = new ArrayList<>();
  protected List<String> m_mannerisms = new ArrayList<>();
  protected List<String> m_secrets = new ArrayList<>();
  protected List<String> m_quests = new ArrayList<>();
  protected List<String> m_interactions = new ArrayList<>();

  protected Optional<String> m_originRegion = Optional.absent();

  /** The possible animal compantion. */
  protected Optional<List<Monster>> m_animalCompanions = Optional.absent();

  /**
   * Get the class levels of the NPC.
   *
   * @return the levels
   */
  public List<Level> getLevels()
  {
    return Collections.unmodifiableList(m_levels);
  }

  /**
   * Get the cumulated levels.
   *
   * @return the cumulated levels
   */
  private SortedMultiset<String> cumulatedLevels()
  {
    SortedMultiset<String> levels = TreeMultiset.create();
    for(Level level : m_levels)
      if(level.getBase().isPresent())
        levels.add(level.getBase().get().getName());
      else
        levels.add(level.getAbbreviation());

    return levels;
  }

  /**
   * Get the cumulated levels, i.e. classes plus levels in each.
   *
   * @return the cumulated classes and levels
   */
  public List<String> getCumulatedLevels()
  {
    SortedMultiset<String> levels = cumulatedLevels();

    List<String> results = new ArrayList<>();
    for(Multiset.Entry<String> entry : levels.entrySet())
      results.add(entry.getElement() + " " + entry.getCount());

    return results;
  }

  /**
   * Get the effective character level. This is the total of all character
   * levels plus the monster's level adjustments, if any.
   *
   * @return the effective character leve.
   */
  public int getEffectiveCharacterLevel()
  {
    int levels = m_levels.size();

    Optional<Integer> levelAdjustment = getCombinedLevelAdjustment().get();
    if(levelAdjustment.isPresent())
      levels += levelAdjustment.get();

    return levels;
  }

  /**
   * Get the NPC's gender.
   *
   * @return the gender
   */
  public Gender getGender()
  {
    return m_gender;
  }

  /**
   * Get the NPC's religion description.
   *
   * @return the description of the religion
   */
  public Optional<String> getReligion()
  {
    return m_religion;
  }

  /**
   * Get the NPC's weight description.
   *
   * @return the weight
   */
  public Optional<String> getWeight()
  {
    return m_weight;
  }

  /**
   * Get the NPC's height description.
   *
   * @return the height
   */
  public Optional<String> getHeight()
  {
    return m_height;
  }

  /**
   * Get the NPC's description of looks.
   *
   * @return how the NPC looks
   */
  public Optional<String> getLooks()
  {
    return m_looks;
  }

  public List<String> getOccupations()
  {
    return m_occupations;
  }

  public Optional<String> getFamilyName()
  {
    return m_familyName;
  }

  public List<String> getTitles()
  {
    return m_titles;
  }

  public List<String> getLocations()
  {
    return m_locations;
  }

  public List<String> getFactions()
  {
    return m_factions;
  }

  public List<String> getMannerisms()
  {
    return m_mannerisms;
  }

  public List<String> getSecrets()
  {
    return m_secrets;
  }

  public List<String> getQuests()
  {
    return m_quests;
  }

  public List<String> getInteractions()
  {
    return m_interactions;
  }

  public Optional<String> getOriginRegion()
  {
    return m_originRegion;
  }

  @Override
  public Annotated.Bonus getCombinedBaseAttack()
  {
    Annotated.Bonus combined = super.getCombinedBaseAttack();

    for(Multiset.Entry<String> entry : cumulatedLevels().entrySet())
    {
      Optional<BaseLevel> level = DMADataFactory.get().getEntry
        (new EntryKey(entry.getElement(), BaseLevel.TYPE));

      int bonus = 0;
      if (level.isPresent())
      {
        List<Integer> attacks = level.get().getBaseAttacks();
        for (int i = 0; i < entry.getCount(); i++)
          if (i < attacks.size())
            bonus += attacks.get(i);
          else
            Log.warning("No base attack information for level " + i + " in "
                        + level.get().getName());

        combined.add(bonus, level.get().getName());
      }
    }

    return combined;
  }

  @Override
  public Annotated.Modifier strength() {
    Annotated.Modifier annotated = super.strength();

    Multiset<String> levels = HashMultiset.create();
    for(Level level : m_levels)
    {
      levels.add(level.getName());

      if(level.getAbilityIncrease().isPresent()
          && level.getAbilityIncrease().get() == Ability.STRENGTH)
        annotated.add(new Modifier(1),
                      level.getName() + " " + levels.count(level.getName()));
    }

    return annotated;
  }

  @Override
  public Annotated.Bonus getCombinedBaseFortitudeSave()
  {
    Annotated.Bonus save = super.getCombinedBaseFortitudeSave();

    for(Multiset.Entry<String> entry : cumulatedLevels().entrySet())
    {
      Optional<BaseLevel> level = DMADataFactory.get().getEntry
          (new EntryKey(entry.getElement(), BaseLevel.TYPE));

      int bonus = 0;
      if(level.isPresent())
      {
        List<Integer> saves = level.get().getFortitudeSaves();
        for(int i = 0; i < entry.getCount(); i++)
          if(i < saves.size())
            bonus += saves.get(i);
          else
            Log.warning("Cannot find fortitude save for level " + i
                            + " in " + level.get().getName());

        save.add(bonus, level.get().getName() + " " + entry.getCount());
      }
    }

    return save;
  }

  @Override
  public Annotated.Bonus getCombinedBaseReflexSave()
  {
    Annotated.Bonus save = super.getCombinedBaseReflexSave();

    for(Multiset.Entry<String> entry : cumulatedLevels().entrySet())
    {
      Optional<BaseLevel> level = DMADataFactory.get().getEntry
        (new EntryKey(entry.getElement(), BaseLevel.TYPE));

      int bonus = 0;
      if(level.isPresent())
      {
        List<Integer> saves = level.get().getReflexSaves();
        for(int i = 0; i < entry.getCount(); i++)
          if(i < saves.size())
            bonus += saves.get(i);
          else
            Log.warning("Cannot find reflex save for level " + i + " in "
                        + level.get().getName());

        save.add(bonus, level.get().getName() + " " + entry.getCount());
      }
    }

    return save;
  }

  @Override
  public Annotated.Bonus getCombinedBaseWillSave()
  {
    Annotated.Bonus save = super.getCombinedBaseWillSave();

    for(Multiset.Entry<String> entry : cumulatedLevels().entrySet())
    {
      Optional<BaseLevel> level = DMADataFactory.get().getEntry
        (new EntryKey(entry.getElement(), BaseLevel.TYPE));

      int bonus = 0;
      if(level.isPresent())
      {
        List<Integer> saves = level.get().getWillSaves();
        for(int i = 0; i < entry.getCount() && i < saves.size(); i++)
          if(i < saves.size())
            bonus += saves.get(i);
          else
            Log.warning("Cannot find will save for level " + i + " in "
                        + level.get().getName());

        save.add(bonus, level.get().getName() + " " + entry.getCount());
      }
    }

    return save;
  }

  @Override
  public int getMaxHP()
  {
    if(m_levels.isEmpty())
      return super.getMaxHP();

    // Use the levels of the character, ignoring a monsters other hps.
    int hp = 0;
    for(Level level : m_levels)
    {
      hp += level.getHP();
      hp += getConstitutionModifier();
    }

    return hp;
  }

  public int maxSkillRanks()
  {
    return BaseLevel.maxSkillRanks(getEffectiveCharacterLevel());
  }

  public Annotated.Arithmetic<Modifier> attackModifier() {
    Annotated.Arithmetic<Modifier> modifier = new Annotated.Arithmetic<>();

    for (Level level : m_levels)
    {
      for(Quality quality : level.getQualities())
        modifier.add(quality.attackModifier(), quality.baseName());
      for(Feat feat : level.getFeats())
        modifier.add(feat.attackModifier(), feat.baseName());
    }

    return modifier;
  }

  public Annotated.Arithmetic<Modifier> damageModifier() {
    Annotated.Arithmetic<Modifier> modifier = new Annotated.Arithmetic<>();

    for (Level level : m_levels)
    {
      for(Quality quality : level.getQualities())
        modifier.add(quality.damageModifier(), quality.baseName());
      for(Feat feat : level.getFeats())
        modifier.add(feat.damageModifier(), feat.baseName());
    }

    return modifier;
  }

  public Annotated.List<Quality> getClassQualities()
  {
    Annotated.List<Quality> combined = new Annotated.List<>();
    for(Level level : m_levels)
      combined.add(level.getQualities(), level.getName());

    return combined;
  }

  @Override
  public int totalSkillPoints()
  {
    int points = 0;
    int intModifier = Math.max(1, abilityModifier(Ability.INTELLIGENCE));
    boolean first = true;

    for(Level level : m_levels)
      if (first)
      {
        points += 4 * (level.getSkillPoints() + intModifier);
        first = false;
      } else
        points += level.getSkillPoints() + intModifier;

    return points;
  }

  @Override
  public int featsAvailable() {
    if(m_levels.isEmpty())
      return super.featsAvailable();

    return 1 + (getEffectiveCharacterLevel() / 3);
  }

  @Override
  protected List<Quality> allQualities()
  {
    List<Quality> qualities = super.allQualities();

    for(Level level : m_levels)
      qualities.addAll(level.getQualities());

    return qualities;
  }

  @Override
  protected Set<Feat> allFeats()
  {
    Set<Feat> feats = super.allFeats();

    Multiset<String> levels = HashMultiset.create();
    for(Level level : m_levels)
    {
      levels.add(level.getName());
      feats.addAll(level.getAllFeats(levels.count(level.getName())));
    }

    return feats;
  }

  private Multiset<String> qualityNames()
  {
    Multiset<String> names = HashMultiset.create();

    for(Quality quality : allQualities())
      names.add(quality.getName());

    return names;
  }

  public List<String> unusedQualities()
  {
    List<String> qualities = new ArrayList<>();
    Multiset<String> names = qualityNames();

    Multiset<String> levels = HashMultiset.create();
    for(Level level : m_levels)
    {
      if(level.getBase().isPresent())
      {
        levels.add(level.getName());
        for(BaseLevel.QualityReference quality
            : level.getBase().get().getSpecialAttacks())
          if(quality.getLevel() == levels.count(level.getName())
            && !removeOneOf(names, quality.getName().split("\\|")))
            qualities.add("-" + quality.getName());

        for(BaseLevel.QualityReference quality
            : level.getBase().get().getSpecialQualities())
          if(quality.getLevel() == levels.count(level.getName())
              && !removeOneOf(names, quality.getName().split("\\|")))
            qualities.add("-" + quality.getName());
      }
    }

    for(Quality quality : super.monsterQualities())
    {
      if (!removeOneOf(names, quality.getName().split("\\|"))) {
        qualities.add("-" + quality.getName());
      }
    }

    for (String name : names) {
      qualities.add("+" + name);
    }

    return qualities;
  }

  private boolean removeOneOf(Multiset<String> set, String ... names)
  {
    for (String name : names)
      if (set.remove(name))
        return true;

    return false;
  }

  public boolean isClassSkill(String inName)
  {
    for(Level level : m_levels)
      if(level.isClassSkill(inName))
        return true;

    return false;
  }

  @Override
  public Annotated<List<String>> weaponProficiencies()
  {
    Annotated.List<String> proficiencies =
        (Annotated.List<String>)super.weaponProficiencies();

    for(Level level : m_levels)
      for(Proficiency proficiency : level.weaponProficiencies())
        proficiencies.addSingle(proficiency.toString(),
                                level.getAbbreviation());

    return proficiencies;
  }

  public Annotated.List<ArmorType> armorProficiencies()
  {
    Annotated.List<ArmorType> proficiencies = new Annotated.List<>();

    for(Level level : m_levels)
      proficiencies.add(level.armorProficiencies(), level.getAbbreviation());

    return proficiencies;
  }

  public Annotated<List<Feat>> getCombinedFeats()
  {
    Annotated.List<Feat> feats = (Annotated.List<Feat>)super.getCombinedFeats();

    Multiset<String> levels = HashMultiset.create();
    for(Level level : m_levels)
    {
      levels.add(level.getAbbreviation());

      feats.add(level.getFeats(), level.getAbbreviation());

      // Bonus feats.
      feats.add(level.getBonusFeats(levels.count(level.getAbbreviation())),
                level.getAbbreviation()
                    + levels.count(level.getAbbreviation()));
    }

    // Quality feats
    for(Quality quality : allQualities())
      for(String feat : quality.bonusFeats())
        feats.add(ImmutableList.of(new Feat(feat)), quality.getName());

    return feats;
  }

  @Override
  public void setValues(Values inValues)
  {
    super.setValues(inValues);

    m_gender = inValues.use("gender", m_gender, Gender.PARSER);
    m_religion = inValues.use("religion", m_religion);
    m_weight = inValues.use("weight", m_weight);
    m_height = inValues.use("height", m_height);
    m_looks = inValues.use("looks", m_looks);
    m_occupations = inValues.use("occupation", m_occupations);
    m_familyName = inValues.use("family_name", m_familyName);
    m_titles = inValues.use("title", m_titles);
    m_locations = inValues.use("location", m_locations);
    m_factions = inValues.use("faction", m_factions);
    m_mannerisms = inValues.use("mannerism", m_mannerisms);
    m_secrets = inValues.use("secret", m_secrets);
    m_quests = inValues.use("quest", m_quests);
    m_interactions = inValues.use("interaction", m_interactions);
    m_levels = inValues.useEntries("level", m_levels,
                                   new NestedEntry.Creator<Level>()
                                   {
                                     @Override
                                     public Level create()
                                     {
                                       return new Level();
                                     }
                                   });
    m_originRegion = inValues.use("origin_region", m_originRegion);
  }

  @Override
  public Message toProto()
  {
    NPCProto.Builder builder = NPCProto.newBuilder();

    builder.setBase((MonsterProto)super.toProto());

    if(m_gender != Gender.UNKNOWN)
      builder.setGender(m_gender.toProto());

    for(Level level : m_levels)
      builder.addLevel(level.toProto());

    if(m_religion.isPresent())
      builder.setReligion(m_religion.get());

    if(m_height.isPresent())
      builder.setHeight(m_height.get());

    if(m_weight.isPresent())
      builder.setWeight(m_weight.get());

    if(m_looks.isPresent())
      builder.setLooks(m_looks.get());

    builder.addAllOccupation(m_occupations);

    if(m_familyName.isPresent())
      builder.setFamilyName(m_familyName.get());

    builder.addAllTitle(m_titles);
    builder.addAllLocation(m_locations);
    builder.addAllFaction(m_factions);
    builder.addAllMannerism(m_mannerisms);
    builder.addAllSecret(m_secrets);
    builder.addAllQuest(m_quests);
    builder.addAllInteractions(m_interactions);

    if(m_originRegion.isPresent())
      builder.setOriginRegion(m_originRegion.get());

    NPCProto proto = builder.build();
    return proto;
  }

  /**
   * Set the NPC's value from the given proto.
   *
   * @param inProto the proto with the values
   */
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
      m_gender = Gender.fromProto(proto.getGender());

    if(proto.hasGivenName())
      m_givenName = Optional.of(proto.getGivenName());

    m_levels.clear();
    for(LevelProto level : proto.getLevelList())
      m_levels.add(Level.fromProto(level));

    if(proto.hasReligion())
      m_religion = Optional.of(proto.getReligion());

    if(proto.hasHeight())
      m_height = Optional.of(proto.getHeight());

    if(proto.hasWeight())
      m_weight = Optional.of(proto.getWeight());

    if(proto.hasLooks())
      m_looks = Optional.of(proto.getLooks());

    m_occupations.addAll(proto.getOccupationList());

    if(proto.hasFamilyName())
      m_familyName = Optional.of(proto.getFamilyName());

    m_titles.addAll(proto.getTitleList());
    m_locations.addAll(proto.getLocationList());
    m_factions.addAll(proto.getFactionList());
    m_mannerisms.addAll(proto.getMannerismList());
    m_secrets.addAll(proto.getSecretList());
    m_quests.addAll(proto.getQuestList());
    m_interactions.addAll(proto.getInteractionsList());

    if(proto.hasOriginRegion())
      m_originRegion = Optional.of(proto.getOriginRegion());
  }

  @Override
  protected Message defaultProto()
  {
    return NPCProto.getDefaultInstance();
  }

  public List<Monster> getAnimalCompanions()
  {
    if(!m_animalCompanions.isPresent()) {
      m_animalCompanions = Optional.of(DMADataFactory.get().getEntries(
          Monster.TYPE, Optional.of(getCampaign().get().getKey()),
          "index-parent", getType().getLink() + "/" + getName().toLowerCase()));
    }

    return m_animalCompanions.get();
  }
}
