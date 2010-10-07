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

import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class represent a resource bundle to be access using the normal
 * Configuration interface. The language used for this resource bundle
 * is taken from the Resource configuration.
 *
 * @file          Bundle.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Bundle extends Resource
{
  //----------------------------------------------------------------- nested

  //----- BundleHandler ----------------------------------------------------

  /**
   * This is a simple extension to the resource bundle class.
   *
   * @example       BundleHandler handler = new BundleHandler("test.file");
   *
   */
  public static class BundleHandler implements DataHandler
  {
    //--------------------------- BundleHandler ---------------------------

    /**
     * Create the properties object and also directly load the data from the
     * URL.
     *
     * @param       inName the name of the file to load
     *
     */
    public BundleHandler(@Nonnull String inName)
    {
      Log.complete("creating bundle " + inName + " for " + s_language);

      // store the values
      m_name     = inName;
      m_language = s_language;

      // add config prefix, bundles are relativ to the config package
      try
      {
        m_bundle = ResourceBundle.getBundle("config/" + m_name, m_language,
                                              Config.getCurrentClassLoader());
      }
      catch(java.util.MissingResourceException e)
      {
        m_bundle = null;
      }

      if(m_bundle == null)
        Log.warning("could not open resource bundle '" + m_name
                    + "' for language " + m_language);
    }

    //......................................................................

    //------------------------------------------------------------ variables

    /** The name of the bundle. */
    private String m_name;

    /** The language locale the handler is currently in. */
    private Locale m_language;

    /** The resource bundle itself. */
    private ResourceBundle m_bundle;

    /** The default language that is set to be used by the program. */
    private static Locale s_language = Locale.getDefault();

    //......................................................................

    //-------------------------------- get ---------------------------------

    /**
     * Get a String value from the underlying data.
     *
     * @param       inKey the key of the value to get
     *
     * @return      the requested String
     *
     * @undefined   may return if the value is not found
     *
     */
    public @Nullable String get(@Nonnull String inKey)
    {
      if(!m_language.equals(s_language))
      {
        m_language = s_language;

        Log.complete("reloading bundle " + m_name + " for " + m_language);

        m_bundle = ResourceBundle.getBundle("config/" + m_name, m_language,
                                            Config.getCurrentClassLoader());

        if(m_bundle == null)
          Log.warning("could not open resource bundle '" + m_name
                      + "' for language " + m_language);
      }

      if(m_bundle == null)
        return null;

      try
      {
        return m_bundle.getString(inKey);
      }
      catch(java.util.MissingResourceException e)
      {
        Log.warning("resource text for " + inKey + " not found in " + m_name
                    + " for language " + m_language);

        return null;
      }
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
      return false;
    }

    //......................................................................

    //-------------------------------- set ---------------------------------

    /**
     * Set a String value to the underlying data.
     *
     * This kind of configuration does not support setting values.
     *
     * @param       inKey   the key of the value to set
     * @param       inValue the value to set to
     *
     */
    public void set(String inKey, String inValue)
    {
      throw new UnsupportedOperationException("cannot set bundle values");
    }

    //......................................................................
    //------------------------------- remove -------------------------------

    /**
     * Remove a value to the underlying data.
     *
     * This operation is not supported for bundles.
     *
     * @param       inKey   the key of the value to set
     *
     * @return      true if removed, false if not present
     *
     */
    public boolean remove(String inKey)
    {
      throw new UnsupportedOperationException("cannot remove bundle values");
    }

    //......................................................................
    //------------------------------- store --------------------------------

    /**
     * Store the configuration.
     *
     * This is operation is not supported for bundles.
     *
     * @return      true if the values were saved, false else
     *
     */
    public boolean store()
    {
      throw new UnsupportedOperationException("cannot store bundle value");
    }

    //......................................................................
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Bundle --------------------------------

  /**
   * This is the (empty) default constructor.
   *
   */
  public Bundle()
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
    //add config prefix
    return new BundleHandler(inName);
  }

  //........................................................................

  //----------------------------- setLanguage ------------------------------

  /**
   * Set the language to be used by the resource bundle.
   *
   * @param       inLanguage the language locale to use
   *
   * @undefined   assertion if value is null
   *
   */
  public static void setLanguage(@Nonnull Locale inLanguage)
  {
    BundleHandler.s_language = inLanguage;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test class. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- get ------------------------------------------------------------

    /** Test for getting values. */
    @org.junit.Test
    public void get()
    {
      // make sure rewriting is off...
      Config.setRewriting(false);

      m_logger.logClass(Bundle.BundleHandler.class);

      Configuration bundle = new Bundle();

      Bundle.setLanguage(new Locale(""));

      m_logger.addExpected("COMPLETE: creating bundle test/test for ");
      assertEquals("get (default)", "default guru",
                   bundle.get("test/test/test.get", "guru"));

      Bundle.setLanguage(Locale.ENGLISH);

      m_logger.addExpected("COMPLETE: reloading bundle test/test for en");

      assertEquals("get (english)", "english guru",
                   bundle.get("test/test/test.get", "guru"));

      Bundle.setLanguage(Locale.GERMAN);

      m_logger.addExpected("COMPLETE: reloading bundle test/test for de");

      assertEquals("get (deutsch)", "deutsch guru",
                   bundle.get("test/test/test.get", "guru"));

      Bundle.setLanguage(new Locale("de", "CH"));

      m_logger.addExpected("COMPLETE: reloading bundle test/test for de_CH");

      assertEquals("get (schweiz)", "schweiz guru",
                   bundle.get("test/test/test.get", "guru"));
      assertEquals("get (schweiz)", "schweiz guru",
                   bundle.get("test/test/test.get", "guru"));

      // test with non existing value
      m_logger.addExpected("WARNING: resource text for key not found in "
                           + "test/test for language de_CH");
      m_logger.addExpected("WARNING: configuration value for test/test/key "
                           + "not found and cannot be stored");

      assertEquals("get (non existant)", "guru",
                   bundle.get("test/test/key", "guru"));

      // test with non existing file
      m_logger.addExpected("COMPLETE: creating bundle test/guru for de_CH");
      m_logger.addExpected("WARNING: could not open resource bundle "
                           + "'test/guru' for language de_CH");
      m_logger.addExpected("WARNING: configuration value for test/guru/key "
                           + "not found and cannot be stored");

      assertEquals("get (non existant)", "guru",
                   bundle.get("test/guru/key", "guru"));

      // make sure rewriting is on again..
      Config.setRewriting(true);
    }

    //......................................................................
    //----- coverage -------------------------------------------------------

    /** Tests for coverage. */
    @org.junit.Test
    public void coverage()
    {
      Bundle bundle = new Bundle();
      m_logger.banClass(Bundle.class);

      try
      {
        bundle.set("key", "value");
        fail("should have thrown an exception");
      }
      catch(UnsupportedOperationException e)
      { /* expected */ }

      try
      {
        bundle.remove("key");
        fail("should have thrown an exception");
      }
      catch(UnsupportedOperationException e)
      { /* expected */ }
    }

    //......................................................................

  }

  //........................................................................
}
