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
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.server.servlets.DMARequest;

/**
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class CurrentCampaignAction extends Action
{
  public CurrentCampaignAction()
  {
    super("campaign", "entry");
  }

  @Override
  public String execute(Optional<String> inCampaign, Optional<String> inEntry)
  {
    if(!inCampaign.isPresent() || !inEntry.isPresent())
      return alert("unknown parameters given");

    DMADataFactory.get().setCurrentCampaignEntry(inCampaign.get(),
                                                 inEntry.get());

    Optional<AbstractEntry> currentEntry =
        DMADataFactory.get().getCurrentCampaignEntry(inCampaign.get());

    if(!currentEntry.isPresent())
      return alert("current campaign entry not set properly");

    return "$('#campaign-current-entry').html('" + currentEntry.get().getName()
        + "').click(function() { util.link(event, '"
        + currentEntry.get().getPath() + "')});";
  }
}
