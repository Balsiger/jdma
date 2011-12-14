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

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.output.commands.Editable;
import net.ixitxachitls.output.commands.Icon;
import net.ixitxachitls.output.commands.Title;
import net.ixitxachitls.output.html.HTMLWriter;
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
    this(DMADataFactory.getBaseData());
  }

  //........................................................................
  //----------------------------- IndexServlet -----------------------------

  /**
   * Create the servlet for indexes.
   *
   * @param       inData      all the available data
   *
   */
  public IndexServlet(@Nonnull DMAData inData)
  {
    m_data = inData;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /** All the avilable data. */
  protected @Nonnull DMAData m_data;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //........................................................................

  //----------------------------------------------------------- manipulators

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
    if(inPath == null)
    {
      inWriter.title("Not Found")
        .begin("h1").add("Invalid index").end("h1")
        .add("The index referenced does not exist!");
      Log.warning("no path given index request");

      return;
    }

    String []match =
      Strings.getPatterns(inPath,
                          "^/_index/([^/]+)/([^/]+)(?:/(.*$))?");
    AbstractType<? extends AbstractEntry> type = AbstractType.get(match[0]);
    String name = match[1];
    String group = match[2];

    if(name == null || name.isEmpty() || type == null)
    {
      inWriter.title("Not Found")
        .begin("h1").add("Unnamed index").end("h1")
        .add("The index referenced does not exist!");
      Log.warning("unnamed index for request");

      return;
    }

    name = name.replace("%20", " ");

    if(group != null)
      group = group.replace("%20", " ");

    // determine the index to use
    Index index = ValueGroup.getIndex(name);
    if(index == null)
    {
      inWriter.title("Not Found")
        .begin("h1").add("Unknown index").end("h1")
        .add("The index referenced does not exist!");
      Log.warning("index '" + name + "' not found for request");

      return;
    }

    Log.info("serving dynamic " + index.getTitle() + " index '" + name + "/"
             + group + "'");

    String title = index.getTitle(group);
    Object titleCommand = title;

    if(index.hasImages())
      titleCommand =
        new Icon(inRequest.getOriginalPath().toLowerCase(Locale.US) + ".png",
                 group, group, true);

    if(index.isEditable(group))
      titleCommand = new Editable("*/" + index.getTitle(), index.getType(),
                                  titleCommand, index.getTitle() + "/" + group,
                                  group, "string");

    if(!index.listEntries(group))
      group = index.write(inWriter, m_data,
                          inRequest.getOriginalPath(), group,
                          inRequest.getPageSize(),
                          inRequest.getStart());

    if(group != null)
      format(inWriter,
             m_data.getIndexEntries(name, type, inRequest.getStart(),
                                    inRequest.getPageSize(), group), true,
             title, new Title(titleCommand), inRequest.getStart(),
             inRequest.getPageSize());

    if(group != null)
      addNavigation(inWriter, index.getNavigation(name, group));
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test
  //........................................................................
}

