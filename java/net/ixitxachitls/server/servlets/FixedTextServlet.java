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

package net.ixitxachitls.server.servlets;

import java.io.IOException;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This handler always returns a fixed, given text to the client, independent
 * of the request made.
 *
 * @file          TextHandler.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ThreadSafe
@ParametersAreNonnullByDefault
public class FixedTextServlet extends BaseServlet
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------- FixedTextServlet ---------------------------

  /**
   * Create the fixed text servlet.
   *
   * @param       inText  the text to return (may contain html)
   * @param       inCode  the return code to return to a client
   */
  public FixedTextServlet(String inText, int inCode)
  {
    this(null, inText, inCode);
  }

  //........................................................................
  //--------------------------- FixedTextServlet ---------------------------

  /**
   * Create the fixed text servlet.
   *
   * @param       inTitle the title to return (no html)
   * @param       inText  the text to return (may contain html)
   * @param       inCode  the return code to return to a client
   */
  public FixedTextServlet(@Nullable String inTitle, String inText, int inCode)
  {
    if (inTitle == null)
      m_error = new TextError(inCode, inText);
    else
      m_error = new HTMLError(inCode, inTitle, inText);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The error with all the information. */
  private SpecialResult m_error;

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- handle ---------------------------------

  /**
   * Really handle the request.
   *
   * @param       inRequest  the request from the client
   * @param       inResponse the response to the client
   *
   * @return      an error if something went wrong
   *
   * @throws      IOException writing to page failed
   *
   */
  @Override
protected SpecialResult handle(HttpServletRequest inRequest,
                               HttpServletResponse inResponse)
    throws IOException
  {
    return m_error;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- handleText -----------------------------------------------------

    /**
     * The handle Test.
     *
     * @throws Exception should not happen */
    @org.junit.Test
    public void handleText() throws Exception
    {
      FixedTextServlet servlet = new FixedTextServlet("text", 42);

      HttpServletRequest request =
        EasyMock.createStrictMock(HttpServletRequest.class);
      HttpServletResponse response =
        EasyMock.createStrictMock(HttpServletResponse.class);

      response.sendError(42, "text");

      EasyMock.replay(request, response);

      servlet.handleAndCheck(request, response);

      EasyMock.verify(request, response);
    }

    //......................................................................
    //----- handleHTML -----------------------------------------------------

    /**
     * The handle Test.
     *
     * @throws Exception should not happen */
    @org.junit.Test
    public void handleHTML() throws Exception
    {
      FixedTextServlet servlet = new FixedTextServlet("title", "text", 42);

      try (java.io.PrintWriter writer =
        EasyMock.createStrictMock(java.io.PrintWriter.class))
      {
        HttpServletRequest request =
          EasyMock.createStrictMock(HttpServletRequest.class);
        HttpServletResponse response =
          EasyMock.createStrictMock(HttpServletResponse.class);

        response.addHeader("Content-Type", "text/html");
        EasyMock.expect(response.getWriter()).andReturn(writer);
        writer.println(EasyMock.matches("<html>.*title.*text.*</html>"));
        writer.close();
        EasyMock.expectLastCall().anyTimes();
        response.setStatus(42);

        EasyMock.replay(writer, request, response);

        servlet.handleAndCheck(request, response);

        EasyMock.verify(writer, request, response);
      }
    }

    //......................................................................
  }

  //........................................................................
}
