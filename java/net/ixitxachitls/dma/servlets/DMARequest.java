/******************************************************************************
 * Copyright (c) 2002-2007 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.servlets;

// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.net.URLDecoder;
import java.util.Collection;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
//import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.google.common.collect.Multimap;

import org.easymock.EasyMock;

// import net.ixitxachitls.dma.entries.AbstractEntry;
// import net.ixitxachitls.dma.entries.BaseCampaign;
// import net.ixitxachitls.dma.entries.BaseCharacter;
// import net.ixitxachitls.dma.entries.Campaign;
// import net.ixitxachitls.dma.entries.Character;
// import net.ixitxachitls.dma.entries.Entry;
// import net.ixitxachitls.dma.server.servlets.LoginServlet;
// import net.ixitxachitls.util.Pair;
// import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;
// import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A wrapper around an http request for DMA purposes, with enhanced data.
 *
 * @file          DMARequest.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@SuppressWarnings("deprecation") // from base class
public class DMARequest extends HttpServletRequestWrapper
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ DMARequest ------------------------------

  /**
   * A request wrapper for dma requests.
   *
   * @param       inRequest the request to be wrapped
   * @param       inParams  the parameters to the request (URL & post)
   *
   */
//    * @param       inUsers the users available in the system
//    * @param       inCampaigns the campaigns
  public DMARequest(@Nonnull HttpServletRequest inRequest,
                    @Nonnull Multimap<String, String> inParams
                    /*, BaseCampaign inUsers, Campaign inCampaigns*/)
  {
    super(inRequest);

    m_params = inParams;
//     m_users = inUsers;
//     m_campaigns = inCampaigns;

//     extractUser(inRequest);
//     extractCampaign(inRequest);
//     extractDM(inRequest);
//     extractPlayer(inRequest);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The URL and post parameters. */
  private Multimap<String, String> m_params;

  /** The base campaign with all the users. */
//   private BaseCampaign m_users = null;

  /** The campaign containing all campaigns. */
//   private Campaign m_campaigns = null;

  /** The campaign for the current request, if any. */
//   private Campaign m_campaign = null;

  /** The user doing the request, if any. */
//   private BaseCharacter m_user = null;

  /** The player for the request, if any. */
//   private Character m_player = null;

  /** The dm for the request, if any. */
//   private BaseCharacter m_dm = null;

  /** The default size of an index page (number of entries shown). */
  protected static final int def_pageSize =
    Config.get("resource:html/product.page", 25);

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- hasUser --------------------------------

  /**
   * Check if the request has a user associated with it.
   *
   * @return      true if there is a user, false if not
   *
   */
//   public boolean hasUser()
//   {
//     return m_user != null;
//   }

  //........................................................................
  //------------------------------ hasPlayer -------------------------------

  /**
   * Check if the request has a player associated with it.
   *
   * @return      true if there is a player, false if not
   *
   */
//   public boolean hasPlayer()
//   {
//     return m_player != null;
//   }

  //........................................................................
  //-------------------------------- hasDM ---------------------------------

  /**
   * Check if the request has a DM associated with it.
   *
   * @return      true if there is a DM, false if not
   *
   */
//   public boolean hasDM()
//   {
//     return m_dm != null;
//   }

  //........................................................................
  //----------------------------- hasCampaign ------------------------------

  /**
   * Check if the request has a campaign associated with it.
   *
   * @return      true if there is a campaign, false if not
   *
   */
//   public boolean hasCampaign()
//   {
//     return m_campaign != null;
//   }

  //........................................................................
  //----------------------------- hasURLParam ------------------------------

  /**
   * Check if the request has a given URL parameter.
   *
   * @param       inName the name of the parameter to check for
   *
   * @return      true if the parameter is there, false if not
   *
   */
//   public boolean hasURLParam(@Nonnull String inName)
//   {
//     return m_params.get(inName) != null;
//   }

  //........................................................................
  //--------------------------- hasCreateParam -----------------------------

  /**
   * Check if the request has a given create parameter.
   *
   * @return      true if the parameter is there, false if not
   *
   */
//   public boolean hasCreateParam()
//   {
//     return m_params.get("create") != null;
//   }

  //........................................................................
  //---------------------------- hasAdminParam -----------------------------

  /**
   * Check if the request has an admin parameter.
   *
   * @return      true if the admin parameter is there, false if not
   *
   */
//   public boolean hasAdminParam()
//   {
//     return m_params.get("admin") != null;
//   }

  //........................................................................
  //------------------------------ isBodyOnly ------------------------------

  /**
   * Check if the request should only return the body of a page.
   *
   * @return      true for the body, false for full page
   *
   */
//   public boolean isBodyOnly()
//   {
//     return hasURLParam("body");
//   }

  //........................................................................

  //---------------------------- getFirstParam -----------------------------

  /**
   * Get the first value given for a key.
   *
   * @param       inName the name of the parameter to get
   *
   * @return      the value of the parameter or null if not found
   *
   */
  public @Nullable String getParam(@Nonnull String inName)
  {
    Collection<String> values = m_params.get(inName);

    if(values == null || values.isEmpty())
      return null;

    return values.iterator().next();
  }

  //........................................................................
  //---------------------------- getStartIndex -----------------------------

  /**
   * Get the start index and end index for the page.
   *
   * @return      the start and end index for pagination, starting with 0
   *
   */
//   public Pair<Integer, Integer> getPagination()
//   {
//     int start = 0;
//     int end = 0;

//     if(hasURLParam("start"))
//     {
//       try
//       {
//         start = Integer.parseInt(getURLParam("start"));
//       }
//       catch(NumberFormatException e)
//       {
//         Log.warning("invalid start parameter ignored: " + e);
//       }
//     }

//     if(hasURLParam("end"))
//     {
//       try
//       {
//         end = Integer.parseInt(getURLParam("end"));
//       }
//       catch(NumberFormatException e)
//       {
//         Log.warning("invalid end parameter ignored: " + e);
//       }
//     }

//     if(end == 0)
//       end = start + def_pageSize;

//     return new Pair<Integer, Integer>(start, end);
//   }

  //........................................................................
  //--------------------------- getURLParamNames ---------------------------

  /**
   * Get all the keys of all the URL paramters.
   *
   * @return      a set of all URL parameter names
   *
   */
//   public Set<String> getURLParamNames()
//   {
//     return m_params.keySet();
//   }

  //........................................................................
  //----------------------------- getPageSize ------------------------------

  /**
   * Gets the page size.
   *
   * @return      the size of the page.
   *
   */
  public int getPageSize()
  {
    return def_pageSize;
  }

  //........................................................................
  //----------------------------- getCampaign ------------------------------

  /**
   * Get the campaign for the request.
   *
   */
//   @MayReturnNull
//   public Campaign getCampaign()
//   {
//     return m_campaign;
//   }

  //........................................................................
  //------------------------------- getUser --------------------------------

  /**
   * Get the user for the request.
   *
   */
//   @MayReturnNull
//   public BaseCharacter getUser()
//   {
//     return m_user;
//   }

  //........................................................................
  //------------------------------ getPlayer -------------------------------

  /**
   * Get the player for the request.
   *
   */
//   @MayReturnNull
//   public Character getPlayer()
//   {
//     return m_player;
//   }

  //........................................................................
  //-------------------------------- getDM ---------------------------------

  /**
   * Get the dm for the request.
   *
   */
//   @MayReturnNull
//   public BaseCharacter getDM()
//   {
//     return m_dm;
//   }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //----------------------------- extractUser ------------------------------

  /**
   * Extract the user from the request, if any.
   *
   * @param       inRequest the request to process
   *
   */
//   public void extractUser(HttpServletRequest inRequest)
//   {
//     if(m_users == null)
//       return;

//     // check for the user and token cookies
//     Cookie []cookies = inRequest.getCookies();

//     String user  = null;
//     String token = null;
//     BaseCharacter.Group group = null;

//     if(cookies != null)
//       for(Cookie cookie : cookies)
//       {
//         if(LoginServlet.COOKIE_USER.equals(cookie.getName()))
//           user = cookie.getValue();
//         else
//           if(LoginServlet.COOKIE_TOKEN.equals(cookie.getName()))
//             token = cookie.getValue();
//       }

//     if(user != null && token != null)
//     {
//       m_user = m_users.getBaseEntry(user, BaseCharacter.TYPE);

//       if(m_user != null)
//         if(m_user.checkToken(token))
//           m_user.action();
//         else
//           m_user = null;
//     }
//   }

  //........................................................................
  //--------------------------- extractCampaign ----------------------------

  /**
   * Extract the campaign from the request, if any.
   *
   * @param       inRequest the request to process
   *
   */
//   public void extractCampaign(HttpServletRequest inRequest)
//   {
//     if(m_campaigns == null)
//       return;

//     String id = Strings.getPattern(inRequest.getRequestURI(),
//                                    "^/campaign/([^/]*)");

//     if(id == null)
//       return;

//     m_campaign = m_campaigns.getEntry(id, Campaign.TYPE);
//   }

  //........................................................................
  //------------------------------ extractDM -------------------------------

  /**
   * Extract the DM from the request, if any.
   *
   * @param       inRequest the request to process
   *
   */
//   public void extractDM(HttpServletRequest inRequest)
//   {
//     if(m_user == null || m_campaign == null)
//       return;

//     if(m_campaign.getDMName().equals(m_user.getName()))
//       m_dm = m_user;
//   }

  //........................................................................
  //---------------------------- extractPlayer -----------------------------

  /**
   * Extract the player from the request, if any.
   *
   * @param       inRequest the request to process
   *
   */
//   public void extractPlayer(HttpServletRequest inRequest)
//   {
//     if(m_user == null || m_campaign == null)
//       return;

//     String []parts = Strings.getPatterns(inRequest.getRequestURI(),
//                                          "^/campaign/.*/(.*)/(.*?)$");

//     if(parts == null || parts.length != 2)
//       return;

//     String id = parts[1];
//     AbstractEntry.Type<? extends Entry> type =
//       AbstractEntry.Type.getEntryType(parts[0]);

//     Entry entry = m_campaign.getEntry(id, type);

//     if(entry == null)
//       return;

//     m_player = entry.getPlayer();

//     if(m_player != null && !m_player.isBased(m_user))
//       m_player = null;
//   }

  //........................................................................

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** The init Test. */
    @org.junit.Test
    public void init()
    {
      HttpServletRequest mockRequest =
        EasyMock.createMock(HttpServletRequest.class);

      EasyMock.replay(mockRequest);

      DMARequest request =
        new DMARequest(mockRequest,
                       com.google.common.collect.HashMultimap.
                       <String, String>create());

      assertEquals("page size", def_pageSize, request.getPageSize());

      EasyMock.verify(mockRequest);
    }

    //......................................................................
  }

  //........................................................................
}
