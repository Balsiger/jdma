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

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.Contribution;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Group;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Percent;
import net.ixitxachitls.dma.values.formatters.Formatter;
import net.ixitxachitls.dma.values.formatters.LinkFormatter;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the weapon extension for all the entries.
 *
 * @file          BaseArmor.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class BaseArmor extends BaseExtension<BaseItem>
{
  //----------------------------------------------------------------- nested

  //----- armor types ------------------------------------------------------

  /** The possible areas to affect (cf. PHB 175). */
  public enum ArmorTypes implements EnumSelection.Named
  {
    /** Light armor. */
    LIGHT("Light Armor"),
    /** Medium armor. */
    MEDIUM("Medium Armor"),
    /** Heavy armor. */
    HEAVY("Heavy Armor"),
    /** A shield. */
    SHIELD("Shield");

    /** The value's name. */
    private String m_name;

    /** Create the name.
     *
     * @param inName     the name of the value
     *
     */
    private ArmorTypes(@Nonnull String inName)
    {
      m_name = constant("armor.types", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public @Nonnull String toString()
    {
      return m_name;
    }
  };

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
  public BaseArmor(@Nonnull BaseItem inEntry, @Nonnull String inName)
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
  protected @Nonnull Modifier m_bonus = new Modifier()
    .withTemplate("link", Index.Path.ARMOR_BONUSES.getPath());

  static
  {
    addIndex(new Index(Index.Path.ARMOR_BONUSES, "Armor Bonuses",
                       BaseItem.TYPE));
  }

  //........................................................................
  //----- type -------------------------------------------------------------

  /** The formatter for types. */
  protected static final Formatter<EnumSelection<ArmorTypes>> s_typeFormatter =
    new LinkFormatter<EnumSelection<ArmorTypes>>
    (link(BaseItem.TYPE, Index.Path.ARMOR_TYPES));

  /** The type of the armor. */
  @Key("armor type")
  protected @Nonnull EnumSelection<ArmorTypes> m_type =
     new EnumSelection<ArmorTypes>(ArmorTypes.class)
    .withFormatter(s_typeFormatter)
    .withTemplate("link", Index.Path.ARMOR_TYPES.getPath());

  static
  {
    addIndex(new Index(Index.Path.ARMOR_TYPES, "Armor Types", BaseItem.TYPE));
  }

  //........................................................................
  //----- maximum dexterity ------------------------------------------------

  /** The formatter for max dex. */
  protected static final Formatter<Number> s_maxDexFormatter =
    new LinkFormatter<Number>(link(BaseItem.TYPE, Index.Path.MAX_DEXTERITIES));

  /** The grouping for max dex. */
  protected static final Group<Number, Long, String> s_maxDexGrouping =
    new Group<Number, Long, String>(new Group.Extractor<Number, Long>()
      {
        @Override
        public Long extract(@Nonnull Number inValue)
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
    .withFormatter(s_maxDexFormatter)
    .withGrouping(s_maxDexGrouping)
    .withTemplate("link", Index.Path.MAX_DEXTERITIES.getPath());

  static
  {
    addIndex(new Index(Index.Path.MAX_DEXTERITIES, "Max Dexterities",
                       BaseItem.TYPE));
  }

  //........................................................................
  //----- check penalty ----------------------------------------------------

  /** The formatter for the check penalty. */
  protected static final Formatter<Number> s_penaltyFormatter =
    new LinkFormatter<Number>(link(BaseItem.TYPE, Index.Path.CHECK_PENALTIES));

  /** The grouping for max dex. */
  protected static final Group<Number, Long, String> s_penaltyGrouping =
    new Group<Number, Long, String>
    (new Group.Extractor<Number, Long>()
      {
        @Override
        public Long extract(@Nonnull Number inValue)
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
    .withFormatter(s_penaltyFormatter)
    .withGrouping(s_penaltyGrouping)
    .withTemplate("link", Index.Path.CHECK_PENALTIES.getPath());

  static
  {
    addIndex(new Index(Index.Path.CHECK_PENALTIES, "Check Penalties",
                       BaseItem.TYPE));
  }

  //........................................................................
  //----- arcane failure ---------------------------------------------------

  /** The formatter for arcane failure. */
  protected static final Formatter<Percent> s_arcaneFormatter =
    new LinkFormatter<Percent>(link(BaseItem.TYPE, Index.Path.ARCANE_FAILURES));

  /** The grouping for arcane failure. */
  protected static final Group<Percent, Long, String> s_arcaneGrouping =
    new Group<Percent, Long, String>
    (new Group.Extractor<Percent, Long>()
      {
        @Override
        public Long extract(@Nonnull Percent inValue)
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
    .withFormatter(s_arcaneFormatter)
    .withGrouping(s_arcaneGrouping)
    .withTemplate("link", Index.Path.ARCANE_FAILURES.getPath());;

  static
  {
    addIndex(new Index(Index.Path.ARCANE_FAILURES, "Arcane Failures",
                       BaseItem.TYPE));
  }

  //........................................................................
  //----- speed ------------------------------------------------------------

  /** The formatter for arcane failure. */
  protected static final Formatter<Distance> s_speedFormatter =
    new LinkFormatter<Distance>(link(BaseItem.TYPE, Index.Path.SPEEDS));

  /** The grouping for arcane failure. */
  protected static final Group<Distance, Long, String> s_speedGrouping =
    new Group<Distance, Long, String>(new Group.Extractor<Distance, Long>()
      {
        @Override
        public Long extract(@Nonnull Distance inValue)
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
      new Multiple.Element(new Distance().withFormatter(s_speedFormatter)
                           .withGrouping(s_speedGrouping)
                           .withEditType("name[30 ft base]"),
                           false, null, "/"),
      new Multiple.Element(new Distance().withFormatter(s_speedFormatter)
                           .withGrouping(s_speedGrouping)
                           .withEditType("name[20 ft base]"),
                           false),
    });

  static
  {
    addIndex(new Index(Index.Path.SPEEDS, "Speeds", BaseItem.TYPE));
  }

  //........................................................................

  static
  {
    setAutoExtensions(BaseArmor.class, "armor", "wearable");
    extractVariables(BaseItem.class, BaseArmor.class);
  }

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
  public void computeIndexValues(@Nonnull Multimap<Index.Path, String> ioValues)
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
   * @param       inName          the name of the value to contribute to
   * @param       ioContributions the list of contributions collected
   *
   */
  @Override
  public void addContributions
    (@Nonnull String inName,
     @Nonnull List<Contribution<? extends Value>> ioContributions)
  {
    super.addContributions(inName, ioContributions);

    if("armor class".equals(inName) && m_bonus.isDefined())
      ioContributions.add(new Contribution<Modifier>(m_bonus, m_entry,
                                                     "armor"));
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
