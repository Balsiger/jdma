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
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.proto.Entries.BaseArmorProto;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseLevelProto;
import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto;
import net.ixitxachitls.dma.proto.Entries.BaseWeaponProto;
import net.ixitxachitls.dma.proto.Values.ParametersProto;
import net.ixitxachitls.dma.values.ArmorType;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.Damage;
import net.ixitxachitls.dma.values.Dice;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.LongFormattedText;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Parameters;
import net.ixitxachitls.dma.values.Proficiency;
import net.ixitxachitls.dma.values.Reference;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.conditions.Condition;
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
  @Key("abbreviation")
  protected Name m_abbreviation = new Name();

  /** The adventures typical for the class. */
  @Key("adventures")
  protected LongFormattedText m_adventures = new LongFormattedText();

  /** The characteristics of the class. */
  @Key("characteristics")
  protected LongFormattedText m_characteristics = new LongFormattedText();

  /** The alignment options for this class. */
  @Key("alignment options")
  protected LongFormattedText m_alignmentOptions = new LongFormattedText();

  /** The religious views of the class. */
  @Key("religion")
  protected LongFormattedText m_religion = new LongFormattedText();

  /** The usual backgrounds for the class. */
  @Key("background")
  protected LongFormattedText m_background = new LongFormattedText();

  /** The races suited for this class. */
  @Key("races")
  protected LongFormattedText m_races = new LongFormattedText();

  /** The relation to other classes. */
  @Key("other classes")
  protected LongFormattedText m_otherClasses = new LongFormattedText();

  /** The role of the class. */
  @Key("role")
  protected LongFormattedText m_role = new LongFormattedText();

  /** The important abilities for the class. */
  @Key("important abilities")
  protected LongFormattedText m_importantAbilities = new LongFormattedText();

  /** The alignemts allowed for the class. */
  @Key("allowed alignments")
  protected ValueList<EnumSelection<BaseMonster.Alignment>>
    m_allowedAlignments = new ValueList<EnumSelection<BaseMonster.Alignment>>
      (", ",
        new EnumSelection<BaseMonster.Alignment>(BaseMonster.Alignment.class));

  /** The hit die. */
  @Key("hit die")
  protected Dice m_hitDie = new Dice();

  /** Skill points per level (x4 at first level, +Int modifier). */
  @Key("skill points")
  protected Number m_skillPoints = new Number(1, 10);

  /** The class skills. */
  @Key("class skills")
  protected ValueList<Reference<BaseSkill>> m_classSkill =
    new ValueList<>(new Reference<BaseSkill>(BaseSkill.TYPE));

  /** The weapon proficiencies. */
  @Key("weapon proficiencies")
  protected ValueList<EnumSelection<Proficiency>>
    m_weaponProficiencies =
      new ValueList<>(new EnumSelection<Proficiency>
        (Proficiency.class));

  /** The weapon proficiencies. */
  @Key("armor proficiencies")
  protected ValueList<EnumSelection<ArmorType>>
    m_armorProficiencies =
      new ValueList<>(new EnumSelection<ArmorType>
        (ArmorType.class));

  /** Special attacks. */
  @Key("special attacks")
  @WithBases
  protected ValueList<Multiple> m_specialAttacks =
  new ValueList<Multiple>
  (", ",
    new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new Number(1, 100).withEditType("number[level]"),
                           false),
      new Multiple.Element(new Multiple(new Multiple.Element []
      {
        new Multiple.Element
          (new Reference<BaseQuality>(BaseQuality.TYPE)
           .withParameter("Range", new Distance(), Parameters.Type.MAX)
           .withParameter("Increment", new Distance(), Parameters.Type.MAX)
           .withParameter("Name", new Name(), Parameters.Type.UNIQUE)
           .withParameter("Summary", new Name(), Parameters.Type.ADD)
           .withParameter("Level", new Number(0, 100), Parameters.Type.ADD)
           .withParameter("SpellLevel", new Number(0, 100), Parameters.Type.ADD)
           .withParameter("Value", new Number(1, 100), Parameters.Type.ADD)
           .withParameter("Modifier", new Modifier(), Parameters.Type.ADD)
           .withParameter("Dice", new Dice(), Parameters.Type.ADD)
           .withParameter("Times", new Number(1, 100), Parameters.Type.ADD)
           .withParameter("Class", new EnumSelection<BaseSpell.SpellClass>
                          (BaseSpell.SpellClass.class), Parameters.Type.ADD)
           .withParameter("Ability", new Number(0, 100), Parameters.Type.MAX)
           .withParameter("Type", new Name(), Parameters.Type.UNIQUE)
           .withParameter("Duration", new Name(), Parameters.Type.ADD)
           .withParameter("Initial", new Name(), Parameters.Type.UNIQUE)
           .withParameter("Secondary", new Name(), Parameters.Type.UNIQUE)
           .withParameter("Damage", new Damage(), Parameters.Type.ADD)
           .withParameter("Incubation", new Name(), Parameters.Type.MIN)
           .withParameter("DC", new Number(1, 100), Parameters.Type.MAX)
           .withParameter("HP", new Number(1, 1000), Parameters.Type.MAX)
           .withParameter("Burst", new Number(1, 100), Parameters.Type.MAX)
           .withParameter("Str", new Number(-100, 100), Parameters.Type.ADD)
           .withParameter("Dex", new Number(-100, 100), Parameters.Type.ADD)
           .withParameter("Con", new Number(-100, 100), Parameters.Type.ADD)
           .withParameter("Wis", new Number(-100, 100), Parameters.Type.ADD)
           .withParameter("Int", new Number(-100, 100), Parameters.Type.ADD)
           .withParameter("Cha", new Number(-100, 100), Parameters.Type.ADD)
           .withTemplate("reference", "/quality/"), false),
        new Multiple.Element(new Number(1, 100)
          .withEditType("name[per day]"), true, "/", null)
        }), false),
      }));

  /** Special qualities. */
  @Key("special qualities")
  protected ValueList<Multiple> m_specialQualities =
  new ValueList<Multiple>
  (", ",
    new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new Number(1, 100).withEditType("number[level]"),
                           false, null, ": "),
      new Multiple.Element(new Multiple(new Multiple.Element []
      {
        new Multiple.Element
          (new Reference<BaseQuality>(BaseQuality.TYPE)
           .withParameter("Range", new Distance(), Parameters.Type.MAX)
           .withParameter("Name", new Name(), Parameters.Type.UNIQUE)
           .withParameter("Summary", new Name(), Parameters.Type.ADD)
           .withParameter("Level", new Number(0, 100), Parameters.Type.ADD)
           .withParameter("SpellLevel", new Number(0, 100),
                          Parameters.Type.ADD)
           .withParameter("Racial",
                          new Number(-50, 50, true), Parameters.Type.ADD)
           .withParameter("Value", new Number(0, 100), Parameters.Type.ADD)
           .withParameter("Modifier", new Modifier(), Parameters.Type.ADD)
           .withTemplate("reference", "/quality/"), false),
        new Multiple.Element(new Condition()
                             .withEditType("string[condition]"),
                             true, " :if ", null),
        new Multiple.Element(new Number(1, 100).withEditType("name[per day]"),
                             true, "/", null),
      }), false, null, null),
    }));

  /** The base attack bonuses per level. */
  @Key("base attacks")
  protected ValueList<Number> m_baseAttacks =
    new ValueList<Number>(new Number(0, 20));

  /** The fortitude saves per level. */
  @Key("fortitude saves")
  protected ValueList<Number> m_fortitudeSaves =
    new ValueList<Number>(new Number(0, 20));

  /** The reflex saves per level. */
  @Key("reflex saves")
  protected ValueList<Number> m_reflexSaves =
    new ValueList<Number>(new Number(0, 20));

  /** The will saves per level. */
  @Key("will saves")
  protected ValueList<Number> m_willSaves =
    new ValueList<Number>(new Number(0, 20));

  static
  {
    extractVariables(BaseLevel.class);
  }

  @Override
  public boolean isDM(@Nullable BaseCharacter inUser)
  {
    if(inUser == null)
      return false;

    return inUser.hasAccess(BaseCharacter.Group.DM);
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
  public String getAbbreviation()
  {
    return m_abbreviation.get();
  }

  /**
   * Get the hit die for the level.
   *
   * @return  the hit die
   */
  public Dice getHitDie()
  {
    return m_hitDie;
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

  /**
   * Collect special qualities from all levels.
   *
   * @param  inLevel the level for which to compute qualities
   * @return all the special quality references for the given level
   */
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

  @Override
  public Message toProto()
  {
    BaseLevelProto.Builder builder = BaseLevelProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    if(m_abbreviation.isDefined())
      builder.setAbbreviation(m_abbreviation.get());

    if(m_adventures.isDefined())
      builder.setAdventures(m_adventures.get());

    if(m_characteristics.isDefined())
      builder.setCharacteristics(m_characteristics.get());

    if(m_alignmentOptions.isDefined())
      builder.setAlignmentOptions(m_alignmentOptions.get());

    if(m_religion.isDefined())
      builder.setReligion(m_religion.get());

    if(m_background.isDefined())
      builder.setBackground(m_background.get());

    if(m_races.isDefined())
      builder.setRaces(m_races.get());

    if(m_otherClasses.isDefined())
      builder.setOtherClasses(m_otherClasses.get());

    if(m_role.isDefined())
      builder.setRole(m_role.get());

    if(m_importantAbilities.isDefined())
      builder.setImportantAbilities(m_importantAbilities.get());

    if(m_allowedAlignments.isDefined())
      for(EnumSelection<BaseMonster.Alignment> alignment : m_allowedAlignments)
        builder.addAllowedAlignment(alignment.getSelected().toProto());

    if(m_hitDie.isDefined())
      builder.setHitDice(m_hitDie.toProto());

    if(m_skillPoints.isDefined())
      builder.setSkillPoints((int)m_skillPoints.get());

    if(m_classSkill.isDefined())
      for(Reference<BaseSkill> reference : m_classSkill)
        builder.addClassSkill(reference.getName());

    if(m_weaponProficiencies.isDefined())
      for(EnumSelection<Proficiency> proficiency
        : m_weaponProficiencies)
        builder.addWeaponProficiency(proficiency.getSelected().toProto());

    if(m_armorProficiencies.isDefined())
      for(EnumSelection<ArmorType> proficiency
        : m_armorProficiencies)
        builder.addArmorProficiency(proficiency.getSelected().toProto());

    if(m_specialAttacks.isDefined())
      for(Multiple special : m_specialAttacks)
      {
        BaseMonsterProto.QualityReference.Builder reference =
          BaseMonsterProto.QualityReference.newBuilder();

        Multiple quality = (Multiple)special.get(1);
        @SuppressWarnings("unchecked")
        Reference<BaseQuality> ref = (Reference<BaseQuality>)quality.get(0);
        reference.setReference
        (BaseMonsterProto.Reference.newBuilder()
         .setName(ref.getName())
         .setParameters(ref.getParameters() != null
         ? ParametersProto.getDefaultInstance()
         : ref.getParameters().toProto())
         .build());

        if(quality.get(1).isDefined())
          reference.setPerDay((int)((Number)quality.get(1)).get());

        builder.addSpecialAttack(BaseLevelProto.LeveledQuality.newBuilder()
                                 .setLevel((int)((Number)special.get(0)).get())
                                 .setQuality(reference.build())
                                 .build());
      }

    if(m_specialQualities.isDefined())
      for(Multiple special : m_specialQualities)
      {
        BaseMonsterProto.QualityReference.Builder reference =
          BaseMonsterProto.QualityReference.newBuilder();

        Multiple quality = (Multiple)special.get(1);
        @SuppressWarnings("unchecked")
        Reference<BaseQuality> ref = (Reference<BaseQuality>)quality.get(0);
        reference.setReference
        (BaseMonsterProto.Reference.newBuilder()
         .setName(ref.getName())
         .setParameters(ref.getParameters() != null
         ? ParametersProto.getDefaultInstance()
         : ref.getParameters().toProto())
         .build());

        if(quality.get(1).isDefined())
          reference.setCondition(((Condition)special.get(1)).getDescription());
        if(quality.get(2).isDefined())
          reference.setPerDay((int)((Number)special.get(2)).get());

        builder.addSpecialQuality(BaseLevelProto.LeveledQuality.newBuilder()
                                 .setLevel((int)((Number)special.get(0)).get())
                                 .setQuality(reference.build())
                                 .build());
      }

    if(m_baseAttacks.isDefined())
      for(Number number : m_baseAttacks)
        builder.addBaseAttack((int)number.get());

    if(m_fortitudeSaves.isDefined())
      for(Number number : m_fortitudeSaves)
        builder.addFortitudeSave((int)number.get());

    if(m_reflexSaves.isDefined())
      for(Number number : m_reflexSaves)
        builder.addReflexSave((int)number.get());
    if(m_willSaves.isDefined())
      for(Number number : m_willSaves)
        builder.addWillSave((int)number.get());


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
      m_abbreviation = m_abbreviation.as(proto.getAbbreviation());

    if(proto.hasAdventures())
      m_adventures = m_adventures.as(proto.getAdventures());

    if(proto.hasCharacteristics())
      m_characteristics = m_characteristics.as(proto.getCharacteristics());

    if(proto.hasAlignmentOptions())
      m_alignmentOptions = m_alignmentOptions.as(proto.getAlignmentOptions());

    if(proto.hasReligion())
      m_religion = m_religion.as(proto.getReligion());

    if(proto.hasBackground())
      m_background = m_background.as(proto.getBackground());

    if(proto.hasOtherClasses())
      m_otherClasses = m_otherClasses.as(proto.getOtherClasses());

    if(proto.hasRaces())
      m_races = m_races.as(proto.getRaces());

    if(proto.hasRole())
      m_role = m_role.as(proto.getRole());

    if(proto.hasImportantAbilities())
      m_importantAbilities =
        m_importantAbilities.as(proto.getImportantAbilities());

    if(proto.getAllowedAlignmentCount() > 0)
    {
      List<EnumSelection<BaseMonster.Alignment>> alignments = new ArrayList<>();
      for(BaseMonsterProto.Alignment alignment
        : proto.getAllowedAlignmentList())
        alignments.add(m_allowedAlignments.createElement()
                       .as(BaseMonster.Alignment.fromProto(alignment)));

      m_allowedAlignments = m_allowedAlignments.as(alignments);
    }

    if(proto.hasHitDice())
      m_hitDie = m_hitDie.fromProto(proto.getHitDice());

    if(proto.hasSkillPoints())
      m_skillPoints = m_skillPoints.as(proto.getSkillPoints());

    if(proto.getClassSkillCount() > 0)
    {
      List<Reference<BaseSkill>> references = new ArrayList<>();
      for(String ref : proto.getClassSkillList())
        references.add(m_classSkill.createElement().as(ref));

      m_classSkill = m_classSkill.as(references);
    }

    if(proto.getWeaponProficiencyCount() > 0)
    {
      List<EnumSelection<Proficiency>> proficiencies =
        new ArrayList<>();
      for(BaseWeaponProto.Proficiency proficiency
        : proto.getWeaponProficiencyList())
        proficiencies.add(m_weaponProficiencies.createElement()
                          .as(Proficiency.fromProto(proficiency)));

      m_weaponProficiencies = m_weaponProficiencies.as(proficiencies);
    }

    if(proto.getArmorProficiencyCount() > 0)
    {
      List<EnumSelection<ArmorType>> proficiencies =
        new ArrayList<>();
      for(BaseArmorProto.Type proficiency
        : proto.getArmorProficiencyList())
        proficiencies.add(m_armorProficiencies.createElement()
                          .as(ArmorType.fromProto(proficiency)));

      m_armorProficiencies = m_armorProficiencies.as(proficiencies);
    }

    if(proto.getSpecialAttackCount() > 0)
    {
      List<Multiple> references = new ArrayList<>();
      for(BaseLevelProto.LeveledQuality quality
        : proto.getSpecialAttackList())
      {
        Multiple multiple = m_specialAttacks.createElement();

        Multiple qMultiple = (Multiple)multiple.get(1);
        @SuppressWarnings("unchecked")
        Reference<BaseQuality> ref = (Reference<BaseQuality>)qMultiple.get(0);
        multiple =
          multiple.as(((Number)multiple.get(0)).as(quality.getLevel()),
                      qMultiple.as(ref.as(quality.getQuality().getReference()
                                          .getName())
                                   .withParameters(ref.getParameters()
                                                   .fromProto(quality
                                                              .getQuality()
                                                              .getReference()
                                                              .getParameters())
                                                              ),
                                   quality.getQuality().hasPerDay()
                                   ? ((Number)qMultiple.get(1))
                                     .as(quality.getQuality().getPerDay())
                                     : qMultiple.get(1)));

        references.add(multiple);
      }

      m_specialAttacks = m_specialAttacks.as(references);
    }

    if(proto.getSpecialQualityCount() > 0)
    {
      List<Multiple> references = new ArrayList<>();
      for(BaseLevelProto.LeveledQuality quality
        : proto.getSpecialQualityList())
      {
        Multiple multiple = m_specialQualities.createElement();

        Multiple qMultiple = (Multiple)multiple.get(1);
        @SuppressWarnings("unchecked")
        Reference<BaseQuality> ref = (Reference<BaseQuality>)qMultiple.get(0);
        multiple =
          multiple.as(((Number)multiple.get(0)).as(quality.getLevel()),
                      qMultiple.as(ref.as(quality.getQuality().getReference()
                                          .getName())
                                   .withParameters(ref.getParameters()
                                                   .fromProto(quality
                                                              .getQuality()
                                                              .getReference()
                                                              .getParameters())
                                                              ),
                                   quality.getQuality().hasCondition()
                                   ? new Condition(quality.getQuality()
                                                   .getCondition())
                                   : qMultiple.get(1),
                                   quality.getQuality().hasPerDay()
                                   ? ((Number)qMultiple.get(2))
                                     .as(quality.getQuality().getPerDay())
                                   : qMultiple.get(2)));

        references.add(multiple);
      }

      m_specialQualities = m_specialQualities.as(references);
    }

    if(proto.getBaseAttackCount() > 0)
    {
      List<Number> attacks = new ArrayList<>();
      for(int baseAttack : proto.getBaseAttackList())
        attacks.add(m_baseAttacks.createElement().as(baseAttack));

      m_baseAttacks = m_baseAttacks.as(attacks);
    }

    if(proto.getFortitudeSaveCount() > 0)
    {
      List<Number> saves = new ArrayList<>();
      for(int save : proto.getFortitudeSaveList())
        saves.add(m_fortitudeSaves.createElement().as(save));

      m_fortitudeSaves = m_fortitudeSaves.as(saves);
    }

    if(proto.getReflexSaveCount() > 0)
    {
      List<Number> saves = new ArrayList<>();
      for(int save : proto.getReflexSaveList())
        saves.add(m_reflexSaves.createElement().as(save));

      m_reflexSaves = m_reflexSaves.as(saves);
    }

    if(proto.getWillSaveCount() > 0)
    {
      List<Number> saves = new ArrayList<>();
      for(int save : proto.getWillSaveList())
        saves.add(m_willSaves.createElement().as(save));

      m_willSaves = m_willSaves.as(saves);
    }
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

