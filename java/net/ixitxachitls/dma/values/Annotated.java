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

import com.google.appengine.repackaged.com.google.common.base.Optional;



/**
 * A value annotated with the sources of how it was computed.
 *
 * @file   Annotated.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Annotated
{
  public static class Integer extends Annotated
  {
    public Integer()
    {
      m_value = Optional.absent();
    }

    public Integer(int inValue, String inSource)
    {
      add(inValue, inSource);

      m_value = Optional.of(inValue);
    }

    public Integer(int inValue, ValueSources inSources)
    {
      super(inSources);

      m_value = Optional.of(inValue);
    }

    private Optional<java.lang.Integer> m_value = Optional.absent();

    protected void add(int inValue)
    {
      if(m_value.isPresent())
        m_value = Optional.of(m_value.get() + inValue);
      else
        m_value = Optional.of(inValue);
    }

    public void add(int inValue, String inSource)
    {
      add(inValue);

      super.add("" + inValue, inSource);
    }

    public void add(Annotated.Integer inAnnotated)
    {
      if(inAnnotated.get().isPresent())
        add(inAnnotated.get().get());

      super.add(inAnnotated);
    }

    public Optional<java.lang.Integer> get()
    {
      return m_value;
    }
  }

  public static class Bonus extends Integer
  {
    public Bonus()
    {
    }

    public Bonus(int inValue, String inSource)
    {
      super(inValue, inSource);
    }

    public Bonus(int inValue, ValueSources inSources)
    {
      super(inValue, inSources);
    }

    @Override
    public void add(int inValue, String inSource)
    {
      add(inValue);

      super.add((inValue >= 0 ? "+" : "") + inValue, inSource);
    }

    public boolean showSign()
    {
      return true;
    }
  }

  public static class Arithmetic<V extends NewValue.Arithmetic> extends Annotated
  {
    public Arithmetic()
    {
      m_value = Optional.absent();
    }

    public Arithmetic(V inValue, String inSource)
    {
      super(inValue, inSource);

      m_value = Optional.of(inValue);
    }

    public Arithmetic(V inValue, ValueSources inSources)
    {
      super(inSources);

      m_value = Optional.of(inValue);
    }

    private Optional<V> m_value;

    @SuppressWarnings("unchecked")
    private void add(V inValue)
    {
      if(m_value.isPresent())
      {
        if(!m_value.get().canAdd(inValue))
          throw new IllegalArgumentException("cannot add " + inValue + " to "
            + m_value);
        m_value = Optional.of((V)m_value.get().add(inValue));

      }
    }

    public void add(V inValue, String inSource)
    {
      add(inValue);

      super.add(inValue.toString(), inSource);
    }

    public void add(Annotated.Arithmetic<V> inAnnotated)
    {
      if(inAnnotated.get().isPresent())
        add(inAnnotated.get().get());

      super.add(inAnnotated);
    }

    public Optional<V> get()
    {
      return m_value;
    }
  }

  public Annotated()
  {
    m_sources = new ValueSources();
  }

  public Annotated(Object inValue, String inSource)
  {
    m_sources = new ValueSources(inValue, inSource);
  }

  public Annotated(ValueSources inSources)
  {
    m_sources = inSources;
  }

  private final ValueSources m_sources;

  public ValueSources getSources()
  {
    return m_sources;
  }

  public void add(String inValue, String inSource)
  {
    m_sources.add(inValue, inSource);
  }

  public void add(Annotated inAnnotated)
  {
    m_sources.add(inAnnotated.m_sources);
  }
}
