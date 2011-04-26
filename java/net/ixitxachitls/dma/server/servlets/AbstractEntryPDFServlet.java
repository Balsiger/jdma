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
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.output.pdf.PDFDocument;
import net.ixitxachitls.output.commands.Left;
import net.ixitxachitls.output.commands.Title;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The base servlet for entry printing.
 *
 * @file          AbstractEntryServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public abstract class AbstractEntryPDFServlet extends PDFServlet
{
  //--------------------------------------------------------- constructor(s)

  //------------------------ AbstractEntryPDFServlet -----------------------

  /**
   * The basic constructor for the servlet.
   *
   * @param   inData all the available data
   *
   */
  public AbstractEntryPDFServlet(@Nonnull DMAData inData)
  {
    m_data = inData;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** All the available data. */
  protected @Nonnull DMAData m_data;

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

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
  //------------------------------- getEntry -------------------------------

  /**
   * Get the abstract entry associated with the given request.
   *
   * @param       inPath the path to the page
   *
   * @return      the entry or null if it could not be found
   *
   */
  public abstract @Nullable AbstractEntry getEntry(@Nonnull String inPath);

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

    boolean dm = entry.isDM(inRequest.getUser());
    document.add(entry.printPage(dm));

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
    private DMARequest m_request;

    /** The response used in the test. */
    private HttpServletResponse m_response;

    /** The output of the test. */
    private net.ixitxachitls.server.ServerUtils.Test.MockServletOutputStream
      m_output;

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
    public AbstractEntryPDFServlet createServlet
      (String inPath, final AbstractEntry inEntry) throws Exception
    {
      m_response.setHeader("Content-Type", "applicaton/pdf");
      m_response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(m_request.getPathInfo()).andReturn(inPath);
      EasyMock.expect(m_response.getOutputStream()).andReturn(m_output);
      EasyMock.expect(m_request.getUser()).andReturn(null).anyTimes();
      EasyMock.replay(m_request, m_response);

      return new AbstractEntryPDFServlet(new DMAData("path"))
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
      AbstractEntryPDFServlet servlet =
        createServlet("/baseentry/guru",
                      new BaseEntry("guru", new net.ixitxachitls.dma.data
                                    .DMAData("path")));

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
      AbstractEntryPDFServlet servlet = createServlet(null, null);

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
      AbstractEntryPDFServlet servlet =
        createServlet("/baseentry/guru", null);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content", "%PDF-1.4\n%",
                   m_output.toString().substring(0, 10));

      m_logger.addExpected("WARNING: could not extract entry from "
                           + "'/baseentry/guru'");
    }

    //......................................................................
  }

  //........................................................................
}
