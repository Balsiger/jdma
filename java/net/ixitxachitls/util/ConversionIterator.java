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

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is an iterator that converts down another iterator.
 *
 * @file          TypeIterator.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param         <O> type of the original iterator
 * @param         <N> type of the new iterator
 *
 */

//..........................................................................

//__________________________________________________________________________

public class ConversionIterator<N, O extends N> implements Iterator<N>
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------- ConversionIterator --------------------------

  /**
    * Create the iterator using the given iterator.
    *
    * @param       inValues the original iterator with the values
    *
    */
  public ConversionIterator(@Nonnull Iterator<O> inValues)
  {
    m_iterator = inValues;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The unterlying iterator. */
  private @Nonnull Iterator<O> m_iterator;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- hasNext --------------------------------

  /**
   * Check if there is a next value available.
   *
   * @return      true if there is another value, false if not
   *
   * @undefined   never
   *
   */
  public boolean hasNext()
  {
    return m_iterator.hasNext();
  }

  //........................................................................
  //--------------------------------- next ---------------------------------

  /**
   * Get the next object, advancing one step ahead.
   *
   * @return      the next Object
   *
   * @undefined   exception if no more elements
   *
   */
  public N next()
  {
    return m_iterator.next();
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
   * @undefined   always exception
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
    //----- iterate --------------------------------------------------------

    /** iterate Test. */
    @org.junit.Test
    public void iterate()
    {
      Iterator<Number> i =
        new ConversionIterator<Number, Integer>
        (new ArrayIterator<Integer>(new Integer [] { 1, 2, 3 }));

      assertTrue("first", i.hasNext());
      assertEquals("first", 1, i.next());
      assertTrue("second", i.hasNext());
      assertEquals("second", 2, i.next());
      assertTrue("third", i.hasNext());
      assertEquals("third", 3, i.next());

      assertFalse("end", i.hasNext());
    }

    //......................................................................
    //----- remove ---------------------------------------------------------

    /** remove Test. */
    @org.junit.Test(expected = UnsupportedOperationException.class)
    public void remove()
    {
      Iterator<Number> i =
        new ConversionIterator<Number, Integer>
        (new ArrayIterator<Integer>(new Integer [] { 1, 2, 3 }));

      i.remove();
    }

    //......................................................................
    //----- overrun --------------------------------------------------------

    /** overrun Test. */
    @org.junit.Test(expected = java.util.NoSuchElementException.class)
    public void overrun()
    {
      Iterator<Number> i =
        new ConversionIterator<Number, Integer>
        (new ArrayIterator<Integer>(new Integer [] {}));

      i.next();
    }

    //......................................................................
  }

  //......................................................................
}
