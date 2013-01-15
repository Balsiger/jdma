/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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

//------------------------------------------------------------------ imports

package net.ixitxachitls.dma.values;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.entries.Variable;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Linebreak;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A combination of various values to compute an end result. This is mainly
 * used to compute a value combine from multiple base values.
 *
 *
 * @file          Combination.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param         <V> the value that is conbined
 *
 */

//..........................................................................

//__________________________________________________________________________

@Deprecated
public class Combination<V extends Value>
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- Combination ------------------------------

  /**
   * The default constructor.
   *
   * @param  inEntry the entry to get the value from
   * @param  inName  the name of the value to combine
   *
   */
  public Combination(@Nonnull ValueGroup inEntry, @Nonnull String inName)
  {
    m_entry = inEntry;
    m_name = inName;
  }

  //........................................................................

  //---------------------------- withIgnoreTop -----------------------------

  /**
   * Ignore top level value.
   *
   * @return      this value for chaining
   *
   */
  public @Nonnull Combination withIgnoreTop()
  {
    m_ignoreTop = true;

    return this;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The entry to extract values from. */
  private @Nonnull ValueGroup m_entry;

  /** The name of the value to combine. */
  private @Nonnull String m_name;

  /** Flag if the top level value should be ignored. */
  private boolean m_ignoreTop = false;

  /** All the values found. */
  private @Nullable Multimap<V, String> m_values = ArrayListMultimap.create();

  /** All the expressions found. */
  private @Nullable Multimap<Expression, ValueGroup> m_expressions =
    ArrayListMultimap.create();

  /** Flag if already combine. */
  private boolean m_combined = false;

  /** Lazy cache for the total value. */
  private @Nullable V m_total;

  /** Lazy cache for the minimal value. */
  private @Nullable V m_min;

  /** Lazy cache for the maximal value. */
  private @Nullable V m_max;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getName --------------------------------

  /**
   * Get the name of the combined value.
   *
   * @return      the name of the combined value
   *
   */
  public @Nonnull String getName()
  {
    return m_name;
  }

  //........................................................................
  //------------------------------- getEntry -------------------------------

  /**
   * Get the entry this combinaton is based on.
   *
   * @return  the entry
   *
   */
  public AbstractEntry getEntry()
  {
    return (AbstractEntry)m_entry;
  }

  //........................................................................
  //------------------------------ getTopValue -----------------------------

  /**
   * Get the top value from this entry.
   *
   * @return  the top value
   *
   */
  @SuppressWarnings("unchecked")
  public V getTopValue()
  {
    return (V)m_entry.getValue(m_name);
  }

  //........................................................................

  //-------------------------------- values --------------------------------

  /**
   * Get all the values for the combination. If the same value is found twice,
   * it is also returned twice.
   *
   * @return      a list with all the values
   *
   */
  public @Nonnull Collection<V> values()
  {
    combine();
    return Collections.unmodifiableCollection(m_values.keys());
  }

  //........................................................................
  //--------------------------- valuesPerGroup -----------------------------

  /**
   * Get the values for this combination, per bases.
   *
   * @return      the base values and their entries
   *
   */
  public @Nullable Multimap<V, String> valuesPerGroup()
  {
    combine();
    return Multimaps.unmodifiableMultimap(m_values);
  }

  //........................................................................
  //----------------------------- expressions ------------------------------

  /**
   * Get all the expressions for the combination. If the same value is found
   * twice, it is also returned twice.
   *
   * @return      a list with all the expressions
   *
   */
  public @Nonnull Collection<Expression> expressions()
  {
    combine();
    return Collections.unmodifiableCollection(m_expressions.keys());
  }

  //........................................................................
  //-------------------------- expressionPerGroup --------------------------

  /**
   * Get the expression per group they came from.
   *
   * @return      the base entries for the given expression per group
   *
   */
  public @Nullable Multimap<Expression, ValueGroup> expressionsPerGroup()
  {
    combine();
    return Multimaps.unmodifiableMultimap(m_expressions);
  }

  //........................................................................

  //--------------------------------- total --------------------------------

  /**
   * Get the total, combine value.
   *
   * @return      the total value
   *
   */
  @SuppressWarnings("unchecked")
  public @Nullable V total()
  {
    if(m_total == null)
    {
      combine();

      for(V value : m_values.keySet())
        for(String name : m_values.get(value))
        {
          if(m_total == null)
            m_total = value;
          else
            m_total = (V)m_total.add(value);
        }

      m_total = computeExpressions(m_total);

      if(m_total instanceof Units)
        m_total = (V)((Units)m_total).simplify();
    }

    return m_total;
  }

  //........................................................................
  //--------------------------------- min ----------------------------------

  /**
   * Get the minimal value, if any.
   *
   * @return  the minimal value
   *
   */
  public @Nullable V min()
  {
    if(m_min == null)
    {
      combine();

      for(V value : m_values.keySet())
        if(m_min == null)
          m_min = value;
        else if(value.compareTo(m_min) < 0)
          m_min = value;

      m_min = computeExpressions(m_min);
    }

    return m_min;
  }

  //........................................................................
  //--------------------------------- max ----------------------------------

  /**
   * Get the maximal value, if any.
   *
   * @return  the maximal value
   *
   */
  public @Nullable V max()
  {
    if(m_max == null)
    {
      combine();

      for(V value : m_values.keySet())
        if(m_max == null)
          m_max = value;
        else if(value.compareTo(m_max) > 0)
            m_max = value;

      m_max = computeExpressions(m_max);
    }

    return m_max;
  }

  //........................................................................
  //------------------------------- summary --------------------------------

  /**
   * Compute a summary of how the final value is computed.
   *
   * @return      the computed summary
   *
   */
  public @Nonnull Command summary()
  {
    combine();

    List<Object> commands = new ArrayList<Object>();

    for(V value : m_values.keySet())
    {
      commands.add(value.format());
      commands.add(" from ");
      boolean first = true;
      for(String name : m_values.get(value))
      {
        if(first)
          first = false;
        else
          commands.add(", ");

        commands.add(name);
      }
      commands.add(new Linebreak());
    }

    for(Expression expression : m_expressions.keySet())
    {
      commands.add(expression);
      commands.add(" from ");
      boolean first = true;
      for(ValueGroup entry : m_expressions.get(expression))
      {
        if(first)
          first = false;
        else
          commands.add(", ");

        commands.add(entry.getName());
      }
      commands.add(new Linebreak());
    }

    return new Command(commands);
  }

  //........................................................................
  //----------------------------- isArithmetic -----------------------------

  /**
   * Check if the value combined is arithmetic.
   *
   * @return      true if it arithmetic, false if not
   *
   */
  public boolean isArithmetic()
  {
    combine();
    if(m_values.isEmpty())
      return true;

    return m_values.keySet().iterator().next().isArithmetic();
  }

  //........................................................................
  //-------------------------- computeExpressions --------------------------

  /**
   * Compute the value, taking all expressions into account.
   *
   * @param       inValue the value to start from
   *
   * @return      the adjusted value from all the expressions
   *
   */
  @SuppressWarnings("unchecked")
  public @Nullable V computeExpressions(@Nullable V inValue)
  {
    if(inValue == null)
      inValue = getAnyValue(m_entry, m_name);

    if(inValue == null)
      return null;

    if(m_expressions.isEmpty())
      return inValue;

    V value = inValue;
    Expression.Shared shared = new Expression.Shared();
    for(Expression expression
          : new TreeSet<Expression>(m_expressions.keySet()))
      for(ValueGroup entry : m_expressions.get(expression))
        if(entry != null)
          value = (V)expression.compute(m_entry, value, shared);

    return value;
  }

  //........................................................................
  //----------------------------- getAnyValue ------------------------------

  /**
   * Get any value for the given name, even if it is undefined or comes from a
   * base entry.
   *
   * @param      inEntry the entry to look in for
   * @param      inName  the name of the value
   *
   * @return     the value found or null if it could not be found in this entry
   *             or any of its bases
   *
   */
  @SuppressWarnings("unchecked")
  public @Nullable V getAnyValue(@Nonnull ValueGroup inEntry,
                                 @Nonnull String inName)
  {
    V value = (V)inEntry.getValue(m_name);
    if(value != null)
      return value;

    for(BaseEntry base : inEntry.getBaseEntries())
    {
      if(base == null)
        continue;

      value = getAnyValue(base, inName);
      if(value != null)
        return value;
    }

    return null;
  }

  //........................................................................

  //------------------------------- toString -------------------------------

  /**
   * Convert to a human redable string for debugging.
   *
   * @return      the string representation
   *
   */
  public @Nonnull String toString()
  {
    combine();
    return "combination of " + m_name + " in " + m_entry.getName() + ": "
      + toString(m_values) + " [" + toString(m_expressions) + "], total "
      + m_total + ", min " + m_min + ", max " + m_max;
  }

  //........................................................................
  //------------------------------- toString -------------------------------

  /**
   * Convert the given map to a human redable string for debugging.
   *
   * @param       inMap the map to convert
   *
   * @return      the string representation
   *
   */
  @SuppressWarnings("unchecked")
  private @Nonnull String toString(Multimap inMap)
  {
    StringBuilder builder = new StringBuilder();

    for(Object object : inMap.keySet())
    {
      builder.append(object.toString() + ": ");
      for(Object entry : inMap.get(object))
        builder.append(entry + ", ");
    }

    return builder.toString();
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- combine --------------------------------

  /**
   * Combine all the values together and preprocess them.
   *
   */
  private void combine()
  {
    if(m_combined)
      return;

    m_combined = true;
    combine(m_entry, m_ignoreTop);
  }

  //........................................................................
  //------------------------------- combine --------------------------------

  /**
   * Combine the base values of the given entry into this combination.
   *
   * @param    inEntry     the entry to combine for
   * @param    inIgnoreTop if true, ignore the top value completely
   *
   */
  @SuppressWarnings("unchecked") // we just have to trust...
  private void combine(ValueGroup inEntry, boolean inIgnoreTop)
  {
    V value = null;
    if(!inIgnoreTop)
    {
      value = (V)inEntry.getValue(m_name);
      if(value != null)
      {
        if(value.isDefined())
          m_values.put(value, inEntry.getName());

        if(value.hasExpression())
          m_expressions.put(value.getExpression(), inEntry);
      }
    }

    Variable variable = inEntry.getVariable(m_name);
    if(value == null || !value.isDefined()
       || (variable != null && variable.isWithBases()))
      for(BaseEntry base : inEntry.getBaseEntries())
      {
        if(base == null)
          continue;

        value = (V)base.getValue(m_name);
        if(value == null)
          continue;

        combine(base, false);
      }

    // check if there is something that changes the value, we do it last to
    // have previous computation available.
    inEntry.adjustCombination(m_name, this);
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the given value with the given name to the combination.
   *
   * @param       inValue the value to add
   * @param       inName  the name to add
   *
   */
  public void add(V inValue, String inName)
  {
    clear();
    m_values.put(inValue, inName);
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the given value with the given entry to the combination.
   *
   * @param       inValue the value to add
   * @param       inEntry the entry the value came from
   *
   */
  public void add(V inValue, ValueGroup inEntry)
  {
    add(inValue, inEntry.getName());
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the given expression with the given entry to the combination.
   *
   * @param       inExpression the expression to add
   * @param       inEntry      the entry the value came from
   *
   */
  public void add(Expression inExpression, ValueGroup inEntry)
  {
    clear();
    m_expressions.put(inExpression, inEntry);
  }

  //........................................................................
  //-------------------------------- clear ---------------------------------

  /**
   * Clear previous computation of the combination.
   *
   */
  public void clear()
  {
    m_total = null;
    m_min = null;
    m_max = null;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test
  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
