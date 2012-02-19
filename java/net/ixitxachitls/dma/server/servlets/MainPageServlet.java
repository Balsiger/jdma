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

// import java.util.ArrayList;
// import java.util.Iterator;
import java.util.List;
// import java.util.Map;
// import java.util.Set;
// import java.util.TreeMap;
// import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
// import javax.servlet.http.HttpServletResponse;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.Campaign;
import net.ixitxachitls.dma.entries.Character;
// import net.ixitxachitls.dma.entries.Character;
// import net.ixitxachitls.dma.entries.Encounter;
// import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.output.html.HTMLDocument;
// import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Divider;
// import net.ixitxachitls.output.commands.Icon;
import net.ixitxachitls.output.commands.Link;
// import net.ixitxachitls.output.commands.Script;
import net.ixitxachitls.output.commands.Subtitle;
// import net.ixitxachitls.output.commands.Table;
import net.ixitxachitls.output.html.HTMLWriter;
// import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.logging.Log;

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

  //------------------------------- writeBody ------------------------------

  /**
   * Handles the body content of the request.
   *
   * @param     inWriter  the writer to take up the content (will be closed
   *                      by the PageServlet)
   * @param     inPath    the path of the request
   * @param     inRequest the request for the page
   *
   */
  @Override
@OverridingMethodsMustInvokeSuper
  protected void writeBody(@Nonnull HTMLWriter inWriter,
                           @Nullable String inPath,
                           @Nonnull DMARequest inRequest)
  {
    super.writeBody(inWriter, inPath, inRequest);

    BaseCharacter user = inRequest.getUser();

    if(user == null)
      return;

    Log.info("serving dynamic user overview for " + user.getName());

    String title = "Overview for " + user.getName();
    inWriter.title(title);

    HTMLDocument document = new HTMLDocument(title);

    // first the characters of the user
    // we don't have these yet, though
    document.add(new Subtitle("Campaigns Playing"));

    Multimap<Campaign, Character> characters = TreeMultimap.create();
    for(Character character : DMADataFactory.get().getEntries
          (Character.TYPE, "base", user.getName()))
      characters.put(character.getCampaign(), character);

    for(Campaign campaign : characters.keySet())
    {
      document.add(new Divider("campaign",
                               new Link(campaign.getName(),
                                        campaign.getPath())));

      for(Character character : characters.get(campaign))
        document.add(character.getIcon(false));
    }

    // the campaigns the user is the dm for
    List<Campaign> campaigns =
      DMADataFactory.get().getEntries(Campaign.TYPE, "dm", user.getName());

    if(!campaigns.isEmpty())
    {
      document.add(new Subtitle("Campaigns DMing"));
      for(Campaign campaign : campaigns)
      {
        document.add(new Divider("campaign",
                                 new Link(campaign.getName(),
                                          campaign.getPath())));

        // and all the characters there
        for(Character character : DMADataFactory.get()
              .getEntries(Character.TYPE, campaign.getKey(), 0, 20))
          document.add(character.getIcon(true));
      }
    }

    inWriter.add(document.toString());
  }

  //-------------------------------- handle --------------------------------

  /**
   * Really handle the request.
   *
   * @param       inRequest  the http request to handle
   * @param       inResponse the http response to write to
   *
   * @throws      java.io.IOException writing to page failed
   *
   * @undefined   never
   *
   */
  // protected void handle(DMARequest inRequest,
  //                       HttpServletResponse inResponse)
  //   throws java.io.IOException
  // {

  //   // add the real content for the page

  //   // get all the characters and encounters for the current user
  //   for(Iterator<Entry> i = m_campaigns.getUnique(); i.hasNext(); )
  //   {
  //     Entry entry = i.next();

  //     Map<String, Object> commands = new TreeMap<String, Object>();
  //     Set<Encounter> encounters = new TreeSet<Encounter>();

  //     // we are only interested in campaigns
  //     if(!(entry instanceof Campaign))
  //       continue;

  //     Campaign campaign = (Campaign)entry;
  //     boolean dm = user.getName().equalsIgnoreCase(campaign.getDMName());

  //     boolean first = true;
  //     for(Entry campaignEntry : campaign)
  //     {
  //       // we are only interested in characters
  //       if(campaignEntry instanceof Character)
  //       {
  //         Character character = (Character)campaignEntry;

  //         // we only treat characters that match either the user or are in a
  //         // campaign the user is DM of
  //         if(!dm && character.isBased(user))
  //           continue;

  //         // print the name of the campaign the first time
  //         if(first)
  //         {
  //           document.add(new Subtitle(campaign.getName()));
  //           first = false;
  //         }

  //         commands.put(character.getName().toLowerCase(),
  //                      character.getIcon(character.isBased(user)));
  //       }
  //       else
  //         // ... and encounters
  //         if(dm && campaignEntry instanceof Encounter)
  //           encounters.add((Encounter)campaignEntry);
  //     }

  //     for(Object command : commands.values())
  //       document.add(command);

  //     if(dm)
  //     {
  //       document.add("<table width=100% id='encounters-table' "
  //                    + "class='encounters'>");
  //       document.add("<colgroup><col width=0/><col/><col/></colgroup>");
  //       document.add("<tr id='encounter-' class='encounter-group'>"
  //                    + "<td colspan=3>Encounters"
  //                    + "<div class='downloads'>"
  //                    + "<a href='/pdf/dm/encounters/" + campaign.getID()
  //                    + ".pdf'><img src='/icons/pdf-small.png'/></a>"
  //                    + "</td>"
  //                    + "</tr>\n");

  //       String []lasts = {};
  //       for(Encounter encounter : encounters)
  //       {
  //         // Determine the group for the encounter
  //         String group = encounter.getQualifiedName();

  //         // Determine if we have to set a new title
  //         lasts = addTitle(document, campaign, group, lasts);

  //         int pos = group.lastIndexOf("::");
  //         String name = group.substring(pos + 2);
  //         String parent = group.substring(0, pos);

  //         document.add("<tr id='encounter-" + Encodings.toCSSString(group)
  //                      + "' class='encounter child-of-encounter-"
  //                      + Encodings.toCSSString(parent) + "'>");

  //         for(Object cell : Encounter.FORMATTER.format(encounter.getName(),
  //                                                      encounter)) {
  //           document.add("<td>");
  //           document.add(cell);
  //           document.add("</td>");
  //         }

  //         document.add("</tr>");
  //       }

  //       document.add("</table>");
  //       document.add(new Script("$j('#encounters-table').treeTable"
  //                               + "({initialState: 'collapsed', "
  //                               + "clickableNodeNames: true})"));
  //     }
  //   }

  //   String text;
  //   if(inRequest.isBodyOnly())
  //  text = "<script>document.title = '" + Encodings.encodeHTMLAttribute(title)
  //       + "';</script>" + document.toBodyString();
  //   else
  //     text = document.toString();

  //   // add content type
  //   inResponse.addHeader("Content-Type", m_type);

  //   PrintStream print = new PrintStream(inResponse.getOutputStream());

  //   print.print(text);
  //   print.close();
  // }

  // private static String []addTitle(HTMLDocument document, Campaign campaign,
  //                                  String group, String []lasts)
  // {
  //   String []groups = group.split("::");
  //   String parent = "";

  //   int i;
  //   for(i = 0; i < groups.length; i++)
  //     if(lasts.length <= i || !lasts[i].equals(groups[i]))
  //       break;
  //     else
  //       parent += groups[i];

  //   for(; i < groups.length - 1; i++)
  //   {
  //     document.add("<tr id='encounter-"
  //                  + Encodings.toCSSString(parent + groups[i])
  //                  + "' class='encounter-group child-of-encounter-"
  //                  + Encodings.toCSSString(parent) + "'>"
  //                  + "<td colspan=3 >"
  //                  + "<span class='encounter-name'>" + groups[i] + "</span>"
  //                  + "<div class='downloads'>"
  //                  + "<a href='/pdf/dm/encounters/" + campaign.getID()
  //                  + ".pdf?sub=" + groups[i] + "'>"
  //                  + "<img src='/icons/pdf-small.png'/></a>"
  //                  + "</td></tr>");
  //     parent += groups[i];
  //   }

  //   return groups;
  // }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................
}
