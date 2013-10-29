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

package net.ixitxachitls.dma.values;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.proto.Values.RangeProto;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a range and is capable of reading such ranges
 * from a reader (and write it to a writer of course).
 *
 * @file          Range.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Range extends BaseNumber<Range>
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Range --------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * Construct the range object with real values.
   *
   * @param       inStart the starting value
   * @param       inEnd   the end value
   * @param       inMin   the minimal allowed value
   * @param       inMax   the maximal allowed value
   *
   */
  public Range(long inStart, long inEnd, long inMin, long inMax)
  {
    super(inMin, inMax);

    if(inMin > inMax)
      throw new IllegalArgumentException("minimum must be less or equal than "
                                         + "the maximum");

    m_number = inStart;
    m_end    = inEnd;

    m_defined = true;

    check();
  }

  //........................................................................
  //-------------------------------- Range --------------------------------

  /**
   * Construct the range object with real values.
   *
   * @param       inMin   the minimal allowed value
   * @param       inMax   the maximal allowed value
   *
   */
  public Range(long inMin, long inMax)
  {
    super(inMin, inMax);

    m_end = m_number;
  }

  //........................................................................

  //-------------------------------- create -------------------------------

  /**
   * Create a new list with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar list, but without any contents
   *
   */
  @Override
  public Range create()
  {
    return new Range(m_min, m_max);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The end value (start is in the number value). */
  protected long m_end = 0;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getStart --------------------------------

  /**
   * Get the start value stored.
   *
   * @return      the start value stored
   *
   */
  public long getStart()
  {
    return get();
  }

  //........................................................................
  //------------------------------- getEnd ---------------------------------

  /**
   * Get the end value stored.
   *
   * @return      the end value stored
   *
   */
  public long getEnd()
  {
    return m_end;
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
  protected String doToString()
  {
    if(m_number != m_end)
      return super.doToString() + "-" + m_end;

    return super.doToString();
  }

  //........................................................................

  //------------------------------ isDefined -------------------------------

  /**
   * Check if the value is defined or not.
   *
   * @return      true if the value is defined, false if not
   *
   */
  @Override
  public boolean isDefined()
  {
    return m_defined;
  }

  //........................................................................

  /**
   * Create a proto for the value.
   *
   * @return the proto representation
   */
  public RangeProto toProto()
  {
    return RangeProto.newBuilder()
      .setLow(m_number)
      .setHigh(m_end)
      .build();
  }

  /**
   * Create a new range similar to the current one with the values from the
   * proto.
   *
   * @param inProto the proto with the values
   * @return the newly created range
   */
  public Range fromProto(RangeProto inProto)
  {
    Range result = create();

    if(inProto.hasLow())
    {
      result.m_number = inProto.getLow();
      result.m_defined = true;
    }
    if(inProto.hasHigh())
    {
      result.m_end = inProto.getHigh();
      result.m_defined = true;
    }

    return result;
  }

  //........................................................................

  //----------------------------------------------------------- manipulators

  //---------------------------------- as ----------------------------------

  /**
   * Set the values of the range.
   *
   * @param       inStart the start value
   * @param       inEnd   the end value
   *
   * @return      true if stored, false if not
   *
   */
  public Range as(long inStart, long inEnd)
  {
    Range result = create();

    result.m_number = inStart;
    result.m_end = inEnd;
    result.m_defined = true;

    result.check();

    return result;
  }

  //........................................................................

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
  protected boolean doRead(ParseReader inReader)
  {
    if(!super.doRead(inReader))
      return false;

    m_end = m_number;

    ParseReader.Position pos = inReader.getPosition();

    if(!inReader.expect('-'))
      return true;

    try
    {
      m_end = inReader.readLong();
    }
    catch(net.ixitxachitls.input.ReadException e)
    {
      inReader.seek(pos);

      return true;
    }

    if(m_end > m_max)
    {
      inReader.logWarning(pos, "value.number.high", "maximal " + m_max);

      return false;
    }

    if(m_end <= m_number)
    {
      inReader.logWarning(pos, "value.number.low", "minimal " + m_number);

      return false;
    }

    return true;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //-------------------------------- check ---------------------------------

  /**
   * Check that the current value is valid.
   *
   */
  @Override
  protected void check()
  {
    super.check();

    // we did not yet fully initialize the class, thus checking is useless
    if(m_end == 0 && !m_defined)
      return;

    if(m_end > m_max)
    {
      Log.warning("range limit " + m_end + " too high, adjusted to "
                  + m_max);

      m_end = m_max;
    }
    else
      if(m_end < m_number)
      {
        Log.warning("range end " + m_end + " lower than start, adjusted to "
                    + m_number);

        m_end = m_number;
      }
  }

  //........................................................................

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
      Range range = new Range(10, 20);

      // undefined value
      assertEquals("not undefined at start", false, range.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   range.toString());
      assertEquals("undefined value not correct", 15, range.getStart());
      assertEquals("undefined value not correct", 15, range.getEnd());

      // now with some range
      range = new Range(11, 20, 0, 20);

      assertEquals("not defined after setting", true, range.isDefined());
      assertEquals("value not correctly gotten", 11, range.getStart());
      assertEquals("value not correctly gotten", 20, range.getEnd());
      assertEquals("value not correctly converted", "11-20", range.toString());

      // now with some range
      range = new Range(10, 10, 0, 20);

      assertEquals("not defined after setting", true, range.isDefined());
      assertEquals("value not correctly gotten", 10, range.getStart());
      assertEquals("value not correctly gotten", 10, range.getEnd());
      assertEquals("value not correctly converted", "10", range.toString());

      range = new Range(11, 20, 0, 20);

      Value.Test.createTest(range);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      String []tests =
        {
          "simple", "10", "10", null,
          "simple", "10-20", "10-20", null,
          "too low", "5", null, "5",
          "too high", "150", null, "150",
          "whites", "10\n-    40", "10-40", null,
          "other", "10-a", "10", "-a",
          "open range", "10-", "10", "-",
          "too low again", "5-50", null, "5-50",
          "high to low", "42-40", null, "42-40",
          "too high", "55-132", null, "55-132",
          "empty", "", null, null,
          "invalid", "a-b", null, "a-b",
        };

      m_logger.addExpectedPattern("WARNING:.*\\(minimal 10\\) "
                                  + "on line 1 in document 'test'."
                                  + "\\.\\.\\.>>>5\\.\\.\\.");
      m_logger.addExpectedPattern("WARNING:.*\\(maximal 100\\) "
                                  + "on line 1 in document 'test'."
                                  + "\\.\\.\\.>>>150\\.\\.\\.");
      m_logger.addExpectedPattern("WARNING:.*\\(minimal 10\\) "
                                  + "on line 1 in document 'test'."
                                  + "\\.\\.\\.>>>5-50\\.\\.\\.");
      m_logger.addExpectedPattern("WARNING:.*\\(minimal 42\\) "
                                  + "on line 1 in document 'test'."
                                  + "\\.\\.\\.42->>>40\\.\\.\\.");
      m_logger.addExpectedPattern("WARNING:.*\\(maximal 100\\) "
                                  + "on line 1 in document 'test'."
                                  + "\\.\\.\\.55->>>132\\.\\.\\.");

      Value.Test.readTest(tests, new Range(10, 100));
    }

    //......................................................................
    //----- as -------------------------------------------------------------

    /** Testing setting. */
    @org.junit.Test
    public void as()
    {
      Range range = new Range(10, 20);

      // undefined value
      assertFalse("not undefined at start", range.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   range.toString());

      assertEquals("set", "15-17", range.as(15, 17).toString());
      assertEquals("single", "18", range.as(18, 18).toString());
      assertEquals("min-max", "10-20", range.as(10, 20).toString());
      assertEquals("too low", "10-20", range.as(9, 20).toString());
      assertEquals("too high", "15-20", range.as(15, 21).toString());
      assertEquals("too low and high", "10-20", range.as(9, 21).toString());

      m_logger.addExpected("WARNING: number 9 too low, adjusted to 10");
      m_logger.addExpected("WARNING: range limit 21 too high, adjusted to 20");
      m_logger.addExpected("WARNING: number 9 too low, adjusted to 10");
      m_logger.addExpected("WARNING: range limit 21 too high, adjusted to 20");
    }

    //......................................................................
  }

  //........................................................................
}
