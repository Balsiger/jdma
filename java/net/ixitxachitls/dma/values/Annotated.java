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
 *
 * @param <V> the type of value being annotated
 */
public abstract class Annotated<V>
{
  /**
   * An annotated minimal value.
   *
   * @param <V> the type of value to annotated
   */
   public static class Min<V extends Comparable<V>>
    extends Annotated<Optional<V>>
  {
    /** Create the minimal annotation. */
    public Min()
    {
    }

    /**
     * Create a minimal annotated value with value and source.
     *
     * @param inValue the initial value
     * @param inSource where the value came from
     */
    public Min(V inValue, java.lang.String inSource)
    {
      add(inValue, inSource);
    }

    /** The minimal value, if any. */
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

    /**
     * Add a value to the minimal annotation.
     *
     * @param inValue the value to add
     * @param inSource where the value came from
     */
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

  /**
   * An annotated minimal bonus value.
   *
   * @param <V> the type of value annotated
   */
  public static class MinBonus<V extends Comparable<V>> extends Min<V>
  {
    /** Create the min bonus annotation. */
    public MinBonus()
    {
    }

    /**
     * Create the min bonus annotation with an initial value.
     *
     * @param inValue the first value to add
     * @param inSource the source of the value
     */
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

  /**
   * Annotate a maximal value.
   *
   * @param <V> the type of value to annotate
   */
  public static class Max<V extends Comparable<V>>
    extends Annotated<Optional<V>>
  {
    /** Create an empty, default annotation. */
    public Max()
    {
    }

    /**
     * Create an annotation with a value.
     *
     * @param inValue the value to initially use
     * @param inSource where the value came from
     */
    public Max(V inValue, java.lang.String inSource)
    {
      add(inValue, inSource);
    }

    /** The maximal value annotated. */
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

    /**
     * Add a value for annotation.
     *
     * @param inValue the value to add
     * @param inSource where the value came from
     */
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

  /**
   * An annotation for a maximal bonus.
   *
   * @param <V> the type of value annotated
   */
  public static class MaxBonus<V extends Comparable<V>> extends Max<V>
  {
    /** Create the max bonus annotated value. */
    public MaxBonus()
    {
    }

    /**
     * Create a max bonus annotated value with an initially added value.
     *
     * @param inValue the first value to add
     * @param inSource the source of the value
     */
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

  /** An annotated string value. */
  public static class String extends Annotated<Optional<java.lang.String>>
  {
    /** Create an empty annotated string. */
    public String()
    {
      m_value = Optional.absent();
    }

    /**
     * Create an annotated string with an intial value.
     *
     * @param inValue the first value added
     * @param inSource the source of the value
     */
    public String(java.lang.String inValue, java.lang.String inSource)
    {
      add(inValue, inSource);

      m_value = Optional.of(inValue);
    }

    /** The annotated value, if any. */
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

  /** An annotated Integer value. */
  public static class Integer extends Annotated<Optional<java.lang.Integer>>
  {
    /** Create an undefined annotated integer. */
    public Integer()
    {
      m_value = Optional.absent();
    }

    /**
     * Create the annotated integer with an intial value.
     *
     * @param inValue the initial value
     * @param inSource the source of the value
     */
    public Integer(int inValue, java.lang.String inSource)
    {
      add(inValue, inSource);

      m_value = Optional.of(inValue);
    }

    /**
     * Create an annotated integer from existing sourced values.
     *
     * @param inValue the final value
     * @param inSources the values and source that contributed the final value
     */
    public Integer(int inValue, ValueSources inSources)
    {
      super(inSources);

      m_value = Optional.of(inValue);
    }

    /** The annotated value, if any. */
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

    /**
     * Add a new value.
     *
     * @param inValue the value to add
     * @param inSource the source of the value
     */
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

  /** An annotated bonus (with + sign). */
  public static class Bonus extends Integer
  {
    /** Create an undefined annotated bonus. */
    public Bonus()
    {
    }

    /**
     * Create an annotated bonus value.
     *
     * @param inValue the initial value
     * @param inSource the source of the initial value
     */
    public Bonus(int inValue, java.lang.String inSource)
    {
      super(inValue, inSource);
    }

    /**
     * Create an annoated bonus value with an annotated value.
     *
     * @param inValue the final value to add
     * @param inSources the sources and values that contributed to the final
     *                  value
     */
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

  /**
   * An annotated arithmetic value.
   *
   * @param <V> the arithmetic value being annotated
   */
  public static class Arithmetic<V extends Value.Arithmetic>
    extends Annotated<Optional<V>>
  {
    /** Create an undefined value. */
    public Arithmetic()
    {
      m_value = Optional.absent();
    }

    /**
     * Create a defined arithmetic, annotated value.
     *
     * @param inValue the intial value
     * @param inSource the source of the value
     */
    public Arithmetic(V inValue, java.lang.String inSource)
    {
      super(inValue, inSource);

      m_value = Optional.of(inValue);
    }

    /**
     * Create an arithmetic, annotated value from another annotated value.
     *
     * @param inValue the final value
     * @param inSources the source and values contributing to the final value
     */
    public Arithmetic(V inValue, ValueSources inSources)
    {
      super(inSources);

      m_value = Optional.of(inValue);
    }

    /** The final value, if any. */
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

    /** Add a value.
     *
     * @param inValue the value to add
     * @param inSource the source of the value
     */
    public void add(V inValue, java.lang.String inSource)
    {
      add(Optional.of(inValue));

      super.add(inValue.toString(), inSource);
    }

    /**
     * Multiply the value.
     *
     * @param inValue the multiplication factor
     * @param inSource the source of the multiplication
     */
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

  /**
   * An annotated list value.
   *
   * @param <V> the type of elements in the list
   */
  public static class List<V> extends Annotated<java.util.List<V>>
  {
    /** Create an undefined list value. */
    public List()
    {
      // nothing to do
    }

    /**
     * Create an annotated list value.
     *
     * @param inValue the initial value
     * @param inSource the source of the value
     */
    public List(V inValue, java.lang.String inSource)
    {
      super(inValue.toString(), inSource);

      m_values.add(inValue);
    }

    /**
     * Create an annotated list value.
     *
     * @param inValues the initial value
     * @param inSource the values and sources that contributed to the value
     */
    public List(java.util.List<V> inValues, java.lang.String inSource)
    {
      m_values.addAll(inValues);
      for (V value : inValues)
        add(value.toString(), inSource);
    }

    /** The annotated list. */
    private java.util.List<V> m_values = new ArrayList<>();

    /**
     * Add a value to the list. The value is only added if it's not already
     * there.
     *
     * @param inValue the value to add to the list
     */
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

    /**
     * Add a value.
     *
     * @param inValue the value to add
     * @param inSource the source of the value
     */
    public void add(V inValue, java.lang.String inSource)
    {
      addValue(inValue);

      super.add(inValue.toString(), inSource);
    }

    /**
     * Add a list of values.
     *
     * @param inValues the values to add
     * @param inSource the source of the values
     */
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

  /** Create an undefined annotated value. */
  public Annotated()
  {
    m_sources = new ValueSources();
  }

  /**
   * Create an annotated value.
   *
   * @param inValue the initial value
   * @param inSource the source of the initial value
   */
  public Annotated(Object inValue, java.lang.String inSource)
  {
    m_sources = new ValueSources(inValue, inSource);
  }

  /**
   * Create an annotated value from sources.
   *
   * @param inSources the values and sources to create from
   */
  public Annotated(ValueSources inSources)
  {
    m_sources = inSources;
  }

  /** The values and sources contributing to this annotated value. */
  private final ValueSources m_sources;

  /**
   * Get the values and sources.
   *
   * @return the values and sources
   */
  public ValueSources getSources()
  {
    return m_sources;
  }

  /**
   * Add a value.
   *
   * @param inValue the value to add (string representation)
   * @param inSource the source of the value
   */
  public void add(java.lang.String inValue, java.lang.String inSource)
  {
    m_sources.add(inValue, inSource);
  }

  /**
   * Add another annotated value to this one.
   *
   * @param inAnnotated the other value
   */
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

  /**
   * Whether to show the + sign for positive numbers.
   *
   * @return true to show, false for not
   */
  public boolean showSign()
  {
    return false;
  }

  /**
   * Get the annotated final value.
   *
   * @return the value
   */
  public abstract V get();

  /**
   * Add another value.
   *
   * @param inValue the value to add
   */
  protected abstract void add(V inValue);
}
