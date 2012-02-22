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

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is an iterator with a filter, returning only those value that match the
 * filter.
 *
 * @param         <T> The type each iterator element
 *
 * @file          FilteredIterator.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class FilteredIterator<T> implements Iterator<T>
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------- FilteredIterator ---------------------------

  /**
   * Create the iterator using the given filter.
   *
   * @param       inValues the original iterator with the values
   * @param       inFilter the filter to use
   *
   */
  public FilteredIterator(@Nonnull Iterator<T> inValues,
                          @Nonnull Filter<T> inFilter)
  {
    m_filter   = inFilter;
    m_iterator = inValues;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The filter. */
  private @Nonnull Filter<T> m_filter;

  /** The unterlying iterator. */
  private @Nonnull Iterator<T> m_iterator;

  /** The current element. */
  private @Nullable T m_next = null;

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
    @Override
    public boolean hasNext()
    {
      if(m_next != null)
        return true;

      while(m_iterator.hasNext())
      {
        T next = m_iterator.next();

        if(m_filter.accept(next))
        {
          m_next = next;

          return true;
        }
      }

      return false;
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
  @Override
  public @Nullable T next()
  {
    if(!hasNext())
      throw new java.util.NoSuchElementException("already at end!");

    T result = m_next;
    m_next = null;

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
    //----- iterator -------------------------------------------------------

    /** Test the iteration process. */
    @org.junit.Test
    public void iterate()
    {
      java.util.List<String> list = com.google.common.collect.ImmutableList.of
        ("+first", "-second", "+third", "+fourth", "-fifth", "-sixth",
         "+seventh", "-eigth");

      Iterator<String> i =
        new FilteredIterator<String>(list.iterator(), new Filter<String>()
          {
            @Override
            public boolean accept(@Nullable String inEntry)
            {
              if(inEntry == null)
                return false;

              return inEntry.startsWith("+");
            }
          });

      assertContent("iterate", i, "+first", "+third", "+fourth", "+seventh");
    }

    //......................................................................
    //----- none -----------------------------------------------------------

    /** none Test. */
    @org.junit.Test
    public void none()
    {
      java.util.List<Integer> list = com.google.common.collect.ImmutableList.of
        (1, 2, 3, 4, 5, 6);

      assertContent("iterate",
                    new FilteredIterator<Integer>(list.iterator(),
                                                  new Filter.None<Integer>()));
    }

    //......................................................................
    //----- all ------------------------------------------------------------

    /** all Test. */
    @org.junit.Test
    public void all()
    {
      java.util.List<Integer> list = com.google.common.collect.ImmutableList.of
        (1, 2, 3, 4, 5, 6);

      assertContent("iterate",
                    new FilteredIterator<Integer>(list.iterator(),
                                                  new Filter.All<Integer>()),
                    1, 2, 3, 4, 5, 6);
    }

    //......................................................................
    //----- non null -------------------------------------------------------

    /** non null Test. */
    @org.junit.Test
    public void nonNull()
    {
      java.util.List<Integer> list = new java.util.ArrayList<Integer>();
      list.add(1);
      list.add(2);
      list.add(null);
      list.add(4);
      list.add(null);
      list.add(null);

      assertContent("iterate",
                    new FilteredIterator<Integer>
                    (list.iterator(), new Filter.NonNull<Integer>()),
                    1, 2, 4);
    }

    //......................................................................
  }

  //........................................................................
}
