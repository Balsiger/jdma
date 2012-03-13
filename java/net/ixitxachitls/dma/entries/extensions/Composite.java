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
import java.util.Random;

import javax.annotation.Nonnull;

import net.ixitxachitls.dma.entries.Item;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.ValueList;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the composite extension for all the entries.
 *
 * @file          Composite.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Composite extends Extension<Item>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Composite ------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the item attached to
   * @param       inName the name of the extension
   *
   */
  public Composite(@Nonnull Item inEntry, @Nonnull String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //------------------------------- Composite ------------------------------

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
  // public Composite(Item inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The printer for printing the whole base item. */
  public static final Print s_pagePrint =
    new Print("%include");

  /** The random generator. */
  private static final Random s_random = new Random();

  //----- include ----------------------------------------------------------

  /** The composite (then this is like a container). */
  @Key("include")
  protected ValueList<Name> m_includes = new ValueList<Name>(new Name());

  //........................................................................

  static
  {
    extractVariables(Item.class, Composite.class);
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
  //---------------------------- getSubEntries -----------------------------

  /**
   * Get all the sub entries present in this extension.
   *
   * @return      the list with all the entries or null if none
   *
   * @undefined   never (may return null)
   *
   */
  // public List<Entry> getSubEntries()
  // {
  //   List<Entry> list = new ArrayList<Entry>();

  //   for(Iterator<EntryValue<Item>> i = m_includes.iterator(); i.hasNext(); )
  //     list.add(i.next().get());

  //   return list;
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
  // public void addListCommands(ListCommand ioCommands, boolean inDM)
  // {
  //   if(ioCommands == null)
  //     return;

  //   super.addListCommands(ioCommands, inDM);

  //   ioCommands.add(ListCommand.Type.NAME, " with ");
  //   for(Iterator<Entry> i = getSubEntries().iterator(); i.hasNext(); )
  //   {
  //     Entry entry = i.next();

  //     if(!(entry instanceof Item))
  //       continue;

  //     ioCommands.add(ListCommand.Type.NAME, ((Item)entry).getPlayerName());
  //     ioCommands.add(ListCommand.Type.NAME,
  //                    new Super(new Scriptsize("(" + ((Item)entry).getID())
  //                              + ")"));

  //     if(i.hasNext())
  //       ioCommands.add(ListCommand.Type.NAME, ", ");
  //   }
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

  //   ioCommands.addExtensionValue(m_includes, "include", "composite",
  //                                 inDM && inEditable);
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
    if(!m_includes.isDefined())
    {
      Combination <ValueList<ValueList<Name>>>base =
        new Combination<ValueList<ValueList<Name>>>(m_entry, "contains");

      List<Name> names = new ArrayList<Name>();
      for(ValueList<ValueList<Name>> andList : base.values())
        for(ValueList<Name> orList : andList)
        {
          String baseName = orList.get(s_random.nextInt(orList.size())).get();
          Item item =
            new Item(m_entry.getCampaign(), baseName);
          item.complete();
          item.save();

          names.add(m_includes.newElement().as(item.getName()));
        }

      if(!names.isEmpty())
      {
        m_includes = m_includes.as(names);
        m_entry.changed();
      }
    }

    // call the super constructor
    super.complete();
  }

  //........................................................................
  //-------------------------------- store ---------------------------------

  /**
   * Store this entry in the given storage container.
   *
   * @param       inStorage   the storage that stores this entry
   *
   * @return      true if stored, false if not
   *
   */
  // public boolean store(Storage<? extends AbstractEntry> inStorage)
  // {
  //   // add all the items to the campaign as well
  //   if(m_includes.isDefined())
  //     for(Value value : m_includes)
  //     {
  //       AbstractEntry entry = ((EntryValue)value).get();

  //       entry.store(inStorage);

  //       if(entry.isChanged())
  //         changed();
  //     }

  //   return true;
  // }

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
  // TODO: remove
//   public Modifier modifyValue(PropertyKey inType, AbstractEntry inEntry,
//                               Value inValue, boolean inDynamic)
//   {
//     if(inDynamic)
//     {
//       if(inType == PropertyKey.getKey("weight"))
//       {
//         // add the weight of all the includeds
//         Weight weight = new Weight();

//         for(Iterator<Value> i = m_includes.iterator(); i.hasNext(); )
//           weight.add(((Item)((EntryValue)i.next()).get()).getWeight());

//         return new Modifier(Modifier.Type.ADD, weight);
//       }

//       if(inType == PropertyKey.getKey("value"))
//       {
//         // add the weight of all the includeds
//         Money value = new Money();

//         for(Iterator<Value> i = m_includes.iterator(); i.hasNext(); )
//           value.add(((Item)((EntryValue)i.next()).get()).getValue());

//         return new Modifier(Modifier.Type.ADD, value);
//       }

//       if(inType == PropertyKey.getKey("hardness"))
//       {
//         // determine the maximal hardness
//         long max = 0;

//         for(Iterator<Value> i = m_includes.iterator(); i.hasNext(); )
//           max = Math.max(max,
//                          ((Number)((Item)((EntryValue)i.next()).get())
//                           .getValue("hardness")).get());

//         return new Modifier(Modifier.Type.MAX, max);
//       }

//       if(inType == PropertyKey.getKey("break DC"))
//       {
//         // determine the maximal break DC
//         long max = 0;

//         for(Iterator<Value> i = m_includes.iterator(); i.hasNext(); )
//         {
//           Item item = (Item)((EntryValue)i.next()).get();

//           if(item.getValue("break DC").isDefined())
//             max =
//               Math.max(max, ((Number)
//                              (item.getValue("break DC"))).get());
//         }

//         return new Modifier(Modifier.Type.MAX, max);
//       }

//       if(inType == PropertyKey.getKey("hp"))
//       {
//         // add all hitpoints
//         Number hp = new Number(0, 0, 100000);

//         for(Iterator<Value> i = m_includes.iterator(); i.hasNext(); )
//           hp.addTo(((Item)((EntryValue)i.next()).get())
//                    .getValue("hp"));

//         return new Modifier(Modifier.Type.ADD, hp);
//       }
//     }

//     return super.modifyValue(inType, inEntry, inValue, inDynamic);
//   }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
