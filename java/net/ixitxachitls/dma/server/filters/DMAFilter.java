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

package net.ixitxachitls.dma.server.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.server.servlets.DMARequest;
import net.ixitxachitls.server.ServerUtils;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The filter to turn a given request into a DMA request (with additional
 * information).
 *
 * @file          DMAFilter.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class DMAFilter implements Filter
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ DMAFilter ------------------------------

  /**
   * Create the filter.
   *
   * @param inUsers all available users
   *
   */
  public DMAFilter(Map<String, BaseCharacter> inUsers)
  {
    m_users = inUsers;
  }

  //........................................................................

  //--------------------------------- init ---------------------------------

  /**
    *
    * Initialize the filter.
    *
    * @param       inConfig the filter configuration
    *
    */
  public void init(FilterConfig inConfig)
  {
    // nothing to do
  }

  //........................................................................
  //------------------------------- destroy --------------------------------

  /**
    *
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

  /** The base campaign with all the users. */
  private Map<String, BaseCharacter> m_users = null;

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
    ServletRequest request = inRequest;
    if(!(inRequest instanceof DMARequest)
       && inRequest instanceof HttpServletRequest)
      request = new DMARequest
        ((HttpServletRequest)inRequest,
         ServerUtils.extractParams((HttpServletRequest)inRequest), m_users);

    inChain.doFilter(request, inResponse);
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
      Map<String, BaseCharacter> users = new HashMap<String, BaseCharacter>();
      DMAFilter filter = new DMAFilter(users);

      javax.servlet.ServletInputStream inputStream =
        new MockServletInputStream("");

      HttpServletRequest request =
        EasyMock.createMock(HttpServletRequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      FilterChain chain = EasyMock.createMock(FilterChain.class);

      EasyMock.expect(request.getInputStream()).andReturn(inputStream);
      EasyMock.expect(request.getQueryString()).andReturn("").times(2);
      EasyMock.expect(request.getCookies()).andReturn
        (new javax.servlet.http.Cookie [0]);

      EasyMock.replay(request, response);

      filter.doFilter(request, response, chain);

      EasyMock.verify(request, response);
    }

    //......................................................................
  }

  //...........................................................................
}
