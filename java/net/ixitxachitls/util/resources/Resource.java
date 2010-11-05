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

import java.net.URL;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.util.Files;

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
   *
   */
  public Resource(@Nonnull URL inName)
  {
    m_name = inName;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The name of the resouce. */
  protected @Nonnull URL m_name;

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
    return m_name.toString();
  }

  //........................................................................

  //--------------------------------- get ----------------------------------

  /**
   * Get the resource represented by the given name.
   *
   * @param       inName the name of the resource, relative to the classpath
   *
   * @return      the resource for this name
   *
   */
  public static @Nullable Resource get(@Nonnull String inName)
  {
    URL url = Resource.class.getResource(Files.concatenate("/", inName));

    if(url == null)
      return null;

    String protocol = url.getProtocol();

    if("file".equals(protocol))
      return new FileResource(url);
    else if("jar".equals(protocol))
      return new JarResource(url);

    return null;
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

  //........................................................................

  //----------------------------------------------------------- manipulators

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- resources ------------------------------------------------------

    /** resources Test. */
    @org.junit.Test
    public void resources()
    {
      assertNull("unknown", Resource.get("guru/gugus"));

      // now for a directory
      assertPattern("file", "file:/.*/config/test",
                    Resource.get("config/test").toString());

      // now for a JAR file
      assertPattern("jar", "jar:file:/.*/test/test.jar!/dir",
                    Resource.get("dir").toString());

      // invalid protocol
      assertNull("http", Resource.get("http://www.ixitxachitls.net"));
    }

    //......................................................................
    //----- has ------------------------------------------------------------

    /** has Test. */
    @org.junit.Test
    public void has()
    {
      assertFalse("unknown", Resource.has("guru/gugus"));
      assertTrue("file", Resource.has("config/test"));
      assertTrue("jar", Resource.has("dir"));
      assertTrue("jar file", Resource.has("dir/NPCs.png"));
      assertFalse("not in jar", Resource.has("dir/guru.gugus"));
    }

    //......................................................................
  }

  //........................................................................
}
