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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class simply represents a pair of arbitrary values.
 *
 * @param         <T1> The type of the first element
 * @param         <T2> The type of the second element
 *
 * @file          Pair.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class Pair<T1, T2>
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Pair ---------------------------------

  /**
   * Create the pair with the given values.
   *
   * @param       inFirst  the first value of the pair
   * @param       inSecond the second value of the pair
   *
   * @undefined   never (null is accepted and returned as well)
   *
   */
  public Pair(@Nullable T1 inFirst, @Nullable T2 inSecond)
  {
    m_first  = inFirst;
    m_second = inSecond;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The first value stored in the pair. */
  @Nullable private T1 m_first;

  /** The second value stored in the pair. */
  @Nullable private T2 m_second;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- first ---------------------------------

  /**
   * Get the first value stored in the pair.
   *
   * @return      the first value stored (may return null if this was stored)
   *
   */
  public @Nullable T1 first()
  {
    return m_first;
  }

  //........................................................................
  //-------------------------------- second --------------------------------

  /**
   * Get the second value stored in the pair.
   *
   * @return      the second value stored (may return null if this was stored)
   *
   */
  public @Nullable T2 second()
  {
    return m_second;
  }

  //........................................................................

  //------------------------------- toString -------------------------------

  /**
   * Convert to human readable string.
   *
   * @return      the value converted into a string
   *
   */
  @Override
  public String toString()
  {
    return "<" + m_first + ", " + m_second + ">";
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  // none, this is immutable !

  //........................................................................

  //------------------------------------------------- other member functions

  // remember, this is meant to be immutable

  //........................................................................

  //------------------------------------------------------------------- test

  /** This is the test class for Pair. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- simple ---------------------------------------------------------

    /** Some simple tests. */
    @org.junit.Test
    public void simple()
    {
      final String first  = "Hello there";
      final int    second = 42;

      Pair<String, Integer> pair =
        new Pair<String, Integer>(first, Integer.valueOf(second));

      assertNotNull("instantiated", pair);
      assertEquals("first", first, pair.first());
      assertEquals("second", second, pair.second().intValue());
      assertEquals("string", "<Hello there, 42>", pair.toString());
    }

    //......................................................................
    //----- limits ---------------------------------------------------------

    /** Testing the limits. */
    @org.junit.Test
    public void limits()
    {
      Pair<String, Integer> pair =
        new Pair<String, Integer>(null, null);

      assertNotNull("instantiated", pair);
      assertNull("first", pair.first());
      assertNull("second", pair.second());

      pair = new Pair<String, Integer>("", Integer.valueOf(0));

      assertNotNull("instantiated", pair);
      assertEquals("first", "", pair.first());
      assertEquals("second", 0, pair.second().intValue());
    }

    //......................................................................
    //----- boxing ---------------------------------------------------------

    /** Testing boxing and unboxing. */
    @org.junit.Test
    public void boxing()
    {
      final double first     = 123.456;
      final int    second    = 42;
      final double precision = 0.001;

      Pair<Double, Integer> pair = new Pair<Double, Integer>(first, second);

      assertNotNull("instantiated", pair);
      assertEquals("first", first, pair.first(), precision);
      assertEquals("second", second, (int)pair.second());
    }

    //......................................................................
  }

  //........................................................................
}
