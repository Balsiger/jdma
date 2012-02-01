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
import net.ixitxachitls.dma.values.Area;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.Group;
import net.ixitxachitls.dma.values.formatters.Formatter;
import net.ixitxachitls.dma.values.formatters.LinkFormatter;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the commodity extension for all the entries.
 *
 * @file          BaseCommodity.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class BaseCommodity extends BaseExtension<BaseItem>
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- BaseCommodity ----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inName the name of the extension
   *
   */
  public BaseCommodity(@Nonnull BaseItem inEntry, @Nonnull String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //----------------------------- BaseCommodity ----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   */
  // public BaseCommodity(BaseItem inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The printer for printing the whole base item. */
  public static final Print s_pagePrint =
    new Print("%area %length");

  //----- area -------------------------------------------------------------

  /** The formatter for types. */
  protected static final Formatter<Area> s_areaFormatter =
    new LinkFormatter<Area>(link(BaseItem.TYPE, Index.Path.AREAS));

  /** The grouping for max dex. */
  protected static final Group<Area, Long, String> s_areaGrouping =
    new Group<Area, Long, String>(new Group.Extractor<Area, Long>()
      {
        public Long extract(@Nonnull Area inValue)
        {
          return (long)inValue.getAsFeet().getValue() * 144;
        }
      }, new Long [] { 1L, 144 * 1L, 144 * 9L, 144 * 9 * 10L, 144 * 9 * 100L, },
                               new String []
      { "1 sq inch", "1 sq foot", "1 sq yard", "10 sq yards", "100 sq yards",
        "ocean wide", }, "$undefined$");

  /** The area for this commodity. */
  @Key("area")
  protected @Nonnull Area m_area = new Area().withFormatter(s_areaFormatter)
    .withGrouping(s_areaGrouping);

  static
  {
    addIndex(new Index(Index.Path.AREAS, "Areas", BaseItem.TYPE));
  }

  //........................................................................
  //----- length -----------------------------------------------------------

  /** The formatter for types. */
  protected static final Formatter<Distance> s_lengthFormatter =
    new LinkFormatter<Distance>(link(BaseItem.TYPE, Index.Path.LENGTHS));

  /** The grouping for max dex. */
  protected static final Group<Distance, Long, String> s_lengthGrouping =
    new Group<Distance, Long, String>(new Group.Extractor<Distance, Long>()
      {
        public Long extract(Distance inValue)
        {
          if(inValue == null)
            throw new IllegalArgumentException("must have a value here");

          return (long)inValue.getAsFeet().getValue() * 12;
        }
      }, new Long [] { 1L, 12 * 1L, 12 * 10L, 12 * 25L, 12 * 50L, 12 * 100L,
                       12 * 250L, 12 * 500L, },
                               new String []
      { "1 in", "1 ft", "10 ft", "25 ft", "50 ft", "100 ft", "250 ft",
        "500 ft", "Infinite", }, "$undefined$");

  /** The length of this commodity. */
  @Key("length")
  protected @Nonnull Distance m_length = new Distance()
    .withFormatter(s_lengthFormatter).withGrouping(s_lengthGrouping);

  static
  {
    addIndex(new Index(Index.Path.LENGTHS, "Lengths", BaseItem.TYPE));
  }

  //........................................................................

  static
  {
    setAutoExtensions(BaseCommodity.class, "commodity");
    extractVariables(BaseItem.class, BaseCommodity.class);
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

    ioValues.put(Index.Path.AREAS, m_area.group());
    ioValues.put(Index.Path.LENGTHS, m_length.group());
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
