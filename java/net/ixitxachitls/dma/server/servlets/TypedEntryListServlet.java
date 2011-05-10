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

package net.ixitxachitls.dma.server.servlets;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A servlet serving a list of entries of a type given by url.
 *
 *
 * @file          TypedEntryListServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class TypedEntryListServlet extends EntryListServlet
{
  //--------------------------------------------------------- constructor(s)

  //------------------------ TypedEntryListServlet -------------------------

  /**
   * Construct the servlet.
   *
   * @param inData   all the avaialble data
   * @param inTitles the titles for each type
   *
   */
  public TypedEntryListServlet(@Nonnull DMAData inData,
                               @Nonnull Map<String, String> inTitles)
  {
    m_data = inData;
    m_titles = inTitles;
  }

  //........................................................................


  //........................................................................

  //-------------------------------------------------------------- variables

  /** All the avilable data. */
  private @Nonnull DMAData m_data;

  /** All the titles. */
  private @Nonnull Map<String, String> m_titles;

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------ getEntries ------------------------------

  /**
   * Get the entries in the given page range.
   *
   * @param       inID    the id for the entries to get
   *
   * @return      a list of all entries in range
   *
   */
  @SuppressWarnings("unchecked") // need to cast
  public List<AbstractEntry> getEntries(@Nonnull String inID)
  {
    return (List<AbstractEntry>)
      m_data.getEntriesList(AbstractType.get(inID));
  }

  //........................................................................
  //------------------------------- getTitle -------------------------------

  /**
   * Get the title for the document.
   *
   * @param       inID the id of the request
   *
   * @return      the title
   *
   */
  public @Nonnull String getTitle(String inID)
  {
    String title = m_titles.get(inID);

    if(title != null)
      return title;

    return "List of " + inID;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- title ----------------------------------------------------------

    /** The title Test. */
    @org.junit.Test
    public void title()
    {
      EntryListServlet servlet =
        new TypedEntryListServlet(new DMAData("path"),
                                  com.google.common.collect.ImmutableMap.of
                                  ("a", "A", "b", "B"));

      assertEquals("title", "A", servlet.getTitle("a"));
      assertEquals("title", "B", servlet.getTitle("b"));
      assertEquals("title", "List of c", servlet.getTitle("c"));
    }

    //......................................................................
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
