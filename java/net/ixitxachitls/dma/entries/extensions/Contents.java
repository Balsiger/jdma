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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.dma.entries.CampaignEntry;
import net.ixitxachitls.dma.entries.FormattedValue;
import net.ixitxachitls.dma.entries.Item;
import net.ixitxachitls.dma.entries.ValueHandle;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.Money;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.Weight;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Link;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.logging.Log;

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

  //------------------------------- compute --------------------------------

  /**
   * Compute a value for a given key, taking base entries into account if
   * available.
   *
   * @param    inKey the key of the value to compute
   *
   * @return   the compute value
   *
   */
  @Override
  public @Nullable Object compute(@Nonnull String inKey)
  {
    if("contents".equals(inKey))
    {
      List<Multiple> list = new ArrayList<Multiple>();

      for(Name name : m_contents)
      {
        Item item = m_entry.getCampaign().getItem(name.get());

        // Note: this is for legacy items that don't yet have a parent.
        if (item.getParent() != m_entry)
          item.setParent(m_entry.getKey());

        list.add(new Multiple
                 (new Multiple.Element(new Name(item.getPlayerName()), false),
                  new Multiple.Element(new Name(item.getDMName()), false),
                  new Multiple.Element(new Name(item.getPath()), false)));
      }

      if(list.isEmpty())
        return new ValueList<Multiple>
          (new Multiple(new Multiple.Element(new Name(), false),
                        new Multiple.Element(new Name(), false),
                        new Multiple.Element(new Name(), false)));
      else
        return new ValueList<Multiple>(list);
    }

    return super.compute(inKey);
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

      if(inDM)
      {
        commands.add(" | ");
        commands.add(new Link
                     ("Add", "javascript:item.create("
                      + Encodings.toJSString(m_entry.getCampaign().getPath())
                      + ", "
                      + Encodings.toJSString
                      (m_entry.getCampaign().getEditType() + "/"
                       + Item.TYPE.getLink()
                       + "/" + m_entry.getName()) + ");\""));
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
  //---------------------------- containedItems ----------------------------

  /**
   * Get all the items contained in this contents.
   *
   * @param       inDeep true for returning all item, including nested ones,
   *                     false for only the top level items
   * @return      a list with all the items
   *
   */
  public @Nonnull Map<String, Item> containedItems(boolean inDeep)
  {
    Map<String, Item> items = new HashMap<String, Item>();
    for(Name name : m_contents)
    {
      Item item = m_entry.getCampaign().getItem(name.get());
      items.put(name.get(), item);

      if(item == null || !inDeep)
        continue;

      Map<String, Item> contained = item.containedItems(true);
      for(String key : contained.keySet())
        if(items.containsKey(key))
          Log.warning("depected item loop for " + key);

      items.putAll(item.containedItems(true));
    }

    return items;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- add ----------------------------------

  /**
   * Add the given entry to the campaign entry.
   *
   * @param       inEntry the entry to add
   *
   * @return      true if added, false if not
   *
   */
  public boolean add(@Nonnull CampaignEntry inEntry)
  {
    new Exception().printStackTrace(System.out);
    String name = inEntry.getName();
    List<Name> names = new ArrayList<Name>();
    for(Name item : m_contents)
      if(name.equals(item.get()))
        return true;
      else
        names.add(item);

    names.add(m_contents.newElement().as(name));
    m_contents = m_contents.as(names);

    return true;
  }

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

    if("weight".equals(inName))
    {
      // add the value of all contained objects
      Weight total = null;
      for(Name name : m_contents)
      {
        Item item = m_entry.getCampaign().getItem(name.get());
        if(item == null)
          continue;

        Weight value = item.getWeight();
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
