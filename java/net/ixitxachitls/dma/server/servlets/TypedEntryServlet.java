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

import java.util.NavigableMap;
import java.util.NavigableSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.util.Strings;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * An entry servlet that has a single type and gets the id from the path of the
 * request.
 *
 *
 * @file          TypedEntryServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param         <T> the type of entry being served
 *
 */

//..........................................................................

//__________________________________________________________________________

public class TypedEntryServlet<T extends AbstractEntry>
  extends AbstractEntryServlet
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------- TypedEntryServlet ---------------------------

  /**
   * Create the servlet.
   *
   * @param       inType     the type of the entries served
   * @param       inBasePath the base path for this servlet
   * @param       inData     all the available data
   *
   */
  public TypedEntryServlet(@Nonnull AbstractType<T> inType,
                           @Nonnull String inBasePath,
                           @Nonnull DMAData inData)
  {
    super(inData);

    m_entries = inData.getEntries(inType);
    m_keys = m_entries.navigableKeySet();
    m_type = inType;
    m_basePath = inBasePath;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** All the entries available. */
  protected @Nonnull NavigableMap<String, T> m_entries;

  /** All the entries available. */
  protected @Nonnull NavigableSet<String> m_keys;

  /** The type of entries to handle. */
  protected @Nonnull AbstractType<T> m_type;

  /** The base path to the entries for this servlet. */
  protected @Nonnull String m_basePath;

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getEntry -------------------------------

  /**
   * Get the abstract entry associated with the given request.
   *
   * @param       inPath the path to the page
   *
   * @return      the entry or null if it could not be found
   *
   */
  public @Nullable AbstractEntry getEntry(@Nonnull String inPath)
  {
    String id = getID(inPath);
    if(id == null)
      return null;

    return m_entries.get(id);
  }

  //........................................................................
  //------------------------------- getType --------------------------------

  /**
   * Get the type associated with the given request.
   *
   * @param       inPath the path to the page
   *
   * @return      the type for the request
   *
   */
  public @Nullable AbstractType<? extends AbstractEntry>
    getType(@Nonnull String inPath)
  {
    return m_type;
  }

  //........................................................................
  //------------------------------- getPath --------------------------------

  /**
   * Get the path to the given entry.
   *
   * @param       inEntry the entry to get the path for
   *
   * @return      the path to the entry
   *
   */
  public @Nullable String getPath(@Nonnull AbstractEntry inEntry)
  {
    return inEntry.getID();
  }

  //........................................................................
  //-------------------------------- getID ---------------------------------

  /**
   * Get the id associated with the given request.
   *
   * @param       inPath the path to the page
   *
   * @return      the id of the entry
   *
   */
  public @Nullable String getID(@Nonnull String inPath)
  {
    return Strings.getPattern(inPath, "/([^/]*?)$");
  }

  //........................................................................
  //------------------------------- getFirst -------------------------------

  /**
   * Get the first available entry.
   *
   * @param     inType the type entry to get
   *
   * @return    the first entry available
   *
   */
  public @Nonnull AbstractEntry getFirst
    (@Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    return m_entries.get(m_entries.firstKey());
  }

  //........................................................................
  //------------------------------ getPrevious -----------------------------

  /**
   * Get the first available entry.
   *
   * @param     inID   the id of the current entry
   * @param     inType the type entry to get
   *
   * @return    the first entry available
   *
   */
  public @Nullable AbstractEntry getPrevious
    (@Nonnull String inID,
     @Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    String previous = m_keys.lower(inID);

    if(previous == null)
      return null;

    return m_entries.get(previous);
  }

  //........................................................................
  //------------------------------- getNext --------------------------------

  /**
   * Get the first available entry.
   *
   * @param     inID   the id of the current entry
   * @param     inType the type entry to get
   *
   * @return    the first entry available
   *
   */
  public @Nullable AbstractEntry getNext
    (@Nonnull String inID,
     @Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    String next = m_keys.higher(inID);

    if(next == null)
      return null;


    return m_entries.get(next);
  }

  //........................................................................
  //------------------------------- getLast -------------------------------

  /**
   * Get the last available entry.
   *
   * @param     inType the type entry to get
   *
   * @return    the first entry available
   *
   */
  public @Nonnull AbstractEntry getLast
    (@Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    return m_entries.get(m_entries.lastKey());
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
    //----- path -----------------------------------------------------------

    /** The path Test. */
    @org.junit.Test
    public void path()
    {
      TypedEntryServlet<net.ixitxachitls.dma.entries.BaseEntry> servlet =
        new TypedEntryServlet<net.ixitxachitls.dma.entries.BaseEntry>
        (net.ixitxachitls.dma.entries.BaseEntry.TYPE, "/base/",
         new DMAData.Test.Data(new net.ixitxachitls.dma.entries.BaseEntry
                               ("test", new DMAData.Test.Data())));


      assertEquals("simple", "id", servlet.getID("/just/some/path/id"));
      assertEquals("simple", "id", servlet.getID("/id"));
      assertEquals("simple", "id.txt-some",
                   servlet.getID("/just/some/path/id.txt-some"));
      assertNull("simple", servlet.getID("id"));
      assertEquals("simple", "", servlet.getID("/just/some/path/"));

      assertEquals("entry", "test",
                   servlet.getEntry("/just/some/path/test").getName());
      assertEquals("entry", "test", servlet.getEntry("/test").getName());
      assertNull("entry", servlet.getEntry("test"));
      assertNull("entry", servlet.getEntry(""));
      assertNull("entry", servlet.getEntry("test/"));
      assertNull("entry", servlet.getEntry("test/guru"));

      assertEquals("type", net.ixitxachitls.dma.entries.BaseEntry.TYPE,
                   servlet.getType("/test"));
      assertEquals("type", net.ixitxachitls.dma.entries.BaseEntry.TYPE,
                   servlet.getType("/just/some/test"));
      assertEquals("type", net.ixitxachitls.dma.entries.BaseEntry.TYPE,
                   servlet.getType("test"));
      assertEquals("type", net.ixitxachitls.dma.entries.BaseEntry.TYPE,
                   servlet.getType(""));

      assertEquals("path", "/base/id",
                   servlet.getPath(new net.ixitxachitls.dma.entries.BaseEntry
                                   ("id", new DMAData.Test.Data())));
    }

    //......................................................................
    //----- navigation -----------------------------------------------------

    /** The navigation Test. */
    @org.junit.Test
    public void navigation()
    {
      DMAData data = new DMAData.Test.Data();
      AbstractType<net.ixitxachitls.dma.entries.BaseEntry> type =
        net.ixitxachitls.dma.entries.BaseEntry.TYPE;
      net.ixitxachitls.dma.entries.BaseEntry one =
        new net.ixitxachitls.dma.entries.BaseEntry("first", data);
      net.ixitxachitls.dma.entries.BaseEntry two =
        new net.ixitxachitls.dma.entries.BaseEntry("further-1", data);
      net.ixitxachitls.dma.entries.BaseEntry three =
        new net.ixitxachitls.dma.entries.BaseEntry("further-2", data);
      net.ixitxachitls.dma.entries.BaseEntry four =
        new net.ixitxachitls.dma.entries.BaseEntry("further-3", data);
      net.ixitxachitls.dma.entries.BaseEntry five =
        new net.ixitxachitls.dma.entries.BaseEntry("last", data);

      TypedEntryServlet<net.ixitxachitls.dma.entries.BaseEntry> servlet =
        new TypedEntryServlet<net.ixitxachitls.dma.entries.BaseEntry>
       (type, "/base/", new DMAData.Test.Data(one, two, three, four, five));

      assertEquals("first", one, servlet.getFirst(type));
      assertEquals("last", five, servlet.getLast(type));

      assertEquals("next", two, servlet.getNext("first", type));
      assertEquals("next", three, servlet.getNext("further-1", type));
      assertEquals("next", four, servlet.getNext("further-2", type));
      assertEquals("next", five, servlet.getNext("further-3", type));
      assertNull("next", servlet.getNext("last", type));

      assertNull("next", servlet.getPrevious("first", type));
      assertEquals("next", one, servlet.getPrevious("further-1", type));
      assertEquals("next", two, servlet.getPrevious("further-2", type));
      assertEquals("next", three, servlet.getPrevious("further-3", type));
      assertEquals("next", four, servlet.getPrevious("last", type));
    }

    //......................................................................
  }

  //........................................................................
}
