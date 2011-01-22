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

package net.ixitxachitls.dma.values;

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.util.Strings;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a single value out of a given selection from a reader (and
 * write it to a writer of course).
 *
 * @file          Selection.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @param         <T> the final type of the value
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Selection<T extends Selection> extends Value<T>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ Selection -------------------------------

  /**
   * Construct the selection object.
   *
   * @param       inSelections the selections valid for this value
   * @param       inSelected   the selected value, starting from 0
   *
   */
  public Selection(@Nonnull String []inSelections, int inSelected)
  {
    m_selections = Arrays.copyOf(inSelections, inSelections.length);
    m_selected   = inSelected;

    withEditType("selection");

    if(!check(inSelected))
      throw new IllegalArgumentException("invalid value '" + inSelected
                                         + "' selected");
  }

  //........................................................................
  //------------------------------ Selection -------------------------------

  /**
   * Construct the selection object.
   *
   * @param       inSelections the selections valid for this value
   *
   */
  public Selection(@Nonnull String []inSelections)
  {
    this(inSelections, -1);
  }

  //........................................................................
  //------------------------------ Selection -------------------------------

  /**
   * Construct the selection object.
   *
   * @param       inSelections the selection inside this value
   * @param       inSelected   the value selected, given as a string
   *
   * @undefined   IllegalArgumentException if invalid selection is given
   *
   */
  public Selection(@Nonnull String []inSelections, @Nonnull String inSelected)
  {
    this(inSelections, -1);

    for(int i = 0; i < m_selections.length; i++)
      if(m_selections[i].equalsIgnoreCase(inSelected))
      {
        m_selected = i;
        break;
      }
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
  @SuppressWarnings("unchecked") // this onlly works if this method is
                                 // overriden in all derivations
  public T create()
  {
    return (T)new Selection(m_selections);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The possible selections. */
  protected @Nonnull String []m_selections;

  /** The currently selected value. */
  protected int m_selected = -1;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- getSelected ------------------------------

  /**
   * Get the selected value, as an integer into the selection array.
   *
   * @return      the selection stored
   *
   */
  public int getSelected()
  {
    return m_selected;
  }

  //........................................................................
  //---------------------------- getSelections -----------------------------

  /**
   * Get the valid selections.
   *
   * @return      all the possible values
   *
   */
  public @Nonnull String []getSelections()
  {
    return Arrays.copyOf(m_selections, m_selections.length);
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
    return new Command(toString());
  }

  //........................................................................
  //------------------------------ doToString ------------------------------

  /**
   * Convert the value to a string, depending on the given kind.
   *
   * @return      a String representation, depending on the kind given
   *
   */
  protected @Nonnull String doToString()
  {
    if(m_selected >= 0)
      return m_selections[m_selected];

    return UNDEFINED;
  }

  //........................................................................

  //------------------------------ isDefined -------------------------------

  /**
   * Check if the value is defined or not.
   *
   * @return      true if the value is defined, false if not
   *
   */
  public boolean isDefined()
  {
    return m_selected >= 0;
  }

  //........................................................................

  //---------------------------- getEditValues -----------------------------

  /**
   * Get the all the possible values this value can be edited with. Returns
   * null if no preselection is available.
   *
   * @return      the possible value to select from or null for no selection
   *
   */
  public @Nonnull String getEditValues()
  {
    return Strings.toString(m_selections, "||", "");
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //----------------------------- setSelected ------------------------------

  /**
   * Set the value of the selection that is set.
   *
   * @param       inValue the value to select
   *
   * @return      true if set, false if value is invalid
   *
   */
//   public boolean setSelected(int inValue)
//   {
//     if(inValue < 0 || inValue >= m_selections.length)
//       return false;

//     m_stored   = true;
//     m_selected = inValue;

//     return true;
//   }

  //........................................................................
  //----------------------------- setSelected ------------------------------

  /**
   * Set the value of the selection that is set (using a String).
   *
   * @param       inValue the value to select
   *
   * @return      true if set, false if value is invalid
   *
   */
//   public boolean setSelected(String inValue)
//   {
//     if(inValue == null)
//       return false;

//     m_stored = true;
//     for(int i = 0; i < m_selections.length; i++)
//       if(m_selections[i].equalsIgnoreCase(inValue))
//       {
//         m_selected = i;

//         return true;
//       }

//     return false;
//   }

  //........................................................................

  //--------------------------------- add ----------------------------------

  /**
   * Add the given value to the beginning of the current one.
   *
   * @param       inValue the value to add to this one
   *
   */
//   public @Nonnull T add(@Nonnull T inValue)
//   {
//     if(isDefined())
//       throw new IllegalStateException("cannot add to a defined value");

//     return inValue.clone();
//   }

  //........................................................................
  //------------------------------- multiply -------------------------------

  /**
   * Multiply the selection. This will increase the current index in the
   * available selection by maximally the given amount.
   *
   * @param       inValue the multiplication factor
   *
   * @return      true if multiplied, false if not
   *
   */
//   public @Nonnull T multiply(long inValue)
//   {
//     T result = create();

//     if(m_selected < 0)
//       return result;

//     result.m_selected += inValue;

//     if(result.m_selected >= result.m_selections.length)
//       result.m_selected = result.m_selections.length - 1;

//     return result;
//   }

  //........................................................................
  //-------------------------------- divide --------------------------------

  /**
   * Divide the dice. This decreases the selection by maximally the given
   * number.
   *
   * @param       inValue the division factor
   *
   * @return      true if divided, false if not
   *
   */
//   public @Nonnull T divide(long inValue)
//   {
//     T result = create();

//     if(m_selected < 0)
//       return result;

//     result.m_selected -= inValue;

//     if(result.m_selected < 0)
//       result.m_selected = 0;

//     return result;
//   }

  //........................................................................
  //-------------------------------- modify --------------------------------

  /**
   *
   * Modify the value.
   *
   * @param       inModify the modifier to apply to this value
   *
   * @return      true if modified, false if not
   *
   */
  // TODO: remove this
//   public boolean modify(ValueGroup.Modifier inModify)
//   {
//     if(inModify.getType() == ValueGroup.Modifier.Type.MULTIPLY)
//       return multiply(inModify.getFactor());

//     if(inModify.getType() == ValueGroup.Modifier.Type.DIVIDE)
//       return divide(inModify.getFactor());

//     throw new UnsupportedOperationException("the modification " + inModify
//                                             + " is not supported");
//   }

  //........................................................................

  //-------------------------------- reset ---------------------------------

  /**
   * Reset the value to undefined.
   *
   */
  public void reset()
  {
    m_selected = -1;
  }

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
  public boolean doRead(@Nonnull ParseReader inReader)
  {
    m_selected = inReader.expectCase(m_selections, true);

    if(m_selected < 0)
      return false;

    return true;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //-------------------------------- check ---------------------------------

  /**
   * Check if the given value would be a valid value for the selection.
   *
   * @param       inValue the value to check for
   *
   * @return      true if the value would be valid, false else
   *
   */
  public boolean check(@Nonnull String inValue)
  {
    for(int i = 0; i < m_selections.length; i++)
      if(m_selections[i].equalsIgnoreCase(inValue))
        return true;

    return false;
  }

  //........................................................................
  //-------------------------------- check ---------------------------------

  /**
   * Check if the given value would be a valid value for the selection.
   *
   * @param       inValue the value to check for
   *
   * @return      true if the value would be valid, false else
   *
   */
  public boolean check(int inValue)
  {
    return inValue == -1
      || (inValue < m_selections.length && inValue >= 0);
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
      Selection selection =
        new Selection(new String [] { "how", "are", "you", "?" });

      // undefined value
      assertEquals("not undefined at start", false, selection.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   selection.toString());
      assertEquals("undefined value not correct", -1,
                   selection.getSelected());

      // now with some selection
      selection = new Selection(new String [] { "how", "are", "you", "?" }, 2);

      assertEquals("not defined after setting", true, selection.isDefined());
      assertEquals("value not correctly gotten", 2, selection.getSelected());
      assertEquals("value not correctly converted", "you",
                   selection.toString());
      assertEquals("edit values", "how||are||you||?",
                   selection.getEditValues());

      // what do we have?
      assertEquals("get", "how", selection.getSelections()[0]);
      assertEquals("get", "are", selection.getSelections()[1]);
      assertEquals("get", "you", selection.getSelections()[2]);
      assertEquals("get", "?",   selection.getSelections()[3]);

      Value.Test.cloneCreateResetTest(selection);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      String []tests =
        {
          "simple", "first", "first", null,
          "simple 2", "third", "third", null,
          "casing", "sEconD", "second", null,
          "whites", "    first   ", "first", "   ",
          "multi", "multi word", "multi word", null,
          "multi white", "    multi   \n  word\n", "multi word", null,
          "empty", "", null, null,
          "invalid", "guru", null, "guru",
          "invalid 2", "firstttt", null, "firstttt",
          "other", "first again", "first", " again",
          "other 2", "second.", "second", ".",
        };

      Value.Test.readTest(tests,
                          new Selection(new String []
                            { "first", "second", "third", "multi word", }));
    }

    //......................................................................
    //----- set ------------------------------------------------------------

    /** Testing setting. */
//     @org.junit.Test
//     public void set()
//     {
//       Selection selection =
//         new Selection(new String [] { "1", "2", "3", "4" });

//       assertFalse("not undefined at start", selection.isDefined());
//       assertEquals("undefined value not correct", "$undefined$",
//                    selection.toString());

//       assertTrue("simple set", selection.setSelected(3));
//       assertEquals("simple set", "4", selection.toString());

//       assertTrue("simple string set", selection.setSelected("3"));
//       assertEquals("simple string set", "3", selection.toString());

//       assertFalse("too low", selection.setSelected(-1));
//       assertEquals("too low", "3", selection.toString());

//       assertFalse("too high", selection.setSelected(4));
//       assertEquals("too high", "3", selection.toString());

//       assertFalse("invalid", selection.setSelected("5"));
//       assertEquals("invalid", "3", selection.toString());
//     }

    //......................................................................
    //----- compute --------------------------------------------------------

    /** Value computations. */
//     @org.junit.Test
//     public void compute()
//     {
//       Selection selection =
//         new Selection(new String [] { "1", "2", "3", "4", "5", "6", "7", "8",
//                                       "9", "10", });

      // not initialized
//       assertFalse(selection.multiply(3));
//       assertEquals("start", -1, selection.getSelected());

//       assertFalse(selection.divide(3));
//       assertEquals("start", -1, selection.getSelected());

//       // initialize in the middle
//       selection.setSelected(5);

//       assertTrue(selection.multiply(3));
//       assertEquals("multiply", 8, selection.getSelected());

//       assertTrue(selection.multiply(3));
//       assertEquals("multiply", 9, selection.getSelected());

//       selection.setSelected(5);

//       assertTrue(selection.divide(3));
//       assertEquals("divide", 2, selection.getSelected());

//       assertTrue(selection.divide(3));
//       assertEquals("divide", 0, selection.getSelected());

      // now for modifications
      // TODO: remove this
//       selection.setSelected(5);

//       assertTrue(selection.modify(new ValueGroup
// .Modifier(ValueGroup.Modifier.Type.MULTIPLY,
//                                             3)));
//       assertEquals("modify", 8, selection.getSelected());

//       assertTrue(selection.modify(new ValueGroup
//                                   .Modifier(ValueGroup.Modifier.Type.DIVIDE,
//                                             6)));
//       assertEquals("modify", 2, selection.getSelected());
//     }

    //......................................................................
  }

  //........................................................................
}
