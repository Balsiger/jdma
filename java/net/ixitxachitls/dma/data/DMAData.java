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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.util.logging.Log;

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
    if(inFiles != null)
      for(String file : inFiles)
        if(file != null)
          m_files.add(new DMAFile(file, inPath, this));
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The files for all the data. */
  private @Nonnull ArrayList<DMAFile> m_files = new ArrayList<DMAFile>();

  /** All the entries, by class and by id. */
  private @Nonnull HashMap<AbstractType<? extends AbstractEntry>,
                           NavigableMap<String, AbstractEntry>> m_entries =
    new HashMap<AbstractType<? extends AbstractEntry>,
                               NavigableMap<String, AbstractEntry>>();

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

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
        result &= file.read();

        for(AbstractEntry entry : file.getEntries())
          add(entry);
      }

    return result;
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
  public @Nonnull Set<String> files
    (@Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    Set<String> names = new HashSet<String>();
    for(DMAFile file : m_files)
      names.add(file.getStorageName());

    return names;
  };

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the entry to the data.
   *
   * @param     inEntry the entry to add
   *
   */
  protected void add(AbstractEntry inEntry)
  {
    NavigableMap<String, AbstractEntry> entries =
      m_entries.get(inEntry.getType());
    if(entries == null)
    {
      entries = new TreeMap<String, AbstractEntry>();
      m_entries.put(inEntry.getType(), entries);
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
       * Create the test data
       *
       * @param inEntries the entries that the data should have for tests.
       */
      public Data(AbstractEntry ... inEntries)
      {
        super("");

        for(AbstractEntry entry : inEntries)
          add(entry);
      }

      /** The id for serialization. */
      private static final long serialVersionUID = 1L;
    }

    //----- read -----------------------------------------------------------

    /** The read Test. */
    @org.junit.Test
    public void read()
    {
      DMAData data = new DMAData("dma", "BaseCharacters/Ixitxachitls.dma");

      assertTrue("read", data.read());
    }

    //......................................................................
  }

  //........................................................................
}
