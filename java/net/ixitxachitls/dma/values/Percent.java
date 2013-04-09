/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.values;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.input.ParseReader;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a percent and is capable of reading such percents
 * from a reader (and write it to a writer of course).
 *
 * @file          Percent.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Percent extends BaseNumber<Percent>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Percent --------------------------------

  /**
   * Construct the percent object using real values.
   *
   * @param       inPercent the percent inside this value
   *
   */
  public Percent(long inPercent)
  {
    super(inPercent, 0, 100);
  }

  //........................................................................
  //------------------------------- Percent --------------------------------

  /**
   * Construct the percent object as undefined.
   *
   */
  public Percent()
  {
    super(0, 100);
  }

  //........................................................................

  {
    withEditType("percent");
  }

  //------------------------------- create ---------------------------------

  /**
   * Create a new list with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar list, but without any contents
   *
   */
  @Override
  public Percent create()
  {
    return super.create(new Percent());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables
  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ doToString ------------------------------

  /**
   * Convert the value to a string, depending on the given kind.
   *
   * @return      a String representation, depending on the kind given
   *
   */
  @Override
  protected String doToString()
  {
    return super.doToString() + "%";
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- doRead ---------------------------------

  /**
   * Read the value from the reader and replace the current one.
   *
   * @param       inReader   the reader to read from
   *
   * @return      true if read, false if not
   *
   */
  @Override
  public boolean doRead(ParseReader inReader)
  {
    ParseReader.Position pos = inReader.getPosition();

    super.doRead(inReader);

    if(!inReader.expect("%"))
    {
      inReader.seek(pos);

      return false;
    }

    return true;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** Test of init. */
    @org.junit.Test
    public void init()
    {
      Percent percent = new Percent();

      // undefined value
      assertFalse("not undefined at start", percent.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   percent.toString());
      assertEquals("undefined value not correct", 50, percent.get());

      // now with some percent
      percent = new Percent(10);

      assertEquals("not defined after setting", true, percent.isDefined());
      assertEquals("value not correctly gotten", 10, percent.get());
      assertEquals("value not correctly converted", "10%", percent.toString());

      assertEquals("max", 100, percent.getMax());
      assertEquals("min", 0, percent.getMin());

      Value.Test.createTest(percent);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      String []tests =
        {
          "simple", "42%", "42%", null,
          "whites", "\n   42   \n%  ", "42%", "  ",
          "zero", "+0%", "0%", null,
          "zero", "-0%", "0%", null,
          "invalid", "a%", null, "a%",
          "empty", "", null, null,
          "other", "42a", null, "42a",
          "number only", "23", null, "23",
        };

      Value.Test.readTest(tests, new Percent());


    }

    //......................................................................
  }

  //........................................................................
}
