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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.util.Pair;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A value combined from different base values.
 *
 * @file          Combined.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param         <T> the type of values combined
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Combined<T extends Value<T>>
{
  //------------------------------------------------------------------- Node

  /**
   * A node storing the collected value for an entry in the derivation chain.
   */
  @ParametersAreNonnullByDefault
  private static class Node<U extends Value<U>>
  {
    //-------------------------------- Node --------------------------------

    /**
     * Create the node.
     *
     * @param       inValue       the value collected, if any
     * @param       inEntry       the entry where the value came from
     * @param       inDescription the description of the value collected
     */
    public Node(@Nullable U inValue, AbstractEntry inEntry,
                String inDescription)
    {
      m_value = inValue;
      m_entry = inEntry;
      m_description = inDescription;
    }

    //......................................................................
    //-------------------------------- Node --------------------------------

    /**
     * Create a node without a description.
     *
     * @param       inValue the value to build from
     * @param       inEntry the entry to build from
     */
    public Node(@Nullable U inValue, AbstractEntry inEntry)
    {
      m_value = inValue;
      m_entry = inEntry;
      m_description = null;
    }

    //......................................................................

    /** The value of this node. */
    private final @Nullable U m_value;

    /** The entry the value for this node came from. */
    private final AbstractEntry m_entry;

    /** The description, if any, for this value. */
    private final @Nullable String m_description;

    /** The children of this node. */
    private final List<Node<U>> m_children = Lists.newArrayList();

    //---------------------------- asDescribed -----------------------------

    /**
     * Create a new node from this one with a changed description.
     *
     * @param   inDescription the new description
     *
     * @return  the newly created node
     */
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

    //......................................................................

    //------------------------------ addChild ------------------------------

    /**
     * Add a child to this node.
     *
     * @param       inNode the child node to add
     */
    public void addChild(Node<U> inNode)
    {
      m_children.add(inNode);
    }

    //......................................................................
    //------------------------------ getValue ------------------------------

    /**
     * Get the value of this node, if any.
     *
     * @return      the value, if any
     */
    public @Nullable U getValue()
    {
      return m_value;
    }

    //......................................................................
    //------------------------------ getEntry ------------------------------

    /**
     * Get the entry this node represents and got its value from.
     *
     * @return      the entry the value came from
     */
    public AbstractEntry getEntry()
    {
      return m_entry;
    }

    //......................................................................
    //--------------------------- getDescription ---------------------------

    /**
     * Get the description (group and text) for the contribution.
     *
     * @return  the description
     */
    public String getDescription()
    {
      return m_entry.getName()
        + (m_description == null ? "" : " (" + m_description + ")");
    }

    //......................................................................

    //------------------------------ toString ------------------------------

    /**
     * Convert to string for debugging.
     *
     * @return      the converted string
     */
    @Override
    public String toString()
    {
      return m_entry.getName() + " = " + m_value + " " + m_children;
    }

    //......................................................................
    //------------------------------- values -------------------------------

    /**
     * Get all the values collected by this node and all its children.
     *
     * @param   inIgnoreTop ignore the top most value and only return bases
     *
     * @return  a list of value node list pairs
     */
    @SuppressWarnings("unchecked") // generic array creation?
    public List<Pair<U, List<Node<U>>>> values(boolean inIgnoreTop)
    {
      List<Pair<U, List<Node<U>>>> values = Lists.newArrayList();

      if(!inIgnoreTop && m_value != null && m_value.isDefined())
        values.add(new Pair<U, List<Node<U>>>(m_value,
                                              Lists.newArrayList(this)));
      else
        for(Node<U> child : m_children)
          valueLoop:
          for(Pair<U, List<Node<U>>> value : child.values(false))
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

    //......................................................................
  }

  //........................................................................
  //----------------------------------------------------------- Contribution

  /**
   * A contribution to a value.
   *
   * @param <V> the value type that is contributed
   */
  @Immutable
  @ParametersAreNonnullByDefault
  private class Contribution<V> implements Comparable<Contribution<V>>
  {
    //--------------------------- Contribution ---------------------------

    /**
     * Create the contribution with a specific value.
     *
     * @param    inValue the value contributed
     * @param    inGroup the group (entry, extension) contributing the value
     * @param    inText  the text describing why the value was contributed
     */
    public Contribution(V inValue, ValueGroup inGroup, @Nullable String inText)
    {
      m_value = inValue;
      m_group = inGroup;
      m_text = inText;
    }

    //......................................................................

    /** The value contributed. */
    private V m_value;

    /** The group contributing the value. */
    private ValueGroup m_group;

    /** The text describing the contribution. */
    private @Nullable String m_text;

    /**
     * Get the value contributed.
     *
     * @return  the value
     */
    public V getValue()
    {
      return m_value;
    }

    //------------------------------ getGroup ------------------------------

    /**
     * Get the entry (or extension) that contributed the value.
     *
     * @return  the entry
     */
    @SuppressWarnings("unused")
    public ValueGroup getGroup()
    {
      return m_group;
    }

    //......................................................................
    //------------------------------ getText -------------------------------

    /**
     * Get the text given with the value.
     *
     * @return  the text
     */
    public @Nullable String getText()
    {
      return m_text;
    }

    //......................................................................
    //--------------------------- getDescription ---------------------------

    /**
     * Get the description (group and text) for the contribution.
     *
     * @return  the description
     */
    public String getDescription()
    {
      return m_group.getName() + (m_text == null ? "" : " (" + m_text + ")");
    }

    //......................................................................
    //----------------------------- compareTo ------------------------------

    /**
     * Compare this expression to the other one.
     *
     * @param       inOther the expression to compare to
     *
     * @return      <0 if this is smaller than the other, >0 if bigger, 0 if
     *              equal
     *
     */
    @Override
    public int compareTo(@Nullable Contribution<V> inOther)
    {
      if(inOther == null)
        return +1;

      V value = getValue();
      V otherValue = inOther.getValue();

      if (value instanceof Value<?> && !(otherValue instanceof Value<?>))
        return +1;

      if (!(value instanceof Value<?>) && otherValue instanceof Value<?>)
        return -1;

      if (value instanceof Expression && !(otherValue instanceof Expression))
        return +1;

      if (!(value instanceof Expression) && otherValue instanceof Expression)
        return -1;

      if (value instanceof Value<?>)
        return ((Value<?>)value).compareTo(otherValue);

      if (value instanceof Expression)
        return ((Expression)value).compareTo((Expression)otherValue);

      return 0;
    }

    //......................................................................
    //------------------------------- equals -------------------------------

    /**
     * Check if the two objects are equal.
     *
     * @param       inOther the object to compare with
     *
     * @return      true if the objects are equal, false if not
     *
     */
    @Override
    public boolean equals(Object inOther)
    {
      return super.equals(inOther);
    }

    //......................................................................
    //------------------------------ hashCode ------------------------------

    /**
     * Compute the hash code.
     *
     * @return      the hash code
     */
    @Override
    public int hashCode()
    {
      return super.hashCode();
    }

    //......................................................................
    //------------------------------ toString ------------------------------

    /**
     * Convert to a string for debugging.
     *
     * @return the converted string
     */
    @Override
    public String toString()
    {
      return m_group.getName() + (m_text == null ? "" : " (" + m_text + ")")
        + ": " + m_value;
    }

    //......................................................................
  }

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------- Combined -------------------------------

  /**
   * Create a combined value.
   *
   * @param       inName   the name of the combined value
   * @param       inEntry  the entry to combine from
   */
  public Combined(String inName, AbstractEntry inEntry)
  {
    m_name = inName;
    m_entry = inEntry;
    m_root = collectValues(inName, inEntry);
  }

  //........................................................................

 //........................................................................

  //-------------------------------------------------------------- variables

  /** The name of the collected value. */
  private final String m_name;

  /** The entry the values are collected from. */
  private final AbstractEntry m_entry;

  /** The root node with the collected values. */
  private final @Nullable Node<T> m_root;

  /** The contributed values. */
  private final List<Contribution<T>> m_values = Lists.newArrayList();

  /** The contributed modifiers. */
  private final List<Contribution<Modifier>> m_modifiers = Lists.newArrayList();

  /** The contributed expression. */
  private final List<Contribution<Expression>> m_expressions =
    Lists.newArrayList();

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getName --------------------------------

  /**
   * Get the name of the combined value.
   *
   * @return      the name
   */
  public String getName()
  {
    return m_name;
  }

  //........................................................................
  //------------------------------- getEntry -------------------------------

  /**
   * Get the entry this value is combined from.
   *
   * @return  the entry
   */
  public AbstractEntry getEntry()
  {
    return m_entry;
  }

  //........................................................................
  //----------------------------- getTopValue ------------------------------

  /**
   * Get the top most value. This is the value that of the top most entry in
   * the derivation hiearachy of entries.
   *
   * @return  the top value, if any
   */
  public @Nullable T getTopValue()
  {
    if(m_root != null)
      return m_root.getValue();

    return null;
  }

  //........................................................................

  //---------------------------- collectValues -----------------------------

  /**
   * Collect all nodes with all the values from the given entry.
   *
   * @param      inName   the name of the value to collect
   * @param      inEntry  the entry to get the values from
   *
   * @return     a node with the value information
   *
   */
  @SuppressWarnings("unchecked") // need to cast
  private Node<T> collectValues(String inName, AbstractEntry inEntry)
  {
    Node<T> root = null;

    T value = (T)inEntry.getValue(inName);
    if(value instanceof Modifier)
      addModifier((Modifier)value, inEntry, "collect");
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

  //........................................................................
  //-------------------------------- values --------------------------------

  /**
   * Get all the values stored.
   *
   * @return  the values as a list of pairs with a value and a list of nodes
   *          where the value was added from
   */
  private List<Pair<T, List<Node<T>>>> values()
  {
    if(m_root == null)
      return Lists.newArrayList();

    List<Pair<T, List<Node<T>>>> values = m_root.values(false);

    if(m_modifiers.isEmpty() || values.size() == 0)
      return values;

    // We will add the last values to the modifiers.
    if(values.get(values.size() - 1).first().getClass() == Number.class)
      values.remove(values.size() - 1);

    return values;
  }

  //........................................................................
  //------------------------------ valuesOnly ------------------------------

  /**
   * Get the values and only the values stored.
   *
   * @return  a list of the values, either collected or contributed.
   */
  public List<T> valuesOnly()
  {
    List<T> values = Lists.newArrayList();

    for(Pair<T, List<Node<T>>> value : values())
      values.add(value.first());

    for(Contribution<T> value : m_values)
      values.add(value.getValue());

    return values;
  }

  //........................................................................
  //------------------------ valuesWithDescriptions ------------------------

  /**
   * Get all values with descriptions where they came from.
   *
   * @return      a list of value and description pairs
   */
  public List<Pair<T, List<Pair<T, String>>>> valuesWithDescriptions()
  {
    List<Pair<T, List<Pair<T, String>>>> values = Lists.newArrayList();
    for(Pair<T, List<Node<T>>> value : values())
    {
      List<Pair<T, String>> descriptions = Lists.newArrayList();
      for(Node<T> node : value.second())
        descriptions.add(new Pair<T, String>(node.getValue(),
                                             node.getDescription()));

      values.add(new Pair<T, List<Pair<T, String>>>(value.first(),
                                                    descriptions));
    }

    for(Contribution<T> value : m_values)
      values.add(new Pair<T, List<Pair<T, String>>>
                 (value.getValue(),
                  ImmutableList.of(new Pair<T, String>
                                   (value.getValue(),
                                    value.getDescription()))));

    return values;
  }

  //........................................................................
  //-------------------------------- total ---------------------------------

  /**
   * Compute the total of all values stored.
   *
   * @return      the total value, if any
   */
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

  //........................................................................
  //--------------------------------- base ---------------------------------

  /**
   * Compute the total base value, ignoring the top value.
   *
   * @return      the total base value, if any
   */
  @SuppressWarnings("unchecked")
  public @Nullable T base()
  {
    T total = null;
    for (Pair<T, List<Node<T>>> value : m_root.values(true))
      if(total == null)
        total = value.first();
      else
        total = total.add(value.first());

    total = computeExpressions(total);
    if(total instanceof Units)
      total = (T)((Units)total).simplify();

    return total;
  }

  //........................................................................
  //--------------------------------- min ----------------------------------

  /**
   * Compute the minimal value.
   *
   * @return      the minimal of all available values
   */
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

  //........................................................................
  //--------------------------------- max ----------------------------------

  /**
   * Compute the maximal value.
   *
   * @return  the maximal value, if any
   */
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

  //........................................................................
  //------------------------------- modifier -------------------------------

  /**
   * Get the modifiers for the value as a modified number.
   *
   * @return  all the modifiers
   */
  @SuppressWarnings({ "rawtypes" })
  public ModifiedNumber modifier()
  {
    ModifiedNumber modifier = new ModifiedNumber(0);

    for (Contribution<Modifier> contribution : m_modifiers)
      modifier.withModifier(contribution.getValue(), contribution.getText());

    return modifier;
  }

  //........................................................................

  //----------------------------- hasModifiers -----------------------------

  /**
   * Check whether the combined value has any modifiers.
   *
   * @return      true if there are modifiers, false if not
   */
  public boolean hasModifiers()
  {
    return !m_modifiers.isEmpty();
  }

  //........................................................................
  //------------------------------ isEditable ------------------------------

  /**
   * Determine whether the combined value can be edited or not.
   *
   * @return      true if the value is editable, false if not
   */
  public boolean isEditable()
  {
    return m_root != null && m_root.getValue() != null;
  }

  //........................................................................
  //------------------------------- hasValue -------------------------------

  /**
   * Check if there is a value at all here.
   *
   * @return      true if there is a value, false if not.
   *
   */
  public boolean hasValue()
  {
    for(T value : valuesOnly())
      if(value.isDefined())
        return true;

    return false;
  }

  //........................................................................


  //------------------------------- toString -------------------------------

  /**
   * Convert the combined value to a string for debugging.
   *
   * @return      the converted string
   */
  @Override
  public String toString()
  {
    return m_name + " (" + m_entry.getName() + "): " + m_root
      + ", modifiers " + m_modifiers
      + ", values " + m_values
      + ", expressions " + m_expressions;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- add ----------------------------------

  /**
   * Add another combined value to this one.
   *
   * @param       inCombined    the combined value to add
   * @param       inDescription the description of the value that is added
   */
  public void add(Combined<T> inCombined, String inDescription)
  {
    if (inCombined.m_root != null)
      m_root.addChild(inCombined.m_root.asDescribed(inDescription));

    m_modifiers.addAll(inCombined.m_modifiers);
  }

  //........................................................................
  //------------------------------- addValue -------------------------------

  /**
   * Add a contributed value to the combined value.
   *
   * @param       inValue        the value to add
   * @param       inGroup        the group the value came from
   * @param       inDescription  a description of why the value was added
   */
  public void addValue(T inValue, ValueGroup inGroup,
                       @Nullable String inDescription)
  {
    if(m_root != null)
      m_root.addChild(new Node<T>(inValue, inGroup.getEntry(), inDescription));
    else
      m_values.add(new Contribution<T>(inValue, inGroup, inDescription));
  }

  //........................................................................
  //----------------------------- addModifier ------------------------------

  /**
   * Add a modifier to the combined value.
   *
   * @param       inModifier     the modifier to add
   * @param       inGroup        the group the modifier came from
   * @param       inDescription  a description of why the modifier was added
   */
  public void addModifier(Modifier inModifier, ValueGroup inGroup,
                          @Nullable String inDescription)
  {
    m_modifiers.add(new Contribution<Modifier>(inModifier, inGroup,
                                               inDescription));
  }

  //........................................................................
  //---------------------------- addExpression -----------------------------

  /**
   * Add an expression.
   *
   * @param       inExpression   the expression to add
   * @param       inGroup        the group the expression came from
   * @param       inDescription  a description of why the expression was added
   */
  public void addExpression(Expression inExpression, ValueGroup inGroup,
                            @Nullable String inDescription)
  {
    m_expressions.add(new Contribution<Expression>(inExpression, inGroup,
                                                   inDescription));
  }

  //........................................................................

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
  // public static class Test extends net.ixitxachitls.util.test.TestCase
  // {
  // }

  //........................................................................
}
