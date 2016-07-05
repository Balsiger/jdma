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

import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.dma.server.servlets.DMARequest;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Group;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.Strings;

/**
 * The action for saving entry values.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class SaveEntryAction extends Action
{
  public SaveEntryAction()
  {
  }

  @Override
  public String execute(DMARequest inRequest)
  {
    Optional<BaseCharacter> user = inRequest.getUser();
    if(!user.isPresent() || !user.get().hasAccess(Group.PLAYER))
    {
      return alert("'You don\\'t have the rights to change the entry!");
    }

    Optional<String> keyParam = inRequest.getParam("_key_");
    if(!keyParam.isPresent() || keyParam.get().isEmpty())
      return alert("Cannot save values, as no key is given");

    Optional<EntryKey> key = EntryKey.fromString(keyParam.get());

    if(!key.isPresent())
      return alert("Cannot create entry key for " + keyParam.get());

    if(!key.get().editableBy(user.get()))
    {
      return alert("You don't own that entry, thus you can't change it!");
    }

    Optional<AbstractEntry> entry = DMADataFactory.get().getEntry(key.get());
    if(!entry.isPresent())
      if(inRequest.hasParam("_create_"))
      {
        entry = (Optional<AbstractEntry>)
            key.get().getType().create(key.get().getID());
        if(entry.isPresent())
          entry.get().updateKey(key.get());
      }
      else
        return alert("Cannot find entry for " + key.get());

    if(!entry.isPresent())
      return alert("could not create entry");

    Values values = new Values(inRequest.getParams());
    entry.get().set(values);
    List<String> errors = values.obtainMessages();

    if(!errors.isEmpty())
      return alert(Encodings.escapeJS(Strings.BR_JOINER.join(errors)));

    if(values.isChanged())
    {
      entry.get().changed();
      entry.get().save();
      return info("Entry " + Encodings.escapeJS(entry.get().getName())
          + " has been saved.");
    }

    return info("No changes needed saving");
  }
}
