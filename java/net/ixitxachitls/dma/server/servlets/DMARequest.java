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
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.values.enums.Group;
import org.easymock.EasyMock;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.util.Tracer;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

/**
 * A wrapper around an http request for DMA purposes, with enhanced data.
 *
 * @file          DMARequest.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

// from base class
@ParametersAreNonnullByDefault
public class DMARequest extends HttpServletRequestWrapper
{
  /**
   * A request wrapper for dma requests.
   *
   * @param       inRequest the request to be wrapped
   * @param       inParams  the parameters to the request (URL & post)
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

  static
  {
    ensureTypes();
  }

  /** The URL and post parameters. */
  private Multimap<String, String> m_params;

  /** Flag if the user has been extracted. */
  private boolean m_extractedUser = false;

  /** The user doing the request, if any. */
  private Optional<BaseCharacter> m_user = Optional.absent();

  /** The user override for doing the request, if any. */
  private Optional<BaseCharacter> m_userOverride = Optional.absent();

  /** The cached entries for the request. */
  private Map<EntryKey, AbstractEntry> m_entries = Maps.newHashMap();

  /** The player for the request, if any. */
//   private Character m_player = null;

  /** The dm for the request, if any. */
//   private BaseCharacter m_dm = null;

  /** The default size of an index page (number of entries shown). */
  protected static final int def_pageSize =
    Config.get("resource:html/product.page", 50);

  /** The attribute to use for the original path. */
  public static final String ORIGINAL_PATH = "originalPath";

  @Override
  public String toString()
  {
    return "DMA Request: "
      + (m_user.isPresent() ? m_user.get().getName() : "[no user]")
      + (m_userOverride.isPresent()
          ? " (" + m_userOverride.get().getName() + ")": "" )
      + ", params " + m_params;

  }

  /**
   * Check if the request has a user associated with it.
   *
   * @return      true if there is a user, false if not
   */
  public boolean hasUser()
  {
    extractUser();
    return m_user.isPresent();
  }

  /**
   * Check if the request has a user override associated with it.
   *
   * @return      true if there is a user override, false if not
   */
  public boolean hasUserOverride()
  {
    extractUser();
    return m_userOverride.isPresent();
  }

  /**
   * Check if the request has a player associated with it.
   *
   * @return      true if there is a player, false if not
   */
//   public boolean hasPlayer()
//   {
//     return m_player != null;
//   }

  /**
   * Check if the request has a DM associated with it.
   *
   * @return      true if there is a DM, false if not
   */
//   public boolean hasDM()
//   {
//     return m_dm != null;
//   }

  /**
   * Check if the request has a campaign associated with it.
   *
   * @return      true if there is a campaign, false if not
   */
//   public boolean hasCampaign()
//   {
//     return m_campaign != null;
//   }

  /**
   * Check if the request has a given parameter.
   *
   * @param       inName the name of the parameter to check for
   *
   * @return      true if the parameter is there, false if not
   */
  public boolean hasParam(String inName)
  {
    return getParam(inName) != null;
  }

  /**
   * Check if the request has an admin parameter.
   *
   * @return      true if the admin parameter is there, false if not
   */
//   public boolean hasAdminParam()
//   {
//     return m_params.get("admin") != null;
//   }

  /**
   * Check if the request should only return the body of a page.
   *
   * @return      true for the body, false for full page
   */
  public boolean isBodyOnly()
  {
    return hasParam("body");
  }

  /**
   * Get the first value given for a key.
   *
   * @param       inName the name of the parameter to get
   *
   * @return      the value of the parameter or null if not found
   */
  public @Nullable String getParam(String inName)
  {
    Collection<String> values = m_params.get(inName);

    if(values == null || values.isEmpty())
      return null;

    return values.iterator().next();
  }

  /**
   * Get the first value given for a key as an integer.
   *
   * @param       inName    the name of the parameter to get
   * @param       inDefault the value to return if it is not present
   *
   * @return      the value of the parameter or null if not found
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

  /**
   * Get all the paramaters.
   *
   * @return      all the parameters
   */
  public Multimap<String, String> getParams()
  {
    return m_params;
  }

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

  /**
   * Get all the keys of all the URL paramters.
   *
   * @return      a set of all URL parameter names
   */
//   public Set<String> getURLParamNames()
//   {
//     return m_params.keySet();
//   }

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

  /**
   * Get the campaign for the request.
   */
