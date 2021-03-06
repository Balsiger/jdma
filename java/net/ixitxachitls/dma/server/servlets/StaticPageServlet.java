/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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

import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Optional;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SanitizedContents;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.shared.restricted.Sanitizers;

import net.ixitxachitls.dma.output.soy.SoyRenderer;

/**
 * A servlet for static HTML dma pages.
 *
 * @file          StaticPageServlet.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */
@Immutable
@ParametersAreNonnullByDefault
public class StaticPageServlet extends PageServlet
{
  /**
   * Default constructor.
   */
  public StaticPageServlet()
  {
    // nothing to do
  }

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  @Override
  protected String getTemplateName(DMARequest inDMARequest,
                                   Map<String, SoyData> inData)
  {
    return "dma.page.print";
  }

  /**
   * Collect the data that is to be printed.
   *
   * @param    inRequest  the request for the page
   * @param    inRenderer the renderer for rendering sub values
   *
   * @return   a map with key/value pairs for data (values can be primitives
   *           or maps or lists)
   */
  @Override
  protected Map<String, Object> collectData(DMARequest inRequest,
                                            SoyRenderer inRenderer)
  {
    String path = inRequest.getRequestURI();
    Map<String, Object> map = super.collectData(inRequest, inRenderer);

    // check the given path for illegal relative stuff and add the root
    if(path == null || path.isEmpty())
      path = "/";

    String name = "dma" + path.replaceAll("/", ".");

    if(name.endsWith("."))
      name += "page.library";
    else if(name.endsWith(".html"))
      name = name.substring(0, name.length() - 5);

    try
    {
      map.put("content", inRenderer.renderStrict(name));
      map.put("title", Sanitizers.cleanHtml("Guru guru"));
    }
    catch(com.google.template.soy.tofu.SoyTofuException e)
    {
      // template could not be loaded
      map.put("content", inRenderer.renderStrictSoy(
          "dma.errors.invalidPage",
          Optional.of(map("name", name,
                          "error", e.toString()))));
    }

    return map;
  }
}
