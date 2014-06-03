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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseSpellProto;
import net.ixitxachitls.dma.proto.Values.SharedProto;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.Duration;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.LongFormattedText;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Parameters;
import net.ixitxachitls.dma.values.Selection;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.Union;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.Strings;
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

@ParametersAreNonnullByDefault
public class BaseSpell extends BaseEntry
{
  //----------------------------------------------------------------- nested

  //----- schools ----------------------------------------------------------

  /** The possible spell schools (cf. PHB 172/173). */
  public enum School implements EnumSelection.Named, EnumSelection.Short
  {
    /** Abjuration. */
    ABJURATION("Abjuration", "Abjur", BaseSpellProto.School.ABJURATION),

    /** Conjuration. */
    CONJURATION("Conjuration", "Conj", BaseSpellProto.School.CONJURATION),

    /** Divination. */
    DIVINATION("Divination", "Div", BaseSpellProto.School.DIVINATION),

    /** Enchantment. */
    ENCHANTMENT("Enchantment", "Ench", BaseSpellProto.School.ENCHANTMENT),

    /** Evocation. */
    EVOCATION("Evocation", "Evoc", BaseSpellProto.School.EVOACATION),

    /** Illusion. */
    ILLUSION("Illusion", "Illus", BaseSpellProto.School.ILLUSION),

    /** Necromancy. */
    NECROMANCY("Necromancy", "Necro", BaseSpellProto.School.NECROMANCY),

    /** Transmutation. */
    TRANSMUTATION("Transmutation", "Trans",
                  BaseSpellProto.School.TRANSMUTATION),

    /** Universal. */
    UNIVERSAL("Universal", "Univ", BaseSpellProto.School.UNIVERSAL);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseSpellProto.School m_proto;

    /** The value's short name. */
    private String m_short;

    /** Create the name.
     *
     * @param inName       the name of the value
     * @param inShort      the short name of the value
     * @param inProto      the proto enum value
     *
     */
    private School(String inName, String inShort, BaseSpellProto.School inProto)
    {
      m_name = constant("school.name", inName);
      m_short = constant("school.short", inShort);
      m_proto = inProto;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the short name of the value
     *
     */
    @Override
    public String getShort()
    {
      return m_short;
    }

    /** Get the save as string.
     *
     * @return the name of the value
     *
     */
    @Override
    public String toString()
    {
      return m_name;
    }

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseSpellProto.School getProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto     the proto value to look for
     * @return the matched enum (will throw exception if not found)
     */
    public static School fromProto(BaseSpellProto.School inProto)
    {
      for(School school: values())
        if(school.m_proto == inProto)
          return school;

      throw new IllegalStateException("invalid proto school: " + inProto);
    }
  }

  //........................................................................
  //----- subschools -------------------------------------------------------

  /** The possible spell schools (cf. PHB 172/173). */
  public enum Subschool implements EnumSelection.Named
  {
    /** None. */
    NONE("None", BaseSpellProto.Subschool.NONE),

    /** Calling. */
    CALLING("Calling", BaseSpellProto.Subschool.CALLING),

    /** Creation or Calling. */
    CREATION_OR_CALLING("Creation or Calling",
                        BaseSpellProto.Subschool.CREATION_OR_CALLING),

    /** Creation. */
    CREATION("Creation", BaseSpellProto.Subschool.CREATION),

    /** Healing. */
    HEALING("Healing", BaseSpellProto.Subschool.HEALING),

    /** Summoning. */
    SUMMONING("Summoning", BaseSpellProto.Subschool.SUMMONING),

    /** Teleportation. */
    TELEPORTATION("Teleportation", BaseSpellProto.Subschool.TELEPORTATION),

    /** Scrying. */
    SCRYING("Scrying", BaseSpellProto.Subschool.SCRYING),

    /** Charmn. */
    CHARM("Charm", BaseSpellProto.Subschool.CHARM),

    /** Compulsion. */
    COMPULSION("Compulsion", BaseSpellProto.Subschool.COMPULSION),

    /** Figment or Glamer. */
    FIGMENT_OR_GLAMER("Figment or Glamer",
                      BaseSpellProto.Subschool.FIGMENT_OR_GLAMER),

    /** Figment. */
    FIGMENT("Figment", BaseSpellProto.Subschool.FIGMENT),

    /** Glamer. */
    GLAMER("Glamer", BaseSpellProto.Subschool.GLAMER),

    /** Pattern. */
    PATTERN("Pattern", BaseSpellProto.Subschool.PATTERN),

    /** Phantasm. */
    PHANTASM("Phantasm", BaseSpellProto.Subschool.PHANTASM),

    /** Shadow. */
    SHADOW("Shadow", BaseSpellProto.Subschool.SHADOW);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseSpellProto.Subschool m_proto;

    /** Create the name.
     *
     * @param inName       the name of the value
     * @param inProto      the proto enum value
     *
     */
    private Subschool(String inName, BaseSpellProto.Subschool inProto)
    {
      m_name = constant("subschool.name", inName);
      m_proto = inProto;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String getName()
    {
      return m_name;
    }

    /** Get the save as string.
     *
     * @return the name of the value
     *
     */
    @Override
    public String toString()
    {
      return m_name;
    }

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseSpellProto.Subschool getProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto     the proto value to look for
     * @return the matched enum (will throw exception if not found)
     */
    public static Subschool fromProto(BaseSpellProto.Subschool inProto)
    {
      for(Subschool subschool: values())
        if(subschool.m_proto == inProto)
          return subschool;

      throw new IllegalStateException("invalid proto subschool: " + inProto);
    }
  }

  //........................................................................
  //----- descriptors ------------------------------------------------------

