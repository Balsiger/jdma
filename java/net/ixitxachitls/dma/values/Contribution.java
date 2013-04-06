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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.entries.ValueGroup;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A contribution to a value to compute the final value. This is not a real
 * dma value.
 *
 * @file          Contribution.java
 * @author        balsiger@ixitxachits.net (Peter Balsiger)
 *
 * @param         <V> the value the contribution stores
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Contribution<V> implements Comparable<Contribution<V>>
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- Contribution -----------------------------

  /**
   * Create the contribution with a specific value.
   *
   * @param    inValue the value contributed
   * @param    inGroup the group (entry, extension) contributing the value
   * @param    inText  the text describing why the value was contributed
   */
  public Contribution(V inValue, ValueGroup inGroup, String inText)
  {
    m_value = inValue;
    m_group = inGroup;
    m_text = inText;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The value contributed. */
  private V m_value;

  /** The group contributing the value. */
  private ValueGroup m_group;

  /** The text describing the contribution. */
  private String m_text;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getValue -------------------------------

  /**
   * Get the value contributed.
   *
   * @return  the value
   */
  public V getValue()
  {
    return m_value;
  }

  //........................................................................
  //------------------------------- getGroup -------------------------------

  /**
   * Get the entry (or extension) that contributed the value.
   *
   * @return  the entry
   */
  public ValueGroup getGroup()
  {
    return m_group;
  }

  //........................................................................
  //------------------------------- getText --------------------------------

  /**
   * Get the text given with the value.
   *
   * @return  the text
   */
  public @Nullable String getText()
  {
    return m_text;
  }

  //........................................................................
  //---------------------------- getDescription ----------------------------

  /**
   * Get the description (group and text) for the contribution.
   *
   * @return  the description
   */
  public String getDescription()
  {
    return m_group.getName() + (m_text == null ? "" : " (" + m_text + ")");
  }

  //........................................................................
  //------------------------------ compareTo -------------------------------

  /**
   * Compare this expression to the other one.
   *
   * @param       inOther the expression to compare to
   *
   * @return      <0 if this is smaller than the other, >0 if bigger, 0 if equal
   *
   */
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

  //........................................................................
  //-------------------------------- equals --------------------------------

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

  //........................................................................
  //------------------------------- hashCode -------------------------------

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

  //........................................................................

  //------------------------------- toString -------------------------------

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

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  // public static class Test extends net.ixitxachitls.util.test.TestCase
  // {
  // }

  //........................................................................
}
