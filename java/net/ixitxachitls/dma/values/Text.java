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

import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.BaseCommand;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a text string and is capable of reading such strings
 * from a reader (and write it to a writer of course).
 *
 * @file          Text.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Text extends BaseText<Text>
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Text ---------------------------------

  /**
   * Construct the text object with an undefined value.
   *
   */
  public Text()
  {
    // nothing to do
  }

  //........................................................................
  //--------------------------------- Text ---------------------------------

  /**
   * Construct the text object.
   *
   * @param       inText the text to store
   *
   */
  public Text(@Nonnull String inText)
  {
    super(inText);
  }

  //........................................................................

  {
    m_editType = "string";
  }

  //-------------------------------- create --------------------------------

  /**
   * Create a new text with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar text, but without any contents
   *
   */
  @Override
  public @Nonnull Text create()
  {
    return super.create(new Text());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The delimiters for ending the text. */
  protected static final @Nonnull String s_nameDelimiters =
    Config.get("resource:values/name.delimiter", "\":,.;=[]{}|/");

  /** The pattern for the delimiters (escaped). */
  protected static final @Nonnull String s_nameDelimPattern =
    Config.get("resource:values/name.delimiter.pattern",
               "\":,.;=\\[\\]\\{\\}\\|");

  /** The delimiter to use to start and end the text when reading or
   * printing. */
  protected static final char s_stringDelimiter =
    Config.get("resource:values/text.delimiter", '\"');

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- doGroup --------------------------------

  /**
   * Really do grouping for this object. This method can be derived to have
   * special grouping in derivations.
   *
   * @return      a string denoting the group this value is in
   *
   */
  @Override
  public @Nonnull String doGroup()
  {
    return m_text;
  }

  //........................................................................
  //------------------------------- doToString ------------------------------

  /**
   * Convert the value to a string.
   *
   * @return      a String representation, depending on the kind given
   *
   */
  @Override
  protected @Nonnull String doToString()
  {
    return s_stringDelimiter
      + m_text.replaceAll("([" + s_stringDelimiter + "])", "\\\\$1")
      + s_stringDelimiter;
  }

  //........................................................................
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
    return new BaseCommand(m_text.replaceAll("[\n\f ]+", " "));
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

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
  public boolean doRead(@Nonnull ParseReader inReader)
  {
    // read and remove escapes for delimiters
    String text = null;

    if(!inReader.expect(s_stringDelimiter))
      return false;

    ParseReader.Position pos = inReader.getPosition();

    text =
      inReader.read(s_stringDelimiter).replaceAll("\\\\" + s_stringDelimiter,
                                                  "" + s_stringDelimiter);

    text = Strings.trim(text);

    if(!inReader.expect(s_stringDelimiter))
      inReader.logWarning(pos, "value.text.unterminated",
                          "read till end of text");

    m_text = text;

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
    public void init()
    {
      Text text = new Text();

      // undefined value
      assertEquals("not undefined at start", false, text.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   text.toString());
      assertEquals("undefined value not correct", null, text.get());

      // now with some text
      text = text.as("just some = test");

      assertEquals("not defined after setting", true, text.isDefined());
      assertEquals("value not correctly gotten", "\"just some = test\"",
                   text.toString());
      assertEquals("value not correctly converted", "just some = test",
                   text.get());

      // now some string representation stuff
      text = new Text();

      // undefined value
      assertEquals("not undefined at start", false, text.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   text.toString());
      assertEquals("undefined value not correct", null, text.get());
      assertEquals("format", "", text.format(true).toString());

      // now with some text
      text = text.as("just some \" test");

      assertEquals("not defined after setting", true, text.isDefined());
      assertEquals("value not correctly gotten", "\"just some \\\" test\"",
                   text.toString());
      assertEquals("value not correctly converted", "just some \" test",
                   text.get());

      text = new Text();

      // undefined value
      assertEquals("not undefined at start", false, text.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   text.toString());
      assertEquals("undefined value not correct", null, text.get());

      // now with some formattedText
      text = text.as("  just \n   some \n\n\" test");

      assertEquals("not defined after setting", true, text.isDefined());
      assertEquals("value not correctly gotten",
                   "\"  just \n   some \n\n\\\" test\"",
                   text.toString());
      assertEquals("value not correctly converted",
                   "  just \n   some \n\n\" test",
                   text.get());

      Value.Test.createTest(text);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      // quoted text
      String []texts = new String []
        {
          "simple", "\"just some test\"", "\"just some test\"", null,
          "empty", "", null, null,
          "empty 2", "\"\"", "\"\"", null,
          "other", "\"some text =\" other", "\"some text =\"", " other",

          "whites",
          "   \"\nsome   \n text  \n \n read  \"  ",
          "\"some text read\"", "  ",

          "escapes",
          "\"some escaped \\\" text\"",
          "\"some escaped \\\" text\"", null,

          "unclosed", "\"some unclosed text", "\"some unclosed text\"", null,
        };

      m_logger.addExpectedPattern("WARNING:.*\\(read till end of text\\) "
                                  + "on line 1 in document 'test'."
                                  + "...\">>>some unclosed text...");

      Value.Test.readTest(texts, new Text());
    }

    //......................................................................
    //----- set ------------------------------------------------------------

    /** Testing setting. */
    @org.junit.Test
    public void set()
    {
      Text text = new Text();

      text = text.as("a test");

      assertEquals("simple", "a test", text.get());

      text = text.as("another = test");

      assertEquals("check", "another = test", text.get());
      assertEquals("converted text does not match",
                   "\"another = test\"", text.toString());
    }

    //......................................................................
    //----- fail -----------------------------------------------------------

    /** Testing failed reading. */
    @org.junit.Test
    public void failTest()
    {
      ParseReader reader =
        new ParseReader(new java.io.StringReader("guru "
                                                 + "\"just \\\" a \\\" test"),
                        "test");

      Text text = new Text().read(reader);

      assertNull("text should not have been read", text);

      reader.read(' ');

      text = new Text().read(reader);
      assertTrue("text should have been read", text != null);
      assertEquals("text does not match", "just \" a \" test", text.get());
      assertEquals("converted text does not match",
                   "\"just \\\" a \\\" test\"", text.toString());

      m_logger.addExpectedPattern("WARNING:.*\\(read till end of text\\) "
                                  + "on line 1 in document 'test'."
                                  + "\\.\\.\\.guru \">>>just \\\\\" a \\\\\" "
                                  + "test\\.\\.\\.");
    }

    //......................................................................
  }

  //........................................................................
}
