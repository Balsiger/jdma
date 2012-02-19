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
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Group;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.formatters.DelimFormatter;
import net.ixitxachitls.dma.values.formatters.Formatter;
import net.ixitxachitls.dma.values.formatters.LinkFormatter;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the light extension for all the entries.
 *
 * @file          BaseLight.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class BaseLight extends BaseExtension<BaseItem>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- BaseLight ------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inName  the name of the extension
   *
   */
  public BaseLight(@Nonnull BaseItem inEntry, @Nonnull String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //------------------------------- BaseLight ------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   * @undefined   never
   *
   */
  // public BaseLight(BaseItem inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The printer for printing the whole base item. */
  public static final Print s_pagePrint =
    new Print("%{bright light} %{shadowy light}");

  //----- bright light -----------------------------------------------------

  /** The formatter for light failure. */
  protected static final Formatter<Multiple> s_lightFormatter =
    new LinkFormatter<Multiple>("/items/lights/");

  /** The grouping for bright light. */
  protected static final Group<Multiple, Long, String> s_brightGrouping =
    new Group<Multiple, Long, String>(new Group.Extractor<Multiple, Long>()
      {
        @Override
		public Long extract(@Nonnull Multiple inValue)
        {
          return (long)((Distance)inValue.get(0)).getAsFeet().getValue();
        }
      }, new Long [] { 0L, 5L, 10L, 25L, 50L, 100L, 250L, },
                                new String []
      { "0 ft bright", "5 ft bright", "10 ft bright", "25 ft bright",
        "50 ft bright", "100 ft bright", "250 ft bright", "infinite bright",
      }, "$undefined$");

  /** The formatter to get a space (before). */
  protected static final Formatter<Distance> s_spaceFormatterD =
    new DelimFormatter<Distance>(" ", null);

  /** The formatter for the space (before). */
  protected static final Formatter<Distance> s_spaceFormatter =
    new DelimFormatter<Distance>(" ", null);

  /** The formatter to get a space (after). */
  protected static final Formatter<Distance> s_spaceAfterFormatter =
    new DelimFormatter<Distance>(null, " ");

  /** The light radius. */
  @Key("bright light")
  protected Multiple m_brightLight = new Multiple(new Multiple.Element []
    {
      // we have to use a formatter for the distance, or distance will use
      // its own formatter, which clashes with the index formatter given
      // for the enclosing Multiple
      new Multiple.Element(new Distance()
                           .withFormatter(s_spaceAfterFormatter), false),
      new Multiple.Element(new EnumSelection<BaseItem.AreaShapes>
                           (BaseItem.AreaShapes.class), false),
    }).withGrouping(s_brightGrouping).withFormatter(s_lightFormatter);

  static
  {
    addIndex(new Index(Index.Path.LIGHTS, "Lights", BaseItem.TYPE));
  }

  //........................................................................
  //----- shadowy light ----------------------------------------------------

  /** The grouping for shadowy light. */
  protected static final Group<Multiple, Long, String> s_shadowyGrouping =
    new Group<Multiple, Long, String>(new Group.Extractor<Multiple, Long>()
      {
        @Override
		public Long extract(@Nonnull Multiple inValue)
        {
          return (long)((Distance)inValue.get(0)).getAsFeet().getValue();
        }
      }, new Long [] { 0L, 5L, 10L, 25L, 50L, 100L, 250L, },
                                new String []
      { "0 ft shadowy", "5 ft shadowy", "10 ft shadowy", "25 ft shadowy",
        "50 ft shadowy", "100 ft shadowy", "250 ft shadowy",
        "infinite shadowy", }, "$undefined$");

  /** The light radius. */
  @Key("shadowy light")
  protected Multiple m_shadowyLight = new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new Distance().withFormatter(s_spaceAfterFormatter),
                           false),
      new Multiple.Element(new EnumSelection<BaseItem.AreaShapes>
                           (BaseItem.AreaShapes.class), false),
    }).withGrouping(s_shadowyGrouping).withFormatter(s_lightFormatter);

  //........................................................................

  static
  {
    setAutoExtensions(BaseLight.class, "light");
    extractVariables(BaseItem.class, BaseLight.class);
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
  @Override
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
  @Override
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

    // light
    ioValues.put(Index.Path.LIGHTS, m_brightLight.group());
    ioValues.put(Index.Path.LIGHTS, m_shadowyLight.group());
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------ setBright -------------------------------

  /**
   * Set the bright light value in feet.
   *
   * @param       inRange the range of the light in feet
   * @param       inShape the shape of the range
   *
   * @return      true if set, false if not
   *
   * @undefined   never
   *
   */
  // @SuppressWarnings("unchecked") // cast to enum selection
  // public boolean setBright(int inRange, BaseItem.AreaShapes inShape)
  // {
  //   if(!((Distance)m_brightLight.get(0).getMutable())
  //      .setFeet(null, new Rational(inRange), null))
  //     return false;

  //   return ((EnumSelection<BaseItem.AreaShapes>)
  //           m_brightLight.get(1).getMutable()).set(inShape);
  // }

  //........................................................................
  //----------------------------- setShadowy -------------------------------

  /**
   * Set the shadowy light value in feet.
   *
   * @param       inRange the range of the light in feet
   * @param       inShape the shape of the range
   *
   * @return      true if set, false if not
   *
   * @undefined   never
   *r
   */
  // @SuppressWarnings("unchecked") // cast to enum selection
  // public boolean setShadowy(int inRange, BaseItem.AreaShapes inShape)
  // {
  //   if(!((Distance)m_shadowyLight.get(0).getMutable())
  //      .setFeet(null, new Rational(inRange), null))
  //     return false;

  //   return ((EnumSelection<BaseItem.AreaShapes>)
  //           m_shadowyLight.get(1).getMutable()).set(inShape);
  // }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
