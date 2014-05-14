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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.extensions.Weapon;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto;
import net.ixitxachitls.dma.proto.Entries.CampaignEntryProto;
import net.ixitxachitls.dma.proto.Entries.MonsterProto;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.Critical;
import net.ixitxachitls.dma.values.Damage;
import net.ixitxachitls.dma.values.Dice;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.ModifiedNumber;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Money;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Parameters;
import net.ixitxachitls.dma.values.Reference;
import net.ixitxachitls.dma.values.Size;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.WeaponStyle;
import net.ixitxachitls.util.Pair;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a real monster.
 *
 * @file          Monster.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class Monster extends CampaignEntry<BaseMonster>
{
  //----------------------------------------------------------------- nested

  //----- Line -----------------------------------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

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
    protected Coins(Money.Coin inType, int inStart, int inEnd,
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
    private Money.Coin m_type;

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
    protected void roll(Money ioMoney)
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
            RANDOM.nextInt((m_dice - 1) * m_multiplier) + m_multiplier;

      ioMoney.add(m_type, value);
    }

    /** Convert the money line to a human readable string.
     *
     * @return the converted string
     *
     */
    @Override
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
      private Type(String inName)
      {
        m_name = inName;
      }

      /** The name of the type. */
      private String m_name;

      /** Get the name of the goods.
       *
       * @return the converted string
       *
       */
      @Override
      public String toString()
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
    protected Goods(Type inCategory, int inStart, int inEnd, int inNumber,
                    int inDice)
    {
      super(inStart, inEnd);

      assert inNumber >= 0 : "must have a positive number here";
      assert inDice >= 0 : "must have a positive dice here";

      m_category   = inCategory;
      m_number     = inNumber;
      m_dice       = inDice;
    }

    /** The category of goods to generate. */
    private Type m_category;

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
    protected Item []roll()
    {
      if(m_dice <= 0 || m_number <= 0)
        return new Item [0];

      int value = 0;

      if(m_dice == 1)
        value = m_number;
      else
        for(int i = 0; i < m_number; i++)
          value += RANDOM.nextInt(m_dice - 1);

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
    @Override
    public String toString()
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
    protected Items(Type inType, int inStart, int inEnd, int inNumber,
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
    private Type m_type;

    /** The number of dice to use to compute the number of items. */
    private int m_number;

    /** The dice to compute the number of items. */
    private int m_dice;

    /**
     * Determine the items that are randomly generated from this category
     * of treasure items.
     *
     * @param inBonus    special major magical items to use for high
     *                   levels (>20)
     *
     * @return the generated items
     */
    protected Item []roll(int inBonus)
    {
      assert inBonus >= 0 : "bonus must not be negative";

      if(m_dice <= 0 || m_number <= 0)
        return new Item [0];

      int value = inBonus;

      if(m_dice == 1)
        value = m_number;
      else
        for(int i = 0; i < m_number; i++)
          value += RANDOM.nextInt(m_dice - 1);

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
    @Override
    public String toString()
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
    private List<Coins> m_coins = new ArrayList<Coins>();

    /** All the different lines for items for this treasure. */
    private List<Items> m_items = new ArrayList<Items>();

    /** All the different lines for goods for this treasure. */
    private List<Goods> m_goods = new ArrayList<Goods>();

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

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------- Monster --------------------------------

  /**
   * This is the internal, default constructor.
   *
   */
  protected Monster()
  {
    super(TYPE);
  }

  //........................................................................
  //------------------------------- Monster --------------------------------

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base item
   *
   */
  public Monster(Campaign inCampaign)
  {
    super(TYPE, inCampaign);
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
  // public Monster(BaseMonster inBase)
  // {
  //   super(inBase.getName(), TYPE, BASE_TYPE, inBase);

  //   // take over the base items values
  //   complete();
  // }

  //........................................................................

  /**
   * Constructor with a derviced type.
   *
   * @param inType the derived type
   */
  protected Monster(Type<? extends Monster> inType)
  {
    super(inType);
  }

  /**
   * Constructor for derivations.
   *
   * @param inName      the name of the monster
   * @param inType      the type of the monster
   */
   protected Monster(String inName, Type<? extends Monster> inType)
   {
     super(inName, inType);
   }

  //........................................................................

  //-------------------------------------------------------------- variables

   /** The type of this entry. */
   public static final Type<Monster> TYPE =
     new Type<Monster>(Monster.class, BaseMonster.TYPE);

   /** The type of the base entry to this entry. */
   public static final BaseType<BaseMonster> BASE_TYPE = BaseMonster.TYPE;

  //----- possessions ------------------------------------------------------

  /** The possessions value. */
  @Key("possesions")
  protected ValueList<Name> m_possessions =
    new ValueList<Name>(new Name());

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
  protected ValueList<Reference<BaseFeat>> m_feats =
    new ValueList<Reference<BaseFeat>>
    (", ", new Reference<BaseFeat>(BaseFeat.TYPE)
     .withParameter("Name", new Name(), Parameters.Type.UNIQUE));

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

  /** The monster's fortitude save. */
  @Key("fortitude save")
  protected Number m_fortitudeSave = new Number(-1, 100, true);

  static
  {
    addIndex(new Index(Index.Path.FORTITUDE_SAVES, "Fortitude Saves", TYPE));
  }

  //........................................................................
  //----- will save --------------------------------------------------------

  /** The monster's will save. */
  @Key("will save")
  protected Number m_willSave = new Number(-1, 100, true);

  static
  {
    addIndex(new Index(Index.Path.WILL_SAVES, "Will Saves", TYPE));
  }

  //........................................................................
  //----- reflex save ------------------------------------------------------

  /** The monster's reflex. */
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
  private static List<Treasure> s_treasures = new ArrayList<Treasure>();

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
  public int abilityModifier(long inScore)
  {
    if(inScore < 0)
      return 0;

    return (int) (inScore / 2) - 5;
  }

  //........................................................................
  //--------------------------- abilityModifier ----------------------------

  /**
   * Compute the ability modifier for the given score.
   *
   * @param       inAbility the ability to compute for
   *
   * @return      the compute modifier
   */
  public int abilityModifier(BaseMonster.Ability inAbility)
  {
    return abilityModifier(ability(inAbility).getMaxValue());
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
    Combined<Number> combinedCon = collect("constitution");
    Number con = combinedCon.max();
    if(con != null)
      return (int)con.get();

    return 0;
  }

  //........................................................................
  //------------------------------- ability --------------------------------

  /**
   * Get an ability score of the monster.
   *
   * @param       inAbility the ability score to get
   *
   * @return      the combined ability score
   */
  public Combined<Number> ability(BaseMonster.Ability inAbility)
  {
   return collect(inAbility.toString().toLowerCase(Locale.US));
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
    ModifiedNumber initiative = collect("initiative").modifier();

    int dexterity = ability(BaseMonster.Ability.DEXTERITY).getMaxValue();
    initiative.withModifier
      (new Modifier(abilityModifier(dexterity), Modifier.Type.ABILITY),
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
    return armorClass().modifier().ignore(Modifier.Type.ARMOR,
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
  public Combined<Modifier> armorClass()
  {
    Combined<Modifier> armor = collect("armor class");
    armor.addModifier(new Modifier(10), this, "base");
    Combined<Modifier> naturalArmor = collect("natural armor");
    armor.add(naturalArmor, "natural armor");

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
    ModifiedNumber armor = armorClass().modifier();

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
    int dex =
      abilityModifier(ability(BaseMonster.Ability.DEXTERITY).getMaxValue());

    // TODO: we should actually only consider worn items.
    for(Name name : m_possessions)
    {
      Item item = getCampaign().getItem(name.get());
      if(item == null)
        continue;

      Combined<Number> combinedMaxDex = item.collect("max dexterity");
      Number maxDex = combinedMaxDex.min();
      if(maxDex != null && maxDex.isDefined() && maxDex.get() < dex)
        dex = (int)maxDex.get();
    }

    return dex;
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
  public Map<String, Object> attacks()
  {
    Combined<Number> baseAttack = collect("base attack");
    ModifiedNumber baseAttackNumber = baseAttack.modifier();
    if(baseAttackNumber.hasConditions())
      throw new UnsupportedOperationException
        ("conditions in base attack are not supported");

    List<Long> baseAttacks = Lists.newArrayList();
    if (baseAttackNumber.getMinValue() == 0)
      baseAttacks.add(0L);
    else
      for(long attack = baseAttackNumber.getMinValue(); attack > 0; attack -= 5)
        baseAttacks.add(attack);

    List<Map<String, Object>> weaponAttacks =
      new ArrayList<Map<String, Object>>();
    for(Name name : m_possessions)
    {
      Item item = getCampaign().getItem(name.get());
      if(item == null)
        continue;

      if(!item.hasExtension(Weapon.class))
        continue;

      Combined<EnumSelection<WeaponStyle>> style =
        item.collect("weapon style");
      List<Long> baseAtks = new ArrayList<Long>(baseAttacks);
      if(hasFeat("Rapid Shot"))
        for(Value<?> styleValue : style.valuesOnly())
          if(styleValue instanceof EnumSelection
             && ((EnumSelection)styleValue).getSelected()
             == WeaponStyle.RANGED)
          {
            baseAtks.add(0, baseAtks.get(0));
            break;
          }

      Map<String, Object> weapon = new HashMap<String, Object>();
      long maxAttacks = item.collect("max attacks").modifier().getMinValue();

      if(maxAttacks <= 0)
        maxAttacks = Long.MAX_VALUE;

      List<ModifiedNumber> attacks = new ArrayList<ModifiedNumber>();
      for(int i = 0; i < maxAttacks && i < baseAtks.size(); i++)
        attacks.add(weaponAttack(item, baseAtks.get(i)));

      weapon.put("attacks", attacks);
      weapon.put("style", style);
      weapon.put("name", item.getDMName());
      weapon.put("damage", collect("damage"));
      weapon.put("critical", critical(item));

      weaponAttacks.add(weapon);
    }

    return new ImmutableMap.Builder<String, Object>()
      .put("base", baseAttacks)
      .put("bases", baseAttack.valuesWithDescriptions())
      .put("primary", attacks("primary attacks", false, baseAttacks))
      .put("secondary", attacks("secondary attacks", true, baseAttacks))
      .put("weapons", weaponAttacks)
      .put("grapple", grapple())
      .build();
  }


  //........................................................................
  //------------------------------- attacks --------------------------------

  /**
   * Compute the attacks the monster can do.
   *
   * @param       inName         the name of the attack made
   * @param       inSecondary    whether this is a secondary attack or not
   * @param       inBaseAttacks  the list of base attack modifiers
   *
   * @return      a list with maps containing the attack data
   *
   */
  @SuppressWarnings("unchecked") // casting
  private List<Map<String, Object>> attacks(String inName, boolean inSecondary,
                                            List<Long> inBaseAttacks)
  {
    boolean weaponFinesse = hasFeat("Weapon Finesse");
    Combined<Number> attacksValue = collect(inName);

    List<Map<String, Object>> result = Lists.newArrayList();
    for(Value<?> list : attacksValue.valuesOnly())
      for(Multiple attack : (ValueList<Multiple>)list)
      {
        BaseMonster.AttackMode attackMode =
          ((EnumSelection<BaseMonster.AttackMode>)attack.get(1)).getSelected();

        if(attackMode == BaseMonster.AttackMode.WEAPON)
          continue;

        boolean melee =
          ((EnumSelection<BaseMonster.AttackStyle>)
           attack.get(2)).getSelected() == BaseMonster.AttackStyle.MELEE;

        BaseMonster.Ability keyAbility;
        if(weaponFinesse || !melee || attackMode.useDexterity())
          keyAbility = BaseMonster.Ability.DEXTERITY;
        else
          keyAbility = BaseMonster.Ability.STRENGTH;

        List<ModifiedNumber> attacks = Lists.newArrayList();
        attacks.add(naturalAttack
                    (keyAbility,
                     inBaseAttacks.get(0)
                     - ("secondary attacks".equals(inName) ? 5 : 0), false,
                    attackMode));

        Map<String, Object> single = Maps.newHashMap();
        single.put("attacks", attacks);
        single.put("number", attack.get(0));
        single.put("mode", attack.get(1));
        single.put("style", attack.get(2));
        single.put("damage",
                    adjustDamageForStrength((Damage)attack.get(3), melee,
                                            inSecondary));
        single.put("critical", critical(null));

        result.add(single);
      }

    return result;
  }

  //........................................................................
  //------------------------------- grapple --------------------------------

  /**
   * Compute the grapple check.
   *
   * @return      the modified number with the grapple check.
   */
  @SuppressWarnings("unchecked")
  public ModifiedNumber grapple()
  {
    long baseAttack = collect("base attack").getMaxValue();
    ModifiedNumber grapple = new ModifiedNumber(0);
    grapple.withModifier(new Modifier((int)baseAttack), "base attack");
    int strength = ability(BaseMonster.Ability.STRENGTH).getMaxValue();
    grapple.withModifier
      (new Modifier(abilityModifier(strength), Modifier.Type.ABILITY),
       "Str of " + strength);

    Combined<Multiple> combinedSize = collect("size");
    List<Pair<Multiple, List<Pair<Multiple, String>>>> sizesPerGroup =
      combinedSize.valuesWithDescriptions();

    Size size = Size.MEDIUM;
    for(Pair<Multiple, List<Pair<Multiple, String>>> sizeGroup : sizesPerGroup)
      if(((EnumSelection<Size>)sizeGroup.first().get(0)).getSelected()
         .isBigger(size))
        size = ((EnumSelection<Size>)sizeGroup.first().get(0))
          .getSelected();

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
  public ModifiedNumber weaponAttack(Item inItem, long inBaseAttack)
  {
    // Strength bonus (or dexterity)
    BaseMonster.Ability keyAbility = BaseMonster.Ability.STRENGTH;
    Combined<EnumSelection<WeaponStyle>> weaponStyle =
      collect("weapon style");
    EnumSelection<WeaponStyle> weaponStyleMin = weaponStyle.min();
    if(weaponStyleMin != null && !weaponStyle.min().getSelected().isMelee())
      keyAbility = BaseMonster.Ability.DEXTERITY;
    else
      // Somehow handle this in the feat!
      for(Pair<Reference<BaseFeat>, List<String>> feat : allFeats())
      {
        Name name = (Name)feat.first().getParameters().getValue("Name");
        if(name != null)
          if("weapon finesse".equalsIgnoreCase(feat.first().getName())
             && feat.first().getParameters() != null)
          {
            for(BaseEntry base : inItem.getBaseEntries())
            {
              if(base == null)
                continue;

              if(base.getName().equalsIgnoreCase(name.get()))
              {
                keyAbility = BaseMonster.Ability.DEXTERITY;
                break;
              }
            }
          }
      }

    ModifiedNumber modified =
      naturalAttack(keyAbility, inBaseAttack, true,
                    BaseMonster.AttackMode.WEAPON);

    for(Pair<Reference<BaseFeat>, List<String>> feat : allFeats())
    {
      Name name = (Name)feat.first().getParameters().getValue("Name");
      if(name != null)
        if("weapon focus".equalsIgnoreCase(feat.first().getName())
           && feat.first().getParameters() != null)
        {
          for(BaseEntry base : inItem.getBaseEntries())
          {
            if(base == null)
              continue;

            if(base.getName().equalsIgnoreCase(name.get()))
              modified.withModifier(new Modifier(1, Modifier.Type.GENERAL),
                                    "Weapn Focus");
          }
        }
    }

    // Magic weapon bonuses
    ModifiedNumber modifier = inItem.collect("attack").modifier();
    for(Map.Entry<String, Modifier> entry : modifier.getModifiers().entrySet())
      modified.withModifier(entry.getValue(), entry.getKey());

    return modified;
  }

  //........................................................................
  //----------------------------- naturalAttack ----------------------------

  /**
   * Compute the attack bonus with the given weapon.
   * TODO: implement this for nonweapons too (improvised weapons).
   *
   * @param       inKeyAbility  the key ability for the attack
   * @param       inBaseAttack  the base attack bonus
   * @param       inWithWeapon  whether to attack with a weapon
   * @param       inAttackMode  the mode of attack
   *
   * @return      the attack bonus for the first attack
   *
   */
  public ModifiedNumber naturalAttack(BaseMonster.Ability inKeyAbility,
                                      long inBaseAttack, boolean inWithWeapon,
                                      BaseMonster.AttackMode inAttackMode)
  {
    ModifiedNumber modified = new ModifiedNumber(inBaseAttack);

    int strength = ability(inKeyAbility).getMaxValue();

    int abilityModifier;
    if (inKeyAbility == BaseMonster.Ability.STRENGTH)
      abilityModifier = abilityModifier(strength);
    else
      abilityModifier = dexterityModifierForAC();

    modified.withModifier
      (new Modifier(abilityModifier, Modifier.Type.ABILITY),
       inKeyAbility.getShort() + " of " + strength);

    if(!inWithWeapon
       && (hasFeat("weapon focus")
           || hasFeat("weapon focus [name " + inAttackMode + "]")))
      modified.withModifier(new Modifier(+1, Modifier.Type.GENERAL),
                            "Weapon Focus");

    Size size = getSize();
    if(size != Size.MEDIUM)
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
  public List<Pair<Reference<BaseFeat>, List<String>>> allFeats()
  {
    Multimap<Reference<BaseFeat>, String> feats = HashMultimap.create();

    for(Reference<BaseFeat> feat : m_feats)
      feats.put(feat, getName());

    for(BaseEntry base : getBaseEntries())
      if(base instanceof BaseMonster)
        ((BaseMonster)base).collectFeats(feats);

    // we have to convert to a real map manuall, as soy can't handle the
    // multimap to map conversion
    List<Pair<Reference<BaseFeat>, List<String>>> result = Lists.newArrayList();
    for(Reference<BaseFeat> ref : feats.keySet())
    {
      List<String> names = Lists.newArrayList();
      for(String name : feats.get(ref))
        names.add(name);

      result.add(new Pair<Reference<BaseFeat>, List<String>>(ref, names));
    }

    return result;
  }

  //........................................................................
  //------------------------------ qualities -------------------------------

  /**
   * Collect all qualities used by this monster.
   *
   * @return  a list of references to all qualities
   */
  @SuppressWarnings("unchecked")
  public List<Reference<BaseQuality>> qualities()
  {
    Combined<ValueList<Multiple>> attacks = collect("special attacks");
    Combined<ValueList<Multiple>> qualities = collect("special qualities");

    List<Reference<BaseQuality>> result = Lists.newArrayList();

    for(ValueList<Multiple> qualityList : attacks.valuesOnly())
      for(Multiple quality : qualityList)
        result.add((Reference<BaseQuality>)quality.get(0));

    for(ValueList<Multiple> qualityList : qualities.valuesOnly())
      for(Multiple quality : qualityList)
        result.add((Reference<BaseQuality>)quality.get(0));

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
  public List<Map<String, Object>> specialAttackSummaries()
  {
    Combined<ValueList<Multiple>> attacks = collect("special attacks");

    Map<String, Reference<BaseQuality>> references = Maps.newHashMap();
    Map<String, Long> numbers = Maps.newHashMap();
    for(ValueList<Multiple> attackList : attacks.valuesOnly())
      for(Multiple attack : attackList)
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
    for(Map.Entry<String, Reference<BaseQuality>> entry
          : references.entrySet())
    {
      Parameters params = entry.getValue().getParameters();
      summaries.add
        (new ImmutableMap.Builder<String, Object>()
         .put("name", references.get(entry.getKey()).getName())
         .put("params", params.getSummary())
         .put("number", numbers.get(entry.getKey()))
         .put("summary", references.get(entry.getKey()).summary
              (ImmutableMap.<String, String>builder()
               .put("level",
                    "" + (params.hasValue("level")
                          && params.getValue("level").isDefined()
                          ? params.getValue("level") : getLevel()))
               .put("class", "" + getSpellClass())
               .put("str", "" + abilityModifier(BaseMonster.Ability.STRENGTH))
               .put("dex", "" + abilityModifier(BaseMonster.Ability.DEXTERITY))
               .put("con",
                    "" + abilityModifier(BaseMonster.Ability.CONSTITUTION))
               .put("int",
                    "" + abilityModifier(BaseMonster.Ability.INTELLIGENCE))
               .put("wis", "" + abilityModifier(BaseMonster.Ability.WISDOM))
               .put("cha", "" + abilityModifier(BaseMonster.Ability.CHARISMA))
               .put("ability",
                    "" + getSpellAbilityModifier(params.getValue("class")))
               .build()))
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
  public List<Map<String, Object>> specialQualitySummaries()
  {
    Combined<ValueList<Multiple>> qualities = collect("special qualities");

    List<Map<String, Object>> summaries = new ArrayList<Map<String, Object>>();
    for(ValueList<Multiple> list : qualities.valuesOnly())
      for(Multiple quality : list)
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
                                     "ability", "" + getSpellAbilityModifier
                                     (null))))
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
  public List<Map<String, Object>> allSkills()
  {
    List<Map<String, Object>> skills = Lists.newArrayList();
    Map<String, Map<Value<?>, ModifiedNumber>> ranks = skillRanks();

    for(BaseSkill skill
          : DMADataFactory.get().getEntries(BaseSkill.TYPE, null, 0, 1000))
    {
      Map<Value<?>, ModifiedNumber> perName = ranks.get(skill.getName());
      if(perName == null)
      {
        perName = Maps.newHashMap();
        perName.put(null, null);
      }

      for(Map.Entry<Value<?>, ModifiedNumber> entry : perName.entrySet())
      {
        ModifiedNumber modifier = entry.getValue();

        if((modifier == null || modifier.isZero()) && !skill.isUntrained())
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
              (new Modifier(abilityModifier(ability(ability)
                                            .getMinValue()),
                            Modifier.Type.ABILITY),
               skill.getAbility().toString());

        // Skill penalty from armor
        // TODO: must be implemented

        // Skill modifiers from items (and other modifiers)
        modifier.with(collect(skill.getName()).modifier());

        Map<String, Object> values = Maps.newHashMap();
        values.put("entry", skill);
        values.put("subtype", entry.getKey());
        values.put("modifier", modifier);

        skills.add(values);
      }
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
  public List<Reference<BaseQuality>> collectSpecialQualities()
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
  // public ModifiedNumber getFortitudeSave()
  // {
  //   return new ModifiedNumber(collectContributions("fortitude save"));
  // }

  //........................................................................
  //----------------------------- getReflexSave -----------------------------

  /**
   * Get the current reflex save.
   *
   * @return      the modifier for the save
   *
   */
  // public ModifiedNumber getReflexSave()
  // {
  //   return new ModifiedNumber(collectContributions("reflex save"));
  // }

  //........................................................................
  //------------------------------ getWillSave ------------------------------

  /**
   * Get the current will save.
   *
   * @return      the modifier for the save
   *
   */
  // public ModifiedNumber getWillSave()
  // {
  //   return new ModifiedNumber(collectContributions("will save"));
  // }

  //........................................................................
  //------------------------------- collect --------------------------------

  /**
   * Collect a combined value.
   *
   * @param       inName       the name of the value to combine
   * @param       ioCombined   the combined to collect into
   * @param       <T>          the type of value collected
   */
  @Override
  @SuppressWarnings("unchecked")
  protected <T extends Value<T>> void collect(String inName,
                                              Combined<T> ioCombined)
  {
    super.collect(inName, ioCombined);

    BaseMonster.Ability saveAbility = null;
    if("fortitude save".equals(inName))
      saveAbility = BaseMonster.Ability.CONSTITUTION;
    else if("reflex save".equals(inName))
      saveAbility = BaseMonster.Ability.DEXTERITY;
    else if("will save".equals(inName))
      saveAbility = BaseMonster.Ability.WISDOM;

    if(saveAbility != null)
    {
      int ability = ability(saveAbility).getMaxValue();
      if(ability >= 0)
        ioCombined.addModifier
          (new Modifier(abilityModifier(ability)), this,
                        saveAbility.getShort() + " of " + ability);
    }

    for(Name name : m_possessions)
    {
      Item item = getCampaign().getItem(name.get());
      if(item == null)
        continue;

      item.collect(inName, ioCombined);
    }

    for(Reference<BaseFeat> reference : m_feats)
    {
      BaseFeat feat = reference.getEntry();
      if(feat == null)
        continue;

      feat.collect(inName, ioCombined, reference.getParameters());
    }

    if("hit dice".equals(inName))
    {
      int constitution = getCombinedConstitution();
      if(constitution >= 0)
      {
        int bonus = abilityModifier(constitution);

        int level = getLevel();
        ioCombined.addModifier
          (new Modifier(level * bonus, Modifier.Type.GENERAL),
           this, "Con of " + constitution + " (" + (bonus > 0 ? "+" : "")
           + bonus + " and level " + level + ")");
      }
    }

    if("armor class".equals(inName))
    {
      // limit this with max dex of items
      int dexterity = ability(BaseMonster.Ability.DEXTERITY).getMaxValue();
      int modifier = dexterityModifierForAC();

      ioCombined.addModifier
        (new Modifier(modifier, Modifier.Type.ABILITY),
         this, "Dex of " + dexterity
         + (abilityModifier(dexterity) == modifier
            ? "" : " (capped for armor)"));

      Combined<Multiple> combinedSize = collect("size");
      Multiple minSize = combinedSize.min();
      if(minSize != null && minSize.isDefined())
      {
        Size size = ((EnumSelection<Size>)minSize.get(0)).getSelected();

        if(size.modifier() != 0)
          ioCombined.addModifier
            (new Modifier(size.modifier(), Modifier.Type.GENERAL), this,
             "size");

      }
    }
  }

  //........................................................................
  //----------------------- adjustDamageForStrength ------------------------

  /**
   * Adjust the given damage value for streangth.
   *
   * @param    inDamage    the damage to adjust
   * @param    inMelee     if the damage is for a melee attack
   * @param    inSecondary if the damage is for a secondary attack
   *
   * @return   the adjusted damage
   */
  public Damage adjustDamageForStrength(Damage inDamage, boolean inMelee,
                                        boolean inSecondary)
  {
    if(!inMelee)
      return inDamage;

    int modifier =
      abilityModifier(ability(BaseMonster.Ability.STRENGTH).getMaxValue());

    if(inSecondary)
      modifier /= 2;

    return inDamage.add(new Damage(new Dice(0, 1, modifier)));
  }

  //........................................................................
  //------------------------------- critical -------------------------------

  /**
   * Get the critical value for the given item.
   *
   * @param   inItem  the item to get the critical for
   *
   * @return  the critical for the item when the monster is using it
   *
   */
  public @Nullable Critical critical(@Nullable Item inItem)
  {
    boolean improvedCritical = hasFeat("improved critical");
    if(inItem == null)
      if(improvedCritical)
        return new Critical(19, 20, 2);
      else
        return null;

    improvedCritical =
      hasFeat("improved critical [name " + inItem.getName() + "]");

    Combined<Critical> combined = collect("critical");
    Critical critical = combined.max();
    if(critical == null)
      if(improvedCritical)
        return new Critical(19, 20, 2);
      else
        return null;

    if(improvedCritical)
      return critical.doubled();

    return critical;
  }

  //........................................................................
  //---------------------------- containedItems ----------------------------

  /**
   * Get all the items contained in this contents.
   *
   * @param       inDeep true for returning all item, including nested ones,
   *                     false for only the top level items
   * @return      a list with all the items
   *
   */
  public Map<String, Item> containedItems(boolean inDeep)
  {
    Map<String, Item> items = new HashMap<String, Item>();
    for(Name name : m_possessions)
    {
      Item item = getCampaign().getItem(name.get());
      if(item == null)
        continue;

      items.put(name.get(), item);
      items.putAll(item.containedItems(inDeep));
    }

    return items;
  }

  //........................................................................
  //------------------------- collectDependencies --------------------------

  /**
   * Collect the dependencies for this entry.
   *
   * @return      a list with all dependent entries
   *
   */
  @Override
  public Set<AbstractEntry> collectDependencies()
  {
    Set<AbstractEntry> entries = super.collectDependencies();

    // qualities
    for(Reference<BaseQuality> quality : qualities())
      if(quality.getEntry() != null)
        entries.add(quality.getEntry());

    // feats
    for(Pair<Reference<BaseFeat>, List<String>> feat : allFeats())
      if(feat.first().getEntry() != null)
        entries.add(feat.first().getEntry());

    // items
    for(Item item : containedItems(true).values())
    {
      entries.addAll(item.collectDependencies());
      entries.add(item);
    }

    return entries;
  }

  //........................................................................

  //------------------------------- getLevel -------------------------------

  /**
   * Get the level of the monster.
   *
   * @return      the monster's level
   */
  public int getLevel()
  {
    // Don't use 'hit dice' here, as this will in turn use level (for con).
    Combined<Number> combinedLevel = collect("level");

    return (int) combinedLevel.modifier().getMaxValue();
  }

  //........................................................................
  //---------------------------- getSpellClass -----------------------------

  /**
   * Get the spellcasting class.
   *
   * @return      the spellcasting class
   *
   */
  public BaseSpell.SpellClass getSpellClass()
  {
    return BaseSpell.SpellClass.SORCERER;
  }

  //........................................................................
  //----------------------- getSpellAbilityModifier ------------------------

  /**
   * The level of the spell, which has to be added separately.
   *
   * @param       inClassParam   the parameter for the class value
   *
   * @return      the modifier
   *
   */
  @SuppressWarnings("unchecked")
  public int getSpellAbilityModifier(@Nullable Value<?> inClassParam)
  {
    BaseMonster.Ability ability;
    BaseSpell.SpellClass spellClass;
    if(inClassParam != null && inClassParam.isDefined())
      spellClass = ((EnumSelection<BaseSpell.SpellClass>)inClassParam)
      .getSelected();
    else
      spellClass = getSpellClass();

    switch(spellClass)
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

    return abilityModifier(ability(ability).getMaxValue());
  }

  //........................................................................

  //------------------------------ hasQuality ------------------------------

  /**
   * Check if the monster has the name quality.
   *
   * @param    inName the name of the quality
   *
   * @return   true if the monster has the quality, false if not
   */
  public boolean hasQuality(String inName)
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
   * Check whether the monster has the name feat.
   *
   * @param    inName the name of the feat to check
   *
   * @return   true if the monster has the feat, false if not
   */
  public boolean hasFeat(String inName)
  {
    for(Reference<BaseFeat> feat : m_feats)
      if(feat.toString().equalsIgnoreCase(inName))
        return true;

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
  @SuppressWarnings("unchecked")
  public Map<String, Map<Value<?>, ModifiedNumber>> skillRanks()
  {
    Combined<ValueList<Multiple>> skills = collect("class skills");

    Map<String, Map<Value<?>, ModifiedNumber>> ranks = Maps.newHashMap();

    for(Pair<ValueList<Multiple>, List<Pair<ValueList<Multiple>, String>>> pair
          : skills.valuesWithDescriptions())
    {
      String group = "";
      for(Pair<ValueList<Multiple>, String> groupValue : pair.second())
        if(group.isEmpty())
          group = groupValue.second();
        else
          group += ", " + groupValue.second();

      for(Multiple skill : pair.first())
      {
        Reference<BaseSkill> ref = ((Reference<BaseSkill>)skill.get(0));
        String name = ref.getName();
        Value<?> type = ref.getParameters().getValue("subtype");
        Map<Value<?>, ModifiedNumber> perName = ranks.get(name);
        if(perName == null)
        {
          perName = Maps.newHashMap();
          ranks.put(name, perName);
        }

        ModifiedNumber number = perName.get(type);
        if(number == null)
          number = new ModifiedNumber(0, true);

        number.withModifier((Modifier)skill.get(1), group);

        perName.put(type, number);
      }
    }

    for(Multiple skill : m_skills)
    {
      Reference<BaseSkill> ref = ((Reference<BaseSkill>)skill.get(0));
      String name = ref.getName();
      Value<?> type = ref.getParameters().getValue("subtype");

      Map<Value<?>, ModifiedNumber> perName = ranks.get(name);
      if(perName == null)
      {
        perName = Maps.newHashMap();
        ranks.put(name, perName);
      }

      ModifiedNumber number = perName.get(type);
      if(number == null)
        number = new ModifiedNumber(0, true);

      number.withModifier(new Modifier((int)((Number)skill.get(1)).get()),
                          this.getName());
      perName.put(type, number);
    }

    return ranks;
  }

  //........................................................................
  //-------------------------------- getSize -------------------------------

  /**
   * Get the size of the monster.
   *
   * @return      the index in the size table.
   *
   */
  @SuppressWarnings("unchecked")
  public Size getSize()
  {
    Combined<Multiple> combined = collect("size");
    Multiple size = combined.min();
    if(size == null)
      return Size.MEDIUM;

    return ((EnumSelection<Size>)size.get(0)).getSelected();
  }

  //........................................................................
  //-------------------------------- dmName --------------------------------

  /**
   * Get the name a dm may see for this entry.
   *
   * @return      the name
   */
  public String dmName()
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

    return Strings.COMMA_JOINER.join(parts);
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
//       //                      "the monster has more maximal hit points than "
//       //                          + "the maximal "
//       //                          + (m_base.getMaxHP() + limits.second())));
//       //}

//       if(m_maxHP.get() < m_base.getMinHP())
//       {
//         result = false;
//         addError(new CheckError("monster.hit.points",
//                              "the monster has less maximal hit points than "
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
  // @SuppressWarnings("unchecked")
  // // TODO: split up this method and move the tag
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
//       // net.ixitxachitls.dma.values.Modifier.Type.DEFLECTION, "Incorporeal",
//       //    null));

//       //m_acTouch.addModifier
//       //  (new net.ixitxachitls.dma.values.Modifier
//       //   (Math.max(1, BaseMonster.abilityMod(m_base.m_charisma.get())),
//       // net.ixitxachitls.dma.values.Modifier.Type.DEFLECTION, "Incorporeal",
//       //    null));

//       //m_acFlatFooted.addModifier
//       //  (new net.ixitxachitls.dma.values.Modifier
//       //   (Math.max(1, BaseMonster.abilityMod(m_base.m_charisma.get())),
//       // net.ixitxachitls.dma.values.Modifier.Type.DEFLECTION, "Incorporeal",
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
    //   Quality quality =
    //   ((EntryValue<Quality>)value.get(0).getMutable()).get();

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
    //   Quality quality =
    //   ((EntryValue<Quality>)value.get(0).getMutable()).get();

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
  @Override
  public boolean add(CampaignEntry<? extends BaseEntry> inEntry)
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
  // public void addWillModifier(net.ixitxachitls.dma.values.Modifier
  // inModifier)
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
//                             net.ixitxachitls.dma.values.Modifier inModifier)
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

  @Override
  public Message toProto()
  {
    MonsterProto.Builder builder = MonsterProto.newBuilder();

    builder.setBase((CampaignEntryProto)super.toProto());

    if(m_possessions.isDefined())
      for(Name possession : m_possessions)
        builder.addPossession(possession.get());

    if(m_strength.isDefined())
      builder.setStrength((int)m_strength.get());

    if(m_dexterity.isDefined())
      builder.setDexterity((int)m_dexterity.get());

    if(m_constitution.isDefined())
      builder.setConstitution((int)m_constitution.get());

    if(m_intelligence.isDefined())
      builder.setIntelligence((int)m_intelligence.get());

    if(m_wisdom.isDefined())
      builder.setWisdom((int)m_wisdom.get());

    if(m_charisma.isDefined())
      builder.setCharisma((int)m_charisma.get());

    if(m_feats.isDefined())
      for(Reference<BaseFeat> feat : m_feats)
      {
        BaseMonsterProto.Reference.Builder reference =
          BaseMonsterProto.Reference.newBuilder();

        reference.setName(feat.getName());
        if(feat.getParameters() != null)
          reference.setParameters(feat.getParameters().toProto());

        builder.addFeat(reference);
      }

    if(m_maxHP.isDefined())
      builder.setMaxHitPoints((int)m_maxHP.get());

    if(m_hp.isDefined())
      builder.setHitPoints((int)m_hp.get());

    if(m_skills.isDefined())
      for(Multiple multiple : m_skills)
      {
        BaseMonsterProto.Reference.Builder reference =
          BaseMonsterProto.Reference.newBuilder();

        @SuppressWarnings("unchecked")
        Reference<BaseSkill> ref = (Reference<BaseSkill>)multiple.get(0);
        reference.setName(ref.getName());
        if(ref.getParameters() != null)
          reference.setParameters(ref.getParameters().toProto());

        MonsterProto.Skill.Builder skill = MonsterProto.Skill.newBuilder();
        skill.setSkill(reference.build());

        if(multiple.get(1).isDefined())
          skill.setRanks((int)((Number)multiple.get(1)).get());

        builder.addSkill(skill.build());
      }

    if(m_alignment.isDefined())
      builder.setAlignment(m_alignment.getSelected().toProto());

    if(m_fortitudeSave.isDefined())
      builder.setFortitudeSave((int)m_fortitudeSave.get());

    if(m_willSave.isDefined())
      builder.setWillSave((int)m_willSave.get());

    if(m_reflexSave.isDefined())
      builder.setReflexSave((int)m_reflexSave.get());

    MonsterProto proto = builder.build();
    return proto;
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof MonsterProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    MonsterProto proto = (MonsterProto)inProto;

    super.fromProto(proto.getBase());

    if(proto.getPossessionCount() > 0)
    {
      List<Name> possessions = new ArrayList<>();
      for(String possession : proto.getPossessionList())
        possessions.add(m_possessions.createElement().as(possession));

      m_possessions = m_possessions.as(possessions);
    }

    if(proto.hasStrength())
      m_strength = m_strength.as(proto.getStrength());

    if(proto.hasDexterity())
      m_dexterity = m_dexterity.as(proto.getDexterity());

    if(proto.hasConstitution())
      m_constitution = m_constitution.as(proto.getConstitution());

    if(proto.hasIntelligence())
      m_intelligence = m_intelligence.as(proto.getIntelligence());

    if(proto.hasWisdom())
      m_wisdom = m_wisdom.as(proto.getWisdom());

    if(proto.hasCharisma())
      m_charisma = m_charisma.as(proto.getCharisma());

    if(proto.getFeatCount() > 0)
    {
      List<Reference<BaseFeat>> references = new ArrayList<>();
      for(BaseMonsterProto.Reference feat : proto.getFeatList())
      {
        Reference<BaseFeat> ref = m_feats.createElement();
        ref = ref.as(feat.getName())
          .withParameters(ref.getParameters().fromProto(feat.getParameters()));
        references.add(ref);
      }

      m_feats = m_feats.as(references);
    }

    if(proto.hasMaxHitPoints())
      m_maxHP = m_maxHP.as(proto.getMaxHitPoints());

    if(proto.hasHitPoints())
      m_hp = m_hp.as(proto.getHitPoints());

    if(proto.getSkillCount() > 0)
    {
      List<Multiple> skills = new ArrayList<>();
      for(MonsterProto.Skill skill : proto.getSkillList())
      {
        Multiple multiple = m_skills.createElement();
        @SuppressWarnings("unchecked")
        Reference<BaseSkill> ref = (Reference<BaseSkill>)multiple.get(0);
        multiple =
          multiple.as(ref.as(skill.getSkill().getName())
                      .withParameters(ref.getParameters()
                                      .fromProto(skill.getSkill()
                                                 .getParameters())),
                      ((Number)multiple.get(1)).as(skill.getRanks()));

        skills.add(multiple);
      }

      m_skills = m_skills.as(skills);
    }

    if(proto.hasAlignment())
      m_alignment =
        m_alignment.as(BaseMonster.Alignment.fromProto(proto.getAlignment()));

    if(proto.hasFortitudeSave())
      m_fortitudeSave = m_fortitudeSave.as(proto.getFortitudeSave());

    if(proto.hasWillSave())
      m_willSave = m_willSave.as(proto.getWillSave());

    if(proto.hasReflexSave())
      m_reflexSave = m_reflexSave.as(proto.getReflexSave());
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(MonsterProto.parseFrom(inBytes));
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

    // /** Test text for item. */
    // private static String s_text =
    //   "monster Achaierai = \n"
    //   + "\n"
    //   + "  hp    45;\n"
    //   + "  money 333 gp.\n";

    // /** Test text for first base item. */
    // private static String s_base =
    //   "base monster Achaierai = \n"
    //   + "\n"
    //   + "  size              Large (Tall);\n"
    //   + "  type              Outsider (Evil, Extraplanar, Lawful);\n"
    //   + "  hit dice          6d8;\n"
    //   + "  speed             50 ft;\n"
    //   + "  natural armor     +10;\n"
    //   + "  base attack       +6;\n"
    //   + "  primary attacks   2 claw melee (2d6);\n"
    //   + "  secondary attacks bite melee (4d6);\n"
    //   + "  special attacks   Black cloud;\n"
    // + "  special qualities Darkvision [Range 60 ft], No Soul, Does Not Eat, "
    // + "Does Not Sleep, Affected As Evil, Weapons Evil, Affected as Lawful, "
    //   + "Weapons Lawful;\n"
    //   + "  strength          19;\n"
    //   + "  dexterity         13;\n"
    //   + "  constitution      14;\n"
    //   + "  intelligence      11;\n"
    //   + "  wisdom            14;\n"
    //   + "  charisma          16;\n"
    //   + "  class skills      Balance +9, Climb +9, Diplomacy, Hide +9, "
    //   + "Jump +9, Listen +9, Move Silently +9, Sense Motive +9, Spot +9;\n"
    //   + "  feats             Dodge, Mobility, Spring Attack;\n"
    //   + "  environment       Infernal Battlefield of Acheron;\n"
    //   + "  organization      Solitary, flock (1d4+4);\n"
    //   + "  challenge rating  5;\n"
    //   + "  treasure          Double standard;\n"
    //   + "  alignment         Always lawful evil;\n"
    //   + "  advancement       7-12 HD (Large), 13-18 HD (Huge);\n"
    //   + "  level adjustment  -;\n"
    //   + "  encounter         \"encounter\";\n"
    //   + "  combat            \"combat\";\n"
    //   + "  languages         Infernal;\n"
    //   + "  tactics           \"tactics\";\n"
    //   + "  character         \"character\";\n"
    //   + "  reproduction      \"reproduction\";\n"
    //   + "  short description \"short\";\n"
    //   + "  world             generic;\n"
    //   + "  references        \"WTC 17755\" 9-10;\n"
    //   + "  description       \"description\".\n";

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
  }

  //........................................................................
}
