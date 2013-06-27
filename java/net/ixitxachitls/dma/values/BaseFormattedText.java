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

import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a text string and is capable of reading such strings from
 * a reader (and write it to a writer of course). The text is kept formatted as
 * it was when readig in in (e.g. keeping newlines intact).
 *
 * @file          FormattedText.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 * @param         <T> The type for formatted text
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class BaseFormattedText<T extends BaseFormattedText<T>>
  extends BaseText<T>
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Text ---------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * Construct the text object with an undefined value.
   *
   */
  public BaseFormattedText()
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
  public BaseFormattedText(String inText)
  {
    super(inText);
  }

  //........................................................................

  {
    m_editType = "formatted";
  }

  //-------------------------------- create --------------------------------

  /**
   * Create a new text with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar text, but without any contents
   *
   */
  @SuppressWarnings("unchecked")
  @Override
  public T create()
  {
    return super.create((T)new BaseFormattedText());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The delimiters for ending the text. */
  protected static final String s_nameDelimiters =
    Config.get("resource:values/name.delimiter", "\":,.;=[]{}|/");

  /** The pattern for the delimiters (escaped). */
  protected static final String s_nameDelimPattern =
    Config.get("resource:values/name.delimiter.pattern",
               "\":,.;=\\[\\]\\{\\}\\|");

  /** The delimiter to use to start and end the text when reading or
   * printing. */
  protected static final char s_stringDelimiter =
    Config.get("resource:values/text.delimiter", '\"');

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ doToString ------------------------------

  /**
   * Convert the value to a string.
   *
   * @return      the String representation.
   */
  @Override
  protected String doToString()
  {
    return s_stringDelimiter
      + m_text.replaceAll("([" + s_stringDelimiter + "])", "\\\\$1")
      + s_stringDelimiter;
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
   * @return      true if read, false if not
   */
  @Override
  public boolean doRead(ParseReader inReader)
  {
    // read and remove escapes for delimiters
    String text = null;

    if(!inReader.expect(s_stringDelimiter))
      return false;

    ParseReader.Position pos = inReader.getPosition();

    text =
      inReader.read(s_stringDelimiter).replaceAll("\\\\" + s_stringDelimiter,
                                                  "" + s_stringDelimiter);

    if(!inReader.expect(s_stringDelimiter))
      inReader.logWarning(pos, "value.text.unterminated",
                          "read till end of text");

    m_text   = text;

    return true;
  }

  //........................................................................

  //........................................................................
}
