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

package net.ixitxachitls.util.configuration;

import java.util.prefs.Preferences;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This file provides access to the java user configuration values.
 *
 * @file          User.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ThreadSafe
public class User extends Resource
{
  //----------------------------------------------------------------- nested

  //----- PreferencesHandler --------------------------------------------

  /**
   * This is a simple extension to the properties class.
   *
   * The preferences node is thread save, so this class is too.
   *
   * @example       PreferencesHandler preferences =
   *                  new PreferencesHandler("test.file");
   *
   */
  @ThreadSafe
  public static class PreferencesHandler implements DataHandler
  {
    //------------------------- PreferencesHandler -------------------------

    /**
     * The constructor for the derivations.
     *
     */
    protected PreferencesHandler()
    {
      // nothing done
    }

    //......................................................................
    //------------------------- PreferencesHandler -------------------------

    /**
     * Create the preferences handler.
     *
     * @param       inName the name of the file to load
     *
     */
    public PreferencesHandler(@Nonnull String inName)
    {
      Log.complete("creating user preferences " + inName);

      // store the values
      m_name = inName;
      m_node = Preferences.userRoot().node(m_name);
    }

    //......................................................................

    //------------------------------------------------------------ variables

    /** The name of the file. */
    protected @Nonnull String m_name;

    /** The preferences node where the data is stored. */
    protected @Nonnull Preferences m_node;

    //......................................................................

    //-------------------------------- get ---------------------------------

    /**
     * Get a String value from the underlying data.
     *
     * @param       inKey the key of the value to get
     *
     * @return      the requested String
     *
     */
    public @Nullable String get(@Nonnull String inKey)
    {
      String value = m_node.get(inKey, null);

      if(value == null)
        return null;

      return Encodings.decodeEscapes(value);
    }

    //......................................................................

    //------------------------------ canSave -------------------------------

    /**
     * Determine if the resource can be saved or not.
     *
     * @return      true if the values can be saved, false else
     *
     */
    public boolean canSave()
    {
      return true;
    }

    //......................................................................

    //-------------------------------- set ---------------------------------

    /**
     * Set a String value to the underlying data.
     *
     * @param       inKey   the key of the value to set
     * @param       inValue the value to set to
     *
     * @return      true if actually set, false if already at this value
     *
     */
    public boolean set(@Nonnull String inKey, @Nonnull String inValue)
    {
      String value = Encodings.encodeEscapes(inValue);

      boolean result = value.equals(m_node.get(inKey, null));
      m_node.put(inKey, value);

      return result;
    }

    //......................................................................
    //------------------------------- remove -------------------------------

    /**
     * Remove a value to the underlying data.
     *
     * @param       inKey   the key of the value to set
     *
     * @return      true if removed, false if not present
     *
     */
    public boolean remove(@Nonnull String inKey)
    {
      m_node.remove(inKey);

      return true;
    }

    //......................................................................
    //------------------------------- store --------------------------------

    /**
     * Store the file to the given OutputStream.
     *
     * @return      true if the values were saved, false else
     *
     */
    public boolean store()
    {
      try
      {
        m_node.flush();
      }
      catch(java.util.prefs.BackingStoreException e)
      {
        Log.warning("could not write to preferences " + m_name + ": " + e);

        return false;
      }

      return true;
    }

    //......................................................................
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //--------------------------------- User ---------------------------------

  /**
   * Default constructor.
   *
   */
  public User()
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
    return new PreferencesHandler(inName);
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

    /** Test get and set of values. */
    @org.junit.Test
    public void getSetRemove()
    {
      Config.setRewriting(false);

      m_logger.logClass(User.PreferencesHandler.class);

      Configuration user = new User();

      m_logger.addExpected("COMPLETE: creating user preferences test/test");
      assertEquals("get", "guru", user.get("test/test/test.get", "guru"));
      assertEquals("get", "guru", user.get("test/test/test.get", "guru2"));
      user.set("test/test/test.get", "a real value");
      assertEquals("get", "a real value",
                   user.get("test/test/test.get", "guru"));
      user.remove("test/test/test.get");
      assertEquals("get", "guru", user.get("test/test/test.get", "guru"));
      user.remove("test/test/test.get");

      Config.setRewriting(true);
    }

    //......................................................................
    //----- coverage -------------------------------------------------------

    /** Coverage tests. */
    @org.junit.Test
    public void coverage()
    {
      new PreferencesHandler();
    }

    //......................................................................
  }

  //........................................................................
}
