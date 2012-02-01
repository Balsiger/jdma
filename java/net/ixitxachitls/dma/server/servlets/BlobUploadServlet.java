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
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.data.DMADatastore;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.output.html.HTMLWriter;
import net.ixitxachitls.server.ServerUtils;
import net.ixitxachitls.server.servlets.BaseServlet;
import net.ixitxachitls.util.Files;
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
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

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
  private @Nonnull BlobstoreService m_blobs =
    BlobstoreServiceFactory.getBlobstoreService();

  /** The image service to serve images. */
  private @Nonnull ImagesService m_image =
    ImagesServiceFactory.getImagesService();

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
  public @Nullable SpecialResult handle(@Nonnull HttpServletRequest inRequest,
                                        @Nonnull HttpServletResponse inResponse)
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

    HTMLWriter writer =
      new HTMLWriter(new PrintWriter(inResponse.getOutputStream()));

    if(request.getParam("form") == null)
    {
      String id = request.getParam("id");
      if(id == null)
        return new TextError(HttpServletResponse.SC_BAD_REQUEST, "no id given");


      AbstractType<? extends AbstractEntry> type =
        AbstractType.getTyped(request.getParam("type"));
      if(type == null)
        return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                             "invalid type " + request.getParam("type"));

      DMADatastore store = (DMADatastore)DMADataFactory.getBaseData();
      AbstractEntry entry = store.getEntry(id, type);

      if(entry == null)
        return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                             "could not find " + type + " " + id);


      String file = request.getParam("filename");
      String name = request.getParam("name");
      String filename = name;
      if(file != null && !"main".equals(name))
        filename = Files.file(file);

      if(name == null)
        return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                             "no name given");

      if(request.getParam("delete") != null)
      {
        store.removeFile(entry, filename);

        writer
          .script("parent.window.edit.updateImage('file-" + filename
                  + "', '/icons/products-dummy.png', null, 'upload-" + name
                  + "');");

        writer.close();
        return null;
      }

      Map<String, BlobKey> blobs = m_blobs.getUploadedBlobs(inRequest);
      BlobKey key = blobs.get("file");

      if(key == null)
        return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                             "No file uploaded");

      if(file == null)
        return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                             "no file name given");

      String fileType = URLConnection.getFileNameMap().getContentTypeFor(file);

      store.addFile(entry, filename, fileType, key);

      Log.event(request.getUser().getName(), "upload",
                "Uploaded " + fileType + " file " + file + " for " + type + " "
              + id);


      String url =  m_image.getServingUrl(key);
      writer
        .script("parent.window.edit.updateImage('file-" + filename + "', '"
                + url + "=s300', 'util.link(event, \"" + url + "\");', "
                + "'upload-" + name + "');");
        ;

      writer.close();
    }
    else
    {
      // return the form to upload
      if(request.getParam("id") == null || request.getParam("type") == null)
        return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                             "invalid arguments given");

     writer
        .addCSSFile("jdma")
        .addJSFile("jdma")
        .begin("div").classes("file-upload")

        .begin("div")
        .classes("sprite image-cancel")
        .attribute("title", "Cancel")
        .attribute("onclick",
                   "parent.window.edit.updateImage('file-"
                   + request.getParam("name") + "', null, "
                   + "null, 'upload-" + request.getParam("name") + "');")
        .end("div")

        .begin("form")
        .attribute("action", m_blobs.createUploadUrl("/fileupload"))
        .attribute("method", "post")
        .attribute("enctype", "multipart/form-data")
        .classes("upload")

        .begin("input")
        .attribute("type", "file")
        .attribute("name", "file")
        .attribute("onchange",
                   "this.parentNode['filename'].value = this.value; "
                   + "this.parentNode.submit();")
        .end("input")

        .begin("input")
        .attribute("type", "hidden")
        .attribute("name", "id")
        .attribute("value", request.getParam("id"))
        .end("input")

        .begin("input")
        .attribute("type", "hidden")
        .attribute("name", "type")
        .attribute("value", request.getParam("type"))
        .end("input")

        .begin("input")
        .attribute("type", "hidden")
        .attribute("name", "filename")
        .attribute("value", "")
        .end("input");

      if(request.getParam("name") != null)
        writer
          .begin("input")
          .attribute("type", "hidden")
          .attribute("name", "name")
          .attribute("value", request.getParam("name"))
          .end("input");

      if("main".equals(request.getParam("name")))
        writer
          .begin("input")
          .attribute("type", "submit")
          .attribute("name", "delete")
          .attribute("value", "Remove")
          .classes("submit")
          .end("input");

      writer
        .end("form")
        .end("div");

      writer.close();
    }

    return null;
  }

  //........................................................................
}