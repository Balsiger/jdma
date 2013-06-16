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

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.values.Area;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.Group;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the commodity extension for all the entries.
 *
 * @file          BaseCommodity.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class BaseCommodity extends BaseExtension<BaseItem>
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- BaseCommodity ----------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inName the name of the extension
   *
   */
  public BaseCommodity(BaseItem inEntry, String inName)
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

  //----- area -------------------------------------------------------------

  /** The grouping for max dex. */
  protected static final Group<Area, Long, String> s_areaGrouping =
    new Group<Area, Long, String>(new Group.Extractor<Area, Long>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Long extract(Area inValue)
        {
          return (long)inValue.getAsFeet().getValue() * 144;
        }
      }, new Long [] { 1L, 144 * 1L, 144 * 9L, 144 * 9 * 10L, 144 * 9 * 100L, },
                               new String []
      { "1 sq inch", "1 sq foot", "1 sq yard", "10 sq yards", "100 sq yards",
        "ocean wide", }, "$undefined$");

  /** The area for this commodity. */
  @Key("area")
  protected Area m_area = new Area()
    .withGrouping(s_areaGrouping)
    .withTemplate("link", Index.Path.AREAS.getPath());

  static
  {
    addIndex(new Index(Index.Path.AREAS, "Areas", BaseItem.TYPE));
  }

  //........................................................................
  //----- length -----------------------------------------------------------

  /** The grouping for max dex. */
  protected static final Group<Distance, Long, String> s_lengthGrouping =
    new Group<Distance, Long, String>(new Group.Extractor<Distance, Long>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
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
  protected Distance m_length = new Distance()
    .withGrouping(s_lengthGrouping);

  static
  {
    addIndex(new Index(Index.Path.LENGTHS, "Lengths", BaseItem.TYPE));
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------- computeIndexValues ---------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @param       ioValues a multi map of values per index name
   *
   */
  @Override
  public void computeIndexValues(Multimap<Index.Path, String> ioValues)
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
