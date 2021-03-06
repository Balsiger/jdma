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

import com.google.common.base.Charsets;
import com.google.common.base.Optional;

import net.ixitxachitls.output.html.HTMLWriter;
import net.ixitxachitls.util.Files;
import net.ixitxachitls.util.logging.Log;

/**
 * A handler for generic resources. This abstracts away handling of resources
 * from the file system and jar files (or any other source).
 *
 * @file          ResourceHandler.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

public abstract class Resource
{
  /**
   * Create the resource.
   *
   * @param       inName the name of the resource
   * @param       inURL  the url to the resource
   */
  protected Resource(String inName, Optional<URL> inURL)
  {
    m_name = inName;
    m_url = inURL;
  }

  /** The name of the resouce. */
  protected String m_name;

  /** The url of the resource. */
  protected Optional<URL> m_url;

  /** The id for serialization. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 1L;

  /** Special presets, mainly for testing. */
  private static final Map<String, Resource> s_presets = new HashMap<>();

  /**
   * Determine and return the files represented by this resource.
   *
   * @return    a list of filenames inside this resource.
   */
  public abstract List<String> files();

  @Override
  public String toString()
  {
    if(m_url.isPresent())
      return m_url.get().toString();

    return m_name + " (invalid)";
  }

  /**
   * Get the resource represented by the given name.
   *
   * @param       inName      the name of the resource, relative to the
   *                          classpath
   *
   * @return      the resource for this name
   */
  public static Resource get(String inName)
  {
    String name = inName;

    if(!s_presets.isEmpty())
    {
      Resource preset = s_presets.get(name);
      if(preset != null)
        return preset;
    }

    URL url = Resource.class.getResource(Files.concatenate("/", name));
    String protocol = url != null ? url.getProtocol() : null;

    if(url == null)
      Log.warning("Cannot find resource '/" + name + "' in "
                      + System.getProperty("java.class.path"));

    if("jar".equals(protocol))
      return new JarResource(name, Optional.fromNullable(url));

    return new FileResource("/" + name, Optional.fromNullable(url));
  }

  /**
   * Chbeck if the given resource is availabe in the system.
   *
   * @param       inName the name of the resource
   *
   * @return      true if found, false if not
   */
  public static boolean has(String inName)
  {
    return Files.class.getResource(Files.concatenate("/", inName)) != null;
  }

  /**
   * Check if the given resource is available as a thumbnail.
   *
   * @param       inName the name of the resource
   *
   * @return      true if found, false if not
   */
  public static boolean hasThumbnail(String inName)
  {
    return Files.class.getResource
      (Files.concatenate("/", Files.asThumbnail(inName))) != null;
  }

  /**
   * Check if the given resource contains the given sub resource.
   *
   * @param       inName the name of the sub resource
   *
   * @return      true if found, false if not
   */
  public boolean hasResource(String inName)
  {
    return Resource.has(inName);
  }

  /**
   * Return the resource as a file in the file system.
   *
   * @return  the File, can be null
   *
   */
  public Optional<File> asFile()
  {
    if(!m_url.isPresent())
      return Optional.absent();

    // requires some replacements for windows...
    return Optional.of(new File(m_url.get().getFile().replaceAll("%20", " ")));
  }

  /**
   * Get the input stream to read the resource.
   *
   * @return      the input stream to read from
   */
  public Optional<InputStream> getInput()
  {
    return Optional.fromNullable(Resource.class.getResourceAsStream(m_name));
  }

  /**
   * Get the whole contents of the resource as a string. Line termination is
   * normalized to \n.
   *
   * @return      the contents as a string
   */
  public String read()
  {
    Optional<InputStream> input = getInput();
    if(!input.isPresent())
      return "(invalid resource for '" + m_name + "')\n";

    BufferedReader reader =
      new BufferedReader(new InputStreamReader(input.get(), Charsets.UTF_8));

    StringBuilder buffer = new StringBuilder();

    try
    {
      for(String line = reader.readLine(); line != null;
          line = reader.readLine())
      {
        buffer.append(line);
        buffer.append('\n');
      }
    }
    catch(java.io.IOException e)
    {
      Log.warning("error when reading resource: " + e);
    }
    finally
    {
      try
      {
        reader.close();
      }
      catch(java.io.IOException e)
      {
        Log.error("could not close reader for reading resource: " + e);
      }
    }

    return buffer.toString();
  }

  /**
   * Write the resources to the given output.
   *
   * @param       inOutput the output stream to write to
   *
   * @return      true if writing ok, false if not
   */
  public boolean write(OutputStream inOutput)
  {
    Optional<InputStream> input = getInput();

    if(!input.isPresent())
    {
      Log.warning("cannot obtain input stream for " + m_name);
      return false;
    }

    try
    {
      byte []buffer = new byte[10000];
      for(int read = input.get().read(buffer); read > 0;
          read = input.get().read(buffer))
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
        input.get().close();
      }
      catch(IOException e)
      {
        Log.warning("cannot close input stream for " + this);
      }
    }

    return true;
  }

  /**
   * Write the resources to the given output.
   *
   * @param       inWriter the output writer to write to
   *
   * @return      true if writing ok, false if not
   */
  @Deprecated
  public boolean write(HTMLWriter inWriter)
  {
    InputStream inputStream = FileResource.class.getResourceAsStream(m_name);

    if(inputStream == null)
    {
      Log.warning("cannot obtain input stream for '" + m_name + "'");
      return false;
    }

    BufferedReader input =
      new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));

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

  /**
   * Add a preset resource.
   *
   * @param       inName     the name of the resource
   * @param       inResource the resource to preset
   *
   */
  public static synchronized void preset(String inName, Resource inResource)
  {
    s_presets.put(inName, inResource);
  }

  /**
   * Clears the preset with the given name, if it is defined.
   *
   * @param       inName the name of the preset to clear
   */
  public static synchronized void clearPreset(String inName)
  {
    s_presets.remove(inName);
  }

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** A resource used for testing. */
    public static final class TestResource extends Resource
    {
      /**
       * Create the test resource.
       *
       * @param inName  the name of the resource
       * @param inFiles the files to be returned
       */
      public TestResource(String inName, String ... inFiles)
      {
        super("test", null);

        m_name = inName;
        for(String file : inFiles)
          m_files.add(file);
      }

      /** The name of the resource. */
      private String m_name;

      /** The files represented. */
      private List<String> m_files = new java.util.ArrayList<String>();

      @Override
      public List<String> files()
      {
        return new ArrayList<String>(m_files);
      }

      @Override
      public boolean hasResource(String inName)
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

    /** resources Test. */
    @org.junit.Test
    public void resources()
    {
      assertNotNull("unknown", Resource.get("guru/gugus"));
      assertFalse("unknown", Resource.get("guru/gugus").m_url.isPresent());

      // now for a directory
      assertPattern("file", "file:/.*/css/?",
                    Resource.get("css").toString());

      // now for a JAR file
      assertPattern("jar", "jar:file:/.*/test/test.jar!/dir",
                    Resource.get("dir").toString());

      // invalid protocol
      assertNotNull("http", Resource.get("http://www.ixitxachitls.net"));
      assertFalse
          ("http",
           Resource.get("http://www.ixitxachitls.net").m_url.isPresent());

      m_logger.addExpectedPattern(
          "WARNING: Cannot find resource '/guru/gugus' in .*");
      m_logger.addExpectedPattern(
          "WARNING: Cannot find resource '/guru/gugus' in .*");
      m_logger.addExpectedPattern(
          "WARNING: Cannot find resource '/http://www.ixitxachitls.net' in .*");
      m_logger.addExpectedPattern(
          "WARNING: Cannot find resource '/http://www.ixitxachitls.net' in .*");
    }

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

  }
}
