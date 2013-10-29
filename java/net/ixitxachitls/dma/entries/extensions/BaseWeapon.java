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
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseWeaponProto;
import net.ixitxachitls.dma.values.Critical;
import net.ixitxachitls.dma.values.Damage;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Group;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Rational;
import net.ixitxachitls.util.logging.Log;

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

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /** The possible weapon types. */
  public enum Type implements EnumSelection.Named, EnumSelection.Short,
    EnumSelection.Proto<BaseWeaponProto.Type>
  {
    /** A piercing OR slashing weapon. */
    PIERCING_OR_SLASHING("Piercing or Slashing", "P or S",
                         BaseWeaponProto.Type.PIERCING_OR_SLASHING),

    /** A bludeoning OR piercing weapon. */
    BLUDGEONING_OR_PIERCING("Bludgeoning or Piercing", "B or P",
                            BaseWeaponProto.Type.BLUDGEONING_OR_PIERCING),

    /** A bludeoning AND piercing weapon. */
    BLUDGEONING_AND_PIERCING("Bludgeoning and Piercing", "B and P",
                             BaseWeaponProto.Type.BLUDGEONING_AND_PIERCING),

    /** A slashing OR piercing weapon. */
    SLASHING_OR_PIERCING("Slashing or Piercing", "S or P",
                         BaseWeaponProto.Type.SLASHING_OR_PIERCING),

    /** A slashing weapon. */
    SLASHING("Slashing", "S", BaseWeaponProto.Type.SLASHING),

    /** A bludgeoning weapon. */
    BLUDGEONING("Bludgeoning", "B", BaseWeaponProto.Type.BLUDGEONING),

    /** A piercing weapon. */
    PIERCING("Piercing", "P", BaseWeaponProto.Type.PIERCING),

    /** A grenade. */
    GRENADE("Grenade", "G", BaseWeaponProto.Type.GRENADE),

    /** No type. */
    NONE("None", "N", BaseWeaponProto.Type.NONE);

    /** The value's name. */
    private String m_name;

    /** The value's short name. */
    private String m_short;

    /** The proto enum value. */
    private BaseWeaponProto.Type m_proto;

    /** Create the name.
     *
     * @param inName     the name of the value
     * @param inShort    the short name of the value
     * @param inProto    the proto enum value
     */
    private Type(String inName, String inShort, BaseWeaponProto.Type inProto)
    {
      m_name = constant("weapon.types", inName);
      m_short = constant("wepon.types.short", inShort);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public String getShort()
    {
      return m_short;
    }

    @Override
    public BaseWeaponProto.Type toProto()
    {
      return m_proto;
    }

    /**
     * Convert the proto enum to its enum value.
     *
     * @param inProto  the proto enum value
     * @return the converted enum value
     */
    public static Type fromProto(BaseWeaponProto.Type inProto)
    {
      for(Type type : values())
        if(type.m_proto == inProto)
          return type;

      throw new IllegalStateException("cannot convert weapon type proto: "
        + inProto);
    }
  };

  //........................................................................
  //----- styles -----------------------------------------------------------

  /** The possible weapon styles. */
  public enum Style implements EnumSelection.Named, EnumSelection.Short,
    EnumSelection.Proto<BaseWeaponProto.Style>
  {
    /** A two-handed melee weapon. */
    TWOHANDED_MELEE("Two-Handed Melee", "Two", true, 0,
                    BaseWeaponProto.Style.TWOHANDED_MELEE),

    /** A one-handed melee weapon. */
    ONEANDED_MELEE("One-Handed Melee", "One", true, -1,
                   BaseWeaponProto.Style.ONEHANDED_MELEE),

    /** A light melee weapon. */
    LIGHT_MELEE("Light Melee", "Light", true, -2,
                BaseWeaponProto.Style.LIGHT_MELEE),

    /** An unarmed 'weapon'. */
    UNARMED("Unarmed", "Unarmed", true, 0, BaseWeaponProto.Style.UNARMED),

    /** A ranged touch weapon. */
    RANGED_TOUCH("Ranged Touch", "Touch R", false, 0,
                 BaseWeaponProto.Style.RANGED_TOUCH),

    /** A ranged weapon. */
    RANGED("Ranged", "Ranged", false, 0, BaseWeaponProto.Style.RANGED),

    /** A thrown touch weapon. */
    THROWN_TOUCH("Thrown Touch", "Touch T", false, 0,
                 BaseWeaponProto.Style.THROWN_TOUCH),

    /** A thrown weapon. */
    THROWN("Thrown", "Thrown", false, 0, BaseWeaponProto.Style.THROWN),

    /** A touch weapon. */
    TOUCH("Touch", "Touch", true, 0, BaseWeaponProto.Style.TOUCH);

    /** The value's name. */
    private String m_name;

    /** The value's short name. */
    private String m_short;

    /** Flag if this is a range or melee weapon. */
    private boolean m_melee;

    /** The size difference between a normal item an a weapon. */
    private int m_sizeDifference;

    /** The corresponding proto value. */
    private BaseWeaponProto.Style m_proto;

    /**
     * Create the name.
     *
     * @param inName           the name of the value
     * @param inShort          the short name of the value
     * @param inMelee          true if this is a melee weapon, false for ranged
     * @param inSizeDifference the number of steps between this and medium
     * @param inProto          the corresponding proto value
     */
    private Style(String inName, String inShort, boolean inMelee,
                  int inSizeDifference, BaseWeaponProto.Style inProto)
    {
      m_name = constant("weapon.types", inName);
      m_short = constant("weapon.types.short", inShort);
      m_melee = inMelee;
      m_sizeDifference = inSizeDifference;
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public String getShort()
    {
      return m_short;
    }

    /**
     * Check if the weapon style is ranged for melee.
     *
     * @return true if the weapon is a melee weapon, false for ranged.
     */
    public boolean isMelee()
    {
      return m_melee;
    }

    /**
     * Get the size difference.
     *
     * @return the number of steps between this and medium.
     */
    public int getSizeDifference()
    {
      return m_sizeDifference;
    }

    @Override
    public BaseWeaponProto.Style toProto()
    {
      return m_proto;
    }

    /**
     * Convert the proto value to the corresponding enum value.
     *
     * @param   inProto the proto value
     * @return  the converted enum value
     */
    public static Style fromProto(BaseWeaponProto.Style inProto)
    {
      for(Style style : values())
        if(style.m_proto == inProto)
          return style;

      throw new IllegalStateException("unknown weapon style: " + inProto);
    }
  }

  //........................................................................
  //----- proficiencies ----------------------------------------------------

  /** The possible weapon proficiencies. */
  public enum Proficiency implements EnumSelection.Named,
    EnumSelection.Proto<BaseWeaponProto.Proficiency>
  {
    /** Proficiency for simple weapons. */
    SIMPLE("Simple", BaseWeaponProto.Proficiency.SIMPLE),

    /** Proficiency for simple weapons. */
    MARTIAL("Martial", BaseWeaponProto.Proficiency.MARTIAL),

    /** Proficiency for simple weapons. */
    EXOTIC("Exotic", BaseWeaponProto.Proficiency.EXOCTIC),

    /** Proficiency for simple weapons. */
    IMPROVISED("Improvised", BaseWeaponProto.Proficiency.IMPROVISED),

    /** Proficiency for simple weapons. */
    NONE("None", BaseWeaponProto.Proficiency.NONE_PROFICIENCY);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseWeaponProto.Proficiency m_proto;

    /**
     * Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the corresponding proto enum value
     */
    private Proficiency(String inName, BaseWeaponProto.Proficiency inProto)
    {
      m_name = constant("weapon.proficiencies", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public BaseWeaponProto.Proficiency toProto()
    {
      return m_proto;
    }

    /**
     * Convert the proto enum value to the corresponding enum value.
     *
     * @param inProto the proto value to convert
     * @return the corresponding enum value
     */
    public static Proficiency fromProto(BaseWeaponProto.Proficiency inProto)
    {
      for(Proficiency proficiency : values())
        if(proficiency.m_proto == inProto)
          return proficiency;

      throw new IllegalStateException("unknown weapon proficiency: " + inProto);
    }
  }

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

  /** The secondary damage the weapon inflicts. */
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
      /**
       *
       */
      private static final long serialVersionUID = 1L;

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
        /**
         *
         */
        private static final long serialVersionUID = 1L;

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

  @Override
  public Message toProto()
  {
    BaseWeaponProto.Builder builder = BaseWeaponProto.newBuilder();

    if(m_damage.isDefined())
      builder.setDamage(m_damage.toProto());
    if(m_secondaryDamage.isDefined())
      builder.setSecondaryDamage(m_secondaryDamage.toProto());
    if(m_splash.isDefined())
      builder.setSplash(m_splash.toProto());
    if(m_type.isDefined())
      builder.setType(m_type.getSelected().toProto());
    if(m_critical.isDefined())
      builder.setCritical(m_critical.toProto());
    if(m_style.isDefined())
      builder.setStyle(m_style.getSelected().toProto());
    if(m_proficiency.isDefined())
      builder.setProficiency(m_proficiency.getSelected().toProto());
    if(m_range.isDefined())
      builder.setRange(m_range.toProto());
    if(m_reach.isDefined())
      builder.setReach(m_reach.toProto());
    if(m_maxAttacks.isDefined())
      builder.setMaxAttacks((int)m_maxAttacks.get());

    return builder.build();
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseWeaponProto))
    {
      Log.warning("cannot parse base weapon proto " + inProto.getClass());
      return;
    }

    BaseWeaponProto proto = (BaseWeaponProto)inProto;

    if(proto.hasDamage())
      m_damage = m_damage.fromProto(proto.getDamage());
    if(proto.hasSecondaryDamage())
      m_secondaryDamage =
        m_secondaryDamage.fromProto(proto.getSecondaryDamage());
    if(proto.hasSplash())
      m_splash = m_splash.fromProto(proto.getSplash());
    if(proto.hasType())
      m_type = m_type.as(Type.fromProto(proto.getType()));
    if(proto.hasCritical())
      m_critical = m_critical.fromProto(proto.getCritical());
    if(proto.hasStyle())
      m_style = m_style.as(Style.fromProto(proto.getStyle()));
    if(proto.hasProficiency())
      m_proficiency =
        m_proficiency.as(Proficiency.fromProto(proto.getProficiency()));
    if(proto.hasRange())
      m_range = m_range.fromProto(proto.getRange());
    if(proto.hasReach())
      m_reach = m_reach.fromProto(proto.getReach());
    if(proto.hasMaxAttacks())
      m_maxAttacks = m_maxAttacks.as(proto.getMaxAttacks());
  }

  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
