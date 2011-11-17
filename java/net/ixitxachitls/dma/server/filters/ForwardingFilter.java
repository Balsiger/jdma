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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
 * The filter to forward paths of a given pattern to a new path.
 *
 * @file          ForwardFilter.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class ForwardingFilter implements Filter
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------- ForwardingFilter ---------------------------

  /**
   * Create the filter.
   *
   */
  public ForwardingFilter()
  {
    // nothing to do
  }

  //........................................................................

  //--------------------------------- init ---------------------------------

  /**
   * Initialize the filter.
   *
   * @param       inConfig the filter configuration
   *
   */
  public void init(@Nonnull FilterConfig inConfig)
  {
    m_config = inConfig;

    for(Enumeration<String> params = m_config.getInitParameterNames();
        params.hasMoreElements(); )
    {
      String name = params.nextElement();
      m_mappings.put(name, m_config.getInitParameter(name));
    }
  }

  //........................................................................
  //------------------------------- destroy --------------------------------

  /**
   * Destroy the filter.
   *
   */
  public void destroy()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The filter configuration. */
  private @Nonnull FilterConfig m_config;

  /** The filter mappings. */
  private @Nonnull Map<String, String> m_mappings =
    new HashMap<String, String>();

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
  public void doFilter(@Nonnull ServletRequest inRequest,
                       @Nonnull ServletResponse inResponse,
                       @Nonnull FilterChain inChain)
    throws ServletException, IOException
  {
    if(!(inRequest instanceof HttpServletRequest))
    {
      inChain.doFilter(inRequest, inResponse);
      return;
    }

    String forward = computeForward((HttpServletRequest)inRequest);
    if(forward == null)
      inChain.doFilter(inRequest, inResponse);
    else
    {
      Log.debug("forwarding request to " + forward);

      m_config.getServletContext().getRequestDispatcher(forward)
        .forward(inRequest, inResponse);
    }
  }

  //........................................................................

  //--------------------------- computeForward ----------------------------

  /**
   * Comute the path to forward to.
   *
   * @param    inRequest the request to be forwarded
   *
   * @return   the path to forward to
   *
   */
  public @Nullable String computeForward(@Nonnull HttpServletRequest inRequest)
  {
    String uri = inRequest.getRequestURI();
    for(Map.Entry<String, String> entry : m_mappings.entrySet())
    {
      String forward = uri.replaceAll(entry.getKey(), entry.getValue());
      if(forward != uri)
        return forward;
    }

    return null;
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
      ForwardingFilter filter = new ForwardingFilter();
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
      java.util.Vector<String> names = new java.util.Vector<String>();
      names.add("/guru/(.*)-hello");
      names.add("/guru/(.*).pdf");
      EasyMock.expect(config.getInitParameterNames())
        .andStubReturn(names.elements());
      EasyMock.expect(config.getInitParameter("/guru/(.*)-hello"))
        .andStubReturn("/something/$1");
      EasyMock.expect(config.getInitParameter("/guru/(.*).pdf"))
        .andStubReturn("/pdf/$1");
      EasyMock.expect(request.getRequestURI())
        .andReturn("/guru/something.there");
      EasyMock.expect(request.getRequestURI())
        .andReturn("/guru/special-hello");
      EasyMock.expect(context.getRequestDispatcher("/something/special"))
        .andReturn(dispatcher);
      dispatcher.forward(request, response);
      EasyMock.expect(request.getRequestURI())
        .andReturn("/guru/file.pdf");
      EasyMock.expect(context.getRequestDispatcher("/pdf/file"))
        .andReturn(dispatcher);
      dispatcher.forward(request, response);

      EasyMock.replay(request, response, config, context, dispatcher);

      filter.init(config);
      filter.doFilter(request, response, chain);
      filter.doFilter(request, response, chain);
      filter.doFilter(request, response, chain);

      EasyMock.verify(request, response, config, context, dispatcher);
    }

    //......................................................................
  }

  //...........................................................................
}
