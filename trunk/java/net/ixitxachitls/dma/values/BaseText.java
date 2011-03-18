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
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a text string and is capable of reading such strings
 * from a reader (and write it to a writer of course).
 *
 * @file          BaseText.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @param         <T> the final type of value represented by this class
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class BaseText<T extends BaseText> extends Value<T>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- BaseText -------------------------------

  /**
   * Construct the text object with an undefined value.
   *
   */
  public BaseText()
  {
    this(null);
  }

  //........................................................................
  //------------------------------- BaseText -------------------------------

  /**
   * Construct the text object.
   *
   * @param       inText           the text to store
   *
   */
  public BaseText(@Nullable String inText)
  {
    m_text     = inText;
    m_editType = "name";
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
  @SuppressWarnings("unchecked") // this only works if it is overriden in all
                                 // derivations
  public @Nonnull T create()
  {
    return (T)new BaseText();
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The contents text (null means undefined!). */
  protected @Nullable String m_text = null;

  /** The delimiters for ending the text. */
  protected static final @Nonnull String s_nameDelimiters =
    Config.get("resource:values/name.delimiter.simple",
               "\":,.;=[]{}|()");

  /** The delimiters for ending the text after a space. */
  protected static final @Nonnull String s_nameSpaceDelimiters =
    Config.get("resource:values/name.delimiter.simple.space",
               "-+*%$#@~!");

  /** The pattern for the delimiters (escaped). */
  protected static final @Nonnull String s_nameDelimPattern =
    Config.get("resource:values/name.delimiter.simple.pattern",
               "\":,.;=\\[\\]\\{\\}\\|\\(\\)");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------------- get ----------------------------------

  /**
   * Get the text stored (without escaping).
   *
   * @return      the text stored
   *
   */
  public @Nullable String get()
  {
    return m_text;
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
    // TODO: we might have to parse the string here!
    return new Command(m_text);
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
    return m_text.replaceAll("([" + s_nameDelimPattern + "])", "\\\\$1");
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
    return m_text != null;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  // immutable!

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
  public T as(@Nonnull String inText)
  {
    T result = create();

    result.m_text   = inText;

    return result;
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
  public @Nonnull T add(@Nonnull T inValue)
  {
    String text = "";
    if(m_text != null)
      text = m_text;

    String value = inValue.toString();
    if (text.matches(".*\\s+$") || value.matches("^\\s+.*"))
      text += value;
    else
      text += " " + value;

    return as(text);
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
  public boolean doRead(@Nonnull ParseReader inReader)
  {
    // read and remove escapes for delimiters
    String text =
      Strings.trim(inReader.read(s_nameDelimiters, s_nameSpaceDelimiters)
                   .replaceAll("\\\\([" + s_nameDelimPattern + "])", "$1"));

    if(text.length() == 0)
      return false;

    m_text   = text;

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
    @SuppressWarnings("unchecked") // need to cast
    public void testInit()
    {
      BaseText<BaseText> text = new BaseText<BaseText>();

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
      BaseText added = text.add(new BaseText<BaseText>("more text"));
      assertEquals("added", "just some \\\" test more text", added.toString());
      assertEquals("added", "just some \" test more text",
                   added.format().toString());

      added = text.add(new BaseText<BaseText>(" and more"));
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

      Value.Test.readTest(texts, new BaseText<BaseText>());
    }

    //......................................................................
  }

  //........................................................................
}