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

import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.entries.Variable;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Divider;
import net.ixitxachitls.output.commands.Linebreak;
import net.ixitxachitls.output.commands.Span;
import net.ixitxachitls.output.commands.Window;
import net.ixitxachitls.util.Strings;

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
  private @Nullable Multimap<V, ValueGroup> m_values;

  /** All the expressions found. */
  private @Nullable Multimap<Expression, ValueGroup> m_expressions;

  /** Lazy cache for the total value. */
  private @Nullable V m_total;

  /** Lazy cache for the minimal value. */
  private @Nullable V m_min;

  /** Lazy cache for the maximal value. */
  private @Nullable V m_max;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- values --------------------------------

  /**
   * Get all the values for the combination. If the same value is found twice,
   * it is also returned twice.
   *
   * @return      a list with all the value
   *
   */
  public @Nonnull Collection<V> values()
  {
    return Collections.unmodifiableCollection(m_values.keys());
  }

  //........................................................................

  //-------------------------------- format --------------------------------

  /**
   * Format the combination of all the values for printing.
   *
   * @param       inDM true if formatting for the DM, false if not
   *
   * @return      the formatted value
   *
   */
  public @Nonnull Command format(boolean inDM)
  {
    combine();

    if(isArithmetic())
    {
      V total = total();
      if(total == null)
        return new Command("");

      if(inDM)
        return new Window(total.format(), summary(), "", "base");

      return total.format();
    }

    // not arithmetic
    List<Object> commands = new ArrayList<Object>();
    for(V value : m_values.keySet())
    {
      List<String> nameList = new ArrayList<String>();
      for(ValueGroup entry : m_values.get(value))
        nameList.add(entry.getName());
      String names = Strings.toString(nameList, ", ", "");

      if(value instanceof FormattedText)
      {
        commands.add(new Divider("base-title", new Span("base-text", names)));
        commands.add(value.format());
      }
      else
      {
        if(!commands.isEmpty())
          if(value instanceof Text)
            commands.add(" ");
          else
            commands.add(", ");


        if(inDM)
          commands.add(new Window(new Span("base-value", value.format()),
                                  "from " + names, " [" + names + "]", "base"));
        else
          commands.add(value.format());
      }

    }

    return new Command(commands);
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
        for(ValueGroup entry : m_values.get(value))
          if(entry != null)
            if(m_total == null)
              m_total = value;
            else
              m_total = (V)m_total.add(value);

      if(!m_expressions.isEmpty())
      {
        Expression.Shared shared = new Expression.Shared();
        for(Expression expression
              : new TreeSet<Expression>(m_expressions.keySet()))
          for(ValueGroup entry : m_expressions.get(expression))
            if(entry != null)
              m_total = (V)expression.compute(m_total, shared);
      }

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
    }

    return m_max;
  }

  //........................................................................
  //------------------------------- summary --------------------------------

  /**
   * Compute a summary of how the final values is computed.
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
      for(ValueGroup entry : m_values.get(value))
      {
        if(first)
          first = false;
        else
          commands.add(", ");

        commands.add(entry.getName());
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

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- combine --------------------------------

  /**
   * Combine all the values together and preprocess them.
   *
   */
  private void combine()
  {
    if(m_values != null)
      return;

    m_values = ArrayListMultimap.create();
    m_expressions = ArrayListMultimap.create();
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
          m_values.put(value, inEntry);

        if(value.hasExpression())
          m_expressions.put(value.getExpression(), inEntry);
      }

      // check if there is something that changes the value
      inEntry.adjustCombination(m_name, this);
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
    m_values.put(inValue, inEntry);
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
    m_expressions.put(inExpression, inEntry);
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
