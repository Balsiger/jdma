/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto;
import net.ixitxachitls.dma.proto.Values.SpeedProto;
import net.ixitxachitls.dma.values.Annotated;
import net.ixitxachitls.dma.values.Damage;
import net.ixitxachitls.dma.values.Dice;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Parser;
import net.ixitxachitls.dma.values.Range;
import net.ixitxachitls.dma.values.Rational;
import net.ixitxachitls.dma.values.SizeModifier;
import net.ixitxachitls.dma.values.Speed;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Alignment;
import net.ixitxachitls.dma.values.enums.AlignmentStatus;
import net.ixitxachitls.dma.values.enums.AttackMode;
import net.ixitxachitls.dma.values.enums.AttackStyle;
import net.ixitxachitls.dma.values.enums.Climate;
import net.ixitxachitls.dma.values.enums.Language;
import net.ixitxachitls.dma.values.enums.LanguageModifier;
import net.ixitxachitls.dma.values.enums.MonsterSubtype;
import net.ixitxachitls.dma.values.enums.MonsterType;
import net.ixitxachitls.dma.values.enums.MovementMode;
import net.ixitxachitls.dma.values.enums.Organization;
import net.ixitxachitls.dma.values.enums.Save;
import net.ixitxachitls.dma.values.enums.Size;
import net.ixitxachitls.dma.values.enums.Terrain;
import net.ixitxachitls.dma.values.enums.Treasure;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the basic jDMA base monster.
 *
 * @file          BaseMonster.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */
public class BaseMonster extends BaseEntry
{
  /** An attack value. */
  public static class Attack
  {
    /**
     * Create the attack.
     *
     * @param inNumber the number of attacks available (can be 1d4)
     * @param inMode the mode of attacking
     * @param inStyle the attack style
     * @param inDamage the damage done by a successful attack
     */
    public Attack(Dice inNumber, AttackMode inMode, AttackStyle inStyle,
                  Damage inDamage)
    {
      m_number = inNumber;
      m_mode = inMode;
      m_style = inStyle;
      m_damage = inDamage;
    }

    /** The number of available attacks per full round. */
    private Dice m_number;

    /** The mode of attacking, i.e. what is used for the attack. */
    private AttackMode m_mode;

    /** The style for attacking, e.g. melee or ranged. */
    private AttackStyle m_style;

    /** The damage done by a single attack. */
    private Damage m_damage;

    /** The parser for attacks. */
    public static final Parser<Attack> PARSER =
      new Parser<Attack>(4)
      {
        @Override
        public Optional<Attack> doParse(String inNumber, String inMode,
                                        String inStyle, String inDamage)
        {
          Optional<Dice> number = Dice.PARSER.parse(inNumber);
          if(!number.isPresent())
            return Optional.absent();

          Optional<AttackMode> mode = AttackMode.fromString(inMode);
          if(!mode.isPresent())
            return Optional.absent();

          Optional<AttackStyle> style = AttackStyle.fromString(inStyle);
          if(!style.isPresent())
            return Optional.absent();

          Optional<Damage> damage = Damage.PARSER.parse(inDamage);
          if(!damage.isPresent())
            return Optional.absent();

          return Optional.of(new Attack(number.get(), mode.get(), style.get(),
                                        damage.get()));
        }
      };

    /**
     * Get the number of avaiable attacks.
     *
     * @return the number of attacks, a real dice for random numbers
     */
    public Dice getNumber()
    {
      return m_number;
    }

    /**
     * Getn the mode used for attacking, e.g. Claw, Fist, Weapon etc.
     *
     * @return the attack mode
     */
    public AttackMode getMode()
    {
      return m_mode;
    }

    /**
     * Get the attack style, whether ranged or melee.
     *
     * @return the attack style
     */
    public AttackStyle getStyle()
    {
      return m_style;
    }

    /**
     * Get the damage inflicted by a single attack.
     *
     * @return the damage value
     */
    public Damage getDamage()
    {
      return m_damage;
    }

    @Override
    public String toString()
    {
      return m_number + " " + m_mode + " " + m_style + " (" + m_damage + ")";
    }
  }

  /** A group value for organization of multiple monsters. */
  public static class Group
  {
    /**
     * Create the group.
     *
     * @param inOrganization how the group is organized
     * @param inNumber the number of monsters in the group
     * @param inPlus the additional monsters that are in the group
     */
    public Group(Organization inOrganization, Dice inNumber,
                 Optional<String> inPlus)
    {
      m_organization = inOrganization;
      m_number = inNumber;
      m_plus = inPlus;
    }

    /** The organization of the group. */
    private Organization m_organization;

    /** The number of monsters in the group with a dice for random. */
    private Dice m_number;

    /** The additional members of the group, if any. */
    private Optional<String> m_plus;

    /** The parser for groups. */
    public static final Parser<Group> PARSER =
      new Parser<Group>(3)
      {
        @Override
        public Optional<Group> doParse(String inOrganization,
                                       String inNumber, String inPlus)
        {
          Optional<Organization> organization =
            Organization.fromString(inOrganization);
          if(!organization.isPresent())
            return Optional.absent();

          Optional<Dice> number = Dice.PARSER.parse(inNumber);
          if(!number.isPresent())
            return Optional.absent();

          Optional<String> plus =
            inPlus.isEmpty() ? Optional.<String>absent() : Optional.of(inPlus);

          return Optional.of(new Group(organization.get(), number.get(), plus));
        }
      };

    /**
     * Get the origanizational type of the group.
     *
     * @return the origanization
     */
    public Organization getOrganization()
    {
      return m_organization;
    }

    /**
     * Get the number of monsters in the group.
     *
     * @return the number of monsters, with a real dice for random values
     */
    public Dice getNumber()
    {
      return m_number;
    }

    /**
     * Get a description about additional members of the group, if any.
     *
     * @return the additional group members
     */
    public Optional<String> getPlus()
    {
      return m_plus;
    }

    @Override
    public String toString()
    {
      return m_organization + " "
        + m_number
        + (m_plus.isPresent() && !m_plus.get().isEmpty()
            ? " plus " + m_plus.get() : "");
    }
  }

  /** An description about how a monster can advance. */
  public static class Advancement
  {
    /**
     * Create the advancement value.
     *
     * @param inRange the hit die range for advancement
     * @param inSize the new size when advancing
     */
    public Advancement(Range inRange, Size inSize)
    {
      m_range = inRange;
      m_size = inSize;
    }

    /** The hit die range for advancement. */
    private Range m_range;

    /** The new sie of the monster after advancement. */
    private Size m_size;

    /** The parser for the advancement value. */
    public static final Parser<Advancement> PARSER =
      new Parser<Advancement>(3)
      {
        @Override
        public Optional<Advancement> doParse(String inRange, String inSize)
        {
          Optional<Range> range = Range.PARSER.parse(inRange);
          if(!range.isPresent())
            return Optional.absent();

          Optional<Size> size = Size.fromString(inSize);
          if(!size.isPresent())
            return Optional.absent();

          return Optional.of(new Advancement(range.get(), size.get()));
        }
      };

    /**
     * Get the hit die (or level) range for this kind of advancement.
     *
     * @return the hit die range
     */
    public Range getRange()
    {
      return m_range;
    }

    /**
     * Get the size the monster gets when advancing.
     *
     * @return the new size
     */
    public Size getSize()
    {
      return m_size;
    }

    @Override
    public String toString()
    {
      return m_range + " HD (" + m_size + ")";
    }
  }

  /** A language option for the monster. */
  public static class LanguageOption
  {
    /**
     * Create the language option.
     *
     * @param inLanguage the language
     * @param inModifier the modifier to the language
     */
    public LanguageOption(Language inLanguage, LanguageModifier inModifier)
    {
      m_language = inLanguage;
      m_modifier = inModifier;
    }

    /** The language. */
    private Language m_language;

    /** The language modifier. */
    private LanguageModifier m_modifier;

    /** The parser for the language option. */
    public static final Parser<LanguageOption> PARSER =
      new Parser<LanguageOption>(2)
      {
        @Override
        public Optional<LanguageOption> doParse(String inLanguage,
                                                String inModifier)
        {
          Optional<Language> language = Language.fromString(inLanguage);
          if(!language.isPresent())
            return Optional.absent();

          Optional<LanguageModifier> modifier =
            LanguageModifier.fromString(inModifier);
          if(!modifier.isPresent())
            return Optional.absent();

          return
            Optional.of(new LanguageOption(language.get(), modifier.get()));
        }
      };

    /**
     * Get the language of the language option.
     *
     * @return the language
     */
    public Language getLanguage()
    {
      return m_language;
    }

    /**
     * Get the language modifier.
     *
     * @return the language modifier
     */
    public LanguageModifier getModifier()
    {
      return m_modifier;
    }

    @Override
    public String toString()
    {
      return m_modifier + " " + m_language;
    }
  }

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
    * This is the internal, default constructor for an undefined value.
    */
  protected BaseMonster()
  {
    super(TYPE);
  }

  /**
    * This is the normal constructor.
    *
    * @param       inName the name of the base item
    */
  public BaseMonster(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final BaseType<BaseMonster> TYPE =
    new BaseType.Builder<>(BaseMonster.class).build();

  /** The monsters size. */
  protected Size m_size = Size.UNKNOWN;

  /** The size modifier. */
  protected SizeModifier m_sizeModifier = SizeModifier.UNKNOWN;

  /** The monster type. */
  protected MonsterType m_monsterType = MonsterType.UNKNOWN;

  /** The monster subtypes. */
  protected List<MonsterSubtype> m_monsterSubtypes = new ArrayList<>();

  /** The monster's hit dice. */
  protected Optional<Dice> m_hitDice = Optional.absent();

  /** The monster's speed. */
  protected List<Speed> m_speeds = new ArrayList<>();

  /** The natural armor of the monster. */
  protected Optional<Modifier> m_naturalArmor = Optional.absent();

  /** The base attack bonus. */
  protected Optional<Integer> m_baseAttack = Optional.absent();

  /** The monster's Strength. */
  protected Optional<Integer> m_strength = Optional.absent();

  /** The monster's Dexterity. */
  protected Optional<Integer> m_dexterity = Optional.absent();

  /** The monster's Constitution. */
  protected Optional<Integer> m_constitution = Optional.absent();

  /** The monster's Intelligence. */
  protected Optional<Integer> m_intelligence = Optional.absent();

  /** The monster's Wisdom. */
  protected Optional<Integer> m_wisdom = Optional.absent();

  /** The monster's Charisma. */
  protected Optional<Integer> m_charisma = Optional.absent();

  /** The monster's fortitude save. */
  protected Optional<Integer> m_fortitudeSave = Optional.absent();

  /** The monster's will save. */
  protected Optional<Integer> m_willSave = Optional.absent();

  /** The monster's reflex save. */
  protected Optional<Integer> m_reflexSave = Optional.absent();

  /** The monster's attacks. */
  protected List<Attack> m_primaryAttacks = new ArrayList<>();

  /** The monster's secondary attacks. */
  protected List<Attack> m_secondaryAttacks = new ArrayList<>();

  /** The space the monster occupies (computed). */
  protected Optional<Distance> m_space = Optional.absent();

  /** The reach of the monster. */
  protected Optional<Distance> m_reach = Optional.absent();

  /** The special attacks. */
  protected List<String> m_specialAttacks = new ArrayList<>();

  /** The special qualities. */
  protected List<String> m_specialQualities = new ArrayList<>();

  /** The class skills. */
  protected List<String> m_classSkills = new ArrayList<>();

  /** The feats. */
  protected List<String> m_feats = new ArrayList<>();

  /** The terrain. */
  protected Terrain m_terrain = Terrain.UNKNOWN;

  /** The climate. */
  protected Climate m_climate = Climate.UNKNOWN;

  /** The monster's organization. */
  protected List<Group> m_organizations = new ArrayList<>();

  /** The monsters challenge rating. */
  protected Optional<Rational> m_cr = Optional.absent();

  /** The monster's possible treasure. */
  protected Treasure m_treasure = Treasure.UNKNOWN;

  /** The monster's alignment. */
  protected Alignment m_alignment = Alignment.UNKNOWN;

  /** The monster's alignemnt status. */
  protected AlignmentStatus m_alignmentStatus = AlignmentStatus.UNKNOWN;

  /** The monster's advancement. */
  protected List<Advancement> m_advancements = new ArrayList<>();

  /** The monsters level adjustment. */
  protected Optional<Integer> m_levelAdjustment = Optional.absent();

  /** The monsters languages. */
  protected List<LanguageOption> m_languages = new ArrayList<>();

  /** The monsters encounter. */
  protected Optional<String> m_encounter = Optional.absent();

  /** The monsters combat tactics. */
  protected Optional<String> m_combat = Optional.absent();

  /** The monsters tactics. */
  protected Optional<String> m_tactics = Optional.absent();

  /** The monsters character. */
  protected Optional<String> m_character = Optional.absent();

  /** The monsters reproduction. */
  protected Optional<String> m_reproduction = Optional.absent();

  /** The standard possessions. */
  protected List<String> m_possessions = new ArrayList<>();

  /** The good saving throws. */
  protected List<Save> m_goodSaves = new ArrayList<>();

  /** The feats entries. */
  protected Set<BaseFeat> m_featEntries = new HashSet<>();

  /** The monsters proficiencies. */
  protected List<String> m_proficiencies = new ArrayList<>();

  /** Whether this is a quadruped. */
  private boolean m_quadruped = false;

  /**
   * Get the monster's size.
   *
   * @return the size
   */
  public Size getSize()
  {
    return m_size;
  }

  /**
   * Get the monster's annotated and combined size.
   *
   * @return the annotated, combined size
   */
  public Annotated<Optional<Size>> getCombinedSize()
  {
    if(m_size != Size.UNKNOWN)
      return new Annotated.Max<>(m_size, getName());

    Annotated<Optional<Size>> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedSize());

    return combined;
  }

