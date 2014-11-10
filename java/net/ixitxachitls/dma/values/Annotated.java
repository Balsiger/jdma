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

import com.google.common.base.Optional;

/**
 * A value annotated with the sources of how it was computed.
 *
 * @file   Annotated.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public abstract class Annotated<V>
{
  public static class Min<V extends Comparable<V>>
    extends Annotated<Optional<V>>
  {
    public Min()
    {
    }

    public Min(V inValue, java.lang.String inSource)
    {
      add(inValue, inSource);
    }

    private Optional<V> m_value = Optional.absent();

    @Override
    protected void add(Optional<V> inValue)
    {
      if(!inValue.isPresent())
        return;

      if(!m_value.isPresent())
        m_value = inValue;

      if(m_value.get().compareTo(inValue.get()) > 0)
        m_value = inValue;
    }

    public void add(V inValue, java.lang.String inSource)
    {
      add(Optional.of(inValue));
      super.add(inValue.toString(), inSource);
    }

    @Override
    public Optional<V> get()
    {
      return m_value;
    }
  }

  public static class MinBonus<V extends Comparable<V>> extends Min<V>
  {
    public MinBonus()
    {
    }

    public MinBonus(V inValue, java.lang.String inSource)
    {
      super(inValue, inSource);
    }

    @Override
    public boolean showSign()
    {
      return true;
    }
  }

  public static class Max<V extends Comparable<V>>
    extends Annotated<Optional<V>>
  {
    public Max()
    {
    }

    public Max(V inValue, java.lang.String inSource)
    {
      add(inValue, inSource);
    }

    private Optional<V> m_value = Optional.absent();

    @Override
    protected void add(Optional<V> inValue)
    {
      if(!inValue.isPresent())
        return;

      if(!m_value.isPresent())
        m_value = inValue;

      if(m_value.get().compareTo(inValue.get()) < 0)
        m_value = inValue;
    }

    public void add(V inValue, java.lang.String inSource)
    {
      add(Optional.of(inValue));
      super.add(inValue.toString(), inSource);
    }

    @Override
    public Optional<V> get()
    {
      return m_value;
    }
  }

  public static class MaxBonus<V extends Comparable<V>> extends Max<V>
  {
    public MaxBonus()
    {
    }

    public MaxBonus(V inValue, java.lang.String inSource)
    {
      super(inValue, inSource);
    }

    @Override
    public boolean showSign()
    {
      return true;
    }
  }

  public static class String extends Annotated<Optional<java.lang.String>>
  {
    public String()
    {
      m_value = Optional.absent();
    }

    public String(java.lang.String inValue, java.lang.String inSource)
    {
      add(inValue, inSource);

      m_value = Optional.of(inValue);
    }

    private Optional<java.lang.String> m_value = Optional.absent();

    @Override
    protected void add(Optional<java.lang.String> inValue)
    {
      if(!inValue.isPresent())
        return;

      if(!m_value.isPresent())
        m_value = inValue;
      else
        m_value = Optional.of(m_value.get() + " " + inValue.get());
    }

    @Override
    public void add(java.lang.String inValue, java.lang.String inSource)
    {
      add(Optional.of(inValue));

      super.add(inValue, inSource);
    }

    @Override
    public Optional<java.lang.String> get()
    {
      return m_value;
    }
  }

  public static class Integer extends Annotated<Optional<java.lang.Integer>>
  {
    public Integer()
    {
      m_value = Optional.absent();
    }

    public Integer(int inValue, java.lang.String inSource)
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

    @Override
    protected void add(Optional<java.lang.Integer> inValue)
    {
      if(!inValue.isPresent())
        return;

      if(m_value.isPresent())
        m_value = Optional.of(m_value.get() + inValue.get());
      else
        m_value = inValue;
    }

    public void add(int inValue, java.lang.String inSource)
    {
      add(Optional.of(inValue));

      super.add("" + inValue, inSource);
    }

    @Override
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

    public Bonus(int inValue, java.lang.String inSource)
    {
      super(inValue, inSource);
    }

    public Bonus(int inValue, ValueSources inSources)
    {
      super(inValue, inSources);
    }

    @Override
    public void add(int inValue, java.lang.String inSource)
    {
      add(Optional.of(inValue));

      super.add((inValue >= 0 ? "+" : "") + inValue, inSource);
    }

    @Override
    public boolean showSign()
    {
      return true;
    }
  }

  public static class Arithmetic<V extends NewValue.Arithmetic>
    extends Annotated<Optional<V>>
  {
    public Arithmetic()
    {
      m_value = Optional.absent();
    }

    public Arithmetic(V inValue, java.lang.String inSource)
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

    @Override
    @SuppressWarnings("unchecked")
    protected void add(Optional<V> inValue)
    {
      if(!inValue.isPresent())
        return;

      if(!m_value.isPresent())
        m_value = inValue;
      else
      {
        if(!m_value.get().canAdd(inValue.get()))
          throw new IllegalArgumentException("cannot add " + inValue + " to "
            + m_value);

        m_value = Optional.of((V)m_value.get().add(inValue.get()));
      }
    }

    public void add(V inValue, java.lang.String inSource)
    {
      add(Optional.of(inValue));

      super.add(inValue.toString(), inSource);
    }

    public void multiply(int inValue, java.lang.String inSource)
    {
      if(inValue > 1)
      {
        Optional<V> value = get();
        if(value.isPresent())
          add(Optional.of((V)value.get().multiply(inValue - 1)));
      }

      super.add("x" + inValue, inSource);
    }

    @Override
    public Optional<V> get()
    {
      return m_value;
    }
  }

  public static class List<V> extends Annotated<java.util.List<V>>
  {
    public List()
    {
      // nothing to do
    }

    public List(V inValue, java.lang.String inSource)
    {
      super(inValue.toString(), inSource);

      m_values.add(inValue);
    }

    public List(java.util.List<V> inValues, java.lang.String inSource)
    {
      m_values.addAll(inValues);
      for (V value : inValues)
        add(value.toString(), inSource);
    }

    private java.util.List<V> m_values = new ArrayList<>();

    private void addValue(V inValue)
    {
      if(!m_values.contains(inValue))
        m_values.add(inValue);
    }

    @Override
    protected void add(java.util.List<V> inValues)
    {
      for(V value : inValues)
        addValue(value);
    }

    public void add(V inValue, java.lang.String inSource)
    {
      addValue(inValue);

      super.add(inValue.toString(), inSource);
    }

    public void add(java.util.List<V> inValues, java.lang.String inSource)
    {
      add(inValues);
      for(V value : inValues)
        super.add(value.toString(), inSource);
    }

    @Override
    public java.util.List<V> get()
    {
      return m_values;
    }
}

  public Annotated()
  {
    m_sources = new ValueSources();
  }

  public Annotated(Object inValue, java.lang.String inSource)
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

  public void add(java.lang.String inValue, java.lang.String inSource)
  {
    m_sources.add(inValue, inSource);
  }

  public void add(Annotated<V> inAnnotated)
  {
    add(inAnnotated.get());
    m_sources.add(inAnnotated.m_sources);
  }

  @Override
  public java.lang.String toString()
  {
    return get() + " (" + m_sources + ")";
  }

  public boolean showSign()
  {
    return false;
  }

  public abstract V get();
  protected abstract void add(V inValue);
}
