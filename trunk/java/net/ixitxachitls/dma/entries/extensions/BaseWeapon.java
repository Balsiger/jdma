/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.entries.extensions;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.values.Critical;
import net.ixitxachitls.dma.values.Damage;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Group;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Rational;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the weapon extension for all the entries.
 *
 * @file          BaseWeapon.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class BaseWeapon extends BaseExtension<BaseItem>
{
  //----------------------------------------------------------------- nested

  //----- types ------------------------------------------------------------

  /** The possible weapon types. */
  public enum Type implements EnumSelection.Named, EnumSelection.Short
  {
    /** A piercing OR slashing weapon. */
    PIERCING_OR_SLASHING("Piercing or Slashing", "P or S"),

    /** A bludeoning OR piercing weapon. */
    BLUDGEONING_OR_PIERCING("Bludgeoning or Piercing", "B or P"),

    /** A bludeoning AND piercing weapon. */
    BLUDGEONING_AND_PIERCING("Bludgeoning and Piercing", "B and P"),

    /** A slashing OR piercing weapon. */
    SLASHING_OR_PIERCING("Slashing or Piercing", "S or P"),

    /** A slashing weapon. */
    SLASHING("Slashing", "S"),

    /** A bludgeoning weapon. */
    BLUDGEONING("Bludgeoning", "B"),

    /** A piercing weapon. */
    PIERCING("Piercing", "P"),

    /** A grenade. */
    GRENADE("Grenade", "G"),

    /** No type. */
    NONE("None", "N");

    /** The value's name. */
    private String m_name;

    /** The value's short name. */
    private String m_short;

    /** Create the name.
     *
     * @param inName     the name of the value
     *
     */
    private Type(String inName, String inShort)
    {
      m_name = constant("weapon.types", inName);
      m_short = constant("wepon.types.short", inShort);
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

    /**
     * Get the name of the value.
     *
     * @return the name of the value
     */
    @Override
    public String toString()
    {
      return m_name;
    }

    /**
     * Get the short name of the value.
     *
     * @return the short name of the value
     */
    public String getShort()
    {
      return m_short;
    }
  };

  //........................................................................
  //----- styles -----------------------------------------------------------

  /** The possible weapon styles. */
  public enum Style implements EnumSelection.Named, EnumSelection.Short
  {
    /** A two-handed melee weapon. */
    TWOHANDED_MELEE("Two-Handed Melee", "Two", true, 0),

    /** A one-handed melee weapon. */
    ONEANDED_MELEE("One-Handed Melee", "One", true, -1),

    /** A light melee weapon. */
    LIGHT_MELEE("Light Melee", "Light", true, -2),

    /** An unarmed 'weapon'. */
    UNARMED("Unarmed", "Unarmed", true, 0),

    /** A ranged touch weapon. */
    RANGED_TOUCH("Ranged Touch", "Touch R", false, 0),

    /** A ranged weapon. */
    RANGED("Ranged", "Ranged", false, 0),

    /** A thrown touch weapon. */
    THROWN_TOUCH("Thrown Touch", "Touch T", false, 0),

    /** A thrown weapon. */
    THROWN("Thrown", "Thrown", false, 0),

    /** A touch weapon. */
    TOUCH("Touch", "Touch", true, 0);

    /** The value's name. */
    private String m_name;

    /** The value's short name. */
    private String m_short;

    /** Flag if this is a range or melee weapon. */
    private boolean m_melee;

    /** The size difference between a normal item an a weapon. */
    private int m_sizeDifference;

    /**
     * Create the name.
     *
     * @param inName           the name of the value
     * @param inShort          the short name of the value
     * @param inMelee          true if this is a melee weapon, false for ranged
     * @param inSizeDifference the number of steps between this and medium
     *
     */
    private Style(String inName, String inShort, boolean inMelee,
                  int inSizeDifference)
    {
      m_name           = constant("weapon.types", inName);
      m_short          = constant("weapon.types.short", inShort);
      m_melee          = inMelee;
      m_sizeDifference = inSizeDifference;
    }

    /**
     * Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String getName()
    {
      return m_name;
    }

    /**
     * Get the name of the value.
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
     * Get the short name of the value.
     *
     * @return the short name of the value
     */
    public String getShort()
    {
      return m_short;
    }

    /**
     * Check if the weapon style is ranged for melee.
     *
     * @return true if the weapon is a melee weapon, false for ranged.
     *
     */
    public boolean isMelee()
    {
      return m_melee;
    }

    /**
     * Get the size difference.
     *
     * @return the number of steps between this and medium. */
    public int getSizeDifference()
    {
      return m_sizeDifference;
    }
  };

  //........................................................................
  //----- proficiencies ----------------------------------------------------

  /** The possible weapon proficiencies. */
  public enum Proficiency implements EnumSelection.Named
  {
    /** Proficiency for simple weapons. */
    SIMPLE("Simple"),

    /** Proficiency for simple weapons. */
    MARTIAL("Martial"),

    /** Proficiency for simple weapons. */
    EXOTIC("Exotic"),

    /** Proficiency for simple weapons. */
    IMPROVISED("Improvised"),

    /** Proficiency for simple weapons. */
    NONE("None");

    /** The value's name. */
    private String m_name;

    /** Create the name.
     *
     * @param inName     the name of the value
     *
     */
    private Proficiency(String inName)
    {
      m_name = constant("weapon.proficiencies", inName);
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
     * @return the name of the value
     *
     */
    @Override
    public String toString()
    {
      return m_name;
    }
  };

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------- BaseWeapon -----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base entry attached to
   * @param       inName  the name of the extension
   *
   */
  public BaseWeapon(BaseItem inEntry, String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //------------------------------- BaseWeapon -----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base entry attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   * @undefined   never
   *
   */
  // public BaseWeapon(BaseItem inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  //----- damage -----------------------------------------------------------

  /** The damage the weapon inflicts. */
  @Key("damage")
  protected Damage m_damage = new Damage()
    .withIndexBase(BaseItem.TYPE);

  static
  {
    addIndex(new Index(Index.Path.DAMAGES, "Damages", BaseItem.TYPE));
    addIndex(new Index(Index.Path.DAMAGE_TYPES, "Damage Types", BaseItem.TYPE));
  }

  //........................................................................
  //----- secondary damage -------------------------------------------------

  /** The damage the weapon inflicts. */
  @Key("secondary damage")
  protected Damage m_secondaryDamage = new Damage()
    .withIndexBase(BaseItem.TYPE);

  //........................................................................
  //----- splash -----------------------------------------------------------

  /** The splash damage the weapon inflicts (if any). */
  @Key("splash")
  protected Damage m_splash =
    new Damage()
    .withIndexBase(BaseItem.TYPE)
    .withEditType("name[splash]");

  //........................................................................
  //----- type -------------------------------------------------------------

  /** The type of the weapon damage. */
  @Key("weapon type")
  protected EnumSelection<Type> m_type = new EnumSelection<Type>(Type.class)
    .withTemplate("link", "weapontypes");

  static
  {
    addIndex(new Index(Index.Path.WEAPON_TYPES, "Weapon Types", BaseItem.TYPE));
  }

  //........................................................................
  //----- critical ---------------------------------------------------------

  /** The critical range. */
  @Key("critical")
  protected Critical m_critical = new Critical()
    .withIndexBase(BaseItem.TYPE)
    .withTemplate("link", "criticals");

  static
  {
    addIndex(new Index(Index.Path.CRITICALS, "Criticals", BaseItem.TYPE));
    addIndex(new Index(Index.Path.THREATS, "Threat Ranges", BaseItem.TYPE));
  }

  //........................................................................
  //----- style ------------------------------------------------------------

  /** The style of the weapon (for a medium character). */
  @Key("weapon style")
  protected EnumSelection<Style> m_style =
    new EnumSelection<Style>(Style.class).withTemplate("link", "weaponstyles");

  static
  {
    addIndex(new Index(Index.Path.STYLES, "Weapon Styles", BaseItem.TYPE));
  }

  //........................................................................
  //----- proficiency ------------------------------------------------------

  /** The proficiency required for the weapon. */
  @Key("proficiency")
  protected EnumSelection<Proficiency> m_proficiency =
    new EnumSelection<Proficiency>(Proficiency.class)
    .withTemplate("link", "proficiencies");

  static
  {
    addIndex(new Index(Index.Path.PROFICIENCIES, "Weapon Proficiencies",
                       BaseItem.TYPE));
  }

  //........................................................................
  //----- range ------------------------------------------------------------

  /** The grouping for ranges. */
  protected static final Group<Distance, Long, String> s_rangeGrouping =
    new Group<Distance, Long, String>(new Group.Extractor<Distance, Long>()
    {
      @Override
      public Long extract(Distance inValue)
      {
        if(inValue == null)
          throw new IllegalArgumentException("must have a value here");

        return (long)inValue.getAsFeet().getValue();
      }
    }, new Long [] { 1L, 10L, 20L, 30L, 40L, 50L, 60L, 70L, 80L, 90L, 100L,
                     110L, 120L, 150L, 200L, }, new String []
      { "1", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100",
        "110", "120", "150", "200", "Infinite", }, "$undefined$");

  /** The range increment, if any, for this weapon. */
  @Key("range increment")
  protected Distance m_range =
    new Distance()
    .withGrouping(s_rangeGrouping)
    .withTemplate("link", "ranges");

  static
  {
    addIndex(new Index(Index.Path.RANGES, "Ranges", BaseItem.TYPE));
  }

  //........................................................................
  //----- reach ------------------------------------------------------------

  /** The grouping for reaches. */
  protected static final Group<Distance, Long, String> s_reachGrouping =
    new Group<Distance, Long, String>(new Group.Extractor<Distance, Long>()
      {
        @Override
        public Long extract(Distance inValue)
        {
          if(inValue == null)
            throw new IllegalArgumentException("must have a value here");

          return (long)inValue.getAsFeet().getValue();
        }
      }, new Long [] { 5L, 10L, 15L, 20L, 25L, }, new String []
        { "5 ft", "10 ft", "15 ft", "20 ft", "25 ft", "Much", },
                                      "$undefined$");

  /** The reach of the weapon. */
  @Key("reach")
  protected Distance m_reach =
    new Distance(null, new Rational(), null, false)
    .withGrouping(s_reachGrouping)
    .withTemplate("link", "reaches");

  static
  {
    addIndex(new Index(Index.Path.REACHES, "Weapon Reaches", BaseItem.TYPE));
  }

  //........................................................................
  //----- max attacks ------------------------------------------------------

  /** The maximal number of attacks per round. */
  @Key("max attacks")
  protected Number m_maxAttacks = new Number(1, 10);

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getDamage -------------------------------

  /**
   * Get the damage value.
   *
   * @return      the damage value
   *
   */
  public Damage getDamage()
  {
    return m_damage;
  }

  //........................................................................
  //--------------------------- getSecondaryDamage--------------------------

  /**
   * Get the secondary damage value.
   *
   * @return      the secondary damage value
   *
   */
  public Damage getSecondaryDamage()
  {
    return m_secondaryDamage;
  }

  //........................................................................

  //------------------------- computeIndexValues ---------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @param       ioValues a multi map of values per index name
   *
   */
  @Override
  public void computeIndexValues(Multimap<Index.Path, String> ioValues)
  {
    super.computeIndexValues(ioValues);

    // damages
    for(Damage damage = m_damage; damage != null; damage = damage.next())
    {
      ioValues.put(Index.Path.DAMAGES, damage.getBaseNumber() + "d"
                   + damage.getBaseDice());

      Damage.Type type = damage.getType();
      if(type != null)
        ioValues.put(Index.Path.DAMAGE_TYPES, type.toString());
    }

    if(m_secondaryDamage.isDefined())
      for(Damage damage = m_secondaryDamage; damage != null;
          damage = damage.next())
      {
        ioValues.put(Index.Path.DAMAGES, damage.getBaseNumber() + "d"
                     + damage.getBaseDice());

        Damage.Type type = damage.getType();
        if(type != null)
          ioValues.put(Index.Path.DAMAGE_TYPES, type.toString());
      }

    if(m_splash.isDefined())
    {
      ioValues.put(Index.Path.DAMAGES, m_splash.getBaseNumber() + "d"
                   + m_splash.getBaseDice());

      Damage.Type type = m_splash.getType();
      if(type != null)
        ioValues.put(Index.Path.DAMAGE_TYPES, type.toString());
    }

    // criticals
    if(m_critical.getMultiplier() == 1)
      ioValues.put(Index.Path.CRITICALS, "None");
    else
      ioValues.put(Index.Path.CRITICALS, "x" + m_critical.group());

    ioValues.put(Index.Path.THREATS, m_critical.getThreatRange().toString());
    ioValues.put(Index.Path.WEAPON_TYPES, m_type.group());
    ioValues.put(Index.Path.WEAPON_STYLES, m_style.group());
    ioValues.put(Index.Path.PROFICIENCIES, m_proficiency.group());
    ioValues.put(Index.Path.RANGES, m_range.group());
    ioValues.put(Index.Path.REACHES, m_reach.toString());
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}