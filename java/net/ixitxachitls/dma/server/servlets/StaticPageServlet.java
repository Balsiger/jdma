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

package net.ixitxachitls.dma.server.servlets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.ThreadSafe;

import org.easymock.EasyMock;

import net.ixitxachitls.output.html.HTMLWriter;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;
import net.ixitxachitls.util.resources.Resource;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A servlet for static HTML dma pages.
 *
 * @file          StaticPageServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ThreadSafe
public class StaticPageServlet extends PageServlet
{
  //--------------------------------------------------------- constructor(s)

  /**
   * Create the servlet.
   *
   * @param inRoot the root in the resources for the html pages to get
   *
   */
  public StaticPageServlet(@Nonnull String inRoot)
  {
    m_root = inRoot;
  }

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The root to the files to server. */
  protected @Nonnull String m_root;

  /** The error to return if a file is not found. */
  protected static final @Nonnull String s_notFoundError =
    Config.get("web/html.not.found", "<h1>File not found</h1>"
               + "The requested file could not be found on the server!");

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators

  //----------------------------- writeHeader ------------------------------

  /**
   * Write the header to the writer.
   *
   * @param     inWriter  the writer to take up the content (will be closed
   *                      by the PageServlet)
   * @param     inPath    the path of the request
   * @param     inRequest the request for the page
   *
   */
  @OverridingMethodsMustInvokeSuper
  protected void writeHeader(@Nonnull HTMLWriter inWriter,
                             @Nonnull String inPath,
                             @Nonnull DMARequest inRequest)
  {
    super.writeHeader(inWriter, inPath, inRequest);

    if(inPath != null && inPath.length() > 0)
      addNavigation(inWriter,
                    Strings.getPattern(inPath, "([^/]*?)\\.[^\\.]*?$"));
    else
      addNavigation(inWriter);
  }

  //........................................................................
  //------------------------------ writeBody -------------------------------

  /**
   * Handles the body content of the request.
   *
   * @param     inWriter  the writer to take up the content (will be closed
   *                      by the PageServlet)
   * @param     inPath    the path for the request
   * @param     inRequest the request for the page
   *
   */
  public void writeBody(@Nonnull HTMLWriter inWriter, @Nullable String inPath,
                        @Nonnull DMARequest inRequest)
  {
    super.writeBody(inWriter, inPath, inRequest);

    // check the given path for illegal relative stuff and add the root
    String path = m_root;

    if(inPath != null && inPath.length() > 1)
      path += inPath.replaceAll("\\.\\./", "/");

    if(path.endsWith("/"))
      path += "index.html";

    Log.info("serving static dma file '" + path + "'");

    if(!Resource.has(path))
    {
      inWriter.add(s_notFoundError);

      return;
    }

    Resource file = Resource.get(path);
    file.write(inWriter);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- headerFooter --------------------------------------------------

    /** The handle Test. */
    @org.junit.Test
    public void headerFooter()
    {
      java.io.ByteArrayOutputStream output =
        new java.io.ByteArrayOutputStream();
      DMARequest request = EasyMock.createMock(DMARequest.class);

      EasyMock.expect(request.getUser()).andReturn(null);

      EasyMock.replay(request);

      HTMLWriter writer = new HTMLWriter(new java.io.PrintWriter(output));

      StaticPageServlet servlet = new StaticPageServlet("/html/");

      servlet.writeHeader(writer, "/about.html", request);
      servlet.writeFooter(writer, "/about.html", request);
      writer.close();
      assertEquals("header",
                   "<HTML>\n"
                   + "  <HEAD>\n"
                   + "    <SCRIPT type=\"text/javascript\" "
                   + "src=\"/js/jquery-1.5.js\"></script>\n"
                   + "    <LINK rel=\"STYLESHEET\" type=\"text/css\" "
                   + "href=\"/css/smoothness/jquery-ui-1.8.9.custom.css\" />\n"
                   + "    <SCRIPT type=\"text/javascript\" "
                   + "src=\"/js/jquery-ui-1.8.9.custom.min.js\"></script>\n"
                   + "    <LINK rel=\"STYLESHEET\" type=\"text/css\" "
                   + "href=\"/css/jdma.css\" />\n"
                   + "    <SCRIPT type=\"text/javascript\" "
                   + "src=\"/js/jdma.js\"></script>\n"
                   + "  </HEAD>\n"
                   + "  <BODY>\n"
                   + "    <DIV id=\"header\">\n"
                   + "      <DIV id=\"header-right\">\n"
                   + "        <A id=\"login-icon\" class=\"icon\" "
                   + "title=\"Login\" onclick=\"login()\">\n"
                   + "        </A>\n"
                   + "        <A class=\"icon library\" title=\"Library\">\n"
                   + "        </A>\n"
                   + "        <A class=\"icon search\" title=\"Search\">\n"
                   + "        </A>\n"
                   + "        <A class=\"icon about\" title=\"About\" "
                   + "href=\"/about.html\">\n"
                   + "        </A>\n"
                   + "      </DIV>\n"
                   + "      <DIV id=\"header-left\">\n"
                   + "        DMA\n"
                   + "      </DIV>\n"
                   + "      <DIV id=\"navigation\">\n"
                   + "        <A id=\"home\" class=\"icon\" title=\"Home\" "
                   + "href=\"/\">\n"
                   + "        </A>\n"
                   + "         &raquo; \n"
                   + "        about\n"
                   + "      </DIV>\n"
                   + "    </DIV>\n"
                   + "  </BODY>\n"
                   + "</HTML>\n", output.toString());

      EasyMock.verify(request);
    }

    //......................................................................
    //----- body -----------------------------------------------------------

    /** The body Test. */
    @org.junit.Test
    public void body()
    {
      java.io.ByteArrayOutputStream output =
        new java.io.ByteArrayOutputStream();
      DMARequest request = EasyMock.createMock(DMARequest.class);

      EasyMock.replay(request);

      HTMLWriter writer = new HTMLWriter(new java.io.PrintWriter(output));

      StaticPageServlet servlet = new StaticPageServlet("/html/");

      servlet.writeBody(writer, "/about.html", request);
      writer.close();

      assertPattern("body", ".*<BODY>.*", output.toString());
      assertPattern("page", ".*<DIV class=\"page\">.*", output.toString());
      assertPattern("about", ".*<h1>About</h1>.*", output.toString());
      assertPattern("title", ".*<TITLE>DMA - About</TITLE>.*",
                    output.toString());

      m_logger.addExpected("WARNING: closing tag div, but was never opened");
      m_logger.addExpected("WARNING: writer closed, but tags [div] not closed");

      EasyMock.verify(request);
    }

    //......................................................................
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
