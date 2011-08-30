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

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.server.servlets.BaseServlet;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The base servlet for DMA specific servlets.
 *
 *
 * @file          DMAServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public abstract class DMAServlet extends BaseServlet
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ DMAServlet ------------------------------

  /**
   * Cerate the dma servlet.
   *
   */
  public DMAServlet()
  {
    // nothing to do here
  }

  //........................................................................

  //------------------------------ withAccess ------------------------------

  /**
   * Set the access level for this servlet.
   *
   * @param       inGroup the group required for accessing the servlet
   *
   * @return      this servlet for chaining
   *
   */
  public @Nonnull DMAServlet withAccess(@Nonnull BaseCharacter.Group inGroup)
  {
    m_group = inGroup;

    return this;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The group required for accessing the content of this servlet. */
  private @Nullable BaseCharacter.Group m_group;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- allows --------------------------------

  /**
   * Check for access to the page.
   *
   * @param       inRequest the request to the page
   *
   * @return      true for access, false for not
   *
   */
  protected boolean allows(@Nonnull DMARequest inRequest)
  {
    // no access restriction defined
    if(m_group == null)
      return true;

    // normal user access
    return inRequest.hasUser() && inRequest.getUser().hasAccess(m_group);
  }

  //........................................................................

  //------------------------------- getData --------------------------------

  /**
   * Get the data associated with the given request.
   *
   * @param       inPath     the path to the page
   * @param       inBaseData the available base data
   *
   * @return      the data to use
   *
   */
  public @Nullable DMAData getData(@Nonnull String inPath,
                                   @Nonnull DMAData inBaseData)
  {
    // check if we need some nested data
    String userID =
      Strings.getPattern(inPath, "^(?:/_entry|/_entries|)/user/([^/]*)/");
    System.out.println(inPath + ": " + userID);
    if(userID != null)
    {
      BaseCharacter user = inBaseData.getEntry(userID, BaseCharacter.TYPE);
      if(user != null)
        return user.getProductData();
    }

    return inBaseData;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- handle --------------------------------

  /**
   * Handle the request if it is allowed.
   *
   * @param       inRequest  the original request
   * @param       inResponse the original response
   *
   * @return      an error if something went wrong
   *
   * @throws      ServletException general error when processing the page
   * @throws      IOException      writing to the page failed
   *
   */
  protected @Nullable SpecialResult handle
    (@Nonnull HttpServletRequest inRequest,
     @Nonnull HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    if(inRequest instanceof DMARequest)
    {
      DMARequest request = (DMARequest)inRequest;

      if(allows(request))
        return handle(request, inResponse);

      Log.error("No access to page");
      return new HTMLError(HttpServletResponse.SC_FORBIDDEN,
                           "Access Denied",
                           "You don't have access to the requested page.");
    }
    else
    {
      Log.error("Invalid request, expected a DMA request!");
      return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                           "Invalid request, expected a DMA request");
    }

  }

  //........................................................................
  //-------------------------------- handle --------------------------------

  /**
   * Handle the request if it is allowed.
   *
   * @param       inRequest  the original request
   * @param       inResponse the original response
   *
   * @return      an error if something went wrong
   *
   * @throws      ServletException general error when processing the page
   * @throws      IOException      writing to the page failed
   *
   * @undefined   never
   *
   */
  protected abstract @Nullable SpecialResult handle
    (@Nonnull DMARequest inRequest,
     @Nonnull HttpServletResponse inResponse)
    throws ServletException, IOException;

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- get ------------------------------------------------------------

    /**
     * The get Test.
     * @throws Exception too lazy to catch
     */
    @org.junit.Test
    public void get() throws Exception
    {
      HttpServletRequest request =
        EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);

      EasyMock.expect(request.getMethod()).andReturn("method");
      EasyMock.expect(request.getRequestURI()).andReturn("uri");
      EasyMock.replay(request, response);

      final java.util.concurrent.atomic.AtomicBoolean handled =
        new java.util.concurrent.atomic.AtomicBoolean(false);
      DMAServlet servlet = new DMAServlet() {
          private static final long serialVersionUID = 1L;
          protected SpecialResult handle
            (@Nonnull DMARequest inRequest,
             @Nonnull HttpServletResponse inResponse)
          {
            handled.set(true);
            return null;
          }
        };

      servlet.doGet(request, response);
      assertTrue("handled", handled.get());

      EasyMock.verify(request, response);
    }

    //......................................................................
  }

  //........................................................................
}
