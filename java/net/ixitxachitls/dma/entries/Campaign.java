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

package net.ixitxachitls.dma.entries;

import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.CampaignEntryProto;
import net.ixitxachitls.dma.proto.Entries.CampaignProto;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the storage container for campaign specific information.
 *
 * @file          Campaign.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@ParametersAreNonnullByDefault
public class Campaign extends CampaignEntry
{
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

  /**
   * This is the normal constructor.
   *
   * @param       inName  the name of the campaign
   */
  public Campaign(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final Type<Campaign> TYPE =
    new Type.Builder<>(Campaign.class, BaseCampaign.TYPE).build();

  /** The type of the base entry to this entry. */
  public static final BaseType<BaseCampaign> BASE_TYPE = BaseCampaign.TYPE;

  /** The dm for this campaign. */
  protected Optional<String> m_dm = Optional.absent();

  public EntryKey getKey()
  {
    List<String> names = getBaseNames();
    if(names.size() != 1)
      Log.warning("expected exactly one base for a campaign, but got " + names);

    return new EntryKey(getName(), Campaign.TYPE,
                        Optional.of(new EntryKey(names.size() > 0
                                                 ? names.get(0)
                                                   : "$undefined$",
                                                   BaseCampaign.TYPE)));
  }

  /**
   * Get the dm name.
   *
   * @return the dm name
   */
  public Optional<String> getDM()
  {
    return m_dm;
  }

  @Override
  public String getDMName()
  {
    if(m_dm.isPresent())
      return m_dm.get();

    return "(none)";
  }

  /**
   * Get the item denoted with the given name from the campaign.
   *
   * @param       inName the name of the item to get
   *
   * @return      the item found or null if not found
   */
  public Optional<Item> getItem(String inName)
  {
    return DMADataFactory.get().getEntry(new EntryKey(inName, Item.TYPE,
                                                      Optional.of(getKey())));
  }

  @Override
  public Optional<Campaign> getCampaign()
  {
    return Optional.of(this);
  }

  /**
   * Get the free roaming monsters in the campaign.
   *
   * @return  the list of monster names
   */
  public List<Monster> monsters()
  {
    List<Monster> monsters =
      DMADataFactory.get().getEntries(Monster.TYPE, getKey(), 0, 100);

    return monsters;
  }

  /**
   * Compute a value for a given key, taking base entries into account if
   * available.
   *
   * @ param    inKey the key of the value to compute
   *
   * @return   the compute value
   *
   */
  /*
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
*/

  @Override
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

    if(m_dm.isPresent())
      values.put(Index.Path.DM, m_dm.get());

    return values;
  }

  @Override
  public String getPath()
  {
    if(!m_base.isEmpty())
      return "/" + BaseCampaign.TYPE.getLink() + "/" + m_base.get(0)
        + "/" + getName();

    return "/" + BaseCampaign.TYPE.getLink() + "/$undefined$/" + getName();
  }

  @Override
  public String getFilePath()
  {
    if(!m_base.isEmpty())
      return Campaign.TYPE.getName() + "/" + m_base.get(0).toLowerCase()
        + "/" + getName().toLowerCase() + "/";

    return Campaign.TYPE.getName() + "/$undefined$/" + getName().toLowerCase()
      + "/";
  }

  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().getName().equals(getDMName());
  }

  @Override
  public void set(Values inValues)
  {
    super.set(inValues);

    m_dm = inValues.use("DM", m_dm);
  }

  @Override
  public Message toProto()
  {
    CampaignProto.Builder builder = CampaignProto.newBuilder();

    builder.setBase((CampaignEntryProto)super.toProto());

    if(m_dm.isPresent())
      builder.setDm(m_dm.get());

    CampaignProto proto = builder.build();
    return proto;
  }

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
      m_dm = Optional.of(proto.getDm());
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
}
