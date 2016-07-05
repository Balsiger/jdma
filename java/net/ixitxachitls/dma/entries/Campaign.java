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

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.CampaignEntryProto;
import net.ixitxachitls.dma.proto.Entries.CampaignProto;
import net.ixitxachitls.dma.values.Calendar;
import net.ixitxachitls.dma.values.CampaignDate;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Gender;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the storage container for campaign specific information.
 *
 * @file          Campaign.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */
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

  /** The date of the campaign. */
  protected Optional<CampaignDate> m_date = Optional.absent();

  /**
   * Get the key for the campaign.
   *
   * @return the key
   */
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

  public Optional<CampaignDate> getDate()
  {
    return m_date;
  }

  @Override
  public String getDMName()
  {
    if(m_dm.isPresent())
      return m_dm.get();

    return "(none)";
  }

  public Calendar getCalendar()
  {
    for(BaseEntry base : getBaseEntries())
      return ((BaseCampaign)base).getCalendar();

    return new Calendar();
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
      DMADataFactory.get().getEntries(Monster.TYPE, Optional.of(getKey()),
                                      0, 100);

    return monsters;
  }

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

  public Optional<AbstractEntry> getCurrentEntry()
  {
    return DMADataFactory.get().getCurrentCampaignEntry(getKey().toString());
  }

  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().getName().equals(getDMName());
  }

  @Override
  public void setValues(Values inValues)
  {
    super.setValues(inValues);

    m_dm = inValues.use("DM", m_dm);

    if(!m_date.isPresent())
      m_date = Optional.of(new CampaignDate(getCalendar()));

    m_date.get().set(inValues);
  }

  @Override
  public Message toProto()
  {
    CampaignProto.Builder builder = CampaignProto.newBuilder();

    builder.setBase((CampaignEntryProto)super.toProto());

    if(m_dm.isPresent())
      builder.setDm(m_dm.get());

    if(m_date.isPresent())
      builder.setDate(m_date.get().toProto());

    CampaignProto proto = builder.build();
    return proto;
  }

  /**
   * Set all the values from the given proto to this campaign.
   *
   * @param inProto the proto to read values from
   */
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

    if(proto.hasDate())
      m_date = Optional.of(CampaignDate.fromProto(
          getCalendar(), proto.getDate()));
  }

  public Optional<CampaignDate> manipulateTime(int inMinutes, int inHours,
                                               int inDays, int inMonths,
                                               int inYears)
  {
    if(m_date.isPresent())
    {
      m_date.get().manipulate(inMinutes, inHours, inDays, inMonths, inYears);
      changed();
    }

    save();
    return m_date;
  }

  public String generateRandomName(String inRace, Optional<String> inRegion,
                                   Gender inGender)
  {
    return ((BaseCampaign)getBaseEntry()).generateRandomName(inRace, inRegion,
                                                             inGender);
  }

  @Override
  protected Message defaultProto()
  {
    return CampaignProto.getDefaultInstance();
  }
}
