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

//..........................................................................

//------------------------------------------------------------------- header

/**
 * An empty iterator.
 *
 * @file          EmptyIterator.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param         <T> The type of values iterated over.
 *
 */

//..........................................................................

//__________________________________________________________________________

public class EmptyIterator<T> implements Iterator<T>
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- EmptyIterator -----------------------------

  /**
   * Default and only constructor.
   *
   */
  public EmptyIterator()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables
  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- hasNext --------------------------------

  /**
   * Determine if there is a next element in the list.
   *
   * @return      always false
   *
   */
  @Override
public boolean hasNext()
  {
    return false;
  }

  //........................................................................
  //--------------------------------- next ---------------------------------

  /**
   * Determine the next element in the iterator.
   *
   * @return      always throws exception
   *
   */
  @Override
public T next()
  {
    throw new java.util.NoSuchElementException("no more elements");
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- remove --------------------------------

  /**
   * Remove the current element from the iterator. This operation is not
   * supported by this iterator.
   *
   * @undefined   UnsupportedOperationException if removing is not supported
   *              (always thrown)
   *
   */
  @Override
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
    //----- iteration ------------------------------------------------------

    /** Test iterating. */
    @org.junit.Test
    public void iteration()
    {
      Iterator<String> i = new EmptyIterator<String>();

      assertFalse("has next", i.hasNext());
    }

    //......................................................................
    //----- removal --------------------------------------------------------

    /** Test removing values. */
    @org.junit.Test(expected = UnsupportedOperationException.class)
    public void testRemoval()
    {
      Iterator<String> i = new EmptyIterator<String>();
      i.remove();
    }

    //......................................................................
    //----- overrun --------------------------------------------------------

    /** overrun Test. */
    @org.junit.Test(expected = java.util.NoSuchElementException.class)
    public void overrun()
    {
      Iterator<String> i = new EmptyIterator<String>();

      i.next();
    }

    //......................................................................

  }

  //........................................................................
}
