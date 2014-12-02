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

package net.ixitxachitls.dma.values.enums;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Entries.BaseItemProto;
import net.ixitxachitls.dma.values.Parser;
import net.ixitxachitls.dma.values.Rational;
import net.ixitxachitls.dma.values.SizeModifier;

/** The possible sizes in the game. */
public enum Size implements Named, net.ixitxachitls.dma.values.enums.Short
{
  /**
   * This is an unknown size.
   */
  UNKNOWN("Unknown", "U", 0, 0, Rational.ZERO, 0, 0, 0,
          BaseItemProto.Size.UNKNOWN_SIZE),

  /**
   * The smallest size.
   */
  FINE("Fine", "F", 0, 0, new Rational(0, 1, 2), 0, 8, -16,
       BaseItemProto.Size.FINE),

  /**
   * A very small size.
   */
  DIMINUTIVE("Diminutive", "D", 0, 0, Rational.ONE, 0, 4, -12,
             BaseItemProto.Size.DIMINUTIVE),

  /**
   * Smaller than small.
   */
  TINY("Tiny", "T", 0, 0, new Rational(2, 1, 2), 0, 2, -8,
       BaseItemProto.Size.TINY),

  /**
   * Just small.
   */
  SMALL("Small", "S", 0, 5, Rational.FIVE, 10, 1, -4,
        BaseItemProto.Size.SMALL),

  /**
   * This is the medium size.
   */
  MEDIUM("Medium-size", "M", 5, 5, Rational.FIVE, 20, 0, 0,
         BaseItemProto.Size.MEDIUM),

  /**
   * Simply large.
   */
  LARGE("Large", "L", 5, 10, Rational.TEN, 30, -1, 4,
        BaseItemProto.Size.LARGE),

  /**
   * Larger than large.
   */
  HUGE("Huge", "H", 10, 15, Rational.FIFTEEN, 40, -2, 8,
       BaseItemProto.Size.HUGE),

  /**
   * Really large.
   */
  GARGANTUAN("Gargantuan", "G", 15, 20, Rational.TWENTY, 60, -4, 12,
             BaseItemProto.Size.GARGANTUAN),

  /**
   * This is the biggest size.
   */
  COLOSSAL("Colossal", "C", 20, 30, Rational.THIRTY, 80, -8, 16,
           BaseItemProto.Size.COLOSSAL);

  /**
   * The value's name.
   */
  private String m_name;

  /**
   * The value's short name.
   */
  private String m_short;

  /**
   * The reach for a long creature.
   */
  private int m_reachLong;

  /**
   * The reach for a tall creature.
   */
  private int m_reachTall;

  /**
   * The space required for this size.
   */
  private Rational m_space;

  /**
   * The bonus hit points for a construct of this size.
   */
  private int m_construct;

  /**
   * The size modifier for armor class and attacks.
   */
  private int m_modifier;

  /**
   * The size modifier for grappling.
   */
  private int m_grapple;

  /**
   * The proto enum value.
   */
  private BaseItemProto.Size m_proto;

  /**
   * The parser for item sizes.
   */
  public static final Parser<Size> PARSER =
      new Parser<Size>(1)
      {
        @Override
        public Optional<Size> doParse(String inValue)
        {
          return Size.fromString(inValue);
        }
      };

  /**
   * Create the name.
   *
   * @param inName      the name of the value
   * @param inShort     the short name of the value
   * @param inReachLong the reach for a long creature
   * @param inReachTall the reach for a tall creature
   * @param inSpace     the space for a creature this size
   * @param inConstruct the bonus hit points for a construct
   * @param inModifier  the armor class and attack bonus for size
   * @param inGrapple   the grapple modifier for size
   * @param inProto     the proto enum value
   */
  private Size(String inName, String inShort, int inReachLong,
               int inReachTall, Rational inSpace, int inConstruct, int inModifier,
               int inGrapple, BaseItemProto.Size inProto)
  {
    m_name = inName;
    m_short = inShort;
    m_reachLong = inReachLong;
    m_reachTall = inReachTall;
    m_space = inSpace;
    m_construct = inConstruct;
    m_modifier = inModifier;
    m_grapple = inGrapple;
    m_proto = inProto;
  }

  /**
   * Get the name of the value.
   *
   * @return the name of the value
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
  @Override
  public String getShort()
  {
    return m_short;
  }

  /**
   * Compute another size based on the difference given. Subtraction can be
   * done by giving a negative value.
   *
   * @param inDifference the difference to compute with
   * @return the newly calculated size
   */
  public Size add(int inDifference)
  {
    return values()[ordinal() + inDifference];
  }

  /**
   * Compute the difference between the two sizes (as categories).
   *
   * @param inOther the other size to compare to
   * @return the size difference
   */
  public int difference(Size inOther)
  {
    if(inOther == null)
      throw new IllegalArgumentException("must have another value here");

    return ordinal() - inOther.ordinal();
  }

