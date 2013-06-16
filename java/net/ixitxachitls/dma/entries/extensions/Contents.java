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

import javax.annotation.ParametersAreNonnullByDefault;

import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.CampaignEntry;
import net.ixitxachitls.dma.entries.Item;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.Money;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.Weight;
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

@ParametersAreNonnullByDefault
public class Contents extends Extension<Item>
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Contents ------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * Default constructor.
   *
   * @param       inEntry the item attached to
   * @param       inName the name of the extension
   *
   */
  public Contents(Item inEntry, String inName)
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
  //---------------------------- containedItems ----------------------------

  /**
   * Get all the items contained in this contents.
   *
   * @param       inDeep true for returning all item, including nested ones,
   *                     false for only the top level items
   * @return      a list with all the items
   *
   */
  public Map<String, Item> containedItems(boolean inDeep)
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
  public boolean add(CampaignEntry<? extends BaseEntry> inEntry)
  {
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

  //------------------------------- collect --------------------------------

  /**
   * Adjust the value for the given name for any special properites.
   *
   * @param       inName      the name of the value to adjust
   * @param       ioCombined  the combined value collect into
   * @param       <V>         the real type of values combined
   *
   */
  @Override
  @SuppressWarnings("unchecked")
  public <V extends Value<V>> void collect(String inName,
                                           Combined<V> ioCombined)
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
        ioCombined.addValue((V)total, this, "contents");
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

        Weight value = item.getTotalWeight();
        if(value == null)
          continue;

        if(total == null)
          total = value;
        else
          total = total.add(value);
      }

      if(total != null)
        ioCombined.addValue((V)total, this, "contents");
    }

    super.collect(inName, ioCombined);
 }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends ValueGroup.Test
  {
    //----- empty container ------------------------------------------------

    /** Testing container. */
    @org.junit.Test
    public void emptyContainer()
    {
      // no contents in container
      String text =
        "item with contents Container = \n"
        + "   hp 2;\n"
        + "   contents .\n";

      net.ixitxachitls.input.ParseReader reader =
        new net.ixitxachitls.input.ParseReader(new java.io.StringReader(text),
                                               "container test");

      Item item = (Item)Item.read(reader);

      assertNotNull("item should have been read", item);
      assertEquals("container text",
                   "#----- Container\n"
                   + "\n"
                   + "item with contents Container =\n"
                   + "\n"
                   + "  hp           2;\n"
                   + "  name         Container.\n"
                   + "\n"
                   + "#.....\n",
                   item.toString());
    }

    //......................................................................
    //----- contents -------------------------------------------------------

    /** The contents Test. */
    @org.junit.Test
    public void contents()
    {
      // now with some items in the container
      String text =
        "item with contents Container = contents item1, item2.";

      net.ixitxachitls.input.ParseReader reader =
        new net.ixitxachitls.input.ParseReader(new java.io.StringReader(text),
                                               "container test");
      Item item = (Item)Item.read(reader);

      assertNotNull("item should have been read", item);
      assertEquals("container text",
                   "#----- Container\n"
                   + "\n"
                   + "item with contents Container =\n"
                   + "\n"
                   + "  name         Container;\n"
                   + "  contents     item1,\n"
                   + "               item2.\n"
                   + "\n"
                   + "#.....\n",
                   item.toString());
    }

    //......................................................................
    //----- value ----------------------------------------------------------

    /** The value Test. */
    @org.junit.Test
    public void value()
    {
      String text = "campaign campaign = base FR.\n"
        + "item item1 = value 100 gp; campaign FR / campaign.\n"
        + "item item2 = value 250 gp; campaign FR / campaign.\n"
        + "item with contents container = value 25 gp; campaign FR / campaign; "
        + "contents item1, item2.\n";

      net.ixitxachitls.input.ParseReader reader =
        new net.ixitxachitls.input.ParseReader(new java.io.StringReader(text),
                                               "container test");

      addEntry(net.ixitxachitls.dma.entries.Campaign.read(reader));
      addEntry(Item.read(reader));
      addEntry(Item.read(reader));

      Item container = (Item)Item.read(reader);

      assertEquals("value", 375.0, container.getGoldValue(), 0.5);
    }

    //......................................................................
    //----- weight ---------------------------------------------------------

    /** The value Test. */
    @org.junit.Test
    public void weight()
    {
      String text = "campaign campaign = base FR.\n"
        + "base item base_item1 = weight 5 lb.\n"
        + "item item1 = base base_item1; campaign FR / campaign.\n"
        + "base item base_item2 = weight 10 lb.\n"
        + "item item2 = base base_item2; campaign FR / campaign.\n"
        + "base item base_container = weight 1 lb.\n"
        + "item with contents container = base base_container; "
        + "campaign FR / campaign; contents item1, item2.\n";

      net.ixitxachitls.input.ParseReader reader =
        new net.ixitxachitls.input.ParseReader(new java.io.StringReader(text),
                                               "container test");

      addEntry(net.ixitxachitls.dma.entries.Campaign.read(reader));
      addEntry(net.ixitxachitls.dma.entries.BaseItem.read(reader));
      addEntry(Item.read(reader));
      addEntry(net.ixitxachitls.dma.entries.BaseItem.read(reader));
      addEntry(Item.read(reader));
      addEntry(net.ixitxachitls.dma.entries.BaseCampaign.read(reader));

      Item container = (Item)Item.read(reader);

      assertEquals("weight", 16.0,
                   container.getTotalWeight().getAsPounds().getValue(), 0.5);
    }

    //......................................................................
  }

  //........................................................................
}
