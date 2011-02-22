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

package net.ixitxachitls.output.commands;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The frac command.
 *
 * @file          Frac.java
 *
 * @author        balsiger@ixitxachils.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Frac extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Frac ---------------------------------

  /**
   * The constructor for the frac command.
   *
   * @param       inNominator   the text above the bar
   * @param       inDenominator the text below the bar
   *
   */
  public Frac(@Nonnull Object inNominator, @Nonnull Object inDenominator)
  {
    this();

    withArguments(inNominator, inDenominator);
  }

  //........................................................................
  //--------------------------------- Frac ---------------------------------

  /**
   * The constructor for the frac command.
   *
   * @param       inPrepend     the text to prepend
   * @param       inNominator   the text above the bar
   * @param       inDenominator the text below the bar
   *
   */
  public Frac(@Nonnull Object inPrepend, @Nonnull Object inNominator,
              @Nonnull Object inDenominator)
  {
    this(inNominator, inDenominator);

    withOptionals(inPrepend);
  }

  //........................................................................
  //--------------------------------- Frac ---------------------------------

  /**
   * This is the internal constructor for a command.
   *
   */
  protected Frac()
  {
    super(FRAC, 1, 2);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for adding fractions as real fraction signs. */
  public static final String FRAC =
    Config.get("resource:commands/frac", "frac");

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- arguments ------------------------------------------------------

    /** Testing arguments. */
    @org.junit.Test
    public void arguments()
    {
      Command command = new Frac("1", "2");
      assertEquals("setup", "\\frac{1}{2}", command.toString());

      command = new Frac("1", "2", "3");
      assertEquals("setup", "\\frac[1]{2}{3}", command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
