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
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.data.DMADatastore;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A servlet to import files to the blob store.
 *
 *
 * @file          BlobImportServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class BlobImportServlet extends HttpServlet
{
  //--------------------------------------------------------- constructor(s)

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //........................................................................

  //----------------------------------------------------------- manipulators

  //........................................................................

  //------------------------------------------------- other member functions

  //-------------------------------- doPost --------------------------------

  /**
   * Post information to the blob store.
   *
   * @param       inRequest  the http request
   * @param       inResponse the http response sent back
   *
   * @throws      ServletException can happen
   * @throws      IOException this too...
   *
   */
  @Override
public void doPost(@Nonnull HttpServletRequest inRequest,
                     @Nonnull HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    if(!(inRequest instanceof DMARequest))
    {
      Log.error("expected a DMA request here, probably not filtered through "
                + "DMAFilter");
      return;
    }

    DMARequest request = (DMARequest)inRequest;

    String type = request.getParam("type");
    // fall back to png image
    if(type == null)
      type = "image/png";

    String name = request.getParam("name");
    if(name == null)
      name = "unknown";

    PrintWriter writer = new PrintWriter(inResponse.getOutputStream());

    try
    {
      String id = request.getParam("id");
      AbstractType<? extends AbstractEntry> entryType =
        AbstractType.getTyped(request.getParam("entry"));


      if(id == null || entryType == null)
      {
        Log.warning("ignoring file " + name + " without id or entry type");
        writer.println("File ignored, not id or entry type given");
      }
      else
      {
        DMADatastore store = (DMADatastore)DMADataFactory.get();
        @SuppressWarnings("unchecked")
        AbstractEntry entry =
          store.getEntry(new AbstractEntry.EntryKey(id, entryType));

        if(entry == null)
        {
          Log.warning("ignoring file " + name + " without matching " + entryType
                      + " " + id);
          writer.println("File ignored, no matching entry found");
        }
        else
        {
          Log.event("admin", "import", "Importing blob " + name + " of type "
                    + type + " for " + entryType + " with id " + id);

          FileService fileService = FileServiceFactory.getFileService();
          AppEngineFile file = fileService.createNewBlobFile(type, name);

          InputStream input = inRequest.getInputStream();
          byte []buffer = new byte[1024 * 100];
          FileWriteChannel channel = fileService.openWriteChannel(file, true);

          for(int read = input.read(buffer); read > 0;
              read = input.read(buffer))
            channel.write(ByteBuffer.wrap(buffer, 0, read));

          // cleanup
          channel.closeFinally();
          input.close();

          // Add a reference to the path to the datastore.
          store.addFile(entry, name, type, fileService.getBlobKey(file));

          writer.println("OK");
        }
      }
    }
    finally
    {
      writer.close();
    }
  }

  //........................................................................

  //........................................................................
}
