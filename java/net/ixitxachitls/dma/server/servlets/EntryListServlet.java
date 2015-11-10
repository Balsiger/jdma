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

package net.ixitxachitls.dma.server.servlets;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyData;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.dma.output.soy.SoyValue;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

/**
 * A page servlet to serve a list of values.
 *
 *
 * @file          ListServlet.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@Immutable
public class EntryListServlet extends PageServlet
{
  /**
   * Create the servlet.
   */
  public EntryListServlet()
  {
  }

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Get the entries in the given page range.
   *
   * @param       inRequest the original request
   * @param       inPath    the path used to access the entries
   * @param       inType    the type of entries to get
   * @param       inStart   the index where to start to get entries
   * @param       inSize    the maximal number of entries to return
   *
   * @return      a list of all entries in range
   */
  @SuppressWarnings("unchecked") // need to cast
  public List<AbstractEntry>
    getEntries(DMARequest inRequest, String inPath,
               AbstractType<? extends AbstractEntry> inType,
               int inStart, int inSize)
  {
    return (List<AbstractEntry>)DMADataFactory.get()
      .getEntries(inType, Optional.<EntryKey>absent(), inStart, inSize);
  }

  @Override
  public String getTemplateName(DMARequest inRequest,
                                Map<String, SoyData> inData)
  {
    String template = inData.get("template").toString();
    if(template == null)
    {
      return "dma.errors.extract";
    }

    return template;
  }

  /**
   * Collect the data that is to be printed.
   *
   * @param    inRequest  the request for the page
   * @param    inRenderer the renderer to render sub values
   *
   * @return   a map with key/value pairs for data (values can be primitives
   *           or maps or lists)
   */
  @Override
  protected Map<String, Object> collectData(DMARequest inRequest,
                                            SoyRenderer inRenderer)
  {
    Map<String, Object> data = super.collectData(inRequest, inRenderer);

    String path = inRequest.getRequestURI();
    String typeName = "";
    if(path != null)
      typeName = Strings.getPattern(path, "([^/]*)/?$");

    Optional<? extends AbstractType<? extends AbstractEntry>> type =
      AbstractType.getTyped(typeName);
    if(!type.isPresent())
    {
      data.put("type", typeName);
      data.put("template", "dma.error.invalidType");
      return data;
    }

    String title = Encodings.toWordUpperCase(type.get().getMultipleLink());
    Log.info("serving dynamic list " + title);

    List<AbstractEntry> rawEntries = getEntries(inRequest, path, type.get(),
                                                inRequest.getStart(),
                                                inRequest.getPageSize() + 1);

    List<SoyValue> entries = new ArrayList<>();
    for(AbstractEntry entry : rawEntries)
      entries.add(new SoyValue(entry.getKey().toString(), entry));

    data.put("title", title);
    data.put("entries", entries);
    data.put("label", title.toLowerCase(Locale.US));
    data.put("path", path);
    data.put("pagesize", inRequest.getPageSize());
    data.put("start", inRequest.getStart());
    data.put("template",
             "dma.entries." + type.get().getMultipleDir().toLowerCase()
             + ".list");

    return data;
  }
}
