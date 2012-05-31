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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.output.soy.SoyEntry;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The base handler for all indexes.
 *
 * @file          IndexServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class IndexServlet extends PageServlet
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- IndexServlet -----------------------------

  /**
   * Create the servlet for indexes.
   */
  public IndexServlet()
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
  //........................................................................

  //------------------------------------------------- other member functions

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
      data.put("content",
               inRenderer.render("dma.errors.invalidPage",
                                 map("name", inRequest.getOriginalPath())));

      return data;
    }

    String []match =
      Strings.getPatterns(path, "^/_index/([^/]+)/([^/]+)(?:/(.*$))?");
    AbstractType<? extends AbstractEntry> type =
      AbstractType.getTyped(match[0]);
    String name = match[1];
    String group = match[2];

    if(name == null || name.isEmpty() || type == null)
    {
      data.put("content",
               inRenderer.render("dma.errors.invalidPage",
                                 map("name", inRequest.getOriginalPath())));

      return data;
    }

    name = name.replace("%20", " ");

    if(group != null)
      group = group.replace("%20", " ");

    // determine the index to use
    Index index = ValueGroup.getIndex(name);
    if(index == null)
    {
      data.put("content",
               inRenderer.render("dma.errors.invalidPage",
                                 map("name", inRequest.getOriginalPath())));

      return data;
    }

    Log.info("serving dynamic " + type + " index '" + name + "/"
             + group + "'");

    String title = index.getTitle();
    if(group == null)
    {
      // get all the index groups available
      SortedSet<String> indexes =
        DMADataFactory.get().getIndexNames(name, type, false);

      if(indexes.size() == 1)
        group = indexes.iterator().next();
      else
      {
        if(isNested(indexes))
        {
          SortedMap<String, List<String>> groups = nestedGroups(indexes);
          data.put("content",
                   inRenderer.render
                   ("dma.entry.indexoverview",
                    map("title", title,
                        "indexes", groups,
                        "keys", new ArrayList<String>(groups.keySet()),
                        "name", name),
                    ImmutableSet.of(type.getName().replace(" ", ""))));
        }
        else
          data.put("content",
                   inRenderer.render
                   ("dma.entry.indexoverview",
                    map("title", title,
                        "indexes", new ArrayList<String>(indexes),
                        "name", name),
                    ImmutableSet.of(type.getName().replace(" ", ""))));

        return data;
      }
    }

    title += " - " + group.replace("::", " ");

    List<? extends AbstractEntry> rawEntries =
      DMADataFactory.get().getIndexEntries(name, type, null, group,
                                           inRequest.getStart(),
                                           inRequest.getPageSize() + 1);

    List<SoyEntry> entries = new ArrayList<SoyEntry>();
    for(AbstractEntry entry : rawEntries)
      entries.add(new SoyEntry(entry, inRenderer));

    data.put("content",
             inRenderer.render
             ("dma.entry.index",
              map("title", title,
                  "name", name,
                  "start", inRequest.getStart(),
                  "pagesize", inRequest.getPageSize(),
                  "entries", entries),
              ImmutableSet.of(type.getName().replace(" ", ""))));

    return data;
  }

  //........................................................................
  //----------------------------- nestedGroups -----------------------------

  /**
   * Generate the data structure for nested groups.
   *
   * @param  inValues the index groups
   *
   * @return A sorted map of index groups to indexs pages
   *
   */
  public static @Nonnull SortedMap<String, List<String>>
    nestedGroups(@Nonnull SortedSet<String> inValues)
  {
    SortedSetMultimap<String, String> groups = TreeMultimap.create();
    for(String value : inValues)
    {
      String []parts = Index.stringToGroups(value);
      if(parts.length >= 2)
        groups.put(parts[0], parts[1]);
      else
        groups.put(parts[0], "");
    }

    SortedMap<String, List<String>> result =
      new TreeMap<String, List<String>>();
    for (String key : groups.keys())
      result.put(key, new ArrayList<String>(groups.get(key)));

    return result;
  }

  //........................................................................
  //------------------------------- isNested -------------------------------

  /**
   * Check if the index values represent a nested index or not.
   *
   * @param   inValues the index groups
   *
   * @return  true if nested groups are given, false if not
   *
   */
  public static boolean isNested(@Nonnull SortedSet<String> inValues)
  {
    for(String value : inValues)
      if(value.contains("::"))
        return true;

    return false;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }

  //........................................................................
}

