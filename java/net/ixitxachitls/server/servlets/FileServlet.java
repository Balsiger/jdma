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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Multimap;

import org.easymock.EasyMock;

import net.ixitxachitls.server.ServerUtils;
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
  //----------------------------------------------------------------- nested

  //----- Type -------------------------------------------------------------

  /**
   * The type of a file.
   */
  private static class Type implements Serializable
  {
    /**
     * Create a file type.
     *
     * @param inExtension the file extension
     * @param inMimeType  the mime type
     *
     */
    public Type(@Nonnull String inExtension, @Nonnull String inMimeType)
    {
      m_extension = inExtension;
      m_mimeType = inMimeType;
    }

    /**
     * Create a file type for an image.
     *
     * @param inExtension the file extension
     * @param inMimeType  the mime type
     * @param inImageFormat the image format for images
     *
     */
    public Type(@Nonnull String inExtension, @Nonnull String inMimeType,
                @Nonnull String inImageFormat)
    {
      this(inExtension, inMimeType);

      m_imageFormat = inImageFormat;
    }

    /** The extension of the file. */
    private @Nonnull String m_extension;

    /** The mime type of the file. */
    private @Nonnull String m_mimeType;

    /** The image format, if any. */
    private @Nullable String m_imageFormat;

    /** The id for serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Get the extension of the file.
     *
     * @return the file extension
     */
    public @Nonnull String getExtension()
    {
      return m_extension;
    }

    /**
     * Get the mim type of the file.
     *
     * @return the file's mime type
     */
    public @Nonnull String getMimeType()
    {
      return m_mimeType;
    }

    /**
     * Get the format of the image or null if not an image.
     *
     * @return the format of the image or null
     */
    public @Nullable String getImageFormat()
    {
      return m_imageFormat;
    }

    /**
     * Check wether the file represents an image.
     *
     * @return true if the file is an image, false if not
     */
    public boolean isImage()
    {
      return m_imageFormat != null;
    }
  }

  //........................................................................

  //........................................................................

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
    m_handleModification = inCache;

    if(inType != null)
      for(Type type : s_types.values())
        if(inType.equals(type.getMimeType()))
          m_type = type;
  }

  //........................................................................

  //----------------------------- FileServlet ------------------------------

  /**
   * Default constructor.
   */
  public FileServlet()
  {
    m_root = "/";
    m_handleModification = true;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The root to the files to server. */
  protected @Nonnull String m_root;

  /** The type of content to return. */
  protected @Nullable Type m_type;

  /** A flag denoting if checking for modification requests or not. */
  protected boolean m_handleModification;

  /** The possible type for different extensions. */
  protected static final @Nonnull Map<String, Type> s_types =
    new HashMap<String, Type>();

  // initialize the possible content types according to file extension
  static
  {
    s_types.put(".png", new Type(".png", "image/png", "png"));
    s_types.put(".jpg", new Type(".jpg", "image/jpeg", "jpg"));
    s_types.put(".gif", new Type(".gif", "image/gif", "gif"));
    s_types.put(".pdf", new Type(".pdf", "application/pdf"));
    s_types.put(".css", new Type(".css", "text/css"));
    s_types.put(".js",  new Type(".js", "text/javascript"));
    s_types.put(".config", new Type(".config", "text/plain"));
    s_types.put(".config", new Type(".template", "text/plain"));
  }

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /** The path to use for missign images. */
  protected static final @Nonnull String s_missingImage =
    Config.get("web/image.missing", "/icons/missing.png");

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

    if(m_type != null && "text/html".equals(m_type.getMimeType())
       && path.endsWith("/"))
      path += "index.html";

    Log.info("serving static file '" + path + "'");

    // check if we have a modified question
    if(m_handleModification)
    {
      try
      {
        long modified = inRequest.getDateHeader("If-Modified-Since");

        // miliseconds are not transmitted back to the client, thus we can't
        // use them here
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
      if(m_type != null && m_type.isImage())
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
    if(m_type != null)
      inResponse.setHeader("Content-Type", m_type.getMimeType());
    else
    {
      // determine the content type from the given file name
      m_type = s_types.get(Files.extension(path));
      if(m_type != null)
        inResponse.setHeader("Content-Type",
                             m_type.getMimeType() + "; charset=utf-8");
      else
      {
        // We just hope the client knows what to do with the type...
        Log.warning("extension " + Files.extension(path)
                    + " is not registered for " + path);
      }
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

    // check if we are dealiong with an image and we have to scale it
    if(m_type != null && m_type.isImage())
    {
      Multimap<String, String> params = ServerUtils.extractParams(inRequest);
      int width = -1;
      int height = -1;

      if(params.containsKey("h"))
        height = Integer.parseInt(params.get("h").iterator().next());

      if(params.containsKey("w"))
        width = Integer.parseInt(params.get("w").iterator().next());

      if(width > 0 || height > 0)
      {
        InputStream imageStream =
          new BufferedInputStream(resource.getInput());
        Image image = ImageIO.read(imageStream)
          .getScaledInstance(width, height, Image.SCALE_SMOOTH);

        BufferedImage out =
          new BufferedImage(image.getWidth(null), image.getHeight(null),
                            BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = out.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        ImageIO.write(out, m_type.getImageFormat(), output);

        output.close();
        return null;
      }
    }

    resource.write(output);
    output.close();

    return null;
  }

  //........................................................................

  //----- init -------------------------------------------------------------

  /**
   * Initialize the servlet.
   *
   * @param inConfig the intial configuration (from web.xml)
   *
   */
  public void init(@Nonnull ServletConfig inConfig)
  {
    // root
    String param = inConfig.getInitParameter("root");
    if(param != null)
      m_root = param;

    // type
    param = inConfig.getInitParameter("type");
    if(param != null)
      for(Type type : s_types.values())
        if(param.equals(type.getMimeType()))
          m_type = type;

    // cache
    param = inConfig.getInitParameter("cache");
    if(param != null)
      m_handleModification = !"false".equals(param);
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
        .andReturn("/resources/dma/BaseCharacters/Ixitxachitls.dma");
      EasyMock.expect(request.getDateHeader("If-Modified-Since"))
        .andReturn(0L);
      response.setHeader("Cache-Control", "max-age=86400");
      response.setDateHeader(EasyMock.eq("Last-Modified"),
                             EasyMock.gt(new Date().getTime()));
      response.setDateHeader(EasyMock.eq("Expires"),
                             EasyMock.gt(new Date().getTime()));
      response.setHeader("Content-Type", "text/plain");
      EasyMock.expect(response.getOutputStream()).andReturn(output);
      EasyMock.replay(request, response);

      FileServlet servlet = new FileServlet("", "text/plain");

      assertNull("handle", servlet.handle(request, response));
      assertPattern("content", ".*base character Merlin.*", output.toString());

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
        .andReturn("/resources/dma/BaseCharacters/Ixitxachitls.dma");
      response.setHeader("Content-Type", "text/plain");
      EasyMock.expect(response.getOutputStream()).andReturn(output);
      EasyMock.replay(request, response);

      FileServlet servlet = new FileServlet("", "text/plain", false);

      assertNull("handle", servlet.handle(request, response));
      assertPattern("content", ".*base character Merlin.*", output.toString());

      EasyMock.verify(request, response);
    }

    //......................................................................
  }

  //........................................................................
}
