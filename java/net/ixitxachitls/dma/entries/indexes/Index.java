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
import java.util.Locale;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;

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
@ParametersAreNonnullByDefault
public class Index implements Serializable, Comparable<Index>
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Index ---------------------------------

  /**
   * Create the index.
   *
   * @param         inPath      the path to the index
   * @param         inTitle     the index title
   * @param         inType      the type of entries served
   *
   */
  public Index(Path inPath, String inTitle,
               AbstractType<? extends AbstractEntry> inType)
  {
    m_path = inPath;
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
  public Index withImages()
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
  public Index withoutPagination()
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
  public Index withEditable()
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
  // public Index<I> withAccess(BaseCharacter.Group inAccess)
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

  /** The prefix for index names. */
  public static final String PREFIX = "index-";

  /** The available index paths. */
  public enum Path
  {
    // CHECKSTYLE:OFF
    PERSONS, JOBS, DATES, AUDIENCES, PAGES, SYSTEMS, TYPES, STYLES,
    PRODUCERS, SERIES, PRICES, PARTS, LAYOUTS, WORLDS, TITLES, VALUES,
    WEIGHTS, PROBABILITIES, SIZES, HARDNESSES, HPS, SUBSTANCES, DISTANCES,
    BREAKS, CATEGORIES, DAMAGES, DAMAGE_TYPES, CRITICALS, THREATS, WEAPON_TYPES,
    WEAPON_STYLES, PROFICIENCIES, RANGES, REACHES, SLOTS, DONS, REMOVES,
    REFERENCES, LIGHTS, DURATIONS, EXTENSIONS, CAPACITIES, STATES, COUNTS,
    UNITS, AREAS, LENGTHS, ARMOR_BONUSES, ARMOR_TYPES, MAX_DEXTERITIES,
    CHECK_PENALTIES, ARCANE_FAILURES, SPEEDS, DMS,
    SUBTYPES, HDS, DICES, MOVEMENT_MODES, MANEUVERABILITIES,
    NATURAL_ARMORS, BASE_ATTACKS, STRENGTHS, DEXTERITIES, CONSTITUTIONS,
    INTELLIGENCES, WISDOMS, CHARISMAS, SPACES, CLIMATES, TERRAINS, CRS,
    TREASURES, ALIGNMENTS, LEVEL_ADJUSTMENTS, LANGUAGES, ORGANIZATIONS,
    EFFECT_TYPES, AFFECTS, MODIFIERS,
    ABILITIES, RESTRICTIONS,
    SCHOOLS, SUBSCHOOLS, DESCRIPTORS, CLASSES, LEVELS, COMPONENTS, MATERIALS,
    FOCUSES, CASTING_TIMES, EFFECTS, SAVING_THROWS, SPELL_RESISTANCES,
    FORTITUDE_SAVES, WILL_SAVES, REFLEX_SAVES,
    PARENT, DM;
    // CHECKSTYLE:ON

    /**
     * Get the path to this index.
     *
     * @return the path to the index
     */
    public String getPath()
    {
      return toString().toLowerCase(Locale.US).replace("_", "");
    }
  }

  /** The joiner to put together the string for nested indexes. */
  private static final Joiner s_joinGroups = Joiner.on("::");

  /** The index path. */
  private Path m_path;

  /** The index title. */
  private String m_title;

  /** The type of entries in this index. */
  private AbstractType<? extends AbstractEntry> m_type;

  /** Flag if showing images or not. */
  private boolean m_images = false;

  /** Flag if index is editable or not. */
  private boolean m_editable = false;

  /** Flag if index is paginated or not. */
  private boolean m_paginated = true;

  /** The access level required for this index. */
  //private @Nullable BaseCharacter.Group m_access = null;

  /** Version for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //---------------------------- groupsToString ----------------------------

  /**
   * Convert given groups into a string for storing.
   *
   * @param       inGroups the index group values
   *
   * @return      the converted string
   *
   */
  public static String groupsToString(String ... inGroups)
  {
    return s_joinGroups.join(inGroups);
  }

  //........................................................................
  //---------------------------- stringToGroups ----------------------------

  /**
   * Convert the given string back into an array of group names.
   *
   * @param       inText the text to convert
   *
   * @return      the individual groups
   *
   */
  public static String [] stringToGroups(String inText)
  {
    return inText.split("::");
  }

  //........................................................................

  //------------------------------- getType --------------------------------

  /**
   * Get the type of entries in this index.
   *
   * @return the type of entries
   *
   */
  public String getPath()
  {
    return m_path.getPath();
  }

  //........................................................................
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
  public String getTitle()
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
//   public Identificator<AbstractEntry> getIdentificator()
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
//   public Formatter getFormatter()
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
//   public String getFormat()
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
  // public boolean allows(BaseCharacter.Group inLevel)
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
  @Override
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
  @Override
  public boolean equals(Object inOther)
  {
    if(this == inOther)
      return true;

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
  @Override
public int hashCode()
  {
    return m_title.hashCode();
  }

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
  public boolean isEditable(@Nullable String inPath)
  {
    return m_editable;
  }

  //........................................................................
  //------------------------------- toString -------------------------------

  /**
   * Conver the index to a string for debugging.
   *
   * @return      the string representation
   *
   */
  @Override
  public String toString()
  {
    return m_title + " (" + m_path + "/" + m_type
      + (m_images ? " with images" : "")
      + (m_editable ? " is editable" : "")
      + (m_paginated ? " is paginated" : "")
      + ")";
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
    //----- init -----------------------------------------------------------

    /** The init Test. */
    @org.junit.Test
    public void init()
    {
      Index index = new Index(Path.TITLES, "title",
                              net.ixitxachitls.dma.entries.BaseCharacter.TYPE);

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
