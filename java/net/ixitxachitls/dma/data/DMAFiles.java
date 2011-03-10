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

package net.ixitxachitls.dma.data;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableSet;

import net.ixitxachitls.util.Files;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.resources.Resource;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * Class to access various dma files.
 *
 * @file          DMAFiles.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public final class DMAFiles
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- DMAFiles -------------------------------

  /**
   * Static class, preventing instantiation.
   */
  private DMAFiles()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The name of a default image. */
  private static final @Nonnull String s_defaultImage =
    Config.get("files.default.image", "dummy.png");

  /** The base path to all dma files. */
  private static final @Nonnull String s_basePath =
    Config.get("files.path.base", "/files");

  /** The special file names that we want to return first. */
  private static final Set<String> s_specialImages =
    ImmutableSet.of("cover", "official", "unofficial", "main");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- mainImage ------------------------------

  /**
   * Get the main image for the given id and type.
   *
   * @param       inID     the id of the entry
   * @param       inType   the path to the type
   *
   * @return      the name of the file to use for the given data
   *
   */
  public static @Nonnull String mainImage(@Nonnull String inID,
                                          @Nonnull String inType)
  {
    Resource resources =
      Resource.get(Files.concatenate(s_basePath, inType, inID));

    List<String> files = resources.files();

    // filter out non images (just by extension)
    for(Iterator<String> i = files.iterator(); i.hasNext(); )
      if(!Files.isImage(i.next()))
        i.remove();

    if(files.isEmpty())
      return defaultImage(inType);

    if(files.size() == 1)
      return files.get(0);

    for(String file : files)
      if(Files.file(file).equals(inID))
        return file;

    for(String file : files)
      if(Files.file(file).contains(inID))
        return file;

    for(String file : files)
      if(s_specialImages.contains(Files.file(file)))
        return file;

    // As a fallback we return the first image that we find
    return files.get(0);
  }

  //........................................................................
  //----------------------------- defaultImage -----------------------------

  /**
   * Get the name of the default image for the given type.
   *
   * @param       inType the type to get the default image for
   *
   * @return      the name of the default image, without leading path
   *
   */
  public static @Nonnull String defaultImage(@Nonnull String inType)
  {
    return Files.concatenate(inType, s_defaultImage);
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
    //----- mainImage ------------------------------------------------------

    /** The mainImage Test. */
    @org.junit.Test
    public void mainImage()
    {
      // no images
      Resource.preset("/files/type/test",
                      new Resource.Test.TestResource("/files/type/test"));
      assertEquals("no images", "type/dummy.png",
                   DMAFiles.mainImage("test", "type"));

      Resource.preset("/files/type/test",
                      new Resource.Test.TestResource("/files/type/test",
                                                     "single.png"));
      assertEquals("single image", "/files/type/test/single.png",
                   DMAFiles.mainImage("test", "type"));

      Resource.preset("/files/type/test",
                      new Resource.Test.TestResource("/files/type/test",
                                                     "single.pdf", "other.doc",
                                                     "some.exe", "..", "."));
      assertEquals("no image type", "type/dummy.png",
                   DMAFiles.mainImage("test", "type"));

      Resource.preset("/files/type/test",
                      new Resource.Test.TestResource("/files/type/test",
                                                     "first.png", "second.jpg",
                                                     "test.png", "..", "."));
      assertEquals("image with id", "/files/type/test/test.png",
                   DMAFiles.mainImage("test", "type"));

      Resource.preset("/files/type/test",
                      new Resource.Test.TestResource("/files/type/test",
                                                     "first.png", "second.jpg",
                                                     "some test image.png",
                                                     "..", "."));
      assertEquals("image with id and some",
                   "/files/type/test/some test image.png",
                   DMAFiles.mainImage("test", "type"));

      Resource.preset("/files/type/test",
                      new Resource.Test.TestResource("/files/type/test",
                                                     "first.png", "second.jpg",
                                                     "official.png",
                                                     "..", "."));
      assertEquals("special image", "/files/type/test/official.png",
                   DMAFiles.mainImage("test", "type"));

      Resource.preset("/files/type/test",
                      new Resource.Test.TestResource("/files/type/test",
                                                     "first.png", "second.jpg",
                                                     "guru.png",
                                                     "..", "."));
      assertEquals("just the first", "/files/type/test/first.png",
                   DMAFiles.mainImage("test", "type"));

      Resource.clearPreset("test");
    }

    //......................................................................
    //----- defaultImage ---------------------------------------------------

    /** The defaultImage Test. */
    @org.junit.Test
    public void defaultImage()
    {
      assertEquals("default", "type/dummy.png", DMAFiles.defaultImage("type"));
    }

    //......................................................................
  }

  //........................................................................
}