//   @MayReturnNull
//   public Campaign getCampaign()
//   {
//     return m_campaign;
//   }

  /**
   * Get the user for the request.
   *
   * @return the currently logged in user or the user on whose behalve we are
   *         acting
   */
  public Optional<BaseCharacter> getUser()
  {
    Tracer tracer = new Tracer("getting user");
    extractUser();

    // only admins are allows to override users
    if(m_userOverride.isPresent() && hasUser()
       && m_user.get().hasAccess(Group.ADMIN))
      return m_userOverride;

    tracer.done();
    return m_user;
  }

  /**
   * Get the real user for the request.
   *
   * @return the currently logged in user
   */
  public Optional<BaseCharacter> getRealUser()
  {
    extractUser();
    return m_user;
  }

  /**
   * Get the player for the request.
   *
   */
//   @MayReturnNull
//   public Character getPlayer()
//   {
//     return m_player;
//   }

  /**
   * Get the dm for the request.
   */
//   @MayReturnNull
//   public BaseCharacter getDM()
//   {
//     return m_dm;
//   }

  /**
   * Get the original path of the request.
   *
   * @return  the original path
   */
  public String getOriginalPath()
  {
    Object path = getAttribute(ORIGINAL_PATH);
    if(path != null)
      return path.toString();

    return getRequestURI();
  }

  /**
   * Get a cached entry from the request.
   *
   * @param       inKey the key of the entry to get
   * @param       <T>   the type of entry to get
   *
   * @return      the entry found or null if none stored
   */
  @SuppressWarnings("unchecked") // need to cast result
  public <T extends AbstractEntry> Optional<T> getEntry(EntryKey inKey)
  {
    Optional<T> entry = Optional.fromNullable((T)m_entries.get(inKey));
    if(entry.isPresent())
      return entry;

    entry = DMADataFactory.get().getEntry(inKey);
    if (entry.isPresent())
      m_entries.put(inKey, entry.get());

    return entry;
  }

  /**
   * Check if the time for the request is running out. There is a time limit
   * of 60s on an app engine request.
   *
   * @return true if time is running out, false if not
   */
  public boolean timeIsRunningOut()
  {
    if (DMAServlet.isDev())
      return false;

    return ApiProxy.getCurrentEnvironment().getRemainingMillis() < 10000;
  }

  /**
   * Get the email address from the AppEngine UserService and
   * lookup a matching BaseCharacter in the DMAData.
   */
  public void extractUser()
  {
    if(m_extractedUser)
      return;

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn())
    {
      if (!m_user.isPresent())
        m_user = DMADataFactory.get()
          .getEntry(BaseCharacter.TYPE, "email",
                    userService.getCurrentUser().getEmail());
    }
    if (m_user.isPresent())
      m_user.get().action();

    String override = getParam("user");
    if(override != null && !override.isEmpty())
      m_userOverride = DMADataFactory.get()
        .getEntry(AbstractEntry.createKey(override, BaseCharacter.TYPE));

    m_extractedUser = true;
  }

  /**
   * Extract the campaign from the request, if any.
   *
   * @param       inRequest the request to process
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

  /**
   * Extract the DM from the request, if any.
   *
   * @param       inRequest the request to process
   */
//   public void extractDM(HttpServletRequest inRequest)
//   {
//     if(m_user == null || m_campaign == null)
//       return;

//     if(m_campaign.getDMName().equals(m_user.getName()))
//       m_dm = m_user;
//   }

  /**
   * Extract the player from the request, if any.
   *
   * @param       inRequest the request to process
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

  /**
   * Make sure all the types are properly loaded.
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
    if(net.ixitxachitls.dma.entries.NPC.TYPE == null)
      Log.warning("could not properly initialize npc type");
  }

  //----------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.server.ServerUtils.Test
  {
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

    /** The user Test. */
    @org.junit.Test
    public void user()
    {
      BaseCharacter user = new BaseCharacter("test");
      //user.set("email", "\"test@test.net\"");
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

    /** The user Test. */
    @org.junit.Test
    public void userOverride()
    {
      BaseCharacter user = new BaseCharacter("test");
      //user.set("email", "\"test@test.net\"");
      user.setGroup(Group.ADMIN);
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

    /** The user Test. */
    @org.junit.Test
    public void userOverrideNonAdmin()
    {
      BaseCharacter user = new BaseCharacter("test");
      //user.set("email", "\"test@test.net\"");
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
  }
}