  /**
   * Get the monster's size modifier.
   *
   * @return the size modifier
   */
  public SizeModifier getSizeModifier()
  {
    return m_sizeModifier;
  }

  /**
   * Get the combined and annotated size modifier.
   *
   * @return the annotated size modifier
   */
  public Annotated<Optional<SizeModifier>> getCombinedSizeModifier()
  {
    if(m_sizeModifier != SizeModifier.UNKNOWN)
      return new Annotated.Max<>(m_sizeModifier, getName());

    Annotated<Optional<SizeModifier>> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedSizeModifier());

    return combined;
  }

  /**
   * Get the monster type.
   *
   * @return the type
   */
  public MonsterType getMonsterType()
  {
    return m_monsterType;
  }

  /**
   * Get the annotated and combined monster type.
   *
   * @return the annotated type
   */
  public Annotated<Optional<MonsterType>> getCombinedMonsterType()
  {
    if(m_monsterType != MonsterType.UNKNOWN)
      return new Annotated.Max<>(m_monsterType, getName());

    Annotated.Max<MonsterType> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedMonsterType());

    return combined;
  }

  /**
   * Get the monster subtype.
   *
   * @return the monster's subtype
   */
  public List<MonsterSubtype> getMonsterSubtypes()
  {
    return Collections.unmodifiableList(m_monsterSubtypes);
  }

  /**
   * Get the monster's annotated and combined subtype.
   *
   * @return the annotated subtype
   */
  public Annotated<List<MonsterSubtype>> getCombinedMonsterSubtypes()
  {
    if(!m_monsterSubtypes.isEmpty())
      return new Annotated.List<>(m_monsterSubtypes, getName());

    Annotated.List<MonsterSubtype> combined = new Annotated.List<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedMonsterSubtypes());

    return combined;
  }

  /**
   * Get the monster's hit dice.
   *
   * @return the hit dice
   */
  public Optional<Dice> getHitDice()
  {
    return m_hitDice;
  }

  public Annotated.Integer getCombinedHitDie()
  {
    if(m_hitDice.isPresent())
      return new Annotated.Integer(m_hitDice.get().getNumber(), getName());

    Annotated.Integer combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedHitDie());

    return combined;
  }

  /**
   * Get all of the monster's available speeds.
   *
   * @return the speeds
   */
  public List<Speed> getSpeeds()
  {
    return Collections.unmodifiableList(m_speeds);
  }

  /**
   * Get the monsters annotated and combined speed for the given movement mode.
   *
   * @param inMode the mode for which to get the speed
   * @return the speed for the movement mode
   */
  public Optional<Annotated.Arithmetic<Speed>>
    getSpeedAnnotated(MovementMode inMode)
  {
    for(Speed speed : m_speeds)
      if(speed.getMode() == inMode)
        return Optional.of(new Annotated.Arithmetic<Speed>
          (speed, getName()));

    Annotated.Arithmetic<Speed> speed = null;
    for(BaseEntry base : getBaseEntries())
    {
      Optional<Annotated.Arithmetic<Speed>> baseSpeed =
        ((BaseMonster)base).getSpeedAnnotated(inMode);
      if(baseSpeed.isPresent())
        if(speed == null || !speed.get().isPresent()
           || speed.get().get().getSpeed().compareTo
               (baseSpeed.get().get().get().getSpeed()) < 0)
          speed = baseSpeed.get();
    }

    return Optional.fromNullable(speed);
  }

  /**
   * Get all the combined and annotated speeds.
   *
   * @return the annotated speeds
   */
  public List<Annotated.Arithmetic<Speed>> getCombinedSpeeds()
  {
    List<Annotated.Arithmetic<Speed>> speeds = new ArrayList<>();

    for(MovementMode mode : MovementMode.values())
    {
      Optional<Annotated.Arithmetic<Speed>> speed =
        getSpeedAnnotated(mode);
      if(speed.isPresent())
        speeds.add(speed.get());
    }

    return speeds;
  }

  /**
   * Get the monster's natural armor.
   *
   * @return the natural armor
   */
  public Optional<Modifier> getNaturalArmor()
  {
    return m_naturalArmor;
  }

  /**
   * Get the combined natural armor.
   *
   * @return the combined natural armor
   */
  public Annotated.Arithmetic<Modifier> getCombinedNaturalArmor()
  {
    if(m_naturalArmor.isPresent())
      return new Annotated.Arithmetic<Modifier>(m_naturalArmor.get(),
                                                   getName());

    Annotated.Arithmetic<Modifier> combined = new Annotated.Arithmetic<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedNaturalArmor());

    return combined;
  }

  /**
   * Get the monster's base attack.
   *
   * @return the base attack
   */
  public Optional<Integer> getBaseAttack()
  {
    return m_baseAttack;
  }

  /**
   * Get the combined and annotated base attack.
   *
   * @return the annotated base attack
   */
  public Annotated<Optional<Integer>> getCombinedBaseAttack()
  {
    Optional<Integer> attack = getBaseAttack();
    if(attack.isPresent())
      return new Annotated.Bonus(attack.get(), getName());

    Annotated.Bonus combined = new Annotated.Bonus();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedBaseAttack());

    return combined;
  }

  /**
   * Get the monster's strenght score.
   *
   * @return the strength score
   */
  public Optional<Integer> getStrength()
  {
    return m_strength;
  }

  /**
   * Get hte monster's annotated and combined strength score.
   *
   * @return the strength score
   */
  public Annotated<Optional<Integer>> getCombinedStrength()
  {
    Optional<Integer> strength = getStrength();
    if(strength.isPresent())
      return new Annotated.Integer(strength.get(), getName());

    Annotated<Optional<Integer>> combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedStrength());

    return combined;
  }

  /**
   * Get the dexterity score.
   *
   * @return the dexterity score
   */
  public Optional<Integer> getDexterity()
  {
    return m_dexterity;
  }

  /**
   * Get the annotated and combined dexterity score.
   *
   * @return the dexterity score
   */
  public Annotated<Optional<Integer>> getCombinedDexterity()
  {
    Optional<Integer> dexterity = getDexterity();
    if(dexterity.isPresent())
      return new Annotated.Integer(dexterity.get(), getName());

    Annotated<Optional<Integer>> combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedDexterity());

    return combined;
  }

  /**
   * Get the constitution score.
   *
   * @return the score
   */
  public Optional<Integer> getConstitution()
  {
    return m_constitution;
  }

  /**
   * Get the annotated and combined constitution score.
   *
   * @return the annotated score
   */
  public Annotated<Optional<Integer>> getCombinedConstitution()
  {
    Optional<Integer> constitution = getConstitution();
    if(constitution.isPresent())
      return new Annotated.Integer(constitution.get(), getName());

    Annotated<Optional<Integer>> combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedConstitution());

    return combined;
  }

  /**
   * Get the intelligence score.
   *
   * @return the score
   */
  public Optional<Integer> getIntelligence()
  {
    return m_intelligence;
  }

  /**
   * Get the annotated and combined intelligence score.
   *
   * @return the annotated and combined score
   */
  public Annotated<Optional<Integer>> getCombinedIntelligence()
  {
    Optional<Integer> intelligence = getIntelligence();
    if(intelligence.isPresent())
      return new Annotated.Integer(intelligence.get(), getName());

    Annotated<Optional<Integer>> combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedIntelligence());

    return combined;
  }

  /**
   * Get the wisdom score.
   *
   * @return the score
   */
  public Optional<Integer> getWisdom()
  {
    return m_wisdom;
  }

  /**
   * Get the annotated and combined wisdom score.
   *
   * @return the annotated and combined score
   */
  public Annotated<Optional<Integer>> getCombinedWisdom()
  {
    Optional<Integer> wisdom = getWisdom();
    if(wisdom.isPresent())
      return new Annotated.Integer(wisdom.get(), getName());

    Annotated<Optional<Integer>> combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedWisdom());

    return combined;
  }

  /**
   * Get the charisma score.
   *
   * @return the score
   */
  public Optional<Integer> getCharisma()
  {
    return m_charisma;
  }

  /**
   * Get the annotated and combined charisma score.
   *
   * @return the annotated and combined score
   */
  public Annotated<Optional<Integer>> getCombinedCharisma()
  {
    Optional<Integer> charisma = getCharisma();
    if(charisma.isPresent())
      return new Annotated.Integer(charisma.get(), getName());

    Annotated<Optional<Integer>> combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedCharisma());

    return combined;
  }

  /**
   * Get the fortitude save.
   *
   * @return the fortitude save
   */
  public Optional<Integer> getFortitudeSave()
  {
    return m_fortitudeSave;
  }

  /**
   * Get the combined and annotated fortitude save.
   *
   * @return the combined and annotated save
   */
  public Annotated.Bonus getCombinedFortitudeSave()
  {
    if(m_fortitudeSave.isPresent())
      return new Annotated.Bonus(m_fortitudeSave.get(), getName());

    Annotated.Bonus combined = new Annotated.Bonus();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedFortitudeSave());

    return combined;
  }

  /**
   * Get the will save.
   *
   * @return the save
   */
  public Optional<Integer> getWillSave()
  {
    return m_willSave;
  }

  /**
   * Get the combined and annotated will save.
   *
   * @return the annotated and combined save
   */
  public Annotated.Bonus getCombinedWillSave()
  {
    if(m_willSave.isPresent())
      return new Annotated.Bonus(m_willSave.get(), getName());

    Annotated.Bonus combined = new Annotated.Bonus();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedWillSave());

    return combined;
  }

  /**
   * Get the relfex save.
   *
   * @return the save
   */
  public Optional<Integer> getReflexSave()
  {
    return m_reflexSave;
  }

  /**
   * Get the combined and annotated relfex save.
   *
   * @return the annotated and combined save
   */
  public Annotated.Bonus getCombinedReflexSave()
  {
    if(m_reflexSave.isPresent())
      return new Annotated.Bonus(m_reflexSave.get(), getName());

    Annotated.Bonus combined = new Annotated.Bonus();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedReflexSave());

    return combined;
  }

  /**
   * Get the list primary attacks.
   *
   * @return the primary attacks
   */
  public List<Attack> getPrimaryAttacks()
  {
    return Collections.unmodifiableList(m_primaryAttacks);
  }

  /**
   * Get all the combined and annotated primary attacks.
   *
   * @return all the primary attacks
   */
  public Annotated<List<Attack>> getCombinedPrimaryAttacks()
  {
    if(!m_primaryAttacks.isEmpty())
      return new Annotated.List<Attack>(m_primaryAttacks, getName());

    Annotated<List<Attack>> combined = new Annotated.List<Attack>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedPrimaryAttacks());

    return combined;
  }

  /**
   * Get the secondary attacks.
   *
   * @return the secondary attacks
   */
  public List<Attack> getSecondaryAttacks()
  {
    return Collections.unmodifiableList(m_secondaryAttacks);
  }

  /**
   * Get all of the combined and annotated secondary attacks.
   *
   * @return all the secondary attacks
   */
  public Annotated<List<Attack>> getCombinedSecondaryAttacks()
  {
    if(!m_primaryAttacks.isEmpty())
      return new Annotated.List<Attack>(m_secondaryAttacks, getName());

    Annotated<List<Attack>> combined = new Annotated.List<Attack>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedSecondaryAttacks());

    return combined;
  }

  /**
   * Get the space the monster occupies.
   *
   * @return the space
   */
  public Optional<Distance> getSpace()
  {
    return m_space;
  }

  /**
   * Get the combined and annotated space.
   *
   * @return the annotated space
   */
  public Annotated<Optional<Distance>> getCombinedSpace()
  {
    if(m_space.isPresent())
      return new Annotated.Max<Distance>(m_space.get(), getName());

    Annotated<Optional<Distance>> combined =
      new Annotated.Max<Distance>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedSpace());

    return combined;
  }

  /**
   * Get a monster's special attacks.
   *
   * @return the special attacks
   */
  public List<String> getSpecialAttacks()
  {
    return Collections.unmodifiableList(m_specialAttacks);
  }

  /**
   * Get all of a monster's combined and annotated special attacks.
   *
   * @return the special attacks
   */
  public Annotated<List<String>> getCombinedSpecialAttacks()
  {
    if(!m_specialAttacks.isEmpty())
      return new Annotated.List<String>(m_specialAttacks, getName());

    Annotated<List<String>> combined = new Annotated.List<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedSpecialAttacks());

    return combined;
  }

  /**
   * Get a monster's special qualities.
   *
   * @return the special qualities
   */
  public List<String> getSpecialQualities()
  {
    return Collections.unmodifiableList(m_specialQualities);
  }

  /**
   * Get all of a monster's annotated and combined special qualtities.
   *
   * @return the special qualities
   */
  public Annotated<List<String>> getCombinedSpecialQualities()
  {
    if(!m_specialQualities.isEmpty())
      return new Annotated.List<String>(m_specialQualities, getName());

    Annotated<List<String>> combined = new Annotated.List<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedSpecialQualities());

    return combined;
  }

  /**
   * Get the class skills.
   *
   * @return a list of the class skills
   */
  public List<String> getClassSkills()
  {
    return Collections.unmodifiableList(m_classSkills);
  }

  /**
   * Get all the combined and annotated class skills.
   *
   * @return the class skills
   */
  public Annotated<List<String>> getCombinedClassSkills()
  {
    if(!m_classSkills.isEmpty())
      return new Annotated.List<String>(m_classSkills, getName());

    Annotated<List<String>> combined = new Annotated.List<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedClassSkills());

    return combined;
  }

  /**
   * Get the monster's feats.
   *
   * @return the feats
   */
  public List<String> getFeats()
  {
    return Collections.unmodifiableList(m_feats);
  }

  /**
   * Get all the combined and annotated feats.
   *
   * @return the feats
   */
  public Annotated<List<String>> getCombinedFeats()
  {
    if(!m_feats.isEmpty())
      return new Annotated.List<String>(m_feats, getName());

    Annotated<List<String>> combined = new Annotated.List<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedFeats());

    return combined;
  }

  /**
   * Get the terrain the monster can usually be found.
   *
   * @return the terrain
   */
  public Terrain getTerrain()
  {
    return m_terrain;
  }

  /**
   * Get the annotated and combined terrain.
   *
   * @return the terrain
   */
  public Annotated<Optional<Terrain>> getCombinedTerrain()
  {
    if(m_terrain != Terrain.UNKNOWN)
      return new Annotated.Max<>(m_terrain, getName());

    Annotated<Optional<Terrain>> combined = new Annotated.Max<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedTerrain());

    return combined;
  }

  /**
   * Get the climate the monster usually can be found in.
   *
   * @return the climate
   */
  public Climate getClimate()
  {
    return m_climate;
  }

  /**
   * Get the annotated and combined climate.
   *
   * @return the climate
   */
  public Annotated<Optional<Climate>> getCombinedClimate()
  {
    if(m_climate != Climate.UNKNOWN)
      return new Annotated.Max<>(m_climate, getName());

    Annotated<Optional<Climate>> combined = new Annotated.Max<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedClimate());

    return combined;
  }

  /**
   * Get all the organizations the monster can be in.
   *
   * @return the organizations
   */
  public List<Group> getOrganizations()
  {
    return Collections.unmodifiableList(m_organizations);
  }

  /**
   * Get all the combined and annotated organizations.
   *
   * @return the organizations
   */
  public Annotated<List<Group>> getCombinedOrganizations()
  {
    if(!m_organizations.isEmpty())
      return new Annotated.List<>(m_organizations, getName());

    Annotated<List<Group>> combined = new Annotated.List<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedOrganizations());

    return combined;
  }

  /**
   * Get the monster's challange rating.
   *
   * @return the CR
   */
  public Optional<Rational> getCr()
  {
    return m_cr;
  }

  /**
   * Get the monster's combined and annotated challenge rating.
   *
   * @return the combined and annotated cr
   */
  public Annotated<Optional<Rational>> getCombinedCr()
  {
    if(m_cr.isPresent())
      return new Annotated.Arithmetic<Rational>(m_cr.get(), getName());

    Annotated<Optional<Rational>> combined = new Annotated.Arithmetic<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedCr());

    return combined;
  }

  /**
   * Get a monster's usual treasure.
   *
   * @return the usual treasure
   */
  public Treasure getTreasure()
  {
    return m_treasure;
  }

  /**
   * Get a monster's usual, combined and annotated treasure.
   *
   * @return the treasure
   */
  public Annotated<Optional<Treasure>> getCombinedTreasure()
  {
    if(m_treasure != Treasure.UNKNOWN)
      return new Annotated.Max<>(m_treasure, getName());

    Annotated<Optional<Treasure>> combined = new Annotated.Max<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedTreasure());

    return combined;
  }

  /**
   * Get the alignment.
   *
   * @return the alignment
   */
  public Alignment getAlignment()
  {
    return m_alignment;
  }

  /**
   * The combined and annnotated alignment.
   *
   * @return the alignment
   */
  public Annotated<Optional<Alignment>> getCombinedAlignment()
  {
    if(m_alignment != Alignment.UNKNOWN)
      return new Annotated.Max<>(m_alignment, getName());

    Annotated<Optional<Alignment>> combined = new Annotated.Max<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedAlignment());

    return combined;
  }

  /**
   * Get the alignment status.
   *
   * @return the status
   */
  public AlignmentStatus getAlignmentStatus()
  {
    return m_alignmentStatus;
  }

  /**
   * Get the combined alignment status.
   *
   * @return the status
   */
  public Annotated<Optional<AlignmentStatus>> getCombinedAlignmentStatus()
  {
    if(m_alignmentStatus != AlignmentStatus.UNKNOWN)
      return new Annotated.Max<>(m_alignmentStatus, getName());

    Annotated<Optional<AlignmentStatus>> combined = new Annotated.Max<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedAlignmentStatus());

    return combined;
  }

  /**
   * Get the possible advvancements the monster can go through.
   *
   * @return the advancements
   */
  public List<Advancement> getAdvancements()
  {
    return m_advancements;
  }

  /**
   * Get the combined and annotated advancements.
   *
   * @return the advancements
   */
  public Annotated<List<Advancement>> getCombinedAdvancements()
  {
    if(!m_advancements.isEmpty())
      return new Annotated.List<>(m_advancements, getName());

    Annotated<List<Advancement>> combined = new Annotated.List<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedAdvancements());

    return combined;
  }

  /**
   * Get the level adjustment for monster with classes.
   *
   * @return the level adjustment
   */
  public Optional<Integer> getLevelAdjustment()
  {
    return m_levelAdjustment;
  }

  /**
   * Get the combined and annotated level adjustment.
   *
   * @return the level adjustment
   */
  public Annotated<Optional<Integer>> getCombinedLevelAdjustment()
  {
    Optional<Integer> adjustment = getLevelAdjustment();
    if(adjustment.isPresent())
      return new Annotated.Integer(adjustment.get(), getName());

    Annotated.Integer combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedLevelAdjustment());

    return combined;
  }

  /**
   * Get the languages available to the monster.
   *
   * @return the languages
   */
  public List<LanguageOption> getLanguages()
  {
    return m_languages;
  }

  /**
   * Get the combined and annotated languages available to the monster.
   *
   * @return the languages
   */
  public Annotated<List<LanguageOption>> getCombinedLanguages()
  {
    if(!m_languages.isEmpty())
      return new Annotated.List<>(m_languages, getName());

    Annotated<List<LanguageOption>> combined = new Annotated.List<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedLanguages());

    return combined;
  }

  /**
   * Get a description on how this monster is usually encountered.
   *
   * @return the description
   */
  public Optional<String> getEncounter()
  {
    return m_encounter;
  }


  /**
   * Get a combined and annotated description on how this monster is usually
   * encountered.
   *
   * @return the description, annotated and combined
   */
  public Annotated<Optional<String>> getCombinedEncounter()
  {
    if(m_encounter.isPresent() && !m_encounter.get().isEmpty())
      return new Annotated.String(m_encounter.get(), getName());

    Annotated<Optional<java.lang.String>> combined = new Annotated.String();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedEncounter());

    return combined;
  }

  /**
   * Get the monster's combat description.
   *
   * @return the description
   */
  public Optional<String> getCombat()
  {
    return m_combat;
  }

  /**
   * Get a monsters combined and annotated combat description.
   *
   * @return the annotated description
   */
  public Annotated<Optional<String>> getCombinedCombat()
  {
    if(m_combat.isPresent() && !m_combat.get().isEmpty())
      return new Annotated.String(m_combat.get(), getName());

    Annotated<Optional<java.lang.String>> combined = new Annotated.String();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedCombat());

    return combined;
  }

  /**
   * Get a description about the monster's tactics.
   *
   * @return the description
   */
  public Optional<String> getTactics()
  {
    return m_tactics;
  }

  /**
   * Get an annotated and combined description about a monster's tactics.
   *
   * @return the tactics
   */
  public Annotated<Optional<String>> getCombinedTactics()
  {
    if(m_tactics.isPresent() && !m_tactics.get().isEmpty())
      return new Annotated.String(m_tactics.get(), getName());

    Annotated<Optional<java.lang.String>> combined = new Annotated.String();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedTactics());

    return combined;
  }

  /**
   * Get the monster's usual character description.
   *
   * @return the description
   */
  public Optional<String> getCharacter()
  {
    return m_character;
  }

  /**
   * Get a monster's combined and annotated character description.
   *
   * @return the description, annotated and combined
   */
  public Annotated<Optional<String>> getCombinedCharacter()
  {
    if(m_character.isPresent() && !m_character.get().isEmpty())
      return new Annotated.String(m_character.get(), getName());

    Annotated<Optional<java.lang.String>> combined = new Annotated.String();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedCharacter());

    return combined;
  }

  /**
   * Get a description how the monster usually reproduces.
   *
   * @return the reproduction description
   */
  public Optional<String> getReproduction()
  {
    return m_reproduction;
  }

  /**
   * Get a combined and annotated description of reproduction.
   *
   * @return the description
   */
  public Annotated<Optional<String>> getCombinedReproduction()
  {
    if(m_reproduction.isPresent() && !m_reproduction.get().isEmpty())
      return new Annotated.String(m_reproduction.get(), getName());

    Annotated<Optional<java.lang.String>> combined = new Annotated.String();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedReproduction());

    return combined;
  }

  /**
   * Get a list of all standard possessions.
   *
   * @return the possesions
   */
  public List<String> getPossessions()
  {
    return m_possessions;
  }

  /**
   * Get the combined and annotated list of possessions.
   *
   * @return the possessions
   */
  public Annotated<List<String>> getCombinedPossessions()
  {
    if(!m_possessions.isEmpty())
      return new Annotated.List<>(m_possessions, getName());

    Annotated<List<String>> combined = new Annotated.List<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedPossessions());

    return combined;
  }

  /**
   * Get monster's good saves.
   *
   * @return the good saves
   */
  public List<Save> getGoodSaves()
  {
    return m_goodSaves;
  }

  /**
   * Get all the entries for the monster's feats.
   *
   * @return the feat entries
   */
  public Set<BaseFeat> getFeatEntries()
  {
    return m_featEntries;
  }

  /**
   * Get the monster's reach.
   *
   * @return      the monsters reach
   */
  public Optional<Distance> getReach()
  {
    return m_reach;
  }

  /**
   * Get the combined and annotated reach of the monster.
   *
   * @return the reach
   */
  public Annotated<Optional<Distance>> getCombinedReach()
  {
    if(m_reach.isPresent())
      return new Annotated.Max<Distance>(m_reach.get(), getName());

    Annotated<Optional<Distance>> combined =
      new Annotated.Max<Distance>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedReach());

    return combined;
  }

  /**
   * Get the proficiencies the monster has.
   *
   * @return the proficienceis
   */
  public List<String> getProficiencies()
  {
    return m_proficiencies;
  }

  /**
   * Get the combined proficiencies.
   *
   * @return the combined proficiencies
   */
  public Annotated.List<String> getCombinedProficiencies()
  {
    if(!m_proficiencies.isEmpty())
      return new Annotated.List<String>(m_proficiencies, getName());

    Annotated.List<String> combined = new Annotated.List<>();
    for (BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedProficiencies());

    return combined;
  }

  public boolean isQuadruped()
  {
    return m_quadruped;
  }

  public Annotated.Boolean isCombinedQuadruped()
  {
    if(m_quadruped)
      return new Annotated.Boolean(true, getName());

    Annotated.Boolean combined = new Annotated.Boolean();
    for (BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).isCombinedQuadruped());

    return combined;
  }

  /**
   * Get the current modifier for the given ability.
   *
   * @param       inAbility the ability to get the modifier for
   *
   * @return      the ability modifier
   */
  // public int getAbilityModifier(Global.Ability inAbility)
  // {
  //   if(inAbility == null)
  //     throw new IllegalArgumentException("must have an ability here");

  //   switch(inAbility)
  //   {
  //     case STRENGTH:

  //       return abilityMod(m_strength.get());

  //     case DEXTERITY:

  //       return abilityMod(m_dexterity.get());

  //     case CONSTITUTION:

  //       return abilityMod(m_constitution.get());

  //     case INTELLIGENCE:

  //       return abilityMod(m_intelligence.get());

  //     case WISDOM:

  //       return abilityMod(m_wisdom.get());

  //     case CHARISMA:

  //       return abilityMod(m_charisma.get());

  //     default:

  //       return 0;
  //   }
  // }

  /**
   * Determine the maximally possible hit points (without any modifiers).
   *
   * @return      the maximally possible hit points
   */
  // public int getMaxHP()
  // {
  //   return (int)m_hitDice.getMax();
  // }

  /**
   * Determine the minimally possible hit points (without any modifiers).
   *
   * @return      the minimally possible hit points
   *
   */
  // public int getMinHP()
  // {
  //   return (int)m_hitDice.getMin();
  // }

  /**
   * Get the special qualities for this and all base monsters.
   *
   * @return  a list of base qualities
   */
  /*
  @SuppressWarnings("unchecked") // need to cast multiple part
  public List<Reference<BaseQuality>> collectSpecialQualities()
  {
    List<Reference<BaseQuality>> qualities = Lists.newArrayList();

    for(Multiple quality : m_specialQualities)
      qualities.add((Reference<BaseQuality>)quality.get(0));

    for(BaseEntry base : getBaseEntries())
    {
      if(!(base instanceof BaseMonster))
        continue;

      qualities.addAll(((BaseMonster)base).collectSpecialQualities());
    }

    return qualities;
  }
  */

  /**
   * Get the level of the monster.
   *
   * @return      the monster's level or hit dice
   */
  public int level()
  {
    if(m_hitDice.isPresent())
      return m_hitDice.get().getNumber();

    return 0;
  }

  /**
   * Get the ability modifier for the given value.
   *
   * @param       inAbility the ability to compute the modifier for
   *
   * @return      the ability modifier
   */
  // public static int abilityMod(long inAbility)
  // {
  //   // if negative, we don't have the ability, thus a modifier of 0
  //   if(inAbility <= 0)
  //     return 0;

  //   return (int)(inAbility / 2) - 5;
  // }

  /**
   * Get the number of skill points of the monster.
   *
   * @return      the number of skill points
   */
  // public int skillPoints()
  // {
  //   int type = ((Selection)m_monsterType.get(0).get()).getSelected();

  //   if(type < 0)
  //     type = 0;

  //   return (level() + 3)
  //     * Math.max(1,
  //              Global.SKILL_POINTS[type] + abilityMod(m_intelligence.get()));
  // }

  /**
   * Get the number of skill ranks in a specific skill.
   *
   * @param       inSkill the name of the skill to get the ranks of
   *
   * @return      the number of skill ranks
   */
  // public int skillRanks(String inSkill)
  // {
  //   for(Multiple skill : m_classSkills)
  //   {
  //     if(((SimpleText)skill.get(0).get()).get().equalsIgnoreCase(inSkill))
  //     {
  //       if(!skill.get(2).get().isDefined())
  //         return 0;

  //       return (int)((Number)skill.get(2).get()).get();
  //     }
  //   }

  //   return 0;
  // }

  /**
   * Roll the maximal hit points of the monster, including all modifiers.
   *
   * @return      the maximal hit point a monster of this type has
   */
  // public int rollMaxHP()
  // {
  //   return m_hitDice.roll();
  // }

  /**
   * Determine if the monster has the given quality.
   *
   * @param       inQuality the quality to look for
   *
   * @return      true if the quality is there, false if not
   */
  /*
  public boolean hasQuality(String inQuality)
  {
    for(Multiple value : m_specialQualities)
      if(value.toString().equalsIgnoreCase(inQuality))
        return true;

    for(Multiple value : m_specialAttacks)
      if(value.toString().equalsIgnoreCase(inQuality))
        return true;

    for(BaseEntry base : getBaseEntries())
      if(base instanceof BaseMonster)
        if(((BaseMonster)base).hasQuality(inQuality))
          return true;

    return false;
  }
  */

  /**
   * Determine if the monster has the given feat.
   *
   * @param       inFeat the feat to look for
   *
   * @return      true if the feat is there, false if not
   */
  /*
  public boolean hasFeat(String inFeat)
  {
    for(Reference<BaseFeat> value : m_feats)
      if(value.toString().equalsIgnoreCase(inFeat))
        return true;

    for(BaseEntry base : getBaseEntries())
      if(base instanceof BaseMonster)
        if(((BaseMonster)base).hasFeat(inFeat))
          return true;

    return false;
  }
  */

  /**
   * Determine if the monster understands the given lanauge.
   *
   * @param       inLanguage the language to look for
   *
   * @return      true if the language is there, false if not
   */
  // public boolean hasLanguage(String inLanguage)
  // {
  //   for(Multiple value : m_languages)
  //     if(value.toString().equalsIgnoreCase(inLanguage))
  //       return true;

  //   return false;
  // }

  /**
   * Determine if the monster has the given subtype.
   *
   * @param       inType the type to look for
   *
   * @return      true if the language is there, false if not
   */
  // @SuppressWarnings("unchecked") // casting for Multiple
  // public boolean hasSubtype(String inType)
  // {
  //   for(EnumSelection<MonsterType> value :
  //         (ValueList<EnumSelection<MonsterType>>)m_monsterType.get(1).get())
  //     if(value.toString().equalsIgnoreCase(inType))
  //       return true;

  //   return false;
  // }

  /**
   * Determine if the monster has the given movement mode.
   *
   * @param       inType the movement mode to look for
   *
   * @return      true if the language is there, false if not
   */
  // public boolean hasMovementMode(String inType)
  // {
  //   for(Iterator<Multiple> i = m_speed.iterator(); i.hasNext(); )
  //     if(i.next().get(0).get().toString().equalsIgnoreCase(inType))
  //       return true;

  //   return false;
  // }

  /**
   * Check if the given name is a describing a class skill of the monster.
   *
   * @param       inName the name of the skill to check
   *
   * @return      true if it is a class skill, false if not
   */
  // public boolean isClassSkill(String inName)
  // {
  //   if(inName == null)
  //     return false;

  //   for(Iterator<Multiple> i = m_classSkills.iterator(); i.hasNext(); )
  //     if(i.next().get(0).get().toString().equalsIgnoreCase(inName))
  //       return true;

  //   return false;
  // }

  /**
   * Check whether the given user is the DM for this entry.
   *
   * @param       inUser the user accessing
   *
   * @return      true for DM, false for not
   */
  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().hasAccess(net.ixitxachitls.dma.values.enums.Group.DM);
  }

  /**
   *
   * Check that the monster has the given qualities.
   *
   * @param       inQualities the qualities to check for
   *
   * @return      true if everything is ok, false if not
   */
  // public boolean checkQualities(String ... inQualities)
  // {
  //   boolean result = true;

  //   for(String quality : inQualities)
  //     if(!hasQuality(quality))
  //     {
  //       result = false;
  //       addError(new CheckError("monster.quality",
  //                               "The should have the '" + quality
  //                               + "' quality."));
  //     }

  //   return result;
  // }

  /**
   * Get all the values for all the indexes.
   *
   * @return      a multi map of values per index name
   */
  @Override
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

    values.put(Index.Path.SIZES, m_size.toString());
    values.put(Index.Path.TYPES, m_monsterType.toString());

    for(MonsterSubtype type : m_monsterSubtypes)
      values.put(Index.Path.SUBTYPES, type.toString());

    if(m_hitDice.isPresent())
    {
      values.put(Index.Path.HDS, "" + m_hitDice.get().toString());
      values.put(Index.Path.DICES, "" + m_hitDice.get().toString());
    }

    for(Speed speed : m_speeds)
    {
      values.put(Index.Path.MOVEMENT_MODES, speed.getMode().toString());
      values.put(Index.Path.SPEEDS, speed.getSpeed().toString());
      values.put(Index.Path.MANEUVERABILITIES,
                 speed.getManeuverability().toString());
    }

    values.put(Index.Path.NATURAL_ARMORS, "" + m_naturalArmor);
    values.put(Index.Path.BASE_ATTACKS, "" + m_baseAttack);
    if(m_strength.isPresent())
      values.put(Index.Path.STRENGTHS, m_strength.get().toString());
    if(m_dexterity.isPresent())
      values.put(Index.Path.DEXTERITIES, m_dexterity.get().toString());
    if(m_constitution.isPresent())
      values.put(Index.Path.CONSTITUTIONS, m_constitution.get().toString());
    if(m_intelligence.isPresent())
      values.put(Index.Path.INTELLIGENCES, m_intelligence.get().toString());
    if(m_wisdom.isPresent())
      values.put(Index.Path.WISDOMS, m_wisdom.get().toString());
    if(m_charisma.isPresent())
      values.put(Index.Path.CHARISMAS, m_charisma.get().toString());
    if(m_space.isPresent())
      values.put(Index.Path.SPACES, m_space.get().toString());
    if(m_reach.isPresent())
      values.put(Index.Path.REACHES, m_reach.get().toString());
    values.put(Index.Path.CLIMATES, m_climate.toString());
    values.put(Index.Path.TERRAINS, m_terrain.toString());

    for(Group organization : m_organizations)
      values.put(Index.Path.ORGANIZATIONS,
                 organization.getOrganization().toString());

    if(m_cr.isPresent())
      values.put(Index.Path.CRS, m_cr.get().toString());
    values.put(Index.Path.TREASURES, m_treasure.toString());
    values.put(Index.Path.ALIGNMENTS, m_alignment.toString());
    if(m_levelAdjustment.isPresent())
    values.put(Index.Path.LEVEL_ADJUSTMENTS,
               m_levelAdjustment.get().toString());

    for(LanguageOption language : m_languages)
      values.put(Index.Path.LANGUAGES, language.getLanguage().toString());

    return values;
  }

  /**
   * Check the entry for possible problems.
   *
   * @param       inCampaign the campaign with all the data
   *
   * @return      false if a problem was found, true if not
   */
  // public boolean check(CampaignData inCampaign)
  // {
  //   boolean result = true;

  //   // check some values because of the given type
  //   switch(((Selection)m_monsterType.get(0).get()).getSelected())
  //   {
  //     // Aberration
  //     // - Natural Weapon proficiency
  //     case 0:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       // - Darkvision 60 ft.
  //       result = checkQualities("Darkvision [Range 60 ft]");

  //       break;

  //     // Animal
  //     // - Natural Weapon proficiency (only)
  //     case 1:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       // - Low-light vision
  //       result = checkQualities("Low-Light Vision");

  //       // - Int 1 or 2
  //       if(m_intelligence.get() != 1 && m_intelligence.get() != 2)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.intelligence",
  //                                 "should have 1 or 2 (animal type)"));
  //       }

  //       // - Always neutral alignment
  //       if(((Selection)m_alignment.get(1).get()).getSelected() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.alignment",
  //                                 "should be neutral for animal"));
  //       }

  //       // - No treasure
  //       if(m_treasure.getSelected() != Treasure.NONE)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.treasure",
  //                                 "should not have treasure as animal"));
  //       }

  //       break;

  //     // Construct
  //     // - Proficient with no armor
  //     case 2:

  //       // - d10 hit dice
  //       if(m_hitDice.getDice() != 10)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d10"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       // - No Con
  //       if(m_constitution.get() >= 0)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.constitution",
  //                                 "should not have a constitution score "
  //                                 + "(construct type)"));
  //       }

  //       // - Darkvision 60 ft.
  //       // - Low-Light Vision
  //       // - Immunity mind affecting effects
  //       // - Immunity poison
  //       // - Immunity sleep effects
  //       // - Immunity paralysis
  //       // - Immunity stunning
  //       // - Immunity disease
  //       // - Immunity death effects
  //       // - Immunity necromancy effects
  //       // - Cannot heal damage on its own
  //       // - Not subject to critical hits
  //       // - Not subject to nonleathal damage
  //       // - Not subject to ability damage
  //       // - Not subject to ability drain
  //       // - Not subject to exhaustion
  //       // - Not subject to energy drain
  //       // - Immunity to any effect requiring Fort save
  //       // - Not subject to death from massive damage
  //       // - Cannot be raised
  //       // - Cannot be resurrected
  //       // - does not eat
  //       // - does not sleep
  //       // - does not breathe
  //       result = checkQualities("Darkvision [Range 60 ft]",
  //                               "Low-Light Vision",
  //                               "Immunity to Mind Affecting Effects",
  //                               "Immunity to Poison",
  //                               "Immunity to Sleep Effects",
  //                               "Immunity to Paralysis",
  //                               "Immunity to Stunning",
  //                               "Immunity to Disease",
  //                               "Immunity to Death Effects",
  //                               "Immunity to Necromancy Effects",
  //                               "No Natural Healing",
  //                               "Not Subject to Critical Hits",
  //                               "Not Subject to Nonlethal Damage",
  //                               "Not Subject to Ability Damage",
  //                               "Not Subject to Ability Drain",
  //                               "Not Subject to Exhaustion",
  //                               "Not Subject to Energy Drain",
  //                               "Immunity to Fortitude Save Effects",
  //                               "Not Subject to Death from Massive Damage",
  //                               "Cannot be Raised",
  //                               "Cannot be Resurrected",
  //                               "Does not Eat",
  //                               "Does not Sleep",
  //                               "Does not Breathe");

  //       break;

  //     // Dragon
  //     // - Proficient with no armor
  //     case 3:

  //       // - d12 hit dice
  //       if(m_hitDice.getDice() != 12)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d12"));
  //       }

  //       // - attack bonus total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber())
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber())));
  //       }

  //       // - Darkvision 60 ft.
  //       // - Low-light vision
  //       // - Immunity to magic sleep
  //       // - Immunity to paralysis
  //       result = checkQualities("Darkvision [Range 60 ft]",
  //                               "Low-Light Vision",
  //                               "Immunity to Magical Sleep",
  //                               "Immunity to Paralysis");
  //       break;

  //     // Elemental
  //     case 4:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       // - Darkvision 60 ft.
  //       // - Immunity to poison
  //       // - Immunity to sleep effects
  //       // - Immunity to paralysis
  //       // - Immunity to stunning
  //       // - Not subject to critical hits
  //       // - Not subject to flanking
  //       // - no soul
  //       // - does not eat, sleep, breathe
  //       result = checkQualities("Darkvision [Range 60 ft]",
  //                               "Immunity to Poison",
  //                               "Immunity to Sleep Effects",
  //                               "Immunity to Paralysis",
  //                               "Immunity to Stunning",
  //                               "Not Subject to Critical Hits",
  //                               "Not Subject to Flanking",
  //                               "No Soul",
  //                               "Does not Eat",
  //                               "Does not Sleep",
  //                               "Does not Breathe");

  //       break;

  //     // Fey
  //     // - Simple weapon proficiency
  //     case 5:

  //       // - d6 hit dice
  //       if(m_hitDice.getDice() != 6)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d6"));
  //       }

  //       // - attack bonus 1/2 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() / 2)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() / 2)));
  //       }

  //       // - Low-light vision
  //       if(!hasQuality("Low-Light Vision"))
  //       {
  //         result = false;
  //         addError(new CheckError("monster.quality",
  //                                 "The should have the 'Low-Light Vision' "
  //                                 + "quality."));
  //       }

  //       break;

  //     // Giant
  //     // - Simple weapon proficiency
  //     // - Martial weapon proficiency
  //     // - Natural weapon proficiency
  //     // - Shield proficiency of any armor proficiency
  //     case 6:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       // - Low-light vision
  //       if(!hasQuality("Low-Light Vision"))
  //       {
  //         result = false;
  //         addError(new CheckError("monster.quality",
  //                                 "The should have the 'Low-Light Vision' "
  //                                 + "quality."));
  //       }

  //       break;

  //     // Humanoid
  //     // - treated as Warriors if 1 Hit Dice
  //     // - Simple weapon proficiency
  //     // - Shield proficiency of any armor proficiency
  //     case 7:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       break;

  //     // Magical Beast
  //     // - natural weapon proficiency (only)
  //     // - No armor proficiency
  //     case 8:

  //       // - d10 hit dice
  //       if(m_hitDice.getDice() != 10)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d10"));
  //       }

  //       // - attack bonus total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber())
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber())));
  //       }

  //       // - Darkvision 60 ft.
  //       if(!hasQuality("Darkvision [Range 60 ft]"))
  //       {
  //         result = false;
  //         addError(new CheckError("monster.quality",
  //                                 "The should have the 'Darkvision 60 Ft' "
  //                                 + "quality."));
  //       }

  //       // - Int > 2
  //       if(m_intelligence.get() <= 2)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.intelligence",
  //                                 "should be more than 2 (magical beast "
  //                                 + "type)"));
  //       }

  //       break;

  //     // Monstrous Humanoid
  //     // - Simple proficiency
  //     case 9:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber())
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber())));
  //       }

  //       // - Darkvision 60 ft.
  //       if(!hasQuality("Darkvision [Range 60 ft]"))
  //       {
  //         result = false;
  //         addError(new CheckError("monster.quality",
  //                                 "The should have the 'Darkvision 60 Ft' "
  //                                 + "quality."));
  //       }

  //       break;

  //     // Ooze
  //     // - Natural weapon proficiency (only)
  //     // - No armor proficiency
  //     case 10:

  //       // - d10 hit dice
  //       if(m_hitDice.getDice() != 10)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d10"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       // - Mindless
  //       // - Blind
  //       // - Immunity poison
  //       // - Immunity sleep effects
  //       // - Immunity paralysis
  //       // - Immunity polymorph
  //       // - Immunity stunning
  //       // - Not subject to critical hits
  //       // - Not subject to flanking
  //       // - eats, breathes, does not sleep
  //       result = checkQualities("Mindless",
  //                               "Blind",
  //                               "Immunity to Poison",
  //                               "Immunity to Sleep Effects",
  //                               "Immunity to Paralysis",
  //                               "Immunity to Polymorph",
  //                               "Immunity to Stunning",
  //                               "Not Subject to Critical Hits",
  //                               "Not Subject to Flanking",
  //                               "Does not Sleep");

  //       break;

  //     // Outsider
  //     // - Simple weapon proficiency
  //     // - Martial weapon proficiency
  //     // - Shield proficiency if any armor proficiency
  //     case 11:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber())
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber())));
  //       }

  //       // - Darkvision 60 ft.
  //       // - No soul
  //       // - breathes, does not eat or sleep (unless native)
  //      result = checkQualities("Darkvision [Range 60 ft]",
  //                              "No Soul",
  //                              "Does not Eat",
  //                              "Does not Sleep");

  //       break;

  //     // Plant
  //     // - Natural weapon proficiency (only)
  //     // - No armor proficiency
  //     case 12:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       // - Low-light vision
  //       // - Immunity mind affecting effects
  //       // - Immunity poison
  //       // - Immunity sleep effects
  //       // - Immunity paralysis
  //       // - Immunity polymorph
  //       // - Immunity stunning
  //       // - Not subject to critical hits
  //       // - eats, breathes, does not sleep
  //       result = checkQualities("Low-Light Vision",
  //                               "Immunity to Mind Affecting Effects",
  //                               "Immunity to Poison",
  //                               "Immunity to Sleep Effects",
  //                               "Immunity to Paralysis",
  //                               "Immunity to Polymorph",
  //                               "Immunity to Stunning",
  //                               "Not Subject to Critical Hits",
  //                               "Does not Sleep");

  //       break;

  //     // Undead
  //     // - Natural weapon proficiency
  //     // - Simple weapon proficiency
  //     // - Shield proficiency if any armor proficiency
  //     case 13:

  //       // - d12 hit dice
  //       if(m_hitDice.getDice() != 12)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d12"));
  //       }

  //       // - attack bonus 1/2 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() / 2)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() / 2)));
  //       }

  //       // - Darkvision 60 ft.
  //       // - Immunity mind affecting effects
  //       // - Immunity poison
  //       // - Immunity sleep effects
  //       // - Immunity paralysis
  //       // - Immunity stunning
  //       // - Immunity disease
  //       // - Immunity death effects
  //       // - Not subject to critical hits
  //       // - Not subject to nonlethal damage
  //       // - Not subject to ability drain
  //       // - Not subject to energy drain
  //       // - Immunity to damage to Str, Dex, Con
  //       // - Immunity to fatigue effects
  //       // - Immunity to exhaustion effects
  //       // - Cannot heal on its own
  //       // - Immunity to effects requiring a Fort save
  //       // - Uses Cha for Concentration checks
  //       // - Not affected by raise dead, reincarnate
  //       // - does no eat, breath, sleep
  //       result = checkQualities("Darkvision [Range 60 ft]",
  //                               "Immunity to Mind Affecting Effects",
  //                               "Immunity to Poison",
  //                               "Immunity to Sleep Effects",
  //                               "Immunity to Paralysis",
  //                               "Immunity to Stunning",
  //                               "Immunity to Disease",
  //                               "Immunity to Death Effects",
  //                               "Not Subject to Critical Hits",
  //                               "Not Subject to Nonlethal Damage",
  //                               "Not Subject to Ability Drain",
  //                               "Not Subject to Energy Drain",
  //                               "Immunity to Strength Damage",
  //                               "Immunity to Dexterity Damage",
  //                               "Immunity to Constitution Damage",
  //                               "Immunity to Fatigue Effects",
  //                               "Immunity to Exhaustion Effects",
  //                               "No Natural Healing",
  //                               "Immunity to Fortitude Save Effects",
  //                               "Uses Charisma for Concentration checks",
  //                               "Not Affected by Raise Dead",
  //                               "Not Affected by Reincarnate",
  //                               "Does not Eat",
  //                               "Does not Breath",
  //                               "Does not Sleep");

  //       // - No Con
  //       if(m_constitution.get() >= 0)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.constitution",
  //                                 "should not have a constitution score "
  //                                 + "(undead type)"));
  //       }

  //       break;

  //     // Vermin
  //     // - Natural weapon proficiency (only)
  //     // - Shield proficiency if any armor proficiency
  //     case 14:

  //       // - d8 hit dice
  //       if(m_hitDice.getDice() != 8)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.HD.type",
  //                                 "The hit dice does not match the monster "
  //                                 + "type, should be d8"));
  //       }

  //       // - attack bonus 3/4 total Hit Dice
  //       if(m_attack.get() != m_hitDice.getNumber() * 3 / 4)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.attack",
  //                                 "The base attack bonus should be "
  //                                 + (m_hitDice.getNumber() * 3 / 4)));
  //       }

  //       // - Darkvision 60 ft.
  //       // - Mindless
  //       // - Immunity mind affecting effects
  //       result = checkQualities("Darkvision [Range 60 ft]",
  //                               "Mindless",
  //                               "Immunity to Mind Affecting Effects");

  //       // - No Int
  //       if(m_intelligence.get() >= 0)
  //       {
  //         result = false;
  //         addError(new CheckError("monster.intelligence",
  //                                 "should not have an intelligence score "
  //                                 + "(vermin type)"));
  //       }

  //       break;

  //     default:

  //       addError(new CheckError("monster.type", "unknown type encountered"));
  //   }

  //   // check some values because of the given subtype
  //   if(hasSubtype("air"))
  //   {
  //     // - fly speed
  //     if(!hasMovementMode("fly"))
  //     {
  //       result = false;
  //       addError(new CheckError("monster.movement.mode.subtype",
  //                               "The monster does not have the fly speed "
  //                               + "required by its subtype"));
  //     }
  //   }

  //   if(hasSubtype("aquatic"))
  //   {
  //     // - swimm speed
  //     if(!hasMovementMode("swim"))
  //     {
  //       result = false;
  //       addError(new CheckError("monster.movement.mode.subtype",
  //                               "The monster does not have the swim speed "
  //                               + "required by its subtype"));
  //     }

  //     // - breathe underwater
  //     if(!hasQuality("breathe water"))
  //     {
  //       result = false;
  //       addError(new CheckError("monster.quality",
  //                               "aquatic subtype should have "
  //                               + "'breathe water'"));
  //     }

  //     // - cannot breathe air unless the amphibious special quality
  //     if(!hasQuality("amphibious") && !hasQuality("breathe no air"))
  //     {
  //       result = false;
  //       addError(new CheckError("monster.quality",
  //                               "aquatic subtype should have "
  //                               + "'breathe no air' (unless amphibious)"));
  //     }
  //   }

  //   if(hasSubtype("archon"))
  //   {
  //     // - Darkvision 60 ft.
  //     // - Low-light vision
  //     // - Aura of Menace
  //     // - Immunity electricity
  //     // - Immunity petrification
  //     // - +4 racial vs. poison
  //     // - Magic Circle Against Evil
  //     // - Teleport
  //     // - Tongues
  //     result = checkQualities("Darkvision [Range 60 ft]",
  //                             "Aura of Menace",
  //                             "Immunity to Electricity",
  //                             "Immunity to Petrification",
  //                             "Poison [Value +4 racial]",
  //                             "Magic Circle Against Evil",
  //                             "Teleport",
  //                             "Tongues");
  //   }

  //   if(hasSubtype("Augmented"))
  //   {
  //     // nothing to do here
  //   }

  //   if(hasSubtype("Baatezu"))
  //   {
  //     // - Immunity fire
  //     // - Immunity poison
  //     // - Resistance acid 10
  //     // - Resistance cold 10
  //     // - See in Darkness
  //     // - Summon
  //     // - Telepathy
  //     result = checkQualities("Immunity to Fire",
  //                             "Immunity to Poison",
  //                             "Resistance Acid [Value 10]",
  //                             "Resistance Cold [Value 10]",
  //                             "See in Darkness",
  //                             "Summon",
  //                             "Telepathy");
  //   }

  //   if(hasSubtype("Chaotic"))
  //   {
  //     // - affected by anything affecting chaotic alignment (even if
  //     //   different alignment)
  //     // - weapons treated as chaotic-aligned
  //     result = checkQualities("Affected as Chaotic",
  //                             "Weapons Chaotic");
  //   }

  //   if(hasSubtype("Cold"))
  //   {
  //     // - Immunity cold
  //     // - Vulnerability fire
  //     result = checkQualities("Immunity to Cold",
  //                             "Vulnerability Fire");
  //   }

  //   if(hasSubtype("Earth"))
  //   {
  //     // - burrow speed
  //     if(!hasMovementMode("burrow"))
  //     {
  //       result = false;
  //       addError(new CheckError("monster.movement.mode.subtype",
  //                               "The monster does not have the burrow "
  //                               + "speed required by its subtype"));
  //     }
  //   }

  //   if(hasSubtype("Eladrin"))
  //   {
  //     // - Darkvision 60 ft
  //     // - Low-light vision
  //     // - Immunity electricity
  //     // - Immunity petrification
  //     // - Resistance cold 10
  //     // - Resistance fire 10
  //     // - Tongues
  //     result = checkQualities("Darkvision [Range 60 ft]",
  //                             "Low-Light Vision",
  //                             "Immunity to Electricity",
  //                             "Immunity to Petrification",
  //                             "Resistance Cold [Value 10]",
  //                             "Resistance Fire [Value 10]",
  //                             "Tongues");
  //   }

  //   if(hasSubtype("Evil"))
  //   {
  //     // - affected by anything affecting evil alignment (even if
  //     //   different alignment)
  //     // - weapons treated as evil-aligned
  //     result = checkQualities("Affected as Evil",
  //                             "Weapons Evil");
  //   }

  //   if(hasSubtype("Extraplanar"))
  //   {
  //     // - can vary depending on where the monster is
  //   }

  //   if(hasSubtype("Fire"))
  //   {
  //     // - Immunity fire
  //     // - Vulnerability cold
  //     result = checkQualities("Immunity to Fire",
  //                             "Vulnerability Cold");
  //   }

  //   if(hasSubtype("Goblinoid"))
  //   {
  //     // - Goblin language
  //     if(!hasLanguage("Goblin"))
  //     {
  //       result = false;
  //       addError(new CheckError("monster.language",
  //                               "Goblinoid should have the Goblin "
  //                               + "language"));
  //     }
  //   }

  //   if(hasSubtype("Good"))
  //   {
  //     // - affected by anything affecting good alignment (even if
  //     //   different alignment)
  //     // - weapons treated as good-aligned
  //     result = checkQualities("Affected as Good",
  //                             "Weapons Good");
  //   }

  //   if(hasSubtype("Guardinal"))
  //   {
  //     // - Darkvision 60 ft
  //     // - Low-light vision
  //     // - Immunity electricity
  //     // - Immunity petrification
  //     // - Resistance cold 10
  //     // - Resistance fire 10
  //     // - Lay on Hands
  //     // - +4 racial vs. poison
  //     // - Speak with Animals
  //     result = checkQualities("Darkvision [Range 60 ft]",
  //                             "Low-Light Vision",
  //                             "Immunity to Electricity",
  //                             "Immunity to Petrification",
  //                             "Resistance Cold [Value 10]",
  //                             "Resistance Fire [Value 10]",
  //                             "Lay on Hands",
  //                             "Poison [Value +4 racial]",
  //                             "Speak with Animals");
  //   }

  //   if(hasSubtype("Incorporeal"))
  //   {
  //     // - Harmed only by incorporeal creatures, magic weapons, spells,
  //     //   spell-like abilities, supernatural abilities
  //     // - 50% chance to ignore damage from corporeal source (except
  //     //   positive energy, negative energy, force effects, ghost touch
  //     //   weapons)
  //     // - Can enter and pass through objects (not larger than self)
  //     // - Can sense creatures within 1 square (total concealment)
  //     // - Cannot pass through force effects
  //     // - Attacks ignore natural armor, armor, shields
  //     // - Operate in water as in air
  //     // - Cannot fall or take falling damage
  //     // - Cannot make trip attacks
  //     // - Cannot make grapple attacks
  //     // - Cannot be tripped
  //     // - Cannot be grappled
  //     // - Cannot manipulate physical objects
  //     // - Cannot be manipulated by physical objects
  //     // - Moves in complete silence
  //     // - No Strength (uses Dex for melee attacks)
  //     // - Scent ineffective
  //     // - Blindsight ineffective
  //     // - Can move at full speed even if cannot see
  //     result = checkQualities("Incorporeal");

  //     // - No natural armor
  //     if(m_natural.get() > 0)
  //     {
  //       result = false;
  //       addError(new CheckError("monster.armor",
  //                               "incorporeal monster don't have natural "
  //                               + "armor"));
  //     }
  //   }

  //   if(hasSubtype("Lawful"))
  //   {
  //     // - affected by anything affecting lawful alignment (even if
  //     //   different alignment)
  //     // - weapons treated as lawful-aligned
  //     result = checkQualities("Affected as Lawful",
  //                             "Weapons Lawful");
  //   }

  //   if(hasSubtype("Native"))
  //   {
  //       // - Native to Material Plane
  //       // - Can be raised, reincarnated or resurrected
  //       // - need to eat and sleep
  //   }

  //   if(hasSubtype("Reptilian"))
  //   {
  //     // - scaly
  //     // - cold-blooded
  //     result = checkQualities("Scaly",
  //                             "Cold-Blooded");
  //   }

  //   if(hasSubtype("Shapechanger"))
  //   {
  //     // - can assume one ore more alternate forms
  //     // - Natural weapon proficiency
  //     // - Simple weapon proficiency
  //     // - Shield proficiency if any armor proficiency
  //     result = checkQualities("Shapechanger");
  //   }

  //   if(hasSubtype("Swarm"))
  //   {
  //     // - collection of fine, diminutive, tiny creatures
  //     // - acts as single creature
  //     // - Can move through enemy squares
  //     // - Can move through cracks large enough for individual creatures
  //     // - 300 nonflying tiny creatures, or 1000 flying tiny creatures, or
  //     //   1500 nonflying diminutive creatures, or 5000 diminutive flying
  //     //   creatures, or 10000 fine creatures
  //     // - Not subject to critical hits
  //     // - Not subject to flanking
  //     // - Half damage from slashing and piercing weapons if tiny
  //     // - Immune to weapon damage if fine or diminutive
  //     // - Reducing it to 0 or less hit points causes it to break up
  //     // - Not subject to staggering
  //     // - Not subject to dying through damage
  //     // - Cannot be tripped
  //     // - Cannot be grappled
  //     // - Cannot be bull-rushed
  //     // - Cannot grapple
  //     // - Immunity to spells targeting a number of creatures
  //     // - Vulnerability area effects
  //     // - Diminutive and Fine swarms susceptible to high winds
  //     // - Deals automatic damage
  //     // - Does not threaten any squares
  //     // - Distraction

  //     result = checkQualities("Swarm");
  //   }

  //   if(hasSubtype("Tanar'ri"))
  //   {
  //     // - Immunity electricity
  //     // - Immunity poison
  //     // - Resistance acid 10
  //     // - Resistance cold 10
  //     // - Resistance fire 10
  //     // - Summon
  //     // - Telepathy
  //     result = checkQualities("Immunity Electricity",
  //                             "Immunity Poison",
  //                             "Resistance Acid [Value 10]",
  //                             "Resistance Cold [Value 10]",
  //                             "Resistance Fire [Value 10]",
  //                             "Summon",
  //                             "Telepathy");
  //   }

  //   if(hasSubtype("Water"))
  //   {
  //     // - need not make swim checks
  //     // - can breath water
  //     // - can breath air
  //     if(!hasMovementMode("swim"))
  //     {
  //       result = false;
  //       addError(new CheckError("monster.movement.mode.subtype",
  //                               "The monster does not have the swim speed "
  //                               + "required by its subtype"));
  //     }

  //     result = checkQualities("Does No Need Swim Checks",
  //                             "Breathe Water");
  //   }

  //   // check the number of skill points defined
  //   long total = 0;
  //   for(Iterator<Multiple> i = m_classSkills.iterator(); i.hasNext(); )
  //     total += ((Number)i.next().get(2).get()).get();

  //   if(total > skillPoints())
  //   {
  //     result = false;
  //     addError(new CheckError("monster.skill.points",
  //                           "uses " + total + " skill points, but only has "
  //                             + skillPoints()));
  //   }
  //   else
  //     if(total < skillPoints())
  //     {
  //       result = false;
  //       addError(new CheckError("monster.skill.points",
  //                               "uses only " + total
  //                             + " skill points from its " + skillPoints()));
  //     }

  //   return super.check() & result;
  // }

  /**
   * Complete the entry and make sure that all values are filled.
   */
  // @SuppressWarnings(value = "unchecked")
  // public void complete()
  // {
  //   super.complete();

  //   BaseItem.Size size = getSize();

    //----- space ----------------------------------------------------------

    // // setup the space
    // if(size != null)
    //   m_space.setFeet(null, size.space(), null);

    // // swarm
    // if(hasSubtype("swarm"))
    //   m_space.setFeet(null, new Rational(10), null);

    //......................................................................
    //----- reach ----------------------------------------------------------

    // // setup the reach
    // if(size != null)
    //   m_reach.setFeet(null, new Rational
    //                 (getSize().reach(((EnumSelection<BaseItem.SizeModifier>)
    //                                     m_size.get(1).get()).getSelected())),
    //                   null);

    // // swarm
    // if(hasSubtype("swarm"))
    //   m_space.setFeet(null, new Rational(0), null);

    //......................................................................
    //----- base feats -----------------------------------------------------

    // for(Text text : m_feats)
    // {
    //   // get the base feat with this name
    //   BaseFeat entry =
    //     BaseCampaign.GLOBAL.getBaseEntry(text.get(), BaseFeat.TYPE);

    //   if(entry == null)
    //   {
    //     Log.warning("could not obtain feat '" + text.get()
    //                 + "' from campaign");

    //     continue;
    //   }

    //   m_featEntries.add(entry);
    // }

    //......................................................................
  // }

  /**
   * Modify the given value with information from the current attachment.
   *
   * @param       inType    the type of value to modify
   * @param       inValue   the value to modify, return in this object
   * @param       inDynamic a flag denoting if dynamic values are requested
   *
   * @return      the newly computed value (or null if no value to use)
   */
