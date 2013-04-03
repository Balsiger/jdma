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

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.values.FormattedText;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.Command;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base for all conditions.
 *
 * @file          Condition.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @param         <T> the type of value
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Condition<T extends Condition<T>> extends Value<T>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Condition ------------------------------

  /**
   * Construct the condition object with an undefined value.
   *
   */
  public Condition()
  {
    withTemplate("condition");
    withEditType("string");
  }

  //........................................................................
  //------------------------------- Condition ------------------------------

  /**
   * The constructor with a condition description.
   *
   * @param       inDescription - a description of the condition
   *
   */
  public Condition(String inDescription)
  {
    m_description = m_description.as(inDescription);

    withTemplate("condition");
    withEditType("string");
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
  // This cast is only safe if all derivations override it!
  @SuppressWarnings("unchecked")
  @Override
  public T create()
  {
    return super.create((T)new Condition());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The description of the condition. */
  private FormattedText m_description = new FormattedText();

  /** The result type of a condition (including an undefined value). */
  public enum Result { TRUE, FALSE, UNDEFINED };

  //........................................................................

  //-------------------------------------------------------------- accessors

  //---------------------------- getDescription ----------------------------

  /**
   * Get the description of the condition stored.
   *
   * @return      the requested description
   *
   */
  public String getDescription()
  {
    return m_description.get();
  }

  //........................................................................
  //------------------------------ doToString ------------------------------

  /**
   * Convert the value to a string.
   *
   * @return      the requested string
   *
   */
  public String doToString()
  {
    return m_description.toString();
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
    return m_description.isDefined();
  }

  //........................................................................
  //-------------------------------- check ---------------------------------

  /**
   * Check if the given condition is currently true or not.
   *
   * @param       inInteractive - true if an interactive request to the
   *                              user is allowed, false if not
   *
   * @return      a Result containing either TRUE, FALSE, or UNDEFINED
   *
   */
  public Result check(boolean inInteractive)
  {
    if(inInteractive)
      throw new UnsupportedOperationException("interactive is not yet "
                                              + "implemented!");

    return Result.UNDEFINED;
  }

  //........................................................................

  //------------------------------- doFormat -------------------------------

  /**
   * Really to the formatting.
   *
   * @return      the command for setting the value
   *
   */
  @Deprecated
  protected Command doFormat()
  {
    return m_description.format(false);
  }

  //........................................................................

  //-------------------------------- format --------------------------------

  /**
   * Format the value for printing.
   *
   * @return      the command to use for printing
   *
   */
  @Deprecated
  public Command format()
  {
    return m_description.format(true);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- doRead ---------------------------------

  /**
   * Read the values only from the reader and replace the current ones.
   *
   * @param       inReader the reader to read from
   *
   * @return      true if read, false if not
   *
   */
  @Override
  public boolean doRead(ParseReader inReader)
  {
    FormattedText description = m_description.read(inReader);
    if (description == null)
      return false;

    m_description = description;
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

    /** Test init. */
    @org.junit.Test
    public void testInit()
    {
      Condition value = new Condition();

      // undefined value
      assertEquals("not undefined at start", false, value.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   value.toString());

      // now with some value (pounds)
      value = new Condition("just some test");

      //System.out.println(value);
      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("output", "\"just some test\"", value.toString());
      assertEquals("output", "just some test", value.getDescription());

      assertEquals("check", Result.UNDEFINED, value.check(false));

      Value.Test.createTest(value);
   }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Test reading. */
    @org.junit.Test
    public void testRead()
    {
      String []tests =
        {
          "empty", "", null, null,
          "nul", "\"\"", "\"\"", null,
          "simple", "\"test\"", "\"test\"", null,
          "longer", "\"some larger text\"", "\"some larger text\"", null,
        };

      Value.Test.readTest(tests, new Condition());
    }

    //......................................................................
  }

  //........................................................................
}
