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

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.Item;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the armor extension for all the entries.
 *
 * @file          Armor.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Armor extends Extension<Item>
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Armor -------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the item attached to
   * @param       inName  the name of the extension
   *
   * @undefined   never
   *
   */
  public Armor(@Nonnull Item inEntry, @Nonnull String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //--------------------------------- Armor -------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the item attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   * @undefined   never
   *
   */
  // public Armor(Item inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The printer for printing the whole base item. */
  public static final Print s_pagePrint =
    new Print("%{AC bonus} %{armor type} %<{max dexterity} %{check penalty} "
              + "%{arcane failure} %speed");

  static
  {
    extractVariables(Armor.class);
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
  }

  //........................................................................
  //-------------------------- addSummaryCommand ---------------------------

  /**
   * Add the attachments value to the summary command list.
   *
   * @param       ioCommands the commands so far, will add here
   * @param       inDM       true if setting for dm
   *
   */
  // public void addSummaryCommands(List<Object> ioCommands, boolean inDM)
  // {
  //   ioCommands.add(", ");

  //   BaseArmor base = getBases(BaseArmor.class).get(0);

  //   if(base == null)
  //     ioCommands.add(new Color("error", "base armor not found"));
  //   else
  //   {
  //     ioCommands.add(base.m_type.format(false));
  //     ioCommands.add(" ");
  //     ioCommands.add(base.m_bonus.format(false));
  //     ioCommands.add(", max DEX ");
  //     ioCommands.add(base.m_maxDex.format(false));
  //     ioCommands.add(", check penalty ");
  //     ioCommands.add(base.m_checkPenalty.format(false));
  //     ioCommands.add(", arcane failure ");
  //     ioCommands.add(base.m_arcane.format(false));
  //     ioCommands.add(", max speed ");
  //     ioCommands.add(base.m_speed);
  //   }
  // }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------- completeExternalValue ------------------------

  /**
    *
    * Complete a specific value in the given entry, making sure that it
    * afterwards contains a valid value.
    *
    * Use this specific value only for values that are not part of the
    * attachment.
    *
    * @param       inCampaign the campaign with all the data
    * @param       inBase     the base entry to complete with
    * @param       inEntry    the entry to complete
    * @param       inVariable the specific variable to complete
    *
    * @undefined   never
    *
    * @algorithm   check all values and fill them.
    *
    * @derivation  must be derived to complete all values of the derivations
    *
    * @example     used internally only
    *
    * @bugs
    * @to_do
    *
    * @keywords    complete . entry
    *
    */
  // TODO: check this and fix
//   public void completeExternalValue(CampaignData inCampaign,
//                                     AbstractEntry inBase,
//                                   AbstractEntry inEntry, Variable inVariable)
//   {
//     if(inVariable == null)
//       throw new IllegalArgumentException("must have a variable here");

//     if(inEntry == null)
//       throw new IllegalArgumentException("must have an entry here");

//     BaseItem.Size size =
//       ((EnumSelection<BaseItem.Size>)inEntry.getValue("user size"))
//       .getSelected();

//     // only a change necessary if size is not medium
//     if(size != BaseItem.Size.MEDIUM)
//     {
//       if(inVariable.getKey() == "value")
//       {
//         Money value = (Money)inVariable.get(inEntry);

//         if(size.isBigger(BaseItem.Size.MEDIUM))
//           value.multiply((int)Math.pow(2,
//                                      size.difference(BaseItem.Size.MEDIUM)));
//         else
//           if(size.isSmaller(BaseItem.Size.SMALL)) // tiny or smaller
//             value.divide(2);

//         inVariable.set(inEntry, value);
//       }

//       if(inVariable.getKey() == "weight")
//       {
//         Weight weight = (Weight)inVariable.get(inEntry);

//         switch(size)
//         {
//           case SMALL:
//             weight.divide(2);

//             break;

//           case LARGE:
//             weight.multiply(2);

//             break;

//           case HUGE:
//             weight.multiply(5);

//             break;

//           case GARGANTUAN:
//             weight.multiply(8);

//             break;

//           case COLOSSAL:
//             weight.multiply(12);

//             break;

//           default:
//             weight.divide(10);
//         }

//         inVariable.set(inEntry, weight);
//       }
//     }
//   }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
