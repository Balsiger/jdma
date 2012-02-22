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
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import net.ixitxachitls.dma.entries.BaseCampaign;
//import net.ixitxachitls.dma.entries.BaseCharacter;
//import net.ixitxachitls.dma.entries.Campaign;
import net.ixitxachitls.dma.server.servlets.DMARequest;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The base filter for all our filters.
 *
 * @file          BaseFilter.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public abstract class BaseFilter implements Filter
{
  //----- nested -----------------------------------------------------------

  //------------------------------------------------------------------ Error

  /**
   * A simple error with a message that can be sent back to the client.
   */
  protected static class Error
  {
    /**
     * Create the error.
     *
     * @param   inCode    the error code
     * @param   inMessage the message printed for the error
     */
    public Error(int inCode, String inMessage)
    {
      m_code = inCode;
      m_message = inMessage;
    }

    /** The error code. */
    protected int m_code;

    /** The text message of the error. */
    protected @Nonnull String m_message;

    /**
     * Sent the error to the given response.
     *
     * @param inResponse the response to send to
     *
     * @throws IOException when writing fails
     */
    public void send(@Nonnull HttpServletResponse inResponse) throws IOException
    {
      inResponse.sendError(m_code, m_message);
    }
  }

  //........................................................................
  //-------------------------------------------------------------- HTMLError

  /**
   * An error class to print an html formatted error.
   */
  protected static class HTMLError extends Error
  {
    /**
     * Create the html error.
     *
     * @param   inCode    the error code of the page
     * @param   inTitle   the title of the page
     * @param   inMessage the message printed on the page
     *
     */
    public HTMLError(int inCode, @Nonnull String inTitle,
                     @Nonnull String inMessage)
    {
      super(inCode, inMessage);

      m_title = inTitle;
    }

    /** The page title. */
    private @Nonnull String m_title;

    /**
     * Sent the error to the given response.
     *
     * @param inResponse the response to send to
     *
     * @throws IOException when writing fails
     */
    @Override
    public void send(@Nonnull HttpServletResponse inResponse) throws IOException
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
  @Override
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
  @Override
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
  @Override
  public void doFilter(@Nonnull ServletRequest inRequest,
                       @Nonnull ServletResponse inResponse,
                       @Nonnull FilterChain inChain)
    throws ServletException, IOException
  {
    if(!(inResponse instanceof HttpServletResponse))
      throw new ServletException("expected http servlet repsonse");

    Error error = null;

    if(!(inRequest instanceof DMARequest))
      error = new Error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "expected a different request or response");
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
    * @throws      ServletException on an error with the servlet
    * @throws      IOException      on errors for writing the contents
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
