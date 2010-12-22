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

package net.ixitxachitls.comm.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

import net.ixitxachitls.util.Files;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;
import net.ixitxachitls.util.resources.Resource;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class represents a simple HTTP handler for static content. This can be
 * html files, text or images.
 *
 * @file          FileServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ThreadSafe
public class FileServlet extends BaseServlet
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- FileServlet ------------------------------

  /**
   * Create the handler for static files.
   *
   * @param       inRoot         the root directory (in classpath) for the
   *                             files to be served
   * @param       inType         the type of the files to handle (e.g. return)
   *
   */
  public FileServlet(@Nonnull String inRoot, @Nullable String inType)
  {
    this(inRoot, inType, true);
  }

  //........................................................................
  //----------------------------- FileServlet ------------------------------

  /**
   * Create the handler for static files.
   *
   * @param       inRoot         the root directory (in classpath) for the
   *                             files to be served
   * @param       inType         the type of the files to handle (e.g. return)
   * @param       inCache        true if caching is enabled, false if not
   *
   */
  public FileServlet(@Nonnull String inRoot, @Nullable String inType,
                     boolean inCache)
  {
    m_root = inRoot;
    m_type = inType;
    m_handleModification = inCache;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The root to the files to server. */
  protected @Nonnull String m_root;

  /** The type of content to return. */
  protected @Nullable String m_type;

  /** A flag denoting if checking for modification requests or not. */
  protected boolean m_handleModification;

  /** The possible type for different extensions. */
  protected static final @Nonnull Map<String, String> s_types =
    new HashMap<String, String>();

  // initialize the possible content types according to file extension
  static
  {
    s_types.put(".png", "image/png");
    s_types.put(".jpg", "image/jpeg");
    s_types.put(".pdf", "application/pdf");
    s_types.put(".css", "text/css");
    s_types.put(".js",  "text/javascript");
  }

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /** The path to use for missign images. */
  protected static final @Nonnull String s_missingImage =
    Config.get("web.image.missing", "missing.png");

  /** The time of the startup of this servlet. */
  protected static final long s_startupTime = new Date().getTime();

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- getResource ------------------------------

  /**
   * Get the resource used for serving this request.
   *
   * @param       inPath the path to the resource
   *
   * @return      the requested resource
   *
   */
  protected @Nonnull Resource getResource(String inPath)
  {
    return Resource.get(inPath);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- handle --------------------------------

  /**
   * Really handle the request.
   *
   * @param       inRequest  the request from the client
   * @param       inResponse the response to the client
   *
   * @throws      java.io.IOException writing output failed
   *
   * @return      a special result, if any
   *
   * @throws      ServletException if something went wrong
   * @throws      IOException     if writing failed
   *
   */
  protected synchronized @Nullable SpecialResult handle
    (@Nonnull HttpServletRequest inRequest,
     @Nonnull HttpServletResponse inResponse)
    throws IOException, ServletException
  {
    // check the given path for illegal relative stuff and add the root
    String path = m_root;

    String pathInfo = inRequest.getPathInfo();
    if(pathInfo != null && pathInfo.length() > 1)
      path += pathInfo.replaceAll("\\.\\./", "/");

    if("text/html".equals(m_type) && path.endsWith("/"))
      path += "index.html";

    Log.info("serving static file '" + path + "'");

    // check if we have a modified question
    if(m_handleModification)
    {
      try
      {
        long modified = inRequest.getDateHeader("If-Modified-Since");

        // miliseconds are not transmitted back to the client, thus we can't use
        // them here
        if(modified + 1000 >= s_startupTime)
          return new NotModified();
      }
      catch(IllegalArgumentException e)
      {
        // sometimes jetty can't parse the given date; in such a case we just
        // ignore it
      }
    }

    Resource resource = null;
    if(Resource.has(path))
      resource = getResource(path);
    else
      if(m_type != null && m_type.startsWith("image/"))
        resource = Resource.get(s_missingImage);

    if(resource == null)
    {
      Log.warning("Could not find file '" + path + "' on the server");
      return new HTMLError(HttpServletResponse.SC_NOT_FOUND,
                           "Not Found",
                           "The resource '" + path + "' was not found on the "
                           + "server!");
    }

    // set the content type
    if(m_type != null && m_type.length() > 0)
      inResponse.setHeader("Content-Type", m_type);
    else
    {
      // determine the content type from the given file name
      if(s_types.containsKey(Files.extension(path)))
        inResponse.setHeader("Content-Type",
                           s_types.get(Files.extension(path))
                           + "; charset=utf-8");
      else
        // We just hope the client knows what to do with the type...
        Log.warning("extension " + Files.extension(path)
                    + " is not registered for " + path);
    }

    // add the date (for caching on the client)
    if(m_handleModification)
    {
      inResponse.setHeader("Cache-Control", "max-age=" + (60 * 60 * 24));
      inResponse.setDateHeader("Last-Modified",  s_startupTime);
      inResponse.setDateHeader("Expires",  s_startupTime + 60 * 60 * 24);
    }

    // write the file to the output
    OutputStream output = inResponse.getOutputStream();
    resource.write(output);
    output.close();

    return null;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
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
      BaseServlet.Test.MockServletOutputStream output =
        new BaseServlet.Test.MockServletOutputStream();

      EasyMock.expect(request.getPathInfo())
        .andReturn("/config/test/test.config");
      EasyMock.expect(request.getDateHeader("If-Modified-Since"))
        .andReturn(0L);
      response.setHeader("Content-Type", "text/plain");
      response.setHeader("Cache-Control", "max-age=86400");
      response.setDateHeader(EasyMock.eq("Last-Modified"),
                             EasyMock.gt(new Date().getTime()));
      response.setDateHeader(EasyMock.eq("Expires"),
                             EasyMock.gt(new Date().getTime()));
      EasyMock.expect(response.getOutputStream()).andReturn(output);
      EasyMock.replay(request, response);

      FileServlet servlet = new FileServlet("", "text/plain");

      assertNull("handle", servlet.handle(request, response));
      assertPattern("content", ".*test.config=guru.*", output.toString());

      output.close();
      EasyMock.verify(request, response);
    }

    //......................................................................
    //----- handleNotModified ----------------------------------------------

    /**
     * The handleNotModified Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void handleNotModified() throws Exception
    {
      HttpServletRequest request =
        EasyMock.createMock(HttpServletRequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);

      EasyMock.expect(request.getPathInfo())
        .andReturn("/config/test/test.config");
      EasyMock.expect(request.getDateHeader("If-Modified-Since"))
        .andReturn(new Date().getTime());
      EasyMock.replay(request, response);

      FileServlet servlet = new FileServlet("", "text/plain");

      assertEquals("handle", "not-modified",
                   servlet.handle(request, response).toString());

      EasyMock.verify(request, response);
    }

    //......................................................................
    //----- handleNotChached -----------------------------------------------

    /**
     * The handleNotChached Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void handleNotChached() throws Exception
    {
      HttpServletRequest request =
        EasyMock.createMock(HttpServletRequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      BaseServlet.Test.MockServletOutputStream output =
        new BaseServlet.Test.MockServletOutputStream();

      EasyMock.expect(request.getPathInfo())
        .andReturn("/config/test/test.config");
      response.setHeader("Content-Type", "text/plain");
      EasyMock.expect(response.getOutputStream()).andReturn(output);
      EasyMock.replay(request, response);

      FileServlet servlet = new FileServlet("", "text/plain", false);

      assertNull("handle", servlet.handle(request, response));
      assertPattern("content", ".*test.config=guru.*", output.toString());

      EasyMock.verify(request, response);
    }

    //......................................................................
  }

  //........................................................................
}
