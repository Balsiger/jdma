/******************************************************************************
 * Copyright (c) 2002-2015 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.base.Optional;
import com.google.common.collect.Iterators;

import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.errors.BaseError;
import net.ixitxachitls.util.logging.Log;

/**
 * This is a class used for parsing streams (mainly files).
 *
 * @file          ParseInputStream.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@NotThreadSafe
public class ParseReader implements AutoCloseable
{
  /**
   * This is an auxiliary class to store the position in the reader.
   */
  @Immutable
  public static class Position
  {
    /**
     * Private constructor, only for the reader itself. Positions can only
     * be obtained through ParseReader.getPosition().
     *
     * @param       inPosition the character position
     * @param       inLine     the line number
     * @param       inBuffer   the back buffer at the position
     */
    protected Position(long inPosition, long inLine, String inBuffer)
    {
      assert inPosition >  0    : "position must be positive";
      assert inLine     >  0    : "line number must be positive";

      m_position = inPosition;
      m_line     = inLine;
      m_buffer   = inBuffer;
    }

    /** The number of characters read so far. */
    private long m_position;

    /** The number of the line currently reading. */
    private long m_line;

    /** The text in the back buffer at the current position. */
    private String m_buffer;

    /**
     * Get the character position of the position.
     *
     * @return      the numbers of characters from the beginning
     */
    public long getPosition()
    {
      return m_position;
    }

    /**
     * Get the line number of the position.
     *
     * @return      the line number of the position
     */
    public long getLine()
    {
      return m_line;
    }

    /**
     * Get the back buffer at the position. This is used to ensure that
     * when going back to a certain position, the back buffer can be
     * restored (the characters in the buffer might not originate from the
     * main text itself, but can be arbitrary).
     *
     * @return      the back buffer at the position
     */
    private String getBuffer()
    {
      return m_buffer;
    }

    @Override
    public String toString()
    {
      return "(pos = " + m_position + ", line = " + m_line + ", back = '"
        + m_buffer + "')";
    }
  }

  /**
   * Create the stream from a String, meaning a file with the corresponding
   * name.
   *
   * @param       inName the name of the file to use for the stream
   *
   * @throws      java.io.FileNotFoundException thrown when the file to be
   *                                            opened cannot be found
   */
  public ParseReader(String inName)
    throws java.io.FileNotFoundException
  {
    open(inName);
  }

  /**
   * Create the parse reader from a given reader and a name of the document
   * thus opened (either the name of the file or some other id).
   *
   * @param       inReader the reader to read from (use a BufferedReader
   *                       for faster input, if required)
   * @param       inName   the name of the document to parse
   */
  public ParseReader(Reader inReader, String inName)
  {
    open(inReader, inName);
  }

  /** The current position in the stream. */
  protected long m_position = 0;

  /** The current line number in the stream. */
  protected long m_newlines = 0;

  /** The name of the current stream. */
  protected Optional<String> m_name;

  /** The stream to read from. */
  protected Optional<Reader> m_buffer;

  /** A flag if currently logging errors or warnings. */
  private boolean m_logErrors = true;

  /** A flag if there was a warning in this stream. */
  private boolean m_warning = false;

  /** A flag if there was an error in this stream. */
  private boolean m_error = false;

  /** All the errors encountered but not yet fetched. */
  private List<BaseError> m_errors = new ArrayList<BaseError>();

  /** The put back buffer to store characters that were put back. */
  protected StringBuilder m_back = new StringBuilder();

  /** All the white spaces. */
  protected static final String s_whites =
    Config.get("resource:parser/white.spaces", " \t\r\n\f");

  /** The word boundaries. */
  protected static final String s_boundaries =
    Config.get("resource:parser/word.boundaries.extension",
               " \t\r\n\f.,;:'\"/?=+-()*&^%$#@!~{}][<>");

  /** Text to print for infinite high or low values. */
  private static final String s_infinity = "Infinity";

  /** The maximal size of a buffer. */
  private static final int s_maxBufferSize =
    Config.get("resource:parser/buffer.size", 5 * 1024 * 1024);

  /**
   * Check if the 'file' is currently open or initialized.
   *
   * @return      true if open, false else
   */
  public boolean isOpen()
  {
    // it's open when buffer and name are set
    return m_buffer != null && m_name != null;
  }

  /**
   * Check if the reader is at the end.
   *
   * @return      true if at the end, false else
   */
  public boolean isAtEnd()
  {
    // if the next character is -1 then we are at the end
    return peek() == -1;
  }

  /**
   * Check if there was an error issued on the reader.
   *
   * @return      true if there was an error, false if not
   */
  public boolean hadError()
  {
    return m_error;
  }

  /**
   * Check if there was a warning issued on the reader.
   *
   * @return      true if there was a warning, false if not
   */
  public boolean hadWarning()
  {
    return m_warning;
  }

  /**
   * Read a character from the reader. White spaces are not returned and are
   * over read.
   *
   * @return      the character read
   * @throws      ReadException raised when no character could be read
   */
  public char readChar() throws ReadException
  {
    // do preprocess (e.g. overread white spaces)
    preprocess();

    // read a single character
    int result = read();

    // do the post processing
    postprocess();

    // check if a character could be read at all
    if(result == -1)
      throw new ReadException("no character found");

    // return the character
    return (char)result;
  }

  /**
   * Read a word from the reader. A word is some characters followed by a
   * white space. The white space is not returned with the word.
   *
   * @return      the next word in the stream
   *
   * @throws      ReadException a word could not be read
   */
  public String readWord() throws ReadException
  {
    return readWord(s_boundaries);
  }

  /**
   * Read a word from the reader. A word is some characters followed by a
   * boundary character. The boundary character is not returned with the word.
   *
   * @param       inBoundaries the word boundaries to use
   *
   * @return      the next word in the stream
   *
   * @throws      ReadException a word could not be read
   */
  public String readWord(String inBoundaries)
    throws ReadException
  {
    // do preprocessing, i.e. white space over reading
    preprocess();

    // read until a boundary character follows
    StringBuilder result = new StringBuilder();

    // no more text, then we are finished
    if(!isAtEnd())
    {
      for(int c = peek(); c > 0; c = peek())
        if(inBoundaries.indexOf((char)c) >= 0)
        {
          // always read at least one character
          if(result.length() == 0)
            result.append((char)read());

          break;
        }
        else
          result.append((char)read());
    }

    // do post processing
    postprocess();

    // check if really a word was read
    if(result.length() == 0)
      throw new ReadException("no string found");

    // return the result
    return result.toString();
  }

  /**
   * Read boolean from the stream. White spaces are over read.
   *
   * @return      the int from the stream (if any)
   *
   * @throws      ReadException raised, when no integer could be read
   */
  public boolean readBoolean() throws ReadException
  {
    // do preprocessing, e.g. over read white spaces
    preprocess();

    if(expect("true"))
      return true;

    if(expect("false"))
      return false;

    throw new ReadException("no boolean found: " + (char)peek());
  }

  /**
   * Read an int from the stream. White spaces are over read.
   *
   * @return      the int from the stream (if any)
   *
   * @throws      ReadException raised, when no integer could be read
   */
  public int readInt() throws ReadException
  {
    // do preprocessing, e.g. over read white spaces
    preprocess();

    // read as long as integer is matched
    StringBuilder result = new StringBuilder();

    for(int c = peek(); c > 0; c = peek())
      if(Character.isDigit((char) c)
         || (result.length() == 0 && ((char)c == '-' || (char)c == '+')))
        result.append((char)read());
      else
        break;

    // do post processing
    postprocess();

    // check if really anything was read
    if(result.length() == 0)
      throw new ReadException("no integer found");

    // remove a leading +, java cannot convert such strings into numbers
    // (don't ask me why)
    if(result.charAt(0) == '+')
      result.deleteCharAt(0);

    // parse the string into an integer
    try
    {
      int value = Integer.parseInt(result.toString());

      return value;
    }
    catch(NumberFormatException e)
    {
      if(result.length() > 0 && result.charAt(0) == '-')
        return Integer.MIN_VALUE;
      else
        return Integer.MAX_VALUE;
    }
  }

  /**
   * Read a long value from the stream. White spaces are over read.
   *
   * @return      the next long value found.
   *
   * @throws      ReadException raised when no long could be read
   */
  public long readLong() throws ReadException
  {
    // do the preprocessing, e.g. white space reading
    preprocess();

    // read the number
    StringBuilder result = new StringBuilder();

    for(int c = peek(); c > 0; c = peek())
      if(Character.isDigit((char)c)
         || (result.length() == 0 && ((char)c == '-' || (char)c == '+')))
        result.append((char)read());
      else
        break;

    // do the post processing
    postprocess();

    // check that really something was read
    if(result.length() == 0)
      throw new ReadException("no integer found");

    // a leading + is removed, java cannot convert otherwise (???)
    if(result.charAt(0) == '+')
      result.deleteCharAt(0);

    // convert the string into a long value
    try
    {
      long value = Long.parseLong(result.toString());

      return value;
    }
    catch(NumberFormatException e)
    {
      if(result.length() == 0)
        return 0;

      if(result.charAt(0) != '-')
        return Long.MAX_VALUE;
      else
        return Long.MIN_VALUE;
    }
  }

  /**
   * Read a float value from the stream, white spaces are over read.
   *
   * @return      the next floating value read from the stream
   *
   * @throws      ReadException raised when a float could not be read
   */
  public float readFloat() throws ReadException
  {
    // do the preprocessing, e.g. white space over reading
    preprocess();

    // check for the special cases of positive or negative infinity
    if(expect(s_infinity))
      return Float.MAX_VALUE;

    if(expect("-" + s_infinity))
      return Float.MIN_VALUE;

    // read characters as long as they match a float
    StringBuilder result = new StringBuilder();

    boolean exp   = false;
    boolean point = false;
    for(int c = peek(); c > 0; c = peek())
      if(Character.isDigit((char)c)
         || ((result.length() == 0 || exp)
             && ((char)c == '-' || (char)c == '+'))
         || ((char)c == '.' && !point)
         || ((char)c == 'e' && !exp)
         || ((char)c == 'E' && !exp))
      {
        point = ((char)c == '.');
        exp   = ((char)c == 'e') || ((char)c == 'E');

        result.append((char)read());
      }
      else
        break;

    // do some post processing
    postprocess();

    // check that really something was read
    if(result.length() == 0)
      throw new ReadException("no float found");

    // convert the result
    try
    {
      float value = Float.parseFloat(result.toString());

      return value;
    }
    catch(NumberFormatException e)
    {
      if(result.charAt(0) == '-')
        return Float.MIN_VALUE;
      else
        return Float.MAX_VALUE;
    }
  }

  /**
   * Read a double value from the stream. White spaces are over read.
   *
   * @return      the next double value found
   *
   * @throws      ReadException raised when no value could be read
   */
  public double readDouble() throws ReadException
  {
    // preprocessing, e.g. over reading white spaces
    preprocess();

    // check for positive or negative infinity
    if(expect("Infinity"))
      return Double.MAX_VALUE;

    if(expect("-Infinity"))
      return Double.MIN_VALUE;

    // read as much characters as possible
    StringBuilder result = new StringBuilder();

    boolean point = false;
    boolean exp   = false;
    for(int c = peek(); c > 0; c = peek())
      if(Character.isDigit((char)c)
         || ((result.length() == 0 || exp)
             && ((char)c == '-' || (char)c == '+'))
         || ((char)c == '.' && !point)
         || ((char)c == 'e' && !exp)
         || ((char)c == 'E' && !exp))
      {
        point = ((char)c == '.');
        exp   = ((char)c == 'e') || ((char)c == 'E');

        result.append((char)read());
      }
      else
        break;

    // do some post processing
    postprocess();

    // check if anything could be read at all
    if(result.length() == 0)
      throw new ReadException("no float found");

    // convert the value
    try
    {
      double value = Double.parseDouble(result.toString());

      return value;
    }
    catch(NumberFormatException e)
    {
      if(result.charAt(0) == '-')
        return Double.MIN_VALUE;
      else
        return Double.MAX_VALUE;
    }
  }

  /**
   * Get the current line number.
   *
   * @return      the current line number
   */
  public long getLineNumber()
  {
    return m_newlines;
  }

  /**
   * Get the name of the currently parsed document.
   *
   * @return      the name of the document
   */
  public Optional<String> getName()
  {
    return m_name;
  }

  /**
   * Get the current position in the stream.
   *
   * @return      the position as Position object
   */
  public Position getPosition()
  {
    return new Position(m_position, m_newlines, m_back.toString());
  }

  /**
   * This is the basic read method, used for reading <STRONG>ANY</STRONG>
   * text from the stream.
   *
   * @return      the read character or -1 if at end of buffer
   */
  public int read()
  {
    // try to read from the back buffer
    if(m_back.length() > 0)
    {
      int result = m_back.charAt(0);

      m_back.deleteCharAt(0);

      return result;
    }

    // if no buffer at all, we are at the end
    if(!m_buffer.isPresent())
      return -1;

    // try to read normally
    int result = -1;
    try
    {
      result = m_buffer.get().read();
    }
    catch(java.io.IOException e)
    {
      Log.error("could not read from '" + m_name + "': " + e);
    }

    // adjust position and newlines
    if(result != -1)
    {
      m_position++;

      if((char)result == '\n')
        m_newlines++;
    }

    return result;
  }

  /**
   * Read all characters up to a special delimiter, this delimiter is
   * <STRONG>NOT</STRONG> read.
   *
   * @param       inDelimiter up to which character to read
   *
   * @return      the characters up to the delimiter (or up to the end)
   */
  public String read(char inDelimiter)
  {
    StringBuilder result = new StringBuilder();

    for(int c = peek(), old = 0; c != -1; old = c, c = peek())
      if((char)c == inDelimiter && (char)old != '\\')
        return result.toString();
      else
        result.append((char)read());

    return result.toString();
  }

  /**
   * Read characters up to one of the given delimiters. The delimiter is
   * <STRONG>NOT</STRONG> read.
   *
   * @param       inDelimiters the delimiters to read up to
   *
   * @return      the text read
   */
  public String read(String inDelimiters)
  {
    return read(inDelimiters, Optional.<String>absent());
  }

  /**
   * Read characters up to one of the given delimiters. The delimiter is
   * <STRONG>NOT</STRONG> read.
   *
   * @param       inDelimiters      the delimiters to read up to
   * @param       inSpaceDelimiters the delimiters to read up to if they have a
   *                                trailing space
   *
   * @return      the text read
   */
  public String read(String inDelimiters, Optional<String> inSpaceDelimiters)
  {
    StringBuilder result = new StringBuilder();

    boolean empty = true;
    for(int c = peek(), old = -1; c != -1; old = c, c = peek())
      if((inDelimiters.indexOf((char)c) >= 0 && (char)old != '\\')
         || (inSpaceDelimiters.isPresent()
             && inSpaceDelimiters.get().indexOf((char)c) >= 0
             && Character.isSpaceChar((char)old)
             && !empty))
        return result.toString();
      else
      {
        char character = (char)read();
        if(empty && !Character.isWhitespace(character))
          empty = false;

        result.append(character);
      }

    return result.toString();
  }

  /**
   * Read the given number of characters.
   *
   * @param       inMax the maximal number of characters to read, but always
   *                    at least one (if there is one)
   *
   * @return      the characters read
   */
  public String read(long inMax)
  {
    StringBuilder result = new StringBuilder();

    int c;
    int i;
    for(c = read(), i = 0; c != -1 && i < inMax - 1; c = read(), i++)
      result.append((char)c);

    if(c != -1)
      result.append((char)c);

    return result.toString();
  }

  /**
   * Read a complete line from the stream. The end of line character is not
   * returned!
   *
   * @return      the complete line
   */
  public String readLine()
  {
    StringBuilder line = new StringBuilder();
    for(int c = read(); c != -1; c = read())
    {
      // we need to handle all 'possible' end of line separators (burn this
      // windoooooooze *sigh*)
      // this will interpret \n\r or \r\n as a single newline, on all systems!
      if(c == '\n' || c == '\r')
      {
        char last = (char)-1;

        if(line.length() > 0)
          last = line.charAt(line.length() - 1);

        if((last == '\n' || last == '\r') && last != c)
          line.deleteCharAt(line.length() - 1);
        else
        {
          int next = peek();

          if((next == '\n' || next == '\r') && next != (char)c)
            read();
        }

        return line.toString();
      }
      else
        line.append((char)c);
    }

    return line.toString();
  }

  /**
   * Peek at the next character in the stream without actually reading it.
   *
   * @return      the next character or -1 if no more characters follow
   */
  public int peek()
  {
    // check if the back buffer has some data
    if(m_back.length() > 0)
      return m_back.charAt(0);

    // if we don't have a buffer at all, then we are at its end
    if(m_buffer == null)
      return -1;

    // read a normal character
    int result = read();

    if(result != -1)
      put((char)result);

    return result;
  }

  /**
   * Ignore all characters up to the given one. The delimiter is ignored
   * as well.
   *
   * @param       inDelimiter the delimiter to ignore to
   *
   * @return      the delimiter ignored, or -1 if at end of stream
   */
  public char ignore(char inDelimiter)
  {
    for(int c = read(); c != -1; c = read())
      if((char)c == inDelimiter)
        return inDelimiter;

    return (char)-1;
  }

  /**
   * Ignore all characters up to and including one of the delimiters given
   * as a String.
   *
   * @param       inDelimiters all the possible delimiters to read to
   *
   * @return      the character actually encountered as delimiter, or -1
   *              if none was found
   */
  public char ignore(String inDelimiters)
  {
    for(int c = read(); c != -1; c = read())
      if(inDelimiters.indexOf((char)c) >= 0)
        return (char)c;

    return (char)-1;
  }

  /**
   * Expect a specific text in the reader, check (and over read) that
   * the given text follows in the stream. White spaces in the text to
   * expected and in the stream are compacted.
   *
   * @param       inText the text to check for
   *
   * @return      true if text was found and read, false otherwise
   */
  public boolean expect(String inText)
  {
    return expectCase(inText, false);
  }

  /**
   * Check for and over read one of many strings given. Again, white
   * spaces in the text and in the stream are compacted.
   *
   * @param       inTexts the texts to try to find
   *
   * @return      the number of the text found, or -1 if none was found
   */
  public int expect(String []inTexts)
  {
    return expectCase(inTexts, false);
  }

  /**
   * Check for and over read one of many string arrays given. Again, white
   * spaces in the text and in the stream are compacted. This method
   * searches for a matching string in any of the given arrays, treating
   * all strings in the same array as similar, thus only returning the
   * string array in which the text was found, if any.
   *
   * @param       inTexts the texts to try to find
   *
   * @return      the number of the array found in, or -1 if none was found
   */
  public int expect(String [][]inTexts)
  {
    return expectCase(inTexts, false);
  }

  /**
   * Check for and over read one of the objects given in the Iterator.
   * Again, white spaces in the text and in the stream are compacted.
   *
   * @param       <T>     the type of objects expected and returned
   * @param       inTexts the texts to try to find
   *
   * @return      the object found, or null if none was found
   *
   */
  public <T> Optional<T> expect(Iterator<T> inTexts)
  {
    return expectCase(inTexts, false);
  }

  /**
   * Expect a single character from the stream. White spaces are over read.
   *
   * @param       inExpected the character to look for
   *
   * @return      true if the character was found, false else
   */
  public boolean expect(char inExpected)
  {
    return expectCase(inExpected, false);
  }

  /**
   * Expect a specific text in the reader, check (and over read) that
   * the given text follows in the stream. White spaces in the text to
   * expected and in the stream are compacted.
   *
   * @param       inText the text to check for
   * @param       inIgnoreCase a flag if casing should be ignored or not
   *
   * @return      true if text was found and read, false otherwise
   */
  public boolean expectCase(String inText, boolean inIgnoreCase)
  {
    if(inText.length() == 0)
      return false;

    String text = inText.trim();

    // store the old position for later reset
    Position pos = getPosition();

    // handle the special case that the input text contains white space only
    if(text.length() == 0)
    {
      // check for the exact match
      for(int i = 0; i < inText.length(); i++)
        if(inText.charAt(i) != (char)read())
        {
          seek(pos);

          return false;
        }

      return true;
    }

    // reduce white spaces in the text to look for
    String []expected = text.split("[" + s_whites + "]+");

    for(int i = 0; i < expected.length; i++)
    {
      // skip white spaces between words
      skipWhites();

      for(int j = 0; j < expected[i].length(); j++)
      {
        char found    = (char)read();
        char required = expected[i].charAt(j);

        if(inIgnoreCase)
        {
          found    = Character.toUpperCase(found);
          required = Character.toUpperCase(required);
        }

        if(found != required)
        {
          seek(pos);

          return false;
        }
      }

      // if the following characters is a character or digit, then we
      // should have read more and the string does not match, unless the
      // last expected character is not a letter or digit
      if(Character.isLetterOrDigit(expected[i].charAt(expected[i].length()
                                                      - 1))
         && Character.isLetterOrDigit((char)peek()))
      {
        seek(pos);

        return false;
      }
    }

    // return result
    return true;
  }

  /**
   * Check for and over read one of many strings given. Again, white
   * spaces in the text and in the stream are compacted.
   *
   * @param       inTexts      the texts to try to find
   * @param       inIgnoreCase a flag denoting if case should be ignored
   *
   * @return      the number of the text found, or -1 if none was found
   */
  public int expectCase(String []inTexts, boolean inIgnoreCase)
  {
    for(int i = 0; i < inTexts.length; i++)
      if(expectCase(inTexts[i], inIgnoreCase))
        return i;

    return -1;
  }

  /**
   * Check for and over read one of many string arrays given. Again, white
   * spaces in the text and in the stream are compacted. This method
   * searches for a matching string in any of the given arrays, treating
   * all strings in the same array as similar, thus only returning the
   * string array in which the text was found, if any.
   *
   * @param       inTexts      the texts to try to find
   * @param       inIgnoreCase a flag denoting if case should be ignored or
   *                           not
   *
   * @return      the number of the array found in, or -1 if none was found
   */
  public int expectCase(String [][]inTexts, boolean inIgnoreCase)
  {
    for(int i = 0; i < inTexts.length; i++)
    {
      if(inTexts[i] != null && inTexts[i].length > 0)
      {
        int result = expectCase(inTexts[i], inIgnoreCase);

        if(result >= 0)
          return i;
      }
    }

    return -1;
  }

  /**
   * Check for and over read one of the objects given in the Iterator.
   * Again, white spaces in the text and in the stream are compacted.
   *
   * @param       <T>          the type of objects expected and returned
   * @param       inTexts      the texts to try to find
   * @param       inIgnoreCase a flag if casing should be ignored or not
   *
   * @return      the object found, or null if none was found
   */
  public <T> Optional<T> expectCase(Iterator<T> inTexts, boolean inIgnoreCase)
  {
    while(inTexts.hasNext())
    {
      T element = inTexts.next();

      if(expectCase(element.toString(), inIgnoreCase))
        return Optional.of(element);
    }

    return Optional.absent();
  }

  /**
   * Check for and over read one of the objects given in the Iterator.
   * Again, white spaces in the text and in the stream are compacted.
   *
   * @param       <T>          the type of objects expected and returned
   * @param       inTexts      the texts to try to find
   * @param       inIgnoreCase a flag if casing should be ignored or not
   *
   * @return      the object found, or null if none was found
   */
  public <T> Optional<T> expectCase(Iterable<T> inTexts,
                                    boolean inIgnoreCase)
  {
    for(T element : inTexts)
      if(expectCase(element.toString(), inIgnoreCase))
        return Optional.of(element);

    return Optional.absent();
  }

  /**
   * Expect a single character from the stream. White spaces are over read.
   *
   * @param       inExpected   the character to look for
   * @param       inIgnoreCase a flag if casing should be ignored or not
   *
   * @return      true if the character was found, false else
   */
  public boolean expectCase(char inExpected, boolean inIgnoreCase)
  {
    for(int c = peek(); c != -1; read(), c = peek())
    {
      boolean check;
      if(inIgnoreCase)
        check =
          Character.toUpperCase((char)c) == Character.toUpperCase(inExpected);
      else
        check = c == inExpected;

      if(check)
      {
        read();

        return true;
      }

      if(s_whites.indexOf((char)c) == -1)
        return false;
    }

    return false;
  }

  /**
   * Create an error from the given stream at the given position.
   *
   * @param       inPosition    the position where the error occured
   * @param       inErrorNumber the number or ID of the error
   * @param       inText        some additional, context specific message
   *
   * @return      the error containing all information about the error
   */
  public ParseError error(Position inPosition, String inErrorNumber,
                          Optional<String> inText)
  {
    // save the starting position to put back later
    Position current = getPosition();

    // determine the characters to read before the error position
    long chars = Math.min(inPosition.getPosition() - 1,
                          Config.get("resource:parser/error.precharacters",
                                     100));

    // determine the position where to start
    long pos = Math.max(1, inPosition.getPosition()
                        - Config.get("resource:parser/error.precharacters",
                                     100));

    // go the start to read characters
    seek(new Position(pos, 1, ""));

    // read the characters before the position
    String pre = "";

    // only read characters if we have to (reading 0 characters produces
    // a 1 charcter string)
    if(chars > 0)
      pre = read(chars);

    // go back to the error position
    seek(inPosition);

    // read the characters after the position
//     String post =
//       inPosition.getBuffer().substring(0,
//                                        Math.min(200,
//                                                 inPosition.getBuffer()
//                                                 .length()));

    String post =
      read(Config.get("resource:parser/error.postcharacters", 200))
      .substring(inPosition.getBuffer().length());


    // reset the position to the starting position
    seek(current);

    // create and return the Error
    return new ParseError(inErrorNumber, inText, inPosition.getLine(),
                          m_name, Optional.of(pre), Optional.of(post));
  }

  /**
   * Log an parsing error to the log file.
   *
   * @param       inPosition     the position where the error occured
   * @param       inErrorNumber the number of ID of the error
   * @param       inText        an context specific error text
   */
  public void logError(Position inPosition, String inErrorNumber,
                       Optional<String> inText)
  {
    if(!m_logErrors)
      return;

    m_error = true;

    ParseError error = error(inPosition, inErrorNumber, inText);
    m_errors.add(error);
    Log.error(error);
  }

  /**
   * Log an parsing warning to the log file.
   *
   * @param       inPosition     the position where the error occured
   * @param       inErrorNumber the number of ID of the error
   * @param       inText        an context specific error text
   */
  public void logWarning(Position inPosition, String inErrorNumber,
                         Optional<String> inText)
  {
    if(!m_logErrors)
      return;

    m_warning = true;

    ParseError error = error(inPosition, inErrorNumber, inText);
    m_errors.add(error);
    Log.warning(error);
  }

  /**
   * Fetch and clear all errors encountred.
   *
   * @return all the errors since the last fetch.
   */
  public List<BaseError> fetchErrors()
  {
    List<BaseError> errors = m_errors;
    m_errors = new ArrayList<BaseError>();

    return errors;
  }

  /**
   * Open the reader to a new file with the given file name.
   *
   * @param       inName the name of the file to open (including path)
   *
   * @throws      java.io.FileNotFoundException if the file could not be found
   */
  public void open(String inName) throws java.io.FileNotFoundException
  {
    // determine the input buffer size (add 1 in case the buffer is empty)
    int size = Math.min((int)(new File(inName)).length(), s_maxBufferSize) + 1;

    // $codepro.audit.disable closeWhereCreated
    open(new BufferedReader(new FileReader(inName), size), inName);
    // $codepro.audit.enable
  }

  /**
   * The reader must be marked at the beginning for some classes
   * (e.g. BufferedReader)
   *
   * @param       inReader the reader to read from (buffered is possible)
   * @param       inName   the name of the buffer read
   */
  public void open(Reader inReader, String inName)
  {
    // check if already open
    if(isOpen())
      close(); // $codepro.audit.disable closeInFinally

    // store the given values
    m_buffer = Optional.of(inReader);
    m_name = Optional.of(inName);

    // reset the position
    m_position = 1;
    m_newlines = 1;

    // mark the stream
    try
    {
      m_buffer.get().mark(s_maxBufferSize);
    }
    catch(java.io.IOException e)
    {
      Log.error("could not mark input buffer: " + e
                + ", not all input operations may be available");
    }
  }

  @Override
  public void close()
  {
    try
    {
      if(m_buffer.isPresent())
        m_buffer.get().close(); // $codepro.audit.disable closeInFinally
    }
    catch(java.io.IOException e)
    {
      Log.error("Could not close buffer '" + m_name + "': " + e);
    }

    m_buffer = Optional.absent();
    m_name = Optional.absent();
  }

  /**
   * Put a character back into the stream to be read next.
   *
   * @param       inChar the character to put back
   */
  public void put(char inChar)
  {
    m_back.insert(0, inChar);
  }

  /**
   * Put a String back into the stream to be read next.
   *
   * @param       inString the String to put back
   */
  public void put(String inString)
  {
    if(inString.length() == 0)
      return;

    m_back.insert(0, inString);
  }

  /**
   * Put a integer back into the stream to be read next.
   *
   * @param       inInteger the integer to put back
   */
  public void put(int inInteger)
  {
    m_back.insert(0, inInteger);
  }

  /**
   * Put a long value back into the stream to be read next.
   *
   * @param       inLong the long value to put back
   */
  public void put(long inLong)
  {
    m_back.insert(0, inLong);
  }

  /**
   * Put a float back into the stream to be read next.
   *
   * @param       inFloat the float to put back
   */
  public void put(float inFloat)
  {
    m_back.insert(0, inFloat);
  }

  /**
   * Put a double back into the stream to be read next.
   *
   * @param       inDouble the double to put back
   */
  public void put(double inDouble)
  {
    m_back.insert(0, inDouble);
  }

  /**
   * Put a boolean back into the stream to be read next.
   *
   * @param       inBoolean the boolean to put back
   */
  public void put(boolean inBoolean)
  {
    m_back.insert(0, inBoolean);
  }

  /**
   * Skip all white spaces following the current position.
   *
   * @return      the white spaces skipped
   */
  protected String skipWhites()
  {
    StringBuilder result = new StringBuilder();

    for(int c = peek(); c != -1; c = peek())
      if(s_whites.indexOf((char)c) < 0)
        break;
      else
        result.append((char)read());

    return result.toString();
  }

  /**
   * Go to a specific, preliminary obtained (through getPosition()),
   * position in the stream.
   *
   * @param       inPosition the position to go to
   */
  public void seek(Position inPosition)
  {
    // set the internal values
    m_position = inPosition.getPosition();
    m_newlines = inPosition.getLine();
    m_back     = new StringBuilder(inPosition.getBuffer());

    // go to the buffer position
    try
    {
      if(m_buffer.isPresent())
      {
        m_buffer.get().reset();
        if(m_buffer.get().skip(m_position - 1) != m_position - 1)
          Log.warning("could not skip the correct amount of characters");
      }
    }
    catch(java.io.IOException e)
    {
      Log.error("could not rewind buffer " + m_name + ": " + e);
    }
  }

  /**
   * Create a string representing the current status of the reader.
   *
   * @return      the created String
   */
  @Override
  public String toString()
  {
    Position pos = getPosition();

    String current = Strings.trim(read(60));

    seek(pos);

    return (m_name.isPresent() ? m_name.get() : "(no name)")
        + " is on line " + m_newlines + ":\n" + current + '\n'
        + "back: '" + m_back + "'\n";
  }

  /**
   * Set if errors are currently logged or not.
   *
   * @param       inLogErrors true for logging errors, false for not logging
   *                          them
   */
  public void setLogErrors(boolean inLogErrors)
  {
    m_logErrors = inLogErrors;
  }

  /** This is the preprocessing step done before any reading operation. */
  protected void preprocess()
  {
    skipWhites();
  }

  /** Do the processing steps after a reading operation. */
  protected void postprocess()
  {
    // nothing done currently
  }

  //----------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //     static { Log.add("ansi",
    //                      new net.ixitxachitls.util.logging.ANSILogger()); }

    /** The number of characters to read maximally when testing. */
    private static final int s_maxRead = 1000;

    /** Test of initialization. */
    @org.junit.Test
    public void init()
    {
      try (ParseReader reader = new ParseReader("guru.guru"))
      {
        assertFalse(reader.isOpen());

        fail("file not found but still continued");
      }
      catch(java.io.FileNotFoundException e)
      { /* nothing to do */ }

      try (ParseReader reader =
        new ParseReader(new java.io.StringReader(""), "test"))
      {
        assertEquals("test", reader.getName().get());
        assertEquals(true, reader.isOpen());
        assertEquals(true, reader.isAtEnd());
        assertEquals(true, reader.isAtEnd());
        assertEquals(-1, reader.read());
      }
    }

    /** Test reading. */
    @org.junit.Test
    public void read()
    {
      String text = "a Word +1234 -10 12345678901234567890 0"
        + "-98765432109876543210 -5.6Infinity.5-010-10e23   -99.99e123456"
        + "  +123e123 -10e-33333 true false a test + another \\- test -";

      try (ParseReader reader =
        new ParseReader(new java.io.StringReader(text), "test"))
      {
        try
        {
          assertEquals("char", 'a', reader.readChar());
          assertEquals("word", "Word", reader.readWord());
          assertEquals("integer", 1234, reader.readInt());
          assertEquals("negative integer", -10, reader.readInt());
          assertEquals("max integer", Integer.MAX_VALUE, reader.readInt());
          assertEquals("long", 0, reader.readLong());
          assertEquals("min long", Long.MIN_VALUE, reader.readLong());
          assertEquals("negative float", -5.6, reader.readFloat(), 0.0001);
          assertEquals("max float", Float.MAX_VALUE, reader.readFloat(), 0);
          assertEquals("float", 0.5, reader.readFloat(), 0);
          assertEquals("negative float again", -10.0, reader.readFloat(), 0);
          assertEquals("big negative flooat", -10e23, reader.readFloat(),
                       0.0001e24);
          assertEquals("negative infinity float", Float.NEGATIVE_INFINITY,
                       reader.readFloat(), 0);
          assertEquals("double", +123e123, reader.readDouble(), 0.001e123);
          assertEquals("double null", 0, reader.readDouble(), 0);
          assertTrue("boolean", reader.readBoolean());
          assertFalse("boolean", reader.readBoolean());
          assertEquals("delim", reader.read("+"), " a test ");
          assertEquals("delim with escape", reader.read("-"),
                       "+ another \\- test ");
        }
        catch(ReadException e)
        {
          fail("reading should not have failed: " + e);
        }

        try
        {
          reader.readChar();
          reader.readWord();
          reader.readInt();
          reader.readLong();
          reader.readFloat();

          fail("reading should have failed");
        }
        catch(ReadException e)
        { /* nothing to do */ }
      }
    }

    /** Test positioning in a file. */
    @org.junit.Test
    public void positioning()
    {
      String text = "\n\n\n\n\n   a\n\n\nguru \n\n\n";

      try (ParseReader reader =
        new ParseReader(new java.io.StringReader(text), "test"))
      {
        try
        {
          assertEquals(1, reader.getLineNumber());
          reader.readChar();
          assertEquals(6, reader.getLineNumber());
          assertEquals(6, reader.getPosition().getLine());
          assertEquals(10, reader.getPosition().getPosition());
          assertEquals("", reader.getPosition().getBuffer());
          reader.expect("hmm");
          assertEquals(6, reader.getLineNumber());
          reader.expect("guru");
          assertEquals(9, reader.getLineNumber());
          assertEquals(9, reader.getPosition().getLine());
          assertEquals(18, reader.getPosition().getPosition());
          assertEquals(" ", reader.getPosition().getBuffer());
          assertEquals("pos", "(pos = 18, line = 9, back = ' ')",
                       reader.getPosition().toString());
        }
        catch(ReadException e)
        {
          fail("reading should not have failed");
        }

        try
        {
          reader.readChar();
        }
        catch(ReadException e)
        { /* nothing to do */ }

        assertEquals(12, reader.getLineNumber());
      }
    }

    /** Test with basic reading. */
    @org.junit.Test
    public void basicRead()
    {
      String text =
        "1234567\n   \nhello$1234567890 and the line end\n";

      try (ParseReader reader =
        new ParseReader(new java.io.StringReader(text), "test"))
      {
        assertEquals('1',        reader.read());
        assertEquals("2345",          reader.read('6'));
        assertEquals('6',        reader.read());
        assertEquals("7\n   \nhello", reader.read("^&%$#@"));
        assertEquals('$',        reader.read());
        assertEquals("123456",        reader.read(6));
        assertEquals("7890 and the line end", reader.readLine());
      }
    }

    /** Text ignoring text. */
    @org.junit.Test
    public void ignore()
    {
      String text = "1234567\n   \nhello$1234567890 and the line end\n";

      try (ParseReader reader =
        new ParseReader(new java.io.StringReader(text), "test"))
      {
        assertEquals('\n', reader.ignore('\n'));
        assertEquals('$',  reader.ignore("!+_$%#@^"));
        assertEquals((char)-1,   reader.ignore('q'));
        assertEquals((char)-1,   reader.ignore("d"));
      }
    }

    /** Testing expecting of text. */
    @org.junit.Test
    public void expect()
    {
      String text = "\n\n just some        test\n\n another text tests!test 2"
        + " b2 word guru ";

      try (ParseReader reader =
        new ParseReader(new java.io.StringReader(text), "test"))
      {
        assertTrue(reader.expect("just some test"));
        assertTrue(reader.expect("another text"));
        assertFalse(reader.expect("test"));
        assertTrue(reader.expect("tests"));
        assertTrue(reader.expect('!'));
        assertEquals(1, reader.expect(new String[]{ "test 1",
                                                    "test 2", "test 3" }));
        assertEquals(1, reader.expect(new String [][]
          {
            { "a1", "a2", "a3", },
            { "b1", "b2", "b3", },
            { "c1", "c2", "c3", },
          }));

        assertFalse(reader.expect("a"));
        assertFalse(reader.expect('a'));
        assertEquals(-1,    reader.expect(new String[]{"a", "b", "c"}));
        assertEquals("word",
                     reader.expect(Iterators.forArray("one", "two", "tree",
                                                      "word", "four")).get());

        // try expecting white spaces
        assertTrue("space", reader.expect("guru"));
        assertEquals("space", ' ', reader.peek());
        assertTrue("space", reader.expect(" "));
        assertFalse("space", reader.expect(" "));
      }
    }

    /** Testing open and close. */
    @org.junit.Test
    public void openClose()
    {
      try (ParseReader reader = new ParseReader("guru.1"))
      {
        fail("file should not have been opened");
      }
      catch(java.io.FileNotFoundException e)
      { /* nothing to do */ }
    }

    /** Testing error handling. */
    @org.junit.Test
    public void error()
    {
      String text = "\njust some test text\n\n with # an \nerror position";

      try (ParseReader reader =
        new ParseReader(new java.io.StringReader(text), "test"))
      {
        reader.read('#');
        Position pos = reader.getPosition();

        ParseError error = reader.error(pos, "test",
                                        Optional.of("just some text"));

        assertEquals("test", error.getErrorNumber());
        assertEquals("just some text", error.getParseMessage().get());
        assertEquals("[test] no definition found for this error",
                     error.getError());
        assertEquals("test", error.getDocument().get());
        assertEquals(4, error.getLine());
        assertEquals("\njust some test text\n\n with #", error.getPre().get());
        assertEquals(" an \nerror position", error.getPost().get());
        assertFalse("error", reader.hadError());
        assertFalse("warning", reader.hadWarning());

        m_logger.addExpected
          ("ERROR: test: [test] no definition found for this "
            + "error (just some text) "
            + "on line 4 in document 'test'\n...\njust some "
            + "test text\n\n with #>>> an \nerror position...");

        reader.logError(pos, "test", Optional.of("just some text"));

        m_logger.addExpected
        ("WARNING: test: [test] no definition found for this "
          + "error (some other text) "
          + "on line 4 in document 'test'\n...\njust some "
          + "test text\n\n with #>>> an \nerror position...");

        reader.logWarning(pos, "test", Optional.of("some other text"));


        m_logger.verify();
      }
    }

    /** Test for putting back characters. */
    @org.junit.Test
    public void put()
    {
      String text = "some text to read";

      try (ParseReader reader =
       new ParseReader(new java.io.StringReader(text), "test"))
      {
        reader.put('a');
        assertEquals("char", 'a',     reader.readChar());
        assertEquals("word", "some",  reader.readWord());
        assertEquals("whites", " ", reader.skipWhites());
        reader.put('#');
        assertEquals("#", reader.readWord());
        reader.put(' ');
        reader.put(true);
        assertEquals("boolean", true, reader.readBoolean());
        reader.put(1.2d);
        reader.put(42);
        reader.put(42L);
        assertEquals("int", 42421.2, reader.readDouble(), 0.001);
        reader.put(4.2f);
        assertEquals("int", 4.2, reader.readFloat(), 0.001);
        reader.put("guru guru guru");
        assertEquals("expecting", true,
                     reader.expect("guru guru guru text to"));
      }
      catch(ReadException e)
      {
        fail("put test should not have failed: " + e);
      }
    }

    /** Miscellaneous test. */
    @org.junit.Test
    public void misc()
    {
      String text = "some \n\n     text \n to \n   read";

      try (ParseReader reader =
       new ParseReader(new java.io.StringReader(text), "test"))
      {
        reader.readWord();
        Position pos = reader.getPosition();

        reader.skipWhites();
        assertEquals('t', reader.readChar());

        assertEquals("test is on line 3:\next to read\nback: ''\n",
                     reader.toString());
        reader.seek(pos);
        assertEquals("text to read", Strings.trim(reader.read(s_maxRead)));
      }
      catch(ReadException e)
      {
        fail("misc test should not have failed");
      }
    }

    /** Test for file reading. */
    @org.junit.Test
    public void file()
    {
      try
      {
        File temp = File.createTempFile("test", "file");

        try (java.io.FileWriter writer = new java.io.FileWriter(temp);
          ParseReader reader = new ParseReader(temp.getPath()))
        {
          writer.write("<?xml version=\"1.0\" ?>\n\n<!-- ");
          writer.flush();

          assertEquals("text not correctly read", "<?xml version=\"1.0\" ?>",
                       reader.readLine());
          reader.ignore('\n');
          assertEquals("not correctly expected", true, reader.expect("<!--"));

        }
        catch(java.io.FileNotFoundException e)
        {
          fail("file should be found");
        }
        finally
        {
          assertTrue(temp.delete());
        }
      }
      catch(java.io.IOException e)
      {
        fail("temp file should be written");
      }
    }

    /** Test for empty files. */
    @org.junit.Test
    public void empty()
    {
      File file = null;
      ParseReader reader = null;
      try
      {
        file = File.createTempFile("test", ".guru");
        reader = new ParseReader(file.getPath());

        reader.readChar();
        fail("reading should have failed");
      }
      catch(ReadException e)
      { /* nothing to do */ }
      catch(java.io.FileNotFoundException e)
      { /* nothing to do */ }
      catch(java.io.IOException e)
      { /* nothing to do */ }
      finally
      {
        if (reader != null)
        {
          reader.close();
        }
      }
      if(file != null)
        assertTrue(file.delete());
    }
  }
}
