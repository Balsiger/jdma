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

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.extensions.BaseIncomplete;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.output.ascii.ASCIIDocument;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.Duration;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.LongFormattedText;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Parameters;
import net.ixitxachitls.dma.values.Rational;
import net.ixitxachitls.dma.values.Selection;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.Union;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.Document;
import net.ixitxachitls.util.Grouping;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the basic jDMA base spell.
 *
 * @file          BaseSpell.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

//..........................................................................

//__________________________________________________________________________

public class BaseSpell extends BaseEntry
{
  //----------------------------------------------------------------- nested

  //----- schools ----------------------------------------------------------

  /** The possible spell schools (cf. PHB 172/173). */
  public enum School implements EnumSelection.Named, EnumSelection.Short
  {
    /** Abjuration. */
    ABJURATION("Abjuration", "Abjur"),

    /** Conjuration. */
    CONJURATION("Conjuration", "Conj"),

    /** Divination. */
    DIVINATION("Divination", "Div"),

    /** Enchantment. */
    ENCHANTMENT("Enchantment", "Ench"),

    /** Evocation. */
    EVOCATION("Evocation", "Evoc"),

    /** Illusion. */
    ILLUSION("Illusion", "Illus"),

    /** Necromancy. */
    NECROMANCY("Necromancy", "Necro"),

    /** Transmutation. */
    TRANSMUTATION("Transmutation", "Trans"),

    /** Universal. */
    UNIVERSAL("Universal", "Univ");

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
    private School(@Nonnull String inName, @Nonnull String inShort)
    {
      m_name = constant("school.name", inName);
      m_short = constant("school.short", inShort);
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
  }

  //........................................................................
  //----- subschools -------------------------------------------------------

  /** The possible spell schools (cf. PHB 172/173). */
  public enum Subschool implements EnumSelection.Named
  {
    /** Calling. */
    CALLING("Calling"),

    /** Creation or Calling. */
    CREATION_OR_CALLING("Creation or Calling"),

    /** Creation. */
    CREATION("Creation"),

    /** Healing. */
    HEALING("Healing"),

    /** Summoning. */
    SUMMONING("Summoning"),

    /** Teleportation. */
    TELEPORTATION("Teleportation"),

    /** Scrying. */
    SCRYING("Scrying"),

    /** Charmn. */
    CHARM("Charm"),

    /** Compulsion. */
    COMPULSION("Compulsion"),

    /** Figment or Glamer. */
    FIGMENT_OR_GLAMER("Figment or Glamer"),

    /** Figment. */
    FIGMENT("Figment"),

    /** Glamer. */
    GLAMER("Glamer"),

    FIGMENT_GLAMER("Figment, Glamer"),

    /** Pattern. */
    PATTERN("Pattern"),

    /** Phantasm. */
    PHANTASM("Phantasm"),

    /** Shadow. */
    SHADOW("Shadow");

    /** The value's name. */
    private @Nonnull String m_name;

    /** The value's short name. */
    private @Nonnull String m_short;

