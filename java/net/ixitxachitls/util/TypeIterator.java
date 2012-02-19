/******************************************************************************
 * Copyright (c) 2002-2011 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.util;

import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is an iterator that converts a given iterator into an iterator of a
 * derivced class, ignoring all elements that cannot be converted..
 *
 * @param         <T> The type to convert to.
 * @param         <O> The original type.
 *
 * @file          TypeIterator.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
public class TypeIterator<O, T extends O> implements Iterator<T>
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------- TypeIterator ---------------------------

  /**
    * Create the iterator using the given iterator.
    *
    * @param       inValues the original iterator with the values
    * @param       inType   the type of
    *
    */

  public TypeIterator(@Nonnull Iterator<O> inValues, @Nonnull Class<T> inType)
  {
    m_iterator = inValues;
    m_type     = inType;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The unterlying iterator. */
  private @Nonnull Iterator<O> m_iterator;

  /** The current element. */
  private T m_current;

  /** The type of the new destination iterator. */
  private @Nonnull Class<T> m_type;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- hasNext --------------------------------

  /**
   * Check if there is a next value available.
   *
   * @return      true if there is another value, false if not
   *
   */
  @Override
@SuppressWarnings("unchecked") // need to cast below
  public boolean hasNext()
  {
    if(!m_iterator.hasNext())
      return false;

    if(m_current != null)
      return true;

    while(m_iterator.hasNext())
    {
      O current = m_iterator.next();

      if(m_type.isAssignableFrom(current.getClass()))
      {
        m_current = (T)current;

        return true;
      }
    }

    m_current = null;

    return false;
  }

  //........................................................................
  //--------------------------------- next ---------------------------------

  /**
   * Get the next object, advancing one step ahead.
   *
   * @return      the next Object
   *
   */
  @Override
public @Nonnull T next()
  {
    if(!hasNext())
      throw new java.util.NoSuchElementException("already at end!");

    T result = m_current;
    m_current = null;
    return result;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- remove --------------------------------

  /**
   * Remove the current element.
   *
   * This method is not supported and always throws an
   * UnsupportedOperationException.
   *
   */
  @Override
public void remove()
  {
    throw new UnsupportedOperationException("this iterator does no "
                                            + "support the removal of "
                                            + "entries");
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- all ------------------------------------------------------------

    /** Testing all iterating. */
    @org.junit.Test
    public void all()
    {
      java.util.List<Number> list = new java.util.ArrayList<Number>();
      list.add(1);
      list.add(2);
      list.add(3.4);
      list.add(4);
      list.add(5);

      Iterator<Integer> i =
        new TypeIterator<Number, Integer>(list.iterator(), Integer.class);

      assertContent("iterator", i, 1, 2, 4, 5);
    }

    //......................................................................
  }

  //......................................................................
}
