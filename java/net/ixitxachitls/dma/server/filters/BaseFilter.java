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
import java.io.PrintWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// import net.ixitxachitls.dma.entries.BaseCampaign;
import net.ixitxachitls.dma.entries.BaseCharacter;
//import net.ixitxachitls.dma.entries.Campaign;
import net.ixitxachitls.dma.server.servlets.DMARequest;
import net.ixitxachitls.dma.server.servlets.LoginServlet;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The base filter for all our filters.
 *
 * @file          BaseFilter.java
 * @author        Peter Balsiger
 *
 */

//..........................................................................

//__________________________________________________________________________

public abstract class BaseFilter implements Filter
{
  //----- nested -----------------------------------------------------------

  protected static class Error
  {
    public Error(int inCode, String inMessage)
    {
      m_code = inCode;
      m_message = inMessage;
    }

    /** The error code. */
    protected int m_code;

    /** The text message of the error. */
    protected String m_message;

    public void send(HttpServletResponse inResponse) throws IOException
    {
      inResponse.sendError(m_code, m_message);
    }
  }

  protected static class HTMLError extends Error
  {
    public HTMLError(int inCode, String inTitle, String inMessage)
    {
      super(inCode, inMessage);

      m_title = inTitle;
    }

    /** The page title. */
    private String m_title;

    public void send(HttpServletResponse inResponse) throws IOException
    {
      inResponse.addHeader("Content-Type", "text/html");

      PrintWriter writer = inResponse.getWriter();

      writer.println("<html>"
                     + "<head><title>" + m_title + "</title></header>"
                     + "<body>"
                     + "<h1>" + m_title + "</h1>"
                     + m_message
                     + "</body>"
                     + "</html>");
      writer.close(); // needs to be closed for Chrome!

      inResponse.setStatus(m_code);
    }
  }

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------ BaseFilter ------------------------------

  /**
   * Create the filter.
   *
   */
  public BaseFilter()
  {
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
  public void init(@Nonnull FilterConfig inConfig)
  {
    // m_users = (BaseCampaign)inConfig.getServletContext().getAttribute("users");
    // m_campaigns =
    //   (Campaign)inConfig.getServletContext().getAttribute("campaigns");
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
  // private BaseCampaign m_users = null;

  /** The campaign containing all campaigns. */
  // private Campaign m_campaigns = null;

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
    * @undefined   IllegalArgumentException if request is null
    * @undefined   IllegalArgumentException if response is null
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
    Error error = null;

    if(!(inRequest instanceof DMARequest))
      error = new Error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "expected a dma request, but got something else");
    else
      doFilter((DMARequest)inRequest, (HttpServletResponse)inResponse, inChain);

    if(error != null)
      error.send((HttpServletResponse)inResponse);
  }

  //........................................................................
  //------------------------------- doFilter -------------------------------

  /**
    * Filter the given request.
    *
    * @param       inRequest  the request to check for
    * @param       inResponse the response that will be sent back
    * @param       inChain    the other filters in the chain
    *
    * @return      an error, if any occured
    *
    */
  protected abstract Error doFilter(@Nonnull DMARequest inRequest,
                                    @Nonnull HttpServletResponse inResponse,
                                    @Nonnull FilterChain inChain)
    throws ServletException, IOException;

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................
}
