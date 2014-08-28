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
  public class ValueSource
  {
    private ValueSource(String inValue, String inSource)
    {
      m_value = inValue;
      m_source = inSource;
    }

    private final String m_value;
    private final String m_source;

   public String getValue()
    {
      return m_value;
    }

    public String getSource()
    {
      return m_source;
    }
  }

  public ValueSources()
  {
  }

  public ValueSources(Object inValue, String inSource)
  {
    add(inValue.toString(), inSource);
  }

  private final List<ValueSource> m_sources = new ArrayList<>();

  public void add(String inValue, String inSource)
  {
    add(new ValueSource(inValue, inSource));
  }

  public void add(ValueSource inSource)
  {
    m_sources.add(inSource);
  }

  public void add(ValueSources inSources)
  {
    for(ValueSource source : inSources.m_sources)
      add(source);
  }

  public List<ValueSource> getSources()
  {
    return Collections.unmodifiableList(m_sources);
  }
}
