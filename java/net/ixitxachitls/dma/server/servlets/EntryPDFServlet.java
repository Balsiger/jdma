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
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.output.pdf.PDFDocument;
import net.ixitxachitls.output.commands.Left;
import net.ixitxachitls.output.commands.Title;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * An entry servlet that has a single type and gets the id from the path of the
 * request.
 *
 *
 * @file          EntryPDFServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class EntryPDFServlet extends PDFServlet
{
  //--------------------------------------------------------- constructor(s)

  //------------------------- EntryPDFServlet -------------------------

  /**
   * Create the servlet.
   *
   * @param       inData     all the available data
   *
   */
  public EntryPDFServlet(@Nonnull DMAData inData)
  {
    m_data = inData;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** All the available entries. */
  protected @Nonnull DMAData m_data;

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getEntry -------------------------------

  /**
   * Get the abstract entry associated with the given request.
   *
   * @param       inPath the path to the page
   *
   * @return      the entry or null if it could not be found
   *
   */
  public @Nullable AbstractEntry getEntry(@Nonnull String inPath)
  {
    String id = Strings.getPattern(inPath, "/([^/]*?)(\\.pdf)?$");
    AbstractType<? extends AbstractEntry> type = getType(inPath);

    if(type == null || id == null)
      return null;

    return m_data.getEntry(id, type);
  }

  //........................................................................
  //------------------------------- getType --------------------------------

  /**
   * Get the type associated with the given request.
   *
   * @param       inPath the path to the page
   *
   * @return      the type for the request
   *
   */
  public @Nullable AbstractType<? extends AbstractEntry>
    getType(@Nonnull String inPath)
  {
    String type = Strings.getPattern(inPath, ".*/([^/]*?)/");
    if(type == null)
      return null;

    return AbstractType.getTyped(type);
  }

  //........................................................................
  //--------------------------- getLastModified ----------------------------

  /**
    * Get the time of the last modification. Since entries can change anytime,
    * we don't want to have any caching.
    *
    * @return      the time of the last modification in miliseconds or -1
    *              if unknown
    *
    */
  public long getLastModified()
  {
    return -1;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //---------------------------- createDocument ----------------------------

  /**
   * Create and populate the pdf document for printing.
   *
   * @param     inRequest the request for the page
   *
   * @return    the PDF document to return with all its contents
   *
   */
  protected @Nonnull PDFDocument createDocument(@Nonnull DMARequest inRequest)
  {
    String path = inRequest.getRequestURI();
    if(path == null)
    {
      PDFDocument document = new PDFDocument("Error: Not Found");

      document.add(new Title("Invalid Reference"));
      document.add(new Left("The page referenced does not exist!"));

      return document;
    }

    AbstractEntry entry = getEntry(path);
    if(entry == null)
    {
      Log.warning("could not extract entry from '" + path + "'");

      PDFDocument document = new PDFDocument("Error: Not Found");

      document.add(new Title("Entry Not Found"));
      document.add(new Left("Could not find the entry for '" + path + "'!"));

      return document;
    }

    String title = entry.getType() + ": " + entry.getName();

    PDFDocument document = new PDFDocument(title);
    document.add(entry.printPage(inRequest.getUser()));

    return document;
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
    private DMARequest m_request = null;

    /** The response used in the test. */
    private HttpServletResponse m_response = null;

    /** The output of the test. */
    private net.ixitxachitls.server.ServerUtils.Test.MockServletOutputStream
      m_output = null;

    /** Paths encountered while testing. */
    private final List<String> m_paths = new ArrayList<String>();

    //----- setUp ----------------------------------------------------------

    /** Setup the mocks for testing. */
    @org.junit.Before
    public void setUp()
    {
      m_request = EasyMock.createMock(DMARequest.class);
      m_response = EasyMock.createMock(HttpServletResponse.class);;
      m_output =
        new net.ixitxachitls.server.ServerUtils.Test.MockServletOutputStream();
      m_paths.clear();
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
     * @param inPath   the path requested
     * @param inEntry  the entry to return
     *
     * @return the created servlet
     * @throws Exception should not happen
     */
    public EntryPDFServlet createServlet
      (String inPath, final AbstractEntry inEntry) throws Exception
    {
      m_response.setHeader("Content-Type", "application/pdf");
      m_response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(m_request.getRequestURI()).andReturn(inPath);
      EasyMock.expect(m_response.getOutputStream()).andReturn(m_output);
      EasyMock.expect(m_request.getUser()).andReturn(null).anyTimes();
      EasyMock.replay(m_request, m_response);

      return new EntryPDFServlet(new DMAData.Test.Data())
        {
          private static final long serialVersionUID = 1L;

          @Override
          public AbstractEntry getEntry(String inPath)
          {
            m_paths.add(inPath);
            return inEntry;
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
      EntryPDFServlet servlet =
        createServlet("/baseentry/guru", new BaseEntry("guru"));

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content", "%PDF-1.4\n%",
                   m_output.toString().substring(0, 10));
      assertContent("paths", m_paths, "/baseentry/guru");
    }

    //......................................................................
    //----- no path ---------------------------------------------------------

    /**
     * No path test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void noPath() throws Exception
    {
      EntryPDFServlet servlet = createServlet(null, null);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content", "%PDF-1.4\n%",
                   m_output.toString().substring(0, 10));
      assertContent("paths", m_paths);
    }

    //......................................................................
    //----- no entry --------------------------------------------------------

    /**
     * No entry test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void noEntry() throws Exception
    {
      EntryPDFServlet servlet =
        createServlet("/baseentry/guru", null);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content", "%PDF-1.4\n%",
                   m_output.toString().substring(0, 10));

      m_logger.addExpected("WARNING: could not extract entry from "
                           + "'/baseentry/guru'");
    }

    //......................................................................
    //----- path -----------------------------------------------------------

    /** The path Test. */
    @org.junit.Test
    public void path()
    {
      EasyMock.replay(m_request, m_response);

      EntryPDFServlet servlet = new EntryPDFServlet
        (new DMAData.Test.Data(new net.ixitxachitls.dma.entries.BaseEntry
                               ("test")));

      assertEquals("entry", "test",
                   servlet.getEntry("/just/some/base entry/test").getName());
      assertEquals("entry", "test",
                   servlet.getEntry("/base entry/test").getName());
      assertNull("entry", servlet.getEntry("test"));
      assertNull("entry", servlet.getEntry(""));
      assertNull("entry", servlet.getEntry("test/"));
      assertNull("entry", servlet.getEntry("test/guru"));
    }

    //......................................................................
  }

  //........................................................................
}
