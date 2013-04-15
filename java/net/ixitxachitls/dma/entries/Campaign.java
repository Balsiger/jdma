/******************************************************************************
 * Copyright (c) 2002-2011 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.data.DMAFile;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the storage container for campaign specific information.
 *
 * @file          Campaign.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class Campaign extends CampaignEntry<BaseCampaign>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Campaign -------------------------------

  /**
   * This is the internal default constructor.
   *
   */
  protected Campaign()
  {
    super(TYPE, BASE_TYPE);
  }

  //........................................................................
  //------------------------------- Campaign -------------------------------

  /**
   * This is the normal constructor.
   *
   * @param       inName  the name of the campaign
   *
   */
  public Campaign(String inName)
  {
    super(inName, TYPE, BASE_TYPE);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final Type<Campaign> TYPE =
    new Type<Campaign>(Campaign.class, BaseCampaign.TYPE);

  /** The type of the base entry to this entry. */
  public static final BaseType<BaseCampaign> BASE_TYPE = BaseCampaign.TYPE;

  /** The base types stored in the campaign. */
  private final SortedSet<AbstractType<?>> m_types =
    new TreeSet<AbstractType<?>>();

  /** The files read for this campaign. */
  private final Map<String, DMAFile> m_dmaFiles =
    new HashMap<String, DMAFile>();

  /** The number of lines read. */
  private int m_lines = 0;

  //----- dm ---------------------------------------------------------------

  /** The dm for this campaign. */
  @Key("dm")
  protected Name m_dm = new Name();

  //........................................................................

  static
  {
    extractVariables(Campaign.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- getKey --------------------------------

  /**
   * Get the key uniqueliy identifying this entry.
   *
   * @return   the key
   *
   */
  @Override
  @SuppressWarnings("unchecked")
  public EntryKey<Campaign> getKey()
  {
    List<String> names = getBaseNames();
    if(names.size() != 1)
      Log.warning("expected exactly one base for a campaign, but got " + names);

    return
      new EntryKey<Campaign>(getName(), Campaign.TYPE,
                             new EntryKey<BaseCampaign>(names.get(0),
                                                        BaseCampaign.TYPE));
  }

  //........................................................................
  //------------------------------ getDMName -------------------------------

  /**
   * Get the name of the DM of the campaign.
   *
   * @return      a String with the name
   *
   */
  public String getDMName()
  {
    return m_dm.get();
  }

  //........................................................................
  //------------------------------- getItem --------------------------------

  /**
   * Get the item denoted with the given name from the campaign.
   *
   * @param       inName the name of the item to get
   *
   * @return      the item found or null if not found
   *
   */
  @SuppressWarnings("unchecked")
  public @Nullable Item getItem(String inName)
  {
    return DMADataFactory.get().getEntry
      (new AbstractEntry.EntryKey<Item>(inName, Item.TYPE, getKey()));
  }

  //........................................................................
  //----------------------------- getCampaign ------------------------------

  /**
   * Get the campaign this character is in.
   *
   * @return      the Campaign for this character
   *
   */
  public Campaign getCampaign()
  {
    return this;
  }

  //........................................................................

  //----------------------------- getFilenames -----------------------------

  /**
   * Get the names of the files read into this campaign.
   *
   * @return      a list with all the file names
   *
   * @undefined   never
   *
   */
  public List<String> getFilenames()
  {
    ArrayList<String> files = new ArrayList<String>();

    for(DMAFile file : m_dmaFiles.values())
      files.add(file.getStorageName());

    return files;
  }

  //........................................................................

  //------------------------------- monsters -------------------------------

  /**
   * Get the free roaming monsters in the campaign.
   *
   * @return  the list of monster names
   *
   */
  public List<Monster> monsters()
  {
    List<Monster> monsters =
      DMADataFactory.get().getEntries(Monster.TYPE, getKey(), 0, 100);

    return monsters;
  }

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
  public @Nullable Object compute(String inKey)
  {
    if("characters".equals(inKey))
    {
      List<String> characters =
        DMADataFactory.get().getIDs(Character.TYPE, getKey());

      List<Multiple> list = new ArrayList<Multiple>();
      for(String character : characters)
        list.add(new Multiple
                 (new Multiple.Element(new Name(character), false),
                  new Multiple.Element(new Name(getPath() + "/"
                                                + Character.TYPE.getLink() + "/"
                                                + character), false)));

      return new ValueList<Multiple>(list);
    }

    if("items".equals(inKey))
    {
      // List<Character> characters =
      //   DMADataFactory.get().getEntries(Character.TYPE, getKey(), 0, 100);
      List<Multiple> list = new ArrayList<Multiple>();

      // Map<String, Item> owned = new HashMap<String, Item>();
      // for(Character character : characters)
      // {
      //   Map<String, Item> contained = character.containedItems(true);
      //   for(String key : contained.keySet())
      //     if(owned.containsKey(key))
      //       Log.warning("item " + key + " is possessed by two characters");

      //   owned.putAll(contained);
      // }

      // for(Monster monster : monsters())
      // {
      //   Map<String, Item> contained = monster.containedItems(true);
      //   for(String key : contained.keySet())
      //     if(owned.containsKey(key))
      //       Log.warning("item " + key
      //                   + " is possessed by two characters/monsters");

      //   owned.putAll(contained);
      // }

      // for(int pos = 0;; pos += 100)
      // {
      //   // TODO: this is expensive, we might want to do this only on demand?
      //   List<Item> items =
      //     DMADataFactory.get().getEntries(Item.TYPE, getKey(), pos, 100);

      //   for(Item item : items)
      //   {
      //     if(owned.containsKey(item.getName()))
      //       continue;

      //     list.add(new Multiple
      //              (new Multiple.Element(new Name(item.getPlayerName()),
      //                                    false),
      //               new Multiple.Element(new Name(item.getDMName()),
      //                                    false),
      //               new Multiple.Element(new Name(getPath() + "/"
      //                                             + Item.TYPE.getLink() + "/"
      //                                           + item.getName()), false)));
      //   }

      //   if(items.size() < 100 || list.size() > 5)
      //     break;
      // }

      if(list.isEmpty())
        return new ValueList<Multiple>
          (new Multiple(new Multiple.Element(new Name(), false),
                        new Multiple.Element(new Name(), false),
                        new Multiple.Element(new Name(), false)));
      else
        return new ValueList<Multiple>(list);
    }

    if("basename".equals(inKey))
      return new Name(m_base.get(0).get());

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
  // @Override
  // public @Nullable ValueHandle computeValue(String inKey,
  // boolean inDM)
  // {
  //   if("name".equals(inKey) && m_baseEntries != null
  //      && m_baseEntries.size() > 0)
  //   {
  //     BaseEntry base = m_baseEntries.get(0);

  //     if(base != null)
  //       return new FormattedValue
  //         (new Command(getName(),
  //                      " (",
  //                      new Link(new BaseCommand(base.getName()),
  //                               base.getPath()),
  //                      ")"), getName(), "name")
  //         .withEditable(true)
  //         .withEditType("name");
  //   }

  //   if("characters".equals(inKey))
  //   {
  //     List<String> characters =
  //       DMADataFactory.get().getIDs(Character.TYPE, getKey());

  //     List<Object> commands = new ArrayList<Object>();
  //     for(String character : characters)
  //     {
  //       if(!commands.isEmpty())
  //         commands.add(", ");

  //       commands.add(new Link(character, getPath() + "/"
  //                             + Character.TYPE.getLink() + "/" + character));
  //     }

  //     return new FormattedValue(new Command(commands), null, "characters")
  //       .withPlural("characters");
  //   }

  //   // if("items".equals(inKey))
  //   // {
  //   //   List<Character> characters =
  //   //     DMADataFactory.get().getEntries(Character.TYPE, getKey(), 0, 100);

  //   //   List<Object> commands = new ArrayList<Object>();

  //   //   for(int pos = 0;; pos += 100)
  //   //   {
  ////     // TODO: this is expensive, we might want to do this only on demand?
  //   //     List<Item> items =
  //   //       DMADataFactory.get().getEntries(Item.TYPE, getKey(), pos, 100);

  //   //     Map<String, Item> owned = new HashMap<String, Item>();
  //   //     for(Character character : characters)
  //   //     {
  //   //       Map<String, Item> contained = character.containedItems(true);
  //   //       for(String key : contained.keySet())
  //   //         if(owned.containsKey(key))
  ////           Log.warning("item " + key + " is possessed by two characters");

  //   //       owned.putAll(contained);
  //   //     }

  //   //     for(Item item : items)
  //   //     {
  //   //       if(owned.containsKey(item.getName()))
  //   //         continue;

  //   //       if(!commands.isEmpty())
  //   //         commands.add(", ");

  //   //       commands.add(new Link(item.getNameCommand(inDM), getPath() + "/"
  //                            + Item.TYPE.getLink() + "/" + item.getName()));
  //   //     }

  //   //     if(items.size() < 100)
  //   //       break;

  //   //     if(commands.size() > 100)
  //   //     {
  //   //       commands.add(", ...");
  //   //       break;
  //   //     }
  //   //   }

  //   //   return new FormattedValue(new Command(commands), null, "items")
  //   //     .withPlural("items");
  //   // }

  //   return super.computeValue(inKey, inDM);
  // }

  //........................................................................
  //------------------------------- getPath --------------------------------

  /**
   * Get the path to this entry.
   *
   * @return      the path to read this entry
   *
   */
  @Override
  public String getPath()
  {
    return "/" + BaseCampaign.TYPE.getLink() + "/" + m_base.get(0).get()
      + "/" + getName();
  }

  //........................................................................
  //----------------------------- getEditType ------------------------------

  /**
   * Get the path to this entry.
   *
   * @return      the path to read this entry
   *
   */
  @Override
  public String getEditType()
  {
    return "/" + BaseCampaign.TYPE + "/" + m_base.get(0).get()
      + "/" + Campaign.TYPE + "/" + getName();
  }

  //........................................................................
  //--------------------------------- isDM ---------------------------------

  /**
   * Check whether the given user is the DM for this entry. Everybody is a DM
   * for a base product.
   *
   * @param       inUser the user accessing
   *
   * @return      true for DM, false for not
   *
   */
  @Override
  public boolean isDM(@Nullable BaseCharacter inUser)
  {
    if(inUser == null)
      return false;

    return inUser.getName().equals(getDMName());
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  /*  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }
  */

  //........................................................................
}
