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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.commands.Command;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A text value representing a name.
 *
 * @file          Name.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Name extends BaseText<Name>
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Name ---------------------------------

  /**
   * Construct the text object with an undefined value.
   *
   */
  public Name()
  {
  }

  //........................................................................
  //--------------------------------- Name ---------------------------------

  /**
   * Construct the text object.
   *
   * @param       inText           the text to store
   *
   */
  public Name(@Nonnull String inText)
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
public @Nonnull Name create()
  {
    return super.create(new Name());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables
  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- doFormat -------------------------------

  /**
   * Really to the formatting.
   *
   * @return      the command for setting the value
   *
   */
  @Override
protected @Nonnull Command doFormat()
  {
    return new Command(m_text);
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
    //----- init -----------------------------------------------------------

    /** Testing init. */
    @org.junit.Test
    public void testInit()
    {
      Name text = new Name();

      // undefined value
      assertEquals("not undefined at start", false, text.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   text.toString());
      assertEquals("undefined value not correct",
                   "\\color{error}{$undefined$}",
                   text.format().toString());
      assertEquals("undefined value not correct", null, text.get());

      // now with some text
      text = text.as("just some = test");

      assertEquals("not defined after setting", true, text.isDefined());
      assertEquals("value not correctly gotten", "just some \\= test",
                   text.toString());
      assertEquals("value not correctly gotten", "just some = test",
                   text.format().toString());
      assertEquals("value not correctly converted", "just some = test",
                   text.get());

      // now with some text
      text = text.as("just some \" test");

      assertEquals("not defined after setting", true, text.isDefined());
      assertEquals("value not correctly gotten", "just some \" test",
                   text.format().toString());
      assertEquals("value not correctly gotten", "just some \\\" test",
                   text.toString());
      assertEquals("value not correctly converted", "just some \" test",
                   text.get());

      // add something to the text
      Name added = text.add(new Name("more text"));
      assertEquals("added", "just some \\\" test more text", added.toString());
      assertEquals("added", "just some \" test more text",
                   added.format().toString());

      added = text.add(new Name(" and more"));
      assertEquals("added", "just some \\\" test and more", added.toString());
      assertEquals("added", "just some \" test and more",
                   added.format().toString());

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
          "simple", "just some test", "just some test", null,
          "empty", "", null, null,
          "other", "some text = other", "some text", "= other",
          "whites", "   \nsome   \n text  \n \n read", "some text read", null,

          "escapes",
          "some \\= escaped \\\" text",
          "some \\= escaped \\\" text", null,

          "space delimiters",
          "some-text-to-read -here",
          "some-text-to-read", "-here",

          "hint 1", "{*} some text", "{*}some text", null,
          "hint 2", "{~}some text", "{~}some text", null,
          "hint 3", "{*, comment # !.} some text",
          "{*,comment # !.}some text", null,
          "hint 4", "{* some text", null, "{* some text",
        };

      Value.Test.readTest(texts, new Name());
    }

    //......................................................................
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
