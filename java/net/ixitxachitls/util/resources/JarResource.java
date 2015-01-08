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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.google.common.base.Optional;

import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;


/**
 * A resource representing a jar file or directory.
 *
 * @file          JarResource.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

public class JarResource extends Resource
{
  /**
   * Create the jar resource.
   *
   * @param    inName the name of the file this resource represents
   * @param    inURL  the url to the resource
   */
  JarResource(String inName, Optional<URL> inURL)
  {
    super(inName, inURL);
  }

  @Override
  public List<String> files()
  {
    List<String> result = new ArrayList<String>();

    if(!m_url.isPresent())
      return result;

    JarFile jar = null;
    try
    {
      jar = new JarFile(Strings.getPattern(m_url.get().getFile(),
                                           "^file:(.*)!"));
      String dir = Strings.getPattern(m_url.get().getFile(), "^file:.*!/(.+)");

      for(java.util.Enumeration<?> i = jar.entries(); i.hasMoreElements(); )
      {
        ZipEntry file = (ZipEntry)i.nextElement();

        // only files in the given directory and not the directory itself
        if(!file.getName().startsWith(dir)
           || file.getName().length() <= dir.length() + 1)
          continue;

        result.add(file.getName().substring(dir.length() + 1));
      }


      return result;
    }
    catch(java.io.IOException e)
    {
      Log.warning("could not open jar file '"
                  + Strings.getPattern(m_url.get().getFile(), ":(.*)!") + "'");

      return result;
    }
    finally
    {
      try
      {
        if(jar != null)
          jar.close();
      }
      catch(IOException e)
      {
        Log.warning("could not close jar file '" + m_url.get().getFile() + "'");
      }
    }
  }

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** directory Test. */
    @org.junit.Test
    public void directory()
    {
      Resource resource = new JarResource
          ("/dir",
           Optional.fromNullable(FileResource.class.getResource("/dir")));

      assertContentAnyOrder("dir", resource.files(), "readme.txt", "NPCs.png");
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
          ("/dir/readme.txt",
           Optional.fromNullable(FileResource.class.getResource
               ("/dir/readme.txt")));

      try (ByteArrayOutputStream output = new ByteArrayOutputStream())
      {
        assertTrue("writing", resource.write(output));
        assertPattern("content", ".*70x200 points.*", output.toString());
      }
    }
  }
}
