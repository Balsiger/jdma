/******************************************************************************
 * Copyright (c) 2002-2013 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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


package net.ixitxachitls.dma.values;

import java.util.ArrayList;
import java.util.Random;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Values.DiceProto;
import net.ixitxachitls.util.Strings;

/**
 * A dice representation, with modifier.
 *
 * @file   NewDice.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */
public class Dice extends Value<DiceProto>
{
  /** The parser for dices. */
  public static class DiceParser extends Parser<Dice>
  {
    /** Create the parser. */
    public DiceParser()
    {
      super(1);
    }

    @Override
    public Optional<Dice> doParse(String inValue)
    {
      if(inValue.matches("^\\s*\\d+\\s*$"))
        return Optional.of(new Dice(0, 0, Integer.parseInt(inValue)));

      String []parts =
        Strings.getPatterns(inValue,
                            "^\\s*(?:(\\d+)+d(\\d+))?\\s*([+-]\\s*\\d+)?\\s*$");

      if(parts.length != 3)
        return Optional.absent();

      try
      {
        return Optional.of(new Dice
                           (parts[0] == null ? 0 : Integer.parseInt(parts[0]),
                            parts[1] == null ? 0 : Integer.parseInt(parts[1]),
                            parts[2] == null ? 0
                              : Integer.parseInt(parts[2].replace(" ", ""))));
      }
      catch(NumberFormatException e)
      {
        return Optional.absent();
      }
    }
  }

  public static class List
  {
    public List()
    {
      m_die = new ArrayList<>();
    }

    public List(Dice inValue)
    {
      this();

      m_die.add(inValue);
    }

    private List(java.util.List<Dice> inValues) {
      m_die = inValues;
    }

    private final java.util.List<Dice> m_die;

    public List add(Dice inValue)
    {
      java.util.List<Dice> die = new ArrayList<>();
      for(Dice dice : m_die)
        if(dice.canAdd(inValue))
          die.add(dice.add(inValue));
        else
          die.add(dice);

      return new List(die);
    }

    public List add(List inValues)
    {
      java.util.List<Dice> die = new ArrayList<>();
      for(Dice value : inValues.m_die)
        for(Dice dice : m_die)
          if(die.add(value))
            die.add(dice.add(value));
          else
            die.add(dice);

      return new List(die);
    }

    public List add(int inModifier)
    {
      java.util.List<Dice> die = new ArrayList<>();
      for(Dice dice : m_die)
        die.add(dice.add(inModifier));

      return new List(die);
    }

    public List addPerDice(int inModifier)
    {
      java.util.List<Dice> die = new ArrayList<>();
      for(Dice dice : m_die)
        die.add(dice.add(dice.getNumber() * inModifier));

      return new List(die);
    }

    @Override
    public String toString()
    {
      return Strings.COMMA_JOINER.join(m_die);
    }
  }

  /** Create a dice value.
   *
   * @param inNumber   the number of dices
   * @param inDice     the dice type (what dice)
   * @param inModifier the modifier to the dice value
   */
  public Dice(int inNumber, int inDice, int inModifier)
  {
    m_number = inNumber;
    m_dice = inDice;
    m_modifier = inModifier;
  }

    /** The number of dice to roll. */
  private final int m_number;

  /** The kind of dice to roll. */
  private final int m_dice;

  /** The modifier for the roll. */
  private final int m_modifier;

  /** The random generator. */
  private static final Random s_random = new Random();

  /** The default parser for dices. */
  public static final Parser<Dice> PARSER = new DiceParser();

  /**
   * Roll the dice and compute a result. This will always return a value
   * of at least 1.
   *
   * @return      the value rolled
   */
  public int roll()
  {
    int rolled = m_modifier;

    for (int i = 0; i < m_number; i++)
      rolled += s_random.nextInt(m_dice) + 1;

    return Math.max(1, rolled);
  }

  /**
   * Get the number stored (without escaping).
   *
   * @return      the number stored
   */
  public int getNumber()
  {
    return m_number;
  }

  /**
   * Get the dice stored (without escaping).
   *
   * @return      the dice stored
   */
  public int getDice()
  {
    return m_dice;
  }

  /**
   * Get the modifier stored (without escaping).
   *
   * @return      the modifier stored
   */
  public int getModifier()
  {
    return m_modifier;
  }

  /**
    * Get the maximal value that can result of this dice.
    *
    * @return      the maximal possible value
    */
  public long getMax()
  {
    return Math.max(1, m_number * m_dice + m_modifier);
  }

