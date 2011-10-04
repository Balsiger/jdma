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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.util.logging.Log;

import org.easymock.EasyMock;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The servlet responsible for handling logout requests.
 *
 * @file          LogoutServlet.java
 *
 * @author        balsiger@ixitxachitls,net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class LogoutServlet extends LoginServlet
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- LogoutServlet -----------------------------

  /**
    * Create the servlet.
    */
  public LogoutServlet()
  {
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- doAction -------------------------------

  /**
   * Execute the action associated with this servlet.
   *
   * @param       inRequest  the request from the client
   * @param       inResponse the response to write to
   *
   * @return      the javascript code to send back to the client
   *
   */
  protected @Nonnull String doAction(@Nonnull DMARequest inRequest,
                            @Nonnull HttpServletResponse inResponse)
  {
    BaseCharacter character = inRequest.getUser();

    if(character == null)
      return "";

    character.clearToken();

    // clear the user and token cookies
    Cookie userCookie = new Cookie(COOKIE_USER, "");
    userCookie.setMaxAge(0);
    userCookie.setPath("/");

    Cookie tokenCookie = new Cookie(COOKIE_TOKEN, "");
    tokenCookie.setMaxAge(0);
    tokenCookie.setPath("/");

    inResponse.addCookie(userCookie);
    inResponse.addCookie(tokenCookie);

    Log.event(character.getID(), "logout", "user logged out");

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
    //----- logout ---------------------------------------------------------

    /**
     * The handle Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void logout() throws Exception
    {
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      MockServletOutputStream output = new MockServletOutputStream();

      BaseCharacter character =
        new BaseCharacter("somebody",
                          new net.ixitxachitls.dma.data.DMAData("path"));
      character.setPassword("secret");

      EasyMock.expect(request.getMethod()).andReturn("POST");
      EasyMock.expect(request.getQueryString())
        .andReturn("username=somebody&password=secret").anyTimes();
      EasyMock.expect(request.getRequestURI()).andReturn("");
      EasyMock.expect(request.getUser()).andReturn(character);
      response.setHeader("Content-Type", "text/javascript");
      response.setHeader("Cache-Control", "max-age=0");
      response.addCookie(EasyMock.isA(Cookie.class));
      response.addCookie(EasyMock.isA(Cookie.class));
      EasyMock.expect(response.getOutputStream()).andReturn(output);

      EasyMock.replay(request, response);

      LogoutServlet servlet = new LogoutServlet();

      servlet.doPost(request, response);
      assertEquals("post", "", output.toString());

      m_logger.addExpected("WARNING: base base character 'somebody' not found");
      m_logger.addExpected("WARNING: cannot find file "
                           + "'path/Products/somebody.dma'");
      EasyMock.verify(request, response);
    }

    //......................................................................
  }

  //........................................................................
}
