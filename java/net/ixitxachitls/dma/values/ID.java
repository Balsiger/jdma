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

import java.util.Random;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A text value representing a unique id (for entries).
 *
 * @file          ID.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class ID extends BaseText<ID>
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- ID ---------------------------------

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Construct the text object with an undefined value.
   *
   */
  public ID()
  {
  }

  //........................................................................
  //--------------------------------- ID ---------------------------------

  /**
   * Construct the text object.
   *
   * @param       inText           the text to store
   *
   */
  public ID(String inText)
  {
    super(inText);
  }

  //........................................................................

  //-------------------------------- create --------------------------------

  /**
   * Create a new text with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar text, but without any contents
   *
   */
  @Override
  public ID create()
  {
    return super.create(new ID());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The number of digits to be used in ids. */
  public static final int s_digits = Config.get("values.id.digits", 4);

  /** The random generator. */
  protected static final Random s_random = new Random();

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //---------------------------------- as ----------------------------------

  /**
   * Set the text stored in this value.
   *
   * @param       inText the new text to set, set to null to undefined the
   *                     value
   *
   * @return      a new value with the given value set
   *
   */
  @Override
  public ID as(String inText)
  {
    throw new UnsupportedOperationException("cannot set an id");
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the given value to the current one.
   *
   * @param       inValue the value to add to this one
   *
   * @return      the additional of the current and the given value
   *
   */
  @Override
  public ID add(ID inValue)
  {
    throw new UnsupportedOperationException("cannot add to id");
  }

  //........................................................................
  //-------------------------------- random --------------------------------

  /**
   * Create a new, random ID.
   *
   * @return      a new value with a random id
   *
   */
  public static ID random()
  {
    ID result = new ID();

    char []generated = new char[s_digits];
    for(int i = 0; i < s_digits; i++)
      generated[i] = (char)(s_random.nextInt(26) + 'A');

    result.m_text = new String(generated);

    return result;
  }

  //........................................................................
  //------------------------------- doRead ---------------------------------

  /**
   * Read the value from the reader and replace the current one. This is
   * copy of the base method, but is required here to be sure to use
   * the static delimiter variables of this version.
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
    char []read = new char[s_digits];
    for(int i = 0; i < s_digits; i++)
    {
      try
      {
        read[i] = inReader.readChar();
      }
      catch(net.ixitxachitls.input.ReadException e)
      {
        read[i] = 0;
      }

      if(read[i] > 'Z' || read[i] < 'A')
      {
        inReader.seek(pos);
        return false;
      }
    }

    m_text = new String(read);

    return true;
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
    public void testInit()
    {
      ID text = new ID();

      // undefined value
      assertEquals("not undefined at start", false, text.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   text.toString());
      assertEquals("undefined value not correct", null, text.get());

      // now with some text
      text = ID.random();

      assertEquals("not defined after setting", true, text.isDefined());
      assertTrue("value not correctly gotten",
                 text.toString().matches("[A-Z]{" + s_digits + "}"));
      assertTrue("value not correctly converted",
                 text.get().matches("[A-Z]{" + s_digits + "}"));

      Value.Test.createTest(text);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void testRead()
    {
      // name test
      String []texts =
        {
          "simple", "ABCD", "ABCD", null,
          "empty", "", null, null,
          "other", "ABCD EF", "ABCD", " EF",
          "whites", "   A    B \n   C D  E F ", "ABCD", "  E F ",

          "lowercase", "abcd", null, "abcd",
        };

      Value.Test.readTest(texts, new ID());
    }

    //......................................................................
  }

  //........................................................................
}