  /** The possible spell descriptors. */
  public enum Descriptor implements EnumSelection.Named
  {
    /** Acid. */
    ACID("Acid", BaseSpellProto.Descriptor.ACID),

    /** Air. */
    AIR("Air", BaseSpellProto.Descriptor.AIR),

    /** Chaotic. */
    CHAOTIC("Chaotic", BaseSpellProto.Descriptor.CHAOTIC),

    /** Cold. */
    COLD("Cold", BaseSpellProto.Descriptor.COLD),

    /** Darkness. */
    DARKNESS("Darkness", BaseSpellProto.Descriptor.DARKNESS),

    /** Death. */
    DEATH("Death", BaseSpellProto.Descriptor.DEATH),

    /** Earth. */
    EARTH("Earth", BaseSpellProto.Descriptor.EARTH),

    /** Electricity. */
    ELECTRICITY("Electricity", BaseSpellProto.Descriptor.ELECTRICITY),

    /** Evil. */
    EVIL("Evil", BaseSpellProto.Descriptor.EVIL),

    /** Fear. */
    FEAR("Fear", BaseSpellProto.Descriptor.FEAR),

    /** Fire or Cold. */
    FIRE_OR_COLD("Fire or Cold", BaseSpellProto.Descriptor.FIRE_OR_COLD),

    /** Fire. */
    FIRE("Fire", BaseSpellProto.Descriptor.FIRE),

    /** Force. */
    FORCE("Force", BaseSpellProto.Descriptor.FORCE),

    /** Good. */
    GOOD("Good", BaseSpellProto.Descriptor.GOOD),

    /** Language-dependent. */
    LANGUAGE_DEPENDENT("Language-dependent",
                       BaseSpellProto.Descriptor.LANGUAGE_DEPENDENT),

    /** Lawful. */
    LAWFUL("Lawful", BaseSpellProto.Descriptor.LAWFUL),

    /** Light. */
    LIGHT("Light", BaseSpellProto.Descriptor.LIGHT),

    /** Mind-affecting. */
    MIND_AFFECTING("Mind-affecting", BaseSpellProto.Descriptor.MIND_AFFECTING),

    /** Scrying. */
    SCRYING("Scrying", BaseSpellProto.Descriptor.SCRYING_DESCRIPTOR),

    /** Sonic. */
    SONIC("Sonic", BaseSpellProto.Descriptor.SONIC),

    /** Water. */
    WATER("Water", BaseSpellProto.Descriptor.WATER),

    /** See Text. */
    SEE_TEXT("See Text", BaseSpellProto.Descriptor.SEE_TEXT);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseSpellProto.Descriptor m_proto;

    /** The value's short name. */
    @SuppressWarnings("unused")
    private String m_short;

    /** Create the name.
     *
     * @param inName       the name of the value
     * @param inProto      the proto enum value
     *
     */
    private Descriptor(String inName, BaseSpellProto.Descriptor inProto)
    {
      m_name = constant("descriptor.name", inName);
      m_proto = inProto;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String getName()
    {
      return m_name;
    }

    /** Get the save as string.
     *
     * @return the name of the value
     *
     */
    @Override
    public String toString()
    {
      return m_name;
    }

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseSpellProto.Descriptor getProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto     the proto value to look for
     * @return the matched enum (will throw exception if not found)
     */
    public static Descriptor fromProto(BaseSpellProto.Descriptor inProto)
    {
      for(Descriptor descriptor : values())
        if(descriptor.m_proto == inProto)
          return descriptor;

      throw new IllegalStateException("invalid proto descriptor: " + inProto);
    }
  }

  //........................................................................
  //----- spell class ------------------------------------------------------

  /** The possible spell classes. */
  public enum SpellClass implements EnumSelection.Named, EnumSelection.Short
  {
    /** Assassin. */
    ASSASSIN("Assassin", "Asn", SharedProto.SpellClass.ASSASSIN),

    /** Bard. */
    BARD("Bard", "Brd", SharedProto.SpellClass.BARD),

    /** Clecric. */
    CLERIC("Cleric", "Clr", SharedProto.SpellClass.CLERIC),

    /** Druid. */
    DRUID("Druid", "Drd", SharedProto.SpellClass.DRUID),

    /** Paladin. */
    PALADIN("Paladin", "Pal", SharedProto.SpellClass.PALADIN),

    /** Ranger. */
    RANGER("Ranger", "Rgr", SharedProto.SpellClass.RANGER),

    /** Sorcerer. */
    SORCERER("Sorcerer", "Sor", SharedProto.SpellClass.SORCERER),

    /** Wizard. */
    WIZARD("Wizard", "Wiz", SharedProto.SpellClass.WIZARD),

    /** Air. */
    AIR("Air", "Air", SharedProto.SpellClass.AIR),

    /** Animal. */
    ANIMAL("Animal", "Animal", SharedProto.SpellClass.ANIMAL),

    /** Chaos. */
    CHAOS("Chaos", "Chaos", SharedProto.SpellClass.CHAOS),

    /** Death. */
    DEATH("Death", "Death", SharedProto.SpellClass.DEATH),

    /** Destruction. */
    DESTRUCTION("Destruction", "Destruction",
                SharedProto.SpellClass.DESTRUCTION),

    /** Drow. */
    DROW("Drow", "Drow", SharedProto.SpellClass.DROW),

    /** Earth. */
    EARTH("Earth", "Earth", SharedProto.SpellClass.EARTH),

    /** Evil. */
    EVIL("Evil", "Evil", SharedProto.SpellClass.EVIL),

    /** Fire. */
    FIRE("Fire", "Fire", SharedProto.SpellClass.FIRE),

    /** Good. */
    GOOD("Good", "Good", SharedProto.SpellClass.GOOD),

    /** Healing. */
    HEALING("Healing", "Healing", SharedProto.SpellClass.HEALING),

