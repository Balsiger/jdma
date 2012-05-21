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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.Immutable;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

import com.google.common.collect.ImmutableSet;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.dma.output.soy.SoyEntry;
import net.ixitxachitls.output.html.HTMLWriter;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A page servlet to serve a list of values.
 *
 *
 * @file          ListServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class EntryListServlet extends PageServlet
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------- EntryListServlet ---------------------------

  /**
   * Create the servlet.
   */
  public EntryListServlet()
  {
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getEntries ------------------------------

  /**
   * Get the entries in the given page range.
   *
   * @param       inRequest the original request
   * @param       inPath    the path used to access the entries
   * @param       inType    the type of entries to get
   * @param       inStart   the index where to start to get entries
   * @param       inSize    the maximal number of entries to return
   *
   * @return      a list of all entries in range
   *
   */
  @SuppressWarnings("unchecked") // need to cast
  public List<AbstractEntry>
    getEntries(@Nonnull DMARequest inRequest, @Nonnull String inPath,
               @Nonnull AbstractType<? extends AbstractEntry> inType,
               int inStart, int inSize)
  {
    return (List<AbstractEntry>)DMADataFactory.get()
      .getEntries(inType, null, inStart, inSize);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //----------------------------- collectData ------------------------------

  /**
   * Collect the data that is to be printed.
   *
   * @param    inRequest the request for the page
   *
   * @return   a map with key/value pairs for data (values can be primitives
   *           or maps or lists)
   *
   */
  @Override
  protected @Nonnull Map<String, Object> collectData
    (@Nonnull DMARequest inRequest, @Nonnull SoyRenderer inRenderer)
  {
    Map<String, Object> data = super.collectData(inRequest, inRenderer);

    String path = inRequest.getRequestURI();
    String typeName = "";
    if(path != null)
      typeName = Strings.getPattern(path, "([^/]*)/?$");

    AbstractType<? extends AbstractEntry> type =
      AbstractType.getTyped(typeName);
    if(type == null)
    {
      data.put("content", inRenderer.render("dma.error.unknownType",
                                            map("type", typeName)));
      return data;
    }

    String title = Encodings.toWordUpperCase(type.getMultipleLink());
    Log.info("serving dynamic list " + title);

    List<AbstractEntry> rawEntries = getEntries(inRequest, path, type,
                                                inRequest.getStart(),
                                                inRequest.getPageSize() + 1);

    List<SoyEntry> entries = new ArrayList<SoyEntry>();
    for(AbstractEntry entry : rawEntries)
      entries.add(new SoyEntry(entry, inRenderer));

    data.put("content",
             inRenderer.render
             ("dma.entry.list",
              map("title", title,
                  "entries", entries,
                  "pagesize", inRequest.getPageSize(),
                  "start", inRequest.getStart()),
              ImmutableSet.of(type.getName().replace(" ", ""))));

    return data;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** The request for the test. */
    private DMARequest m_request;

    /** The response used in the test. */
    private HttpServletResponse m_response;

    /** The output of the test. */
    private net.ixitxachitls.server.ServerUtils.Test.MockServletOutputStream
      m_output;

    //----- setUp ----------------------------------------------------------

    /** Setup the mocks for testing. */
    @org.junit.Before
    public void setUp()
    {
      m_request = EasyMock.createMock(DMARequest.class);
      m_response = EasyMock.createMock(HttpServletResponse.class);;
      m_output =
        new net.ixitxachitls.server.ServerUtils.Test.MockServletOutputStream();
    }

    //......................................................................
    //----- cleanup --------------------------------------------------------

    /**
     * Cleanup after a test.
     *
     * @throws Exception should not happen
     */
    @org.junit.After
    public void cleanup() throws Exception
    {
      m_output.close();
      EasyMock.verify(m_request, m_response);
    }

    //......................................................................
    //----- createServlet --------------------------------------------------

    /**
     * Create the servlet for testing.
     *
     * @param inEntries the entry to return
     * @param inID      the id of the entry looking for
     * @param inStart   the pagination start
     * @param inEnd     the pagination end
     *
     * @return the created servlet
     * @throws Exception should not happen
     */
    public EntryListServlet createServlet
      (final List<AbstractEntry> inEntries,
       final String inID, int inStart, int inEnd) throws Exception
    {
      m_response.setHeader("Content-Type", "text/html");
      m_response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(m_request.isBodyOnly()).andReturn(true).anyTimes();
      EasyMock.expect(m_request.getQueryString()).andReturn("").anyTimes();
      EasyMock.expect(m_request.getRequestURI())
        .andReturn("/request/base entry")
        .anyTimes();
      EasyMock.expect(m_response.getOutputStream()).andReturn(m_output);
      EasyMock.expect(m_request.getUser()).andStubReturn(null);
      EasyMock.expect(m_request.getStart()).andStubReturn(inStart);
      EasyMock.expect(m_request.getPageSize()).andStubReturn(50);
      EasyMock.replay(m_request, m_response);

      return new EntryListServlet()
        {
          private static final long serialVersionUID = 1L;

          @Override
          public List<AbstractEntry>
            getEntries(DMARequest inRequest, String inPath,
                       AbstractType<? extends AbstractEntry> inType,
                       int inStart, int inSize)
          {
            return inEntries;
          }
        };
    }

    //......................................................................

    //----- simple ---------------------------------------------------------

    /**
     * The simple Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void simple() throws Exception
    {
      EntryListServlet servlet =
        createServlet(com.google.common.collect.ImmutableList.<AbstractEntry>of
                      (new net.ixitxachitls.dma.entries.BaseEntry("guru1"),
                       new net.ixitxachitls.dma.entries.BaseEntry("guru2"),
                       new net.ixitxachitls.dma.entries.BaseEntry("guru3")),
                      "/baseentry", 0, 42);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content",
                   "    <SCRIPT type=\"text/javascript\">\n"
                   + "      document.title = 'Entrys';\n"
                   + "    </SCRIPT>\n"
                   + "    <H1>\n"
                   + "      Entrys\n"
                   + "    </H1>\n"
                   + "    \n"
                   + "<table class=\"entrylist\">"
                   + "<tr class=\"title\">"
                   + "<td class=\"title\"></td>"
                   + "<td class=\"title\">Name</td>"
                   + "</tr>"
                   + "<tr>"
                   + "<td class=\"icon\">"
                   + "<img src=\"/icons/labels/BaseEntry.png\" "
                   + "alt=\"BaseEntry\" class=\"image label\"/>"
                   + "</td><td class=\"name\">guru1</td>"
                   + "</tr>"
                   + "<tr>"
                   + "<td class=\"icon\">"
                   + "<img src=\"/icons/labels/BaseEntry.png\" "
                   + "alt=\"BaseEntry\" class=\"image label\"/>"
                   + "</td>"
                   + "<td class=\"name\">guru2</td>"
                   + "</tr>"
                   + "<tr>"
                   + "<td class=\"icon\">"
                   + "<img src=\"/icons/labels/BaseEntry.png\" "
                   + "alt=\"BaseEntry\" class=\"image label\"/>"
                   + "</td>"
                   + "<td class=\"name\">guru3</td>"
                   + "</tr>"
                   + "</table>\n"
                   + "    <SCRIPT type=\"text/javascript\">\n"
                   + "      $('#subnavigation').html(' &raquo; "
                   + "<a href=\"/entrys\" class=\"navigation-link\" "
                   + "onclick=\"return util.link(event, \\'/entrys\\');\" >"
                   + "entrys</a>');\n"
                   + "    </SCRIPT>\n",
                   m_output.toString());
    }

    //......................................................................
  }

  //........................................................................
}
