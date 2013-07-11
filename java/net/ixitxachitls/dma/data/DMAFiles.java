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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
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
@ParametersAreNonnullByDefault
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
  private static final String DEFAULT_IMAGE =
    Config.get("files.default.image", "dummy.png");

  /** The base path to all dma files. */
  private static final String BASE_PATH =
    Config.get("files.path.base", "/files");

  /** The directory with the icons. */
  private static final String DIR_ICONS =
    Config.get("resource:html/dir.icons", "/icons");

  /** The special file names that we want to return first. */
  private static final Set<String> SPECIAL_IMAGES =
    ImmutableSet.of("cover", "official", "unofficial", "main");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- mainImage ------------------------------

  /**
   * Get the main image for the given id and type.
   *
   * @param       inID       the id of the entry
   * @param       inType     the path to the type
   * @param       inBaseType the type of the base entries (if any)
   * @param       inBaseIDs  the ids of the base entries (if any)
   *
   * @return      the name of the file to use for the given data
   *
   */
  public static String mainImage(String inID, String inType,
                                 @Nullable String inBaseType,
                                 @Nullable String ... inBaseIDs)
  {
    String file = mainImage(inID, inType);
    if(file != null)
      return file;

    if(inBaseIDs != null)
      for(String id : inBaseIDs)
      {
        file = mainImage(id, inBaseType);
        if(file != null)
          return file;
      }

    return defaultImage(inType);
  }

  //........................................................................
  //------------------------------ mainImage -------------------------------

  /**
   * Get the main iamge for the given id and type.
   *
   * @param       inID     the id of the entry
   * @param       inType   the path to the type
   *
   * @return      the name of the file to use for the given data
   *
   */
  private static @Nullable String mainImage(String inID, String inType)
  {
   Resource resources =
     Resource.get(Files.concatenate(BASE_PATH, inType, inID));

   List<String> files = resources.files();

    // filter out non images (just by extension)
    for(Iterator<String> i = files.iterator(); i.hasNext(); )
      if(!Files.isImage(i.next()))
        i.remove();

    if(files.isEmpty())
      return null;

    if(files.size() == 1)
      return Files.concatenate(BASE_PATH, inType, inID, files.get(0));

    for(String file : files)
      if(Files.file(file).equals(inID))
        return Files.concatenate(BASE_PATH, inType, inID, file);

    for(String file : files)
      if(Files.file(file).contains(inID))
        return Files.concatenate(BASE_PATH, inType, inID, file);

    for(String file : files)
      if(SPECIAL_IMAGES.contains(Files.file(file)))
        return Files.concatenate(BASE_PATH, inType, inID, file);

    // As a fallback we return the first image that we find
    return Files.concatenate(BASE_PATH, inType, inID, files.get(0));
  }

  //........................................................................

  //----------------------------- otherFiles -------------------------------

  /**
   * Get all the other images for the given id.
   *
   * @param       inID       the id of the entry
   * @param       inType     the path to the type
   * @param       inBaseType the type of the base entries (if any)
   * @param       inBaseIDs  the ids of the base entries (if any)
   *
   * @return      the names of the file to use for the given data
   *
   */
  public static List<String> otherFiles(String inID, String inType,
                                        @Nullable String inBaseType,
                                        @Nullable String ... inBaseIDs)
  {
    String main = mainImage(inID, inType, inBaseType, inBaseIDs);

    List<String> withPath = new ArrayList<String>();
    otherFiles(withPath, main, inID, inType);

    if(inBaseIDs != null)
      for(String id : inBaseIDs)
        otherFiles(withPath, main, id, inBaseType);

    Collections.sort(withPath);

    return withPath;
  }

  //........................................................................
  //------------------------------ otherFiles ------------------------------

  /**
   * Get all the other images for the given id.
   *
   * @param       ioFiles    the list of files to add to
   * @param       inMain     the name of the main image
   * @param       inID       the id of the entry
   * @param       inType     the path to the type
   *
   * @return      the names of the file to use for the given data
   *
   */
  private static List<String> otherFiles(List<String> ioFiles,
                                         String inMain,
                                         String inID,
                                         String inType)
  {
    Resource resources =
      Resource.get(Files.concatenate(BASE_PATH, inType, inID));

    for(String file : resources.files())
      if(!Files.file(file).equals(Files.file(inMain))
         && !Files.isThumbnail(file)
         && !Files.isIgnored(file))
        ioFiles.add(Files.concatenate(BASE_PATH, inType, inID, file));

    return ioFiles;
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
  public static String defaultImage(String inType)
  {
    return Files.concatenate(DIR_ICONS, inType + "-" + DEFAULT_IMAGE);
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
      assertEquals("no images", "/icons/type-dummy.png",
                   DMAFiles.mainImage("test", "type", null));

      Resource.preset("/files/type/test",
                      new Resource.Test.TestResource("/files/type/test",
                                                     "single.png"));
      assertEquals("single image", "/files/type/test/single.png",
                   DMAFiles.mainImage("test", "type", null));

      Resource.preset("/files/type/test",
                      new Resource.Test.TestResource("/files/type/test",
                                                     "single.pdf", "other.doc",
                                                     "some.exe", "..", "."));
      assertEquals("no image type", "/icons/type-dummy.png",
                   DMAFiles.mainImage("test", "type", null));

      Resource.preset("/files/type/test",
                      new Resource.Test.TestResource("/files/type/test",
                                                     "first.png", "second.jpg",
                                                     "test.png", "..", "."));
      assertEquals("image with id", "/files/type/test/test.png",
                   DMAFiles.mainImage("test", "type", null));

      Resource.preset("/files/type/test",
                      new Resource.Test.TestResource("/files/type/test",
                                                     "first.png", "second.jpg",
                                                     "some test image.png",
                                                     "..", "."));
      assertEquals("image with id and some",
                   "/files/type/test/some test image.png",
                   DMAFiles.mainImage("test", "type", null));

      Resource.preset("/files/type/test",
                      new Resource.Test.TestResource("/files/type/test",
                                                     "first.png", "second.jpg",
                                                     "official.png",
                                                     "..", "."));
      assertEquals("special image", "/files/type/test/official.png",
                   DMAFiles.mainImage("test", "type", null));

      Resource.preset("/files/type/test",
                      new Resource.Test.TestResource("/files/type/test",
                                                     "first.png", "second.jpg",
                                                     "guru.png",
                                                     "..", "."));
      assertEquals("just the first", "/files/type/test/first.png",
                   DMAFiles.mainImage("test", "type", null));

      Resource.clearPreset("/files/type/test");
    }

    //......................................................................
    //----- defaultImage ---------------------------------------------------

    /** The defaultImage Test. */
    @org.junit.Test
    public void defaultImage()
    {
      assertEquals("default", "/icons/type-dummy.png",
                   DMAFiles.defaultImage("type"));
    }

    //......................................................................
    //----- otherFiles -----------------------------------------------------

    /** The mainImage Test. */
    @org.junit.Test
    public void otherFiles()
    {
      // no images
      Resource.preset("/files/type/test",
                      new Resource.Test.TestResource("/files/type/test",
                                                     "single.pdf", "..", ".",
                                                     "some.file", "CVS",
                                                     "test.png",
                                                     "backup~", "image.png"));
      assertContent("other images",
                    DMAFiles.otherFiles("test", "type", null, (String [])null),
                    "/files/type/test/image.png",
                    "/files/type/test/single.pdf",
                    "/files/type/test/some.file");

      Resource.clearPreset("/files/type/test");
    }

    //......................................................................
  }

  //........................................................................
}
