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
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class defines an iterator that can be used on arrays.
 *
 * @param         <T> The elements of each iteration
 *
 * @file          ArrayIterator.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
public class ArrayIterator<T> implements Iterator<T>
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- ArrayIterator -----------------------------

  /**
   * Create the iterator with the given array.
   *
   * @param       inValues the values of the array
   *
   */
  public ArrayIterator(@Nonnull T ... inValues)
  {
    m_values = inValues;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The values iterated over. */
  private @Nonnull T []m_values;

  /** The index of the current value used. */
  private int m_index = 0;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- hasNext --------------------------------

  /**
   * Check if there is a next value available.
   *
   * @return      true if there is another value, false if not
   *
   */
  public boolean hasNext()
  {
    return m_index < m_values.length;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- next ---------------------------------

  /**
   * Get the next object, advancing one step ahead.
   *
   * @return      the next Object
   *
   */
  public @Nullable T next()
  {
    if(m_index >= m_values.length)
      throw new java.util.NoSuchElementException("no more values");

    return m_values[m_index++];
  }

  //........................................................................
  //-------------------------------- remove --------------------------------

  /**
   * Remove the current element.
   *
   * This method is not supported and always throws an
   * UnsupportedOperationException.
   *
   */
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
    //----- remove ---------------------------------------------------------

    /** Removing test. */
    @org.junit.Test(expected = UnsupportedOperationException.class)
    public void remove()
    {
      Iterator<Integer> i =
        new ArrayIterator<Integer>(1, 2, 3, 4);

      i.remove();
    }

    //......................................................................
    //----- iterate --------------------------------------------------------

    /** Test the iteration process. */
    @org.junit.Test
    public void iterate()
    {
      Iterator<String> i =
        new ArrayIterator<String>("first", "second", "third", "fourth");

      assertTrue("first", i.hasNext());
      assertEquals("first", "first", i.next());

      assertTrue("second", i.hasNext());
      assertEquals("second", "second", i.next());

      assertTrue("third", i.hasNext());
      assertEquals("third", "third", i.next());

      assertTrue("fourth", i.hasNext());
      assertEquals("fourth", "fourth", i.next());

      assertFalse("end", i.hasNext());
    }

    //......................................................................
    //----- overrun --------------------------------------------------------

    /** overrun Test. */
    @org.junit.Test(expected = java.util.NoSuchElementException.class)
    public void overrun()
    {
      Iterator<String> i = new ArrayIterator<String>(new String [0]);

      i.next();
    }

    //......................................................................

  }

  //........................................................................
}
