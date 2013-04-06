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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.conversion.Asset;
import com.google.appengine.api.conversion.Conversion;
import com.google.appengine.api.conversion.ConversionResult;
import com.google.appengine.api.conversion.ConversionService;
import com.google.appengine.api.conversion.ConversionServiceFactory;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.output.Document;
import net.ixitxachitls.util.logging.Log;
import net.ixitxachitls.util.resources.TemplateResource;

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
public abstract class PDFServlet extends SoyServlet
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ PDFServlet ------------------------------

  /**
   * Create the servlet.
   *
   */
  public PDFServlet()
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
  @Override
  protected @Nullable SpecialResult handle
    (@Nonnull DMARequest inRequest,
     @Nonnull HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    Log.info("creating pdf for " + inRequest.getOriginalPath());

    Document document = createDocument(inRequest);
    String content = document.toString();

    if(isDev())
    {
      // Set the output header.
      inResponse.setHeader("Content-Type", "text/html");
      inResponse.setHeader("Cache-Control", "max-age=0");

      if(!document.write(inResponse.getOutputStream()))
        return new HTMLError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                             "PDF Writing Failed",
                             "Could not successfully write the requested "
                             + "pdf file.");
    }
    else
    {
      // Set the output header.
      inResponse.setHeader("Content-Type", "application/pdf");
      inResponse.setHeader("Cache-Control", "max-age=0");

      // Remove links as they are not supported and might break rendering.
      content = content.replaceAll("<link\\s+.*?>", "");

      // Create a conversion request from HTML to PDF.
      List<Asset> assets = new ArrayList<Asset>();
      assets.add(new Asset("text/html", content.getBytes(Charsets.UTF_8),
                           "pdf conversion"));

      // Determine all image assets.
      Matcher matcher =
        Pattern.compile("<img\\s+[^>]*src=\"(.*?)\".*?>").matcher(content);
      while(matcher.find())
      {
        Asset asset = createImageAsset(matcher.group(1));
        assets.add(asset);
        Log.warning("adding asset for " + matcher.group(1) + " / "
                    + asset.getName() + " / " + asset.getMimeType());
      }

      com.google.appengine.api.conversion.Document doc =
        new com.google.appengine.api.conversion.Document(assets);
      Conversion conversion = new Conversion(doc, "application/pdf");

      ConversionService service =
        ConversionServiceFactory.getConversionService();
      ConversionResult result = service.convert(conversion);

      if(result.success())
        // Note: in most cases, we will return data all in one asset,
        // except that we return multiple assets for multi-page images.
        for (Asset resultAsset : result.getOutputDoc().getAssets())
          inResponse.getOutputStream().write(resultAsset.getData());
      else
        return new HTMLError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                             "PDF Writing Failed",
                             "Could not successfully write the requested "
                             + "pdf file (" + result.getErrorCode() + ")");
    }

    return null;
  }

  //--------------------------- createImageAsset ---------------------------

  /**
   * Create an image asset for an image in the pdf file.
   *
   * @param       inName the name of the image
   *
   * @return      the asset that can be added output document
   *
   */
  private @Nonnull Asset createImageAsset(@Nonnull String inName)
  {
    if(inName.startsWith("http://"))
      return createURLImageAsset(inName);
    else
      return createFileImageAsset(inName);
  }

  //........................................................................
  //------------------------- createURLImageAsset --------------------------

  /**
   * Create an url based image asset. This is used to include images served by
   * app engine.
   *
   * @param       inName the name of the image
   *
   * @return      the asset to read from an url
   *
   */
  private @Nonnull Asset createURLImageAsset(@Nonnull String inName)
  {
    for(int i = 1; i <= 5; i++)
    {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      InputStream input = null;

      try
      {
        try
        {
          URLConnection connection = new URL(inName).openConnection();

          byte[] buffer = new byte[100 * 1024];
          input = connection.getInputStream();

          for(int read = input.read(buffer); read > 0;
              read = input.read(buffer))
            output.write(buffer, 0, read);

          return new Asset("image/png", output.toByteArray(), inName);
        }
        catch(java.io.IOException e)
        {
          Log.error("Deadline exceeded when trying to download from url "
                    + inName + " (retrying " + i + "): " + e);
        }
        finally
        {
          if(input != null)
            input.close();

          output.close();
        }
      }
      catch(java.io.IOException e)
      {
        Log.warning("could not properly close streams");
      }
    }

    return createNotFoundImageAsset(inName);
  }

  //........................................................................
  //------------------------ createFileImageAsset --------------------------

  /**
   * Create a file based image asset to include image files into the output.
   *
   * @param       inName the name of the image
   *
   * @return      the file based assed.
   *
   */
  private @Nonnull Asset createFileImageAsset(@Nonnull String inName)
  {
    try
    {
      String name = inName;
      if(name.startsWith("/"))
        name = name.substring(1);

      byte []content = Files.toByteArray(new File(name));

      return new Asset("image/png", content, inName);
    }
    catch(java.io.IOException e)
    {
      return createNotFoundImageAsset(inName);
    }
  }

  //........................................................................
  //----------------------- createNotFoundImageAsset -----------------------

  /**
   * Create an image asset for images that could not be found.
   *
   * @param       inName the name of the image that was not found
   *
   * @return      the image asset for inclusion in the output
   *
   */
  private @Nonnull Asset createNotFoundImageAsset(@Nonnull String inName)
  {
    try
    {
      byte []content = Files.toByteArray(new File("icons/not_found.png"));
      return new Asset("image/png", content, inName);
    }
    catch(java.io.IOException e2)
    {
      Log.error("could not find not found image icons/not_found.png: " + e2);
      throw new UnsupportedOperationException
        ("cannot complete action because image icons/not_found.png could "
         + "not be found");
    }
  }

  //........................................................................

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
  protected abstract @Nonnull Document createDocument
    (@Nonnull DMARequest inRequest);

  //........................................................................
  //------------------------- collectInjectedData --------------------------

  /**
   * Collect the injected data that is to be printed.
   *
   * @param    inRequest  the request for the page
   * @param    inRenderer the renderer to render sub values
   *
   * @return   a map with key/value pairs for data (values can be primitives
   *           or maps or lists)
   *
   */
  @Override
  protected @Nonnull Map<String, Object> collectInjectedData
    (@Nonnull DMARequest inRequest, @Nonnull SoyRenderer inRenderer)
  {
    Map<String, Object> data = super.collectInjectedData(inRequest, inRenderer);

    data.put("isPDF", true);
    data.put("css_jdma",
             TemplateResource.get("css/jdma.css", "web.template.css")
             .read()
             .replaceAll("url\\(.*?\\)", ""));

    return data;
  }

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, as conversion only works on app engine

  //........................................................................
}
