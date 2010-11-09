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

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class simply represents a triple of arbitrary values.
 *
 * @param         <T1> The type of the first element
 * @param         <T2> The type of the second element
 * @param         <T3> The type of the third element
 *
 * @file          Triple.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @example       Triple&lt;Integer, String&gt; triple =
 *                  new Triple&lt;Integer, String&gt;(42, "Hello there");
 *
 *                int    first  = triple.first();
 *                String second = triple.second();
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Triple<T1, T2, T3>
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Triple --------------------------------

  /**
   * Create the triple with the given values.
   *
   * @param       inFirst  the first value of the triple
   * @param       inSecond the second value of the triple
   * @param       inThird  the third value of the triple
   *
   */
  public Triple(@Nullable T1 inFirst, @Nullable T2 inSecond,
                @Nullable T3 inThird)
  {
    m_first  = inFirst;
    m_second = inSecond;
    m_third  = inThird;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The first value stored in the triple. */
  private @Nullable T1 m_first;

  /** The second value stored in the triple. */
  private @Nullable T2 m_second;

  /** The third value stored in the triple. */
  private @Nullable T3 m_third;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- first ---------------------------------

  /**
   * Get the first value stored in the triple.
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
   * Get the second value stored in the triple.
   *
   * @return      the second value stored (may return null if this was stored)
   *
   */
  public @Nullable T2 second()
  {
    return m_second;
  }

  //........................................................................
  //-------------------------------- third ---------------------------------

  /**
   * Get the third value stored in the triple.
   *
   * @return      the third value stored (may return null if this was stored)
   *
   */
  public @Nullable T3 third()
  {
    return m_third;
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

  /** This is the test class for Triple. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- simpel ---------------------------------------------------------

    /** Some simple tests. */
    @org.junit.Test
    public void simple()
    {
      final String first  = "Hello there";
      final int    second = 42;
      final String third  = "So long";

      Triple<String, Integer, String> triple =
        new Triple<String, Integer, String>(first, Integer.valueOf(second),
                                            third);

      assertNotNull("instantiated", triple);
      assertEquals("first",  first, triple.first());
      assertEquals("second", second, triple.second().intValue());
      assertEquals("first",  third, triple.third());
    }

    //......................................................................
    //----- limits ---------------------------------------------------------

    /** Testing the limits. */
    @org.junit.Test
    public void limits()
    {
      Triple<String, Integer, String> triple =
        new Triple<String, Integer, String>(null, null, null);

      assertNotNull("instantiated", triple);
      assertNull("first",  triple.first());
      assertNull("second", triple.second());
      assertNull("third", triple.third());

      triple = new Triple<String, Integer, String>("", Integer.valueOf(0), "");

      assertNotNull("instantiated", triple);
      assertEquals("first", "", triple.first());
      assertEquals("second", 0, triple.second().intValue());
      assertEquals("third", "", triple.third());
    }

    //......................................................................
  }

  //........................................................................
}
