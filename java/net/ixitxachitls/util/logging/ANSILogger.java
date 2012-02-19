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

import net.ixitxachitls.output.WrapBuffer;
import net.ixitxachitls.util.Strings;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a logger capable of printing ANSI formatted messages.
 *
 * @file          ANSILogger.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @example       <PRE>
 *
 * // add the logger
 * Log.add("test", new ANSILogger());
 *
 * // set the logging level
 * Log.setLevel(Log.Type.DEBUG);
 *
 * // add some logging messages
 * Log.fatal("This is a fatal error message");
 * Log.error("This is an error message");
 * Log.warning("This is a warning message");
 * Log.necessary("This is a necessary message");
 * Log.important("this is an important message");
 * Log.useful("This is a useful message");
 * Log.info("This is an information message");
 * Log.complete("This is a complete message");
 * Log.debug("This is a debug message, make it a bit longer to check " +
 *           "for breaking of lines.........");
 * </PRE>
 *
 */

//..........................................................................

//__________________________________________________________________________

public class ANSILogger extends ASCIILogger
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- ANSILogger -------------------------------

  /**
   * Create the logger.
   *
   */
  public ANSILogger()
  {
    // nothing to do
  }

  //........................................................................
  //----------------------------- ANSILogger -------------------------------

  /**
   * Create the logger.
   *
   * @param       inStream the stream to print to
   *
   */
  public ANSILogger(@Nonnull OutputStream inStream)
  {
    super(inStream);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  // color codes
  /** Console code to RESET anything. */
  public static final String RESET         = "\u001B[0m";

  /** Console code for printing boldface. */
  public static final String BOLD          = "\u001B[1m";

  /** Console code for switching boldface off. */
  public static final String BOLD_OFF      = "\u001B[22m";

  /** Console code for underline. */
  public static final String UNDERLINE     = "\u001B[4m";

  /** Console code for switching underline off. */
  public static final String UNDERLINE_OFF = "\u001B[24m";

  /** Console code for blinking. */
  public static final String BLINK         = "\u001B[5m";

  /** Console code for blinking off. */
  public static final String BLINK_OFF     = "\u001B[25m";

  /** Console code for black text. */
  public static final String BLACK   = "\u001B[30m";

  /** Console code for red text. */
  public static final String RED     = "\u001B[31m";

  /** Console code for green text. */
  public static final String GREEN   = "\u001B[32m";

  /** Console code for brown text. */
  public static final String BROWN   = "\u001B[33m";

  /** Console code for blue text. */
  public static final String BLUE    = "\u001B[34m";

  /** Console code for magenta text. */
  public static final String MAGENTA = "\u001B[35m";

  /** Console code for cyan text. */
  public static final String CYAN    = "\u001B[36m";

  /** Console code for white text. */
  public static final String WHITE   = "\u001B[37m";


  /** Console code for black background. */
  public static final String BLACK_BACK   = "\u001B[40m";

  /** Console code for red background. */
  public static final String RED_BACK     = "\u001B[41m";

  /** Console code for green background. */
  public static final String GREEN_BACK   = "\u001B[42m";

  /** Console code for brown background. */
  public static final String BROWN_BACK   = "\u001B[43m";

  /** Console code for blue background. */
  public static final String BLUE_BACK    = "\u001B[44m";

  /** Console code for magenta background. */
  public static final String MAGENTA_BACK = "\u001B[45m";

  /** Console code for cyan background. */
  public static final String CYAN_BACK    = "\u001B[46m";

  /** Console code for white background. */
  public static final String WHITE_BACK   = "\u001B[47m";

  /** The format for fatal messages. */
  protected static final String def_fatal   =
    RED + BLINK + "%<%Y-%M-%D %h:%m:%s - %L: %>" + BLINK_OFF + "%T" + RESET;

  /** The format for event messages. */
  protected static final String def_event   =
    CYAN + "%<%Y-%M-%D %h:%m:%s - %L: %>%T" + RESET;

  /** The format for error messages. */
  protected static final String def_error   =
    RED + "%<%Y-%M-%D %h:%m:%s - %L: %>%T" + RESET;

  /** The format for warning messages. */
  protected static final String def_warning =
    BROWN + "%<%Y-%M-%D %h:%m:%s - %L---: %>%T" + RESET;

  /** The format for informational messages.. */
  protected static final String def_info    =
    GREEN + "%<%Y-%M-%D %h:%m:%s - %L: %>%T" + RESET;

  /** The format for debugging messages.. */
  protected static final String def_debug   =
    BLUE + "%<%Y-%M-%D %h:%m:%s - %L: %>%T" + RESET;

  /** The character pattern to ignore for width. */
  protected static final String s_ignore = "\u001B\\[\\d+m";

  /** The formats for the individual levels. */
  protected String []m_formats = new String[Log.Type.DEBUG.ordinal() + 1];

  // set the default formats
  {
    setFormat(Log.Type.FATAL,     def_fatal);
    setFormat(Log.Type.EVENT,     def_event);
    setFormat(Log.Type.ERROR,     def_error);
    setFormat(Log.Type.WARNING,   def_warning);
    setFormat(Log.Type.NECESSARY, def_info);
    setFormat(Log.Type.IMPORTANT, def_info);
    setFormat(Log.Type.USEFUL,    def_info);
    setFormat(Log.Type.INFO,      def_info);
    setFormat(Log.Type.STATUS,    def_info);
    setFormat(Log.Type.COMPLETE,  def_info);
    setFormat(Log.Type.DEBUG,     def_debug);
  }

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
   * @undefined   never
   *
   */
  @Override
protected @Nonnull WrapBuffer getWrapBuffer(int inWidth)
  {
    return new WrapBuffer(inWidth, s_ignore);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- setFormat ------------------------------

  /**
   * Set the color (or general escape sequence) used for a specific logging
   * type.
   *
   * @param       inType   the logging type to set the color for
   * @param       inFormat the format to use for this type
   *
   */
  public void setFormat(@Nonnull Log.Type inType, @Nonnull String inFormat)
  {
    m_formats[inType.ordinal()] = inFormat;
  }

  //........................................................................

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
  @Override
public void print(@Nonnull String inText, @Nonnull Log.Type inType)
  {
    // set the text to write to together
    String text;

    if(inType != Log.Type.STATUS)
      text = format(inText, inType, m_formats[inType.ordinal()]) + '\n';
    else
      text = inText + Strings.spaces(78 - inText.length()) + '\r';

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

  //........................................................................

  //------------------------------------------------------------------- test

  /** The Test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- print ----------------------------------------------------------

    /** Test printing. */
    @org.junit.Test
    public void print()
    {
      ASCIILogger.Test.StreamMock mock = new ASCIILogger.Test.StreamMock();
      ANSILogger ansi = new ANSILogger(mock);

      ansi.setFormat(Log.Type.ERROR, "%<error %Y - %L: %>%T");
      ansi.setFormat(Log.Type.INFO,  "%<info %Y - %L: %>%T");
      ansi.setFormat(Log.Type.DEBUG, BLUE + "%<debug %Y - %L: %>"
                     + BLACK + "%T");

      Calendar current = new GregorianCalendar();

      // error test
      mock.setExpected("error " + current.get(Calendar.YEAR)
                       + " - ERROR    : just an error message\n");
      ansi.print("just an error message", Log.Type.ERROR);

      mock.verify();

      // info test
      mock.setExpected("info " + current.get(Calendar.YEAR)
                       + " - INFO     : just some info message\n");
      ansi.print("just some info message", Log.Type.INFO);

      mock.verify();

      // wrapped test
      mock.setExpected("info " + current.get(Calendar.YEAR)
                       + " - INFO     : just some info message, but this time "
                       + "the message is long\n"
                       + "                       enough to require wrapping "
                       + "of the lines\n");
      ansi.print("just some info message, but this time the message is "
                 + "long enough to require wrapping of the lines",
                 Log.Type.INFO);

      mock.verify();

      // color test
      mock.setExpected(BLUE + "debug " + current.get(Calendar.YEAR)
                       + " - DEBUG    : " + BLACK + "some test\n");
      ansi.print("some test", Log.Type.DEBUG);

      mock.verify();

      // wrapped color test
      mock.setExpected(BLUE + "debug " + current.get(Calendar.YEAR)
                       + " - DEBUG    : " + BLACK + "some test, this time "
                       + "somewhat larger to see if word     \n"
                       + "                        wrapping works ok if the "
                       + "text is longer than a line\n");
      ansi.print("some test, this time somewhat larger to see if word "
                 + "wrapping works ok if the text is longer than a line",
                 Log.Type.DEBUG);

      mock.verify();

      // 80 character problem
      mock.setExpected(BLUE + "debug " + current.get(Calendar.YEAR)
                       + " - DEBUG    : " + BLACK
                       + "12345678901234567890123456789012345678901234567890"
                       + "123456\n"
                       + "                        789012345678901234567890\n");
      ansi.print("12345678901234567890123456789012345678901234567890"
                 + "123456789012345678901234567890", Log.Type.DEBUG);

      mock.verify();

      // object printing
      mock.setExpected(BLUE + "debug " + current.get(Calendar.YEAR)
                       + " - DEBUG    : " + BLACK + "ERROR\n");
      ansi.print(Log.Type.ERROR, Log.Type.DEBUG);

      mock.verify();

      // status printing
      mock.setExpected("some status text                                    "
                       + "                          \r");

      ansi.print("some status text", Log.Type.STATUS);

      mock.verify();
    }

    //......................................................................
  }

  //........................................................................
}
