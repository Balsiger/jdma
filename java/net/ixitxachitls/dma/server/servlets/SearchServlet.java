/*******************************************************************************
 * Copyright (c) 2002-2016 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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
 ******************************************************************************/

package net.ixitxachitls.dma.server.servlets;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.labs.repackaged.com.google.common.collect.ImmutableMap;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;

import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.output.soy.SoyRenderer;

/**
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class SearchServlet extends PageServlet
{
  public SearchServlet() {

  }

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  @Override
  public String getTemplateName(DMARequest inRequest,
                                Map<String, SoyData> inData) {
    return "dma.page.search";
  }

  @Override
  protected Map<String, Object> collectData(DMARequest inRequest,
                                            SoyRenderer inSoyRenderer)
  {
    Map<String, Object> data = super.collectData(inRequest, inSoyRenderer);


    String term = inRequest.getOriginalPath().replace("/search/", "");
    try
    {
      term = URLDecoder.decode(term, Charsets.UTF_8.name());
    } catch(UnsupportedEncodingException inE)
    {
    }
    data.put("search", term);

    QueryOptions options = QueryOptions.newBuilder()
        .setLimit(20)
        .setFieldsToSnippet(BaseEntry.FIELD_DESCRIPTION)
        .setFieldsToReturn(BaseEntry.FIELD_NAME, BaseEntry.FIELD_LINK,
                           BaseEntry.FIELD_TYPE)
        .build();
    Query query = Query.newBuilder()
        .setOptions(options)
        .build(term);

    List<Map<String, String>> entries = new ArrayList<>();
    Results<ScoredDocument> results = BaseEntry.INDEX_ENTRIES.search(query);
    for(ScoredDocument document : results) {
      String snippet = "";
      for(Field field : document.getExpressions())
        snippet += field.getHTML();

      entries.add(ImmutableMap.<String, String>builder()
                      .put("type", document.getOnlyField("type").getText())
                      .put("name", document.getOnlyField("name").getText())
                      .put("link", document.getOnlyField("link").getText())
                      .put("snippet", snippet)
                      .build());
    }

    data.put("entries", entries);

    return data;
  }
}
