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

package net.ixitxachitls.dma.values;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.input.ParseReader;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class repretents a union of one of multiple possible values.
 *
 * @file          Union.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Union extends Value<Union>
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Union ---------------------------------

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Construct the value selection object with a undefined value.
   *
   * @param       inValues all the possible values that can be read
   *
   */
  public Union(Value ... inValues)
  {
    this(-1, inValues);
  }

  //........................................................................
  //-------------------------------- Union ---------------------------------

  /**
   * Construct the value selection object with a defined value.
   *
   * @param       inIndex  the index of the value selected
   * @param       inValues all the possible values that can be read
   *
   */
  public Union(int inIndex, Value ... inValues)
  {
    if(inValues.length < 2)
      throw new IllegalArgumentException("must have at least two values here");

    if(inIndex < -1 || inIndex >= inValues.length)
      throw new IllegalArgumentException("invalid value index given");

    m_values = inValues;
    m_index  = inIndex;
  }

  //........................................................................

  //-------------------------------- create --------------------------------

  /**
   * Create a new list with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar list, but without any contents
   *
   * @undefined   never
   *
   */
  @Override
  public Union create()
  {
    return super.create(new Union(m_values));
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The possible values store here. */
  private Value []m_values;

  /** The index of the value actually read. */
  private int m_index;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------------- get ----------------------------------

  /**
   * Get the value read (or null if none is read).
   *
   * @return      the value read (or null if none)
   *
   */
  public @Nullable Value<?> get()
  {
    if(m_index < 0 || m_index >= m_values.length)
      return null;

    return m_values[m_index];
  }

  //........................................................................
  //------------------------------- getIndex -------------------------------

  /**
   * Get the index of the value read (or -1 if none is read).
   *
   * @return      the index of the value read (or -1 if none)
   *
   */
  public int getIndex()
  {
    return m_index;
  }

  //........................................................................
  //----------------------------- getEditValue -----------------------------

  /**
   * Convert the given value into a String for editing.
   *
   * @return      the object converted to a String
   *
   */
  @Override
  public String getEditValue()
  {
    if(isDefined())
      return m_values[m_index].getEditValue();

    return "";
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
    if(isDefined())
      return m_values[m_index].toString();

    return "";
  }

  //........................................................................
  //----------------------------- isArithmetic -----------------------------

  /**
   * Checks whether the value is arithmetic and thus can be computed with.
   *
   * @return      true if the value is arithemtic
   *
   */
  @Override
  public boolean isArithmetic()
  {
    for(Value<?> value : m_values)
      if(!value.isArithmetic())
        return false;

    return true;
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
    return m_index >= 0;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- addTo ---------------------------------

  /**
   * Add the given value to the beginning of the current one.
   *
   * @param       inValue the value to add to this one
   *
   */
  // public void addTo(Union inValue)
  // {
  //   if(isDefined())
  //     throw new UnsupportedOperationException("cannot add to this "
  //                                             + getClass() + " if defined");

  //   m_stored = true;
  //   m_index  = inValue.m_index;
  // }

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
  public boolean doRead(ParseReader inReader)
  {
    for(int i = 0; i < m_values.length; i++)
    {
      ParseReader.Position pos = inReader.getPosition();
      Value<?> value = m_values[i].read(inReader);
      if(value != null)
      {
        m_values[i] = value;
        m_index = i;

        return true;
      }
      inReader.seek(pos);
    }

    return false;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The Test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** Testing init. */
    @org.junit.Test
    public void init()
    {
      Union value =
        new Union(new Selection(new String [] { "a", "b", "c", }), new Name());

      // undefined value
      assertEquals("not undefined at start", false, value.isDefined());
      assertEquals("undefined output", "$undefined$",
                   value.toString());

      // now with some value
      value =
        new Union(1, new Selection(new String [] { "a", "b", "c", }),
                  new Name("GURU"));

      //System.out.println(value);
      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("output", "GURU", value.toString());
      assertEquals("index", 1, value.getIndex());

      // now for the test of reset, clone and createNew
      Value.Test.createTest(value);
   }

    //......................................................................
    //----- get ------------------------------------------------------------

    /** Testing get. */
    @org.junit.Test
    public void get()
    {
      Union value =
        new Union(1,
                           new Selection(new String [] { "a", "b", "c", }),
                           new Name("GURU"));

      assertEquals("get", "GURU", value.get().toString());
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing read. */
    @org.junit.Test
    public void read()
    {
      String []tests =
        {
          "empty", "", null, null,
          "text", "hello", "hello", null,
          "selection", "a", "A", null,
          "selection 2", "B", "B", null,
          "text", "cc", "cc", null,
        };

      Value.Test.readTest(tests,
                          new Union(new Selection(new String []
                            { "A", "B", "C", }),
                                             new Name("GURU")));
    }

    //......................................................................
  }

  //........................................................................
}
