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
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Symbol;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the wearable extension for all the entries.
 *
 * @file          Wearable.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Wearable extends Extension<Item>
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Wearable -------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the entry this extension is attached to
   * @param       inName  the name of the extension
   *
   */
  public Wearable(@Nonnull Item inEntry, @Nonnull String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //-------------------------------- Wearable -------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the entry this extension is attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   */
  // public Wearable(Item inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The printer for printing the whole base item. */
  /** The printer for printing the whole base item. */
  public static final Print s_pagePrint =
    new Print("%slot %don %remove");

  //----- user size --------------------------------------------------------

  // /** The size of items of this kind. */
  // @Key("user size")
  // protected EnumSelection<BaseItem.Size> m_userSize =
  //   new EnumSelection<BaseItem.Size>(BaseItem.Size.class);

  //........................................................................

  static
  {
    extractVariables(Item.class, Wearable.class);
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
    if("summary".equals(inKey))
    {
      List<Object> commands = new ArrayList<Object>();
      commands.add(new Symbol("\u269c"));
      maybeAddValue(commands, "slot", inDM, " ", null);
      maybeAddValue(commands, "don", inDM, ", don ", null);
      maybeAddValue(commands, "remove", inDM, ", remove ", null);

      return new FormattedValue(new Command(commands), null, "summary");
    }

    return super.computeValue(inKey, inDM);
  }

  //........................................................................
  //------------------------------ getUserSize -----------------------------

  /**
   * Get the size of the intended user of an item.
   *
   * @return      the user size
   *
   */
  // public @Nonnull BaseItem.Size getUserSize()
  // {
  //   return m_userSize.getSelected();
  // }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //----------------------------- setUserSize ------------------------------

  /**
   * Set the user size of the item.
   *
   * @param       inSize the new size
   *
   * @return      true if set, false on error
   *
   */
  // public boolean setUserSize(@Nonnull BaseItem.Size inSize)
  // {
  //   if(!m_userSize.set(inSize))
  //     return false;

  //   // call complete to update all relevant modifiers
  //   complete();

  //   return true;
  // }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled.
   */
  // @Override
  // public void complete()
  // {
  //   super.complete();

  //   //----- user size ------------------------------------------------------

  //   // do we have to compute an intended user size?
  //   if(!m_userSize.isDefined())
  //   {
  //     m_userSize.set(BaseItem.Size.MEDIUM);
  //     changed(true);
  //   }

  //   //......................................................................
  //   //----- value ----------------------------------------------------------

  //   // adjust the value according to size
  //   BaseItem.Size size = m_userSize.getSelected();

  //   if(size.isBigger(BaseItem.Size.MEDIUM))
  //     m_entry.addValueModifier
  //       (new NumberModifier
  //        (BaseModifier.Operation.MULTIPLY,
  //         (int)Math.pow(2, size.difference(BaseItem.Size.MEDIUM)),
  //         BaseModifier.Type.GENERAL, "user size " + size));

  //   //......................................................................
  //   //----- hp -------------------------------------------------------------

  //   // adjust the hit points according to size
  //   if(size != BaseItem.Size.MEDIUM)
  //     if(size.isBigger(BaseItem.Size.MEDIUM))
  //     {
  //       m_entry.addMaxHPModifier
  //         (new NumberModifier
  //          (BaseModifier.Operation.MULTIPLY,
  //           (int)Math.pow(2, size.difference(BaseItem.Size.MEDIUM)),
  //           BaseModifier.Type.GENERAL, "user size " + size));
  //       m_entry.addHPModifier
  //         (new NumberModifier
  //          (BaseModifier.Operation.MULTIPLY,
  //           (int)Math.pow(2, size.difference(BaseItem.Size.MEDIUM)),
  //           BaseModifier.Type.GENERAL, "user size " + size));
  //     }
  //     else
  //     {
  //       m_entry.addMaxHPModifier
  //         (new NumberModifier
  //          (BaseModifier.Operation.DIVIDE,
  //           (int)Math.pow(2, BaseItem.Size.MEDIUM.difference(size)),
  //           BaseModifier.Type.GENERAL, "user size " + size));
  //       m_entry.addHPModifier
  //         (new NumberModifier
  //          (BaseModifier.Operation.DIVIDE,
  //           (int)Math.pow(2, BaseItem.Size.MEDIUM.difference(size)),
  //           BaseModifier.Type.GENERAL, "user size " + size));
  //     }

  //   //......................................................................
  //   //----- weight ---------------------------------------------------------

  //   // adjust the weight for size
  //   if(size != BaseItem.Size.MEDIUM)
  //     if(size.isBigger(BaseItem.Size.MEDIUM))
  //       m_entry.addWeightModifier
  //         (new NumberModifier
  //           (BaseModifier.Operation.MULTIPLY,
  //            (int)Math.pow(2, size.difference(BaseItem.Size.MEDIUM)),
  //           BaseModifier.Type.GENERAL, "user size " + size));
  //      else
  //        m_entry.addWeightModifier
  //          (new NumberModifier
  //           (BaseModifier.Operation.DIVIDE,
  //            (int)Math.pow(2, BaseItem.Size.MEDIUM.difference(size)),
  //           BaseModifier.Type.GENERAL, "user size " + size));


  //   //......................................................................

  //   super.complete();
  // }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
 //public static class Test extends net.ixitxachitls.dma.entries.ValueGroup.Test
  // {
    //----- get/set --------------------------------------------------------

    /** Test getting and setting values. */
    // public void testGetSet()
    // {
    //   BaseItem base = new BaseItem("some item");

    //   assertTrue("base hp", base.setHP(42));
    //   assertTrue("value", base.setValue(0, 12, 3, 0));
    //   assertTrue("weight", base.setWeight(new net.ixitxachitls.dma.values
    //                                       .Rational(2, 1, 2)));

    //   Item item = new Item(base);

    //   Wearable extension = new Wearable(item, "wearable");
    //   extension.m_userSize.set(BaseItem.Size.HUGE);

    //   item.addExtension("wearable", extension);

    //   item.complete();

    //   assertEquals("user size", BaseItem.Size.HUGE, extension.getUserSize());
    //   assertEquals("value",
    //                "48 gp 12 sp [+12 gp 3 sp some item, *4 user size Huge]",
    //                item.getValue("value").toString());
    //   assertEquals("value", "48 gp 12 sp", item.getValue().toString());
    //   assertEquals("user size hp", "168 [+42 some item, *4 user size Huge]",
    //                item.getValue("max hp").toString());
    //   assertEquals("user size hp", 168, item.getMaxHP());
    //   assertEquals("user size hp", 168, item.getHP());
    //   assertEquals("weight",
    //                "10 lbs [+2 1/2 lbs some item, *4 user size Huge]",
    //                item.getValue("weight").toString());

    //   item.complete();

    //   assertEquals("user size", BaseItem.Size.HUGE, extension.getUserSize());
    //   assertEquals("value",
    //                "48 gp 12 sp [+12 gp 3 sp some item, *4 user size Huge]",
    //                item.getValue("value").toString());
    //   assertEquals("value", "48 gp 12 sp", item.getValue().toString());
    //   assertEquals("user size hp", "168 [+42 some item, *4 user size Huge]",
    //                item.getValue("max hp").toString());
    //   assertEquals("user size hp", 168, item.getMaxHP());
    //   assertEquals("user size hp", 168, item.getHP());
    //   assertEquals("weight",
    //                "10 lbs [+2 1/2 lbs some item, *4 user size Huge]",
    //                item.getValue("weight").toString());
    // }

    //......................................................................
  // }

  //........................................................................
}
