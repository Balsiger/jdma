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

package net.ixitxachitls.dma.server.servlets;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The servlet responsible for handling login requests.
 *
 * @file          LoginServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class LoginServlet extends ActionServlet
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- LoginServlet -----------------------------

  /**
    * Create the servlet.
    *
    * @param      inUsers the base campaign with all the user information
    *
    */
  public LoginServlet(@Nonnull Map<String, BaseCharacter> inUsers)
  {
    m_users = inUsers;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** the base campaign with the user information. */
  private Map<String, BaseCharacter> m_users;

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
    String password = inRequest.getParam("password");

    BaseCharacter user = m_users.get(username);

    if(user == null)
    {
      Log.warning("invalid username '" + username + "'");

      return "Invalid username or password!";
    }

    // check password and set login information
    String token = user.login(username, password);

    if(token == null)
    {
      Log.warning("invalid password for '" + username + "'");

      return "Invalid username or password!";
    }

    // set the cookie with the username and the token
    Cookie userCookie = new Cookie(COOKIE_USER, username);
    userCookie.setMaxAge(2 * 365 * 24 * 60 * 60);
    userCookie.setPath("/");

    Cookie tokenCookie = new Cookie(COOKIE_TOKEN, token);
    tokenCookie.setMaxAge(2 * 365 * 24 * 60 * 60);
    tokenCookie.setPath("/");

    inResponse.addCookie(userCookie);
    inResponse.addCookie(tokenCookie);

    Log.event(username, "login", "user logged in");

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
    //----- noData ---------------------------------------------------------

    /**
     * The handle Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void noData() throws Exception
    {
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      MockServletOutputStream output = new MockServletOutputStream();

      EasyMock.expect(request.getMethod()).andReturn("POST");
      EasyMock.expect(request.getQueryString()).andReturn("").anyTimes();
      EasyMock.expect(request.getRequestURI()).andReturn("");
      EasyMock.expect(request.getParam("username")).andReturn(null);
      EasyMock.expect(request.getParam("password")).andReturn(null);
      response.setHeader("Content-Type", "text/javascript");
      response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(response.getOutputStream()).andReturn(output);

      EasyMock.replay(request, response);

      BaseCharacter character = new BaseCharacter("somebody");
      character.setPassword("secret");

      LoginServlet servlet = new LoginServlet
        (com.google.common.collect.ImmutableMap.of("somebody", character));

      servlet.doPost(request, response);
      assertEquals("post", "Invalid username or password!", output.toString());

      m_logger.addExpected("WARNING: invalid username 'null'");
      EasyMock.verify(request, response);
    }

    //......................................................................
    //----- wrongPassword --------------------------------------------------

    /**
     * The handle Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void wrongPassword() throws Exception
    {
      DMARequest request =
        EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      MockServletOutputStream output = new MockServletOutputStream();

      EasyMock.expect(request.getMethod()).andReturn("POST");
      EasyMock.expect(request.getQueryString())
        .andReturn("username=somebody&password=guru").anyTimes();
      EasyMock.expect(request.getRequestURI()).andReturn("");
      EasyMock.expect(request.getParam("username")).andReturn("somebody");
      EasyMock.expect(request.getParam("password")).andReturn("guru");
      response.setHeader("Content-Type", "text/javascript");
      response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(response.getOutputStream()).andReturn(output);

      EasyMock.replay(request, response);

      BaseCharacter character = new BaseCharacter("somebody");
      character.setPassword("secret");

      LoginServlet servlet = new LoginServlet
        (com.google.common.collect.ImmutableMap.of("somebody", character));

      servlet.doPost(request, response);
      assertEquals("post", "Invalid username or password!", output.toString());

      m_logger.addExpected("WARNING: login for 'somebody' with wrong password");
      m_logger.addExpected("WARNING: invalid password for 'somebody'");
      EasyMock.verify(request, response);
    }

    //......................................................................
    //----- login ----------------------------------------------------------

    /**
     * The handle Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void login() throws Exception
    {
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      MockServletOutputStream output = new MockServletOutputStream();

      EasyMock.expect(request.getMethod()).andReturn("POST");
      EasyMock.expect(request.getQueryString())
        .andReturn("username=somebody&password=secret").anyTimes();
      EasyMock.expect(request.getRequestURI()).andReturn("");
      EasyMock.expect(request.getParam("username")).andReturn("somebody");
      EasyMock.expect(request.getParam("password")).andReturn("secret");
      response.setHeader("Content-Type", "text/javascript");
      response.setHeader("Cache-Control", "max-age=0");
      response.addCookie(EasyMock.isA(Cookie.class));
      response.addCookie(EasyMock.isA(Cookie.class));
      EasyMock.expect(response.getOutputStream()).andReturn(output);

      EasyMock.replay(request, response);

      BaseCharacter character = new BaseCharacter("somebody");
      character.setPassword("secret");

      LoginServlet servlet = new LoginServlet
        (com.google.common.collect.ImmutableMap.of("somebody", character));

      servlet.doPost(request, response);
      assertEquals("post", "", output.toString());

      EasyMock.verify(request, response);
    }

    //......................................................................
  }

  //........................................................................
}
