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

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.output.html.HTMLBodyWriter;
import net.ixitxachitls.output.html.HTMLWriter;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The base servlet for all dma pages. This class is mainly responsible for
 * rendering headers and footers and everything that is the same for every dma
 * page.
 *
 * @file          PageServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class PageServlet extends DMAServlet
{
  //--------------------------------------------------------- constructor(s)

  /**
   * Create the servlet.
   *
   */
  public PageServlet()
  {
  }

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The project name. */
  public static final String PROJECT = Config.get("project.name", "jDMA");

  /** The project name. */
  public static final String PROJECT_URL =
    Config.get("project.url", "http://www.ixitxachitls.net");

  /** The version number (or monster in this case ;-)). */
  public static final String VERSION =
    Config.get("project.version", "Allip");

  /**
   * The build date. Because I did not manage to simply replace the date in
   * the config file from ant, all the build dates from all builds are
   * concatenated in this value. We only need the first, though.
   */
  public static final String BUILD =
    Config.get("project.build", "").replaceAll(";.*", "");

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- handle --------------------------------

  /**
   * Handle the request.
   *
   * @param       inRequest  the original request
   * @param       inResponse the original response
   *
   * @return      a special result if something went wrong
   *
   * @throws      ServletException general error when processing the page
   * @throws      IOException      writing to the page failed
   *
   */
  protected @Nullable SpecialResult handle
    (@Nonnull DMARequest inRequest,
     @Nonnull HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    // Set the output header.
    inResponse.setHeader("Content-Type", "text/html");
    inResponse.setHeader("Cache-Control", "max-age=0");

    boolean bodyOnly = inRequest.isBodyOnly();

    String path = inRequest.getRequestURI();
    HTMLWriter writer;
    if(bodyOnly)
      writer =
        new HTMLBodyWriter(new PrintWriter(inResponse.getOutputStream()));
    else
      writer = new HTMLWriter(new PrintWriter(inResponse.getOutputStream()));

    if(!bodyOnly)
    {
      writeHeader(writer, path, inRequest);
      writer.end("div"); // header
      writer.begin("div").id("page").classes("page");
    }

    writeBody(writer, path, inRequest);

    if(!bodyOnly)
    {
      writer.end("div"); // page
      writeFooter(writer, path, inRequest);
      writer.end("div"); // footer
    }

    writer.close();

    return null;
  }

  //........................................................................

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
    inWriter
      .comment("This file was generate by " + PROJECT + ", version " + VERSION
               + " (" + BUILD + ")")
      // jquery
      .addJSFile("jquery-1.5")
      .addCSSFile("smoothness/jquery-ui-1.8.9.custom")
      .addJSFile("jquery-ui-1.8.9.custom.min")
      // jdma
      .addJSFile("util")
      .addJSFile("form")
      .addCSSFile("gui")
      .addJSFile("gui")
      .addJSFile("edit")
      .addCSSFile("jdma")
      .addJSFile("jdma")
      // make android use the device width/height
      .meta("viewport",
            "width=device-width, height=device-height")
      .meta("Content-Type", "text/html; charset=utf-8",
            "xml:lang", "en", "lang", "en")
      // favicon
      .link("SHORTCUT ICON", "/icons/favicon.png")
      // header
      .begin("div").id("header")
      .begin("div").id("header-right");

    BaseCharacter user = inRequest.getUser();

    if(user == null)
      inWriter
        .begin("a").id("login-icon").classes("icon").tooltip("Login")
        .onClick("login()").end("a");
    else
    {
      inWriter
        .begin("span").classes("user").add(user.getName());

      if(inRequest.hasUserOverride())
        inWriter.add(" (" + inRequest.getRealUser().getName() + ")");

      inWriter
        .end("span")
        .add(" | ")
        .begin("a").id("logout-icon").classes("icon").tooltip("Logout")
        .onClick("logout()").end("a");
    }

    inWriter
      .begin("a").classes("icon", "library").tooltip("Library").end("a")
      .begin("a").classes("icon", "about").tooltip("About").href("/about.html")
      .onClick("util.link(event, '/about.html')").end("a")
      .begin("div").onMouseOver("$('#search :input').show()")
      .onMouseOut("$('#search :input').hide()")
      .begin("form").classes("search").id("search")
      .onSubmit("util.link(event, '/search/' + this.search.value)")
      .begin("input").name("search").end("input")
      .begin("div").classes("icon", "search").tooltip("Search").end("div")
      .end("form") // search
      .end("div") // search
      .end("div") // header-right
      .begin("div").id("header-left")
      .add("DMA")
      .end("div") // header-left
      .begin("div").id("navigation")
      .begin("a").id("home").classes("icon").tooltip("Home").href("/")
      .onClick("util.link(event, '/')").end("a")
      .begin("span").id("subnavigation").add("&nbsp;").end("span")
      .end("div")
      .begin("div").id("actions").end("div");
  }

  //........................................................................
  //------------------------------- writeBody ------------------------------

  /**
   * Handles the body content of the request.
   *
   * @param     inWriter  the writer to take up the content (will be closed
   *                      by the PageServlet)
   * @param     inPath    the path of the request
   * @param     inRequest the request for the page
   *
   */
  @OverridingMethodsMustInvokeSuper
  protected void writeBody(@Nonnull HTMLWriter inWriter,
                           @Nullable String inPath,
                           @Nonnull DMARequest inRequest)
  {
    // nothing done here
  }

  //........................................................................
  //----------------------------- writeFooter ------------------------------

  /**
   * Write the footer to the writer.
   *
   * @param     inWriter  the writer to take up the content (will be closed
   *                      by the PageServlet)
   * @param     inPath    the path of the request
   * @param     inRequest the request for the page
   *
   */
  @OverridingMethodsMustInvokeSuper
  protected void writeFooter(@Nonnull HTMLWriter inWriter,
                             @Nonnull String inPath,
                             @Nonnull DMARequest inRequest)
  {
    inWriter.begin("div").classes("footer")
      .begin("p").end("p")
      .begin("div").classes("version")
      .add(PROJECT + " version " + VERSION + " (build " + BUILD + ")")
      .end("div")
      // we can't have spaces here, so we add it directly
      .add("<a href=\"http://validator.w3.org/check?uri=referer\">"
           + "<img src=\"/icons/valid-xhtml10.png\" alt=\"Valid XHTML 1.0!\" />"
           + "</a>")
      .begin("img").src("/icons/html5.png").alt("Uses HTML 5!")
      .end("img")
      .script("if(location.hostname != 'localhost')",
              "{",
              "  var gaJsHost = ((\"https:\" == document.location.protocol) ? "
              + "\"https://ssl.\" : \"http://www.\");",
              "  document.write(unescape(\"%3Cscript src='\" + gaJsHost + \""
              + "google-analytics.com/ga.js' "
              + "type='text/javascript'%3E%3C/script%3E\"));",
              "}")
      .script("if(location.hostname != 'localhost')",
              "{",
              "  var pageTracker = _gat._getTracker(\"UA-1524401-1\");",
              "  pageTracker._initData();",
              "  pageTracker._trackPageview();",
              "}");
  }

  //........................................................................

  //---------------------------- addNavigation -----------------------------

  /**
   * Add the navigation structure to the page.
   *
   * @param       inWriter   the writer to write to
   * @param       inSections the sections and subsections to the current page
   *
   */
  public void addNavigation(@Nonnull HTMLWriter inWriter,
                            @Nonnull String ... inSections)
  {
    StringBuilder builder = new StringBuilder();

    if(inSections.length > 0)
    {
      for(String section : inSections)
        if(section != null)
        {
          builder.append(" &raquo; ");
          builder.append(section);
        }
    }

    String navigation = builder.toString();
    if(navigation.isEmpty())
      navigation = "&nbsp;";

    inWriter.script("$('#subnavigation').html('" + navigation + "');");
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.server.ServerUtils.Test
  {
    //----- simple ---------------------------------------------------------

    /** The simple Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void simple() throws Exception
    {
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      MockServletOutputStream output = new MockServletOutputStream();

      response.setHeader("Content-Type", "text/html");
      response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(request.isBodyOnly()).andReturn(false).anyTimes();
      EasyMock.expect(request.getUser()).andReturn(null);
      EasyMock.expect(request.getQueryString()).andReturn("").anyTimes();
      EasyMock.expect(request.getPathInfo()).andReturn("/about.html");
      EasyMock.expect(response.getOutputStream()).andReturn(output);
      EasyMock.replay(request, response);

      PageServlet servlet = new PageServlet() {
          private static final long serialVersionUID = 1L;
          protected void writeBody(@Nonnull HTMLWriter inWriter,
                                   @Nullable String inPath,
                                   @Nonnull DMARequest inRequest)
          {
            super.writeBody(inWriter, inPath, inRequest);
            inWriter.add("This is the body.");
          }
        };

      assertNull("handle", servlet.handle(request, response));
      assertEquals("content",
                   "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                   + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN"
                   + "\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
                   + "\">\n"
                   + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                   + "<HTML>\n"
                   + "  <HEAD>\n"
                   + "    <SCRIPT type=\"text/javascript\" "
                   + "src=\"/js/jquery-1.5.js\"></SCRIPT>\n"
                   + "    <LINK rel=\"STYLESHEET\" type=\"text/css\" "
                   + "href=\"/css/smoothness/jquery-ui-1.8.9.custom.css\" />\n"
                   + "    <SCRIPT type=\"text/javascript\" "
                   + "src=\"/js/jquery-ui-1.8.9.custom.min.js\"></SCRIPT>\n"
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
                   + "        var gaJsHost = ((\"https:\" == "
                   + "document.location.protocol) ? \"https://ssl.\" : "
                   + "\"http://www.\");\n"
                   + "        document.write(unescape(\"%3Cscript src='\" + "
                   + "gaJsHost + \"google-analytics.com/ga.js' "
                   + "type='text/javascript'%3E%3C/script%3E\"));\n"
                   + "      }\n"
                   + "    </SCRIPT>\n"
                   + "    <SCRIPT type=\"text/javascript\">\n"
                   + "      if(location.hostname != 'localhost')\n"
                   + "      {\n"
                   + "        var pageTracker = "
                   + "_gat._getTracker(\"UA-1524401-1\");\n"
                   + "        pageTracker._initData();\n"
                   + "        pageTracker._trackPageview();\n"
                   + "      }\n"
                   + "    </SCRIPT>\n"
                   + "  </HEAD>\n"
                   + "  <BODY>\n"
                   + "    <!-- This file was generate by jDMA, version "
                   + "Allip () -->\n"
                   + "    <DIV id=\"header\">\n"
                   + "      <DIV id=\"header-right\">\n"
                   + "        <A id=\"login-icon\" class=\"icon\" "
                   + "title=\"Login\" onclick=\"login()\">\n"
                   + "        </A>\n"
                   + "        <A class=\"icon library\" title=\"Library\">\n"
                   + "        </A>\n"
                   + "        <A class=\"icon about\" title=\"About\" "
                   + "href=\"/about.html\" "
                   + "onclick=\"util.link(event, '/about.html')\">\n"
                   + "        </A>\n"
                   + "        <DIV onmouseover=\"$('#search :input').show()\" "
                   + "onmouseout=\"$('#search :input').hide()\">\n"
                   + "          <FORM class=\"search\" id=\"search\" "
                   + "onsubmit=\"util.link(event, '/search/' + "
                   + "this.search.value)\">\n"
                   + "            <INPUT name=\"search\"/>\n"
                   + "            <DIV class=\"icon search\" "
                   + "title=\"Search\">\n"
                   + "            </DIV>\n"
                   + "          </FORM>\n"
                   + "        </DIV>\n"
                   + "      </DIV>\n"
                   + "      <DIV id=\"header-left\">\n"
                   + "        DMA\n"
                   + "      </DIV>\n"
                   + "      <DIV id=\"navigation\">\n"
                   + "        <A id=\"home\" class=\"icon\" title=\"Home\" "
                   + "href=\"/\" onclick=\"util.link(event, '/')\">\n"
                   + "        </A>\n"
                   + "        <SPAN id=\"subnavigation\">\n"
                   + "          &nbsp;\n"
                   + "        </SPAN>\n"
                   + "      </DIV>\n"
                   + "      <DIV id=\"actions\">\n"
                   + "      </DIV>\n"
                   + "    </DIV>\n"
                   + "    <DIV id=\"page\" class=\"page\">\n"
                   + "      This is the body.\n"
                   + "    </DIV>\n"
                   + "    <DIV class=\"footer\">\n"
                   + "      <P/>\n"
                   + "      <DIV class=\"version\">\n"
                   + "        jDMA version Allip (build )\n"
                   + "      </DIV>\n"
                   + "      <a href=\"http://validator.w3.org/check?"
                   + "uri=referer\"><img src=\"/icons/valid-xhtml10.png\" "
                   + "alt=\"Valid XHTML 1.0!\" /></a>\n"
                   + "      <IMG src=\"/icons/html5.png\" "
                   + "alt=\"Uses HTML 5!\"/>\n"
                   + "    </DIV>\n"
                   + "  </BODY>\n"
                   + "</HTML>\n",
                   output.toString());

      output.close();
      EasyMock.verify(request, response);
    }

    //......................................................................
    //----- bodyOnly -------------------------------------------------------

    /** The simple Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void bodyOnly() throws Exception
    {
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      MockServletOutputStream output = new MockServletOutputStream();

      response.setHeader("Content-Type", "text/html");
      response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(request.isBodyOnly()).andReturn(true).anyTimes();
      EasyMock.expect(request.getQueryString()).andReturn("").anyTimes();
      EasyMock.expect(request.getPathInfo()).andReturn("/about.html");
      EasyMock.expect(response.getOutputStream()).andReturn(output);
      EasyMock.replay(request, response);

      PageServlet servlet = new PageServlet() {
          private static final long serialVersionUID = 1L;
          protected void writeBody(@Nonnull HTMLWriter inWriter,
                                   @Nullable String inPath,
                                   @Nonnull DMARequest inRequest)
          {
            super.writeBody(inWriter, inPath, inRequest);
            inWriter.add("This is the body.");
          }
        };

      assertNull("handle", servlet.handle(request, response));
      assertEquals("content", "    This is the body.\n", output.toString());

      output.close();
      EasyMock.verify(request, response);
    }

    //......................................................................
    //----- navigation -----------------------------------------------------

    /** The navigation Test. */
    @org.junit.Test
    public void navigation()
    {
      java.io.ByteArrayOutputStream output =
        new java.io.ByteArrayOutputStream();
      HTMLWriter writer = new HTMLWriter(new PrintWriter(output));

      PageServlet servlet = new PageServlet();

      servlet.addNavigation(writer, "s1", "s2", "s3");
      writer.close();
      assertEquals("3 sections",
                   "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                   + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN"
                   + "\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
                   + "\">\n"
                   + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                   + "<HTML>\n"
                   + "    <SCRIPT type=\"text/javascript\">\n"
                   + "      $('#subnavigation').html(' &raquo; s1 &raquo; "
                   + "s2 &raquo; s3');\n"
                   + "    </SCRIPT>\n"
                   + "  <BODY>\n"
                   + "  </BODY>\n"
                   + "</HTML>\n", output.toString());

      output = new java.io.ByteArrayOutputStream();
      writer = new HTMLWriter(new PrintWriter(output));

      servlet.addNavigation(writer);
      writer.close();
      assertEquals("no section",
                   "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                   + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN"
                   + "\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
                   + "\">\n"
                   + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                   + "<HTML>\n"
                   + "    <SCRIPT type=\"text/javascript\">\n"
                   + "      $('#subnavigation').html('&nbsp;');\n"
                   + "    </SCRIPT>\n"
                   + "  <BODY>\n"
                   + "  </BODY>\n"
                   + "</HTML>\n", output.toString());
    }

    //......................................................................
  }

  //........................................................................
}
