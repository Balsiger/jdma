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

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.BaseCharacter.Group;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The servlet responsible for handling registration requests.
 *
 * @file          RegistrationServlet.java
 *
 * @author        fred@dobler.net (Fred Dobler)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class RegistrationServlet extends ActionServlet
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- RegistrationServlet ----------------------
  /**
    * Create the servlet.
    */
  public RegistrationServlet()
  {
    //nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The name of the user cookie. */
  public static final String COOKIE_USER = "USER";

  /** The name of the token cookie. */
  public static final String COOKIE_TOKEN = "TOKEN";

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- doAction -------------------------------

  /**
   *
   * Execute the action associated with this servlet.
   *
   * @param       inRequest  the request for the page
   * @param       inResponse the response to write to
   *
   * @return      the javascript code to send back to the client
   *
   */
  protected @Nonnull String doAction(@Nonnull DMARequest inRequest,
                                     @Nonnull HttpServletResponse inResponse)
  {
    String username = inRequest.getParam("username");

    if(username == null)
      return "No username given";

    UserService userService = UserServiceFactory.getUserService();
    if(!userService.isUserLoggedIn())
    {
      return "You must login to register";
    }

    BaseCharacter user =
        DMADataFactory.getBaseData().getEntry(username, BaseCharacter.TYPE);
    if (user != null)
    {
      return "Username allready used, choose a new one.";
    }

    //Save new guest BaseCharacter
    BaseCharacter baseCharacter = new BaseCharacter(username, userService
        .getCurrentUser().getEmail(), Group.GUEST);
    DMADataFactory.getBaseData().update(baseCharacter);

    Log.event(username, "registration", "user registered");

    return "";
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.server.ServerUtils.Test
  {
    /** Setup before tests. */
    @org.junit.Before
    public void setUp()
    {
      net.ixitxachitls.dma.data.DMADataFactory.clearBase();
      net.ixitxachitls.util.configuration
        .Config.set("web.data.datastore", false);
      net.ixitxachitls.util.configuration
        .Config.set("web.data.datafiles", false);
    }

    /** Cleanup after tests. */
    @org.junit.After
    public void tearDown()
    {
      net.ixitxachitls.dma.data.DMADataFactory.clearBase();
    }

    //----- noData ---------------------------------------------------------

  }
  //........................................................................
}
