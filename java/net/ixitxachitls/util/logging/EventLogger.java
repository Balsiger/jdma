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

import javax.annotation.Nonnull;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a simple convenience class for logging to a file.
 *
 * @file          EventLogger.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class EventLogger extends FileLogger
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- EventLogger ------------------------------

  /**
    * Create the file logger with the default name and format.
    *
    */
  public EventLogger()
  {
    this(s_defNameFormat, def_format);
  }

  //........................................................................
  //----------------------------- EventLogger ------------------------------

  /**
    * Create the file logger with a named file (format).
    *
    * @param       inName the name of the file to write to
    *
    */
  public EventLogger(@Nonnull String inName)
  {
    this(inName, def_format);
  }

  //........................................................................
  //----------------------------- EventLogger ------------------------------

  /**
    * Create the file logger with a named file (format).
    *
    * @param       inName   the name of the file to write to
    * @param       inFormat the format to use for printing
    *
    */
  public EventLogger(@Nonnull String inName, @Nonnull String inFormat)
  {
    super(inName, inFormat);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables
  //........................................................................

  //-------------------------------------------------------------- accessors
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
  public void print(@Nonnull String inText, @Nonnull Log.Type inType)
  {
    // don't print status messages to file logs
    if(inType != Log.Type.EVENT)
      return;

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
     *
     */
    @org.junit.Test
    public void print() throws Exception
    {
      Log.setLevel(Log.Type.COMPLETE);

      java.io.File temp = java.io.File.createTempFile("test", ".file");

      EventLogger file = new EventLogger(temp.getPath(), "%<%L: %>%T");

      file.print("just an error message", Log.Type.ERROR);
      file.print("just some event message", Log.Type.EVENT);
      file.print("just some info message", Log.Type.INFO);

      java.io.BufferedReader input = null;
      try
      {
        input =
          new java.io.BufferedReader(new java.io.
                                     InputStreamReader(new java.io.
                                                       FileInputStream(temp)));

        assertEquals("event", "EVENT    : just some event message",
                     input.readLine());

        assertNull("end", input.readLine());
      }
      catch(java.io.IOException e)
      {
        fail("exception " + e);
      }
      finally
      {
        if(input != null)
          input.close();

        //close output stream (prevent windows file lock on temp file)
        if(file.m_out != null)
          file.m_out.close();
      }

      assertTrue("cleanup", temp.delete());
    }

    //......................................................................
  }

  //........................................................................
}