    /** Knowledge. */
    KNOWLEDGE("Knowledge", "Knowledge", SharedProto.SpellClass.KNOWLEDGE),

    /** Law. */
    LAW("Law", "Law", SharedProto.SpellClass.LAW),

    /** Luck. */
    LUCK("Luck", "Luck", SharedProto.SpellClass.LUCK),

    /** Magic. */
    MAGIC("Magic", "Magic", SharedProto.SpellClass.MAGIC),

    /** Plant. */
    PLANT("Plant", "Plant", SharedProto.SpellClass.PLANT),

    /** Protection. */
    PROTECTION("Protection", "Protection",
               SharedProto.SpellClass.PROTECTION),

    /** Strength. */
    STRENGTH("Strength", "Strength", SharedProto.SpellClass.STRENGTH),

    /** Sun. */
    SUN("Sun", "Sun", SharedProto.SpellClass.SUN),

    /** Travel. */
    TRAVEL("Travel", "Travel", SharedProto.SpellClass.TRAVEL),

    /** Trickery. */
    TRICKERY("Trickery", "Trickery", SharedProto.SpellClass.TRICKERY),

    /** War. */
    WAR("War", "War", SharedProto.SpellClass.WAR),

    /** Water. */
    Water("Water", "Water", SharedProto.SpellClass.WATER);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private SharedProto.SpellClass m_proto;

    /** The value's short name. */
    private String m_short;

    /** Create the name.
     *
     * @param inName       the name of the value
     * @param inShort      the short name of the value
     * @param inProto      the proto enum value
     *
     */
    private SpellClass(String inName, String inShort,
                       SharedProto.SpellClass inProto)
    {
      m_name = constant("spellclass.name", inName);
      m_short = constant("spellclass.short", inShort);
      m_proto = inProto;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the short name of the value
     *
     */
    @Override
    public String getShort()
    {
      return m_short;
    }

    /** Get the save as string.
    *
    * @return the name of the value
    *
    */
   @Override
   public String toString()
   {
     return m_name;
   }

   /**
    * Get the proto value for this value.
    *
    * @return the proto enum value
    */
   public SharedProto.SpellClass getProto()
   {
     return m_proto;
   }

   /**
    * Get the group matching the given proto value.
    *
    * @param  inProto     the proto value to look for
    * @return the matched enum (will throw exception if not found)
    */
   public static SpellClass fromProto(SharedProto.SpellClass inProto)
   {
     for(SpellClass spellClass: values())
       if(spellClass.m_proto == inProto)
         return spellClass;

     throw new IllegalStateException("invalid proto class: " + inProto);
   }
  }

  //........................................................................
  //----- components -------------------------------------------------------

  /** The possible spell components. */
  public enum Components implements EnumSelection.Named, EnumSelection.Short
  {
    /** Verbose. */
    VERBOSE("Verbose", "V", BaseSpellProto.Components.VERBOSE),

    /** Somatic. */
    SOMATIC("Somatic", "S", BaseSpellProto.Components.SOMATIC),

    /** Material/Divine Focus. */
    MATERIAL_DEVINE_FOCUS("Material/Divine Focus", "M/DF",
                          BaseSpellProto.Components.MATERIAL_DEVINE_FOCUS),

    /** Material. */
    MATERIAL("Material", "M", BaseSpellProto.Components.MATERIAL),

    /** Focus/Divine Focus. */
    FOCUS_DIVINE_FOCUS("Focus/Divine Focus", "F/DF",
                       BaseSpellProto.Components.FOCUS_DIVINE_FOCUS),

    /** Focus. */
    FOCUS("Focus", "F", BaseSpellProto.Components.FOCUS),

    /** Divine Focus. */
    DIVINE_FOCUS("Divine Focus", "DF", BaseSpellProto.Components.DIVINE_FOCUS),

    /** Experience points. */
    EXPERIENCE_POINTS("Experience Points", "XP",
                      BaseSpellProto.Components.EXPERIENCE_POINTS);

    /** The value's name. */
    private String m_name;

    /** The value's short name. */
    private String m_short;

    /** The proto enum value. */
    private BaseSpellProto.Components m_proto;

    /** Create the name.
     *
     * @param inName       the name of the value
     * @param inShort      the short name of the value
     * @param inProto      the proto enum value
     *
     */
    private Components(String inName, String inShort,
                       BaseSpellProto.Components inProto)
    {
      m_name = constant("school.name", inName);
      m_short = constant("school.short", inShort);
      m_proto = inProto;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the short name of the value
     *
     */
    @Override
    public String getShort()
    {
      return m_short;
    }

    /** Get the save as string.
     *
     * @return the name of the value
     *
     */
    @Override
    public String toString()
    {
      return m_name;
    }

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseSpellProto.Components getProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto     the proto value to look for
     * @return the matched enum (will throw exception if not found)
     */
    public static Components fromProto(BaseSpellProto.Components inProto)
    {
      for(Components components : values())
        if(components.m_proto == inProto)
          return components;

      throw new IllegalStateException("invalid proto components: " + inProto);
    }
  }

  //........................................................................
  //----- range ------------------------------------------------------------

  /** The possible spell ranges. */
  public enum Range implements EnumSelection.Named
  {
    /** Personal or Touch. */
    PERSONAL_OR_TOUCH("Personal or Touch",
                      BaseSpellProto.Range.PERSONAL_OR_TOUCH),

    /** Personal and Touch. */
    PERSONAL_AND_TOUCH("Personal and Touch",
                       BaseSpellProto.Range.PERSONAL_AND_TOUCH),

    /** Personal or Close. */
    PERSONAL_OR_CLOSE("Personal or Close",
                      BaseSpellProto.Range.PERSONAL_OR_CLOSE),

