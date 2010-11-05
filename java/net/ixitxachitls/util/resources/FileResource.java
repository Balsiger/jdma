/******************************************************************************
 * Copyright (c) 2002-2007 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

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

public class FileResource extends Resource
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- FileResource -----------------------------

  /**
   * Create the file resource.
   *
   * @param    inName the name of the file this resource represents
   *
   */
  public FileResource(@Nonnull URL inName)
  {
    super(inName);
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
  public @Nonnull List<String> files()
  {
    List<String> result = new ArrayList<String>();

    File file = new File(m_name.getFile().replaceAll("%20", " "));

    // not really a directory
    if(file.isDirectory())
      for(File entry : file.listFiles())
        result.add(entry.getName());
    else
      result.add(file.getName());

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
        new FileResource(FileResource.class.getResource("/config/test"));

      assertContentAnyOrder("config/test", resource.files(),
                            ".svn", "project", "project.config", "test",
                            "test path.config", "test test.config",
                            "test.config", "test.properties",
                            "test_de.properties", "test_de_CH.properties",
                            "test_en.properties");
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
        new FileResource(FileResource.class.getResource
                         ("/config/test/test.config"));

      assertContentAnyOrder("config/test/test.config", resource.files(),
                            "test.config");

      resource = new FileResource(new URL("file:/guru"));
      assertContentAnyOrder("non existant", resource.files(), "guru");
    }

    //......................................................................
  }

  //........................................................................
}
