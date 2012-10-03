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

//------------------------------------------------------------------ imports

package net.ixitxachitls.dma.entries;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.extensions.BaseIncomplete;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.values.Damage;
import net.ixitxachitls.dma.values.Dice;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.Duration;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Group;
import net.ixitxachitls.dma.values.LongFormattedText;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Range;
import net.ixitxachitls.dma.values.Rational;
import net.ixitxachitls.dma.values.Reference;
import net.ixitxachitls.dma.values.Selection;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.Union;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.conditions.Condition;
//import net.ixitxachitls.dma.values.aux.Group;
//import net.ixitxachitls.dma.values.conditions.Condition;
//import net.ixitxachitls.dma.values.formatters.LinkFormatter;
//import net.ixitxachitls.dma.values.formatters.MultipleFormatter;
//import net.ixitxachitls.dma.values.formatters.ValueFormatter;
import net.ixitxachitls.input.ParseReader;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the basic jDMA base monster.
 *
 * @file          BaseMonster.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class BaseMonster extends BaseEntry
{
  //----------------------------------------------------------------- nested

  //----- monster type -----------------------------------------------------

  /** The possible monster types in the game. */
  public enum MonsterType implements EnumSelection.Named
  {
    /** Aberration. */
    ABERRATION("Aberration"),

    /** Animal. */
    ANIMAL("Animal"),

    /** Construct. */
    CONSTRUCT("Construct"),

    /** Dragon. */
    DRAGON("Dragon"),

    /** Elemental. */
    ELEMENTAL("Elemental"),

    /** Fey. */
    FEY("Fey"),

    /** Giant. */
    GIANT("Giant"),

    /** Humanoid. */
    HUMANOID("Humanoid"),

    /** Magical Beast. */
    MAGICAL_BEAST("Magical Beast"),

    /** Monstrous Humanoid. */
    MONSTROUS_HUMANOID("Monstrous Humanoid"),

    /** Ooze. */
    OOZE("Ooze"),

    /** Outsider. */
    OUTSIDER("Outsider"),

    /** Plant. */
    PLANT("Plant"),

    /** Undead. */
    UNDEAD("Undead"),

    /** Vermin. */
    VERMIN("Vermin");

    /** The value's name. */
    private @Nonnull String m_name;

    /** Create the enum value.
     *
     * @param inName the name of the value
     *
     */
    private MonsterType(@Nonnull String inName)
    {
      m_name = constant("monster.type", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }
  }

  //........................................................................
  //----- monster subtype --------------------------------------------------

  /** The possible monster sub types in the game. */
  public enum MonsterSubtype implements EnumSelection.Named
  {
    /** Air. */
    AIR("Air"),

    /** Aquatic. */
    AQUATIC("Aquatic"),

    /** Archon. */
    ARCHON("Archon"),

    /** Augmented. */
    AUGMENTED("Augmented"),

    /** Baatezu. */
    BAATEZU("Baatezu"),

    /** Chaotic. */
    CHAOTIC("Chaotic"),

    /** Cold. */
    COLD("Cold"),

    /** Earth. */
    EARTH("Earth"),

    /** Eladrin. */
    ELADRIN("Eladrin"),

    /** Elf. */
    ELF("Elf"),

    /** Evil. */
    EVIL("Evil"),

    /** Extraplanar. */
    EXTRAPLANAR("Extraplanar"),

    /** Fire. */
    FIRE("Fire"),

    /** Goblinoid. */
    GOBLINOID("Goblinoid"),

    /** Good. */
    GOOD("Good"),

    /** Guardinal. */
    GUARDINAL("Guardinal"),

    /** Human. */
    HUMAN("Human"),

    /** Incorporeal. */
    INCORPOREAL("Incorporeal"),

    /** Lawful. */
    LAWFUL("Lawful"),

    /** Native. */
    NATIVE("Native"),

    /** Orc. */
    ORC("Orc"),

    /** Reptilian. */
    REPTILIAN("Reptilian"),

    /** Shapechanger. */
    SHAPECHANGER("Shapechanger"),

    /** Swarm. */
    SWARM("Swarm"),

    /** Water. */
    WATER("Water");

    /** The value's name. */
    private @Nonnull String m_name;

    /** Create the enum value.
     *
     * @param inName the name of the value
     *
     */
    private MonsterSubtype(@Nonnull String inName)
    {
      m_name = constant("monster.type", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }
  }

  //........................................................................
  //----- movement mode ----------------------------------------------------

  /** The possible movement modes in the game. */
  public enum MovementMode implements EnumSelection.Named
  {
    /** Burrowing movement. */
    BURROW("Burrow"),

    /** Climbing. */
    CLIMB("Climb"),

    /** Flying. */
    FLY("Fly"),

    /** Swimming. */
    SWIM("Swim"),

    /** Running. */
    RUN("");

    /** The value's name. */
    private @Nonnull String m_name;

    /** Create the enum value.
     *
     * @param inName the name of the value
     *
     */
    private MovementMode(@Nonnull String inName)
    {
      m_name = constant("movement.mode", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }
  }

  //........................................................................
  //----- maneuverability --------------------------------------------------

  /** The possible movement modes in the game. */
  public enum Maneuverability implements EnumSelection.Named
  {
    /** Perfect maneuverability. */
    PERFECT("Pefect"),

    /** Good maneuverability. */
    GOOD("Good"),

    /** Average maneuverability. */
    AVERAGE("Average"),

    /** Poor maneuverability. */
    POOR("Poor"),

    /** Clumsy maneuverability. */
    CLUMSY("Clumsy"),

    /** Clumsy maneuverability. */
    NONE("");

    /** The value's name. */
    private @Nonnull String m_name;

    /** Create the enum value.
     *
     * @param inName the name of the value
     *
     */
    private Maneuverability(@Nonnull String inName)
    {
      m_name = constant("maneuverability", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }
  }

  //........................................................................
  //----- climate ----------------------------------------------------------

  /** The possible climates in the game. */
  public enum Climate implements EnumSelection.Named
  {
    /** Warm climate. */
    WARM("Warm"),

    /** Cold climate. */
    COLD("cold"),

    /** Any climate. */
    ANY("Any"),

    /** Temparete climate. */
    TEMPERATE("Temperate");

    /** The value's name. */
    private String m_name;

    /** Create the enum value.
     *
     * @param inName the name of the value
     *
     */
    private Climate(@Nonnull String inName)
    {
      m_name = constant("climate", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }
  }

  //........................................................................
  //----- terrain ----------------------------------------------------------

  /** The possible terrains in the game. */
  public enum Terrain implements EnumSelection.Named
  {
    /** Forest terrain. */
    FOREST("Forest"),

    /** Marsh terrain. */
    MARSH("Marsh"),

    /** Hills terrain. */
    HILLS("Hills"),

    /** Mountain terrain. */
    MOUNTAIN("Mountain"),

    /** Desert terrain. */
    DESERT("Desert"),

    /** Plains terrain. */
    PLAINS("Plains"),

    /** Aquatic terrain. */
    AQUATIC("Aquatic"),

    /** Underground terrain. */
    UNDERGROUND("Underground"),

    /** Infernal Battlefield of Acheron terrain. */
    INFENRAL_BATTLEFIELD_OF_ACHERON("Infernal Battlefield of Acheron"),

    /** Any terrain. */
    ANY("Any");

    /** The value's name. */
    private @Nonnull String m_name;

    /** Create the enum value.
     *
     * @param inName the name of the value
     *
     */
    private Terrain(@Nonnull String inName)
    {
      m_name = constant("terrain", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }
  }

  //........................................................................
  //----- organization -----------------------------------------------------

  /** The possible terrains in the game. */
  public enum Organization implements EnumSelection.Named
  {
    /** Any organization. */
    ANY("Any"),

    /** Band organization. */
    BAND("band"),

    /** Brood organization. */
    BROOD("brood"),

    /** Colony organization. */
    COLONY("colony"),

    /** Flock organization. */
    FLOCK("Flock"),

    /** Gang organization. */
    GANG("Gang"),

    /** Herd organization. */
    HERD("Herd"),

    /** Nest organization. */
    NEST("Nest"),

    /** Pack organization. */
    PACK("Pack"),

    /** Pair organization. */
    PAIR("pair"),

    /** Patrol organization. */
    PATROL("patrol"),

    /** Slaver Brood organization. */
    SLAVER_BROOD("slaver brood"),

    /** Solitary organization. */
    SOLITARY("solitary"),

    /** Squad organization. */
    SQUAD("squad"),

    /** Storm organization. */
    STORM("storm"),

    /** Swarm organization. */
    SWARM("Swarm");

    /** The value's name. */
    private @Nonnull String m_name;

    /** Create the enum value.
     *
     * @param inName the name of the value
     *
     */
    private Organization(@Nonnull String inName)
    {
      m_name = constant("organization", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }
  }

  //........................................................................
  //----- attack style -----------------------------------------------------

  /** The possible attack styles in the game. */
  public enum AttackStyle implements EnumSelection.Named
  {
    /** A melee attack. */
    MELEE("melee"),

    /** A ranged attack. */
    RANGED("ranged");

    /** The value's name. */
    private @Nonnull String m_name;

    /** Create the name.
     *
     * @param inName       the name of the value
     *
     */
    private AttackStyle(@Nonnull String inName)
    {
      m_name = constant("attack.style", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }
  };

  //........................................................................
  //----- attack mode -----------------------------------------------------

  /** The possible attack styles in the game. */
  public enum AttackMode implements EnumSelection.Named
  {
    /** A tentacle attack. */
    TENTACLE("Tentacle"),

    /** A claw attack. */
    CLAW("Claw"),

    /** A bite attack. */
    BITE("bite"),

    /** A fist attack. */
    FIST("Fist"),

    /** A weapon attack. */
    WEAPON("Weapon"),

    /** A touch attack. */
    TOUCH("Touch"),

    /** A slam attack. */
    SLAM("Slam"),

    /** A sting attack. */
    STING("Sting"),

    /** A hoof attack. */
    HOOF("Hoof");

    /** The value's name. */
    private @Nonnull String m_name;

    /** Create the name.
     *
     * @param inName       the name of the value
     *
     */
    private AttackMode(@Nonnull String inName)
    {
      m_name = constant("attack.mode", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }
  };

  //........................................................................
  //----- treasure ---------------------------------------------------------

  /** The possible sizes in the game. */
  public enum Treasure implements EnumSelection.Named
  {
    /** No treasure at all. */
    NONE("none", 0),

    /** Standard treasure. */
    STANDARD("standard", 1),

    /** Double the standard treasure. */
    DOUBLE("double standard", 2),

    /** Triple the standard treasure. */
    TRIPLE("triple standard", 3),

    /** Quadruple the standard treasure. */
    QUADRUPLE("quadruple standard", 4);

    /** The value's name. */
    private @Nonnull String m_name;

    /** The multiplier for treasures. */
    private int m_multiplier;

    /** Create the name.
     *
     * @param inName       the name of the value
     * @param inMultiplier how much treasure we get
     *
     */
    private Treasure(@Nonnull String inName, int inMultiplier)
    {
      m_name      = constant("skill.modifier", inName);
      m_multiplier    = inMultiplier;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /**
     * Convert to a string
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }

    /** Get the multiplier for this treasure type.
     *
     * @return the multiplier to use for computing treasure amounts
     *
     */
    public int multiplier()
    {
      return m_multiplier;
    }
  };

  //........................................................................
  //----- alignment --------------------------------------------------------

  /** The possible sizes in the game. */
  public enum Alignment implements EnumSelection.Named, EnumSelection.Short
  {
    /** Lawful Evil. */
    LE("Lawful Evil", "LE"),

    /** Lawful Neutral. */
    LN("Lawful Neutral", "LN"),

    /** Lawful Good. */
    LG("Lawful Good", "LG"),

    /** Chaotic Evil. */
    CE("Chaotic Evil", "CE"),

    /** Chaotic Neutral. */
    CN("Chaotic Neutral", "CN"),

    /** Chaotic Good. */
    CG("Chaotic Good", "CG"),

    /** Neutral Evil. */
    NE("Neutral Evil", "NE"),

    /** True Neutral. */
    N("Neutral", "N"),

    /** Neutral Good. */
    NG("Neutral Good", "NG"),

    /** Any alignment. */
    ANY("Any", "A");

    /** The value's name. */
    private @Nonnull String m_name;

    /** The value's short name. */
    private @Nonnull String m_short;

    /** Create the name.
     *
     * @param inName      the name of the value
     * @param inShort     the short name of the value
     *
     */
    private Alignment(@Nonnull String inName, @Nonnull String inShort)
    {
      m_name      = constant("alignment",       inName);
      m_short     = constant("alignment.short", inShort);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }

    /** Get the short name of the value.
     *
     * @return the short name of the value
     *
     */
    public @Nonnull String getShort()
    {
      return m_short;
    }
  };

  //........................................................................
  //----- alignment modifier -----------------------------------------------

  /** The possible alignment modifiers in the game. */
  public enum AlignmentStatus implements EnumSelection.Named
  {
    /** Always. */
    ALWAYS("Always"),

    /** Usually. */
    USUALLY("Usually"),

    /** Often. */
    OFTEN("Often");

    /** The value's name. */
    private @Nonnull String m_name;

    /** Create the name.
     *
     * @param inName      the name of the value
     *
     */
    private AlignmentStatus(@Nonnull String inName)
    {
      m_name = constant("alignment.status", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }
  };

  //........................................................................
  //----- language ---------------------------------------------------------

  /** The possible sizes in the game. */
  public enum Language implements EnumSelection.Named
  {
    /** Aboleth. */
    ABOLETH("Aboleth"),

    /** Abyssal. */
    ABYSSAL("Abyssal"),

    /** Aquan. */
    AQUAN("Aquan"),

    /** Celestial. */
    CELESTIAL("Celestial"),

    /** Common. */
    COMMON("Common"),

    /** Draconic. */
    DRACONIC("Draconic"),

    /** Drow Sign Language. */
    DROW_SIGN("Drow Sign"),

    /** Druidic. */
    DRUIDIC("Druidic"),

    /** Dwarven. */
    DWARVEN("Dwarven"),

    /** Elven. */
    ELVEN("Elven"),

    /** Giant. */
    GIANT("Giant"),

    /** Gnome. */
    GNOME("Gnome"),

    /** Goblin. */
    GOBLIN("Goblin"),

    /** Gnoll. */
    GNOLL("Gnoll"),

    /** Halfling. */
    HALFLING("Halfling"),

    /** Ignan. */
    IGNAN("Ignan"),

    /** Infernal. */
    INFERNAL("Infernal"),

    /** Kuo-toa. */
    KUO_TOA("Kuo-toa"),

    /** Orc. */
    ORC("Orc"),

    /** Sylvan. */
    SYLVAN("Sylvan"),

    /** Terran. */
    TERRAN("Terran"),

    /** Undercommon. */
    UNDERCOMMON("Undercommon"),

    /** None. */
    NONE("-");

    /** The value's name. */
    private @Nonnull String m_name;

    /** Create the name.
     *
     * @param inName       the name of the value
     *
     */
    private Language(@Nonnull String inName)
    {
      m_name = constant("language", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }
  };

  //........................................................................
  //----- language modifier ------------------------------------------------

  /** The possible sizes in the game. */
  public enum LanguageModifier implements EnumSelection.Named
  {
    /** Automatic. */
    AUTOMATIC("Automatic"),

    /** Bonus. */
    BONUS("Bonus"),

    /** Some. */
    SOME("Some"),

    /** Understand. */
    UNDERSTAND("Understand");

    /** The value's name. */
    private @Nonnull String m_name;

    /** Create the name.
     *
     * @param inName       the name of the value
     *
     */
    private LanguageModifier(@Nonnull String inName)
    {
      m_name = constant("language.modifier", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }
  };

  //........................................................................
  //----- save ------------------------------------------------------------

  /** The possible sizes in the game. */
  public enum Save implements EnumSelection.Named, EnumSelection.Short
  {
    /** Fortitude. */
    FORTITUDE("Fortitude", "For"),

    /** Reflex. */
    REFLEX("Reflex", "Ref"),

    /** Wisdom. */
    WISDOM("Widsom", "Wis");

    /** The value's name. */
    private @Nonnull String m_name;

    /** The value's short name. */
    private @Nonnull String m_short;

    /** Create the name.
     *
     * @param inName       the name of the value
     * @param inShort      the short name of the value
     *
     */
    private Save(@Nonnull String inName, @Nonnull String inShort)
    {
      m_name = constant("save.name", inName);
      m_short = constant("save.short", inShort);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the short name of the value
     *
     */
    public @Nonnull String getShort()
    {
      return m_short;
    }

    /** Get the save as string.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }
  };

  //........................................................................
  //----- ability ---------------------------------------------------------

  /** The possible sizes in the game. */
  public enum Ability implements EnumSelection.Named, EnumSelection.Short
  {
    /** Strength. */
    STRENGTH("Strength", "Str"),

    /** Dexterity. */
    DEXTERITY("Dexterity", "Dex"),

    /** Constitution. */
    CONSTITUTION("Constitution", "Con"),

    /** Intelligence. */
    INTELLIGENCE("Intelligence", "Int"),

    /** Wisdom. */
    WISDOM("Widsom", "Wis"),

    /** Charisma. */
    CHARISMA("Charisma", "Cha"),

    /** No ability. */
    NONE("None", "-");

    /** The value's name. */
    private @Nonnull String m_name;

    /** The value's short name. */
    private @Nonnull String m_short;

    /** Create the name.
     *
     * @param inName       the name of the value
     * @param inShort      the short name of the value
     *
     */
    private Ability(@Nonnull String inName, @Nonnull String inShort)
    {
      m_name = constant("ability.name", inName);
      m_short = constant("ability.short", inShort);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the short name of the value
     *
     */
    public @Nonnull String getShort()
    {
      return m_short;
    }

    /** Get the save as string.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }
  };

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //----------------------------- BaseMonster ------------------------------

  /**
    * This is the internal, default constructor for an undefined value.
    *
    */
  protected BaseMonster()
  {
    super(TYPE);
  }

  //........................................................................
  //----------------------------- BaseMonster ------------------------------

  /**
    * This is the normal constructor.
    *
    * @param       inName the name of the base item
    *
    */
  public BaseMonster(String inName)
  {
    super(inName, TYPE);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final BaseType<BaseMonster> TYPE =
    new BaseType<BaseMonster>(BaseMonster.class);

  //----- size -------------------------------------------------------------

  /** The formatter for sizes. */
  // protected static ValueFormatter<Multiple> s_sizeFormatter =
  //   new LinkFormatter<Multiple>("/index/monstersizes/");

  /** The monsters size. */
  @Key("size")
  protected Multiple m_size = new Multiple(new Multiple.Element []
    {
      new Multiple.Element
      (new EnumSelection<BaseItem.Size>(BaseItem.Size.class), false),
      new Multiple.Element
      (new EnumSelection<BaseItem.SizeModifier>(BaseItem.SizeModifier.class),
       true, " (", ")"),
    });

  static
  {
    addIndex(new Index(Index.Path.SIZES, "Sizes", TYPE));
  }

  //........................................................................
  //----- type -------------------------------------------------------------

  /** The monster type and subtype. */
  @Key("type")
  @SuppressWarnings("unchecked") // unchecked generic array creation
  protected Multiple m_monsterType = new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new EnumSelection<MonsterType>(MonsterType.HUMANOID),
                           false),
      new Multiple.Element(new ValueList<EnumSelection<MonsterSubtype>>
                           (", ", new EnumSelection<MonsterSubtype>
                            (MonsterSubtype.HUMAN)), true, " (", ")"),
    });

  static
  {
    addIndex(new Index(Index.Path.TYPES, "Types", TYPE));
    addIndex(new Index(Index.Path.SUBTYPES, "Subtypes", TYPE));
  }

  //........................................................................
  //----- hit dice ---------------------------------------------------------

  /** The formatter for hit dices. */
  // ValueFormatter<Dice> s_hdFormatter = new ValueFormatter<Dice>() {
  //   public Command format(Dice inValue) {
  //     return new Command(new Object []
  //       {
  //         new Link(inValue.getNumber(),
  //                  "/index/monsterhds/" + inValue.getNumber()),
  //         "d",
  //         new Link(inValue.getDice(),
  //                  "/index/monsterdices/" + inValue.getDice()),
  //         inValue.getModifier() > 0 ? "+" + inValue.getModifier()
  //         : inValue.getModifier() < 0 ? inValue.getModifier() : "",
  //       });
  //   }
  // };

  /** The monster's hit dice. */
  @Key("hit dice")
  protected Dice m_hitDice = new Dice();

  // the indexes for number of dices and dice type
  static
  {
    addIndex(new Index(Index.Path.HDS, "HDs", TYPE));
    addIndex(new Index(Index.Path.DICES, "Dices", TYPE));
  }

  //........................................................................
  //----- speed ------------------------------------------------------------

  /** The monster's speed. */
  @Key("speed")
  protected ValueList<Multiple> m_speed =
    new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new EnumSelection<MovementMode>(MovementMode.class),
                           true),
      new Multiple.Element(new Distance(), false),
      new Multiple.Element(new EnumSelection<Maneuverability>
                           (Maneuverability.class), true, " (", ")"),
    }));

  static
  {
    addIndex(new Index(Index.Path.MOVEMENT_MODES, "Movement Modes", TYPE));
    addIndex(new Index(Index.Path.SPEEDS, "Speeds", TYPE));
    addIndex(new Index(Index.Path.MANEUVERABILITIES, "Maneuverabilitys", TYPE));
  }

  //........................................................................
  //----- natural armor ----------------------------------------------------

  /** The natural armor of the monster. */
  @Key("natural armor")
  protected Number m_natural = new Number(0, 1000, true);

  static
  {
    addIndex(new Index(Index.Path.NATURAL_ARMORS, "Natural Armors", TYPE));
  }

  //........................................................................
  //----- base attack ------------------------------------------------------

  /** The formatter for base attacks. */
  // protected static ValueFormatter<Number> s_baseAttackFormatter =
  //   new LinkFormatter<Number>("/index/baseattacks/");

  /** The base attack bonus. */
  @Key("base attack")
  protected Number m_attack = new Number(-1, 100, true);

  static
  {
    addIndex(new Index(Index.Path.BASE_ATTACKS, "Base Attacks", TYPE));
  }

  //........................................................................
  //----- strength ---------------------------------------------------------

  /** The strength formatter. */
  // protected static ValueFormatter<Number> s_strengthFormatter =
  //   new LinkFormatter<Number>("/index/strengths/");

  /** The monster's Strength. */
  @Key("strength")
  protected Number m_strength = new Number(-1, 100, false);

  static
  {
    addIndex(new Index(Index.Path.STRENGTHS, "Strengths", TYPE));
  }

  //........................................................................
  //----- dexterity --------------------------------------------------------

  /** The dexterity formatter. */
  // protected static ValueFormatter<Number> s_dexterityFormatter =
  //   new LinkFormatter<Number>("/index/dexterities/");

  /** The monster's Dexterity. */
  @Key("dexterity")
  protected Number m_dexterity = new Number(-1, 100, false);

  static
  {
    addIndex(new Index(Index.Path.DEXTERITIES, "Dexterities", TYPE));
  }

  //........................................................................
  //----- constitution -----------------------------------------------------

  /** The constitution formatter. */
  // protected static ValueFormatter<Number> s_constitutionFormatter =
  //   new LinkFormatter<Number>("/index/constitutions/");

  /** The monster's Constitution. */
  @Key("constitution")
  protected Number m_constitution = new Number(-1, 100, false);

  static
  {
    addIndex(new Index(Index.Path.CONSTITUTIONS, "Constitutions", TYPE));
  }

  //........................................................................
  //----- intelligence -----------------------------------------------------

  /** The intelligence formatter. */
  // protected static ValueFormatter<Number> s_intelligenceFormatter =
  //   new LinkFormatter<Number>("/index/inteligences/");

  /** The monster's Intelligence. */
  @Key("intelligence")
  protected Number m_intelligence = new Number(-1, 100, false);

  static
  {
    addIndex(new Index(Index.Path.INTELLIGENCES, "Intelligences", TYPE));
  }

  //........................................................................
  //----- wisdom -----------------------------------------------------------

  /** The wisdom formatter. */
  // protected static ValueFormatter<Number> s_wisdomFormatter =
  //   new LinkFormatter<Number>("/index/wisdoms/");

  /** The monster's Wisdom. */
  @Key("wisdom")
  protected Number m_wisdom = new Number(-1, 100, false);

  static
  {
    addIndex(new Index(Index.Path.WISDOMS, "Wisdoms", TYPE));
  }

  //........................................................................
  //----- charisma ---------------------------------------------------------

  /** The charisma formatter. */
  // protected static ValueFormatter<Number> s_charismaFormatter =
  //   new LinkFormatter<Number>("/index/strengths/");

  /** The monster's Charisma. */
  @Key("charisma")
  protected Number m_charisma = new Number(-1, 100, false);

  static
  {
    addIndex(new Index(Index.Path.CHARISMAS, "Charismas", TYPE));
  }

  //........................................................................
  //----- primary attacks --------------------------------------------------

  /** The monster's attacks. */
  @Key("primary attacks")
  protected ValueList<Multiple> m_primaryAttacks
    = new ValueList<Multiple>(",", new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new Number(1, 20).withEditType("name[number]"),
                           true),
      new Multiple.Element(new EnumSelection<AttackMode>(AttackMode.class),
                           false),
      new Multiple.Element(new EnumSelection<AttackStyle>(AttackStyle.class),
                           false),
      new Multiple.Element(new Damage().withEditType("name[damage]"),
                           false, " (", ")"),
    }));

  //........................................................................
  //----- secondary attacks ------------------------------------------------

  /** The monster's attacks. */
  @Key("secondary attacks")
  protected ValueList m_secondaryAttacks = m_primaryAttacks;

  //........................................................................
  //----- space ------------------------------------------------------------

  /** The formatter for space. */
  // protected static ValueFormatter<Distance> s_spaceFormatter =
  //   new LinkFormatter<Distance>("/index/spaces/");

  /** The grouping for space values. */
  protected static final Group<Distance, Long, String> s_spaceGrouping =
    new Group<Distance, Long, String>(new Group.Extractor<Distance, Long>()
      {
        public Long extract(Distance inValue)
        {
          if(inValue == null)
            throw new IllegalArgumentException("must have a distance here");

          return 10L * (long)inValue.getAsFeet().getValue();
        }
      }, new Long [] { 5L, 10L, 25L, 50L, 100L, 150L, 200L, 300L, },
      new String [] { "0.5 ft", "1 ft", "2.5 ft", "5 ft",
                      "10 ft", "15 ft", "20 ft", "30 ft", "Infinite", },
      "$undefined$");

  /** The space the monster occupies (computed). */
  @Key("space")
  protected Distance m_space = new Distance()
    .withGrouping(s_spaceGrouping);

  static
  {
    addIndex(new Index(Index.Path.SPACES, "Spaces", TYPE));
  }

  //........................................................................
  //----- reach ------------------------------------------------------------

  /** The formatter for space. */
  // protected static ValueFormatter<Distance> s_reachFormatter =
  //   new LinkFormatter<Distance>("/index/reaches/");

  /** The grouping for space values. */
  protected static final Group<Distance, Long, String> s_reachGrouping =
    new Group<Distance, Long, String>(new Group.Extractor<Distance, Long>()
      {
        public Long extract(Distance inValue)
        {
          if(inValue == null)
            throw new IllegalArgumentException("must have a distance here");

          return (long)inValue.getAsFeet().getValue();
        }
      }, new Long [] { 0L, 5L, 10L, 15L, 20L, 30L, },
      new String [] { "0 ft", "5 ft", "10 ft", "15 ft", "20 ft", "30 ft",
                      "Infinite", },
      "$undefined$");

  /** The reach of the monster. */
  @Key("reach")
  protected Distance m_reach = new Distance()
    .withGrouping(s_reachGrouping);

  static
  {
    addIndex(new Index(Index.Path.REACHES, "Reaches", TYPE));
  }

  //........................................................................
  //----- special attacks --------------------------------------------------

  /** The special attacks. */
  @Key("special attacks")
  protected ValueList<Multiple> m_specialAttacks = new ValueList<Multiple>
    (", ",
     new Multiple(new Multiple.Element []
       {
         new Multiple.Element(new Reference<BaseQuality>(BaseQuality.TYPE)
                              .withParameters(new ImmutableMap.Builder
                                              <String, Value>()
                                              .put("Range", new Distance())
                                              .put("Name", new Name())
                                              .put("Level", new Number(1, 100))
                                              .put("Value", new Number(1, 100))
                                              .put("Modifier", new Modifier())
                                              .put("Dice", new Dice())
                                              .put("Times", new Number(1, 100))
                                              .build())
                              .withTemplate("reference", "/quality/"), false),
         new Multiple.Element(new Number(1, 100)
                              .withEditType("name[per day]"), true, "/", null)
       }));

  //........................................................................
  //----- special qualities ------------------------------------------------

  /** The special qualities. */
  @Key("special qualities")
  protected ValueList<Multiple> m_specialQualities = new ValueList<Multiple>
    (", ",
     new Multiple(new Multiple.Element []
       {
         new Multiple.Element(new Reference<BaseQuality>(BaseQuality.TYPE)
                              .withParameters(new ImmutableMap.Builder
                                              <String, Value>()
                                              .put("Range", new Distance())
                                              .put("Name", new Name())
                                              .put("Level", new Number(1, 100))
                                              .put("Racial",
                                                   new Number(-50, 50, true))
                                              .put("Value", new Number(0, 100))
                                              .put("Modifier", new Modifier())
                                              .build())
                              .withTemplate("reference", "/quality/"), false),
         new Multiple.Element(new Condition()
                              .withEditType("string[condition]"),
                              true, " if ", null),
         new Multiple.Element(new Number(1, 100)
                              .withEditType("name[per day]"), true, "/", null),
       }));


  //........................................................................
  //----- class skills -----------------------------------------------------

  /** The class skills. */
  @Key("class skills")
  protected ValueList<Multiple> m_classSkills =
    new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
      {
        new Multiple.Element
        (new Reference<BaseSkill>(BaseSkill.TYPE)
         .withParameters(new ImmutableMap.Builder
                         <String, Value>()
                         .put("Subtype",
                              new EnumSelection<BaseSkill.Subtype>
                              (BaseSkill.Subtype.class))
                         .build()), false),
        new Multiple.Element(new Number(0, 100, true)
                             .withEditType("number[modifier]"),
                             false, ": ", null),
      }));

  //........................................................................
  //----- feats ------------------------------------------------------------

  /** The feats. */
  @Key("feats")
  protected ValueList<Reference> m_feats = new ValueList<Reference>
    (", ", new Reference<BaseFeat>(BaseFeat.TYPE)
     .withParameters(new ImmutableMap.Builder<String, Value>()
                     .put("Name", new Name())
                     .build()));

  //........................................................................
  //----- environment ------------------------------------------------------

  /** The environment. */
  @Key("environment")
  protected Multiple m_environment = new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new EnumSelection<Climate>(Climate.ANY), true),
      new Multiple.Element(new EnumSelection<Terrain>(Terrain.ANY), false),
    });

  static
  {
    addIndex(new Index(Index.Path.CLIMATES, "Climates", TYPE));
    addIndex(new Index(Index.Path.TERRAINS, "Terrains", TYPE));
  }

  //........................................................................
  //----- organization -----------------------------------------------------

  /** The monster's organization. */
  @Key("organization")
  protected ValueList<Multiple> m_organizations =
    new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
      {
        new Multiple.Element(new EnumSelection<Organization>(Organization.ANY),
                             false),
        new Multiple.Element(new Dice().withEditType("dice[number]"), false),
        new Multiple.Element(new ValueList<Multiple>
                             (", ",
                              new Multiple(new Multiple.Element []
                                {
                                  new Multiple.Element(new Dice(), false),
                                  new Multiple.Element(new Name(), false),
                                })).withEditType("any[additional]"),
                             true, " plus ", null),
      }));

  static
  {
    addIndex(new Index(Index.Path.ORGANIZATIONS, "Organizations", TYPE));
  }

  //........................................................................
  //----- challenge rating -------------------------------------------------

  /** The formatter for organizations. */
  // protected static ValueFormatter<Rational> s_crFormatter =
  //   new LinkFormatter<Rational>("/index/crs/");

  /** The monsters challenge rating. */
  @Key("challenge rating")
  protected Rational m_cr = new Rational();

  static
  {
    addIndex(new Index(Index.Path.CRS, "CRs", TYPE));
  }

  //........................................................................
  //----- treasure ---------------------------------------------------------

  /** The formatter for the treasure. */
  // protected static ValueFormatter<EnumSelection<Treasure>>
  //s_treasureFormatter =
  //   new LinkFormatter<EnumSelection<Treasure>>("/index/treasures/");

  /** The monster's possible treasure. */
  @Key("treasure")
  protected EnumSelection<Treasure> m_treasure =
    new EnumSelection<Treasure>(Treasure.class);

  static
  {
    addIndex(new Index(Index.Path.TREASURES, "Treasures", TYPE));
  }

  //........................................................................
  //----- alignment --------------------------------------------------------

  /** The formatter for the treasure. */
  // protected static ValueFormatter<EnumSelection<Alignment>>
  //   s_alignmentFormatter =
  //   new LinkFormatter<EnumSelection<Alignment>>("/index/alignments/");

  /** The monster's alignment. */
  @Key("alignment")
  protected Multiple m_alignment = new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new EnumSelection<AlignmentStatus>
                           (AlignmentStatus.class), false),
      new Multiple.Element(new EnumSelection<Alignment>(Alignment.class),
                           false, " ", null),
    });

  static
  {
    addIndex(new Index(Index.Path.ALIGNMENTS, "Alignments", TYPE));
  }

  //........................................................................
  //----- advancements -----------------------------------------------------

  /** The monster's advancement. */
  @Key("advancements")
  protected ValueList<Multiple> m_advancements =
    new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
      {
        new Multiple.Element(new Range(1, 100), false),
        new Multiple.Element
        (new EnumSelection<BaseItem.Size>(BaseItem.Size.class), false,
         " HD (", ")"),
      }));

  //........................................................................
  //----- level adjustment -------------------------------------------------

  /** The formatter for the treasure. */
  // protected static ValueFormatter<ValueSelection>
  //   s_levelAdjustmentFormatter =
  //   new LinkFormatter<ValueSelection>("/index/leveladjustments/");

  /** The monsters level adjustment. */
  @Key("level adjustment")
  protected Union m_levelAdjustment =
    new Union(new Selection(new String [] { "-" }),
              new Number(0, 20, true)).withEditType("name");

  static
  {
    addIndex(new Index(Index.Path.LEVEL_ADJUSTMENTS, "Level Adjustments",
                       TYPE));
  }

  //........................................................................
  //----- languages --------------------------------------------------------

  /** The formatter for the treasure. */
  // protected static ValueFormatter<EnumSelection<Language>>
  //   s_languageFormatter =
  //   new LinkFormatter<EnumSelection<Language>>("/index/languages/");

  /** The monsters languages. */
  @Key("languages")
  protected ValueList<Multiple> m_languages =
    new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
      {
        new Multiple.Element(new EnumSelection<LanguageModifier>
                             (LanguageModifier.class), true),
        new Multiple.Element(new EnumSelection<Language>(Language.class),
                             false),
      }));

  static
  {
    addIndex(new Index(Index.Path.LANGUAGES, "Languages", TYPE));
  }

  //........................................................................
  //----- encounter --------------------------------------------------------

  /** The monsters encounter. */
  @Key("encounter")
  protected LongFormattedText m_encounter = new LongFormattedText();

  //........................................................................
  //----- combat -----------------------------------------------------------

  /** The monsters combat tactics. */
  @Key("combat")
  protected LongFormattedText m_combat = new LongFormattedText();

  //........................................................................
  //----- tactics ----------------------------------------------------------

  /** The monsters tactics. */
  @Key("tactics")
  protected LongFormattedText m_tactics = new LongFormattedText();

  //........................................................................
  //----- character --------------------------------------------------------

  /** The monsters character. */
  @Key("character")
  protected LongFormattedText m_character = new LongFormattedText();

  //........................................................................
  //----- reproduction -----------------------------------------------------

  /** The monsters reproduction. */
  @Key("reproduction")
  protected LongFormattedText m_reproduction = new LongFormattedText();

  //........................................................................
  //----- possessions ------------------------------------------------------

  /** The standard possessions. */
  @Key("possessions")
  protected ValueList<Multiple> m_possessions =
    new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
      {
        new Multiple.Element(new Name(), true),
        new Multiple.Element(new Text(), true),
      }));

  //........................................................................
  //----- good saves -------------------------------------------------------

  /** The good saving throws. */
  @Key("good saves")
  protected ValueList<EnumSelection<Save>> m_goodSaves =
    new ValueList<EnumSelection<Save>>(new EnumSelection<Save>(Save.class),
                                       ", ");

  //........................................................................

  /** The feats entries. */
  protected Set<BaseFeat> m_featEntries = new HashSet<BaseFeat>();

  static
  {
    extractVariables(BaseMonster.class);
    extractVariables(BaseMonster.class, BaseIncomplete.class);
  }

  //----- commands ---------------------------------------------------------

  //----- page -------------------------------------------------------------

  // public static Command PAGE_COMMAND = new Command(new Object []
  //   {
  //     new TocEntry("$name"),
  //     new Divider("center",
  //                 new Command("#world #size #attachment #categories")),
  //     "$title",
  //     new Textblock(new Command(new Object []
  //       {
  //         "${+description}",
  //         new Hrule(),
  //         "${short description}",
  //       }), "desc"),
  //     new OverviewFiles("$image"),
  //     new Table("description", "f" + "Illustrations: ".length()
  //               + ":L(desc-label);100:L(desc-text)",
  //               new Command("%base %synonyms %type %{hit dice} %speed "
  //                           + "%{natural armor} "
  //                           + "%strength %dexterity %constitution "
  //                           + "%intelligence %wisdom %charisma "
  //                           + "%{base attack} "
  //                           + "%{primary attacks} %{secondary attacks} "
  //                           + "%{special attacks} "
  //                           + "%space %reach "
  //                           + "%{special qualities} "
  //                           + "%{class skills} %feats %{good saves}"
  //                           + "%advancements %{level adjustment}"
  //                           + "%alignment %languages "
  //                         + "%environment %organization %{challenge rating} "
  //                           + "%treasure %possessions "
  //                           // incomplete
  //                           + "%incomplete "
  //                           // admin
  //                           + "%{+references} %file")),
  //     new Divider("clear", " "),
  //     //new Table("texts", "100:B", new Object [] {
  //     new Block(new Command(new Object []
  //       {
  //         new Par(),
  //         new Italic("$encounter"),
  //         new Par(),
  //         new Right(new Scriptsize(new Italic(new Color("color-light",
  //                                                       "Combat")))),
  //         "$combat",
  //         new Right(new Scriptsize(new Italic(new Color("color-light",
  //                                                       "Tactics")))),
  //         "$tactics",
  //         new Right(new Scriptsize(new Italic(new Color("color-light",
  //                                                       "Character")))),
  //         "$character",
  //         new Right(new Scriptsize(new Italic(new Color("color-light",
  //                                                       "Reproduction")))),
  //         "$reproduction",
  //       })),
  //   });

  //........................................................................

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getAttacks ------------------------------

  /**
   * Get the different attacks made by this monster.
   *
   * @return      an iterator with all the values
   *
   */
  // public Iterator<Multiple> getPrimaryAttacks()
  // {
  //   return m_primaryAttacks.iterator();
  // }

  //........................................................................
  //-------------------------------- getSize -------------------------------

  /**
   * Get the size of the monster.
   *
   * @return      the index in the size table.
   *
   */
  // @SuppressWarnings(value = "unchecked")
  // public BaseItem.Size getSize()
  // {
  //   return ((EnumSelection<BaseItem.Size>)
  //           m_size.get(0).get()).getSelected();
  // }

  //........................................................................
  //------------------------------- getReach -------------------------------

  /**
   * Get the monster's reach.
   *
   * @return      the monsters reach
   *
   */
  public Distance getReach()
  {
    return m_reach;
  }

  //........................................................................
  //-------------------------- getAbilityModifier --------------------------

  /**
   * Get the current modifier for the given ability.
   *
   * @param       inAbility the ability to get the modifier for
   *
   * @return      the ability modifier
   *
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

  //........................................................................
  //------------------------------- getMaxHP -------------------------------

  /**
   * Determine the maximally possible hit points (without any modifiers).
   *
   * @return      the maximally possible hit points
   *
   */
  // public int getMaxHP()
  // {
  //   return (int)m_hitDice.getMax();
  // }

  //........................................................................
  //------------------------------- getMinHP -------------------------------

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

  //........................................................................
  //------------------------------- getFeats -------------------------------

  /**
   * Get the base feats of this monster.
   *
   * @return      an iterator over all base feats
   *
   */
  // public Iterator<BaseFeat> getFeats()
  // {
  //   return m_featEntries.iterator();
  // }

  //........................................................................

  //-------------------------------- level ---------------------------------

  /**
   * Get the level of the monster.
   *
   * @return      the monster's level or hit dice
   *
   */
  public int level()
  {
    return m_hitDice.getNumber();
  }

  //........................................................................
  //------------------------------ abilityMod ------------------------------

  /**
   * Get the ability modifier for the given value.
   *
   * @param       inAbility the ability to compute the modifier for
   *
   * @return      the ability modifier
   *
   */
  // public static int abilityMod(long inAbility)
  // {
  //   // if negative, we don't have the ability, thus a modifier of 0
  //   if(inAbility <= 0)
  //     return 0;

  //   return (int)(inAbility / 2) - 5;
  // }

  //........................................................................
  //----------------------------- skillPoints ------------------------------

  /**
   * Get the number of skill points of the monster.
   *
   * @return      the number of skill points
   *
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

  //........................................................................
  //----------------------------- skillRanks -------------------------------

  /**
   * Get the number of skill ranks in a specific skill.
   *
   * @param       inSkill the name of the skill to get the ranks of
   *
   * @return      the number of skill ranks
   *
   *
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

  //........................................................................

  //------------------------------ rollMaxHP -------------------------------

  /**
   * Roll the maximal hit points of the monster, including all modifiers.
   *
   * @return      the maximal hit point a monster of this type has
   *
   */
  // public int rollMaxHP()
  // {
  //   return m_hitDice.roll();
  // }

  //........................................................................

  //------------------------------ hasQuality ------------------------------

  /**
   * Determine if the monster has the given quality.
   *
   * @param       inQuality the quality to look for
   *
   * @return      true if the quality is there, false if not
   *
   */
  // public boolean hasQuality(String inQuality)
  // {
  //   for(Multiple value : m_specialQualities)
  //     if(value.toString().equalsIgnoreCase(inQuality))
  //       return true;

  //   return false;
  // }

  //........................................................................
  //------------------------------ hasLanguage -----------------------------

  /**
   * Determine if the monster understands the given lanauge.
   *
   * @param       inLanguage the language to look for
   *
   * @return      true if the language is there, false if not
   *
   */
  // public boolean hasLanguage(String inLanguage)
  // {
  //   for(Multiple value : m_languages)
  //     if(value.toString().equalsIgnoreCase(inLanguage))
  //       return true;

  //   return false;
  // }

  //........................................................................
  //------------------------------ hasSubtype ------------------------------

  /**
   * Determine if the monster has the given subtype.
   *
   * @param       inType the type to look for
   *
   * @return      true if the language is there, false if not
   *
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

  //........................................................................
  //--------------------------- hasMovementMode ----------------------------

  /**
   * Determine if the monster has the given movement mode.
   *
   * @param       inType the movement mode to look for
   *
   * @return      true if the language is there, false if not
   *
   */
  // public boolean hasMovementMode(String inType)
  // {
  //   for(Iterator<Multiple> i = m_speed.iterator(); i.hasNext(); )
  //     if(i.next().get(0).get().toString().equalsIgnoreCase(inType))
  //       return true;

  //   return false;
  // }

  //........................................................................
  //----------------------------- isClassSkill -----------------------------

  /**
   * Check if the given name is a describing a class skill of the monster.
   *
   * @param       inName the name of the skill to check
   *
   * @return      true if it is a class skill, false if not
   *
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

  //........................................................................
  //--------------------------------- isDM ---------------------------------

  /**
   * Check whether the given user is the DM for this entry.
   *
   * @param       inUser the user accessing
   *
   * @return      true for DM, false for not
   *
   */
  @Override
  public boolean isDM(@Nullable BaseCharacter inUser)
  {
    return inUser != null && inUser.hasAccess(BaseCharacter.Group.DM);
  }

  //........................................................................

  //------------------------------ printCommand ----------------------------

  /**
   * Print the item to the document, in the general section.
   *
   * @param       inDM       true if set for DM, false for player
   * @param       inEditable true if values are editable, false if not
   *
   * @return      the command representing this item in a list
   *
   */
  // public PrintCommand printCommand(final boolean inDM, boolean inEditable)
  // {
  //   final PrintCommand commands = super.printCommand(inDM, inEditable);

  //   commands.type = "monster";

  //   commands.temp = new ArrayList<Object>();
  //   commands.temp.add(PAGE_COMMAND.transform(new ValueTransformer(commands,
  //                                                                 inDM)));

  //   return commands;
  // }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //---------------------------- checkQualities ----------------------------

  /**
   *
   * Check that the monster has the given qualities.
   *
   * @param       inQualities the qualities to check for
   *
   * @return      true if everything is ok, false if not
   *
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

  //........................................................................
  //-------------------------- computeIndexValues --------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @return      a multi map of values per index name
   *
   */
  @Override
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

    values.put(Index.Path.SIZES, m_size.group());
    values.put(Index.Path.TYPES, m_monsterType.get(0).group());

    for(Value value : ((ValueList<? extends Value>)m_monsterType.get(1)))
      values.put(Index.Path.SUBTYPES, value.group());

    values.put(Index.Path.HDS, "" + m_hitDice.getNumber());
    values.put(Index.Path.DICES, "" + m_hitDice.getDice());

    for(Multiple value : m_speed)
    {
      values.put(Index.Path.MOVEMENT_MODES, value.get(0).group());
      values.put(Index.Path.SPEEDS, "" + value.get(1).group());
      values.put(Index.Path.MANEUVERABILITIES, "" + value.get(2).group());
    }

    values.put(Index.Path.NATURAL_ARMORS, m_natural.group());
    values.put(Index.Path.BASE_ATTACKS, m_attack.group());
    values.put(Index.Path.STRENGTHS, m_strength.group());
    values.put(Index.Path.DEXTERITIES, m_dexterity.group());
    values.put(Index.Path.CONSTITUTIONS, m_constitution.group());
    values.put(Index.Path.INTELLIGENCES, m_intelligence.group());
    values.put(Index.Path.WISDOMS, m_wisdom.group());
    values.put(Index.Path.CHARISMAS, m_charisma.group());
    values.put(Index.Path.SPACES, m_space.group());
    values.put(Index.Path.REACHES, m_reach.group());
    values.put(Index.Path.CLIMATES, m_environment.get(0).group());
    values.put(Index.Path.TERRAINS, m_environment.get(1).group());

    for(Multiple organization : m_organizations)
      values.put(Index.Path.ORGANIZATIONS, organization.get(0).group());

    values.put(Index.Path.CRS, m_cr.group());
    values.put(Index.Path.TREASURES, m_treasure.group());
    values.put(Index.Path.ALIGNMENTS, m_alignment.get(1).group());
    values.put(Index.Path.LEVEL_ADJUSTMENTS, m_levelAdjustment.group());

    for(Multiple language : m_languages)
      values.put(Index.Path.LANGUAGES, language.get(1).group());

    return values;
  }

  //........................................................................

  //-------------------------------- check ---------------------------------

  /**
   * Check the entry for possible problems.
   *
   * @param       inCampaign the campaign with all the data
   *
   * @return      false if a problem was found, true if not
   *
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

  //........................................................................
  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled.
   *
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

  //........................................................................
  //----------------------------- modifyValue ------------------------------

  /**
   * Modify the given value with information from the current attachment.
   *
   * @param       inType    the type of value to modify
   * @param       inValue   the value to modify, return in this object
   * @param       inDynamic a flag denoting if dynamic values are requested
   *
   * @return      the newly computed value (or null if no value to use)
   *
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

  //........................................................................

  //------------------------------- groupHP --------------------------------

  /**
   * Make a grouping of the given HPs.
   *
   * @param       inHP the input value
   *
   * @return      the grouped value (as a String)
   *
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

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- createBaseMonster() --------------------------------------------

    /**
     * Create a typical base item for testing purposes.
     *
     * @return the newly created base item
     *
     */
    public static AbstractEntry createBaseMonster()
    {
      ParseReader reader =
        new ParseReader(new java.io.StringReader(s_text), "test");

      return BaseMonster.read(reader);
    }

    //......................................................................

    //----- text -----------------------------------------------------------

    /** Test text. */
    private static String s_text =
      "#----- Aboleth -------------------------------------------------\n"
      + "\n"
      + "base monster Aboleth = \n"
      + "\n"
      + "  size              huge (long);\n"
      + "  type              Aberration (Aquatic);\n"
      + "  hit dice          8d8;\n"
      + "  speed             10 ft, swim 60 ft;\n"
      + "  natural armor     +7;\n"
      + "  base attack       +6;  \n"
      + "  primary attacks   4 tentacle melee (1d6 plus slime);  \n"
      + "  special attacks   enslave, psionics, slime;\n"
      + "  special qualities darkvision [range 60 ft], mucus cloud, breathe "
      + "water, \n"
      + "                    breathe no air, good swimmer [racial +8];\n"
      + "  strength          26;\n"
      + "  dexterity         12;\n"
      + "  constitution      20;\n"
      + "  intelligence      15;\n"
      + "  wisdom            17;\n"
      + "  charisma          17;\n"
      + "  class skills      Concentration: +11, Knowledge [subtype any one]: "
      + "+11, \n"
      + "                    Listen: +11, Spot: +11, Swim: 0;\n"
      + "  feats             Alertness, Combat Casting, Iron Will;\n"
      + "  environment       underground;\n"
      + "  organization      solitary, brood (1d3+1), \n"
      + "                    slaver brood (1d3+1 plus 1d6+6 skum);\n"
      + "  challenge rating  7;\n"
      + "  treasure          double standard;\n"
      + "  alignment         usually lawful evil;\n"
      + "  advancements      9-16 HD (Huge), 17-24 HD (Gargantuan);\n"
      + "  level adjustment  -;\n"
      + "  worlds            generic;\n"
      + "  references        WTC 17755: 8-9;\n"
      + "  short description \"short description\";\n"
      + "  encounter         \"encounter\";\n"
      + "  combat            \"combat\";\n"
      + "  languages         Aboleth, Undercommon, Aquan;\n"
      + "  tactics           \"tactic\";\n"
      + "  character         \"character\";\n"
      + "  reproduction      \"reproduction\";\n"
      + "  description \n"
      + "\n"
      + "  \"description\".\n"
      + "\n"
      + "#...............................................................\n";

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Test reading. */
    @org.junit.Test
    public void testRead()
    {
      //net.ixitxachitls.util.logging.Log.add("out",
      //                                      new net.ixitxachitls.util.logging
      //                                      .ANSILogger());

      String result =
      "#----- Aboleth\n"
      + "\n"
      + "base monster Aboleth =\n"
      + "\n"
      + "  size              Huge (long);\n"
      + "  type              Aberration (Aquatic);\n"
      + "  hit dice          8d8;\n"
      + "  speed             10 ft, Swim 60 ft;\n"
      + "  natural armor     +7;\n"
      + "  base attack       +6;\n"
      + "  strength          26;\n"
      + "  dexterity         12;\n"
      + "  constitution      20;\n"
      + "  intelligence      15;\n"
      + "  wisdom            17;\n"
      + "  charisma          17;\n"
      + "  primary attacks   4 Tentacle melee (1d6 plus slime);\n"
      + "  special attacks   enslave, psionics, slime;\n"
      + "  special qualities darkvision [Range 60 ft], mucus cloud, breathe "
      + "water, breathe no air, good swimmer [Racial +8];\n"
      + "  class skills      Concentration: +11, Knowledge [Subtype Any One]:"
      + " +11, Listen: +11, Spot: +11, Swim: +0;\n"
      + "  feats             Alertness, Combat Casting, Iron Will;\n"
      + "  environment       Underground;\n"
      + "  organization      solitary, brood (1d3 +1), slaver brood (1d3 +1 "
      + "plus 1d6 +6 skum);\n"
      + "  challenge rating  7;\n"
      + "  treasure          double standard;\n"
      + "  alignment         Usually Lawful Evil;\n"
      + "  advancements      9-16 HD (Huge), 17-24 HD (Gargantuan);\n"
      + "  level adjustment  -;\n"
      + "  languages         Aboleth, Undercommon, Aquan;\n"
      + "  encounter         \"encounter\";\n"
      + "  combat            \"combat\";\n"
      + "  tactics           \"tactic\";\n"
      + "  character         \"character\";\n"
      + "  reproduction      \"reproduction\";\n"
      + "  possessions       ;\n"
      + "  worlds            Generic;\n"
      + "  references        WTC 17755: 8-9;\n"
      + "  description       \"description\";\n"
      + "  short description \"short description\".\n"
      + "\n"
      + "#.....\n";

      AbstractEntry entry = createBaseMonster();

      //System.out.println("read entry:\n'" + entry + "'");

      assertNotNull("base item should have been read", entry);
      assertEquals("base item name does not match", "Aboleth",
                   entry.getName());
      assertEquals("base item does not match", result, entry.toString());
    }

    //......................................................................
    //----- print ----------------------------------------------------------

    /** Test raw printing. */
    // public void testPrint()
    // {
    //   ParseReader reader =
    //     new ParseReader(new java.io.StringReader(s_text), "test");

    //   AbstractEntry entry = BaseMonster.read(reader);

    //   m_logger.verify();

    //   // title and icons
    //   String result =
    //     "\\toc{Aboleth}"
    //     + "\\page{\\title[title][\\link[BaseMonsters/index]{(base monster)}]"
    //     + "{Aboleth}}"
    //     + "{\\icon{worlds/Generic.png}{world: Generic}"
    //     + "{../index/worlds/\\worduppercase{Generic}.html}{highlight}"
    //     + "\\icon{sizes/6Huge-Long.png}"
    //     + "{size: Huge (Long)}"
    //     + "{../index/monstersizes/6Huge-Long.html}{highlight}"
    //     + "\\icon{monstertypes/Aberration.png}"
    //     + "{type: Aberration}"
    //     + "{../index/monstertypes/Aberration.html}{highlight}"
    //     + "\\icon{monstersubtypes/Aquatic.png}"
    //     + "{subtype: Aquatic}"
    //     + "{../index/monstersubtypes/Aquatic.html}{highlight}"
    //     + "}";

    //   // files
    //   result += "{\\files[WTC 17755]{BaseMonsters/Aboleth}}";

    //   // files 2
    //   result += "{\\files[~WTC 17755]{BaseMonsters/Aboleth}}";

    //   // description table
    //   result += "{\\table[description]{30:L(desc-label);70:L(desc-text)}"
    //     + "{null}{null}"
    //     + "{\\nopictures{\\bold{World:}}}"
    //     + "{\\nopictures{\\link[index/worlds/Generic]{Generic}}}"
    //     + "{\\nopictures{\\bold{Size:}}}"
    //     + "{\\nopictures{Huge (Long)}}"
    //     + "{\\nopictures{\\bold{Type:}}}"
    //     + "{\\nopictures{Aberration (Aquatic)}}"
    //     + "{\\divider{desc-width}{\\window{\\bold{Hit Dice:}}{"
    //     + Config.get("resource:help/label.hit.dice", (String)null)
    //     + "}}}{\\link[index/monsterhitdices/8]{8}d"
    //     + "\\link[index/monsterdices/8]{8}}"
    //     + "{\\window{\\bold{CR:}}{"
    //     + Config.get("resource:help/label.cr", (String)null)
    //     + "}}{\\link[index/crs/007]{7}}"
    //     + "{\\window{\\bold{Level Adj.:}}{"
    //     + Config.get("resource:help/label.level.adjustment", (String)null)
    //     + "}}{\\link[index/leveladjustments/-]{-}}"
    //     + "{\\window{\\bold{Alignment:}}{"
    //     + Config.get("resource:help/label.alignment", (String)null)
    //     + "}}{\\link[index/monsteralignments/Usually Lawful Evil]"
    //     + "[]{Usually Lawful Evil}}"
    //     + "{\\window{\\bold{Environment:}}{"
    //     + Config.get("resource:help/label.environment", (String)null)
    //     + "}}{ \\link[index/terrains/Underground]{Underground} }"
    //     + "{\\window{\\bold{Speed:}}{"
    //     + Config.get("resource:help/label.speed", (String)null)
    //     + "}}{ \\link[index/speeds/10 ft]{\\window{\\span{unit}{10 ft}}"
    //     + "{\\table{#inline#1:L,,;100:L}{Total:}{10 ft}{}{}{Metric:}"
    //     + "{\\span{unit}{4 m}}}}"
    //     + ", \\link[index/movementmodes/Swim]{Swim} "
    //     + "\\link[index/speeds/60 ft]{\\window{\\span{unit}{60 ft}}"
    //     + "{\\table{#inline#1:L,,;100:L}{Total:}{60 ft}{}{}{Metric:}"
    //     + "{\\span{unit}{24 m}}}}}"
    //     + "{\\window{\\bold{Base Attack:}}{"
    //     + Config.get("resource:help/label.base.attack", (String)null)
    //     + "}}{\\link[index/baseattacks/+6]{+6}}"
    //     + "{\\window{\\italic{\\bold{Space/Reach:}}}{"
    //     + Config.get("resource:help/label.space", (String)null)
    //     + "}}{\\color{error}{$undefined$}/\\color{error}{$undefined$}}"
    //     + "{\\window{\\bold{Organization:}}{"
    //     + Config.get("resource:help/label.organization", (String)null)
    //     + "}}{\\link[index/organizations/Solitary]{Solitary}, "
    //     + "\\link[index/organizations/Brood]{Brood} (1d3 +1), "
    //     + "\\link[index/organizations/Slaver Brood]{Slaver Brood} "
    //     + "(1d3 +1 plus 1d6 +6 \\link[BaseMonsters/skum]{skum})}"
    //     + "{\\window{\\bold{Treasure:}}{"
    //     + Config.get("resource:help/label.treasure", (String)null)
    //     + "}}{\\link[index/treasures/2Double Standard]{Double Standard}}"
    //     + "{\\window{\\bold{Advancement:}}{"
    //     + Config.get("resource:help/label.advancement", (String)null)
    //     + "}}{9-16 HD (Huge), 17-24 HD (Gargantuan)}"
    //     + "{\\window{\\bold{Languages:}}{"
    //     + Config.get("resource:help/label.languages", (String)null)
    //     + "}}{ \\link[index/languages/Aboleth]{Aboleth},  "
    //     + "\\link[index/languages/Undercommon]{Undercommon},  "
    //     + "\\link[index/languages/Aquan]{Aquan}}"
    //     + "{\\window{\\bold{Sp. Attacks:}}{"
    //     + Config.get("resource:help/label.special.attacks", (String)null)
    //     + "}}{\\link[BaseQualitys/enslave]{enslave}, "
    //     + "\\link[BaseQualitys/psionics]{psionics}, "
    //     + "\\link[BaseQualitys/slime]{slime}}"
    //     + "{\\window{\\bold{Sp. Qualities:}}{"
    //     + Config.get("resource:help/label.special.qualities", (String)null)
    //     + "}}{\\link[BaseQualitys/darkvision]{darkvision} (Range "
    //    + "\\window{\\span{unit}{60 ft}}{\\table{#inline#1:L,,;100:L}{Total:}"
    //     + "{60 ft}{}{}{Metric:}{\\span{unit}{24 m}}}), "
    //     + "\\link[BaseQualitys/mucus cloud]{mucus cloud}, "
    //     + "\\link[BaseQualitys/breathe water]{breathe water}, "
    //     + "\\link[BaseQualitys/breathe no air]{breathe no air}, "
    //     + "\\link[BaseQualitys/good swimmer]{good swimmer} (Racial +8)}"
    //     + "{\\window{\\bold{Abilities:}}{"
    //     + Config.get("resource:help/label.abilities", (String)null)
    //     + "}}{Str \\link[index/strengths/26]{26} (+8), "
    //     + "Dex \\link[index/dexterities/12]{12} (+1), "
    //     + "Con \\link[index/constitutions/20]{20} (+5), "
    //     + "Int \\link[index/intelligences/15]{15} (+2), "
    //     + "Wis \\link[index/wisdoms/17]{17} (+3), "
    //     + "Cha \\link[index/charismas/17]{17} (+3)}"
    //     + "{\\window{\\bold{Class Skills:}}{"
    //     + Config.get("resource:help/label.skills", (String)null)
    //     + "}}{\\link[BaseSkills/Concentration]{Concentration} +11, "
    //     + "\\link[BaseSkills/Knowledge]{Knowledge} [Subtype Any One] +11, "
    //     + "\\link[BaseSkills/Listen]{Listen} +11, "
    //     + "\\link[BaseSkills/Spot]{Spot} +11, \\link[BaseSkills/Swim]{Swim} "
    //     + "(44 skill points)}"
    //     + "{\\window{\\bold{Feats:}}{"
    //     + Config.get("resource:help/label.feats", (String)null)
    //     + "}}{\\link[BaseFeats/Alertness]{Alertness}, "
    //     + "\\link[BaseFeats/Combat Casting]{Combat Casting}, "
    //     + "\\link[BaseFeats/Iron Will]{Iron Will}}"
    //     + "{\\window{\\bold{References:}}{"
    //     + Config.get("resource:help/label.references", (String)null)
    //    + "}}{\\span{unit}{\\link[BaseProducts/WTC 17755]{WTC 17755} p. 8-9}}"
    //     + "{\\window{\\bold{Monsters:}}{"
    //     + Config.get("resource:help/label.monsters", (String)null)
    //     + "}}{null}"
    //     + "{null}{null}}"
    //     + "{\\table[texts]{100:B}{\\par }"
    //     + "{\\emph{\\textblock[desc]{encounter}}}"
    //     + "{\\par }{\\textblock[desc]{description}}"
    //   + "{\\right{\\scriptsize{\\italic{\\color{#AAAAAA}{Combat}}}}}{combat}"
    //     + "{\\right{\\scriptsize{\\italic{\\color{#AAAAAA}{Tactics}}}}}"
    //     + "{tactic}"
    //     + "{\\right{\\scriptsize{\\italic{\\color{#AAAAAA}{Character}}}}}"
    //     + "{character}"
    //     + "{\\right{\\scriptsize{\\italic{\\color{#AAAAAA}{Reproduction}}}}}"
    //     + "{reproduction}}"
    //     + "{\\table[text]{100:L}{\\right{\\scriptsize{\\italic"
    //     + "{\\color{#AAAAAA}{Feats}}}}}{null}"
    //     + "{\\right{\\scriptsize{\\italic"
    //     + "{\\color{#AAAAAA}{Skills}}}}}{null}"
    //     + "{\\right{\\scriptsize{\\italic"
    //     + "{\\color{#AAAAAA}{Special Qualities}}}}}{null}"
    //     + "{\\right{\\scriptsize{\\italic"
    //     + "{\\color{#AAAAAA}{Special Attacks}}}}}{null}}\\newpage ";

    //   assertEquals("print commands",
    //                result,
    //                entry.getPrintCommand(false));
    // }

    //......................................................................
    //----- shortPrint -----------------------------------------------------

    /** Test short printing. */
    // public void testShortPrint()
    // {
    //   ParseReader reader =
    //     new ParseReader(new java.io.StringReader(s_text), "test");

    //   AbstractEntry entry = BaseMonster.read(reader);

    //   String result =
    //     "\\title[label]{Aboleth (\\color{error}{$undefined$})}\n\n"
    //     + "\\label{World}"
    //     + "\\link[index/worlds/Generic]{Generic}"
    //     + "\\label{Size}H (L)\\label{Type}Aberration (Aquatic)"
    //     + "\\label{Hit Dice}\\link[index/monsterhitdices/8]{8}d"
    //     + "\\link[index/monsterdices/8]{8}\\label{Speed} "
    //     + "\\link[index/speeds/10 ft]{10 ft}, "
    //     + "\\link[index/movementmodes/Swim]{Swim} "
    //     + "\\link[index/speeds/60 ft]{60 ft}short description\n";

    //   //System.out.println(entry.getShortPrintCommand().toString());
    //   assertEquals("print commands",
    //                result, entry.getPrintCommand(false));
    // }

    //......................................................................
  }

  //........................................................................
}
