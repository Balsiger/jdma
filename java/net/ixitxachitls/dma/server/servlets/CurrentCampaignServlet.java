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

package net.ixitxachitls.dma.server.servlets;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyData;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.dma.output.soy.SoyValue;

/**
 * Servlet showing current campaign information.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class CurrentCampaignServlet extends SoyServlet
{
  @Override
  protected String getTemplateName(DMARequest inDMARequest, Map<String, SoyData> inData)
  {
    return "dma.entries.campaigns.current";
  }

  @Override
  protected Map<String, Object> collectData(DMARequest inRequest,
                                            SoyRenderer inRenderer)
  {
    Map<String, Object> data = super.collectData(inRequest, inRenderer);

    String path = (String)data.get(Key.path.name());
    if(path == null)
      return data;

    Optional<AbstractEntry> campaign = getEntry(inRequest, path);
    if(!campaign.isPresent())
      return data;

    data.put("campaign",
             new SoyValue(campaign.get().getKey().toString(), campaign.get()));

    return data;
  }
}
