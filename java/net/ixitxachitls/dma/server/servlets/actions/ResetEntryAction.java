/*******************************************************************************
 * Copyright (c) 2002-2016 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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
 ******************************************************************************/

package net.ixitxachitls.dma.server.servlets.actions;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.data.DMADatastore;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.dma.server.servlets.DMARequest;
import net.ixitxachitls.dma.values.enums.Group;

/**
 * Reset an entry to its default values.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class ResetEntryAction extends Action
{
  public ResetEntryAction()
  {
  }

  @Override
  public String execute(DMARequest inRequest)
  {
    Optional<BaseCharacter> user = inRequest.getUser();
    if(!user.isPresent() || !user.get().hasAccess(Group.DM))
    {
      return alert("'You don\\'t have the rights to reset this entry!");
    }

    Optional<String> keyParam = inRequest.getParam("key");
    if(!keyParam.isPresent() || keyParam.get().isEmpty())
      return alert("Cannot reset values, as no key is given");

    Optional<EntryKey> key = EntryKey.fromString(keyParam.get());

    if(!key.isPresent())
      return alert("Cannot create entry key for " + keyParam.get());

    if(!key.get().editableBy(user.get()))
    {
      return alert("You don't own that entry, thus you can't change it!");
    }

    Optional<AbstractEntry> entry = DMADataFactory.get().getEntry(key.get());
    if(!entry.isPresent())
      return alert("Cannot find entry " + key.get() + " to reset");

    entry.get().initialize(true);
    entry.get().save();

    return info("Entry reset, reloading.") + " util.link(); true;";
  }
}
