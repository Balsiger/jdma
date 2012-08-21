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
import net.ixitxachitls.dma.values.Duration;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the timed extension for all the entries.
 *
 * @file          Timed.java
 *
 * @author        balsiger@ixitxachitlslnet (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Timed extends Extension<Item>
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Timed -------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the entry attached to
   * @param       inName  the name of the extension
   *
   */
  public Timed(@Nonnull Item inEntry, @Nonnull String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //--------------------------------- Timed -------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the entry attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   * @undefined   never
   *
   */
  // public Timed(Item inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The printer for printing the whole base item. */
  public static final Print s_pagePrint =
    new Print("%duration");

  //----- duration ---------------------------------------------------------

  /** The time that is left for the item. */
  @Key("duration")
  protected Duration m_duration = new Duration();

  //........................................................................

  static
  {
    extractVariables(Item.class, Timed.class);
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
  //----------------------------- computeValue -----------------------------

  /**
   * Get a value for printing.
   *
   * @param     inKey  the name of the value to get
   * @param     inDM   true if formattign for dm, false if not
   *
   * @return    a value handle ready for printing
   *
   */
  // @Override
  //public @Nullable ValueHandle computeValue(@Nonnull String inKey, boolean
  //inDM)
  // {
  //   if("duration".equals(inKey))
  //     return
  //       new FormattedValue(new Command
  //                          (computeValue("_duration", inDM)
  //                           .format(this, inDM, false),
  //                           " (max ",
  //                           new Combination(this, "duration")
  //                           .withIgnoreTop().format(inDM),
  //                           ")"),
  //                          m_duration, "duration")
  //       .withEditable(true);

  //   if("summary".equals(inKey))
  //   {
  //     List<Object> commands = new ArrayList<Object>();
  //     commands.add(new Symbol("\u27F3"));
  //     maybeAddValue(commands, "duration", inDM, null, null);

  //     return new FormattedValue(new Command(commands), null, "summary");
  //   }

  //   if("list".equals(inKey))
  //     return new FormattedValue
  //       (new Command(m_entry.getNameCommand(inDM),
  //                    computeValue("duration", inDM)
  //                    .format(m_entry, inDM, true)),
  //        null, "list");

  //   return super.computeValue(inKey, inDM);
  // }

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

  //   ArrayList<Object> commands = new ArrayList<Object>();

  //   BaseTimed base = getBases(BaseTimed.class).get(0);

  //   ioCommands.add(ListCommand.Type.TIMED,
  //                  new Command(new Object []
  //     {
  //       new Bold(new Color("subtitle", m_entry.getPlayerName())),
  //       new Super(new Scriptsize("(" + m_entry.getID() + ")")),
  //     }));

  //   if(base == null || !base.m_duration.isDefined())
  //     ioCommands.add(ListCommand.Type.TIMED, m_duration.format(true));
  //   else
  //     ioCommands.add(ListCommand.Type.TIMED,
  //                    new Command(new Object []
  //                      {
  //                        m_duration.format(true),
  //                        new Scriptsize(new Command(new Object []
  //                          {
  //                            " of ",
  //                            base.m_duration.format(true),
  //                          })),
  //                      }));
  // }

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

  //   if(inDM)
  //     ioCommands.addExtensionValue(m_duration, "duration", "timed",
  //                                   inDM && inEditable);
  // }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled.
   *
   */
  // public void complete()
  // {
  //   // take over the base value for the count
  //   if(!m_duration.isDefined())
  //   {
  //     BaseTimed base = getBases(BaseTimed.class).get(0);

  //     if(base != null)
  //       m_duration.add(base.m_duration);
  //   }
  // }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