  /**
    * Get the minimal value that can result of this dice.
    *
    * @return      the minimal possible value
    */
  public long getMin()
  {
    return Math.max(1, m_number + m_modifier);
  }

  /**
   * Get the average value that can result of this dice.
   *
   * The minimal value is always 1.
   *
   * @return      the average value
   */
  public long getAverage()
  {
    return Math.max(1, m_number * (m_dice + 1) / 2 + m_modifier);
  }

  /**
   * Determine if this dice represents any random value or is basically static.
   *
   * @return   true if the die is random, false if not
   */
  public boolean isRandom()
  {
    return m_dice != 1 && m_dice != 0 && m_number != 0;
  }

  public boolean isZero()
  {
    return m_modifier == 0 && m_dice == m_number;
  }

  public boolean isOne()
  {
    return m_modifier == 1 && m_dice == m_number;
  }

  @Override
  public String toString()
  {
    String dice = m_number + "d" + m_dice;

    if(m_number == 0 || m_dice == 0)
      return formatModifier();

    if(m_modifier == 0)
      return m_number + "d" + m_dice;

    return m_number + "d" + m_dice + " " + formatModifier();
  }

  /** Format the modifier (with +).
   *
   * @return the formatted modifier
   */
  private String formatModifier()
  {
    if(m_modifier >= 0)
      return "+" + m_modifier;

    return "" + m_modifier;
  }

  /**
   * Add the current and given value and return it. The current value is not
   * changed.
   *
   * @param       inValue the value to add to this one
   *
   * @return      the added values
   */
  public Dice add(Dice inValue)
  {
    if(!canAdd(inValue))
      throw new UnsupportedOperationException("can only add same dice");

    int number = m_number + inValue.m_number;
    int dice = m_number > 0 ? m_dice : inValue.m_dice;
    int modifier = m_modifier + inValue.m_modifier;

    return new Dice(number, dice, modifier);
  }

  public boolean canAdd(Dice inValue)
  {
    return m_number == 0 || inValue.m_number == 0 || m_dice != inValue.m_dice;
  }

  public Dice add(int inModifier)
  {
    return new Dice(m_number, m_dice, m_modifier + inModifier);
  }

  /** Multiply the dice into a new dice.
   *
   * @param inFactor the multiplicator for the dice
   * @return the newly created dice as a multiplication of the current
   */
  public Dice multiply(int inFactor)
  {
    return new Dice(m_number * inFactor, m_dice, m_modifier * inFactor);
  }

  @Override
  public DiceProto toProto()
  {
    DiceProto.Builder builder = DiceProto.newBuilder();

    if(m_number > 0)
    {
      builder.setNumber(m_number);
      builder.setDice(m_dice);
    }

    if(m_modifier > 0)
      builder.setModifier(m_modifier);

    return builder.build();
  }

  /**
   * Create a new dice value with the values from the given proto.
   *
   * @param inProto the proto to read the values from
   * @return the newly crated dice
   */
  public static Dice fromProto(DiceProto inProto)
  {
    int number = 0;
    int dice = 0;
    int modifier = 0;

    if(inProto.hasNumber())
      number = inProto.getNumber();
    if(inProto.hasDice())
      dice = inProto.getDice();
    if(inProto.hasModifier())
      modifier = inProto.getModifier();

    return new Dice(number, dice, modifier);
  }

  //----------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Parsing tests. */
    @org.junit.Test
    public void parse()
    {
      assertEquals("parsing", "1d4", PARSER.parse("1d4").get().toString());
      assertEquals("parsing", "1d4 +2",
                   PARSER.parse(" 1d4   \n+ 2").get().toString());
      assertEquals("parsing", "1d4 -3",
                   PARSER.parse("  1d4  -  3").get().toString());
      assertEquals("parsing", "5d12", PARSER.parse("5d12").get().toString());
      assertEquals("parsing", "+3", PARSER.parse("  +3  ").get().toString());
      assertFalse("parsing", PARSER.parse("1d").isPresent());
      assertFalse("parsing", PARSER.parse("d5").isPresent());
      assertFalse("parsing", PARSER.parse("1 d 5 + 2").isPresent());
      assertFalse("parsing", PARSER.parse("1d4 ++3").isPresent());
      assertFalse("parsing", PARSER.parse("1d2 +").isPresent());
      assertFalse("parsing", PARSER.parse("2 - 3").isPresent());
    }
  }
}