    /** Personal. */
    PERSONAL("Personal", BaseSpellProto.Range.PERSONAL),

    /** Touch. */
    TOUCH("Touch", BaseSpellProto.Range.TOUCH),

    /** Close. */
    CLOSE("Close", BaseSpellProto.Range.CLOSE),

    /** Medium. */
    MEDIUM("Medium", BaseSpellProto.Range.MEDIUM),

    /** Long. */
    LONG("Long", BaseSpellProto.Range.LONG),

    /** Unlimited. */
    UNLIMITED("Unlimited", BaseSpellProto.Range.UNLIMITED),

    /** 40 ft/level. */
    FOURTY_FEET_PER_LEVEL("40 ft/level",
                          BaseSpellProto.Range.FOURTY_FEET_PER_LEVEL),

    /** See Text. */
    SEE_TEXT("See Text", BaseSpellProto.Range.SEE_TEXT_RANGE),

    /** Anywhere within the area to be warded. */
    ANYWHERE_WITHIN_AREA_WARDED
      ("Anywhere within the area to be warded",
       BaseSpellProto.Range.ANYWHERE_WITHIN_AREA_WARDED),

    /** Up to 10 ft/level. */
    UP_TO_TEN_FEET_PER_LEVEL("Up to 10 ft/level",
                             BaseSpellProto.Range.UP_TO_TEN_FEE_PER_LEVEL),

    /** 1 mile/level. */
    ONE_MILE_PER_LEVEL("1 mile/level", BaseSpellProto.Range.ONE_MILE_PER_LEVEL);

    /** The value's name. */
    private String m_name;

    /** The prot enum value. */
    private BaseSpellProto.Range m_proto;

    /** Create the name.
     *
     * @param inName       the name of the value
     * @param inProto      the proto enum value
     *
     */
    private Range(String inName, BaseSpellProto.Range inProto)
    {
      m_name = constant("range.name", inName);
      m_proto = inProto;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String getName()
    {
      return m_name;
    }

    /** Get the save as string.
     *
     * @return the name of the value
     *
     */
    @Override
    public String toString()
    {
      return m_name;
    }

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseSpellProto.Range getProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto     the proto value to look for
     * @return the matched enum (will throw exception if not found)
     */
    public static Range fromProto(BaseSpellProto.Range inProto)
    {
      for(Range range : values())
        if(range.m_proto == inProto)
          return range;

      throw new IllegalStateException("invalid proto range: " + inProto);
    }
  }

  //........................................................................
  //----- effect -----------------------------------------------------------

  /** The possible spell ranges. */
  public enum Effect implements EnumSelection.Named
  {
    /** Ray. */
    RAY("Ray", BaseSpellProto.Effect.Type.RAY),

    /** Spread. */
    SPREAD("Spread", BaseSpellProto.Effect.Type.SPREAD);

    /** The value's name. */
    private final String m_name;

    /** The proto enum value. */
    private final BaseSpellProto.Effect.Type m_proto;

    /** Create the name.
     *
     * @param inName       the name of the value
     * @param inProto      the proto enum value
     */
    private Effect(String inName, BaseSpellProto.Effect.Type inProto)
    {
      m_name = constant("effect.name", inName);
      m_proto = inProto;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String getName()
    {
      return m_name;
    }

    /** Get the save as string.
     *
     * @return the name of the value
     *
     */
    @Override
    public String toString()
    {
      return m_name;
    }

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseSpellProto.Effect.Type getProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto     the proto value to look for
     * @return the matched enum (will throw exception if not found)
     */
    public static Effect fromProto(BaseSpellProto.Effect.Type inProto)
    {
      for(Effect effect : values())
        if(effect.m_proto == inProto)
          return effect;

      throw new IllegalStateException("invalid proto effect: " + inProto);
    }
  }

  //........................................................................
  //----- spell durations --------------------------------------------------

  /** The possible spell durations (cf. PHB 176). */
  static final String []SPELL_DURATIONS =
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
  static final String []LEVELS =
    Config.get("/game/levels", new String []
      {
        "level",
        "2 level",
        "3 level",
      });

  //........................................................................
  //----- spell durations --------------------------------------------------

  /** The possible spell duration. */
  static final String []SPELL_DURATION_FLAGS =
    Config.get("/game/spell.duration.flags", new String []
      {
        "(D)",
      });

  //........................................................................
  //----- saving throws ----------------------------------------------------

