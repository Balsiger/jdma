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
import java.util.Locale;
//import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.Immutable;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.ValueGroup;
//import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.output.html.HTMLDocument;
//import net.ixitxachitls.dma.server.servlets.DMARequest;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Divider;
import net.ixitxachitls.output.commands.Editable;
import net.ixitxachitls.output.commands.Icon;
import net.ixitxachitls.output.commands.Link;
import net.ixitxachitls.output.commands.Par;
//import net.ixitxachitls.output.commands.Script;
import net.ixitxachitls.output.commands.Table;
import net.ixitxachitls.output.commands.Title;
import net.ixitxachitls.output.html.HTMLWriter;
//import net.ixitxachitls.util.Files;
//import net.ixitxachitls.util.Filter;
//import net.ixitxachitls.util.FilteredIterator;
import net.ixitxachitls.util.Pair;
import net.ixitxachitls.util.Strings;
//import net.ixitxachitls.util.configuration.Config;
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

  /** All the available data. */
  private @Nonnull DMAData m_data;

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

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
                          "^/index/([^/]+)/([^/]+)(?:/(?:$|([^/]+$)))?");
    String name = match[1];
    String group = match[2];

    if(name == null || name.isEmpty())
    {
      inWriter.title("Not Found")
        .begin("h1").add("Unnamed index").end("h1")
        .add("The index referenced does not exist!");
      Log.warning("unnamed index for request");

      return;
    }

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

    inWriter.title(index.getTitle());

    if(group == null || group.length() == 0)
    {
      inWriter.add(handleOverview(inRequest, name, index).toString());
      addNavigation(inWriter, index.getType().getMultipleLink(),
                    "/" + index.getType().getMultipleLink(),
                    name, "/" + index.getType().getMultipleLink() + "/" + name);
    }
    else
    {
      inWriter.add(handleDetailed(inRequest, name, group, index).toString());
      addNavigation(inWriter, index.getType().getMultipleLink(),
                    "/" + index.getType().getMultipleLink(),
                    name, "/" + index.getType().getMultipleLink() + "/" + name,
                    group,
                    "/" + index.getType().getMultipleLink() + "/" + name + "/"
                    + group);
    }
  }

  //........................................................................

  //---------------------------- handleOverview ----------------------------

  /**
   * Handle an overview index.
   *
   * @param       inRequest the request for the page
   * @param       inPath    the path to the index pages
   * @param       inIndex   the index to write
   *
   * @return      the html document with all the contents added
   *
   */
  protected HTMLDocument handleOverview(@Nonnull DMARequest inRequest,
                                        @Nonnull String inPath,
                                        @Nonnull Index inIndex)
  {
    // the general index
    Log.info("serving dynamic " + inIndex.getTitle() + " index");

    // compute all the different category names
    SortedSet<String> names =
      new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    inIndex.names(names, m_data);

    HTMLDocument document = new HTMLDocument(inIndex.getTitle());

    if(inIndex.hasImages())
      document.add(new Title(new Icon(inIndex.getType().getLink()
                                      + "/index.png",
                                      inIndex.getTitle(), "", true)));
    else
      document.add(new Title(inIndex.getTitle()));

    document.add(new Par());

    List<Command> commands = new ArrayList<Command>();

    for(String name : names)
      if(inIndex.hasImages())
        commands.add(new Icon(inIndex.getType().getLink() + "/"
                              + name.toLowerCase(Locale.US)
                              + ".png", inPath + "/" + name, true));
      else
        commands.add(new Link(new Divider("index-overview", name),
                              inPath + "/" + name, "index-link"));

    commands.add(new Divider("clear", ""));

    document.add(new Command(commands.toArray()));

    return document;
  }

  //........................................................................
  //---------------------------- handleDetailed ----------------------------

  /**
   * Handle a detailed index.
   *
   * @param       inRequest the request for the page
   * @param       inPath    the path to the page
   * @param       inGroup   the group for the page
   * @param       inIndex   the index to write
   *
   * @return      the html document with all the contents added
   *
   */
  protected HTMLDocument handleDetailed(@Nonnull DMARequest inRequest,
                                        @Nonnull String inPath,
                                        @Nonnull String inGroup,
                                        @Nonnull Index inIndex)
  {
    // determine start and end of index to show
    Pair<Integer, Integer> pagination = inRequest.getPagination();
    int start = pagination.first();
    int end   = pagination.second();

    if(end <= 0)
      end = start + inRequest.getPageSize();

    Log.info("serving dynamic " + inIndex.getTitle() + " index '" + inGroup
             + "'");

    // create a detailed index file
    HTMLDocument document =
      new HTMLDocument(inIndex.getTitle() + ": " + inGroup);

    if(inIndex.hasImages())
      document.add(new Title(new Editable
                             ("", inIndex.getType(), inGroup,
                              "person",
                              new Icon(inPath + "/"
                                       + inGroup.toLowerCase(Locale.US)
                                       + ".png", inPath, inPath, true),
                              "string")));
     else
       document.add(new Title(new Editable
                              ("", inIndex.getType(), inGroup, "person",
                               inGroup, "string")));

    List<String> navigation = new ArrayList<String>();
    if(inIndex.isPaginated())
      if(start > 0)
        if(start - inRequest.getPageSize() > 0)
          navigation.add("<a href=\"?start="
                         + (start - inRequest.getPageSize())
                         + "\"  onclick=\"return util.link(event, '?start="
                         + (start - inRequest.getPageSize()) + "&end=" + end
                         + "');\" "
                         + "class=\"paginate-previous\">"
                         + "&laquo; previous</a>");
        else
          navigation.add("<a href=\"?\" "
                         + "onclick=\"return util.link(event, '?');\" "
                         + "class=\"paginate-previous\">"
                         + "&laquo; previous</a>");

    document.add(navigation);

    // TODO: extract this from the request
    boolean dm = true;

    List<? extends AbstractEntry> entries =
      m_data.getEntriesList(inIndex.getType());
    List<Object> cells = new ArrayList<Object>();
    for(AbstractEntry entry : entries)
      if(inIndex.matches(inGroup, entry))
        cells.addAll(entry.printList(entry.getName(), dm));

    document.add(new Table("entrylist", entries.get(0).getListFormat(),
                           new Command(cells)));

    document.add(navigation);

    return document;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
//   public static class Test extends net.ixitxachitls.util.test.TestCase
//   {
//   }

  //........................................................................
}
