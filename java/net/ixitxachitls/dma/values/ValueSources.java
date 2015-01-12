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
import java.util.Collections;
import java.util.List;

/**
 * A class representing the possible sources and source values that contributed
 * to a final value.
 *
 * @file   ValueSources.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class ValueSources
{
  /** A single value and where it came from (source). */
  public final static class ValueSource
  {
    /**
     * Create the value source.
     *
     * @param inValue the value
     * @param inSource the source
     */
    private ValueSource(String inValue, String inSource)
    {
      m_value = inValue;
      m_source = inSource;
    }

    /** The value representation. */
    private final String m_value;

    /** The source of the value. */
    private final String m_source;

    /**
     * Get the value representation.
     *
     * @return the value
     */
   public String getValue()
   {
      return m_value;
    }

    /**
     * Get the source description for the value.
     *
     * @return the description
     */
    public String getSource()
    {
      return m_source;
    }

    @Override
    public String toString()
    {
      return m_value + ": " + m_source;
    }
  }

  /** Create an empty value sources. */
  public ValueSources()
  {
  }

  /**
   * Create value sources with a single initial value.
   *
   * @param inValue the value representation
   * @param inSource the source description
   */
  public ValueSources(Object inValue, String inSource)
  {
    add(inValue.toString(), inSource);
  }

  /** The list of value sources. */
  private final List<ValueSource> m_sources = new ArrayList<>();

  /**
   * Add a value with a source.
   *
   * @param inValue the value representation
   * @param inSource the source description
   */
  public void add(String inValue, String inSource)
  {
    add(new ValueSource(inValue, inSource));
  }

  /** Add a value source.
   *
   * @param inSource the source and vaule
   */
  public void add(ValueSource inSource)
  {
    m_sources.add(inSource);
  }

  /**
   * Add another set of value sources to this one.
   *
   * @param inSources the other sources
   */
  public void add(ValueSources inSources)
  {
    for(ValueSource source : inSources.m_sources)
      add(source);
  }

  /**
   * Get all the value sources stored.
   *
   * @return the value sources.
   */
  public List<ValueSource> getSources()
  {
    return Collections.unmodifiableList(m_sources);
  }

  @Override
  public String toString()
  {
    return m_sources.toString();
  }
}
