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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Optional;

/**
 * A resource for the file system.
 *
 * @file          FileResource.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

public class FileResource extends Resource
{
  /**
   * Create the file resource.
   *
   * @param    inName the name of the file this resource represents
   * @param    inURL  the url to the resource
   */
  public FileResource(String inName, Optional<URL> inURL)
  {
    super(inName, inURL);
  }

  @Override
  public List<String> files()
  {
    List<String> result = new ArrayList<String>();

    if(!m_url.isPresent())
      return result;

    Optional<File> file = asFile();
    if(!file.isPresent())
      return result;

    // not really a directory
    if(file.get().isDirectory())
      for(File entry : file.get().listFiles())
        result.add(entry.getName());
    else
      result.add(file.get().getName());

    Collections.sort(result);
    return result;
  }

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Directory Test. */
    @org.junit.Test
    public void directory()
    {
      Resource resource = new FileResource
          ("css",
           Optional.fromNullable(FileResource.class.getResource("/css")));

      //on windows, the directory 'css' is copied to WEB-INF/classes
      //without the '.svn' directory
      if (System.getProperty("os.name").startsWith("Windows"))
      {
        assertContentAnyOrder("css", resource.files(),
                              "gui.css", "jdma.css", "smoothness");
      }
      else
      {
        assertContentAnyOrder("css", resource.files(),
                              "gui.css", "jdma.css", "smoothness");
      }

      // invalid
      resource = new FileResource("guru", Optional.<URL>absent());
      assertEquals("empty size", 0, resource.files().size());
    }

    /**
     * file Test.
     *
     * @throws Exception  cover any exceptions
     */
    @org.junit.Test
    public void file() throws Exception
    {
      Resource resource = new FileResource
          ("/css/jdma.css",
           Optional.fromNullable(FileResource.class.getResource
               ("/css/jdma.css")));

      assertContentAnyOrder("css/jdma.css", resource.files(),
                            "jdma.css");

      resource = new FileResource("guru", Optional.of(new URL("file:/guru")));
      assertContentAnyOrder("non existant", resource.files(), "guru");
    }

    /**
     * The write Test.
     *
     * @throws IOException when closing output buffer
     */
    @org.junit.Test
    public void write() throws IOException
    {
      Resource resource = new FileResource
          ("/css/jdma.css",
           Optional.fromNullable(FileResource.class.getResource("/css")));

      try (ByteArrayOutputStream output = new ByteArrayOutputStream())
      {
        assertTrue("writing", resource.write(output));
        assertPattern("content", ".*A\\.Product:hover.*", output.toString());

        // invalid resource
        resource = new FileResource("guru", null);
        assertFalse("writing", resource.write(output));

        m_logger.addExpected("WARNING: cannot obtain input stream for guru");
      }
    }
  }
}
