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
import java.util.Random;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.Nullable;

import net.ixitxachitls.dma.entries.FormattedValue;
import net.ixitxachitls.dma.entries.Item;
import net.ixitxachitls.dma.entries.ValueHandle;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.Contribution;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Link;
import net.ixitxachitls.util.logging.Log;

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

@ParametersAreNonnullByDefault
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
  public Composite(Item inEntry, String inName)
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

  //----------------------------- getIncludes ------------------------------

  /**
   * Get all the items included in this extension.
   *
   * @return      the list with all the entries
   *
   */
  public List<Item> getIncludes()
  {
    List<Item> list = new ArrayList<Item>();

    for(Name name : m_includes)
    {
      Item item = m_entry.getCampaign().getItem(name.get());
      if(item == null)
        continue;

      list.add(item);
    }

    return list;
  }

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
    for(Name name : m_includes)
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
  // public @Nullable ValueHandle computeValue(String inKey, boolean inDM)
  // {
  //   if("include".equals(inKey))
  //   {
  //     // we have to add the links here as we can't do it at construction time
  //     // as the campaign might not yet be available
  //     List<Object> commands = new ArrayList<Object>();

  //     for(Name name : m_includes)
  //     {
  //       if(!commands.isEmpty())
  //         commands.add(", ");

  //       Item item = m_entry.getCampaign().getItem(name.get());
  //       String url = m_entry.getCampaign().getPath() + "/item/" + name;
  //       if(item != null)
  //         commands.add(new Link(item.getNameCommand(inDM), url));
  //       else
  //         commands.add(new Link(name, url));
  //     }

  //     return new FormattedValue(new Command(commands), m_includes, "include");
  //   }

  //   return super.computeValue(inKey, inDM);
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
      Combined <ValueList<ValueList<Name>>>base = m_entry.collect("contains");

      List<Name> names = new ArrayList<Name>();
      for(ValueList<ValueList<Name>> andList : base.valuesOnly())
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
  public <V extends Value<V>> void collect(String inName,
                                           Combined<V> ioCombined)
  {
    if("value".equals(inName) || "weight".equals(inName))
    {
      V total = sum(inName, getIncludes());
      if(total != null)
        ioCombined.addValue(new Contribution<V>(total, this, "composite"));
    }

    super.collect(inName, ioCombined);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
