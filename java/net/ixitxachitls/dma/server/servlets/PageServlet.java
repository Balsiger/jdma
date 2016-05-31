/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.server.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import javax.annotation.concurrent.Immutable;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyData;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.dma.values.enums.Group;
import net.ixitxachitls.output.html.HTMLWriter;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.Files;
import net.ixitxachitls.util.Tracer;
import net.ixitxachitls.util.logging.Log;

/**
 * The base servlet for all dma pages. This class is mainly responsible for
 * rendering headers and footers and everything that is the same for every dma
 * page.
 *
 * @file          PageServlet.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@Immutable
public class PageServlet extends SoyServlet
{
  /**
   * Create the servlet.
   */
  public PageServlet()
  {
  }

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  @Override
  protected String getTemplateName(DMARequest inDMARequest,
                                   Map<String, SoyData> inData)
  {
    return "dma.page.empty";
  }

  @Override
  protected void render(DMARequest inRequest, PrintWriter inWriter,
                        SoyRenderer inRenderer)
  {
    boolean bodyOnly = inRequest.isBodyOnly();
    boolean print = inRequest.getRequestURI().endsWith(".print");

    if(!bodyOnly)
    {
      inWriter.println(inRenderer.render("dma.page.head"));

      if(print)
        inWriter.print(inRenderer.render("dma.page.printStart"));
      else
      {
        inWriter.println(inRenderer.render("dma.page.header"));
        inWriter.println(inRenderer.render("dma.page.start"));
      }
    }

    super.render(inRequest, inWriter, inRenderer);

    if(!bodyOnly)
    {
      if(print)
        inWriter.println(inRenderer.render("dma.page.printEnd"));
      else
      {
        inWriter.println(inRenderer.render("dma.page.end"));
        inWriter.println(inRenderer.render("dma.page.footer"));
      }
    }
  }

  @Override
  protected Map<String, Object> collectInjectedData
    (DMARequest inRequest, SoyRenderer inRenderer)
  {
    Tracer tracer = new Tracer("collecting page injected data");
    Map<String, Object> data = super.collectInjectedData(inRequest, inRenderer);

    if(inRequest.hasUser())
      data.put("dm",
               inRequest.getUser().get().hasAccess(Group.DM));
    else
      data.put("dm", false);

    data.put("isDev", DMAServlet.isDev() || inRequest.hasParam("dev"));

    tracer.done();
    return data;
  }

  /**
   * Add the navigation structure to the page.
   *
   * @param       inWriter   the writer to write to
   * @param       inSections the sections and subsections to the current page
   */
  public void addNavigation(HTMLWriter inWriter, String ... inSections)
  {
    StringBuilder builder = new StringBuilder();

    for(int i = 0; i < inSections.length; i += 2)
      if(inSections[i] != null)
      {
        builder.append(" &raquo; ");

        if(i + 1 < inSections.length)
          builder.append("<a href=\""
                         + Encodings.encodeHTMLAttribute(inSections[i + 1])
                         + "\" class=\"navigation-link\" "
                         + "onclick=\"return util.link(event, \\'"
                         + Encodings.encodeHTMLAttribute(inSections[i + 1])
                         + "\\');\" >"
                         + Encodings.encodeHTMLAttribute
                         (inSections[i].toLowerCase(Locale.US)) + "</a>");
        else
          builder.append(inSections[i]);
      }

    String navigation = builder.toString();
    if(navigation.isEmpty())
      navigation = "&nbsp;";

    inWriter.bodyScript("$('#subnavigation').html('" + navigation + "');");
  }

  /**
   * Write an icon to the current position in the writer.
   *
   * @param       inWriter   the html writer to output to
   * @param       inIcon     the name of the image to use for the icon
   *                         (/icons/ will be prefixed, .png appended)
   * @param       inCaption  the caption below the icon
   * @param       inURL      the url to link to when clicking the icon
   */
  protected static void writeIcon(HTMLWriter inWriter, String inIcon,
                                  String inCaption, String inURL)
  {
    inWriter
      .begin("div").classes("caption-container")
      .begin("a").classes("icon-link").href(inURL)
      .onClick("link(event, '" + inURL + "');")
      .begin("img")
      .src("/icons/"
           + Files.encodeName(inIcon.toLowerCase(Locale.US)) + ".png")
      .alt(inCaption).tooltip(inCaption).classes("icon highlight")
      .end("img")
      .begin("div").classes("caption").add(inCaption).end("div")
      .end("a")
      .end("div");
  }

  /**
   * Write an error text to the output.
   *
   * @param    inWriter   the output to write to
   * @param    inTitle    the title of the error
   * @param    inMessage  the error message
   */
  protected static void writeError(HTMLWriter inWriter, String inTitle,
                                   String inMessage)
  {
    inWriter
      .title(inTitle)
      .begin("h1").add(inTitle).end("h1")
      .add(inMessage);

    Log.warning(inTitle + " - " + inMessage);
  }

  //---------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.server.ServerUtils.Test
  {
    /** The simple Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void simple() throws Exception
    {
      m_localServiceTestHelper.setEnvIsLoggedIn(false);
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);

      try (StringWriter writer = new StringWriter())
      {
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "max-age=0");
        response.setCharacterEncoding("UTF-8");
        EasyMock.expect(request.isBodyOnly()).andReturn(false).anyTimes();
        EasyMock.expect(request.hasUser()).andStubReturn(false);
        EasyMock.expect(request.getUser()).andStubReturn(
            Optional.<BaseCharacter>absent());
        EasyMock.expect(request.getOriginalPath()).andStubReturn("index.html");
        EasyMock.expect(request.getQueryString()).andReturn("").anyTimes();
        EasyMock.expect(request.getRequestURI()).andStubReturn("/about.html");
        EasyMock.expect(request.hasUserOverride()).andStubReturn(false);
        EasyMock.expect(response.getWriter())
            .andStubReturn(new PrintWriter(writer));
        EasyMock.replay(request, response);

        PageServlet servlet = new PageServlet();
        assertFalse("handle", servlet.handle(request, response).isPresent());
        String content = writer.toString();
        assertPattern("content", ".*<title>DMA</title>.*", content);

        EasyMock.verify(request, response);
      }
    }

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

      try (StringWriter writer = new StringWriter())
      {
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "max-age=0");
        response.setCharacterEncoding("UTF-8");
        EasyMock.expect(request.isBodyOnly()).andReturn(true).anyTimes();
        EasyMock.expect(request.getQueryString()).andReturn("").anyTimes();
        EasyMock.expect(request.getRequestURI()).andStubReturn("/about.html");
        EasyMock.expect(request.hasUser()).andStubReturn(false);
        EasyMock.expect(request.getUser()).andStubReturn(
            Optional.<BaseCharacter>absent());
        EasyMock.expect(request.hasUserOverride()).andStubReturn(false);
        EasyMock.expect(request.getOriginalPath()).andStubReturn("/about.html");
        EasyMock.expect(response.getWriter())
            .andReturn(new PrintWriter(writer));
        EasyMock.replay(request, response);

        PageServlet servlet = new PageServlet();
        assertFalse("handle", servlet.handle(request, response).isPresent());
        assertPattern("content", ".*Nothing to see here..*", writer.toString());

        EasyMock.verify(request, response);
      }
    }

    /** The navigation Test.
     *
     * @throws IOException when closing the output
     */
    @org.junit.Test
    @SuppressWarnings("try")
    public void navigation() throws IOException
    {
      try (java.io.ByteArrayOutputStream output =
        new java.io.ByteArrayOutputStream())
      {
        PageServlet servlet = new PageServlet();
        try (HTMLWriter writer = new HTMLWriter(new PrintWriter(output)))
        {
          servlet.addNavigation(writer, "s1", "l1", "s2", "l2", "s3", "l3");
          writer.close();
          assertEquals
          ("3 sections",
           "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
             + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN"
             + "\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
             + "\">\n"
             + "<HTML xmlns=\"http://www.w3.org/1999/xhtml\">\n"
             + "  <BODY>\n"
             + "    <SCRIPT type=\"text/javascript\">\n"
             + "      $('#subnavigation').html(' &raquo; <a href=\"l1\" "
             + "class=\"navigation-link\" "
             + "onclick=\"return util.link(event, \\'l1\\');\" >s1</a> "
             + "&raquo; <a href=\"l2\" class=\"navigation-link\" "
             + "onclick=\"return util.link(event, \\'l2\\');\" >s2</a> "
             + "&raquo; <a href=\"l3\" class=\"navigation-link\" "
             + "onclick=\"return util.link(event, \\'l3\\');\" >s3</a>"
             + "');\n"
             + "    </SCRIPT>\n"
             + "  </BODY>\n"
             + "</HTML>\n", output.toString());
        }

        output.reset();

        try (HTMLWriter writer = new HTMLWriter(new PrintWriter(output)))
        {
          servlet.addNavigation(writer);
          writer.close();
          assertEquals
          ("no section",
           "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
             + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN"
             + "\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
             + "\">\n"
             + "<HTML xmlns=\"http://www.w3.org/1999/xhtml\">\n"
             + "  <BODY>\n"
             + "    <SCRIPT type=\"text/javascript\">\n"
             + "      $('#subnavigation').html('&nbsp;');\n"
             + "    </SCRIPT>\n"
             + "  </BODY>\n"
             + "</HTML>\n", output.toString());
        }
      }
    }
  }
}
