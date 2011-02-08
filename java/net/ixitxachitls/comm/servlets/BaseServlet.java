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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import org.easymock.EasyMock;

import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base for all servlets used in the DMA web server.
 *
 * @file          BaseServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public abstract class BaseServlet extends HttpServlet
{
  //----------------------------------------------------------------- nested

  //----- SpecialResult ----------------------------------------------------

  /** The interface for special return values. */
  public interface SpecialResult
  {
    /**
     * Send the error to the given response.
     *
     * @param inResponse the response to send back to.
     *
     * @throws IOException if something does wrong
     *
     */
    public void send(@Nonnull HttpServletResponse inResponse)
      throws IOException;

    /**
     * Convert to a string for debugging.
     *
     * @return the string representation
     *
     */
    public @Nonnull String toString();
  }

  //........................................................................
  //----- TextError --------------------------------------------------------

  /** A class for a handling error. */
  @Immutable
  public static class TextError implements SpecialResult, Serializable
  {
    /**
     * Create the error.
     *
     * @param inCode     the error code for the error
     * @param inMessage  the error message
     *
     */
    public TextError(int inCode, @Nonnull String inMessage)
    {
      m_code = inCode;
      m_message = inMessage;
    }

    /** The error code. */
    protected int m_code;

    /** The error message. */
    protected @Nonnull String m_message;

    /** The id for serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Send the error to the given response.
     *
     * @param inResponse the response to send back to.
     *
     * @throws IOException if something does wrong
     *
     */
    public void send(@Nonnull HttpServletResponse inResponse)
      throws IOException
    {
      inResponse.sendError(m_code, m_message);
    }

    /**
     * Convert to a string for debugging.
     *
     * @return the string representation
     *
     */
    public @Nonnull String toString()
    {
      return m_code + ": " + m_message;
    }
  }

  //........................................................................
  //----- HTMLError --------------------------------------------------

  /** A class for a handling error in html. */
  @Immutable
  public static class HTMLError extends TextError
  {
    /**
     * Create the error.
     *
     * @param inCode    the error code
     * @param inTitle   the errro title
     * @param inMessage the error message
     *
     */
    public HTMLError(int inCode, @Nonnull String inTitle,
                     @Nonnull String inMessage)
    {
      super(inCode, inMessage);

      m_title = inTitle;
    }

    /** The title for the error. */
    protected @Nonnull String m_title;

    /** The id for serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Send the error to the given response.
     *
     * @param inResponse the response to send back to.
     *
     * @throws IOException if something does wrong
     *
     */
    public void send(@Nonnull HttpServletResponse inResponse)
      throws IOException
    {
      inResponse.addHeader("Content-Type", "text/html");

      PrintWriter writer = inResponse.getWriter();

      writer.println("<html>"
                     + "<head><title>Error: " + m_title + "</title></head>"
                     + "<body style=\"margin: 0\">"
                     + "<h1 style=\"padding: 10px; background-color: #880000; "
                     + "color: white; width: 100%; "
                     + "border-bottom: 1px solid #FF0000\"; margin: 0>"
                     + m_title + "</h1>"
                     + "<div style=\"padding: 10px\">" + m_message + "</div>"
                     + "</body>"
                     + "</html>");

      writer.close(); // needs to be closed for Chrome!

      inResponse.setStatus(m_code);
    }

    /**
     * Convert to a string for debugging.
     *
     * @return the string representation
     *
     */
    public @Nonnull String toString()
    {
      return m_code + ": " + m_title + "(" + m_message + ")";
    }
  }

  //........................................................................
  //----- NotModified ------------------------------------------------------

  /** Special result for not modified pages. */
  @Immutable
  public static class NotModified implements SpecialResult, Serializable
  {
    /** Create the special result. */
    public NotModified()
    {
      // nothing to do
    }

    /**
     * Send the error to the given response.
     *
     * @param inResponse the response to send back to.
     *
     * @throws IOException if something does wrong
     *
     */
    public void send(@Nonnull HttpServletResponse inResponse)
      throws IOException
    {
      inResponse.sendError(HttpServletResponse.SC_NOT_MODIFIED, "");
    }

    /** The id for serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Convert to a string for debugging.
     *
     * @return the string representation
     *
     */
    public @Nonnull String toString()
    {
      return "not-modified";
    }
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //---------------------------- BaseServlet -------------------------------

  /**
   * Create the base handler.
   *
   */
  public BaseServlet()
  {
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The data format for http dates. */
  protected static final SimpleDateFormat s_httpTime =
    new SimpleDateFormat("EEE',' dd MMM yyyy HH:mm:ss 'GMT'");

  static
  {
    // set the timezone of the format to greenwich mean time
    s_httpTime.setTimeZone(new SimpleTimeZone(0, "GMT"));
  }

  /** The data of the startup. */
  protected static final Date s_startupDate = new Date();

  /** The startup time of the server. */
  protected static final String s_startup = s_httpTime.format(s_startupDate);

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- doGet ---------------------------------

  /**
   * Handle a get requets from the client.
   *
   * @param       inRequest  the request from the client
   * @param       inResponse the response to the client
   *
   * @throws      ServletException general error when processing the page
   * @throws      IOException      writing to the page failed
   *
   */
  public void doGet(@Nonnull HttpServletRequest inRequest,
                    @Nonnull HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    Log.debug("Handling " + inRequest.getMethod() + " request for "
              + inRequest.getRequestURI() + "...");

    handleAndCheck(inRequest, inResponse);

    Log.debug("...handled");
  }

  //........................................................................
  //-------------------------------- doPost --------------------------------

  /**
   * Handle a post requets from the client.
   *
   * @param       inRequest  the request from the client
   * @param       inResponse the response to the client
   *
   * @throws      ServletException general error when processing the page
   * @throws      IOException      writing to the page failed
   *
   */
  public void doPost(@Nonnull HttpServletRequest inRequest,
                     @Nonnull HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    Log.debug("Handling " + inRequest.getMethod() + " request for "
              + inRequest.getRequestURI() + "...");

    handleAndCheck(inRequest, inResponse);

    Log.debug("...handled");
  }

  //........................................................................

  //---------------------------- handleAndCheck ----------------------------

  /**
   * Handle the request and check for errors.
   *
   * @param       inRequest  the original request
   * @param       inResponse the original response
   *
   * @throws      ServletException general error when processing the page
   * @throws      IOException writing to page failed
   *
   */
  public void handleAndCheck(@Nonnull HttpServletRequest inRequest,
                             @Nonnull HttpServletResponse inResponse)
    throws IOException, ServletException
  {
    SpecialResult result = handle(inRequest, inResponse);

    if(result != null)
      result.send(inResponse);
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
    (@Nonnull HttpServletRequest inRequest,
     @Nonnull HttpServletResponse inResponse)
    throws ServletException, IOException;

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //--------------------------- extractParams ------------------------------

  /**
   * Extract all the parameters from the request and return them.
   * This extraction is a bit limited, as it does not allow specifying multiple
   * values for a single key.
   *
   * @param       inRequest the request from the client
   *
   * @return      A multi map with keys of the parameter names and values
   *              containing the parameter values.
   *
   */
  protected static Multimap<String, String>
    extractParams(@Nonnull HttpServletRequest inRequest)
  {
    Multimap<String, String> values = HashMultimap.create();

    BufferedReader reader = null;
    try
    {
      reader =
        new BufferedReader(new InputStreamReader(inRequest.getInputStream()));

      // parse all the key values pairs
      for(String line = reader.readLine(); line != null;
          line = reader.readLine())
      {
        String []matches = line.split("=", 2);

        if(matches.length != 2)
          Log.warning("invalid line of post request ignored: " + line);
        else
          values.put(matches[0], URLDecoder.decode(matches[1], "utf-8"));
      }
    }
    catch(java.io.IOException e)
    {
      Log.warning("Could not extract post parameters!");
    }
    finally
    {
      try
      {
        if(reader != null)
          reader.close();
      }
      catch(java.io.IOException e)
      {
        Log.warning("Could not close stream of post parameters");
      }
    }

    try
    {
      if(inRequest.getQueryString() != null)
        for(String param : inRequest.getQueryString().split("&"))
        {
          String []parts = param.split("=");

          String key   = param;
          String value = "";

          if(parts != null && parts.length == 2)
          {
            key   = parts[0];
            value = parts[1];
          }

          values.put(key, URLDecoder.decode(value, "utf-8"));
        }
    }
    catch(java.io.UnsupportedEncodingException e)
    {
      Log.error("Unsupported encoding for parsing get paramters!");
    }

    return values;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //--------------------------------------------------------------- nested

    /** A simple mock servlet stream implementation. */
    public static class MockServletInputStream extends ServletInputStream
    {
      /**
       * Create a mock input stream.
       *
       * @param inContents The contents the stream will return.
       *
       */
      public MockServletInputStream(@Nonnull String inContents)
      {
        m_contents = new java.io.ByteArrayInputStream(inContents.getBytes());
      }

      /** The contents of the stream. */
      private @Nonnull java.io.ByteArrayInputStream m_contents;

      /**
       * Read a character from the stream.
       *
       * @return The character read.
       *
       */
      public int read()
      {
        return m_contents.read();
      }
    }

    /** A simple mock servlet output stream implementation. */
    public static class MockServletOutputStream extends ServletOutputStream
    {
      /**
       * Create a mock output stream.
       */
      public MockServletOutputStream()
      {
      }

      /** The text printed. */
      private @Nonnull java.io.ByteArrayOutputStream m_contents =
        new java.io.ByteArrayOutputStream();

      /**
       * Wrie a character to the string.
       *
       * @param inCharacter the character to write
       *
       */
      public void write(int inCharacter)
      {
        m_contents.write(inCharacter);
      }

      /**
       * Get the contents of the stream.
       *
       * @return the contents printed so far.
       *
       */
      public String toString()
      {
        return m_contents.toString();
      }
    }

    //......................................................................

    //----- params ---------------------------------------------------------

    /**
     * The params Test.
     * @throws Exception to lazy to catch
     */
    @org.junit.Test
    public void params() throws Exception
    {
      HttpServletRequest request =
        EasyMock.createMock(HttpServletRequest.class);
      javax.servlet.ServletInputStream inputStream =
        new MockServletInputStream("post_1=val_3\npost_2=\n"
                                   + "post_3=val_4\npost_3=val_4a\n"
                                   + "both=val_5a");

      EasyMock.expect(request.getInputStream()).andReturn(inputStream);
      EasyMock.expect(request.getQueryString())
        .andReturn("url_1=val_1&url_2&url_3=val_2&url_3=val_2a"
                   + "&both=val_5b&both=val_5c").times(2);

      EasyMock.replay(request);

      Multimap<String, String> params = BaseServlet.extractParams(request);
      assertContent("url_1", params.get("url_1"), "val_1");
      assertContent("url_2", params.get("url_2"), "");
      assertContentAnyOrder("url_3", params.get("url_3"), "val_2", "val_2a");
      assertContent("post_1", params.get("post_1"), "val_3");
      assertContent("post_2", params.get("post_2"), "");
      assertContentAnyOrder("post_3", params.get("post_3"), "val_4", "val_4a");
      assertContentAnyOrder("both", params.get("both"), "val_5a", "val_5b",
                            "val_5c");

      EasyMock.verify(request);
    }

    //......................................................................
    //----- returnNotModified ----------------------------------------------

    /**
     * The returns Test.
     * @throws Exception to lazy to catch
     */
    @org.junit.Test
    public void returnNotModified() throws Exception
    {
      HttpServletRequest request =
        EasyMock.createMock(HttpServletRequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);

      response.sendError(HttpServletResponse.SC_NOT_MODIFIED, "");

      EasyMock.replay(request, response);

      BaseServlet servlet = new BaseServlet() {
          private static final long serialVersionUID = 1L;
          protected SpecialResult handle
            (@Nonnull HttpServletRequest inRequest,
             @Nonnull HttpServletResponse inResponse)
          {
            return new NotModified();
          }
        };

      servlet.handleAndCheck(request, response);

      EasyMock.verify(request, response);
    }

    //......................................................................
    //----- returnHTMLError ------------------------------------------------

    /**
     * The returnHTMLError Test.
     * @throws Exception to lazy to catch
     */
    @org.junit.Test
    public void returnHTMLError() throws Exception
    {
      HttpServletRequest request =
        EasyMock.createMock(HttpServletRequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      java.io.StringWriter strWriter = new java.io.StringWriter();
      PrintWriter writer = new PrintWriter(strWriter);

      response.addHeader("Content-Type", "text/html");
      EasyMock.expect(response.getWriter()).andReturn(writer);
      response.setStatus(200);

      EasyMock.replay(request, response);

      BaseServlet servlet = new BaseServlet() {
          private static final long serialVersionUID = 1L;
          protected SpecialResult handle
            (@Nonnull HttpServletRequest inRequest,
             @Nonnull HttpServletResponse inResponse)
          {
            return new HTMLError(200, "title", "message");
          }
        };

      servlet.handleAndCheck(request, response);

      assertPattern("html", "<html>.*</html>\\s*", strWriter.toString());
      assertPattern("head", ".*<head>.*</head>.*", strWriter.toString());
      assertPattern("body", ".*<body.*?>.*</body>.*", strWriter.toString());
      assertPattern("title", ".*<title>Error: title</title>.*",
                    strWriter.toString());
      assertPattern("title (h1)", ".*<h1.*?>title</h1>.*",
                    strWriter.toString());
      assertPattern("message", ".*message.*", strWriter.toString());

      EasyMock.verify(request, response);
    }

    //......................................................................
    //----- returnError ----------------------------------------------------

    /**
     * The returnError Test.
     * @throws Exception to lazy to catch
     */
    @org.junit.Test
    public void returnError() throws Exception
    {
      HttpServletRequest request =
        EasyMock.createMock(HttpServletRequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);

      response.sendError(123, "message");
      EasyMock.replay(request, response);

      BaseServlet servlet = new BaseServlet() {
          private static final long serialVersionUID = 1L;
          protected SpecialResult handle
            (@Nonnull HttpServletRequest inRequest,
             @Nonnull HttpServletResponse inResponse)
          {
            return new TextError(123, "message");
          }
        };

      servlet.handleAndCheck(request, response);

      EasyMock.verify(request, response);
    }

    //......................................................................
    //----- get ------------------------------------------------------------

    /**
     * The get Test.
     * @throws Exception to lazy to catch
     */
    @org.junit.Test
    public void get() throws Exception
    {
      HttpServletRequest request =
        EasyMock.createMock(HttpServletRequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);

      EasyMock.expect(request.getMethod()).andReturn("method");
      EasyMock.expect(request.getRequestURI()).andReturn("uri");
      EasyMock.replay(request, response);

      final java.util.concurrent.atomic.AtomicBoolean handled =
        new java.util.concurrent.atomic.AtomicBoolean(false);
      BaseServlet servlet = new BaseServlet() {
          private static final long serialVersionUID = 1L;
          protected SpecialResult handle
            (@Nonnull HttpServletRequest inRequest,
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
