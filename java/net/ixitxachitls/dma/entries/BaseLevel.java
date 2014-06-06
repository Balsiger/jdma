/*****************************************************************************
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

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.proto.Entries.BaseArmorProto;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseLevelProto;
import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto;
import net.ixitxachitls.dma.proto.Entries.BaseWeaponProto;
import net.ixitxachitls.dma.values.ArmorType;
import net.ixitxachitls.dma.values.NewDice;
import net.ixitxachitls.dma.values.NewReference;
import net.ixitxachitls.dma.values.NewValue;
import net.ixitxachitls.dma.values.Proficiency;
import net.ixitxachitls.util.logging.Log;

/**
 * An entry representing a base character level.
 *
 * @file   BaseLevel.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
@ParametersAreNonnullByDefault
public class BaseLevel extends BaseEntry
{
  public static class QualityReference
  {
    public QualityReference(String inName, int inLevel, int inUsesPerDay,
                            Optional<String> inCondition)
    {
      m_reference = new NewReference<BaseQuality>(BaseQuality.TYPE, inName);
      m_level = inLevel;
      m_usesPerDay = inUsesPerDay;
      m_condition = inCondition;
    }

    private final NewReference<BaseQuality> m_reference;
    private final int m_level;
    private final int m_usesPerDay;
    private final Optional<String> m_condition;

    public static final NewValue.Parser<QualityReference> PARSER =
      new NewValue.Parser<QualityReference>(4)
      {
        @Override
        public Optional<QualityReference> doParse
        (String inName, String inLevel, String inPerDay, String inCondition)
        {
          String name = inName;
          int level = Integer.parseInt(inLevel);
          int perDay = inPerDay.isEmpty() ? 0 : Integer.parseInt(inPerDay);
          Optional<String> condition = inCondition.isEmpty()
            ? Optional.<String>absent() : Optional.of(inCondition);

          return Optional.of(new QualityReference(name, level, perDay,
                                                  condition));
        }
      };

    public String getName()
    {
      return m_reference.getName();
    }

    public NewReference<BaseQuality> getReference()
    {
      return m_reference;
    }

    public int getLevel()
    {
      return m_level;
    }

    public int getUsesPerDay()
    {
      return m_usesPerDay;
    }

    public Optional<String> getCondition()
    {
      return m_condition;
    }

    @Override
    public String toString()
    {
      return m_level + ": " + m_reference
        + (m_usesPerDay > 0 ? ", " + m_usesPerDay + "/day" : "")
        + (m_condition.isPresent() ? ", if " + m_condition.get() : "");
    }
  }

  /**
   * Create the base level.
   */
  public BaseLevel()
  {
    super(TYPE);
  }

  /**
   * Create the base level with the given name.
   *
   * @param inName the name of the level
   */
  public BaseLevel(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final BaseType<BaseLevel> TYPE =
    new BaseType<>(BaseLevel.class);

  /** Serialize version id. */
  private static final long serialVersionUID = 1L;

  /** The short name for the class. */
  protected Optional<String> m_abbreviation = Optional.absent();

  /** The adventures typical for the class. */
  protected Optional<String> m_adventures = Optional.absent();

  /** The characteristics of the class. */
  protected Optional<String> m_characteristics = Optional.absent();

  /** The alignment options for this class. */
  protected Optional<String> m_alignmentOptions = Optional.absent();

  /** The religious views of the class. */
  protected Optional<String> m_religion = Optional.absent();

  /** The usual backgrounds for the class. */
  protected Optional<String> m_background = Optional.absent();

  /** The races suited for this class. */
  protected Optional<String> m_races = Optional.absent();

  /** The relation to other classes. */
  protected Optional<String> m_otherClasses = Optional.absent();

  /** The role of the class. */
  protected Optional<String> m_role = Optional.absent();

  /** The important abilities for the class. */
  protected Optional<String> m_importantAbilities = Optional.absent();

  /** The alignments allowed for the class. */
  protected List<BaseMonster.Alignment> m_allowedAlignments =
    new ArrayList<>();

  /** The hit die. */
  protected Optional<NewDice> m_hitDie = Optional.absent();

  /** Skill points per level (x4 at first level, +Int modifier). */
  protected Optional<Integer> m_skillPoints = Optional.absent();

  /** The class skills. */
  protected List<NewReference<BaseSkill>> m_classSkills = new ArrayList<>();

  /** The weapon proficiencies. */
  protected List<Proficiency> m_weaponProficiencies = new ArrayList<>();

  /** The armor proficiencies. */
  protected List<ArmorType> m_armorProficiencies = new ArrayList<>();

  /** Special attacks. */
  protected List<QualityReference> m_specialAttacks = new ArrayList<>();

  /** Special qualities. */
  protected List<QualityReference> m_specialQualities = new ArrayList<>();

  /** The base attack bonuses per level. */
  protected List<Integer> m_baseAttacks = new ArrayList<>();

  /** The fortitude saves per level. */
  protected List<Integer> m_fortitudeSaves = new ArrayList<>();

  /** The reflex saves per level. */
  protected List<Integer> m_reflexSaves = new ArrayList<>();

  /** The will saves per level. */
  protected List<Integer> m_willSaves = new ArrayList<>();

  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().hasAccess(BaseCharacter.Group.DM);
  }

  /**
   * Computes the minimal number of xp used to reach the given level.
   *
   * @param inLevel the level to compute for
   * @return the XP minimally required for the given level
   */
  public static int xpPerLevel(int inLevel)
  {
    return 500 * inLevel * (inLevel + 1);
  }

  /**
   * Returns the maximal number of skill ranks a character of the given
   * level can have.
   *
   * @param inLevel  the character level
   * @return the maximal number of skill ranks
   */
  public static int maxSkillRanks(int inLevel)
  {
    return inLevel + 3;
  }

  /**
   * Returns the maximal number of skill ranks a character can have in a cross
   * class skill.
   *
   * @param inLevel  the character level
   * @return the maximal cross skill points
   */
  public static int maxCrossSkillRanks(int inLevel)
  {
    return maxSkillRanks(inLevel) / 2;
  }

  /**
   * Returns the number of standard feats a character of the given level has.
   *
   * @param inLevel  the character level
   * @return the number of standard feats available
   */
  public static int standardFeats(int inLevel)
  {
    return 1 + inLevel / 3;
  }

  /**
   * Checks whether the given level gives an ability increase.
   *
   * @param inLevel  the character level
   * @return true if the level gives an ability increase, false if not
   */
  public static boolean abilityIncrease(int inLevel)
  {
    return (inLevel / 4) * 4 == inLevel;
  }

  /**
   * Get the abbreviation of the level.
   *
   * @return the level abbreviation
   */
  public Optional<String> getAbbreviation()
  {
    return m_abbreviation;
  }

  public Optional<String> getAdventures()
  {
    return m_adventures;
  }

  public Optional<String> getCharacteristics()
  {
    return m_characteristics;
  }

  public Optional<String> getAlignmentOptions()
  {
    return m_alignmentOptions;
  }

  public Optional<String> getReligion()
  {
    return m_religion;
  }

  public Optional<String> getRaces()
  {
    return m_races;
  }

  public Optional<String> getOtherClasses()
  {
    return m_otherClasses;
  }

  public Optional<String> getRole()
  {
    return m_role;
  }

  public Optional<String> getBackground()
  {
    return m_background;
  }

  public Optional<String> getImportantAbilities()
  {
    return m_importantAbilities;
  }

  public List<BaseMonster.Alignment> getAllowedAlignments()
  {
    return Collections.unmodifiableList(m_allowedAlignments);
  }

  /**
   * Get the hit die for the level.
   *
   * @return  the hit die
   */
  public Optional<NewDice> getHitDie()
  {
    return m_hitDie;
  }

  public Optional<Integer> getSkillPoints()
  {
    return m_skillPoints;
  }

  public List<NewReference<BaseSkill>> getClassSkills()
  {
    return Collections.unmodifiableList(m_classSkills);
  }

  public List<Proficiency> getWeaponProficiencies()
  {
    return Collections.unmodifiableList(m_weaponProficiencies);
  }

  public List<ArmorType> getArmorProficiencies()
  {
    return Collections.unmodifiableList(m_armorProficiencies);
  }

  public List<QualityReference> getSpecialAttacks()
  {
    return Collections.unmodifiableList(m_specialAttacks);
  }

  public List<QualityReference> getSpecialQualities()
  {
    return Collections.unmodifiableList(m_specialQualities);
  }

  public List<Integer> getBaseAttacks()
  {
    return Collections.unmodifiableList(m_baseAttacks);
  }

  public List<Integer> getFortitudeSaves()
  {
    return Collections.unmodifiableList(m_fortitudeSaves);
  }

  public List<Integer> getReflexSaves()
  {
    return Collections.unmodifiableList(m_reflexSaves);
  }

  public List<Integer> getWillSaves()
  {
    return Collections.unmodifiableList(m_willSaves);
  }

  public List<String> getAlignmentNames()
  {
    return BaseMonster.Alignment.names();
  }

  public List<String> getWeaponProficiencyNames()
  {
    return Proficiency.names();
  }

  public List<String> getArmorProficiencyNames()
  {
    return ArmorType.names();
  }

  /**
   * Collect data for a specific level of this base level.
   *
   * @param inLevel       the number of this level
   * @param inName        the name of the value to collect
   * @param ioCombined    the combined value to add to
   * @param inDescription the description for what to collect the data
   * @param <T>           the type of value to be collected
   */
  /*
  @SuppressWarnings("unchecked")
  public <T extends Value<T>> void collect(int inLevel, String inName,
                                           Combined<T> ioCombined,
                                           String inDescription)
  {
    for(Reference<BaseQuality> quality : collectSpecialQualities(inLevel))
      if(quality.hasEntry())
        // TODO: add conditions and maybe counts
        quality.getEntry().collect(inName, ioCombined, inDescription,
                                   quality.getParameters(), null);

    switch(inName)
    {
      case "base attack":
        if(m_baseAttacks.get(inLevel - 1).get() > 0)
          ioCombined.addModifier
            (new Modifier((int)m_baseAttacks.get(inLevel - 1).get()), this,
             getAbbreviation() + inLevel);
        break;

      case "fortitude save":
        if(m_fortitudeSaves.get(inLevel - 1).get() > 0)
          ioCombined.addModifier
            (new Modifier((int)m_fortitudeSaves.get(inLevel - 1).get()), this,
             getAbbreviation() + inLevel);
        break;

      case "reflex save":
        if(m_reflexSaves.get(inLevel - 1).get() > 0)
          ioCombined.addModifier
            (new Modifier((int)m_reflexSaves.get(inLevel - 1).get()), this,
             getAbbreviation() + inLevel);
        break;

      case "will save":
        if(m_willSaves.get(inLevel - 1).get() > 0)
          ioCombined.addModifier
            (new Modifier((int)m_willSaves.get(inLevel - 1).get()), this,
             getAbbreviation() + inLevel);
        break;

      case "special qualities":
        List<Multiple> qualities = new ArrayList<>();
        for(Multiple quality : m_specialQualities)
          if(((Number)quality.get(0)).get() == inLevel)
            qualities.add((Multiple)quality.get(1));

          ioCombined.addValue((T)m_specialQualities.as(qualities), this,
                              getAbbreviation() + inLevel);
      break;

      case "special attacks":
        qualities = new ArrayList<>();
        for(Multiple quality : m_specialAttacks)
          if(((Number)quality.get(0)).get() == inLevel)
            qualities.add((Multiple)quality.get(1));

        ioCombined.addValue((T)m_specialAttacks.as(qualities), this,
                            getAbbreviation() + inLevel);
        break;

      default:
        break;
    }
  }
  */

  /**
   * Collect special qualities from all levels.
   *
   * @param  inLevel the level for which to compute qualities
   * @return all the special quality references for the given level
   */
  /*
  @SuppressWarnings("unchecked")
  public List<Reference<BaseQuality>> collectSpecialQualities(int inLevel)
  {
    List<Reference<BaseQuality>> qualities = new ArrayList<>();

    for(Multiple quality : m_specialQualities)
      if(((Number)quality.get(0)).get() == inLevel)
        qualities.add((Reference<BaseQuality>)
                      ((Multiple)quality.get(1)).get(0));

    for(Multiple quality : m_specialAttacks)
      if(((Number)quality.get(0)).get() == inLevel)
        qualities.add((Reference<BaseQuality>)
                      ((Multiple)quality.get(1)).get(0));

    for(BaseEntry base : getBaseEntries())
      if(base instanceof BaseLevel)
        qualities.addAll(((BaseLevel)base).collectSpecialQualities(inLevel));

    return qualities;
  }
  */

  @Override
  public void set(Values inValues)
  {
    super.set(inValues);

    m_abbreviation = inValues.use("abbreviation", m_abbreviation);
    m_adventures = inValues.use("adventures", m_adventures);
    m_characteristics = inValues.use("characteristics", m_characteristics);
    m_alignmentOptions = inValues.use("alignment_options", m_alignmentOptions);
    m_religion = inValues.use("religion", m_religion);
    m_background = inValues.use("background", m_background);
    m_races = inValues.use("races", m_races);
    m_otherClasses = inValues.use("other_classes", m_otherClasses);
    m_role = inValues.use("role", m_role);
    m_importantAbilities =
      inValues.use("important_abilities", m_importantAbilities);
    m_allowedAlignments =
      inValues.use("allowed_alignment", m_allowedAlignments,
                   BaseMonster.Alignment.PARSER);
    m_hitDie = inValues.use("hit_die", m_hitDie, NewDice.PARSER);
    m_classSkills =
      inValues.use("class_skill", m_classSkills,
                   new NewReference.ReferenceParser<>(BaseSkill.TYPE));
    m_weaponProficiencies =
      inValues.use("weapon_proficiency", m_weaponProficiencies,
                   Proficiency.PARSER);
    m_armorProficiencies =
      inValues.use("armor_proficiency", m_armorProficiencies,
                   ArmorType.PARSER);
    m_specialAttacks =
      inValues.use("special_attack", m_specialAttacks, QualityReference.PARSER,
                   "name", "level", "per_day", "condition");
    m_specialQualities =
      inValues.use("special_quality",
                   m_specialQualities, QualityReference.PARSER,
                   "name", "level", "per_day", "condition");
    m_baseAttacks =
      inValues.use("base_attack", m_baseAttacks, NewValue.INTEGER_PARSER);
    m_fortitudeSaves =
      inValues.use("fortitude_save", m_fortitudeSaves, NewValue.INTEGER_PARSER);
    m_reflexSaves =
      inValues.use("relfex_save", m_reflexSaves, NewValue.INTEGER_PARSER);
    m_willSaves =
      inValues.use("will_save", m_willSaves, NewValue.INTEGER_PARSER);
  }

  @Override
  public Message toProto()
  {
    BaseLevelProto.Builder builder = BaseLevelProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    if(m_abbreviation.isPresent())
      builder.setAbbreviation(m_abbreviation.get());

    if(m_adventures.isPresent())
      builder.setAdventures(m_adventures.get());

    if(m_characteristics.isPresent())
      builder.setCharacteristics(m_characteristics.get());

    if(m_alignmentOptions.isPresent())
      builder.setAlignmentOptions(m_alignmentOptions.get());

    if(m_religion.isPresent())
      builder.setReligion(m_religion.get());

    if(m_background.isPresent())
      builder.setBackground(m_background.get());

    if(m_races.isPresent())
      builder.setRaces(m_races.get());

    if(m_otherClasses.isPresent())
      builder.setOtherClasses(m_otherClasses.get());

    if(m_role.isPresent())
      builder.setRole(m_role.get());

    if(m_importantAbilities.isPresent())
      builder.setImportantAbilities(m_importantAbilities.get());

    for(BaseMonster.Alignment alignment : m_allowedAlignments)
      builder.addAllowedAlignment(alignment.toProto());

    if(m_hitDie.isPresent())
      builder.setHitDice(m_hitDie.get().toProto());

    if(m_skillPoints.isPresent())
      builder.setSkillPoints(m_skillPoints.get());

    for(NewReference<BaseSkill> reference : m_classSkills)
      builder.addClassSkill(reference.getName());

    for(Proficiency proficiency : m_weaponProficiencies)
      builder.addWeaponProficiency(proficiency.toProto());

    for(ArmorType proficiency : m_armorProficiencies)
        builder.addArmorProficiency(proficiency.toProto());

    for(QualityReference special : m_specialAttacks)
    {
      BaseMonsterProto.QualityReference.Builder reference =
        BaseMonsterProto.QualityReference.newBuilder();

      NewReference<BaseQuality> ref = special.getReference();
      reference.setReference(BaseMonsterProto.Reference.newBuilder()
                             .setName(ref.getName())
                             .build());

      if(special.getUsesPerDay() > 0)
        reference.setPerDay(special.getUsesPerDay());

      if(special.getCondition().isPresent())
        reference.setCondition(special.getCondition().get());

      builder.addSpecialAttack(BaseLevelProto.LeveledQuality.newBuilder()
                               .setLevel(special.getLevel())
                               .setQuality(reference.build())
                               .build());
    }

    for(QualityReference special : m_specialQualities)
    {
      BaseMonsterProto.QualityReference.Builder reference =
        BaseMonsterProto.QualityReference.newBuilder();

      NewReference<BaseQuality> ref = special.getReference();
      reference.setReference(BaseMonsterProto.Reference.newBuilder()
                             .setName(ref.getName())
                             .build());

      if(special.getUsesPerDay() > 0)
        reference.setPerDay(special.getUsesPerDay());

      if(special.getCondition().isPresent())
        reference.setCondition(special.getCondition().get());

      builder.addSpecialQuality(BaseLevelProto.LeveledQuality.newBuilder()
                                .setLevel(special.getLevel())
                                .setQuality(reference.build())
                                .build());
    }

    for(Integer number : m_baseAttacks)
      builder.addBaseAttack(number);

    for(Integer number : m_fortitudeSaves)
      builder.addFortitudeSave(number);

    for(Integer number : m_reflexSaves)
      builder.addReflexSave(number);

    for(Integer number : m_willSaves)
      builder.addWillSave(number);

    BaseLevelProto proto = builder.build();
    return proto;
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseLevelProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    BaseLevelProto proto = (BaseLevelProto)inProto;

    super.fromProto(proto.getBase());

    if(proto.hasAbbreviation())
      m_abbreviation = Optional.of(proto.getAbbreviation());

    if(proto.hasAdventures())
      m_adventures = Optional.of(proto.getAdventures());

    if(proto.hasCharacteristics())
      m_characteristics = Optional.of(proto.getCharacteristics());

    if(proto.hasAlignmentOptions())
      m_alignmentOptions = Optional.of(proto.getAlignmentOptions());

    if(proto.hasReligion())
      m_religion = Optional.of(proto.getReligion());

    if(proto.hasBackground())
      m_background = Optional.of(proto.getBackground());

    if(proto.hasOtherClasses())
      m_otherClasses = Optional.of(proto.getOtherClasses());

    if(proto.hasRaces())
      m_races = Optional.of(proto.getRaces());

    if(proto.hasRole())
      m_role = Optional.of(proto.getRole());

    if(proto.hasImportantAbilities())
      m_importantAbilities = Optional.of(proto.getImportantAbilities());

    for(BaseMonsterProto.Alignment alignment : proto.getAllowedAlignmentList())
      m_allowedAlignments.add(BaseMonster.Alignment.fromProto(alignment));

    if(proto.hasHitDice())
      m_hitDie = Optional.of(NewDice.fromProto(proto.getHitDice()));

    if(proto.hasSkillPoints())
      m_skillPoints = Optional.of(proto.getSkillPoints());

    for(String ref : proto.getClassSkillList())
      m_classSkills.add(new NewReference<BaseSkill>(BaseSkill.TYPE, ref));

    for(BaseWeaponProto.Proficiency proficiency
      : proto.getWeaponProficiencyList())
      m_weaponProficiencies.add(Proficiency.fromProto(proficiency));

    for(BaseArmorProto.Type proficiency : proto.getArmorProficiencyList())
      m_armorProficiencies.add(ArmorType.fromProto(proficiency));

    for(BaseLevelProto.LeveledQuality quality : proto.getSpecialAttackList())
      m_specialAttacks.add(new QualityReference
                           (quality.getQuality().getReference().getName(),
                            quality.getLevel(),
                            quality.getQuality().getPerDay(),
                            quality.getQuality().hasCondition()
                            ? Optional.<String>of
                              (quality.getQuality().getCondition())
                            : Optional.<String>absent()));

    for(BaseLevelProto.LeveledQuality quality : proto.getSpecialQualityList())
      m_specialQualities.add(new QualityReference
                             (quality.getQuality().getReference().getName(),
                              quality.getLevel(),
                              quality.getQuality().getPerDay(),
                              quality.getQuality().hasCondition()
                              ? Optional.<String>of
                                (quality.getQuality().getCondition())
                              : Optional.<String>absent()));

    for(int baseAttack : proto.getBaseAttackList())
      m_baseAttacks.add(baseAttack);

    for(int save : proto.getFortitudeSaveList())
      m_fortitudeSaves.add(save);

    for(int save : proto.getReflexSaveList())
      m_reflexSaves.add(save);

    for(int save : proto.getWillSaveList())
      m_willSaves.add(save);
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(BaseLevelProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }
}

