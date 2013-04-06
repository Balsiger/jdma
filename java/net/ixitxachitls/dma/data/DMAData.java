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
import java.util.SortedSet;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.server.servlets.DMARequest;

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

@ParametersAreNonnullByDefault
public interface DMAData
{
  //----------------------------------------------------------------- nested

  //----- File -------------------------------------------------------------

  /**
   * A simple representation for a file associated with an entry.
   */
  @ParametersAreNonnullByDefault
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
    public File(String inName, String inType, String inPath, String inIcon)
    {
      m_name = inName;
      m_type = inType;
      m_path = inPath;
      m_icon = inIcon;
    }

    /** The name of the file. */
    private String m_name;

    /** The mime type of the file. */
    private String m_type;

    /** The url to display the file. */
    private String m_path;

    /** The url to display a thumbnail of the file. */
    private String m_icon;

    /**
     * Get the name of the file.
     *
     * @return the name of the file (without path)
     */
    public String getName()
    {
      return m_name;
    }

    /**
     * Get the mime type of the file.
     *
     * @return the mime type
     */
    public String getType()
    {
      return m_type;
    }

    /**
     * Get the path of the file.
     *
     * @return the path to access the file
     */
    public String getPath()
    {
      return m_path;
    }

    /**
     * Get the icon for the file.
     *
     * @return the icon for the file
     */
    public String getIcon()
    {
      return m_icon;
    }

    /**
     * A string representation for printing.
     */
    public String toString()
    {
      return m_name + "/" + m_type + "=" + m_path;
    }
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getEntries ------------------------------

  /**
   * Gets all the entries of a specific type.
   *
   * @param    <T>      The type of entry to get
   * @param    inType   the type of entries to get
   * @param    inParent the key of the parent, if any
   * @param    inStart  the starting number of entires to get (starts as 0)
   * @param    inSize   the maximal number of entries to return
   *
   * @return   a map with id and type
   *
   */
  public <T extends AbstractEntry> List<T>
            getEntries(AbstractType<T> inType,
                       @Nullable AbstractEntry.EntryKey
                       <? extends AbstractEntry> inParent,
                       int inStart, int inSize);

  //........................................................................
  //------------------------------ getOwners -------------------------------

  /**
   * Get the owner of the given base product.
   *
   * @param    inID the id of the base product to own
   *
   * @return   a multi map from owner to ids
   *
   */
  public abstract Multimap<String, String> getOwners(String inID);

  //........................................................................
  //-------------------------------- getIDs --------------------------------

  /**
   * Get all the ids of a specific type, sorted and navigable.
   *
   * @param       inType the type of entries to get ids for
   * @param       inParent the key of the parent, if any
   *
   * @return      all the ids
   *
   */
  public abstract List<String> getIDs
    (AbstractType<? extends AbstractEntry> inType,
     @Nullable AbstractEntry.EntryKey<? extends AbstractEntry> inParent);

  //........................................................................
  //--------------------------- getRecentEntries ---------------------------

  /**
   * Get the recent ids of a specific type.
   *
   * @param       <T>      the real type of the entries to get
   * @param       inType   the type of entries to get ids for
   * @param       inParent the key of the parent entry
   *
   * @return      the recent ids
   *
   */
  public abstract <T extends AbstractEntry>
    List<T> getRecentEntries
      (AbstractType<T> inType,
       @Nullable AbstractEntry.EntryKey<? extends AbstractEntry> inParent);

  //........................................................................
  //------------------------------- getEntry -------------------------------

  /**
   * Get an entry denoted by type and id and their respective parents.
   *
   * @param      inKey  the key to the entry to get
   *
   * @param      <T>    the type of the entry to get
   *
   * @return     the entry found, if any
   *
   */
  public abstract @Nullable <T extends AbstractEntry> T getEntry
                               (AbstractEntry.EntryKey<T> inKey);

  //........................................................................
  //------------------------------- getEntry -------------------------------

  /**
   * Get the first entry denoted by a key value pair.
   *
   * @param      inType  the type of entry to get
   * @param      inKey   the key to look for
   * @param      inValue the value for the key to look for
   *
   * @param      <T>    the type of the entry to get
   *
   * @return     the entry found, if any
   *
   */
  @SuppressWarnings("unchecked") // casting return
  public @Nullable <T extends AbstractEntry> T
                      getEntry(AbstractType<T> inType, String inKey,
                               String inValue);

  //........................................................................
  //------------------------------ getEntries ------------------------------

  /**
   * Get the entry denoted by a key value pair. This throws a
   * TooManyResultsException if more thone one result is found.
   *
   * @param      inType  the type of entry to get
   * @param      inKey   the key to look for
   * @param      inValue the value for the key to look for
   *
   * @param      <T>    the type of the entry to get
   *
   * @return     the entries found
   *
   */
  @SuppressWarnings("unchecked") // casting return
  public abstract @Nullable <T extends AbstractEntry> List<T>
                               getEntries(AbstractType<T> inType,
                                          String inKey, String inValue);

  //........................................................................
  //--------------------------- getIndexEntries ----------------------------

