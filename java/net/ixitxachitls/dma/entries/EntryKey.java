/******************************************************************************
 * Copyright (c) 2002-2013 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.values.enums.Group;

/**
 * The key for an entry for storage.
 *
 * @file EntryKey.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class EntryKey
{
  /**
   * Create a key without a parent key.
   *
   * @param inID   the id of the entry
   * @param inType the type of the entry
   */
  public EntryKey(String inID, AbstractType<?> inType)
  {
    m_id = inID.toLowerCase();
    m_type = inType;
    m_parent = Optional.absent();
  }

  /**
   * Create a key with a parent key.
   *
   * @param inID     the id of the entry
   * @param inType   the type of the entry
   * @param inParent the parent key for the entry
   */
  public EntryKey(String inID, AbstractType<?> inType,
                  Optional<EntryKey> inParent)
  {
    this(inID, inType);

    m_parent = inParent;
  }

  /** The entry key. */
  private final AbstractType<?> m_type;

  /** The entry id. */
  private final String m_id;

  /** The parent key, if any. */
  private Optional<EntryKey> m_parent;

  /**
   * Get the id of the entry represented by this key.
   *
   * @return the entry id
   */
  public String getID()
  {
    return m_id;
  }

  /**
   * Get the type for the entry represented by the key.
   *
   * @return the type
   */
  public AbstractType<?> getType()
  {
    return m_type;
  }

  /**
   * Get the parent key for this one, if any.
   *
   * @return the parent key or null if there is no parent
   */
  public Optional<EntryKey> getParent()
  {
    return m_parent;
  }

  /**
   * Check whether the entry is editable by the given user.
   *
   * @param inUser the user checking for edits
   * @return true if editable, false if not
   */
  public boolean editableBy(BaseCharacter inUser)
  {
    // ADMINs can edit anything.
    if(inUser.hasAccess(Group.ADMIN))
      return true;

    // DMs can edit anything in their campaign.
    if(inUser.hasAccess(Group.DM))
    {
      Optional<Campaign> campaign = getCampaign();
      if(!campaign.isPresent())
        return true;

      return campaign.get().isDM(Optional.of(inUser));
    }

    return inUser.hasAccess(Group.PLAYER) && isOwner(inUser);
  }

  /**
   * Check whether the given user is the owner of the entry denoted by this key.
   *
   * @param inUser the user to check ownership for
   * @return true if the user is the owner, false if not
   */
  public boolean isOwner(BaseCharacter inUser)
  {
    Optional<Entry> entry = DMADataFactory.get().getEntry(this);
    if(!entry.isPresent())
      return false;

    return entry.get().isOwner(inUser);
  }

  /**
   * Get the campaign for the entry for this key, if any.
   *
   * @return the campaign
   */
  public Optional<Campaign> getCampaign()
  {
    if(getType().equals(Campaign.TYPE))
      return DMADataFactory.get().getEntry(this);

    if(getParent().isPresent())
      return getParent().get().getCampaign();

    return Optional.absent();
  }

  /**
   * Convert the key to a string for debugging.
   *
   * @return the converted string
   */
  @Override
  public String toString()
  {
    return (m_parent.isPresent() ? m_parent.get() : "") + "/" + m_type
      + "/" + m_id;
  }

  /**
   * Convert the given string to a key.
   *
   * @param   inText the text to convert
   *
   * @return  the converted key
   */
  public static Optional<EntryKey> fromString(String inText)
  {
    String []paths = inText.split("/");
    if(paths.length == 0)
      return Optional.absent();

    return fromString(paths, paths.length - 1);
  }

  /**
   * Extract the key from the given paths array and index.
   *
   * @param    inPaths the paths pieces
   * @param    inIndex the index to start from with computation (descending)
   *
   * @return   the key for the path part or null if not found
   *
   */
  private static Optional<EntryKey> fromString(String []inPaths, int inIndex)
  {
    if(inPaths.length <= inIndex || inIndex < 1)
      return Optional.absent();

    String id = inPaths[inIndex--].replace("%20", " ").replace("\\.\\w+$", "");
    Optional<? extends AbstractType<? extends AbstractEntry>> type =
      AbstractType.getTyped(inPaths[inIndex].replace("%20", " "));

    if(!type.isPresent())
      return Optional.absent();

    Optional<EntryKey> parent = fromString(inPaths, inIndex - 1);
    EntryKey key = new EntryKey(id, type.get(), parent);
    return Optional.of(key);
  }

  @Override
  public boolean equals(Object inOther)
  {
    if(this == inOther)
      return true;

    if(inOther == null)
      return false;

    if(!(inOther instanceof EntryKey))
      return false;

    EntryKey other = (EntryKey)inOther;
    return m_id.equals(other.m_id) && m_type.equals(other.m_type)
      && ((m_parent == null && other.m_parent == null)
          || (m_parent != null && m_parent.equals(other.m_parent)));
  }

  @Override
  public int hashCode()
  {
    return toString().hashCode();
  }
}
