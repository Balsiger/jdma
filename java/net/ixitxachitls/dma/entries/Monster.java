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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.extensions.Armor;
import net.ixitxachitls.dma.entries.extensions.BaseWeapon;
import net.ixitxachitls.dma.entries.extensions.Weapon;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.Contribution;
import net.ixitxachitls.dma.values.Critical;
import net.ixitxachitls.dma.values.Damage;
import net.ixitxachitls.dma.values.Dice;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.Duration;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.ModifiedNumber;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Money;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Parameters;
import net.ixitxachitls.dma.values.Reference;
import net.ixitxachitls.dma.values.Selection;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.conditions.Condition;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Divider;
import net.ixitxachitls.output.commands.Hrule;
import net.ixitxachitls.output.commands.Linebreak;
import net.ixitxachitls.output.commands.Table;
import net.ixitxachitls.output.commands.Textblock;
import net.ixitxachitls.util.Filter;
import net.ixitxachitls.util.FilteredIterator;
import net.ixitxachitls.util.Pair;
//import net.ixitxachitls.util.errors.CheckError;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 *
 * This is a real monster.
 *
 * @file          Monster.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Monster extends CampaignEntry<BaseMonster>
{
  //----------------------------------------------------------------- nested

  //----- Line -----------------------------------------------------------

  /** The class represents a single line in the treasure table (DMG 52/53). */
  private static class Line
  {
    /** Create the line for a specific treasure.
     *
     * @param inStart the start of the line range
     * @param inEnd   the end of the line range
     *
     */
    protected Line(int inStart, int inEnd)
    {
      assert inStart > 0 && inStart <= 100 : "invalid start given";
      assert inEnd > 0 && inEnd <= 100 && inEnd >= inStart : "invalid end";

      m_start = inStart;
      m_end   = inEnd;
    }

    /** The start of the range for this line. */
    protected int m_start;

    /** The end of the range for this line. */
    protected int m_end;

    /** Determine if this line matches to the given random value.
     *
     * @param inRandom the random value to determine the line for
     *
     * @return true if the line matches, false if not
     *
     */
    protected boolean matches(int inRandom)
    {
      return inRandom >= m_start && inRandom <= m_end;
    }
  }

  //......................................................................
  //----- Coins ----------------------------------------------------------

  /** This represents a line in the DMG treasure table for coins. */
  protected static class Coins extends Line
  {
    /** Create a complete coin line for the treasure.
     *
     * @param inType       the type of coins for the line
     * @param inStart      the range of the random values this line is valid
     * @param inEnd        the end of the range for this line
     * @param inNumber     the number of dice to use for generating the value
     * @param inDice       the dice to use for generating the value
     * @param inMultiplier the multiplier for the total value
     *
     */
    protected Coins(@Nonnull Money.Coin inType, int inStart, int inEnd,
                    int inNumber, int inDice, int inMultiplier)
    {
      super(inStart, inEnd);

      assert inNumber >= 0 : "must have a positive number here";
      assert inDice >= 0 : "must have a positive dice here";
      assert inMultiplier >= 0 : "must have a positive multiplier here";

      m_type       = inType;
      m_number     = inNumber;
      m_dice       = inDice;
      m_multiplier = inMultiplier;
    }

    /** The type of coins to generate. */
    private @Nonnull Money.Coin m_type;

    /** The number of dice to use to generate the value. */
    private int m_number;

    /** The dice to use for random generation of the value. */
    private int m_dice;

    /** The multiplier to multiply the value with. */
    private int m_multiplier;

    /**
     * Determine the random money value for this treasure line.
     *
     * @param ioMoney the money value to adjust
     *
     */
    protected void roll(@Nonnull Money ioMoney)
    {
      if(m_dice == 0 || m_number == 0 || m_multiplier == 0)
      {
        // set some value to make sure the money value is defined
        ioMoney.add(m_type, 0);

        return;
      }

      int value = 0;

      if(m_dice == 1)
        value = m_number;
      else
        for(int i = 0; i < m_number; i++)
          value +=
            s_random.nextInt((m_dice - 1) * m_multiplier) + m_multiplier;

      ioMoney.add(m_type, value);
    }

    /** Convert the money line to a human readable string.
     *
     * @return the converted string
     *
     */
    public String toString()
    {
      return m_start + "-" + m_end + ": " + m_number + "d" + m_dice + "x"
        + m_multiplier + " " + m_type;
    }
  }

  //........................................................................
  //----- Goods ----------------------------------------------------------

  /** This class represents a single line of goods in the treasure table. */
  protected static class Goods extends Line
  {
    /** The type of goods to generate. */
    public enum Type
    {
      /** A gem. */
      gem("gem"),

      /** An art object. */
      art("art object");

      /** Create a goods type.
       *
       * @param inName the name of the goods type to create
       *
       */
      private Type(@Nonnull String inName)
      {
        m_name = inName;
      }

      /** The name of the type. */
      private @Nonnull String m_name;

      /** Get the name of the goods.
       *
       * @return the converted string
       *
       */
      public @Nonnull String toString()
      {
        return m_name;
      }
    }

    /** Create a line of goods for the treasure table.
     *
     * @param inCategory the category or type of goods to create
     * @param inStart    the start of random values this line stands for
     * @param inEnd      the end (inclusive) of random values this lines
     *                   stands for
     * @param inNumber   the number of dice to determine the count of goods
     * @param inDice     the dice to use to determine the count of goods
     *
     */
    protected Goods(@Nonnull Type inCategory, int inStart, int inEnd,
                    int inNumber, int inDice)
    {
      super(inStart, inEnd);

      assert inNumber >= 0 : "must have a positive number here";
      assert inDice >= 0 : "must have a positive dice here";

      m_category   = inCategory;
      m_number     = inNumber;
      m_dice       = inDice;
    }

    /** The category of goods to generate. */
    private @Nonnull Type m_category;

    /** The number of dice to roll to generate the goods items. */
    private int m_number;

    /** The dice to roll when determining the number of items generated. */
    private int m_dice;

    /** Generate the items for the treasure of the creature.
     *
     * @return the items generated (the items are already added to the
     *         campaign)
     *
     */
    protected @Nonnull Item []roll()
    {
      if(m_dice <= 0 || m_number <= 0)
        return new Item [0];

      int value = 0;

      if(m_dice == 1)
        value = m_number;
      else
        for(int i = 0; i < m_number; i++)
          value += s_random.nextInt(m_dice - 1);

      // determine the given objects from the campaign
      Item []result = new Item[value];

      // for(int i = 0; i < value; i++)
      // {
      //   BaseItem random =
      //     (BaseItem)BaseCampaign.GLOBAL.lookup
      //     (new Filter<BaseEntry>()
      //      {
      //        public boolean accept(BaseEntry inEntry)
      //        {
      //          if(!(inEntry instanceof BaseItem) || inEntry == null)
      //            return false;

      //          BaseItem base = (BaseItem)inEntry;

      //          for(Text value : base.m_categories)
      //            if(value.get().equalsIgnoreCase(m_category.toString()))
      //              return true;

      //          return false;
      //        }
      //      });

      //   if(random == null)
      //   {
      //     Log.warning("could not look up a " + m_category
      //                 + " for a creature's treasure");

      //     return new Item[0];
      //   }

      //   result[i] = new Item(random);
      // }

      return result;
    }

    /** Convert the line to human readable string.
     *
     * @return the string
     *
     */
    public @Nonnull String toString()
    {
      return m_start + "-" + m_end + ": " + m_number + "d" + m_dice + " "
        + m_category;
    }
  }

  //........................................................................
  //----- Items ----------------------------------------------------------

  /** This class represents a single line of items in the treasure table. */
  protected static class Items extends Line
  {
    /** The types of items to generate. */
    public enum Type { mundane, minor, medium, major, };

    /** Create the line of items.
     *
     * @param inType   the type of items for this line
     * @param inStart  the start of random values this line is used for
     * @param inEnd    the end of random values this line is used for
     * @param inNumber the number of dice to roll for the item count
     * @param inDice   the dice to roll for the item count
     *
     */
    protected Items(@Nonnull Type inType, int inStart, int inEnd, int inNumber,
                    int inDice)
    {
      super(inStart, inEnd);

      assert inNumber >= 0 : "must have a positive number here";
      assert inDice >= 0 : "must have a positive dice here";

      m_type   = inType;
      m_number = inNumber;
      m_dice   = inDice;
    }

    /** The type of items to generate. */
    private @Nonnull Type m_type;

    /** The number of dice to use to compute the number of items. */
    private int m_number;

    /** The dice to compute the number of items. */
    private int m_dice;

    /** Determine the items that are randomly generated from this category
     * of treasure items.
     *
     * @param inCampaign the campaign the items are taken from
     * @param inBonus    special major magical items to use for high
     *                   levels (>20)
     *
     * @return the generated items
     *
     */
    @SuppressWarnings(value = "unchecked")
    protected @Nonnull Item []roll(int inBonus)
    {
      assert inBonus >= 0 : "bonus must not be negative";

      if(m_dice <= 0 || m_number <= 0)
        return new Item [0];

      int value = inBonus;

      if(m_dice == 1)
        value = m_number;
      else
        for(int i = 0; i < m_number; i++)
          value += s_random.nextInt(m_dice - 1);

      // determine the given objects from the campaign
      Item []result = new Item[value];

      // for(int i = 0; i < value; i++)
      // {
      //   BaseItem random = null;

      //   if(m_type == Type.mundane)
      //     random =
      //       (BaseItem)BaseCampaign.GLOBAL.lookup
      //       (new Filter<BaseEntry>()
      //        {
      //          public boolean accept(BaseEntry inEntry)
      //          {
      //            if(!(inEntry instanceof BaseItem) || inEntry == null)
      //              return false;

      //            BaseItem base = (BaseItem)inEntry;

      //            for(Text value : base.m_categories)
      //              if(value.get().equalsIgnoreCase("magic"))
      //                return false;

      //            return true;
      //          }
      //        });
      //   else
      //     random =
      //       (BaseItem)BaseCampaign.GLOBAL.lookup
      //       (new Filter<BaseEntry>()
      //        {
      //          public boolean accept(BaseEntry inEntry)
      //          {
      //            if(!(inEntry instanceof BaseItem) || inEntry == null)
      //              return false;

      //            BaseItem base = (BaseItem)inEntry;

      //            boolean magic = false;
      //            boolean type  = false;

      //            for(Iterator<Text> i = base.m_categories.iterator();
      //                i.hasNext() && !magic && !type; )
      //            {
      //              Text value = i.next();

      //              if(!magic && value.get().equalsIgnoreCase("magic"))
      //                magic = true;

      //              if(!type
      //                 && value.get().equalsIgnoreCase(m_type.toString()))
      //                type = true;
      //            }

      //            return magic && type;
      //          }
      //        });


      //   if(random == null)
      //   {
      //     Log.warning("could not look up a " + m_type
      //                 + " for a creature's treasure");

      //     return new Item[0];
      //   }

      //   result[i] = new Item(random);
      // }

      return result;
    }

    /** Convert the Item definition to a human readable string.
     *
     * @return the object converted to a string
     *
     */
    public @Nonnull String toString()
    {
      return m_start + "-" + m_end + ": " + m_number + "d" + m_dice + " "
        + m_type;
    }
  }

  //........................................................................
  //----- Treasure -------------------------------------------------------

  /** A class storing and computing treasure for a specific encounter level. */
  protected static class Treasure
  {
    /** Create the treasure for a specific encounter level with the given lines
     * determine different random value possibilities (c.f. DMG p. 52/53).
     *
     * @param inLines the lines for each random category
     *
     */
    protected Treasure(Line ... inLines)
    {
      for(Line line : inLines)
        if(line instanceof Coins)
          m_coins.add((Coins)line);
        else
          if(line instanceof Goods)
            m_goods.add((Goods)line);
          else
            if(line instanceof Items)
              m_items.add((Items)line);
            else
              assert false : "invalid line class " + line.getClass();
    }

    /** All the different line for coins for this treasure. */
    private @Nonnull ArrayList<Coins> m_coins = new ArrayList<Coins>();

    /** All the different lines for items for this treasure. */
    private @Nonnull ArrayList<Items> m_items = new ArrayList<Items>();

    /** All the different lines for goods for this treasure. */
    private @Nonnull ArrayList<Goods> m_goods = new ArrayList<Goods>();

    /** Determine the coins value to the given random value.
     *
     * @param inRandom the random value (1-100) to get the coins description
     *        for
     *
     * @return the coins line determining what treasures a creature will get
     *
     */
    public @Nullable Coins coins(int inRandom)
    {
      // determine the matching coin value
      for(Coins coins : m_coins)
        if(coins.matches(inRandom))
          return coins;

      return null;
    }

    /** Determine the items value to the given random value.
     *
     * @param inRandom the random value (1-100) to get the items description
     *        for
     *
     * @return the items line determining what treasures a creature will get
     *
     */
    public @Nullable Items items(int inRandom)
    {
      // determine the matching item value
      for(Items items : m_items)
        if(items.matches(inRandom))
          return items;

      return null;
    }

    /** Determine the goods value to the given random value.
     *
     * @param inRandom the random value (1-100) to get the goods description
     *        for
     *
     * @return the goods line determining what treasures a creature will get
     *
     */
    public @Nullable Goods goods(int inRandom)
    {
      // determine the matching item value
      for(Goods goods : m_goods)
        if(goods.matches(inRandom))
          return goods;

      return null;
    }
  }

  //........................................................................
  //----- Gender ---------------------------------------------------------

  /** The possible gender types in the game. */
  public enum Gender implements EnumSelection.Named
  {
    /** Male. */
    MALE("Male"),

    /** Female. */
    FEMALE("Female"),

    /** Not known. */
    UNKNOWN("Unknown"),

    /** Other. */
    OTHER("Other");

    /** The value's name. */
    private @Nonnull String m_name;

    /** Create the enum value.
     *
     * @param inName the name of the value
     *
     */
    private Gender(@Nonnull String inName)
    {
      m_name = constant("monster.gender", inName);
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
  //----- strength ---------------------------------------------------------

  /** The monster's Strength. */
  @Key("strength")
  protected Number m_strength = new Number(-1, 100, false);

  //........................................................................
  //----- dexterity --------------------------------------------------------

  /** The monster's Dexterity. */
  @Key("dexterity")
  protected Number m_dexterity = new Number(-1, 100, false);

  //........................................................................
  //----- constitution -----------------------------------------------------

  /** The monster's Constitution. */
  @Key("constitution")
  protected Number m_constitution = new Number(-1, 100, false);

  //........................................................................
  //----- intelligence -----------------------------------------------------

  /** The monster's Intelligence. */
  @Key("intelligence")
  protected Number m_intelligence = new Number(-1, 100, false);

  //........................................................................
  //----- wisdom -----------------------------------------------------------

  /** The monster's Wisdom. */
  @Key("wisdom")
  protected Number m_wisdom = new Number(-1, 100, false);

  //........................................................................
  //----- charisma ---------------------------------------------------------

  /** The monster's Charisma. */
  @Key("charisma")
  protected Number m_charisma = new Number(-1, 100, false);

  //........................................................................
  //----- feats ------------------------------------------------------------

  /** The feats. */
  @Key("feats")
  protected ValueList<Reference> m_feats = new ValueList<Reference>
    (", ", new Reference<BaseFeat>(BaseFeat.TYPE)
     .withParameter("Name", new Name(), Parameters.Type.UNIQUE));

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------- Monster --------------------------------

  /**
   * This is the internal, default constructor.
   *
   */
  protected Monster()
  {
    super(TYPE, BASE_TYPE);
  }

  //........................................................................
  //------------------------------- Monster --------------------------------

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base item
   *
   */
  public Monster(@Nonnull String inName)
  {
    super(inName, TYPE, BASE_TYPE);
  }

  //........................................................................
  //------------------------------- Monster --------------------------------

  /**
   * This constructs the item with random values from the given
   * base item.
   *
   * @param       inBase the base item to take values from
   *
   */
  // public Monster(@Nonnull BaseMonster inBase)
  // {
  //   super(inBase.getName(), TYPE, BASE_TYPE, inBase);

  //   // take over the base items values
  //   complete();
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final Type<Monster> TYPE =
    new Type<Monster>(Monster.class, BaseMonster.TYPE);

  /** The type of the base entry to this entry. */
  public static final BaseType<BaseMonster> BASE_TYPE = BaseMonster.TYPE;

  //----- given name -------------------------------------------------------

  /** A special name for the monster, if any. */
  @Key("given name")
  protected Text m_givenName = new Text();

  //........................................................................
  //----- gender -----------------------------------------------------------

  /** The gender of the monster. */
  @Key("gender")
  protected EnumSelection<Gender> m_gender =
    new EnumSelection<Gender>(Gender.class);

  //........................................................................
  //----- possesions -------------------------------------------------------

  /** The possessions value. */
  @Key("possesions")
  protected ValueList<Name> m_possessions = new ValueList<Name>(new Name());

  //........................................................................

  //----- max hp -----------------------------------------------------------

  /** The actual maximal number of hit points the monster can have. */
  @Key("max hp")
  protected Number m_maxHP = new Number(0, 10000);

  //........................................................................
  //----- hp ---------------------------------------------------------------

  /** The actual number of hit points the monster currently has. */
  @Key("hp")
  protected Number m_hp = new Number(0, 10000);

  //........................................................................
  //----- skills -----------------------------------------------------------

  /** The skills, in addition to what we find in base. */
  @Key("skills")
  protected ValueList<Multiple> m_skills =
    new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
      {
        new Multiple.Element
        (new Reference<BaseSkill>(BaseSkill.TYPE)
         .withParameter("Subtype", new EnumSelection<BaseSkill.Subtype>
                        (BaseSkill.Subtype.class), Parameters.Type.UNIQUE),
         false),
        new Multiple.Element(new Number(0, 100, true)
                             .withEditType("number[modifier]"),
                             false, ": ", null),
      }));

  //........................................................................
  //----- alignment --------------------------------------------------------

  /** The monster's alignment. */
  @Key("alignment")
  protected EnumSelection<BaseMonster.Alignment> m_alignment =
    new EnumSelection<BaseMonster.Alignment>(BaseMonster.Alignment.class);

  //........................................................................
  //----- fortitude save ---------------------------------------------------

  /** The monster's Charisma. */
  @Key("fortitude save")
  protected Number m_fortitudeSave = new Number(-1, 100, true);

  static
  {
    addIndex(new Index(Index.Path.FORTITUDE_SAVES, "Fortitude Saves", TYPE));
  }

  //........................................................................
  //----- will save --------------------------------------------------------

  /** The monster's Charisma. */
  @Key("will save")
  protected Number m_willSave = new Number(-1, 100, true);

  static
  {
    addIndex(new Index(Index.Path.WILL_SAVES, "Will Saves", TYPE));
  }

  //........................................................................
  //----- reflex save ------------------------------------------------------

  /** The monster's Charisma. */
  @Key("reflex save")
  protected Number m_reflexSave = new Number(-1, 100, true);

  static
  {
    addIndex(new Index(Index.Path.REFLEX_SAVES, "Reflex Saves", TYPE));
  }

  //........................................................................

  /** The special attacks. */
  // @Key("special attacks")
  // protected ValueList<Multiple> m_specialAttacks =
  //   new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
  //     {
  //       new Multiple.Element(new EntryValue<Quality>(new Quality()), false),
  //       new Multiple.Element(new Duration(), true, "/", ""),
  //     }));

  /** The special qualities. */
  // @Key("special qualities")
  // protected ValueList<Multiple> m_specialQualities =
  //   new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
  //     {
  //       new Multiple.Element(new EntryValue<Quality>(new Quality()), false),
  //       new Multiple.Element(new Duration(), true, "/", ""),
  //     }));

  /** The feats. */
  // @Key("feats")
  // @SuppressWarnings("unchecked") // generic array creation
  // protected ValueList<EntryValue<Feat>> m_feats =
  //   new ValueList<EntryValue<Feat>>(", ", new EntryValue<Feat>(new Feat()));

  /** The monetary treasure. */
  // @Key("money")
  // protected Money m_money = new Money();

  /** The fortitude save. */
  //protected Composite m_saveFort = new Composite(0, true);

  /** The reflex save. */
  //protected Composite m_saveRef = new Composite(0, true);

  /** The will save. */
  //protected Composite m_saveWill = new Composite(0, true);

  /** The random generator. */
  // private static Random s_random = new Random();

  /** The treasures per level. */
  private static ArrayList<Treasure> s_treasures = new ArrayList<Treasure>();

  //----- treasure definition ----------------------------------------------

  static
  {
    // 0
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,  14, 0, 0, 0),
                    new Coins(Money.Coin.copper,   15,  29, 1, 3, 100),
                    new Coins(Money.Coin.silver,   30,  52, 1, 4, 10),
                    new Coins(Money.Coin.gold,     53,  95, 2, 4, 1),
                    new Coins(Money.Coin.platinum, 96, 100, 1, 2, 1),
                    new Goods(Goods.Type.gem,       1,  99, 0,  0),
                    new Goods(Goods.Type.gem,      99,  99, 1,  1),
                    new Goods(Goods.Type.art,     100, 100, 1,  1),
                    new Items(Items.Type.mundane,   1,  85, 0,  0),
                    new Items(Items.Type.mundane,  85,  99, 1,  1),
                    new Items(Items.Type.minor,   100, 100, 1,  1)));
    // 1
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,  14, 0, 0, 0),
                    new Coins(Money.Coin.copper,   15,  29, 1, 6, 1000),
                    new Coins(Money.Coin.silver,   30,  52, 1, 8, 100),
                    new Coins(Money.Coin.gold,     53,  95, 2, 8, 10),
                    new Coins(Money.Coin.platinum, 96, 100, 1, 4, 10),
                    new Goods(Goods.Type.gem,       1,  90, 0,  0),
                    new Goods(Goods.Type.gem,      91,  95, 1,  1),
                    new Goods(Goods.Type.art,      96, 100, 1,  1),
                    new Items(Items.Type.mundane,   1,  71, 0,  0),
                    new Items(Items.Type.mundane,  72,  92, 1,  1),
                    new Items(Items.Type.minor,    96, 100, 1,  1)));
    // 2
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,  13, 0,  0, 0),
                    new Coins(Money.Coin.copper,   14,  23, 1, 10, 1000),
                    new Coins(Money.Coin.silver,   24,  43, 2, 10, 100),
                    new Coins(Money.Coin.gold,     44,  95, 4, 10, 10),
                    new Coins(Money.Coin.platinum, 96, 100, 2,  8, 10),
                    new Goods(Goods.Type.gem,       1,  81, 0,  0),
                    new Goods(Goods.Type.gem,      82,  95, 1,  3),
                    new Goods(Goods.Type.art,      96, 100, 1,  3),
                    new Items(Items.Type.mundane,   1,  49, 0,  0),
                    new Items(Items.Type.mundane,  50,  85, 1,  1),
                    new Items(Items.Type.minor,    96, 100, 1,  1)));
    // 3
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,  11, 0,  0, 0),
                    new Coins(Money.Coin.copper,   12,  21, 2, 10, 1000),
                    new Coins(Money.Coin.silver,   22,  41, 4,  8, 100),
                    new Coins(Money.Coin.gold,     42,  95, 1,  4, 100),
                    new Coins(Money.Coin.platinum, 96, 100, 1, 10, 10),
                    new Goods(Goods.Type.gem,       1,  77, 0,  0),
                    new Goods(Goods.Type.gem,      78,  95, 1,  3),
                    new Goods(Goods.Type.art,      96, 100, 1,  3),
                    new Items(Items.Type.mundane,   1,  49, 0,  0),
                    new Items(Items.Type.mundane,  50,  79, 1,  3),
                    new Items(Items.Type.minor,    80, 100, 1,  1)));
    // 4
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,  11, 0,  0, 0),
                    new Coins(Money.Coin.copper,   12,  21, 3, 10, 1000),
                    new Coins(Money.Coin.silver,   22,  41, 4, 12, 100),
                    new Coins(Money.Coin.gold,     42,  95, 1,  6, 100),
                    new Coins(Money.Coin.platinum, 96, 100, 1,  8, 10),
                    new Goods(Goods.Type.gem,       1,  70, 0,  0),
                    new Goods(Goods.Type.gem,      71,  95, 1,  4),
                    new Goods(Goods.Type.art,      96, 100, 1,  3),
                    new Items(Items.Type.mundane,   1,  42, 0,  0),
                    new Items(Items.Type.mundane,  43,  62, 1,  4),
                    new Items(Items.Type.minor,    63, 100, 1,  1)));
    // 5
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,  10, 0,  0, 0),
                    new Coins(Money.Coin.copper,   11,  19, 1,  4, 10000),
                    new Coins(Money.Coin.silver,   20,  38, 1,  6, 1000),
                    new Coins(Money.Coin.gold,     39,  95, 1,  8, 100),
                    new Coins(Money.Coin.platinum, 96, 100, 1, 10, 10),
                    new Goods(Goods.Type.gem,       1,  60, 0,  0),
                    new Goods(Goods.Type.gem,      61,  95, 1,  4),
                    new Goods(Goods.Type.art,      96, 100, 1,  4),
                    new Items(Items.Type.mundane,   1,  57, 0,  0),
                    new Items(Items.Type.mundane,  58,  67, 1,  4),
                    new Items(Items.Type.minor,    68, 100, 1,  3)));
    // 6
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,  10, 0,  0, 0),
                    new Coins(Money.Coin.copper,   11,  18, 1,  6, 10000),
                    new Coins(Money.Coin.silver,   19,  35, 1, 12, 1000),
                    new Coins(Money.Coin.gold,     36,  95, 1, 10, 100),
                    new Coins(Money.Coin.platinum, 96, 100, 1, 12, 10),
                    new Goods(Goods.Type.gem,       1,  56, 0,  0),
                    new Goods(Goods.Type.gem,      57,  92, 1,  4),
                    new Goods(Goods.Type.art,      93, 100, 1,  4),
                    new Items(Items.Type.mundane,   1,  54, 0,  0),
                    new Items(Items.Type.mundane,  55,  59, 1,  4),
                    new Items(Items.Type.minor,    60,  99, 1,  3),
                    new Items(Items.Type.medium,  100, 100, 1,  1)));
    // 7
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,  11, 0,  0, 0),
                    new Coins(Money.Coin.copper,   12,  18, 1, 10, 10000),
                    new Coins(Money.Coin.silver,   19,  35, 1, 12, 1000),
                    new Coins(Money.Coin.gold,     36,  93, 2,  6, 100),
                    new Coins(Money.Coin.platinum, 94, 100, 3,  4, 10),
                    new Goods(Goods.Type.gem,       1,  48, 0,  0),
                    new Goods(Goods.Type.gem,      49,  88, 1,  4),
                    new Goods(Goods.Type.art,      89, 100, 1,  4),
                    new Items(Items.Type.mundane,   1,   51, 0,  0),
                    new Items(Items.Type.minor,    52,   97, 1,  3),
                    new Items(Items.Type.medium,   98,  100, 1,  1)));
    // 8
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,  10, 0,  0, 0),
                    new Coins(Money.Coin.copper,   11,  15, 1, 12, 10000),
                    new Coins(Money.Coin.silver,   16,  29, 2,  6, 1000),
                    new Coins(Money.Coin.gold,     30,  87, 2,  8, 100),
                    new Coins(Money.Coin.platinum, 88, 100, 3,  6, 10),
                    new Goods(Goods.Type.gem,       1,  45, 0,  0),
                    new Goods(Goods.Type.gem,      46,  85, 1,  6),
                    new Goods(Goods.Type.art,      86, 100, 1,  6),
                    new Items(Items.Type.mundane,   1,   48, 0,  0),
                    new Items(Items.Type.minor,    49,   96, 1,  4),
                    new Items(Items.Type.medium,   97,  100, 1,  1)));
    // 9
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,  10, 0,  0, 0),
                    new Coins(Money.Coin.copper,   11,  15, 2,  6, 10000),
                    new Coins(Money.Coin.silver,   16,  29, 2,  8, 1000),
                    new Coins(Money.Coin.gold,     30,  85, 5,  4, 100),
                    new Coins(Money.Coin.platinum, 86, 100, 2, 12, 10),
                    new Goods(Goods.Type.gem,       1,  40, 0,  0),
                    new Goods(Goods.Type.gem,      41,  80, 1,  8),
                    new Goods(Goods.Type.art,      81, 100, 1,  4),
                    new Items(Items.Type.mundane,   1,   43, 0,  0),
                    new Items(Items.Type.minor,    44,   91, 1,  4),
                    new Items(Items.Type.medium,   92,  100, 1,  1)));
    // 10
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,  10, 0,  0, 0),
                    new Coins(Money.Coin.silver,   11,  24, 2, 10, 1000),
                    new Coins(Money.Coin.gold,     25,  79, 6,  4, 100),
                    new Coins(Money.Coin.platinum, 80, 100, 5,  6, 10),
                    new Goods(Goods.Type.gem,       1,  35, 0,  0),
                    new Goods(Goods.Type.gem,      36,  79, 1,  8),
                    new Goods(Goods.Type.art,      80, 100, 1,  6),
                    new Items(Items.Type.mundane,   1,  40, 0,  0),
                    new Items(Items.Type.minor,    41,  88, 1,  4),
                    new Items(Items.Type.medium,   89,  99, 1,  1),
                    new Items(Items.Type.major,   100, 100, 1,  1)));
    // 11
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,   8, 0,  0, 0),
                    new Coins(Money.Coin.silver,    9,  14, 3, 10, 1000),
                    new Coins(Money.Coin.gold,     15,  75, 4,  8, 100),
                    new Coins(Money.Coin.platinum, 76, 100, 4, 10, 10),
                    new Goods(Goods.Type.gem,       1,  24, 0,  0),
                    new Goods(Goods.Type.gem,      25,  74, 1, 10),
                    new Goods(Goods.Type.art,      75, 100, 1,  6),
                    new Items(Items.Type.mundane,   1,  31, 0,  0),
                    new Items(Items.Type.minor,    32,  84, 1,  4),
                    new Items(Items.Type.medium,   85,  98, 1,  1),
                    new Items(Items.Type.major,    99, 100, 1,  1)));
    // 12
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,   8, 0,  0, 0),
                    new Coins(Money.Coin.silver,    9,  14, 3, 12, 1000),
                    new Coins(Money.Coin.gold,     15,  75, 1,  4, 1000),
                    new Coins(Money.Coin.platinum, 76, 100, 1,  4, 100),
                    new Goods(Goods.Type.gem,       1,  17, 0,  0),
                    new Goods(Goods.Type.gem,      18,  70, 1, 10),
                    new Goods(Goods.Type.art,      71, 100, 1,  8),
                    new Items(Items.Type.mundane,   1,  27, 0,  0),
                    new Items(Items.Type.minor,    28,  82, 1,  6),
                    new Items(Items.Type.medium,   83,  97, 1,  1),
                    new Items(Items.Type.major,    98, 100, 1,  1)));
    // 13
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,   8, 0,  0, 0),
                    new Coins(Money.Coin.gold,      9,  75, 1,  4, 1000),
                    new Coins(Money.Coin.platinum, 76, 100, 1, 10, 100),
                    new Goods(Goods.Type.gem,       1,  11, 0,  0),
                    new Goods(Goods.Type.gem,      12,  66, 1, 12),
                    new Goods(Goods.Type.art,      67, 100, 1, 10),
                    new Items(Items.Type.mundane,   1,  19, 0,  0),
                    new Items(Items.Type.minor,    20,  73, 1,  6),
                    new Items(Items.Type.medium,   74,  95, 1,  1),
                    new Items(Items.Type.major,    96, 100, 1,  1)));
    // 14
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,   8, 0,  0, 0),
                    new Coins(Money.Coin.gold,      9,  75, 1,  6, 1000),
                    new Coins(Money.Coin.platinum, 76, 100, 1, 12, 100),
                    new Goods(Goods.Type.gem,       1,  11, 0,  0),
                    new Goods(Goods.Type.gem,      12,  66, 2,  8),
                    new Goods(Goods.Type.art,      67, 100, 2,  6),
                    new Items(Items.Type.mundane,   1,  19, 0,  0),
                    new Items(Items.Type.minor,    20,  58, 1,  6),
                    new Items(Items.Type.medium,   59,  92, 1,  1),
                    new Items(Items.Type.major,    93, 100, 1,  1)));
    // 15
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,   3, 0,  0, 0),
                    new Coins(Money.Coin.gold,      4,  74, 1,  8, 1000),
                    new Coins(Money.Coin.platinum, 75, 100, 3,  4, 100),
                    new Goods(Goods.Type.gem,       1,   9, 0,  0),
                    new Goods(Goods.Type.gem,      10,  65, 2, 10),
                    new Goods(Goods.Type.art,      66, 100, 2,  8),
                    new Items(Items.Type.mundane,   1,  11, 0,  0),
                    new Items(Items.Type.minor,    12,  46, 1, 10),
                    new Items(Items.Type.medium,   47,  90, 1,  1),
                    new Items(Items.Type.major,    91, 100, 1,  1)));
    // 16
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,   3, 0,  0, 0),
                    new Coins(Money.Coin.gold,      4,  74, 1, 12, 1000),
                    new Coins(Money.Coin.platinum, 75, 100, 3,  4, 100),
                    new Goods(Goods.Type.gem,       1,   7, 0,  0),
                    new Goods(Goods.Type.gem,       8,  64, 4,  6),
                    new Goods(Goods.Type.art,      65, 100, 2, 10),
                    new Items(Items.Type.mundane,   1,  40, 0,  0),
                    new Items(Items.Type.minor,    41,  46, 1, 10),
                    new Items(Items.Type.medium,   47,  90, 1,  3),
                    new Items(Items.Type.major,    90, 100, 1,  1)));
    // 17
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,   3, 0,  0, 0),
                    new Coins(Money.Coin.gold,      4,  68, 3,  4, 1000),
                    new Coins(Money.Coin.platinum, 69, 100, 2, 10, 100),
                    new Goods(Goods.Type.gem,       1,   4, 0,  0),
                    new Goods(Goods.Type.gem,       5,  63, 4,  8),
                    new Goods(Goods.Type.art,      64, 100, 3,  8),
                    new Items(Items.Type.mundane,   1,  33, 0,  0),
                    new Items(Items.Type.medium,   34,  84, 1,  3),
                    new Items(Items.Type.major,    85, 100, 1,  1)));
    // 18
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,   2, 0,  0, 0),
                    new Coins(Money.Coin.gold,      3,  65, 3,  6, 1000),
                    new Coins(Money.Coin.platinum, 66, 100, 5,  4, 100),
                    new Goods(Goods.Type.gem,       1,   4, 0,  0),
                    new Goods(Goods.Type.gem,       5,  54, 3, 12),
                    new Goods(Goods.Type.art,      55, 100, 3, 10),
                    new Items(Items.Type.mundane,   1,  24, 0,  0),
                    new Items(Items.Type.medium,   25,  80, 1,  4),
                    new Items(Items.Type.major,    81, 100, 1,  1)));
    // 19
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,   2, 0,  0, 0),
                    new Coins(Money.Coin.gold,      3,  65, 3,  8, 1000),
                    new Coins(Money.Coin.platinum, 66, 100, 3, 10, 100),
                    new Goods(Goods.Type.gem,       1,   3, 0,  0),
                    new Goods(Goods.Type.gem,       4,  50, 6,  6),
                    new Goods(Goods.Type.art,      50, 100, 6,  6),
                    new Items(Items.Type.mundane,   1,   4, 0,  0),
                    new Items(Items.Type.medium,    5,  70, 1,  4),
                    new Items(Items.Type.major,    71, 100, 1,  1)));
    // 20
    s_treasures.add
      (new Treasure(new Coins(Money.Coin.copper,    1,   2, 0,  0, 0),
                    new Coins(Money.Coin.gold,      3,  65, 4,  8, 1000),
                    new Coins(Money.Coin.platinum, 66, 100, 4, 10, 100),
                    new Goods(Goods.Type.gem,       1,   2, 0,  0),
                    new Goods(Goods.Type.gem,       3,  38, 4, 10),
                    new Goods(Goods.Type.art,      39, 100, 7,  6),
                    new Items(Items.Type.mundane,   1,  25, 0,  0),
                    new Items(Items.Type.medium,   26,  65, 1,  4),
                    new Items(Items.Type.major,    66, 100, 1,  3)));
  }

  //........................................................................

  static
  {
    extractVariables(Monster.class);
  }

  //----- printing commands ------------------------------------------------

  /** The command for printing on a page. */
  // public static Command PAGE_COMMAND = new Command(new Object []
  //   {
  //     new Divider("center", "#^world #attachment #+categories"),
  //     "$title ${player title}",
  //     new Textblock(new Command(new Object []
  //       {
  //         "$description",
  //         new Linebreak(),
  //         new Hrule(),
  //         "${short description}",
  //       }), "desc"),
  //     new OverviewFiles("$image"),
  //     new Table("description", "f" + "Illustrations: ".length()
  //               + ":L(desc-label);100:L(desc-text)",
  //               new Command("%base %{player name} %{player notes} %{dm notes} "
  //                           + ""
  //                           // incomplete
  //                           + "%incomplete "
  //                           // admin
  //                           + "%{+references} %file")),
  //     new Divider("clear", "${scripts}"),
  //   });

  //........................................................................

  //----- commands ---------------------------------------------------------

  //----- print ------------------------------------------------------------

  /** The command for printing the item. */
  static
  {
    // TODO: make sure this is reimplemented
//     addCommand
//       (Monster.class, PrintType.print,
//        new Command(new Object []
//          {
//            new Center
//            (new Command(new Object []
//              {
//                new Icon(new Command(new Object []
//                  { "worlds/",
//                   new BaseVariableStringCommand(PropertyKey.getKey("world")),
//                    ".png",
//                  }), new Command(new Object []
//                    { "world: ",
//                  new BaseVariableStringCommand(PropertyKey.getKey("world")),
//                    }), new Command(new Object []
//                      { "../index/worlds/",
//                        new WordUpperCase
//                        (new BaseVariableStringCommand(PropertyKey.getKey
//                                                       ("world"))),
//                        ".html",
//                      }), true),
//                new AttachmentCommand(false),
//              })),
//            "\n",
//            new Divider("main", new Command(new Object []
//              {
//                new Title(new Command(new Object []
//                  {
//                   new Link(new AccessorCommand(new AccessorCommand.Accessor()
//                      {
//                        public String get(ValueGroup inEntry)
//                        {
//                          return ((Monster)inEntry).getName();
//                        }
//                      }),
//                            new AccessorCommand(new AccessorCommand.Accessor()
//                               {
//                                 public String get(ValueGroup inEntry)
//                                 {
//                                   return "BaseMonsters/"
//                                     + ((Monster)inEntry).getName();
//                                 }
//                               }), "nocolor"),
//                    new Linebreak(),
//                    new Tiny(new Link("(monster)", "Monsters/index")),
//                  })),
//                "\n",
//                new BaseVariableCommand(PropertyKey.getKey("description")),
//                "\n",
//                CMD_FILES,
//                new Table("description", "f" + "Illustrations: ".length()
//                          + ":L(desc-label);100:L(desc-text)", new Object []
//                  {
//                    BaseEntry.s_synonymLabel,
//                    BaseEntry.s_synonymCmd,
//                    new IfDefinedCommand
//                    (NAME,
//                     new Window(new Bold("Name:"),
//                                Config.get("resource:help/label.monster.name",
//                                           "A special name for the monster, "
//                                           + "if it has any")), null),
//                  new IfDefinedCommand(NAME, new VariableCommand(NAME), null),
//                    new Window(new Bold("Hit Points:"),
//                               Config.get
//                               ("resource:help/label.monster.hp",
//                                "These is the current hit point total of the "
//                                + "monster. Additionally, the maximal hit "
//                                + "points are given for the monster in its "
//                                + "healthy state. Certain magical effects "
//                                + "might raise the current hit point total "
//                                + "above this one, but natural healing will "
//                                + "not.")),
//                    new AccessorCommand(new AccessorCommand.Accessor()
//                      {
//                        public String get(ValueGroup inEntry)
//                        {
//                          Monster monster = (Monster)inEntry;

//                          long hp  = monster.m_hp.get();
//                          long max = monster.m_maxHP.get();

//                          return hp + " (" + monster.m_hpMod
//                            .convertValue(Value.Convert.PRINT, false)
//                            + ", max " + max + ")";
//                        }
//                      }),
//                    new Window(new Bold("Initiative:"),
//                               Config.get
//                               ("resource:help/label.initiative",
//                                "This line gives the creature's modifier on "
//                                + "initiative checks")),
//                    new AccessorCommand(new AccessorCommand.Accessor()
//                      {
//                        public String get(ValueGroup inEntry)
//                        {
//                          return
//                            "\\link[index/initiatives/"
//                            + ((Monster)inEntry)
//                            .m_initiative.getLimits().first() + "]{"
//                            + ((Monster)inEntry).m_initiative.toPrint()
//                            + "}";
//                        }
//                      }),
//                    new Window(new Bold("Speed:"),
//                               Config.get
//                               ("resource:help/label.speed", (String)null)),
//                    new BaseVariableCommand(BaseMonster.SPEED, true),
//                    new Window(new Bold("Armor Class:"),
//                               Config.get
//                               ("resource:help/label.armor.class",
//                              "The Armor Class line gives the creature's AC "
//                                + "for normal combat and includes a "
//                                + "parenthetical mention of the modifiers "
//                                + "contributing to it (usually size, "
//                                + "Dexterity, and natural armor). The "
//                              + "creature's touch and flat-footed ACs follow "
//                                + "the combat-ready AC.\\par "
//                              + "A creature's armor proficiencies (if it has "
//                                + "any) depend on its type, but in general a "
//                                + "creature is automatically proficient with "
//                                + "any kind of armor it is described as "
//                              + "wearing (light, medium, or heavy), and with "
//                                + "all lighter kinds of armor.\\par "
//                                + "This value is computed from the other "
//                                + "values given.")),
//                    new AccessorCommand(new AccessorCommand.Accessor()
//                      {
//                        public String get(ValueGroup inEntry)
//                        {
//                          Monster monster = (Monster)inEntry;

//                          // TODO: should only clone if necessary
//                          Composite ac = monster.m_ac.clone();
//                          Composite flat  = monster.m_acFlatFooted.clone();

//                          for(Iterator<Value> i =
//                                monster.m_possessions.iterator();
//                              i.hasNext(); )
//                          {
//                          AbstractEntry entry = ((EntryValue)i.next()).get();

//                            if(!entry.hasAttachment(Armor.class))
//                              continue;

//                            Item armor = (Item)entry;

//                            if(armor.m_ac.hasModifiers())
//                              for(Iterator<net.ixitxachitls.dma.values
//                                    .Modifier> j =
//                                    armor.m_ac.getModifiers();
//                                  j.hasNext(); )
//                              {
//                                net.ixitxachitls.dma.values
//                                  .Modifier modifier = j.next();

//                                ac.addModifier(modifier);
//                                flat.addModifier(modifier);
//                              }
//                          }

//                          return
//                            "\\link[index/acs/"
//                            + ac.getLimits().first() + "]{"
//                            + ac.toPrint()
//                            + "}, touch \\link[index/touchacs/"
//                            + monster.m_acTouch.getLimits().first() + "]{"
//                            + monster.m_acTouch.toPrint()
//                            + "}, flat-footed \\link[index/flatfootedacs/"
//                            + flat.getLimits().first() + "]{"
//                            + flat.toPrint()
//                            + "}";
//                        }
//                      }),
//                    new Window(new Bold("Base Attack:"),
//                               Config.get
//                               ("resource:help/label.base.attack",
//                                (String)null)),
//                    new BaseVariableCommand(BaseMonster.BASE_ATTACK),
//                    new Window(new Bold("Grapple:"),
//                               Config.get
//                               ("resource:help/label.grapple",
//                                "This is the creature's grapple bonus, which "
//                                + "is used when the creature makes a grapple "
//                              + "attack or when someone tries to grapple the "
//                                + "creature. The grapple bonus includes all "
//                                + "modifiers that apply to the creature's "
//                              + "grapple checks (base attack bonus, Strength "
//                                + "modifier, special size modifier, and any "
//                                + "other applicable modifier, such as racial "
//                                + "bonus on grapple checks.")),
//                    new AccessorCommand(new AccessorCommand.Accessor()
//                      {
//                        public String get(ValueGroup inEntry)
//                        {
//                          return
//                            "\\link[index/grapples/"
//                            + ((Monster)inEntry)
//                            .m_grapple.getLimits().first() + "]{"
//                            + ((Monster)inEntry).m_grapple.toPrint() + "}";
//                        }
//                      }),
//                    new Window(new Bold("Attack:"),
//                               Config.get
//                               ("resource:help/label.attack",
//                                "This line shows the single attack the "
//                                + "creature makes with an attack action. In "
//                                + "most cases, this is also the attack the "
//                                + "creature uses when making an attack of "
//                                + "opportunity as well. The attack line "
//                                + "provides the weapon used (natural or "
//                                + "manufactured), attack bonus, and form of "
//                               + "attack (melee or ranged). The attack bonus "
//                                + "given includes modifications for size and "
//                                + "Strength (for melee attack) or Dexterity "
//                                + "(for ranged attacks). A creature with the "
//                                + "\\Feat{Weapon Finesse} feat can use its "
//                                + "Dexterity modifier on melee attacks.\\par "
//                                + "If the creature uses natural attacks, the "
//                              + "natural weapon given here is the creature's "
//                                + "primary natural weapon.\\par "
//                                + "If the creature has several different "
//                                + "weapons at its disposal, the alternatives "
//                                + "are shown, with each different attack "
//                                + "separated by the word 'or'.\\par "
//                                + "A creature can use one of its secondary "
//                                + "natural weapons when making an attack "
//                                + "action, but if it does it takes an attack "
//                                + "penalty, as noted in the Full Attack "
//                                + "section.\\par "
//                               + "The damage that each attack deals is noted "
//                                + "parenthetically. Damage from an attack is "
//                                + "always at least 1 point, even if a "
//                                + "subtraction from a die roll reduces the "
//                                + "result to 0 or lower.")),
//                    new AccessorCommand(new AccessorCommand.CommandAccessor()
//                      {
//                        @SuppressWarnings(value = "unchecked")
//                        public Command get(ValueGroup inEntry,
//                                           CampaignData inCampaign)
//                        {
//                          Monster entry = (Monster)inEntry;
//                          BaseMonster base = (BaseMonster)entry.m_base;

//                          if(base == null)
//                            return new Color("error", "no base entry");

//                          Multiple primary =
//                            (Multiple)base.m_primaryAttacks.get(0);

//                          String weapons = "";
//                          for(Iterator<Value> i =
//                                entry.m_possessions.iterator();
//                              i.hasNext(); )
//                          {
//                          AbstractEntry aEntry = ((EntryValue)i.next()).get();

//                            if(!aEntry.hasAttachment(Weapon.class))
//                              continue;

//                            Item weapon = (Item)aEntry;

//                            int style =
//                              ((Selection)weapon.getValue
//                               ("weapon style"))
//                              .getSelected();

//                            if(weapons.length() > 0)
//                              weapons += " or ";

//                            // compute the attack bonus
//                            Composite attack;

//                            if(style < 4)
//                              attack = entry.m_attackMelee;
//                            else
//                              attack = entry.m_attackRanged;

//                            Composite damageMod =
//                              new Composite(0, true,
//                                            new net.ixitxachitls.dma.values
//                                            .Modifier
//                                            (entry.getAbilityModifier
//                                             (Global.Ability.STRENGTH),
//                                             net.ixitxachitls.dma.values
//                                             .Modifier.Type.ABILITY, "Str",
//                                             null));

//                            // add a modifier, if necessary
//                            if(weapon.m_attack.hasModifiers())
//                            {
//                              attack = attack.clone();

//                              for(Iterator<net.ixitxachitls.dma.values
//                                    .Modifier> j =
//                                    weapon.m_attack.getModifiers();
//                                  j.hasNext(); )
//                                attack.addModifier(j.next());
//                            }

//                            if(weapon.m_damage.hasModifiers())
//                            {
//                              for(Iterator<net.ixitxachitls.dma.values
//                                    .Modifier> j =
//                                    weapon.m_damage.getModifiers();
//                                  j.hasNext(); )
//                                damageMod.addModifier(j.next());
//                            }

//                            Damage damage =
//                              (Damage)
//                              ((Multiple)weapon.getValue
//                            ("damate")).get(0).get();

//                            weapons += "\\link[Items/" + weapon.getID()
//                              + "]{" + weapon.getName() + "} "
//                              + attack.toPrint()
//                              + " \\link[index/attackStyles/"
//                              + (style < 4 ? "Melee" : "Ranged") + "]{"
//                              + (style < 4 ? "Melee" : "Ranged") + "}  ("
//                              + damage.convertValue(Value.Convert.PRINT,
//                                                    damageMod) + ")";
//                          }

//                          // nothing found
//                          if(primary == null)
//                            if(weapons.length() == 0)
//                              return new Command(Value.UNDEFINED);
//                            else
//                              return new Command(weapons);

//                          return new Command
//                            (weapons + " or \\link[index/attackmodes/"
//                             + primary.get(1).get().toStore() + "]{"
//                             + primary.get(1).get().toPrint() + "} "
//                             + (((EnumSelection<Global.AttackStyles>)
//                                 primary.get(2).get()).getSelected()
//                                == Global.AttackStyles.MELEE
//                                ? entry.m_attackMelee.toPrint()
//                                : entry.m_attackRanged.toPrint())
//                             + " \\link[index/attackstyles/"
//                             + primary.get(2).get().toStore() + "]{"
//                             + primary.get(2).get().toPrint() + "} ("
//                             + ((Damage)primary.get(3).get())
//                           .convertValue(Value.Convert.PRINT, entry.m_damage)
//                             + ")");
//                        }
//                      }),
//                    new Window(new Bold("Full Attack:"),
//                               Config.get
//                               ("resource:help/label.full.attack",
//                               "This line shows all the physical attacks the "
//                                + "creature makes when it uses a full-round "
//                               + "action to make a full attack. It gives the "
//                                + "number of attacks along with the weapon, "
//                               + "attack bonus, and form of attack (melee or "
//                                + "ranged). The first entry is for the "
//                                + "creature's primary weapon, with an attack "
//                               + "bonus including modifications for size and "
//                                + "Strength (for melee attacks) or Dexterity "
//                                + "(for ranged attacks). A creature with the "
//                                + "\\Feat{Weapon Finess} feat can use its "
//                               + "Dexterity modifier on melee attacks.\\par "
//                                + "The remaining weapons are secondary, and "
//                                + "attacks with them are made with a -5 "
//                                + "penalty to the attack roll, no matter how "
//                                + "many there are. Creatures with the "
//                                + "\\Feat{Multiattack} feat take only a -2 "
//                                + "penalty on secondary attacks.\\par "
//                              + "A creature's primary attack damage includes "
//                              + "its full Strength modidier (1-1/2 times its "
//                                + "Strength bonus if the attack is with the "
//                                + "creature's sole natural weapon) and is "
//                                + "given first. Secondary attacks only deal "
//                                + "1/2 the creature's Strength bonus and are"
//                                + " given second in the parantheses.\\par "
//                                + "If any attacks also have some special "
//                               + "effect other then damage (poison, disease, "
//                                + "energy drain, and so forth, that "
//                                + "information is given here.\\par "
//                                + "Unless noted otherwise, creatures using "
//                                + "natural weapons deal double damage on "
//                                + "critical hits.)")),
//                    new AccessorCommand(new AccessorCommand.CommandAccessor()
//                      {
//                       // TODO: the values here are not correctly computed and
//                        // don't take items into account
//                        @SuppressWarnings(value = "unchecked")
//                        public Command get(ValueGroup inEntry,
//                                          CampaignData inCampaign)
//                        {
//                          Monster monster  = (Monster)inEntry;
//                          BaseMonster base = (BaseMonster)monster.m_base;

//                          if(base == null)
//                            return new Color("error", "no base entry]");

//                          StringBuffer result = new StringBuffer();

//                          boolean first = true;
//                          for(Iterator<Value> i =
//                                monster.m_possessions.iterator();
//                              i.hasNext(); )
//                          {
//                          AbstractEntry entry = ((EntryValue)i.next()).get();

//                            if(!entry.hasAttachment(Weapon.class))
//                              continue;

//                            Item weapon = (Item)entry;

//                            int attack =
//                              ((Selection)weapon.getValue
//                               ("weapon style"))
//                              .getSelected();

//                            if(first)
//                              first = false;
//                            else
//                              result.append(" or ");

//                            Damage damage =
//                              (Damage)
//                              ((Multiple)weapon.getValue
//                               ("damage")).get(0).get();

//                            result.append
//                              ("\\link[Items/" + weapon.getID()
//                               + "]{" + weapon.getName() + "} "
//                               + (attack < 4 ? monster.m_attackMelee.toPrint()
//                                  : monster.m_attackRanged.toPrint())
//                               + " \\link[index/attackStyles/"
//                               + (attack < 4 ? "Melee" : "Ranged") + "]{"
//                               + (attack < 4 ? "Melee" : "Ranged") + "}  ("
//                               + damage.convertValue
//                               (Value.Convert.PRINT,
//                                new Composite(0, true,
//                                              new net.ixitxachitls.dma.values
//                                              .Modifier
//                                              (monster.getAbilityModifier
//                                               (Global.Ability.STRENGTH),
//                                               net.ixitxachitls.dma.values
//                                               .Modifier.Type.ABILITY, "Str",
//                                               null)))
//                               + ")");
//                          }

//                          if(!first && base.m_primaryAttacks.isDefined())
//                            result.append(" or ");

//                          for(Iterator<Value> i =
//                              base.m_primaryAttacks.iterator(); i.hasNext(); )
//                          {
//                            Multiple attack = (Multiple)i.next();

//                            // number of attacks, if any
//                            if(attack.get(0).get().isDefined())
//                            {
//                              result.append(attack.get(0).get().toString());
//                              result.append(" ");
//                            }

//                            // attack mode
//                            result.append("\\link[index/attackmodes/");
//                            result.append(attack.get(1).get().toString());
//                            result.append("]{");
//                            result.append(attack.get(1).get().toPrint());
//                            result.append("} ");

//                            // attack modifier
//                            if(((EnumSelection<Global.AttackStyles>)
//                                attack.get(2).get()).getSelected()
//                               == Global.AttackStyles.MELEE)
//                              result.append(monster.m_attackMelee.toPrint());
//                            else
//                              result.append(monster.m_attackRanged.toPrint());

//                            // attack style
//                            result.append(" \\link[index/attackstyles/");
//                            result.append(attack.get(2).get().toString());
//                            result.append("]{");
//                            result.append(attack.get(2).get().toPrint());
//                            result.append("} (");

//                            // damage
//                            result.append(((Damage)attack.get(3).get())
//                                          .convertValue(Value.Convert.PRINT,
//                                                        monster.m_damage));
//                            result.append(")");

//                            if(i.hasNext())
//                              result.append(", ");
//                          }

//                          if(base.m_secondaryAttacks.isDefined())
//                          {
//                            result.append(" and ");

//                            for(Iterator<Value> i =
//                                  base.m_secondaryAttacks.iterator();
//                                i.hasNext(); )
//                            {
//                              Multiple attack = (Multiple)i.next();

//                              // number of attacks, if any
//                              if(attack.get(0).get().isDefined())
//                              {
//                                result.append(attack.get(0).get().toString());
//                                result.append(" ");
//                              }

//                              // attack mode
//                              result.append("\\link[index/attackmodes/");
//                              result.append(attack.get(1).get().toString());
//                              result.append("]{");
//                              result.append(attack.get(1).get().toPrint());
//                              result.append("} ");

//                              // attack modifier
//                              if(((EnumSelection<Global.AttackStyles>)
//                                  attack.get(2).get()).getSelected()
//                                 == Global.AttackStyles.MELEE)
//                                result.append
//                                  (monster.m_attackMelee2nd.toPrint());
//                              else
//                                result.append
//                                  (monster.m_attackRanged2nd.toPrint());

//                              // attack style
//                              result.append(" \\link[index/attackstyles/");
//                              result.append(attack.get(2).get().toString());
//                              result.append("]{");
//                              result.append(attack.get(2).get().toPrint());
//                              result.append("} (");

//                              result.append
//                                (((Damage)attack.get(3).get()).convertValue
//                                 (Value.Convert.PRINT, monster.m_damage2nd));
//                              result.append(")");

//                              if(i.hasNext())
//                                result.append(", ");
//                            }
//                          }

//                          return new Command(result.toString());
//                        }
//                      }),
//                    new Window(new Bold("Space:"),
//                               Config.get
//                               ("resource:help/label.space", (String)null)),
//                    new BaseVariableCommand(BaseMonster.SPACE),
//                    new Window(new Bold("Reach:"),
//                               Config.get
//                               ("resource:help/label.reach", (String)null)),
//                    new BaseVariableCommand(BaseMonster.REACH),
//                    new Window(new Bold("Special Attacks:"),
//                               Config.get
//                               ("resource:help/label.special.attacks",
//                                (String)null)),
//                    new VariableCommand(BaseMonster.SPECIAL_ATTACKS),
//                    new Window(new Bold("Special Qualities:"),
//                               Config.get
//                               ("resource:help/label.special.qualities",
//                                (String)null)),
//                    new VariableCommand(BaseMonster.SPECIAL_QUALITIES),
//                    new Window(new Bold("Saves:"),
//                               Config.get
//                               ("resource:help/label.saves",
//                                "This line gives the creature's Fortitude, "
//                                + "Reflex and Will save modifiers.")),
//                    new AccessorCommand(new AccessorCommand.Accessor()
//                      {
//                        public String get(ValueGroup inEntry)
//                        {
//                          Monster monster = (Monster)inEntry;

//                          return "Fort " + monster.m_saveFort.toPrint()
//                            + ", Ref " + monster.m_saveRef.toPrint()
//                            + ", Will " + monster.m_saveWill.toPrint();
//                        }
//                      }),
//                    new Window(new Bold("Skills:"),
//                               Config.get
//                               ("resource:help/label.skills",
//                              "This line gives the creature's skills, along "
//                                + "with each skill's modifier (including "
//                              + "adjustments for ability scores, armor check "
//                                + "penalties, and any bonuses from feats or "
//                                + "racial traits). Bold skills are class "
//                              + "skills. A creature's type and intelligence "
//                              + "score determine the number skill points it "
//                                + "has (as shown last in the line).\\par "
//                                + "The Skills section of the creature's "
//                                + "description description recaps racial "
//                                + "bonuses and other bonuses and other "
//                              + "adjustments to skill modifiers for the sake "
//                                + "of clarity; these bonuses should not be "
//                                + "added to the listed skill modifiers.\\par "
//                              + "\\bold{Natural Tendencies:} Some creatures "
//                                + "simply aren't made for certain types of "
//                                + "physical activity. \\Monster{Elephants}, "
//                                + "despite their great Strength scores, are "
//                                + "terrible at jumping. \\Monster{Giant "
//                                + "crocodiles}, despite their high Strength "
//                              + "scores, don't climb well. \\Monster{Horses} "
//                                + "can't walk tightropes. If it seems clear "
//                                + "to you that a particular creature simply "
//                                + "is not made for a particular physical "
//                                + "activity, you can say that the creature "
//                                + "takes a -8 penalty on skill checks that "
//                                + "defy its natural tendencies. In extreme "
//                                + "circumstances (a \\Monster{porpoise} "
//                                + "attempting a \\Skill{Climb} check, for "
//                                + "instance) you can rule that the creature "
//                                + "fails the check automatically.")),
//                    new VariableCommand(SKILLS),
//                    new Window(new Bold("Feats:"),
//                               Config.get
//                               ("resource:help/label.feats", (String)null)),
//                    new VariableCommand(BaseMonster.FEATS),
//                    new Window(new Bold("Money:"),
//                               Config.get
//                               ("resource:help/label.money",
//                                "The monetary treasure the monster has. It "
//                              + "can either be carried around or rest in the "
//                                + "creature's lair or even partially in one "
//                                + "place and the other.")),
//                    new VariableCommand(MONEY),
//                    new Window(new Bold("Possessions:"),
//                               Config.get
//                               ("resource:help/label.possessions",
//                                "The treasure the monster has besides money. "
//                               + "It can either be carried around or rest in "
//                                + "the creature's lair or even partially in "
//                                + "one place and the other. The monster will "
//                                + "use treasure that it can and knows how to "
//                                + "use.")),
//                    new VariableCommand(BaseMonster.POSSESSIONS),
//                    new Skip(new AttachmentCommand(true)),
//                    BaseEntry.s_referencesLabel,
//                    BaseEntry.s_referencesCmd,
//                    BaseEntry.s_errorLabel,
//                    BaseEntry.s_errorCmd,
//                  }),
//                new Divider("clear", ""),
//              })),
//           new Nopictures(new Table(new int [] { -"Illustrations: ".length(),
//                                                  100 },
//                                     new Document.Alignment []
//              { Document.Alignment.left, Document.Alignment.left }, null,
//                                     new Object []
//              {
//                new Bold("World:"),
//                new BaseVariableCommand(PropertyKey.getKey("world")),
//              })),
//            "\n",
//          }));
  }

  //........................................................................
  //----- brief ------------------------------------------------------------

  /** The command for the monster in brief. */
  static
  {
//     addCommand
//       (Monster.class, PrintType.brief,
//        new Command(new Object []
//          {
//            new Table("70:L;30:R", new Object []
//              {
//                new IfDefinedCommand
//                (NAME,
//                 new Command(new Object []
//                   {
//                     new IfDefinedCommand(NAME, new VariableCommand(NAME),
//                                          null),
//                     " (",
//                     new NormalSize(new Bold(CMD_NAME_ONLY)),
//                     ")",
//                   }),
//                 new NormalSize(new Bold(CMD_NAME_ONLY))),
//                new NormalSize(new Bold(new Command(new Object []
//                  {
//                    "CR ",
//                    new BaseVariableCommand(BaseMonster.CR),
//                  }))),
//              }),
//            new AccessorCommand(new AccessorCommand.Accessor()
//              {
//                public String get(ValueGroup inEntry)
//                {
//                  BaseMonster base =
//                    (BaseMonster)((Monster)inEntry).m_base;

//                  if(base == null)
//                    return "\\color{error}{no base}";

//                  return base.m_alignment.get(1).get()
//                    .convert(Value.Convert.SHORT);
//                }
//              }),
//            " ",
//            new AccessorCommand(new AccessorCommand.Accessor()
//              {
//                public String get(ValueGroup inEntry)
//                {
//                  BaseMonster base =
//                    (BaseMonster)((Monster)inEntry).m_base;

//                  if(base == null)
//                    return "\\color{error}{no base}";

//                  return base.m_size.get(0).get()
//                    .convert(Value.Convert.PRINT);
//                }
//              }),
//            " ",
//            new BaseVariableCommand(BaseMonster.MONSTER_TYPE),
//            new Linebreak(),
//            new Bold("Init "),
//            new AccessorCommand(new AccessorCommand.Accessor()
//              {
//                // a computed value
//                public String get(ValueGroup inEntry)
//                {
//                  return ((Monster)inEntry)
//                    .m_initiative.convert(Value.Convert.SHORT);
//                }
//              }),
//            "; ",
//            new Bold("Senses "),
//            new AccessorCommand(new AccessorCommand.Accessor()
//              {
//                public String get(ValueGroup inEntry)
//                {
//                  Monster monster = (Monster)inEntry;

//                  StringBuffer result = new StringBuffer();

//                  boolean first = true;
//                  // the monsters sense special qualities
//                  for(Iterator<Value> i =
//                        monster.m_specialQualities.iterator();
//                      i.hasNext(); )
//                  {
//                    Quality quality =
//                      (Quality)((EntryValue)i.next()).get();

//                    if(!quality.hasCategory("sense"))
//                      continue;

//                    if(first)
//                      first = false;
//                    else
//                      result.append(", ");

//                    result.append(quality.convert(Value.Convert.SHORT));
//                  }

//                  // the monsters sense skills
//                  for(Iterator<Value> i = monster.m_skills.iterator();
//                      i.hasNext(); )
//                  {
//                    Skill skill = (Skill)((EntryValue)i.next()).get();

//                    // check that we have a sense category
//                    if(!skill.hasCategory("sense"))
//                      continue;

//                    if(first)
//                      first = false;
//                    else
//                      result.append(", ");

//                    result.append(skill.convert(Value.Convert.SHORT));
//                  }

//                  return result.toString();
//                }
//              }),
//            new AccessorCommand(new AccessorCommand.CommandAccessor()
//              {
//                public Command get(ValueGroup inEntry,
//                                   CampaignData inCampaign)
//                {
//                  Monster monster = (Monster)inEntry;

//                  StringBuffer result = new StringBuffer();

//                  boolean first = true;
//                  // the monsters sense special qualities
//                  for(Iterator<Value> i =
//                        monster.m_specialQualities.iterator();
//                      i.hasNext(); )
//                  {
//                    Quality quality =
//                      (Quality)((EntryValue)i.next()).get();

//                    if(!quality.hasCategory("aura"))
//                      continue;

//                    if(first)
//                      first = false;
//                    else
//                      result.append(", ");

//                    result.append(quality.convert(Value.Convert.SHORT));
//                  }

//                  if(result.length() > 0)
//                    return new Command(new Object []
//                      {
//                        new Linebreak(),
//                        new Bold("Aura "),
//                        new Command(result.toString()),
//                      });
//                  else
//                    return null;
//                }
//              }),
//            new Linebreak(),
//            new Bold("Languages "),
//            new BaseVariableCommand(BaseMonster.LANGUAGES),
//            new AccessorCommand(new AccessorCommand.Accessor()
//              {
//                public String get(ValueGroup inEntry)
//                {
//                  Monster monster = (Monster)inEntry;

//                  StringBuffer result = new StringBuffer();

//                  boolean first = true;
//                  // the monsters sense special qualities
//                  for(Iterator<Value> i =
//                        monster.m_specialQualities.iterator();
//                      i.hasNext(); )
//                  {
//                    Quality quality =
//                      (Quality)((EntryValue)i.next()).get();

//                    if(!quality.hasCategory("communication"))
//                      continue;

//                    result.append(", ");
//                    result.append(quality.convert(Value.Convert.SHORT));
//                  }

//                  return result.toString();
//                }
//              }),
//            new Hrule(),
//            new Bold("AC "),
//            new AccessorCommand(new AccessorCommand.Accessor()
//              {
//                public String get(ValueGroup inEntry)
//                {
//                  Monster monster = (Monster)inEntry;

//                  // TODO: should only clone if necessary
//                  Composite ac = monster.m_ac.clone();
//                  Composite flat  = monster.m_acFlatFooted.clone();

//                  for(Iterator<Value> i = monster.m_possessions.iterator();
//                      i.hasNext(); )
//                  {
//                    AbstractEntry entry = ((EntryValue)i.next()).get();

//                    if(!entry.hasAttachment(Armor.class))
//                      continue;

//                    Item armor = (Item)entry;

//                    if(armor.m_ac.hasModifiers())
//                      for(Iterator<net.ixitxachitls.dma.values.Modifier> j =
//                            armor.m_ac.getModifiers(); j.hasNext(); )
//                      {
//                        net.ixitxachitls.dma.values.Modifier modifier =
//                          j.next();

//                        ac.addModifier(modifier);
//                        flat.addModifier(modifier);
//                      }
//                  }

//                  return ac.convert(Value.Convert.SHORT) + ", touch "
//                    + monster.m_acTouch.convert(Value.Convert.SHORT)
//                    + ", flat-footed " + flat.convert(Value.Convert.SHORT);
//                }
//              }),
//            new Linebreak(),
//            new Bold("hp "),
//            new VariableCommand(HP),
//            new AccessorCommand(new AccessorCommand.Accessor()
//              {
//                public String get(ValueGroup inEntry)
//                {
//                  BaseMonster base =
//                    (BaseMonster)((Monster)inEntry).m_base;

//                  if(base == null)
//                    return " (\\color{error}{no base})";

//                  return " (" + base.m_hitDice.getNumber() + " HD)";
//                }
//              }),
//            new AccessorCommand(new AccessorCommand.Accessor()
//              {
//                public String get(ValueGroup inEntry)
//                {
//                  Monster monster = (Monster)inEntry;

//                  StringBuffer result = new StringBuffer();

//                  boolean first = true;
//                  // the monsters sense special qualities
//                  for(Iterator<Value> i =
//                        monster.m_specialQualities.iterator();
//                      i.hasNext(); )
//                  {
//                    Quality quality =
//                      (Quality)((EntryValue)i.next()).get();

//                    if(!quality.hasCategory("health"))
//                      continue;

//                    result.append(", ");
//                    result.append(quality.convert(Value.Convert.SHORT));
//                  }

//                  return result.toString();
//                }
//              }),
//            new AccessorCommand(new AccessorCommand.CommandAccessor()
//              {
//                public Command get(ValueGroup inEntry,
//                                   CampaignData inCampaign)
//                {
//                  Monster monster = (Monster)inEntry;

//                  StringBuffer result = new StringBuffer();

//                  boolean first = true;
//                  // the monsters sense special qualities
//                  for(Iterator<Value> i =
//                        monster.m_specialQualities.iterator();
//                      i.hasNext(); )
//                  {
//                    Quality quality =
//                      (Quality)((EntryValue)i.next()).get();

//                    if(!quality.hasCategory("immunity"))
//                      continue;

//                    if(first)
//                      first = false;
//                    else
//                      result.append(", ");

//                    result.append(quality.convert(Value.Convert.SHORT));
//                  }

//                  if(result.length() > 0)
//                    return new Command(new Object []
//                      {
//                        new Linebreak(),
//                        new Bold("Immune "),
//                        new Command(result.toString()),
//                      });
//                  else
//                    return null;
//                }
//              }),
//            new AccessorCommand(new AccessorCommand.CommandAccessor()
//              {
//                public Command get(ValueGroup inEntry,
//                                   CampaignData inCampaign)
//                {
//                  Monster monster = (Monster)inEntry;

//                  StringBuffer result = new StringBuffer();

//                  boolean first = true;
//                  // the monsters sense special qualities
//                  for(Iterator<Value> i =
//                        monster.m_specialQualities.iterator();
//                      i.hasNext(); )
//                  {
//                    Quality quality =
//                      (Quality)((EntryValue)i.next()).get();

//                    if(!quality.hasCategory("resistance"))
//                      continue;

//                    if(first)
//                      first = false;
//                    else
//                      result.append(", ");

//                    result.append(quality.convert(Value.Convert.SHORT));
//                  }

//                  // TODO: add spell resistance!!

//                  if(result.length() > 0)
//                    return new Command(new Object []
//                      {
//                        new Linebreak(),
//                        new Bold("Resist "),
//                        new Command(result.toString()),
//                      });
//                  else
//                    return null;
//                }
//              }),
//            new Linebreak(),
//            new Bold("Fort "),
//            new AccessorCommand(new AccessorCommand.Accessor()
//              {
//                public String get(ValueGroup inEntry)
//                {
//                  Monster monster = (Monster)inEntry;

//                  return
//                    monster.m_saveFort.convert(Value.Convert.SHORT);
//                }
//              }),
//            ", ",
//            new Bold("Ref "),
//            new AccessorCommand(new AccessorCommand.Accessor()
//              {
//                public String get(ValueGroup inEntry)
//                {
//                  Monster monster = (Monster)inEntry;

//                  return
//                    monster.m_saveRef.convert(Value.Convert.SHORT);
//                }
//              }),
//            ", ",
//            new Bold("Will "),
//            new AccessorCommand(new AccessorCommand.Accessor()
//              {
//                public String get(ValueGroup inEntry)
//                {
//                  Monster monster = (Monster)inEntry;

//                  return
//                    monster.m_saveWill.convert(Value.Convert.SHORT);
//                }
//              }),
//            new Linebreak(),
//            new AccessorCommand(new AccessorCommand.CommandAccessor()
//              {
//                public Command get(ValueGroup inEntry,
//                                   CampaignData inCampaign)
//                {
//                  Monster monster = (Monster)inEntry;

//                  StringBuffer result = new StringBuffer();

//                  boolean first = true;
//                  // the monsters sense special qualities
//                  for(Iterator<Value> i =
//                        monster.m_specialQualities.iterator();
//                      i.hasNext(); )
//                  {
//                    Quality quality =
//                      (Quality)((EntryValue)i.next()).get();

//                    if(!quality.hasCategory("vulnerability"))
//                      continue;

//                    if(first)
//                      first = false;
//                    else
//                      result.append(", ");

//                    result.append(quality.convert(Value.Convert.SHORT));
//                  }

//                  // TODO: add spell resistance!!

//                  if(result.length() > 0)
//                    return new Command(new Object []
//                      {
//                        new Bold("Weakness "),
//                        new Command(result.toString()),
//                        new Linebreak(),
//                      });
//                  else
//                    return null;
//                }
//              }),
//            new Hrule(),
//            new Bold("Speed "),
//            new BaseVariableCommand(BaseMonster.SPEED, true),
//            new Linebreak(),
//            new AccessorCommand(new AccessorCommand.CommandAccessor()
//              {
//                @SuppressWarnings(value = "unchecked")
//                public Command get(ValueGroup inEntry,
//                                   CampaignData inCampaign)
//                {
//                  Monster monster = (Monster)inEntry;

//                  BaseMonster base = (BaseMonster)monster.m_base;

//                  if(base == null)
//                    return new Color("error", "no base found");

//                  StringBuffer result = new StringBuffer();

//                  boolean first   = true;
//                  boolean weapons = false;

//                  for(Iterator<Value> i = monster.m_possessions.iterator();
//                      i.hasNext(); )
//                  {
//                    AbstractEntry entry = ((EntryValue)i.next()).get();

//                    if(!entry.hasAttachment(Weapon.class))
//                      continue;

//                    Item weapon = (Item)entry;

//                    int attack =
//                      ((Selection)weapon.getValue
//                       ("weapon style")).getSelected();

//                    // only melee
//                    if(attack >= 4)
//                      continue;

//                    if(first)
//                      first = false;
//                    else
//                      result.append(" or ");

//                    weapons = true;

//                    Damage damage =
//                      (Damage)
//                      ((Multiple)weapon.getValue("damage"))
//                      .get(0).get();

//                    Composite damageMod =
//                      new Composite(0, true,
//                                    new net.ixitxachitls.dma.values
//                                    .Modifier
//                                    (monster.getAbilityModifier
//                                     (Global.Ability.STRENGTH),
//                                     net.ixitxachitls.dma.values.Modifier
//                                     .Type.ABILITY, "Str", null));


//                    Composite attackMod = monster.m_attackMelee.clone();

//                    // add a modifier, if necessary
//                    if(weapon.m_attack.hasModifiers())
//                    {
//                      for(Iterator<net.ixitxachitls.dma.values
//                            .Modifier> j =
//                            weapon.m_attack.getModifiers();
//                          j.hasNext(); )
//                        attackMod.addModifier(j.next());
//                    }

//                    if(weapon.m_damage.hasModifiers())
//                    {
//                      for(Iterator<net.ixitxachitls.dma.values
//                            .Modifier> j =
//                            weapon.m_damage.getModifiers();
//                          j.hasNext(); )
//                        damageMod.addModifier(j.next());
//                    }

//                    result.append
//                      (weapon.getName() + " "
//                       + attackMod.toString() + " ("
//                       + damage.convertValue(Value.Convert.SHORT, damageMod)
//                       + ")");
//                  }

//                  if(!first && base.m_primaryAttacks.isDefined())
//                    result.append(" or ");

//                  first = true;

//                  for(Iterator<Value> i =
//                        base.m_primaryAttacks.iterator();
//                      i.hasNext(); )
//                  {
//                    Multiple attack = (Multiple)i.next();

//                    if(((EnumSelection<Global.AttackStyles>)
//                        attack.get(2).get()).getSelected()
//                       != Global.AttackStyles.MELEE)
//                      continue;

//                    if(weapons)
//                      result.append(" or ");

//                    if(first)
//                      first = false;
//                    else
//                      result.append(", ");

//                    // number of attacks
//                    if(attack.get(0).get().isDefined())
//                    {
//                      result.append(attack.get(0).get().convert
//                                    (Value.Convert.SHORT));
//                      result.append(" ");
//                    }

//                    // attack mode
//                    result.append(attack.get(1).get().convert
//                                  (Value.Convert.SHORT));
//                    result.append(" ");

//                    // attack modifier
//                    if(((EnumSelection<Global.AttackStyles>)
//                        attack.get(2).get()).getSelected()
//                       == Global.AttackStyles.MELEE )
//                      result.append(monster.m_attackMelee.convert
//                                    (Value.Convert.SHORT));
//                    else
//                      result.append(monster.m_attackRanged.convert
//                                    (Value.Convert.SHORT));

//                    // attack style
//                    //result.append(attack.get(2).get().toPrint());

//                    // damage
//                    result.append(" (");
//                    result.append(((Damage)attack.get(3).get())
//                                  .convertValue(Value.Convert.SHORT,
//                                                monster.m_damage));
//                    result.append(")");
//                  }

//                  for(Iterator<Value> i =
//                        base.m_secondaryAttacks.iterator();
//                      i.hasNext(); )
//                  {
//                    Multiple attack = (Multiple)i.next();

//                    if(((EnumSelection<Global.AttackStyles>)
//                        attack.get(2).get()).getSelected()
//                       != Global.AttackStyles.MELEE)
//                      continue;

//                    if(first)
//                      first = false;
//                    else
//                      result.append(", ");

//                    // number of attacks
//                    if(attack.get(0).get().isDefined())
//                    {
//                      result.append(attack.get(0).get().convert
//                                    (Value.Convert.SHORT));
//                      result.append(" ");
//                    }

//                    // attack mode
//                    result.append(attack.get(1).get().convert
//                                  (Value.Convert.SHORT));
//                    result.append(" ");

//                    // attack modifier
//                    result.append(monster.m_attackMelee2nd.convert
//                                  (Value.Convert.SHORT));

//                    // damage
//                    result.append(" (");
//                    result.append(attack.get(3).get().convert
//                                  (Value.Convert.SHORT));
//                    result.append(")");
//                  }

//                  if(result.length() > 0)
//                    return new Command(new Object []
//                      {
//                        new Bold("Melee "),
//                        result.toString(),
//                        new Linebreak(),
//                      });
//                  else
//                    return null;
//                }
//              }),
//            new AccessorCommand(new AccessorCommand.CommandAccessor()
//              {
//                @SuppressWarnings(value = "unchecked")
//                public Command get(ValueGroup inEntry,
//                                   CampaignData inCampaign)
//                {
//                  Monster monster = (Monster)inEntry;

//                  BaseMonster base = (BaseMonster)monster.m_base;

//                  if(base == null)
//                    return new Color("error", "no base monster found");

//                  StringBuffer result = new StringBuffer();

//                  boolean first   = true;
//                  boolean weapons = false;

//                  for(Iterator<Value> i = monster.m_possessions.iterator();
//                      i.hasNext(); )
//                  {
//                    AbstractEntry entry = ((EntryValue)i.next()).get();

//                    if(!entry.hasAttachment(Weapon.class))
//                      continue;

//                    Item weapon = (Item)entry;

//                    int attack =
//                      ((Selection)weapon
//                       .getValue("weapon style"))
//                      .getSelected();

//                    // only ranged
//                    if(attack < 4)
//                      continue;

//                    if(first)
//                      first = false;
//                    else
//                      result.append(" or ");

//                    weapons = true;

//                    Damage damage =
//                      (Damage)
//                      ((Multiple)weapon.getValue("damage"))
//                      .get(0).get();

//                    Composite damageMod =
//                      new Composite(0, true,
//                                    new net.ixitxachitls.dma.values.Modifier
//                                    (monster.getAbilityModifier
//                                     (Global.Ability.STRENGTH),
//                                     net.ixitxachitls.dma.values.Modifier
//                                     .Type.ABILITY, "Str", null));

//                    Composite attackMod = monster.m_attackRanged.clone();

//                    // add a modifier, if necessary
//                    if(weapon.m_attack.hasModifiers())
//                    {
//                      for(Iterator<net.ixitxachitls.dma.values
//                            .Modifier> j =
//                            weapon.m_attack.getModifiers();
//                          j.hasNext(); )
//                        attackMod.addModifier(j.next());
//                    }

//                    if(weapon.m_damage.hasModifiers())
//                    {
//                      for(Iterator<net.ixitxachitls.dma.values
//                            .Modifier> j =
//                            weapon.m_damage.getModifiers();
//                          j.hasNext(); )
//                        damageMod.addModifier(j.next());
//                    }

//                    result.append
//                      (weapon.getName() + " "
//                       + attackMod.toString() + " ("
//                       + damage.convertValue(Value.Convert.SHORT, damageMod)
//                       + ")");
//                  }

//                  first = true;

//                  for(Iterator<Value> i =
//                        base.m_primaryAttacks.iterator();
//                      i.hasNext(); )
//                  {
//                    Multiple attack = (Multiple)i.next();

//                    if(((EnumSelection<Global.AttackStyles>)
//                        attack.get(2).get()).getSelected()
//                       != Global.AttackStyles.RANGED)
//                      continue;

//                    if(weapons)
//                      result.append(" or ");

//                    if(first)
//                      first = false;
//                    else
//                      result.append(", ");

//                    // number of attacks
//                    if(attack.get(0).get().isDefined())
//                    {
//                      result.append(attack.get(0).get().convert
//                                    (Value.Convert.SHORT));
//                      result.append(" ");
//                    }

//                    // attack mode
//                    result.append(attack.get(1).get().convert
//                                  (Value.Convert.SHORT));
//                    result.append(" ");

//                    // attack modifier
//                    if(((EnumSelection<Global.AttackStyles>)
//                        attack.get(2).get()).getSelected()
//                       == Global.AttackStyles.MELEE )
//                      result.append(monster.m_attackMelee.convert
//                                    (Value.Convert.SHORT));
//                    else
//                      result.append(monster.m_attackRanged.convert
//                                    (Value.Convert.SHORT));

//                    // attack style
//                    result.append(attack.get(2).get().toPrint());

//                    // damage
//                    result.append(" (");
//                    result.append(((Damage)attack.get(3).get())
//                                  .convertValue(Value.Convert.SHORT,
//                                                monster.m_damage));
//                    result.append(")");
//                  }

//                  for(Iterator<Value> i =
//                        base.m_secondaryAttacks.iterator();
//                      i.hasNext(); )
//                  {
//                    Multiple attack = (Multiple)i.next();

//                    if(((EnumSelection<Global.AttackStyles>)
//                        attack.get(2).get()).getSelected()
//                       != Global.AttackStyles.RANGED)
//                      continue;

//                    if(first)
//                      first = false;
//                    else
//                      result.append(", ");

//                    // number of attacks
//                    if(attack.get(0).get().isDefined())
//                    {
//                      result.append(attack.get(0).get().convert
//                                    (Value.Convert.SHORT));
//                      result.append(" ");
//                    }

//                    // attack mode
//                    result.append(attack.get(1).get().convert
//                                  (Value.Convert.SHORT));
//                    result.append(" ");

//                    // attack modifier
//                    result.append(monster.m_attackMelee2nd.convert
//                                  (Value.Convert.SHORT));

//                    // damage
//                    result.append(" (");
//                    result.append(attack.get(3).get().convert
//                                  (Value.Convert.SHORT));
//                    result.append(")");
//                  }

//                  if(result.length() > 0)
//                    return new Command(new Object []
//                      {
//                        new Bold("Ranged "),
//                        result.toString(),
//                        new Linebreak(),
//                      });
//                  else
//                    return null;
//                }
//              }),
//            new AccessorCommand(new AccessorCommand.CommandAccessor()
//              {
//                public Command get(ValueGroup inEntry,
//                                   CampaignData inCampaign)
//                {
//                  Monster monster = (Monster)inEntry;
//                  BaseMonster base = (BaseMonster)monster.m_base;

//                  if(base == null)
//                    return new Color("error", "no base monster found");

//                  ArrayList<Object> commands = new ArrayList<Object>();

//                  // only print something if not 5 ft
//                  if(base.m_space.getAsFeet().compare(5) != 0)
//                  {
//                    commands.add(new Bold("Space "));
//                    commands.add(base.m_space.convert
//                                 (Value.Convert.SHORT));
//                    commands.add(" ");
//                  }

//                  // only print something if not 5 ft
//                  if(base.m_reach.getAsFeet().compare(5) != 0)
//                  {
//                    commands.add(new Bold("Reach "));
//                    commands.add(base.m_reach.convert
//                                 (Value.Convert.SHORT));
//                  }

//                  if(commands.size() > 0)
//                  {
//                    commands.add(new Linebreak());

//                    return
//                      new Command(commands.toArray(new Object [0]));
//                  }

//                  return null;
//                }
//              }),
//            new Bold("Base Atk "),
//            new BaseVariableCommand(BaseMonster.BASE_ATTACK),
//            "; ",
//            new Bold("Grp "),
//            new AccessorCommand(new AccessorCommand.Accessor()
//              {
//                public String get(ValueGroup inEntry)
//                {
//                  Monster monster = (Monster)inEntry;

//                  return monster.m_grapple.convert(Value.Convert.SHORT);
//                }
//              }),
//            new Linebreak(),
//            new AccessorCommand(new AccessorCommand.CommandAccessor()
//              {
//                public Command get(ValueGroup inEntry,
//                                   CampaignData inCampaign)
//                {
//                  Monster monster = (Monster)inEntry;

//                  StringBuffer result = new StringBuffer();

//                  boolean first = true;
//                  // the monsters sense special qualities
//                  for(Iterator<Value> i =
//                        monster.m_specialAttacks.iterator();
//                      i.hasNext(); )
//                  {
//                    Quality quality =
//                      (Quality)((EntryValue)i.next()).get();

//                    if(!quality.hasCategory("attack"))
//                      continue;

//                    if(first)
//                      first = false;
//                    else
//                      result.append(", ");

//                    result.append(quality.convert(Value.Convert.SHORT));
//                  }

//                  if(result.length() > 0)
//                    return new Command(new Object []
//                      {
//                        new Bold("Atk Options "),
//                        new Command(result.toString()),
//                        new Linebreak(),
//                      });
//                  else
//                    return null;
//                }
//              }),
//            new AccessorCommand(new AccessorCommand.CommandAccessor()
//              {
//                public Command get(ValueGroup inEntry,
//                                   CampaignData inCampaign)
//                {
//                  Monster monster = (Monster)inEntry;

//                  StringBuffer result = new StringBuffer();

//                  boolean first = true;
//                  // the monsters sense special qualities
//                  for(Iterator<Value> i =
//                        monster.m_specialAttacks.iterator();
//                      i.hasNext(); )
//                  {
//                    Quality quality =
//                      (Quality)((EntryValue)i.next()).get();

//                    if(!quality.hasCategory("action"))
//                      continue;

//                    if(first)
//                      first = false;
//                    else
//                      result.append(", ");

//                    result.append(quality.convert(Value.Convert.SHORT));
//                  }

//                  if(result.length() > 0)
//                    return new Command(new Object []
//                      {
//                        new Bold("Special Actions "),
//                        new Command(result.toString()),
//                        new Linebreak(),
//                      });
//                  else
//                    return null;
//                }
//              }),
//            // missing: combat gear
//            // missing: spells known
//            // missing: spells prepared
//            // missing: spell-like abilities
//            new Hrule(),
//            new Bold("Abilities "),
//            "Str ",
//            new BaseVariableCommand(BaseMonster.STRENGTH),
//            ", Dex ",
//            new BaseVariableCommand(BaseMonster.DEXTERITY),
//            ", Con ",
//            new BaseVariableCommand(BaseMonster.CONSTITUTION),
//            ", Int ",
//            new BaseVariableCommand(BaseMonster.INTELLIGENCE),
//            ", Wis ",
//            new BaseVariableCommand(BaseMonster.WISDOM),
//            ", Cha ",
//            new BaseVariableCommand(BaseMonster.CHARISMA),
//            new Linebreak(),
//            new AccessorCommand(new AccessorCommand.CommandAccessor()
//              {
//                public Command get(ValueGroup inEntry,
//                                   CampaignData inCampaign)
//                {
//                  Monster monster = (Monster)inEntry;

//                  StringBuffer result = new StringBuffer();

//                  boolean first = true;
//                  // the monsters sense special qualities
//                  for(Iterator<Value> i =
//                        monster.m_specialQualities.iterator();
//                      i.hasNext(); )
//                  {
//                    Quality quality =
//                      (Quality)((EntryValue)i.next()).get();

//                    // don't print the ones already printed
//                    if(quality.hasCategory("sense")
//                       || quality.hasCategory("aura")
//                       || quality.hasCategory("communication")
//                       || quality.hasCategory("health")
//                       || quality.hasCategory("immunity")
//                       || quality.hasCategory("resistance")
//                       || quality.hasCategory("vulnerability"))
//                      continue;

//                    if(first)
//                      first = false;
//                    else
//                      result.append(", ");

//                    result.append(quality.convert(Value.Convert.SHORT));
//                  }

//                  if(result.length() > 0)
//                    return new Command(new Object []
//                      {
//                        new Bold("SQ "),
//                        new Command(result.toString()),
//                        new Linebreak(),
//                      });
//                  else
//                    return null;
//                }
//              }),
//            new Bold("Feats "),
//            new BaseVariableCommand(BaseMonster.FEATS),
//            new Linebreak(),
//            new Bold("Skills "),
//            new AccessorCommand(new AccessorCommand.Accessor()
//              {
//                public String get(ValueGroup inEntry)
//                {
//                  Monster monster = (Monster)inEntry;

//                  StringBuffer result = new StringBuffer();

//                  boolean first = true;
//                  for(Iterator<Value> i = monster.m_skills.iterator();
//                      i.hasNext(); )
//                  {
//                    Skill skill = (Skill)((EntryValue)i.next()).get();

//                    // check that we have more than just the attribute
//                    if(!skill.isSpecial())
//                      continue;

//                    if(first)
//                      first = false;
//                    else
//                      result.append(", ");

//                    result.append(skill.convert(Value.Convert.SHORT));
//                  }

//                  return result.toString();
//                }
//              }),
//            new Linebreak(),
//            new IfDefinedCommand(BaseMonster.POSSESSIONS,
//                                 new Bold("Possessions "), null),
//            new IfDefinedCommand(BaseMonster.POSSESSIONS,
//                                new VariableCommand(BaseMonster.POSSESSIONS),
//                                 null),
//            // missing: spellbook
//            new Hrule(),
//            new AccessorCommand(new AccessorCommand.CommandAccessor()
//            {
//              public Command get(ValueGroup inEntry, CampaignData inCampaign)
//              {
//                Monster     monster = (Monster)inEntry;
//                BaseMonster base    = (BaseMonster)monster.m_base;

//                if(base == null)
//                  return new Color("error", "no base monster found");

//                ArrayList<String> commands = new ArrayList<String>();

//                for(Iterator<Value> i = base.m_references.iterator();
//                    i.hasNext(); )
//                {
//                  Multiple reference = (Multiple)i.next();
//                  String   id        = ((Text)reference.get(0).get()).get();
//                  ValueList     pages     = (ValueList)reference.get(1).get();

//                  // get the title of the product
//                  BaseProduct product =
//                    (BaseProduct)BaseCampaign.GLOBAL.getBaseEntry(id);

//                  if(product == null)
//                    commands.add(id + " p. " + pages);
//                  else
//                    commands.add(product.getFullTitle() + " [" + id + "] p. "
//                                 + pages);
//                }

//                return
//                  new Right(new Tiny(new Command
//                                     (commands.toArray(new Object[0]))));
//              }
//            }),
//            // descriptions about the feats and qualities
//            new Scriptsize
//            (new AccessorCommand(new AccessorCommand.CommandAccessor()
//              {
//               public Command get(ValueGroup inEntry, CampaignData inCampaign)
//                {
//                  java.util.List<Object> commands = new ArrayList<Object>();

//                  Monster monster = (Monster)inEntry;

//                  for(Iterator<Value> i = monster.m_feats.iterator();
//                      i.hasNext(); )
//                  {
//                    Feat feat = (Feat)((EntryValue)i.next()).get();

//                  commands.add(feat.getCommand(inCampaign, PrintType.brief));
//                  }

//                 for(Iterator<Value> i = monster.m_specialAttacks.iterator();
//                      i.hasNext(); )
//                  {
//                    Quality feat = (Quality)((EntryValue)i.next()).get();

//                  commands.add(feat.getCommand(inCampaign, PrintType.brief));
//                  }

//               for(Iterator<Value> i = monster.m_specialQualities.iterator();
//                      i.hasNext(); )
//                  {
//                    Quality feat = (Quality)((EntryValue)i.next()).get();

//                  commands.add(feat.getCommand(inCampaign, PrintType.brief));
//                  }

//                  return new Command(commands.toArray(new Object[0]));
//                }
//              })),
//            new Linebreak(),
//            new Scriptsize(new AttachmentCommand(false)),
//            " ",
//          }));
  }

  //........................................................................
  //----- reference --------------------------------------------------------

  /** The command for printing a reference to the item. */
  static
  {
//     addCommand
//       (Monster.class, PrintType.reference,
//        new Command(new Object []
//          {
//            new Label("Monster"),
//            CMD_NAME,
//            CMD_ID,
//            new VariableCommand(PropertyKey.getKey("short description")),
//            new VariableCommand(HP),
//            new BaseVariableCommand(PropertyKey.getKey("world")),
//            BaseEntry.ALL_CATEGORIES,
//          }));

//     addCommand(Monster.class, PrintType.referenceFormat,
//                new Command("1:L(icon);"
//                            + "1:L(name)[Name];"
//                            + "1:L(id)[ID];"
//                            + "100:L(description)[Short Description];"
//                            + "1:L(hp)[HP];"
//                            + "1:L(world)[World];"
//                            + "1:L(categories)[Categories]"));
  }

  //........................................................................

  //........................................................................
  //----- indices ----------------------------------------------------------

  static
  {
    //----- initiative -----------------------------------------------------

//     s_indices.add
//       (new IndexInfo
//        (Config.get("resource:html/title.initiatives",
//                    "Initiatives"),
//         Config.get("resource:html/dir.initiatives",
//                    "initiatives"), "Monster", true,
//         new Filter<AbstractEntry>()
//        {
//          public boolean accept(AbstractEntry inEntry)
//            {
//              return inEntry instanceof Monster;
//            }
//        },
//         new Identificator<ValueGroup>()
//        {
//          public String []id(ValueGroup inEntry)
//            {
//              Pair<Integer, Integer>limits =
//                ((Monster)inEntry).m_initiative.getLimits();

//              if(limits.first().equals(limits.second()))
//                return new String [] { limits.first().toString(), };
//              else
//                return new String [] { limits.first().toString(),
//                                       limits.second().toString(), };
//            }
//        },
//         new Command(new Object []
//           {
//             //CMD_LABEL,
//             //CMD_NAME,
//          //new BaseVariableCommand(PropertyKey.getKey("short description")),
//             //new BaseVariableCommand(PropertyKey.getKey("world")),
//           }),
//         "1:L(icon);1:L(name)[Name];5:L(description)[Description];"
//         + "1:L(world)[World]", null));

    //......................................................................
    //----- acs ------------------------------------------------------------

//     s_indices.add
//       (new IndexInfo
//        (Config.get("resource:html/title.armor.classes",
//                    "Armor Classes"),
//         Config.get("resource:html/dir.armor.classes",
//                    "acs"), "Monster", true,
//         new Filter<AbstractEntry>()
//        {
//          public boolean accept(AbstractEntry inEntry)
//            {
//              return inEntry instanceof Monster;
//            }
//        },
//         new Identificator<ValueGroup>()
//        {
//          public String []id(ValueGroup inEntry)
//            {
//              Pair<Integer, Integer> ac =
//                ((Monster)inEntry).m_ac.getLimits();

//              if(ac.first().equals(ac.second()))
//                return new String [] { ac.first().toString(), };
//              else
//                return new String [] { ac.first().toString(),
//                                       ac.second().toString(), };
//            }
//        },
//         new Command(new Object []
//           {
//             //CMD_LABEL,
//             //CMD_NAME,
//          //new BaseVariableCommand(PropertyKey.getKey("short description")),
//             //new BaseVariableCommand(PropertyKey.getKey("world")),
//           }),
//         "1:L(icon);1:L(name)[Name];5:L(description)[Description];"
//         + "1:L(world)[World]", null));

    //......................................................................
    //----- touch acs ------------------------------------------------------

//     s_indices.add
//       (new IndexInfo
//        (Config.get("resource:html/title.armor.classes.touch",
//                    "Touch ACs"),
//         Config.get("resource:html/dir.armor.classes.touch",
//                    "touchacs"), "Monster", true,
//         new Filter<AbstractEntry>()
//        {
//          public boolean accept(AbstractEntry inEntry)
//            {
//              return inEntry instanceof Monster;
//            }
//        },
//         new Identificator<ValueGroup>()
//        {
//          public String []id(ValueGroup inEntry)
//            {
//              Pair<Integer, Integer> ac =
//                ((Monster)inEntry).m_acTouch.getLimits();

//              if(ac.first().equals(ac.second()))
//                return new String [] { ac.first().toString(), };
//              else
//                return new String [] { ac.first().toString(),
//                                       ac.second().toString(), };
//            }
//        },
//         new Command(new Object []
//           {
//             //CMD_LABEL,
//             //CMD_NAME,
//          //new BaseVariableCommand(PropertyKey.getKey("short description")),
//             //new BaseVariableCommand(PropertyKey.getKey("world")),
//           }),
//         "1:L(icon);1:L(name)[Name];5:L(description)[Description];"
//         + "1:L(world)[World]", null));

    //......................................................................
    //----- flat footed acs ------------------------------------------------

//     s_indices.add
//       (new IndexInfo
//        (Config.get("resource:html/title.armor.classes.flat.footed",
//                    "Flat Footed ACs"),
//         Config.get("resource:html/dir.armor.classes.flat.footed",
//                    "flatfootedacs"), "Monster", true,
//         new Filter<AbstractEntry>()
//        {
//          public boolean accept(AbstractEntry inEntry)
//            {
//              return inEntry instanceof Monster;
//            }
//        },
//         new Identificator<ValueGroup>()
//        {
//          public String []id(ValueGroup inEntry)
//            {
//              Pair<Integer, Integer> ac =
//                ((Monster)inEntry).m_acFlatFooted.getLimits();

//              if(ac.first().equals(ac.second()))
//                return new String [] { ac.first().toString(), };
//              else
//                return new String [] { ac.first().toString(),
//                                       ac.second().toString(), };
//            }
//        },
//         new Command(new Object []
//           {
//             //CMD_LABEL,
//             //CMD_NAME,
//          //new BaseVariableCommand(PropertyKey.getKey("short description")),
//             //new BaseVariableCommand(PropertyKey.getKey("world")),
//           }),
//         "1:L(icon);1:L(name)[Name];5:L(description)[Description];"
//         + "1:L(world)[World]", null));

    //......................................................................
    //----- grapple --------------------------------------------------------

//     s_indices.add
//       (new IndexInfo
//        (Config.get("resource:html/title.grapples",
//                    "Grapples"),
//         Config.get("resource:html/dir.grapples",
//                    "grapples"), "Monster", true,
//         new Filter<AbstractEntry>()
//        {
//          public boolean accept(AbstractEntry inEntry)
//            {
//              return inEntry instanceof Monster;
//            }
//        },
//         new Identificator<ValueGroup>()
//        {
//          public String []id(ValueGroup inEntry)
//            {
//              Pair<Integer, Integer>limits =
//                ((Monster)inEntry).m_grapple.getLimits();

//              if(limits.first().equals(limits.second()))
//                return new String [] { limits.first().toString(), };
//              else
//                return new String [] { limits.first().toString(),
//                                       limits.second().toString(), };
//            }
//        },
//         new Command(new Object []
//           {
//             //CMD_LABEL,
//             //CMD_NAME,
//          //new BaseVariableCommand(PropertyKey.getKey("short description")),
//             //new BaseVariableCommand(PropertyKey.getKey("world")),
//           }),
//         "1:L(icon);1:L(name)[Name];5:L(description)[Description];"
//         + "1:L(world)[World]", null));

    //......................................................................
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------- abilityModifier ----------------------------

  /**
   * Compute the ability modifier for the given score.
   *
   * @param       inScore the ability score to compute for
   *
   * @return      the compute modifier
   *
   */
  public int abilityModifier(int inScore)
  {
    return (inScore / 2) - 5;
  }

  //........................................................................
  //--------------------------- getConstitution ----------------------------

  /**
   * Get the constitution score of the monster.
   *
   * @return      the constitution score
   *
   */
  public int getCombinedConstitution()
  {
    return (int)new Combination<Number>(this, "constitution").max().get();
  }

  //........................................................................
  //------------------------------- ability --------------------------------

  /**
   * Get an ability score of the monster.
   *
   * @param       inAbility the ability score to get
   *
   * @return      the ability score
   *
   */
  public ModifiedNumber ability(BaseMonster.Ability inAbility)
  {
    Combination<Number> ability =
      new Combination<Number>(this, inAbility.toString().toLowerCase());

    Map<String, Modifier> modifiers =
      collectModifiers(inAbility.toString().toLowerCase());
    ModifiedNumber modified = new ModifiedNumber(ability.max().get());
    for(Map.Entry<String, Modifier> entry : modifiers.entrySet())
      modified.withModifier(entry.getValue(), entry.getKey());

    return modified;
  }

  //........................................................................
  //---------------------------- getInitiative -----------------------------

  /**
   * Get the initiative of the monster.
   *
   * @return      the initiative as a combination
   *
   */
  public ModifiedNumber getInitiative()
  {
    ModifiedNumber initiative =
      new ModifiedNumber(collectContributions("initiative"));

    ModifiedNumber dexterity = ability(BaseMonster.Ability.DEXTERITY);
    if(dexterity.hasConditions())
      throw new UnsupportedOperationException("cannot handle conditional "
                                              + "dexterity for initiative");

    initiative.withModifier
      (new Modifier(abilityModifier((int)dexterity.getMaxValue()),
                    Modifier.Type.ABILITY),
       "Dex of " + dexterity);

    return initiative;
  }

  //........................................................................
  //--------------------------- armorClassTouch ----------------------------

  /**
   * Get the armor class for touch attacks of the monster.
   *
   * @return      the armor class
   *
   */
  public ModifiedNumber armorClassTouch()
  {
    return armorClass().ignore(Modifier.Type.ARMOR,
                             Modifier.Type.NATURAL_ARMOR,
                             Modifier.Type.SHIELD,
                             Modifier.Type.ENHANCEMENT);
  }

  //........................................................................
  //----------------------------- armorClass -------------------------------

  /**
   * Get the armor class of the monster.
   *
   * @return      the armor class
   *
   */
  public ModifiedNumber armorClass()
  {
    ModifiedNumber armor =
      new ModifiedNumber(10, collectContributions("armor class"));

    Combination<Number> natural =
      new Combination<Number>(this, "natural armor");
    armor.withModifier
      (new Modifier((int)natural.total().get(), Modifier.Type.NATURAL_ARMOR),
       "natural armor");

    return armor;
  }

  //........................................................................
  //------------------------ armorClassFlatFooted --------------------------

  /**
   * Get the armor class of the monster when its flat-footed.
   *
   * @return      the armor class when flat-footed
   *
   */
  public ModifiedNumber armorClassFlatFooted()
  {
    ModifiedNumber armor = armorClass();

    if(hasQuality("uncanny dodge"))
      return armor;

    return armor.ignore(Modifier.Type.ABILITY);
  }

  //........................................................................
  //------------------------ dexterityModifierForAC ------------------------

  /**
   * Get the current modifier from dexterity. This is capped by the armor's
   * maximum dexterity.
   *
   * @return      the current dexterity modifier
   *
   */
  public int dexterityModifierForAC()
  {
    ModifiedNumber dex = ability(BaseMonster.Ability.DEXTERITY);

    int max = abilityModifier((int)dex.getMaxValue());

    // TODO: we should actually only consider worn items.
    for(Name name : m_possessions)
    {
      Item item = getCampaign().getItem(name.get());
      if(item == null)
        continue;

      Number maxDex = new Combination<Number>(item, "max dexterity").min();
      if(maxDex != null && maxDex.isDefined() && maxDex.get() < max)
        max = (int)maxDex.get();
    }

    if(dex.hasConditions() && abilityModifier((int)dex.getMinValue()) < max)
      throw new UnsupportedOperationException("Got a conditional value for "
                                              + "dexterity but can return only "
                                              + "a single value");

    return max;
  }

  //........................................................................
  //------------------------------- attacks --------------------------------

  /**
   * Compute the values to render for attacks.
   *
   * @return  a map with the following values:
   *   base attacks - a list with the base attack values
   *
   */
  @SuppressWarnings("unchecked")
  public Map<String, Object> attacks()
  {
    List<Long> baseAttacks = new ArrayList<Long>();
    Combination<Number> baseAttack =
      new Combination<Number>(this, "base attack");

    if(baseAttack.total() != null)
      for(long attack = baseAttack.total().get(); attack > 0; attack -= 5)
        baseAttacks.add(attack);

    boolean weaponFinesse = hasFeat("Weapon Finesse");

    List<Map<String, Object>> primaryAttacks = Lists.newArrayList();
    Combination<ValueList<Multiple>> primaries =
      new Combination<ValueList<Multiple>>(this, "primary attacks");
    if(primaries.total() != null && primaries.total().isDefined())
      for(Multiple attack : primaries.total())
      {
        if(((EnumSelection<BaseMonster.AttackMode>)attack.get(1)).getSelected()
           == BaseMonster.AttackMode.WEAPON)
          continue;

        boolean melee =
          ((EnumSelection<BaseMonster.AttackStyle>)
           attack.get(2)).getSelected() == BaseMonster.AttackStyle.MELEE;

        BaseMonster.Ability keyAbility;
        if(weaponFinesse || !melee)
          keyAbility = BaseMonster.Ability.DEXTERITY;
        else
          keyAbility = BaseMonster.Ability.STRENGTH;

        List<ModifiedNumber> attacks = Lists.newArrayList();
        attacks.add(naturalAttack(keyAbility, baseAttacks.get(0), false));

        Map<String, Object> primary = Maps.newHashMap();
        primary.put("attacks", attacks);
        primary.put("number", ((Number)attack.get(0)).get());
        primary.put("mode", attack.get(1));
        primary.put("style", attack.get(2));
        primary.put("damage",
                    adjustDamageForStrength((Damage)attack.get(3), melee));
        primary.put("critical", critical(null));

        primaryAttacks.add(primary);
      }

    List<Map<String, Object>> secondaryAttacks = Lists.newArrayList();
    Combination<ValueList<Multiple>> secondaries =
      new Combination<ValueList<Multiple>>(this, "secondary attacks");
    if(secondaries.total() != null && secondaries.total().isDefined())
      for(Multiple attack : secondaries.total())
      {
        if(((EnumSelection<BaseMonster.AttackMode>)attack.get(1)).getSelected()
           == BaseMonster.AttackMode.WEAPON)
          continue;

        boolean melee =
          ((EnumSelection<BaseMonster.AttackStyle>)
           attack.get(2)).getSelected() == BaseMonster.AttackStyle.MELEE;

        BaseMonster.Ability keyAbility;
        if(weaponFinesse || !melee)
          keyAbility = BaseMonster.Ability.DEXTERITY;
        else
          keyAbility = BaseMonster.Ability.STRENGTH;

        List<ModifiedNumber> attacks = Lists.newArrayList();
        attacks.add(naturalAttack(keyAbility, baseAttacks.get(0) - 5, false));

        Map<String, Object> secondary = Maps.newHashMap();
        secondary.put("attacks", attacks);
        secondary.put("number", ((Number)attack.get(0)).get());
        secondary.put("mode", attack.get(1));
        secondary.put("style", attack.get(2));
        secondary.put("damage",
                      adjustDamageForStrength((Damage)attack.get(3), melee));
        secondary.put("critical", critical(null));

        secondaryAttacks.add(secondary);
      }

    List<Map<String, Object>> weaponAttacks =
      new ArrayList<Map<String, Object>>();
    for(Name name : m_possessions)
    {
      Item item = getCampaign().getItem(name.get());
      if(item == null)
        continue;

      if(!item.hasExtension(Weapon.class))
        continue;

      Map<String, Object> weapon = new HashMap<String, Object>();
      Number maxAttacksValue =
        new Combination<Number>(item, "max attacks").min();

      long maxAttacks = Long.MAX_VALUE;
      if(maxAttacksValue != null && maxAttacksValue.isDefined())
        maxAttacks = maxAttacksValue.get();

      List<ModifiedNumber> attacks = new ArrayList<ModifiedNumber>();
      for(int i = 0; i < maxAttacks && i < baseAttacks.size(); i++)
        attacks.add(weaponAttack(item, baseAttacks.get(i)));

      weapon.put("attacks", attacks);
      weapon.put("style",
                 new Combination<EnumSelection<BaseWeapon.Style>>
                 (item, "weapon style").total());
      weapon.put("name", item.getDMName());
      weapon.put("damage", new Combination<Multiple>(item, "damage"));
      weapon.put("critical", critical(item));

      weaponAttacks.add(weapon);
    }

    return new ImmutableMap.Builder<String, Object>()
      .put("base", baseAttacks)
      .put("bases", baseAttack.valuesPerGroup())
      .put("primary", primaryAttacks)
      .put("secondary", secondaryAttacks)
      .put("weapons", weaponAttacks)
      .put("grapple", grapple())
      .build();
  }

  //........................................................................
  //------------------------------- grapple --------------------------------

  /**
   * Compute the grapple check.
   *
   * @return      the modified number with the grapple check.
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull ModifiedNumber grapple()
  {
    long baseAttack =
      new Combination<Number>(this, "base attack").total().get();

    ModifiedNumber grapple = new ModifiedNumber(baseAttack);
    ModifiedNumber strength = ability(BaseMonster.Ability.STRENGTH);
    grapple.withModifier
      (new Modifier(abilityModifier((int)strength.getMaxValue()),
                    Modifier.Type.ABILITY), "Str of " + strength);

    Multimap<Multiple, String> sizesPerGroup =
      new Combination<Multiple>(this, "size").valuesPerGroup();
    BaseItem.Size size = BaseItem.Size.MEDIUM;
    String group = "default";
    for(Multiple multiple : sizesPerGroup.keySet())
      if(((EnumSelection<BaseItem.Size>)multiple.get(0)).getSelected()
         .isBigger(size))
      {
        size = ((EnumSelection<BaseItem.Size>)multiple.get(0)).getSelected();
        group = Strings.COMMA_JOINER.join(sizesPerGroup.get(multiple));
      }

    grapple.withModifier(new Modifier(size.grapple()), size.toString());

    return grapple;
  }

  //........................................................................
  //----------------------------- weaponAttack -----------------------------

  /**
   * Compute the attack bonus with the given weapon.
   * TODO: implement this for nonweapons too (improvised weapons).
   *
   * @param       inItem       the weapon to attack with
   * @param       inBaseAttack the base attack bonus
   *
   * @return      the attack bonus for the first attack
   *
   */
  public @Nonnull ModifiedNumber weaponAttack(@Nonnull Item inItem,
                                              long inBaseAttack)
  {
    // Strength bonus (or dexterity)
    BaseMonster.Ability keyAbility = BaseMonster.Ability.STRENGTH;

    if(!new Combination<EnumSelection<BaseWeapon.Style>>
       (inItem, "weapon style").total().getSelected().isMelee())
      keyAbility = BaseMonster.Ability.DEXTERITY;
    else
      // Somehow handle this in the feat!
      for(Pair<Reference, List<String>> feat : allFeats())
      {
        Name name = (Name)feat.first().getParameters().getValue("Name");
        if(name != null)
          if("weapon finesse".equalsIgnoreCase(feat.first().getName())
             && feat.first().getParameters() != null)
          {
            for(BaseEntry base : inItem.getBaseEntries())
              if(base.getName().equalsIgnoreCase(name.get()))
              {
                keyAbility = BaseMonster.Ability.DEXTERITY;
                break;
              }
          }
      }

    ModifiedNumber modified = naturalAttack(keyAbility, inBaseAttack, true);

    for(Pair<Reference, List<String>> feat : allFeats())
    {
      Name name = (Name)feat.first().getParameters().getValue("Name");
      if(name != null)
        if("weapon focus".equalsIgnoreCase(feat.first().getName())
           && feat.first().getParameters() != null)
        {
          for(BaseEntry base : inItem.getBaseEntries())
            if(base.getName().equalsIgnoreCase(name.get()))
              modified.withModifier(new Modifier(1, Modifier.Type.GENERAL),
                                    "Weapn Focus");
        }
    }

    // Magic weapon bonuses
    Map<String, Modifier> modifiers = inItem.collectModifiers("attack");
    for(Map.Entry<String, Modifier> entry : modifiers.entrySet())
      modified.withModifier(entry.getValue(), entry.getKey());

    return modified;
  }

  //........................................................................
  //----------------------------- naturalAttack ----------------------------

  /**
   * Compute the attack bonus with the given weapon.
   * TODO: implement this for nonweapons too (improvised weapons).
   *
   * @param       inBaseAttack the base attack bonus
   *
   * @return      the attack bonus for the first attack
   *
   */
  public @Nonnull ModifiedNumber naturalAttack
    (BaseMonster.Ability keyAbility, long inBaseAttack, boolean withWeapon)
  {
    ModifiedNumber modified = new ModifiedNumber(inBaseAttack);

    ModifiedNumber strength = ability(keyAbility);

    int abilityModifier;
    if (keyAbility == BaseMonster.Ability.STRENGTH)
      abilityModifier = abilityModifier((int)strength.getMaxValue());
    else
      abilityModifier = dexterityModifierForAC();

    modified.withModifier
      (new Modifier(abilityModifier, Modifier.Type.ABILITY),
       keyAbility.getShort() + " of " + strength);

    if(!withWeapon && hasFeat("weapon focus"))
      modified.withModifier(new Modifier(+1, Modifier.Type.GENERAL),
                            "Weapon Focus");

    BaseItem.Size size = getSize();
    if(size != BaseItem.Size.MEDIUM)
      modified.withModifier(new Modifier(size.modifier()), "size");

    return modified;
  }

  //........................................................................
  //-------------------------------- feats ---------------------------------

  /**
   * Get all the feats the monster has.
   *
   * @return      a map of feats to the names they were found in
   *
   */
  public List<Pair<Reference, List<String>>> allFeats()
  {
    Multimap<Reference, String> feats = HashMultimap.create();

    for(Reference feat : m_feats)
      feats.put(feat, getName());

    for(BaseEntry base : getBaseEntries())
      if(base instanceof BaseMonster)
        ((BaseMonster)base).collectFeats(feats);

    // we have to convert to a real map manuall, as soy can't handle the
    // multimap to map conversion
    List<Pair<Reference, List<String>>> result = Lists.newArrayList();
    for(Reference ref : feats.keySet())
    {
      List<String> names = Lists.newArrayList();
      for(String name : feats.get(ref))
        names.add(name);

      result.add(new Pair<Reference, List<String>>(ref, names));
    }

    return result;
  }

  //........................................................................
  //------------------------ specialAttackSummaries ------------------------

  /**
   * Get the summaries for special attacks.
   *
   * @return      a map with the value for the summary
   *
   */
  @SuppressWarnings("unchecked") // casting reference
  public @Nonnull List<Map<String, Object>> specialAttackSummaries()
  {
    Combination<ValueList<Multiple>> attacks =
      new Combination<ValueList<Multiple>>(this, "special attacks");

    Map<String, Reference<BaseQuality>> references = Maps.newHashMap();
    Map<String, Long> numbers = Maps.newHashMap();
    for(Multiple attack : attacks.total())
    {
      Reference<BaseQuality> ref = (Reference)attack.get(0);
      String name = ref.getFullName();
      Number number = (Number)attack.get(1);
      Reference<BaseQuality> existing = references.get(name);
      if(existing == null)
      {
        references.put(name, ref);
        numbers.put(name, number.isDefined() ? number.get() : -1);
      }
      else
      {
        references.put(name, existing.add(ref));
        if(number.isDefined())
          if(numbers.get(name) > 0)
            numbers.put(name, numbers.get(name) + number.get());
          else
            numbers.put(name, number.get());
      }
    }

    List<Map<String, Object>> summaries = new ArrayList<Map<String, Object>>();
    for(String key : references.keySet())
    {
      summaries.add
        (new ImmutableMap.Builder<String, Object>()
         .put("name", references.get(key).getName())
         .put("params", references.get(key).getParameters().getSummary())
         .put("number", numbers.get(key))
         .put("summary", references.get(key).summary
              (ImmutableMap.of("level", "" + getLevel(),
                               "class", "" + getSpellClass(),
                               "ability", "" + getSpellAbilityModifier())))
         .build());
    }

    return summaries;
  }

  //........................................................................
  //------------------------ specialQualitySummaries ------------------------

  /**
   * Get the summaries for special qualities.
   *
   * @return      a map with the value for the summary
   *
   */
  @SuppressWarnings("unchecked") // casting reference
  public @Nonnull List<Map<String, Object>> specialQualitySummaries()
  {
    Combination<ValueList<Multiple>> qualities =
      new Combination<ValueList<Multiple>>(this, "special qualities");

    List<Map<String, Object>> summaries = new ArrayList<Map<String, Object>>();
    for(Multiple quality : qualities.total())
    {
      summaries.add(new ImmutableMap.Builder<String, Object>()
                    .put("name", ((Reference)quality.get(0)).getName())
                    .put("params",
                         ((Reference)quality.get(0)).getParameters()
                         .getSummary())
                    .put("condition",
                         quality.get(1).isDefined() ? quality.get(1) : "")
                    .put("number", ((Number)quality.get(2)).isDefined()
                         ? ((Number)quality.get(2)).get() : -1)
                    .put("summary",
                         ((Reference<BaseQuality>)quality.get(0))
                         .summary(ImmutableMap.of
                                  ("level", "" + getLevel(),
                                   "class", "" + getSpellClass(),
                                   "ability", "" + getSpellAbilityModifier())))
                    .build());
    }

    return summaries;
  }

  //........................................................................
  //------------------------------- allSkills -------------------------------

  /**
   * Get information about the current skills of the monster. Skills that can
   * only be used trained for which the monster has no ranks are not returned.
   *
   * @return      the skills information
   *
   */
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> allSkills()
  {
    List<Map<String, Object>> skills = Lists.newArrayList();
    Map<String, ModifiedNumber> ranks = skillRanks();
    List<Reference<BaseQuality>> qualities = collectSpecialQualities();

    for(BaseSkill skill :
          DMADataFactory.get().getEntries(BaseSkill.TYPE, null, 0, 1000))
    {
      ModifiedNumber modifier = ranks.get(skill.getName());
      if((modifier == null || modifier.getMaxValue() == 0)
         && !skill.isUntrained())
        continue;

      if(modifier == null)
        modifier = new ModifiedNumber(0, true);

      // Ability modifiers
      BaseMonster.Ability ability = skill.getAbility();
      if(ability != null)
        if(ability == BaseMonster.Ability.DEXTERITY)
          modifier.withModifier
            (new Modifier(dexterityModifierForAC()), "Dexterity");
        else
          modifier.withModifier
            (new Modifier(abilityModifier((int)ability(ability).getMinValue()),
                          Modifier.Type.ABILITY),
             skill.getAbility().toString());

      // Skill penalty from armor
      // TODO: must be implemented

      // Skill modifiers from items (and other modifiers)
      for (Map.Entry<String, Modifier> entry
             : collectModifiers(skill.getName()).entrySet())
        modifier.withModifier(entry.getValue(), entry.getKey());

      // Skill modifiers from special qualities
      for(Reference<BaseQuality> reference : qualities)
      {
        BaseQuality quality = reference.getEntry();
        if(quality == null)
          continue;

        Modifier qualityModifier = quality.computeSkillModifier
          (skill.getName(), reference.getParameters());
        if(qualityModifier != null)
          modifier.withModifier(qualityModifier, quality.getName() + " "
                                + reference.getParameters().getSummary());
      }

      for(Contribution<? extends Value> contribution
            : collectContributions(skill.getName()))
        modifier.withModifier((Modifier)contribution.getValue(),
                              contribution.getDescription());

      Map<String, Object> values = Maps.newHashMap();

      values.put("entry", skill);
      values.put("modifier", modifier);

      skills.add(values);
    }

    return skills;
  }

  //........................................................................
  //----------------------- collectSpecialQualities ------------------------

  /**
   * Get the special qualities for this and all base monsters.
   *
   * @return  a list of base qualities
   *
   */
  public @Nonnull List<Reference<BaseQuality>> collectSpecialQualities()
  {
    List<Reference<BaseQuality>> qualities = Lists.newArrayList();

    for(BaseEntry base : getBaseEntries())
    {
      if(!(base instanceof BaseMonster))
        continue;

      qualities.addAll(((BaseMonster)base).collectSpecialQualities());
    }

    return qualities;
  }

  //........................................................................
  //--------------------------- getFortitudeSave ----------------------------

  /**
   * Get the current fortitude save.
   *
   * @return      the modifier for the save
   *
   */
  public ModifiedNumber getFortitudeSave()
  {
    return new ModifiedNumber(collectContributions("fortitude save"));
  }

  //........................................................................
  //----------------------------- getReflexSave -----------------------------

  /**
   * Get the current reflex save.
   *
   * @return      the modifier for the save
   *
   */
  public ModifiedNumber getReflexSave()
  {
    return new ModifiedNumber(collectContributions("reflex save"));
  }

  //........................................................................
  //------------------------------ getWillSave ------------------------------

  /**
   * Get the current will save.
   *
   * @return      the modifier for the save
   *
   */
  public ModifiedNumber getWillSave()
  {
    return new ModifiedNumber(collectContributions("will save"));
  }

  //........................................................................
  //--------------------------- addContributions ---------------------------

  /**
   * Add contributions for this entry to the given list.
   *
   * @param       inName          the name of the value to contribute to
   * @param       ioContributions the list of contributions to add to
   *
   */
  @SuppressWarnings("unchecked")
  @Override
  public void addContributions
    (@Nonnull String inName,
     @Nonnull List<Contribution<? extends Value>> ioContributions)
  {
    super.addContributions(inName, ioContributions);

    BaseMonster.Ability saveAbility = null;
    if("fortitude save".equals(inName))
      saveAbility = BaseMonster.Ability.CONSTITUTION;
    else if("reflex save".equals(inName))
      saveAbility = BaseMonster.Ability.DEXTERITY;
    else if("will save".equals(inName))
     saveAbility = BaseMonster.Ability.WISDOM;

    if(saveAbility != null)
    {
      ModifiedNumber ability = ability(saveAbility);
      if(ability.hasConditions())
        throw new UnsupportedOperationException
          ("cannot handle conditional values for abiliies for saves");

      ioContributions.add
        (new Contribution<Number>
         (new Number(abilityModifier((int)ability.getMaxValue()),
                     -100, 100),
          this, saveAbility.getShort() + " of " + ability.getMaxValue()));
    }

    for(Name name : m_possessions)
    {
      Item item = getCampaign().getItem(name.get());
      if(item == null)
        continue;

      item.addContributions(inName, ioContributions);
    }

    if("armor class".equals(inName))
    {
      // limit this with max dex of items
      ModifiedNumber dexterity = ability(BaseMonster.Ability.DEXTERITY);
      int modifier = dexterityModifierForAC();

      ioContributions.add
        (new Contribution<Modifier>
         (new Modifier(modifier, Modifier.Type.ABILITY),
          this, "Dex of " + dexterity
          + (abilityModifier((int)dexterity.getMaxValue()) == modifier
             ? "" : " (capped for armor)")));

      Multiple minSize = new Combination<Multiple>(this, "size").min();
      if(minSize != null && minSize.isDefined())
      {
        BaseItem.Size size =
          ((EnumSelection<BaseItem.Size>)minSize.get(0)).getSelected();

        if(size.modifier() != 0)
          ioContributions.add
            (new Contribution<Modifier>(new Modifier(size.modifier(),
                                                     Modifier.Type.GENERAL),
                                        this, "size"));

      }
    }
  }

  //........................................................................
  //----------------------- adjustDamageForStrength ------------------------

  /**
   *
   *
   * @param
   *
   * @return
   *
   */
  public @Nonnull Damage adjustDamageForStrength(@Nonnull Damage damage,
                                                 boolean isMelee)
  {
    if(!isMelee)
      return damage;

    int modifier =
      abilityModifier((int)ability(BaseMonster.Ability.STRENGTH).getMaxValue());

    return damage.add(new Damage(new Dice(0, 1, modifier)));
  }

  //........................................................................
  //------------------------------- critical -------------------------------

  /**
   *
   *
   * @param
   *
   * @return
   *
   */
  public @Nullable Critical critical(@Nullable Item item)
  {
    boolean improvedCritical = hasFeat("improved critical");
    if(item == null)
      if(improvedCritical)
        return new Critical(19, 20, 2);
      else
        return null;

    improvedCritical = hasFeat("improved critical [name " + item.getName() + "]");
    Critical critical = new Combination<Critical>(item, "critical").total();
    if(critical == null)
      if(improvedCritical)
        return new Critical(19, 20, 2);
      else
        return null;

    return critical.doubled();
  }

  //........................................................................

  //------------------------------- getLevel -------------------------------

  /**
   * Get the level of the monster.
   *
   * @return      the monster's level
   *
   */
  public int getLevel()
  {
    return new Combination<Dice>(this, "hit dice").total().getNumber();
  }

  //........................................................................
  //---------------------------- getSpellClass -----------------------------

  /**
   * Get the spellcasting class.
   *
   * @return      the spellcasting class
   *
   */
  public @Nonnull BaseSpell.SpellClass getSpellClass()
  {
    return BaseSpell.SpellClass.SORCERER;
  }

  //........................................................................
  //----------------------- getSpellAbilityModifier ------------------------

  /**
   * the level of the spell, which has to be added separately.
   *
   * @return      the modifier
   *
   */
  public int getSpellAbilityModifier()
  {
    BaseMonster.Ability ability;
    switch(getSpellClass())
    {
      case WIZARD:
        ability = BaseMonster.Ability.INTELLIGENCE;
        break;

      case CLERIC:
      case PALADIN:
      case RANGER:
      case DRUID:
        ability = BaseMonster.Ability.WISDOM;
        break;

      case SORCERER:
      case BARD:
      default:
        ability = BaseMonster.Ability.CHARISMA;
    }

    return abilityModifier((int)ability(ability).getMaxValue());
  }

  //........................................................................

  //------------------------------ hasQuality ------------------------------

  /**
   *
   *
   * @param
   *
   * @return
   *
   */
  public boolean hasQuality(@Nonnull String inName)
  {
    for(BaseEntry base : getBaseEntries())
      if(base instanceof BaseMonster)
        if(((BaseMonster)base).hasQuality(inName))
          return true;

    return false;
  }

  //........................................................................
  //------------------------------- hasFeat --------------------------------

  /**
   *
   *
   * @param
   *
   * @return
   *
   */
  public boolean hasFeat(@Nonnull String inName)
  {
    for(BaseEntry base : getBaseEntries())
      if(base instanceof BaseMonster)
        if(((BaseMonster)base).hasFeat(inName))
          return true;

    return false;
  }

  //........................................................................

  //------------------------------- badSave --------------------------------

  /**
   * Compute the base saving throw for a bad save.
   *
   * @param       inLevel the level or hit dice to compute for
   *
   * @return      the base value for a save
   *
   */
  public static int badSave(int inLevel)
  {
    return inLevel / 3;
  }

  //........................................................................
  //------------------------------- goodSave -------------------------------

  /**
   *
   * Compute the base saving throw for a good save.
   *
   * @param       inLevel the level or hit dice to compute for
   *
   * @return      the base value for a save
   *
   */
  public static int goodSave(int inLevel)
  {
    return inLevel / 2 + 2;
  }

  //........................................................................
  //----------------------------- skillRanks -------------------------------

  /**
   * Get the number of skill ranks for all skills with ranks.
   *
   * @return      the number of skill ranks per skill name
   *
   */
  public Map<String, ModifiedNumber> skillRanks()
  {
    Combination<ValueList<Multiple>> skills =
      new Combination<ValueList<Multiple>>(this, "class skills");

    Map<String, ModifiedNumber> ranks = Maps.newHashMap();

    for(Map.Entry<ValueList<Multiple>, String> entry :
          skills.valuesPerGroup().entries())
    {
      String group = entry.getValue();
      for(Multiple skill : entry.getKey())
      {
        String name = skill.get(0).toString();
        ModifiedNumber number = ranks.get(name);
        if(number == null)
          number = new ModifiedNumber(0, true);

        number.withModifier(new Modifier((int)((Number)skill.get(1)).get()),
                            group);

        ranks.put(name, number);
      }
    }

    for(Multiple skill : m_skills)
    {
      String name = skill.get(0).toString();
      ModifiedNumber number = ranks.get(name);
      if(number == null)
        number = new ModifiedNumber(0, true);

      number.withModifier(new Modifier((int)((Number)skill.get(1)).get()),
                          this.getName());
      ranks.put(name, number);
    }

    return ranks;
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

//     if(m_base != null)
//     {
//       // check armor max dexterity
//       int max = m_base.getAbilityModifier(inAbility);

//       if(inAbility == Global.Ability.DEXTERITY)
//       {
//         for(EntryValue<Item> value : m_possessions)
//         {
//           Item armor = value.get();

//           if(!armor.hasAttachment(Armor.class))
//             continue;

//           max =
//             Math.min(max,
//                      (int)((Number)armor
//                            .getValue("max dexterity")).get());
//         }
//       }

//       return max;
//     }

  //   return 0;
  // }

  //........................................................................
  //------------------------------- getSpeed -------------------------------

  /**
   * Get the land speed of the monster.
   *
   * @return      the speed in feet
   *
   */
  // public int getSpeed()
  // {
//     if(m_base == null)
//       return 0;

//     for(Multiple mult : m_base.m_speed)
//       if(!mult.get(0).get().isDefined())
//         // we have the land speed
//         return (int)((Distance)mult.get(1).get()).getAsFeet().getValue();

  //   return 0;
  // }

  //........................................................................
  //-------------------------------- getSize -------------------------------

  /**
   * Get the size of the monster.
   *
   * @return      the index in the size table.
   *
   */
  @SuppressWarnings("unchecked")
  public BaseItem.Size getSize()
  {
    Multiple size = new Combination<Multiple>(this, "size").min();
    if(size == null)
      return BaseItem.Size.MEDIUM;

    return ((EnumSelection<BaseItem.Size>)size.get(0)).getSelected();
  }

  //........................................................................
  //-------------------------------- dmName --------------------------------

  /**
   * Get the name a dm may see for this entry
   *
   * @return      the name
   *
   */
  public @Nonnull String dmName()
  {
    List<String> parts = new ArrayList<String>();

    for(BaseEntry base : getBaseEntries())
    {
      if(base == null)
        continue;

      String name = base.getName();
      List<String> synonyms = base.getSynonyms();
      // if the first synonym does not contain a ',', we use that name as it
      // might be better readable than the restricted real name
      if(!synonyms.isEmpty() && synonyms.get(0).indexOf(',') < 0)
        name = synonyms.get(0);

      parts.add(name);
    }

    if(parts.isEmpty())
      parts.add(getName());

    String name = Strings.COMMA_JOINER.join(parts);
    if(m_givenName.isDefined())
      name = m_givenName.get() + " - " + name;

    return name;
  }

  //........................................................................

  //------------------------- getArmorCheckPenalty -------------------------

  /**
   * Get the skill check penalty for armor this monster is wearing.
   *
   * @return      the penalty, or 0 for none
   *
   */
  // public int getArmorCheckPenalty()
  // {
  //   int penalty = 0;

  //   // add up all the armor check penalties
  //   for(EntryValue<Item> value : m_possessions)
  //   {
  //     Item armor = value.get();

  //     if(!armor.hasAttachment(Armor.class))
  //       continue;

  //     penalty +=
  //       (int)((Number)armor.getValue("check penalty")).get();
  //   }

  //   return penalty;
  // }

  //........................................................................

  //------------------------- getEntrySubEntries ---------------------------

  /**
   * Get the sub entries that are part of this entry only (without
   * attachments).
   *
   * @return      an Iterator with all the sub entries
   *
   */
  // public List<Entry> getEntrySubEntries()
  // {
  //   List<Entry> list = new ArrayList<Entry>();

  //   for(EntryValue<Item> value : m_possessions)
  //     list.add(value.get());

  //   return list;
  // }

  //........................................................................

  //----------------------------- printCommand -----------------------------

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

  //-------------------------- adjustCombination ---------------------------

  /**
   * Adjust the value for the given name for any special properites.
   *
   * @param       inName        the name of the value to adjust
   * @param       ioCombination the combinstaion to adjust
   * @param       <V>           the real type of the value combined
   *
   */
  @SuppressWarnings("unchecked")
  @Override
  public <V extends Value> void
            adjustCombination(@Nonnull String inName,
                              Combination<V> ioCombination)
  {
    if("hit dice".equals(inName))
    {
      Combination<Dice> combination = (Combination<Dice>)ioCombination;
      int constitution = getCombinedConstitution();
      if(constitution >= 0)
      {
        int level = combination.total().getNumber();
        int bonus = abilityModifier(constitution);
        combination.add(new Dice(0, 1, level * bonus),
                        "Con of " + constitution + " (" + (bonus > 0 ? "+" : "")
                        + bonus + ") and level " + level);
      }
    }

    super.adjustCombination(inName, ioCombination);
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

//     if(m_base != null)
//     {
//       //----- hit points ---------------------------------------------------

//       // check the current number of hit points if it is over the maximum
//       //Pair<Integer, Integer> limits = m_hpMod.getLimits();

//       //if(m_hp.get() > m_base.getMaxHP() + limits.second())
//       //{
//       // result = false;
//       //  addError(new CheckError("monster.hit.points",
//       //                          "the monster has more hit points than the "
//       //                          + "maximal "
//       //                          + (m_base.getMaxHP() + limits.second())));
//       //}

//       //if(m_maxHP.get() > m_base.getMaxHP() + limits.second())
//       //{
//       //  result = false;
//       //  addError(new CheckError("monster.hit.points",
//       //                         "the monster has more maximal hit points than "
//       //                          + "the maximal "
//       //                          + (m_base.getMaxHP() + limits.second())));
//       //}

//       if(m_maxHP.get() < m_base.getMinHP())
//       {
//         result = false;
//         addError(new CheckError("monster.hit.points",
//                                 "the monster has less maximal hit points than "
//                                 + "the minimal " + m_base.getMinHP()));
//       }

//       //....................................................................
//       //----- skill points -------------------------------------------------

//       // check the number of skill points defined
//       long total = 0;
//       for(EntryValue<Skill> value : m_skills)
//         total += value.get().getRanks();

//       long points = m_base.skillPoints();

//       if(total > points)
//       {
//         result = false;
//         addError(new CheckError("monster.skill.points",
//                                 "uses " + total
//                                 + " skill points, but only has " + points));
//       }
//       else
//         if(total < points)
//         {
//           result = false;
//           addError(new CheckError("monster.skill.points",
//                                   "uses only " + total
//                                   + " skill points from its " + points));
//         }

      //....................................................................
//     }

  //   return super.check() & result;
  // }

  //........................................................................
  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled. We do only
   * the value and the appearance here and let the base class handle the rest.
   *
   */
  // TODO: fix this
  // @SuppressWarnings("unchecked") // TODO: split up this method and move the tag
  // public void complete()
  // {
//     // can't complete anything if we don't have a base value
//     if(m_base == null || !(m_base instanceof BaseMonster))
//       return;

//     BaseItem.Size size = m_base.getSize();

    //----- possessions ----------------------------------------------------

    // add the standard possessions of the base monster (do it first, in case
    // it affects something else)
//     if(!m_possessions.isDefined())
//       for(Multiple mult : m_base.m_possessions)
//       {
//         Item item;
//         if(mult.get(0).get().isDefined())
//         {
//           // a simple item denoted by name
//           String name = ((Text)mult.get(0).get()).get();

//           item = new Item(name);
//         }
//         else
//         {
//           // denoted by name and others
//           String definition = ((Text)mult.get(1).get()).get();

//           // complete the item definition
//           definition = "item " + definition + ".";

//           ParseReader reader = new ParseReader(new StringReader(definition),
//                                                "possesions");

//           item = (Item)AbstractEntry.read(reader);

//           if(item == null)
//           {
//             Log.warning("invalid item in possession ignored");

//             continue;
//           }
//         }

//         m_possessions.add(new EntryValue(item));

//         // add the item to the target
//         // TODO: fix this
//         //m_file.getCampaign().add(item);
//       }
//     else
//       for(EntryValue<Item> value : m_possessions)
//       {
//         Item item = value.get();
//       }

    //......................................................................
    //----- hit points -----------------------------------------------------

    // the maximal hit points (if not yet set)
    // if(!m_maxHP.isDefined())
    //   if(!m_hp.isDefined())
    //   {
        //Pair<Integer, Integer> modifier = m_hpMod.getLimits();

        //if(modifier.first() != modifier.second())
        //  Log.warning("differing modifiers for hit points, will use lower "
        //              + "only, thus hp might be off!");

        //m_maxHP.set(m_base.rollMaxHP() + modifier.first());
      // }
      // else
      //   m_maxHP.set(m_hp.get());

    // the real current hit points (without modifiers)
    // if(!m_hp.isDefined())
    //   m_hp.set(m_maxHP.get());

    // setup the hp modifier
    //m_hpMod.addModifier(new net.ixitxachitls.dma.values.Modifier
    //                    (BaseMonster.abilityMod(m_base.m_constitution.get())
    //                     * m_base.m_hitDice.getNumber(),
    //                     net.ixitxachitls.dma.values.Modifier.Type.ABILITY,
    //                    "Con", null));

    // bonus hit points for constructs
    //if(size.isBigger(BaseItem.Size.TINY)
    //   && ((Selection)m_base.m_type.get(0).get()).getSelected() == 2)
    //  m_hpMod.addModifier(new net.ixitxachitls.dma.values.Modifier
    //    (size.construct(), null, "Construct Bonus", null));

    //......................................................................
    //----- initiative -----------------------------------------------------

    // setup the initiative modifier
    //m_initiative.addModifier(new net.ixitxachitls.dma.values.Modifier
    //                         (m_base.abilityMod(m_base.m_dexterity.get()),
    //                          net.ixitxachitls.dma.values.Modifier
    //                          .Type.ABILITY, "Dex", null));

    //......................................................................
    //----- armor class ----------------------------------------------------

    // setup the AC modifiers
    //if(size != null)
    //  m_ac.addModifier(new net.ixitxachitls.dma.values.Modifier
    //                   (size.modifier(), null, "Size", null));
    //m_ac.addModifier(new net.ixitxachitls.dma.values.Modifier
    //                 ((int)m_base.m_natural.get(),
    //                net.ixitxachitls.dma.values.Modifier.Type.NATURAL_ARMOR,
    //                  "Natural Armor",
    //                  null));
    //m_ac.addModifier(new net.ixitxachitls.dma.values.Modifier
    //                 (getAbilityModifier(Global.Ability.DEXTERITY),
    //                  net.ixitxachitls.dma.values.Modifier.Type.ABILITY,
    //                  "Dex", null));

    // setup the touch AC
    //if(size != null)
    //  m_acTouch.addModifier(new net.ixitxachitls.dma.values.Modifier
    //                        (size.modifier(),
    //                         net.ixitxachitls.dma.values.Modifier.Type.SIZE,
    //                         "Size", null));
    //m_acTouch.addModifier(new net.ixitxachitls.dma.values.Modifier
    //                      (getAbilityModifier(Global.Ability.DEXTERITY),
    //                       net.ixitxachitls.dma.values.Modifier.Type.ABILITY,
    //                       "Dex", null));

    // setup the flat footed AC
    //if(size != null)
    //  m_acFlatFooted.addModifier(new net.ixitxachitls.dma.values.Modifier
    //                             (size.modifier(),
    //                              net.ixitxachitls.dma.values.Modifier
    //                              .Type.SIZE, "Size", null));
    //m_acFlatFooted.addModifier(new net.ixitxachitls.dma.values.Modifier
    //                          ((int)m_base.m_natural.get(),
    //                            net.ixitxachitls.dma.values.Modifier
    //                            .Type.NATURAL_ARMOR, "Natural Armor", null));

    // deflection bonus for incorporeal monsters
//     if(m_base.hasSubtype("Incorporeal"))
//     {
//       //m_ac.addModifier
//       //  (new net.ixitxachitls.dma.values.Modifier
//       //   (Math.max(1, BaseMonster.abilityMod(m_base.m_charisma.get())),
//       //   net.ixitxachitls.dma.values.Modifier.Type.DEFLECTION, "Incorporeal",
//       //    null));

//       //m_acTouch.addModifier
//       //  (new net.ixitxachitls.dma.values.Modifier
//       //   (Math.max(1, BaseMonster.abilityMod(m_base.m_charisma.get())),
//       //   net.ixitxachitls.dma.values.Modifier.Type.DEFLECTION, "Incorporeal",
//       //    null));

//       //m_acFlatFooted.addModifier
//       //  (new net.ixitxachitls.dma.values.Modifier
//       //   (Math.max(1, BaseMonster.abilityMod(m_base.m_charisma.get())),
//       //   net.ixitxachitls.dma.values.Modifier.Type.DEFLECTION, "Incorporeal",
//       //    null));
//     }

    //......................................................................
    //----- grapple --------------------------------------------------------

    // the grapple bonus
    //m_grapple.setBase((int)m_base.m_attack.get());
    //if(size != null)
    //  m_grapple.addModifier(new net.ixitxachitls.dma.values.Modifier
    //                        (size.grapple(),
    //                         net.ixitxachitls.dma.values.Modifier.Type.SIZE,
    //                         "Size", null));
    //m_grapple.addModifier(new net.ixitxachitls.dma.values.Modifier
    //                      (BaseMonster.abilityMod(m_base.m_strength.get()),
    //                       net.ixitxachitls.dma.values.Modifier.Type.ABILITY,
    //                      "Str", null));

    //......................................................................
    //----- melee attacks --------------------------------------------------

    // melee attacks
    //m_attackMelee.setBase((int)m_base.m_attack.get());
    //m_attackMelee.addModifier(new net.ixitxachitls.dma.values.Modifier
    //                        (BaseMonster.abilityMod(m_base.m_strength.get()),
    //                           net.ixitxachitls.dma.values.Modifier
    //                           .Type.ABILITY, "Str", null));
    //if(size != null)
    //  m_attackMelee.addModifier(new net.ixitxachitls.dma.values.Modifier
    //                            (size.modifier(),
    //                             net.ixitxachitls.dma.values.Modifier
    //                             .Type.SIZE, "Size", null));

    // melee attacks (secondary)
    //m_attackMelee2nd.setBase((int)m_base.m_attack.get() - 5);
    //m_attackMelee2nd.addModifier
    //  (new net.ixitxachitls.dma.values.Modifier
    //   (BaseMonster.abilityMod(m_base.m_strength.get()),
    //    net.ixitxachitls.dma.values.Modifier.Type.ABILITY, "Str", null));
    //if(size != null)
    //  m_attackMelee2nd.addModifier(new net.ixitxachitls.dma.values.Modifier
    //                               (size.modifier(),
    //                                net.ixitxachitls.dma.values.Modifier
    //                                .Type.SIZE, "Size", null));

    //......................................................................
    //----- ranged attacks -------------------------------------------------

    // ranged attacks
    //m_attackRanged.setBase((int)m_base.m_attack.get());
    //m_attackRanged.addModifier(new net.ixitxachitls.dma.values.Modifier
    //                        (BaseMonster.abilityMod(m_base.m_dexterity.get()),
    //                            net.ixitxachitls.dma.values.Modifier
    //                            .Type.ABILITY, "Dex", null));

    // ranged attacks (secondary)
    //m_attackRanged2nd.setBase((int)m_base.m_attack.get() - 5);
    //m_attackRanged2nd.addModifier
    //  (new net.ixitxachitls.dma.values.Modifier
    //   (BaseMonster.abilityMod(m_base.m_dexterity.get()),
    //   net.ixitxachitls.dma.values.Modifier.Type.ABILITY, "Dex",
    //    null));

    //......................................................................
    //----- damage ---------------------------------------------------------

    // damage
    //m_damage.addModifier(new net.ixitxachitls.dma.values.Modifier
                         //(BaseMonster.abilityMod(m_base.m_strength.get()),
                          //net.ixitxachitls.dma.values.Modifier.Type.ABILITY,
                          //                      "Str", null));

    // damage (secondary)
    //m_damage2nd.addModifier(new net.ixitxachitls.dma.values.Modifier
    //                    (BaseMonster.abilityMod(m_base.m_strength.get()) / 2,
    //                       net.ixitxachitls.dma.values.Modifier.Type.ABILITY,
    //                         "Str", null));

    //......................................................................
    //----- saves ----------------------------------------------------------

//     if(m_base.m_goodSaves.isDefined())
//     {
//       // set all to bad saves, reset below
//       //m_saveFort.setBase(badSave(m_base.level()));
//       //m_saveRef.setBase(badSave(m_base.level()));
//       //m_saveWill.setBase(badSave(m_base.level()));

//       for(Iterator<Value> i = ((ValueList)m_base.m_goodSaves).iterator();
//           i.hasNext(); )
//       {
//         switch(((Selection)i.next()).getSelected())
//         {
//           // Fortitude
//           case 0:

//             //m_saveFort.setBase(goodSave(m_base.level()));

//             break;

//           // Reflex
//           case 1:

//             //m_saveRef.setBase(goodSave(m_base.level()));

//             break;

//           // Will
//           case 2:

//             //m_saveWill.setBase(goodSave(m_base.level()));

//             break;

//           default:

//             assert false : "should never happen";
//         }
//       }
//     }
//     else
      // TODO: fix this
      // the saving throws according to type
//       switch(((Selection)m_base.m_type.get(0).get()).getSelected())
//       {
//         // Aberration
//         // Undead
//         case 0:
//         case 13:

//           // Good Will save
//           //m_saveFort.setBase(badSave(m_base.level()));
//           //m_saveRef.setBase(badSave(m_base.level()));
//           //m_saveWill.setBase(goodSave(m_base.level()));

//           break;

//           // Animal
//         case 1:

//           // good fortitude and reflex saves (certain animals differ!)
//           //m_saveFort.setBase(goodSave(m_base.level()));
//           //m_saveRef.setBase(goodSave(m_base.level()));
//           //m_saveWill.setBase(goodSave(m_base.level()));

//           break;

//           // Construct
//           // Ooze
//         case 2:
//         case 10:

//           // no good saving throws
//           //m_saveFort.setBase(badSave(m_base.level()));
//           //m_saveRef.setBase(badSave(m_base.level()));
//           //m_saveWill.setBase(badSave(m_base.level()));

//           break;

//           // Dragon
//           // Outsider
//         case 3:
//         case 11:

//           // Good Fort, Ref, Will saves
//           //m_saveFort.setBase(goodSave(m_base.level()));
//           //m_saveRef.setBase(goodSave(m_base.level()));
//           //m_saveWill.setBase(goodSave(m_base.level()));

//           break;

//           // Elemental
//         case 4:

//           // Good Fort (earth, water), Good Ref (air fire)

//           // TODO: fix this
// //           if(m_base.m_type.get(1).get().isDefined())
// //             switch(((Selection)m_base.m_type.get(1).get()).getSelected())
// //             {
// //               case 1:

// //                 //m_saveFort.setBase(badSave(m_base.level()));
// //                 //m_saveRef.setBase(goodSave(m_base.level()));
// //                 //m_saveWill.setBase(badSave(m_base.level()));

// //                 break;

// //               case 7:

// //                 //m_saveFort.setBase(goodSave(m_base.level()));
// //                 //m_saveRef.setBase(badSave(m_base.level()));
// //                 //m_saveWill.setBase(badSave(m_base.level()));

// //                 break;

// //               default:

// //                 addError(new CheckError("monster.saves.elemental",
// //                                         "unknown subtype"));

// //                 break;
// //             }
// //           else
// //             addError(new CheckError("monster.saves.elemental",
// //                                     "no subtype"));

//           break;

//           // Fey
//           // Monstrous Humanoid
//         case 5:
//         case 9:

//           // Good Ref and Will saves
//           //m_saveFort.setBase(badSave(m_base.level()));
//           //m_saveRef.setBase(goodSave(m_base.level()));
//           //m_saveWill.setBase(goodSave(m_base.level()));

//           break;

//           // Giant
//           // Plant
//           // Vermin
//         case 6:
//         case 12:
//         case 14:

//           // Good Fort save
//           //m_saveFort.setBase(goodSave(m_base.level()));
//           //m_saveRef.setBase(badSave(m_base.level()));
//           //m_saveWill.setBase(badSave(m_base.level()));

//           break;

//           // Humanoid
//         case 7:

//           // Good Ref save
//           //m_saveFort.setBase(badSave(m_base.level()));
//           //m_saveRef.setBase(goodSave(m_base.level()));
//           //m_saveWill.setBase(badSave(m_base.level()));

//           break;

//           // Magical Beast
//         case 8:

//           // Good Fort, Ref saves
//           //m_saveFort.setBase(goodSave(m_base.level()));
//           //m_saveRef.setBase(goodSave(m_base.level()));
//           //m_saveWill.setBase(badSave(m_base.level()));

//           break;

//         default:

//           addError(new CheckError("monster.saves",
//                                   "unknown type encountered"));
//       }

    // set the ability modifiers
    // m_saveFort.addModifier(new net.ixitxachitls.dma.values.Modifier
//                        (BaseMonster.abilityMod(m_base.m_constitution.get()),
//                           net.ixitxachitls.dma.values.Modifier.Type.ABILITY,
//                             "Con", null));
//     m_saveRef.addModifier(new net.ixitxachitls.dma.values.Modifier
//                          (BaseMonster.abilityMod(m_base.m_dexterity.get()),
//                           net.ixitxachitls.dma.values.Modifier.Type.ABILITY,
//                             "Dex", null));
//     m_saveWill.addModifier(new net.ixitxachitls.dma.values.Modifier
//                            (BaseMonster.abilityMod(m_base.m_wisdom.get()),
//                           net.ixitxachitls.dma.values.Modifier.Type.ABILITY,
    //"Wis", null));

    //......................................................................
    //----- skills ---------------------------------------------------------

    // the skills must be done before the feats and qualities, as these could
    // adjust the skills

    // first we setup all the skills that are given from the base
//     HashSet<String> names = new HashSet<String>();

    // TODO: fix this
//     if(!m_skills.isDefined())
//       for(Multiple skill : m_base.m_classSkills)
//       {
//         // create the appropriate skills
//         String     name       = ((SimpleText)skill.get(0).get()).get();
//         Parameters parameters = (Parameters)skill.get(1).get();
//         int        ranks      = (int)((Number)skill.get(2).get()).get();

//         // TODO: change this
//         Skill newSkill =
//           new Skill(name, ranks, true, parameters, null
//                     /*m_file.getCampaign()*/);

//         names.add(newSkill.getName());

//         m_skills.add(new EntryValue(newSkill));
//       }

//     for(FilteredIterator<BaseEntry> i =
//           new FilteredIterator<BaseEntry>(BaseCampaign.GLOBAL.iterator(),
//                                           new Filter<BaseEntry>()
//             {
//               public boolean accept(BaseEntry inEntry)
//               {
//                 return inEntry instanceof BaseSkill;
//               }
//             }); i.hasNext(); )
//     {
//       BaseSkill skill = (BaseSkill)i.next();

//       if(names.contains(skill.getName()) || !skill.isUntrained())
//         continue;

//       // TODO: change this
//       m_skills.add(new EntryValue(new Skill(skill.getName(), -1, false, null,
//                                             null /*m_file.getCampaign()*/)));
//     }

    // finally, complete all the skills
    // for(EntryValue<Skill> value : m_skills)
    // {
    //   Skill skill = value.get();

    //   // complete the skill with this monster
    //   skill.complete();
    // }

    //......................................................................
    //----- special attacks ------------------------------------------------

    // setup all the qualities that are given from the base
    // TODO: fix this
//     if(!m_specialAttacks.isDefined())
//       for(Multiple quality : m_base.m_specialAttacks)
//       {
//         // create the appropriate skills
//         String name           = ((SimpleText)quality.get(0).get()).get();
//         Parameters parameters = (Parameters)quality.get(1).get();

//         Quality newQuality =
//           // TODO: change this
//           new Quality(name, parameters /*m_file.getCampaign()*/);

//         Multiple value = m_base.m_specialAttacks.createElement();

//         ((EntryValue<Quality>)value.get(0).getMutable()).set(newQuality);

//         m_specialAttacks.add(value);
//       }

    // finally, comllete all the qualities
    // for(Multiple value : m_specialAttacks)
    // {
    //   Quality quality = ((EntryValue<Quality>)value.get(0).getMutable()).get();

    //   // complete the skill with this monster
    //   quality.complete();
    // }

    //......................................................................
    //----- special qualities ----------------------------------------------

    // setup all the qualities that are given from the base
    // TODO: fix this
//     if(!m_specialQualities.isDefined())
//       for(Multiple quality : m_base.m_specialQualities)
//       {
//         // create the appropriate skills
//         String name           = ((SimpleText)quality.get(0).get()).get();
//         Parameters parameters = (Parameters)quality.get(1).get();
//         Condition  condition  = (Condition)quality.get(2).get();

//         Quality newQuality =
//           // TODO: change this
//           new Quality(name, parameters, condition /*m_file.getCampaign()*/);

//         m_specialQualities.add(new Multiple(new Multiple.Element []
//           {
//             new Multiple.Element(new EntryValue<Quality>(newQuality), false),
//             new Multiple.Element(new Duration(), true, "/", ""),
//           }));
//       }

    // finally, comllete all the qualities
    // for(Multiple value : m_specialQualities)
    // {
    //   Quality quality = ((EntryValue<Quality>)value.get(0).getMutable()).get();

    //   // complete the skill with this monster
    //   // TODO: change this
    //   quality.complete(this /*m_file.getCampaign()*/);
    // }

    //......................................................................
    //----- feats ----------------------------------------------------------

    // the skills must be done before the feats and qualities, as these could
    // adjust the skills

    // first we setup all the skills that are given from the base
    // TODO: fix this
//     if(!m_feats.isDefined())
//       for(Text text : m_base.m_feats)
//       {
//         // create the appropriate skills
//         String name = text.get();

//         // TODO: change this
//         Feat newFeat = new Feat(name, null /*m_file.getCampaign()*/);

//         m_feats.add(new EntryValue<Feat>(newFeat));
//       }

    // finally, comllete all the feats
    // for(EntryValue<Feat> value : m_feats)
    // {
    //   Feat feat = value.get();

    //   // complete the skill with this monster
    //   // TOOD: change this
    //   feat.complete(this, null /*m_file.getCampaign()*/);
    // }

    //......................................................................
    //----- treasure -------------------------------------------------------

    // if(!m_money.isDefined())
    // {
      // TODO: change this
//       addTreasure(m_base.m_treasure.getSelected(),
//                   null/*m_file.getCampaign()*/);
    // }

    //......................................................................
    //----- possesions -----------------------------------------------------

    // armor bonus, if we have any armor
    // for(EntryValue<Item> value : m_possessions)
    // {
    //   Item armor = value.get();

    //   if(!armor.hasAttachment(Armor.class))
    //     continue;

    //   // armor bonus
    //   net.ixitxachitls.dma.values.Modifier bonus =
    //     (net.ixitxachitls.dma.values.Modifier)
    //     armor.getValue("ac bonus");

    //   // set the item name into the modifier
    //   bonus.setDescription(armor.getName());

    //   //m_ac.addModifier(bonus);
    //   //m_acFlatFooted.addModifier(bonus);
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

//     // adjust speed for armor
//     if(inType == BaseMonster.SPEED)
//     {
//       int speed30 = Integer.MAX_VALUE;
//       int speed20 = Integer.MAX_VALUE;

//       for(Iterator<Value> i = m_possessions.iterator(); i.hasNext(); )
//       {
//         AbstractEntry entry = ((EntryValue)i.next()).get();

//         if(!entry.hasAttachment(Armor.class))
//           continue;

//         Item armor      = (Item)entry;
//         Multiple speeds = (Multiple)armor.getValue("speed");

//         if(!speeds.isDefined())
//           continue;

//         speed30 =
//           Math.min(speed30, (int)((Distance)
//                                 speeds.get(0).get()).getAsFeet().getValue());
//         speed20 =
//           Math.min(speed20, (int)((Distance)
//                                 speeds.get(1).get()).getAsFeet().getValue());
//       }

//       if(speed30 != Integer.MAX_VALUE || speed20 != Integer.MAX_VALUE)
//       {
//         ValueList modified = (ValueList)inValue.clone();

//         for(Iterator<Value> i = modified.mutableIterator(); i.hasNext(); )
//         {
//           Multiple mult = (Multiple)i.next();

//           if(!mult.get(0).get().isDefined())
//           {
//             // we have the land speed
//             Distance dist = (Distance)mult.get(1).get();

//             int armorSpeed = 0;
//             if(dist.getAsFeet().getLeader() == 30)
//               armorSpeed = speed30;
//             else
//               if(dist.getAsFeet().getLeader() == 20)
//                 armorSpeed = speed20;

//             if(armorSpeed < dist.getAsFeet().getLeader())
//             {
//               // set the values back
//               dist.setFeet(null, new Rational(armorSpeed), null);

//               mult.set(mult.get(0).get(), dist, mult.get(2).get());

//               return new Modifier(Modifier.Type.FIXED,
//                                   (net.ixitxachitls.dma.values.Modifiable)
//                                   modified);
//             }
//           }
//         }
//       }
//     }

//     return super.modifyValue(inType, inValue, inDynamic);
//   }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the given entry to the campaign entry.
   *
   * @param       inEntry the entry to add
   *
   * @return      true if added, false if not
   *
   */
  public boolean add(@Nonnull CampaignEntry inEntry)
  {
    String name = inEntry.getName();
    List<Name> names = new ArrayList<Name>();
    for(Name item : m_possessions)
      if(name.equals(item.get()))
        return true;
      else
        names.add(item);

    names.add(m_possessions.newElement().as(name));
    m_possessions = m_possessions.as(names);

    save();
    return true;
  }

  //.......................................................................

  //--------------------------- addFortModifier ----------------------------

  /**
   * Add a fortitude modifier to the monster.
   *
   * @param       inModifier the modifier to add
   *
   */
// public void addFortModifier(net.ixitxachitls.dma.values.Modifier inModifier)
//   {
//     //m_saveFort.addModifier(inModifier);
//   }

  //........................................................................
  //--------------------------- addRefModifier ----------------------------

  /**
   * Add a reflex modifier to the monster.
   *
   * @param       inModifier the modifier to add
   *
   */
// public void addRefModifier(net.ixitxachitls.dma.values.Modifier inModifier)
//   {
//     //m_saveRef.addModifier(inModifier);
//   }

  //........................................................................
  //--------------------------- addWillModifier ----------------------------

  /**
   * Add a will modifier to the monster.
   *
   * @param       inModifier the modifier to add
   *
   */
  // public void addWillModifier(net.ixitxachitls.dma.values.Modifier inModifier)
  // {
  //   //m_saveWill.addModifier(inModifier);
  // }

  //........................................................................
  //-------------------------- addGrappleModifier --------------------------

  /**
   * Add a grapple modifier to the monster.
   *
   * @param       inModifier the modifier to add
   *
   */
// public void addGrappleModifier(net.ixitxachitls.dma.values.Modifier
//                                inModifier)
// {
//     //m_grapple.addModifier(inModifier);
//   }

  //........................................................................
  //-------------------------- addAttackModifier ---------------------------

  /**
   * Add an attack modifier to the monster.
   *
   * @param       inModifier the modifier to add
   *
   */
  // public void addAttackModifier(net.ixitxachitls.dma.values.Modifier
  //                               inModifier)
  // {
  //   //m_attackMelee.addModifier(inModifier);
  //   //m_attackRanged.addModifier(inModifier);
  // }

  //........................................................................
  //------------------------ addInitiativeModifier -------------------------

  /**
   * Add an initiative modifier to the monster.
   *
   * @param       inModifier the modifier to add
   *
   */
  // public void addInitiativeModifier(net.ixitxachitls.dma.values.Modifier
  //                               inModifier)
  // {
  //   //m_initiative.addModifier(inModifier);
  // }

  //........................................................................
  //---------------------------- addHPModifier -----------------------------

  /**
   * Add a hit point modifier to the monster.
   *
   * @param       inModifier the modifier to add
   *
   */
  // public void addHPModifier(net.ixitxachitls.dma.values.Modifier inModifier)
  // {
  //   //m_hpMod.addModifier(inModifier);
  // }

  //........................................................................
  //---------------------------- addACModifier -----------------------------

  /**
   * Add a armor class modifier to the monster.
   *
   * @param       inModifier the modifier to add
   *
   */
// public void addACModifier(net.ixitxachitls.dma.values.Modifier inModifier)
//   {
//     //m_ac.addModifier(inModifier);

//     // add it to flatfooted or touch if necessary
//     //if("dodge".equals(inModifier.getType()))
//     //  m_acTouch.addModifier(inModifier);
//   }

  //........................................................................
  //--------------------------- addSkillModifier ---------------------------

  /**
   * Add a will modifier to the monster.
   *
   * @param       inSkill    the name of the skill to modify
   * @param       inModifier the modifier to add
   *
   */
// public void addSkillModifier(String inSkill,
//                                net.ixitxachitls.dma.values.Modifier inModifier)
//   {
    // look for the skill
//     for(Iterator<Value> i = m_skills.iterator(); i.hasNext(); )
//     {
//       Skill skill = (Skill)((EntryValue)i.next()).get();

//       if(skill.getName().equalsIgnoreCase(inSkill))
//       {
//         skill.addModifier(inModifier);

//         return;
//       }
//     }
  // }

  //........................................................................
  //----------------------------- addTreasure ------------------------------

  /**
   * Add a random treasure horde to this monster.
   *
   * @param       inType     the type of treasure to generate
   * @param       inCampaign the campaign to add the treasure from
   *
   */
  // protected void addTreasure(Treasure inType, CampaignData inCampaign)
  // {
//     if(m_base == null)
//       return;

    // TODO: this this
    // throw new UnsupportedOperationException("must be reimplemented");
//     int level = (int)m_base.m_cr.getLeader();

//     int bonus = 0;

//     if(level > s_treasures.size())
//     {
//       switch(level)
//       {
//         case 21: bonus = 1;

//           break;

//         case 22: bonus = 2;

//           break;

//         case 23: bonus = 4;

//           break;

//         case 24: bonus = 6;

//           break;

//         case 25: bonus = 9;

//           break;

//         case 26: bonus = 12;

//           break;

//         case 27: bonus = 17;

//           break;

//         case 28: bonus = 23;

//           break;

//         case 29: bonus = 31;

//           break;

//         case 30: bonus = 42;

//           break;

//         default:

//           Log.warning("don't know how to handle treasure of level " + level);

//           break;
//       }

//       // take the last treasure entry for all the higher CRs
//       level = s_treasures.size();
//     }

//     Treasure treasure = s_treasures.get(level);

//     for(int i = 0; i < inType.multiplier(); i++)
//     {
//       Coins coins = treasure.coins(s_random.nextInt(100) + 1);

//       if(coins != null)
//         coins.roll(m_money);

//       Goods goods = treasure.goods(s_random.nextInt(100) + 1);

//       if(goods != null)
//       {
//         m_possessions.define();

//         Item []possessions = goods.roll(inCampaign);

//         for(Item item : possessions)
//         {
//           m_possessions.add(new EntryValue<Item>(item));
//           inCampaign.add(item);
//         }
//       }

//       Items items = treasure.items(s_random.nextInt(100) + 1);

//       if(items != null)
//       {
//         m_possessions.define();

//         Item []possessions = items.roll(inCampaign, bonus);

//         for(Item item : possessions)
//         {
//           m_possessions.add(new EntryValue<Item>(item));
//           inCampaign.add(item);
//         }
//       }
//     }
  // }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- createMonster() ------------------------------------------------

    /** Create a typical item for testing purposes.
     *
     * @return the newly created item
     *
     */
    public static AbstractEntry createMonster()
    {
//       ParseReader reader =
//         new ParseReader(new java.io.StringReader(s_text), "test");

//       return Monster.read(reader, new BaseCampaign("Test"));
      return null;
    }

    //......................................................................
    //----- createBasedMonster() -------------------------------------------

    /** Create a typical item for testing purposes.
     *
     * @return the newly created item
     *
     */
    public static AbstractEntry createBasedMonster()
    {
//       // read the base entry
//       ParseReader reader =
//         new ParseReader(new java.io.StringReader(s_base), "test");

//       BaseCampaign campaign = new BaseCampaign("Test");

//       BaseEntry base = (BaseEntry)BaseMonster.read(reader, campaign);

//       campaign.add(base);

//       // and empty value
//       reader =
//         new ParseReader(new java.io.StringReader("skill test 1."),
//                         "test");

//       return Monster.read(reader, campaign);
      return null;
    }

    //......................................................................

    //----- text -----------------------------------------------------------

    /** Test text for item. */
    private static String s_text =
      "monster Achaierai = \n"
      + "\n"
      + "  hp    45;\n"
      + "  money 333 gp.\n";

    /** Test text for first base item. */
    private static String s_base =
      "base monster Achaierai = \n"
      + "\n"
      + "  size              Large (Tall);\n"
      + "  type              Outsider (Evil, Extraplanar, Lawful);\n"
      + "  hit dice          6d8;\n"
      + "  speed             50 ft;\n"
      + "  natural armor     +10;\n"
      + "  base attack       +6;\n"
      + "  primary attacks   2 claw melee (2d6);\n"
      + "  secondary attacks bite melee (4d6);\n"
      + "  special attacks   Black cloud;\n"
      + "  special qualities Darkvision [Range 60 ft], No Soul, Does Not Eat, "
      + "Does Not Sleep, Affected As Evil, Weapons Evil, Affected as Lawful, "
      + "Weapons Lawful;\n"
      + "  strength          19;\n"
      + "  dexterity         13;\n"
      + "  constitution      14;\n"
      + "  intelligence      11;\n"
      + "  wisdom            14;\n"
      + "  charisma          16;\n"
      + "  class skills      Balance +9, Climb +9, Diplomacy, Hide +9, "
      + "Jump +9, Listen +9, Move Silently +9, Sense Motive +9, Spot +9;\n"
      + "  feats             Dodge, Mobility, Spring Attack;\n"
      + "  environment       Infernal Battlefield of Acheron;\n"
      + "  organization      Solitary, flock (1d4+4);\n"
      + "  challenge rating  5;\n"
      + "  treasure          Double standard;\n"
      + "  alignment         Always lawful evil;\n"
      + "  advancement       7-12 HD (Large), 13-18 HD (Huge);\n"
      + "  level adjustment  -;\n"
      + "  encounter         \"encounter\";\n"
      + "  combat            \"combat\";\n"
      + "  languages         Infernal;\n"
      + "  tactics           \"tactics\";\n"
      + "  character         \"character\";\n"
      + "  reproduction      \"reproduction\";\n"
      + "  short description \"short\";\n"
      + "  world             generic;\n"
      + "  references        \"WTC 17755\" 9-10;\n"
      + "  description       \"description\".\n";

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      // TODO: fix tests
//       ParseReader reader =
//         new ParseReader(new java.io.StringReader(s_text), "test");

//       String result = "monster Achaierai =\n"
//         + "\n"
//         + "  hp                45;\n"
//         + "  money             333 gp.\n";

//       Entry entry = (Entry)Monster.read(reader, new BaseCampaign("Test"));

//       //System.out.println("read entry:\n'" + entry + "'");

//       assertNotNull("item should have been read", entry);
//       assertEquals("item name does not match", "Achaierai",
//                    entry.getName());
//       assertEquals("item does not match", result, entry.toString());

//       m_logger.addExpectedPattern("WARNING: base.not-found:.*"
//                                   + "(base name 'Achaierai').*");
    }

    //......................................................................
    //----- print ----------------------------------------------------------

    /** Testing printing. */
    @org.junit.Test
    public void print()
    {
//       ParseReader reader =
//         new ParseReader(new java.io.StringReader(s_text), "test");

//       Entry entry = (Entry)Monster.read(reader, new BaseCampaign("Test"));

//       String result =
//         "\\center{"
//         + "\\icon{worlds/[no base entry].png}"
//         + "{world: [no base entry]}"
//         + "{../index/worlds/\\worduppercase{[no base entry]}.html}"
//         + "{highlight}}\n"
//         + "\\divider{main}{\\title{\\link[BaseMonsters/Achaierai]"
//         + "[nocolor]{Achaierai}\\linebreak "
//         + "\\tiny{\\link[Monsters/index]{(monster)}}}\n"
//         + "\\color{error}{[no base entry]}\n"
//         + "\\files{Achaierai}"
//         + "\\table[description]{f15:L(desc-label);100:L(desc-text)}"
//         + "{null}{null}"
//         + "{null}{null}"
//         + "{\\window{\\bold{Hit Points:}}{"
//         + Config.get("resource:help/label.monster.hp", (String)null)
//         + "}}{45 (, max 5000)}"
//         + "{\\window{\\bold{Initiative:}}{"
//         + Config.get("resource:help/label.initiative", (String)null)
//         + "}}{\\link[index/initiatives/0][]{+0}}"
//         + "{\\window{\\bold{Speed:}}{"
//         + Config.get("resource:help/label.speed", (String)null)
//         + "}}{\\color{error}{[no base entry]}}"
//         + "{\\window{\\bold{Armor Class:}}{"
//         + Config.get("resource:help/label.armor.class", (String)null)
//         + "}}{\\link[index/acs/10][]{10}, "
//         + "touch \\link[index/touchacs/10][]{10}, "
//         + "flat-footed \\link[index/flatfootedacs/10][]{10}}"
//         + "{\\window{\\bold{Base Attack:}}{"
//         + Config.get("resource:help/label.base.attack", (String)null)
//         + "}}{\\color{error}{[no base entry]}}"
//         + "{\\window{\\bold{Grapple:}}{"
//         + Config.get("resource:help/label.grapple", (String)null)
//         + "}}{\\link[index/grapples/0][]{+0}}"
//         + "{\\window{\\bold{Attack:}}{"
//         + Config.get("resource:help/label.attack", (String)null)
//         + "}}{\\color{error}{no base entry}}"
//         + "{\\window{\\bold{Full Attack:}}{"
//         + Config.get("resource:help/label.full.attack", (String)null)
//         + "}}{\\color{error}{no base entry]}}"
//         + "{\\window{\\bold{Space:}}{"
//         + Config.get("resource:help/label.space", (String)null)
//         + "}}{\\color{error}{[no base entry]}}"
//         + "{\\window{\\bold{Reach:}}{"
//         + Config.get("resource:help/label.reach", (String)null)
//         + "}}{\\color{error}{[no base entry]}}"
//         + "{\\window{\\bold{Special Attacks:}}{"
//         + Config.get("resource:help/label.special.attacks", (String)null)
//         + "}}{\\color{error}{$undefined$}}"
//         + "{\\window{\\bold{Special Qualities:}}{"
//         + Config.get("resource:help/label.special.qualities", (String)null)
//         + "}}{\\color{error}{$undefined$}}"
//         + "{\\window{\\bold{Saves:}}{"
//         + Config.get("resource:help/label.saves", (String)null)
//         + "}}{Fort +0, Ref +0, Will +0}"
//         + "{\\window{\\bold{Skills:}}{"
//         + Config.get("resource:help/label.skills", (String)null)
//         + "}}{\\color{error}{$undefined$}}"
//         + "{\\window{\\bold{Feats:}}{"
//         + Config.get("resource:help/label.feats", (String)null)
//         + "}}{\\color{error}{$undefined$}}"
//         + "{\\window{\\bold{Money:}}{"
//         + Config.get("resource:help/label.money", (String)null)
//         + "}}{\\window{\\span{unit}{333 gp}}"
//         + "{\\table{#inline#1:L,,;100:L}{Total:}{333 gp}}}"
//         + "{\\window{\\bold{Possessions:}}{"
//         + Config.get("resource:help/label.possessions", (String)null)
//         + "}}{\\color{error}{$undefined$}}"
//         + "{\\window{\\bold{References:}}{"
//         + Config.get("resource:help/label.references", (String)null)
//         + "}}{\\color{error}{*unknown variable*}}{null}{null}"
//         + "\\divider{clear}{}}"
//         + "\\nopictures{\\table{f15:L;100:L}"
//         + "{\\bold{World:}}{\\color{error}{[no base entry]}}"
//         + "}\n";

//       //System.out.println(entry.getPrintCommand());
//       assertEquals("print commands",
//                    result,
//                    entry.getCommand(new BaseCampaign("Test"),
//                                     PrintType.print).toString());

//       m_logger.addExpectedPattern("WARNING: base.not-found:.*"
//                                   + "(base name 'Achaierai').*");
    }

    //......................................................................
    //----- printComplete --------------------------------------------------

    /** Testing printing. */
    @org.junit.Test
    public void pPrintComplete()
    {
//       BaseCampaign campaign = new BaseCampaign("Test");

//       ParseReader reader =
//         new ParseReader(new java.io.StringReader(s_base), "test");

//       BaseEntry base = (BaseEntry)BaseMonster.read(reader, campaign);

//       campaign.add(base);

//       reader =
//         new ParseReader(new java.io.StringReader(s_text), "test");

//       Entry entry = (Entry)Monster.read(reader, campaign);

//       campaign.add(entry);

//       String result =
//         "\\center{"
//         + "\\icon{worlds/Generic.png}"
//         + "{world: Generic}"
//         + "{../index/worlds/\\worduppercase{Generic}.html}"
//         + "{highlight}}\n"
//         + "\\divider{main}{\\title{\\link[BaseMonsters/Achaierai]"
//         + "[nocolor]{Achaierai}\\linebreak "
//         + "\\tiny{\\link[Monsters/index]{(monster)}}}\n"
//         + "\\textblock[desc]{description}\n"
//         + "\\files{Achaierai}"
//         + "\\table[description]{f15:L(desc-label);100:L(desc-text)}"
//         + "{null}{null}"
//         + "{null}{null}"
//         + "{\\window{\\bold{Hit Points:}}{"
//         + Config.get("resource:help/label.monster.hp", (String)null)
//         + "}}{45 (\\window[ability, stacks]{+12 Con}{ability bonus"
//         + "\\linebreak this modifier stacks with similar ones}, max "
//         + (((Monster)entry).m_maxHP.get()) + ")}"
//         + "{\\window{\\bold{Initiative:}}{"
//         + Config.get("resource:help/label.initiative", (String)null)
//         + "}}{\\link[index/initiatives/1][]{+1 (\\window[ability, stacks]"
//         + "{+1 Dex}{ability bonus\\linebreak this modifier stacks with "
//         + "similar ones})}}"
//         + "{\\window{\\bold{Speed:}}{"
//         + Config.get("resource:help/label.speed", (String)null)
//         + "}}{ \\link[index/speeds/50 ft]{\\window{\\span{unit}{50 ft}}"
//         + "{\\table{#inline#1:L,,;100:L}{Total:}{50 ft}{}{}{Metric:}"
//         + "{\\span{unit}{20 m}}}}}"
//         + "{\\window{\\bold{Armor Class:}}{"
//         + Config.get("resource:help/label.armor.class", (String)null)
//         + "}}{\\link[index/acs/20][]{20 "
//         + "(\\window[natural armor, does not stack]{+10 Natural Armor}"
//         + "{natural armor bonus\\linebreak this modifier does \\emph{not} "
//         + "stack with similar ones}, "
//       + "\\window[general, stacks]{-1 Size}{general penalty\\linebreak this "
//         + "modifier stacks with similar ones}, "
//         + "\\window[ability, stacks]{+1 Dex}{ability bonus"
//         + "\\linebreak this modifier stacks with similar ones})}, "
//         + "touch \\link[index/touchacs/10][]{10 "
//         + "(\\window[ability, stacks]{+1 Dex}{ability bonus"
//         + "\\linebreak this modifier stacks with similar ones}, "
//         + "\\window[size, stacks]{-1 Size}{size penalty\\linebreak this "
//         + "modifier stacks with similar ones})}, "
//         + "flat-footed \\link[index/flatfootedacs/19][]{19 "
//         + "(\\window[natural armor, does not stack]{+10 Natural Armor}"
//         + "{natural armor bonus\\linebreak this modifier does \\emph{not} "
//         + "stack with similar ones}, "
//         + "\\window[size, stacks]{-1 Size}{size penalty\\linebreak this "
//         + "modifier stacks with similar ones})}}"
//         + "{\\window{\\bold{Base Attack:}}{"
//         + Config.get("resource:help/label.base.attack", (String)null)
//         + "}}{\\link[index/baseattacks/+6]{+6}}"
//         + "{\\window{\\bold{Grapple:}}{"
//         + Config.get("resource:help/label.grapple", (String)null)
//         + "}}{\\link[index/grapples/14][]{+14 (\\window[ability, stacks]"
//         + "{+4 Str}{ability bonus\\linebreak this modifier stacks with "
//         + "similar ones}, \\window[size, does not stack]{+4 Size}"
//         + "{size bonus\\linebreak this modifier does \\emph{not} stack with "
//         + "similar ones})}}"
//         + "{\\window{\\bold{Attack:}}{"
//         + Config.get("resource:help/label.attack", (String)null)
//         + "}}{ or " // TODO
//         + "\\link[index/attackmodes/Claw][]{Claw} +9 (\\window[ability, "
//         + "stacks]{+4 Str}{ability bonus\\linebreak this modifier stacks "
//         + "with similar ones}, \\window[size, stacks]{-1 Size}{size penalty"
//         + "\\linebreak this modifier stacks with similar ones}) "
//         + "\\link[index/attackstyles/Melee][]{Melee} "
//         + "(\\link[index/damages/2d6][]{2d6}+4 (\\window[ability, stacks]"
//         + "{+4 Str}{ability bonus\\linebreak this modifier stacks with "
//         + "similar ones}))}"
//         + "{\\window{\\bold{Full Attack:}}{"
//         + Config.get("resource:help/label.full.attack", (String)null)
//       + "}}{2 \\link[index/attackmodes/Claw][]{Claw} +9 (\\window[ability, "
//         + "stacks]{+4 Str}{ability bonus\\linebreak this modifier stacks "
//         + "with similar ones}, \\window[size, stacks]{-1 Size}{size penalty"
//         + "\\linebreak this modifier stacks with similar ones}) "
//         + "\\link[index/attackstyles/Melee][]{Melee} "
//         + "(\\link[index/damages/2d6][]{2d6}+4 (\\window[ability, stacks]"
//         + "{+4 Str}{ability bonus\\linebreak this modifier stacks with "
//         + "similar ones})) and \\link[index/attackmodes/Bite][]{Bite} +4 "
//         + "(\\window[ability, stacks]{+4 Str}{ability bonus\\linebreak this "
//         + "modifier stacks with similar ones}, \\window[size, stacks]"
//         + "{-1 Size}{size penalty\\linebreak this modifier stacks with "
//         + "similar ones}) \\link[index/attackstyles/Melee][]{Melee} "
//         + "(\\link[index/damages/4d6][]{4d6}+2 (\\window[ability, stacks]"
//         + "{+2 Str}{ability bonus\\linebreak this modifier stacks with "
//         + "similar ones}))}"
//         + "{\\window{\\bold{Space:}}{"
//         + Config.get("resource:help/label.space", (String)null)
//         + "}}{\\span{computed}{\\link[index/spaces/10]"
//         + "{\\window{\\span{unit}{10 ft}}{\\table{#inline#1:L,,;100:L}"
//         + "{Total:}{10 ft}{}{}{Metric:}{\\span{unit}{4 m}}}}}}"
//         + "{\\window{\\bold{Reach:}}{"
//         + Config.get("resource:help/label.reach", (String)null)
//         + "}}{\\span{computed}{\\link[index/reaches/10]"
//         + "{\\window{\\span{unit}{10 ft}}{\\table{#inline#1:L,,;100:L}"
//         + "{Total:}{10 ft}{}{}{Metric:}{\\span{unit}{4 m}}}}}}"
//         + "{\\window{\\bold{Special Attacks:}}{"
//         + Config.get("resource:help/label.special.attacks", (String)null)
//         + "}}{\\link[BaseQualitys/Black cloud][]{Black cloud}}"
//         + "{\\window{\\bold{Special Qualities:}}{"
//         + Config.get("resource:help/label.special.qualities", (String)null)
//         + "}}{\\link[BaseQualitys/Darkvision][]{Darkvision} [60 ft], "
//         + "\\link[BaseQualitys/No Soul][]{No Soul}, "
//         + "\\link[BaseQualitys/Does Not Eat][]{Does Not Eat}, "
//         + "\\link[BaseQualitys/Does Not Sleep][]{Does Not Sleep}, "
//         + "\\link[BaseQualitys/Affected As Evil][]{Affected As Evil}, "
//         + "\\link[BaseQualitys/Weapons Evil][]{Weapons Evil}, "
//         + "\\link[BaseQualitys/Affected as Lawful][]{Affected as Lawful}, "
//         + "\\link[BaseQualitys/Weapons Lawful][]{Weapons Lawful}}"
//         + "{\\window{\\bold{Saves:}}{"
//         + Config.get("resource:help/label.saves", (String)null)
//         + "}}{Fort +7 (\\window[ability, stacks]{+2 Con}"
//         + "{ability bonus\\linebreak this modifier stacks with similar "
//         + "ones}), Ref +6 (\\window[ability, stacks]{+1 Dex}{ability bonus"
//         + "\\linebreak this modifier stacks with similar ones}), Will +7 "
//         + "(\\window[ability, stacks]{+2 Wis}{ability bonus\\linebreak this "
//         + "modifier stacks with similar ones})}"
//         + "{\\window{\\bold{Skills:}}{"
//         + Config.get("resource:help/label.skills", (String)null)
//         + "}}{\\link[BaseSkills/Balance][bold]{Balance} +9 "
//         + "(\\window[general, stacks]{+9 Ranks}{general bonus\\linebreak "
//         + "this modifier stacks with similar ones}), "
//         + "\\link[BaseSkills/Climb][bold]{Climb} +9 "
//         + "(\\window[general, stacks]{+9 Ranks}{general bonus\\linebreak "
//         + "this modifier stacks with similar ones}), "
//         + "\\link[BaseSkills/Diplomacy][bold]{Diplomacy} +0 "
//         + "(\\window[general, stacks]{+0 Ranks}{general bonus\\linebreak "
//         + "this modifier stacks with similar ones}), "
//         + "\\link[BaseSkills/Hide][bold]{Hide} +9 "
//         + "(\\window[general, stacks]{+9 Ranks}{general bonus\\linebreak "
//         + "this modifier stacks with similar ones}), "
//         + "\\link[BaseSkills/Jump][bold]{Jump} +9 "
//         + "(\\window[general, stacks]{+9 Ranks}{general bonus\\linebreak "
//         + "this modifier stacks with similar ones}), "
//         + "\\link[BaseSkills/Listen][bold]{Listen} +9 "
//         + "(\\window[general, stacks]{+9 Ranks}{general bonus\\linebreak "
//         + "this modifier stacks with similar ones}), "
//         + "\\link[BaseSkills/Move Silently][bold]{Move Silently} +9 "
//         + "(\\window[general, stacks]{+9 Ranks}{general bonus\\linebreak "
//         + "this modifier stacks with similar ones}), "
//         + "\\link[BaseSkills/Sense Motive][bold]{Sense Motive} +9 "
//         + "(\\window[general, stacks]{+9 Ranks}{general bonus\\linebreak "
//         + "this modifier stacks with similar ones}), "
//         + "\\link[BaseSkills/Spot][bold]{Spot} +9 "
//         + "(\\window[general, stacks]{+9 Ranks}{general bonus\\linebreak "
//         + "this modifier stacks with similar ones})}"
//         + "{\\window{\\bold{Feats:}}{"
//         + Config.get("resource:help/label.feats", (String)null)
//         + "}}{\\link[BaseFeats/Dodge][nocolor]{Dodge}, "
//         + "\\link[BaseFeats/Mobility][nocolor]{Mobility}, "
//         + "\\link[BaseFeats/Spring Attack][nocolor]{Spring Attack}}"
//         + "{\\window{\\bold{Money:}}{"
//         + Config.get("resource:help/label.money", (String)null)
//         + "}}{\\window{\\span{unit}{333 gp}}"
//         + "{\\table{#inline#1:L,,;100:L}{Total:}{333 gp}}}"
//         + "{\\window{\\bold{Possessions:}}{"
//         + Config.get("resource:help/label.possessions", (String)null)
//         + "}}{\\color{error}{$undefined$}}"
//         + "{\\window{\\bold{References:}}{"
//         + Config.get("resource:help/label.references", (String)null)
//       + "}}{\\span{unit}{\\link[BaseProducts/WTC 17755]{WTC 17755} p. 9-10}}"
//         + "{null}{null}"
//         + "\\divider{clear}{}}"
//         + "\\nopictures{\\table{f15:L;100:L}"
//         + "{\\bold{World:}}{\\link[index/worlds/Generic]{Generic}}"
//         + "}\n";

//       //System.out.println(entry.getPrintCommand());
//       assertEquals("print commands",
//                    result,
//                    entry.getCommand(new BaseCampaign("Test"),
//                                     PrintType.print)
//                    .toString());

//       m_logger.addExpected("WARNING: could not obtain feat 'Dodge' from "
//                            + "campaign");
//       m_logger.addExpected("WARNING: could not obtain feat 'Mobility' from "
//                            + "campaign");
//       m_logger.addExpected("WARNING: could not obtain feat 'Spring Attack' "
//                            + "from campaign");
//       m_logger.addExpected("WARNING: could not find base for 'Balance'");
//       m_logger.addExpected("WARNING: could not find base for 'Climb'");
//       m_logger.addExpected("WARNING: could not find base for 'Diplomacy'");
//       m_logger.addExpected("WARNING: could not find base for 'Hide'");
//       m_logger.addExpected("WARNING: could not find base for 'Jump'");
//       m_logger.addExpected("WARNING: could not find base for 'Listen'");
//     m_logger.addExpected("WARNING: could not find base for 'Move Silently'");
//     m_logger.addExpected("WARNING: could not find base for 'Sense Motive'");
//       m_logger.addExpected("WARNING: could not find base for 'Spot'");
//       m_logger.addExpected("WARNING: could not find base quality for "
//                            + "'Black cloud'");
//       m_logger.addExpected("WARNING: could not find base quality for "
//                            + "'Darkvision'");
//       m_logger.addExpected("WARNING: could not find base quality for "
//                            + "'No Soul'");
//       m_logger.addExpected("WARNING: could not find base quality for "
//                            + "'Does Not Eat'");
//       m_logger.addExpected("WARNING: could not find base quality for "
//                            + "'Does Not Sleep'");
//       m_logger.addExpected("WARNING: could not find base quality for "
//                            + "'Affected As Evil'");
//       m_logger.addExpected("WARNING: could not find base quality for "
//                            + "'Weapons Evil'");
//       m_logger.addExpected("WARNING: could not find base quality for "
//                            + "'Affected as Lawful'");
//       m_logger.addExpected("WARNING: could not find base quality for "
//                            + "'Weapons Lawful'");
//       m_logger.addExpected("WARNING: could not find base for 'Dodge'");
//       m_logger.addExpected("WARNING: could not find base for 'Mobility'");
//     m_logger.addExpected("WARNING: could not find base for 'Spring Attack'");
    }

    //......................................................................
  }

  //........................................................................
}
