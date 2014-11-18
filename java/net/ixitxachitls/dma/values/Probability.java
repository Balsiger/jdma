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
import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.proto.Entries.BaseItemProto;
import net.ixitxachitls.dma.values.enums.Named;

/** The possible probabilities for items. */
public enum Probability implements Named
{
  /** Only a single such item exists in the world. */
  UNKNOWN("Unknown", BaseItemProto.Probability.UNKNOWN),
  /** Only a single such item exists in the world. */
  UNIQUE("Unique", BaseItemProto.Probability.UNIQUE),
  /** A very rare thing, multiple might exist in the whole world. */
  VERY_RARE("Very Rare", BaseItemProto.Probability.VERY_RARE),
  /** A rare thing, most people rarely see it. */
  RARE("Rare", BaseItemProto.Probability.RARE),
  /** An uncommon thing, but still often seen. */
  UNCOMMON("Uncommon", BaseItemProto.Probability.UNCOMMON),
  /** A common, everyday thing. */
  COMMON("Common", BaseItemProto.Probability.COMMON);

  /** The value's name. */
  private String m_name;

  /** The prot enum value. */
  private BaseItemProto.Probability m_proto;

  /** The parser for probability values. */
  public static final Value.Parser<Probability> PARSER =
    new Value.Parser<Probability>(1)
    {
      @Override
      public Optional<Probability> doParse(String inValue)
      {
        return Probability.fromString(inValue);
      }
    };

  /**
   * Create the name.
   *
   * @param inName     the name of the value
   * @param inProto    the proto enum value
   */
  private Probability(String inName, BaseItemProto.Probability inProto)
  {
    m_name = BaseItem.constant("item.probabilities", inName);
    m_proto = inProto;
  }

  @Override
  public String getName()
  {
    return m_name;
  }

  @Override
  public String toString()
  {
    return m_name;
  }

  /**
   * Get the probability for this selection.
   *
   * @return the probability number for this. It can be used to randomly roll
   *         up items with the appropriate probability distribution.
   */
  public int getProbability()
  {
    return (int)Math.pow(FACTOR, ordinal());
  }

  /**
   * The probabilistic factor, each category will be more probable according
   * to this factor.
   */
  public static final int FACTOR = 5;

  //                               /
  //                              /
  //                             /
  //     _______________________/
  //    /
  //   /
  //  /
  //     | VALUE_MOD_RANGE_LOW   | VALUE_MODE_RANGE_HIGH

  /** The percentage of the random range to use for adjustments (high). */
  public static final int RANGE_HIGH = 90;

  /** The percentage of the random range to use for adjustments (high). */
  public static final int RANGE_LOW = 10;

  /**
   * Get the proto value for this value.
   *
   * @return the proto enum value
   */
  public BaseItemProto.Probability getProto()
  {
    return m_proto;
  }

  /**
   * Get the group matching the given proto value.
   *
   * @param  inProto     the proto value to look for
   * @return the matched enum (will throw exception if not found)
   */
  public static Probability fromProto(BaseItemProto.Probability inProto)
  {
    for(Probability probability : values())
      if(probability.m_proto == inProto)
        return probability;

    throw new IllegalStateException("invalid proto probability: " + inProto);
  }

 /**
   * All the possible names for the probability.
   *
   * @return the possible names
   */
  public static List<String> names()
  {
    List<String> names = new ArrayList<>();

    for(Probability probability : values())
      names.add(probability.getName());

    return names;
  }

  /**
   * Get the layout matching the given text.
   */
  public static Optional<Probability> fromString(String inText)
  {
    for(Probability probability : values())
      if(probability.m_name.equalsIgnoreCase(inText))
        return Optional.of(probability);

    return Optional.absent();
  }
}