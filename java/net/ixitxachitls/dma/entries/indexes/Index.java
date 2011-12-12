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

package net.ixitxachitls.dma.entries.indexes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.output.html.HTMLWriter;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The base class for all index specifications.
 *
 * @file          Index.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public abstract class Index implements Serializable, Comparable<Index>
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Index ---------------------------------

  /**
   * Create the index.
   *
   * @param         inTitle     the index title
   * @param         inType      the type of entries served
   *
   */
  public Index(@Nonnull String inTitle,
               AbstractType<? extends AbstractEntry> inType)
  {
    m_title = inTitle;
    m_type = inType;
  }

  //........................................................................

  //------------------------------ withImages ------------------------------

  /**
   * Enable images for the index.
   *
   * @return  the index for chaining
   *
   */
  public @Nonnull Index withImages()
  {
    m_images = true;

    return this;
  }

  //........................................................................
  //-------------------------- withoutPagination ---------------------------

  /**
   * Disables pagination for the index.
   *
   * @return  the index for chaining
   *
   */
  public @Nonnull Index withoutPagination()
  {
    m_paginated = false;

    return this;
  }

  //........................................................................
  //-------------------------- withoutPagination ---------------------------

  /**
   * Disables pagination for the index.
   *
   * @return  the index for chaining
   *
   */
  public @Nonnull Index withEditable()
  {
    m_editable = true;

    return this;
  }

  //........................................................................

  //----------------------------- withAccess -------------------------------

  /**
   * Set the access level for this index.
   *
   * This method can be chained with the constructor.
   *
   * @param       inAccess the new access rights required, null for none
   *
   * @return      this object
   *
   */
  // @SuppressWarnings("unchecked")
  // public @Nonnull Index<I> withAccess(@Nonnull BaseCharacter.Group inAccess)
  // {
  //   m_access = inAccess;

  //   return this;
  // }

  //........................................................................
  //--------------------------- withDataSource -----------------------------

  /**
   * Set the data source to for this index.
   *
   * This method can be chained with the constructor.
   *
   * @param       inSource the data source to use for this index
   *
   * @return      this object
   *
   */
//   @SuppressWarnings("unchecked")
//   public Index<I> withDataSource(DataSource inSource)
//   {
//     m_source = inSource;

//     return this;
//   }

  //........................................................................
  //--------------------------- withDataSource -----------------------------

  /**
   * Set the data source to for this index.
   *
   * This method can be chained with the constructor.
   *
   * @param       inSource the data source to use for this index
   *
   * @return      this object
   *
   */
// public Index<I> withType(AbstractEntry.Type<? extends AbstractEntry> inType)
//   {
//     m_type = inType;

//     if(m_type != null)
//       withDataSource(DataSource.typed);

//     return this;
//   }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The index title. */
  private @Nonnull String m_title;

  /** The type of entries in this index. */
  private @Nonnull AbstractType<? extends AbstractEntry> m_type;

  /** Flag if showing images or not. */
  private boolean m_images = false;

  /** Flag if index is editable or not. */
  private boolean m_editable = false;

  /** Flag if index is paginated or not. */
  private boolean m_paginated = true;

  /** The access level required for this index. */
  //private @Nullable BaseCharacter.Group m_access = null;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getType --------------------------------

  /**
   * Get the type of entries in this index.
   *
   * @return the type of entries
   *
   */
  public AbstractType<? extends AbstractEntry> getType()
  {
    return m_type;
  }

  //........................................................................
  //------------------------------- getTitle -------------------------------

  /**
   * Get the index title.
   *
   * @return      the title
   *
   */
  public @Nonnull String getTitle()
  {
    return m_title;
  }

  //........................................................................
  //--------------------------- getIdentificator ---------------------------

  /**
   * Get the indentificator used to name items.
   *
   * @return      the identificator
   *
   */
//   public @Nonnull Identificator<AbstractEntry> getIdentificator()
//   {
//     return s_identificator;
//   }

  //........................................................................
  //----------------------------- getFormatter -----------------------------

  /**
   * Get the formatter to use for formatting a single entry.
   *
   * @return      the formatter
   *
   */
//   public @Nonnull Formatter getFormatter()
//   {
//     return m_formatter;
//   }

  //........................................................................
  //------------------------------ getFormat ------------------------------

  /**
   * Get the format for the complete index table.
   *
   * @return      the string with the format
   *
   */
//   public @Nonnull String getFormat()
//   {
//     return m_format;
//   }

  //........................................................................
  //-------------------------------- allows --------------------------------

  /**
   * Check if this index allows access by the given access level.
   *
   * @param       inLevel the level to check for
   *
   * @return      true if allowed, false if not
   *
   */
  // public boolean allows(@Nonnull BaseCharacter.Group inLevel)
  // {
  //   if(m_access == null)
  //     return true;

  //   return m_access.allows(inLevel);
  // }

  //........................................................................
  //---------------------------- getDataSource -----------------------------

  /**
   * Get the data source to use for this index.
   *
   * @return      the enum value denoting the data source to use
   *
   */
