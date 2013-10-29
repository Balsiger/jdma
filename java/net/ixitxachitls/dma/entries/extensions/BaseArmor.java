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

package net.ixitxachitls.dma.entries.extensions;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Multimap;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseArmorProto;
import net.ixitxachitls.dma.proto.Entries.BaseArmorProto.Type;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Group;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Percent;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the weapon extension for all the entries.
 *
 * @file          BaseArmor.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class BaseArmor extends BaseExtension<BaseItem>
{
  //----------------------------------------------------------------- nested

  //----- armor types ------------------------------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The possible armor types. */
  public enum ArmorTypes implements EnumSelection.Named,
    EnumSelection.Proto<BaseArmorProto.Type>
  {
    /** Light armor. */
    LIGHT("Light Armor", BaseArmorProto.Type.LIGHT),
    /** Medium armor. */
    MEDIUM("Medium Armor", BaseArmorProto.Type.MEDIUM),
    /** Heavy armor. */
    HEAVY("Heavy Armor", BaseArmorProto.Type.HEAVY),
    /** A shield. */
    SHIELD("Shield", BaseArmorProto.Type.SHIELD),
    /** A shield. */
    TOWER_SHIELD("Tower Shield", BaseArmorProto.Type.TOWER_SHIELD),
    /** A shield. */
    None("None", BaseArmorProto.Type.NONE);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseArmorProto.Type m_proto;

    /** Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the prot enum value
     */
    private ArmorTypes(String inName, BaseArmorProto.Type inProto)
    {
      m_name = constant("armor.types", inName);
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
    public Type toProto()
    {
      return m_proto;
    }

    /**
     * Convert the given proto into the corresponding enum value.
     *
     * @param inProto  the proto value to convert
     * @return  the converted enum value
     */
    public static ArmorTypes fromProto(BaseArmorProto.Type inProto)
    {
      for(ArmorTypes type : values())
        if(type.m_proto == inProto)
          return type;

      throw new IllegalArgumentException("cannot convert armor type: "
        + inProto);
    }
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------- BaseArmor -----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the entry associated with this extension
   * @param       inName  the name of the extension
   *
   */
  public BaseArmor(BaseItem inEntry, String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //------------------------------- BaseArmor -----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the entry associated with this extension
   * @param       inTag  the tag name for this instance
   * @param       inName the name of the extension
   *
   */
  // public BaseArmor(BaseItem inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  //----- ac bonus ---------------------------------------------------------

  /** The bonus of the armor. */
  @Key("AC bonus")
  @DM
  protected Modifier m_bonus = new Modifier()
    .withTemplate("link", Index.Path.ARMOR_BONUSES.getPath());

  static
  {
    addIndex(new Index(Index.Path.ARMOR_BONUSES, "Armor Bonuses",
                       BaseItem.TYPE));
  }

  //........................................................................
  //----- type -------------------------------------------------------------

  /** The type of the armor. */
  @Key("armor type")
  protected EnumSelection<ArmorTypes> m_type =
     new EnumSelection<ArmorTypes>(ArmorTypes.class)
    .withTemplate("link", Index.Path.ARMOR_TYPES.getPath());

  static
  {
    addIndex(new Index(Index.Path.ARMOR_TYPES, "Armor Types", BaseItem.TYPE));
  }

  //........................................................................
  //----- maximum dexterity ------------------------------------------------

  /** The grouping for max dex. */
  protected static final Group<Number, Long, String> s_maxDexGrouping =
    new Group<Number, Long, String>(new Group.Extractor<Number, Long>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Long extract(Number inValue)
        {
          return inValue.get();
        }
      }, new Long [] { 0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L,
                       12L, },
                                new String []
        { "+0", "+1", "+2", "+3", "+4", "+5", "+6", "+7", "+8", "+9", "+10",
          "+11", "+12", "Much", }, "$undefined$");

  /** The maximal dexterity bonus. */
  @Key("max dexterity")
  @DM
  protected Number m_maxDex = new Number(0, 30, true)
    .withGrouping(s_maxDexGrouping)
    .withTemplate("link", Index.Path.MAX_DEXTERITIES.getPath());

  static
  {
    addIndex(new Index(Index.Path.MAX_DEXTERITIES, "Max Dexterities",
                       BaseItem.TYPE));
  }

  //........................................................................
  //----- check penalty ----------------------------------------------------

  /** The grouping for max dex. */
  protected static final Group<Number, Long, String> s_penaltyGrouping =
    new Group<Number, Long, String>
    (new Group.Extractor<Number, Long>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Long extract(Number inValue)
        {
          return inValue.get();
        }
      }, new Long [] { -10L, -9L, -8L, -7L, -6L, -5L, -4L, -3L, -2L, -1L,
                       0L, },
                                    new String []
        { "-10", "-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1", "-0",
          "???", }, "$undefined$");

  /** The armor check penalty. */
  @Key("check penalty")
  @DM
  protected Number m_checkPenalty = new Number(-20, 0)
    .withGrouping(s_penaltyGrouping)
    .withTemplate("link", Index.Path.CHECK_PENALTIES.getPath());

  static
  {
    addIndex(new Index(Index.Path.CHECK_PENALTIES, "Check Penalties",
                       BaseItem.TYPE));
  }

  //........................................................................
  //----- arcane failure ---------------------------------------------------

  /** The grouping for arcane failure. */
  protected static final Group<Percent, Long, String> s_arcaneGrouping =
    new Group<Percent, Long, String>
    (new Group.Extractor<Percent, Long>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Long extract(Percent inValue)
        {
          return inValue.get();
        }
      }, new Long [] { 0L, 5L, 10L, 15L, 20L, 25L, 30L, 35L, 40L, 45L, 50L, },
                                new String []
      { "0%", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%",
        "50%", "Too Much", }, "$undefined$");

  /** The arcane spell failure. */
  @Key("arcane failure")
  protected Percent m_arcane = new Percent()
    .withGrouping(s_arcaneGrouping)
    .withTemplate("link", Index.Path.ARCANE_FAILURES.getPath());;

  static
  {
    addIndex(new Index(Index.Path.ARCANE_FAILURES, "Arcane Failures",
                       BaseItem.TYPE));
  }

  //........................................................................
  //----- speed ------------------------------------------------------------

  /** The grouping for arcane failure. */
  protected static final Group<Distance, Long, String> s_speedGrouping =
    new Group<Distance, Long, String>(new Group.Extractor<Distance, Long>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Long extract(Distance inValue)
        {
          return inValue.getAsFeet().getLeader();
        }
      }, new Long [] { 10L, 20L, 30L, 40L, 50L, },
                                      new String []
        { "10 ft", "20 ft", "30 ft", "40 ft", "50 ft", "Flash", },
                                      "$undefined$");

  /** The speed in the armor. */
  @Key("speed")
  protected Multiple m_speed = new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new Distance()
                           .withGrouping(s_speedGrouping)
                           .withEditType("name[30 ft base]"),
                           false, null, "/"),
      new Multiple.Element(new Distance()
                           .withGrouping(s_speedGrouping)
                           .withEditType("name[20 ft base]"),
                           false),
    });

  static
  {
    addIndex(new Index(Index.Path.SPEEDS, "Speeds", BaseItem.TYPE));
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

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

    ioValues.put(Index.Path.ARMOR_BONUSES, m_bonus.group());
    ioValues.put(Index.Path.ARMOR_TYPES, m_type.group());
    ioValues.put(Index.Path.MAX_DEXTERITIES, m_maxDex.group());
    ioValues.put(Index.Path.CHECK_PENALTIES, m_checkPenalty.group());
    ioValues.put(Index.Path.ARCANE_FAILURES, m_arcane.group());

    ioValues.put(Index.Path.SPEEDS, m_speed.get(0).group());
    ioValues.put(Index.Path.SPEEDS, m_speed.get(1).group());
  }

  //........................................................................
  //-------------------------- addContributions ----------------------------

  /**
   * Add current contributions to the given list.
   *
   * @param       inName     the name of the value to collect
   * @param       ioCombined the combined value to collect into
   * @param   <T>        the type of value being collected
   */
  @Override
  public <T extends Value<T>> void collect(String inName,
                                           Combined<T> ioCombined)
  {
    super.collect(inName, ioCombined);

    if("armor class".equals(inName) && m_bonus.isDefined())
      ioCombined.addModifier(m_bonus, m_entry, "armor");
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  @Override
  public Message toProto()
  {
    BaseArmorProto.Builder builder = BaseArmorProto.newBuilder();

    if(m_bonus.isDefined())
      builder.setAcBonus(m_bonus.toProto());
    if(m_type.isDefined())
      builder.setType(m_type.getSelected().toProto());
    if(m_maxDex.isDefined())
      builder.setMaxDexterity((int)m_maxDex.get());
    if(m_checkPenalty.isDefined())
      builder.setCheckPenalty((int)m_checkPenalty.get());
    if(m_arcane.isDefined())
      builder.setArcaneFailure((int)m_arcane.get());
    if(m_speed.isDefined())
    {
      builder.setSpeedFast(((Distance)m_speed.get(0)).toProto());
      builder.setSpeedSlow(((Distance)m_speed.get(1)).toProto());
    }

    return builder.build();
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseArmorProto))
    {
      Log.warning("cannot parse base armor proto " + inProto.getClass());
      return;
    }

    BaseArmorProto proto = (BaseArmorProto)inProto;

    if(proto.hasAcBonus())
      m_bonus = m_bonus.fromProto(proto.getAcBonus());
    if(proto.hasType())
      m_type = m_type.as(ArmorTypes.fromProto(proto.getType()));
    if(proto.hasMaxDexterity())
      m_maxDex = m_maxDex.as(proto.getMaxDexterity());
    if(proto.hasCheckPenalty())
      m_checkPenalty = m_checkPenalty.as(proto.getCheckPenalty());
    if(proto.hasArcaneFailure())
      m_arcane = m_arcane.as(proto.getArcaneFailure());
    if(proto.hasSpeedFast() && proto.hasSpeedSlow())
      m_speed = m_speed.as(((Distance)m_speed.get(0))
                           .fromProto(proto.getSpeedFast()),
                           ((Distance)m_speed.get(0))
                           .fromProto(proto.getSpeedSlow()));


  }

  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
