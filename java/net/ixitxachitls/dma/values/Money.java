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

package net.ixitxachitls.dma.values;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a monetary value (in game currency).
 *
 * @file          Money.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Money extends Units<Money>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Money --------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;


  /**
   * Construct the money object with an undefined value.
   *
   */
  public Money()
  {
    super(s_sets, 1);
    m_template = "money";
  }

  //........................................................................
  //------------------------------- Money --------------------------------

  /**
   * Construct the money object with real money values. This is just a
   * convenience method.
   *
   * @param       inCopper   the amount of copper pieces
   * @param       inSilver   the amount of silver pieces
   * @param       inGold     the amount of gold pieces
   * @param       inPlatinum the amount of platinum pieces
   *
    */
  public Money(int inCopper, int inSilver, int inGold, int inPlatinum)
  {
    super(new Rational [] { new Rational(inPlatinum), new Rational(inGold),
                            new Rational(inSilver), new Rational(inCopper), },
          s_sets, s_sets[0], 1);

    if(inCopper < 0)
      throw new IllegalArgumentException("can only set positive values");

    if(inSilver < 0)
      throw new IllegalArgumentException("can only set positive values");

    if(inGold < 0)
      throw new IllegalArgumentException("can only set positive values");

    if(inPlatinum < 0)
      throw new IllegalArgumentException("can only set positive values");

    m_template = "money";
  }

  //........................................................................

  //------------------------------ createNew -------------------------------

  /**
   * Create a new list with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar list, but without any contents
   *
   */
  @Override
  public Money create()
  {
    return super.create(new Money());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The unit definition. */
  private static String s_definition =
    Config.get("/rules/money.units",
               "1/1 : D&D = 10/1   : pp : "
               + "platinum|platinums|platinum piece|platinum pieces,"
               + "             1/1   : gp : gold|golds|gold piece|gold pieces,"
               + "             1/10  : sp : "
               + "silver|silvers|silver piece|silver pieces,"
               + "             1/100 : cp : "
               + "copper|coppers|copper piece|copper pieces.");

  /** The set definition. */
  private static Set []s_sets = parseDefinition(s_definition);

  /** The type for the standard coins. */
  public enum Coin
  {
    /** Copper pieces. */
    copper(3),

    /** Silver pieces. */
    silver(2),

    /** Gold pieces. */
    gold(1),

    /** Platinum pieces. */
    platinum(0);

    /** Create the coin object.
     *
     * @param inIndex the index in the value array that is used
     *
     */
    private Coin(int inIndex)
    {
      m_index = inIndex;
    }

    /** The index for this coin in the values array. */
    private int m_index;

    /** Get the index into the value array for this type of coin.
     *
     * @return the index
     *
     */
    public int index()
    {
      return m_index;
    }
  }

  /** The grouping. */
  protected static final Group<Money, Long, String> s_grouping =
    new Group<Money, Long, String>(new Group.Extractor<Money, Long>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Long extract(Money inValue)
        {
          if(inValue == null)
            throw new IllegalArgumentException("must have a value here");

          return (long)(inValue.getAsGold().getValue() * 100);
        }
      }, new Long [] { 1L, 1 * 10L, 1 * 100L, 10 * 100L, 100 * 100L,
                       1000 * 100L, 2000 * 100L, 4000 * 100L,
                       1000000 * 100L, },
                                   new String []
        { "1 cp", "1 sp", "1 gp", "10 gp", "100 gp", "1000 gp", "2000 gp",
          "4000 gp", "100000 gp", "Infinite", }, "$undefined$");


  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getCopper ------------------------------

  /**
   * Get the amount of coppers.
   *
   * @return      the amount of coppers
   *
   */
  public int getCopper()
  {
    if(!isDefined())
      return 0;

    if(m_set == m_sets[0])
      if(m_values[3] == null)
        return 0;
      else
        return (int)m_values[3].getValue();

    return (toSet(0, true)).getCopper();
  }

  //........................................................................
  //------------------------------- getSilver ------------------------------

  /**
   * Get the amount of silvers.
   *
   * @return      the amount of silver
   *
   */
  public int getSilver()
  {
    if(!isDefined())
      return 0;

    if(m_set == m_sets[0])
      if(m_values[2] == null)
        return 0;
      else
        return (int)m_values[2].getValue();

    return (toSet(0, true)).getSilver();
  }

  //........................................................................
  //-------------------------------- getGold -------------------------------

  /**
   * Get the amount of golds.
   *
   * @return      the amount of golds
   *
   */
  public int getGold()
  {
    if(!isDefined())
      return 0;

    if(m_set == m_sets[0])
      if(m_values[1] == null)
        return 0;
      else
        return (int)m_values[1].getValue();

    return (toSet(0, true)).getSilver();
  }

  //........................................................................
  //------------------------------ getPlatinum -----------------------------

  /**
   * Get the amount of platinums.
   *
   * @return      the amount of platinums
   *
   */
  public int getPlatinum()
  {
    if(!isDefined())
      return 0;

    if(m_set == m_sets[0])
      if(m_values[0] == null)
        return 0;
      else
        return (int)m_values[0].getValue();

    return (toSet(0, true)).getSilver();
  }

  //........................................................................

  //------------------------------ getAsGold -------------------------------

  /**
   * Get the whole value but as if it was converted to all copper pieces.
   *
   * @return      the total value in copper pieces
   *
   */
  public Rational getAsGold()
  {
    if(!isDefined())
      return new Rational(0);

    if(m_set == m_sets[0])
      return getAsBase();

    return toSet(0, false).getAsBase();
  }

  //........................................................................
  //----------------------------- collectData ------------------------------

  /**
   * Collect the data available for printing the value.
   *
   * @param       inEntry    the entry this value is in
   * @param       inRenderer the renderer to render sub values
   *
   * @return      the data as a map
   *
   */
  // @Override
  // public Map<String, Object> collectData(AbstractEntry inEntry,
  //                                        SoyRenderer inRenderer)
  // {
  //   Map<String, Object> data = super.collectData(inEntry, inRenderer);

  //   Rational gold = getAsGold();
  //   if(gold.compare(getGold()) != 0)
  //     data.put("gold", new SoyValue("as gold", gold, inEntry, inRenderer));

  //   return data;
  // }

  //........................................................................

  //------------------------------- doGroup --------------------------------

  /**
   * Return the group this value belongs to.
   *
   * @return      a string denoting the group this value is in
   *
   */
  @Override
  protected String doGroup()
  {
    return s_grouping.group(this);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------ asStandard ------------------------------

  /**
   * Set the standard money value.
   *
   * @param       inPlatinum the number of platinum coins
   * @param       inGold     the number of gold coins
   * @param       inSilver   the number of silver coins
   * @param       inCopper   the number of copper coins
   *
   * @return      a new money value
   *
   */
  public Money asStandard(@Nullable Rational inPlatinum,
                          @Nullable Rational inGold,
                          @Nullable Rational inSilver,
                          @Nullable Rational inCopper)
  {
    return as(new Rational [] { inPlatinum, inGold, inSilver, inCopper }, 0);
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the given amount of coins to this value. You can subtract values by
   * giving a negative value.
   *
   * @param       inCoin  the type of coin to add
   * @param       inValue the value to add (use a negative value to subtract)
   *
   * @return      true if the coins were added, false if it was not possible.
   *
   */
  public Money add(Coin inCoin, int inValue)
  {
    // nothing defined yet, thus cannot subtract values
    if((m_values == null || m_values[inCoin.index()] == null) && inValue < 0)
      throw new IllegalArgumentException("invalid values given");

    Money result = asStandard(null, null, null, null);

    if(m_values != null)
      result.m_values = m_values;

    assert m_values != null : "values should not be null";

    // can we subtract the given amount of coins
    if(m_values[inCoin.index()] != null
       && m_values[inCoin.index()].compare(-1L * inValue) < 0)
      throw new IllegalArgumentException("too much money to subtract");

    if(m_values[inCoin.index()] == null)
      result.m_values[inCoin.index()] = new Rational(inValue);
    else
      result.m_values[inCoin.index()] = m_values[inCoin.index()].add(inValue);

    return result;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** Testing init. */
    @org.junit.Test
    public void init()
    {
      Money value = new Money();

      // undefined value
      assertEquals("not undefined at start", false, value.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   value.toString());
      assertEquals("undefined value not correct", 0.0,
                   value.getAsGold().getValue(), 0.001);

      // now with some value
      value = new Money(1, 2, 3, 4);

      assertEquals("not defined after setting", true, value.isDefined());
      assertEquals("copper",   1, value.getCopper());
      assertEquals("silver",   2, value.getSilver());
      assertEquals("gold",     3, value.getGold());
      assertEquals("platinum", 4, value.getPlatinum());
      assertEquals("total",    43.21, value.getAsGold().getValue(), 0.001);
      assertEquals("output", "4 pp 3 gp 2 sp 1 cp",
                   value.toString());

      value =
        value.asStandard(new Rational(1, 2, 3), null, new Rational(5), null);

      assertEquals("output", "1 2/3 pp 5 sp", value.toString());

      Value.Test.createTest(value);
    }

    //......................................................................
    //----- add ------------------------------------------------------------

    /** Add some values. */
    @org.junit.Test
    public void add()
    {
      Money value = new Money(1, 2, 3, 4);

      value = value.add(Coin.platinum, 10);
      assertEquals("add", "14 pp 3 gp 2 sp 1 cp", value.toString());

      value = value.add(Coin.silver, 5);
      assertEquals("add", "14 pp 3 gp 7 sp 1 cp", value.toString());

      value = value.add(Coin.copper, -1);
      assertEquals("add", "14 pp 3 gp 7 sp", value.toString());

      try
      {
        value = value.add(Coin.copper, -5);
        assertNull("should not have been read", value);
        fail("expected an illegal argument exception");
      }
      catch(IllegalArgumentException e)
      {
        // expected
      }
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      String []tests =
        {
          "simple", "5 sp", "5 sp", null,
          "multi", "3 gp 1 sp 5 cp", "3 gp 1 sp 5 cp", null,
          "double", "3 gp 5 gp", "8 gp", null,

          "whites",
          "3 gp 5sp 1 \n      pp 4 \ncp  2    gp\n",
          "1 pp 5 gp 5 sp 4 cp", null,

          "other", "5 sp hello", "5 sp", " hello",
          "zero", "1 pp 0 sp", "1 pp", null,
          "other 2", "3 gp 4 cp a 9pp", "3 gp 4 cp", " a 9pp",
          "none", "hello", null, "hello",
          "empty", "", null, null,
          "invalid", "a5 gp", null, "a5 gp",
          "invalid 2", "2 tons", null, "2 tons",
          "number only", "42 ", null, "42 ",
          "negative", "-5 gp", null, "-5 gp",
          "negative multi", "3 sp -5 gp", "3 sp", " -5 gp",
        };

      Value.Test.readTest(tests, new Money());
    }

    //......................................................................
    //----- coin ----------------------------------------------------------

    /** Check coins. */
    @org.junit.Test
    public void coin()
    {
      assertEquals("coin", 0, Coin.platinum.index());
      assertEquals("coin", "platinum", Coin.platinum.toString());
      assertEquals("coin", 1, Coin.gold.index());
      assertEquals("coin", "gold", Coin.gold.toString());
      assertEquals("coin", 2, Coin.silver.index());
      assertEquals("coin", "silver", Coin.silver.toString());
      assertEquals("coin", 3, Coin.copper.index());
      assertEquals("coin", "copper", Coin.copper.toString());
    }

    //......................................................................
  }

  //........................................................................
}
