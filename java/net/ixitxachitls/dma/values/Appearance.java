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

import net.ixitxachitls.dma.values.enums.Probability;

/**
 * The appearance of an item.
 *
 * @file Appearance.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Appearance implements Comparable<Appearance>
{
  /**
   * Create the appearance.
   *
   * @param inProbability the probability for this specific description
   * @param inText the description of how the item looks
   */
  public Appearance(Probability inProbability, String inText)
  {
    m_probability = inProbability;
    m_text = inText;
  }

  /** The probability for this appearance. */
  private final Probability m_probability;

  /** The description of the item. */
  private final String m_text;

  /** The parser for appearances. */
  public static final Parser<Appearance> PARSER =
    new Parser<Appearance>(2)
    {
      @Override
      public Optional<Appearance> doParse
      (String inProbability, String inText)
      {
        Optional<Probability> probability =
        Probability.fromString(inProbability);
        String text = inText;
        if(!probability.isPresent()
          || text == null || text.isEmpty())
          return Optional.absent();

        return Optional.of(new Appearance(probability.get(), text));
      }
    };

  /**
   * Get the probability of this appearance.
   *
   * @return the probability
   */
  public Probability getProbability()
  {
    return m_probability;
  }

  /**
   * Get the description of the appearance.
   *
   * @return the description
   */
  public String getText()
  {
    return m_text;
  }

  @Override
  public String toString()
  {
    return m_text + " (" + m_probability + ")";
  }

  @Override
  public int compareTo(Appearance inOther)
  {
    int probability = m_probability.compareTo(inOther.m_probability);
    if(probability != 0)
      return probability;

    return m_text.compareTo(inOther.m_text);
  }
}
