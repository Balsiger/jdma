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
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseSpellProto;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.Duration;
import net.ixitxachitls.dma.values.Parser;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Group;
import net.ixitxachitls.dma.values.enums.School;
import net.ixitxachitls.dma.values.enums.SpellClass;
import net.ixitxachitls.dma.values.enums.SpellComponent;
import net.ixitxachitls.dma.values.enums.SpellDescriptor;
import net.ixitxachitls.dma.values.enums.SpellEffect;
import net.ixitxachitls.dma.values.enums.SpellRange;
import net.ixitxachitls.dma.values.enums.Subschool;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the basic jDMA base spell.
 *
 * @file          BaseSpell.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
public class BaseSpell extends BaseEntry
{
  /** The level of a spell. */
  public static class Level
  {
    /**
     * Create the spell level.
     *
     * @param inClass the spell class
     * @param inLevel the level
     */
    public Level(SpellClass inClass, int inLevel)
    {
      m_class = inClass;
      m_level = inLevel;
    }

    /** The spell class. */
    private final SpellClass m_class;

    /** The spell level. */
    private final int m_level;

    /** The parser for spell levels. */
    public static final Parser<Level> PARSER = new Parser<Level>(2)
    {
      @Override
      public Optional<Level> doParse(String inClass, String inLevel)
      {
        try
        {
          Optional<SpellClass> spellClass = SpellClass.fromString(inClass);
          if(!spellClass.isPresent())
            return Optional.absent();
          int level = Integer.parseInt(inLevel);
          return Optional.of(new Level(spellClass.get(), level));
        }
        catch(NumberFormatException e)
        {
          return Optional.absent();
        }
      }
    };

    /**
     * Get the spellcasting class.
     *
     * @return the spell class
     */
    public SpellClass getSpellClass()
    {
      return m_class;
    }

    /**
     * Get the level of the spell.
     *
     * @return the spell level
     */
    public int getLevel()
    {
      return m_level;
    }

    @Override
    public String toString()
    {
      return m_class.getShort() + m_level;
    }
  }

  /** The material components for a spell. */
  public static class Material
  {
    /**
     * Create the material components.
     *
     * @param inUse how the components are used
     * @param inComponents the components needed
     */
    public Material(String inUse, List<String> inComponents)
    {
      m_use = inUse;
      m_components = inComponents;
    }

    /** How the components have to be used. */
    private final String m_use;

    /** What components need to be used for casting the spall. */
    private final List<String> m_components;

    /** The parser for material components. */
    public static final Parser<Material> PARSER =
      new Parser<Material>(2)
      {
        @Override
        public Optional<Material> doParse(String inUse, String inComponents)
        {
          return Optional.of(new Material(inUse,
                                          Strings.COMMA_SPLITTER
                                          .splitToList(inComponents)));
        }
      };

    /**
     * Get the use of the material.
     *
     * @return how the material is to be used
     */
    public String getUse()
    {
      return m_use;
    }

    /**
     * Get what components have to be used.
     *
     * @return the list of components
     */
    public List<String> getComponents()
    {
      return Collections.unmodifiableList(m_components);
    }

    /**
     * Get the components as a single, comma separated string.
     *
     * @return the comma spearated string of components.
     */
    public String getComponentsString()
    {
      return Strings.COMMA_JOINER.join(m_components);
    }

    @Override
    public String toString()
    {
      return m_use + ": " + Strings.COMMA_JOINER.join(m_components);
    }
  }

  /** The effect of a spell. */
  public static class Effect
  {
    /**
     * Create the spell effect.
     *
     * @param inDistance the distance to where the effect can take place
     * @param inEffect the effect itself
     * @param inText some text describing the effect
     */
    public Effect(Optional<Distance> inDistance,
                  Optional<SpellEffect> inEffect, String inText)
    {
      m_distance = inDistance;
      m_effect = inEffect;
      m_text = inText;
    }

    /** The distance up to which the effect can happen. */
    private final Optional<Distance> m_distance;

    /** The actual effect. */
    private final Optional<SpellEffect> m_effect;

    /** The description of the effect. */
    private final String m_text;

