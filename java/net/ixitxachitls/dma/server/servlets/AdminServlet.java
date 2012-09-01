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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.Immutable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.memcache.MemcacheServiceFactory;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.output.html.HTMLWriter;
import net.ixitxachitls.server.servlets.BaseServlet;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * Servlet for the admin page.
 *
 * @file          AdminServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class AdminServlet extends SoyServlet
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- AdminServlet -----------------------------

  /**
   * Create the admin servlet.
   */
  public AdminServlet()
  {
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------- getLastModified ----------------------------

  /**
    * Get the time of the last modification.
    *
    * @return      the time of the last modification in miliseconds or -1
    *              if unknown
    *
    */
  public long getLastModified()
  {
    return -1;
  }

  //........................................................................
  //--------------------------- getTemplateName ----------------------------

  /**
   * Get the name of the template to render the page.
   *
   * @param     inRequest the request for the page
   *
   * @return    the name of the template
   *
   */
  protected @Nonnull String getTemplateName(@Nonnull DMARequest inRequest)
  {
    return "dma.admin.page";
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //----------------------------- collectData ------------------------------

  /**
   * Collect the data that is to be printed.
   *
   * @param    inRequest  the request for the page
   * @param    inRenderer the renderer for rendering sub values
   *
   * @return   a map with key/value pairs for data (values can be primitives
   *           or maps or lists)
   *
   */
  protected @Nonnull Map<String, Object> collectData
    (@Nonnull DMARequest inRequest, @Nonnull SoyRenderer inRenderer)
  {
    Map<String, Object> data = super.collectData(inRequest, inRenderer);

    List<String> types = new ArrayList<String>();
    for(AbstractType<? extends AbstractEntry> type : AbstractType.getAll())
      types.add(type.toString());

    data.put("types", types);

    List<Map<String, Object>> logs = new ArrayList<Map<String, Object>>();
    List<Map<String, Object>> events = new ArrayList<Map<String, Object>>();
    for(Iterator<Log.Message> i = Log.getLast(); i.hasNext(); )
    {
      Log.Message message = i.next();

      if(message.getType() == Log.Type.EVENT)
        events.add(map("text", message.getText(),
                       "date", "" + message.getDate()));
      else
        logs.add(map("types", message.getType().types(),
                     "text", message.getText(),
                     "date", "" + message.getDate()));
    }

    data.put("logs", logs);
    data.put("events", events);

    return data;
  }

  //........................................................................
  //--------------------------------- handle -------------------------------

  /**
   * Handles the body content of the request.
   *
   * @param       inRequest  the original request
   * @param       inResponse the original response
   *
   * @return      an error if something went wrong
   *
   * @throws      IOException      writing to the page failed
   * @throws      ServletException  writing to the page failed
   *
   */
  @Override
  protected @Nullable SpecialResult handle
    (@Nonnull HttpServletRequest inRequest,
     @Nonnull HttpServletResponse inResponse)
    throws IOException, javax.servlet.ServletException
  {
    if(!(inRequest instanceof DMARequest))
    {
      Log.error("expected a dma request");
      return new TextError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                           "expected a dma request");
    }

    DMARequest request = (DMARequest)inRequest;

    BaseCharacter user = request.getUser();
    if(user == null || !user.hasAccess(BaseCharacter.Group.ADMIN))
    {
      if(user == null)
        Log.warning("admin request without valid user");
      else
        Log.warning("admin request by non-admin " + user.getName());

      return new TextError(HttpServletResponse.SC_FORBIDDEN,
                           "action not allowed");
    }

    String reset = request.getParam("reset");
    if(reset != null)
    {
      // Set the output header.
      inResponse.setHeader("Content-Type", "text/html");
      inResponse.setHeader("Cache-Control", "max-age=0");

      AbstractType<? extends AbstractEntry> type = AbstractType.getTyped(reset);
      if(type == null)
        return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                             "Invalid type '" + reset + "'.");

      int size = DMADataFactory.get().rebuild(type);

      PrintWriter writer = new PrintWriter(inResponse.getOutputStream());
      writer.println("gui.info('The indexes for " + reset
                     + " have been rebuilt! " + size + " entries updated.');");
      writer.close();
      Log.event(user.getName(), "admin index reset",
                "index " + reset + " was reset for " + size + " entries");
      return null;
    }

    String cache = request.getParam("cache");
    if(cache != null)
    {
      // Set the output header.
      inResponse.setHeader("Content-Type", "text/html");
      inResponse.setHeader("Cache-Control", "max-age=0");

      MemcacheServiceFactory.getMemcacheService("entity").clearAll();
      MemcacheServiceFactory.getMemcacheService("byValue").clearAll();
      MemcacheServiceFactory.getMemcacheService("listByValue").clearAll();
      MemcacheServiceFactory.getMemcacheService("ids").clearAll();
      MemcacheServiceFactory.getMemcacheService("idsByValue").clearAll();
      MemcacheServiceFactory.getMemcacheService("recent").clearAll();
      MemcacheServiceFactory.getMemcacheService("values").clearAll();
      MemcacheServiceFactory.getMemcacheService("multiValues").clearAll();
      Log.event(user.getName(), "admin clear cache",
                "All caches have been cleared");

      PrintWriter writer = new PrintWriter(inResponse.getOutputStream());
      writer.println("gui.info('All the caches have been cleared');");
      writer.close();
      return null;
    }

    return super.handle(inRequest, inResponse);

    //   .begin("div").id("admin-events").end("div")
    //   .begin("select").classes("admin-button")
    //   .onChange("admin.show(this.value)")
    //   .begin("option").value("COMPLETE").add("Complete").end("option")
    //   .begin("option").value("DEBUG").add("Debug").end("option")
    //   .begin("option").value("STATUS").attribute("selected", "").add("Status")
    //   .end("option")
    //   .begin("option").value("INFO").add("Info").end("option")
    //   .begin("option").value("WARNING").add("Warning").end("option")
    //   .begin("option").value("ERROR").add("Error").end("option")
    //   .end("select")
    //   .begin("h2").add("Recent Log Entries").end("h2")
    //   .begin("div").id("admin-logs").end("div")
    //   .begin("script");

    // for(Iterator<Log.Message> i = Log.getLast(); i.hasNext(); )
    // {
    //   Log.Message message = i.next();

    //   if(message.getType() == Log.Type.EVENT)
    //     writer.add("admin.addEvent("
    //                  + Encodings.toJSString(message.getText())
    //                  + ", " + message.getDate() + ");\n");
    //   else
    //     writer.add("admin.addLog("
    //                  + Encodings.toJSString(message.getType().types())
    //                  + ", " + Encodings.toJSString(message.getText())
    //                  + ", " + message.getDate() + ");\n");
    // }

    // writer.end("script");
    // writer.close();
  }

  //........................................................................
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** No test here, as we don't have mocks for the surrounding classes. */

  //........................................................................
}
