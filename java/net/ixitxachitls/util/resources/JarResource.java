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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A resource representing a jar file or directory.
 *
 * @file          JarResource.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class JarResource extends Resource
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ JarResource ------------------------------

  /**
   * Create the jar resource.
   *
   * @param    inName the name of the file this resource represents
   * @param    inURL  the url to the resource
   *
   */
  JarResource(@Nonnull String inName, @Nullable URL inURL)
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
  public @Nonnull List<String> files()
  {
    List<String> result = new ArrayList<String>();

    if(m_url == null)
      return result;

    try
    {
      JarFile jar =
        new JarFile(Strings.getPattern(m_url.getFile(), "^file:(.*)!"));
      String dir = Strings.getPattern(m_url.getFile(), "^file:.*!/(.+)");

      for(java.util.Enumeration i = jar.entries(); i.hasMoreElements(); )
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
                  + Strings.getPattern(m_url.getFile(), ":(.*)!") + "'");

      return result;
    }
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
        new JarResource("/dir", FileResource.class.getResource("/dir"));

      assertContentAnyOrder("dir", resource.files(), "readme.txt", "NPCs.png");
    }

    //......................................................................
    //----- write ----------------------------------------------------------

    /** The write Test. */
    @org.junit.Test
    public void write()
    {
      Resource resource =
        new FileResource("/dir/readme.txt",
                         FileResource.class.getResource("/dir/readme.txt"));

      ByteArrayOutputStream output = new ByteArrayOutputStream();

      assertTrue("writing", resource.write(output));
      assertPattern("content", ".*70x200 points.*", output.toString());
    }

    //......................................................................
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}