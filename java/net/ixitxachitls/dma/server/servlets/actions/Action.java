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

package net.ixitxachitls.dma.server.servlets.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.server.servlets.DMARequest;
import net.ixitxachitls.util.Encodings;

/**
 * The interface for actions called from the action servlet.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public abstract class Action
{
  public Action(String ... inNames)
  {
    m_names = Arrays.asList(inNames);
  }

  private List<String> m_names = new ArrayList<>();

  public String execute(DMARequest inRequest)
  {
    List<Optional<String>> params = extractParams(inRequest, m_names);

    switch(params.size())
    {
      case 0:
        return execute();

      case 1:
        return execute(params.get(0));

      case 2:
        return execute(params.get(0), params.get(1));

      case 3:
        return execute(params.get(0), params.get(1), params.get(2));

      default:
        return execute(params);
    }
  }

  protected String execute() {
    return alert("not implemented");
  }

  protected String execute(Optional<String> inFirst)
  {
    return alert("not implemented: " + inFirst);
  }

  protected String execute(Optional<String> inFirst, Optional<String> inSecond)
  {
    return alert("not implemented: " + inFirst + "/" + inSecond);
  }

  protected String execute(Optional<String> inFirst, Optional<String> inSecond,
                           Optional<String> inThird)
  {
    return alert("not implemented: " + inFirst + "/" + inSecond + "/"
                     + inThird);
  }

  private String execute(List<Optional<String>> inParams)
  {
    return alert("not implemented: " + inParams);
  }

  public static String alert(String inMessage)
  {
    return "gui.alert('" + Encodings.escapeJS(inMessage) + "');";
  }

  protected static String warning(String inMessage)
  {
    return "gui.warning('" + Encodings.escapeJS(inMessage) + "');";
  }

  protected static String info(String inMessage)
  {
    return "gui.info('" + Encodings.escapeJS(inMessage) + "');";
  }

  private List<Optional<String>> extractParams(DMARequest inRequest,
                                               List<String> inNames)
  {
    List<Optional<String>> params = new ArrayList<>();

    for(String name : inNames)
      params.add(inRequest.getParam(name));

    return params;
  }
}