  /** The possible spell saving throws (PHB p. 176/177). */
  static final String []SAVING_THROWS =
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
  static final String []SPELL_RESISTANCES =
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
   * @param       inName the name of the base spell
   */
  public BaseSpell(String inName)
  {
    super(inName, TYPE);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final BaseType<BaseSpell> TYPE =
    new BaseType<BaseSpell>(BaseSpell.class);

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

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
  public String getSummary(String ... inParameters)
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
  public String getSummary(@Nullable SpellClass inKind, int inLevel,
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
   * @param       inParameters  the parameters to modify the summary
   *
   * @return      the string with the summary
   */
  @Override
  @SuppressWarnings("unchecked")
  public String getSummary(@Nullable Parameters inParameters)
  {
    long casterLevel = -1;
    long spellLevel = -1;
    if(inParameters != null && inParameters.hasValue("level"))
      casterLevel = Integer.parseInt(inParameters.getValue("level").toString());

    String spellClass = "";
    if(inParameters != null && inParameters.hasValue("class"))
      spellClass = inParameters.getValue("class").toString();

    for(Multiple spellLevelValue : m_level)
    {
      long level = ((Number)spellLevelValue.get(1)).get();
      if(spellClass.equals(((EnumSelection<SpellClass>)spellLevelValue.get(0))
                           .getSelected().toString()))
      {
        spellLevel = level;
        break;
      }
      else
        if(spellLevel < 0)
          spellLevel = level;
        else
          spellLevel = Math.max(spellLevel, level);
    }

    if(casterLevel < 0 && spellLevel >= 0)
      casterLevel = spellLevel * 2 - 1;

    long ability = Long.MIN_VALUE;
    if(inParameters != null && inParameters.hasValue("ability"))
      try
      {
        ability = Integer.parseInt(inParameters.getValue("ability").toString());
      }
      catch(NumberFormatException e)
      {
        // just ignore it
      }

    StringBuilder summary = new StringBuilder();

    summary.append(getShortDescription());
    summary.append(' ');
    summary.append(m_school);
    if(m_descriptor.isDefined())
    {
      summary.append(" [");
      summary.append(m_descriptor);
      summary.append(']');
    }

    summary.append(", level ");
    summary.append(spellLevel);
    summary.append(" (caster ");
    summary.append(casterLevel);
    summary.append(')');

    if(!m_castingTime.isStandardAction())
      summary.append(", CT " + m_castingTime.toShortString());

    summary.append(", range ");
    if(casterLevel >= 0 && m_range.isDefined()
       && m_range.get() instanceof EnumSelection)
      switch(((EnumSelection<Range>)m_range.get()).getSelected())
      {
        case PERSONAL_OR_TOUCH:
        case PERSONAL_AND_TOUCH:
        case PERSONAL:
        case TOUCH:
        case UNLIMITED:
        case SEE_TEXT:
        case ANYWHERE_WITHIN_AREA_WARDED:
          summary.append(m_range);
          break;

        case PERSONAL_OR_CLOSE:
          summary.append("Personal or "
                            + (25 + (casterLevel / 2) * 5) + " ft");
          break;

        case CLOSE:
          summary.append((25 + (casterLevel / 2) * 5) + " ft");
          break;

        case MEDIUM:
          summary.append((100 + casterLevel * 10) + " ft");
          break;

        case LONG:
          summary.append((400 + casterLevel * 40) + " ft");
          break;

        case FOURTY_FEET_PER_LEVEL:
          summary.append((40 * casterLevel) + " ft");
          break;

        case UP_TO_TEN_FEET_PER_LEVEL:
          summary.append((10 * casterLevel) + " ft");
          break;

        case ONE_MILE_PER_LEVEL:
          summary.append(casterLevel + " mi");
          break;

        default:
          break;
      }
    else
      summary.append(m_range);

    if(m_effect.isDefined())
    {
      summary.append(", ");
      if(m_effect.get(0).isDefined())
      {
        summary.append(m_effect.get(0));
        summary.append(' ');
      }
      if(m_effect.get(1).isDefined())
      {
        summary.append(m_effect.get(1));
        summary.append(' ');
      }

      summary.append(((Text)m_effect.get(2)).get());
    }

    if(m_target.isDefined())
    {
      summary.append(", ");
      summary.append(m_target.get());
    }

    if(m_area.isDefined())
    {
      summary.append(", ");
      summary.append(m_area.get());
    }

    if(m_duration.isDefined())
    {
      summary.append(", duration ");

      if(casterLevel < 0)
        summary.append(m_duration);
      else
      {
        String prefix = "";
        Duration duration = null;
        if(((Union)m_duration.get(0)).get() instanceof Selection)
        {
          switch(((Union)m_duration.get(0)).get().toString())
          {
            case "Instantaneous or concentration (up to 1 round/level)":
              prefix = "Instantaneous or concentration up to ";
              duration = Duration.ROUND.multiply(casterLevel);
              break;

            case "Instantaneous or 1 round/level":
              prefix = "Instantaneous or ";
              duration = Duration.ROUND.multiply(casterLevel);
              break;

            case "Permanent until triggered, then 1 round/level":
              prefix = "Permanent until triggered, then ";
              duration = Duration.ROUND.multiply(casterLevel);
              break;

            case "Permanent or until discharged until released or 1d4 "
              + "days + one day/level":
              prefix = "Permanent or until dischargred or until released or "
                + "1d4 days + ";
              duration = Duration.DAY.multiply(casterLevel);
              break;

            case "Concentration (up to 1 round/level) or instantaneous":
              prefix = "Instantaneous or Contentration up to ";
              duration = Duration.ROUND.multiply(casterLevel);
              break;

            case "Concentration up to 1 round/level":
              prefix = "Concentration up to ";
              duration = Duration.ROUND.multiply(casterLevel);
              break;

            case "Concentration + 1 round/level":
              prefix = "Contentration + ";
              duration = Duration.ROUND.multiply(casterLevel);
              break;

            case "Concentration + 1 hour/level":
              prefix = "Concentration + ";
              duration = Duration.HOUR.multiply(casterLevel);
              break;

            case "Concentration up to 1 min/level":
              prefix = "Concentration up to ";
              duration = Duration.MINUTE.multiply(casterLevel);
              break;

            case "Concentration up to 10 min/level":
              prefix = "Concentration up to ";
              duration = Duration.MINUTE.multiply(10 * casterLevel);
              break;

            case "One round per three levels":
              duration = Duration.ROUND.multiply(casterLevel / 3);
              break;

            case "One hour/level or until discharged":
              prefix = "Until discharged or ";
              duration = Duration.HOUR.multiply(casterLevel);
              break;

            case "One round/level or One round":
              prefix = "One round or ";
              duration = Duration.ROUND.multiply(casterLevel);
              break;

            case "Until landing or 1 round/level":
              prefix = "Until landing or ";
              duration = Duration.ROUND.multiply(casterLevel);
              break;

            case "10 min/level or until used":
              prefix = "Until used or ";
              duration = Duration.MINUTE.multiply(10 * casterLevel);
              break;

            case "10 min/level or until discharged":
              prefix = "Until discharged or ";
              duration = Duration.MINUTE.multiply(10 * casterLevel);
              break;

            case "One day/level or until discharged":
              prefix = "Until discharged or ";
              duration = Duration.DAY.multiply(casterLevel);
              break;

            case "One hour/level or until you return to your body":
              prefix = "Until you return to your body or ";
              duration = Duration.HOUR.multiply(casterLevel);
              break;

            case "One round + 1 round per three levels":
              duration =
                Duration.ROUND.multiply(casterLevel).add(Duration.ROUND);
              break;

            case "1 round/level (D) and concentration + 3 rounds":
              prefix = "Concentration + 3 rounds after ";
              duration = Duration.ROUND.multiply(casterLevel);
              break;

            case "1 hour/caster level or until discharged, then 1 round/caster "
              + "level":
              prefix = Duration.HOUR.multiply(casterLevel)
                + " or until discharged, then ";
              duration = Duration.ROUND.multiply(casterLevel);
              break;

            case "One Usage per two levels":
              prefix = (casterLevel / 2) + " usages";
              break;

            case "1 round/level or 1 round":
              prefix = "1 round or ";
              duration = Duration.ROUND.multiply(casterLevel);
              break;

            case "Until expended or 10 min/level":
              prefix = "Until expended or ";
              duration = Duration.MINUTE.multiply(casterLevel);
              break;

            case "1 hour/level or until completed":
              prefix = "Until completed or ";
              duration = Duration.HOUR.multiply(casterLevel);
              break;

            case "1 hour/level or until expended":
              prefix = "Until expended or ";
              duration = Duration.HOUR.multiply(casterLevel);
              break;

            case "1 round/level or until all beams are exhausted":
              prefix = "Until all beams are exhausted or ";
              duration = Duration.ROUND.multiply(casterLevel);
              break;

            case "Up to 1 round/level":
              prefix = "Up to ";
              duration = Duration.ROUND.multiply(casterLevel);
              break;

            case "No more than 1 hour/level or until discharged (destination "
              + "is reached)":
              prefix = "Until descharged (destination is reached) or no more "
                + "than ";
              duration = Duration.HOUR.multiply(casterLevel);
              break;

            default:
              prefix = m_duration.get(0).toString();
          }
        }
        else
        {
          Multiple durationValue = (Multiple)((Union)m_duration.get(0)).get();
          duration = (Duration)durationValue.get(0);

          if(durationValue.get(1).isDefined())
            switch(durationValue.get(1).toString())
            {
              case "level":
                duration = duration.multiply(casterLevel);
                break;

              case "2 level":
                duration = duration.multiply(casterLevel / 2);
                break;

              case "3 level":
                duration = duration.multiply(casterLevel / 3);
                break;

              default:
                break;
            }

          if(durationValue.get(2).isDefined())
            duration = duration.add((Duration)durationValue.get(2));
        }

        summary.append(prefix);
        if(duration != null)
          summary.append(duration);

        if(m_duration.get(1).isDefined())
        {
          summary.append(' ');
          summary.append(m_duration.get(1));
        }
        if(m_duration.get(2).isDefined())
        {
          summary.append(' ');
          summary.append(((Text)m_duration.get(2)).get());
        }
      }
    }

    if(m_savingThrow.isDefined())
    {
      summary.append(", save ");
      summary.append(m_savingThrow);

      if(spellLevel >= 0)
      {
        long dc = 0;
        String save = m_savingThrow.toString();
        if(save.matches(".*\b(Will|Reflex|Fortitude)\b.*"))
          dc = 10 + spellLevel + ability;

        if(dc > 0)
          summary.append(" DC " + dc);
      }
    }

    summary.append(", SR ");
    summary.append(m_resistance);
    summary.append(" (");
    summary.append(Strings.COMMA_JOINER.join(getReferences()));
    summary.append(')');

    Value<?> notes =
        inParameters != null ? inParameters.getValue("Notes") : null;
    if(notes != null)
    {
      summary.append(" (");
      summary.append(notes);
      summary.append(')');
    }

    return summary.toString();
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

    for(EnumSelection<Subschool> subschool
          : (ValueList<EnumSelection<Subschool>>)m_school.get(1))
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
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().hasAccess(BaseCharacter.Group.ADMIN);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  @SuppressWarnings("unchecked")
  @Override
  public Message toProto()
  {
    BaseSpellProto.Builder builder = BaseSpellProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    if(m_school.isDefined())
    {
      builder.setSchool(((EnumSelection<School>)m_school.get(0)).getSelected()
                        .getProto());
      for(EnumSelection<Subschool> subschool
        : ((ValueList<EnumSelection<Subschool>>)m_school.get(1)))
        if(subschool.isDefined())
          builder.addSubschool(subschool.getSelected().getProto());
    }

    if(m_summary.isDefined())
      builder.setSummary(m_summary.get());

    if(m_descriptor.isDefined())
      for(EnumSelection<Descriptor> descriptor : m_descriptor)
        builder.addDescriptor(descriptor.getSelected().getProto());

    if(m_level.isDefined())
      for(Multiple level : m_level)
        builder.addLevel
          (BaseSpellProto.Level.newBuilder()
           .setSpellClass(((EnumSelection<SpellClass>)level.get(0))
                          .getSelected().getProto())
           .setLevel((int)((Number)level.get(1)).get())
           .build());

    if(m_components.isDefined())
      for(EnumSelection<Components> components : m_components)
        builder.addComponents(components.getSelected().getProto());

    if(m_material.isDefined())
      for(Multiple material : m_material)
      {
        BaseSpellProto.Material.Builder materialBuilder =
          BaseSpellProto.Material.newBuilder();

        materialBuilder.setUse(((Name)material.get(0)).get());
        for(Text component : (ValueList<Text>)material.get(1))
          materialBuilder.addComponent(component.get());

        builder.addMaterial(materialBuilder.build());
      }

    if(m_focus.isDefined())
    {
      BaseSpellProto.Material.Builder materialBuilder =
        BaseSpellProto.Material.newBuilder();

      materialBuilder.setUse(((Name)m_focus.get(0)).get());
      for(Text component : (ValueList<Text>)m_focus.get(1))
        materialBuilder.addComponent(component.get());

      builder.setFocus(materialBuilder.build());
    }

    if(m_castingTime.isDefined())
      builder.setCastingTime(m_castingTime.toProto());

    if(m_range.isDefined())
      if(m_range.getIndex() == 0)
        builder.setSpecialRange(((EnumSelection<Range>)m_range.get())
                                .getSelected().getProto());
      else
       builder.setRange(((Distance)m_range.get()).toProto());

    if(m_effect.isDefined())
    {
      BaseSpellProto.Effect.Builder effect = BaseSpellProto.Effect.newBuilder();

      if(m_effect.get(0).isDefined())
        effect.setDistance(((Distance)m_effect.get(0)).toProto());
      if(m_effect.get(1).isDefined())
        effect.setType(((EnumSelection<Effect>)m_effect.get(1))
                       .getSelected().getProto());
      if(m_effect.get(2).isDefined())
        effect.setDescription(((Text)m_effect.get(2)).get());

      builder.setEffect(effect.build());
    }

    if(m_target.isDefined())
      builder.setTarget(m_target.get());

    if(m_area.isDefined())
      builder.setArea(m_area.get());

    if(m_duration.isDefined())
    {
      BaseSpellProto.Duration.Builder duration =
        BaseSpellProto.Duration.newBuilder();

      if(m_duration.get(0).isDefined())
        if(((Union)m_duration.get(0)).getIndex() == 0)
          duration.setDurationDescription
            (((Union)m_duration.get(0)).get().toString());
        else
        {
          Multiple multiple = (Multiple)((Union)m_duration.get(0)).get();
          if(multiple.get(0).isDefined())
            duration.setDuration(((Duration)multiple.get(0)).toProto());
          if(multiple.get(1).isDefined())
            duration.setLevels(((Selection)multiple.get(1)).toString());
          if(multiple.get(2).isDefined())
            duration.setAdditionalDuration(((Duration)multiple.get(2))
                                           .toProto());
        }

      if(m_duration.get(1).isDefined())
        duration.setFlags(((Selection)m_duration.get(1)).toString());

      if(m_duration.get(2).isDefined())
        duration.setDescription(((Text)m_duration.get(2)).get());

      builder.setDuration(duration.build());
    }

    if(m_savingThrow.isDefined())
      builder.setSavingThrow(m_savingThrow.toString());

    if(m_resistance.isDefined())
      builder.setSpellResistance(m_resistance.toString());

    BaseSpellProto proto = builder.build();
    return proto;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseSpellProto))
    {
      Log.warning("cannot parse proto " + inProto.getClass());
      return;
    }

    BaseSpellProto proto = (BaseSpellProto)inProto;

    super.fromProto(proto.getBase());

    if(proto.hasSchool())
    {
      ValueList<EnumSelection<Subschool>> subschoolsList =
        (ValueList<EnumSelection<Subschool>>)m_school.get(1);
      List<EnumSelection<Subschool>> subschools = new ArrayList<>();
      for (BaseSpellProto.Subschool subschool : proto.getSubschoolList())
        subschools.add(subschoolsList.createElement()
                       .as(Subschool.fromProto(subschool)));

      m_school =
        m_school.as(((EnumSelection<School>)m_school.get(0))
                    .as(School.fromProto(proto.getSchool())),
                    subschoolsList.as(subschools));
    }

    if(proto.hasSummary())
      m_summary = m_summary.as(proto.getSummary());

    if(proto.getDescriptorCount() > 0)
    {
      List<EnumSelection<Descriptor>> descriptors = new ArrayList<>();
      for(BaseSpellProto.Descriptor descriptor : proto.getDescriptorList())
        descriptors.add(m_descriptor.createElement()
                        .as(Descriptor.fromProto(descriptor)));

      m_descriptor = m_descriptor.as(descriptors);
    }

    if(proto.getLevelCount() > 0)
    {
      Multiple example = m_level.createElement();
      List<Multiple> levels = new ArrayList<>();
      for(BaseSpellProto.Level level : proto.getLevelList())
        levels.add(example.as(((EnumSelection<SpellClass>)example.get(0))
                              .as(SpellClass.fromProto(level.getSpellClass())),
                              ((Number)example.get(1)).as(level.getLevel())));

      m_level = m_level.as(levels);
    }

    if(proto.getComponentsCount() > 0)
    {
      List<EnumSelection<Components>> components = new ArrayList<>();
      for(BaseSpellProto.Components component : proto.getComponentsList())
        components.add(m_components.createElement()
                       .as(Components.fromProto(component)));

      m_components = m_components.as(components);
    }

    if(proto.getMaterialCount() > 0)
    {
      List<Multiple> materials = new ArrayList<>();
      for(BaseSpellProto.Material material : proto.getMaterialList())
      {
        List<Text> components = new ArrayList<>();
        for(String component : material.getComponentList())
          components.add(new Text(component));

        Multiple multiple = m_material.createElement();
        materials.add(multiple
                      .as(((Name)multiple.get(0)).as(material.getUse()),
                          ((ValueList<Text>)multiple.get(1)).as(components)));
      }

      m_material = m_material.as(materials);
    }

    if(proto.hasFocus())
    {
      List<Text> components = new ArrayList<>();
      for(String component : proto.getFocus().getComponentList())
        components.add(new Text(component));

      m_focus =
        m_focus.as(((Name)m_focus.get(0)).as(proto.getFocus().getUse()),
                   ((ValueList<Text>)m_focus.get(1)).as(components));
    }

    if(proto.hasCastingTime())
      m_castingTime = m_castingTime.fromProto(proto.getCastingTime());

    if(proto.hasSpecialRange())
      m_range = m_range.as(0, ((EnumSelection<Range>)m_range.get(0))
                              .as(Range.fromProto(proto.getSpecialRange())));
    else if(proto.hasRange())
      m_range = m_range.as(1, ((Distance)m_range.get(1))
                              .fromProto(proto.getRange()));

    if(proto.hasEffect())
      m_effect = m_effect.as(proto.getEffect().hasDistance()
                             ? ((Distance)m_effect.get(0))
                               .fromProto(proto.getEffect().getDistance())
                             : m_effect.get(0),
                             proto.getEffect().hasType()
                             ? ((EnumSelection<Effect>)m_effect.get(1))
                               .as(Effect.fromProto
                                   (proto.getEffect().getType()))
                             : m_effect.get(1),
                             proto.getEffect().hasDescription()
                             ? ((Text)m_effect.get(2))
                               .as(proto.getEffect().getDescription())
                             : m_effect.get(2));

    if(proto.hasTarget())
      m_target = m_target.as(proto.getTarget());

    if(proto.hasArea())
      m_area = m_area.as(proto.getArea());

    if(proto.hasDuration())
    {
      Value []values = new Value[3];

      if(proto.getDuration().hasDurationDescription())
        values[0] = ((Union)m_duration.get(0))
          .as(0, ((Selection)((Union)m_duration.get(0)).get(0))
              .as(proto.getDuration().getDurationDescription()));
      else
      {
        Multiple old = (Multiple)((Union)m_duration.get(0)).get(1);
        values[0] = ((Union)m_duration.get(0))
          .as(1, old.as(proto.getDuration().hasDuration()
                        ? ((Duration)old.get(0))
                          .fromProto(proto.getDuration().getDuration())
                        : old.get(0),
                        proto.getDuration().hasLevels()
                        ? ((Selection)old.get(1))
                          .as(proto.getDuration().getLevels())
                        : old.get(1),
                        proto.getDuration().hasAdditionalDuration()
                        ? ((Duration)old.get(2)).fromProto
                          (proto.getDuration().getAdditionalDuration())
                        : old.get(2)));
      }

      if(proto.getDuration().hasFlags())
        values[1] =
          ((Selection)m_duration.get(1)).as(proto.getDuration().getFlags());
      else
        values[1] = m_duration.get(1);

      if(proto.getDuration().hasDescription())
        values[2] =
          ((Text)m_duration.get(2)).as(proto.getDuration().getDescription());
      else
        values[2] = m_duration.get(2);

      m_duration = m_duration.as(values);
    }

    if(proto.hasSavingThrow())
      m_savingThrow = m_savingThrow.as(proto.getSavingThrow());

    if(proto.hasSpellResistance())
      m_resistance = m_resistance.as(proto.getSpellResistance());
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(BaseSpellProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
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
      try (java.io.StringReader sReader = new java.io.StringReader(s_text))
      {
        ParseReader reader = new ParseReader(sReader, "test");

        return null; //BaseSpell.read(reader);
      }
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
      + "  effect            20 ft spread \"Fog\";\n"
      + "  material          combined : \"dried, powdered peas (pinch)\",\n"
      + "                    \"powdered animal hoof\";\n"
      + "  casting time      1 standard action;\n"
      + "  duration          1 round/level;\n"
      + "  saving throw      None;\n"
      + "  spell resistance  No;\n"
      + "  worlds            generic;\n"
      + "  references        WTC 17524: 196;\n"
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
    @org.junit.Test
    public void testRead()
    {
      String result =
        "#----- Acid Fog\n"
        + "\n"
        + "base spell Acid Fog =\n"
        + "\n"
        + "  school            Conjuration (Creation);\n"
        + "  descriptor        Acid;\n"
        + "  level             Sorcerer 6, Wizard 6, Water 7;\n"
        + "  components        Verbose, Somatic, Material/Divine Focus;\n"
        + "  material          combined:\"dried, powdered peas (pinch)\", "
        + "\"powdered animal hoof\";\n"
        + "  casting time      1 standard action;\n"
        + "  range             Medium;\n"
        + "  effect            20 ft Spread \"Fog\";\n"
        + "  duration          1 round/level;\n"
        + "  saving throw      None;\n"
        + "  spell resistance  No;\n"
        + "  worlds            Generic;\n"
        + "  references        WTC 17524: 196;\n"
        + "  description       "
        + "\"\\Spell{Acid fog} creates a billowing mass of misty vapors "
        + "similar to that\n"
        + "                    produced by a \\Spell{solid fog} spell. In "
        + "addition to slowing creatures down\n"
        + "                    and obscuring sight, this spell's vapors are "
        + "highly acidic. Each round on\n"
        + "                    your turn, starting when you cast the spell, "
        + "the fog deals 2d6 points of acid\n"
        + "                    damage to each creature and object within "
        + "it.\";\n"
        + "  short description \"Fog deals acid damage.\";\n"
        + "  synonyms          \"Fog, Acid\";\n"
        + "  name              Acid Fog.\n"
        + "\n"
        + "#.....\n";

      AbstractEntry entry = createBaseSpell();

      assertNotNull("base item should have been read", entry);
      assertEquals("base item name does not match", "Acid Fog",
                   entry.getName());
      assertEquals("base item does not match", result, entry.toString());
    }

    //......................................................................
  }

  //........................................................................
}
