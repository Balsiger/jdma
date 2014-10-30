/******************************************************************************
 * Copyright (c) 2002-2014 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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

package net.ixitxachitls.dma.server.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
// import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.ParametersAreNonnullByDefault;
// import javax.servlet.http.HttpServletResponse;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.BaseCampaign;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.Campaign;
import net.ixitxachitls.dma.entries.Character;
// import net.ixitxachitls.dma.entries.Character;
// import net.ixitxachitls.dma.entries.Encounter;
// import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.output.soy.SoyEntry;
import net.ixitxachitls.dma.output.soy.SoyRenderer;

/**
 * The servlet for displaying the overview page for a user.
 *
 * @file          MainPageServlet.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@ParametersAreNonnullByDefault
public class MainPageServlet extends PageServlet
{
  /**
   * The standard constructor for the servlet.
   */
  public MainPageServlet()
  {
  }

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Get the time of the last modification.
   *
   * @return      the time of the last modification in milliseconds or -1
   *              if unknown
   */
  public long getLastModified()
  {
    return -1;
  }

  /**
   * Collect the data that is to be printed.
   *
   * @param    inRequest  the request for the page
   * @param    inRenderer the renderer for rendering sub values
   *
   * @return   a map with key/value pairs for data (values can be primitives
   *           or maps or lists)
   */
  @Override
  protected Map<String, Object> collectData(DMARequest inRequest,
                                            SoyRenderer inRenderer)
  {
    Map<String, Object> data = super.collectData(inRequest, inRenderer);

    Optional<BaseCharacter> user = inRequest.getUser();

    if(!user.isPresent())
      return data;

    SortedSet<Campaign> campaigns = new TreeSet<Campaign>();
    Map<String, List<SoyEntry>> characters =
      new HashMap<String, List<SoyEntry>>();
    for(Character character : DMADataFactory.get().getEntries
          (Character.TYPE, null, "base", user.get().getName()))
    {
      if(!character.getCampaign().isPresent())
        continue;

      campaigns.add(character.getCampaign().get());
      List<SoyEntry> list =
        characters.get(character.getCampaign().get().getName());
      if(list == null)
      {
        list = new ArrayList<SoyEntry>();
        characters.put(character.getCampaign().get().getName(), list);
      }

      list.add(new SoyEntry(character));
    }

    List<SoyEntry> soyCampaigns = new ArrayList<SoyEntry>();
    for(Campaign campaign : campaigns)
      soyCampaigns.add(new SoyEntry(campaign));

    List<Campaign> dmCampaigns =
      DMADataFactory.get().getEntries(Campaign.TYPE, null, "index-dm",
                                      user.get().getName());
    List<SoyEntry> soyDMCampaigns = new ArrayList<SoyEntry>();
    Map<String, List<SoyEntry>> dmCharacters =
      new HashMap<String, List<SoyEntry>>();

    for(Campaign campaign : dmCampaigns)
    {
      soyDMCampaigns.add(new SoyEntry(campaign));

      // and all the characters there
      List<SoyEntry> chars = new ArrayList<SoyEntry>();
      for(Character character : DMADataFactory.get()
            .getEntries(Character.TYPE, campaign.getKey(), 0, 20))
        chars.add(new SoyEntry(character));
      dmCharacters.put(campaign.getName(), chars);
    }


    List<BaseCampaign> baseCampaigns =
      DMADataFactory.get().getEntries(BaseCampaign.TYPE, null, 0, 50);
    List<SoyEntry> soyBaseCampaigns = new ArrayList<SoyEntry>();
    for(BaseCampaign campaign : baseCampaigns)
      soyBaseCampaigns.add(new SoyEntry(campaign));

    data.put("content",
             inRenderer.render
             ("dma.page.main",
              map("playing",
                  map("campaigns", soyCampaigns,
                      "characters", characters),
                  "dm",
                  map("campaigns", soyDMCampaigns,
                      "characters", dmCharacters),
                  "campaigns",
                  soyBaseCampaigns)));

    return data;
  }
}
