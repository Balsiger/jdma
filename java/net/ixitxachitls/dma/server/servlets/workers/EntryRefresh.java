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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.dma.server.servlets.DMARequest;
import net.ixitxachitls.dma.server.servlets.DMAServlet;
import net.ixitxachitls.server.servlets.BaseServlet;
import net.ixitxachitls.util.logging.Log;

/**
 * A worker task to refresh entries of a given kind.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class EntryRefresh extends DMAServlet
{
  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  public static String TYPE_PARAM = "type";
  public static String PARENT_PARAM = "parent";

  @Override
  protected Optional<? extends SpecialResult> handle(DMARequest inRequest,
                                                     HttpServletResponse inResponse)
      throws ServletException, IOException
  {
    Optional<String> typeParam = inRequest.getParam(TYPE_PARAM);
    Optional<String> parentParam = inRequest.getParam(PARENT_PARAM);

    if(!typeParam.isPresent())
      return Optional.of(new TextError(200,
                                       "A type parameter has to be given"));

    Optional<? extends AbstractType<? extends AbstractEntry>> type =
        AbstractType.getTyped(typeParam.get());
    if(!type.isPresent())
      return Optional.of(new TextError(200, "Cannot parse given type '"
          + typeParam + "'"));

    Optional<EntryKey> parent;
    if(parentParam.isPresent())
      parent = EntryKey.fromString(parentParam.get());
    else
      parent = Optional.absent();

    Log.important("Refreshing type " + type + " for parent " + parent);
    DMADataFactory.get().refresh(type.get(), parent);
    Log.important("Refreshing of type " + type + " for parent " + parent
                  + " is done");

    return Optional.absent();
  }
}
