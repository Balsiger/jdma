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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A resource for the file system.
 *
 * @file          FileResource.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class FileResource extends Resource
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- FileResource -----------------------------

  /**
   * Create the file resource.
   *
   * @param    inName the name of the file this resource represents
   * @param    inURL  the url to the resource
   *
   */
  FileResource(String inName, @Nullable URL inURL)
  {
    super(inName, inURL);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables
  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- files ---------------------------------

  /**
   * Determine and return the files represented by this resource.
   *
   * @return    a list of filenames inside this resource.
   *
   */
  @Override
  public List<String> files()
  {
    List<String> result = new ArrayList<String>();

    if(m_url == null)
      return result;

    File file = asFile();

    // not really a directory
    if(file.isDirectory())
      for(File entry : file.listFiles())
        result.add(entry.getName());
    else
      result.add(file.getName());

    Collections.sort(result);
    return result;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- directory ------------------------------------------------------

    /** directory Test. */
    @org.junit.Test
    public void directory()
    {
      Resource resource =
        new FileResource("css",
                         FileResource.class.getResource("/css"));

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
                              ".svn", "gui.css", "jdma.css", "smoothness");
      }

      // invalid
      resource = new FileResource("guru", null);
      assertEquals("empty size", 0, resource.files().size());
    }

    //......................................................................
    //----- file -----------------------------------------------------------

    /**
     * file Test.
     *
     * @throws Exception  cover any exceptions
     *
     */
    @org.junit.Test
    public void file() throws Exception
    {
      Resource resource =
        new FileResource("/css/jdma.css",
                         FileResource.class.getResource
                         ("/css/jdma.css"));

      assertContentAnyOrder("css/jdma.css", resource.files(),
                            "jdma.css");

      resource = new FileResource("guru", new URL("file:/guru"));
      assertContentAnyOrder("non existant", resource.files(), "guru");
    }

    //......................................................................
    //----- write ----------------------------------------------------------

    /** The write Test. */
    @org.junit.Test
    public void write()
    {
      Resource resource =
        new FileResource("/css/jdma.css",
                         FileResource.class.getResource("/css"));

      ByteArrayOutputStream output = new ByteArrayOutputStream();

      assertTrue("writing", resource.write(output));
      assertPattern("content", ".*A\\.Product:hover.*", output.toString());

      // invalid resource
      resource = new FileResource("guru", null);
      assertFalse("writing", resource.write(output));

      m_logger.addExpected("WARNING: cannot obtain input stream for guru");
    }

    //......................................................................
  }

  //........................................................................
}
