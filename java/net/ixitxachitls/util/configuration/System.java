/******************************************************************************
 * Copyright (c) 2002,2003 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.util.configuration;

import java.util.prefs.Preferences;

import javax.annotation.Nonnull;

import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This file provides access to the java system configuration values.
 *
 * @file          System.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class System extends User
{
  //----------------------------------------------------------------- nested

  //----- SystemPreferencesHandler -----------------------------------------

  /**
   * This is a simple extension to the properties class.
   *
   * @example       SystemPreferencesHandler preferences =
   *                  new SystemPreferencesHandler("test.file");
   *
   */
  public static class SystemPreferencesHandler extends PreferencesHandler
  {
    //---------------------- SystemPreferencesHandler ----------------------

    /**
     * Create the preferences handler.
     *
     * @param       inName the name of the file to load
     *
     */
    public SystemPreferencesHandler(@Nonnull String inName)
    {
      Log.complete("creating system preferences " + inName);

      // store the values
      m_name = inName;
      m_node = Preferences.systemRoot().node(m_name);
    }

    //......................................................................
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //-------------------------------- System --------------------------------

  /**
   * Default constructor.
   *
   */
  public System()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables
  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- load ---------------------------------

  /**
   * Load the property file with the given name.
   *
   * @param       inName the name of the property file to load (an
   *                     extension is added if none is present)
   *
   * @return      the requested property
   *
   */
  protected @Nonnull DataHandler load(@Nonnull String inName)
  {
    return new SystemPreferencesHandler(inName);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test class. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- get/set/remove -------------------------------------------------

    /** All tests. */
    @org.junit.Test
    public void getSetRemove()
    {
      Config.setRewriting(false);

      m_logger.logClass(System.SystemPreferencesHandler.class);

      Configuration system = new System();

      m_logger.addExpected("COMPLETE: creating system preferences test/test");
      assertEquals("get", "guru", system.get("test/test/test.get", "guru"));
      assertEquals("get", "guru", system.get("test/test/test.get", "guru2"));
      system.set("test/test/test.get", "a real value");
      assertEquals("get", "a real value",
                   system.get("test/test/test.get", "guru"));
      system.remove("test/test/test.get");
      assertEquals("get", "guru2", system.get("test/test/test.get", "guru2"));
      system.remove("test/test/test.get");

      Config.setRewriting(true);
    }

    //......................................................................
  }

  //........................................................................
}
