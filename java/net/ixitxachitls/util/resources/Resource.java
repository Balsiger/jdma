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

package net.ixitxachitls.util.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.output.html.HTMLWriter;
import net.ixitxachitls.util.Files;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A handler for generic resources. This abstracts away handling of resources
 * from the file system and jar files (or any other source).
 *
 * @file          ResourceHandler.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public abstract class Resource
{
  //------------------------------------------------------------ constructor

  //------------------------------- Resource -------------------------------

  /**
   * Create the resource.
   *
   * @param       inName the name of the resource
   * @param       inURL  the url to the resource
   *
   */
  public Resource(@Nonnull String inName, @Nullable URL inURL)
  {
    m_name = inName;
    m_url = inURL;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The name of the resouce. */
  protected @Nonnull String m_name;

  /** The url of the resource. */
  protected @Nullable URL m_url;

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /** Special presets, mainly for testing. */
  private static @Nullable Map<String, Resource> s_presets;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- files ---------------------------------

  /**
   * Determine and return the files represented by this resource.
   *
   * @return    a list of filenames inside this resource.
   *
   */
  public abstract @Nonnull List<String> files();

  //........................................................................
  //------------------------------- toString -------------------------------

  /**
   * Convert the resource into a human readable string.
   *
   * @return      the string representation
   *
   */
  public String toString()
  {
    if(m_url != null)
      return m_url.toString();

    return m_name + " (invalid)";
  }

  //........................................................................

  //--------------------------------- get ----------------------------------

  /**
   * Get the resource represented by the given name.
   *
   * @param       inName      the name of the resource, relative to the
   *                          classpath
   *
   * @return      the resource for this name
   *
   */
  public static @Nonnull Resource get(@Nonnull String inName)
  {
    String name = inName;

    if(s_presets != null)
    {
      Resource preset = s_presets.get(name);
      if(preset != null)
        return preset;
    }

    URL url = Resource.class.getResource(Files.concatenate("/", name));
    String protocol = url != null ? url.getProtocol() : null;

    if("jar".equals(protocol))
      return new JarResource(name, url);

    return new FileResource(name, url);
  }

  //........................................................................
  //--------------------------------- has ----------------------------------

  /**
   * Chbeck if the given resource is availabe in the system.
   *
   * @param       inName the name of the resource
   *
   * @return      true if found, false if not
   *
   */
  public static boolean has(String inName)
  {
    return Files.class.getResource(Files.concatenate("/", inName)) != null;
  }

  //........................................................................
  //--------------------------------- has ----------------------------------

  /**
   * Chbeck if the given resource is availabe as a thumbnail.
   *
   * @param       inName the name of the resource
   *
   * @return      true if found, false if not
   *
   */
  public static boolean hasThumbnail(String inName)
  {
    return Files.class.getResource
      (Files.concatenate("/", Files.asThumbnail(inName))) != null;
  }

  //........................................................................
  //---------------------------- hasResource -------------------------------

  /**
   * Chbeck if the given resource contains the given sub resource.
   *
   * @param       inName the name of the sub resource
   *
   * @return      true if found, false if not
   *
   */
  public boolean hasResource(String inName)
  {
    return Resource.has(inName);
  }

  //........................................................................
  //-------------------------------- asFile --------------------------------

  /**
   * Return the resource as a file in the file system.
   *
   * @return  the File, can be null
   *
   */
  public @Nullable File asFile()
  {
    if(m_url == null)
      return null;

    // requires some replacements for windows...
    return new File(m_url.getFile().replaceAll("%20", " "));
  }

  //........................................................................
  //------------------------------- getInput -------------------------------

  /**
   * Get the input stream to read the resource.
   *
   * @return      the input stream to read from
   *
   */
  public InputStream getInput()
  {
    return FileResource.class.getResourceAsStream(m_name);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- write ---------------------------------

  /**
   * Write the resources to the given output.
   *
   * @param       inOutput the output stream to write to
   *
   * @return      true if writing ok, false if not
   *
   */
  public boolean write(@Nonnull OutputStream inOutput)
  {
    InputStream input = getInput();

    if(input == null)
    {
      Log.warning("cannot obtain input stream for " + m_name);
      return false;
    }

    try
    {
      byte []buffer = new byte[10000];
      for(int read = input.read(buffer); read > 0;
          read = input.read(buffer))
        inOutput.write(buffer, 0, read);
    }
    catch(java.io.IOException e)
    {
      Log.warning("Could not write static file: " + e);
      return false;
    }
    finally
    {
      try
      {
        input.close();
      }
      catch(IOException e)
      {
        Log.warning("cannot close input stream for " + this);
      }
    }

    return true;
  }

  //........................................................................
  //-------------------------------- write ---------------------------------

  /**
   * Write the resources to the given output.
   *
   * @param       inWriter the output writer to write to
   *
   * @return      true if writing ok, false if not
   *
   */
  public boolean write(@Nonnull HTMLWriter inWriter)
  {
    InputStream inputStream = FileResource.class.getResourceAsStream(m_name);

    if(inputStream == null)
    {
      Log.warning("cannot obtain input stream for '" + m_name + "'");
      return false;
    }

    BufferedReader input =
      new BufferedReader(new InputStreamReader(inputStream));

    try
    {
      String line = input.readLine();

      if(line != null && line.startsWith("title:"))
      {
        inWriter.title(line.substring(6));
        line = input.readLine();
      }

      for(; line != null; line = input.readLine())
        inWriter.add(line);
    }
    catch(IOException e)
    {
      Log.warning("cannot write to output for " + this);
      return false;
    }
    finally
    {
      try
      {
        input.close();
      }
      catch(IOException e)
      {
        Log.warning("cannot close input stream for " + this);
        return false;
      }
    }

    return true;
  }

  //........................................................................

  //-------------------------------- preset --------------------------------

  /**
   * Add a preset resource.
   *
   * @param       inName     the name of the resource
   * @param       inResource the resource to preset
   *
   */
  public static synchronized void preset(@Nonnull String inName,
                                         @Nonnull Resource inResource)
  {
    if(s_presets == null)
      s_presets = new HashMap<String, Resource>();

    s_presets.put(inName, inResource);
  }

  //........................................................................
  //----------------------------- clearPreset ------------------------------

  /**
   * Clears the preset with the given name, if it is defined.
   *
   * @param       inName the name of the preset to clear
   *
   */
  public static synchronized void clearPreset(String inName)
  {
    if(s_presets == null)
      return;

    s_presets.remove(inName);

    if(s_presets.isEmpty())
      s_presets = null;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //--------------------------------------------------------------- nested

    /** A resource used for testing. */
    public static final class TestResource extends Resource
    {
      /**
       * Create the test resource.
       *
       * @param inName  the name of the resource
       * @param inFiles the files to be returned
       */
      public TestResource(@Nonnull String inName, @Nonnull String ... inFiles)
      {
        super("test", null);

        m_name = inName;
        for(String file : inFiles)
          m_files.add(file);
      }

      /** The name of the resource. */
      private @Nonnull String m_name;

      /** The files represented. */
      private @Nonnull List<String> m_files = new java.util.ArrayList<String>();

      @Override
      public @Nonnull List<String> files()
      {
        return new ArrayList<String>(m_files);
      }

      @Override
      public boolean hasResource(@Nonnull String inName)
      {
        return m_name.equals(inName.substring(0, m_name.length()))
          && m_files.contains(inName.substring(m_name.length() + 1));
      }

      @Override
      public String toString()
      {
        return m_name + ": " + m_files;
      }
    }

    //......................................................................

    //----- resources ------------------------------------------------------

    /** resources Test. */
    @org.junit.Test
    public void resources()
    {
      assertNotNull("unknown", Resource.get("guru/gugus"));
      assertNull("unknown", Resource.get("guru/gugus").m_url);

      // now for a directory
      assertPattern("file", "file:/.*/css/",
                    Resource.get("css").toString());

      // now for a JAR file
      assertPattern("jar", "jar:file:/.*/test/test.jar!/dir",
                    Resource.get("dir").toString());

      // invalid protocol
      assertNotNull("http", Resource.get("http://www.ixitxachitls.net"));
      assertNull("http", Resource.get("http://www.ixitxachitls.net").m_url);
    }

    //......................................................................
    //----- has ------------------------------------------------------------

    /** has Test. */
    @org.junit.Test
    public void has()
    {
      assertFalse("unknown", Resource.has("guru/gugus"));
      assertTrue("file", Resource.has("css"));
      assertTrue("jar", Resource.has("dir"));
      assertTrue("jar file", Resource.has("dir/NPCs.png"));
      assertFalse("not in jar", Resource.has("dir/guru.gugus"));
    }

    //......................................................................
  }

  //........................................................................
}
