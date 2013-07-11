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

package net.ixitxachitls.dma.server.filters;

import java.io.IOException;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

import net.ixitxachitls.server.ServerUtils;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The filter to redirect paths of a given pattern to a new path.
 *
 * @file          RedirectFilter.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public abstract class RedirectFilter implements Filter
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ RedirectFilter ------------------------------

  /**
   * Create the filter.
   */
  protected RedirectFilter()
  {
    // nothing to do
  }

  //........................................................................

  //--------------------------------- init ---------------------------------

  /**
   * Initialize the filter.
   *
   * @param       inConfig the filter configuration
   */
  @Override
  public void init(FilterConfig inConfig)
  {
    m_config = inConfig;
  }

  //........................................................................
  //------------------------------- destroy --------------------------------

  /**
   * Destroy the filter.
   *
   */
  @Override
  public void destroy()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The filter configuration. */
  private @Nullable FilterConfig m_config;

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- doFilter -------------------------------

  /**
    * Filter the given request.
    *
    * @param       inRequest  the request to check for
    * @param       inResponse the response that will be sent back
    * @param       inChain    the other filters in the chain
    *
    * @throws      IOException      writing to page failed
    * @throws      ServletException general failure when creating response
    *
    */
  @Override
  public void doFilter(ServletRequest inRequest, ServletResponse inResponse,
                       FilterChain inChain)
    throws ServletException, IOException
  {
    if(!(inRequest instanceof HttpServletRequest))
    {
      inChain.doFilter(inRequest, inResponse);
      return;
    }

    String forward = computeRedirect((HttpServletRequest)inRequest);
    Log.debug("forwarding request to " + forward);

    m_config.getServletContext().getRequestDispatcher(forward)
      .forward(inRequest, inResponse);
  }

  //........................................................................

  //--------------------------- computeRedirect ----------------------------

  /**
   * Comute the path to redirect to.
   *
   * @param    inRequest the request to be redirected
   *
   * @return   the path to redirect to
   *
   */
  public abstract String computeRedirect(HttpServletRequest inRequest);

  //........................................................................


  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends ServerUtils.Test
  {
    //----- filter ---------------------------------------------------------

    /**
     * The filter Test.
     *
     * @throws Exception should not happen
     *
     */
    @org.junit.Test
    public void filter() throws Exception
    {
      RedirectFilter filter = new RedirectFilter() {
        @Override
        public String
            computeRedirect(HttpServletRequest inRequest)
          {
            return "redirect";
          }
        };

      HttpServletRequest request =
        EasyMock.createMock(HttpServletRequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      FilterChain chain = EasyMock.createMock(FilterChain.class);
      FilterConfig config = EasyMock.createMock(FilterConfig.class);
      javax.servlet.ServletContext context =
        EasyMock.createMock(javax.servlet.ServletContext.class);
      javax.servlet.RequestDispatcher dispatcher =
        EasyMock.createMock(javax.servlet.RequestDispatcher.class);

      EasyMock.expect(config.getServletContext()).andStubReturn(context);
      EasyMock.expect(context.getRequestDispatcher("redirect"))
        .andStubReturn(dispatcher);
      dispatcher.forward(request, response);

      EasyMock.replay(request, response, config, context, dispatcher);

      filter.init(config);
      filter.doFilter(request, response, chain);

      EasyMock.verify(request, response, config, context, dispatcher);
    }

    //......................................................................
  }

  //...........................................................................
}
