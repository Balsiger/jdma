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

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.output.pdf.PDFDocument;
import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.commands.Left;
import net.ixitxachitls.output.commands.Title;
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
   */
  public EntryPDFServlet()
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
  @Override
  protected @Nonnull Document createDocument(@Nonnull DMARequest inRequest)
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

    //PDFDocument document = new PDFDocument(title);
    Document document =
      new net.ixitxachitls.dma.output.html.HTMLDocument(title);
    document.add(entry.print(inRequest.getUser()));

    return document;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................
}
