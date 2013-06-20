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

//------------------------------------------------------------------ imports

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

import org.easymock.EasyMock;

import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.output.soy.SoyEntry;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.dma.output.soy.SoyTemplate;
import net.ixitxachitls.dma.output.soy.SoyValue;
import net.ixitxachitls.server.servlets.FileServlet;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The base servlet for all soy rendered pages.
 *
 *
 * @file          SoyServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class SoyServlet extends DMAServlet
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- SoyServlet -------------------------------

   /**
    * Create the servlet.
    *
    */
   public  SoyServlet()
   {
     // nothing to do
   }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /** The template to render a page. */
  protected static final SoyTemplate s_template =
     new SoyTemplate("page", "errors", "about", "main", "navigation", "entry",
                     "commands", "value", "admin", "cards",
                     "basecharacter", "character",
                     "baseproduct", "product",
                     "baseitem", "item",
                     "basecampaign", "campaign",
                     "basequality",
                     "basefeat",
                     "baseskill",
                     "basespell",
                     "basemonster", "monster");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------- getTemplateName ----------------------------

  /**
   * Get the name of the template to render the page.
   *
   * @param     inRequest the request for the page
   *
   * @return    the name of the template
   *
   */
  protected String getTemplateName(DMARequest inRequest)
  {
    if(inRequest.isBodyOnly())
      return "dma.page.bodyOnly";

    if(inRequest.getRequestURI().endsWith(".print"))
      return "dma.page.print";

    return "dma.page.full";
  }

  //........................................................................

  //------------------------------- isPublic -------------------------------

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

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //-------------------------------- handle --------------------------------

  /**
   * Handle the request.
   *
   * @param       inRequest  the original request
   * @param       inResponse the original response
   *
   * @return      a special result if something went wrong
   *
   * @throws      ServletException general error when processing the page
   * @throws      IOException      writing to the page failed
   *
   */
  @Override
  protected @Nullable SpecialResult handle(DMARequest inRequest,
                                           HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    if(FileServlet.wasReloaded() && isDev())
    {
      Log.important("recompiling soy templates on dev");
      s_template.recompile();
      SoyValue.COMMAND_RENDERER.recompile();
      SoyTemplate.COMMAND_RENDERER.recompile();
    }

    // Set the output header.
    inResponse.setHeader("Content-Type", "text/html");
    inResponse.setHeader("Cache-Control", "max-age=0");

    SoyRenderer renderer = new SoyRenderer(s_template);
    // we have to collect injected data before other data to have it available
    // when collecting
    renderer.setInjected(collectInjectedData(inRequest, renderer));
    renderer.setData(collectData(inRequest, renderer));

    PrintWriter print = new PrintWriter(inResponse.getOutputStream());
    print.println(renderer.render(getTemplateName(inRequest)));
    print.close();

    return null;
  }

  //........................................................................

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
  protected Map<String, Object> collectData(DMARequest inRequest,
                                            SoyRenderer inRenderer)
  {
    return SoyTemplate.map();
  }

  //........................................................................
  //------------------------- collectInjectedData --------------------------

  /**
   * Collect the injected data that is to be printed.
   *
   * @param    inRequest the request for the page
   * @param    inRenderer the renderer for rendering sub values
   *
   * @return   a map with key/value pairs for data (values can be primitives
   *           or maps or lists)
   *
   */
  protected Map<String, Object> collectInjectedData
    (DMARequest inRequest, SoyRenderer inRenderer)
  {
    BaseCharacter user = inRequest.getUser();
    UserService userService = UserServiceFactory.getUserService();

    return SoyTemplate.map
      ("user", user == null ? "" : new SoyEntry(user),
       "isPublic", isPublic(inRequest),
       "originalPath", inRequest.getOriginalPath(),
       "loginURL", userService.createLoginURL(inRequest.getOriginalPath()),
       "logoutURL", userService.createLogoutURL(inRequest.getOriginalPath()),
       "registerScript",
       user == null && userService.isUserLoggedIn()
       ? "$().ready(function(){ register(); } );" : "",
       "userOverride",
       inRequest.hasUserOverride() ? inRequest.getRealUser().getName() : "",
       "isUser", user != null,
       "isAdmin", user != null && user.hasAccess(BaseCharacter.Group.ADMIN));
  }

  //........................................................................
  //--------------------------------- map ----------------------------------

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

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

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
