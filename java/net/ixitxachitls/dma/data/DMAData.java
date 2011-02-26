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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;

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

public class DMAData
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
          m_files.add(new DMAFile(file, inPath));
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The files for all the data. */
  private @Nonnull List<DMAFile> m_files = new ArrayList<DMAFile>();

  /** All the entries, by class and by id. */
  private @Nonnull Map<AbstractType<? extends AbstractEntry>,
                               Map<String, AbstractEntry>> m_entries =
    new HashMap<AbstractType<? extends AbstractEntry>,
                               Map<String, AbstractEntry>>();

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
  public @Nonnull <T extends AbstractEntry> Map<String, T>
    getEntries(AbstractType<T> inType)
  {
    Map<String, AbstractEntry> entries = m_entries.get(inType);

    if(entries == null)
    {
      entries = new HashMap<String, AbstractEntry>();
      m_entries.put(inType, entries);
    }

    return (Map<String, T>)Collections.unmodifiableMap(entries);
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
        {
          Map<String, AbstractEntry> entries = m_entries.get(entry.getType());
          if(entries == null)
          {
            entries = new HashMap<String, AbstractEntry>();
            m_entries.put(entry.getType(), entries);
          }

          entries.put(entry.getID(), entry);
        }
      }

    return result;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
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
