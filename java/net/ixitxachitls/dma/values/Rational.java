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

import java.math.BigInteger;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Values.RationalProto;
import net.ixitxachitls.util.Strings;

/**
 * A rational value, i.e. 2 1/2 or similar.
 *
 * @file   NewRational.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Rational extends Value.Arithmetic<RationalProto>
{
  /** The parser for ration values. */
  public static class RationalParser extends Parser<Rational>
  {
    /** Create the parser. */
    public RationalParser()
    {
      super(1);
    }

    @Override
    public Optional<Rational> doParse(String inValue)
    {
      String [] parts =
        Strings.getPatterns(inValue,
                            "^\\s*(\\d+)?\\s*(?:(\\d+)\\s*/\\s*(\\d+))?$");

      if(parts.length != 3)
        return Optional.absent();

      try
      {
        return Optional.of(new Rational
          (parts[0] == null ? 0 : Integer.parseInt(parts[0]),
           parts[1] == null ? 0 : Integer.parseInt(parts[1]),
           parts[2] == null ? 0 : Integer.parseInt(parts[2])));
      }
      catch(IllegalArgumentException e)
      {
        return Optional.absent();
      }
    }
  }

  /**
   * Create a rational with specific values.
   *
   * @param  inLeader      the number before the fraction
   * @param  inNominator   the nominator (above the line)
   * @param  inDenominator the denominator (below the line)
   */
  public Rational(int inLeader, int inNominator, int inDenominator)
  {
    if(inDenominator == 0 && inNominator != 0)
      throw new IllegalArgumentException("denominator cannot be 0");

    m_leader = inLeader;
    m_nominator = inNominator;
    m_denominator = inDenominator;
  }

  /** The parser. */
  public static final Parser<Rational> PARSER = new RationalParser();

  /** The rational for 0. */
  public static final Rational ZERO = new Rational(0, 0, 0);

  /** The rational for 1. */
  public static final Rational ONE = new Rational(1, 0, 0);

  /** The rational for 5. */
  public static final Rational FIVE = new Rational(5, 0, 0);

  /** The rational for 10. */
  public static final Rational TEN = new Rational(10, 0, 0);

  /** The rational for 15. */
  public static final Rational FIFTEEN = new Rational(15, 0, 0);

  /** The rational for 20. */
  public static final Rational TWENTY = new Rational(20, 0, 0);

  /** The rational for 30. */
  public static final Rational THIRTY = new Rational(30, 0, 0);

  /** The leading value, before the fraction. */
  private final int m_leader;

  /** The nominator (above line). */
  private final int m_nominator;

  /** The denominator (below line). */
  private final int m_denominator;

  /**
   * Get the leading value, before the fraction.
   *
   * @return the leading value
   */
  public int getLeader()
  {
    return m_leader;
  }

  /**
   * Get the nominator (number above the line).
   *
   * @return the nominator
   */
  public int getNominator()
  {
    return m_nominator;
  }

  /**
   * Get the denominator (number below the line).
   *
   * @return the denominator
   */
  public int getDenominator()
  {
    return m_denominator;
  }

  /**
   * Convert the rational into a double value.
   *
   * @return the rational as double
   */
  public double asDouble()
  {
    if(m_nominator == 0 || m_denominator == 0)
      return m_leader;

    return m_leader + m_nominator * 1.0 / m_denominator;
  }

  @Override
  public String toString()
  {
    if(m_nominator == 0)
      return "" + m_leader;

    if(m_leader == 0)
      return m_nominator + "/" + m_denominator;

    return m_leader + " " + m_nominator + "/" + m_denominator;
  }

  @Override
  public RationalProto toProto()
  {
    RationalProto.Builder builder = RationalProto.newBuilder();

    if (m_leader != 0)
      builder.setLeader(m_leader);

    if (m_nominator != 0)
    {
      builder.setNominator(m_nominator);
      builder.setDenominator(m_denominator);
    }

    return builder.build();
  }

  /**
   * Convert the proto into a rational.
   *
   * @param  inProto the proto to convert
   * @return the converted rational
   */
  public static Rational fromProto(RationalProto inProto)
  {
    return new Rational(inProto.getLeader(), inProto.getNominator(),
                           inProto.getDenominator());
  }

  @Override
  public Value.Arithmetic<RationalProto>
    add(Value.Arithmetic<RationalProto> inValue)
  {
    if(!(inValue instanceof Rational))
      throw new IllegalArgumentException("can only add another rational value");

    Rational value = (Rational)inValue;
    return new Rational(m_leader + value.m_leader,
                           m_nominator * value.m_denominator
                           + value.m_nominator * m_denominator,
                           m_denominator * value.m_denominator).simplify();
  }

  /**
   * Simplify into a new rational. This mainly simplifies fractions,
   * e.g. 2/4 to 1/2. This does _NOT_ change this rational.
   *
   * @return the new, simplified rational
   */
  public Rational simplify()
  {
    if(m_nominator == 0 || m_denominator == 0)
      return this;

    int leader = m_leader + m_nominator / m_denominator;
    int nominator = m_nominator % m_denominator;
    int denominator = m_denominator;

    int common = BigInteger.valueOf(nominator)
      				     .gcd(BigInteger.valueOf(m_denominator))
      				     .intValue();

    nominator /= common;
    denominator /= common;

    if(denominator == 1)
      return new Rational(leader + nominator, 0, 0);

    return new Rational(leader, nominator, denominator);
  }

  @Override
  public boolean canAdd(Value.Arithmetic<RationalProto> inValue)
  {
    return inValue instanceof Rational;
  }

  @Override
  public Value.Arithmetic<RationalProto> multiply(int inFactor)
  {
    return new Rational(m_leader * inFactor, m_nominator * inFactor,
                           m_denominator).simplify();
  }

  //---------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Parsing tests. */
    @org.junit.Test
    public void parse()
    {
      assertEquals("parsing", "1 1/2", PARSER.parse("1 1/2").get().toString());
      assertEquals("parsing", "1 1/2",
                   PARSER.parse("1   1  /   2").get().toString());
      assertEquals("parsing", "1", PARSER.parse("1").get().toString());
      assertEquals("parsing", "1/2", PARSER.parse("1/2").get().toString());
      assertFalse("parsing", PARSER.parse("1/0").isPresent());
      assertEquals("parsing", "2/4", PARSER.parse("2/4").get().toString());
      assertEquals("parsing", "0", PARSER.parse("0/1").get().toString());
      assertFalse("parsing", PARSER.parse("1 1").isPresent());
      assertFalse("parsing", PARSER.parse("1/").isPresent());
    }

    /** Printing tests. */
    @org.junit.Test
    public void printing()
    {
      assertEquals("printing", "0", new Rational(0, 0, 0).toString());
      assertEquals("printing", "1", new Rational(1, 0, 0).toString());
      assertEquals("printing", "1 1/2", new Rational(1, 1, 2).toString());
      assertEquals("printing", "1 1/1", new Rational(1, 1, 1).toString());
      assertEquals("printing", "2 26/13", new Rational(2, 26, 13).toString());
    }

    /** Simplifying tests. */
    @org.junit.Test
    public void simplify()
    {
      assertEquals("simplify", "0",
                   new Rational(0, 0, 0).simplify().toString());
      assertEquals("simplify", "1",
                   new Rational(1, 0, 0).simplify().toString());
      assertEquals("simplify", "1 1/2",
                   new Rational(1, 1, 2).simplify().toString());
      assertEquals("simplify", "2",
                   new Rational(1, 1, 1).simplify().toString());
      assertEquals("simplify", "3 1/2",
                   new Rational(3, 4, 8).simplify().toString());
      assertEquals("simplify", "1 1/7",
                   new Rational(1, 7, 49).simplify().toString());
      assertEquals("simplify", "4",
                   new Rational(2, 26, 13).simplify().toString());
    }

    /** Tests for addition. */
    @org.junit.Test
    public void add()
    {
      assertEquals("add", "2",
                   new Rational(1, 0, 0).add(new Rational(1, 0, 0))
                   .toString());
      assertEquals("add", "2",
                   new Rational(1, 1, 2).add(new Rational(0, 1, 2))
                   .toString());
      assertEquals("add", "2 13/15",
                   new Rational(1, 2, 3).add(new Rational(1, 1, 5))
                   .toString());
    }
  }
}
