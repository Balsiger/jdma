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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.dma.entries.FormattedValue;
import net.ixitxachitls.dma.entries.Item;
import net.ixitxachitls.dma.entries.ValueHandle;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Count;
import net.ixitxachitls.output.commands.Symbol;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the counted extension for all the entries.
 *
 * @file          Counted.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Counted extends Extension<Item>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Counted ------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the entry attached to
   * @param       inName the name of the extension
   *
   */
  public Counted(@Nonnull Item inEntry, @Nonnull String inName)
  {
    super(inEntry, inName);

    init();
  }

  //........................................................................
  //------------------------------- Counted ------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the entry attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   */
  // public Counted(Item inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The printer for printing the whole base item. */
  public static final Print s_pagePrint =
    new Print("%count %unit");

  public static final Print s_emptyPrint = new Print("");

  //----- count ------------------------------------------------------------

  /** The amount of units of this counted. */
  @Key("count")
  protected Number m_count = new Number(0, 10000);

  //........................................................................

  static
  {
    extractVariables(Item.class, Counted.class);
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
  @Override
  public @Nullable ValueHandle computeValue(@Nonnull String inKey, boolean inDM)
  {
    if(inDM && "summary".equals(inKey))
    {
      List<Object> commands = new ArrayList<Object>();
      commands.add(new Symbol("\u2736"));
      maybeAddValue(commands, "count", inDM, null, null);
      maybeAddValue(commands, "unit", inDM, " ", null);

      return new FormattedValue(new Command(commands), null, "summary");
    }

    if("list".equals(inKey))
      return new FormattedValue
        (new Command(m_entry.getNameCommand(inDM),
                     new Count(m_count.get(), m_count.get(),
                               computeValue("unit", inDM)
                               .format(this, inDM, true))), null, "list");

    return super.computeValue(inKey, inDM);
  }

  //........................................................................
  //--------------------------- addListCommands ----------------------------

  /**
    *
    * Add the commands for printing this extension to a list.
    *
    * @param       ioCommands the commands to add to
    * @param       inDM   true if printing for dm, false else
    *
    */
  //public void addListCommands(@MayBeNull ListCommand ioCommands, boolean inDM)
  // {
  //   if(ioCommands == null)
  //     return;

  //   ioCommands.add(ListCommand.Type.COUNTED,
  //                  new Command(new Object []
  //     {
  //       new Bold(new Color("subtitle", m_entry.getPlayerName())),
  //       new Super(new Scriptsize("(" + m_entry.getID() + ")")),
  //     }));

  //   List<BaseCounted> bases = getBases(BaseCounted.class);

  // if(bases == null || bases.size() == 0 || !bases.get(0).m_count.isDefined())
  //     ioCommands.add(ListCommand.Type.COUNTED,
  //                    new Count(m_count.get(), m_count.get(), ""));
  //   else
  //     ioCommands.add(ListCommand.Type.COUNTED,
  //                  new Count(m_count.get(), bases.get(0).m_count.get(), ""));
  // }

  //........................................................................

  //-------------------------- addSummaryCommand ---------------------------

  /**
   * Add the extensions value to the summary command list.
   *
   * @param       ioCommands the commands so far, will add here
   * @param       inDM       true if setting for dm
   *
   */
  // public void addSummaryCommands(List<Object> ioCommands, boolean inDM)
  // {
  //   ioCommands.add(", ");
  //   ioCommands.add(m_count.format(false));
  //   ioCommands.add(" ");

  //   if(getBases(BaseCounted.class).size() > 0)
  //   {
  //     BaseCounted base = getBases(BaseCounted.class).get(0);

  //     if(base != null)
  //     {
  //       BaseCounted.Unit unit = base.m_unit.getSelected();

  //       if(unit != null)
  //       {
  //         if(m_count.get() == 1)
  //           ioCommands.add(unit.getName());
  //         else
  //           ioCommands.add(unit.getMultiple());
  //       }
  //     }
  //   }
  //   else
  //     if(m_count.get() == 1)
  //       ioCommands.add("time");
  //     else
  //       ioCommands.add("times");
  // }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled.
   *
   */
  @Override
  public void complete()
  {
    super.complete();

    init();
  }

  //........................................................................
  //--------------------------------- init ---------------------------------

  /**
   * Initialize the count if it is not yet set.
   *
   */
  private void init()
  {
    if(!m_count.isDefined())
    {
      Number total = new Combination<Number>(this, "count").total();
      if(total != null)
      {
        m_count = m_count.as(total.get());
        changed();
      }
    }
  }

  //........................................................................


  //........................................................................

  //------------------------------------------------- other member functions

  //----------------------------- modifyValue ------------------------------

  /**
    *
    * Modify the given value with information from the current extension.
    *
    * @param       inType    the type of value to modify
    * @param       inEntry   the entry to modify in
    * @param       inValue   the value to modify, return in this object
    * @param       inDynamic a flag denoting if dynamic modifiers should be
    *                        returned
    *
    * @return      the newly computed value (or null if no value to use)
    *
    * @undefined   never
    *
    * @algorithm   nothing done here
    *
    * @derivation  necessary if real modifications are desired
    *
    * @example     see Item
    *
    * @bugs
    * @to_do
    *
    * @keywords    modify . value
    *
    */
//   public Modifier modifyValue(PropertyKey inType, AbstractEntry inEntry,
//                               Value inValue, boolean inDynamic)
//   {
//     if(inValue == null || !inValue.isDefined())
//       return null;

//     if(m_count.isDefined() && inDynamic && inType == BaseCounted.UNIT)
//       if(m_count.get() != 1)
//         return
//           new Modifier(Modifier.Type.FIXED,
//                        new Selection(Global.COUNT_UNITS_PLURAL,
//                                      ((Selection)inValue).getSelected()));

//     return super.modifyValue(inType, inEntry, inValue, inDynamic);
//   }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
