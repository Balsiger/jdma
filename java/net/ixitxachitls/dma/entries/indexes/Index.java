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
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.BaseEntry;
//import net.ixitxachitls.dma.entries.Campaign;
//import net.ixitxachitls.dma.entries.Entry;
//import net.ixitxachitls.output.commands.Command;
//import net.ixitxachitls.output.commands.Label;
//import net.ixitxachitls.output.commands.Link;
import net.ixitxachitls.util.Identificator;
//import net.ixitxachitls.util.Encodings;
//import net.ixitxachitls.util.Strings;

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
public abstract class Index implements Serializable
{
  //----------------------------------------------------------------- nested

  /**
   * A simple inferface to format an entry.
   *
   */
  public interface Formatter<S extends AbstractEntry>
  {
    /**
     * Format the entry with the given key.
     *
     * @param       inKey   the key of the entry to format
     * @param       inEntry the entry to format
     *
     * @return      a list with all the commands to format the entry
     *
     */
    public @Nonnull List<Object>
      format(@Nonnull String inKey, @Nonnull S inEntry);
  }

  //........................................................................

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

  /** The formatter to use. */
//   private @Nonnull Formatter<AbstractEntry> m_formatter = FORMATTER;

  /** The format to use. */
  private @Nonnull String m_format = FORMAT;

  /** Flag if index is paginated or not. */
  private boolean m_paginated = true;

  /** The access level required for this index. */
  private @Nullable BaseCharacter.Group m_access = null;

  /** The possible data sources. */
  //public enum DataSource { global, campaign, products, dm, user, typed };

  /** The data that is to be used. */
  //private DataSource m_source = DataSource.global;

  /** The type of entries in the index, if any. */
  //private AbstractEntry.Type m_type = null;

  /** The standard identificator for indexes. */
  protected static final Identificator<AbstractEntry> s_identificator =
    new Identificator<AbstractEntry>()
    {
      public @Nonnull List<String> id(@Nonnull AbstractEntry inEntry)
      {
        List<String> ids = new ArrayList<String>();

        if(inEntry instanceof BaseEntry)
          ids.addAll(((BaseEntry)inEntry).getSynonyms());

        ids.add(inEntry.getRefName());

        return ids;
      }
    };

  /** The standard table format for indexes. */
  public static final @Nonnull String FORMAT =
    "1:L(icon);20:L(name)[Name];20:L(short)[Short Description]";

  /** The standard formatter for indexes. */
//   public static final @Nonnull Formatter<AbstractEntry>
//     FORMATTER = new Formatter<AbstractEntry>()
//     {
//       public @Nonnull List<Object>
//         format(@Nonnull String inKey, @Nonnull AbstractEntry inEntry)
//       {
//         ArrayList<Object> list = new ArrayList<Object>();

//         // label
//         list.add(new Label(Encodings.toWordUpperCase
//                            (inEntry.getType().toString())));

//         String name = inKey;

//         // try to find the real, unchanged name
//         if(name.equalsIgnoreCase(inEntry.getID()))
//           name = inEntry.getID();
//         else
//           if(inEntry instanceof BaseEntry)
//           {
//             List<String> synonyms = ((BaseEntry)inEntry).getSynonyms();

//             for(String synonym : synonyms)
//               if(name.equalsIgnoreCase(synonym))
//               {
//                 name = synonym;

//                 break;
//               }
//           }

//         // name
//         CampaignData campaign = inEntry.getCampaign();

//         String prefix = "";
//         if(campaign instanceof Campaign)
//           prefix = "/campaign/" + ((Campaign)campaign).getID();
//         else
//           prefix = "/entry";

//         list.add(new Link(name, prefix
//                             + "/" + inEntry.getType().getLink()
//                             + "/" + inEntry.getID()));

//         // short description
//         list.add(new Command(inEntry.getShortDescription()));

//         return list;
//       }
//     };

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
  public boolean allows(@Nonnull BaseCharacter.Group inLevel)
  {
    if(m_access == null)
      return true;

    return m_access.allows(inLevel);
  }

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
  //------------------------------- getType --------------------------------

  /**
   * Get the type of entries in this index.
   *
   * @return      the type for the entries
   *
   */
//   public AbstractEntry.Type<? extends AbstractEntry> getType()
//   {
//     return m_type;
//   }

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

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //-------------------------------- names --------------------------------

  /**
   * Build up all the names to put up into the index.
   *
   * @param       ioNames the names collected so far
   * @param       inData  all the data to used in the index
   *
   * @return      a set with all the names (usually Strings, but can be
   *              anything)
   *
   */
  public abstract @Nonnull Set<String> names(@Nonnull Set<String> ioNames,
                                             @Nonnull DMAData inData);

  //........................................................................
  //------------------------------- matches --------------------------------

  /**
   * Check if the given entry matches the name.
   *
   * @param       inName  the name to match
   * @param       inEntry the entry to match on
   *
   * @return      true if entry matches the index, false if not
   *
   */
  public abstract boolean matches(@Nonnull String inName,
                                  @Nonnull AbstractEntry inEntry);

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- build ----------------------------------------------------------

