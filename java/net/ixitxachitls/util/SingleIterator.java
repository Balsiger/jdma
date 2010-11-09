/******************************************************************************
 * Copyright (c) 2002,2003 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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
import javax.annotation.Nullable;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * An iterator over a single (or none at all) element.
 *
 * @param         <T> The type each iterator element
 *
 * @file          SingleIterator.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class SingleIterator<T> implements Iterator<T>
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- SingleIterator ----------------------------

  /**
   * Create the iterator.
   *
   * @param       inElement the element to create with (may be null)
   *
   */
  public SingleIterator(@Nullable T inElement)
  {
    m_element = inElement;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The element in the iterator. */
  private @Nullable T m_element;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- hasNext --------------------------------

  /**
   * Determine if there is a next element in the list.
   *
   * @return      true if there is a next element, false if not
   *
   * @undefined   never
   *
   */
  public boolean hasNext()
  {
    return m_element != null;
  }

  //........................................................................
  //--------------------------------- next ---------------------------------

  /**
   * Determine the next element in the iterator.
   *
   * @return      the next element in the iterator
   *
   */
  public @Nonnull T next()
  {
    if(m_element == null)
      throw new java.util.NoSuchElementException("no more elements");

    T result = m_element;
    m_element = null;

    return result;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- remove --------------------------------

  /**
   * Remove the current element from the iterator.
   *
   * @undefined   UnsupportedOperationException if removing is not supported
   *
   */
  public void remove()
  {
    throw new UnsupportedOperationException("removes are not allowed");
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- simple ---------------------------------------------------------

    /** A simple test. */
    @org.junit.Test
    public void simple()
    {
      Iterator<String> i = new SingleIterator<String>("42");

      assertContent("simple", i, "42");
    }

    //......................................................................
  }

  //........................................................................
}
