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

package net.ixitxachitls.util.logging;

import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import net.ixitxachitls.output.WrapBuffer;
import net.ixitxachitls.util.Strings;

import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the logger to print ASCII messages.
 *
 * @file          ASCIILogger.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @example       <PRE>
 * // add the logger to write to
 * Log.add("test", new ASCIILogger());
 *
 * // set the logging level
 * Log.setLevel(Log.Type.DEBUG);
 *
 * // print various log messages
 * Log.fatal("This is a fatal error message");
 * Log.error("This is an error message");
 * Log.warning("This is a warning message");
 * Log.necessary("This is a necessary message");
 * Log.important("this is an important message");
 * Log.useful("This is a useful message");
 * Log.info("This is an information message");
 * Log.complete("This is a complete message");
 * Log.debug("This is a debug message");
 * </PRE>
 *
 */

//..........................................................................

//__________________________________________________________________________

@ThreadSafe
public class ASCIILogger implements Logger
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- ASCIILogger ------------------------------

  /**
   * Create the logger.
   *
   */
  public ASCIILogger()
  {
    this(System.out, def_format, def_width);
  }

  //........................................................................
  //----------------------------- ASCIILogger ------------------------------

  /**
   * Create the logger.
   *
   * @param       inStream the stream to print to
   *
   */
  public ASCIILogger(@Nonnull OutputStream inStream)
  {
    this(inStream, def_format, def_width);
  }

  //........................................................................
  //----------------------------- ASCIILogger ------------------------------

  /**
   * Create the logger.
   *
   * @param       inFormat the format used for printing
   *
   */
  public ASCIILogger(String inFormat)
  {
    this(System.out, inFormat, def_width);
  }

  //........................................................................
  //----------------------------- ASCIILogger ------------------------------

  /**
   * Create the logger.
   *
   * @param       inStream the stream to print to
   * @param       inFormat the format used for printing
   * @param       inWidth  the width to print to (or negative for standard
   *                       width)
   *
   */
  public ASCIILogger(@Nonnull OutputStream inStream, @Nonnull String inFormat,
                     int inWidth)
  {
    m_out    = inStream;
    m_format = inFormat;

    if(inWidth > 0)
      m_width  = inWidth;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Where to print to, defaults to System.out. */
  protected @Nonnull OutputStream m_out;

  /** The format to use for printing. */
  private @Nonnull String m_format;

  /** The default format for printing. */
  protected static final String def_format =
    Config.get("logger.format", "%<%Y-%M-%D %h:%m:%s - %L: %>%T");

  /** The standard width of the output buffer. */
  private static final int def_width =
    Config.get("logger.width", 80);

  /** The current width for printing. */
  private int m_width = def_width;

  /** The default indent to use if a message covers more than one line. */
  private static final int s_indent =
    Config.get("logger.indent", 20);

  /** The maximally allowed percentage of a line that may be prompt. */
  private static final double s_maxPrompt = 0.9;

  /** The maximum length of the level. */
  private static final int s_maxLevelLength = 9;

  /** A decade for computing current year. */
  private static final int s_decade = 100;

  /** The number of digits to use for a year. */
  private static final int s_yearDigits = 4;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //---------------------------- getWrapBuffer -----------------------------

  /**
   * Get a wrap buffer for the given width. This method is mainly used
   * to allow a derivation to add characters to be ignored for wrapping.
   *
   * @param       inWidth the width of the buffer to create
   *
   * @return      the wrap buffer created
   *
   */
  protected @Nonnull WrapBuffer getWrapBuffer(int inWidth)
  {
    return new WrapBuffer(inWidth);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //-------------------------------- print ---------------------------------

  /**
   * Print the given message.
   *
   * @param       inText the text to print
   * @param       inType the level of detail to print for
   *
   */
  public void print(@Nonnull String inText, Log.Type inType)
  {
    // set the text to write to together
    String text = format(inText,  inType, m_format) + '\n';

    try
    {
      m_out.write(text.getBytes());
      m_out.flush();
    }
    catch(java.io.IOException e)
    {
      System.err.println("cannot print log message: " + e);
    }
  }

  //........................................................................
  //-------------------------------- print ---------------------------------

  /**
   * Print the given object.
   *
   * @param       inObject the object to print
   * @param       inType   the type of object printed (logging level)
   *
   */
  public void print(@Nonnull Object inObject, Log.Type inType)
  {
    print(inObject.toString(), inType);
  }

  //........................................................................

  //-------------------------------- format --------------------------------

  /**
   * Format the message for printing.
   *
   * @param       inMessage the message to print
   * @param       inType    the type of the message
   * @param       inFormat  the format of the message to write
   *
   * @return      the formatted string
   *
   */
  protected String format(@Nullable String inMessage,
                          @Nullable Log.Type inType,
                          @Nonnull String inFormat)
  {
    return format(inMessage, inType, inFormat, new GregorianCalendar());
  }

  /**
   * Format the message for printing.
   *
   * Use this method only for testing, otherwise use the simple format().
   *
   *
   * @param       inMessage the message to print
   * @param       inType    the type of the message
   * @param       inFormat  the format of the message to write
   * @param       inCurrent the calendar for the current date/time (used
   *                        mainly for testing)
   *
   * @return      the formatted string
   *
   */
  private String format(@Nullable String inMessage, @Nullable Log.Type inType,
                        @Nonnull String inFormat, @Nonnull Calendar inCurrent)
  {
    // replace the appropriate texts in the given string
    String result = inFormat;
    result = result.replaceAll("%Y", Strings.pad(inCurrent.get(Calendar.YEAR),
                                                 s_yearDigits, true));
    result = result.replaceAll("%y", Strings.pad(inCurrent.get(Calendar.YEAR)
                                                 % s_decade, 2, true));
    result = result.replaceAll("%M", Strings.pad(inCurrent.get(Calendar.MONTH)
                                                 + 1, 2, true));
    result =
      result.replaceAll("%D",
                        Strings.pad(inCurrent.get(Calendar.DAY_OF_MONTH),
                                    2, true));
    result = result.replaceAll("%h",
                               Strings.pad(inCurrent.get(Calendar.HOUR_OF_DAY),
                                           2, true));
    result = result.replaceAll("%m",
                               Strings.pad(inCurrent.get(Calendar.MINUTE),
                                           2, true));
    result = result.replaceAll("%s",
                               Strings.pad(inCurrent.get(Calendar.SECOND),
                                           2, true));

    if(inType != null)
      result = result.replaceAll("%L", Strings.pad(inType.toString(),
                                                s_maxLevelLength, false));

    if(inMessage != null)
    {
      inMessage = inMessage.replaceAll("\\\\", "\\\\\\\\");
      result = result.replaceAll("%T", inMessage.replaceAll("\\$", "\\\\\\$"));
    }

    // determine indent, if any
    int indentStart = result.indexOf("%<");
    int indentEnd   = result.indexOf("%>") - 2;

    result = result.replaceAll("%<", "");
    result = result.replaceAll("%>", "");

    // wrap at all?
    if(m_width == 0 || indentStart < 0 || indentEnd < 0)
      return result;

    int indent = indentEnd - indentStart;

    // save the texts
    String prompt = result.substring(0, indentEnd);
    String text   = result.substring(indentEnd);

    // check for too large prompt
    if(indent > (m_width * s_maxPrompt))
    {
      indent  = s_indent;
      prompt += '\n' + Strings.spaces(indent);
    }

    WrapBuffer buffer = getWrapBuffer(m_width - indent);

    // now wrap the text
    buffer.add(text);

    StringBuffer wrapped = new StringBuffer(prompt);
    for(String line = buffer.getLine(); line != null; line = buffer.getLine())
    {
      wrapped.append(line);
      wrapped.append('\n');

      if(buffer.hasMore())
        wrapped.append(Strings.spaces(indent));
    }

    wrapped.append(buffer.getLines());

    return wrapped.toString();
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The Test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- mock -----------------------------------------------------------

    /** This is a mock for an output stream for testing. */
    public static class StreamMock extends OutputStream
    {
      /** Empty constructor. */
      public StreamMock()
      {
        // nothgin to do here
      }

      /** The buffer with all the output. */
      private StringBuffer m_buffer = new StringBuffer();

      /** The text that is expected to appear. */
      private String m_expected;

      /**
       * Set the expected String.
       *
       * @param inExpected the String to expect
       *
       */
      public void setExpected(String inExpected)
      {
        m_expected = inExpected;
      }

      /**
       * Write some bytes.
       *
       * @param inText the bytes to write
       *
       */
      public void write(byte []inText)
      {
        m_buffer.append(new String(inText));
      }

      /**
       * Write a character.
       *
       * @param inChar the character to write
       *
       */
      public void write(int inChar)
      {
        m_buffer.append((char)inChar);
      }

      /** Flush the buffer. */
      public void flush()
      {
        // nothing necessary
      }

      /** Verify the text obtained with the text expected. */
      public void verify()
      {
        junit.framework.Assert.assertEquals("texts do not match",
                                            m_expected, m_buffer.toString());

        m_buffer = new StringBuffer();
      }
    }

    //......................................................................

    //----- print ----------------------------------------------------------

    /** Test printing. */
    @org.junit.Test
    public void print()
    {
      StreamMock mock = new StreamMock();
      Logger ascii = new ASCIILogger(mock, "%<test %Y - %L: %>%T", -1);

      Calendar current = new GregorianCalendar();

      // error test
      mock.setExpected("test " + current.get(Calendar.YEAR)
                       + " - ERROR    : "
                       + "just an error message\n");
      ascii.print("just an error message", Log.Type.ERROR);

      mock.verify();

      // info test
      mock.setExpected("test " + current.get(Calendar.YEAR)
                       + " - INFO     : "
                       + "just some info \\ message\n");
      ascii.print("just some info \\ message", Log.Type.INFO);

      mock.verify();

      // wrapped test
      mock.setExpected("test " + current.get(Calendar.YEAR)
                       + " - INFO     : "
                       + "just some info message, but this time the message "
                       + "is long\n"
                       + "                       "
                       + "enough to require wrapping of the lines\n");
      ascii.print("just some info message, but this time the message is "
                  + "long enough to require wrapping of the lines",
                  Log.Type.INFO);

      mock.verify();

      // special characters
      mock.setExpected("test " + current.get(Calendar.YEAR)
                       + " - INFO     : "
                       + "%$#*&^@\n");
      ascii.print("%$#*&^@", Log.Type.INFO);

      mock.verify();

      // object printing
      mock.setExpected("test " + current.get(Calendar.YEAR)
                       + " - INFO     : ERROR\n");
      ascii.print(Log.Type.ERROR, Log.Type.INFO);

      mock.verify();
    }

    //......................................................................
    //----- indent ---------------------------------------------------------

    /** Test indenting. */
    @org.junit.Test
    public void indent()
    {
      StreamMock mock = new StreamMock();
      Logger ascii = new ASCIILogger(mock,
                                     "%<this is just a very long and very "
                                     + "useless indentation, that's just used "
                                     + "only to make some problems: %>%T", -1);


      mock.setExpected("this is just a very long and very useless "
                       + "indentation, that's just used only to make some "
                       + "problems: \n"
                       + "                    just some test, of course "
                       + "somewhat larger to see if wrapping\n"
                       + "                    of lines works here as well!\n");
      ascii.print("just some test, of course somewhat larger to see "
                  + "if wrapping of lines works here as well!", Log.Type.INFO);

      mock.verify();
    }

    //......................................................................
    //----- formatting -----------------------------------------------------

    /** Test the formatting of messages. */
    @org.junit.Test
    public void formatting()
    {
      StreamMock mock = new StreamMock();
      ASCIILogger ascii = new ASCIILogger(mock, "", 20);

      // border cases
      assertEquals("empty", "", ascii.format("", Log.Type.INFO, "%T"));
      assertEquals("empty", "", ascii.format("a message", Log.Type.INFO, ""));

      // simple case
      assertEquals("simple", "a message - INFO     ",
                   ascii.format("a message", Log.Type.INFO, "%T - %L"));

      // date
      assertEquals("date", "1969-69-05-17-23-42-00",
                   ascii.format("message", Log.Type.WARNING,
                                "%Y-%y-%M-%D-%h-%m-%s",
                                new GregorianCalendar(1969, 4, 17, 23, 42,
                                                      0)));

      // indentation and wrapping
      assertEquals("indentation",
                   // the first line is too long here, because the prompt
                   // contains printable characters that are not in the indent
                     "Hello there: a large text \n"
                   + "     that is surely \n"
                   + "     wrapped",
                   ascii.format("a large text that is surely wrapped",
                                Log.Type.WARNING, "Hello %<there%>: %T"));
    }

    //......................................................................
  }

  //........................................................................
}
