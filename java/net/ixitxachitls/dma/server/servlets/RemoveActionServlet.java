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

import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.util.logging.Log;

/**
 * The servlet to save given entries.
 *
 * @file          RemoveActionServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@ParametersAreNonnullByDefault
public class RemoveActionServlet extends ActionServlet
{
  /**
   * Create the entry action servlet.
   */
  public RemoveActionServlet()
  {
    // nothing to do
  }

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   *
   * Execute the action associated with this servlet.
   *
   * @param       inRequest  the request for the page
   * @param       inResponse the response to write to
   *
   * @return      the javascript code to send back to the client
   */
  @Override
  protected String doAction(DMARequest inRequest,
                            HttpServletResponse inResponse)
  {
    Optional<BaseCharacter> user = inRequest.getUser();

    if(!user.isPresent())
      return "gui.alert('Must be logged in to delete!');";

    Optional<String> keyParam = inRequest.getParam("key");
    if(!keyParam.isPresent())
      return "gui.alert('No key given')";

    Optional<EntryKey> key =
        EntryKey.fromString(keyParam.get().replace("_", "+").replace("-", " "));

    if(!key.isPresent())
      return "gui.alert('Invalid key " + keyParam + "');";

    Optional<AbstractEntry> entry = DMADataFactory.get().getEntry(key.get());
    if(!entry.isPresent())
      return "gui.alert('Could not find " + key.get() + " to delete');";

    if(!entry.get().isDM(user))
      return "gui.alert('Not allow to delete " + key.get() + "!');";

    if(DMADataFactory.get().remove(entry.get()))
    {
      Log.important("Deleted entry " + keyParam);
      return "gui.info('Entry " + key.get() + " deleted!');";
    }

    return "gui.alert('Could not delete " + key.get() + "');";
  }
}
