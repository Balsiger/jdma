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

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableSet;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.output.soy.SoyEntry;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

/**
 * A page servlet to serve a list of values.
 *
 *
 * @file          ListServlet.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@Immutable
@ParametersAreNonnullByDefault
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
      .getEntries(inType, null, inStart, inSize);
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

    AbstractType<? extends AbstractEntry> type =
      AbstractType.getTyped(typeName);
    if(type == null)
    {
      data.put("content", inRenderer.render("dma.error.invalidType",
                                            map("type", typeName)));
      return data;
    }

    String title = Encodings.toWordUpperCase(type.getMultipleLink());
    Log.info("serving dynamic list " + title);

    List<AbstractEntry> rawEntries = getEntries(inRequest, path, type,
                                                inRequest.getStart(),
                                                inRequest.getPageSize() + 1);

    List<SoyEntry> entries = new ArrayList<SoyEntry>();
    for(AbstractEntry entry : rawEntries)
      entries.add(new SoyEntry(entry));

    data.put("content",
             inRenderer.render
             ("dma.entries." + type.getMultipleDir().toLowerCase() + ".list",
              map("title", title,
                  "entries", entries,
                  "label", title.toLowerCase(Locale.US),
                  "path", path,
                  "pagesize", inRequest.getPageSize(),
                  "start", inRequest.getStart()),
              ImmutableSet.of(type.getName().replace(" ", ""))));

    return data;
  }
}