    /** The parser for spell effects. */
    public static final Parser<Effect> PARSER =
      new Parser<Effect>(3)
      {
        @Override
        public Optional<Effect> doParse(String inDistance, String inEffect,
                                        String inText)
        {
          return Optional.of(new Effect(Distance.PARSER.parse(inDistance),
                                        SpellEffect.fromString(inEffect),
                                        inText));
        }
      };

    /**
     * Get the distance for the effect.
     *
     * @return the distance
     */
    public Optional<Distance> getDistance()
    {
      return m_distance;
    }

    /**
     * Get the actual spell effect.
     *
     * @return the spell effect
     */
    public Optional<SpellEffect> getEffect()
    {
      return m_effect;
    }

    /**
     * Get the effect description.
     *
     * @return the text
     */
    public String getText()
    {
      return m_text;
    }

    @Override
    public String toString()
    {
      return (m_distance.isPresent() ? m_distance + " " : "")
        + (m_effect.isPresent() ? m_effect + " " : "") + m_text;
    }
  }

  /** A spell's duration. */
  public static class Duration
  {
    /**
     * Create the spell duration.
     *
     * @param inDuration the duration description
     * @param inDismissable whether the spell is dismissable
     * @param inText other comments on the spell duration
     */
    public Duration(String inDuration, boolean inDismissable,
                    Optional<String> inText)
    {
      m_durationText = Optional.of(inDuration);
      m_duration = Optional.absent();
      m_levels = Optional.absent();
      m_plusDuration = Optional.absent();
      m_dismissable = inDismissable;
      m_text = inText;
    }

    /**
     * Create a spell duration with a proper duration.
     *
     * @param inDuration the base duration of the effect
     * @param inLevels the number of levels per duration
     * @param inPlusDuration some additional duration, if avaiable
     * @param inDismissable whether the spell is dismissable
     * @param inText additional descriptions for the duration
     */
    public Duration(
        net.ixitxachitls.dma.values.Duration inDuration,
        Optional<String> inLevels,
        Optional<net.ixitxachitls.dma.values.Duration> inPlusDuration,
        boolean inDismissable, Optional<String> inText)
    {
      m_durationText = Optional.absent();
      m_duration = Optional.of(inDuration);
      m_levels = inLevels;
      m_plusDuration = inPlusDuration;
      m_dismissable = inDismissable;
      m_text = inText;
    }

    /** The duration text. */
    private final Optional<String> m_durationText;

    /** The time duration. */
    private final Optional<net.ixitxachitls.dma.values.Duration> m_duration;

    /** The levels per duration. */
    private final Optional<String> m_levels;

    /** Some additional timed duration, if needed. */
    private final Optional<net.ixitxachitls.dma.values.Duration> m_plusDuration;

    /** Whether the spell is dismissable. */
    private final boolean m_dismissable;

    /** Additional text descripiton for the duration. */
    private final Optional<String> m_text;

    /** The parser for the duration. */
    public static final Parser<Duration> PARSER =
      new Parser<Duration>(5)
      {
        @Override
        public Optional<Duration> doParse(String inDuration, String inLevels,
                                          String inPlusDuration,
                                          String inDismissable,
                                          String inText)
        {
          Optional<net.ixitxachitls.dma.values.Duration> duration =
              net.ixitxachitls.dma.values.Duration.PARSER.parse(inDuration);
          if(!duration.isPresent())
          {
            return Optional.of(new Duration
                               (inDuration
                                + (inLevels.isEmpty() ? "" : "/" + inLevels)
                                + (inPlusDuration.isEmpty()
                                   ? "" : " " + inPlusDuration),
                                   !inDismissable.isEmpty(),
                                   Optional.fromNullable(inText)));
          }

          return Optional.of(new Duration
                             (duration.get(),
                              Optional.of(inLevels),
                              net.ixitxachitls.dma.values.Duration.PARSER.parse(
                                  inPlusDuration),
                              !inDismissable.isEmpty(), Optional.of(inText)));

        }
      };

    /**
     * Get the text used for the duration.
     *
     * @return the text
     */
    public Optional<String> getDurationText()
    {
      return m_durationText;
    }

    /**
     * Get the duration time for the spell.
     *
     * @return the duration as time
     */
    public Optional<net.ixitxachitls.dma.values.Duration> getDuration()
    {
      return m_duration;
    }

