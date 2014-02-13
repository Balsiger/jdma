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
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.data.DMADatastore;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.output.soy.SoyValue;
import net.ixitxachitls.server.ServerUtils;
import net.ixitxachitls.server.servlets.BaseServlet;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A small servlet to upload blobs. Real uploading of the data is done by the
 * blobstore service, we only need to do the bookeeping here.
 *
 * We can't make this a DMAServlet, as the blobstore redirects to us without
 * going through the DMAFilter, thus we don't have the appropriate DMARequest.
 *
 * @file          BlobServlet.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class BlobUploadServlet extends BaseServlet
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------- BlobUploadServlet ---------------------------

  /**
   * Create the servlet.
   *
   */
  public  BlobUploadServlet()
  {
    // nothing to do here
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /** The blob store service. */
  private BlobstoreService m_blobs =
    BlobstoreServiceFactory.getBlobstoreService();

  /** The image service to serve images. */
  private ImagesService m_image = ImagesServiceFactory.getImagesService();

  //........................................................................

  //-------------------------------------------------------------- accessors

  //........................................................................

  //----------------------------------------------------------- manipulators

  //........................................................................

  //------------------------------------------------- other member functions

  /**
   * Handle requests to download blob.
   *
   * @param inRequest  the http request
   * @param inResponse the http response
   *
   * @return a special result in case of error or null if ok
   *
   * @throws IOException if problems reading the blob
   *
   */
  @Override
  public @Nullable SpecialResult handle(HttpServletRequest inRequest,
                                        HttpServletResponse inResponse)
    throws IOException
  {
    DMARequest request;
    if(inRequest instanceof DMARequest)
      request = (DMARequest)inRequest;
    else
    {
      Multimap<String, String> params = ServerUtils.extractParams(inRequest);
      request = new DMARequest(inRequest, params);
    }

    if(!request.hasUser())
      return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                           "you must be logged in to upload");

    inResponse.setHeader("Content-Type", "text/html");
    inResponse.setHeader("Cache-Control", "max-age=0");

    try(PrintWriter writer = new PrintWriter(inResponse.getOutputStream()))
    {
      String keyName = request.getParam("key");
      if(keyName == null)
        return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                             "no key given");

      if(request.getParam("form") == null)
      {
        AbstractEntry.EntryKey<?> key = DMAServlet.extractKey(keyName);
        if(key == null)
          return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                               "invalid key '" + keyName + "' given");

        DMADatastore store = (DMADatastore)DMADataFactory.get();
        AbstractEntry entry = store.getEntry(key);

        if(entry == null)
          return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                               "could not find " + keyName);

        store.uncacheEntry(key);

        String file = request.getParam("filename");
        String name = request.getParam("name");

        if(name == null)
          return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                               "no name given");

        if(request.getParam("delete") != null)
        {
          if(store.removeFile(entry, name))
            writer.print("parent.window.edit.removeImage('"
                         + request.getParam("id") + "-" + name + "');"
                         + "parent.window.gui.info('Image " + name
                         + " has been removed');");
          else
            writer.print("parent.window.gui.alert('Could not delete image \\'"
                         + name + "\\'');");

          return null;
        }

        Map<String, List<BlobKey>> blobs = m_blobs.getUploads(inRequest);
        List<BlobKey> blobKeys = blobs.get("file");

        if(blobKeys == null)
          return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                               "No file uploaded");

        if(file == null)
          return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                               "no file name given");

        if(blobKeys.size() > 1)
          return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                               "expected a single blob key only");

        BlobKey blobKey = blobKeys.get(0);

        String fileType =
          URLConnection.getFileNameMap().getContentTypeFor(file);

        store.addFile(entry, name, fileType, blobKey);

        Log.event(request.getUser().getName(), "upload",
                  "Uploaded " + fileType + " file " + name + " for " + key);


        String url =
          m_image.getServingUrl(ServingUrlOptions.Builder.withBlobKey(blobKey));
        writer.print("<script>parent.window.edit.addImage('"
                     + request.getParam("id")
                     + "', '" + url + "=s100', '" + name + "');");
        writer.print("parent.window.gui.info('Image " + name
                      + " has been added.');</script>");
      }

      writer.print(SoyValue.COMMAND_RENDERER.render
                   ("dma.page.imageUploadForm",
                    ImmutableMap.<String, Object>of
                    ("url", m_blobs.createUploadUrl("/fileupload"),
                     "key", request.getParam("key"),
                     "id", request.getParam("id"),
                     "name", request.getParam("name")), (Set<String>)null));
    }

    return null;
  }

  //........................................................................
}
