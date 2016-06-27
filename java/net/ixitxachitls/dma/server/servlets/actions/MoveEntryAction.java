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

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.dma.server.servlets.DMARequest;
import net.ixitxachitls.util.Strings;

/**
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class MoveEntryAction extends Action
{
  public static class ParentUpdate {
    private final String m_id;
    private final String m_parent;

    public ParentUpdate(String inID, String inParent)
    {
      m_id = inID;
      m_parent = inParent;
    }

    public String getId()
    {
      return m_id;
    }

    public String getParent()
    {
      return m_parent;
    }
  }

  @Override
  public String execute(DMARequest inRequest)
  {
    List<String> errors = new ArrayList<>();
    List<ParentUpdate> updates = new ArrayList<>();
    for(String id : inRequest.getParams().get("names"))
    {
      String []parts = Strings.getPatterns(id, "(.*):(.*)");
      if (parts.length != 2)
        errors.add(id);
      else
      {
        Optional<EntryKey> parent = EntryKey.fromString(parts[0]);
        Optional<EntryKey> key = EntryKey.fromString(parts[1]);
        if(!parent.isPresent() || !key.isPresent()
            || !DMADataFactory.get().setParent(key.get(), parent.get()))
          errors.add("failed to move " + id);
      }
    }

    if(errors.isEmpty())
      return info("Entries moved!");

    return alert("Could not move items: "
        + Strings.COMMA_JOINER.join(errors));
  }
}