  /**
   * Check if the given size is bigger than the current one.
   *
   * @param inOther the other size to compare to
   * @return true if this is bigger than the other, false else
   */
  public boolean isBigger(Size inOther)
  {
    if(inOther == null)
      throw new IllegalArgumentException("must have another value here");

    return ordinal() > inOther.ordinal();
  }

  /**
   * Check if the given size is smaller than the current one.
   *
   * @param inOther the other size to compare to
   * @return true if this is smaller than the other, false else
   */
  public boolean isSmaller(Size inOther)
  {
    if(inOther == null)
      throw new IllegalArgumentException("must have another value here");

    return ordinal() < inOther.ordinal();
  }

  /**
   * Get the reach for a creature of this size.
   *
   * @param inModifier the modifier if the creature is more tall or long
   * @return the reach in feet
   */
  public int reach(SizeModifier inModifier)
  {
    if(inModifier == SizeModifier.TALL)
      return m_reachTall;

    return m_reachLong;
  }

  /**
   * Get the space that is required for this size in feet.
   *
   * @return the space in feet
   */
  public Rational space()
  {
    return m_space;
  }

  /**
   * Get the bonus hit points for size for constructs.
   *
   * @return the number of bonus hit points
   */
  public int construct()
  {
    return m_construct;
  }

  /**
   * Get the general modifier for armor class and attack for this size.
   *
   * @return the armor class or attack modifier for size
   */
  public int modifier()
  {
    return m_modifier;
  }

  /**
   * Get the modifier for grappling.
   *
   * @return the grappling modifier for size
   */
  public int grapple()
  {
    return m_grapple;
  }

  /**
   * Get the proto value for this value.
   *
   * @return the proto enum value
   */
  public BaseItemProto.Size toProto()
  {
    return m_proto;
  }

  /**
   * Get the group matching the given proto value.
   *
   * @param inProto the proto value to look for
   * @return the matched enum (will throw exception if not found)
   */
  public static Size fromProto(BaseItemProto.Size inProto)
  {
    for(Size size : values())
      if(size.m_proto == inProto)
        return size;

    throw new IllegalStateException("invalid proto size: " + inProto);
  }

  /**
   * All the possible names for the layout.
   *
   * @return the possible names
   */
  public static List<String> names()
  {
    List<String> names = new ArrayList<>();

    for(Size size : values())
      names.add(size.getName());

    return names;
  }

  /**
   * Get the layout matching the given text.
   */
  public static Optional<Size> fromString(String inText)
  {
    for(Size size : values())
      if(size.m_name.equalsIgnoreCase(inText))
        return Optional.of(size);

    return Optional.absent();
  }

  //----------------------------------------------------------------------------

  /**
   * The test.
   */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Check size values. */
    @org.junit.Test
    public void size()
    {
      assertEquals("add", Size.MEDIUM, Size.TINY.add(2));
      assertEquals("add", Size.LARGE, Size.SMALL.add(2));
      assertEquals("add", Size.GARGANTUAN, Size.SMALL.add(4));
      assertEquals("add", Size.SMALL, Size.GARGANTUAN.add(-4));

      assertEquals("difference", 1, Size.TINY.difference(Size.DIMINUTIVE));
      assertEquals("difference", -1, Size.TINY.difference(Size.SMALL));
      assertEquals("difference", -6, Size.TINY.difference(Size.COLOSSAL));

      assertTrue("bigger", Size.SMALL.isBigger(Size.TINY));
      assertTrue("bigger", Size.LARGE.isBigger(Size.SMALL));
      assertTrue("bigger", Size.GARGANTUAN.isBigger(Size.HUGE));
      assertFalse("bigger", Size.HUGE.isBigger(Size.GARGANTUAN));
      assertFalse("bigger", Size.SMALL.isBigger(Size.MEDIUM));
      assertFalse("bigger", Size.TINY.isBigger(Size.GARGANTUAN));

      assertFalse("smaller", Size.SMALL.isSmaller(Size.TINY));
      assertFalse("smaller", Size.LARGE.isSmaller(Size.SMALL));
      assertFalse("smaller", Size.GARGANTUAN.isSmaller(Size.HUGE));
      assertTrue("smaller", Size.HUGE.isSmaller(Size.GARGANTUAN));
      assertTrue("smaller", Size.SMALL.isSmaller(Size.MEDIUM));
      assertTrue("smaller", Size.TINY.isSmaller(Size.GARGANTUAN));

      assertEquals("reach", 5, Size.SMALL.reach(SizeModifier.TALL));
      assertEquals("reach", 0, Size.SMALL.reach(SizeModifier.LONG));
      assertEquals("reach", 5, Size.MEDIUM.reach(SizeModifier.TALL));
      assertEquals("reach", 5, Size.MEDIUM.reach(SizeModifier.LONG));
      assertEquals("reach", 10, Size.LARGE.reach(SizeModifier.TALL));
      assertEquals("reach", 5, Size.LARGE.reach(SizeModifier.LONG));
      assertEquals("reach", 20, Size.GARGANTUAN.reach(SizeModifier.TALL));
      assertEquals("reach", 15, Size.GARGANTUAN.reach(SizeModifier.LONG));
    }
  }
}