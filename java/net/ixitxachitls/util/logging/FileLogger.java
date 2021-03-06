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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a simple convenience class for logging to a file.
 *
 * @file          FileLogger.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class FileLogger extends ASCIILogger
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ FileLogger ------------------------------

  /**
   * Create the file logger with the default name and format.
   *
   */
  public FileLogger()
  {
    this(s_defNameFormat, def_format);
  }

  //........................................................................
  //------------------------------ FileLogger ------------------------------

  /**
   * Create the file logger with a named file (format).
   *
   * @param       inName the name of the file to write to
   *
   */
  public FileLogger(String inName)
  {
    this(inName, def_format);
  }

  //........................................................................
  //------------------------------ FileLogger ------------------------------

  /**
   * Create the file logger with a named file (format).
   *
   * @param       inName   the name of the file to write to
   * @param       inFormat the format to use for printing
   *
   */
  public FileLogger(String inName, String inFormat)
  {
    super(System.out, inFormat, def_width);

    m_nameFormat = inName;
    m_name       = format(null, null, m_nameFormat);

    open();
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The name of the file to log to. */
  private String m_name;

  /** The format for the file name to use. */
  protected String m_nameFormat;

  /** The output stream to write to. */
  private @Nullable FileOutputStream m_file;

  /** The standard width of the output buffer. */
  protected static final int def_width = 130;

  /** The standard name format. */
  protected static final String s_defNameFormat =
    Config.get("logger.nameformat", "%y-%M-%D.log");

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- open ---------------------------------

  /**
   * Open the current log file for appending.
   *
   */
  protected void open()
  {
    // try to close the file first
    if(m_file != null)
    {
      try
      {
        m_file.close(); // $codepro.audit.disable closeInFinally
      }
      catch(java.io.IOException e)
      {
        System.err.println("could not close file '" + m_name + "': " + e);
      }

      m_file = null;
    }

    try
    {
      // $codepro.audit.disable closeWhereCreated
      m_file = new FileOutputStream(m_name, true);
      m_out  = new BufferedOutputStream(m_file);
      // $codepro.audit.enable
    }
    catch(java.io.FileNotFoundException e)
    {
      System.err.println("could not open log file '" + m_name + "': " + e
                         + "\nwill log to system error");

      m_out = System.err;
    }
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
  public void print(String inText, Log.Type inType)
  {
    // don't print status messages to file logs
    if(inType == Log.Type.STATUS)
      return;

    // check if the current name changed
    String currentName = format(null, null, m_nameFormat);

    if(!currentName.equals(m_name))
    {
      m_name = currentName;

      open();
    }

    super.print(inText, inType);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The Test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- print ----------------------------------------------------------

    /**
     * Test printing.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void print() throws Exception
    {
      Log.setLevel(Log.Type.COMPLETE);

      java.io.File temp = java.io.File.createTempFile("test", ".file");
      java.util.Calendar current = new java.util.GregorianCalendar();

      try (FileLogger file = new FileLogger(temp.getPath(), "%<%Y - %L: %>%T"))
      {
        file.print("just an error message", Log.Type.ERROR);

        // wrapped test
        file.print("just some info message, but this time the message is "
                   + "long enough to require wrapping of the lines",
                   Log.Type.INFO);

        //close output stream (prevent windows file lock on temp file)
        if (file.m_out != null)
          file.m_out.close(); // $codepro.audit.disable closeInFinally
      }

      // constructor with name only
      try (FileLogger file = new FileLogger(temp.getPath()))
      {
        file.print("printing to name constructor", Log.Type.INFO);

        // status printing
        file.print("some status message", Log.Type.STATUS);

        java.io.BufferedReader input = null;
        try
        {
          input = new java.io.BufferedReader(new java.io.InputStreamReader
                                             (new java.io.FileInputStream
                                              (temp)));

          assertEquals("first",
                       current.get(java.util.Calendar.YEAR)
                       + " - ERROR    : just an error message",
                       input.readLine());

          assertEquals
            ("second",
             current.get(java.util.Calendar.YEAR)
             + " - INFO     : just some info message, but this time "
             + "the message is long enough to require wrapping of the "
             + "lines",
             input.readLine());

          String line = input.readLine();
          assertNotNull("third (null)", line);
          assertEquals("third",
                       " - INFO     : printing to name constructor",
                       line.substring(19));

          assertNull("end", input.readLine());
        }
        catch(java.io.IOException e)
        {
          fail("exception " + e);
        }
        finally
        {
          try
          {
            if(input != null)
              input.close();
          }
          catch(java.io.IOException e)
          {
            fail("exception " + e);
          }
        }

        //close output stream (prevent windows file lock on temp file)
        if (file.m_out != null)
          file.m_out.close(); // $codepro.audit.disable closeInFinally

        assertTrue("cleanup", temp.delete());
      }
    }

    //......................................................................
    //----- rename ---------------------------------------------------------

    /**
     * Test renaming of the output file if necessary.
     *
     * @throws Exception should not happen
     *
     */
    @org.junit.Test
    public void rename() throws Exception
    {
      java.io.File temp = java.io.File.createTempFile("test", ".file");

      try (FileLogger logger =
        new FileLogger(temp.getPath().replaceAll(".file", "-%s.file")))
      {
        logger.print("just a test", Log.Type.WARNING);

        Thread.sleep(1 * 1000);

        logger.print("another test", Log.Type.ERROR);

        // check that the files were properly written
        String path = net.ixitxachitls.util.Files.path(temp.getPath());
        java.io.File dir = new java.io.File(path);

        assertTrue("dir", dir.isDirectory());

        java.io.File []files = dir.listFiles(new java.io.FilenameFilter()
          {
            @Override
            public boolean accept(java.io.File inDir, String inName)
            {
              if(inName == null)
                return false;

              return inName.matches("test.*-\\d\\d\\.file");
            }
          });

        assertEquals("length of directory " + dir, 2, files.length);

        assertEquals
          ("diff", 1,
           (Integer.parseInt
            (net.ixitxachitls.util.Strings.getPattern(files[1].getPath(),
                                                      "-(\\d\\d).file$"))
            - Integer.parseInt
            (net.ixitxachitls.util.Strings.getPattern(files[0].getPath(),
                                                      "-(\\d\\d).file$"))
            + 60) % 60);

        //close output stream (prevent windows file lock on temp file)
        if (logger.m_out != null)
          logger.m_out.close(); // $codepro.audit.disable closeInFinally

        // delete the files
        assertTrue("cleanup", temp.delete());
        assertTrue("cleanup", files[0].delete());
        assertTrue("cleanup", files[1].delete());
      }
    }

    //......................................................................
  }

  //........................................................................
}
