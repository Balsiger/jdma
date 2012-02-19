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

package net.ixitxachitls.dma.server.servlets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.ServletConfig;

import org.easymock.EasyMock;

import net.ixitxachitls.output.html.HTMLBodyWriter;
import net.ixitxachitls.output.html.HTMLWriter;
import net.ixitxachitls.util.Strings;
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

  //-------------------------- StaticPageServlet ---------------------------

  /**
   * Default constructor.
   */
  public StaticPageServlet()
  {
    this("/html");
  }

  //........................................................................
  //-------------------------- StaticPageServlet ---------------------------

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

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The root to the files to server. */
  protected @Nonnull String m_root;

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators

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
  @OverridingMethodsMustInvokeSuper
  public void writeBody(@Nonnull HTMLWriter inWriter,
                        @Nullable String inPath,
                        @Nonnull DMARequest inRequest)
  {
    super.writeBody(inWriter, inPath, inRequest);

    if(inPath != null && inPath.length() > 0)
      addNavigation(inWriter,
                    Strings.getPattern(inPath, "([^/]*?)\\.[^\\.]*?$"));
    else
      addNavigation(inWriter);

    // check the given path for illegal relative stuff and add the root
    String path = m_root;

    if(inPath != null && inPath.length() > 1)
      path += inPath.replaceAll("\\.\\./", "/");

    if(path.endsWith("/"))
      path += "index.html";

    Log.info("serving static dma file '" + path + "'");

    if(!Resource.has(path))
    {
      inWriter
        .title("Not Found")
        .begin("h1").add("Could not find file '" + path + "'").end("h1")
        .add("The requested file could not be found on the server!");

      return;
    }

    Resource file = Resource.get(path);
    file.write(inWriter);
  }

  //........................................................................
  //--------------------------------- init ---------------------------------

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
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.server.ServerUtils.Test
  {
    //----- headerFooter --------------------------------------------------

    /** The handle Test. */
    @org.junit.Test
    public void headerFooter()
    {
      m_localServiceTestHelper.setEnvIsLoggedIn(false);
      java.io.ByteArrayOutputStream output =
        new java.io.ByteArrayOutputStream();
      DMARequest request = EasyMock.createMock(DMARequest.class);

      EasyMock.expect(request.getUser()).andReturn(null);
      EasyMock.expect(request.getOriginalPath()).andReturn("index.html");

      EasyMock.replay(request);

      HTMLWriter writer = new HTMLWriter(new java.io.PrintWriter(output));

      StaticPageServlet servlet = new StaticPageServlet("/html/");

      servlet.writeHeader(writer, "/about.html", request);
      servlet.writeFooter(writer, "/about.html", request);
      writer.close();
      assertEquals("header",
                   "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                   + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN"
                   + "\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
                   + "\">\n"
                   + "<HTML xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                   + "  <HEAD>\n"
                   + "    <SCRIPT type=\"text/javascript\" "
                   + "src=\"/js/jquery-1.5.1.min.js\"></SCRIPT>\n"
                   + "    <LINK rel=\"STYLESHEET\" type=\"text/css\" "
                   + "href=\"/css/smoothness/jquery-ui-1.8.14.custom.css\" />\n"
                   + "    <SCRIPT type=\"text/javascript\" "
                   + "src=\"/js/jquery-ui-1.8.14.custom.min.js\"></SCRIPT>\n"
                   + "    <SCRIPT type=\"text/javascript\" "
                   + "src=\"/js/util.js\"></SCRIPT>\n"
                   + "    <SCRIPT type=\"text/javascript\" src=\"/js/form.js\">"
                   + "</SCRIPT>\n"
                   + "    <LINK rel=\"STYLESHEET\" type=\"text/css\" "
                   + "href=\"/css/gui.css\" />\n"
                   + "    <SCRIPT type=\"text/javascript\" src=\"/js/gui.js\">"
                   + "</SCRIPT>\n"
                   + "    <SCRIPT type=\"text/javascript\" src=\"/js/edit.js\">"
                   + "</SCRIPT>\n"
                   + "    <LINK rel=\"STYLESHEET\" type=\"text/css\" "
                   + "href=\"/css/jdma.css\" />\n"
                   + "    <SCRIPT type=\"text/javascript\" "
                   + "src=\"/js/jdma.js\"></SCRIPT>\n"
                   + "    <META name=\"viewport\" "
                   + "content=\"width=device-width, height=device-height\"/>\n"
                   + "    <META name=\"Content-Type\" "
                   + "content=\"text/html; charset=utf-8\" xml:lang=\"en\" "
                   + "lang=\"en\"/>\n"
                   + "    <LINK ref=\"SHORTCUT ICON\" "
                   + "href=\"/icons/favicon.png\" />\n"
                   + "    <SCRIPT type=\"text/javascript\">\n"
                   + "      if(location.hostname != 'localhost')\n"
                   + "      {\n"
                   + "        var _gaq = _gaq || [];\n"
                   + "        _gaq.push(['_setAccount', 'UA-1524401-1']);\n"
                   + "        _gaq.push(['_trackPageview']);\n"
                   + "        (function() {\n"
                   +  "          var ga = document.createElement('script'); "
                   + "ga.type = 'text/javascript'; ga.async = true;\n"
                   + "          ga.src = "
                   + "('https:' == document.location.protocol "
                   + "? 'https://ssl' : 'http://www') + "
                   + "'.google-analytics.com/ga.js';\n"
                   + "          var s = "
                   + "document.getElementsByTagName('script')[0]; "
                   + "s.parentNode.insertBefore(ga, s);\n"
                   + "        })();\n"
                   + "      }\n"
                   + "    </SCRIPT>\n"
                   + "  </HEAD>\n"
                   + "  <BODY>\n"
                   + "    <!-- This file was generate by jDMA, version "
                   + "Allip () -->\n"
                   + "    <DIV id=\"header\">\n"
                   + "      <DIV id=\"header-right\">\n"
                   + "        <A id=\"login-icon\" class=\"sprite\" "
                   + "title=\"Login\" "
                   + "href=\"/_ah/login?continue=index.html\">\n"
                   + "</A>\n"
                   + "        <A class=\"sprite library\" title=\"Library\" "
                   + "href=\"/library\" "
                   + "onclick=\"util.link(event, '/library');\">\n"
                   + "</A>\n"
                   + "        <A class=\"sprite about\" title=\"About\" "
                   + "href=\"/about.html\" "
                   + "onclick=\"util.link(event, '/about.html')\">\n"
                   + "</A>\n"
                   + "        <DIV onmouseover=\"$('#search :input').show()\" "
                   + "onmouseout=\"$('#search :input').hide()\">\n"
                   + "          <FORM class=\"search\" id=\"search\" "
                   + "onsubmit=\"util.link(event, '/search/' + "
                   + "this.search.value)\">\n"
                   + "            <INPUT name=\"search\"/>\n"
                   + "            <DIV class=\"sprite search\" "
                   + "title=\"Search\">\n"
                   + "            </DIV>\n"
                   + "          </FORM>\n"
                   + "        </DIV>\n"
                   + "      </DIV>\n"
                   + "      <DIV id=\"header-left\">\n"
                   + "        DMA\n"
                   + "      </DIV>\n"
                   + "      <DIV id=\"navigation\">\n"
                   + "        <A id=\"home\" class=\"sprite\" title=\"Home\" "
                   + "href=\"/\" onclick=\"util.link(event, '/')\">\n"
                   + "</A>\n"
                   + "        <SPAN id=\"subnavigation\">\n"
                   + "          &nbsp;\n"
                   + "        </SPAN>\n"
                   + "      </DIV>\n"
                   + "      <DIV id=\"actions\">\n"
                   + "      </DIV>\n"
                   + "      <DIV class=\"footer\">\n"
                   + "        <P/>\n"
                   + "        <DIV class=\"version\">\n"
                   + "          jDMA version Allip (build )\n"
                   + "        </DIV>\n"
                   + "        <IMG src=\"/icons/html5.png\" "
                   + "alt=\"Uses HTML 5!\"/>\n"
                   + "        <IMG src=\"http://code.google.com/appengine/"
                   + "images/appengine-silver-120x30.gif\" "
                   + "alt=\"Powered by Google App Engine\"/>\n"
                   + "        <A href=\"https://plus.google.com/"
                   + "109501801834573360902?prsrc=3\" "
                   + "style=\"text-decoration:none;\">"
                   + "<IMG src=\"https://ssl.gstatic.com/images/icons/"
                   + "gplus-32.png\" alt=\"jDMA on Google+\" "
                   + "style=\"border:0;width:32px;height:32px;\"/>\n"
                   + "</A>\n"
                   + "  </BODY>\n"
                   + "</HTML>\n", output.toString());

      // because these are closed outside of header/footer (to allow for
      // derivations) in handle (which is not called here)
      m_logger.addExpected("WARNING: writer closed, but tags [div, div] "
                           + "not closed");

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

      HTMLBodyWriter writer =
        new HTMLBodyWriter(new java.io.PrintWriter(output));

      StaticPageServlet servlet = new StaticPageServlet("/html/");

      servlet.writeBody(writer, "/about.html", request);
      writer.close();

      assertPattern("about", ".*<h1>About</h1>.*", output.toString());
      assertPattern("title", ".*document.title = 'DMA - About';.*",
                    output.toString());

      EasyMock.verify(request);
    }

    //......................................................................
  }

  //........................................................................
}