//   public DataSource getDataSource()
//   {
//     return m_source;
//   }

  //........................................................................
  //---------------------------- getNavigation -----------------------------

  /**
   * Get the navigation information for the current index.
   *
   * @param       inName the index name
   * @param       inPath the path to the index to print
   *
   * @return      an array if text/url pairs for the navigation
   *
   */
  public abstract @Nonnull String [] getNavigation(@Nonnull String inName,
                                                   @Nonnull String inPath);

  //........................................................................
  //------------------------------ getEntries ------------------------------

  /**
   * Get all the entries to be included in this index.
   *
   * @param    inData all the available data
   * @param    inPath the sub path to the index
   *
   * @return   a list of all the entries to print
   *
   */
  public abstract @Nonnull List<AbstractEntry>
    getEntries(@Nonnull DMAData inData, @Nonnull String inPath);

  //........................................................................
  //------------------------------- getTitle -------------------------------

  /**
   * Get the title of the index for the given path.
   *
   * @param       inPath the sub path to the index
   *
   * @return      the title for the page
   *
   */
  public abstract @Nonnull String getTitle(@Nonnull String inPath);

  //........................................................................

  //------------------------------ hasImages -------------------------------

  /**
   * Check if the index shows images or not.
   *
   * @return      true if images are used, false not
   *
   */
  public boolean hasImages()
  {
    return m_images;
  }

  //........................................................................
  //----------------------------- isPaginated ------------------------------

  /**
   *
   * Check if the index is to be served paginated or not.
   *
   * @return      true for paginated, false for not
   *
   */
  public boolean isPaginated()
  {
    return m_paginated;
  }

  //......................................................................
  //------------------------------ compareTo -------------------------------

  /**
   * Compare this index to another one for sorting.
   *
   * @param       inOther the other type to compare to
   *
   * @return      < 0 if this is lower, > if this is bigger, 0 if equal
   *
   */
  public int compareTo(@Nullable Index inOther)
  {
    if(inOther == null)
      return -1;

    return m_title.compareTo(inOther.m_title);
  }

  //........................................................................
  //-------------------------------- equals --------------------------------

  /**
   * Check for equality of the given index.
   *
   * @param       inOther the object to compare to
   *
   * @return      true if equal, false else
   *
   */
  public boolean equals(Object inOther)
  {
    if(inOther == null)
      return false;

    if(inOther instanceof Index)
      return m_title.equals(((Index)inOther).m_title);
    else
      return false;
  }

  //........................................................................
  //------------------------------- hashCode -------------------------------

  /**
   * Compute the hash code for this class.
   *
   * @return      the hash code
   *
   */
  public int hashCode()
  {
    return m_title.hashCode();
  }

  //........................................................................
  //----------------------------- listEntries ------------------------------

  /**
   * Check if we have to show entries or an index overview.
   *
   * @param     inPath the sub path of the index
   *
   * @return    true for printing entries, false for overview(s)
   *
   */
  public abstract boolean listEntries(@Nonnull String inPath);

  //........................................................................
  //------------------------------ isEditable ------------------------------

  /**
   * Check whether the index can be edited.
   *
   * @param       inPath the sub path to the index
   *
   * @return      true if index is editable, false if not
   *
   */
  public boolean isEditable(@Nonnull String inPath)
  {
    return m_editable;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //-------------------------------- write ---------------------------------

  /**
   * Write the contents of the index to the given writer and request.
   *
   * @param      inWriter     the writer to output to
   * @param      inData       the data to print
   * @param      inName       the nameing part of the index url
   * @param      inPath       the sub path of the index
   * @param      inPageSize   the size of the page as number of elements
   * @param      inStart      the index of the first element to use
   *
   * @return     the page with the entries to show instead, if any
   *
   */
  public abstract @Nullable String write(@Nonnull HTMLWriter inWriter,
                                         @Nonnull DMAData inData,
                                         @Nonnull String inName,
                                         @Nonnull String inPath,
                                         int inPageSize,
                                         int inStart);

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** The init Test. */
    @org.junit.Test
    public void init()
    {
      Index index = new Index("title",
                              net.ixitxachitls.dma.entries.BaseCharacter.TYPE)
        {
          private static final long serialVersionUID = 1L;

          public @Nonnull String [] getNavigation(@Nonnull String inName,
                                                  @Nonnull String inPath)
          {
            return new String[0];
          }

          public String write(@Nonnull HTMLWriter inWriter,
                              @Nonnull DMAData inData,
                              @Nonnull String inName,
                              @Nonnull String inPath,
                              int inPageSize, int inStart)
          {
            return null;
          }

          public boolean listEntries(String inPath)
          {
            return false;
          }

          public List<AbstractEntry> getEntries(DMAData inData, String inPath)
          {
            return new ArrayList<AbstractEntry>();
          }

          public String getTitle(String inPath)
          {
            return inPath;
          }
        };

      assertEquals("type", net.ixitxachitls.dma.entries.BaseCharacter.TYPE,
                   index.getType());
      assertFalse("images", index.hasImages());
      assertEquals("title", "title", index.getTitle());
      assertTrue("paginated", index.isPaginated());

      index = index.withImages().withoutPagination();

      assertTrue("images", index.hasImages());
      assertFalse("paginated", index.isPaginated());
    }

    //......................................................................
  }

  //........................................................................
}