    /**
     * Get the description on how much the duration changes per level.
     *
     * @return the levels
     */
    public Optional<String> getLevels()
    {
      return m_levels;
    }

    /**
     * Get the additional duration.
     *
     * @return the additional duration
     */
    public Optional<net.ixitxachitls.dma.values.Duration> getPlusDuration()
    {
      return m_plusDuration;
    }

    /**
     * Get whether the spell is dismissable or not.
     *
     * @return true of dismissable, false if not
     */
    public boolean getDismissable()
    {
      return m_dismissable;
    }

    /**
     * Get the descriptive text for the duration.
     *
     * @return the text
     */
    public Optional<String> getText()
    {
      return m_text;
    }

    @Override
    public String toString()
    {
      if(m_durationText.isPresent())
        return m_durationText.get()
            + (m_dismissable ? " (D)" : "")
            + (m_text.isPresent() ? ", " + m_text.get() : "");

      return m_duration.get()
          + (m_levels.isPresent() && !m_levels.get().isEmpty()
          ? "/" + m_levels.get() : "")
          + (m_plusDuration.isPresent() ? " + " + m_plusDuration.get() : "")
          + (m_dismissable ? " (D)" : "")
          + (m_text.isPresent() ? ", " + m_text.get() : "");
    }

    public String toShortString(int level)
    {
      if(m_durationText.isPresent())
        return m_durationText.get()
            + (m_dismissable ? " (D)" : "")
            + (m_text.isPresent() ? ", " + m_text.get() : "");

      net.ixitxachitls.dma.values.Duration duration = m_duration.get();
      try
      {
        if(m_levels.isPresent() && !m_levels.get().isEmpty())
          duration = (net.ixitxachitls.dma.values.Duration)
              duration.multiply(Integer.parseInt(m_levels.get()));
      }
      catch(NumberFormatException e)
      {
        Log.warning("Invalid number of levels for duration encountered: "
                        + m_levels.get());
      }

      return duration.toShortString()
          + (m_plusDuration.isPresent() ?
            " + " + m_plusDuration.get().toShortString() : "")
          + (m_dismissable ? " (D)" : "")
          + (m_text.isPresent() && !m_text.get().isEmpty()
            ? ", " + m_text.get() : "");
    }
  }

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

  /** The possible level measurements. */
  static final String []LEVELS =
    Config.get("/game/levels", new String []
      {
        "level",
        "2 level",
        "3 level",
      });

  /** The possible spell duration. */
  static final String []SPELL_DURATION_FLAGS =
    Config.get("/game/spell.duration.flags", new String []
      {
        "(D)",
      });

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

