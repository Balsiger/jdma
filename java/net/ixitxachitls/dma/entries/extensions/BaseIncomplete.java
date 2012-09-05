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

package net.ixitxachitls.dma.entries.extensions;

import javax.annotation.Nonnull;

import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.values.LongFormattedText;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the incomplete extension for all the entries.
 *
 * @file          BaseIncomplete.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class BaseIncomplete extends BaseExtension<BaseEntry>
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- BaseIncomplete ---------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base entry attached to
   * @param       inName  the name of the extension
   *
   */
  public BaseIncomplete(@Nonnull BaseEntry inEntry, @Nonnull String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //----------------------------- BaseIncomplete ---------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base entry attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   * @undefined   never
   *
   */
  // public BaseIncomplete(BaseEntry inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  // /** The printer for printing the whole base item. */
  // public static final Print s_pagePrint =
  //   new Print("%incomplete");

  //----- incomplete -------------------------------------------------------

  /** The incomplete radius. */
  @Key("incomplete")
  protected @Nonnull LongFormattedText m_incomplete = new LongFormattedText();

  //........................................................................

  static
  {
    setAutoExtensions(BaseIncomplete.class, "incomplete");
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
  // @Override
  // protected @Nonnull Print getPagePrint()
  // {
  //   return s_pagePrint;
  // }

  //........................................................................
  //----------------------------- getListPrint -----------------------------

  /**
   * Get the print for a list entry.
   *
   * @return the print for list entry
   *
   */
  // @Override
  // protected @Nonnull ListPrint getListPrint()
  // {
  //   return s_listPrint;
  // }

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
