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
import com.google.protobuf.Message;

import net.ixitxachitls.dma.proto.Entries.BaseArmorProto;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseLevelProto;
import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto;
import net.ixitxachitls.dma.proto.Entries.BaseWeaponProto;
import net.ixitxachitls.dma.values.ArmorType;
import net.ixitxachitls.dma.values.Dice;
import net.ixitxachitls.dma.values.Parser;
import net.ixitxachitls.dma.values.Proficiency;
import net.ixitxachitls.dma.values.Reference;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Ability;
import net.ixitxachitls.dma.values.enums.Alignment;
import net.ixitxachitls.dma.values.enums.Group;
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
  /** A reference to a quality per level with name, uses and conditions. */
  public static class QualityReference
  {
    /**
     * Create the quality reference.
     *
     * @param inName the name of the quality
     * @param inLevel the level for which this reference is relevant
     * @param inUsesPerDay the number of uses per day
     * @param inCondition the condition for the quality to be used
     */
    public QualityReference(String inName, int inLevel, int inUsesPerDay,
                            Optional<String> inCondition)
    {
      m_reference = new Reference<>(BaseQuality.TYPE, inName);
      m_level = inLevel;
      m_usesPerDay = inUsesPerDay;
      m_condition = inCondition;
    }

    /** The reference to the actualy quality. */
    private final Reference<BaseQuality> m_reference;

    /** The level the quality comes into play as described. */
    private final int m_level;

    /** The number of times the quality can be used per day. */
    private final int m_usesPerDay;

    /** The condition that allows the usage of the quality. */
    private final Optional<String> m_condition;

    /** The parser for the quality reference. */
    public static final Parser<QualityReference> PARSER =
      new Parser<QualityReference>(4)
      {
        @Override
        public Optional<QualityReference> doParse
        (String inName, String inLevel, String inPerDay, String inCondition)
        {
          int level = Integer.parseInt(inLevel);
          int perDay = inPerDay.isEmpty() ? 0 : Integer.parseInt(inPerDay);
          Optional<String> condition = inCondition.isEmpty()
            ? Optional.<String>absent() : Optional.of(inCondition);

          return Optional.of(new QualityReference(inName, level, perDay,
                                                  condition));
        }
      };

    /**
     * Get the quality name.
     *
     * @return the name (id) of the quality
     */
    public String getName()
    {
      return m_reference.getName();
    }

    /**
     * Get the full quality referenced, if found.
     *
     * @return the quality or absent if not found
     */
    public Reference<BaseQuality> getReference()
    {
      return m_reference;
    }

    /**
     * Get the level this quality comes into play.
     *
     * @return the level
     */
    public int getLevel()
    {
      return m_level;
    }

    /**
     * Get the number of times the quality can be used per day.
     *
     * @return the number of usages per day
     */
    public int getUsesPerDay()
    {
      return m_usesPerDay;
    }

    /**
     * Get the condition restricting useage of this quality.
     *
     * @return the condition
     */
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
    new BaseType.Builder<>(BaseLevel.class).build();

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
  protected List<Alignment> m_allowedAlignments =
    new ArrayList<>();

  /** The hit die. */
  protected Optional<Dice> m_hitDie = Optional.absent();

  /** Skill points per level (x4 at first level, +Int modifier). */
  protected Optional<Integer> m_skillPoints = Optional.absent();

  /** The class skills. */
  protected List<Reference<BaseSkill>> m_classSkills = new ArrayList<>();

  /** The weapon proficiencies. */
  protected List<Proficiency> m_weaponProficiencies = new ArrayList<>();
  protected List<String> m_individualWeaponProficiencies = new ArrayList<>();

  /** The armor proficiencies. */
  protected List<ArmorType> m_armorProficiencies = new ArrayList<>();

  /** Special attacks. */
  protected List<QualityReference> m_specialAttacks = new ArrayList<>();

  /** Special qualities. */
  protected List<QualityReference> m_specialQualities = new ArrayList<>();

  /** The bonus feats. */
  protected List<QualityReference> m_bonusFeats = new ArrayList<>();

  /** The base attack bonuses per level. */
  protected List<Integer> m_baseAttacks = new ArrayList<>();

  /** The fortitude saves per level. */
  protected List<Integer> m_fortitudeSaves = new ArrayList<>();

  /** The reflex saves per level. */
  protected List<Integer> m_reflexSaves = new ArrayList<>();

  /** The will saves per level. */
  protected List<Integer> m_willSaves = new ArrayList<>();

  /** n-level spells available per day per level. */
  protected List<Integer> []m_spellsPerDay = new List[10];
  {
    for(int i = 0; i < m_spellsPerDay.length; i++)
      m_spellsPerDay[i] = new ArrayList<>();
  }

  /** The n-level spells known per level. */
  protected List<Integer> []m_spellsKnown = new List[10];
  {
    for(int i = 0; i < m_spellsKnown.length; i++)
      m_spellsKnown[i] = new ArrayList<>();
  }

  /** The ability that governs spell casting for this level, if any. */
  protected Optional<Ability> m_spellAbility = Optional.absent();

  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().hasAccess(Group.DM);
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

  /**
   * Get a description how a character of this class behaves on adventures.
   *
   * @return the adventures description
   */
  public Optional<String> getAdventures()
  {
    return m_adventures;
  }

  /**
   * Get a description about the characteristics of this class.
   *
   * @return the class characteristics
   */
  public Optional<String> getCharacteristics()
  {
    return m_characteristics;
  }

  /**
   * Get the options describing what aligments are open to a character of
   * this class and what these mean.
   *
   * @return the alignment options
   */
  public Optional<String> getAlignmentOptions()
  {
    return m_alignmentOptions;
  }

  /**
   * Get a description how this class stands to religions.
   *
   * @return the religion behavior
   */
  public Optional<String> getReligion()
  {
    return m_religion;
  }

  /**
   * Get a description about the races and their interaction with this class.
   *
   * @return descriptions about races and this class
   */
  public Optional<String> getRaces()
  {
    return m_races;
  }

  /**
   * Get a description how other classes react to characters with this level.
   *
   * @return the description for other classes
   */
  public Optional<String> getOtherClasses()
  {
    return m_otherClasses;
  }

  /**
   * Get a description about the role of a character of this class in the party.
   *
   * @return the role description
   */
  public Optional<String> getRole()
  {
    return m_role;
  }

  /**
   * Get a description about possible backgrounds for characters of this class.
   *
   * @return the background description
   */
  public Optional<String> getBackground()
  {
    return m_background;
  }

  /**
   * Get a summary about important abilities for this class.
   *
   * @return the important abilities
   */
  public Optional<String> getImportantAbilities()
  {
    return m_importantAbilities;
  }

  /**
   * Get the list of alignments this class is allowed to have.
   *
   * @return the alignments allowed
   */
  public List<Alignment> getAllowedAlignments()
  {
    return Collections.unmodifiableList(m_allowedAlignments);
  }

  /**
   * Get the hit die for the level.
   *
   * @return  the hit die
   */
  public Optional<Dice> getHitDie()
  {
    return m_hitDie;
  }

  /**
   * Get the skills points per level of this class.
   *
   * @return the number of skill points gained per level
   */
  public Optional<Integer> getSkillPoints()
  {
    return m_skillPoints;
  }

  /**
   * Get the list of class skills for this class.
   *
   * @return the references to class skills
   */
  public List<Reference<BaseSkill>> getClassSkills()
  {
    return Collections.unmodifiableList(m_classSkills);
  }

  /**
   * Get the list of proficiencies this class grants.
   *
   * @return the class proficiencies
   */
  public List<Proficiency> getWeaponProficiencies()
  {
    return Collections.unmodifiableList(m_weaponProficiencies);
  }

  public List<String> getIndividualWeaponProficiencies()
  {
    return Collections.unmodifiableList(m_individualWeaponProficiencies);
  }

  /**
   * Get the list of armor proficiencies this class grants.
   *
   * @return the armor proficiencies
   */
  public List<ArmorType> getArmorProficiencies()
  {
    return Collections.unmodifiableList(m_armorProficiencies);
  }

  /**
   * Get the special attacks available for the class.
   *
   * @return the list of quality references representing special attacks
   */
  public List<QualityReference> getSpecialAttacks()
  {
    return Collections.unmodifiableList(m_specialAttacks);
  }

  /**
   * Get the special qualities of the class.
   *
   * @return the list of quality references representig special qualities.
   */
  public List<QualityReference> getSpecialQualities()
  {
    return Collections.unmodifiableList(m_specialQualities);
  }

  public List<QualityReference> getBonusFeats()
  {
    return Collections.unmodifiableList(m_bonusFeats);
  }

  public List<Feat> getBonusFeats(int inLevel) {
    List<Feat> feats = new ArrayList<>();
    for(QualityReference feat : m_bonusFeats)
      if (feat.getLevel() == inLevel) {
        feats.add(new Feat(feat.getName()));
      }

    return feats;
  }

  /**
   * Get the base attack bonuses per level in this class.
   *
   * @return the base attack bonus per level
   */
  public List<Integer> getBaseAttacks()
  {
    return Collections.unmodifiableList(m_baseAttacks);
  }

  /**
   * Get the fortitude saves per level for this class.
   *
   * @return the fortitude save per level
   */
  public List<Integer> getFortitudeSaves()
  {
    return Collections.unmodifiableList(m_fortitudeSaves);
  }

  /**
   * Get the relfex saves per level for this class.
   *
   * @return the reflex saves per level
   */
  public List<Integer> getReflexSaves()
  {
    return Collections.unmodifiableList(m_reflexSaves);
  }

  /**
   * Get the will saves per level for this class.
   *
   * @return the will saves per level
   */
  public List<Integer> getWillSaves()
  {
    return Collections.unmodifiableList(m_willSaves);
  }

  public List<Integer> getSpellsPerDay(Integer inLevel)
  {
    if(inLevel < 0 || inLevel > 9)
      throw new IllegalArgumentException(
          "Given level out of range: " + inLevel);

    return Collections.unmodifiableList(m_spellsPerDay[inLevel]);
  }

  public List<Integer> getSpellsKnown(Integer inLevel)
  {
    if(inLevel < 0 || inLevel > 9)
      throw new IllegalArgumentException(
          "Given level out of range: " + inLevel);

    return Collections.unmodifiableList(m_spellsKnown[inLevel]);
  }

  public Optional<Ability> getSpellAbility()
  {
    return m_spellAbility;
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
   * @ param  inLevel the level for which to compute qualities
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
  public void setValues(Values inValues)
  {
    super.setValues(inValues);

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
                   Alignment.PARSER);
    m_hitDie = inValues.use("hit_die", m_hitDie, Dice.PARSER);
    m_skillPoints = inValues.use("skill_points", m_skillPoints,
                                 Value.INTEGER_PARSER);
    m_classSkills =
      inValues.use("class_skill", m_classSkills,
                   new Reference.ReferenceParser<>(BaseSkill.TYPE));
    m_weaponProficiencies =
      inValues.use("weapon_proficiency", m_weaponProficiencies,
                   Proficiency.PARSER);
    m_individualWeaponProficiencies =
        inValues.use("weapon_proficiency_individual",
                     m_individualWeaponProficiencies);
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
    m_bonusFeats =
        inValues.use("bonus_feat",
                     m_bonusFeats, QualityReference.PARSER,
                     "name", "level", "per_day", "condition");
    m_baseAttacks =
      inValues.use("base_attack", m_baseAttacks, Value.INTEGER_PARSER);
    m_fortitudeSaves =
      inValues.use("fortitude_save", m_fortitudeSaves, Value.INTEGER_PARSER);
    m_reflexSaves =
      inValues.use("reflex_save", m_reflexSaves, Value.INTEGER_PARSER);
    m_willSaves = inValues.use("will_save", m_willSaves, Value.INTEGER_PARSER);
    m_spellsPerDay[0] = inValues.use("spells_per_day_0", m_spellsPerDay[0],
                                     Value.INTEGER_PARSER);
    m_spellsPerDay[1] = inValues.use("spells_per_day_1", m_spellsPerDay[1],
                                     Value.INTEGER_PARSER);
    m_spellsPerDay[2] = inValues.use("spells_per_day_2", m_spellsPerDay[2],
                                     Value.INTEGER_PARSER);
    m_spellsPerDay[3] = inValues.use("spells_per_day_3", m_spellsPerDay[3],
                                     Value.INTEGER_PARSER);
    m_spellsPerDay[4] = inValues.use("spells_per_day_4", m_spellsPerDay[4],
                                     Value.INTEGER_PARSER);
    m_spellsPerDay[5] = inValues.use("spells_per_day_5", m_spellsPerDay[5],
                                     Value.INTEGER_PARSER);
    m_spellsPerDay[6] = inValues.use("spells_per_day_6", m_spellsPerDay[6],
                                     Value.INTEGER_PARSER);
    m_spellsPerDay[7] = inValues.use("spells_per_day_7", m_spellsPerDay[7],
                                     Value.INTEGER_PARSER);
    m_spellsPerDay[8] = inValues.use("spells_per_day_8", m_spellsPerDay[8],
                                     Value.INTEGER_PARSER);
    m_spellsPerDay[9] = inValues.use("spells_per_day_9", m_spellsPerDay[9],
                                     Value.INTEGER_PARSER);
    m_spellsKnown[0] = inValues.use("spells_known_0", m_spellsKnown[0],
                                    Value.INTEGER_PARSER);
    m_spellsKnown[1] = inValues.use("spells_known_1", m_spellsKnown[1],
                                    Value.INTEGER_PARSER);
    m_spellsKnown[2] = inValues.use("spells_known_2", m_spellsKnown[2],
                                    Value.INTEGER_PARSER);
    m_spellsKnown[3] = inValues.use("spells_known_3", m_spellsKnown[3],
                                    Value.INTEGER_PARSER);
    m_spellsKnown[4] = inValues.use("spells_known_4", m_spellsKnown[4],
                                    Value.INTEGER_PARSER);
    m_spellsKnown[5] = inValues.use("spells_known_5", m_spellsKnown[5],
                                    Value.INTEGER_PARSER);
    m_spellsKnown[6] = inValues.use("spells_known_6", m_spellsKnown[6],
                                    Value.INTEGER_PARSER);
    m_spellsKnown[7] = inValues.use("spells_known_7", m_spellsKnown[7],
                                    Value.INTEGER_PARSER);
    m_spellsKnown[8] = inValues.use("spells_known_8", m_spellsKnown[8],
                                    Value.INTEGER_PARSER);
    m_spellsKnown[9] = inValues.use("spells_known_9", m_spellsKnown[9],
                                    Value.INTEGER_PARSER);
    m_spellAbility = inValues.use("spell_ability", m_spellAbility,
                                  Ability.PARSER);
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

    for(Alignment alignment : m_allowedAlignments)
      builder.addAllowedAlignment(alignment.toProto());

    if(m_hitDie.isPresent())
      builder.setHitDice(m_hitDie.get().toProto());

    if(m_skillPoints.isPresent())
      builder.setSkillPoints(m_skillPoints.get());

    for(Reference<BaseSkill> reference : m_classSkills)
      builder.addClassSkill(reference.getName());

    for(Proficiency proficiency : m_weaponProficiencies)
      builder.addWeaponProficiency(proficiency.toProto());

    builder.addAllIndividualWeaponProficiency(m_individualWeaponProficiencies);

    for(ArmorType proficiency : m_armorProficiencies)
        builder.addArmorProficiency(proficiency.toProto());

    for(QualityReference special : m_specialAttacks)
    {
      BaseMonsterProto.QualityReference.Builder reference =
        BaseMonsterProto.QualityReference.newBuilder();

      Reference<BaseQuality> ref = special.getReference();
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

      Reference<BaseQuality> ref = special.getReference();
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

    for(QualityReference feat : m_bonusFeats)
    {
      BaseMonsterProto.QualityReference.Builder reference =
          BaseMonsterProto.QualityReference.newBuilder();

      Reference<BaseQuality> ref = feat.getReference();
      reference.setReference(BaseMonsterProto.Reference.newBuilder()
                                 .setName(ref.getName())
                                 .build());

      if(feat.getCondition().isPresent())
        reference.setCondition(feat.getCondition().get());

      builder.addBonusFeat(BaseLevelProto.LeveledQuality.newBuilder()
                               .setLevel(feat.getLevel())
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

    for(int i = 0; i < m_spellsPerDay.length; i++)
    {
      BaseLevelProto.PerLevel.Builder level =
          BaseLevelProto.PerLevel.newBuilder();
      for(Integer spellsPerDay : m_spellsPerDay[i])
        level.addValue(spellsPerDay);
      builder.addSpellsPerDay(level.build());
    }

    for(int i = 0; i < m_spellsKnown.length; i++)
    {
      BaseLevelProto.PerLevel.Builder level =
          BaseLevelProto.PerLevel.newBuilder();
      for(Integer spellsKnown : m_spellsKnown[i])
        level.addValue(spellsKnown);
      builder.addSpellsKnown(level.build());
    }

    if(m_spellAbility.isPresent())
      builder.setSpellAbility(m_spellAbility.get().toProto());

    BaseLevelProto proto = builder.build();
    return proto;
  }

  /**
   * Read the values for the base level (class) from the given proto.
   *
   * @param inProto the proto to read from
   */
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
      m_allowedAlignments.add(Alignment.fromProto(alignment));

    if(proto.hasHitDice())
      m_hitDie = Optional.of(Dice.fromProto(proto.getHitDice()));

    if(proto.hasSkillPoints())
      m_skillPoints = Optional.of(proto.getSkillPoints());

    for(String ref : proto.getClassSkillList())
      m_classSkills.add(new Reference<BaseSkill>(BaseSkill.TYPE, ref));

    for(BaseWeaponProto.Proficiency proficiency
      : proto.getWeaponProficiencyList())
      m_weaponProficiencies.add(Proficiency.fromProto(proficiency));

    m_individualWeaponProficiencies.addAll(
        proto.getIndividualWeaponProficiencyList());

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

    for(BaseLevelProto.LeveledQuality feat : proto.getBonusFeatList())
      m_bonusFeats.add(new QualityReference
                                 (feat.getQuality().getReference().getName(),
                                  feat.getLevel(),
                                  feat.getQuality().getPerDay(),
                                  feat.getQuality().hasCondition()
                                      ? Optional.<String>of
                                      (feat.getQuality().getCondition())
                                      : Optional.<String>absent()));

    for(int baseAttack : proto.getBaseAttackList())
      m_baseAttacks.add(baseAttack);

    for(int save : proto.getFortitudeSaveList())
      m_fortitudeSaves.add(save);

    for(int save : proto.getReflexSaveList())
      m_reflexSaves.add(save);

    for(int save : proto.getWillSaveList())
      m_willSaves.add(save);

    for(int i = 0;
        i < m_spellsPerDay.length && i < proto.getSpellsPerDayCount(); i++)
      m_spellsPerDay[i] = proto.getSpellsPerDay(i).getValueList();

    for(int i = 0; i < m_spellsKnown.length && i < proto.getSpellsKnownCount();
        i++)
      m_spellsKnown[i] = proto.getSpellsKnown(i).getValueList();

    if(proto.hasSpellAbility())
      m_spellAbility = Optional.of(Ability.fromProto(proto.getSpellAbility()));
  }

  @Override
  protected Message defaultProto()
  {
    return BaseLevelProto.getDefaultInstance();
  }

  public boolean isClassSkill(String inName)
  {
    for(Reference<BaseSkill> skill : m_classSkills)
      if(skill.getName().equalsIgnoreCase(inName))
        return true;

    return false;
  }
}

