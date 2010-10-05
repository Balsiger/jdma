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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.util.Pair;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a configuration allowing to access resources (searched on the class
 * path). It does _NOT_ allow to store any values.
 *
 * @file          Resource.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Resource implements Configuration
{
  //----------------------------------------------------------------- nested

  //----- DataHandler ------------------------------------------------------

  /** This is a small interface to handle the basic data files. */
  public interface DataHandler
  {
    //-------------------------------- get ---------------------------------

    /**
     * Get a String value from the underlying data.
     *
     * @param       inKey the key of the value to get
     *
     * @return      the requested String
     *
     */
    public @Nullable String get(@Nonnull String inKey);

    //......................................................................
    //-------------------------------- set ---------------------------------

    /**
     * Set a String value to the underlying data.
     *
     * @param       inKey   the key of the value to set
     * @param       inValue the value to set to
     *
     */
    public void set(@Nonnull String inKey, @Nonnull String inValue);

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
    public boolean remove(@Nonnull String inKey);

    //......................................................................

    //------------------------------ canSave -------------------------------

    /**
     * Determine if the resource can be saved or not.
     *
     * @return      true if the values can be saved, false else
     *
     */
    public boolean canSave();

    //......................................................................

    //------------------------------- store --------------------------------

    /**
     * Store the file to the given OutputStream.
     *
     * @return      true if the values were be saved, false else
     *
     */
    public boolean store();

    //......................................................................
  }

  //........................................................................
  //----- PropertiesHandler ------------------------------------------------

  /**
   * This is a simple extension to the properties class capable of
   * loading its contents from an url.
   *
   * @example       DataHandler properties =
   *                  new PropertiesHandler(new URL("test.file"));
   *
   */
  public static class PropertiesHandler extends Properties
    implements DataHandler
  {
    //-------------------------- PropertiesHandler -------------------------

    /**
     * Create the properties object and also directly load the data from the
     * URL.
     *
     * @param       inURL the url to load the data from
     *
     */
    public PropertiesHandler(@Nullable URL inURL)
    {
      // store the value
      m_url = inURL;

      // try to load the data from the url
      if(m_url != null)
      {
        InputStream input = null;

        try
        {
          input = m_url.openStream();
          load(input);
        }
        catch(java.io.IOException e)
        {
          Log.warning("cannot load resource file '" + m_url + "': " + e);
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
            Log.warning("cannot close resource file '" + m_url + "': " + e);
          }
        }
      }
    }

    //......................................................................

    //------------------------------------------------------------ variables

    /** The url loaded from (used for storing again). */
    @Nullable private URL m_url;

    /** The Default header written to each file. */
    protected static final String def_header =
    "#\n"
    + "# Automatically generated configuration file of jDMA\n"
    + "# Do not make changes while your program is running ;-)\n"
    + "#\n";

    /** The id for serialization. */
    private static final long serialVersionUID = 1L;

    //......................................................................

    //-------------------------------- get ---------------------------------

    /**
     * Get a String value from the underlying data.
     *
     * @param       inKey the key of the value to get
     *
     * @return      the requested String
     *
     * @undefined   may return null if the String was not found
     * @undefined   key may not be null
     *
     */
    public @Nullable String get(@Nonnull String inKey)
    {
      return getProperty(inKey);
    }

    //......................................................................
    //------------------------------- getURL -------------------------------

    /**
     * Get the URL the data is loaded from and stored to.
     *
     * @return      the URL
     *
     */
    public @Nullable URL getURL()
    {
      return m_url;
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
      return m_url != null && m_url.getProtocol().equals("file");
    }

    //......................................................................
    //------------------------------- equals -------------------------------

    /**
     * Check if the given object is equal to the current one.
     *
     * @param       inOther the object to compare with
     *
     * @return      true if they objects are equal, false if not
     *
     */
    public boolean equals(Object inOther)
    {
      if(!(inOther instanceof PropertiesHandler))
        return false;

      PropertiesHandler other = (PropertiesHandler)inOther;

      if(other.m_url != m_url
         || (m_url != null && !m_url.toString().equals(other.m_url.toString())))
        return false;

      return super.equals(inOther);
    }

    //......................................................................
    //------------------------------ hashCode ------------------------------

    /**
     * Compute the hash code for the object.
     *
     * @return      the computed hash code
     *
     */
    public int hashCode()
    {
      return super.hashCode();
    }

    //......................................................................

    //-------------------------------- set ---------------------------------

    /**
     * Set a String value to the underlying data.
     *
     * @param       inKey   the key of the value to set
     * @param       inValue the value to set to
     *
     * @undefined   key and value may not be null
     *
     */
    public void set(@Nonnull String inKey, @Nonnull String inValue)
    {
      setProperty(inKey, inValue);
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
      return super.remove(inKey) != null;
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
      if(m_url == null)
        return false;

      FileOutputStream file = null;
      try
      {
        file = new FileOutputStream(new File(new URI(m_url.toString())));
        store(file, def_header);
      }
      catch(java.io.FileNotFoundException e)
      {
        Log.warning("could not find output file '" + m_url.getFile() + "': "
                    + e);

        return false;
      }
      catch(URISyntaxException e)
      {
        Log.warning("could not find output file '" + m_url.getFile() + "': "
                    + e);

        return false;
      }
      catch(java.io.IOException e)
      {
        Log.warning("could not write to file '" + m_url.getFile() + "': " + e);

        return false;
      }
      finally
      {
        if(file != null)
          try
          {
            file.close();
          }
          catch(java.io.IOException e)
          {
            Log.warning("cannot close output '" + m_url + "': " + e);

            return false;
          }
      }

      return true;
    }

    //......................................................................
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------- Resource -------------------------------

  /**
   * Default constructor (empty).
   *
   */
  public Resource()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Default extension for configuration files. */
  protected static final String def_extension = ".config";

  /** The Name of the default configuration file. */
  protected static final String def_file = "default" + def_extension;

  /** The storage for all property handlers. */
  protected Hashtable<String, DataHandler> m_handlers =
    new Hashtable<String, DataHandler>();

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------------- get ----------------------------------

  /**
   * Get a property value from the configuration.
   *
   * @param       inKey      the key of the value to obtain
   * @param       inDefault  the default value if no value is found
   *
   * @return      the requested string
   *
   */
  public @Nonnull String get(@Nonnull String inKey, @Nonnull String inDefault)
  {
    Pair<String, String> keys = splitPath(inKey, def_file);

    DataHandler data = getDataHandler(keys.first());

    String value = data.get(keys.second());

    // if in rewrite mode, always overwrite the existing values, but only
    // if the values can be saved at all
    if((!Config.isRewriting()) && value != null)
      return value;

    // value was not found, thus set it (only if it can be set as well!)
    if(data.canSave())
      set(inKey, inDefault);
    else
      if(value == null)
        Log.warning("configuration value for " + inKey
                    + " not found and cannot be stored");

    return inDefault;
  }

  //........................................................................
  //---------------------------- getDataHandler ----------------------------

  /**
   * Get a properties file as described in the given name.
   *
   * @param       inName the property file to use
   *
   * @return      the requested property
   *
   */
  protected DataHandler getDataHandler(@Nonnull String inName)
  {
    DataHandler result = m_handlers.get(inName);

    if(result != null)
      return result;

    result = load(inName);

    m_handlers.put(inName, result);

    return result;
  }

  //........................................................................

  //-------------------------------- hasKey --------------------------------

  /**
   * Check if a key exists in the configuration.
   *
   * @param       inKey the key to check for
   *
   * @return      true if there is already a configuration value with the
   *              given key
   *
   */
  public boolean hasKey(@Nonnull String inKey)
  {
    Pair<String, String> keys = splitPath(inKey, def_file);

    DataHandler data = getDataHandler(keys.first());

    String value = data.get(keys.second());

    return value != null;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   * @return      true if value was set, false if not
   *
   */
  public boolean set(@Nonnull String inKey, @Nonnull String inValue)
  {
    Pair<String, String> keys = splitPath(inKey, def_file);

    // determine if we can actually store the file
    DataHandler data = getDataHandler(keys.first());

    data.set(keys.second(), inValue);

    return store(data);
  }

  //........................................................................
  //-------------------------------- remove --------------------------------

  /**
   * Remove a property value from the configuration.
   *
   * @param       inKey   the key of the value to remove
   *
   * @return      true if value was removed and stored, false if not present
   *
   */
  public boolean remove(@Nonnull String inKey)
  {
    Pair<String, String> keys = splitPath(inKey, def_file);

    // determine if we can actually store the file
    DataHandler data = getDataHandler(keys.first());

    return data.remove(keys.second()) && store(data);
  }

  //........................................................................
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
  protected @Nullable DataHandler load(@Nonnull String inName)
  {
    String name = inName;

    int pos = name.lastIndexOf(".");
    if(pos < 0)
      name += def_extension;

    // no '/' in front needed, because it's not accessed over Class

    //add "config" as prefix, resource are relative to the config package
    URL url = Config.getCurrentClassLoader().getResource("config/" + name);

    if(url == null)
      Log.warning("could not load configuration resource file '" + name
                  + "', values will not be saved!");
    else
      Log.complete("loading resource " + url);

    return new PropertiesHandler(url);
  }

  //........................................................................
  //-------------------------------- store ---------------------------------

  /**
   * Store the given properties.
   *
   * @param       inData the handler to save
   *
   * @return      true if stored, false if error
   *
   */
  protected boolean store(@Nonnull DataHandler inData)
  {
    // cannot save a jar file
    if(!inData.canSave())
      return false;

    inData.store();

    return true;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------ splitPath -------------------------------

  /**
   * Split a given configuration name into its key and file selection part.
   *
   * @param       inName    the name to split
   * @param       inDefault default path if none is found
   *
   * @return      an array with the value 0 being the configuration
   *              descriptor and the value 1 being the key
   *
   * @undefined   never (values are always returned)
   *
   */
  protected static @Nonnull Pair<String, String> splitPath
    (@Nonnull String inName, @Nonnull String inDefault)
  {
    int pos = inName.lastIndexOf('/');
    if(pos >= 0)
      return new Pair<String, String>(inName.substring(0, pos),
                                      inName.substring(pos + 1));

    return new Pair<String, String>(inDefault, inName);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test class. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    // static { Log.add("ansi",
    //                  new net.ixitxachitls.util.logging.ANSILogger()); }

    //----- split path -----------------------------------------------------

    /** Test for splitting up paths. */
    @org.junit.Test
    public void splitPath()
    {
      assertEquals("simple", "guru",
                   Resource.splitPath("test", "guru").first());
      assertEquals("simple", "test",
                   Resource.splitPath("test", "guru").second());

      assertEquals("full", "a/b/c",
                   Resource.splitPath("a/b/c/test", "d").first());
      assertEquals("full", "test",
                   Resource.splitPath("a/b/c/test", "d").second());
    }

    //......................................................................
    //----- get ------------------------------------------------------------

    /** Test for getting a value. */
    @org.junit.Test
    public void get()
    {
      m_logger.logClass(Resource.class);

      Resource resource = new Resource();

      m_logger.addExpectedPattern("COMPLETE: loading resource "
                                  + ".*/test.config");

      assertEquals("get", "guru", resource.get("test/test/test.get", "guru"));
      resource.remove("test/test/test.set");

      // the same with a space in the name
      assertEquals("get", "guru", resource.get("test/test test/test.get",
                                               "guru"));
      resource.remove("test/test test/test.set");

      m_logger.addExpectedPattern("COMPLETE: loading resource "
                                  + ".*/test/test(%20| )test.config");

      // equals, hashCode
      m_logger.addExpectedPattern("COMPLETE: loading resource "
                                  + ".*/test/test(%20| )test.config");
      m_logger.addExpected("WARNING: could not load configuration resource "
                           + "file 'invalid.config', values will not be "
                           + "saved!");

      PropertiesHandler first =
        (PropertiesHandler)resource.getDataHandler("test/test");
      PropertiesHandler second =
        (PropertiesHandler)resource.getDataHandler("/test/test test");
      PropertiesHandler invalid =
        (PropertiesHandler)resource.getDataHandler("invalid");

      assertTrue("equals first", first.equals(first));
      assertTrue("equals second", second.equals(second));
      assertFalse("not equals", first.equals(second));
      assertFalse("not equals type", first.equals(this));

      assertTrue("hash code", first.hashCode() != second.hashCode());

      // saving
      assertTrue("save first?", first.canSave());
      assertTrue("save second?", second.canSave());
      assertFalse("save invalid?", invalid.canSave());
      assertTrue("save first", first.store());
      assertFalse("save invalid", invalid.store());

      // check the URL
      String url = first.getURL().toString();

      assertEquals("url", "config/test/test.config",
                   url.substring(url.indexOf("config")));


      m_logger.verify();
    }

    //......................................................................
    //----- hasKey ---------------------------------------------------------

    /** Test for checking for values. */
    @org.junit.Test
    public void hasKey()
    {
      m_logger.logClass(Resource.class);

      Configuration resource = new Resource();

      m_logger.addExpectedPattern("COMPLETE: loading resource "
                                  + ".*/test.config");

      assertEquals("not yet", false, resource.hasKey("test/test/test.hasKey"));
      assertEquals("get", "guru",
                   resource.get("test/test/test.hasKey", "guru"));

      if(((Resource)resource)
         .getDataHandler("test/test").canSave())
        assertEquals("now there", true,
                     resource.hasKey("test/test/test.hasKey"));
      else
        m_logger.addExpected("WARNING: configuration value for "
                             + "test/test/test.hasKey not found and cannot "
                             + "be stored");

      resource.remove("test/test/test.hasKey");
    }

    //......................................................................
    //----- set ------------------------------------------------------------

    /** Test for setting values. */
    @org.junit.Test
    public void set()
    {
      Config.setRewriting(false);

      m_logger.logClass(Resource.class);

      Resource resource = new Resource();

      m_logger.addExpectedPattern("COMPLETE: loading resource "
                                  + ".*/test.config");

      // only do this if not in a jar file (i.e. if we can store the file)
      if(resource.getDataHandler("test/test").canSave())
      {
        // normal set
        assertEquals("get", "guru",
                     resource.get("test/test/test.set", "guru"));

        resource.set("test/test/test.set", "test");
        assertEquals("get", "test",
                     resource.get("test/test/test.set", "guru"));
        resource.remove("test/test/test.set");
      }

      m_logger.addExpectedPattern("COMPLETE: loading resource "
                                  + ".*/test/test(%20| )test.config");

      // only do this if not in a jar file (i.e. if we can store the file)
      if(resource.getDataHandler("test/test test").canSave())
      {
        // normal set
        assertEquals("get", "guru",
                     resource.get("test/test test/test.set", "guru"));

        resource.set("test/test test/test.set", "test");
        assertEquals("get", "test",
                     resource.get("test/test test/test.set", "guru"));
        resource.remove("test/test test/test.set");
      }

      // set with a preset value
      assertEquals("get", "just some guru guru",
                   resource.get("test/test/test.value", "guru"));


      // set with path containing " "
      m_logger.addExpectedPattern("COMPLETE: loading resource "
                                  + ".*/test.*path.config");
      resource.set("test/test path/test path", "test path");

      Config.setRewriting(true);
    }

    //......................................................................
    //----- store ----------------------------------------------------------

    /**
     * Sorting test.
     *
     * @throws Exception Too lazy to catch it.
     *
     */
    @org.junit.Test
    public void store() throws Exception
    {
      PropertiesHandler handler = new PropertiesHandler(null);

      assertFalse("store null", handler.store());

      m_logger.addExpected("WARNING: cannot load resource file "
                           + "'file:/just a test': "
                           + "java.io.FileNotFoundException: /just a test "
                           + "(No such file or directory)");
      m_logger.addExpected("WARNING: could not find output file "
                           + "'/just a test': "
                           + "java.net.URISyntaxException: Illegal character "
                           + "in path at index 10: file:/just a test");
      handler = new PropertiesHandler(new URL("file:///just a test"));
      assertFalse("store syntax", handler.store());

      m_logger.addExpected("WARNING: cannot load resource file "
                           + "'file:/guru/guru': "
                           + "java.io.FileNotFoundException: /guru/guru "
                           + "(Not a directory)");
      m_logger.addExpected("WARNING: could not find output file "
                           + "'/guru/guru': "
                           + "java.io.FileNotFoundException: /guru/guru "
                           + "(Not a directory)");
      handler = new PropertiesHandler(new URL("file:/guru/guru"));
      assertFalse("store invalid", handler.store());
    }

    //......................................................................

  }

  //........................................................................
}
