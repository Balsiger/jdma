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

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.CampaignEntryProto;
import net.ixitxachitls.dma.proto.Entries.EntryProto;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

/**
 * An abstract base for all entries in campaigns.
 *
 * @file          CampaignEntry.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@ParametersAreNonnullByDefault
public abstract class CampaignEntry extends Entry
{
  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * The complete and 'default' constructor.
   *
   * @param       inName     the name of the entry
   * @param       inType     the type of the entry
   */
  protected CampaignEntry(String inName, Type<?> inType)
  {
    super(inName, inType);
  }

  /**
   * The default constructor.
   *
   * @param       inType     the type of the entry
   */
  protected CampaignEntry(Type<?> inType)
  {
    super(inType);
  }

  /**
   * This constructs the item with random values from the given
   * base item.
   *
   * @param       inType     the type of the entry
   * @param       inCampaign     the campaign this entry is in
   *
   */
  protected CampaignEntry(Type<?> inType, Campaign inCampaign)
  {
    super(inType);

    EntryKey key = inCampaign.getKey();
    m_cachedCampaign = Optional.of(inCampaign);
    m_baseCampaignName = Optional.of(key.getParent().get().getID());
    m_campaignName = Optional.of(key.getID());
  }

  /** The cached campaign object. */
  private Optional<Campaign> m_cachedCampaign = Optional.absent();

  /** The base campaign name. */
  protected Optional<String> m_baseCampaignName = Optional.absent();

  /** The campaign name. */
  protected Optional<String> m_campaignName = Optional.absent();

  /** The parent entry, if any. */
  protected Optional<String> m_parentName = Optional.absent();

  /** The cached parent entry, if any. */
  private Optional<CampaignEntry> m_cachedParent = Optional.absent();

  /**
   * Get the key of the campaign entry.
   *
   * @return the key
   */
  public EntryKey getKey()
  {
    Optional<Campaign> campaign = getCampaign();

    if(!campaign.isPresent())
      throw new IllegalStateException("expected campaign '"
                                      + m_campaignName.get()
                                      + "' not found");

    return new EntryKey(getName(), getType(),
                        Optional.of(campaign.get().getKey()));
  }

  @Override
  public String getPath()
  {
    if(!getCampaign().isPresent())
      return super.getPath();

    return getCampaign().get().getPath() + super.getPath();
  }

  @Override
  public String getFilePath()
  {
    if(!getCampaign().isPresent())
      return super.getFilePath();

    return getCampaign().get().getFilePath() + super.getFilePath();
  }

  /**
   * Get the campaign this character is in.
   *
   * @return      the Campaign for this character
   */
  public Optional<Campaign> getCampaign()
  {
    if(!m_cachedCampaign.isPresent() && m_baseCampaignName.isPresent()
      && m_campaignName.isPresent())
      m_cachedCampaign = DMADataFactory.get().getEntry
        (new EntryKey(m_campaignName.get(), Campaign.TYPE,
         Optional.of(new EntryKey(m_baseCampaignName.get(),
                                  BaseCampaign.TYPE))));

    return m_cachedCampaign;
  }

  /**
   * Get the id of the parent entry, if any.
   *
   * @return the id of the parent entry
   */
  public Optional<String> getParent()
  {
    return m_parentName;
  }

  /**
   * Get the parent entry this entry is in.
   *
   * @return      the campaign entry that includes this one
   */
  public Optional<CampaignEntry> getParentEntry()
  {
    if(!m_cachedParent.isPresent() && m_parentName.isPresent()
      && getCampaign().isPresent())
    {
      Optional<EntryKey> parentKey =
        EntryKey.fromString(getCampaign().get().getKey() + "/"
                            + m_parentName.get());
      if(parentKey.isPresent())
        m_cachedParent = DMADataFactory.get().getEntry(parentKey.get());
    }

    return m_cachedParent;
  }

  /**
   * Get the name of the entry as given to the player.
   *
   * @return      the requested name
   */
  public String getPlayerName()
  {
    return getName();
  }

  /**
   * Get the name of the item for DMs.
   *
   * @return the dm specific name
   */
  public String getDMName()
  {
    List<String> parts = new ArrayList<String>();

    for(BaseEntry base : getBaseEntries())
      parts.add(base.getName());

    if(parts.isEmpty())
      parts.add(getName());

    return Strings.SPACE_JOINER.join(parts);
  }

  /**
   * Check whether the given user is the DM for this entry. Everybody is a DM
   * for a base product.
   *
   * @param       inUser the user accessing
   *
   * @return      true for DM, false for not
   */
  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent() || !getCampaign().isPresent())
      return false;

    return inUser.get().getName().equals(getCampaign().get().getDMName());
  }

  @Override
  public List<Link> getNavigation()
  {
    ArrayList<Link> navigations = new ArrayList<>();
    navigations.add(new Link(getName(), getPath()));

    for(Optional<CampaignEntry> parent = getParentEntry(); parent.isPresent();
      parent = parent.get().getParentEntry())
      navigations.add(0,
                      new Link(parent.get().getName(), parent.get().getPath()));

    return navigations;
  }

  /**
   * Set the parent to the given key.
   *
   * @param   inParent the key of the parent entry
   */
  public void setParent(Optional<EntryKey> inParent)
  {
    if(inParent.isPresent())
      m_parentName = Optional.of(inParent.toString());
    else
      m_parentName = Optional.absent();

    changed();
    save();
  }

  /**
   * Add the given entry to the campaign entry.
   *
   * @param       inEntry the entry to add
   *
   * @return      true if added, false if not
   */
  public boolean add(CampaignEntry inEntry)
  {
    inEntry.setParent(Optional.of(getKey()));
    changed();
    save();

    return true;
  }

  @Override
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

    if(m_parentName.isPresent())
      values.put(Index.Path.PARENT, m_parentName.get().toLowerCase());

    return values;
  }

    /**
   * Save the entry if it has been changed.
   *
   * @return      true if saved, false if not
   */
  @Override
  public boolean save()
  {
    if(m_name.startsWith(Entry.TEMPORARY))
      do
      {
        randomID();
      } while(DMADataFactory.get().getEntry(getKey()).isPresent());

    return super.save();
  }

  /**
   * Update any values that are related to the key with new data.
   *
   * @param       inKey the new key of the entry
   */
  @Override
  public void updateKey(EntryKey inKey)
  {
    Optional<EntryKey> parent = inKey.getParent();
    if(!parent.isPresent())
      return;

    Optional<EntryKey> parentParent = parent.get().getParent();
    if(!parentParent.isPresent())
      return;

    m_campaignName = Optional.of(parent.get().getID());
    m_baseCampaignName = Optional.of(parentParent.get().getID());
    m_cachedCampaign = Optional.absent();
  }

  @Override
  public void setValues(Values inValues)
  {
    super.setValues(inValues);

    m_parentName = inValues.use("parent", m_parentName);
  }

  @Override
  public Message toProto()
  {
    CampaignEntryProto.Builder builder = CampaignEntryProto.newBuilder();

    builder.setBase((EntryProto)super.toProto());

    if(m_campaignName.isPresent())
      builder.setCampaign(m_campaignName.get());

    if(m_baseCampaignName.isPresent())
      builder.setBaseCampaign(m_baseCampaignName.get());

    if(m_parentName.isPresent())
      builder.setParent(m_parentName.get());

    return builder.build();
  }

  /**
   * Set all the value from the given proto to this entry.
   *
   * @param inProto the proto with the values
   */
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof CampaignEntryProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    CampaignEntryProto proto = (CampaignEntryProto)inProto;

    if(proto.hasBaseCampaign())
      m_baseCampaignName = Optional.of(proto.getBaseCampaign());

    if(proto.hasCampaign())
      m_campaignName = Optional.of(proto.getCampaign());

    if(proto.hasParent())
      m_parentName = Optional.of(proto.getParent());

    super.fromProto(proto.getBase());
  }

  @Override
  protected Message defaultProto()
  {
    return CampaignEntryProto.getDefaultInstance();
  }
}
