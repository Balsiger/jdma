/******************************************************************************
 * Copyright (c) 2002,2003 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import java.io.IOException;
import java.io.PrintStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The base servlet for action calls.
 *
 * @file          ActionServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public abstract class ActionServlet extends DMAServlet
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- ActionServlet -----------------------------

  /**
   * Create the servlet for actions.
   *
   */
  public ActionServlet()
  {
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables
  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- doGet ---------------------------------

  /**
   * Handle a get requets from the client.
   *
   * @param       inRequest  the request from the client
   * @param       inResponse the response to the client
   *
   * @throws      IOException       when writing to the page fails
   * @throws      ServletException  a general problem with handling the request
   *                                happens
   */
  public void doGet(@Nonnull HttpServletRequest inRequest,
                    @Nonnull HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    Log.info("Denying " + inRequest.getMethod() + " request for "
             + inRequest.getRequestURI() + "...");

    new TextError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                  inRequest.getMethod() + " not allowed for this request")
      .send(inResponse);
  }

  //........................................................................
  //-------------------------------- handle --------------------------------

  /**
   * Really handle the request.
   *
   * @param       inRequest  the request from the client
   * @param       inResponse the response to the client
   *
   * @return      a special result if something went wrong
   *
   * @throws      IOException       when writing to the page fails
   * @throws      ServletException  a general problem with handling the request
   *                                happens
   *
   */
  protected @Nullable SpecialResult handle
    (@Nonnull DMARequest inRequest,
     @Nonnull HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    // set the content type
    inResponse.setHeader("Content-Type", "text/javascript");
    inResponse.setHeader("Cache-Control", "max-age=0");

    try
    {
      // TODO: this is actually very coarse, as it does not allow two
      // action servlets to be executed at the same time, even if they work
      // on different data. But it is easy and should cover most problems
      // at the time being. This has to be moved down to protect the real
      // data, though.
      synchronized(ActionServlet.class)
      {
        String text = doAction(inRequest, inResponse);

        PrintStream print = new PrintStream(inResponse.getOutputStream());

        print.print(text);
        print.close();
      }
    }
    catch(java.io.IOException e)
    {
      Log.warning("could not return action result: " + e);
    }

    return null;
  }

  //........................................................................
  //------------------------------ setMessage ------------------------------

  /**
    *
    * Send back a message to the client, using a cookie to show the message
    * after page reload.
    *
    * @param       inResponse the response to send to
    * @param       inMessage  the message to send back
    *
    */
  protected void setMessage(@Nonnull HttpServletResponse inResponse,
                            @Nonnull String inMessage)
  {
    Cookie cookie = new Cookie("INFO", inMessage);
    cookie.setPath("/");

    inResponse.addCookie(cookie);
  }

  //........................................................................

  //------------------------------- doAction -------------------------------

  /**
   *
   * Execute the action associated with this servlet.
   *
   * @param       inRequest  the request from the client
   * @param       inResponse the response to write to
   *
   * @return      the javascript code to send back to the client
   *
   */
  protected abstract String doAction(@Nonnull DMARequest inRequest,
                                     @Nonnull HttpServletResponse inResponse);

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //--------------------------------- fail ---------------------------------

  /**
   * Fail handling the action with the given message.
   *
   * @param       inMessage the message with which to fail
   *
   * @return      javascript to send back to the client for failure
   *
   */
  protected @Nonnull String fail(@Nonnull String inMessage)
  {
    Log.warning(inMessage);

    return "gui.alert('" + inMessage + "');";
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- get ------------------------------------------------------------

    /**
     * The get Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void get() throws Exception
    {
      HttpServletRequest request =
        EasyMock.createMock(HttpServletRequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);

      EasyMock.expect(request.getMethod()).andReturn("GET").times(2);
      response.sendError(405, "GET not allowed for this request");
      EasyMock.expect(request.getRequestURI()).andReturn("uri");
      EasyMock.replay(request, response);

      ActionServlet servlet = new ActionServlet() {
          private static final long serialVersionUID = 1L;
          protected String doAction(@Nonnull DMARequest inRequest,
                                    @Nonnull HttpServletResponse inResponse)
          {
            return "done";
          }
        };

      servlet.doGet(request, response);

      EasyMock.verify(request, response);
    }

    //......................................................................
    //----- handle ---------------------------------------------------------

    /**
     * The handle Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void handle() throws Exception
    {
      HttpServletRequest request =
        EasyMock.createMock(HttpServletRequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      net.ixitxachitls.comm.servlets.BaseServlet.Test.MockServletOutputStream
        output = new net.ixitxachitls.comm.servlets.BaseServlet.Test.
        MockServletOutputStream();

      EasyMock.expect(request.getMethod()).andReturn("POST");
      EasyMock.expect(request.getRequestURI()).andReturn("uri");
      response.setHeader("Content-Type", "text/javascript");
      response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(response.getOutputStream()).andReturn(output);
      EasyMock.replay(request, response);

      ActionServlet servlet = new ActionServlet() {
          private static final long serialVersionUID = 1L;
          protected String doAction(@Nonnull DMARequest inRequest,
                                    @Nonnull HttpServletResponse inResponse)
          {
            return "done";
          }
        };

      servlet.doPost(request, response);
      assertEquals("post", "done", output.toString());

      EasyMock.verify(request, response);
    }

    //......................................................................
    //----- setMessage -----------------------------------------------------

    /** The setMessage Test. */
    @org.junit.Test
    public void setMessage()
    {
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);

      response.addCookie(EasyMock.isA(Cookie.class));

      EasyMock.replay(response);

      ActionServlet servlet = new ActionServlet() {
          private static final long serialVersionUID = 1L;
          protected String doAction(@Nonnull DMARequest inRequest,
                                    @Nonnull HttpServletResponse inResponse)
          {
            return "done";
          }
        };

      servlet.setMessage(response, "message");

      EasyMock.verify(response);
    }

    //......................................................................
    //----- fail -----------------------------------------------------------

    /** The fail Test. */
    @org.junit.Test
    public void checkFail()
    {
      ActionServlet servlet = new ActionServlet()
        {
          private static final long serialVersionUID = 1L;
          protected String doAction(@Nonnull DMARequest inRequest,
                                    @Nonnull HttpServletResponse inResponse)
          {
            return "done";
          }
        };

      assertEquals("fail", "gui.alert('message');", servlet.fail("message"));

      m_logger.addExpected("WARNING: message");
    }

    //......................................................................
  }

  //........................................................................
}
