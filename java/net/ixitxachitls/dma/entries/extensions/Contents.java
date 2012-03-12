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
import net.ixitxachitls.dma.values.Money;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Link;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the contents extension for all the entries.
 *
 * @file          Contents.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Contents extends Extension<Item>
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Contents ------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the item attached to
   * @param       inName the name of the extension
   *
   */
  public Contents(@Nonnull Item inEntry, @Nonnull String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //-------------------------------- Contents ------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the item attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   */
  // public Contents(Item inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The printer for printing the whole base item. */
  public static final Print s_pagePrint =
    new Print("%contents");

  //----- contents ---------------------------------------------------------

  /** The contents (then this is a container). */
  @Key("contents")
  protected ValueList<Name> m_contents = new ValueList<Name>(new Name());

  //........................................................................

  static
  {
    extractVariables(Item.class, Contents.class);
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
   * @param       inDeep true if get all sub entries, even nested one,
   *                     false for only the ones of here
   *
   * @return      the list with all the entries or null if none
   *
   * @undefined   never (may return null)
   *
   */
  // public List<Entry> getSubEntries(boolean inDeep)
  // {
  //   List<Entry> list = new ArrayList<Entry>();

  //   if(inDeep)
  //    for(Iterator<EntryValue<Item>> i = m_contents.iterator(); i.hasNext(); )
  //       list.addAll(i.next().get().getSubEntries(true));
  //   else
  //    for(Iterator<EntryValue<Item>> i = m_contents.iterator(); i.hasNext(); )
  //       list.add(i.next().get());

  //   return list;
  // }

  //........................................................................
  //---------------------------- getStorageName ----------------------------

  /**
   * Get the name of the file.
   *
   * @return      the name of the file (without 'unnecessary path' info)
   *
   */
  // public String getStorageName()
  // {
  //   return m_entry.getStorage().getStorageName() + ":" + getName();
  // }

  //........................................................................
  //----------------------------- getStorageID -----------------------------

  /**
   * Get the id of the storage or null if none (usually if not an value group).
   *
   * @return      the id of the storage or null
   *
   */
  // public String getStorageID()
  // {
  //   return m_entry.getID();
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

  //   for(AbstractEntry entry : getSubEntries(false))
  //     ioCommands.add(ListCommand.Type.CONTENTS,
  //                 entry.getListCommands(inDM).get(ListCommand.Type.GENERAL));
  // }

  //........................................................................

  //-------------------------------- accept --------------------------------

  /**
   * Check if the container accepts the given item. Currently, it only checks
   * if the item is already there.
   *
   * @param       inItem the item to add
   *
   * @return      true if the item can be added, false if not
   *
   * @undefined   never
   *
   */
  // public boolean accept(AbstractEntry inItem)
  // {
  //   return contains(inItem);
  // }

  //........................................................................
  //------------------------------- contains -------------------------------

  /**
   * Check if the given entry is contained in this container.
   *
   * @param       inItem the item to check for
   *
   * @return      true if the item is in the container, false if  not
   *
   */
  // public boolean contains(AbstractEntry inItem)
  // {
  //   if(inItem == null || !(inItem instanceof Item))
  //     return false;

  //   // check if already there
  //   for(Value value : m_contents)
  //     if(((EntryValue)value).get() == inItem)
  //       return false;

  //   return true;
  // }

  //........................................................................
  //-------------------------- adjustCombination ---------------------------

  /**
   * Adjust the value for the given name for any special properites.
   *
   * @param       inName        the name of the value to adjust
   * @param       ioCombination the combinstaion to adjust
   * @param       <V>           the real type of values combined
   *
   */
  @Override
  @SuppressWarnings("unchecked")
  public <V extends Value> void
            adjustCombination(@Nonnull String inName,
                              Combination<V> ioCombination)
  {
    if("value".equals(inName))
    {
      // add the value of all contained objects
      Money total = null;
      for(Name name : m_contents)
      {
        Item item = m_entry.getCampaign().getItem(name.get());
        if(item == null)
          continue;

        Money value = item.getValue();
        if(value == null)
          continue;

        if(total == null)
          total = value;
        else
          total = total.add(value);
      }

      if(total != null)
        ioCombination.add((V)total, this);
    }

    super.adjustCombination(inName, ioCombination);
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
    if("contents".equals(inKey))
    {
      // we have to add the links here as we can't do it at construction time
      // as the campaign might not yet be available
      List<Object> commands = new ArrayList<Object>();

      for(Name name : m_contents)
      {
        if(!commands.isEmpty())
          commands.add(", ");

        Item item = m_entry.getCampaign().getItem(name.get());
        String url = m_entry.getCampaign().getPath() + "/item/" + name;
        if(item != null)
          commands.add(new Link(item.getNameCommand(inDM), url));
        else
          commands.add(new Link(name, url));
      }

      return new FormattedValue(new Command(commands), m_contents, "contents");
    }

    return super.computeValue(inKey, inDM);
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

  //   Weight weight = new Weight();
  //   Money value = new Money();
  //   for(Entry entry : getSubEntries(false))
  //     if(entry instanceof Item)
  //     {
  //       weight.addTo(((Item)entry).getWeight());
  //       value.addTo(((Item)entry).getValue());
  //     }

  //   weight.simplify();
  //   value.simplify();

  //   ioCommands.appendValue("weight", new Command(new Object []
  //     {
  //       new Linebreak(),
  //       new Tiny(new Command(new Object []
  //         {
  //           "(contents ",
  //           weight,
  //           ")",
  //         })),
  //     }));

  //   ioCommands.appendValue("value", new Command(new Object []
  //     {
  //       new Linebreak(),
  //       new Tiny(new Command(new Object []
  //         {
  //           " (contents ",
  //           value,
  //           ")",
  //         })),
  //     }));

  //   List<Command> deepContents = new ArrayList<Command>();
  //   for(AbstractEntry entry : getSubEntries(false))
  //     deepContents.add(entry.getCommand(entry.printCommand(inDM, false),
  //                                       inDM ? Item.LIST_COMMAND_DM
  //                                       : Item.LIST_COMMAND, inDM));

  //   ioCommands.addValue("deep contents", new Command(new Object []
  //     {
  //       new Indent(new Command(deepContents.toArray())),
  //       new Par(),
  //     }),
  //                       false, false, false, "deep contents");

  //   ioCommands.addExtensionValue(m_contents, "contents", "contents",
  //                                 inEditable);

  //   List<BaseContainer> bases = getBases(BaseContainer.class);
  //   if(bases.size() > 0)
  //   {
  //     ioCommands.addExtensionValue(bases.get(0).m_capacity, "capacity",
  //                                   "capacity", false);
  //     ioCommands.addExtensionValue(bases.get(0).m_state, "state", "state",
  //                                   false);
  //   }
  //   else
  //   {
  //     Log.warning("could not find base for contents extension");
  //   }
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
  //   ioCommands.add(new Linebreak());

  //   for(EntryValue<Item> value : m_contents)
  //   {
  //     ioCommands.add(value.get().getSummaryCommand(inDM));
  //     ioCommands.add(new Linebreak());
  //   }
  // }

  //........................................................................
  //------------------------------- contains -------------------------------

  /**
   * Check if the container contains the item with the given name.
   *
   * @param       inItem the name of the item to look for
   *
   * @return      true if the item is contained, false if not
   *
   */
  public boolean contains(@Nonnull String inItem)
  {
    for(Name name : m_contents)
      if(inItem.equals(name.get()))
        return true;

    return false;
  }

  //........................................................................


  //........................................................................

  //----------------------------------------------------------- manipulators

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
  //   super.store(inStorage);

  //   for(EntryValue<Item> value : m_contents)
  //   {
  //     Item item = value.get();

  //     item.store(this);

  //     if(item.isChanged())
  //       changed();
  //   }

  //   return true;
  // }

  //........................................................................

  //--------------------------------- add ----------------------------------

  /**
   * Add the given item to the container.
   *
   * @param       inItem  the item to add
   * @param       inAfter the item to add the new one after (if null, add at
   *                      the end)
   *
   * @return      true if item added, false if adding was not possible
   *
   */
  // public boolean add(@MayBeNull Item inItem, @MayBeNull Item inAfter)
  // {
  //   if(inItem == null)
  //     return false;

  //   if(contains(inItem))
  //     remove(inItem);

  //   inItem.store(this);

  //   if(inAfter == null)
  //     return m_contents.add(0, new EntryValue<Item>(inItem));

  //   for(int i = 0; i < m_contents.size(); i++)
  //     if(m_contents.get(i).get() == inAfter)
  //       return m_contents.add(i + 1, new EntryValue<Item>(inItem));

  //   if(m_contents.add(new EntryValue<Item>(inItem)))
  //   {
  //     m_entry.changed();

  //     return m_entry.getStorage().write();
  //   }
  //   else
  //     return false;
  // }

  //........................................................................
  //-------------------------------- remove --------------------------------

  /**
   * Remove the given item from the container.
   *
   * @param       inItem the item to remove
   *
   * @return      true if item remove, false if removing was not possible
   *              (not there)
   *
   * @undefined   never
   *
   */
  // public boolean remove(Item inItem)
  // {
  //   if(inItem == null)
  //     return false;

  //   for(Iterator<EntryValue<Item>> i = m_contents.iterator(); i.hasNext(); )
  //   {
  //     EntryValue<Item> value = i.next();

  //     if(value.get() == inItem)
  //     {
  //       i.remove();

  //       return true;
  //     }
  //   }

  //   return false;
  // }

  //........................................................................
  //-------------------------------- write ---------------------------------

  /**
   * Write the contents of the storage to a persistent place.
   *
   * @return      true if written, false if not
   *
   */
  // public boolean write()
  // {
  //   return m_entry.getStorage().write();
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
  // TODO: fix these tests
//   @SuppressWarnings("unchecked")
//   public Modifier modifyValue(PropertyKey inType, AbstractEntry inEntry,
//                               Value inValue, boolean inDynamic)
//   {
//     if(inDynamic)
//     {
//       if(inType == PropertyKey.getKey("weight") && inValue != null)
//       {
//         // add the weight of all the contents
//         Weight weight = new Weight();

//         for(Iterator i = m_contents.mutableIterator(); i.hasNext(); )
//           weight.add((Weight)
//                    ((Item)((EntryValue)i.next()).get()).getValue("weight"));

//         // adjust the weight for the size of the intended user (this was
//       // already adjusted in the item itself but only to get the half weight
//       // (as is necessary for weapons). According to PHP p. 129 we need a 1/4
//         // though
//         if(inEntry != null && inEntry instanceof Item
//            && inValue instanceof Weight)
//         {
//           Pair<ValueGroup, Variable> pair =
//             inEntry.getVariable(Item.USER_SIZE.toString());

//           if(pair.first() != null && pair.second() != null)
//           {
//             BaseItem.Size size =
//               ((EnumSelection<BaseItem.Size>)
//                pair.second().get(pair.first())).getSelected();

//             if(size != BaseItem.Size.MEDIUM)
//             {
//               Weight adjust = (Weight)(inValue.clone());

//               if(size.isBigger(BaseItem.Size.MEDIUM))
//               {
//                 adjust.multiply
//                   ((int)Math.pow(2, size.difference(BaseItem.Size.MEDIUM)));
//                 weight.add(adjust);
//               }
//               else
//               {
//                 adjust.divide
//                   ((int)Math.pow(2, BaseItem.Size.MEDIUM.difference(size)));
//                 weight.subtract(adjust);
//               }
//             }
//           }
//         }

//         return new Modifier(Modifier.Type.ADD, weight);
//       }

//       if(inType == BaseContainer.CAPACITY)
//       {
//         if(inEntry instanceof Item)
//         {
//           Pair<ValueGroup, Variable> pair =
//             inEntry.getVariable(Item.USER_SIZE.toString());

//           if(pair.first() != null && pair.second() != null)
//           {
//             BaseItem.Size size =
//               ((EnumSelection<BaseItem.Size>)
//                pair.second().get(pair.first())).getSelected();

//             if(size.isBigger(BaseItem.Size.MEDIUM))
//               return new Modifier(Modifier.Type.MULTIPLY,
//                                   (int)Math.pow
//                                 (4, size.difference(BaseItem.Size.MEDIUM)));
//             else
//               return new Modifier(Modifier.Type.DIVIDE,
//                                   (int)Math.pow
//                                 (4, BaseItem.Size.MEDIUM.difference(size)));
//           }
//         }
//       }
//     }

//     return super.modifyValue(inType, inEntry, inValue, inDynamic);
//   }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

    //----- container ------------------------------------------------------

    /** Testing container. */
    public void testContainer()
    {
      // TODO: does not work anymore, as it is added to the campaign
//       // no contents in container
//       String text =
//         "item with contents Container = \n"
//         + "   hp 2;\n"
//         + "   contents .\n";

//       ParseReader reader =
//         new ParseReader(new java.io.StringReader(text), "container test");

//       Item item = (Item)Item.read(reader, new Campaign("Test", "tst"));

//       assertNotNull("item should have been read", item);
//       assertEquals("container text",
//                    "item with contents Container =\n"
//                    + "\n"
//                    + "  hp           2;\n"
//                    + "  user size    Medium-size.\n",
//                    item.toString());

      // now with some items in the container
//       String text =
//         "item with contents Container = \n"
//         + "   hp 2;\n"
//         + "   contents item guru., item guru2 = hp 3..\n";

//       ParseReader reader =
//         new ParseReader(new java.io.StringReader(text), "container test");

//       Item item = (Item)Item.read(reader, new BaseCampaign("Test"));

//       assertNotNull("item should have been read", item);
//       assertEquals("container text",
//                    "item with contents Container =\n"
//                    + "\n"
//                    + "  contents     item guru =\n"
//                    + "\n"
//                    + "  user size    Medium-size;\n"
//                    + "  id           tst-1.\n"
//                    + "\n"
//                    + ", item guru2 =\n"
//                    + "\n"
//                    + "  hp           3;\n"
//                    + "  user size    Medium-size;\n"
//                    + "  id           tst-2.\n"
//                    + "\n"
//                    + ";\n"
//                    + "  hp           2;\n"
//                    + "  user size    Medium-size.\n",
//                    item.toString());

//       m_logger.addExpectedPattern("WARNING: base.not-found:.*"
//                                   + "(base name 'Container').*");
//       m_logger.addExpectedPattern("WARNING: base.not-found:.*"
//                                   + "(base name 'guru').*");
//       m_logger.addExpectedPattern("WARNING: base.not-found:.*"
//                                   + "(base name 'guru2').*");

//       // adding stuff to the container
//       Item subItem = null;
//         for(Iterator<AbstractExtension> i = item.getExtensions();
//             i.hasNext(); )
//       {
//         AbstractExtension extension = i.next();

//         if(extension instanceof
//            net.ixitxachitls.dma.entries.extensions.Contents)
//         {
//           ParseReader subReader =
//           new ParseReader(new java.io.StringReader(s_text), "container add");

//           subItem = (Item)Item.read(subReader, new BaseCampaign("Test"));

//           assertTrue(((net.ixitxachitls.dma.entries.extensions.Contents)
//                       extension).accept(subItem));
//           assertTrue(((net.ixitxachitls.dma.entries.extensions.Contents)
//                       extension).add(subItem));
//           assertFalse(((net.ixitxachitls.dma.entries.extensions.Contents)
//                       extension).add(subItem));
//           assertFalse(((net.ixitxachitls.dma.entries.extensions.Contents)
//                       extension).accept(subItem));

//           break;
//         }
//       }

//       assertEquals("added",
//                    "item with contents Container =\n"
//                    + "\n"
//                    + "  contents     item guru =\n"
//                    + "\n"
//                    + "  user size    Medium-size;\n"
//                    + "  id           tst-1.\n"
//                    + "\n"
//                    + ", item guru2 =\n"
//                    + "\n"
//                    + "  hp           3;\n"
//                    + "  user size    Medium-size;\n"
//                    + "  id           tst-2.\n"
//                    + "\n"
//                    + ", #------ Winter Blanket "
//                    + "----------------------------------------\n"
//                    + "\n"
//                    + "item Winter Blanket =\n"
//                    + "\n"
//                    + "  hp           1;\n"
//                    + "  user size    Small.\n"
//                    + "\n"
//                    + "#..............."
//                    + "...............................................\n"
//                    + "\n"
//                    + ";\n"
//                    + "  hp           2;\n"
//                    + "  user size    Medium-size.\n", item.toString());

//       m_logger.addExpectedPattern("WARNING: base.not-found:.*"
//                                   + "(base name 'Winter Blanket').*");

//       // removing item again
//       for(Iterator<AbstractExtension> i = item.getExtensions();
//           i.hasNext(); )
//       {
//         AbstractExtension extension = i.next();

//         if(extension instanceof
//            net.ixitxachitls.dma.entries.extensions.Contents)
//         {
//           assertFalse(((net.ixitxachitls.dma.entries.extensions.Contents)
//                        extension).accept(subItem));
//           assertTrue(((net.ixitxachitls.dma.entries.extensions.Contents)
//                       extension).remove(subItem));
//           assertFalse(((net.ixitxachitls.dma.entries.extensions.Contents)
//                        extension).remove(subItem));
//           assertTrue(((net.ixitxachitls.dma.entries.extensions.Contents)
//                       extension).accept(subItem));
//         }
//       }

//       assertEquals("removed",
//                    "item with contents Container =\n"
//                    + "\n"
//                    + "  contents     item guru =\n"
//                    + "\n"
//                    + "  user size    Medium-size;\n"
//                    + "  id           tst-1.\n"
//                    + "\n"
//                    + ", item guru2 =\n"
//                    + "\n"
//                    + "  hp           3;\n"
//                    + "  user size    Medium-size;\n"
//                    + "  id           tst-2.\n"
//                    + "\n"
//                    + ";\n"
//                    + "  hp           2;\n"
//                    + "  user size    Medium-size.\n", item.toString());

//       // check if it accepts

    }

    //......................................................................
  // no tests, see BaseItem for tests

  //........................................................................
}
