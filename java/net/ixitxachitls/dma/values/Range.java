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

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;

import net.ixitxachitls.dma.proto.Values.RangeProto;

/**
 * A range of two values.
 *
 * @file   NewRange.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Range extends NewValue<RangeProto>
{
  public Range(int inNumber)
  {
    this(inNumber, inNumber);
  }

  public Range(long inLow, long inHigh)
  {
    m_low = inLow;
    m_high = inHigh;
  }

  private final long m_low;
  private final long m_high;

  private static final Splitter DASH_SPLITTER = Splitter.on('-').trimResults();
  public static final Parser<Range> PARSER = new Parser<Range>(1)
  {
    @Override
    public Optional<Range> doParse(String inRange)
    {
      int i = 0;
      long low = 0;
      long high = 0;
      for(String part : DASH_SPLITTER.split(inRange))
      {
        if(i > 1)
          return Optional.absent();

        try
        {
          if(i == 0)
          {
            low = Long.parseLong(part);
            high = low;
          }
          else
            high = Long.parseLong(part);
        }
        catch(NumberFormatException e)
        {
          return Optional.absent();
        }

        i++;
      }

      return Optional.of(new Range(low, high));
    }
  };

  /**
   * Get the low value of the range.
   *
   * @return the low value
   */
  public long getLow()
  {
    return m_low;
  }

  /**
   * Get the high value of the range.
   *
   * @return the high value
   */
  public long getHigh()
  {
    return m_high;
  }

  @Override
  public String toString()
  {
    if(m_low == m_high)
      return "" + m_low;

    return m_low + " - " + m_high;
  }

  @Override
  public RangeProto toProto()
  {
    return RangeProto.newBuilder()
      .setLow(m_low)
      .setHigh(m_high)
      .build();
  }

  /**
   * Create a range from the given proto message.
   *
   * @param inProto the proto message
   *
   * @return the newly created range
   */
  public static Range fromProto(RangeProto inProto)
  {
    return new Range(inProto.getLow(), inProto.getHigh());
  }

  @Override
  public boolean equals(@Nullable Object inOther)
  {
    if(this == inOther)
      return true;

    if(inOther == null)
      return false;

    if(!(inOther instanceof Range))
      return false;

    Range other = (Range)inOther;
    return m_low == other.m_low && m_high == other.m_high;
  }

  @Override
  public int hashCode()
  {
    return (int)(m_low + m_high);
  }
}
