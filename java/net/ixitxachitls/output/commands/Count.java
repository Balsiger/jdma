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
 * The bold command.
 *
 * @file          Count.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Count extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Count ---------------------------------

  /**
   * The constructor for the count command.
   *
   * @param       inCount the counter itself
   * @param       inMax   the maximal counter value
   * @param       inUnit  the unit per count
   *
   */
  public Count(long inCount, long inMax, @Nonnull Object inUnit)
  {
    this();

    if(inCount < 0 || inMax < 0)
      throw new IllegalArgumentException("must have a positive count or "
                                         + "maximum value");

    if(inCount > inMax)
      throw new IllegalArgumentException("maximum (" + inMax
                                         + ") must be higher or equal to "
                                         + "given count (" + inCount + ")");

    withArguments(inCount, inMax, inUnit);
  }

  //........................................................................
  //-------------------------------- Count ---------------------------------

  /**
   * The constructor for the count command.
   *
   * @param       inCount the counter itself
   * @param       inMax   the maximal counter value
   * @param       inUnit  the unit per count
   * @param       inStep  the step from counter to counter
   *
   */
  public Count(long inCount, long inMax, @Nonnull Object inUnit, long inStep)
  {
    this(inCount, inMax, inUnit);

    if(inStep <= 0)
      throw new IllegalArgumentException("must have a positive step width");

    withOptionals(inStep);
  }

  //........................................................................
  //-------------------------------- Count ---------------------------------

  /**
   * The constructor for the bold command.
   *
   * @param       inCount the counter itself
   * @param       inMax   the maximal counter value
   * @param       inUnit  the unit per count
   *
   */
  public Count(@Nonnull Command inCount, @Nonnull Command inMax,
               @Nonnull Command inUnit)
  {
    this();

    withArguments(inCount, inMax, inUnit);
  }

  //........................................................................
  //-------------------------------- Count ---------------------------------

  /**
   * The constructor for the bold command.
   *
   * @param       inCount the counter itself
   * @param       inMax   the maximal counter value
   * @param       inUnit  the unit per count
   * @param       inStep  the step with from count to count
   *
   */
  public Count(@Nonnull Command inCount, @Nonnull Command inMax,
               @Nonnull Command inUnit, @Nonnull Command inStep)
  {
    this(inCount, inMax, inUnit);

    withOptionals(inStep);
  }

  //........................................................................
  //-------------------------------- Count ---------------------------------

  /**
    *
    * This is the internal constructor for a command.
    *
    */
  protected Count()
  {
    super(NAME, 1, 3);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for counting. */
  public static final @Nonnull String NAME =
    Config.get("resource:commands/count", "count");

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
      Command command = new Command("\\count{5}{10}{unit}");
      assertEquals("count", "\\count{5}{10}{unit}", command.toString());

      command = new Count(5, 10, "unit");
      assertEquals("command", "\\count{5}{10}{unit}", command.toString());

      command = new Count(5, 10, "unit", 2);
      assertEquals("command", "\\count[2]{5}{10}{unit}", command.toString());

      command = new Count(new Command("5"), new Command("10"),
                          new Command("unit"));
      assertEquals("command", "\\count{5}{10}{unit}", command.toString());

      command = new Count(new Command("5"), new Command("10"),
                          new Command("unit"), new Command("2"));
      assertEquals("command", "\\count[2]{5}{10}{unit}", command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
