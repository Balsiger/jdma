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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.CampaignEntryProto;
import net.ixitxachitls.dma.proto.Entries.CampaignProto;
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

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * This is the internal default constructor.
   *
   */
  protected Campaign()
  {
    super(TYPE);
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
    super(inName, TYPE);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final Type<Campaign> TYPE =
    new Type<Campaign>(Campaign.class, BaseCampaign.TYPE);

  /** The type of the base entry to this entry. */
  public static final BaseType<BaseCampaign> BASE_TYPE = BaseCampaign.TYPE;

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
  public EntryKey<Campaign> getKey()
  {
    List<String> names = getBaseNames();
    if(names.size() != 1)
      Log.warning("expected exactly one base for a campaign, but got " + names);

    return
      new EntryKey<Campaign>(getName(), Campaign.TYPE,
                             new EntryKey<>(names.size() > 0
                               ? names.get(0)
                                 : "$undefined$",
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
  @Override
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
  public @Nullable Item getItem(String inName)
  {
    return DMADataFactory.get().getEntry
      (new EntryKey<Item>(inName, Item.TYPE, getKey()));
  }

  //........................................................................
  //----------------------------- getCampaign ------------------------------

  /**
   * Get the campaign this character is in.
   *
   * @return      the Campaign for this character
   *
   */
  @Override
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
//  public List<String> getFilenames()
//  {
//    ArrayList<String> files = new ArrayList<String>();
//
//    for(DMAFile file : m_dmaFiles.values())
//      files.add(file.getStorageName());
//
//    return files;
//  }

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
  @Override
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

      if (list.isEmpty())
        return null;

      return new ValueList<Multiple>(list);
    }

    if("encounters".equals(inKey))
    {
      List<Encounter> encounters =
        DMADataFactory.get().getEntries(Encounter.TYPE, getKey(), 0, 100);

      List<Encounter> list = new ArrayList<>();
      for(Encounter encounter : encounters)
        if (encounter.getCampaign().getName().equals(this.getName()))
          list.add(encounter);

      return list;
    }

    if("items".equals(inKey))
    {
       List<Character> characters =
         DMADataFactory.get().getEntries(Character.TYPE, getKey(), 0, 100);
      List<Multiple> list = new ArrayList<Multiple>();

       Map<String, Item> owned = new HashMap<String, Item>();
       for(Character character : characters)
       {
         Map<String, Item> contained = character.containedItems(true);
         for(String key : contained.keySet())
           if(owned.containsKey(key))
             Log.warning("item " + key + " is possessed by two characters");

         owned.putAll(contained);
       }

       for(Monster monster : monsters())
       {
         Map<String, Item> contained = monster.containedItems(true);
         for(String key : contained.keySet())
           if(owned.containsKey(key))
             Log.warning("item " + key
                         + " is possessed by two characters/monsters");

         owned.putAll(contained);
       }

       for(int pos = 0;; pos += 100)
       {
         // TODO: this is expensive, we might want to do this only on demand?
         List<Item> items =
           DMADataFactory.get().getEntries(Item.TYPE, getKey(), pos, 100);

         for(Item item : items)
         {
           if(owned.containsKey(item.getName()))
             continue;

           list.add(new Multiple
                    (new Multiple.Element(new Name(item.getPlayerName()),
                                          false),
                     new Multiple.Element(new Name(item.getDMName()),
                                          false),
                     new Multiple.Element(new Name(getPath() + "/"
                                                   + Item.TYPE.getLink() + "/"
                                                 + item.getName()), false)));
         }

         if(items.size() < 100 || list.size() > 5)
           break;
       }

      if(list.isEmpty())
        return new ValueList<Multiple>
          (new Multiple(new Multiple.Element(new Name(), false),
                        new Multiple.Element(new Name(), false),
                        new Multiple.Element(new Name(), false)));
      else
        return new ValueList<Multiple>(list);
    }

    if("basename".equals(inKey))
      return new Name(m_base.get(0));

    return super.compute(inKey);
  }

  //........................................................................
  //------------------------------- getPath --------------------------------

  /**
   * Get the path to this entry.
   *
   * @return      the path to read this entry
   */
  @Override
  public String getPath()
  {
    if(!m_base.isEmpty())
      return "/" + BaseCampaign.TYPE.getLink() + "/" + m_base.get(0)
        + "/" + getName();

    return "/" + BaseCampaign.TYPE.getLink() + "/$undefined$/" + getName();
  }

  //........................................................................
  //----------------------------- getEditType ------------------------------

  /**
   * Get the path to this entry.
   *
   * @return      the path to read this entry
   */
  @Override
  public String getEditType()
  {
    return "/" + BaseCampaign.TYPE + "/" + m_base.get(0)
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

  @Override
  public Message toProto()
  {
    CampaignProto.Builder builder = CampaignProto.newBuilder();

    builder.setBase((CampaignEntryProto)super.toProto());

    if(m_dm.isDefined())
      builder.setDm(m_dm.get());

    CampaignProto proto = builder.build();
    return proto;
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof CampaignProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    CampaignProto proto = (CampaignProto)inProto;

    super.fromProto(proto.getBase());

    if(proto.hasDm())
      m_dm = m_dm.as(proto.getDm());
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(CampaignProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  /*  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }
  */

  //........................................................................
}
