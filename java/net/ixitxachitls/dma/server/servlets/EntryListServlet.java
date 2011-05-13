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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.Immutable;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.output.html.HTMLDocument;
import net.ixitxachitls.output.commands.Color;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Table;
import net.ixitxachitls.output.html.HTMLWriter;
import net.ixitxachitls.util.Pair;
import net.ixitxachitls.util.Strings;

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
public abstract class EntryListServlet extends PageServlet
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------- EntryListServlet ---------------------------

  /**
   * Create the servlet.
   */
  public EntryListServlet()
  {
    // nothing to do
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
   * @param       inID    the id for the entries to get
   *
   * @return      a list of all entries in range
   *
   */
  public abstract List<AbstractEntry> getEntries(@Nonnull String inID);

  //........................................................................
  //------------------------------- getTitle -------------------------------

  /**
   * Get the title for the document.
   *
   * @param       inID the id of the request
   *
   * @return      the title
   *
   */
  public abstract @Nonnull String getTitle(String inID);

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

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
    String id = "";
    if(inPath != null)
      id = Strings.getPattern(inPath, "([^/]*)$");

    // determine start and end of index to show
    Pair<Integer, Integer> pagination = inRequest.getPagination();
    int start = pagination.first();
    int end   = pagination.second();

    List<AbstractEntry> entries = getEntries(id);

    inWriter.title(getTitle(id));
    HTMLDocument document = new HTMLDocument(getTitle(id));

    List<String> navigation = new ArrayList<String>();
    if(start > 0)
      if(start - inRequest.getPageSize() > 0)
        navigation.add("<a href=\"?start="
                       + (start - inRequest.getPageSize())
                       + "\"  onclick=\"return util.link(event, '?start="
                       + (start - inRequest.getPageSize()) + "');\" "
                       + "class=\"paginate-previous\">"
                       + "&laquo; previous</a>");
      else
        navigation.add("<a href=\"?\" "
                       + "onclick=\"return util.link(event, '?');\" "
                       + "class=\"paginate-previous\">"
                       + "&laquo; previous</a>");

    if(entries.size() >= end)
      navigation.add("<a href=\"?start="
                     + (start + inRequest.getPageSize()) + "\" "
                     + " onclick=\"return util.link(event, '?start="
                     + (start + inRequest.getPageSize()) + "');\" "
                     + "class=\"paginate-next\">"
                     + "&raquo; next</a>");

    document.add(navigation);
    boolean dm = true;
    if(entries.isEmpty())
      document.add(new Color("error", "No entries found!"));
    else
    {
      List<Object> cells = new ArrayList<Object>();
      for(AbstractEntry entry : sublist(entries, start, end))
        cells.addAll(entry.printList(entry.getName(), dm));

      document.add(new Table("entrylist",
                             entries.get(0).getListFormat(),
                             new Command(cells)));
    }
    document.add(navigation);

    inWriter.add(document.toString());
//     HTMLDocument document;

//     if(id == null || id.length() == 0)
//       document = handleOverview(inRequest.getServletPath(), start,
//                                 end > 0 ? end
//                                 : (m_index.isPaginated()
//                                    ? start + inRequest.getPageSize()
//                                    : Integer.MAX_VALUE),
//                                 inRequest.getUser(), inRequest);
//     else
//       document = handleDetailed(id, start,
//                              end > 0 ? end : start + inRequest.getPageSize(),
//                                 inRequest.getUser(), inRequest);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- sublist --------------------------------

  /**
   * Create a sublist using the given limits.
   *
   * @param       <T>     the type of elements in the list
   * @param       inList  the list with the elements
   * @param       inStart the start position (inclusive)
   * @param       inEnd   the end position (exclusive)
   *
   * @return      a sublist for the given range
   *
   */
  public @Nonnull <T> List<T> sublist(@Nonnull List<T> inList, int inStart,
                                      int inEnd)
  {
    if(inList.isEmpty())
      return inList;

    int start = inStart;
    int end = inEnd;

    if(start < 0)
      start = 0;

    if(end > inList.size())
      end = inList.size();

    if(start > end || start > inList.size())
      start = end;

    return inList.subList(start, end);
  }

  //........................................................................

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
      EasyMock.expect(m_request.getRequestURI()).andReturn("/request/uri")
        .anyTimes();
      EasyMock.expect(m_response.getOutputStream()).andReturn(m_output);
      EasyMock.expect(m_request.getUser()).andStubReturn(null);
      EasyMock.expect(m_request.getPagination())
        .andStubReturn(new Pair<Integer, Integer>(inStart, inEnd));
      EasyMock.replay(m_request, m_response);

      return new EntryListServlet()
        {
          private static final long serialVersionUID = 1L;

          @Override
          public List<AbstractEntry> getEntries(String inID)
          {
            return inEntries;
          }

          @Override
          public String getTitle(String inID)
          {
            return "Title";
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
                      (new net.ixitxachitls.dma.entries.BaseEntry
                       ("guru1", new net.ixitxachitls.dma.data
                                     .DMAData("path")),
                       new net.ixitxachitls.dma.entries.BaseEntry
                       ("guru2", new net.ixitxachitls.dma.data
                                     .DMAData("path")),
                       new net.ixitxachitls.dma.entries.BaseEntry
                       ("guru3", new net.ixitxachitls.dma.data
                                     .DMAData("path"))),
                      "/baseentry", 0, 42);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content",
                   "    \n"
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
                   + "</table>\n",
                   m_output.toString());
    }

    //......................................................................
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}