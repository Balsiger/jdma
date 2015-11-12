/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyData;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCampaign;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.BaseType;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.util.Files;

/**
 * The servlet for the library of base entries.
 *
 * @file          LibraryServlet.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */
@Immutable
public class LibraryServlet extends PageServlet
{
  /**
   * Create the servlet.
   */
  public LibraryServlet()
  {
  }

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

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

    Map<String, List<Map<String, Object>>> indexes =
      new HashMap<String, List<Map<String, Object>>>();

    /* TODO: indexes are currently empty.
    for(Index index : ValueGroup.getIndexes())
    {
      String name = index.getType().getName();
      List<Map<String, Object>> group = indexes.get(name);
      if(group == null)
      {
        group = new ArrayList<Map<String, Object>>();
        indexes.put(name, group);
      }

      group.add(map("title", index.getTitle(),
                    "path", index.getPath()));
    }
    */

    for(List<Map<String, Object>> index : indexes.values())
      Collections.sort(index, new Comparator<Map<String, Object>>()
        {
          @Override
          public int compare(Map<String, Object> inFirst,
                             Map<String, Object> inSecond)
          {
            return ((String)inFirst.get("title")).compareTo
              ((String)inSecond.get("title"));
          }
        });

    List<Map<String, Object>> types = new ArrayList<Map<String, Object>>();
    for(AbstractType<? extends AbstractEntry> type : AbstractType.getAll())
    {
      if(!(type instanceof BaseType)
          || type == BaseEntry.TYPE)
        continue;

      types.add(map("name", type.getName(),
                    "file", Files.encodeName(type.getName()),
                    "link", type.getLink(),
                    "multi", type.getMultiple(),
                    "multilink", type.getMultipleLink(),
                    "multidir", type.getMultipleDir(),
                    "multishort", type.getMultiple().replace("Base ", ""),
                    "css", type.getName().replace(" ", "-")));
    }

    data.put("types", types);
    data.put("indexes", indexes);
    data.put("template", "dma.page.library");
    data.put("content",
             inRenderer.render("dma.page.library",
                               Optional.of(map("types", types,
                                               "indexes", indexes))));

    return data;
  }

  @Override
  public String getTemplateName(DMARequest inRequest,
                                Map<String, SoyData> inData)
  {
    return "dma.page.library";
  }
}
