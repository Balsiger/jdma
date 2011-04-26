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

public class TypedEntryPDFServlet<T extends AbstractEntry>
  extends AbstractEntryPDFServlet
{
  //--------------------------------------------------------- constructor(s)

  //------------------------- TypedEntryPDFServlet -------------------------

  /**
   * Create the servlet.
   *
   * @param       inType     the type of the entries served
   * @param       inBasePath the base path for this servlet
   * @param       inData     all the available data
   *
   */
  public TypedEntryPDFServlet(@Nonnull AbstractType<T> inType,
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
    String id = Strings.getPattern(inPath, "/([^/]*?)\\.pdf$");
    if(id == null)
      return null;

    return m_entries.get(id);
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
      TypedEntryPDFServlet<net.ixitxachitls.dma.entries.BaseEntry> servlet =
        new TypedEntryPDFServlet<net.ixitxachitls.dma.entries.BaseEntry>
        (net.ixitxachitls.dma.entries.BaseEntry.TYPE, "/base/",
         new DMAData.Test.Data(new net.ixitxachitls.dma.entries.BaseEntry
                               ("test", new DMAData.Test.Data())));


      assertEquals("entry", "test",
                   servlet.getEntry("/just/some/path/test").getName());
      assertEquals("entry", "test", servlet.getEntry("/test").getName());
      assertNull("entry", servlet.getEntry("test"));
      assertNull("entry", servlet.getEntry(""));
      assertNull("entry", servlet.getEntry("test/"));
      assertNull("entry", servlet.getEntry("test/guru"));
    }

    //......................................................................
  }

  //........................................................................
}
