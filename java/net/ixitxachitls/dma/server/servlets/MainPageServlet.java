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

package net.ixitxachitls.dma.server.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
// import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Nonnull;
// import javax.servlet.http.HttpServletResponse;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.Campaign;
import net.ixitxachitls.dma.entries.Character;
// import net.ixitxachitls.dma.entries.Character;
// import net.ixitxachitls.dma.entries.Encounter;
// import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.output.soy.SoyEntry;
import net.ixitxachitls.dma.output.soy.SoyRenderer;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The servlet for displaying the overview page for a user.
 *
 * @file          MainPageServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class MainPageServlet extends PageServlet
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------- MainPageServlet ----------------------------

  /**
   * The standard constructor for the servlet.
   *
   */
  public MainPageServlet()
  {
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------- getLastModified ----------------------------

  /**
    * Get the time of the last modification.
    *
    * @return      the time of the last modification in miliseconds or -1
    *              if unknown
    *
    */
  public long getLastModified()
  {
    return -1;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------------------------- other member functions

  //----------------------------- collectData ------------------------------

  /**
   * Collect the data that is to be printed.
   *
   * @param    inRequest  the request for the page
   * @param    inRenderer the renderer for rendering sub values
   *
   * @return   a map with key/value pairs for data (values can be primitives
   *           or maps or lists)
   *
   */
  @Override
  protected @Nonnull Map<String, Object> collectData
    (@Nonnull DMARequest inRequest, @Nonnull SoyRenderer inRenderer)
  {
    Map<String, Object> data = super.collectData(inRequest, inRenderer);

    BaseCharacter user = inRequest.getUser();

    if(user == null)
      return data;

    SortedSet<Campaign> campaigns = new TreeSet<Campaign>();
    Map<String, List<SoyEntry>> characters =
      new HashMap<String, List<SoyEntry>>();
    for(Character character : DMADataFactory.get().getEntries
          (Character.TYPE, "base", user.getName()))
    {
      campaigns.add(character.getCampaign());
      List<SoyEntry> list = characters.get(character.getCampaign().getName());
      if(list == null)
      {
        list = new ArrayList<SoyEntry>();
        characters.put(character.getCampaign().getName(), list);
      }

      list.add(new SoyEntry(character));
    }

    List<SoyEntry> soyCampaigns = new ArrayList<SoyEntry>();
    for(Campaign campaign : campaigns)
      soyCampaigns.add(new SoyEntry(campaign));

    List<Campaign> dmCampaigns =
      DMADataFactory.get().getEntries(Campaign.TYPE, "dm", user.getName());
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

    data.put("content",
             inRenderer.render
             ("dma.page.main",
              map("playing",
                  map("campaigns", soyCampaigns,
                      "characters", characters),
                  "dm",
                  map("campaigns", soyDMCampaigns,
                      "characters", dmCharacters))));

    return data;
  }

  //........................................................................

  //........................................................................
}
