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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.BaseType;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.output.html.HTMLWriter;
import net.ixitxachitls.util.Files;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The servlet for the library of base entries.
 *
 *
 * @file          LibraryServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class LibraryServlet extends PageServlet
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- LibraryServlet ----------------------------

  /**
   * Create the servlet.
   *
   */
  public LibraryServlet()
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

  //------------------------------- writeBody ------------------------------

  /**
   * Handles the body content of the request.
   *
   * @param     inWriter  the writer to take up the content (will be closed
   *                      by the PageServlet)
   * @param     inPath    the path of the request
   * @param     inRequest the request for the page
   *
   */
  @OverridingMethodsMustInvokeSuper
  protected void writeBody(@Nonnull HTMLWriter inWriter,
                           @Nullable String inPath,
                           @Nonnull DMARequest inRequest)
  {
    inWriter
      .title("DMA Library")
      .begin("h1").add("DMA Library").end("h1");

    Multimap <AbstractType<? extends AbstractEntry>, Index> types =
      TreeMultimap.create();

    for(Index index : ValueGroup.getIndexes())
      types.put(index.getType(), index);

    for(AbstractType<? extends AbstractEntry> type : AbstractType.getAll())
    {
      if(!(type instanceof BaseType) || type == BaseEntry.TYPE)
        continue;

      inWriter
        .begin("a").classes("type-image").href("/" + type.getMultipleLink())
        .onClick("util.link(event, '/" + type.getMultipleLink() + "');")
        .begin("img").classes("type", "highlight")
        .src("/icons/types/" + Files.encodeName(type.getName()) + ".png")
        .alt(type.getMultiple()).end("img")
        .begin("div").classes("caption").add(type.getMultipleLink()).end("div")
        .end("a") // type-image
        .begin("div").classes("type-indexes");

      if(types.get(type) != null)
        for(Index index : types.get(type))
        {
          String link = "/" + type.getMultipleLink() + "/" + index.getPath();

          inWriter
            .begin("a").classes("type-index").href(link)
            .onClick("util.link(event, '" + link + "');")
            .add(index.getTitle())
            .end("a");
        }

      inWriter
        .end("div") // type-indexes
        .begin("div").classes("clear").end("div");
    }

    addNavigation(inWriter, "library");
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test
  //........................................................................
}
