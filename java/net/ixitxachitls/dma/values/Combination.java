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
import java.util.TreeSet;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.entries.BaseEntry;

/**
 * A combination of values from the various base entries.
 *
 * @file   Combination.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public abstract class Combination<T>
{
  public static class Addable<V extends NewValue.Addable> extends Combination<V>
  {
    public Addable(BaseEntry inEntry, V inValue)
    {
      super(inEntry, inValue);
    }

    public Addable(BaseEntry inEntry, java.util.List<Combination<V>> inValues)
    {
      super(inEntry, inValues);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V getValue()
    {
      if(m_value.isPresent())
        return m_value.get();

      V value = null;
      for(Combination<V> combination : m_combinations)
      {
        if(value == null)
          value = combination.getValue();
        else
          value = (V)value.add(combination.getValue());
      }

      return value;
    }
  }

  public static class Max<V extends Comparable> extends Combination<V>
  {
    public Max(BaseEntry inEntry, V inValue)
    {
      super(inEntry, inValue);
    }

    public Max(BaseEntry inEntry, java.util.List<Combination<V>> inValues)
    {
      super(inEntry, inValues);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V getValue()
    {
      if(m_value.isPresent())
        return m_value.get();

      V value = null;
      for(Combination<V> combination : m_combinations)
      {
        if(value == null)
          value = combination.getValue();
        else
        {
          V other = combination.getValue();
          if (other != null && value.compareTo(other) < 0)
            value = other;
        }
      }

      return value;
    }
  }

  public static class Min<V extends Comparable> extends Combination<V>
  {
    public Min(BaseEntry inEntry, V inValue)
    {
      super(inEntry, inValue);
    }

    public Min(BaseEntry inEntry, java.util.List<Combination<V>> inValues)
    {
      super(inEntry, inValues);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V getValue()
    {
      if(m_value.isPresent())
        return m_value.get();

      V value = null;
      for(Combination<V> combination : m_combinations)
      {
        if(value == null)
          value = combination.getValue();
        else
        {
          V other = combination.getValue();
          if (other != null && value.compareTo(other) > 0)
            value = other;
        }
      }

      return value;
    }
  }

  public static class First<V> extends Combination<V>
  {
    public First(BaseEntry inEntry, V inValue)
    {
      super(inEntry, inValue);
    }

    public First(BaseEntry inEntry, java.util.List<Combination<V>> inValues)
    {
      super(inEntry, inValues);
    }

    @Override
    public V getValue()
    {
      if(m_value.isPresent())
        return m_value.get();

      for(Combination<V> combination : m_combinations)
        return combination.getValue();

      return null;
    }
  }

  public static class Integer extends Combination<java.lang.Integer>
  {
    public Integer(BaseEntry inEntry, java.lang.Integer inValue)
    {
      super(inEntry, inValue);
    }

    public Integer(BaseEntry inEntry,
                   java.util.List<Combination<java.lang.Integer>> inValues)
    {
      super(inEntry, inValues);
    }

    @Override
    public java.lang.Integer getValue()
    {
      if(m_value.isPresent())
        return m_value.get();

      java.lang.Integer value = null;
      for(Combination<java.lang.Integer> combination : m_combinations)
      {
        if(value == null)
          value = combination.getValue();
        else
          value += combination.getValue();
      }

      return value;
    }
  }

  public static class String extends Combination<java.lang.String>
  {
    public String(BaseEntry inEntry, java.lang.String inValue)
    {
      super(inEntry, inValue);
    }

    public String(BaseEntry inEntry,
                  java.util.List<Combination<java.lang.String>> inValues)
    {
      super(inEntry, inValues);
    }

    public String(BaseEntry inEntry, java.lang.String inValue,
                  java.util.List<Combination<java.lang.String>> inValues)
    {
      super(inEntry, inValue, inValues);
    }

    @Override
    public java.lang.String getValue()
    {
      if(m_value.isPresent())
        return m_value.get();

      java.lang.String value = "";
      for(Combination<java.lang.String> combination : m_combinations)
      {
        if(value.isEmpty())
          value = combination.getValue();
        else
          value += combination.getValue();
      }

      return value;
    }
  }

  public static class List<T> extends Combination<java.util.List<T>>
  {
    public List(BaseEntry inEntry, java.util.List<T> inValue)
    {
      super(inEntry, inValue);
    }

    public List(java.util.List<Combination<java.util.List<T>>> inValues,
                BaseEntry inEntry)
    {
      super(inEntry, inValues);
    }

    public List(BaseEntry inEntry, java.util.List<T> inValue,
                java.util.List<Combination<java.util.List<T>>> inValues)
    {
      super(inEntry, inValue, inValues);
    }

    @Override
    public java.util.List<T> getValue()
    {
      java.util.List<T> values = null;
      if(m_value.isPresent())
        values = new ArrayList<>(m_value.get());

      for(Combination<java.util.List<T>> combination : m_combinations)
        if(values == null)
          values = new ArrayList<>(combination.getValue());
        else
        {
          java.util.List<T> subvalues = combination.getValue();
          for(T subvalue : subvalues)
            add(values, subvalue);
        }

      if(values == null)
        return new ArrayList<>();

      return values;
    }

    @SuppressWarnings("unchecked")
    private void add(java.util.List<T> inValues, T inValue)
    {
      if(inValue instanceof NewValue.Addable)
      {
        for(T value : inValues)
          if(value instanceof NewValue.Addable
            && ((NewValue.Addable)value).canAdd((NewValue.Addable)inValue))
          {
            ((NewValue.Addable)value).add((NewValue.Addable)inValue);
            return;
          }

        inValues.add(inValue);
      }
      else
        if(!inValues.contains(inValue))
          inValues.add(inValue);
    }
  }

  public static class Set<T> extends Combination<java.util.List<T>>
  {
    public Set(BaseEntry inEntry, java.util.List<T> inValue)
    {
      super(inEntry, inValue);
    }

    public Set(java.util.List<Combination<java.util.List<T>>> inValues,
               BaseEntry inEntry)
    {
      super(inEntry, inValues);
    }

    public Set(BaseEntry inEntry, java.util.List<T> inValue,
               java.util.List<Combination<java.util.List<T>>> inValues)
    {
      super(inEntry, inValue, inValues);
    }

    @Override
    public java.util.List<T> getValue()
    {
      java.util.Set<T> values = new TreeSet<T>();
      if(m_value.isPresent())
        values.addAll(m_value.get());
      for(Combination<java.util.List<T>> combination : m_combinations)
        values.addAll(combination.getValue());

      return new ArrayList<T>(values);
    }
  }

  public class Annotated<V>
  {
    public Annotated(V inValue, java.lang.String inSource)
    {
      m_value = inValue;
      m_source = inSource;
    }

    private final V m_value;
    private final java.lang.String m_source;

    public V getValue()
    {
      return m_value;
    }

    public java.lang.String getSource()
    {
      return m_source;
    }
  }

  public Combination(BaseEntry inEntry, T inValue)
  {
    m_entry = inEntry;
    m_value = Optional.of(inValue);
    m_combinations = new ArrayList<>();
  }

  public Combination(BaseEntry inEntry, java.util.List<Combination<T>> inValues)
  {
    m_entry = inEntry;
    m_value = Optional.absent();
    m_combinations = inValues;
  }

  public Combination(BaseEntry inEntry, T inValue,
                     java.util.List<Combination<T>> inValues)
  {
    m_entry = inEntry;
    m_value = Optional.of(inValue);
    m_combinations = inValues;
  }

  protected final BaseEntry m_entry;
  protected final Optional<T> m_value;
  protected final java.util.List<Combination<T>> m_combinations;

  public abstract T getValue();

  public java.lang.String getValueString()
  {
    T value = getValue();
    if(value == null)
      return "";

    return value.toString();
  }

  public boolean hasValue()
  {
    return m_value.isPresent();
  }

  public boolean hasAnyValue()
  {
    if(hasValue())
      return true;

    for(Combination<T> combination : m_combinations)
      if(combination.hasAnyValue())
        return true;

    return false;
  }

  public java.lang.String getSource()
  {
    return m_entry.getName();
  }

  public java.util.List<Annotated<T>> annotate()
  {
    java.util.List<Annotated<T>> values = new ArrayList<>();

    for(Combination<T> combination : m_combinations)
    {
      if(combination.hasValue())
        values.add(new Annotated<T>(combination.getValue(),
                                    combination.getSource()));

      for(Annotated<T> annotated : combination.annotate())
        values.add(new Annotated<T>(annotated.getValue(),
                                    combination.getSource() + "/"
                                      + annotated.getSource()));
    }

    return values;
  }

  @Override
  public java.lang.String toString()
  {
    return m_entry.getName() + ": "
      + (m_value.isPresent() ? m_value.get() : "(null)")
      + " / " + m_combinations;
  }
}
