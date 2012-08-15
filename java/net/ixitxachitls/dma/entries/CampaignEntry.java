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

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.extensions.Contents;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.output.commands.Link;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * An abstract base for all entries in campaigns.
 *
 *
 * @file          CampaignEntry.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param         <T> the type of base entry associated with this entry
 *
 */

//..........................................................................

//__________________________________________________________________________

public abstract class CampaignEntry<T extends BaseEntry> extends Entry<T>
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- CampaignEntry -----------------------------

  /**
   * The complete and 'default' constructor.
   *
   * @param       inName     the name of the entry
   * @param       inType     the type of the entry
   * @param       inBaseType the type of the base entry to this one
   *
   */
  protected CampaignEntry(@Nonnull String inName,
                          @Nonnull Type<? extends Entry> inType,
                          @Nonnull BaseType<? extends BaseEntry> inBaseType)
  {
    super(inName, inType, inBaseType);
  }

  //........................................................................
  //---------------------------- CampaignEntry -----------------------------

  /**
   * The default constructor.
   *
   * @param       inType     the type of the entry
   * @param       inBaseType the type of the base entry to this one
   *
   */
  protected CampaignEntry(@Nonnull Type<? extends Entry> inType,
                          @Nonnull BaseType<? extends BaseEntry> inBaseType)
  {
    super(inType, inBaseType);
  }

  //........................................................................
  //---------------------------- CampaignEntry -----------------------------

  /**
   * This constructs the item with random values from the given
   * base item.
   *
   * @param       inType     the type of the entry
   * @param       inBaseType the type of the base entry to this one
   * @param       inCampaign     the campaign this entry is in
   * @param       inBases        the base items to take values from
   *
   */
  public CampaignEntry(@Nonnull Type<? extends Entry> inType,
                       @Nonnull BaseType<? extends BaseEntry> inBaseType,
                       @Nonnull Campaign inCampaign,
                       @Nonnull String ... inBases)
  {
    super(inType, inBaseType, inBases);

    EntryKey key = inCampaign.getKey();
    m_campaign =
      m_campaign.as(((Name)m_campaign.get(0)).as(key.getParent().getID()),
                    ((Name)m_campaign.get(1)).as(key.getID()));
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  //----- campaign ---------------------------------------------------------

  /** The cached campaign object. */
  private @Nullable Campaign m_cachedCampaign;

  /** The state value. */
  @Key("campaign")
  protected Multiple m_campaign =
    new Multiple(new Multiple.Element(new Name(), false, null, " / "),
                 new Multiple.Element(new Name(), false))
    .withTemplate("campaign");

  //........................................................................
  //----- parent -----------------------------------------------------------

  /** The parent entry, if any. */
  @Key("parent")
  protected Name m_parent = new Name();

  //........................................................................

  /** The cached parent entry, if any. */
  private @Nullable CampaignEntry m_cachedParent;

  static
  {
    extractVariables(CampaignEntry.class);
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
  @SuppressWarnings("unchecked")
  @Override
  public @Nonnull EntryKey<? extends AbstractEntry> getKey()
  {
    Campaign campaign = getCampaign();

    if(campaign == null)
      throw new IllegalStateException("expected campaign '" + m_campaign
                                      + "' not found");

    return new EntryKey<Character>(getName(), (Type<Character>)getType(),
                                   campaign.getKey());
  }

  //........................................................................
  //------------------------------- getPath --------------------------------

  /**
   * Get the path to this entry.
   *
   * @return      the path to read this entry
   *
   */
  @Override
  public @Nonnull String getPath()
  {
    return getCampaign().getPath() + super.getPath();
  }

  //........................................................................
  //----------------------------- getCampaign ------------------------------

  /**
   * Get the campaign this character is in.
   *
   * @return      the Campaign for this character
   *
   */
  public @Nullable Campaign getCampaign()
  {
    if(m_cachedCampaign == null && m_campaign.isDefined())
      m_cachedCampaign = DMADataFactory.get().getEntry
        (new AbstractEntry.EntryKey<Campaign>
         (m_campaign.get(1).toString(), Campaign.TYPE,
          new AbstractEntry.EntryKey<BaseCampaign>(m_campaign.get(0).toString(),
                                                   BaseCampaign.TYPE)));

    return m_cachedCampaign;
  }

  //........................................................................
  //------------------------------ getParent -------------------------------

  /**
   * Get the parent entry this character is in.
   *
   * @return      the campaign entry that includes this one
   *
   */
  public @Nullable CampaignEntry getParent()
  {
    if(m_cachedParent == null && m_parent.isDefined())
      m_cachedParent = (CampaignEntry)DMADataFactory.get().getEntry
        (EntryKey.fromString(m_parent.get()));

    return m_cachedParent;
  }

  //........................................................................
  //----------------------------- getEditType ------------------------------

  /**
   * Get the type of the entry.
   *
   * @return      the requested name
   *
   */
  @Override
  public @Nonnull String getEditType()
  {
    return getCampaign().getEditType() + "/" + super.getEditType();
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

    return inUser.getName().equals(getCampaign().getDMName());
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
    if("campaign".equals(inKey))
      return new FormattedValue(new Link(m_campaign.get(1),
                                         "/campaign/" + m_campaign.get(0)
                                         + "/" + m_campaign.get(1)),
                                null, "campign");

    return super.computeValue(inKey, inDM);
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
  public @Nullable Object compute(@Nonnull String inKey)
  {
    if("campaign".equals(inKey))
      return getCampaign();

    if("navigation".equals(inKey))
    {
      List<CampaignEntry> list = new ArrayList<CampaignEntry>();

      list.add(this);

      for(CampaignEntry parent = getParent(); parent != null;
          parent = parent.getParent())
        list.add(parent);

      if(getCampaign() != null)
        list.add(getCampaign());

      Collections.reverse(list);
      return list;
    }

    return super.compute(inKey);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------ setParent -------------------------------

  /**
   * Set the parent to the given key.
   *
   * @param   inParent the key of the parent entry
   *
   * @return
   *
   */
  public void setParent(@Nullable EntryKey inParent)
  {
    if(inParent != null)
      m_parent = m_parent.as(inParent.toString());
    else
      m_parent = m_parent.create();

    changed();
    save();
  }

  //........................................................................

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
    Contents contents = (Contents)getExtension("contents");
    if(contents == null)
      return false;

    if(contents.add(inEntry))
    {
      inEntry.setParent(getKey());

      changed();
      save();

      return true;
    }

    return false;
  }

  //........................................................................

  //--------------------------------- save ---------------------------------

  /**
   * Save the entry if it has been changed.
   *
   * @return      true if saved, false if not
   *
   */
  @Override
  public boolean save()
  {
    if(m_name.get().startsWith(Entry.TEMPORARY))
      do
      {
        randomID();
      } while(DMADataFactory.get().getEntry(getKey()) != null);

    return super.save();
  }

  //........................................................................
  //------------------------------ updateKey -------------------------------

  /**
   * Update the any values that are related to the key with new data.
   *
   * @param       inKey the new key of the entry
   *
   */
  @Override
  public void updateKey(@Nonnull EntryKey<? extends AbstractEntry> inKey)
  {
    EntryKey parent = inKey.getParent();
    if(parent == null)
      return;

    EntryKey parentParent = parent.getParent();
    if(parentParent == null)
      return;

    m_campaign = m_campaign.as(new Name(parentParent.getID()),
                               new Name(parent.getID()));
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................
}
