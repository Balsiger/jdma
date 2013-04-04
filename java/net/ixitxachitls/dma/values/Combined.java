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

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.Lists;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.util.Pair;
import net.ixitxachitls.util.Strings;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A value combined from different base values.
 *
 * @file          Combined.java
 *
 * @author        Peter Balsiger
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Combined<T extends Value<T>>
{
  //------------------------------------------------------------------- Node

  @ParametersAreNonnullByDefault
  public static class Node<U extends Value<U>>
  {
    public Node(@Nullable U inValue, AbstractEntry inEntry,
                String inDescription)
    {
      m_value = inValue;
      m_entry = inEntry;
      m_description = inDescription;
    }

    public Node(@Nullable U inValue, AbstractEntry inEntry)
    {
      m_value = inValue;
      m_entry = inEntry;
      m_description = null;
    }

    private final @Nullable U m_value;
    private final AbstractEntry m_entry;
    private final @Nullable String m_description;
    private final List<Node<U>> m_children = Lists.newArrayList();

    public Node<U> asDescribed(String inDescription)
    {
      String description = getDescription();
      if(description == null)
        description = inDescription;
      else
        description += ", " + inDescription;

      Node<U> result = new Node<U>(getValue(), getEntry(), description);

      for(Node<U> child : m_children)
        result.addChild(child.asDescribed(inDescription));

      return result;
    }

    public void addChild(Node<U> inNode)
    {
      m_children.add(inNode);
    }

    public @Nullable U getValue()
    {
      return m_value;
    }

    public AbstractEntry getEntry()
    {
      return m_entry;
    }

    public @Nullable String getDescription()
    {
      return m_description;
    }

    public @Nullable U getTopValue()
    {
      return m_value;
    }

    public String toString()
    {
      return m_entry.getName() + " = " + m_value + " " + m_children;
    }

    @SuppressWarnings("unchecked") // generic array creation?
    public List<Pair<U, List<Node<U>>>> values()
    {
      List<Pair<U, List<Node<U>>>> values = Lists.newArrayList();

      if(m_value != null && m_value.isDefined())
        values.add(new Pair<U, List<Node<U>>>(m_value,
                                              Lists.newArrayList(this)));
      else
        for(Node<U> child : m_children)
          valueLoop:
          for(Pair<U, List<Node<U>>> value : child.values())
          {
            for(int i = 0; i < values.size(); i++)
            {
              // Can only add same types.
              if (value.first().getClass() != values.get(i).first().getClass())
                continue;

              try
              {
                U first = values.get(i).first();
                U second = value.first();
                U added = first.add(second);
                List<Node<U>> nodes = values.get(i).second();
                nodes.addAll(value.second());
                values.set(i, new Pair<U, List<Node<U>>>(added, nodes));

                continue valueLoop;
              }
              catch(UnsupportedOperationException e)
              {
                // simply ignore it and try the next value
              }
            }

            values.add(new Pair<U, List<Node<U>>>(value.first(),
                                                  value.second()));
          }

      return values;
    }
  }

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  public Combined(String inName, AbstractEntry inEntry)
  {
    m_name = inName;
    m_entry = inEntry;
    m_root = collectValues(inName, inEntry);
  }

  //........................................................................

  //-------------------------------------------------------------- variables

  private final String m_name;
  private final AbstractEntry m_entry;
  private final @Nullable Node<T> m_root;
  private final List<Contribution<T>> m_values = Lists.newArrayList();
  private final List<Contribution<Modifier>> m_modifiers = Lists.newArrayList();
  private final List<Contribution<Expression>> m_expressions =
    Lists.newArrayList();

  //........................................................................

  //-------------------------------------------------------------- accessors

  @SuppressWarnings("unchecked") // need to cast
  private Node<T> collectValues(String inName, AbstractEntry inEntry)
  {
    Node<T> root = null;

    T value = (T)inEntry.getValue(inName);
    if(value instanceof Modifier)
      addModifier(new Contribution<Modifier>((Modifier)value, inEntry,
                                             "collect"));
    else
      root = new Node<T>((T)inEntry.getValue(inName), inEntry);

    for(BaseEntry base : inEntry.getBaseEntries())
    {
      if(base == null)
        continue;

      Node<T> child = collectValues(inName, base);
      if (child != null)
        if(root != null)
          root.addChild(collectValues(inName, base));
        else
          root = child;
    }

    return root;
  }

  public String getName()
  {
    return m_name;
  }

  public AbstractEntry getEntry()
  {
    return m_entry;
  }

  public String toString()
  {
    return m_name + " (" + m_entry.getName() + "): " + m_root
      + ", modifiers " + m_modifiers
      + ", values " + m_values
      + ", expressions " + m_expressions;
  }

  public void addValue(Contribution<T> inContribution)
  {
    m_values.add(inContribution);
  }

  public void addModifier(Contribution<Modifier> inContribution)
  {
    m_modifiers.add(inContribution);
  }

  public void addExpression(Contribution<Expression> inContribution)
  {
    m_expressions.add(inContribution);
  }

  public List<T> valuesOnly()
  {
    List<T> values = Lists.newArrayList();

    for(Pair<T, List<Node<T>>> value : values())
      values.add(value.first());

    for(Contribution<T> value : m_values)
      values.add(value.getValue());

    return values;
  }

  @SuppressWarnings("unchecked")
  public @Nullable T min()
  {
    T min = null;
    for (Pair<T, List<Node<T>>> value : values())
      if(min == null)
        min = value.first();
      else if(value.first().compareTo(min) < 0)
        min = value.first();

    min = computeExpressions(min);
    if(min instanceof Units)
      min = (T)((Units)min).simplify();

    return min;
  }

  @SuppressWarnings("unchecked")
  public @Nullable T max()
  {
    T max = null;
    for (Pair<T, List<Node<T>>> value : values())
      if(max == null)
        max = value.first();
      else if(value.first().compareTo(max) > 0)
        max = value.first();

    max = computeExpressions(max);
    if(max instanceof Units)
      max = (T)((Units)max).simplify();

    return max;
  }

  public List<Pair<T, String>> valuesWithDescriptions()
  {
    List<Pair<T, String>> values = Lists.newArrayList();
    for(Pair<T, List<Node<T>>> value : values())
    {
      List<String> descriptions = Lists.newArrayList();
      for(Node<T> node : value.second())
      {
        String description = node.getDescription();
        if(description != null)
          descriptions.add(description);
      }

      values.add(new Pair<T, String>
                 (value.first(),
                  Strings.COMMA_JOINER.join(descriptions)));
    }

    for(Contribution<T> value : m_values)
      values.add(new Pair<T, String>(value.getValue(), value.getDescription()));

    return values;
  }

  @SuppressWarnings("unchecked")
  public @Nullable T total()
  {
    T total = null;
    for(T value : valuesOnly())
      if(total == null)
        total = value;
      else
        total = total.add(value);

    total = computeExpressions(total);
    if(total instanceof Units)
      total = (T)((Units)total).simplify();

    return total;
  }

  private List<Pair<T, List<Node<T>>>> values()
  {
    if(m_root == null)
      return Lists.newArrayList();

    List<Pair<T, List<Node<T>>>> values = m_root.values();

    if(m_modifiers.isEmpty() || values.size() == 0)
      return values;

    // We will add the last values to the modifiers.
    if(values.get(values.size() - 1).first().getClass() == Number.class)
      values.remove(values.size() - 1);

    return values;
  }

  public void add(Combined<T> inCombined, String inDescription)
  {
    if (inCombined.m_root != null)
      m_root.addChild(inCombined.m_root.asDescribed(inDescription));

    m_modifiers.addAll(inCombined.m_modifiers);
  }

  public boolean hasModifiers()
  {
    return !m_modifiers.isEmpty();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public ModifiedNumber modifier()
  {
    List modifiers = Lists.newArrayList();
    modifiers.addAll(m_modifiers);
    List<Pair<T, List<Node<T>>>> values;
    if (m_root != null)
      values = m_root.values();
    else
      values = Lists.newArrayList();

    if(values.size() > 0
       && values.get(values.size() - 1).first().getClass() == Number.class)
      for(Node<T> node : values.get(values.size() - 1).second())
        modifiers.add
          (new Contribution<T>(node.getValue(), node.getEntry(),
                               node.getDescription()));

    return ModifiedNumber.create(0, modifiers);
  }

  public @Nullable T getTopValue()
  {
    if(m_root != null)
      return m_root.getTopValue();

    return null;
  }

  public boolean isEditable()
  {
    return m_root != null && m_root.getTopValue() != null;
  }

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

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
  private @Nullable T computeExpressions(@Nullable T inValue)
  {
    if(inValue == null)
      return null;

    if(m_expressions.isEmpty())
      return inValue;

    T value = inValue;
    Expression.Shared shared = new Expression.Shared();
    Collections.sort(m_expressions);
    for(Contribution<Expression> contribution : m_expressions)
      value = (T)contribution.getValue().compute(m_entry, value, shared);

    return value;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }

  //........................................................................
}
