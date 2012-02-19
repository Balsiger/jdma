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

package net.ixitxachitls.output;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import net.ixitxachitls.util.Strings;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a simple buffer that can wrap lines correctly.
 *
 * @file          WrapBuffer.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @example       <PRE>
 * WrapBuffer buffer = new WrapBuffer(80);
 *
 * buffer.add("just some test\n");
 * buffer.add("of, course, sever lines, longer lines are " +
 *            "automatically wrapped over several lines, as " +
 *            "it should be.\n");
 *
 * // obtain the lines (we do nothing with them here...)
 * while(buffer.hasMoreLines())
 *   String line = buffer.getLine();
 *
 * String last = buffer.getLines(); // the rest of the buffer
 * </PRE>
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
public class WrapBuffer implements Buffer
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ WrapBuffer ------------------------------

  /**
   * Construct the buffer with the given with.
   *
   * @param       inWidth the width at which to wrap
   *
   */
  public WrapBuffer(int inWidth)
  {
    if(inWidth < 0)
      throw new IllegalArgumentException("must have a positive width");

    m_width = inWidth;
  }

  //........................................................................
  //------------------------------ WrapBuffer ------------------------------

  /**
   * Construct the buffer with the given with.
   *
   * @param       inWidth  the width at which to wrap
   * @param       inIgnore the characters to ignored for line length counting
   *
   */
  public WrapBuffer(int inWidth, @Nonnull String inIgnore)
  {
    this(inWidth);

    m_ignore = inIgnore;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The width of the buffer. */
  private int m_width;

  /** The pattern of text to ignore for wrapping it. */
  private @Nullable String m_ignore = null;

  /** The actual lines in the buffer. */
  private @Nonnull List<String> m_lines = new LinkedList<String>();

  /** The current line of the buffer. */
  private @Nonnull StringBuilder m_current = new StringBuilder();

  /** The alignment inside the current line of the buffer. */
  private @Nonnull Alignment m_alignment = Alignment.left;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getWidth -------------------------------

  /**
   * Get the width of the buffer.
   *
   * @return      the with in characters
   *
   */
  public int getWidth()
  {
    return m_width;
  }

  //........................................................................

  //------------------------------- getLine --------------------------------

  /**
   * Get a complete, full line from the buffer. If no full line is available,
   * null is returned, even if there is some partially filled line stored.
   *
   * @return      the first line in the buffer or null if none
   *
   */
  public @Nullable String getLine()
  {
    // are there any lines
    if(m_lines.size() > 0)
      return m_lines.remove(0);
    else
      return null;
  }

  //........................................................................
  //------------------------------- getLines -------------------------------

  /**
   * Get all the lines currently in the buffer.
   *
   * @return      a String containing all the lines
   *
   */
  public @Nonnull String getLines()
  {
    StringBuilder result = new StringBuilder();

    for(String line = getLine(); line != null; line = getLine())
    {
      result.append(line);
      result.append('\n');
    }

    // now append the last line
    result.append(m_current);
    m_current = new StringBuilder();

    return result.toString();
  }

  //........................................................................
  //------------------------- hasMoreCompleteLines -------------------------

  /**
   * Check if the buffer has more complete lines.
   *
   * @return      true if there are more complete lines, false else
   *
   */
  public boolean hasMoreCompleteLines()
  {
    return m_lines.size() > 0;
  }

  //........................................................................

  //----------------------------- getAlignment -----------------------------

  /**
   * Get the current alignment.
   *
   * @return      the current alignment
   *
   */
  public @Nonnull Alignment getAlignment()
  {
    return m_alignment;
  }

  //........................................................................

  //------------------------------- getLength ------------------------------

  /**
   * Determine the length of the given String. This method mainly exists
   * to allow to ignore special characters
   *
   * @param       inText the text to determine the length of
   *
   * @return      the number of characters in the text
   *
   */
  public int getLength(@Nullable String inText)
  {
    if(inText == null || inText.length() == 0)
      return 0;

    if(m_ignore == null || m_ignore.length() == 0)
      return inText.length();

    return inText.replaceAll(m_ignore, "").length();
  }

  //........................................................................
  //-------------------------- getSubstringLength --------------------------

  /**
   * Get a substring of the given string with the given length but
   * also taking into account the ignored characters.
   *
   * @param       inString the String to extract from
   * @param       inLength the length of the resulting strings
   *
   * @return      the length of the substring to use
   *
   */
  public int getSubstringLength(@Nullable String inString, int inLength)
  {
    if(inString == null || inLength <= 0)
      return 0;

    int length = inString.length();

    // substring complete string
    if(inLength >= length)
      return length;

    // no ignored characters
    if(m_ignore == null)
      return inLength;

    // now the real problem...

    // without special characters the string is smaller
    if(getLength(inString) <= inLength)
      return length;

    String []parts = inString.split(m_ignore);

    int pos = inString.indexOf(parts[0]);
    for(int i = 0, real = 0; i < parts.length; i++)
    {
      pos += inString.substring(pos).indexOf(parts[i]);

      int partLength = parts[i].length();

      if(real + partLength > inLength)
        return pos + (inLength - real);

      real += partLength;
      pos  += partLength;
    }

    return length;
  }

  //........................................................................

  //------------------------------ newBuffer -------------------------------

  /**
   * Get a similar buffer.
   *
   * @return      the new buffer
   *
   */
  @Override
public @Nonnull Buffer newBuffer()
  {
    return new WrapBuffer(m_width, m_ignore);
  }

  //........................................................................
  //------------------------------ newBuffer -------------------------------

  /**
   * Get a similar buffer as the current one, but with a given width.
   *
   * @param       inWidth the desired with of the new buffer
   *
   * @return      the new buffer
   *
   */
  @Override
public @Nonnull Buffer newBuffer(int inWidth)
  {
    return new WrapBuffer(inWidth, m_ignore);
  }

  //........................................................................

  //----------------------------- getContents ------------------------------

  /**
   * Get the complete contents of the buffer.
   *
   * @return      a String with the complete contents of the buffer
   *
   */
  @Override
public @Nonnull String getContents()
  {
    StringBuilder result = new StringBuilder();

    for(Iterator<String> i = m_lines.iterator(); i.hasNext(); )
    {
      result.append(i.next());
      result.append('\n');
    }

    // now append the last line
    result.append(m_current);

    return result.toString();
  }

  //........................................................................
  //------------------------------- hasMore --------------------------------

  /**
   * Check if the buffer has more text not yet read.
   *
   * @return      true if there is more, false if not
   *
   */
  public boolean hasMore()
  {
    if(hasMoreCompleteLines())
      return true;

    return m_current.length() > 0;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- append --------------------------------

  /**
   * Append the given object to the buffer.
   *
   * @param       inObject the object to add
   *
   */
  @Override
  public void append(@Nullable Object inObject)
  {
    if(inObject == null)
      return;

    add(inObject.toString());
  }

  //........................................................................

  //--------------------------------- add ----------------------------------

  /**
    * Add a String to the buffer (and wrap it if necessary).
    *
    * @param       inText the text to add to the buffer
    *
    */
  public void add(@Nonnull String inText)
  {
    preprocess();

    m_current.append(inText);

    postprocess();
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add an character value to the buffer (and wrap it if necessary).
   *
   * @param       inValue the value to add to the buffer
   *
   */
  public void add(char inValue)
  {
    preprocess();

    m_current.append(inValue);

    postprocess();
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add an integer value to the buffer (and wrap it if necessary).
   *
   * @param       inValue the value to add to the buffer
   *
   */
  public void add(int inValue)
  {
    preprocess();

    m_current.append(inValue);

    postprocess();
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add a long value to the buffer (and wrap it if necessary).
   *
   * @param       inValue the value to add to the buffer
   *
   */
  public void add(long inValue)
  {
    preprocess();

    m_current.append(inValue);

    postprocess();
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add a float value to the buffer (and wrap it if necessary).
   *
   * @param       inValue the value to add to the buffer
   *
   */
  public void add(float inValue)
  {
    preprocess();

    m_current.append(inValue);

    postprocess();
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add a double value to the buffer (and wrap it if necessary).
   *
   * @param       inValue the value to add to the buffer
   *
   */
  public void add(double inValue)
  {
    preprocess();

    m_current.append(inValue);

    postprocess();
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add a boolean value to the buffer (and wrap it if necessary).
   *
   * @param       inValue the value to add to the buffer
   *
   */
  public void add(boolean inValue)
  {
    preprocess();

    m_current.append(inValue);

    postprocess();
  }

  //........................................................................
  //----------------------------- setAlignment -----------------------------

  /**
   * Set the current alignment of the buffer. This alignment will be used
   * for the WHOLE current and all subsequent lines (until the alignment
   * is changed again that is).
   *
   * @param       inAlignment the new alignment
   *
   */
  public void setAlignment(@Nonnull Alignment inAlignment)
  {
    m_alignment = inAlignment;
  }

  //........................................................................

  //------------------------------ preprocess ------------------------------

  /**
   * This is the preprocessing method called before any text is added to
   * the internal buffer.
   *
   */
  protected void preprocess()
  {
    // nothing to do
  }

  //........................................................................
  //----------------------------- postprocess ------------------------------

  /**
   * Do the post processing stuff. This does all stuff that has to be done
   * after text is added to the internal buffer.
   *
   */
  protected void postprocess()
  {
    while(checkLength())
    {
      // nothing to do here, all done in checkLength()
    }
  }

  //........................................................................
  //----------------------------- checkLength ------------------------------

  /**
   * Check the length of the current line and if possible move ONE line
   * to the list of lines.
   *
   * @return      true if a line was transferred, false else
   *
   */
  protected boolean checkLength()
  {
    // check for newlines
    int pos = findChar("\n", m_current, m_width, true);

    if(pos >= 0)
    {
      process(pos, true, true, false);

      return true;
    }

    if(getLength(m_current.toString()) > m_width)
    {
      // find a space to break at
      pos = findChar(" ", m_current, m_width, false);

      // no space found
      if(pos < 0)
        if(m_ignore == null)
          process(m_width, false, false, true);
        else
        {
          pos = getSubstringLength(m_current.toString(), m_width);

          process(pos, false, false, true);
        }
      else
        process(pos, false, false, false);

      return true;
    }

    return false;
  }

  //........................................................................
  //------------------------------- process --------------------------------

  /**
   * Process the current line up to a given position by moving it to the
   * list of wrapped lines. This also takes the alignment into consideration.
   *
   * @param       inPos       the position at which to wrap
   * @param       inParagraph true if the break is the end of a paragraph
   *                          or not
   * @param       inNewline   flag if to be on a new line or not
   * @param       inForced    true if break was forced, false else
   *
   */
  protected void process(int inPos, boolean inParagraph, boolean inNewline,
                         boolean inForced)
  {
    // argument checks
    assert inPos >= 0 : "position must not be negative";

    // if the position is 0, then no real line was encountered but only
    // a single newline
    if(inPos == 0)
      m_lines.add("");
    else
    {
      // now we need to read the text and align it accordingly
      StringBuilder text    = new StringBuilder(m_current.substring(0, inPos));
      int           length  = getLength(text.toString());
      int           missing = m_width - length;

      // if the alignment if right, just add the appropriate number of
      // spaces in the front
      if(m_alignment == Alignment.right)
        text.insert(0, Strings.spaces(missing));
      else
        // if the alignment is centered, add half the number of spaces in the
        // front
        if(m_alignment == Alignment.center)
        {
          text.insert(0, Strings.spaces(missing - missing / 2));
          text.append(Strings.spaces(missing / 2));
        }
        else
          // if the alignment is blocked and we don't end a paragraph, add
          // the missing number of spaces to all spaces on the line, starting
          // from the middle of the line
          if(m_alignment == Alignment.block && !inParagraph)
          {
            // compute the base values
            int []spaces = findSpaces(text);
            int count    = spaces.length;

            // do we have any space at all to enlarge, if not, we do nothing
            if(count > 0)
            {
              // add in the middle at first
              int start = spaces.length / 2;
              int add = (int)Math.round(Math.ceil((double)missing / count--));
              text.insert(spaces[start], Strings.spaces(add));

              // sum up in correction the number of spaces inserted before the
              // middle of the text. This number has adjusts the spaces found
              // afterwards, because new text was added
              int correction  = add;
              missing        -= add;
              for(int off = 1; missing > 0; off++)
              {
                // add before the middle
                add = (int)Math.round(Math.ceil((double)missing / count--));
                text.insert(spaces[start - off], Strings.spaces(add));

                // update correction value and missing spaces
                correction += add;
                missing    -= add;

                // add after the middle
                if(missing > 0)
                {
                  add = (int)Math.round(Math.ceil((double)missing / count--));
                  text.insert(spaces[start + off] + correction,
                              Strings.spaces(add));

                  correction += add;
                  missing    -= add;
                }
              }
            }
          }
      // if you want to have spaces to the right of left aligned text,
      // uncomment the following
      else
        text.append(Strings.spaces(missing));

      // add the line to the list of lines
      m_lines.add(text.toString());
    }

    int end = inPos;
    if(!inForced)
      end++;

    if(!inNewline)
      // over read all the white spaces following the wrapped character
      for(; end < m_current.length() && m_current.charAt(end) == ' '; end++)
        /* nothing to do here */;

    // delete all the text moved over
    m_current.delete(0, end);
  }

  //........................................................................

  //------------------------------ findChar --------------------------------

  /**
   * Find a specific character in the buffer up to the given width.
   *
   * @param       inChar  the char to search
   * @param       inText  the text to search in
   * @param       inWidth the maximal with up to which to search
   * @param       inFirst search for the first character found
   *
   * @return      the position where the character was found, or -1 if
   *              none could be found
   *
   */
  protected int findChar(@Nonnull String inChar, @Nonnull StringBuilder inText,
                         int inWidth, boolean inFirst)
  {
    if(inFirst)
    {
      int pos = inText.indexOf(inChar);

      if(pos < 0)
        return -1;

      if((m_ignore == null && pos > inWidth)
         || (m_ignore != null
             && getLength(inText.substring(0, pos)) > inWidth))
          return -1;

      return pos;
    }

    if(m_ignore == null)
      return inText.lastIndexOf(inChar, inWidth);

    int pos;
    int old;
    for(pos = inText.lastIndexOf(inChar, inWidth), old = -1;
        pos >= 0;
        old = pos, pos = inText.indexOf(inChar, pos + 1))
      if(getLength(inText.substring(0, pos)) > inWidth)
        return old;

    return old;
  }

  //........................................................................
  //------------------------------ findSpaces ------------------------------

  /**
   * This is a small auxiliary method to find all spaces in a specified
   * StringBuffer.
   *
   * @param       inBuffer the buffer to find the spaces in
   *
   * @return      an array containing all positions of spaces
   *
   */
  public static @Nonnull int []findSpaces(@Nonnull StringBuilder inBuffer)
  {
    int number = 0;
    for(int i = 0; i < inBuffer.length(); i++)
      if(inBuffer.charAt(i) == ' ')
        number++;

    int []result = new int[number];
    for(int i = 0, j = 0; i < inBuffer.length(); i++)
      if(inBuffer.charAt(i) == ' ')
        result[j++] = i;

    return result;
  }

  //........................................................................

  //------------------------------- endLine --------------------------------

  /**
   * End the current line of the buffer. If the line is already terminated
   * with a newline, nothing is done, otherwise a newline is added.
   *
   */
  @Override
public void endLine()
  {
    // the line in the buffer is already ended, if it is empty; in that case we
    // don't need to end it again
    if(getLength(m_current.toString()) > 0)
      add('\n');
  }

  //........................................................................

  //------------------------------- toString -------------------------------

  /**
   * Create a string representation of the buffer.
   *
   * @return      the String representing the buffer
   *
   */
  @Override
public @Nonnull String toString()
  {
    StringBuilder result = new StringBuilder("buffer has width " + m_width
                                             + " and alignment " + m_alignment
                                             + " and the following lines:\n");

    // add all the lines
    for(int i = 0; i < m_lines.size(); i++)
    {
      result.append("         ");
      result.append(m_lines.get(i));
      result.append('\n');
    }

    result.append("current: '");
    result.append(m_current);
    result.append("'\n");

    return result.toString();
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- add ------------------------------------------------------------

    /** Test adding of text. */
    @org.junit.Test
    public void add()
    {
      WrapBuffer buffer = new WrapBuffer(20);

      buffer.add("just some text to add to the buffer to see if wrapping"
                 + " actually works, ");
      buffer.setAlignment(Alignment.right);

      assertEquals("right", Alignment.right, buffer.getAlignment());

      buffer.add("of course we can add as much text as we want, "
                 + "including\nnewlines in");
      buffer.add(" the text");
      buffer.add("\n");
      buffer.setAlignment(Alignment.center);

      assertEquals("center", Alignment.center, buffer.getAlignment());

      buffer.add("\n\n");
      buffer.add("how about some centered text, how does this look?\n");
      buffer.setAlignment(Alignment.block);

      assertEquals("block", Alignment.block, buffer.getAlignment());

      buffer.add("last but not least we want to see how text is layouted "
                 + "in block mode, where the lines\nall end at the same "
                 + "borders, left and right\n");
      buffer.setAlignment(Alignment.left);

      assertEquals("left", Alignment.left, buffer.getAlignment());

      buffer.add("ok, now lets finish....\n");
      buffer.add("withsomeverylongtextthatcannotbebrokenataspaceheheheh");

      // some other types to add
      buffer.add(true);
      buffer.add(42.123);
      buffer.add(42.1f);
      buffer.add(42);
      buffer.add(42L);

      String result =
        "just some text to   \n"
        + "add to the buffer to\n"
        + "see if wrapping     \n"
        + "  actually works, of\n"
        + "course we can add as\n"
        + "     much text as we\n"
        + "     want, including\n"
        + "newlines in the text\n"
        + "\n"
        + "\n"
        + "   how about some   \n"
        + " centered text, how \n"
        + "   does this look?  \n"
        + "last  but  not least\n"
        + "we want  to  see how\n"
        + "text is  layouted in\n"
        + "block  mode,   where\n"
        + "the lines           \n"
        + "all end at  the same\n"
        + "borders,  left   and\n"
        + "right               \n"
        + "ok, now lets        \n"
        + "finish....          \n"
        + "withsomeverylongtext\n"
        + "thatcannotbebrokenat\n"
        + "aspacehehehehtrue42.\n"
        + "12342.14242";

      //System.out.println(buffer);
      assertEquals("lines do not match", result, buffer.getLines());
      assertEquals("buffer has width 20 and alignment left and the "
                   + "following lines:\ncurrent: ''\n", buffer.toString());
      assertEquals("", buffer.getLines());
    }

    //......................................................................
    //----- wrap -----------------------------------------------------------

    /** Test wrapping of lines. */
    @org.junit.Test
    public void wrap()
    {
      WrapBuffer buffer = new WrapBuffer(20);
      assertEquals("width", 20, buffer.getWidth());
      assertEquals("new width", 20,
                   ((WrapBuffer)buffer.newBuffer()).getWidth());
      assertEquals("new width", 42,
                   ((WrapBuffer)buffer.newBuffer(42)).getWidth());

      String test = "12345678901234567890";

      buffer.add(test + '\n');
      //System.out.println("'" + buffer.getLine() + "'");
      //System.out.println("'" + buffer.getLine() + "'");
      //System.out.println("'" + buffer.getLine() + "'");
      //System.out.println("'" + buffer.getLines() + "'");
      assertEquals("wrapping not correct", test, buffer.getLine());
      assertNull("superfluous line return", buffer.getLine());

      buffer.add(test);
      buffer.append(test);
      buffer.add('\n');
      buffer.endLine();
      assertEquals("contents",
                   "12345678901234567890\n12345678901234567890\n",
                   buffer.getContents());
      assertTrue("more", buffer.hasMoreCompleteLines());
      assertTrue("more", buffer.hasMore());
      assertEquals("wrapping not correct", test, buffer.getLine());
      assertTrue("more", buffer.hasMoreCompleteLines());
      assertTrue("more", buffer.hasMore());
      assertEquals("wrapping not correct", test, buffer.getLine());
      assertFalse("more", buffer.hasMoreCompleteLines());
      assertFalse("more", buffer.hasMore());
      assertNull("superfluous line return", buffer.getLine());
    }

    //......................................................................
    //----- ignore ---------------------------------------------------------

    /** Test ignoring of character. */
    @org.junit.Test
    public void ignore()
    {
      WrapBuffer buffer = new WrapBuffer(20, "A");

      String test = "1234567890AAAAAA1234567890";

      buffer.add(test + '\n');

      //System.out.println("line '" + buffer.getLine() + "'");
      //System.out.println("line '" + buffer.getLine() + "'");
      //System.out.println("line '" + buffer.getLine() + "'");
      //System.out.println("lines '" + buffer.getLines() + "'");
      assertEquals("wrapping not correct", test, buffer.getLine());
      assertNull("superfluous line return", buffer.getLine());

      test = "12345 67890AAAAAA12345 67890";

      buffer.add(test + '\n');
      //System.out.println("line '" + buffer.getLine() + "'");
      //System.out.println("line '" + buffer.getLine() + "'");
      //System.out.println("line '" + buffer.getLine() + "'");
      //System.out.println("lines '" + buffer.getLines() + "'");
      assertEquals("wrapping not correct", test.substring(0, 22) + "    ",
                   buffer.getLine());
      assertEquals("wrapping not correct",
                   test.substring(23) + "               ",
                   buffer.getLine());
      assertNull("superfluous line return", buffer.getLine());

      test = "1234567890AAAAAA12345678901234567890";
      buffer.add(test + '\n');

      assertEquals("wrapping not correct", test.substring(0, 26),
                   buffer.getLine());
      assertEquals("wrapping not correct", test.substring(26) + "          ",
                   buffer.getLine());
      assertNull("superfluous line return", buffer.getLine());

      // problem with non breakable texts
      test = "12345678901234567";
      buffer.add("AAA" + test + "AAA" + test + "\n");
      assertEquals("wrapping not correct",
                   "AAA" + test + "AAA" + test.substring(0, 3),
                   buffer.getLine());
      assertEquals("wrapping not correct", test.substring(3) + "      ",
                   buffer.getLine());
      assertNull("superfluous line return", buffer.getLine());

      // same with possible breaks
      test = "1 2 3 4 5 6 7 8 9 0 ";
      buffer.add("AAA" + test + "AAA" + test + "\n");
      assertEquals("wrapping not correct", "AAA" + test.substring(0, 19) + " ",
                   buffer.getLine());
      assertEquals("wrapping not correct", "AAA" + test, buffer.getLine());
      assertNull("superfluous line return", buffer.getLine());

      // problem with trailing ignored characters only
      test = "1234567890";
      buffer.add("AAA" + test + "AAA" + test + "AAA\n");
      assertEquals("wrapping not correct", "AAA" + test + "AAA" + test + "AAA",
                   buffer.getLine());
      assertNull("superfluous line return", buffer.getLine());
    }

    //......................................................................
    //----- length ---------------------------------------------------------

    /** Test length computations. */
    @org.junit.Test
    public void length()
    {
      WrapBuffer buffer = new WrapBuffer(20, "a");

      assertEquals("length", 20, buffer.getLength("12345678901234567890"));
      assertEquals("length", 20,
                   buffer.getLength("a1234567890a12aaa345a67a890a"));
      assertEquals("length", 20, buffer.getLength("aaaa12345678901234567890"));
      assertEquals("length", 20, buffer.getLength("12345678901234567890aaaa"));

      assertEquals("length (0)", 0, buffer.getLength(""));
      assertEquals("length (0)", 0, buffer.getLength(null));

      assertEquals("substring", 10,
                   buffer.getSubstringLength("1234567890", 10));
      assertEquals("substring", 19,
                   buffer.getSubstringLength("aaa12a34a567a890aaa", 10));
      assertEquals("substring", 13,
                   buffer.getSubstringLength("1234567890aaa1234567890", 10));
      assertEquals("substring", 21,
                   buffer.getSubstringLength("a1a2a3a4a5a6a7a8a9a0a", 10));
      assertEquals("substring", 22,
                   buffer.getSubstringLength("a12aa34aaa56aaaa78aa901a23aa",
                                             10));
      assertEquals("substring (0)", 0,
                   buffer.getSubstringLength("1234567890", 0));
      assertEquals("substring (0)", 0,
                   buffer.getSubstringLength("1234567890", -5));
      assertEquals("substring (0)", 0, buffer.getSubstringLength(null, -5));
    }

    //......................................................................
  }

  //........................................................................
}
