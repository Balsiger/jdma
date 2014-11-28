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

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.dma.values.enums.Group;
import net.ixitxachitls.util.logging.Log;

/**
 * Servlet for the admin page.
 *
 * @file          AdminServlet.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@Immutable
public class AdminServlet extends SoyServlet
{
  /**
   * Create the admin servlet.
   */
  public AdminServlet()
  {
  }

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /**
    * Get the time of the last modification.
    *
    * @return      the time of the last modification in miliseconds or -1
    *              if unknown
    */
  public long getLastModified()
  {
    return -1;
  }

  /**
   * Get the name of the template to render the page.
   *
   * @param     inRequest the request for the page
   *
   * @return    the name of the template
   */
  @Override
  protected String getTemplateName(DMARequest inRequest)
  {
    return "dma.admin.page";
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

  /**
   * Handles the body content of the request.
   *
   * @param       inRequest  the original request
   * @param       inResponse the original response
   *
   * @return      an error if something went wrong
   *
   * @throws      IOException      writing to the page failed
   * @throws      javax.servlet.ServletException  writing to the page failed
   */
  @Override
  protected @Nullable SpecialResult handle(HttpServletRequest inRequest,
                                           HttpServletResponse inResponse)
    throws IOException, javax.servlet.ServletException
  {
    if(!(inRequest instanceof DMARequest))
    {
      Log.error("expected a dma request");
      return new TextError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                           "expected a dma request");
    }

    DMARequest request = (DMARequest)inRequest;

    Optional<BaseCharacter> user = request.getUser();
    if(!user.isPresent() || !user.get().hasAccess(Group.ADMIN))
    {
      if(user.isPresent())
        Log.warning("admin request by non-admin " + user.get().getName());
      else
        Log.warning("admin request without valid user");

      return new TextError(HttpServletResponse.SC_FORBIDDEN,
                           "action not allowed");
    }

    String reset = request.getParam("reset");
    if(reset != null)
    {
      // Set the output header.
      inResponse.setHeader("Content-Type", "text/html");
      inResponse.setHeader("Cache-Control", "max-age=0");

      Optional<? extends AbstractType<? extends AbstractEntry>> type =
          AbstractType.getTyped(reset);
      if(!type.isPresent())
        return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                             "Invalid type '" + reset + "'.");

      int size = DMADataFactory.get().rebuild(type.get());

      try (PrintWriter writer = new PrintWriter(inResponse.getOutputStream()))
      {
        writer.println("gui.info('The indexes for " + reset
                       + " have been rebuilt! " + size
                       + " entries updated.');");
      }

      Log.event(user.get().getName(), "admin index reset",
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
      Log.event(user.get().getName(), "admin clear cache",
                "All caches have been cleared");

      try (PrintWriter writer = new PrintWriter(inResponse.getOutputStream()))
      {
        writer.println("gui.info('All the caches have been cleared');");
      }
      return null;
    }

    String refresh = request.getParam("refresh");
    if(refresh != null)
    {
      // Set the output header.
      inResponse.setHeader("Content-Type", "text/html");
      inResponse.setHeader("Cache-Control", "max-age=0");

      Optional<? extends AbstractType<? extends AbstractEntry>> type =
        AbstractType.getTyped(refresh);
      if(!type.isPresent())
        return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                             "Invalid type '" + refresh + "'.");

      int size = DMADataFactory.get().refresh(type.get(), request);

      Log.event(user.get().getName(), "admin refresh " + refresh,
                size + " entries of " + refresh + " have been refreshed.");

      try (PrintWriter writer = new PrintWriter(inResponse.getOutputStream()))
      {
        writer.println("gui.info('" + size + " entries for " + refresh
                       + " have been refreshed');");
      }
      return null;
    }

    String upgrade = request.getParam("upgrade");
    if(upgrade != null)
    {
      inResponse.setHeader("Content-Type", "text/html");
      inResponse.setHeader("Cache-Control", "max-age=0");

      /*
      DMADatastore store = ((DMADatastore)DMADataFactory.get());
      AbstractType type = AbstractType.getTyped(upgrade);
      if (type == null)
        try (PrintWriter writer = new PrintWriter(inResponse.getOutputStream()))
        {
          writer.println("gui.info('invalid type " + upgrade
                         + ", nothing done');");
          return null;
        }

      int start = 0;
      if (request.hasParam("start")) {
        start = Integer.parseInt(request.getParam("start"));
      }
      int count = 0;
      int converted = 0;
      int first = start;
      for(List<? extends AbstractEntry> entries =
            store.getEntries(type, null, start, 100);
          !entries.isEmpty();
            start += 100, entries = store.getEntries(type, null, start, 100))
      {
        if(request.timeIsRunningOut())
          break;

        for(AbstractEntry entry : entries)
        {
          count++;
          if(entry.getFiles().isEmpty())
          {
            List<DMAData.File> files = store.getFiles(entry, false);
            if (!files.isEmpty())
            {
              converted++;
              for (DMAData.File file : files)
                entry.addFile(file.getName(), file.getType(), file.getPath(),
                              file.getIcon());

              entry.save();
            }
          }
        }
      }

      try (PrintWriter writer = new PrintWriter(inResponse.getOutputStream()))
      {
        writer.println("gui.info('updated " + converted + " of " + count + " "
                       + type + " starting at " + first + " and ending at "
                       + start + " for files');");
      }

      return null;
      */
    }

    return super.handle(inRequest, inResponse);
  }

  //----------------------------------------------------------------------------

  /** No test here, as we don't have mocks for the surrounding classes. */
}
