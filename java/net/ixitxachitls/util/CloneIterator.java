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
import javax.annotation.concurrent.NotThreadSafe;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class defines an iterator over another iterator of PublicCloneable
 * objects and clones each object before returning it.
 *
 * @param         <T> The elements of each iteration
 *
 * @file          CloneIterator.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
public class CloneIterator<T extends PublicCloneable> implements Iterator<T>
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- CloneIterator -----------------------------

  /**
   * Create the clone iterator with the given iterator.
   *
   * @param       inValues the values of the iterator to 'clone'
   *
   */
  public CloneIterator(@Nonnull Iterator<T> inValues)
  {
    m_values = inValues;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The values iterated over. */
  private @Nonnull Iterator<T> m_values;

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
    return m_values.hasNext();
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
  @SuppressWarnings("unchecked")
  public T next()
  {
    // the following produces an unchecked cast. It would be possible to
    // prevent that by making PublicCloneable a generic type. But then we have
    // some problems with the Value class. This one must implement the
    // PublicCloneable interface as well, but its derivations cannot
    // subsequently implement their own version of it as well (unless Value
    // itself is a generic type, which in turn produces many other
    // problems). Thus, we just have to live with it here, sorry.
    return (T)m_values.next().clone();
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
                                            + "support removal of entries");
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //---------------------------------------------------------------- value

    /** A public cloneable class for testing. */
    private class Value implements PublicCloneable
    {
      /**
       * Create the value.
       *
       * @param inName the name of the value
       */
      public Value(@Nonnull String inName)
      {
        m_name = inName;
      }

      /** The name of the value. */
      private String m_name;

      /**
       * Clone the current object.
       *
       * @return the cloned object
       *
       */
      public Object clone()
      {
        return new Value(m_name + "(cloned)");
      }

      /**
       * Convert to string.
       *
       * @return the converted string
       */
      public String toString()
      {
        return m_name;
      }
    }

    //......................................................................
    //----- iterator -------------------------------------------------------

    /** Test the iteration. */
    @org.junit.Test()
    public void iterate()
    {
      Iterator<Value> i = new CloneIterator<Value>
        (new ArrayIterator<Value>(new Value []
          { new Value("first"),
            new Value("second"),
            new Value("third"),
            new Value("fourth"),
          }));

      assertTrue("first", i.hasNext());
      assertEquals("first", "first(cloned)", i.next().toString());

      assertTrue("second", i.hasNext());
      assertEquals("second", "second(cloned)", i.next().toString());

      assertTrue("third", i.hasNext());
      assertEquals("third", "third(cloned)", i.next().toString());

      assertTrue("fourth", i.hasNext());
      assertEquals("fourth", "fourth(cloned)", i.next().toString());

      assertFalse("end", i.hasNext());
    }

    //......................................................................
    //----- overrun --------------------------------------------------------

    /** overrun Test. */
    @org.junit.Test(expected = java.util.NoSuchElementException.class)
    public void overrun()
    {
      Iterator<Value> i =
        new CloneIterator<Value>(new ArrayIterator<Value>(new Value [0]));

      i.next();
    }

    //......................................................................
    //----- remove ---------------------------------------------------------

    /** remove Test. */
    @org.junit.Test(expected = UnsupportedOperationException.class)
    public void remove()
    {
      Iterator<Value> i =
        new CloneIterator<Value>(new ArrayIterator<Value>(new Value []
          { new Value("first") }));

      i.remove();
    }

    //......................................................................
  }

  //........................................................................
}