    /** Create the name.
     *
     * @param inName       the name of the value
     *
     */
    private Subschool(@Nonnull String inName)
    {
      m_name = constant("subschool.name", inName);
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

    /** Get the save as string.
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
  //----- descriptors ------------------------------------------------------

  /** The possible spell descriptors. */
  public enum Descriptor implements EnumSelection.Named
  {
    /** Acid. */
    ACID("Acid"),

    /** Air. */
    AIR("Air"),

    /** Chaotic. */
    CHAOTIC("Chaotic"),

    /** Cold. */
    COLD("Cold"),

    /** Darkness. */
    DARKNESS("Darkness"),

    /** Death. */
    DEATH("Death"),

    /** Earth. */
    EARTH("Earth"),

    /** Electricity. */
    ELECTRICITY("Electricity"),

    /** Evil. */
    EVIL("Evil"),

    /** Fear. */
    FEAR("Fear"),

    /** Fire or Cold. */
    FIRE_OR_COLD("Fire or Cold"),

    /** Fire. */
    FIRE("Fire"),

    /** Force. */
    FORCE("Force"),

    /** Good. */
    GOOD("Good"),

    /** Language-dependent. */
    LANGUAGE_DEPENDENT("Language-dependent"),

    /** Lawful. */
    LAWFUL("Lawful"),

    /** Light. */
    LIGHT("Light"),

    /** Mind-affecting. */
    MIND_AFFECTING("Mind-affecting"),

    /** Scrying. */
    SCRYING("Scrying"),

    /** Sonic. */
    SONIC("Sonic"),

    /** Water. */
    WATER("Water"),

    /** See Text. */
    SEE_TEXT("See Text");

    /** The value's name. */
    private @Nonnull String m_name;

    /** The value's short name. */
    private @Nonnull String m_short;

    /** Create the name.
     *
     * @param inName       the name of the value
     *
     */
    private Descriptor(@Nonnull String inName)
    {
      m_name = constant("descriptor.name", inName);
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

    /** Get the save as string.
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
  //----- spell class ------------------------------------------------------

  /** The possible spell classes. */
  public enum SpellClass implements EnumSelection.Named, EnumSelection.Short
  {
    /** Assassin. */
    ASSASSIN("Assassin", "Asn"),

    /** Bard. */
    BARD("Bard", "Brd"),

    /** Clecric. */
    CLERIC("Cleric", "Clr"),

    /** Druid. */
    DRUID("Druid", "Drd"),

    /** Paladin. */
    PALADIN("Paladin", "Pal"),

    /** Ranger. */
    RANGER("Ranger", "Rgr"),

    /** Sorcerer. */
    SORCERER("Sorcerer", "Sor"),

    /** Wizard. */
    WIZARD("Wizard", "Wiz"),

    /** Air. */
    AIR("Air", "Air"),

    /** Animal. */
    ANIMAL("Animal", "Animal"),

    /** Chaos. */
    CHAOS("Chaos", "Chaos"),

    /** Death. */
    DEATH("Death", "Death"),

    /** Destruction. */
    DESTRUCTION("Destruction", "Destruction"),

    /** Drow. */
    DROW("Drow", "Drow"),

    /** Earth. */
    EARTH("Earth", "Earth"),

    /** Evil. */
    EVIL("Evil", "Evil"),

    /** Fire. */
    FIRE("Fire", "Fire"),

    /** Good. */
    GOOD("Good", "Good"),

    /** Healing. */
    HEALING("Healing", "Healing"),

    /** Knowledge. */
    KNOWLEDGE("Knowledge", "Knowledge"),

    /** Law. */
    LAW("Law", "Law"),

    /** Luck. */
    LUCK("Luck", "Luck"),

    /** Magic. */
    MAGIC("Magic", "Magic"),

    /** Plant. */
    PLANT("Plant", "Plant"),

    /** Protection. */
    PROTECTION("Protection", "Protection"),

    /** Strength. */
    STRENGTH("Strength", "Strength"),

    /** Sun. */
    SUN("Sun", "Sun"),

    /** Travel. */
    TRAVEL("Travel", "Travel"),

    /** Trickery. */
    TRICKERY("Trickery", "Trickery"),

    /** War. */
    WAR("War", "War"),

    /** Water. */
    Water("Water", "Water");

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
    private SpellClass(@Nonnull String inName, @Nonnull String inShort)
    {
      m_name = constant("spellclass.name", inName);
      m_short = constant("spellclass.short", inShort);
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
  }

  //........................................................................
  //----- components -------------------------------------------------------

  /** The possible spell components. */
  public enum Components implements EnumSelection.Named, EnumSelection.Short
  {
    /** Verbose. */
    VERBOSE("Verbose", "V"),

    /** Somatic. */
    SOMATIC("Somatic", "S"),

    /** Material/Divine Focus. */
    MATERIAL_DEVINE_FOCUS("Material/Divine Focus", "M/DF"),

    /** Material. */
    MATERIAL("Material", "M"),

    /** Focus/Divine Focus. */
    FOCUS_DIVINE_FOCUS("Focus/Divine Focus", "F/DF"),

    /** Focus. */
    FOCUS("Focus", "F"),

    /** Divine Focus. */
    DIVINE_FOCUS("Divine Focus", "DF"),

    /** Experience points. */
    EXPERIENCE_POINTS("Experience Points", "XP");

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
    private Components(@Nonnull String inName, @Nonnull String inShort)
    {
      m_name = constant("school.name", inName);
      m_short = constant("school.short", inShort);
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
  }

  //........................................................................
  //----- range ------------------------------------------------------------

  /** The possible spell ranges. */
  public enum Range implements EnumSelection.Named
  {
    /** Personal or Touch. */
    PERSONAL_OR_TOUCH("Personal or Touch"),

    /** Personal and Touch. */
    PERSONAL_AND_TOUCH("Personal and Touch"),

    /** Personal or Close. */
    PERSONAL_OR_CLOSE("Personal or Close"),

    /** Personal. */
    PERSONAL("Personal"),

    /** Touch. */
    TOUCH("Touch"),

    /** Close. */
    CLOSE("Close"),

    /** Medium. */
    MEDIUM("Medium"),

    /** Long. */
    LONG("Long"),

    /** Unlimited. */
    UNLIMITED("Unlimited"),

    /** 40 ft/level. */
    FOURTY_FEET_PER_LEVEL("40 ft/level"),

    /** See Text. */
    SEE_TEXT("See Text"),

    /** Anywhere within the area to be warded. */
    ANYWHERE_WITHIN_AREA_WARDED("Anywhere within the area to be warded"),

    /** Up to 10 ft/level. */
    UP_TO_TEN_FEET_PER_LEVEL("Up to 10 ft/level"),

    /** 1 mile/level. */
    ONE_MILE_PER_LEVEL("1 mile/level");

    /** The value's name. */
    private @Nonnull String m_name;

    /** Create the name.
     *
     * @param inName       the name of the value
     *
     */
    private Range(@Nonnull String inName)
    {
      m_name = constant("range.name", inName);
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

    /** Get the save as string.
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
  //----- effect -----------------------------------------------------------

  /** The possible spell ranges. */
  public enum Effect implements EnumSelection.Named
  {
    /** Ray. */
    RAY("Ray"),

    /** Spread. */
    SPREAD("Spread");

    /** The value's name. */
    private @Nonnull String m_name;

    /** Create the name.
     *
     * @param inName       the name of the value
     *
     */
    private Effect(@Nonnull String inName)
    {
      m_name = constant("effect.name", inName);
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

    /** Get the save as string.
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
  //----- spell durations --------------------------------------------------

  /** The possible spell durations (cf. PHB 176). */
  public static final String []SPELL_DURATIONS =
    Config.get("/game/spell.durations", new String []
      {
        "Instantaneous or concentration (up to 1 round/level)",
        "Instantaneous or 1 round/level",
        "Instantaneous (1 round)",
        "Instantaneous (1d4 rounds)",
        "Instantaneous/1 hour",
        "Instantaneous",
        "Permanent until Discharged",
        "Permanent until triggered, then 1 round/level",
        "Permanent or until discharged until released or 1d4 days + one "
        + "day/level",
        "Permanent",
        "Concentration (up to 1 round/level) or instantaneous",
        "Concentration up to 1 round/level",
        "Concentration + 1 round/level",
        "Concentration + 1 hour/level",
        "Concentration up to 1 min/level",
        "Concentration up to 10 min/level",
        "Concentration + 2 rounds",
        "Concentration + 3 rounds",
        "Concentration (maximum 10 rounds)",
        "Concentration",
        "Discharge",
        "Indefinite",
        "See Text",
        "4d12 hours",
        "5 rounds or less",
        "One hour or less",
        "One round per three levels",
        "One hour/level or until discharged",
        "One round/level or One round",
        "Until landing or 1 round/level",
        "10 min/level or until used",
        "10 min/level or until discharged",
        "One day/level or until discharged",
        "1d6+2 rounds",
        "One minute or until discharged",
        "One hour plus 12 hours",
        "2d4 rounds",
        "Sixty days or until discharged",
        "One hour/level or until you return to your body",
        "30 minutes or until discharged",
        "One round + 1 round per three levels",
        "30 minutes and 2d6 rounds",
        "1 round/level (D) and concentration + 3 rounds",
        "1 hour/caster level or until discharged, then 1 round/caster level",
        "1d4+1 rounds (apparent time)",
        "1d4+1 rounds",
        "One Usage per two levels",
        "1 round/level or 1 round",
        "Seven days or seven months",
        "Until triggered or broken",
        "Until expended or 10 min/level",
        "1 hour/level or until completed",
        "1 hour/level or until expended",
        "1 round/level or until all beams are exhausted",
        "Up to 1 round/level",
        "No more than 1 hour/level or until discharged (destination is "
        + "reached)",
     });

  //........................................................................
  //----- levels -----------------------------------------------------------

  /** The possible level measurements. */
  public static final String []LEVELS =
    Config.get("/game/levels", new String []
      {
        "level",
        "2 level",
        "3 level",
      });

  //........................................................................
  //----- spell durations --------------------------------------------------

  /** The possible spell duration. */
  public static final String []SPELL_DURATION_FLAGS =
    Config.get("/game/spell.duration.flags", new String []
      {
        "(D)",
      });

  //........................................................................
  //----- saving throws ----------------------------------------------------

  /** The possible spell saving throws (PHB p. 176/177). */
  public static final String []SAVING_THROWS =
    Config.get("/game/saving.throws", new String []
      {
        "Negates",
        "Partial",
        "Half",
        "None and Will Negates (Object)",
        "None or Will Negates (Harmless, Object)",
        "None or Will Negates (Harmless)",
        "None or Will Negates (Object)",
        "None or Will Negates",
        "None or Reflex Half",
        "None or Will disbelief (if interacted with)",
        "None",
        "No and Will Negates (Harmless)",
        "No",
        "Disbelief",
        "(Object)",
        "(Harmless)",
        "Will Disbelief (if interacted with) then Fortitude Partial",
        "Will Disbelief (if interacted with)",
        "Will Half (Harmless) or Will Half",
        "Will Half (Harmless)",
        "Will Half",
        "Will Negates (Harmless) or Will Negates (Harmless, Object)",
        "Will Negates (Harmless) or Will Negates (Object)",
        "Will Negates (Harmless, Object)",
        "Will Negates (Harmless)",
        "Will Negates (Object) Will Negates (Object) or Fortitude Half",
        "Will Negates (Object) or None",
        "Will Negates (Object)",
        "Will Negates (Blinding Only)",
        "Will Negates or Fortitude Negates",
        "Will Negates or None (Object)",
        "Will Negates or Will",
        "Will Negates",
        "Will Partial",
        "Fortitude Negates, Will Partial",
        "Fortitude Negates (Harmless)",
        "Fortitude Negates (Object)",
        "Fortitude Negates",
        "Fortitude Half",
        "Fortitude Partial or Will Negates",
        "Fortitude Partial or Reflex Negates (Object)",
        "Fortitude Partial (Object)",
        "Fortitude Partial",
        "Reflex Half or Reflex Negates",
        "Reflex Half",
        "Reflex Partial",
        "Reflex Negates (Object)",
        "Reflex Negates and Reflex Half",
        "Reflex Negates",
        "See Text",
      });

  //........................................................................
  //----- spell resistance -------------------------------------------------

  /** The possible spell resistances. */
  public static final String []SPELL_RESISTANCES =
    Config.get("/game/spell.resistance", new String []
      {
        "No and Yes (Object)",
        "No and Yes (Harmless)",
        "No and Yes",
        "No or Yes (Harmless)",
        "No or Yes (Object)",
        "No (object) and Yes",
        "No (harmless)",
        "No",
        "Yes or No (Object)",
        "Yes or No",
        "Yes (harmless) or Yes (Harmless, Object)",
        "Yes (harmless) or Yes",
        "Yes (harmless)",
        "Yes (object)",
        "Yes (harmless, object)",
        "Yes",
        "See Text",
      });

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------ BaseSpell -------------------------------

  /**
   * This is the internal, default constructor for an undefined value.
   *
   */
  protected BaseSpell()
  {
    super(TYPE);
  }

  //........................................................................
  //------------------------------ BaseSpell -------------------------------

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base item
   */
  public BaseSpell(@Nonnull String inName)
  {
    super(inName, TYPE);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final BaseType<BaseSpell> TYPE =
    new BaseType<BaseSpell>(BaseSpell.class);

  //----- school -----------------------------------------------------------

  /** The total standard value of the base item. */
  @Key("school")
  protected Multiple m_school = new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new EnumSelection<School>(School.class), false),
      new Multiple.Element(new ValueList<EnumSelection<Subschool>>
                           (new EnumSelection<Subschool>(Subschool.class), ","),
                           true, " (", ")"),
    });

  static
  {
    addIndex(new Index(Index.Path.SCHOOLS, "Schools", TYPE));
  }

  //........................................................................
  //----- summary ----------------------------------------------------------

  /** The summary text for the spell. */
  @Key("summary")
  protected LongFormattedText m_summary = new LongFormattedText();

  //........................................................................
  //----- descriptor -------------------------------------------------------

  /** The spell descriptor. */
  @Key("descriptor")
  protected ValueList<EnumSelection<Descriptor>> m_descriptor =
    new ValueList<EnumSelection<Descriptor>>
    (new EnumSelection<Descriptor>(Descriptor.class), ",");

  static
  {
    addIndex(new Index(Index.Path.DESCRIPTORS, "Descriptors", TYPE));
  }

  //........................................................................
  //----- level ------------------------------------------------------------

  /** The spell level. */
  @Key("level")
  protected ValueList<Multiple> m_level =
    new ValueList<Multiple>(new Multiple(new Multiple.Element []
      {
        new Multiple.Element
        (new EnumSelection<SpellClass>(SpellClass.class), false),
        new Multiple.Element(new Number(0, 9), false),
      }), ", ");

  static
  {
    addIndex(new Index(Index.Path.CLASSES, "Classes", TYPE));
    addIndex(new Index(Index.Path.LEVELS, "Levels", TYPE));
  }

  //........................................................................
  //----- components -------------------------------------------------------

  /** The various components. */
  @Key("components")
  protected ValueList<EnumSelection<Components>> m_components =
    new ValueList<EnumSelection<Components>>
    (new EnumSelection<Components>(Components.class), ", ");

  static
  {
    addIndex(new Index(Index.Path.COMPONENTS, "Components", TYPE));
  }

  //........................................................................
  //----- material ---------------------------------------------------------

  /** The specific material components. */
  @Key("material")
  protected ValueList<Multiple> m_material =
    new ValueList<Multiple>(new Multiple(new Multiple.Element []
      {
        new Multiple.Element(new Name(), false, null, ":"),
        new Multiple.Element(new ValueList<Text>(new Text(), ", "), false),
      }), ", ");

  static
  {
    addIndex(new Index(Index.Path.MATERIALS, "Materials", TYPE));
  }

  //........................................................................
  //----- focus ------------------------------------------------------------

  /** The specific material components. */
  @Key("focus")
  protected Multiple m_focus = new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new Name(), false, null, ":"),
      new Multiple.Element(new ValueList<Text>(new Text(), ", "), false),
    });

  static
  {
    addIndex(new Index(Index.Path.FOCUSES, "Focuses", TYPE));
  }

  //........................................................................
  //----- casting time -----------------------------------------------------

  /** The casting time required for this spell. */
  @Key("casting time")
  protected Duration m_castingTime = new Duration();

  static
  {
    addIndex(new Index(Index.Path.CASTING_TIMES, "Casting Times", TYPE));
  }

  //........................................................................
  //----- range ------------------------------------------------------------

  /** The range of the spell. */
  @Key("range")
  protected Union m_range =
    new Union(new EnumSelection<Range>(Range.class), new Distance())
    .withEditType("name");

  static
  {
    addIndex(new Index(Index.Path.RANGES, "Ranges", TYPE));
  }

  //........................................................................
  //----- effect -----------------------------------------------------------

  /** The target of the spell. */
  @Key("effect")
  protected Multiple m_effect = new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new Distance(), true),
      new Multiple.Element(new EnumSelection<Effect>(Effect.class), true),
      new Multiple.Element(new Text(), false),
    });

  static
  {
    addIndex(new Index(Index.Path.EFFECT_TYPES, "Effect Types", TYPE));
    addIndex(new Index(Index.Path.EFFECTS, "Effects", TYPE));
  }

  //........................................................................
  //----- target -----------------------------------------------------------

  /** The target of the spell. */
  @Key("target")
  protected Text m_target = new Text();

  //........................................................................
  //----- area -------------------------------------------------------------

  /** The area of the spell. */
  @Key("area")
  protected Text m_area = new Text();

  //........................................................................
  //----- duration ---------------------------------------------------------

  /** The duration of the spell. */
  @Key("duration")
  protected Multiple m_duration = new Multiple(new Multiple.Element []
    {
      new Multiple.Element
      (new Union
       (new Selection(SPELL_DURATIONS),
        new Multiple(new Multiple.Element []
          {
            new Multiple.Element(new Duration(), false),
            new Multiple.Element(new Selection(LEVELS), true, "/", null),
            new Multiple.Element(new Duration(), true, "+", null),
          })), false),
      new Multiple.Element(new Selection(SPELL_DURATION_FLAGS), true),
      new Multiple.Element(new Text(), true),
    }).withEditType("non-empty");

  static
  {
    addIndex(new Index(Index.Path.DURATIONS, "Durations", TYPE));
  }

  //........................................................................
  //----- saving throw -----------------------------------------------------

  /** The saving throw for the spell. */
  @Key("saving throw")
  protected Selection m_savingThrow = new Selection(SAVING_THROWS);

  static
  {
    addIndex(new Index(Index.Path.SAVING_THROWS, "Saving Throws", TYPE));
  }

  //........................................................................
  //----- spell resistance -------------------------------------------------

  /** The spell resistance for the spell. */
  @Key("spell resistance")
  protected Selection m_resistance = new Selection(SPELL_RESISTANCES);

  static
  {
    addIndex(new Index(Index.Path.SPELL_RESISTANCES, "Spell Resistances",
                       TYPE));
  }

  //........................................................................

  static
  {
    extractVariables(BaseSpell.class);
    extractVariables(BaseSpell.class, BaseIncomplete.class);
  }

  //----- special indexes --------------------------------------------------

  static
  {
    addIndex(new Index(Index.Path.WORLDS, "Worlds", TYPE));
    addIndex(new Index(Index.Path.REFERENCES, "References", TYPE));
    addIndex(new Index(Index.Path.EXTENSIONS, "Extensions", TYPE));
  }

  //........................................................................

  //........................................................................

  //----- commands ---------------------------------------------------------

  //----- print ------------------------------------------------------------

  /** The formatters to use for basic printing. */
  static
  {
    // TODO: reimplement
//     addCommand
//       (BaseSpell.class, PrintType.print,
//        new Command(new Object []
//          {
//            new Center(new Command(new Object []
//              {
//                new Icon(new Command(new Object []
//                  { "worlds/",
//                    new ValueCommand(PropertyKey.getKey("world")),
//                    ".png",
//                  }),
//                         new Command(new Object []
//                           { "world: ",
//                             new ValueCommand(PropertyKey.getKey("world")),
//                           }),
//                         new Command(new Object []
//                           { "../index/worlds/",
//                             new WordUpperCase
//                             (new ValueCommand(PropertyKey.getKey("world"))),
//                             ".html",
//                           }), true),
//                new Icon(new Command(new Object []
//                  { "schools/",
//                    new ValueCommand(SCHOOL),
//                    ".png",
//                  }),
//                         new Command(new Object []
//                           { "school: ",
//                             new AccessorCommand
//                             (new AccessorCommand.Accessor()
//                               {
//                                 public String get(ValueGroup inEntry)
//                                 {
//                                   Selection school =
//                                     (Selection)((BaseSpell)inEntry).m_school
//                                     .get(0).get();

//                                   Selection sub =
//                                     (Selection)((BaseSpell)inEntry).m_school
//                                     .get(1).get();

//                                   if(sub.isDefined())
//                                     return
//                                       school.toPrint() + " "
//                                       + sub.toPrint();
//                                   else
//                                     return
//                                       school.toPrint();
//                                 }
//                               }),
//                           }),
//                         new Command(new Object []
//                           { "../index/schools/",
//                             new AccessorCommand
//                             (new AccessorCommand.Accessor()
//                               {
//                                 public String get(ValueGroup inEntry)
//                                 {
//                                   Selection school =
//                                     (Selection)((BaseSpell)inEntry).m_school
//                                     .get(0).get();

//                                   Selection sub =
//                                     (Selection)((BaseSpell)inEntry).m_school
//                                     .get(1).get();

//                                   if(sub.isDefined())
//                                     return
//                                       school.getSelected()
//                                       + school.toString() + "-"
//                                       + sub.getSelected()
//                                       + sub.toString();
//                                   else
//                                     return
//                                       school.getSelected()
//                                       + school.toString();
//                                 }
//                               }),
//                             ".html",
//                           }), true),
//                new IfDefinedCommand(DESCRIPTOR,
//                                     new Icon(new Command(new Object []
//                                       { "descriptor/",
//                                         new ValueCommand(DESCRIPTOR),
//                                         ".png",
//                                       }),
//                                              new Command(new Object []
//                                                { "descriptor: ",
//                                                new ValueCommand(DESCRIPTOR),
//                                                }),
//                                              new Command(new Object []
//                                                { "../index/descriptors/",
//                                                new ValueCommand(DESCRIPTOR),
//                                                  ".html",
//                                                }), true), null),
//                // attachments
//                new AttachmentCommand(false),
//              })),
//            "\n",
//            new Divider("main", new Command(new Object []
//              {
//                new Title(new Command(new Object []
//                  {
//                    new AccessorCommand(new AccessorCommand.Accessor()
//                      {
//                        public String get(ValueGroup inEntry)
//                        {
//                          return ((BaseSpell)inEntry).getName();
//                        }
//                      }),
//                    new Linebreak(),
//                    new Tiny(new Link("(base spell)", "BaseSpells/index")),
//                  })),
//                "\n",
//                 new VariableCommand(PropertyKey.getKey("description")),
//                "\n",
//                new net.ixitxachitls.output.commands.
//                Files(new AccessorCommand(new AccessorCommand.Accessor()
//                  {
//                    public String get(ValueGroup inEntry)
//                    {
//                      return "BaseSpells/" + ((BaseSpell)inEntry).getName();
//                    }
//                  })),
//                new Table("description", "f" + "Illustrations: ".length()
//                          + ":L(desc-label);100:L(desc-text)", new Object []
//                  {
//                    BaseEntry.s_synonymLabel,
//                    BaseEntry.s_synonymCmd,
//                    new Window(new Bold("Level:"),
//                               Config.get
//                               ("resource:help/label.level",
//                                "This gives the spell's level, a number "
//                                + "between 0 and 9 that defines the spell's "
//                              + "relative power. This number is preceded by "
//                              + "the class whose members can cast the spell. "
//                                + "The Level entry also indicates whether a "
//                                + "spell is a domain spell and, if so, what "
//                              + "its domain and its level as a domain spell "
//                              + "are. A spell's level affects the DC for any "
//                                + "save allowed against the effect.")),
//                    new VariableCommand(LEVEL),
//                    new Window(new Bold("Components:"),
//                               Config.get
//                               ("resource:help/label.components",
//                              "A spell's components are what you must do or "
//                              + "possess to cast it. The Components entry in "
//                              + "a spell description includes abbreviations "
//                                + "that tell you what type of components it "
//                                + "has.\\par "
//                              + "\\bold{Verbal (V):} A verbal component is a "
//                                + "spoken incantation. To provide a verbal "
//                                + "component, you must be able to speak in a "
//                              + "strong voice. A \\Spell{silence} spell or a "
//                                + "gag spoils the incantation (and thus the "
//                              + "spell). A spellcaster who has been deafened "
//                              + "has a 20% chance to spoil any spell with a "
//                                + "verbal component that he or she tries to "
//                                + "cast.\\par "
//                              + "\\bold{Somatic (S):} A somatic component is "
//                                + "a measured and precise movement of the "
//                              + "hand. You must have at least one hand free "
//                                + "to provide a somatic component.\\par "
//                              + "\\bold{Material (M):} A material component "
//                                + "is one or more physical substances or "
//                                + "objects that are annihilated by the spell "
//                                + "energies in the casting process. Unless a "
//                              + "cost is given for a material component, the "
//                                + "cost is negligible. Don't bother to keep "
//                                + "track of material components with "
//                                + "negligible cost. Assume you have all you "
//                                + "need as long as you have your "
//                                + "\\Item{spell component pouch}.\\par "
//                                + "\\bold{Focus (F):} A focus component is a "
//                                + "prop of some sort. Unlike a material "
//                              + "component, a focus is not consumed when the "
//                                + "spell is cast and can be reused. As with "
//                                + "material components, the cost for a focus "
//                                + "is negligible unless a price is given. "
//                              + "Assume that focus components of negligible "
//                                + "cost are in your \\Item{spell component "
//                                + "pouch}.\\par "
//                                + "\\bold{Divine Focus (DF):} A divine focus "
//                                + "component is an item of spiritual "
//                              + "significance. The divine focus for a cleric "
//                                + "or a paladin is a holy symbol appropriate "
//                                + "to the character's faith. For an evil "
//                                + "cleric, the divine focus is an unholy "
//                                + "symbol. The default divine focus for a "
//                                + "druid or a ranger is a sprig of mistletoe "
//                                + "or holly.\\par "
//                                + "If the Components line includes F/DF or "
//                              + "M/DF, the arcane version of the spell has a "
//                                + "focus component or a material component "
//                              + "(the abbreviation before the slash) and the "
//                              + "divine version has a divine focus component "
//                                + "(the abbreviation after the slash).\\par "
//                              + "\\bold{XP Cost (XP):} Some powerful spells "
//                                + "(such as \\Spell{wish}, \\Spell{commune}, "
//                              + "and \\Spell{miracle}) entail an experience "
//                                + "point cost to you. No spell, not even "
//                                + "\\Spell{restoration}, can restore the XP "
//                                + "lost in this manner. You cannot spend "
//                                + "so much XP that you lose a level, so you "
//                                + "cannot cast the spell unless you have "
//                                + "enough XP to spare. However, you may, on "
//                                + "gaining enough XP to attain a new level, "
//                                + "use those XP for casting a spell rather "
//                                + "than keeping them and advancing a level. "
//                                + "The XP are treated just like a material "
//                                + "component---expended when you cast the "
//                                + "spell, whether or not the casting "
//                                + "succeeds.")),
//                    new VariableCommand(COMPONENTS),
//                    new IfDefinedCommand
//                    (MATERIAL,
//                     new Window(new Bold("Materials:"),
//                                Config.get
//                                ("resource:help/label.material",
//                                 "These are the specific material components "
//                               + "required for casting this spell.")), null),
//                    new IfDefinedCommand(MATERIAL,
//                                       new VariableCommand(MATERIAL), null),
//                    new IfDefinedCommand
//                    (FOCUS,
//                     new Window(new Bold("Focus:"),
//                                Config.get
//                                ("resource:help/label.focus",
//                                 "These are the specific foci "
//                               + "required for casting this spell.")), null),
//                    new IfDefinedCommand(FOCUS,
//                                         new VariableCommand(FOCUS), null),
//                    new Window(new Bold("Casting Time:"),
//                               Config.get
//                               ("resource:help/label.casting.time",
//                              "Most spells have a casting time of 1 standard "
//                              + "action. Others take 1 round or more, while "
//                                + "a few require only a free action.\\par "
//                                + "A spell that takes 1 round to cast is a "
//                                + "full-round action. It comes into effect "
//                                + "just before the beginning of your turn in "
//                                + "the round after you began casting the "
//                                + "spell. You then act normally after the "
//                                + "spell is completed.\\par "
//                                + "A spell that takes 1 minute to cast comes "
//                              + "into effect just before your turn 1 minute "
//                              + "later (and for each of those 10 rounds, you "
//                              + "are casting a spell as a full-round action, "
//                                + "just as noted above for 1-round casting "
//                                + "times). These actions must be consecutive "
//                                + "and uninterrupted, or the spell "
//                                + "automatically fails.\\par "
//                                + "When you begin a spell that takes 1 round "
//                                + "or longer to cast, you must continue the "
//                                + "concentration from the current round to "
//                              + "just before your turn in the next round (at "
//                                + "least). If you lose concentration before "
//                                + "the casting is complete, you lose the "
//                                + "spell.\\par "
//                                + "A spell with a casting time of 1 free "
//                              + "action (such as feather fall) doesn't count "
//                              + "against your normal limit of one spell per "
//                                + "round. However, you may cast such a spell "
//                              + "only once per round. Casting a spell with a "
//                                + "casting time of 1 free action doesn't "
//                                + "provoke attacks of opportunity.\\par "
//                                + "You make all pertinent decisions about a "
//                                + "spell (range, target, area, effect, "
//                              + "version, and so forth) when the spell comes "
//                                + "into effect. For example, when casting a "
//                              + "\\Spell{summon monster} spell, you need not "
//                              + "decide where you want the monster to appear "
//                              + "(or indeed, what monster you are summoning) "
//                                + "until the spell comes into effect in the "
//                                + "round after you begin casting.")),
//                    new VariableCommand(CASTING_TIME),
//                    new Window(new Bold("Range:"),
//                               Config.get
//                               ("resource:help/label.spell.range",
//                              "A spell's range indicates how far from you it "
//                              + "can reach, as defined in the Range entry of "
//                                + "the spell description. A spell's range is "
//                                + "the maximum distance from you that the "
//                                + "spell's effect can occur, as well as the "
//                              + "maximum distance at which you can designate "
//                              + "the spell's point of origin. If any portion "
//                                + "of the spell's area would extend beyond "
//                                + "this range, that area is wasted. Standard "
//                                + "ranges include the following.\\par "
//                                + "\\bold{Personal:} The spell affects only "
//                                + "you.\\par "
//                                + "\\bold{Touch:} You must touch a creature "
//                              + "or object to affect it. A touch spell that "
//                              + "deals damage can score a critical hit just "
//                              + "as a weapon can. A touch spell threatens a "
//                                + "critical hit on a natural roll of 20 and "
//                                + "deals double damage on a successful "
//                                + "critical hit. Some touch spells, such "
//                                + "as \\Spell{teleport} and \\Spell{water "
//                              + "walk}, allow you to touch multiple targets. "
//                                + "You can touch as many willing targets as "
//                                + "you can reach as part of the casting, but "
//                              + "all targets of the spell must be touched in "
//                              + "the same round that you finish casting the "
//                                + "spell.\\par "
//                              + "\\bold{Close:} The spell reaches as far as "
//                                + "25 feet away from you. The maximum range "
//                                + "increases by 5 feet for every two full "
//                              + "caster levels (30 feet at 2nd caster level, "
//                                + "35 feet at 4th caster level, and so on)."
//                                + "\\par "
//                              + "\\bold{Medium:} The spell reaches as far as "
//                                + "100 feet + 10 feet per caster level.\\par "
//                                + "\\bold{Long:} The spell reaches as far as "
//                                + "400 feet + 40 feet per caster level.\\par "
//                                + "\\bold{Unlimited:} The spell reaches "
//                                + "anywhere on the same plane of existence."
//                                + "\\par "
//                                + "\\bold{Range Expressed in Feet:} Some "
//                                + "spells have no standard range category, "
//                                + "just a range expressed in feet.")),
//                    new VariableCommand(RANGE),
//                    new IfDefinedCommand
//                    (EFFECT,
//                     new Window(new Bold("Effect:"),
//                                Config.get
//                                ("resource:help/label.effect",
//                               "Some spells, such as \\Spell{summon monster} "
//                                 + "spells, create or summon things rather "
//                                 + "than affecting things that are already "
//                                 + "present. You must designate the location "
//                                 + "where these things are to appear, either "
//                               + "by seeing it or defining it (for example, "
//                               + "'The \\Spell{insect plague} will appear 20 "
//                                 + "feet into the area of darkness that the "
//                                 + "\\Monster{nagas} are hiding in'. Range "
//                                 + "determines how far away an effect can "
//                                 + "appear, but if the effect is mobile "
//                               + "(a summoned monster, for instance), it can "
//                               + "move regardless of the spell's range.\\par "
//                                 + "\\bold{Ray:} Some effects are rays (for "
//                                 + "example, \\Spell{ray of enfeeblement}). "
//                               + "You aim a ray as if using a ranged weapon, "
//                               + "though typically you make a ranged touch "
//                               + "attack rather than a normal ranged attack. "
//                               + "As with a ranged weapon, you can fire into "
//                                 + "the dark or at an invisible creature and "
//                                + "hope you hit something. You don't have to "
//                                + "see the creature you're trying to hit, as "
//                               + "you do with a targeted spell. Intervening "
//                                 + "creatures and obstacles, however, can "
//                               + "block your line of sight or provide cover "
//                                 + "for the creature you're aiming at.\\par "
//                                 + "If a ray spell has a duration, it's the "
//                                 + "duration of the effect that the ray "
//                                 + "causes, not the length of time the ray "
//                                 + "itself persists.\\par "
//                               + "If a ray spell deals damage, you can score "
//                                 + "a critical hit just as if it were a "
//                                 + "weapon. A ray spell threatens a critical "
//                                 + "hit on a natural roll of 20 and deals "
//                                 + "double damage on a successful critical "
//                                 + "hit.\\par "
//                                 + "\\bold{Spread:} Some effects, notably "
//                                 + "clouds and fogs, spread out from a point "
//                                 + "of origin, which must be a grid "
//                               + "intersection. The effect can extend around "
//                               + "corners and into areas that you can't see. "
//                                 + "Figure distance by actual distance "
//                                 + "traveled, taking into account turns the "
//                                 + "spelleffect takes. When determining "
//                               + "distance for spread effects, count around "
//                               + "walls, not through them. As with movement, "
//                               + "do not trace diagonals across corners. You "
//                                 + "must designate the point of origin for "
//                               + "such an effect, but you need not have line "
//                                 + "of effect to all portions of the effect. "
//                                 + "Example: \\Spell{obscuring mist}.")),
//                     null),
//                    new IfDefinedCommand(EFFECT,
//                                         new VariableCommand(EFFECT), null),
//                    new IfDefinedCommand
//                    (TARGET,
//                     new Window(new Bold("Target:"),
//                                Config.get
//                                ("resource:help/label.target",
//                               "Some spells, such as \\Spell{charm person}, "
//                                 + "have a target or targets. You cast these "
//                               + "spells on creatures or objects, as defined "
//                                 + "by the spell itself. You must be able to "
//                                 + "see or touch the target, and you must "
//                                 + "specifically choose that target. For "
//                                 + "example, you can't fire a \\Spell{magic "
//                                 + "missile} spell (which always hits its "
//                                 + "target) into a group of bandits with the "
//                                 + "instruction to strike 'the leader'. To "
//                                 + "strike the leader, you must be able to "
//                                 + "identify and see the leader (or guess "
//                                 + "which is the leader and get lucky). "
//                                 + "However, you do not have to select your "
//                                 + "target until you finish casting the "
//                                 + "spell.\\par "
//                               + "If the target of a spell is yourself (the "
//                                 + "spell description has a line that reads "
//                               + "Target: You), you do not receive a saving "
//                                 + "throw, and spell resistance does not "
//                                 + "apply. The Saving Throw and Spell "
//                                 + "Resistance lines are omitted from such "
//                                 + "spells.\\par "
//                                 + "Some spells restrict you to willing "
//                                 + "targets only. Declaring yourself as a "
//                                 + "willing target is something that can be "
//                                 + "done at any time (even if you're "
//                                 + "flat-footed or it isnÂ¢t your turn). "
//                                 + "Unconscious creatures are automatically "
//                               + "considered willing, but a character who is "
//                                 + "conscious but immobile or helpless (such "
//                               + "as one who is bound, cowering, grappling, "
//                                 + "paralyzed, pinned, or stunned) is not "
//                                 + "automatically willing. Some spells, such "
//                                 + "as \\Spell{flaming sphere} and "
//                                 + "\\Spell{spiritual weapon}, allow you to "
//                                 + "redirect the effect to new targets or "
//                                 + "areas after you cast the spell. "
//                               + "Redirecting a spell is a move action that "
//                               + "does not provoke attacks of opportunity.")),
//                     null),
//                    new IfDefinedCommand(TARGET,
//                                         new VariableCommand(TARGET), null),
//                    new IfDefinedCommand
//                    (AREA,
//                     new Window(new Bold("Area:"),
//                                Config.get
//                                ("resource:help/label.area",
//                                 "Some spells affect an area. Sometimes a "
//                                 + "spell description specifies a specially "
//                                 + "defined area, but usually an area falls "
//                                 + "into one of the categories defined "
//                                 + "below.\\par "
//                                 + "Regardless of the shape of the area, you "
//                                 + "select the point where the spell "
//                                 + "originates, but otherwise you don't "
//                                 + "control which creatures or objects the "
//                                 + "spell affects. The point of origin of a "
//                               + "spell is always a grid intersection. When "
//                                 + "determining whether a given creature is "
//                               + "within the area of a spell, count out the "
//                                 + "distance from the point of origin in "
//                                 + "squares just as you do when moving a "
//                                 + "character or when determining the range "
//                                 + "for a ranged attack. The only difference "
//                                 + "is that instead of counting from the "
//                               + "center of one square to the center of the "
//                                 + "next, you count from intersection to "
//                                 + "intersection. You can count diagonally "
//                                 + "across a square, but remember that every "
//                                 + "second diagonal counts as 2 squares of "
//                                 + "distance. If the far edge of a square is "
//                                + "within the spellÂ¢s area, anything within "
//                               + "that square is within the spell's area. If "
//                                 + "the spell's area only touches the near "
//                               + "edge of a square, however, anything within "
//                                 + "that square is unaffected by the "
//                                 + "spell.\\par "
//                               + "\\bold{Burst, Emanation, or Spread:} Most "
//                                 + "spells that affect an area function as a "
//                               + "burst, an emanation, or a spread. In each "
//                                 + "case, you select the spell's point of "
//                                 + "origin and measure its effect from that "
//                                 + "point.\\par "
//                               + "A burst spell affects whatever it catches "
//                               + "in its area, even including creatures that "
//                                 + "you can't see. For instance, if you can "
//                                 + "designate a fourway intersection of "
//                                 + "corridors to be the point of origin of a "
//                                 + "dispel magic spell, the spell bursts in "
//                                 + "all four directions, possibly catching "
//                                + "creatures that you canÂ¢t see because "
//                               + "they're around the corner from you but not "
//                               + "from the point of origin. It can't affect "
//                               + "creatures with total cover from its point "
//                                 + "of origin (in other words, its effects "
//                               + "don't extend around corners). The default "
//                               + "shape for a burst effect is a sphere, but "
//                                 + "some burst spells are specifically "
//                                 + "described as coneshaped. A burst's area "
//                                 + "defines how far from the point of origin "
//                                 + "the spell's effect extends. Example: "
//                                 + "\\Spell{holy smite}.\\par "
//                               + "An emanation spell functions like a burst "
//                               + "spell, except that the effect continues to "
//                                 + "radiate from the point of origin for the "
//                               + "duration of the spell. Most emanations are "
//                               + "cones or spheres. Example: \\Spell{detect "
//                                 + "magic}.\\par "
//                                 + "A spread spell spreads out like a burst "
//                               + "but can turn corners. You select the point "
//                                 + "of origin, and the spell spreads out a "
//                                 + "given distance in all directions. Figure "
//                               + "the area the spell effect fills by taking "
//                                 + "intoaccount any turns the spell effect "
//                                 + "takes. Example: \\Spell{fireball}.\\par "
//                                 + "\bold{Cone, Cylinder, Line, or Sphere:} "
//                                 + "Most spells that affect an area have a "
//                                 + "particular shape, such as a cone, "
//                                 + "cylinder, line, or sphere.\\par "
//                                 + "A cone-shaped spell shoots away from you "
//                                 + "in a quarter-circle in the direction you "
//                                 + "designate. It starts from any corner of "
//                                 + "your square and widens out as it goes. "
//                                 + "Most cones are either bursts or "
//                               + "emanations (see above), and thus won't go "
//                                 + "around corners. Example: \\Spell{cone of "
//                                 + "cold}.\\par "
//                               + "When casting a cylinder-shaped spell, you "
//                                 + "select the spell's point of origin. This "
//                                 + "point is the center of a horizontal "
//                               + "circle, and the spell shoots down from the "
//                                 + "circle, filling a cylinder. A "
//                                 + "cylinder-shaped spell ignores any "
//                                 + "obstructions within its area. Example: "
//                                 + "\\Spell{flame strike}.\\par "
//                                 + "A line-shaped spell shoots away from you "
//                               + "in a line in the direction you designate. "
//                                 + "It starts from any corner of your square "
//                                 + "and extends to the limit of its range or "
//                                 + "until it strikes a barrier that blocks "
//                                 + "line of effect. A line-shaped spell "
//                               + "affects all creatures in squares that the "
//                                 + "line passes through. Example: "
//                                 + "\\Spell{lightning bolt}.\\par "
//                                 + "A sphere-shaped spell expands from its "
//                               + "point of origin to fill a spherical area. "
//                                 + "Spheres may be bursts, emanations, or "
//                                 + "spreads. Example: \\Spell{globe of "
//                                 + "invulnerability}.\\par "
//                               + "\\bold{Creatures:} A spell with this kind "
//                               + "of area affects creatures directly (like a "
//                                 + "targeted spell), but it affects all "
//                                 + "creatures in an area of some kind rather "
//                               + "than individual creatures you select. The "
//                                 + "area might be a spherical burst (such as "
//                                 + "sleep), a cone-shaped burst (such as "
//                                 + "fear), or some other shape.\\par "
//                                 + "Many spells affect 'living creatures', "
//                                 + "which means all creatures other than "
//                               + "constructs and undead. The \\Spell{sleep} "
//                                 + "spell, for instance, affects only living "
//                                 + "creatures. If you cast \\Spell{sleep} in "
//                                 + "the midst of \\Monster{gnolls} and "
//                                 + "\\Monster{skeletons}, the \\Spell{sleep} "
//                               + "spell ignores the \\Monster{skeletons} and "
//                                 + "affects the \\Monster{gnolls}. The "
//                               + "\\Monster{skeletons} do not count against "
//                                 + "the creatures affected.\\par "
//                                 + "\\bold{Objects:} A spell with this kind "
//                               + "of area affects objects within an area you "
//                                 + "select (as Creatures, but affecting "
//                                 + "objects instead).\\par "
//                                 + "\\bold{Other:} A spell can have a unique "
//                               + "area, as defined in its description.\\par "
//                                 + "\\bold{(S) Shapeable:} If an Area or "
//                                 + "Effect entry ends with '(S)', you can "
//                                 + "shape the spell. A shaped effect or area "
//                                 + "can have no dimension smaller than 10 "
//                                 + "feet. Many effects or areas are given as "
//                                 + "cubes to make it easy to model irregular "
//                               + "shapes. Three-dimensional volumes are most "
//                                 + "often needed to define aerial or "
//                                 + "underwater effects and areas.\\par "
//                                 + "\\bold{Line of Effect:} A line of effect "
//                                 + "is a straight, unblocked path that "
//                               + "indicates what a spell can affect. A line "
//                               + "of effect is canceled by a solid barrier. "
//                                 + "It's like line of sight for ranged "
//                                 + "weapons, except that it's not blocked by "
//                                 + "fog, darkness, and other factors that "
//                                 + "limit normal sight.\\par "
//                                 + "You must have a clear line of effect to "
//                               + "any target that you cast a spell on or to "
//                                 + "any space in which you wish to create an "
//                               + "effect (such as conjuring a monster). You "
//                                 + "must have a clear line of effect to the "
//                                 + "point of origin of any spell you cast, "
//                               + "such as the center of a fireball. A burst, "
//                               + "cone, cylinder, or emanation spell affects "
//                                 + "only an area, creatures, or objects to "
//                                 + "which it has line of effect from its "
//                               + "origin (a spherical burstÂ¢s center point, "
//                                 + "a cone-shaped burstÂ¢s starting point, a "
//                               + "cylinder's circle, or an emanation's point "
//                                 + "of origin).\\par "
//                               + "An otherwise solid barrier with a hole of "
//                               + "at least 1 square foot through it does not "
//                                 + "block a spell's line of effect. Such an "
//                                 + "opening means that the 5-foot length of "
//                                 + "wall containing the hole is no longer "
//                                 + "considered a barrier for purposes of a "
//                                 + "spell's line of effect.")),
//                     null),
//                    new IfDefinedCommand(AREA,
//                                         new VariableCommand(AREA), null),
//                    new Window(new Bold("Duration:"),
//                               Config.get
//                               ("resource:help/label.spell.duration",
//                                "A spell's Duration entry tells you how long "
//                              + "the magical energy of the spell lasts.\\par "
//                              + "\\bold{Timed Durations:} Many durations are "
//                              + "measured in rounds, minutes, hours, or some "
//                                + "other increment. When the time is up, the "
//                                + "magic goes away and the spell ends. If a "
//                              + "spell's duration is variable (\\Spell{power "
//                                + "word stun}, for example) the DM rolls it "
//                                + "secretly.\\par "
//                                + "\\bold{Instantaneous:} The spell energy "
//                                + "comes and goes the instant the spell is "
//                                + "cast, though the consequences might be "
//                                + "long-lasting. For example, a \\Spell{cure "
//                               + "light wounds} spell lasts only an instant, "
//                                + "but the healing it bestows never runs out "
//                                + "or goes away.\\par "
//                                + "\\bold{Permanent:} The energy remains as "
//                                + "long as the effect does. This means the "
//                                + "spell is vulnerable to dispel magic. "
//                                + "Example: \\Spell{secret page}.\\par "
//                                + "\\bold{Concentration:} The spell lasts as "
//                                + "long as you concentrate on it. "
//                                + "Concentrating to maintain a spell is a "
//                                + "standard action that does not provoke "
//                              + "attacks of opportunity. Anything that could "
//                                + "break your concentration when casting a "
//                                + "spell can also break your concentration "
//                                + "while you're maintaining one, causing the "
//                              + "spell to end. You can't cast a spell while "
//                              + "concentrating on another one. Sometimes "
//                                + "a spell lasts for a short time after you "
//                              + "cease concentrating. For example, the spell "
//                              + "\\Spell{hypnotic pattern} has a duration of "
//                                + "concentration + 2 rounds. In such a case, "
//                              + "the spell keeps going for the given length "
//                                + "of time after you stop concentrating, but "
//                              + "no longer. Otherwise, you must concentrate "
//                                + "to maintain the spell, but you can't "
//                              + "maintain it for more than a stated duration "
//                                + "in any event. If a target moves out of "
//                                + "range, the spell reacts as if your "
//                                + "concentration had been broken.\\par "
//                                + "\\bold{Subjects, Effects, and Areas:} If "
//                                + "the spell affects creatures directly (for "
//                              + "example, \\Spell{charm person}), the result "
//                                + "travels with the subjects for the spell's "
//                                + "duration. If the spell creates an effect, "
//                                + "the effect lasts for the duration. The "
//                              + "effect might move (for example, a summoned "
//                              + "monster might chase your enemies) or remain "
//                                + "still. Such an effect can be destroyed "
//                                + "prior to when its duration ends (for "
//                                + "example, \\Spell{fog cloud} can be "
//                              + "dispersed by wind). If the spell affects an "
//                                + "area, as \\Spell{silence} does, then the "
//                                + "spell stays with that area for its "
//                                + "duration. Creatures become subject to the "
//                                + "spell when they enter the area and are no "
//                              + "longer subject to it when they leave.\\par "
//                              + "\\bold{Touch Spells and Holding the Charge:}"
//                                + " In most cases, if you don't discharge a "
//                                + "touch spell on the round you cast it, you "
//                              + "can hold the charge (postpone the discharge "
//                                + "of the spell) indefinitely. You can make "
//                                + "touch attacks round after round. If you "
//                                + "cast another spell, the touch spell "
//                                + "dissipates. Some touch spells, such as "
//                              + "\\Spell{teleport} and \\Spell{water walk}, "
//                                + "allow you to touch multiple targets as "
//                                + "part of the spell. You can't hold the "
//                              + "charge of such a spell; you must touch all "
//                              + "targets of the spell in the same round that "
//                                + "you finish casting the spell.\\par "
//                                + "\\bold{Discharge:} Occasionally a spells "
//                              + "lasts for a set duration or until triggered "
//                              + "or discharged. For instance, \\Spell{magic "
//                              + "mouth} waits until triggered, and the spell "
//                                + "ends once the mouth has said its "
//                                + "message.\\par "
//                                + "\\bold{(D) Dismissible:} If the Duration "
//                                + "line ends with '(D)', you can dismiss the "
//                              + "spell at will. You must be within range of "
//                              + "the spell's effect and must speak words of "
//                                + "dismissal, which are usually a modified "
//                                + "form of the spell's verbal component. If "
//                              + "the spell has no verbal component, you can "
//                                + "dismiss the effect with a gesture. "
//                                + "Dismissing a spell is a standard action "
//                                + "that does not provoke attacks of "
//                                + "opportunity. A spell that depends on "
//                                + "concentration is dismissible by its very "
//                              + "nature, and dismissing it does not take an "
//                              + "action, since all you have to do to end the "
//                                + "spell is to stop concentrating on your "
//                                + "turn.")),
//                    new VariableCommand(DURATION),
//                    new Window(new Bold("Saving Throw:"),
//                               Config.get
//                               ("resource:help/label.saving_throw",
//                                "Usually a harmful spell allows a target to "
//                              + "make a saving throw to avoid some or all of "
//                                + "the effect. The Saving Throw entry in a "
//                                + "spell description defines which type of "
//                              + "saving throw the spell allows and describes "
//                                + "how saving throws against the spell "
//                                + "work.\\par "
//                              + "\\bold{Negates:} The spell has no effect on "
//                                + "a subject that makes a successful saving "
//                                + "throw.\\par "
//                              + "\\bold{Partial:} The spell causes an effect "
//                              + "on its subject, such as death. A successful "
//                              + "saving throw means that some lesser effect "
//                                + "occurs (such as being dealt damage rather "
//                                + "than being killed).\\par "
//                              + "\\bold{Half:} The spell deals damage, and a "
//                                + "successful saving throw halves the damage "
//                                + "taken (round down).\\par "
//                                + "\\bold{None:} No saving throw is "
//                                + "allowed.\\par "
//                                + "\\bold{Disbelief:} A successful save lets "
//                                + "the subject ignore the effect.\\par "
//                              + "\\bold{(object):} The spell can be cast on "
//                                + "objects, which receive saving throws only "
//                              + "if they are magical or if they are attended "
//                                + "(held, worn, grasped, or the like) by a "
//                              + "creature resisting the spell, in which case "
//                              + "the object uses the creature's saving throw "
//                                + "bonus unless its own bonus is greater. "
//                                + "(This notation does not mean that a spell "
//                              + "can be cast only on objects. Some spells of "
//                                + "this sort can be cast on creatures or "
//                                + "objects.) A magic item's saving throw "
//                              + "bonuses are each equal to 2 + one-half the "
//                                + "item's caster level.\\par "
//                                + "\\bold{(harmless):} The spell is usually "
//                                + "beneficial, not harmful, but a targeted "
//                              + "creature can attempt a saving throw if it :"
//                                + "desires.\\par "
//                                + "\\bold{Saving Throw Difficulty Class:} A "
//                              + "saving throw against your spell has a DC of "
//                                + "10 + the level of the spell + your bonus "
//                              + "for the relevant ability (Intelligence for "
//                                + "a \\Class{wizard}, Charisma for a \\Class"
//                                + "{sorcerer} or \\Class{bard}, or Wisdom "
//                                + "for a \\Class{cleric}, \\Class{druid}, "
//                                + "\\Class{paladin}, or \\Class{ranger}). A "
//                                + "spell's level can vary depending on your "
//                              + "class. For example, a \\Spell{fire trap} is "
//                              + "a 2ndlevel spell for a \\Class{druid} but a "
//                              + "4th-level spell for a \\Class{sorcerer} or "
//                              + "\\Class{wizard}. Always use the spell level "
//                                + "applicable to your class. \\par "
//                                + "\\bold{Succeeding on a Saving Throw:} A "
//                              + "creature that successfully saves against a "
//                              + "spell that has no obvious physical effects "
//                                + "feels a hostile force or a tingle, but "
//                                + "cannot deduce the exact nature of the "
//                                + "attack. For example, if you secretly cast "
//                              + "\\Spell{charm person} on a creature and its "
//                                + "saving throw succeeds, it knows that "
//                              + "someone used magic against it, but it can't "
//                              + "tell what you were trying to do. Likewise, "
//                                + "if a creature's saving throw succeeds "
//                                + "against a targeted spell, such as "
//                                + "\\Spell{charm person}, you sense that the "
//                                + "spell has failed. You do not sense when "
//                                + "creatures succeed on saves against effect "
//                                + "and area spells.\\par "
//                              + "\\bold{Automatic Failures and Successes:} A "
//                              + "natural 1 (the d20 comes up 1) on a saving "
//                                + "throw is always a failure, and the spell "
//                                + "may cause damage to exposed items (see "
//                              + "below). A natural 20 (the d20 comes up 20) "
//                                + "is always a success.\\par "
//                                + "\\bold{Voluntarily Giving up a Saving "
//                              + "Throw:} A creature can voluntarily forego a "
//                              + "saving throw and willingly accept a spell's "
//                                + "result. Even a character with a special "
//                                + "resistance to magic (for example, an "
//                                + "\\Monster{elf}'s resistance to "
//                                + "\\Spell{sleep} effects) can suppress this "
//                                + "quality.\\par "
//                                + "\\emph{Items Affected by Magical Attacks "
//                              + "(in order of most likely to least likely to "
//                                + "be affected)} "
//                                + "\\table{1:L;100:L}"
//                                + "{\\bold{Order}}{\\bold{Item}}{1st}{Shield}"
//                                + "{2nd}{Armor}{3rd}{Magic helmet, hat, or "
//                                + "headband}{4th}{Item in hand (including "
//                                + "weapon, wand, or the like)}{5th}"
//                                + "{Magic cloak}{6th}{Stowed or sheathed "
//                                + "weapon}{7th}{Magic bracers}{8th}"
//                                + "{Magic clothing}{9th}{Magic jewelry "
//                                + "(including rings)}{10th}{Anything else} "
//                                + "\\bold{Items Surviving after a Saving "
//                              + "Throw:} Unless the descriptive text for the "
//                                + "spell specifies otherwise, all items "
//                                + "carried or worn by a creature are assumed "
//                              + "to survive a magical attack. If a creature "
//                                + "rolls a natural 1 on its saving throw "
//                                + "against the effect, however, an exposed "
//                                + "item is harmed (if the attack can harm "
//                                + "objects), see above. Determine which four "
//                                + "objects carried or worn by the creature "
//                                + "are most likely to be affected and roll "
//                                + "randomly among them. The randomly "
//                                + "determined item must make a saving throw "
//                                + "against the attack form and take whatever "
//                                + "damage the attack deal (see "
//                                + "\\Product{Player's Handbook}, page 165). "
//                                + "For instance, \\NPC{Tordek} is hit by a "
//                              + "\\Spell{lightning bolt} and gets a natural "
//                                + "1 on his saving throw. The items of his "
//                                + "most likely to have been affected are his "
//                                + "\\Item{shield}, his armor, his "
//                                + "\\Item{waraxe} (in his hand), and his "
//                              + "\\Item{shortbow} (stowed). (He doesn't have "
//                                + "magic headgear or a magic cloak, so those "
//                                + "entries are skipped.)  If an item is not "
//                              + "carried or worn and is not magical, it does "
//                               + "not get a saving throw. It simply is dealt "
//                                + "the appropriate damage.")),
//                    new VariableCommand(SAVING_THROW),
//                    new Window(new Bold("Spell Resistance:"),
//                               Config.get
//                               ("resource:help/label.spell.resistance",
//                                "Spell resistance is a special defensive "
//                              + "ability. If your spell is being resisted by "
//                              + "a creature with spell resistance, you must "
//                                + "make a caster level check (1d20 + caster "
//                                + "level) at least equal to the creature's "
//                                + "spell resistance for the spell to affect "
//                                + "that creature. The defender's spell "
//                                + "resistance is like an Armor Class against "
//                                + "magical attacks. The \\Product{Dungeon "
//                                + "Master's Guide} has more details on spell "
//                              + "resistance. Include any adjustments to your "
//                                + "caster level (such as from domain granted "
//                                + "powers) to this caster level check.\\par "
//                                + "The Spell Resistance entry and the "
//                                + "descriptive text of a spell description "
//                               + "tell you whether spell resistance protects "
//                                + "creatures from the spell. In many cases, "
//                                + "spell resistance applies only when a "
//                                + "resistant creature is targeted by the "
//                                + "spell, not when a resistant creature "
//                                + "encounters a spell that is already in "
//                                + "place.\\par "
//                              + "The terms 'object' and 'harmless' mean the "
//                              + "same thing for spell resistance as they do "
//                                + "for saving throws. A creature with spell "
//                                + "resistance must voluntarily lower the "
//                              + "resistance (a standard action) in order to "
//                                + "be affected by a spell noted as harmless. "
//                              + "In such a case, you do not need to make the "
//                                + "caster level check described above.")),
//                    new VariableCommand(RESISTANCE),
//                    new Window(new Bold("Short Description:"),
//                               Config.get
//                               ("resource:help/label.short.description",
//                              "This is the short description of the spell.")),
//                    new VariableCommand(PropertyKey.getKey
//                                        ("short description")),
//                    new Skip(new AttachmentCommand(true)),
//                    new Window(new Bold("References:"),
//                               Config.get
//                               ("resource:help/label.references",
//                                "These are the products that mention "
//                                + "this entry. These references were used "
//                                + "to create the statistics for this "
//                                + "entry.")),
//                    new VariableCommand(PropertyKey.getKey("references")),
//                    BaseEntry.s_errorLabel,
//                    BaseEntry.s_errorCmd,
//                  }),
//                new Divider("clear", ""),
//              })),
//          new Nopictures(new Table(new int [] { -"Illustrations: ".length(),
//                                                  100 },
//                                     new Document.Alignment []
//              { Document.Alignment.left, Document.Alignment.left }, null,
//                                     new Object []
//              {
//                new Bold("World:"),
//                new VariableCommand(PropertyKey.getKey("world")),
//                new Bold("School:"),
//                new VariableCommand(SCHOOL),
//                new IfDefinedCommand(DESCRIPTOR,
//                                     new Command(new Object []
//                                       {
//                                         " [",
//                                         new VariableCommand(DESCRIPTOR),
//                                         "]",
//                                       }), null),
//              })),
//            "\n",
//          }));
  }

  //........................................................................
  //----- short ------------------------------------------------------------

  /** The commands for short printing. */
  static
  {
//     addCommand
//       (BaseSpell.class, PrintType.brief,
//        new Command(new Object []
//          {
//            new Title(new Command(new Object []
//              {
//                new AccessorCommand(new AccessorCommand.Accessor()
//                  {
//                    public String get(ValueGroup inEntry)
//                    {
//                      return ((BaseSpell)inEntry).getName();
//                    }
//                  }),
//                " (",
//                new VariableCommand(PropertyKey.getKey("synonyms")),
//                ")",
//              }), "label"),
//            "\n\n",
//            new Label("World"),
//            new VariableCommand(PropertyKey.getKey("world")),
//            new Label("School"),
//            new VariableCommand(SCHOOL, true, true),
//            new IfDefinedCommand(DESCRIPTOR,
//                                 new Command(new Object []
//                                   {
//                                     " [",
//                                     new VariableCommand(DESCRIPTOR),
//                                     "]",
//                                   }), null),
//            new Label("Level"),
//            new VariableCommand(LEVEL, true, true),
//            new Label("Components"),
//            new VariableCommand(COMPONENTS, true, true),
//            new Label("Range"),
//            new VariableCommand(RANGE),
//            new IfDefinedCommand(EFFECT,
//                                 new Command(new Object []
//                                   {
//                                     new Label("Effect"),
//                                     new VariableCommand(EFFECT),
//                                   }), null),
//            new IfDefinedCommand(TARGET,
//                                 new Command(new Object []
//                                   {
//                                     new Label("Target"),
//                                     new VariableCommand(TARGET),
//                                   }), null),
//            new IfDefinedCommand(AREA,
//                                 new Command(new Object []
//                                   {
//                                     new Label("Area"),
//                                     new VariableCommand(AREA),
//                                   }), null),
//            "\n\n",
//            new Label("Duration"),
//            new VariableCommand(DURATION),
//            new Label("Saving Throw"),
//            new VariableCommand(SAVING_THROW),
//            new Label("Spell Resistance"),
//            new VariableCommand(RESISTANCE),
//            new VariableCommand(PropertyKey.getKey("short description")),
//            "\n",
//          }));
  }

  //........................................................................
  //----- reference --------------------------------------------------------

  /** Command for printing a reference. */
  static
  {
//     addCommand
//       (BaseSpell.class, PrintType.reference,
//        new Command(new Object []
//          {
//            new Label("Base Spell"),
//            new Link(new AccessorCommand(new AccessorCommand.Accessor()
//              {
//                public String get(ValueGroup inEntry)
//                {
//                  return ((BaseSpell)inEntry).getName();
//                }
//              }), new Command(new Object []
//                { "BaseSpells/",
//                  new AccessorCommand(new AccessorCommand.Accessor()
//                    {
//                      public String get(ValueGroup inEntry)
//                      {
//                        return ((BaseSpell)inEntry).getName();
//                      }
//                    }),
//                })),
//            new VariableCommand(PropertyKey.getKey("short description")),
//            new Command(new Object []
//              {
//                new VariableCommand(SCHOOL, true, true),
//                new IfDefinedCommand(DESCRIPTOR,
//                                     new Command(new Object []
//                                       {
//                                         " [",
//                                         new VariableCommand(DESCRIPTOR),
//                                         "]",
//                                       }), null),
//              }),
//            new VariableCommand(LEVEL, true, true),
//            //new VariableCommand(COMPONENTS, true, true),
//            //new VariableCommand(RANGE),
//            //new IfDefinedCommand(EFFECT, new VariableCommand(EFFECT),
//            //                     new IfDefinedCommand(TARGET,
//            //                     new VariableCommand(TARGET),
//            //                     new VariableCommand(AREA))),
//            //new VariableCommand(DURATION),
//            //new VariableCommand(SAVING_THROW),
//            //new VariableCommand(RESISTANCE),
//            new VariableCommand(PropertyKey.getKey("world")),
//          }));

//     addCommand(BaseSpell.class, PrintType.referenceFormat,
//                new Command("1:L(icon);"
//                            + "1:L(name)[Name];"
//                            + "1:L(short)[Short Description];"
//                            + "1:L(school)[School];"
//                            + "1:L(level)[Level];"
//                            //+ "1:L(components)[Comp.];"
//                            //+ "1:L(range)[Range];"
//                            //+ "1:L(effect)[Eff/Trg/Area];"
//                            //+ "1:L(duration)[Duration];"
//                            //+ "1:L(save)[ST];"
//                            //+ "1:L(resistance)[SR];"
//                            + "1:L(world)[World];"));
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getSummary ------------------------------

  /**
   * Get a summary for the entry.
   *
   * @param       inParameters optional parametrs to further specifiy the entry
   *                           (for spells the first argument is the class and
   *                            level and the second is the dc)
   *
   * @return      the string with the summary
   *
   */
  public @Nonnull String getSummary(@Nonnull String ... inParameters)
  {
    SpellClass kind = null;
    int level = 1;
    int dc = 10;

    if(inParameters.length > 0)
    {
      String []words = inParameters[0].split(" ");
      try
      {
        kind = SpellClass.valueOf(words[0]);
      }
      catch(IllegalArgumentException e)
      {
        Log.warning("cannot convert spell class '" + words[0] + ": " + e);
      }

      try
      {
        level = Integer.parseInt(words[1]);
      }
      catch(NumberFormatException e)
      {
        Log.warning("Cannot convert spell level '" + words[1] + "': " + e);
      }
    }

    try
    {
      if(inParameters.length > 1)
        dc = Integer.parseInt(inParameters[1]);
    }
    catch(NumberFormatException e)
    {
      Log.warning("Cannot convert spell dc '" + inParameters[1] + "': " + e);
    }

    return getSummary(kind, level, dc);
  }

  //........................................................................
  //------------------------------ getSummary ------------------------------

  /**
   * Get the summary of the spell description, with appropriate level and
   * other computations done.
   *
   * @param       inKind  the kind of spellcaster
   * @param       inLevel the spell level to compute for
   * @param       inDC    the DC for the spell to compute
   *
   * @return      a string with the summary
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull String getSummary(@Nullable SpellClass inKind, int inLevel,
                                    int inDC)
  {
    String summary = m_summary.get();

    if(summary == null)
      return "";

    int level = 0;

    for(Multiple levelData : m_level)
    {
      if(((EnumSelection<SpellClass>)levelData.get(0)).getSelected() == inKind)
      {
        level = (int)((Number)levelData.get(1)).get();

        break;
      }
    }

    int dc = inDC + level;

    // fill in dynamic values and replace expressions
    return computeExpressions
      (summary, new Parameters()
       .with("level", new Number(inLevel, 0, 100), Parameters.Type.ADD)
       .with("dc", new Number(dc, -100, 100), Parameters.Type.MAX));
  }

  //........................................................................
  //------------------------------ getSummary ------------------------------

  /**
   * Get a summary for the entry, using the given parameters.
   *
   * @param       inParameters
   *
   * @return      the string with the summary
   *
   */
  public @Nonnull String getSummary(@Nullable Parameters inParameters)
  {
    String summary = m_summary.get();

    if(summary == null || summary.isEmpty())
      return "(no summary)";

    if(inParameters == null || !inParameters.isDefined())
      return summary;

    summary = computeExpressions(summary, inParameters);

    Value notes = inParameters != null ? inParameters.getValue("Notes") : null;
    if(notes != null)
      summary += " (" + notes + ")";

    return summary;
  }

  //........................................................................
  //-------------------------- computeIndexValues --------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @return      a multi map of values per index name
   *
   */
  @SuppressWarnings("unchecked")
  @Override
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

    values.put(Index.Path.SCHOOLS, m_school.get(0).group());
    values.put(Index.Path.DESCRIPTORS, m_descriptor.group());
    values.put(Index.Path.CASTING_TIMES, m_castingTime.group());
    values.put(Index.Path.RANGES, m_range.group());
    values.put(Index.Path.EFFECT_TYPES, m_effect.get(1).group());
    values.put(Index.Path.DURATIONS, m_duration.get(0).group());
    values.put(Index.Path.SAVING_THROWS, m_savingThrow.group());
    values.put(Index.Path.SPELL_RESISTANCES, m_resistance.group());
    values.put(Index.Path.FOCUSES, m_focus.get(0).group());

    for(Multiple level : m_level)
    {
      values.put(Index.Path.LEVELS, level.get(1).group());
      values.put(Index.Path.CLASSES, level.get(0).group());
    }

    for(EnumSelection<Components> component : m_components)
      values.put(Index.Path.COMPONENTS, component.group());

    for(Multiple material : m_material)
      values.put(Index.Path.MATERIALS, material.group());

    for(EnumSelection<Subschool> subschool :
          (ValueList<EnumSelection<Subschool>>)m_school.get(1))
      values.put(Index.Path.SUBSCHOOLS, subschool.group());

    return values;
  }

  //........................................................................
  //--------------------------------- isDM ---------------------------------

  /**
   * Check whether the given user is the DM for this entry. Every user is a DM
   * for a base campaign.
   *
   * @param       inUser the user accessing
   *
   * @return      true for DM, false for not
   *
   */
  @Override
  public boolean isDM(@Nonnull BaseCharacter inUser)
  {
    return inUser.hasAccess(BaseCharacter.Group.ADMIN);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test.
   *
   * @hidden
   *
   */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- createBaseSpell() ----------------------------------------------

    /** Create a typical base item for testing purposes.
     *
     * @return the newly created base item
     *
     */
    public static AbstractEntry createBaseSpell()
    {
      ParseReader reader =
        new ParseReader(new java.io.StringReader(s_text), "test");

      return BaseSpell.read(reader);
    }

    //......................................................................

    //----- text -----------------------------------------------------------

    /** Test text. */
    private static String s_text =
      "#----- Acid Fog -----------------------------------------------\n"
      + "\n"
      + "base spell Acid Fog =\n"
      + "\n"
      + "  synonyms          \"Fog, Acid\";\n"
      + "  school            Conjuration (Creation);\n"
      + "  descriptor        acid;\n"
      + "  level             Sorcerer 6, Wizard 6, Water 7;\n"
      + "  components        V, S, M/DF;\n"
      + "  casting time      1 standard action;\n"
      + "  range             medium;\n"
      + "  effect            20 ft spread Fog;\n"
      + "  material          combined : \"dried, powdered peas (pinch)\",\n"
      + "                    \"powdered animal hoof\";\n"
      + "  casting time      1 standard action;\n"
      + "  duration          1 round/level;\n"
      + "  saving throw      None;\n"
      + "  spell resistance  No;\n"
      + "  world             generic;\n"
      + "  references        \"WTC 17524\" 196;\n"
      + "  short description \"Fog deals acid damage.\";\n"
      + "  description       \n"
      + "\n"
      + "  \"\\Spell{Acid fog} creates a billowing mass of misty vapors "
      + "similar to that\n"
      + "  produced by a \\Spell{solid fog} spell. In addition to slowing "
      + "creatures down\n"
      + "  and obscuring sight, this spell's vapors are highly acidic. Each "
      + "round on\n"
      + "  your turn, starting when you cast the spell, the fog deals 2d6 "
      + "points of acid\n"
      + "  damage to each creature and object within it.\".\n"
      + "\n"
      + "#..............................................................\n";

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Test reading. */
    public void testRead()
    {
      //net.ixitxachitls.util.logging.Log.add("out",
      //                                      new net.ixitxachitls.util.logging
      //                                      .ANSILogger());

      String result =
        "#----- Acid Fog -----------------------------------------------\n"
        + "\n"
        + "base spell Acid Fog =\n"
        + "\n"
        + "  school            Conjuration (Creation);\n"
        + "  descriptor        Acid;\n"
        + "  level             Sorcerer 6, Wizard 6, Water 7;\n"
        + "  components        Verbose, Somatic, Material/Divine Focus;\n"
        + "  material          combined: \"dried, powdered peas (pinch)\", "
        + "\"powdered animal hoof\";\n"
        + "  casting time      1 standard action;\n"
        + "  range             Medium;\n"
        + "  effect            20 ft Spread Fog;\n"
        + "  duration          1 round/level;\n"
        + "  saving throw      None;\n"
        + "  spell resistance  No;\n"
        + "  synonyms          \"Fog, Acid\";\n"
        + "  world             Generic;\n"
        + "  references        \"WTC 17524\" 196;\n"
        + "  short description \"Fog deals acid damage.\";\n"
        + "  description       "
        + "\"\\Spell{Acid fog} creates a billowing mass of misty vapors "
        + "similar to that\n"
        + "  produced by a \\Spell{solid fog} spell. In addition to slowing "
        + "creatures down\n"
        + "  and obscuring sight, this spell's vapors are highly acidic. Each "
        + "round on\n"
        + "  your turn, starting when you cast the spell, the fog deals 2d6 "
        + "points of acid\n"
        + "  damage to each creature and object within it.\".\n"
        + "\n"
        + "#..............................................................\n";

      AbstractEntry entry = createBaseSpell();

      //System.out.println("read entry:\n'" + entry + "'");

      assertNotNull("base item should have been read", entry);
      assertEquals("base item name does not match", "Acid Fog",
                   entry.getName());
      assertEquals("base item does not match", result, entry.toString());
    }

    //......................................................................
  }

  //........................................................................
}
