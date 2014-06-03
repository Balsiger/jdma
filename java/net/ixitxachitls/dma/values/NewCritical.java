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
public class NewCritical extends NewValue.Arithmetic<CriticalProto>
{
  public static final Parser<NewCritical> PARSER = new Parser<NewCritical>(1)
  {
    @Override
    protected Optional<NewCritical> doParse(String inValue)
    {
      if(inValue.trim().isEmpty())
        return Optional.absent();

      if("none".equalsIgnoreCase(inValue.trim()))
        return Optional.of(new NewCritical(1, 20));

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
        {
          threatLow = Integer.parseInt(parts[0]);
        }

        if(parts[2] != null)
          multiplier = Integer.parseInt(parts[2]);

        return Optional.of(new NewCritical(multiplier, threatLow));
      }
      catch(NumberFormatException e)
      {
        return Optional.absent();
      }
    }
  };

  public NewCritical(int inMultiplier, int inLowThreat)
  {
    m_multiplier = inMultiplier;
    m_threatLow = inLowThreat;
  }

  private final int m_multiplier;
  private final int m_threatLow;

  public int getMultiplier()
  {
    return m_multiplier;
  }

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
  public NewValue.Arithmetic<CriticalProto>
    add(NewValue.Arithmetic<CriticalProto> inValue)
  {
    if(!(inValue instanceof NewCritical))
      return this;

    NewCritical value = (NewCritical)inValue;

    if(value.m_threatLow == 2 && value.m_multiplier == 1)
      return doubled();

    if(m_threatLow == 2 && value.m_multiplier == 1)
      return value.doubled();

    if(m_threatLow == value.m_threatLow && m_multiplier == value.m_multiplier)
      return this;

    return new NewCritical(Math.max(m_multiplier, value.m_multiplier),
                           Math.min(m_threatLow, value.m_threatLow));
  }

  @Override
  public NewValue.Arithmetic<CriticalProto> multiply(int inFactor)
  {
    if(inFactor >= 2)
      return doubled();

    return this;
  }

  public NewCritical doubled()
  {
    int threatLow = 2 * m_threatLow - 20;
    return new NewCritical(m_multiplier, threatLow);
  }

  /**
   * Create a new critical value with the values from the given proto.
   *
   * @param inProto the proto to read the values from
   * @return the newly created critical
   */
  public static NewCritical fromProto(CriticalProto inProto)
  {
    int threatLow = 20;

    if(inProto.hasThreat())
    {
      threatLow = (int)inProto.getThreat().getLow();
    }

    int multiplier = 1;
    if(inProto.hasMultiplier())
      multiplier = inProto.getMultiplier();

    return new NewCritical(multiplier, threatLow);
  }

  @Override
  public boolean canAdd(NewValue.Arithmetic<CriticalProto> inValue)
  {
    return inValue instanceof NewCritical;
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
      assertEquals("parse", "12-19/x5",
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
