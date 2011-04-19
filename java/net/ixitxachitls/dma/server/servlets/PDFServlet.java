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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.output.pdf.PDFDocument;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The base servlet for pdf files.
 *
 *
 * @file          PDFServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ThreadSafe
public abstract class PDFServlet extends DMAServlet
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ PDFServlet ------------------------------

  /**
   * Create the servlet.
   *
   */
  public PDFServlet()
  {

  }

  //........................................................................

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
    (@Nonnull DMARequest inRequest,
     @Nonnull HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    // Set the output header.ß
    inResponse.setHeader("Content-Type", "applicaton/pdf");
    inResponse.setHeader("Cache-Control", "max-age=0");

    PDFDocument document = createDocument(inRequest);

    if(!document.write(inResponse.getOutputStream()))
      return new HTMLError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                           "PDF Writing Failed",
                           "Could not successfully write the requested "
                            + "pdf file.");

    return null;
  }

  //........................................................................
  //---------------------------- createDocument ----------------------------

  /**
   * Create and populate the pdf document for printing.
   *
   * @param     inRequest the request for the page
   *
   * @return    the PDF document to return with all its contents
   *
   */
  protected abstract @Nonnull PDFDocument createDocument
    (@Nonnull DMARequest inRequest);

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.server.ServerUtils.Test
  {
    //----- handle ---------------------------------------------------------

    /** The handle Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void handle() throws Exception
    {
      PDFServlet servlet = new PDFServlet() {
          private static final long serialVersionUID = 1L;
          protected PDFDocument createDocument(@Nonnull DMARequest inRequest)
          {
            PDFDocument test = new PDFDocument("test");
            test.setAlignment(net.ixitxachitls.output.Buffer.Alignment.left);
            test.add(new net.ixitxachitls.output.commands
                     .Left("This is just some test file"));

            return test;
          }
        };

      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);;
      MockServletOutputStream output = new MockServletOutputStream();

      response.setHeader("Content-Type", "applicaton/pdf");
      response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(response.getOutputStream()).andReturn(output);

      EasyMock.replay(request, response);

      assertNull(servlet.handle(request, response));
      assertEquals("content", "%PDF-1.4\n%",
                   output.toString().substring(0, 10));

      EasyMock.verify(request, response);
    }

    //......................................................................
  }

  //........................................................................
}
