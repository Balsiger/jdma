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

package net.ixitxachitls.dma.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Multimap;

import org.easymock.EasyMock;

import net.ixitxachitls.comm.servlets.BaseServlet;
import net.ixitxachitls.output.html.HTMLWriter;

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
public class PageServlet extends BaseServlet
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
    (@Nonnull HttpServletRequest inRequest,
     @Nonnull HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    // Set the output header.
    inResponse.setHeader("Content-Type", "text/html");
    inResponse.setHeader("Cache-Control", "max-age=0");

    Multimap<String, String> params = BaseServlet.extractParams(inRequest);
    String path = inRequest.getPathInfo();
    HTMLWriter writer =
      new HTMLWriter(new PrintWriter(inResponse.getOutputStream()));

    // TODO: handle body param and other params here here!
    writeHeader(writer, path, params);
    writeBody(writer, path, params);
    writeFooter(writer, path, params);

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
   * @param     inParams  the parameters given in the request
   *
   */
  @OverridingMethodsMustInvokeSuper
  protected void writeHeader(@Nonnull HTMLWriter inWriter,
                             @Nonnull String inPath,
                             @Nonnull Multimap<String, String> inParams)
  {
    inWriter
      .addCSSFile("jdma")
      .begin("div").id("header")
      .begin("div").id("header-right")
      .begin("a").id("login-icon").classes("icon").tooltip("Login").end("a")
      .begin("a").id("logout-icon").classes("icon").tooltip("Logout").end("a")
      .begin("a").classes("icon", "library").tooltip("Library").end("a")
      .begin("a").classes("icon", "search").tooltip("Search").end("a")
      .begin("a").classes("icon", "about").tooltip("About").href("/about.html")
      .end("a")
      .end("div") // header-right
      .begin("div").id("header-left")
      .add("DMA")
      .end("div"); // header-left
  }

  //........................................................................
  //------------------------------- writeBody ------------------------------

  /**
   * Handles the body content of the request.
   *
   * @param     inWriter  the writer to take up the content (will be closed
   *                      by the PageServlet)
   * @param     inPath    the path of the request
   * @param     inParams  the parameters given in the request
   *
   */
  @OverridingMethodsMustInvokeSuper
  protected void writeBody(@Nonnull HTMLWriter inWriter,
                           @Nullable String inPath,
                           @Nonnull Multimap<String, String> inParams)
  {
    inWriter
      .end("div") // header
      .begin("div").classes("page");
  }

  //........................................................................
  //----------------------------- writeFooter ------------------------------

  /**
   * Write the footer to the writer.
   *
   * @param     inWriter  the writer to take up the content (will be closed
   *                      by the PageServlet)
   * @param     inPath    the path of the request
   * @param     inParams  the parameters given in the request
   *
   */
  @OverridingMethodsMustInvokeSuper
  protected void writeFooter(@Nonnull HTMLWriter inWriter,
                             @Nonnull String inPath,
                             @Nonnull Multimap<String, String> inParams)
  {
    inWriter
      .end("div"); // page
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
    inWriter
      .begin("div").id("navigation")
      .begin("a").id("home").classes("icon").tooltip("Home").href("/").end("a");

    for(String section : inSections)
      if(section != null)
        inWriter.add(" &raquo; ").add(section);

    inWriter
      .end("div"); // navigation
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- simple ---------------------------------------------------------

    /** The simple Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void simple() throws Exception
    {
      HttpServletRequest request =
        EasyMock.createMock(HttpServletRequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      BaseServlet.Test.MockServletOutputStream output =
        new BaseServlet.Test.MockServletOutputStream();
      BaseServlet.Test.MockServletInputStream input =
        new BaseServlet.Test.MockServletInputStream("");

      response.setHeader("Content-Type", "text/html");
      response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(request.getInputStream()).andReturn(input);
      EasyMock.expect(request.getQueryString()).andReturn("").anyTimes();
      EasyMock.expect(request.getPathInfo()).andReturn("/about.html");
      EasyMock.expect(response.getOutputStream()).andReturn(output);
      EasyMock.replay(request, response);

      PageServlet servlet = new PageServlet();

      assertNull("handle", servlet.handle(request, response));
      assertEquals("content",
                   "<HTML>\n"
                   + "  <HEAD>\n"
                   + "    <LINK rel=\"STYLESHEET\" type=\"text/css\" "
                   + "href=\"/css/jdma.css\" />\n"
                   + "  </HEAD>\n"
                   + "  <BODY>\n"
                   + "    <DIV id=\"header\">\n"
                   + "      <DIV id=\"header-right\">\n"
                   + "        <A id=\"login-icon\" class=\"icon\" "
                   + "title=\"Login\">\n"
                   + "        </A>\n"
                   + "        <A id=\"logout-icon\" class=\"icon\" "
                   + "title=\"Logout\">\n"
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
                   + "    </DIV>\n"
                   + "    <DIV class=\"page\">\n"
                   + "    </DIV>\n"
                   + "  </BODY>\n"
                   + "</HTML>\n",
                   output.toString());

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
                   "<HTML>\n"
                   + "  <BODY>\n"
                   + "    <DIV id=\"navigation\">\n"
                   + "      <A id=\"home\" class=\"icon\" title=\"Home\" "
                   + "href=\"/\">\n"
                   + "      </A>\n"
                   + "       &raquo; \n"
                   + "      s1\n"
                   + "       &raquo; \n"
                   + "      s2\n"
                   + "       &raquo; \n"
                   + "      s3\n"
                   + "    </DIV>\n"
                   + "  </BODY>\n"
                   + "</HTML>\n", output.toString());

      output = new java.io.ByteArrayOutputStream();
      writer = new HTMLWriter(new PrintWriter(output));

      servlet.addNavigation(writer);
      writer.close();
      assertEquals("no section", "<HTML>\n"
                   + "  <BODY>\n"
                   + "    <DIV id=\"navigation\">\n"
                   + "      <A id=\"home\" class=\"icon\" title=\"Home\" "
                   + "href=\"/\">\n"
                   + "      </A>\n"
                   + "    </DIV>\n"
                   + "  </BODY>\n"
                   + "</HTML>\n", output.toString());
    }

    //......................................................................
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