  /**
   * Get the entries for the given index.
   *
   * @param    <T>      The type of the entries to get
   * @param    inIndex  the name of the index to get
   * @param    inType   the type of entries to return for the index (app engine
   *                    can only do filter on queries with kind)
   * @param    inParent the parent key, if any
   * @param    inGroup  the index group to show
   * @param    inStart  the 0 based index of the first entry to return
   * @param    inSize   the maximal number of entries to return
   *
   * @return   the entries matching the given index
   *
   */
  public <T extends AbstractEntry> List<T> getIndexEntries
    (String inIndex, AbstractType<T> inType,
     @Nullable AbstractEntry.EntryKey<? extends AbstractEntry> inParent,
     String inGroup, int inStart, int inSize);

  //........................................................................
  //---------------------------- getIndexNames -----------------------------

  /**
   * Get the names for the given index.
   *
   * @param       inIndex   the index to get it for
   * @param       inType    the type of entries to look for (required for app
   *                        engine)
   * @param       inCached  true to use the cache if possible, false for not
   * @param       inFilters pairs of property key and values to use for
   *                        filtering
   *
   * @return      a list with all the names
   *
   */
  public SortedSet<String> getIndexNames
    (String inIndex,
     AbstractType<? extends AbstractEntry> inType, boolean inCached,
     String ... inFilters);

  //........................................................................
  //------------------------------ getValues -------------------------------

  /**
   * Get the value for the given fields.
   *
   * @param       inType   the type of entries to look for
   * @param       inFields the fields to return
   *
   * @return      a list of records found, each with values for each field,
   *              in the order they were specificed
   *
   */
  public abstract List<List<String>> getMultiValues
    (AbstractType<? extends AbstractEntry> inType,
     String ... inFields);

  //........................................................................
  //------------------------------ getValues -------------------------------

  /**
   * Get the value for the given fields.
   *
   * @param       inType   the type of entries to look for
   * @param       inField  the fields to return
   *
   * @return      a list of records found, each with values for each field,
   *              in the order they were specificed
   *
   */
  public abstract SortedSet<String> getValues
    (AbstractType<? extends AbstractEntry> inType,
     String inField);

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

  //------------------------------ clearCache ------------------------------

  /**
   * Clear the cache of entries.
   */
  public abstract void clearCache();

  //........................................................................
  //-------------------------------- remove --------------------------------

  /**
   * Removes the entry from the datastore.
   *
   * @param       inID    the id of the entry to remove
   * @param       inType  the type of the entry to remove
   *
   * @return      true if removed, false if not
   *
   */
  public abstract boolean remove
    (String inID,
     AbstractType<? extends AbstractEntry> inType);

  //........................................................................
  //-------------------------------- remove --------------------------------

  /**
   * Removes the entry from the datastore.
   *
   * @param       inKey   the key of the entry to remove
   *
   * @return      true if removed, false if not
   *
   */
  public abstract boolean remove(AbstractEntry.EntryKey<?> inKey);

  //........................................................................
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
  public boolean update(AbstractEntry inEntry);

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
  public List<File> getFiles(AbstractEntry inEntry);

  //........................................................................
  //------------------------------- rebuild --------------------------------

  /**
   * Rebuild the given type. This means mainly rebuilding the indexs. It is
   * accomplished by reading all entries and writing them back.
   *
   * NOTE: this produces a lot of datastore traffic.
   *
   * @param      inType  the type to rebuild for
   *
   * @return     the numbert of enties updated
   *
   */
  public int rebuild(AbstractType<? extends AbstractEntry> inType);

  //........................................................................
  //------------------------------- refresh --------------------------------

  /**
   * Refresh the given type, by reading the entries and checking if they
   * changed from their saved state.
   *
   * NOTE: this produces a lot of datastore traffic.
   *
   * @param      inType    the type to rebuild for
   * @param      inRequest the request for the refresh
   *
   * @return     the numbert of enties updated
   *
   */
  public int refresh(AbstractType<? extends AbstractEntry> inType,
                     DMARequest inRequest);

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** A simple data class for testing. */
    @ParametersAreNonnullByDefault
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
        {
          addInternal(entry);
          m_ids.add(entry.getName());
        }
      }

      /** The ids of all the entries. */
      private List<String> m_ids = new java.util.ArrayList<String>();

      /**
       * Save all the changed files.
       *
       * @return true if all data successfully saved, false if there was an
       *         error.
       *
       */
      @Override
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
      @Override
      public boolean update(AbstractEntry inEntry)
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
      public boolean add(AbstractEntry inEntry)
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

      /**
       * Get all the ids of a specific type, sorted and navigable.
       *
       * @param       inType the type of entries to get ids for
       * @param       inParent the key of the parent, if any
       *
       * @return      all the ids
       *
       */
      @Override
      public List<String> getIDs
        (AbstractType<? extends AbstractEntry> inType,
         @Nullable AbstractEntry.EntryKey<? extends AbstractEntry> inParent)
      {
        return m_ids;
      }

      /**
       * Get the names for the given index.
       *
       * @param       inType    the type of entries to look for
       * @param       inField   the field for which to get the value
       *
       * @return      a list with all the names
       *
       */
      @Override
      public SortedSet<String> getValues
        (AbstractType<? extends AbstractEntry> inType, String inField)
      {
        return ImmutableSortedSet.of("Index-1", "Index-2", "Index-3");
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
