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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto;
import net.ixitxachitls.dma.proto.Entries.CampaignEntryProto;
import net.ixitxachitls.dma.proto.Entries.FeatProto;
import net.ixitxachitls.dma.proto.Entries.MonsterProto;
import net.ixitxachitls.dma.proto.Entries.QualityProto;
import net.ixitxachitls.dma.proto.Entries.SkillProto;
import net.ixitxachitls.dma.rules.CarryingCapacity;
import net.ixitxachitls.dma.rules.Combat;
import net.ixitxachitls.dma.rules.Monsters;
import net.ixitxachitls.dma.values.Annotated;
import net.ixitxachitls.dma.values.Damage;
import net.ixitxachitls.dma.values.Dice;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Rational;
import net.ixitxachitls.dma.values.Slot;
import net.ixitxachitls.dma.values.Speed;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueSources;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.Weight;
import net.ixitxachitls.dma.values.enums.*;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

/**
 * This is a real monster.
 *
 * @file          Monster.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */
public class Monster extends CampaignEntry
{
  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  private static final int MIN_SYNERGY_RANKS = 5;
  private static final int SYNERGY_BONUS = 2;

  private static final Comparator<Item> PRICE_ORDERING = new Comparator<Item>()
  {
    @Override
    public int compare(Item first, Item second)
    {
      return Double.compare(second.getGoldValue(), first.getGoldValue());
    }
  };

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
     * @ param inType       the type of coins for the line
     * @param inStart      the range of the random values this line is valid
     * @param inEnd        the end of the range for this line
     * @param inNumber     the number of dice to use for generating the value
     * @param inDice       the dice to use for generating the value
     * @param inMultiplier the multiplier for the total value
     *
     */
    protected Coins(/*Money.Coin inType*,*/ int inStart, int inEnd,
                    int inNumber, int inDice, int inMultiplier)
    {
      super(inStart, inEnd);

      assert inNumber >= 0 : "must have a positive number here";
      assert inDice >= 0 : "must have a positive dice here";
      assert inMultiplier >= 0 : "must have a positive multiplier here";

      //m_type       = inType;
      m_number     = inNumber;
      m_dice       = inDice;
      m_multiplier = inMultiplier;
    }

    /** The type of coins to generate. */
    //private Money.Coin m_type;

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
    /*
    protected void roll(NewMoney ioMoney)
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
    */

    /** Convert the money line to a human readable string.
     *
     * @return the converted string
     *
     */
    @Override
    public String toString()
    {
      return m_start + "-" + m_end + ": " + m_number + "d" + m_dice + "x"
        + m_multiplier /*+ " " + m_type*/;
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
    public Optional<Coins> coins(int inRandom)
    {
      // determine the matching coin value
      for(Coins coins : m_coins)
        if(coins.matches(inRandom))
          return Optional.of(coins);

      return Optional.absent();
    }

    /** Determine the items value to the given random value.
     *
     * @param inRandom the random value (1-100) to get the items description
     *        for
     *
     * @return the items line determining what treasures a creature will get
     *
     */
    public Optional<Items> items(int inRandom)
    {
      // determine the matching item value
      for(Items items : m_items)
        if(items.matches(inRandom))
          return Optional.of(items);

      return Optional.absent();
    }

    /** Determine the goods value to the given random value.
     *
     * @param inRandom the random value (1-100) to get the goods description
     *        for
     *
     * @return the goods line determining what treasures a creature will get
     *
     */
    public Optional<Goods> goods(int inRandom)
    {
      // determine the matching item value
      for(Goods goods : m_goods)
        if(goods.matches(inRandom))
          return Optional.of(goods);

      return Optional.absent();
    }
  }

  /**
   * This is the internal, default constructor.
   */
  protected Monster()
  {
    super(TYPE);
  }

  public Monster(String inName)
  {
    super(inName, TYPE);
  }

  /**
   * This is the normal constructor.
   *
   * @param inCampaign the campaign this monster is in
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
     new Type.Builder<>(Monster.class, BaseMonster.TYPE).build();

   /** The type of the base entry to this entry. */
   public static final BaseType<BaseMonster> BASE_TYPE = BaseMonster.TYPE;

  /** A special name for the npc, if any. */
  protected Optional<String> m_givenName = Optional.absent();

  /** The possessions value. */
  private boolean m_createPossessionsOnSave = false;
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

  /** The actual number of hit points the monster currently has. */
  protected int m_hp = 0;
  protected int m_maxHP = 0;

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

  /** The languages the monster knows. */
  protected List<Language> m_languages = new ArrayList<>();

  /** The monster's personality. */
  protected Optional<String> m_personality = Optional.absent();

  /** The monetary treasure. */
  // protected Money m_money = new Money();

  /** The random generator. */
  // private static Random RANDOM = new Random();

  /** The treasures per level. */
  //private static List<Treasure> s_treasures = new ArrayList<Treasure>();

  //----- treasure definition ----------------------------------------------

  /*
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
  */

  public int abilityModifier(String inAbility)
  {
    Optional<Ability> ability = Ability.fromString(inAbility);
    if(!ability.isPresent())
      return 0;

    return abilityModifier(ability.get());
  }

  public int abilityModifier(Ability inAbility)
  {
    switch(inAbility)
    {
      case UNKNOWN:
      case NONE:
      default:
        return 0;

      case STRENGTH:
        return getStrengthModifier();

      case DEXTERITY:
        return getDexterityModifier();

      case CONSTITUTION:
        return getConstitutionModifier();

      case INTELLIGENCE:
        return getIntelligenceModifier();

      case WISDOM:
        return getWisdomModifier();

      case CHARISMA:
        return getCharismaModifier();
    }
  }

  public int ability(Ability inAbility)
  {
    switch(inAbility)
    {
      case UNKNOWN:
      case NONE:
      default:
        return 0;

      case STRENGTH:
        return totalStrength();

      case DEXTERITY:
        return totalDexterity();

      case CONSTITUTION:
        return totalConstitution();

      case INTELLIGENCE:
        return totalIntelligence();

      case WISDOM:
        return totalWisdom();

      case CHARISMA:
        return totalCharisma();
    }
  }

  /**
   * Get the monster's weapons.
   *
   * @return the list of weapons
   */
  public List<Item> getWeapons()
  {
    List<Item> weapons = new ArrayList<>();
    for(Item item : getPossessions())
      if(item != null && item.isWeapon() && !item.isAmmunition())
        weapons.add(item);

    Collections.sort(weapons, PRICE_ORDERING);
    return weapons;
  }

  /**
   * Get the monster's armor.
   * @return the armor
   */
  public List<Item> getArmor()
  {
    List<Item> armor = new ArrayList<>();
    for(Item item : getPossessions())
      if(item != null && item.isArmor() && !item.getArmorType().isShield())
        armor.add(item);

    return armor;
  }

  /**
   * Get all of the monsters possessions.
   *
   * @return the possesions
   */
  public List<Item> getPossessions()
  {
    if(m_possessions == null)
      m_possessions = DMADataFactory.get().getEntries(
          Item.TYPE, Optional.of(getCampaign().get().getKey()),
          "index-parent", getType().getLink() + "/" + getName().toLowerCase());

    return Collections.unmodifiableList(m_possessions);
  }

  public List<Item> getPossessions(List<String> inNames)
  {
    List<Item> matching = new ArrayList<>();

    for(Item item : getPossessions())
      for(String name : inNames)
        if(item.hasBaseName(name))
          matching.add(item);

    return matching;
  }

  /**
   * Get the feats the monster's has.
   *
   * @return the feats
   */
  public List<Feat> getFeats()
  {
    return Collections.unmodifiableList(m_feats);
  }

  public Annotated<List<Feat>> getCombinedFeats()
  {
    Annotated<List<Feat>> feats = new Annotated.List<>(m_feats, getName());

    for(BaseEntry base : getBaseEntries())
    {
      List<Feat> baseFeats = new ArrayList<>();
      for(Feat feat : ((BaseMonster)base).getCombinedFeats().get())
        baseFeats.add(feat);

      feats.add(new Annotated.List<>(baseFeats, base.getName()));
    }

    return feats;
  }


  /**
   * Get all of the monster's qualities.
   *
   * @return the qualities
   */
  public List<Quality> getQualities()
  {
    return Collections.unmodifiableList(m_qualities);
  }

