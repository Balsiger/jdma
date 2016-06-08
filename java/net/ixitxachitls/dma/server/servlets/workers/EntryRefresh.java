/*******************************************************************************
 * Copyright (c) 2002-2016 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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
 ******************************************************************************/

package net.ixitxachitls.dma.server.servlets.workers;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.util.logging.Log;

/**
 * A worker task to refresh entries of a given kind.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class EntryRefresh extends HttpServlet
{
  public static String TYPE_PARAM = "type";
  public static String PARENT_PARAM = "parent";

  protected void doPost(HttpServletRequest inRequest,
                        HttpServletResponse inResponse)
  {
    String typeParam = inRequest.getParameter(TYPE_PARAM);
    String parentParam = inRequest.getParameter(PARENT_PARAM);

    Optional<? extends AbstractType<? extends AbstractEntry>> type =
        AbstractType.getTyped(typeParam);
    if(!type.isPresent())
      return;

    Optional<EntryKey> parent = EntryKey.fromString(parentParam);

    Log.important("Refreshing type " + type + " for parent " + parent);
    DMADataFactory.get().refresh(type.get(), parent);
    Log.important("Refreshing of type " + type + " for parent " + parent
                  + " is done");
  }
}