    /** Testing building of commands. */
    @org.junit.Test
    public void build()
    {
      Index index = new Index("title", BaseCharacter.TYPE)
        {
          private static final long serialVersionUID = 1L;

          public Set<String> names(@Nonnull Set<String> ioNames,
                                   @Nonnull DMAData inData)
          {
            ioNames.add("first");
            ioNames.add("second");
            ioNames.add("third");

            return ioNames;
          }

          public boolean matches(String inName, AbstractEntry inEntry)
          {
            return true;
          }
        };
      index.withImages().withoutPagination();

      assertEquals("title", "title", index.getTitle());
      assertTrue("images", index.hasImages());
//     assertEquals("identificator", s_identificator, index.getIdentificator());
//       assertEquals("formater", FORMATTER, index.getFormatter());
//       assertEquals("format", FORMAT, index.getFormat());
      assertTrue("allows", index.allows(BaseCharacter.Group.GUEST));
//       assertEquals("datasource", DataSource.global, index.getDataSource());
      assertFalse("paginated", index.isPaginated());

//       index.withAccess(BaseCharacter.Group.ADMIN)
//         .withDataSource(DataSource.campaign);

//       assertFalse("allows", index.allows(BaseCharacter.Group.GUEST));
//       assertEquals("datasource", DataSource.campaign, index.getDataSource());

//       View<AbstractEntry> view =
//         new View<AbstractEntry>(Arrays.asList(new AbstractEntry []
//           { new AbstractEntry("a"), new AbstractEntry("b"),
//             new AbstractEntry("c"), new AbstractEntry("d"), }).iterator(),
//                                 new Identificator<AbstractEntry>()
//           {
//             public String []id(AbstractEntry inEntry)
//             {
//               return new String [] { "1", "2", };
//             }
//           });

//       Collection<Object> commands = index.buildCommand(null, null);

//       assertFalse("empty", commands.iterator().hasNext());

//       commands = index.buildCommand("id", view);

//       Iterator<Object> i = commands.iterator();
//       assertEquals("simple", "\\label{Abstract Entry}", i.next().toString());
//       assertEquals("simple", "\\link[/entry/abstractentry/a]{1}",
//                    i.next().toString());
//       assertEquals("simple", "", i.next().toString());
//       assertEquals("simple", "\\label{Abstract Entry}", i.next().toString());
//       assertEquals("simple", "\\link[/entry/abstractentry/b]{1}",
//                    i.next().toString());
//       assertEquals("simple", "", i.next().toString());
//       assertEquals("simple", "\\label{Abstract Entry}", i.next().toString());
//       assertEquals("simple", "\\link[/entry/abstractentry/c]{1}",
//                    i.next().toString());
//       assertEquals("simple", "", i.next().toString());
//       assertEquals("simple", "\\label{Abstract Entry}", i.next().toString());
//       assertEquals("simple", "\\link[/entry/abstractentry/d]{1}",
//                    i.next().toString());
//       assertEquals("simple", "", i.next().toString());
//       assertEquals("simple", "\\label{Abstract Entry}", i.next().toString());
//       assertEquals("simple", "\\link[/entry/abstractentry/a]{2}",
//                    i.next().toString());
//       assertEquals("simple", "", i.next().toString());
//       assertEquals("simple", "\\label{Abstract Entry}", i.next().toString());
//       assertEquals("simple", "\\link[/entry/abstractentry/b]{2}",
//                    i.next().toString());
//       assertEquals("simple", "", i.next().toString());
//       assertEquals("simple", "\\label{Abstract Entry}", i.next().toString());
//       assertEquals("simple", "\\link[/entry/abstractentry/c]{2}",
//                    i.next().toString());
//       assertEquals("simple", "", i.next().toString());
//       assertEquals("simple", "\\label{Abstract Entry}", i.next().toString());
//       assertEquals("simple", "\\link[/entry/abstractentry/d]{2}",
//                    i.next().toString());
//       assertEquals("simple", "", i.next().toString());
//       assertFalse("simple", i.hasNext());

//       // with pagination
//       commands = index.buildCommand("id", view, 3, 5);

//       i = commands.iterator();
//       assertEquals("simple", "\\label{Abstract Entry}", i.next().toString());
//       assertEquals("simple", "\\link[/entry/abstractentry/d]{1}",
//                    i.next().toString());
//       assertEquals("simple", "", i.next().toString());
//       assertEquals("simple", "\\label{Abstract Entry}", i.next().toString());
//       assertEquals("simple", "\\link[/entry/abstractentry/a]{2}",
//                    i.next().toString());
//       assertEquals("simple", "", i.next().toString());
//       assertEquals("simple", "\\label{Abstract Entry}", i.next().toString());
//       assertEquals("simple", "\\link[/entry/abstractentry/b]{2}",
//                    i.next().toString());
//       assertEquals("simple", "", i.next().toString());
//       assertFalse("simple", i.hasNext());

//       // with standard identificator
//       view = new View<AbstractEntry>(Arrays.asList(new AbstractEntry []
//         { new AbstractEntry("a"), new AbstractEntry("b"),
//           new AbstractEntry("c"), new AbstractEntry("d"), }).iterator(),
//                                      s_identificator);

//       commands = index.buildCommand("id", view);

//       i = commands.iterator();
//       assertEquals("simple", "\\label{Abstract Entry}", i.next().toString());
//       assertEquals("simple", "\\link[/entry/abstractentry/a]{a}",
//                    i.next().toString());
//       assertEquals("simple", "", i.next().toString());
//       assertEquals("simple", "\\label{Abstract Entry}", i.next().toString());
//       assertEquals("simple", "\\link[/entry/abstractentry/b]{b}",
//                    i.next().toString());
//       assertEquals("simple", "", i.next().toString());
//       assertEquals("simple", "\\label{Abstract Entry}", i.next().toString());
//       assertEquals("simple", "\\link[/entry/abstractentry/c]{c}",
//                    i.next().toString());
//       assertEquals("simple", "", i.next().toString());
//       assertEquals("simple", "\\label{Abstract Entry}", i.next().toString());
//       assertEquals("simple", "\\link[/entry/abstractentry/d]{d}",
//                    i.next().toString());
//       assertEquals("simple", "", i.next().toString());
//       assertFalse("simple", i.hasNext());
    }

    //......................................................................
  }

  //........................................................................
}
