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

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.entries.Item;
import net.ixitxachitls.dma.output.pdf.PDFDocument;
import net.ixitxachitls.dma.output.soy.SoyEntry;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
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

    SoyRenderer renderer = new SoyRenderer(s_template);
    // we have to collect injected data before other data to have it available
    // when collecting
    renderer.setInjected(collectInjectedData(inRequest, renderer));
    renderer.setData(collectData(inRequest, renderer));
    document.add(renderer.render(getTemplateName(inRequest)));

    return document;
  }

  //........................................................................

  //----------------------------- collectData ------------------------------

  /**
   * Collect the data that is to be printed.
   *
   * @param    inRequest  the request for the page
   * @param    inRenderer the renderer to render sub values
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
    if(path == null)
    {
      data.put("content", inRenderer.render("dma.error.noEntry"));
      return data;
    }

    boolean dma = path.endsWith(".dma");
    if(dma)
      path = path.substring(0, path.length() - 4);

    AbstractEntry entry = getEntry(path);
    if(entry != null && !entry.isShownTo(inRequest.getUser()))
    {
      data.put("content", inRenderer.render
               ("dma.errors.invalidPage",
                map("name", inRequest.getAttribute(DMARequest.ORIGINAL_PATH))));

      return data;
    }

    if(entry == null)
    {
      AbstractEntry.EntryKey<? extends AbstractEntry> key = extractKey(path);
      if(key == null)
      {
        data.put("content", inRenderer.render("dma.errors.extract",
                                              map("name", path)));
        return data;
      }

      AbstractType<? extends AbstractEntry> type = key.getType();
      String id = key.getID();

      if(inRequest.hasParam("create") && inRequest.hasUser())
      {
        // create a new entry for filling out
        Log.info("creating " + type + " '" + id + "'");

        if(type.getBaseType() == type)
          entry = type.create(id);
        else
        {
          String postfix = "";
          if(inRequest.hasParam("store"))
            postfix = "-" + inRequest.getParam("store");

          entry = type.create(Entry.TEMPORARY + postfix);
          entry.updateKey(key);

          if(inRequest.hasParam("bases"))
            for(String base : inRequest.getParam("bases").split("\\s*,\\s*"))
              entry.addBase(base);

          if(inRequest.hasParam("identified") && entry instanceof Item)
            ((Item)entry).identify();

          if(inRequest.hasParam("extensions"))
            for(String extension
                  : inRequest.getParam("extensions").split("\\s*,\\s*"))
              if(extension != null && !extension.isEmpty())
                entry.addExtension(extension);
          else
            entry.addBase(id);

          if(entry instanceof Entry)
            ((Entry)entry).complete();
        }
        entry.setOwner(inRequest.getUser());
      }

      if(entry == null)
      {
        data.put("content", inRenderer.render("dma.entry.create",
                                              map("id", id,
                                                  "type", type.getName())));
        return data;
      }
    }

    AbstractType<? extends AbstractEntry> type = entry.getType();
    List<String> ids = DMADataFactory.get().getIDs(type, null);

    int current = ids.indexOf(entry.getName());
    int last = ids.size() - 1;

    String template;
    String extension;
    if(dma)
    {
      extension = ".dma";
      template = "dma.entry.dmacontainer";
    }
    else
    {
      extension = "";
      template = "dma.entry.container";
    }

    data.put("content",
             inRenderer.render
             (template,
              map("entry",
                  new SoyEntry(entry, inRenderer),
                  "first", current <= 0 ? "" : ids.get(0) + extension,
                  "previous",
                  current <= 0 ? "" : ids.get(current - 1) + extension,
                  "list", "/" + entry.getType().getMultipleLink(),
                  "next",
                  current >= last ? "" : ids.get(current + 1) + extension,
                  "last",
                  current >= last ? "" : ids.get(last) + extension),
              ImmutableSet.of(type.getName().replace(" ", ""))));

    return data;
  }

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
    BaseCharacter user = inRequest.getUser();
    AbstractEntry entry = getEntry(inRequest.getRequestURI());

    Map<String, Object> data = super.collectInjectedData(inRequest, inRenderer);

    data.put("isDM", user != null && entry != null && entry.isDM(user));
    data.put("isOwner", user != null && entry != null && entry.isOwner(user));

    return data;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................
}
