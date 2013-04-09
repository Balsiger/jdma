/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * Utility functions for entries.
 *
 * @file          Entries.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public final class Entries
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Entries --------------------------------

  /**
   * Private constructor to prevent instantiation.
   *
   */
  private Entries()
  {
    // don't construct
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The joiner to create comma separated list strings. */
  private static final Joiner s_commaJoiner = Joiner.on(", ");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //........................................................................

  //----------------------------------------------------------- manipulators

  //........................................................................

  //------------------------------------------------- other member functions

  //-------------------------------- names ---------------------------------

  /**
   * Get a list of names of the given entries.
   *
   * @param     inEntries the entries to get the names for; null entries are
   *                      skipped
   *
   * @return    a list of all the entries
   *
   */
  public static List<String> names(Iterable<ValueGroup> inEntries)
  {
    List<String> names = new ArrayList<String>();
    for(ValueGroup entry : inEntries)
      if(entry != null && entry instanceof AbstractEntry)
        names.add(((AbstractEntry)entry).getName());

    return names;
  }

  //........................................................................
  //----------------------------- namesString ------------------------------

  /**
   * Get all the names of the given entries as a comma separated string.
   *
   * @param       inEntries the entries to get the names for
   *
   * @return      a comma separated string with all the names
   *
   */
  public static String namesString(Iterable<ValueGroup> inEntries)
  {
    return s_commaJoiner.join(names(inEntries));
  }

  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- names ----------------------------------------------------------

    /** The names Test. */
    @org.junit.Test
    public void names()
    {
      assertEquals("empty", "", namesString(entries()));
      assertEquals("one", "name", namesString(entries("name")));
      assertEquals("two", "name1, name2",
                   namesString(entries("name1", "name2")));
      assertEquals("full", "first name, second name, third name",
                   namesString(entries("first name", "second name",
                                       "third name")));
    }

    /** Create a list of entries with the given names.
     *
     * @param  inNames the names of the entries
     * @return the created list
     */
    private List<ValueGroup> entries(String ... inNames)
    {
      List<ValueGroup> list = new ArrayList<ValueGroup>();

      for(String name : inNames)
        list.add(new BaseEntry(name));

      return list;
    }

    //......................................................................
  }

  //........................................................................
}
