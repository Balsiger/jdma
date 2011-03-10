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

package net.ixitxachitls.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.swing.ImageIcon;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class contains some static file handling methods.
 *
 * @file          Files.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ThreadSafe
public final class Files
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Files ---------------------------------

  /**
   * Just to prevent instantiation.
   *
   */
  private Files()
  {
    // no instances
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The separator to use delimiting paths. */
  public static final char SEPARATOR = '/';

  /** The extension sepearator. */
  public static final char EXTENSION_SEPARATOR = '.';

  /** The joiner for concatenating files. */
  private static final Joiner s_pathJoiner = Joiner.on(SEPARATOR).skipNulls();

  /** The extensions for backups. */
  private static final String s_extBackup = ".bak";

  /** The date format for backup files. */
  private static String s_backupFormat =
    Config.get("resource:format.backup.date", "yyyy-MM-dd-HH-mm-ss");

  /** Extensions supported as images. */
  private static final Set<String> s_imageExtensions =
    ImmutableSet.of(".png", ".jpg");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ encodeName ------------------------------

  /**
   * Encode the given filename to create a name that can potentially be used
   * on any operating system.
   *
   * The following is replaced:
   *
   * <PRE>
   *                :        -> %3A
   *                *        -> %2A
   *                &amp;    -> &
   *                &ucirc;  -> Ã»
   *                &Ucirc;  -> Ã
   *                &ocirc;  -> Ã´
   *                &Ocirc;  -> Ã
   *                &eacute; -> Ã©
   *                &Eacute; -> Ã
   * </PRE>
   *
   * @param       inName the name to encode
   *
   * @return      the encoded name
   *
   */
  public static @Nonnull String encodeName(@Nonnull String inName)
  {
    return inName.replaceAll(":", "-3A-").replaceAll("\\*", "-2A-")
      .replaceAll("&amp;", "&").replaceAll("&ucirc;", "Ã»")
      .replaceAll("&Ucirc;", "Ã").replaceAll("&ocirc;", "Ã´")
      .replaceAll("&Ocirc;", "Ã").replaceAll("&eacute;", "Ã©")
      .replaceAll("&Eacute;", "Ã");
  }

  //........................................................................
  //------------------------------ decodeName ------------------------------

  /**
   * Decode the given filename to get back the name of the original.
   *
   * The following is replaced:
   *
   * <PRE>
   *                :        <- %3A
   *                *        <- %2A
   *                &amp;    <- &
   *                &ucirc;  <- Ã»
   *                &Ucirc;  <- Ã
   *                &ocirc;  <- Ã´
   *                &Ocirc;  <- Ã
   *                &eacute; <- Ã©
   *                &Eacute; <- Ã
   * </PRE>
   *
   * @param       inName the name to decode
   *
   * @return      the decoded name
   *
   */
  public static @Nonnull String decodeName(@Nonnull String inName)
  {
    return inName.replaceAll("-3A-", ":").replaceAll("-2A-", "*")
      .replaceAll("&", "&amp;").replaceAll("Ã»", "&ucirc;")
      .replaceAll("Ã", "&Ucirc;").replaceAll("Ã´", "&ocirc;")
      .replaceAll("Ã", "&Ocirc;").replaceAll("Ã©", "&eacute;")
      .replaceAll("Ã", "&Eacute;");
  }

  //........................................................................
  //----------------------------- concatenate ------------------------------

  /**
   * Concatenate the given parts to a single file name.
   *
   * @param       inParts the parts of the file name
   *
   * @return      the concatenation
   *
   */
  public static @Nonnull String concatenate(@Nonnull String ... inParts)
  {
    return s_pathJoiner.join(inParts)
      .replaceAll(SEPARATOR + "{2,}", "" + SEPARATOR);
  }

  //........................................................................
  //------------------------------ extension -------------------------------

  /**
   * Get the extension of the given file.
   *
   * @param       inName the name of the file to get the extension of
   *
   * @return      the extension, or "" if no extension was found (the '.' is
   *              returned as well)
   *
   */
  public static @Nonnull String extension(@Nonnull String inName)
  {
    int pos = inName.lastIndexOf(EXTENSION_SEPARATOR);

    if(pos < 0)
      return "";

    return inName.substring(pos);
  }

  //........................................................................
  //-------------------------------- file ----------------------------------

  /**
   * Get the file name part of the given file and path (this excludes the
   * path and the extension).
   *
   * @param       inName the name to extract from
   *
   * @return      the filename part
   *
   */
  public static @Nonnull String file(@Nonnull String inName)
  {
    // check the path for both, unix styles and windows style, to allow right
    // treatment of unix files, like HTML names, under windows
    int path = Math.max(inName.lastIndexOf("\\"), inName.lastIndexOf("/"));

    int ext = inName.lastIndexOf(EXTENSION_SEPARATOR);
    if(ext < 0)
      ext = inName.length();

    return inName.substring(path + 1, ext);
  }

  //........................................................................
  //-------------------------------- path ----------------------------------

  /**
   * Get the path part of the given filename.
   *
   * @param       inName the file name to extract from
   *
   * @return      the path part, including the trailing separator
   *
   */
  public static @Nonnull String getPath(@Nonnull String inName)
  {
    // we use both separators to be able to parse unix file names on windoooze
    // and vice versa
    int pos = Math.max(inName.lastIndexOf('/'), inName.lastIndexOf('\\'));
    if(pos < 0)
      return "";

    return inName.substring(0, pos + 1);
  }

  //........................................................................
  //------------------------------ imageSize -------------------------------

  /**
   * Get the size of the image denoted by its file name.
   *
   * @param       inFile the name of the file with the image
   *
   * @return      a pair with height & width (or <-1,-1> if the file cannot be
   *              read)
   *
   */
  public static @Nonnull Pair<Integer, Integer> imageSize
    (@Nonnull String inFile)
  {
    // determine the pictures size (use ImageIcon to load it synchronously)
    ImageIcon image = new ImageIcon(inFile);

    return new Pair<Integer, Integer>(image.getIconHeight(),
                                      image.getIconWidth());
  }

  //........................................................................
  //-------------------------------- backup --------------------------------

  /**
   * Move the file with the given name to a backup file with a unique name.
   *
   * @param       inName the name of the file to backup
   * @param       inDir  the directory into which to backup the files
   *
   * @return      the name of the backuped file or null if backup failed
   *
   */
  public static @Nullable String backup(@Nonnull String inName,
                                        @Nonnull String inDir)
  {
    // check if the file exists
    File original = new File(inName);

    if(!original.exists())
      return null;

    // try to simply backup the file with date and time
    String name =
      concatenate(inDir,
                  file(inName) + "-"
                  + new SimpleDateFormat(s_backupFormat).format(new Date())
                  + extension(inName)
                  + s_extBackup);
    File destination = new File(name);
    if(!destination.exists() && original.renameTo(destination))
      return destination.getPath();

    // simply backing up did not work, now add a number as well
    for(int i = 1; i < 10000; i++)
    {
      destination = new File(name + "." + i);

      if(!destination.exists() && original.renameTo(destination))
        return destination.getPath();
    }

    // still did not work, we give up
    return null;
  }

  //........................................................................
  //--------------------------- deleteDirectory ----------------------------

  /**
   * Delete a complete directory, with all its subfiles (use with care!).
   *
   * @param       inDirectory the name of the directory to delete
   *
   * @return      true if deleted, false if not
   *
   */
  public static boolean deleteDirectory(@Nonnull String inDirectory)
  {
    return deleteDirectory(new File(inDirectory));
  }

  //........................................................................
  //--------------------------- deleteDirectory ----------------------------

  /**
   * Delete a complete directory, with all its subfiles (use with care!).
   *
   * @param       inDirectory the name of the directory to delete
   *
   * @return      true if deleted, false if not
   *
   */
  public static boolean deleteDirectory(@Nonnull File inDirectory)
  {
    if(!inDirectory.isDirectory())
    {
      Log.warning("'" + inDirectory
                  + "' is not a directory and will not be deleted");

      return false;
    }

    try
    {
      com.google.common.io.Files.deleteRecursively(inDirectory);
    }
    catch(java.io.IOException e)
    {
      Log.warning("could not delete directory: " + e);

      return false;
    }

    return true;
  }

  //........................................................................
  //-------------------------------- exists --------------------------------

  /**
   * Check a file with the given name exists or not.
   *
   * @param       inName the name of the file
   *
   * @return      true if a file exists, false else
   *
   */
  public static boolean exists(@Nonnull String inName)
  {
    return new File(inName).exists();
  }

  //........................................................................
  //----------------------------- relativeRoot -----------------------------

  /**
   * Determine the relative root directory to set if in the sub directory
   * given.
   *
   * @param       inSubDir the sub directory to compute the relative root
   *                       for
   *
   * @return      the relative root directory to the given sub dir
   *
   */
  public static @Nonnull String relativeRoot(@Nonnull String inSubDir)
  {
    StringBuilder result = new StringBuilder();

    String []dirs = inSubDir.split("" + SEPARATOR);

    for(int i = 0; i < dirs.length; i++)
      if(dirs[i].length() > 0)
        result.append(".." + SEPARATOR);

    return result.toString();
  }

  //........................................................................

  //------------------------------- isImage --------------------------------

  /**
   * Checks that the given name is an image. This currently only checks the
   * extension.
   *
   * @param    inName the name of the file to check
   *
   * @return   true if the extension matches an image, false if not
   *
   */
  public static boolean isImage(@Nonnull String inName)
  {
    return s_imageExtensions.contains(extension(inName));
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
    //----- encode decode --------------------------------------------------

    /** Test for encoding. */
    @org.junit.Test
    public void encodeDecode()
    {
      assertEquals("encode", "_ +-2A--3A-?!&^&~",
                   Files.encodeName("_ +*:?!&^&amp;~"));
      assertEquals("decode", "_ +*:?!^&amp;~",
                   Files.decodeName("_ +-2A--3A-?!^&~"));

      assertEquals("both", "_ +*:?!&amp;^~abcd",
                   Files.decodeName(Files.encodeName("_ +*:?!&amp;^~abcd")));
    }

    //......................................................................
    //----- concatenate ----------------------------------------------------

    /** Test for concatenation. */
    @org.junit.Test
    public void concatenate()
    {
      assertEquals("simple",    "a/b" + SEPARATOR + "c",
                   Files.concatenate("a/b", "c"));
      assertEquals("simple",    "a/b" + SEPARATOR + "c",
                   Files.concatenate("a/b" + SEPARATOR, "c"));
      assertEquals("null path", "file", Files.concatenate(null, "file"));
      assertEquals("empty path", "/file", Files.concatenate("", "file"));
      assertEquals("null file", "path", Files.concatenate("path", null));
      assertEquals("empty file", "path/", Files.concatenate("path", ""));
      assertEquals("with delimiter", "/a/b", Files.concatenate("/a", "/b"));
      assertEquals("with delimiter", "/a/b/c",
                   Files.concatenate("/a", "/b", "/c"));
      assertEquals("root",       "/guru", Files.concatenate("/", "/guru"));
      assertEquals("root",       "/guru/test/",
                   Files.concatenate("/", "/guru", "///test///"));
        assertEquals("null both", "", Files.concatenate(null, null));
      assertEquals("null", "", Files.concatenate((String)null));
      assertEquals("single", "single", Files.concatenate("single"));
      assertEquals("0", "", Files.concatenate());
      assertEquals("some null", "some/text/",
                   Files.concatenate(null, "some", null, "", "text", null, ""));
      assertEquals("many nulls", "", Files.concatenate(null, null, null, null));
    }

    //......................................................................
    //----- part -----------------------------------------------------------

    /** Test for parts. */
    @org.junit.Test
    public void parts()
    {
      assertEquals("extension", ".extension",
                   Files.extension(File.separator + "my" + File.separator
                                      + "path" + File.separator
                                      + "file.extension"));
      assertEquals("extension", ".extension",
                   Files.extension(File.separator + "my" + File.separator
                                      + "path" + File.separator
                                      + ".extension"));
      assertEquals("extension", ".extension",
                   Files.extension("file.extension"));
      assertEquals("extension", ".", Files.extension("file."));
      assertEquals("extension", "", Files.extension("file"));
      assertEquals("file", "file",
                   Files.file(File.separator + "my" + File.separator
                              + "path" + File.separator
                              + "file.extension"));
      assertEquals("file", "file",
                   Files.file("file.extension"));
      assertEquals("file", "file",
                   Files.file("file"));
      assertEquals("file", "", Files.file("path" + File.separator));
      assertEquals("file", "", Files.file(""));
      assertEquals("path", File.separator + "my" + File.separator + "path"
                   + File.separator,
                   Files.getPath(File.separator + "my" + File.separator
                                 + "path" + File.separator
                                 + "file.extension"));
      assertEquals("path", "", Files.getPath("file.extension"));
      assertEquals("path", "", Files.getPath(""));
      assertEquals("path", "/", Files.getPath(File.separator));

      assertEquals("extension (empty)", "",
                   Files.extension(File.separator + "my" + File.separator
                                      + "path" + File.separator + "file"));
      assertEquals("file (empty)",      "",
                   Files.file(File.separator + "my" + File.separator
                                 + "path" + File.separator + ".extension"));
      assertEquals("path (empty)",      "",
                   Files.getPath("file.extension"));
    }

    //......................................................................
    //----- exists ---------------------------------------------------------

    /** Test for file existence. */
    @org.junit.Test
    public void exist()
    {
      assertEquals("exists", true, Files.exists("."));
      assertEquals("not exists", false, Files.exists("guru.guru"));

      assertEquals("empty", false, Files.exists(""));
    }

    //......................................................................
    //----- relativeRoot ---------------------------------------------------

    /** Test for relative roots. */
    @org.junit.Test
    public void relativeRoot()
    {
      assertEquals("none", "../", Files.relativeRoot("dir"));
      assertEquals("none", "../", Files.relativeRoot("dir/"));
      assertEquals("one", "../../", Files.relativeRoot("dir/dir2"));
      assertEquals("one", "../../", Files.relativeRoot("dir/dir2/"));
      assertEquals("two", "../../../", Files.relativeRoot("dir/dir2/dir3/"));
      assertEquals("three", "../../../../", Files.relativeRoot("dir/2/3/4"));
    }

    //......................................................................
    //----- image size -----------------------------------------------------

    /** Test getting image sizes. */
    @org.junit.Test
    public void imageSizes()
    {
      assertEquals("not found", -1, (int)imageSize("guru").first());
      assertEquals("not found", -1, (int)imageSize("guru").second());

      assertEquals("monsters", 120,
                   (int)
                   imageSize("resources/icons/Monsters.png").first());
      assertEquals("monsters", 120,
                   (int)
                   imageSize("resources/icons/Monsters.png").second());
    }

    //......................................................................
    //----- backup ---------------------------------------------------------

    /** Test the backup method. */
    @org.junit.Test
    public void backups()
    {
      String old = s_backupFormat;
      Files.s_backupFormat = "2008-03-15-20-21";

      try
      {
        // create test files
        assertTrue(new File("build/backup.test").createNewFile());
        assertTrue(new File("build/backups").mkdir());

        assertNull("unknown", backup("build/gugus", ""));
        assertEquals("simple",
                     "build/backups/backup-2008-03-15-20-21.test.bak",
                     backup("build/backup.test", "build/backups"));
        assertTrue(new File("build/backup.test").createNewFile());
        assertEquals("one", "build/backups/backup-2008-03-15-20-21.test.bak.1",
                     backup("build/backup.test", "build/backups"));
        assertTrue(new File("build/backup.test").createNewFile());
        assertEquals("one", "build/backups/backup-2008-03-15-20-21.test.bak.2",
                     backup("build/backup.test", "build/backups"));

        // remove all the files again
        assertTrue(deleteDirectory("build/backups"));
        assertFalse(new File("build/backup.test").delete());
      }
      catch(Exception e)
      {
        e.printStackTrace(System.out);
        fail("failed: " + e);
      }

      Files.s_backupFormat = old;
    }

    //......................................................................
    //----- delete directory -----------------------------------------------

    /** Test deleting directories. */
    @org.junit.Test
    public void deleteDirectories()
    {
      try
      {
        // create some files
        assertTrue(new File("build/dirdel").mkdir());
        assertTrue(new File("build/dirdel/a").createNewFile());
        assertTrue(new File("build/dirdel/b").createNewFile());
        assertTrue(new File("build/dirdel/c").createNewFile());
        assertTrue(new File("build/dirdel/d").createNewFile());

        // now delete them
        assertTrue("deletion failed", deleteDirectory("build/dirdel"));

        // check deletion
        assertFalse("should not exist", exists("build/dirdel"));
      }
      catch(java.io.IOException e)
      {
        fail("could not create files: " + e);
      }
    }

    //......................................................................
    //----- coverage -------------------------------------------------------

    /** coverage Test. */
    @org.junit.Test
    public void coverage()
    {
      new Files();
    }

    //......................................................................

  }

  //........................................................................
}
