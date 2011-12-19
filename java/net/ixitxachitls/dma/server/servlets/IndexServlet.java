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
import java.util.TreeSet;

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
import net.ixitxachitls.output.html.HTMLWriter;
import net.ixitxachitls.util.Encodings;
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
      writeError(inWriter, "Not Found", "The index referenced does not exist!");
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
      writeError(inWriter, "Not Found", "The index referenced does not exist!");
      return;
    }

    name = name.replace("%20", " ");

    if(group != null)
      group = group.replace("%20", " ");

    // determine the index to use
    Index index = ValueGroup.getIndex(name);
    if(index == null)
    {
      writeError(inWriter, "Not Found", "The index referenced does not exist!");
      return;
    }

    Log.info("serving dynamic " + type + " index '" + name + "/"
             + group + "'");

    // write the title for the page
    writeTitle(inWriter, index, name, type, group);

    if(group == null)
      group = writeOverview(inWriter, index, name, type);

    if(group != null)
    {
      String typeLink = type.getMultipleLink();
      format(inWriter,
             // we get one more entry to know if we have to add pagination
             m_data.getIndexEntries(name, type, group, inRequest.getStart(),
                                    inRequest.getPageSize() + 1), true,
             inRequest.getStart(), inRequest.getPageSize());
      addNavigation(inWriter,
                    typeLink, "/" + typeLink,
                    name, "/" + typeLink + "/" + name,
                    group, "/" + typeLink + "/" + name + "/" + group);
    }
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //---------------------------- writeOverview -----------------------------

  /**
   * Write the overview information for the current index.
   *
   * @param   inWriter  where to write to
   * @param   inIndex   the index to write the overview for
   * @param   inName    the name of the index to show
   * @param   inType    the type of entries to show
   *
   * @return  the group to show if the overview only contains one entry
   *
   */
  protected @Nullable String writeOverview
    (@Nonnull HTMLWriter inWriter,
     @Nonnull Index inIndex,
     @Nonnull String inName,
     @Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    // get all the index groups available
    SortedSet<String> values = m_data.getIndexNames(inName, inType);
    if(values.isEmpty())
    {
      writeError(inWriter, "Not Found",
                   "The index referenced does not exist!");
      return null;
    }

    if(values.size() == 1)
      return values.iterator().next();

    if(values.iterator().next().contains("::"))
      writeNestedOverview(inWriter, inName, values);
    else
      for(String value : values)
        if(inIndex.hasImages())
          writeIcon(inWriter,
                    inType.getMultipleLink() + "/" + inName + "/" + value,
                    value, inName + "/" + value);
        else
          writeName(inWriter, value, inName + "/" + value, "index-overview");

    inWriter
      .begin("div").classes("clear")
      .end("div");

    String typeLink = inType.getMultipleLink();
    addNavigation(inWriter,
                  typeLink, "/" + typeLink,
                  inName, "/" + typeLink + "/" + inName);

    return null;
  }

  //........................................................................
  //------------------------- writeNestedOverview --------------------------

  /**
   * Write an overview with nested group names.
   *
   * @param     inWriter  where to write to
   * @param     inPath    the base path to the index page
   * @param     inNames   the group names of the overview
   *
   */
  private void writeNestedOverview(@Nonnull HTMLWriter inWriter,
                                   @Nonnull String inPath,
                                   @Nonnull SortedSet<String> inNames)
  {
    SortedMap<String, List<String>> grouped = convertGroups(inNames);
    for(Map.Entry<String, List<String>> value : grouped.entrySet())
    {
      if(value.getValue().size() <= 1)
        writeName(inWriter, value.getKey(),
                  inPath + "/" + value.getKey() + "::", "index-overview");
      else
      {
        inWriter
          .begin("div").classes("index-overview")
          .onClick("$(this).next().toggle();")
          .add(value.getKey())
          .end("div")
          .begin("div").classes("index-group");

        for(String subvalue : value.getValue())
          writeName(inWriter, subvalue, inPath + "/" + value.getKey()
                    + "::" + subvalue, "index-overview index-nested");

        inWriter
          .end("div");
      }
    }
  }

  //........................................................................
  //------------------------------ writeTitle ------------------------------

  /**
   * Write the title to the page.
   *
   * @param    inWriter  where to write to
   * @param    inIndex   the index being written
   * @param    inPath    the base path to the index pages
   * @param    inType    the type of entries being printed
   * @param    inGroup   the group written
   *
   */
  protected static void writeTitle
    (@Nonnull HTMLWriter inWriter, @Nonnull Index inIndex,
     @Nonnull String inPath,
     @Nonnull AbstractType<? extends AbstractEntry> inType,
     @Nullable String inGroup)
  {
    String title = inIndex.getTitle();
    if(inGroup != null)
      title += " - " + inGroup.replace("::", " ");

    inWriter.title(title);

    inWriter.begin("h1");

    if(inGroup != null && inIndex.hasImages())
      writeIcon(inWriter,
                inType.getMultipleLink() + "/" + inPath + "/" + inGroup,
                title, inPath + "/" + inGroup);
    else if(inIndex.isEditable(inGroup))
      inWriter
        .begin("dmaeditable")
        .id("*/" + inPath)
        .classes("editable")
        .attribute("entry", inType.toString())
        .attribute("value", Encodings.encodeHTMLAttribute(inGroup))
        .attribute("key", inPath + "/" + inGroup)
        .attribute("type", "string")
        .begin("span")
        .add(title)
        .end("span")
        .end("dmaeditable");
    else
      inWriter.add(title);

    inWriter.end("h1");
  }

  //........................................................................

  //---------------------------- convertGroups -----------------------------

  /**
   * Convert the given set of values into a list of lists by grouping all
   * values with the same prefix (delimited by ::) together.
   *
   * @param       inValues the values to convert
   *
   * @return      the converted lists
   *
   */
  public static @Nonnull SortedMap<String, List<String>>
    convertGroups(SortedSet<String> inValues)
  {
    SortedMap<String, List<String>> grouped =
      new TreeMap<String, List<String>>();

    String group = null;
    List<String> list = null;

    for(String value : inValues)
    {
      String []parts = Index.stringToGroups(value);
      if(!parts[0].equals(group))
      {
        group = parts[0];
        list = new ArrayList<String>();
        grouped.put(group, list);
      }

      if(parts.length >= 2)
        list.add(parts[1]);
      else
        list.add("");
    }

    return grouped;
  }

  //........................................................................
  //------------------------------ formatName ------------------------------

  /**
   * Write the given name to the given writer.
   *
   * @param     inWriter  the write to write to
   * @param     inName    the name of the index
   * @param     inPath    the path to link to, if any
   * @param     inStyle   the style class for the name
   *
   */
  public void writeName(@Nonnull HTMLWriter inWriter, @Nonnull String inName,
                        @Nonnull String inPath, @Nonnull String inStyle)
  {
    inWriter
      .begin("a").href(inPath).classes("index-link")
      .begin("div").classes(inStyle)
      .add(inName)
      .end("div")
      .end("a");
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- grouping -------------------------------------------------------

    /** The grouping Test. */
    @org.junit.Test
      public void grouping()
    {
      assertTrue("grouped", convertGroups(new TreeSet<String>()).isEmpty());

      SortedSet<String> set = new TreeSet<String>();
      set.add("a::A");
      set.add("a::C");
      set.add("b::B");
      set.add("a::D");
      set.add("b::A");
      set.add("b::C");
      set.add("b::B");
      assertContent("grouped keys", convertGroups(set).keySet(), "a", "b");
      assertContent("grouped a", convertGroups(set).get("a"), "A", "C", "D");
      assertContent("grouped b", convertGroups(set).get("b"), "A", "B", "C");
    }

    //......................................................................
  }

  //........................................................................
}

