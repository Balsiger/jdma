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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.Command;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a critical and is capable of reading such criticals
 * from a reader (and write it to a writer of course).
 *
 * @file          Critical.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Critical extends BaseNumber<Critical>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Critical -------------------------------

  /**
   * Construct the critical object using real values.
   *
   * @param       inLow        the low threat value
   * @param       inHigh       the high threat value
   * @param       inMultiplier the critical multiplier
   *
   */
  public Critical(long inLow, long inHigh, long inMultiplier)
  {
    super(inMultiplier, 1, 5);

    m_threat = new Range(inLow, inHigh, 10, 20);
  }

  //........................................................................
  //------------------------------- Critical -------------------------------

  /**
   * Construct the critical object using real values.
   *
   * @param       inMultiplier the critical multiplier only
   *
   */
  public Critical(long inMultiplier)
  {
    super(inMultiplier, 1, 5);

    m_threat = new Range(10, 20);
  }

  //........................................................................
  //------------------------------- Critical -------------------------------

  /**
   * Construct the critical object using real values.
   *
   */
  public Critical()
  {
    super(1, 5);

    m_threat = new Range(10, 20);
  }

  //........................................................................

  //-------------------------------- create --------------------------------

  /**
   * Create a new list with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar list, but without any contents
   *
   */
  public Critical create()
  {
    return super.create(new Critical());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The threat range value. */
  protected @Nonnull Range m_threat;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //---------------------------- getThreatRange ----------------------------

  /**
   * Get the threat range of the critical.
   *
   * @return      the threat range
   *
   */
  public @Nonnull Range getThreatRange()
  {
    return m_threat;
  }

  //........................................................................
  //---------------------------- getMultiplier -----------------------------

  /**
   * Get the multiplication value.
   *
   * @return      the multiplier for the critical damage
   *
   */
  public long getMultiplier()
  {
    return get();
  }

  //........................................................................

  //------------------------------ doToString ------------------------------

  /**
   * Convert the value to a string, depending on the given kind.
   *
   * @return      a String representation, depending on the kind given
   *
   */
  @Override
  protected @Nonnull String doToString()
  {
    if(m_number == 1)
      return "None";

    StringBuilder result = new StringBuilder();

    if(m_threat.isDefined())
    {
      result.append(m_threat.toString());
      result.append("/");
    }

    result.append("x" + super.doToString());

    return result.toString();
  }

  //........................................................................
  //------------------------------- doFormat -------------------------------

  /**
   * Really to the formatting.
   *
   * @return      the command for setting the value
   *
   */
  protected @Nonnull Command doFormat()
  {
    if(m_number == 1)
      return new Command("None");

    java.util.List<Object> commands = new ArrayList<Object>();

    if(m_threat.isDefined())
    {
      commands.add(m_threat.toString());
      commands.add("/");
    }

    commands.add("x" + m_number);

    return new Command(commands.toArray());
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- multiply -------------------------------

  /**
   * Multiply the number.
   *
   * @param       inValue the multiplication factor
   *
   * @return      true if multiplied, false if not
   *
   */
  // public boolean multiply(long inValue)
  // {
  //   if(!m_defined)
  //     return false;

  //   m_stored  = true;

  //   long start = m_threat.getStart();
  //   long end   = m_threat.getEnd();

  //   m_threat.set(end - ((end - start) * inValue), end);

  //   check();

  //   return true;
  // }

  //........................................................................
  //------------------------------- multiply -------------------------------

  /**
   * Multiply the value with the given value.
   *
   * @param       inValue the multiplication factor
   *
   * @return      true if multiplied, false if not
   *
   * @undefined   never
   *
   */
  // public boolean multiply(@MayBeNull Rational inValue)
  // {
  //   if(inValue == null)
  //     return false;

  //   if(!m_defined)
  //     return false;

  //   m_stored  = true;
  //   m_stored  = true;

  //   long start = m_threat.getStart();
  //   long end   = m_threat.getEnd();

  //   m_threat.set((long)(end - ((end - start) * inValue.getValue())), end);

  //   return true;
  // }

  //........................................................................
  //-------------------------------- divide --------------------------------

  /**
   * Divide the dice. This decreases the dice type to the corresponding
   * dice, as shown in the Player's Handbook p. 116 and 114.
   *
   * @param       inValue the division factor
   *
   * @return      true if divided, false if not
   *
   * @undefined   never
   *
   */
  // public boolean divide(long inValue)
  // {
  //   if(!m_defined)
  //     return false;

  //   m_stored = true;
  //   long start = m_threat.getStart();
  //   long end   = m_threat.getEnd();

  //   m_threat.set(end - ((end - start) / inValue), end);

  //   check();

  //   return true;
  // }

  //........................................................................

  //------------------------------- doRead ---------------------------------

  /**
   * Read the value from the reader and replace the current one.
   *
   * @param       inReader the reader to read from
   *
   * @return      true if read, false if not
   *
   */
  protected boolean doRead(@Nonnull ParseReader inReader)
  {
    if(inReader.expect("none"))
    {
      m_number = 1;
      m_threat = m_threat.as(20, 20);

      return true;
    }

    if(m_threat.doRead(inReader))
      if(!inReader.expect("/"))
        return false;

    if(!inReader.expect('x'))
      return false;

    return super.doRead(inReader);
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

    /** Testing init. */
    @org.junit.Test
    public void init()
    {
      Critical critical = new Critical();

      // undefined value
      assertEquals("not undefined at start", false, critical.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   critical.toString());
      assertEquals("undefined value not correct", "\\color{error}{$undefined$}",
                   critical.format(false).toString());

      // now with some critical
      critical = new Critical(19, 20, 3);

      assertEquals("not defined after setting", true, critical.isDefined());
      assertEquals("value not correctly converted", "19-20/x3",
                   critical.toString());
      assertEquals("value not correctly converted", "19-20/x3",
                   critical.format(false).toString());

      // less values
      critical = new Critical(20, 20, 4);

      assertEquals("not defined after setting", true, critical.isDefined());
      assertEquals("value not correctly converted", "20/x4",
                   critical.toString());
      assertEquals("value not correctly converted", "20/x4",
                   critical.format(false).toString());

      assertEquals("multiplier", 4, critical.getMultiplier());
      assertEquals("threat range", "20", critical.getThreatRange().toString());

      // even less values
      critical = new Critical(2);

      assertEquals("not defined after setting", true, critical.isDefined());
      assertEquals("value not correctly converted", "x2",
                   critical.toString());
      assertEquals("value not correctly converted", "x2",
                   critical.format(false).toString());

      Value.Test.createTest(critical);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing read. */
    @org.junit.Test
    public void read()
    {
      String []tests =
        {
          "simple", "19-20/x2", "19-20/x2",  null,
          "whites", "\n   19   -\n 20 /   x\n 2 ", "19-20/x2", " ",
          "single", "20/x3", "20/x3",  null,
          "multiplier", "x5", "x5",  null,
          "invalid", "a", null, "a",
          "empty", "", null, null,
          "delimiter 1", "19-20x2", null, "19-20x2",
          "delimiter 2", "19-20/2", null, "19-20/2",
          "too high", "19-21/x3", null, "19-21/x3",
          "too high", "18-17/x3", null, "18-17/x3",
          "too high", "x6", null, "x6",
          "too high", "19-20/x6", null, "19-20/x6",
          "too high", "22/x5", null, "22/x5",
          "too low", "5-7/x3", null, "5-7/x3",
          "too low", "5-17/x3", null, "5-17/x3",
          "none", "15-17/x1", "None", null,
          "too low", "-5-17/x2", null, "-5-17/x2",
          "none 2", "x1", "None", null,
          "too low", "x-1", null, "x-1",
        };

      m_logger.addExpectedPattern("WARNING:.*\\(maximal 20\\) "
                                  + "on line 1 in document 'test'."
                                  + "\\.\\.\\.19->>>21/x3\\.\\.\\.");
      m_logger.addExpectedPattern("WARNING:.*\\(minimal 18\\) "
                                  + "on line 1 in document 'test'."
                                  + "\\.\\.\\.18->>>17/x3\\.\\.\\.");
      m_logger.addExpectedPattern("WARNING:.*\\(maximal 5\\) "
                                  + "on line 1 in document 'test'."
                                  + "\\.\\.\\.x>>>6\\.\\.\\.");
      m_logger.addExpectedPattern("WARNING:.*\\(maximal 5\\) "
                                  + "on line 1 in document 'test'."
                                  + "\\.\\.\\.19-20/x>>>6\\.\\.\\.");
      m_logger.addExpectedPattern("WARNING:.*\\(maximal 20\\) "
                                  + "on line 1 in document 'test'."
                                  + "\\.\\.\\.>>>22/x5\\.\\.\\.");
      m_logger.addExpectedPattern("WARNING:.*\\(minimal 10\\) "
                                  + "on line 1 in document 'test'."
                                  + "\\.\\.\\.>>>5-7/x3\\.\\.\\.");
      m_logger.addExpectedPattern("WARNING:.*\\(minimal 10\\) "
                                  + "on line 1 in document 'test'."
                                  + "\\.\\.\\.>>>5-17/x3\\.\\.\\.");
      m_logger.addExpectedPattern("WARNING:.*\\(minimal 10\\) "
                                  + "on line 1 in document 'test'."
                                  + "\\.\\.\\.>>>-5-17/x2\\.\\.\\.");
      m_logger.addExpectedPattern("WARNING:.*\\(minimal 1\\) "
                                  + "on line 1 in document 'test'."
                                  + "\\.\\.\\.x>>>-1\\.\\.\\.");

      Value.Test.readTest(tests, new Critical());
    }

    //......................................................................
  }

  //........................................................................
}
