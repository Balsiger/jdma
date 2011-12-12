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

import java.util.List;
import java.util.NavigableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A wrapper around loaded data, e.g. entries.
 *
 *
 * @file          DMAData.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public interface DMAData
{
  //----------------------------------------------------------------- nested

  /**
   * A simple representation for a file associated with an entry.
   */
  public static class File
  {
    /**
     * Create the file with all its data.
     *
     * @param inName   the file name
     * @param inType   the mime type of the file
     * @param inPath   the url path to access the file
     * @param inIcon   the icon or thumbnail to show for the file
     *
     */
    public File(@Nonnull String inName, @Nonnull String inType,
                @Nonnull String inPath, @Nonnull String inIcon)
    {
      m_name = inName;
      m_type = inType;
      m_path = inPath;
      m_icon = inIcon;
    }

    /** The name of the file. */
    private @Nonnull String m_name;

    /** The mime type of the file. */
    private @Nonnull String m_type;

    /** The url to display the file. */
    private @Nonnull String m_path;

    /** The url to display a thumbnail of the file. */
    private @Nonnull String m_icon;

    /**
     * Get the name of the file.
     *
     * @return the name of the file (without path)
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /**
     * Get the mime type of the file.
     *
     * @return the mime type
     */
    public @Nonnull String getType()
    {
      return m_type;
    }

    /**
     * Get the path of the file.
     *
     * @return the path to access the file
     */
    public @Nonnull String getPath()
    {
      return m_path;
    }

    /**
     * Get the icon for the file.
     *
     * @return the icon for the file
     */
    public @Nonnull String getIcon()
    {
      return m_icon;
    }
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getEntries ------------------------------

  /**
   * Gets all the entries of a specific type.
   *
   * @param    <T> The type of entry to get
   * @param    inType the type of entries to get
   *
   * @return   a map with id and type
   *
   */
  public @Nonnull <T extends AbstractEntry> NavigableMap<String, T>
    getEntries(AbstractType<T> inType);

  //........................................................................
  //------------------------------ getEntries ------------------------------

  /**
   * Gets all the entries of a specific type.
   *
   * @param    <T> The type of entry to get
   * @param    inType the type of entries to get
   * @param    inStart the starting number of entires to get (starts as 0)
   * @param    inSize  the maximal number of entries to return
   *
   * @return   a map with id and type
   *
   */
  public @Nonnull <T extends AbstractEntry> List<T>
                     getEntries(AbstractType<T> inType, int inStart,
                                int inSize);

  //........................................................................
  //------------------------------- getEntry -------------------------------

  /**
   * Get an entry denoted by type and id.
   *
   * @param      inID   the id of the entry to get
   * @param      inType the type of the entry to get
   *
   * @param      <T>    the type of the entry to get
   *
   * @return     the entry found, if any
   *
   */
  public @Nullable <T extends AbstractEntry> T
                      getEntry(@Nonnull String inID,
                               @Nonnull AbstractType<T> inType);

  //........................................................................
  //---------------------------- getFirstEntry -----------------------------

  /**
   * Get the first entry of a specific type.
   *
   * @param      inType the type of the entry to get
   *
   * @param      <T>    the type of the entry to get
   *
   * @return     the entry found, if any
   *
   */
  public @Nullable <T extends AbstractEntry> T
                      getFirstEntry(@Nonnull AbstractType<T> inType);

  //........................................................................
  //----------------------------- getLastEntry -----------------------------

  /**
   * Get the last entry of a specific type.
   *
   * @param      inType the type of the entry to get
   *
   * @param      <T>    the type of the entry to get
   *
   * @return     the entry found, if any
   *
   */
  public @Nullable <T extends AbstractEntry> T
                      getLastEntry(@Nonnull AbstractType<T> inType);

  //........................................................................
  //----------------------------- getNextEntry -----------------------------

  /**
   * Get the next entry of a specific type.
   *
   * @param      inID   the id of the entry after the desired one
   * @param      inType the type of the entry to get
   *
   * @param      <T>    the type of the entry to get
   *
   * @return     the entry found, if any
   *
   */
  public @Nullable <T extends AbstractEntry> T
                      getNextEntry(@Nonnull String inID,
                                   @Nonnull AbstractType<T> inType);

  //........................................................................
  //--------------------------- getPreviousEntry ---------------------------

  /**
   * Get the previous entry of a specific type.
   *
   * @param      inID   the id of the entry before the desired one
   * @param      inType the type of the entry to get
   *
   * @param      <T>    the type of the entry to get
   *
   * @return     the entry found, if any
   *
   */
  public @Nullable <T extends AbstractEntry> T
                      getPreviousEntry(@Nonnull String inID,
                                       @Nonnull AbstractType<T> inType);

  //........................................................................
  //----------------------------- getBaseData ------------------------------

  /**
   * Get the base data for entries.
   *
   * @return      the repository with all the base data
   *
   */
  public @Nonnull DMAData getBaseData();

  //........................................................................
  //----------------------------- getUserData ------------------------------

  /**
   * Get user specific data for the given user.
   *
   * @param       inUser the user for whom to get the data
   *
   * @return      the user specific data
   *
   */
  public @Nonnull DMAData getUserData(@Nonnull BaseCharacter inUser);

  //........................................................................

  //------------------------------ isChanged -------------------------------

  /**
   * Check if any of the data has been changed and needs saving.
   *
   * @return      true if data is changed from store, false if not
   *
   */
  @Deprecated
  public boolean isChanged();

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //---------------------------------- update -------------------------------

  /**
   * Update the entry in the store. Will add the entry to the store if not
   * yet there, otherwise just update all the values and stores it.
   *
   * @param       inEntry the entry to add
   *
   * @return      true if updated, false if there was an error
   *
   */
  public boolean update(@Nonnull AbstractEntry inEntry);

  //........................................................................
  //------------------------------- getFiles -------------------------------

  /**
   * Get the files for the given entry.
   *
   * @param    inEntry the entry for which to get files
   *
   * @return   a list of all the files found
   *
   */
  public @Nonnull List<File> getFiles(@Nonnull AbstractEntry inEntry);

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** A simple data class for testing. */
    public static class Data extends DMADatafiles
    {
      /**
       * Create the test data.
       *
       * @param inEntries the entries that the data should have for tests.
       */
      public Data(AbstractEntry ... inEntries)
      {
        super("");

        for(AbstractEntry entry : inEntries)
          addInternal(entry);
      }

      /**
       * Save all the changed files.
       *
       * @return true if all data successfully saved, false if there was an
       *         error.
       *
       */
      public boolean save()
      {
        m_saved = true;
        return true;
      }

      /**
       * Update the entry in the store. Will add the entry to the store if not
       * yet there, otherwise just update all the values and stores it.
       *
       * @param       inEntry the entry to add
       *
       * @return      true if updated, false if there was an error
       *
       */
      public boolean update(@Nonnull AbstractEntry inEntry)
      {
        addInternal(inEntry);
        return save();
      }

      /**
       * Add the entry to the data.
       *
       * @param     inEntry the entry to add
       *
       * @return      true if added, false if there was an error
       *
       */
      protected boolean add(@Nonnull AbstractEntry inEntry)
      {
        addInternal(inEntry);
        return save();
      }

      /**
       * Check whether data has been saved.
       *
       * @return true if data has been saved, false if not
       */
      public boolean wasSaved()
      {
        return m_saved;
      }

      /** True if data has been saved, false if not. */
      private boolean m_saved = false;

      /** The id for serialization. */
      private static final long serialVersionUID = 1L;
    }

    //----- simple ---------------------------------------------------------

    /** The simple Test. */
    @org.junit.Test
    public void simple()
    {
      // we need a test or junit wil fail
    }

    //......................................................................
  }

  //........................................................................
}
