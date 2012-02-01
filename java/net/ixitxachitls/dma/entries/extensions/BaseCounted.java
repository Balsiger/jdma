/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.entries.extensions;

import javax.annotation.Nonnull;

import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.formatters.Formatter;
import net.ixitxachitls.dma.values.formatters.LinkFormatter;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the counted extension for all the entries.
 *
 * @file          BaseCounted.java
 *
 * @author        balsiger@ixitxachitls.net Peter 'Merlin' Balsiger
 *
 */

//..........................................................................

//__________________________________________________________________________

public class BaseCounted extends BaseExtension<BaseItem>
{
  //----------------------------------------------------------------- nested

  //----- units -------------------------------------------------------------

  /** The possible counting unites in the game. */
  public enum Unit implements EnumSelection.Named
  {
    /** Number of days. */
    DAY("day", "days"),
    /** Numer of pieces. */
    PIECE("piece", "pieces"),
    /** Number of sheets. */
    SHEET("sheet", "sheets"),
    /** Number of individual uses. */
    USE("use", "uses"),
    /** Number of pages. */
    PAGE("page", "pages"),
    /** Charges. */
    CHARGE("charge", "charges"),
    /** Can be applied. */
    APPLICATION("application", "applications"),
    /** Can absorb or take some damage. */
    DAMAGE("damage", "damage");

    /** The value's name. */
    private String m_name;

    /** The value's name for multiple unites. */
    private String m_multiple;

    /** Create the name.
     *
     * @param inName     the name of the value
     * @param inMultiple the text for multiple units
     *
     */
    private Unit(String inName, String inMultiple)
    {
      m_name = constant("count.unit.name", inName);
      m_multiple = constant("count.unit.multiple", inMultiple);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public String getName()
    {
      return m_name;
    }

    /** Get the multiple name.
     *
     * @return the multiple name
     *
     */
    public String getMultiple()
    {
      return m_multiple;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public String toString()
    {
      return m_name;
    }
  };

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //----------------------------- BaseCounted ----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inName  the name of the extension
   *
   */
  public BaseCounted(@Nonnull BaseItem inEntry, @Nonnull String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //----------------------------- BaseCounted ----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   */
  // public BaseCounted(BaseItem inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The printer for printing the whole base item. */
  public static final Print s_pagePrint =
    new Print("%count %unit");

  //----- count ------------------------------------------------------------

  /** The formatter for substances. */
  protected static final Formatter<Number> s_countFormatter =
    new LinkFormatter<Number>(link(BaseItem.TYPE, Index.Path.COUNTS));

  /** The base  count value. */
  @Key("count")
  protected @Nonnull Number m_count =
    new Number(1, 10000).withFormatter(s_countFormatter);

  static
  {
    addIndex(new Index(Index.Path.COUNTS, "Counts", BaseItem.TYPE));
  }

  //........................................................................
  //----- unit -------------------------------------------------------------

  /** The formatter for substances. */
  protected static final Formatter<EnumSelection<Unit>> s_unitFormatter =
    new LinkFormatter<EnumSelection<Unit>>
    (link(BaseItem.TYPE, Index.Path.UNITS));

  /** The unit used for the things counted. */
  @Key("unit")
  protected EnumSelection<Unit> m_unit = new EnumSelection<Unit>(Unit.class)
    .withFormatter(s_unitFormatter);

  static
  {
    addIndex(new Index(Index.Path.UNITS, "Units", BaseItem.TYPE));
  }

  //........................................................................

  static
  {
    setAutoExtensions(BaseCounted.class, "counted");
    extractVariables(BaseCounted.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- getPagePrint -----------------------------

  /**
   * Get the print for a full page.
   *
   * @return the print for page printing
   *
   */
  protected @Nonnull Print getPagePrint()
  {
    return s_pagePrint;
  }

  //........................................................................
  //----------------------------- getListPrint -----------------------------

  /**
   * Get the print for a list entry.
   *
   * @return the print for list entry
   *
   */
  protected @Nonnull ListPrint getListPrint()
  {
    return s_listPrint;
  }

  //........................................................................
  //------------------------- computeIndexValues ---------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @param       ioValues a multi map of values per index name
   *
   */
  @Override
  public void computeIndexValues(@Nonnull Multimap<Index.Path, String> ioValues)
  {
    super.computeIndexValues(ioValues);

    ioValues.put(Index.Path.COUNTS, m_count.group());
    ioValues.put(Index.Path.UNITS, m_unit.group());
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
