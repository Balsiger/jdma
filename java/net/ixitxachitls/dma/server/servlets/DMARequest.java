/******************************************************************************
 * Copyright (c) 2002-2011 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.apphosting.api.ApiProxy;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.util.Tracer;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;


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

// from base class
@ParametersAreNonnullByDefault
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
  public DMARequest(HttpServletRequest inRequest,
                    Multimap<String, String> inParams)
  {
    super(inRequest);

    m_params = inParams;

//     extractCampaign(inRequest);
//     extractDM(inRequest);
//     extractPlayer(inRequest);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  static
  {
    ensureTypes();
  }

  /** The URL and post parameters. */
  private Multimap<String, String> m_params;

  /** Flag if the user has been extracted. */
  private boolean m_extractedUser = false;

  /** The user doing the request, if any. */
  private @Nullable BaseCharacter m_user = null;

  /** The user override for doing the request, if any. */
  private @Nullable BaseCharacter m_userOverride = null;

  /** The cached entries for the request. */
  private Map<AbstractEntry.EntryKey<?>, AbstractEntry> m_entries =
    Maps.newHashMap();

  /** The player for the request, if any. */
//   private Character m_player = null;

  /** The dm for the request, if any. */
//   private BaseCharacter m_dm = null;

  /** The default size of an index page (number of entries shown). */
  protected static final int def_pageSize =
    Config.get("resource:html/product.page", 50);

  /** The attribute to use for the original path. */
  public static final String ORIGINAL_PATH = "originalPath";

  //........................................................................

  //-------------------------------------------------------------- accessors

  @Override
  public String toString()
  {
    return "DMA Request: "
      + (m_user == null ? "no user" : m_user.getName())
      + (m_userOverride == null ? "" : " (" + m_userOverride.getName() + ")")
      + ", params " + m_params;

  }

  //------------------------------- hasUser --------------------------------

  /**
   * Check if the request has a user associated with it.
   *
   * @return      true if there is a user, false if not
   *
   */
  public boolean hasUser()
  {
    extractUser();
    return m_user != null;
  }

  //........................................................................
  //--------------------------- hasUserOverride ----------------------------

  /**
   * Check if the request has a user override associated with it.
   *
   * @return      true if there is a user override, false if not
   *
   */
  public boolean hasUserOverride()
  {
    extractUser();
    return m_userOverride != null;
  }

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
  //------------------------------- hasParam -------------------------------

  /**
   * Check if the request has a given parameter.
   *
   * @param       inName the name of the parameter to check for
   *
   * @return      true if the parameter is there, false if not
   *
   */
  public boolean hasParam(String inName)
  {
    return getParam(inName) != null;
  }

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
  public boolean isBodyOnly()
  {
    return hasParam("body");
  }

  //........................................................................

  //------------------------------ getParam --------------------------------

  /**
   * Get the first value given for a key.
   *
   * @param       inName the name of the parameter to get
   *
   * @return      the value of the parameter or null if not found
   *
   */
  public @Nullable String getParam(String inName)
  {
    Collection<String> values = m_params.get(inName);

    if(values == null || values.isEmpty())
      return null;

    return values.iterator().next();
  }

  //........................................................................
  //------------------------------ getParam --------------------------------

  /**
   * Get the first value given for a key as an integer.
   *
   * @param       inName    the name of the parameter to get
   * @param       inDefault the value to return if it is not present
   *
   * @return      the value of the parameter or null if not found
   *
   */
  public @Nullable int getParam(String inName, int inDefault)
  {
    String value = getParam(inName);

    try
    {
      if(value != null)
        return Integer.parseInt(value);
    }
    catch(NumberFormatException e)
    {
      Log.warning("invalid integer parameter " + inName + " ignored: " + e);
    }

    return inDefault;
  }

  //........................................................................
  //------------------------------ getParams -------------------------------

  /**
   * Get all the paramaters.
   *
   * @return      all the parameters
   *
   */
  public Multimap<String, String> getParams()
  {
    return m_params;
  }

  //........................................................................
  //------------------------------ getStart --------------------------------

  /**
   * Get the start and end indexes for the page.
   *
   * @return      the start index for pagination, starting with 0
   *
   */
  public int getStart()
  {
    return getParam("start", 0);
  }

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
    return getParam("size", def_pageSize);
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
   * @return the currently logged in user or the user on whose behalve we are
   *         acting
   *
   */
  public @Nullable BaseCharacter getUser()
  {
    Tracer tracer = new Tracer("getting user");
    extractUser();

    // only admin are allows to do that
    if(m_userOverride != null && hasUser()
       && m_user.hasAccess(BaseCharacter.Group.ADMIN))
      return m_userOverride;

    tracer.done();
    return m_user;
  }

  //........................................................................
  //------------------------------- getUser --------------------------------

  /**
   * Get the real user for the request.
   *
   * @return the currently logged in user
   *
   */
  public @Nullable BaseCharacter getRealUser()
  {
    extractUser();
    return m_user;
  }

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
  //--------------------------- getOriginalPath ----------------------------

  /**
   * Get the original path of the request.
   *
   * @return  the original path
   *
   */
  public String getOriginalPath()
  {
    Object path = getAttribute(ORIGINAL_PATH);
    if(path != null)
      return path.toString();

    return getRequestURI();
  }

  //........................................................................
  //------------------------------- getEntry -------------------------------

  /**
   * Get a cached entry from the request.
   *
   * @param       inKey the key of the entry to get
   * @param       <T>   the type of entry to get
   *
   * @return      the entry found or null if none stored
   *
   */
  @SuppressWarnings("unchecked") // need to cast result
  public @Nullable <T extends AbstractEntry> T
                      getEntry(AbstractEntry.EntryKey<T> inKey)
  {
    if(!m_entries.containsKey(inKey))
      m_entries.put(inKey, DMADataFactory.get().getEntry(inKey));

    return (T)m_entries.get(inKey);
  }

  //........................................................................
  //--------------------------- timeIsRunningOUt ---------------------------

  /**
   * Check if the time for the request is running out. There is a time limit
   * of 60s on an appengine request.
   *
   * @return true if time is running out, false if not
   *
   */
  public boolean timeIsRunningOut()
  {
    if (DMAServlet.isDev())
      return false;

    return ApiProxy.getCurrentEnvironment().getRemainingMillis() < 5000;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //----------------------------- extractUser ------------------------------

  /**
   * Get the email address from the AppEngine UserService and
   * lookup a matching BaseCharacter in the DMAData.
   *
   */
  public void extractUser()
  {
    if(m_extractedUser)
      return;

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn())
    {
      m_user = DMADataFactory.get()
          .getEntry(BaseCharacter.TYPE, "email", new Text(userService
              .getCurrentUser().getEmail()).toString());
    }
    if (m_user != null)
      m_user.action();
    else
      m_user = null;

    String override = getParam("user");
    if(override != null && !override.isEmpty())
      m_userOverride = DMADataFactory.get()
        .getEntry(AbstractEntry.createKey(override, BaseCharacter.TYPE));

    m_extractedUser = true;
  }

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

  //----------------------------- ensureTypes ------------------------------

  /**
   * Make sure all the types are properly loaded.
   *
   */
  public static void ensureTypes()
  {
    // We have to setup the types here, as we otherwise end up with them
    // not being properly initialized.
    if(net.ixitxachitls.dma.entries.BaseCharacter.TYPE == null)
      Log.warning("could not properly initialize base character type");
    if(net.ixitxachitls.dma.entries.Character.TYPE == null)
      Log.warning("could not properly initialize base character type");
    if(net.ixitxachitls.dma.entries.BaseItem.TYPE == null)
      Log.warning("could not properly initialize base item type");
    if(net.ixitxachitls.dma.entries.Item.TYPE == null)
      Log.warning("could not properly initialize item type");
    if(net.ixitxachitls.dma.entries.BaseProduct.TYPE == null)
      Log.warning("could not properly initialize base product type");
    if(net.ixitxachitls.dma.entries.Product.TYPE == null)
      Log.warning("could not properly initialize product type");
    if(net.ixitxachitls.dma.entries.BaseCampaign.TYPE == null)
      Log.warning("could not properly initialize base campaign type");
    if(net.ixitxachitls.dma.entries.Campaign.TYPE == null)
      Log.warning("could not properly initialize campaign type");
    if(net.ixitxachitls.dma.entries.BaseQuality.TYPE == null)
      Log.warning("could not properly initialize base quality type");
    if(net.ixitxachitls.dma.entries.BaseFeat.TYPE == null)
      Log.warning("could not properly initialize base feat type");
    if(net.ixitxachitls.dma.entries.BaseMonster.TYPE == null)
      Log.warning("could not properly initialize base monster type");
    if(net.ixitxachitls.dma.entries.Monster.TYPE == null)
      Log.warning("could not properly initialize monster type");
    if(net.ixitxachitls.dma.entries.BaseSkill.TYPE == null)
      Log.warning("could not properly initialize base skill type");
    if(net.ixitxachitls.dma.entries.BaseSpell.TYPE == null)
      Log.warning("could not properly initialize base spell type");
    if(net.ixitxachitls.dma.entries.BaseEncounter.TYPE == null)
      Log.warning("could not properly initialize base encounter type");
    if(net.ixitxachitls.dma.entries.Encounter.TYPE == null)
      Log.warning("could not properly initialize encounter type");
    if(net.ixitxachitls.dma.entries.BaseLevel.TYPE == null)
      Log.warning("could not properly initialize base level type");
    if(net.ixitxachitls.dma.entries.Level.TYPE == null)
      Log.warning("could not properly initialize level type");
    if(net.ixitxachitls.dma.entries.NPC.TYPE == null)
      Log.warning("could not properly initialize npc type");
  }

  //........................................................................


  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.server.ServerUtils.Test
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
                       com.google.common.collect.HashMultimap
                       .<String, String>create());

      assertEquals("page size", def_pageSize, request.getPageSize());

      EasyMock.verify(mockRequest);
    }

    //......................................................................
    //----- user -----------------------------------------------------------

    /** The user Test. */
    @org.junit.Test
    public void user()
    {
      BaseCharacter user = new BaseCharacter("test");
      user.set("email", "\"test@test.net\"");
      addEntry(user);
      m_localServiceTestHelper.setEnvIsLoggedIn(true);

      HttpServletRequest mockRequest =
        EasyMock.createMock(HttpServletRequest.class);

      EasyMock.replay(mockRequest);

      DMARequest request =
        new DMARequest(mockRequest,
                       com.google.common.collect.HashMultimap
                       .<String, String>create());

      assertEquals("user", user, request.getUser());

      EasyMock.verify(mockRequest);
    }

    //.....................................................................
    //----- user override --------------------------------------------------

    /** The user Test. */
    @org.junit.Test
    public void userOverride()
    {
      BaseCharacter user = new BaseCharacter("test");
      user.set("email", "\"test@test.net\"");
      user.setGroup(BaseCharacter.Group.ADMIN);
      addEntry(user);
      BaseCharacter other = new BaseCharacter("other");
      addEntry(other);

      HttpServletRequest mockRequest =
        EasyMock.createMock(HttpServletRequest.class);

      EasyMock.replay(mockRequest);

      DMARequest request =
        new DMARequest(mockRequest,
                       com.google.common.collect.ImmutableMultimap.of
                       ("user", "other"));

      assertEquals("user", other, request.getUser());

      EasyMock.verify(mockRequest);
    }

    //......................................................................
    //----- user override no admin -----------------------------------------

    /** The user Test. */
    @org.junit.Test
    public void userOverrideNonAdmin()
    {
      BaseCharacter user = new BaseCharacter("test");
      user.set("email", "\"test@test.net\"");
      addEntry(user);
      BaseCharacter other = new BaseCharacter("other");
      addEntry(other);

      HttpServletRequest mockRequest =
        EasyMock.createMock(HttpServletRequest.class);

      EasyMock.replay(mockRequest);

      DMARequest request =
         new DMARequest(mockRequest,
                        com.google.common.collect.ImmutableMultimap.of
                        ("user", "other"));

      assertEquals("user", user, request.getUser());

      EasyMock.verify(mockRequest);
    }

    //......................................................................
  }

  //........................................................................
}
