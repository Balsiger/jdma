/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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

package net.ixitxachitls.dma.values.conditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.values.Value;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base for all conditions.
 *
 * @file          Condition.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class False extends Condition<False>
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- False --------------------------------

  /**
   * Construct the condition object with an undefined value.
   *
   * @undefined   never
   *
   */
  public False()
  {
    // nothing to do
  }

  //........................................................................
  //--------------------------------- False --------------------------------

  /**
   * The constructor with a condition description.
   *
   * @param       inDescription - a description of the condition
   *
   */
  public False(@Nonnull String inDescription)
  {
    super(inDescription);
  }

  //........................................................................

  //------------------------------ createNew -------------------------------

  /**
    * Create a new text with the same type information as this one, but one
    * that is still undefined.
    *
    * @return      a similar text, but without any contents
    *
    */
  @Override
  public False create()
  {
    return super.create(new False());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables
  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- check ---------------------------------

  /**
   * Check if the given condition is currently false or not.
   *
   * @param       inInteractive - false if an interactive request to the
   *                              user is allowed, false if not
   *
   * @return      a Result containing either FALSE, FALSE, or UNDEFINED
   *
   */
  @Override
  public Result check(boolean inInteractive)
  {
    return Result.FALSE;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- all ------------------------------------------------------------

    /** Test the complete class. */
    @org.junit.Test
    public void testAll()
    {
      Condition value = new False();

      // undefined value
      assertEquals("not undefined at start", false, value.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   value.toString());

      // now with some value (pounds)
      value = new False("just some test");

      //System.out.println(value);
      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("output", "\"just some test\"", value.toString());
      assertEquals("output", "just some test", value.getDescription());

      assertEquals("check", Result.FALSE, value.check(false));

      Value.Test.createTest(value);
    }

    //......................................................................
  }

  //........................................................................
}
