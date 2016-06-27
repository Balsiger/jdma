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

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.server.servlets.actions.Action;
import net.ixitxachitls.dma.server.servlets.actions.CampaignTimeAction;
import net.ixitxachitls.dma.server.servlets.actions.CurrentCampaignAction;
import net.ixitxachitls.dma.server.servlets.actions.MoveEntryAction;
import net.ixitxachitls.dma.server.servlets.actions.RecompileAction;
import net.ixitxachitls.dma.server.servlets.actions.RegisterAction;
import net.ixitxachitls.dma.server.servlets.actions.RemoveEntryAction;
import net.ixitxachitls.dma.server.servlets.actions.SaveEntryAction;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

/**
 * The base servlet for action calls.
 *
 * @file          ActionServlet.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class ActionServlet extends DMAServlet
{
  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  private static Map<String, Action> s_actions = new HashMap<>();
  static
  {
    s_actions.put("current-campaign", new CurrentCampaignAction());
    s_actions.put("save", new SaveEntryAction());
    s_actions.put("move", new MoveEntryAction());
    s_actions.put("recompile", new RecompileAction());
    s_actions.put("register", new RegisterAction());
    s_actions.put("remove", new RemoveEntryAction());
    s_actions.put("time", new CampaignTimeAction());
  }

  /**
   * Create the servlet for actions.
   */
  public ActionServlet()
  {
  }

  /**
   * Handle a get requets from the client.
   *
   * @param       inRequest  the request from the client
   * @param       inResponse the response to the client
   *
   * @throws      IOException       when writing to the page fails
   * @throws      ServletException  a general problem with handling the request
   *                                happens
   */
  @Override
  public void doGet(HttpServletRequest inRequest,
                    HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    Log.info("Denying " + inRequest.getMethod() + " request for "
             + inRequest.getRequestURI() + "...");

    new TextError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                  inRequest.getMethod() + " not allowed for this request")
      .send(inResponse);
  }

  /**
   * Really handle the request.
   *
   * @param       inRequest  the request from the client
   * @param       inResponse the response to the client
   *
   * @return      a special result if something went wrong
   *
   * @throws      IOException       when writing to the page fails
   * @throws      ServletException  a general problem with handling the request
   *                                happens
   */
  @Override
  protected Optional<? extends SpecialResult>
  handle(DMARequest inRequest, HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    // set the content type
    inResponse.setHeader("Content-Type", "text/javascript");
    inResponse.setHeader("Cache-Control", "max-age=0");

    try
    {
      // TODO: this is actually very coarse, as it does not allow two
      // action servlets to be executed at the same time, even if they work
      // on different data. But it is easy and should cover most problems
      // at the time being. This has to be moved down to protect the real
      // data, though.
      synchronized(ActionServlet.class)
      {
        String name = extractActionName(inRequest);
        Action action = s_actions.get(name);
        String text;
        if(action == null)
          text = Action.alert("unknown acton " + name);
        else
          text = action.execute(inRequest);

        try (PrintStream print = new PrintStream(inResponse.getOutputStream()))
        {
          print.print(text);
        }
      }
    }
    catch(java.io.IOException e)
    {
      Log.warning("could not return action result: " + e);
    }

    return Optional.absent();
  }

  /**
   * Fail handling the action with the given message.
   *
   * @param       inMessage the message with which to fail
   *
   * @return      javascript to send back to the client for failure
   */
  protected String fail(String inMessage)
  {
    Log.warning(inMessage);

    return "gui.alert('" + inMessage + "');";
  }

  private static String extractActionName(DMARequest inRequest)
  {
    String path = inRequest.getOriginalPath();
    String name = Strings.getPattern(path, "/actions/(.*)$");
    if(name == null)
      return "";

    return name;
  }

  //----------------------------------------------------------------------------

  /** The tests. */
  public static class Test extends net.ixitxachitls.server.ServerUtils.Test
  {
  }
}
