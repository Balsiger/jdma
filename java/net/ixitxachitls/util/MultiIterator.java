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

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.NotThreadSafe;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class defines an iterator that can be used on multiple iterators.
 *
 * @param         <T> The type each iterator element
 *
 * @file          MultiIterator.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
@ParametersAreNonnullByDefault
public class MultiIterator<T> implements Iterator<T>
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- MultiIterator -----------------------------

  /**
   * Create the iterator with the multiple given iterators.
   *
   * @param       inIterators the iterators to iterator over
   *
   * @undefined   never
   *
   */
  @SuppressWarnings("unchecked") // see below
  public MultiIterator(Iterator<?> ... inIterators)
  {
    // the following is VERY UGLY, but as far as I understood, there is no way
    // to create generic arrays... *sigh*
    // so, this will create a unchecked cast warning, but unfortunately there
    // is no way for us to get rid of it... *big sigh*
    m_iterators = (Iterator<T> [])inIterators;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The values iterated over. */
  private Iterator<T> []m_iterators;

  /** The index of the current iterator used. */
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
  @Override
public boolean hasNext()
  {
    if(m_index >= m_iterators.length)
      return false;

    for(; m_index < m_iterators.length; m_index++)
      if(m_iterators[m_index] != null && m_iterators[m_index].hasNext())
        return true;

    return false;
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
  @Override
public T next()
  {
    if(!hasNext())
      throw new java.util.NoSuchElementException("no more values");

    return m_iterators[m_index].next();
  }

  //........................................................................
  //-------------------------------- remove --------------------------------

  /**
   * Remove the current element. If this is not supported by the underlying
   * iterator, an UnnsuportedOperationException is thrown.
   *
   */
  @Override
public void remove()
  {
    m_iterators[m_index].remove();
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The Test class. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- iterate --------------------------------------------------------

    /** Iteration test. */
    @org.junit.Test
    public void iterate()
    {
      java.util.List<String> list1 = com.google.common.collect.ImmutableList.of
        ("1", "2", "3");
      java.util.List<String> list2 = com.google.common.collect.ImmutableList.of
        ();
      java.util.List<String> list3 = com.google.common.collect.ImmutableList.of
        ("4", "5");

      assertContent("iterate", new MultiIterator<String>(list1.iterator(),
                                                         list2.iterator(), null,
                                                         list3.iterator()),
                    "1", "2", "3", "4", "5");
    }

    //......................................................................
    //----- remove ---------------------------------------------------------

    /** Testing removal of values. */
    @org.junit.Test
    public void remove()
    {
      java.util.ArrayList<String> list1 = new java.util.ArrayList<String>();
      list1.add("1");
      list1.add("2");
      list1.add("3");

      java.util.ArrayList<String> list2 = new java.util.ArrayList<String>();
      list2.add("4");
      list2.add("5");

      Iterator<String> i =
        new MultiIterator<String>(list1.iterator(), list2.iterator());

      assertTrue("first", i.hasNext());
      assertEquals("first", "1", i.next());
      assertTrue("first", i.hasNext());
      assertEquals("first", "2", i.next());
      assertTrue("first", i.hasNext());
      assertEquals("first", "3", i.next());
      i.remove();
      assertTrue("first", i.hasNext());
      assertEquals("first", "4", i.next());
      i.remove();
      assertTrue("first", i.hasNext());
      assertEquals("first", "5", i.next());
      assertFalse("first", i.hasNext());


      // start anew, but with remove elements
      i = new MultiIterator<String>(list1.iterator(), list2.iterator());

      assertTrue("first", i.hasNext());
      assertEquals("first", "1", i.next());
      assertTrue("first", i.hasNext());
      assertEquals("first", "2", i.next());
      assertTrue("first", i.hasNext());
      assertEquals("first", "5", i.next());
      assertFalse("first", i.hasNext());
    }

    //......................................................................
  }

  //........................................................................
}
