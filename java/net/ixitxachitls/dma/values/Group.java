/******************************************************************************
 * Copyright (c) 2002-2011 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.util.Grouping;

/**
 * A standard grouping accordig to strings extracted from values.
 *
 * @file          Group.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param         <T> the type of value that is grouped
 * @param         <S> the type that is used for comparing the values
 * @param         <U> the type values are grouped into
 */
@Immutable
@ParametersAreNonnullByDefault
public class Group<T,
                   S extends Comparable<S> & Serializable,
                   U extends Serializable>
  implements Grouping<T, U>, Comparator<U>, Serializable
{
  /**
   * The interface for exracting data.
   *
   * @param <K> the type of values to extract from
   * @param <V> the type of grouping value extracted
   */
  public interface Extractor<K, V extends Comparable<V>>
    extends Serializable
  {
    //------------------------------ extract -------------------------------

    /** Extract a comparable value from the given dma value.
     *
     * @param inValue     the value to extract from
     *
     * @return the value to compare with
     *
     */
    V extract(K inValue);

    //......................................................................
  }

  /**
   * Create the group.
   *
   * @param       inExtractor the extractor used to get the value
   * @param       inRanges    the values to compare against
   * @param       inGroups    the group values to return
   * @param       inUndefined the undefined value to use if none given
   */
  public Group(Extractor<T, S> inExtractor, S []inRanges,
               U []inGroups, U inUndefined)
  {
    if(inRanges.length != inGroups.length - 1)
      throw new IllegalArgumentException("number of ranges and groups don't "
                                         + "match");

    m_extractor = inExtractor;
    m_ranges    = Arrays.copyOf(inRanges, inRanges.length);
    m_undefined = inUndefined;
    m_groups    = Arrays.copyOf(inGroups, inGroups.length);
  }

  /** The extractor to get the value. */
  private Extractor<T, S> m_extractor;

  /** The ranges to check for. */
  private S []m_ranges;

  /** The values for each range. */
  protected U []m_groups;

  /** The undefined value to use. */
  private U m_undefined;

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Group the given value.
   *
   * @param       inValue the value to group
   *
   * @return      the group this value belongs to
   */
  @Override
  public U group(T inValue)
  {
    if(inValue == null)
      return m_undefined;

    S compare = m_extractor.extract(inValue);

    for(int i = 0; i < m_ranges.length; i++)
      if(compare.compareTo(m_ranges[i]) <= 0)
        return m_groups[i];

    // not yet found a value
    return m_groups[m_groups.length - 1];
  }

  /**
   * Convert the grouping to a human readable string.
   *
   * @return      a human readable representation of the grouping
   */
  @Override
  public String toString()
  {
    return Arrays.toString(m_groups);
  }

  /**
   * Compare two values for ordering.
   *
   * @param  inFirst  the first value to compare
   * @param  inSecond the second values to compare
   *
   * @return <0 if first is smaller, 0 if equal, >0 if first is bigger
   */
  @Override
  public int compare(U inFirst, U inSecond)
  {
    return ordinal(inFirst) - ordinal(inSecond);
  }

  /**
   * The number of the given string in the list of groups.
   *
   * @param       inGroup the group to get the ordinal for
   *
   * @return      the index into the possible groups, can be higher in case of
   *              undefined or null
   */
  public int ordinal(U inGroup)
  {
    if(inGroup.equals(m_undefined))
      return m_groups.length + 1;

    for(int i = 0; i < m_groups.length; i++)
      if(inGroup == m_groups[i] || inGroup.equals(m_groups[i]))
        return i;

    return m_groups.length;
  }

  //----------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }
}
