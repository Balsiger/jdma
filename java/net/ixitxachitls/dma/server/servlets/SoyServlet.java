/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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
import java.io.PrintWriter;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Optional;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.output.soy.SoyEntry;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.dma.output.soy.SoyTemplate;
import net.ixitxachitls.dma.output.soy.SoyValue;
import net.ixitxachitls.server.servlets.FileServlet;
import net.ixitxachitls.util.Tracer;
import net.ixitxachitls.util.logging.Log;

/**
 * The base servlet for all soy rendered pages.
 *
 *
 * @file          SoyServlet.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@Immutable
@ParametersAreNonnullByDefault
public class SoyServlet extends DMAServlet
{
   /**
    * Create the servlet.
    */
   public  SoyServlet()
   {
     // nothing to do
   }

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /** The template to render a page. */
  protected static final SoyTemplate TEMPLATE =
     new SoyTemplate("page", "errors", "about", "main", "navigation", "entry",
                     "commands", "value", "admin", "cards", "edit",
                     "entries/basecharacters",
                     //"character",
                     "entries/baseproducts", "entries/products",
                     "entries/basecampaigns", "entries/campaigns",
                     "entries/baseitems", "entries/items",
                     "entries/basequalities",
                     "entries/baselevels",
                     "entries/basefeats",
                     "entries/baseskills",
                     "entries/basespells"//,
                     //"basemonster", "monster",
                     //"npc",
                     //"baseencounter", "encounter",
                     );

  /**
   * Get the name of the template to render the page.
   *
   * @param     inRequest the request for the page
   *
   * @return    the name of the template
   */
  protected String getTemplateName(DMARequest inRequest)
  {
    if(inRequest.isBodyOnly())
      return "dma.page.bodyOnly";

    if(inRequest.getRequestURI().endsWith(".print"))
      return "dma.page.print";

    return "dma.page.full";
  }

  /**
   * Checks whether the current page is public or requires some kind of
   * login.
   *
   * @param       inRequest the request for the page
   *
   * @return      true if public, false if login is required
   */
  public boolean isPublic(DMARequest inRequest)
  {
    return true;
  }

  @Override
  public String toString()
  {
    return "soy servlet with template " + TEMPLATE;
  }

  @Override
  protected @Nullable SpecialResult handle(DMARequest inRequest,
                                           HttpServletResponse inResponse)
    throws ServletException, IOException
  {

    if(FileServlet.wasReloaded() && isDev())
    {
      Tracer tracer = new Tracer("compiling soy templates");
      Log.important("recompiling soy templates on dev");
      TEMPLATE.recompile();
      SoyValue.COMMAND_RENDERER.recompile();
      SoyTemplate.COMMAND_RENDERER.recompile();
      tracer.done();
    }

    // Set the output header.
    inResponse.setHeader("Content-Type", "text/html");
    inResponse.setHeader("Cache-Control", "max-age=0");

    Tracer tracer = new Tracer("creating renderer");
    SoyRenderer renderer = new SoyRenderer(TEMPLATE);
    tracer.done();
    // we have to collect injected data before other data to have it available
    // when collecting
    tracer = new Tracer("setting injected data");
    renderer.setInjected(collectInjectedData(inRequest, renderer));
    tracer.done();
    tracer = new Tracer("collecting data");
    Map<String, Object> data = collectData(inRequest, renderer);
    tracer.done();
    tracer = new Tracer("setting soy data");
    renderer.setData(data);
    tracer.done();

    tracer = new Tracer("rendering soy template");
    try (PrintWriter print = new PrintWriter(inResponse.getOutputStream()))
    {
      print.println(renderer.render(getTemplateName(inRequest)));
    }
    tracer.done();

    return null;
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
  protected Map<String, Object> collectData(DMARequest inRequest,
                                            SoyRenderer inRenderer)
  {
    return SoyTemplate.map();
  }

  /**
   * Collect the injected data that is to be printed.
   *
   * @param    inRequest the request for the page
   * @param    inRenderer the renderer for rendering sub values
   *
   * @return   a map with key/value pairs for data (values can be primitives
   *           or maps or lists)
   */
  protected Map<String, Object> collectInjectedData
    (DMARequest inRequest, SoyRenderer inRenderer)
  {
    Tracer tracer = new Tracer("collecting soy injected data");
    Optional<BaseCharacter> user = inRequest.getUser();
    UserService userService = UserServiceFactory.getUserService();

    Map<String, Object> map = SoyTemplate.map
      ("user", user.isPresent() ? new SoyEntry(user.get()) : "",
       "isPublic", isPublic(inRequest),
       "originalPath", inRequest.getOriginalPath(),
       "loginURL", userService.createLoginURL(inRequest.getOriginalPath()),
       "logoutURL", userService.createLogoutURL(inRequest.getOriginalPath()),
       "registerScript",
       user.isPresent() && userService.isUserLoggedIn()
       ? "$().ready(function(){ register(); } );" : "",
       "userOverride", inRequest.hasUserOverride()
         ? inRequest.getRealUser().get().getName() : "",
       "isUser", user.isPresent(),
       "isAdmin", user.isPresent()
                  && user.get().hasAccess(BaseCharacter.Group.ADMIN));

    tracer.done();
    return map;
  }

  /**
   * Convert the given data into a map, using odd params as keys and even as
   * values.
   *
   * @param    inData the data to convert to a map
   *
   * @return   the converted map
   *
   */
  public Map<String, Object> map(Object ... inData)
  {
    return SoyTemplate.map(inData);
  }

  //----------------------------------------------------------------------------

  /** The tests. */
  public static class Test extends net.ixitxachitls.server.ServerUtils.Test
  {
    //----- map ------------------------------------------------------------

    /** The map Test. */
    @org.junit.Test
    public void map()
    {
      SoyServlet servlet = new SoyServlet();
      assertEquals("empty", "{}", servlet.map().toString());
      assertEquals("simple", "{second=b, third=c, first=a}",
                   servlet.map("first", "a", "second", "b", "third", "c")
                   .toString());
    }

    //......................................................................
    //----- collectData ----------------------------------------------------

    /** The collectData Test. */
    @org.junit.Test
    public void collectData()
    {
      DMARequest request = EasyMock.createMock(DMARequest.class);

      EasyMock.replay(request);

      SoyServlet servlet = new SoyServlet();
      assertEquals("content", "{}",
                   servlet.collectData(request,
                                       new SoyRenderer(new SoyTemplate()))
                   .toString());

      EasyMock.verify(request);
    }

    //......................................................................
    //----- collectInjectedData --------------------------------------------

    /** The collectInjectedData Test. */
    @org.junit.Test
    public void collectInjectedData()
    {
      DMARequest request = EasyMock.createMock(DMARequest.class);

      EasyMock.expect(request.getUser()).andStubReturn(null);
      EasyMock.expect(request.getOriginalPath()).andStubReturn("path");
      EasyMock.expect(request.hasUserOverride()).andStubReturn(false);
      EasyMock.replay(request);

      SoyServlet servlet = new SoyServlet();
      assertEquals("content",
                   "{isUser=false, "
                   + "registerScript=$().ready(function(){ register(); } );, "
                   + "userOverride=, "
                   + "logoutURL=/_ah/logout?continue=path, "
                   + "isAdmin=false, "
                   + "originalPath=path, "
                   + "isPublic=true, "
                   + "user=, "
                   + "loginURL=/_ah/login?continue=path}",
                   servlet.collectInjectedData
                   (request,
                    new SoyRenderer(new SoyTemplate())).toString());

      EasyMock.verify(request);
    }

    //......................................................................

  }

  //........................................................................
}