//   public Modifier modifyValue(PropertyKey inType, Value inValue,
//                               boolean inDynamic)
//   {
//     if(inValue == null || !inValue.isDefined())
//       return null;

//     // temporary correction
//     if(inType == HIT_DICE)
//       return new Modifier(Modifier.Type.ADD, 40);

//     return super.modifyValue(inType, inValue, inDynamic);
//   }

  /**
   * Make a grouping of the given HPs.
   *
   * @ param       inHP the input value
   *
   * @ return      the grouped value (as a String)
   */
  // protected static String groupHP(long inHP)
  // {
  //   if(inHP <= 0)
  //     return "0000";

  //   if(inHP <= 5)
  //     return "0005";

  //   if(inHP <= 10)
  //     return "0010";

  //   if(inHP <= 15)
  //     return "0015";

  //   if(inHP <= 20)
  //     return "0020";

  //   if(inHP <= 25)
  //     return "0025";

  //   if(inHP <= 30)
  //     return "0030";

  //   if(inHP <= 40)
  //     return "0050";

  //   if(inHP <= 50)
  //     return "0050";

  //   if(inHP <= 100)
  //     return "0100";

  //   if(inHP <= 250)
  //     return "0250";

  //   if(inHP <= 500)
  //     return "0500";

  //   if(inHP <= 1000)
  //     return "1000";

  //   return "Infinite";
  // }

  //........................................................................

  @Override
  public void setValues(Values inValues)
  {
    super.setValues(inValues);

    m_size = inValues.use("size", m_size, Size.PARSER);
    m_sizeModifier = inValues.use("size_modifier", m_sizeModifier,
                                  SizeModifier.PARSER);
    m_monsterType = inValues.use("monster_type", m_monsterType,
                                 MonsterType.PARSER);
    m_monsterSubtypes = inValues.use("monster_subtype", m_monsterSubtypes,
                                     MonsterSubtype.PARSER);
    m_hitDice = inValues.use("hit_dice", m_hitDice, Dice.PARSER);
    m_speeds = inValues.use("speed", m_speeds, Speed.PARSER,
                            "mode", "speed", "maneuverability");
    m_naturalArmor = inValues.use("natural_armor", m_naturalArmor,
                                  Modifier.PARSER);
    m_baseAttack = inValues.use("attack", m_baseAttack,
                                Value.INTEGER_PARSER);
    m_strength = inValues.use("strength", m_strength, Value.INTEGER_PARSER);
    m_dexterity = inValues.use("dexterity", m_dexterity,
                               Value.INTEGER_PARSER);
    m_constitution = inValues.use("constitution", m_constitution,
                                  Value.INTEGER_PARSER);
    m_intelligence = inValues.use("intelligence", m_intelligence,
                                  Value.INTEGER_PARSER);
    m_wisdom = inValues.use("wisdom", m_wisdom, Value.INTEGER_PARSER);
    m_charisma = inValues.use("charisma", m_charisma, Value.INTEGER_PARSER);
    m_reflexSave = inValues.use("reflex_save", m_reflexSave,
                                Value.INTEGER_PARSER);
    m_willSave = inValues.use("will_save", m_willSave, Value.INTEGER_PARSER);
    m_fortitudeSave = inValues.use("fortitude_save", m_fortitudeSave,
                                   Value.INTEGER_PARSER);
    m_primaryAttacks = inValues.use("primary_attack", m_primaryAttacks,
                                    Attack.PARSER,
                                    "number", "mode", "style", "damage");
    m_secondaryAttacks = inValues.use("secondary_attack", m_primaryAttacks,
                                      Attack.PARSER,
                                      "number", "mode", "style", "damage");
    m_space = inValues.use("space", m_space, Distance.PARSER);
    m_reach = inValues.use("reach", m_reach, Distance.PARSER);
    m_specialAttacks = inValues.use("special_attack", m_specialAttacks);
    m_specialQualities = inValues.use("special_quality", m_specialQualities);
    m_classSkills = inValues.use("class_skill", m_classSkills);
    m_feats = inValues.use("feats", m_feats);
    m_terrain = inValues.use("terrain", m_terrain, Terrain.PARSER);
    m_climate = inValues.use("climate", m_climate, Climate.PARSER);
    m_cr = inValues.use("cr", m_cr, Rational.PARSER);
    m_organizations = inValues.use("organization",
                                   m_organizations, Group.PARSER,
                                   "organization", "number", "plus");
    m_treasure = inValues.use("treasure", m_treasure, Treasure.PARSER);
    m_alignment = inValues.use("alignment", m_alignment, Alignment.PARSER);
    m_alignmentStatus = inValues.use("alignment_status", m_alignmentStatus,
                                     AlignmentStatus.PARSER);
    m_advancements = inValues.use("advancement", m_advancements,
                                  Advancement.PARSER,
                                  "range", "size");
    m_levelAdjustment = inValues.use("level_adjustment", m_levelAdjustment,
                                     Value.INTEGER_PARSER);
    m_languages = inValues.use("language", m_languages, LanguageOption.PARSER,
                               "language", "modifier");
    m_encounter = inValues.use("encounter", m_encounter);
    m_combat = inValues.use("combat", m_combat);
    m_tactics = inValues.use("tactics", m_tactics);
    m_character = inValues.use("character", m_character);
    m_reproduction = inValues.use("reproduction", m_reproduction);
    m_possessions = inValues.use("possessions", m_possessions);
    m_goodSaves = inValues.use("good_saves", m_goodSaves, Save.PARSER);
    m_proficiencies = inValues.use("proficiency", m_proficiencies);
    m_quadruped = inValues.use("quadruped", m_quadruped, Value.BOOLEAN_PARSER);
  }

  @Override
  public Message toProto()
  {
    BaseMonsterProto.Builder builder = BaseMonsterProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    if(m_size != Size.UNKNOWN)
      builder.setSize(m_size.toProto());
    if(m_sizeModifier != SizeModifier.UNKNOWN)
      builder.setSizeModifier(m_sizeModifier.toProto());

    if(m_monsterType != MonsterType.UNKNOWN)
      builder.setType(m_monsterType.toProto());
    for(MonsterSubtype subtype : m_monsterSubtypes)
      builder.addSubtype(subtype.toProto());

    if(m_hitDice.isPresent())
      builder.setHitDice(m_hitDice.get().toProto());

    for(Speed speed : m_speeds)
      builder.addSpeed(speed.toProto());

    if(m_naturalArmor.isPresent())
      builder.setNaturalArmor(m_naturalArmor.get().toProto());

    if(m_baseAttack.isPresent())
    builder.setBaseAttack(m_baseAttack.get());

    if(m_strength.isPresent())
      builder.setStrength(m_strength.get());

    if(m_dexterity.isPresent())
      builder.setDexterity(m_dexterity.get());

    if(m_constitution.isPresent())
      builder.setConstitution(m_constitution.get());

    if(m_wisdom.isPresent())
      builder.setWisdom(m_wisdom.get());

    if(m_intelligence.isPresent())
      builder.setIntelligence(m_intelligence.get());

    if(m_charisma.isPresent())
      builder.setCharisma(m_charisma.get());

    if(m_fortitudeSave.isPresent())
      builder.setFortitudeSave(m_fortitudeSave.get());

    if(m_willSave.isPresent())
      builder.setWillSave(m_willSave.get());

    if(m_reflexSave.isPresent())
      builder.setReflexSave(m_reflexSave.get());

    for(Attack attack : m_primaryAttacks)
    {
      BaseMonsterProto.Attack.Builder attackBuilder =
        BaseMonsterProto.Attack.newBuilder();

      attackBuilder.setAttacks(attack.getNumber().toProto());
      attackBuilder.setMode(attack.getMode().toProto());
      attackBuilder.setStyle(attack.getStyle().toProto());
      attackBuilder.setDamage(attack.getDamage().toProto());

      builder.addPrimaryAttack(attackBuilder.build());
    }

    for(Attack attack : m_secondaryAttacks)
    {
      BaseMonsterProto.Attack.Builder attackBuilder =
        BaseMonsterProto.Attack.newBuilder();

      attackBuilder.setAttacks(attack.getNumber().toProto());
      attackBuilder.setMode(attack.getMode().toProto());
      attackBuilder.setStyle(attack.getStyle().toProto());
      attackBuilder.setDamage(attack.getDamage().toProto());

      builder.addSecondaryAttack(attackBuilder.build());
    }

    if(m_space.isPresent())
      builder.setSpace(m_space.get().toProto());

    if(m_reach.isPresent())
      builder.setReach(m_reach.get().toProto());

    for(String special : m_specialAttacks)
      builder.addSpecialAttack
        (BaseMonsterProto.QualityReference.newBuilder()
         .setReference(BaseMonsterProto.Reference.newBuilder()
                       .setName(special)
                       .build())
                       .build());

    for(String special : m_specialQualities)
      builder.addSpecialQuality
        (BaseMonsterProto.QualityReference.newBuilder()
         .setReference(BaseMonsterProto.Reference.newBuilder()
                       .setName(special)
                       .build())
                       .build());

    for(String skill : m_classSkills)
      builder.addClassSkill
        (BaseMonsterProto.SkillReference.newBuilder()
         .setReference(BaseMonsterProto.Reference.newBuilder()
                       .setName(skill)
                       .build())
                       .build());

    for(String feat : m_feats)
      builder.addFeat(BaseMonsterProto.Reference.newBuilder()
                      .setName(feat)
                      .build());

    if(m_climate != Climate.UNKNOWN)
      builder.setClimate(m_climate.toProto());

    if(m_terrain != Terrain.UNKNOWN)
      builder.setTerrain(m_terrain.toProto());

    for(Group organization : m_organizations)
    {
      BaseMonsterProto.Organization.Builder org =
        BaseMonsterProto.Organization.newBuilder();

      org.setType(organization.getOrganization().toProto());
      org.setNumber(organization.getNumber().toProto());
      if(organization.getPlus().isPresent())
        org.addPlus(BaseMonsterProto.Organization.Plus.newBuilder()
                    .setText(organization.getPlus().get())
                    .build());

      builder.addOrganization(org.build());
    }

    if(m_cr.isPresent())
      builder.setChallengeRating(m_cr.get().toProto());

    if(m_treasure != Treasure.UNKNOWN)
      builder.setTreasure(m_treasure.toProto());

    if(m_alignment != Alignment.UNKNOWN)
      builder.setAlignment(m_alignment.toProto());
    if(m_alignmentStatus != AlignmentStatus.UNKNOWN)
      builder.setAlignmentStatus(m_alignmentStatus.toProto());

    for(Advancement advancement : m_advancements)
      builder.addAdvancement
          (BaseMonsterProto.Advancement.newBuilder()
           .setRange(advancement.getRange().toProto())
           .setSize(advancement.getSize().toProto())
           .build());

    if(m_levelAdjustment.isPresent())
      builder.setLevelAdjustment(m_levelAdjustment.get());

    for(LanguageOption language : m_languages)
      builder.addLanguage(BaseMonsterProto.Language.newBuilder()
                          .setName(language.getLanguage().toProto())
                          .setModifier(language.getModifier().toProto())
                          .build());

    if(m_encounter.isPresent())
      builder.setEncounter(m_encounter.get());

    if(m_combat.isPresent())
      builder.setCombat(m_combat.get());

    if(m_tactics.isPresent())
      builder.setTactics(m_tactics.get());

    if(m_character.isPresent())
      builder.setCharacter(m_character.get());

    if(m_reproduction.isPresent())
      builder.setReproduction(m_reproduction.get());

    for(String possession : m_possessions)
      builder.addPossession(BaseMonsterProto.Possession.newBuilder()
                            .setText(possession)
                            .build());

    for(Save save : m_goodSaves)
      builder.addGoodSave(save.toProto());

    for(String proficiency : m_proficiencies)
      builder.addProficiency(proficiency);

    if(m_quadruped)
      builder.setQuadruped(true);

    BaseMonsterProto proto = builder.build();
    return proto;
  }

  /**
   * Set the value from the given proto.
   *
   * @param inProto the proto with the values
   */
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseMonsterProto))
    {
      Log.warning("cannot parse proto " + inProto.getClass());
      return;
    }

    BaseMonsterProto proto = (BaseMonsterProto)inProto;

    if(proto.hasSize())
      m_size = Size.fromProto(proto.getSize());
    if(proto.hasSizeModifier())
      m_sizeModifier = SizeModifier.fromProto(proto.getSizeModifier());
    if(proto.hasType())
      m_monsterType = MonsterType.fromProto(proto.getType());

    for(BaseMonsterProto.Subtype subtype : proto.getSubtypeList())
      m_monsterSubtypes.add(MonsterSubtype.fromProto(subtype));

    if(proto.hasHitDice())
      m_hitDice = Optional.of(Dice.fromProto(proto.getHitDice()));

    for(SpeedProto speed : proto.getSpeedList())
      m_speeds.add(Speed.fromProto(speed));

    if(proto.hasNaturalArmor())
      m_naturalArmor =
        Optional.of(Modifier.fromProto(proto.getNaturalArmor()));

    if(proto.hasBaseAttack())
      m_baseAttack = Optional.of(proto.getBaseAttack());

    if(proto.hasStrength())
      m_strength = Optional.of(proto.getStrength());

    if(proto.hasDexterity())
      m_dexterity = Optional.of(proto.getDexterity());

    if(proto.hasConstitution())
      m_constitution = Optional.of(proto.getConstitution());

    if(proto.hasIntelligence())
      m_intelligence = Optional.of(proto.getIntelligence());

    if(proto.hasWisdom())
      m_wisdom = Optional.of(proto.getWisdom());

    if(proto.hasCharisma())
      m_charisma = Optional.of(proto.getCharisma());

    if(proto.hasFortitudeSave())
      m_fortitudeSave = Optional.of(proto.getFortitudeSave());

    if(proto.hasWillSave())
      m_willSave = Optional.of(proto.getWillSave());

    if(proto.hasReflexSave())
      m_reflexSave = Optional.of(proto.getReflexSave());

    for(BaseMonsterProto.Attack attack : proto.getPrimaryAttackList())
      m_primaryAttacks.add(new Attack(Dice.fromProto(attack.getAttacks()),
                                      AttackMode.fromProto(attack.getMode()),
                                      AttackStyle.fromProto(attack.getStyle()),
                                      Damage.fromProto(attack.getDamage())));

    for(BaseMonsterProto.Attack attack : proto.getSecondaryAttackList())
      m_secondaryAttacks.add
      (new Attack(Dice.fromProto(attack.getAttacks()),
                  AttackMode.fromProto(attack.getMode()),
                  AttackStyle.fromProto(attack.getStyle()),
                  Damage.fromProto(attack.getDamage())));

    if(proto.hasSpace())
      m_space = Optional.of(Distance.fromProto(proto.getSpace()));

    if(proto.hasReach())
      m_reach = Optional.of(Distance.fromProto(proto.getReach()));

    for(BaseMonsterProto.QualityReference reference
      : proto.getSpecialAttackList())
      m_specialAttacks.add(reference.getReference().getName());

    for(BaseMonsterProto.QualityReference reference
      : proto.getSpecialQualityList())
      m_specialQualities.add(reference.getReference().getName());
    for(BaseMonsterProto.SkillReference reference
      : proto.getClassSkillList())
      m_classSkills.add(reference.getReference().getName());

    for(BaseMonsterProto.Reference feat : proto.getFeatList())
      m_feats.add(feat.getName());

    if(proto.hasClimate())
      m_climate = Climate.fromProto(proto.getClimate());

    if(proto.hasTerrain())
      m_terrain = Terrain.fromProto(proto.getTerrain());

    for(BaseMonsterProto.Organization org : proto.getOrganizationList())
    {
      List<String> pluses = new ArrayList<>();
      for(BaseMonsterProto.Organization.Plus plus : org.getPlusList())
        pluses.add(plus.getNumber() + plus.getText());

      m_organizations.add
      (new Group(Organization.fromProto(org.getType()),
                 Dice.fromProto(org.getNumber()),
                 Optional.of(Strings.COMMA_JOINER.join(pluses))));
    }

    if(proto.hasChallengeRating())
      m_cr = Optional.of(Rational.fromProto(proto.getChallengeRating()));

    if(proto.hasTreasure())
      m_treasure = Treasure.fromProto(proto.getTreasure());

    if(proto.hasAlignment())
      m_alignment = Alignment.fromProto(proto.getAlignment());

    if(proto.hasAlignmentStatus())
      m_alignmentStatus = AlignmentStatus.fromProto(proto.getAlignmentStatus());

    for(BaseMonsterProto.Advancement advancement : proto.getAdvancementList())
      m_advancements.add
      (new Advancement(Range.fromProto(advancement.getRange()),
                       Size.fromProto(advancement.getSize())));

    if(proto.hasLevelAdjustment())
      m_levelAdjustment = Optional.of(proto.getLevelAdjustment());

    for(BaseMonsterProto.Language language : proto.getLanguageList())
      m_languages.add
      (new LanguageOption(Language.fromProto(language.getName()),
                          LanguageModifier.fromProto(language.getModifier())));

    if(proto.hasEncounter())
      m_encounter = Optional.of(proto.getEncounter());

    if(proto.hasCombat())
      m_combat = Optional.of(proto.getCombat());

    if(proto.hasTactics())
      m_tactics = Optional.of(proto.getTactics());

    if(proto.hasCharacter())
      m_character = Optional.of(proto.getCharacter());

    if(proto.hasReproduction())
      m_reproduction = Optional.of(proto.getReproduction());

    for(BaseMonsterProto.Possession possession : proto.getPossessionList())
      if(possession.hasName())
        m_possessions.add(possession.getName());
      else
        m_possessions.add(possession.getText());

    for(BaseMonsterProto.Save save : proto.getGoodSaveList())
      m_goodSaves.add(Save.fromProto(save));

    for(String proficiency : proto.getProficiencyList())
      m_proficiencies.add(proficiency);

    if(proto.hasQuadruped())
      m_quadruped = proto.getQuadruped();

    super.fromProto(proto.getBase());
  }

  @Override
  protected Message defaultProto()
  {
    return BaseMonsterProto.getDefaultInstance();
  }

  //---------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {

  }
}
