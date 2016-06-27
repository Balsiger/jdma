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

import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.dma.output.soy.SoyTemplate;
import net.ixitxachitls.dma.server.servlets.DMAServlet;
import net.ixitxachitls.util.Tracer;
import net.ixitxachitls.util.logging.Log;

/**
 * Action to initiate recompiling of soy templates.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class RecompileAction extends Action
{
  @Override
  public String execute()
  {
    if(!DMAServlet.isDev())
      return alert("Can only recompile on dev!");

    Tracer tracer = new Tracer("compiling soy templates");
    Log.important("recompiling soy templates on dev");
    SoyRenderer.getDefaultTemplate().recompile();
    SoyTemplate.COMMAND_RENDERER.recompile();
    tracer.done();

    return info("Template recompliation started. Page reloading.");
  }
}
