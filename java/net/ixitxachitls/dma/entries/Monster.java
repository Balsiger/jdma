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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.CampaignEntryProto;
import net.ixitxachitls.dma.proto.Entries.FeatProto;
import net.ixitxachitls.dma.proto.Entries.MonsterProto;
import net.ixitxachitls.dma.proto.Entries.QualityProto;
import net.ixitxachitls.dma.proto.Entries.SkillProto;
import net.ixitxachitls.dma.values.Annotated;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.Money;
import net.ixitxachitls.dma.values.NewModifier;
import net.ixitxachitls.dma.values.NewValue;
import net.ixitxachitls.dma.values.Size;
import net.ixitxachitls.dma.values.Speed;
import net.ixitxachitls.dma.values.enums.Alignment;
import net.ixitxachitls.util.logging.Log;

/**
 * This is a real monster.
 *
 * @file          Monster.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
public class Monster extends CampaignEntry
{
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

  /**
   * This is the internal, default constructor.
   */
  protected Monster()
  {
    super(TYPE);
  }

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base item
   */
  public Monster(Campaign inCampaign)
  {
    super(TYPE, inCampaign);
  }

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

   /** The type of this entry. */
   public static final Type<Monster> TYPE =
     new Type<Monster>(Monster.class, BaseMonster.TYPE);

   /** The type of the base entry to this entry. */
   public static final BaseType<BaseMonster> BASE_TYPE = BaseMonster.TYPE;

  /** The possessions value. */
  protected List<Item> m_possessions = null;

  /** The monster's Strength. */
  protected Optional<Integer> m_strength = Optional.absent();

  /** The monster's Dexterity. */
  protected Optional<Integer> m_dexterity = Optional.absent();

  /** The monster's Constitution. */
  protected Optional<Integer> m_constitution = Optional.absent();

  /** The monster's Intelligence. */
  protected Optional<Integer>m_intelligence = Optional.absent();;

  /** The monster's Wisdom. */
  protected Optional<Integer> m_wisdom = Optional.absent();

  /** The monster's Charisma. */
  protected Optional<Integer> m_charisma = Optional.absent();

  /** The feats. */
  protected List<Feat> m_feats = new ArrayList<>();

  /** The actual maximal number of hit points the monster can have. */
  protected int m_maxHP = 0;

  /** The actual number of hit points the monster currently has. */
  protected int m_hp = 0;

  /** The skills, in addition to what we find in base. */
  protected List<Skill> m_skills = new ArrayList<>();

  /** The monster's alignment. */
  protected Alignment m_alignment = Alignment.UNKNOWN;

  /** The monster's fortitude save. */
  protected Optional<Integer> m_fortitudeSave = Optional.absent();

  /** The monster's will save. */
  protected Optional<Integer> m_willSave = Optional.absent();

  /** The monster's reflex. */
  protected Optional<Integer> m_reflexSave = Optional.absent();

  /** The qualities. */
  protected List<Quality> m_qualities = new ArrayList<>();

  /** The monetary treasure. */
  // protected Money m_money = new Money();

  /** The random generator. */
  // private static Random s_random = new Random();

  /** The treasures per level. */
  private static List<Treasure> s_treasures = new ArrayList<Treasure>();

  //----- treasure definition ----------------------------------------------

  static
    // 0
  {
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

  /**
   * Compute the ability modifier for the given score.
   *
   * @param       inScore the ability score to compute for
   *
   * @return      the compute modifier
   */
  public int abilityModifier(long inScore)
  {
    if(inScore < 0)
      return 0;

    return (int) (inScore / 2) - 5;
  }

  public List<Item> getWeapons()
  {
    List<Item> weapons = new ArrayList<>();
    for(Item item : getPossessions())
      if(item != null && item.isWeapon())
        weapons.add(item);

    return weapons;
  }

  public List<Item> getArmor()
  {
    List<Item> armor = new ArrayList<>();
    for(Item item : getPossessions())
      if(item != null && item.isArmor() && !item.getArmorType().isShield())
        armor.add(item);

    return armor;
  }

  public List<Item> getPossessions()
  {
    if(m_possessions == null)
      m_possessions =
        DMADataFactory.get().getEntries(Item.TYPE,
                                         getCampaign().get().getKey(),
                                         "index-parent",
                                         "monster/" + getName().toLowerCase());

    return Collections.unmodifiableList(m_possessions);
  }

  public List<Feat> getFeats()
  {
    return Collections.unmodifiableList(m_feats);
  }

  public List<Quality> getQualities()
  {
    return Collections.unmodifiableList(m_qualities);
  }

  public Optional<Integer> getStrength()
  {
    return m_strength;
  }

  /**
   * Get the strength score of the monster.
   *
   * @return      the constitution score
   */
  public Annotated<Optional<Integer>> getCombinedStrength()
  {
    Optional<Integer> strength = getStrength();
    if(strength.isPresent())
      return new Annotated.Integer(strength.get(), getName());

    Annotated<Optional<Integer>> combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedStrength());

    return combined;
  }

  public int getStrengthModifier()
  {
    Optional<Integer> strength = getCombinedStrength().get();
    if(!strength.isPresent())
      return 0;

    return abilityModifier(strength.get());
  }

  public Optional<Integer> getConstitution()
  {
    return m_constitution;
  }

  public Annotated<Optional<Integer>> getCombinedConstitution()
  {
    Optional<Integer> constitution = getConstitution();
    if(constitution.isPresent())
      return new Annotated.Integer(constitution.get(), getName());

    Annotated<Optional<Integer>> combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedConstitution());

    return combined;
  }


  public int getConstitutionModifier()
  {
    Optional<Integer> constitution = getCombinedCharisma().get();
    if(!constitution.isPresent())
      return 0;

    return abilityModifier(constitution.get());
  }

  public Optional<Integer> getDexterity()
  {
    return m_dexterity;
  }

  public Annotated<Optional<Integer>> getCombinedDexterity()
  {
    Optional<Integer> dexterity = getDexterity();
    if(dexterity.isPresent())
      return new Annotated.Integer(dexterity.get(), getName());

    Annotated<Optional<Integer>> combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedDexterity());

    return combined;
  }

  public int getDexterityModifier()
  {
    Optional<Integer> dexterity = getCombinedDexterity().get();
    if(!dexterity.isPresent())
      return 0;

    return abilityModifier(dexterity.get());
  }

  public Optional<Integer> getIntelligence()
  {
    return m_intelligence;
  }

  public Annotated<Optional<Integer>> getCombinedIntelligence()
  {
    Optional<Integer> intelligence = getIntelligence();
    if(intelligence.isPresent())
      return new Annotated.Integer(intelligence.get(), getName());

    Annotated<Optional<Integer>> combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedIntelligence());

    return combined;
  }

  public int getIntelligenceModifier()
  {
    Optional<Integer> intelligence = getCombinedBaseAttack().get();
    if(!intelligence.isPresent())
      return 0;

    return abilityModifier(intelligence.get());
  }

  public Optional<Integer> getWisdom()
  {
    return m_wisdom;
  }

  public Annotated<Optional<Integer>> getCombinedWisdom()
  {
    Optional<Integer> wisdom = getWisdom();
    if(wisdom.isPresent())
      return new Annotated.Integer(wisdom.get(), getName());

    Annotated<Optional<Integer>> combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedWisdom());

    return combined;
  }

  public int getWisdomModifier()
  {
    Optional<Integer> wisdom = getCombinedWisdom().get();
    if(!wisdom.isPresent())
      return 0;

    return abilityModifier(wisdom.get());
  }

  public Optional<Integer> getCharisma()
  {
    return m_charisma;
  }

  public Annotated<Optional<Integer>> getCombinedCharisma()
  {
    Optional<Integer> charisma = getCharisma();
    if(charisma.isPresent())
      return new Annotated.Integer(charisma.get(), getName());

    Annotated<Optional<Integer>> combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedCharisma());

    return combined;
  }

  public int getCharismaModifier()
  {
    Optional<Integer> charisma = getCombinedCharisma().get();
    if(!charisma.isPresent())
      return 0;

    return abilityModifier(charisma.get());
  }

  public Combination<Integer> getCombinedLevelAdjustment()
  {
    List<Combination<Integer>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseMonster)entry).getCombinedLevelAdjustment());

    return new Combination.Integer(this, combinations);
  }

  public Annotated<Optional<Size>> getCombinedSize()
  {
    Annotated<Optional<Size>> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedSize());

    return combined;
  }

  public Annotated.Bonus getCombinedBaseAttack()
  {
    Annotated.Bonus combined = new Annotated.Bonus();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedBaseAttack());

    return combined;
  }

  public Alignment getAlignment()
  {
    return m_alignment;
  }

  public int getMaxHP()
  {
    return m_maxHP;
  }

  public int getHP()
  {
    return m_hp;
  }

  public Optional<Annotated.Arithmetic<Speed>>
    getSpeedAnnotated(MovementMode inMode)
  {
    Annotated.Arithmetic<Speed> speed = null;
    for(BaseEntry base : getBaseEntries())
    {
      Optional<Annotated.Arithmetic<Speed>> baseSpeed =
        ((BaseMonster)base).getSpeedAnnotated(inMode);
      if(baseSpeed.isPresent())
        if(speed == null || !speed.get().isPresent()
           || speed.get().get().getSpeed().compareTo
               (baseSpeed.get().get().get().getSpeed()) < 0)
          speed = baseSpeed.get();
    }

    for(Quality quality : m_qualities)
    {
      Optional<Speed> qualitySpeed = quality.getSpeed(inMode);
      if(qualitySpeed.isPresent())
        if(speed == null)
          speed = new Annotated.Arithmetic<Speed>(qualitySpeed.get(),
                                                  quality.getName());
        else
          speed.add(qualitySpeed.get(), quality.getName());
    }

    return Optional.fromNullable(speed);
  }

  public List<Annotated.Arithmetic<Speed>> getSpeedsAnnotated()
  {
    List<Annotated.Arithmetic<Speed>> speeds = new ArrayList<>();

    for(MovementMode mode : MovementMode.values())
    {
      Optional<Annotated.Arithmetic<Speed>> speed = getSpeedAnnotated(mode);
      if(speed.isPresent())
        speeds.add(speed.get());
    }

    return speeds;

  }

  public int getInitiative()
  {
    return getDexterityModifier();
  }

  public int getGrappleBonus()
  {
    Optional<Integer> base = getCombinedBaseAttack().get();
    if(!base.isPresent())
      return getStrengthModifier();

    return base.get() + getStrengthModifier();
  }

  public Optional<Integer> getFortitudeSave()
  {
    return m_fortitudeSave;
  }

  public Annotated.Bonus getCombinedBaseFortitudeSave()
  {
    if(m_fortitudeSave.isPresent())
      return new Annotated.Bonus(m_fortitudeSave.get(), getName());

    Annotated.Bonus combined = new Annotated.Bonus();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedFortitudeSave());

    return combined;
  }

  public Annotated.Bonus getCombinedFortitudeSave()
  {
    Annotated.Bonus save = getCombinedBaseFortitudeSave();
    save.add(getConstitutionModifier(), "Constitution");

    return save;
  }

  public Annotated.Bonus getCombinedBaseReflexSave()
  {
    if(m_reflexSave.isPresent())
      return new Annotated.Bonus(m_reflexSave.get(), getName());

    Annotated.Bonus combined = new Annotated.Bonus();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedReflexSave());

    return combined;
  }

  public Annotated.Bonus getCombinedReflexSave()
  {
    Annotated.Bonus save = getCombinedBaseReflexSave();
    save.add(getDexterityModifier(), "Dexterity");

    return save;
  }

  public Annotated.Bonus getCombinedBaseWillSave()
  {
    if(m_willSave.isPresent())
      return new Annotated.Bonus(m_willSave.get(), getName());

    Annotated.Bonus combined = new Annotated.Bonus();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedWillSave());

    return combined;
  }

  public Annotated.Bonus getCombinedWillSave()
  {
    Annotated.Bonus save = getCombinedBaseWillSave();
    save.add(getWisdomModifier(), "Wisdom");

    return save;
  }

  public Annotated.Arithmetic<NewModifier> getCombinedNaturalArmor()
  {
    Annotated.Arithmetic<NewModifier> combined = new Annotated.Arithmetic<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedNaturalArmor());

    return combined;
  }

  public NewModifier getArmorBonus()
  {
    NewModifier bonus = null;
    for(Item armor : getArmor())
    {
      if(armor.getArmorType().isShield())
        continue;

      Optional<NewModifier> modifier = armor.getArmorClass();
      if(modifier.isPresent())
        if(bonus == null)
          bonus = modifier.get();
        else
          bonus = (NewModifier) bonus.add(modifier.get());
    }

    if(bonus == null)
      return new NewModifier();

    return bonus;
  }

  public NewModifier getShieldBonus()
  {
    NewModifier bonus = null;
    for(Item armor : getArmor())
    {
      if(!armor.getArmorType().isShield())
        continue;

      Optional<NewModifier> modifier = armor.getArmorClass();
      if(modifier.isPresent())
        if(bonus == null)
          bonus = modifier.get();
        else
          bonus = (NewModifier) bonus.add(modifier.get());
    }

    if(bonus == null)
      return new NewModifier();

    return bonus;
  }

  public int getArmorClass()
  {
    NewModifier armor = getArmorBonus();
    NewModifier shield = getShieldBonus();
    int dexterity = getDexterityModifier();

    return 10 + armor.getModifier() + shield.getModifier() + dexterity;
  }

  public int getTouchArmorClass()
  {
    int dexterity = getDexterityModifier();

    return 10 + dexterity;
  }

  public int getFlatFootedArmorClass()
  {
    NewModifier armor = getArmorBonus();
    NewModifier shield = getShieldBonus();

    return 10 + armor.getModifier() + shield.getModifier();
  }

  public Optional<Item> getArmorWorn()
  {
    List<Item> armor = getArmor();
    for(Item item : armor)
      if(item.isArmor() && !item.getArmorType().isShield())
        return Optional.of(item);

    return Optional.absent();
  }

  public Optional<Item> getShieldWorn()
  {
    List<Item> armor = getArmor();
    for(Item item : armor)
      if(item.isArmor() && item.getArmorType().isShield())
        return Optional.of(item);

    return Optional.absent();
  }

  @Override
  public void set(Values inValues)
  {
    super.set(inValues);

    m_alignment = inValues.use("alignment", m_alignment, Alignment.PARSER);
    m_strength = inValues.use("strength", m_strength, NewValue.INTEGER_PARSER);
    m_constitution = inValues.use("constitution", m_constitution,
                                  NewValue.INTEGER_PARSER);
    m_dexterity = inValues.use("dexterity", m_dexterity,
                               NewValue.INTEGER_PARSER);
    m_intelligence = inValues.use("intelligence", m_intelligence,
                                  NewValue.INTEGER_PARSER);
    m_wisdom = inValues.use("wisdom", m_wisdom, NewValue.INTEGER_PARSER);
    m_charisma = inValues.use("charisma", m_charisma, NewValue.INTEGER_PARSER);
    m_feats = inValues.useEntries("feat", m_feats,
                                  new NestedEntry.Creator<Feat>()
                                  {
                                    @Override
                                    public Feat create()
                                    {
                                      return new Feat();
                                    }
                                  });
    m_hp = inValues.use("hp", m_hp, NewValue.INTEGER_PARSER);
    m_qualities = inValues.useEntries("quality", m_qualities,
                                      new NestedEntry.Creator<Quality>()
                                      {
                                        @Override
                                        public Quality create()
                                        {
                                          return new Quality();
                                        }
                                      });
  }

  /**
   * Get the initiative of the monster.
   *
   * @return      the initiative as a combination
   */
  /*
  public ModifiedNumber getInitiative()
  {
    ModifiedNumber initiative = collect("initiative").modifier();

    int dexterity = ability(Ability.DEXTERITY).getMaxValue();
    initiative.withModifier
      (new Modifier(abilityModifier(dexterity), Modifier.Type.ABILITY),
       "Dex of " + dexterity);

    return initiative;
  }
  */

  /**
   * Get the armor class for touch attacks of the monster.
   *
   * @return      the armor class
   */
  /*
  public ModifiedNumber armorClassTouch()
  {
    return armorClass().modifier().ignore(Modifier.Type.ARMOR,
                                          Modifier.Type.NATURAL_ARMOR,
                                          Modifier.Type.SHIELD,
                                          Modifier.Type.ENHANCEMENT);
  }
  */

  /**
   * Get the armor class of the monster.
   *
   * @return      the armor class
   */
  /*
  public Combined<Modifier> armorClass()
  {
    Combined<Modifier> armor = collect("armor class");
    armor.addModifier(new Modifier(10), this, "base");
    Combined<Modifier> naturalArmor = collect("natural armor");
    armor.add(naturalArmor, "natural armor");

    return armor;
  }
  */

  /**
   * Get the armor class of the monster when its flat-footed.
   *
   * @return      the armor class when flat-footed
   */
  /*
  public ModifiedNumber armorClassFlatFooted()
  {
    ModifiedNumber armor = armorClass().modifier();

    if(hasQuality("uncanny dodge"))
      return armor;

    return armor.ignore(Modifier.Type.ABILITY);
  }
  */

  /**
   * Get the current modifier from dexterity. This is capped by the armor's
   * maximum dexterity.
   *
   * @return      the current dexterity modifier
   */
  /*
  public int dexterityModifierForAC()
  {
    int dex =
      abilityModifier(ability(Ability.DEXTERITY).getMaxValue());

    // TODO: we should actually only consider worn items.
    if(getCampaign().isPresent())
      for(Name name : m_possessions)
      {
        Item item = getCampaign().get().getItem(name.get());
        if(item == null)
          continue;

        Combined<Number> combinedMaxDex = item.collect("max dexterity");
        Number maxDex = combinedMaxDex.min();
        if(maxDex != null && maxDex.isDefined() && maxDex.get() < dex)
          dex = (int)maxDex.get();
      }

    return dex;
  }
  */

  /**
   * Compute the values to render for attacks.
   *
   * @return  a map with the following values:
   *   base attacks - a list with the base attack values
   */
  /*
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
    if(getCampaign().isPresent())
      for(Name name : m_possessions)
      {
        Item item = getCampaign().get().getItem(name.get());
        if(item == null)
          continue;

        //if(!item.hasExtension(Weapon.class))
        //  continue;

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
  */

  /**
   * Compute the attacks the monster can do.
   *
   * @param       inName         the name of the attack made
   * @param       inSecondary    whether this is a secondary attack or not
   * @param       inBaseAttacks  the list of base attack modifiers
   *
   * @return      a list with maps containing the attack data
   */
  /*
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
        AttackMode attackMode =
          ((EnumSelection<AttackMode>)attack.get(1)).getSelected();

        if(attackMode == AttackMode.WEAPON)
          continue;

        boolean melee =
          ((EnumSelection<AttackStyle>)
           attack.get(2)).getSelected() == AttackStyle.MELEE;

        Ability keyAbility;
        if(weaponFinesse || !melee || attackMode.useDexterity())
          keyAbility = Ability.DEXTERITY;
        else
          keyAbility = Ability.STRENGTH;

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
  */

  /**
   * Compute the grapple check.
   *
   * @return      the modified number with the grapple check.
   */
  /*
  @SuppressWarnings("unchecked")
  public ModifiedNumber grapple()
  {
    long baseAttack = collect("base attack").getMaxValue();
    ModifiedNumber grapple = new ModifiedNumber(0);
    grapple.withModifier(new Modifier((int)baseAttack), "base attack");
    int strength = ability(Ability.STRENGTH).getMaxValue();
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
  */

  /**
   * Compute the attack bonus with the given weapon.
   * TODO: implement this for nonweapons too (improvised weapons).
   *
   * @param       inItem       the weapon to attack with
   * @param       inBaseAttack the base attack bonus
   *
   * @return      the attack bonus for the first attack
   */
  /*
  public ModifiedNumber weaponAttack(Item inItem, long inBaseAttack)
  {
    // Strength bonus (or dexterity)
    Ability keyAbility = Ability.STRENGTH;
    Combined<EnumSelection<WeaponStyle>> weaponStyle =
      collect("weapon style");
    EnumSelection<WeaponStyle> weaponStyleMin = weaponStyle.min();
    if(weaponStyleMin != null && !weaponStyle.min().getSelected().isMelee())
      keyAbility = Ability.DEXTERITY;
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
                keyAbility = Ability.DEXTERITY;
                break;
              }
            }
          }
      }

    ModifiedNumber modified =
      naturalAttack(keyAbility, inBaseAttack, true,
                    AttackMode.WEAPON);

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
  */

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
   */
  /*
  public ModifiedNumber naturalAttack(Ability inKeyAbility,
                                      long inBaseAttack, boolean inWithWeapon,
                                      AttackMode inAttackMode)
  {
    ModifiedNumber modified = new ModifiedNumber(inBaseAttack);

    int strength = ability(inKeyAbility).getMaxValue();

    int abilityModifier;
    if (inKeyAbility == Ability.STRENGTH)
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
  */

  /**
   * Get all the feats the monster has.
   *
   * @return      a map of feats to the names they were found in
   */
  /*
  public List<Pair<Reference<BaseFeat>, List<String>>> allFeats()
  {
    Multimap<Reference<BaseFeat>, String> feats = HashMultimap.create();

    for(Reference<BaseFeat> feat : m_feats)
      feats.put(feat, getName());

    / *
    for(BaseEntry base : getBaseEntries())
      if(base instanceof BaseMonster)
        ((BaseMonster)base).collectFeats(feats);
    * /

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
  */

  /**
   * Collect all qualities used by this monster.
   *
   * @return  a list of references to all qualities
   */
  /*
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
  */

  /**
   * Get the summaries for special attacks.
   *
   * @return      a map with the value for the summary
   */
  /*
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
               .put("str", "" + abilityModifier(Ability.STRENGTH))
               .put("dex", "" + abilityModifier(Ability.DEXTERITY))
               .put("con",
                    "" + abilityModifier(Ability.CONSTITUTION))
               .put("int",
                    "" + abilityModifier(Ability.INTELLIGENCE))
               .put("wis", "" + abilityModifier(Ability.WISDOM))
               .put("cha", "" + abilityModifier(Ability.CHARISMA))
               .put("ability",
                    "" + getSpellAbilityModifier(params.getValue("class")))
               .build()))
         .build());
    }

    return summaries;
  }
  */

  /**
   * Get the summaries for special qualities.
   *
   * @return      a map with the value for the summary
   */
  /*
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
  */

  /**
   * Get information about the current skills of the monster. Skills that can
   * only be used trained for which the monster has no ranks are not returned.
   *
   * @return      the skills information
   */
  /*
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
        Ability ability = skill.getAbility();
        if(ability != null)
          if(ability == Ability.DEXTERITY)
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
  */

  /**
   * Get the special qualities for this and all base monsters.
   *
   * @return  a list of base qualities
   */
  /*
  public List<Reference<BaseQuality>> collectSpecialQualities()
  {
    List<Reference<BaseQuality>> qualities = Lists.newArrayList();

    for(BaseEntry base : getBaseEntries())
    {
      if(!(base instanceof BaseMonster))
        continue;

      / *
      qualities.addAll(((BaseMonster)base).collectSpecialQualities());
      * /
    }

    return qualities;
  }
  */

  /**
   * Collect a combined value.
   *
   * @param       inName       the name of the value to combine
   * @param       ioCombined   the combined to collect into
   * @param       <T>          the type of value collected
   */
  /*
  @Override
  @SuppressWarnings("unchecked")
  protected <T extends Value<T>> void collect(String inName,
                                              Combined<T> ioCombined)
  {
    super.collect(inName, ioCombined);

    Ability saveAbility = null;
    if("fortitude save".equals(inName))
      saveAbility = Ability.CONSTITUTION;
    else if("reflex save".equals(inName))
      saveAbility = Ability.DEXTERITY;
    else if("will save".equals(inName))
      saveAbility = Ability.WISDOM;

    if(saveAbility != null)
    {
      int ability = ability(saveAbility).getMaxValue();
      if(ability >= 0)
        ioCombined.addModifier
          (new Modifier(abilityModifier(ability)), this,
                        saveAbility.getShort() + " of " + ability);
    }

    if(getCampaign().isPresent())
      for(Name name : m_possessions)
      {
        Item item = getCampaign().get().getItem(name.get());
        if(item == null)
          continue;

        item.collect(inName, ioCombined);
      }

//    for(Reference<BaseFeat> reference : m_feats)
//    {
//      BaseFeat feat = reference.getEntry();
//      if(feat == null)
//        continue;
//
//      feat.collect(inName, ioCombined, reference.getParameters());
//    }

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
      int dexterity = ability(Ability.DEXTERITY).getMaxValue();
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
  */

  /**
   * Adjust the given damage value for streangth.
   *
   * @param    inDamage    the damage to adjust
   * @param    inMelee     if the damage is for a melee attack
   * @param    inSecondary if the damage is for a secondary attack
   *
   * @return   the adjusted damage
   */
  /*
  public Damage adjustDamageForStrength(Damage inDamage, boolean inMelee,
                                        boolean inSecondary)
  {
    if(!inMelee)
      return inDamage;

    int modifier =
      abilityModifier(ability(Ability.STRENGTH).getMaxValue());

    if(inSecondary)
      modifier /= 2;

    return inDamage.add(new Damage(new Dice(0, 1, modifier)));
  }
  */

  /**
   * Get the critical value for the given item.
   *
   * @param   inItem  the item to get the critical for
   *
   * @return  the critical for the item when the monster is using it
   */
  /*
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
  */

  /**
   * Get all the items contained in this contents.
   *
   * @param       inDeep true for returning all item, including nested ones,
   *                     false for only the top level items
   * @return      a list with all the items
   */
  /*
  public Map<String, Item> containedItems(boolean inDeep)
  {
    Map<String, Item> items = new HashMap<String, Item>();
    if(getCampaign().isPresent())
      for(Name name : m_possessions)
      {
        Item item = getCampaign().get().getItem(name.get());
        if(item == null)
          continue;

        items.put(name.get(), item);
        //items.putAll(item.containedItems(inDeep));
      }

    return items;
  }
  */

  /**
   * Collect the dependencies for this entry.
   *
   * @return      a list with all dependent entries
   */
  /*
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
  */

  /**
   * Get the level of the monster.
   *
   * @return      the monster's level
   */
  /*
  public int getLevel()
  {
    // Don't use 'hit dice' here, as this will in turn use level (for con).
    Combined<Number> combinedLevel = collect("level");

    return (int) combinedLevel.modifier().getMaxValue();
  }
  */

  /**
   * Get the spellcasting class.
   *
   * @return      the spellcasting class
   */
  /*
  public SpellClass getSpellClass()
  {
    return SpellClass.SORCERER;
  }
  */

  /**
   * The level of the spell, which has to be added separately.
   *
   * @param       inClassParam   the parameter for the class value
   *
   * @return      the modifier
   */
  /*
  @SuppressWarnings("unchecked")
  public int getSpellAbilityModifier(@Nullable Value<?> inClassParam)
  {
    Ability ability;
    SpellClass spellClass;
    if(inClassParam != null && inClassParam.isDefined())
      spellClass = ((EnumSelection<SpellClass>)inClassParam)
      .getSelected();
    else
      spellClass = getSpellClass();

    switch(spellClass)
    {
      case WIZARD:
        ability = Ability.INTELLIGENCE;
        break;

      case CLERIC:
      case PALADIN:
      case RANGER:
      case DRUID:
        ability = Ability.WISDOM;
        break;

      case SORCERER:
      case BARD:
      default:
        ability = Ability.CHARISMA;
    }

    return abilityModifier(ability(ability).getMaxValue());
  }
  */

  /**
   * Check if the monster has the name quality.
   *
   * @param    inName the name of the quality
   *
   * @return   true if the monster has the quality, false if not
   */
  /*
  public boolean hasQuality(String inName)
  {
    / *
    for(BaseEntry base : getBaseEntries())
      if(base instanceof BaseMonster)
        if(((BaseMonster)base).hasQuality(inName))
          return true;
    * /

    return false;
  }
  */

  /**
   * Check whether the monster has the name feat.
   *
   * @param    inName the name of the feat to check
   *
   * @return   true if the monster has the feat, false if not
   */
  public boolean hasFeat(String inName)
  {
    for(Feat feat : m_feats)
      if(feat.getName().equalsIgnoreCase(inName))
        return true;

    return false;
  }

  public Optional<Feat> getFeat(String inName)
  {
    for(Feat feat : m_feats)
      if(feat.getName().equalsIgnoreCase(inName))
        return Optional.of(feat);

    return Optional.absent();
  }

  public boolean hasQuality(String inName)
  {
    for(Quality quality : m_qualities)
      if(quality.getName().equalsIgnoreCase(inName))
        return true;

    return false;
  }

  public Optional<Quality> getQuality(String inName)
  {
    for(Quality quality : m_qualities)
      if(quality.getName().equalsIgnoreCase(inName))
        return Optional.of(quality);

    return Optional.absent();
  }

  /**
   * Compute the base saving throw for a bad save.
   *
   * @param       inLevel the level or hit dice to compute for
   *
   * @return      the base value for a save
   */
  public static int badSave(int inLevel)
  {
    return inLevel / 3;
  }

  /**
   * Compute the base saving throw for a good save.
   *
   * @param       inLevel the level or hit dice to compute for
   *
   * @return      the base value for a save
   */
  public static int goodSave(int inLevel)
  {
    return inLevel / 2 + 2;
  }

  /**
   * Get the number of skill ranks for all skills with ranks.
   *
   * @return      the number of skill ranks per skill name
   */
  /*
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
  */

  /**
   * Get the size of the monster.
   *
   * @return      the index in the size table.
   */
  /*
  @SuppressWarnings("unchecked")
  public Size getSize()
  {
    Combined<Multiple> combined = collect("size");
    Multiple size = combined.min();
    if(size == null)
      return Size.MEDIUM;

    return ((EnumSelection<Size>)size.get(0)).getSelected();
  }
  */

  /**
   * Get the name a dm may see for this entry.
   *
   * @return      the name
   */
  /*
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
  */

  /**
   * Get the skill check penalty for armor this monster is wearing.
   *
   * @return      the penalty, or 0 for none
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

  // }

  /**
   * Add the given entry to the campaign entry.
   *
   * @param       inEntry the entry to add
   *
   * @return      true if added, false if not
   */
  /*
  @Override
  public boolean add(CampaignEntry inEntry)
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
  */

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

  @Override
  public Message toProto()
  {
    MonsterProto.Builder builder = MonsterProto.newBuilder();

    builder.setBase((CampaignEntryProto)super.toProto());

    if(m_strength.isPresent())
      builder.setStrength(m_strength.get());

    if(m_dexterity.isPresent())
      builder.setDexterity(m_dexterity.get());

    if(m_constitution.isPresent())
      builder.setConstitution(m_constitution.get());

    if(m_intelligence.isPresent())
      builder.setIntelligence(m_intelligence.get());

    if(m_wisdom.isPresent())
      builder.setWisdom(m_wisdom.get());

    if(m_charisma.isPresent())
      builder.setCharisma(m_charisma.get());

    for(Feat feat : m_feats)
      builder.addFeat(feat.toProto());

    for(Quality quality : m_qualities)
      builder.addQuality(quality.toProto());

    builder.setMaxHitPoints(m_maxHP);
    builder.setHitPoints(m_hp);

    for(Skill skill : m_skills)
      builder.addSkill(skill.toProto());

    if(m_alignment != Alignment.UNKNOWN)
      builder.setAlignment(m_alignment.toProto());

    if(m_fortitudeSave.isPresent())
      builder.setFortitudeSave(m_fortitudeSave.get());

    if(m_willSave.isPresent())
      builder.setWillSave(m_willSave.get());

    if(m_reflexSave.isPresent())
      builder.setReflexSave(m_reflexSave.get());

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

    if(proto.hasStrength())
      m_strength = Optional.of(proto.getStrength());

    if(proto.hasDexterity())
      m_dexterity = Optional.of(proto.getDexterity());

    if(proto.hasConstitution())
      m_constitution = Optional.of(proto.getConstitution());

    if(proto.hasIntelligence())
      m_intelligence = Optional.of(proto.getIntelligence());

    if(proto.hasWisdom())
      m_wisdom = Optional.of(proto.getWisdom());

    if(proto.hasCharisma())
      m_charisma = Optional.of(proto.getCharisma());

    for(FeatProto feat : proto.getFeatList())
      m_feats.add(Feat.fromProto(feat));

    for(QualityProto quality : proto.getQualityList())
      m_qualities.add(Quality.fromProto(quality));

    m_maxHP = proto.getMaxHitPoints();
    m_hp = proto.getHitPoints();

    for(SkillProto skill : proto.getSkillList())
      m_skills.add(Skill.fromProto(skill));

    if(proto.hasAlignment())
      m_alignment = Alignment.fromProto(proto.getAlignment());

    if(proto.hasFortitudeSave())
      m_fortitudeSave = Optional.of(proto.getFortitudeSave());

    if(proto.hasWillSave())
      m_willSave = Optional.of(proto.getWillSave());

    if(proto.hasReflexSave())
      m_reflexSave = Optional.of(proto.getReflexSave());
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
}
