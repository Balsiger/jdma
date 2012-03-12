/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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

import net.ixitxachitls.dma.entries.Item;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.values.Number;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the commodity extension for all the entries.
 *
 * @file          Commodity.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Commodity extends Extension<Item>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Commodity ------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the item attached to
   * @param       inName the name of the extension
   *
   */
  public Commodity(@Nonnull Item inEntry, @Nonnull String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //------------------------------- Commodity ------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the item attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   */
  // public Commodity(Item inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The printer for printing the whole base item. */
  public static final Print s_pagePrint =
    new Print("%amount %area %length");

  //----- amount -----------------------------------------------------------

  /** The amount of units of this commodity. */
  @Key("amount")
  protected Number m_amount = new Number(1, 10000);

  //........................................................................

  static
  {
    extractVariables(Item.class, Commodity.class);
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
  //--------------------------- addPrintCommands ---------------------------

  /**
   * Add the commands for printing this extension to the given print command.
   *
   * @param       ioCommands the commands to add to
   * @param       inDM       flag if setting for DM or not
   * @param       inEditable flag if values editable or not
   *
   * @undefined   IllegalArgumentException if given commands are null
   *
   */
  // public void addPrintCommands(@MayBeNull PrintCommand ioCommands,
  //                              boolean inDM, boolean inEditable)
  // {
  //   if(ioCommands == null)
  //     return;

  //   super.addPrintCommands(ioCommands, inDM, inEditable);

  //   BaseCommodity base = getBases(BaseCommodity.class).get(0);

  //   if(base == null)
  //     ioCommands.addExtensionValue(m_amount, "amount", "commodity",
  //                                   inEditable);
  //   else
  //     ioCommands.addValue(createHighlightedValueLabel("amount", "commodity"),
  //                         new Command(new Object []
  //                           {
  //                             createValueCommand(m_amount, "amount",
  //                                                inEditable),
  //                             " x ",
  //                             base.m_area.isDefined()
  //                             ? base.m_area.format(false)
  //                             : base.m_length.format(false),
  //                           }));
  // }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................
}