  /**
   * This is the internal, default constructor for an undefined value.
   */
  protected BaseSpell()
  {
    super(TYPE);
  }

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base spell
   */
  public BaseSpell(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final BaseType<BaseSpell> TYPE =
    new BaseType.Builder<>(BaseSpell.class).build();

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The school of the spell. */
  protected School m_school = School.UNKNOWN;

  /** The subschools of the spell. */
  protected List<Subschool> m_subschools = new ArrayList<>();

  /** The summary text for the spell. */
  protected Optional<String> m_summary = Optional.absent();

  /** The spell descriptor. */
  protected List<SpellDescriptor> m_descriptors = new ArrayList<>();

  /** The spell levels. */
  protected List<Level> m_levels = new ArrayList<>();

  /** The various components. */
  protected List<SpellComponent> m_components = new ArrayList<>();

  /** The specific material components. */
  protected List<Material> m_materials = new ArrayList<>();

  /** The specific material components. */
  protected Optional<Material> m_focus = Optional.absent();

  /** The casting time required for this spell. */
  protected Optional<net.ixitxachitls.dma.values.Duration> m_castingTime =
      Optional.absent();

  /** The range of the spell. */
  protected SpellRange m_range = SpellRange.UNKNOWN;

  /** The distance the spell works at. */
  protected Optional<Distance> m_distance = Optional.absent();

  /** The target of the spell. */
  protected Optional<Effect> m_effect = Optional.absent();

  /** The target of the spell. */
  protected Optional<String> m_target = Optional.absent();

  /** The area of the spell. */
  protected Optional<String> m_area = Optional.absent();

  /** The duration of the spell. */
  protected Optional<Duration> m_duration = Optional.absent();

  /** The saving throw for the spell. */
  protected Optional<String> m_savingThrow = Optional.absent();

  /** The spell resistance for the spell. */
  protected Optional<String> m_resistance = Optional.absent();

  /**
   * Get the school of the spell.
   *
   * @return the school
   */
  public School getSchool()
  {
    return m_school;
  }

  /**
   * Get all the subschools of the spell.
   *
   * @return the sub schools
   */
  public List<Subschool> getSubschools()
  {
    return m_subschools;
  }

  /**
   * Get the spell summary.
   *
   * @return the summary
   */
  public Optional<String> getSummary()
  {
    return m_summary;
  }

  /**
   * Get all the spell descriptors.
   *
   * @return the spell descriptors
   */
  public List<SpellDescriptor> getDescriptors()
  {
    return m_descriptors;
  }

  /**
   * Get all the levels the spell has.
   *
   * @return all the levels
   */
  public List<Level> getLevels()
  {
    return m_levels;
  }

  /**
   * Get all the components for the spell.
   *
   * @return the spell components
   */
  public List<SpellComponent> getComponents()
  {
    return m_components;
  }

  /**
   * Get all the materials needed for casting the spell.
   *
   * @return the material components
   */
  public List<Material> getMaterials()
  {
    return m_materials;
  }

  /**
   * Get all the foci needed for casting the spell.
   *
   * @return the material focus
   */
  public Optional<Material> getFocus()
  {
    return m_focus;
  }

  /**
   * Get the casting time for the spell.
   *
   * @return the casting time
   */
  public Optional<net.ixitxachitls.dma.values.Duration> getCastingTime()
  {
    return m_castingTime;
  }

  /**
   * Get the spell's range.
   *
   * @return the spell range
   */
  public SpellRange getRange()
  {
    return m_range;
  }

  /**
   * Get the distance the spell can be cast to.
   *
   * @return the spell distance
   */
  public Optional<Distance> getDistance()
  {
    return m_distance;
  }

  /**
   * Get the effect of the spell.
   *
   * @return the spell effect.
   */
  public Optional<Effect> getEffect()
  {
    return m_effect;
  }

  /**
   * Get the spell target.
   *
   * @return the spell target
   */
  public Optional<String> getTarget()
  {
    return m_target;
  }

  /**
   * Get the area of the spell effect.
   *
   * @return the spell area
   */
  public Optional<String> getArea()
  {
    return m_area;
  }

  /**
   * Get the duration of the spell.
   *
   * @return the spell duration
   */
  public Optional<Duration> getDuration()
  {
    return m_duration;
  }

  /**
   * Get the saving throw for the spell.
   *
   * @return the saving throw
   */
  public Optional<String> getSavingThrow()
  {
    return m_savingThrow;
  }

  /**
   * Get the resistance information about the spell.
   *
   * @return the spell resistance information
   */
  public Optional<String> getResistance()
  {
    return m_resistance;
  }

  /**
   * Get a summary for the entry.
   *
   * @param       inParameters optional parametrs to further specifiy the entry
   *                           (for spells the first argument is the class and
   *                            level and the second is the dc)
   *
   * @return      the string with the summary
   */
  /*
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
  */

  /**
   * Get the summary of the spell description, with appropriate level and
   * other computations done.
   *
   * @param       inKind  the kind of spellcaster
   * @param       inLevel the spell level to compute for
   * @param       inDC    the DC for the spell to compute
   *
   * @return      a string with the summary
   */
  /*
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
  */

  /**
   * Get a summary for the entry, using the given parameters.
   *
   * @param       inParameters  the parameters to modify the summary
   *
   * @return      the string with the summary
   */
  /*
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
      switch(((EnumSelection<SpellRange>)m_range.get()).getSelected())
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
  */

  /**
   * Check whether the given user is the DM for this entry. Every user is a DM
   * for a base campaign.
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

    return inUser.get().hasAccess(Group.ADMIN);
  }

  @Override
  public void setValues(Values inValues)
  {
    super.setValues(inValues);

    m_school = inValues.use("school", m_school, School.PARSER);
    m_subschools = inValues.use("subschool", m_subschools, Subschool.PARSER);
    m_summary = inValues.use("summary", m_summary);
    m_descriptors = inValues.use("descriptor", m_descriptors,
                                 SpellDescriptor.PARSER);
    m_levels = inValues.use("level", m_levels, Level.PARSER, "class", "level");
    m_components = inValues.use("component", m_components,
                                SpellComponent.PARSER);
    m_materials = inValues.use("material", m_materials, Material.PARSER,
                               "use", "components");
    m_focus = inValues.use("focus", m_focus, Material.PARSER,
                           "use", "components");
    m_castingTime = inValues.use("casting_time", m_castingTime,
                                 net.ixitxachitls.dma.values.Duration.PARSER);
    m_range = inValues.use("range", m_range, SpellRange.PARSER);
    m_distance = inValues.use("distance", m_distance, Distance.PARSER);
    m_effect = inValues.use("effect", m_effect, Effect.PARSER,
                            "distance", "effect", "text");
    m_target = inValues.use("target", m_target);
    m_area = inValues.use("area", m_area);
    m_duration = inValues.use("duration", m_duration, BaseSpell.Duration.PARSER,
                              "duration", "levels", "plus", "dismissable",
                              "text");
    m_savingThrow = inValues.use("saving_throw", m_savingThrow);
    m_resistance = inValues.use("resistance", m_resistance);
  }

  @Override
  public Message toProto()
  {
    BaseSpellProto.Builder builder = BaseSpellProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    if(m_school != School.UNKNOWN)
    {
      builder.setSchool(m_school.toProto());
      for(Subschool subschool : m_subschools)
          builder.addSubschool(subschool.toProto());
    }

    if(m_summary.isPresent())
      builder.setSummary(m_summary.get());

    for(SpellDescriptor descriptor : m_descriptors)
        builder.addDescriptor(descriptor.toProto());

    for(Level level : m_levels)
        builder.addLevel
          (BaseSpellProto.Level.newBuilder()
           .setSpellClass(level.getSpellClass().toProto())
           .setLevel(level.getLevel())
           .build());

    for(SpellComponent components : m_components)
        builder.addComponents(components.toProto());

    for(Material material : m_materials)
      builder.addMaterial(BaseSpellProto.Material.newBuilder()
                          .setUse(material.getUse())
                          .addAllComponent(material.getComponents())
                          .build());

    if(m_focus.isPresent())
    builder.setFocus(BaseSpellProto.Material.newBuilder()
                     .setUse(m_focus.get().getUse())
                     .addAllComponent(m_focus.get().getComponents())
                     .build());

    if(m_castingTime.isPresent())
      builder.setCastingTime(m_castingTime.get().toProto());

    if(m_range != SpellRange.UNKNOWN)
      builder.setSpecialRange(m_range.toProto());

    if(m_distance.isPresent())
      builder.setRange(m_distance.get().toProto());

    if(m_effect.isPresent())
    {
      BaseSpellProto.Effect.Builder effect = BaseSpellProto.Effect.newBuilder();

      System.out.println(m_effect.get().getDistance());
      if(m_effect.get().getDistance().isPresent())
        effect.setDistance(m_effect.get().getDistance().get().toProto());
      if(m_effect.get().getEffect().isPresent())
        effect.setType(m_effect.get().getEffect().get().toProto());

      effect.setDescription(m_effect.get().getText());
      builder.setEffect(effect.build());
    }

    if(m_target.isPresent())
      builder.setTarget(m_target.get());

    if(m_area.isPresent())
      builder.setArea(m_area.get());

    if(m_duration.isPresent())
    {
      BaseSpellProto.Duration.Builder duration =
        BaseSpellProto.Duration.newBuilder();

      if(m_duration.get().getDurationText().isPresent())
        duration.setDurationDescription
            (m_duration.get().getDurationText().get());
      else
      {
        duration.setDuration(m_duration.get().getDuration().get().toProto());
        if(m_duration.get().getLevels().isPresent())
          duration.setLevels(m_duration.get().getLevels().get());
        if(m_duration.get().getPlusDuration().isPresent())
          duration.setAdditionalDuration(m_duration.get().getPlusDuration()
                                         .get().toProto());
      }

      if(m_duration.get().getDismissable())
        duration.setFlags("(D)");

      if(m_duration.get().getText().isPresent())
        duration.setDescription(m_duration.get().getText().get());

      builder.setDuration(duration.build());
    }

    if(m_savingThrow.isPresent())
      builder.setSavingThrow(m_savingThrow.get());

    if(m_resistance.isPresent())
      builder.setSpellResistance(m_resistance.get());

    BaseSpellProto proto = builder.build();
    return proto;
  }

  /**
   * Set all the values from the given proto to this entry.
   *
   * @param inProto the proto to read from
   */
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
      m_school = School.fromProto(proto.getSchool());

    for (BaseSpellProto.Subschool subschool : proto.getSubschoolList())
      m_subschools.add(Subschool.fromProto(subschool));

    if(proto.hasSummary())
      m_summary = Optional.of(proto.getSummary());

    for(BaseSpellProto.Descriptor descriptor : proto.getDescriptorList())
      m_descriptors.add(SpellDescriptor.fromProto(descriptor));

    for(BaseSpellProto.Level level : proto.getLevelList())
      m_levels.add(new Level(SpellClass.fromProto(level.getSpellClass()),
                             level.getLevel()));

    for(BaseSpellProto.Components component : proto.getComponentsList())
      m_components.add(SpellComponent.fromProto(component));

    for(BaseSpellProto.Material material : proto.getMaterialList())
      m_materials.add(new Material(material.getUse(),
                                   material.getComponentList()));

    if(proto.hasFocus())
      m_focus = Optional.of(new Material(proto.getFocus().getUse(),
                                         proto.getFocus().getComponentList()));

    if(proto.hasCastingTime())
      m_castingTime =
        Optional.of(net.ixitxachitls.dma.values.Duration.fromProto(
            proto.getCastingTime()));

    if(proto.hasSpecialRange())
      m_range = SpellRange.fromProto(proto.getSpecialRange());

    if(proto.hasRange())
      m_distance = Optional.of(Distance.fromProto(proto.getRange()));

    if(proto.hasEffect())
      m_effect = Optional.of
        (new Effect(proto.getEffect().hasDistance()
                    ? Optional.of(Distance.fromProto
            (proto.getEffect().getDistance()))
                    : Optional.<Distance>absent(),
                    proto.getEffect().hasType()
                    ? Optional.of(SpellEffect.fromProto
                                  (proto.getEffect().getType()))
                    : Optional.<SpellEffect>absent(),
                    proto.getEffect().getDescription()));

    if(proto.hasTarget())
      m_target = Optional.of(proto.getTarget());

    if(proto.hasArea())
      m_area = Optional.of(proto.getArea());

    if(proto.hasDuration())
      if(proto.getDuration().hasDurationDescription())
        m_duration = Optional.of
          (new Duration(proto.getDuration().getDurationDescription(),
                        proto.getDuration().hasFlags(),
                        proto.getDuration().hasDescription()
                        ? Optional.of(proto.getDuration().getDescription())
                        : Optional.<String>absent()));
      else
        m_duration = Optional.of
          (new Duration(net.ixitxachitls.dma.values.Duration.fromProto(
              proto.getDuration().getDuration()),
                        proto.getDuration().hasLevels()
                            ? Optional.of(proto.getDuration().getLevels())
                            : Optional.<String>absent(),
                        proto.getDuration().hasAdditionalDuration()
                            ? Optional.of
                            (net.ixitxachitls.dma.values.Duration.fromProto(
                                proto.getDuration().getAdditionalDuration()))
                            : Optional.<net.ixitxachitls.dma.values.Duration>
                            absent(),
                        proto.getDuration().hasFlags(),
                        proto.getDuration().hasDescription()
                            ? Optional.of(proto.getDuration().getDescription())
                            : Optional.<String>absent()));

    if(proto.hasSavingThrow())
      m_savingThrow = Optional.of(proto.getSavingThrow());

    if(proto.hasSpellResistance())
      m_resistance = Optional.of(proto.getSpellResistance());
  }

  @Override
  protected Message defaultProto()
  {
    return BaseSpellProto.getDefaultInstance();
  }

  //---------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }
}
