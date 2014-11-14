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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.output.html.JsonWriter;

/**
 * The base servlet for autocomplete requests.
 *
 *
 * @file          Autocomplete.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@Immutable
@ParametersAreNonnullByDefault
public class Autocomplete extends JSONServlet
{
  /**
   * Create the autocomplete servlet.
   *
   */
  public Autocomplete()
  {
    // nothing to do here
  }

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /** The maximal number of results to return. */
  private static final int s_max = 20;

  /** The cache for the values. */
  private static final Map<String, SortedSet<String>> s_cache =
    Maps.newHashMap();

  /** The joiner for keys. */
  private static final Joiner s_keyJoiner = Joiner.on("//");

  @Override
  protected synchronized void writeJson(DMARequest inRequest,
                                        String inPath,
                                        JsonWriter inWriter)
  {
    // compute the index involved
    String []parts = inPath.replace("%20", " ").split("/");

    if(parts.length > 3)
    {
      String term = normalize(inRequest.getParam("term"));
      AbstractType<? extends AbstractEntry> type =
        AbstractType.getTyped(parts[2]);
      String field = parts[3];
      String []keys = Arrays.copyOfRange(parts, 2, parts.length);

      if(type != null && field != null)
      {
        Collection<String> items;
        if("name".equals(field))
          items = DMADataFactory.get().getIDs(type, null);
        else
        {
          ensureCached(type, field);
          items = cached(keys);
        }

        List<String> names = Lists.newArrayList();
        if(items != null)
        {
          for(String name : items)
          {
            if(match(name, term))
              names.add(name);

            if(names.size() > s_max)
              break;
          }
        }

        inWriter.strings(names);
        return;
      }
    }

    inWriter.strings(ImmutableList.of("* Error computing autocomplete *"));
  }

  /**
   * Check if the given name matches the given autocomplete string.
   *
   * @param    inName the name to match against
   * @param    inAuto the autocomplete value to match with
   *
   * @return   true if the values match, false if not
   *
   */
  private boolean match(String inName, @Nullable String inAuto)
  {
    if(inAuto == null || inAuto.isEmpty())
      return true;

    if(inName.regionMatches(true, 0, inAuto, 0, inAuto.length()))
      return true;

    String []nameParts = inName.split(" ");
    String []autoParts = inAuto.split(" ");
    for(int i = 0; i < autoParts.length; i++)
      if(nameParts.length <= i || !nameParts[i].regionMatches
         (true, 0, autoParts[i], 0, autoParts[i].length()))
        return false;

    return true;
  }

  /**
   * Normalize the given name for comparison.
   *
   * @param    inName the name to normalize
   *
   * @return   the normalized name
   */
  private String normalize(String inName)
  {
    return inName.replaceAll(" +", " ");
  }

  /**
   * Ensure that the desired values are in the cache, loading them if
   * necessary. Note that this is an expensive operation.
   *
   * @param       inType  the type of entries to auto complete
   * @param       inField the field with the autocomplete values
   *
   */
  public void ensureCached(AbstractType<? extends AbstractEntry> inType,
                           String inField)
  {
    // Check if already cached.
    SortedSet<String> values = cached(inType.toString(), inField);
    if(values != null)
      return;

    values = DMADataFactory.get().getValues(inType, inField);
    cache(values, inType.toString(), inField);
  }

  /**
   * Cache the given value with the given keys.
   *
   * @param       inValues the values to store
   * @param       inKeys   the key parts to store with
   *
   */
  public void cache(SortedSet<String> inValues, String ... inKeys)
  {
    s_cache.put(s_keyJoiner.join(inKeys), inValues);
  }

  /**
   * Get the cached value for the given keys.
   *
   * @param   inKeys the key parts for the cached value
   *
   * @return  the cached value or null if not cached
   *
   */
  public @Nullable SortedSet<String> cached(String ... inKeys)
  {
    return s_cache.get(s_keyJoiner.join(inKeys));
  }

  //----------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.server.ServerUtils.Test
  {
    /**
     * The handle Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void handle() throws Exception
    {
      HttpServletRequest request =
        EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      try (MockServletOutputStream output = new MockServletOutputStream())
      {
        EasyMock.expect(request.getMethod()).andReturn("POST");
        EasyMock.expect(request.getRequestURI()).andStubReturn("uri");
        response.setHeader("Content-Type", "application/json");
        response.setHeader("Cache-Control", "max-age=0");
        EasyMock.expect(response.getOutputStream()).andReturn(output);
        EasyMock.replay(request, response);

        Autocomplete servlet = new Autocomplete() {
            /** Serial version id. */
            private static final long serialVersionUID = 1L;
            @Override
            protected void writeJson(DMARequest inRequest,
                                     String inPath,
                                     JsonWriter inWriter)
            {
              inWriter.add(inPath);
            }
          };

        servlet.doPost(request, response);
        assertEquals("post", "uri", output.toString());

        EasyMock.verify(request, response);
      }
    }
  }
}
