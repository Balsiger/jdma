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

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Values.CriticalProto;
import net.ixitxachitls.dma.proto.Values.RangeProto;
import net.ixitxachitls.util.Strings;

/**
 * A critical descriptor for a weapon.
 *
 * @file   NewCritical.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */
public class Critical extends Value.Arithmetic<CriticalProto>
{
  /** The parser for critical vaues. */
  public static final Parser<Critical> PARSER = new Parser<Critical>(1)
  {
    @Override
    protected Optional<Critical> doParse(String inValue)
    {
      if(inValue.trim().isEmpty())
        return Optional.absent();

      if("none".equalsIgnoreCase(inValue.trim()))
        return Optional.of(new Critical(1, 20));

      String []parts =
        Strings.getPatterns(inValue,
                            "^(?:\\s*(?:(\\d+)?\\s*-)?\\s*(\\d+)\\s*/\\s*)?"
                            + "(?:\\s*x\\s*(\\d+))?\\s*$");
      if(parts == null || parts.length == 0)
        return Optional.absent();

      try
      {
        int multiplier = 1;
        int threatLow = 20;
        if(parts[0] != null && parts[1] != null)
          threatLow = Integer.parseInt(parts[0]);

        if(parts[2] != null)
          multiplier = Integer.parseInt(parts[2]);

        return Optional.of(new Critical(multiplier, threatLow));
      }
      catch(NumberFormatException e)
      {
        return Optional.absent();
      }
    }
  };

  /**
   * Create a critical value.
   *
   * @param inMultiplier the critical multiplier (default x2).
   * @param inLowThreat the low number of the threat (high is always 20).
   */
  public Critical(int inMultiplier, int inLowThreat)
  {
    m_multiplier = inMultiplier;
    m_threatLow = inLowThreat;
  }

  /** The damage multiplier for the critical. */
  private final int m_multiplier;

  /** The low number of the threat range (high is always 20). */
  private final int m_threatLow;

  /**
   * Get the damage multiplier.
   *
   * @return the damage multiplier
   */
  public int getMultiplier()
  {
    return m_multiplier;
  }

  /** Get the low end of the threat range.
   *
   * @return get the low end of the threat range (high is always 20).
   */
  public int getLowThreat()
  {
    return m_threatLow;
  }

  @Override
  public String toString()
  {
    if(m_multiplier == 1)
      return "None";

    if(m_threatLow != 20)
      return m_threatLow + "-20" + "/x" + m_multiplier;

    return "x" + m_multiplier;
  }

  @Override
  public CriticalProto toProto()
  {
    CriticalProto.Builder builder = CriticalProto.newBuilder();
    if(m_threatLow != 20)
      builder.setThreat(RangeProto.newBuilder()
                        .setLow(m_threatLow)
                        .build());

    builder.setMultiplier(m_multiplier);

    return builder.build();
  }

  @Override
  public Value.Arithmetic<CriticalProto>
    add(Value.Arithmetic<CriticalProto> inValue)
  {
    if(!(inValue instanceof Critical))
      return this;

    Critical value = (Critical)inValue;

    if(value.m_threatLow == 2 && value.m_multiplier == 1)
      return doubled();

    if(m_threatLow == 2 && value.m_multiplier == 1)
      return value.doubled();

    if(m_threatLow == value.m_threatLow && m_multiplier == value.m_multiplier)
      return this;

    return new Critical(Math.max(m_multiplier, value.m_multiplier),
                           Math.min(m_threatLow, value.m_threatLow));
  }

  @Override
  public Value.Arithmetic<CriticalProto> multiply(int inFactor)
  {
    if(inFactor >= 2)
      return doubled();

    return this;
  }

  @Override
  public Value.Arithmetic<CriticalProto> multiply(Rational inFactor)
  {
    if(inFactor.asDouble() > 2.0)
      return doubled();

    return this;
  }

  /**
   * Get a critical with a double threat range than this one.
   *
   * @return a new critical with double the threat range
   */
  public Critical doubled()
  {
    int threatLow = 2 * m_threatLow - 20;
    return new Critical(m_multiplier, threatLow);
  }

  /**
   * Create a new critical value with the values from the given proto.
   *
   * @param inProto the proto to read the values from
   * @return the newly created critical
   */
  public static Critical fromProto(CriticalProto inProto)
  {
    int threatLow = 20;

    if(inProto.hasThreat())
    {
      threatLow = (int)inProto.getThreat().getLow();
    }

    int multiplier = 1;
    if(inProto.hasMultiplier())
      multiplier = inProto.getMultiplier();

    return new Critical(multiplier, threatLow);
  }

  @Override
  public boolean canAdd(Value.Arithmetic<CriticalProto> inValue)
  {
    return inValue instanceof Critical;
  }

  //----------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Testing init. */
    @org.junit.Test
    public void parse()
    {
      assertEquals("parse", "None", PARSER.parse(" none  ").get().toString());
      assertEquals("parse", "x3", PARSER.parse(" x 3  ").get().toString());
      assertEquals("parse", "x3", PARSER.parse(" 20/x3  ").get().toString());
      assertEquals("parse", "19-20/x2",
                   PARSER.parse(" 19 - 20 / x 2 ").get().toString());
      assertEquals("parse", "12-20/x5",
                   PARSER.parse("12-19/x5").get().toString());
      assertFalse("parse", PARSER.parse("/").isPresent());
      assertFalse("parse", PARSER.parse("").isPresent());
      assertFalse("parse", PARSER.parse("12").isPresent());
      assertFalse("parse", PARSER.parse("x").isPresent());
      assertFalse("parse", PARSER.parse("19-20 x3").isPresent());
      assertFalse("parse", PARSER.parse("19 - x 4").isPresent());
    }
  }
}
