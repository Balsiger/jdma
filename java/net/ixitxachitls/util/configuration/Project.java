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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class encapsulates access to a project specific configuration file
 * (searched from where the program was started).
 *
 * @file          Project.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ThreadSafe
public class Project extends Resource
{
  //----------------------------------------------------------------- nested

  //----- FileHandler ------------------------------------------------------

  /**
   * This is a simple extension to the properties class capable of
   * loading its contents from a file.
   *
   * @example       File Handler handler = new FileHandler("test.file");
   *
   */
  @ThreadSafe
  public static class FileHandler extends Properties implements DataHandler
  {
    //----------------------------- FileHandler ----------------------------

    /**
     * Create the properties object and also directly load the data from the
     * file.
     *
     * @param       inFile the name of the file to load
     *
     */
    public FileHandler(@Nonnull String inFile)
    {
      // store the value
      m_file = inFile;

      // try to load the data from the url
      FileInputStream file = null;
      try
      {
        file = new FileInputStream(m_file);

        load(file);
      }
      catch(java.io.IOException e)
      {
        // file could not be loaded, thus we try to create it ;-)
        try
        {
          if(!new File(m_file).createNewFile())
            Log.warning("cannot create configuration file '" + m_file + "': "
                        + e);
        }
        catch(java.io.IOException e2)
        {
          Log.warning("cannot create configuration file '" + m_file + "': "
                      + e2);
        }
      }
      finally
      {
        try
        {
          if(file != null)
            file.close();
        }
        catch(java.io.IOException e)
        {
          Log.warning("cannot close configuration file '" + m_file + "': "
                      + e);
        }

        Log.complete("loaded configuration file '" + m_file + "'");
      }
    }

    //......................................................................

    //------------------------------------------------------------ variables

    /** The name of the file to be used. */
    private @Nonnull String m_file;

    /** The default header to be written to the file. */
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
     */
    public @Nullable String get(@Nonnull String inKey)
    {
      return getProperty(inKey);
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
      if(!(inOther instanceof FileHandler))
        return false;

      FileHandler other = (FileHandler)inOther;

      if(m_file != null && !m_file.equals(other.m_file))
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
      return m_file.hashCode() + super.hashCode();
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
      return new File(m_file).canWrite();
    }

    //......................................................................

    //-------------------------------- set ---------------------------------

    /**
     * Set a String value to the underlying data.
     *
     * @param       inKey   the key of the value to set
     * @param       inValue the value to set to
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
     * @undefined   an assertion is raised if the given stream is null
     *
     */
    public boolean store()
    {
      FileOutputStream file = null;
      try
      {
        file = new FileOutputStream(m_file);

        store(file, def_header);
      }
      catch(java.io.FileNotFoundException e)
      {
        Log.warning("could not find output file '" + m_file + "': " + e);

        return false;
      }
      catch(java.io.IOException e)
      {
        Log.warning("could not write to file '" + m_file + "': " + e);

        return false;
      }
      finally
      {
        try
        {
          if(file != null)
            file.close();
        }
        catch(java.io.IOException e)
        {
          Log.warning("could not close file '" + m_file + "': " + e);
        }
      }

      return true;
    }

    //......................................................................
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------- Project --------------------------------

  /**
   * Default constructor.
   *
   */
  public Project()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The default name of the configuration. */
  protected static final String s_default = "project" + def_extension;

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
  public @Nonnull DataHandler load(@Nullable String inName)
  {
    String name = inName;

    if(name == null || name.length() == 0)
      name = s_default;

    if(name.lastIndexOf(".") < 0)
      name += def_extension;

    return new FileHandler(name);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test class. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    // static { Log.add("ansi",
    //                  new net.ixitxachitls.util.logging.ANSILogger()); }

    //----- get ------------------------------------------------------------

    /** The test for getting. */
    @org.junit.Test
    public void get()
    {
      m_logger.logClass(Project.FileHandler.class);

      Configuration project = new Project();

      m_logger.addExpected("COMPLETE: loaded configuration file "
                           + "'resources/config/test/project.config'");

      assertEquals("get", "guru",
                   project.get("resources/config/test/project/test.get",
                               "guru"));
      project.remove("resources/config/test/project/test.get");

      m_logger.verify();
    }

    //......................................................................
    //----- set ------------------------------------------------------------

    /** The test for setting. */
    @org.junit.Test
    public void set()
    {
      Config.setRewriting(false);

      m_logger.logClass(Project.FileHandler.class);

      Configuration project = new Project();

      m_logger.addExpected("COMPLETE: loaded configuration file "
                           + "'resources/config/test/project.config'");

      // normal set
      assertEquals("get", "guru",
                   project.get("resources/config/test/project/test.set",
                               "guru"));
      project.set("resources/config/test/project/test.set", "test");

      assertEquals("get", "test",
                   project.get("resources/config/test/project/test.set",
                               "guru"));
      project.remove("resources/config/test/project/test.set");

      // set with a preset value
      assertEquals("get", "just some guru guru",
                   project.get("resources/config/test/project/test.value",
                               "guru"));

      m_logger.verify();

      Config.setRewriting(true);
    }

    //......................................................................
    //----- load -----------------------------------------------------------

    /** Loading test. */
    @org.junit.Test
    public void load()
    {
      FileHandler handler =
        new FileHandler("resources/config/test/project.config");
      assertTrue("can save", handler.canSave());
      assertTrue("store", handler.store());

      handler = new FileHandler("resources/config/test/guru.config");
      assertTrue("can save (created)", handler.canSave());
      assertTrue("store (created)", handler.store());
      assertTrue("delete",
                 new File("resources/config/test/guru.config").delete());

      m_logger.addExpected("WARNING: cannot create configuration file "
                           + "'test/guru.config': java.io.IOException: No "
                           + "such file or directory");

      m_logger.addExpected("WARNING: could not find output file "
                           + "'test/guru.config': "
                           + "java.io.FileNotFoundException: test/guru.config "
                           + "(No such file or directory)");

      handler = new FileHandler("test/guru.config");
      assertFalse("can save (invalid)", handler.canSave());
      assertFalse("store (invalid)", handler.store());
    }

    //......................................................................
    //----- equals ---------------------------------------------------------

    /** Equality tests. */
    @org.junit.Test
    public void equals()
    {
      FileHandler first = new FileHandler("resources/config/test/test");
      FileHandler second = new FileHandler("resources/config/test/project");
      FileHandler third = new FileHandler("reosource/config/test/test");

      assertTrue("same", first.equals(first));
      assertTrue("similar", first.equals(third));
      assertFalse("different", first.equals(second));
      assertTrue("same hash", first.hashCode() == first.hashCode());
      assertTrue("similar hash", first.hashCode() == third.hashCode());
      assertFalse("different hash", first.hashCode() == second.hashCode());
    }

    //......................................................................

  }

  //........................................................................
}
