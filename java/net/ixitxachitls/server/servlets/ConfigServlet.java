/******************************************************************************
 * Copyright (c) 2002-2007 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.server.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ixitxachitls.output.html.HTMLWriter;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The configuration servlet.
 *
 * @file          ConfigServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class ConfigServlet extends BaseServlet
{
  //--------------------------------------------------------- constructor(s)

  public ConfigServlet()
  {
  }

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- handle --------------------------------

  /**
   * Handle the request if it is allowed.
   *
   * @param       inRequest  the original request
   * @param       inResponse the original response
   *
   * @return      an error if something went wrong
   *
   * @throws      ServletException general error when processing the page
   * @throws      IOException      writing to the page failed
   *
   */
  protected @Nullable SpecialResult handle
    (@Nonnull HttpServletRequest inRequest,
     @Nonnull HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    // Set the output header.
    inResponse.setHeader("Content-Type", "text/html");
    inResponse.setHeader("Cache-Control", "max-age=0");

    HTMLWriter writer =
      new HTMLWriter(new PrintWriter(inResponse.getOutputStream()));

    writer
      .title("DMA - Configuration")
      .begin("h1").add("DMA Configuration").end("h1")
      .begin("h2").add("System Properties").end("h2")
      .begin("table").style("max-width:100%;word-wrap:break-word;");

    Properties systemProperties = System.getProperties();
    SortedSet<Object> names = new TreeSet<Object>();
    for(Object name : systemProperties.keySet())
      names.add(name);

    for(Object name : names)
      writer
        .begin("tr").style("vertical-align:top")
        .begin("td").style("font-weight:bold").add(name.toString()).end("td")
        .begin("td")
        .begin("div").style("max-width:100px;overflow:hidden;word-wrap:break-word").add(systemProperties.get(name).toString())
        .end("div")
        .end("td")
        .end("tr");

    writer
      .end("table")
      .begin("h2").add("Configuration Values").end("h2")
      .begin("table");

    for(Map.Entry<String, String> entry : Config.getValues().entrySet())
      writer
        .begin("tr")
        .begin("td").style("font-weight:bold").add(entry.getKey())
        .end("td")
        .begin("td").style("").add(entry.getValue()).end("td")
        .end("tr");

    writer.end("table");

    writer.close();

    return null;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