  public Annotated.List<Quality> getRacialQualities()
  {
    Annotated.List<Quality> combined = new Annotated.List<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedQualities());

    return combined;
  }

  public Annotated.List<Quality> getCombinedQualities()
  {
    Annotated.List<Quality> combined = new Annotated.List<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedQualities());

    combined.add(getQualities(), getName());

    return combined;
  }

  protected Set<Feat> allFeats()
  {
    Set<Feat> feats = new HashSet<>(getFeats());

    for(BaseEntry entry : getBaseEntries())
      feats.addAll(((BaseMonster)entry).getCombinedFeats().get());

    return feats;
  }

  protected Modifier abilityModifierFromQualities(Ability inAbility)
  {
    Modifier modifier = new Modifier();

    for(Quality quality : allQualities())
      modifier = (Modifier)modifier.add(quality.abilityModifier(inAbility));
    return modifier;
  }

  protected List<Quality> allQualities()
  {
    return monsterQualities();
  }

  protected List<Quality> monsterQualities()
  {
    List<Quality> qualities = new ArrayList<>();

    for(BaseEntry entry : getBaseEntries())
      qualities.addAll(((BaseMonster)entry).getCombinedQualities().get());

    return qualities;
  }

  /**
   * Get the monster's strength, if it has any.
   *
   * @return the strength if the monster has any
   */
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
    if(m_strength.isPresent())
      return new Annotated.Integer(m_strength.get(), getName());

    Annotated.Integer combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedStrength());

    return combined;
  }

  public int totalStrength()
  {
    int score = 0;

    Optional<Integer> combined = getCombinedStrength().get();
    if(combined.isPresent())
      score = combined.get();

    score += getRacialStrengthModifier().unconditionalModifier();
    score += getStrengthMiscModifier().unconditionalModifier();

    return score;
  }

  public Annotated.Modifier strength()
  {
    Annotated.Modifier annotated = new Annotated.Modifier();

    if(m_strength.isPresent())
      annotated.add(new Modifier(m_strength.get()), getName());
    else
      for(BaseEntry base : getBaseEntries())
        for(ValueSources.ValueSource<Optional<Integer>> bonus
            : ((BaseMonster)base)
            .getCombinedStrength().getSources().getSources())
          if(bonus.getValue().isPresent())
            annotated.add(new Modifier(bonus.getValue().get()), base.getName());

    // Qualities
    for(Quality quality : allQualities())
    {
      Modifier modifier = quality.abilityModifier(Ability.STRENGTH);
      if(modifier.hasValue())
        annotated.add(modifier, quality.getName());
    }

    // Feats
    for(Feat feat : allFeats())
    {
      Modifier modifier = feat.getStrengthModifier();
      if(modifier.hasValue())
        annotated.add(modifier, feat.getName() + " feat");
    }

    return annotated;
  }

  public Annotated.Modifier dexterity()
  {
    Annotated.Modifier annotated = new Annotated.Modifier();

    if(m_dexterity.isPresent())
      annotated.add(new Modifier(m_dexterity.get()), getName());
    else
      for(BaseEntry base : getBaseEntries())
        for(ValueSources.ValueSource<Optional<Integer>> bonus
            : ((BaseMonster)base)
            .getCombinedDexterity().getSources().getSources())
          if(bonus.getValue().isPresent())
            annotated.add(new Modifier(bonus.getValue().get()), base.getName());

    // Qualities
    for(Quality quality : allQualities())
    {
      Modifier modifier = quality.abilityModifier(Ability.DEXTERITY);
      if(modifier.hasValue())
        annotated.add(modifier, quality.getName());
    }

    // Feats
    for(Feat feat : allFeats())
    {
      Modifier modifier = feat.getDexterityModifier();
      if(modifier.hasValue())
        annotated.add(modifier, feat.getName() + " feat");
    }

    return annotated;
  }

  public Annotated.Modifier constitution()
  {
    Annotated.Modifier annotated = new Annotated.Modifier();

    if(m_constitution.isPresent())
      annotated.add(new Modifier(m_constitution.get()), getName());
    else
      for(BaseEntry base : getBaseEntries())
        for(ValueSources.ValueSource<Optional<Integer>> bonus
            : ((BaseMonster)base)
            .getCombinedConstitution().getSources().getSources())
          if(bonus.getValue().isPresent())
            annotated.add(new Modifier(bonus.getValue().get()), base.getName());

    // Qualities
    for(Quality quality : allQualities())
    {
      Modifier modifier = quality.abilityModifier(Ability.CONSTITUTION);
      if(modifier.hasValue())
        annotated.add(modifier, quality.getName());
    }

    // Feats
    for(Feat feat : allFeats())
    {
      Modifier modifier = feat.getConstitutionModifier();
      if(modifier.hasValue())
        annotated.add(modifier, feat.getName() + " feat");
    }

    return annotated;
  }

  public Annotated.Modifier intelligence()
  {
    Annotated.Modifier annotated = new Annotated.Modifier();

    if(m_intelligence.isPresent())
      annotated.add(new Modifier(m_intelligence.get()), getName());
    else
      for(BaseEntry base : getBaseEntries())
        for(ValueSources.ValueSource<Optional<Integer>> bonus
            : ((BaseMonster)base)
            .getCombinedIntelligence().getSources().getSources())
          if(bonus.getValue().isPresent())
            annotated.add(new Modifier(bonus.getValue().get()), base.getName());

    // Qualities
    for(Quality quality : allQualities())
    {
      Modifier modifier = quality.abilityModifier(Ability.INTELLIGENCE);
      if(modifier.hasValue())
        annotated.add(modifier, quality.getName());
    }

    // Feats
    for(Feat feat : allFeats())
    {
      Modifier modifier = feat.getIntelligenceModifier();
      if(modifier.hasValue())
        annotated.add(modifier, feat.getName() + " feat");
    }

    return annotated;
  }

  public Annotated.Modifier wisdom()
  {
    Annotated.Modifier annotated = new Annotated.Modifier();

    if(m_wisdom.isPresent())
      annotated.add(new Modifier(m_wisdom.get()), getName());
    else
      for(BaseEntry base : getBaseEntries())
        for(ValueSources.ValueSource<Optional<Integer>> bonus
            : ((BaseMonster)base)
            .getCombinedWisdom().getSources().getSources())
          if(bonus.getValue().isPresent())
            annotated.add(new Modifier(bonus.getValue().get()), base.getName());

    // Qualities
    for(Quality quality : allQualities())
    {
      Modifier modifier = quality.abilityModifier(Ability.WISDOM);
      if(modifier.hasValue())
        annotated.add(modifier, quality.getName());
    }

    // Feats
    for(Feat feat : allFeats())
    {
      Modifier modifier = feat.getWisdomModifier();
      if(modifier.hasValue())
        annotated.add(modifier, feat.getName() + " feat");
    }

    return annotated;
  }

  public Annotated.Modifier charisma()
  {
    Annotated.Modifier annotated = new Annotated.Modifier();

    if(m_charisma.isPresent())
      annotated.add(new Modifier(m_charisma.get()), getName());
    else
      for(BaseEntry base : getBaseEntries())
        for(ValueSources.ValueSource<Optional<Integer>> bonus
            : ((BaseMonster)base)
            .getCombinedCharisma().getSources().getSources())
          if(bonus.getValue().isPresent())
            annotated.add(new Modifier(bonus.getValue().get()), base.getName());

    // Qualities
    for(Quality quality : allQualities())
    {
      Modifier modifier = quality.abilityModifier(Ability.CHARISMA);
      if(modifier.hasValue())
        annotated.add(modifier, quality.getName());
    }

    // Feats
    for(Feat feat : allFeats())
    {
      Modifier modifier = feat.getCharismaModifier();
      if(modifier.hasValue())
        annotated.add(modifier, feat.getName() + " feat");
    }

    return annotated;
  }

  public Modifier getRacialStrengthModifier()
  {
    return abilityModifierFromQualities(Ability.STRENGTH);
  }

  /**
   * Get the monster's strength modifier. If the monster does not have a
   * strength, this returns 0.
   *
   * @return the the monsters strength modifier
   */
  public int getStrengthModifier()
  {
    return Monsters.abilityModifier(totalStrength());
  }

  public Modifier getStrengthMiscModifier()
  {
    Modifier modifier = new Modifier();

    for(Feat feat : allFeats())
      modifier = (Modifier)modifier.add(feat.getStrengthModifier());

    return modifier;
  }

  /**
   * Get a monster's constitution value, if any.
   *
   * @return the constitution value
   */
  public Optional<Integer> getConstitution()
  {
    return m_constitution;
  }

  /**
   * Get a monster's annotated and combined constitution score.
   *
   * @return the annotated constitution score
   */
  public Annotated<Optional<Integer>> getCombinedConstitution()
  {
    if(m_constitution.isPresent())
      return new Annotated.Integer(m_constitution.get(), getName());

    Annotated.Integer combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedConstitution());

    return combined;
  }

  public int totalConstitution()
  {
    int score = 0;

    Optional<Integer> combined = getCombinedConstitution().get();
    if(combined.isPresent())
      score = combined.get();

    score += getRacialConstitutionModifier().unconditionalModifier();
    score += getConstitutionMiscModifier().unconditionalModifier();

    return score;
  }

  public Modifier getRacialConstitutionModifier()
  {
    return abilityModifierFromQualities(Ability.CONSTITUTION);
  }

  /**
   * Get the monster's constitution modifier. This return 0 if the monster
   * does not have a constitution score.
   *
   * @return the constition modifier
   */
  public int getConstitutionModifier()
  {
    return Monsters.abilityModifier(totalConstitution());
  }

  public Modifier getConstitutionMiscModifier()
  {
    Modifier modifier = new Modifier();

    for(Feat feat : allFeats())
      modifier = (Modifier)modifier.add(feat.getConstitutionModifier());

    return modifier;
  }

  /**
   * Get a monsters dexterity score.
   *
   * @return the dexterity score, if it has any
   */
  public Optional<Integer> getDexterity()
  {
    return m_dexterity;
  }

  /**
   * Get the annotated, combined dexterity score of the monster.
   *
   * @return the annotated dexterity score
   */
  public Annotated<Optional<Integer>> getCombinedDexterity()
  {
    if(m_dexterity.isPresent())
      return new Annotated.Integer(m_dexterity.get(), getName());

    Annotated.Integer combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedDexterity());

    combined.add(getRacialDexterityModifier().totalModifier(),
                 "racial " + getRacialDexterityModifier());

    return combined;
  }

  public int totalDexterity()
  {
    int score = 0;

    Optional<Integer> combined = getCombinedDexterity().get();
    if(combined.isPresent())
      score = combined.get();

    score += getRacialDexterityModifier().unconditionalModifier();
    score += getDexterityMiscModifier().unconditionalModifier();

    return score;
  }

  public Modifier getRacialDexterityModifier()
  {
    return abilityModifierFromQualities(Ability.DEXTERITY);
  }

  /**
   * Get the dexerity modifier of the monster. This returns 0 if the monster
   * does not have a dexerity score.
   *
   * @return the dexterity modifier
   */
  public int getDexterityModifier()
  {
    return Monsters.abilityModifier(totalDexterity());
  }

  public Modifier getDexterityMiscModifier()
  {
    Modifier modifier = new Modifier();

    for(Feat feat : allFeats())
      modifier = (Modifier)modifier.add(feat.getDexterityModifier());

    return modifier;
  }

  /**
   * Get a monster's intelligence score, if it has any.
   *
   * @return the intelligence score of the monster
   */
  public Optional<Integer> getIntelligence()
  {
    return m_intelligence;
  }

  /**
   * Get the annotated and combined intelligence score of the monster and
   * its bases.
   *
   * @return the annotated intelligence score
   */
  public Annotated<Optional<Integer>> getCombinedIntelligence()
  {
    if(m_intelligence.isPresent())
      return new Annotated.Integer(m_intelligence.get(), getName());

    Annotated.Integer combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedIntelligence());

    return combined;
  }

  public int totalIntelligence()
  {
    int score = 0;

    Optional<Integer> combined = getCombinedIntelligence().get();
    if(combined.isPresent())
      score = combined.get();

    score += getRacialIntelligenceModifier().unconditionalModifier();
    score += getIntelligenceMiscModifier().unconditionalModifier();

    return score;
  }

  public Modifier getRacialIntelligenceModifier()
  {
    return abilityModifierFromQualities(Ability.INTELLIGENCE);
  }

  /**
   * Get a monster's intelligence modifier. This will return 0 if the monster
   * does not have an intelligence score.
   *
   * @return the intelligence modifier
   */
  public int getIntelligenceModifier()
  {
    return Monsters.abilityModifier(totalIntelligence());
  }

  public Modifier getIntelligenceMiscModifier()
  {
    Modifier modifier = new Modifier();

    for(Feat feat : allFeats())
      modifier = (Modifier)modifier.add(feat.getIntelligenceModifier());

    return modifier;
  }

  /**
   * Get a monster's wisdom score, if it has any.
   *
   * @return the monster's wisdom score
   */
  public Optional<Integer> getWisdom()
  {
    return m_wisdom;
  }

  /**
   * Get a monster's annotated and combined wisdom score.
   *
   * @return the annoated wisdom score
   */
  public Annotated<Optional<Integer>> getCombinedWisdom()
  {
    if(m_wisdom.isPresent())
      return new Annotated.Integer(m_wisdom.get(), getName());

    Annotated.Integer combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedWisdom());

    return combined;
  }

  public int totalWisdom()
  {
    int score = 0;

    Optional<Integer> combined = getCombinedWisdom().get();
    if(combined.isPresent())
      score = combined.get();

    score += getRacialWisdomModifier().unconditionalModifier();
    score += getWisdomMiscModifier().unconditionalModifier();

    return score;
  }

  public Modifier getRacialWisdomModifier()
  {
    return abilityModifierFromQualities(Ability.WISDOM);
  }

  /**
   * Get a monster's wisdom modifier. If the monster does not have a wisdom
   * score, this return 0.
   *
   * @return the monster's wisdom modifier
   */
  public int getWisdomModifier()
  {
    return Monsters.abilityModifier(totalWisdom());
  }

  public Modifier getWisdomMiscModifier()
  {
    Modifier modifier = new Modifier();

    for(Feat feat : allFeats())
      modifier = (Modifier)modifier.add(feat.getWisdomModifier());

    return modifier;
  }

  /**
   * Get a monsters charisma score, if it has any.
   *
   * @return the monster's charisma
   */
  public Optional<Integer> getCharisma()
  {
    return m_charisma;
  }

  /**
   * Get a monsters annotated and combined charisma score.
   *
   * @return the annotated charisma score
   */
  public Annotated<Optional<Integer>> getCombinedCharisma()
  {
    if(m_charisma.isPresent())
      return new Annotated.Integer(m_charisma.get(), getName());

    Annotated.Integer combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedCharisma());

    return combined;
  }

  public int totalCharisma()
  {
    int score = 0;

    Optional<Integer> combined = getCombinedCharisma().get();
    if(combined.isPresent())
      score = combined.get();

    score += getRacialCharismaModifier().unconditionalModifier();
    score += getCharismaMiscModifier().unconditionalModifier();

    return score;
  }

  public Modifier getRacialCharismaModifier()
  {
    return abilityModifierFromQualities(Ability.CHARISMA);
  }

  /**
   * Get a monster's charisma modifier. This return 0 if the monster does not
   * have a charisma score.
   *
   * @return the charisma modifier
   */
  public int getCharismaModifier()
  {
    return Monsters.abilityModifier(totalCharisma());
  }

  public Modifier getCharismaMiscModifier()
  {
    Modifier modifier = new Modifier();

    for(Feat feat : allFeats())
      modifier = (Modifier)modifier.add(feat.getCharismaModifier());

    return modifier;
  }

  public int strengthBonus()
  {
    return Monsters.abilityModifier(getCombinedStrength().get());
  }

  public int dexterityBonus()
  {
    return Monsters.abilityModifier(getCombinedDexterity().get());
  }

  public int constitutionBonus()
  {
    return Monsters.abilityModifier(getCombinedConstitution().get());
  }

  public int intelligenceBonus()
  {
    return Monsters.abilityModifier(getCombinedIntelligence().get());
  }

  public int wisdomBonus()
  {
    return Monsters.abilityModifier(getCombinedWisdom().get());
  }

  public int charismaBonus()
  {
    return Monsters.abilityModifier(getCombinedCharisma().get());
  }

  public Annotated<List<Skill>> getCombinedSkills()
  {
    Annotated<List<Skill>> combined =
        new Annotated.List<Skill>(m_skills, getName());

    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedSkills());

    return combined;
  }

  public int skillPointsUsed()
  {
    int used = 0;
    for(BaseEntry base : getBaseEntries())
      used += ((BaseMonster)base).skillPointsUsed();

    for(Skill skill : m_skills)
      used += skill.getRanks();

    return used;
  }

  public int skillPoints()
  {
    int points = 0;
    for(BaseEntry base : getBaseEntries())
      points += ((BaseMonster)base).skillPoints();

    return points;
  }

  /**
   * Get a monster's annotated and combined level adjustment.
   *
   * @return the level adjustment
   */
  public Annotated<Optional<Integer>> getCombinedLevelAdjustment()
  {
    Annotated.Integer combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedLevelAdjustment());

    return combined;
  }

  /**
   * Get a monster's annotated and combined size.
   *
   * @return the monster's size
   */
  public Annotated<Optional<Size>> getCombinedSize()
  {
    Annotated<Optional<Size>> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedSize());

    return combined;
  }

  public int sizeModifier()
  {
    Annotated<Optional<Size>> size = getCombinedSize();
    if(!size.get().isPresent())
      return 0;

    return size.get().get().modifier();
  }

  public int sizeGrappleModifier()
  {
    Annotated<Optional<Size>> size = getCombinedSize();
    if(!size.get().isPresent())
      return 0;

    return size.get().get().grapple();
  }

  /**
   * Get a monsters combined base attack bonus.
   *
   * @return the combined base attack bonus
   */
  public Annotated.Bonus getCombinedBaseAttack()
  {
    Annotated.Bonus combined = new Annotated.Bonus();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedBaseAttack());

    return combined;
  }

  public int grappleModifier()
  {
    int modifier = getStrengthModifier();
    if (getCombinedBaseAttack().get().isPresent())
      modifier += getCombinedBaseAttack().get().get();

    modifier += sizeGrappleModifier();

    return modifier;
  }

  /**
   * Get a monster's alignment.
   *
   * @return the alignment
   */
  public Alignment getAlignment()
  {
    return m_alignment;
  }

  /**
   * Get a monster's current hit points.
   *
   * @return the current hit points
   */
  public int getHP()
  {
    return m_hp;
  }

  public int getMaxHP()
  {
    return m_maxHP;
  }

  public Annotated.Boolean isCombinedQuadruped()
  {
    Annotated.Boolean combined = new Annotated.Boolean();
    for (BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).isCombinedQuadruped());

    return combined;
  }

  public int lightLoad()
  {
    Optional<Integer> strength = getCombinedStrength().get();
    Optional<Size> size = getCombinedSize().get();
    Optional<Boolean> quadruped = isCombinedQuadruped().get();

    if(!strength.isPresent() || !size.isPresent())
      return 0;

    return CarryingCapacity.lightLoad(
        strength.get(), size.get(),
        quadruped.isPresent() ? quadruped.get() : false);
  }

  public int mediumLoad()
  {
    Optional<Integer> strength = getCombinedStrength().get();
    Optional<Size> size = getCombinedSize().get();
    Optional<Boolean> quadruped = isCombinedQuadruped().get();

    if(!strength.isPresent() || !size.isPresent())
      return 0;

    return CarryingCapacity.mediumLoad(
        strength.get(), size.get(),
        quadruped.isPresent() ? quadruped.get() : false);
  }

  public int heavyLoad()
  {
    Optional<Integer> strength = getCombinedStrength().get();
    Optional<Size> size = getCombinedSize().get();
    Optional<Boolean> quadruped = isCombinedQuadruped().get();

    if(!strength.isPresent() || !size.isPresent())
      return 0;

    return CarryingCapacity.heavyLoad(
        strength.get(), size.get(),
        quadruped.isPresent() ? quadruped.get() : false);
  }

  public int liftLoad()
  {
    Optional<Integer> strength = getCombinedStrength().get();
    Optional<Size> size = getCombinedSize().get();
    Optional<Boolean> quadruped = isCombinedQuadruped().get();

    if(!strength.isPresent() || !size.isPresent())
      return 0;

    return CarryingCapacity.liftLoad(
        strength.get(), size.get(),
        quadruped.isPresent() ? quadruped.get() : false);
  }

  public int dragLoad()
  {
    Optional<Integer> strength = getCombinedStrength().get();
    Optional<Size> size = getCombinedSize().get();
    Optional<Boolean> quadruped = isCombinedQuadruped().get();

    if(!strength.isPresent() || !size.isPresent())
      return 0;

    return CarryingCapacity.dragLoad(
        strength.get(), size.get(),
        quadruped.isPresent() ? quadruped.get() : false);
  }

  public Weight currentLoad()
  {
    Weight load = new Weight(Optional.<Rational>absent(),
                             Optional.<Rational>absent());

    for(Item item : getPossessions())
      if(!item.hasBaseName("Storage"))
      {
        Optional<Weight> weight = item.getCombinedWeight().get();
        if(weight.isPresent())
          load = (Weight) load.add(weight.get());
      }

    return load;
  }

  /**
   * Get a monsters annotated and combined speed.
   *
   * @param inMode the movement mode to get the speed for
   * @return the speed for the given movement mode, if any
   */
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

  /**
   * Get all of a monsters annoated and combined speeds.
   *
   * @return a list of all the available speeds
   */
  public List<Annotated.Arithmetic<Speed>> getCombinedSpeeds()
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

  public Annotated<Optional<Distance>> getCombinedSpace()
  {
    Annotated<Optional<Distance>> combined =
      new Annotated.Max<Distance>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedSpace());

    return combined;
  }

  public Annotated<Optional<Distance>> getCombinedReach()
  {
    Annotated<Optional<Distance>> combined =
      new Annotated.Max<Distance>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedReach());

    return combined;
  }

  /**
   * Get a monster's initiative value.
   *
   * @return the initiative
   */
  public int getInitiative()
  {
    int initiative = 0;
    for(BaseEntry base : getBaseEntries())
    {
      int baseInitiative = ((BaseMonster)base).initiative();
      if (baseInitiative != 0)
        if(initiative == 0)
          initiative = baseInitiative;
        else
          initiative = Math.max(initiative, baseInitiative);
    }

    return initiative;
  }

  /**
   * Get a monster's grapple bonus.
   *
   * @return the grapple bonus
   */
  public int getGrappleBonus()
  {
    int grapple = 0;
    for (BaseEntry base : getBaseEntries())
    {
      int baseGrapple = ((BaseMonster)base).grappleBonus();
      if(baseGrapple != 0)
        if(grapple == 0)
          grapple = baseGrapple;
        else
        grapple = Math.max(grapple, baseGrapple);
    }

    return grapple;
  }

  /**
   * Get a monster's fortitude save.
   *
   * @return the fortitude save
   */
  public Optional<Integer> getFortitudeSave()
  {
    return m_fortitudeSave;
  }

  /**
   * Get a monster's combined and annotated base fortitude save.
   *
   * @return the annotated base fortitude save
   */
  public Annotated.Bonus getCombinedBaseFortitudeSave()
  {
    if(m_fortitudeSave.isPresent())
      return new Annotated.Bonus(m_fortitudeSave.get(), getName());

    Annotated.Bonus combined = new Annotated.Bonus();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedFortitudeSave());

    return combined;
  }

  public Modifier miscFortitudeModifier()
  {
    Modifier modifier = new Modifier();

    for(Feat feat : allFeats())
      modifier = (Modifier)modifier.add(feat.fortitudeModifier());

    return modifier;
  }

  public Modifier miscWillModifier()
  {
    Modifier modifier = new Modifier();

    for(Feat feat : allFeats())
      modifier = (Modifier)modifier.add(feat.willModifier());

    return modifier;
  }

  public Modifier miscReflexModifier()
  {
    Modifier modifier = new Modifier();

    for(Feat feat : allFeats())
      modifier = (Modifier)modifier.add(feat.reflexModifier());

    return modifier;
  }

  /**
   * Get a monster's combined fortitude save.
   *
   * @return the annotated fortitude save
   */
  public Annotated.Bonus getCombinedFortitudeSave()
  {
    Annotated.Bonus save = getCombinedBaseFortitudeSave();
    save.add(getConstitutionModifier(), "Constitution");

    return save;
  }

  public Annotated.Modifier fortitudeSave()
  {
    Annotated.Modifier save = new Annotated.Modifier();

    // Racial values.
    for (ValueSources.ValueSource<Optional<Integer>> bonus
        : getCombinedFortitudeSave().getSources().getSources())
      if (bonus.getValue().isPresent())
        save.add(new Modifier(bonus.getValue().get()), bonus.getSource());

    // Qualities.
    for (Quality quality : allQualities())
      if(quality.fortitudeModifier().hasValue())
        save.add(quality.fortitudeModifier(), quality.getName());

    for(Feat feat : allFeats())
    {
      Modifier modifier = feat.fortitudeModifier();
      if(modifier.hasValue())
        save.add(modifier, feat.getName() + " feat");
    }

    return save;
  }

  public Annotated.Modifier reflexSave()
  {
    Annotated.Modifier save = new Annotated.Modifier();

    // Racial values.
    for (ValueSources.ValueSource<Optional<Integer>> bonus
        : getCombinedReflexSave().getSources().getSources())
      if (bonus.getValue().isPresent())
        save.add(new Modifier(bonus.getValue().get()), bonus.getSource());

    // Qualities.
    for(Quality quality : allQualities())
      if(quality.reflexModifier().hasValue())
        save.add(quality.reflexModifier(), quality.getName());

    for(Feat feat : allFeats())
    {
      Modifier modifier = feat.reflexModifier();
      if(modifier.hasValue())
        save.add(modifier, feat.getName() + " feat");
    }

    return save;
  }

  public Annotated.Modifier willSave()
  {
    Annotated.Modifier save = new Annotated.Modifier();

    // Racial values.
    for (ValueSources.ValueSource<Optional<Integer>> bonus
        : getCombinedWillSave().getSources().getSources())
      if (bonus.getValue().isPresent())
        save.add(new Modifier(bonus.getValue().get()), bonus.getSource());

    // Qualities.
    for (Quality quality : allQualities())
      if(quality.willModifier().hasValue())
        save.add(quality.willModifier(), quality.getName());

    for(Feat feat : allFeats())
    {
      Modifier modifier = feat.willModifier();
      if(modifier.hasValue())
        save.add(modifier, feat.getName() + " feat");
    }

    return save;
  }

  public Modifier racialFortitudeModifier()
  {
    Modifier modifier = new Modifier();

    for(Quality quality : allQualities())
      modifier.add(quality.fortitudeModifier());

    return modifier;
  }

  /**
   * Get a monster's combined and annotated base reflex save.
   *
   * @return the annotated base relflex save
   */
  public Annotated.Bonus getCombinedBaseReflexSave()
  {
    if(m_reflexSave.isPresent())
      return new Annotated.Bonus(m_reflexSave.get(), getName());

    Annotated.Bonus combined = new Annotated.Bonus();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedReflexSave());

    return combined;
  }

  /**
   * Get a monsters combined and annoated relfex save. Includes the dexterity
   * bonus.
   *
   * @return the annotated reflex save
   */
  public Annotated.Bonus getCombinedReflexSave()
  {
    Annotated.Bonus save = getCombinedBaseReflexSave();
    save.add(getDexterityModifier(), "Dexterity");

    return save;
  }

  public Modifier racialReflexModifier()
  {
    Modifier modifier = new Modifier();

    for(Quality quality : allQualities())
      modifier.add(quality.reflexModifier());

    return modifier;
  }

  /**
   * Get a monster's annotated and combined base will save.
   *
   * @return the annotated base will save
   */
  public Annotated.Bonus getCombinedBaseWillSave()
  {
    if(m_willSave.isPresent())
      return new Annotated.Bonus(m_willSave.get(), getName());

    Annotated.Bonus combined = new Annotated.Bonus();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedWillSave());

    return combined;
  }

  /**
   * Get a monster's combined and annotated will save. Includes the wisdom
   * modifier.
   *
   * @return the annotated will save
   */
  public Annotated.Bonus getCombinedWillSave()
  {
    Annotated.Bonus save = getCombinedBaseWillSave();
    save.add(getWisdomModifier(), "Wisdom");

    return save;
  }

  public Modifier racialWillModifier()
  {
    Modifier modifier = new Modifier();

    for(Quality quality : allQualities())
      modifier = (Modifier)modifier.add(quality.willModifier());

    return modifier;
  }

  /**
   * Get a monster's annotated and combined natural armor.
   *
   * @return the annotated natural armor
   */
  public Annotated.Arithmetic<Modifier> getCombinedNaturalArmor()
  {
    Annotated.Arithmetic<Modifier> combined = new Annotated.Arithmetic<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedNaturalArmor());

    return combined;
  }

  public Annotated<Optional<MonsterType>> getCombinedMonsterType()
  {
    Annotated<Optional<MonsterType>> combined =
        new Annotated.Max<>(MonsterType.UNKNOWN, getName());
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedMonsterType());

    return combined;
  }

  public Annotated<List<MonsterSubtype>> getCombinedMonsterSubtypes()
  {
    Annotated.List<MonsterSubtype> combined = new Annotated.List<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedMonsterSubtypes());

    return combined;
  }

  public boolean hasSubtype()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseMonster)base).hasSubtype())
        return true;

    return false;
  }

  public Annotated.Integer getCombinedHitDie()
  {
    Annotated.Integer combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseMonster)entry).getCombinedHitDie());

    return combined;
  }

  public Dice.List totalHitDie()
  {
    Dice.List dice = new Dice.List();
    for (BaseEntry base : getBaseEntries())
      dice = dice.add(((BaseMonster)base).totalHitDie());

    return dice;
  }

  /**
   * Get the monster's armor bonus.
   *
   * @return the armor bonus
   */
  public Modifier getArmorBonus()
  {
    Modifier bonus = null;
    for(Item armor : getArmor())
    {
      if(armor.getArmorType().isShield())
        continue;

      Optional<Modifier> modifier = armor.getArmorClass();
      if(modifier.isPresent())
        if(bonus == null)
          bonus = modifier.get();
        else
          bonus = (Modifier) bonus.add(modifier.get());
    }

    if(bonus == null)
      return new Modifier();

    return bonus;
  }

  /**
   * Get a monster's shield bonus.
   *
   * @return the shield bonus
   */
  public Modifier getShieldBonus()
  {
    Modifier bonus = null;
    for(Item armor : getArmor())
    {
      if(!armor.getArmorType().isShield())
        continue;

      Optional<Modifier> modifier = armor.getArmorClass();
      if(modifier.isPresent())
        if(bonus == null)
          bonus = modifier.get();
        else
          bonus = (Modifier) bonus.add(modifier.get());
    }

    if(bonus == null)
      return new Modifier();

    return bonus;
  }

  public int totalArmorClass()
  {
    return armorClass().totalModifier();
  }

  public Modifier armorClass() {
    Modifier armor = new Modifier(10, Modifier.Type.GENERAL,
                                  Optional.<String>absent(),
                                  Optional.<Modifier>absent());
    armor = (Modifier)armor.add(getArmorBonus());
    armor = (Modifier)armor.add(getShieldBonus());
    Optional<Modifier> natural = getCombinedNaturalArmor().get();
    if(natural.isPresent())
      armor = (Modifier)armor.add(natural.get());

    armor = (Modifier)armor.add(new Modifier(getDexterityModifier(),
                                             Modifier.Type.ABILITY,
                                             Optional.<String>absent(),
                                             Optional.<Modifier>absent()));
    armor = (Modifier)armor.add(new Modifier(sizeModifier(),
                                             Modifier.Type.SIZE,
                                             Optional.<String>absent(),
                                             Optional.<Modifier>absent()));

    return armor;
  }

  /**
   * Get a monster's armor class against touch attacks.
   *
   * @return the monster's touch AC
   */

  public int totalArmorClassTouch()
  {
    return armorClassTouch().totalModifier();
  }

  public Modifier armorClassTouch()
  {
    Modifier armor = new Modifier(10, Modifier.Type.GENERAL,
                                  Optional.<String>absent(),
                                  Optional.<Modifier>absent());

    armor = (Modifier)armor.add(new Modifier(getDexterityModifier(),
                                             Modifier.Type.ABILITY,
                                             Optional.<String>absent(),
                                             Optional.<Modifier>absent()));
    armor = (Modifier)armor.add(new Modifier(sizeModifier(),
                                             Modifier.Type.SIZE,
                                             Optional.<String>absent(),
                                             Optional.<Modifier>absent()));

    return armor;
  }

  /**
   * Get a monster's armor class when it's flat footed.
   *
   * @return the flat footed AC
   */
  public int totalArmorClassFlatfooted()
  {
    return armorClassFlatfooted().totalModifier();
  }

  public Modifier armorClassFlatfooted()
  {
    Modifier armor = new Modifier(10, Modifier.Type.GENERAL,
                                  Optional.<String>absent(),
                                  Optional.<Modifier>absent());
    armor = (Modifier)armor.add(getArmorBonus());
    armor = (Modifier)armor.add(getShieldBonus());
    Optional<Modifier> natural = getCombinedNaturalArmor().get();
    if(natural.isPresent())
      armor = (Modifier)armor.add(natural.get());

    armor = (Modifier)armor.add(new Modifier(sizeModifier(),
                                             Modifier.Type.SIZE,
                                             Optional.<String>absent(),
                                             Optional.<Modifier>absent()));

    return armor;
  }


  /**
   * Get all the armor items a monster is wearing.
   *
   * @return the armor worn
   */
  public Optional<Item> getArmorWorn()
  {
    List<Item> armor = getArmor();
    for(Item item : armor)
      if(item.isArmor() && !item.getArmorType().isShield())
        return Optional.of(item);

    return Optional.absent();
  }

  /**
   * Get the shield worn by the monster, if any.
   *
   * @return the shield worn
   */
  public Optional<Item> getShieldWorn()
  {
    List<Item> armor = getArmor();
    for(Item item : armor)
      if(item.isArmor() && item.getArmorType().isShield())
        return Optional.of(item);

    return Optional.absent();
  }

  public List<Item> getPossessionsOnPerson()
  {
    List<Item> possessions = getPossessions();
    List<Item> items = new ArrayList<>();

    for(Item item : possessions)
    {
      if(!item.hasBaseName("Storage"))
      {
        items.add(item);
        items.addAll(item.getAllContents());
      }
    }

    return items;
  }

  public Weight weightOnPerson()
  {
    Weight weight = new Weight();
    for(Item item : getPossessions())
      if(!item.hasBaseName("Storage"))
      {
        Optional<Weight> itemWeight = item.getCombinedWeight().get();
        if(itemWeight.isPresent())
          weight = (Weight)weight.add(itemWeight.get());
      }

    return weight;
  }

  public List<Item> magicalItemsOnPerson()
  {
    List<Item> possessions = getPossessions();
    List<Item> items = new ArrayList<>();

    for(Item item : possessions)
    {
      if(item.hasBaseName("Storage"))
        continue;

      if(item.isMagical())
        items.add(item);
    }

    return items;
  }

  public List<Item> magicalItemsOnPerson(Slot inSlot)
  {
    List<Item> items = new ArrayList<>();
    for(Item item : magicalItemsOnPerson())
      if(item.getCombinedSlot().get().isPresent()
          && item.getCombinedSlot().get().get() == inSlot)
        items.add(item);

    return items;
  }

  public Optional<Item> magicalItemOnHead()
  {
    return Entries.first(magicalItemsOnPerson(Slot.HEAD));
  }

  public Optional<Item> magicalItemOnHands()
  {
    return Entries.first(magicalItemsOnPerson(Slot.HANDS));
  }

  public Optional<Item> magicalItemOnEyes()
  {
    return Entries.first(magicalItemsOnPerson(Slot.EYES));
  }

  public Optional<Item> magicalItemOnWrists()
  {
    return Entries.first(magicalItemsOnPerson(Slot.WRISTS));
  }

  public Optional<Item> magicalItemOnNeck()
  {
    return Entries.first(magicalItemsOnPerson(Slot.NECK));
  }

  public Optional<Item> magicalItemOnBody()
  {
    return Entries.first(magicalItemsOnPerson(Slot.BODY));
  }

  public Optional<Item> magicalItemOnSchoulders()
  {
    return Entries.first(magicalItemsOnPerson(Slot.SHOULDERS));
  }

  public Optional<Item> magicalItemOnTorso()
  {
    return Entries.first(magicalItemsOnPerson(Slot.TORSO));
  }

  public Optional<Item> magicalItemOnRing1()
  {
    return Entries.first(magicalItemsOnPerson(Slot.FINGER));
  }

  public Optional<Item> magicalItemOnRing2()
  {
    return Entries.second(magicalItemsOnPerson(Slot.FINGER));
  }

  public Optional<Item> magicalItemOnWaist()
  {
    return Entries.first(magicalItemsOnPerson(Slot.WAIST));
  }

  public Optional<Item> magicalItemOnFeet()
  {
    return Entries.first(magicalItemsOnPerson(Slot.FEET));
  }

  public List<Item> moneyOnPerson()
  {
    List<Item> items = new ArrayList<>();
    for(Item item : getPossessionsOnPerson()) {
      if(item.isMonetary())
        items.add(item);
    }

    Item.sortByValue(items);
    return items;
  }

  public List<Item> moneyNotOnPerson()
  {
    List<Item> items = new ArrayList<>();
    for(Item item : getPossessionsNotOnPerson()) {
      if(item.isMonetary())
        items.add(item);
    }

    Item.sortByValue(items);
    return items;
  }

  public List<Item> getPossessionsNotOnPerson()
  {
    List<Item> possessions = getPossessions();
    List<Item> items = new ArrayList<>();

    for(Item item : possessions)
    {
      if(item.hasBaseName("Storage"))
      {
        items.add(item);
        items.addAll(item.getAllContents());
      }
    }

    return items;
  }

  public List<Item> getAllPossessions()
  {
    List<Item> possessions = getPossessions();
    List<Item> items = new ArrayList<>();

    for(Item item : possessions)
    {
      items.add(item);
      items.addAll(item.getAllContents());
    }

    return items;
  }

  public List<Skill> getSkills()
  {
    return Collections.unmodifiableList(m_skills);
  }

  public int skillRanks(String inSkill)
  {
    for(Skill skill : m_skills)
      if(skill.getName().equalsIgnoreCase(inSkill))
        return skill.getRanks();

    int ranks = 0;
    for (BaseEntry base : getBaseEntries())
      ranks += ((BaseMonster)base).skillRanks(inSkill);

    return ranks;
  }

  public int skillModifier(String inSkill, Ability inAbility)
  {
    return skillRanks(inSkill) + abilityModifier(inAbility)
        + miscModifier(inSkill).unconditionalModifier();
  }

  public Modifier miscModifier(String inSkill)
  {
    Optional<BaseSkill> skill = BaseSkill.get(inSkill);
    Modifier modifier = new Modifier();
    if(skill.isPresent())
    {
      // Armor check penalty, if relevant.
      if(skill.get().hasArmorCheckPenalty())
      {
        int penalty = 0;
        for(Item armor : getArmor())
        {
          Optional<Integer> checkPenalty = armor.getCombinedCheckPenalty().get();
          if(checkPenalty.isPresent())
            penalty += checkPenalty.get();
        }

        if(skill.get().hasDoubleArmorCheckPenalty())
          penalty *= 2;

        modifier = (Modifier)
            modifier.add(new Modifier(penalty, Modifier.Type.ARMOR,
                                      Optional.<String>absent(),
                                      Optional.<Modifier>absent()));
      }

      // Synergies, if any
      for(BaseSkill.Synergy synergy : skill.get().getCombinedSynergies().get())
        if(skillRanks(synergy.getName()) >= MIN_SYNERGY_RANKS)
          modifier = (Modifier)
              modifier.add(new Modifier(SYNERGY_BONUS, Modifier.Type.SYNERGY,
                                        synergy.getCondition(),
                                        Optional.<Modifier>absent()));

      // Qualities
      for(Quality quality : allQualities())
        modifier = (Modifier)modifier.add(quality.skillModifier(inSkill));

      // Feats
      for(Feat feat : allFeats())
        modifier = (Modifier)modifier.add(feat.skillModifier(inSkill));
    }

    return modifier;
  }

  public int totalSkillPoints()
  {
    int ranks = 0;

    // According to MM p. 301
    Optional<MonsterType> type = getCombinedMonsterType().get();
    if(type.isPresent())
      ranks = type.get().getSkillRanks();
    else
      ranks = MonsterType.UNKNOWN.getSkillRanks();

    ranks += Math.max(1, abilityModifier(Ability.INTELLIGENCE));

    if(getCombinedHitDie().get().isPresent())
      ranks *= getCombinedHitDie().get().get() + 3;

    return ranks;
  }

  public int totalUsedSkillPoints()
  {
    int ranks = 0;
    for(Skill skill : m_skills)
      ranks += skill.getRanks();

    return ranks;
  }

  public Modifier skillModifier(String inName)
  {
    Modifier modifier = new Modifier();
    for(Feat feat : allFeats())
      modifier = (Modifier)modifier.add(feat.skillModifier(inName));

    for(Quality quality : allQualities())
      modifier = (Modifier)modifier.add(quality.skillModifier(inName));

    return modifier;
  }

  public Annotated<List<BaseMonster.Group>> getCombinedOrganizations()
  {
    Annotated<List<BaseMonster.Group>> combined = new Annotated.List<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedOrganizations());

    return combined;
  }

  public Annotated<List<String>> getCombinedProficiencies()
  {
    Annotated.List<String> proficiencies = new Annotated.List<>();
    for(BaseEntry base : getBaseEntries())
      proficiencies.add(((BaseMonster)base).getCombinedProficiencies());

    return proficiencies;
  }

  public Annotated<List<String>> weaponProficiencies()
  {
    return getCombinedProficiencies();
  }

  public Annotated<List<BaseMonster.Attack>> getCombinedPrimaryAttacks()
  {
    Annotated<List<BaseMonster.Attack>> attacks = new Annotated.List<>();

    for(BaseEntry base : getBaseEntries())
      attacks.add(((BaseMonster)base).getCombinedPrimaryAttacks());

    return attacks;
  }

  public Annotated<Optional<Integer>> attackBonus(BaseMonster.Attack inAttack)
  {
    Annotated.Integer bonus = new Annotated.Bonus();

    Optional<Integer> baseAttack = getCombinedBaseAttack().get();
    if(baseAttack.isPresent())
      bonus.add(baseAttack.get(), "base attack");

    switch(inAttack.getStyle())
    {
      default:
      case UNKNOWN:
        break;

      case MELEE:
        // TODO: missing handling of weapon finesse with dexterity
        bonus.add(Monsters.abilityModifier(getCombinedStrength().get()),
                  "Strength");
        break;

      case RANGED:
        bonus.add(Monsters.abilityModifier(getCombinedDexterity().get()),
                  "Dexterity");
        break;
    }

    bonus.add(sizeModifier(), "size");

    for(Feat feat : getCombinedFeats().get())
      if(!feat.getQualifier().isPresent()
          || feat.getQualifier().get().equalsIgnoreCase(
          inAttack.getMode().toString()))
      {
        Modifier modifier = feat.attackModifier();
        if(modifier.hasValue())
          bonus.add(modifier.totalModifier(), feat.getName());
      }

    return bonus;
  }

  public Annotated<Optional<Integer>> secondaryAttackBonus(
      BaseMonster.Attack inAttack)
  {
    Annotated.Integer bonus = (Annotated.Integer)attackBonus(inAttack);
    bonus.add(-Monsters.SECONDARY_ATTACK_PENALTY, "secondary attack");

    return bonus;
  }

  public class NaturalAttack
  {
    public NaturalAttack(BaseMonster.Attack inAttack, boolean inPrimary)
    {
      m_attack = inAttack;
      m_primary = inPrimary;
    }

    private final BaseMonster.Attack m_attack;
    private final boolean m_primary;

    public int bonus()
    {
      Optional<Integer> combinedBase = getCombinedBaseAttack().get();

      int base = combinedBase.isPresent() ? combinedBase.get() : 0;
      if(hasFeat(Combat.FEAT_WEAPON_FINESSE)
          || m_attack.getStyle() == AttackStyle.RANGED)
        base += getDexterityModifier();
      else
      {
        base += getStrengthModifier();
        base += sizeModifier();
      }

      if (!m_primary)
        base += Combat.MULTIPLE_ATTACK_PENALTY;

      return base;
    }

    public BaseMonster.Attack getAttack()
    {
      return m_attack;
    }

    public Damage getDamage()
    {
      int strengthModifier = getStrengthModifier();
      if (!m_primary)
        strengthModifier /= 2;

      if (strengthModifier == 0)
        return m_attack.getDamage();

      return (Damage)m_attack.getDamage().add(new Damage(
          new Dice(0, 0, strengthModifier)));
    }
  }

  public List<NaturalAttack> naturalAttacks()
  {
    List<NaturalAttack> attacks = new ArrayList<>();

    for (BaseMonster.Attack attack : getCombinedPrimaryAttacks().get())
      attacks.add(new NaturalAttack(attack, true));

    for (BaseMonster.Attack attack : getCombinedSecondaryAttacks().get())
      attacks.add(new NaturalAttack(attack, false));

    return attacks;
  }

  public Damage damage(BaseMonster.Attack inAttack)
  {
    Damage damage = inAttack.getDamage();

    int strength = Monsters.abilityModifier(getCombinedStrength().get());
    if(strength > 0)
      damage = (Damage) damage.add(new Damage(new Dice(0, 0, strength)));

    return damage;
  }

  public Damage secondaryDamage(BaseMonster.Attack inAttack)
  {
    Damage damage = inAttack.getDamage();

    int strength =
        (int) (Monsters.abilityModifier(getCombinedStrength().get())
            * Monsters.SECONDARY_DAMAGE_FACTOR);
    if(strength > 0)
      damage = (Damage) damage.add(new Damage(new Dice(0, 0, strength)));

    return damage;
  }


  public Annotated<List<BaseMonster.Attack>> getCombinedSecondaryAttacks()
  {
    Annotated<List<BaseMonster.Attack>> attacks = new Annotated.List<>();

    for(BaseEntry base : getBaseEntries())
      attacks.add(((BaseMonster)base).getCombinedSecondaryAttacks());

    return attacks;
  }

  @Override
  public void setValues(Values inValues)
  {
    super.setValues(inValues);

    m_givenName = inValues.use("given_name", m_givenName);
    m_alignment = inValues.use("alignment", m_alignment, Alignment.PARSER);
    m_strength = inValues.use("strength", m_strength, Value.INTEGER_PARSER);
    m_constitution = inValues.use("constitution", m_constitution,
                                  Value.INTEGER_PARSER);
    m_dexterity = inValues.use("dexterity", m_dexterity,
                               Value.INTEGER_PARSER);
    m_intelligence = inValues.use("intelligence", m_intelligence,
                                  Value.INTEGER_PARSER);
    m_wisdom = inValues.use("wisdom", m_wisdom, Value.INTEGER_PARSER);
    m_charisma = inValues.use("charisma", m_charisma, Value.INTEGER_PARSER);
    m_feats = inValues.useEntries("feat", m_feats,
                                  new NestedEntry.Creator<Feat>()
                                  {
                                    @Override
                                    public Feat create()
                                    {
                                      return new Feat();
                                    }
                                  });
    m_hp = inValues.use("hp", m_hp, Value.INTEGER_PARSER);
    m_maxHP = inValues.use("max_hp", m_maxHP, Value.INTEGER_PARSER);
    m_qualities = inValues.useEntries("quality", m_qualities, Quality.CREATOR);
    m_skills = inValues.useEntries("skill", m_skills,
                                   new NestedEntry.Creator<Skill>()
                                   {
                                     @Override
                                     public Skill create()
                                     {
                                       return new Skill();
                                     }
                                   });
    m_languages = inValues.use("language", m_languages, Language.PARSER);
    m_personality = inValues.use("personality", m_personality);
  }

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

  /**
   * Get a monster's feat with the given name.
   *
   * @param inName the name (id) of the feat
   * @return the feat or absent if the monster does not have the feat
   */
  @Deprecated
  public Optional<Feat> getFeat(String inName)
  {
    for(Feat feat : allFeats())
      if(feat.getName().equalsIgnoreCase(inName))
        return Optional.of(feat);

    return Optional.absent();
  }

  public List<Feat> getFeats(String inName)
  {
    List<Feat> feats = new ArrayList<>();

    for(Feat feat : allFeats())
      if(feat.getName().equalsIgnoreCase(inName))
        feats.add(feat);

    return feats;
  }

  /**
   * Check whether the monster has a quality with the given name.
   *
   * @param inName the name (id) of the quality
   * @return true if the monster has the quality, false if not
   */
  public boolean hasQuality(String inName)
  {
    for(Quality quality : m_qualities)
      if(quality.getName().equalsIgnoreCase(inName))
        return true;

    return false;
  }

  /**
   * Get a named quality the monster has.
   *
   * @param inName the name (id) of the quality
   * @return the quality or absent if the monster does not have it
   */
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

  public Annotated<Optional<Terrain>> getCombinedTerrain()
  {
    Annotated<Optional<Terrain>> combined = new Annotated.Max<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedTerrain());

    return combined;
  }

  public Annotated<Optional<Climate>> getCombinedClimate()
  {
    Annotated<Optional<Climate>> combined = new Annotated.Max<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedClimate());

    return combined;
  }

  public Annotated<Optional<Rational>> getCombinedCr()
  {
    Annotated<Optional<Rational>> combined = new Annotated.Arithmetic<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedCr());

    return combined;
  }

  public Annotated<Optional<net.ixitxachitls.dma.values.enums.Treasure>>
  getCombinedTreasure()
  {
    Annotated<Optional<net.ixitxachitls.dma.values.enums.Treasure>> combined =
        new Annotated.Max<>();
    for(BaseEntry base : getBaseEntries())
      combined.add(((BaseMonster)base).getCombinedTreasure());

    return combined;
  }

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
   * Add a random treasure horde to this monster.
   *
   * @ param       inType     the type of treasure to generate
   * @ param       inCampaign the campaign to add the treasure from
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
//       Coins coins = treasure.coins(RANDOM.nextInt(100) + 1);

