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

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

import net.ixitxachitls.server.ServerUtils;
import net.ixitxachitls.util.Files;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The filter to redirect paths of a given pattern to a new path.
 *
 * @file          RedirectFilter.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class PrefixRedirectFilter extends RedirectFilter
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ RedirectFilter ------------------------------

  /**
   * Create the filter.
   *
   * @param inPrefix the prefix for the redirect
   *
   */
  public PrefixRedirectFilter(@Nonnull String inPrefix)
  {
    m_prefix = inPrefix;
  }

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The prefix. */
  private @Nonnull String m_prefix;

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------- computeRedirect ----------------------------

  /**
   * Comute the path to redirect to.
   *
   * @param    inRequest the request to be redirected
   *
   * @return   the path to redirect to
   *
   */
  public @Nonnull String computeRedirect(@Nonnull HttpServletRequest inRequest)
  {
    return Files.concatenate(m_prefix, inRequest.getRequestURI());
  }

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
      RedirectFilter filter = new PrefixRedirectFilter("/prefix");

      HttpServletRequest request =
        EasyMock.createMock(HttpServletRequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      javax.servlet.FilterChain chain =
        EasyMock.createMock(javax.servlet.FilterChain.class);
      javax.servlet.FilterConfig config =
        EasyMock.createMock(javax.servlet.FilterConfig.class);
      javax.servlet.ServletContext context =
        EasyMock.createMock(javax.servlet.ServletContext.class);
      javax.servlet.RequestDispatcher dispatcher =
        EasyMock.createMock(javax.servlet.RequestDispatcher.class);

      EasyMock.expect(config.getServletContext()).andStubReturn(context);
      EasyMock.expect(context.getRequestDispatcher("/prefix/request"))
        .andStubReturn(dispatcher);
      EasyMock.expect(request.getRequestURI()).andStubReturn("request");
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
