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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.util.Files;
import net.ixitxachitls.util.logging.Log;
import net.ixitxachitls.util.resources.Resource;

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

public class DMAData implements Serializable
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- DMAData --------------------------------

  /**
   * Create the data repository with the given files.
   *
   * @param       inPath  the base path to all files
   * @param       inFiles the default files to read data from
   *
   */
  public DMAData(@Nonnull String inPath, @Nullable String ... inFiles)
  {
    m_path = inPath;

    if(inFiles != null)
      for(String file : inFiles)
        if(file != null)
          addFile(file);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The path from where to load the files. */
  private @Nonnull String m_path;

  /** The name of the files read. */
  private @Nonnull List<String> m_names = new ArrayList<String>();

  /** The files for all the data. */
  private @Nonnull ArrayList<DMAFile> m_files = new ArrayList<DMAFile>();

  /** All the entries, by type and by id. */
  private @Nonnull HashMap<AbstractType<? extends AbstractEntry>,
                           NavigableMap<String, AbstractEntry>> m_entries =
    new HashMap<AbstractType<? extends AbstractEntry>,
                               NavigableMap<String, AbstractEntry>>();

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getPath --------------------------------

  /**
   * Get the path this data file is reading files from.
   *
   * @return      the path to the files.
   *
   */
  public @Nonnull String getPath()
  {
    return m_path;
  }

  //........................................................................

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
  @SuppressWarnings("unchecked") // need to cast
  public @Nonnull <T extends AbstractEntry> NavigableMap<String, T>
    getEntries(AbstractType<T> inType)
  {
    NavigableMap<String, AbstractEntry> entries = m_entries.get(inType);

    if(entries == null)
    {
      entries = new TreeMap<String, AbstractEntry>();
      m_entries.put(inType, entries);
    }

    // TODO: this should actually be unmodifiable
    return (NavigableMap<String, T>)entries;
  }

  //........................................................................
  //---------------------------- getEntriesList ----------------------------

  /**
   * Gets all the entries of a specific type.
   *
   * @param    <T> The type of entry to get
   * @param    inType the type of entries to get
   *
   * @return   a map with id and type
   *
   */
  @SuppressWarnings("unchecked") // need to cast
  public @Nonnull <T extends AbstractEntry> List<T>
                     getEntriesList(AbstractType<T> inType)
  {
    NavigableMap<String, AbstractEntry> entries = m_entries.get(inType);

    if(entries == null)
    {
      entries = new TreeMap<String, AbstractEntry>();
      m_entries.put(inType, entries);
    }

    return (List<T>)new ArrayList<AbstractEntry>(entries.values());
  }

  //........................................................................
  //------------------------------- getEntry -------------------------------

  /**
   * Get a type denoted by type and id.
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
                               @Nonnull AbstractType<T> inType)
  {
    return getEntries(inType).get(inID);
  }

  //........................................................................
  //----------------------------- getBaseData ------------------------------

  /**
   * Get the base data for entries.
   *
   * @return      the repository with all the base data
   *
   */
  public @Nonnull DMAData getBaseData()
  {
    return this;
  }

  //........................................................................

  //--------------------------------- files --------------------------------

  /**
   * Get the names of the files that this entry can possibly be stored in.
   *
   * @param       inType the type of entries for which to get files
   *
   * @return      a list of names that can be used for storage
   *
   */
  public @Nonnull List<String> files
    (@Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    return m_names;
  };

  //........................................................................
  //------------------------------- getFile --------------------------------

  /**
   * Get the file with the given name.
   *
   * @param       inName the name of the file to get
   *
   * @return      the file found or null if not found
   *
   */
  public @Nullable DMAFile getFile(@Nonnull String inName)
  {
    for(DMAFile file : m_files)
      if(file.getStorageName().equals(inName))
        return file;

    return null;
  }

  //........................................................................

  //------------------------------ isChanged -------------------------------

  /**
   * Check if any of the data has been changed and needs saving.
   *
   * @return      true if data is changed from store, false if not
   *
   */
  public boolean isChanged()
  {
    for(DMAFile file : m_files)
      if(file.isChanged())
        return true;

    return false;
  }

  //........................................................................

  //------------------------------- toString -------------------------------

  /**
   * Convert to human readable string.
   *
   * @return      the string representation
   *
   */
  public @Nonnull String toString()
  {
    StringBuilder result =
      new StringBuilder("path " + m_path + ", files " + m_names);

    for(AbstractType<? extends AbstractEntry> type : m_entries.keySet())
    {
      result.append(", " + type + ": ");
      for(String name : m_entries.get(type).keySet())
        result.append(name + "/");
    }

    return result.toString();
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- read ---------------------------------

  /**
   * Read all the files associated, if not yet read.
   *
   * @return true if all files could be read without error, false if there were
   *         errors
   *
   */
  @SuppressWarnings("unchecked") // cast for entry.getType() in put
  public boolean read()
  {
    boolean result = true;
    for(DMAFile file : m_files)
      if(!file.wasRead())
      {
        result &= file.read(this);

        for(AbstractEntry entry : file.getEntries())
          add(entry);
      }

    return result;
  }

  //........................................................................
  //------------------------------- addFile --------------------------------

  /**
   * Load the file with the given name.
   *
   * @param       inFile the file name of file to load relative to the path
   *
   * @return      true if loaded, false if already there
   *
   */
  public boolean addFile(@Nonnull String inFile)
  {
    if(m_names.contains(inFile))
      return false;

    m_names.add(inFile);
    m_files.add(new DMAFile(inFile, m_path, this));

    return true;
  }

  //........................................................................
  //------------------------------- addFiles -------------------------------

  /**
   * Load all the dma file from the given path.
   *
   * @param    inPath the path to the directory to load files from
   *
   */
  public void addAllFiles(@Nonnull String inPath)
  {
    Resource path = Resource.get(Files.concatenate(m_path, inPath));

    for(String name : path.files())
    {
      if(name.endsWith(".dma"))
        addFile(Files.concatenate(inPath, name));
    }
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the entry to the data.
   *
   * @param     inEntry the entry to add
   *
   */
  protected void add(@Nonnull AbstractEntry inEntry)
  {
    NavigableMap<String, AbstractEntry> entries =
      m_entries.get(inEntry.getType());

    if(entries == null)
    {
      entries = new TreeMap<String, AbstractEntry>();
      m_entries.put(inEntry.getType(), entries);
    }

    if(inEntry instanceof Entry)
    {
      Entry entry = (Entry)inEntry;
      String id = entry.getID();
      while(entries.containsKey(id))
      {
        entry.randomID();
        String oldID = id;
        id = entry.getID();
        Log.warning("duplicate id detected for '" + oldID
                    + "', setting new id to '" + id + "'");
      }
    }

    entries.put(inEntry.getID(), inEntry);
  }

  //........................................................................
  //--------------------------------- save ---------------------------------

  /**
   * Save all the changed files.
   *
   * @return true if all data successfully saved, false if there was an error.
   *
   */
  public boolean save()
  {
    boolean success = true;
    for(DMAFile file : m_files)
      success = success && file.write();

    Log.event("*system*", "save",
              "Saved all data " + (success ? "without errors" : "with errors"));
    return success;
  }

  //........................................................................
  //------------------------------- addEntry -------------------------------

  /**
   * Adds the given entry to the data, using the given file. Contrary to add(),
   * this will also add the entry to the correct file.
   *
   * @param       inEntry the entry to add
   * @param       inFile  the name of the file to add to
   *
   * @return      true if added, false if not, usually due to wrong file name
   *
   */
  public boolean addEntry(@Nonnull AbstractEntry inEntry,
                          @Nonnull String inFile)
  {
    DMAFile file = getFile(inFile);
    if(file == null)
      return false;

    file.add(inEntry, true);
    add(inEntry);

    return true;
  }

  //........................................................................

  //----------------------------- removeEntry ------------------------------

  /**
   * Remove the indicated entry from the repository.
   *
   * @param       inID   the id of the entry to remove
   * @param       inType the type of entry to remove
   *
   * @return      true if removed, false if not
   *
   */
  public boolean removeEntry
    (@Nonnull String inID,
     @Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    AbstractEntry entry = getEntries(inType).remove(inID);
    if(entry == null)
      return false;

    for(DMAFile file : m_files)
      if(file.remove(entry))
        return true;

    return false;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** A simple data class for testing. */
    public static class Data extends DMAData
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
          add(entry);
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

    //----- read -----------------------------------------------------------

    /** The read Test. */
    @org.junit.Test
    public void read()
    {
      DMAData data = new DMAData("dma", "BaseCharacters/Test.dma");

      assertTrue("read", data.read());

      m_logger.addExpected("WARNING: cannot find file "
                           + "'dma/Products/Myrddin.dma'");
      m_logger.addExpected("WARNING: cannot find file 'dma/Products/zzz.dma'");
    }

    //......................................................................
  }

  //........................................................................
}
