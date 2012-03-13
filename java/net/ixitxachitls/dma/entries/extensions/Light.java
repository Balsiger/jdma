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

//..........................................................................

//------------------------------------------------------------------- header

/**
  * This is the light extension for all the entries.
 *
 * @file          Light.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Light extends Extension<Item>
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Light --------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the entry attached to
   * @param       inName the name of the extension
   *
   */
  public Light(@Nonnull Item inEntry, @Nonnull String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //--------------------------------- Light --------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the entry attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   */
  // public Light(Item inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The printer for printing the whole base item. */
  public static final Print s_pagePrint =
    new Print("%{bright light} %{shadowy light}");

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
  //---------------------------- addListCommands ---------------------------

  /**
   * Add the commands for printing this extension to a list.
   *
   * @param       ioCommands the commands to add to
   * @param       inDM       flag if setting for DM or not
   *
   * @undefined   IllegalArgumentException if given commands are null
   *
   */
  //public void addListCommands(@MayBeNull ListCommand ioCommands, boolean inDM)
  // {
  //   if(ioCommands == null)
  //     return;

  //   super.addListCommands(ioCommands, inDM);

  //   BaseLight base = getBases(BaseLight.class).get(0);

  //   if(base == null)
  //     return;

  //   // damage + critical, type, style
  //   ioCommands.add(ListCommand.Type.LIGHT,
  //                  new Command(new Object []
  //     {
  //       new Bold(new Color("subtitle", m_entry.getPlayerName())),
  //       new Super(new Scriptsize("(" + m_entry.getID() + ")")),
  //     }));
  //   ioCommands.add(ListCommand.Type.LIGHT,
  //                  base.m_brightLight.get(0).get() + " "
  //                  + base.m_brightLight.get(1).get());
  //   ioCommands.add(ListCommand.Type.LIGHT,
  //                  base.m_shadowyLight.get(0).get() + " "
  //                  + base.m_shadowyLight.get(1).get());
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