//       if(coins != null)
//         coins.roll(m_money);

//       Goods goods = treasure.goods(RANDOM.nextInt(100) + 1);

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

//       Items items = treasure.items(RANDOM.nextInt(100) + 1);

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

    if(m_givenName.isPresent())
      builder.setGivenName(m_givenName.get());

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

    builder.setHitPoints(m_hp);
    builder.setMaxHitPoints(m_maxHP);

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

    for(Language language : m_languages)
      builder.addLanguage(language.toProto());

    if(m_personality.isPresent())
      builder.setPersonality(m_personality.get());

    MonsterProto proto = builder.build();
    return proto;
  }

  /**
   * Set all the values from the given proto.
   *
   * @param inProto the proto with the values
   */
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof MonsterProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    MonsterProto proto = (MonsterProto)inProto;

    super.fromProto(proto.getBase());

    if(proto.hasGivenName())
      m_givenName = Optional.of(proto.getGivenName());

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

    m_feats.clear();
    for(FeatProto feat : proto.getFeatList())
      m_feats.add(Feat.fromProto(feat));

    m_qualities.clear();
    for(QualityProto quality : proto.getQualityList())
      m_qualities.add(Quality.fromProto(quality));

    m_hp = proto.getHitPoints();
    m_maxHP = proto.getMaxHitPoints();

    m_skills.clear();
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

    for(BaseMonsterProto.Language.Name language : proto.getLanguageList())
      m_languages.add(Language.fromProto(language));

    if(proto.hasPersonality())
      m_personality = Optional.of(proto.getPersonality());
  }

  @Override
  protected Message defaultProto()
  {
    return MonsterProto.getDefaultInstance();
  }

  public boolean shownAsWorn(Item inItem)
  {
    if(inItem.isArmor())
      return inList(inItem, getArmor(), 2);

    if(inItem.isAmmunition())
    {
      List<Item> weapons = getWeapons();
      for(int i = 0; i < weapons.size() && i < 4; i++)
        if(inList(inItem, weapons.get(i).availableAmmunition(),
                  Integer.MAX_VALUE))
          return true;

      return false;
    }

    if(inItem.isWeapon())
      return inList(inItem, getWeapons(), 4);

    return false;
  }

  private boolean inList(Item inItem, List<Item> inItems, int inMaxIndex)
  {
    for(int i = 0; i < inItems.size() && i < inMaxIndex; i++)
      if(inItems.get(i).getName().equals(inItem.getName()))
        return true;

    return false;
  }

  public List<Language> getLanguages() {
    return m_languages;
  }

  public Optional<String> getPersonality() {
    return m_personality;
  }

  public Annotated<List<Language>> combinedLanguages() {
    Annotated.List<Language> languages = new Annotated.List<>();
    Set<Language> learned = new HashSet<>();
    Set<Language> possible = new HashSet<>();
    for(BaseEntry base : getBaseEntries())
      for(BaseMonster.LanguageOption option
          : ((BaseMonster)base).getCombinedLanguages().get())
        if(!learned.contains(option.getLanguage()))
        {
          if(option.getModifier() == LanguageModifier.AUTOMATIC)
          {
            languages.addSingle(option.getLanguage(),
                                base.getName() + " (automatic)");
            learned.add(option.getLanguage());
          }
          else
          {
            possible.add(option.getLanguage());
          }
        }

    int allowed = learned.size() + abilityModifier(Ability.INTELLIGENCE);
    for(Language language : m_languages)
      if(!learned.contains(language))
      {
        List<String> comments = new ArrayList<>();
        if(learned.size() >= allowed)
          comments.add("too much");
        if(!possible.contains(language))
          comments.add("not in race list");

        languages.addSingle(language, "learned"
            + (comments.isEmpty() ? ""
            : " (" + Strings.COMMA_JOINER.join(comments) + ")"));

        learned.add(language);
      }

    return languages;
  }

  public String getBaseName()
  {
    List<String> names = new ArrayList<>();
    for(BaseEntry base : getBaseEntries())
      names.add(base.getName());

    return Strings.COMMA_JOINER.join(names);
  }

  public Optional<String> getGivenName()
  {
    return m_givenName;
  }

  @Override
  public void initialize(boolean inForce)
  {
    // setup hit points
    m_maxHP = randomHp();
    m_hp = m_maxHP;

    // setup items from base monster possessions
    m_createPossessionsOnSave = true;

    changed();
  }

  private int randomHp()
  {
    int hp = 0;
    for(BaseEntry base : getBaseEntries())
      hp += ((BaseMonster)base).randomHp();

    return hp;
  }

  @Override
  public boolean save()
  {
    boolean saved = super.save();

    // This has to be done after saving above to ensure we have a proper name
    // for the monsters (for parent).
    if(m_createPossessionsOnSave)
    {
      m_createPossessionsOnSave = false;
      m_possessions = new ArrayList<>();
      for(BaseEntry base : getBaseEntries())
        for(BaseMonster.Possession possession
            : ((BaseMonster)base).getPossessions())
        {
          Multimap<String, String> values = ArrayListMultimap.create();
          values.put("parent", getType().getLink() + "/" + getName());

          int count = possession.getCount().roll();
          boolean multiple = possession.getName().startsWith(Type.RANDOM_NAME);

          for (int i = 0; i < (multiple ? count : 1); i++)
          {
            Optional<Item> item = Item.TYPE.createNew(
                new EntryKey(Entry.TEMPORARY, Item.TYPE, getKey().getParent()),
                ImmutableList.of(possession.getName()),
                Optional.<String>absent(), Optional.of(new Values(values)));

            if(!item.isPresent())
              Log.warning("Could not create item " + possession.getName()
                              + " for " + getKey());
            else
            {
              if(!multiple)
                item.get().setMultiple(count);

              item.get().save();
            }
          }
        }
    }

    return saved;
  }
}
